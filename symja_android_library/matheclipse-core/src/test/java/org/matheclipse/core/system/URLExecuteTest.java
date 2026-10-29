package org.matheclipse.core.system;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * <code>URLExecute</code> against a server on the loopback address, so that no test depends on the
 * internet.
 */
public class URLExecuteTest extends ExprEvaluatorTestCase {

  private final boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;

  private HttpServer server;

  /** A Symja string literal of the server's address, left open for a path to be appended. */
  private String base;

  @BeforeEach
  public void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/json", exchange -> respond(exchange, 200,
        "application/json; charset=utf-8",
        "{\"tag1\":1234,\"tag2\":{\"subTag1\":42,\"subTag2\":[1,2.5,true]},\"name\":\"Euler\"}",
        StandardCharsets.UTF_8));
    server.createContext("/problem", exchange -> respond(exchange, 200,
        "application/problem+json", "{\"status\":400}", StandardCharsets.UTF_8));
    server.createContext("/badjson", exchange -> respond(exchange, 200, "application/json",
        "{\"tag1\":", StandardCharsets.UTF_8));
    server.createContext("/text", exchange -> respond(exchange, 200, "text/plain",
        "{\"tag1\":1234}", StandardCharsets.UTF_8));
    server.createContext("/latin1", exchange -> respond(exchange, 200,
        "text/plain; charset=ISO-8859-1", "Café", StandardCharsets.ISO_8859_1));
    server.createContext("/code", exchange -> respond(exchange, 200, "text/plain",
        "Print(\"evaluated\")", StandardCharsets.UTF_8));
    // answers with the query it was asked, so that the encoding of parameters can be checked
    server.createContext("/echo", exchange -> {
      String query = exchange.getRequestURI().getRawQuery();
      respond(exchange, 200, "text/plain", query == null ? "" : query, StandardCharsets.UTF_8);
    });
    server.createContext("/agent", exchange -> respond(exchange, 200, "text/plain",
        exchange.getRequestHeaders().getFirst("User-Agent"), StandardCharsets.UTF_8));
    server.createContext("/missing",
        exchange -> respond(exchange, 404, "text/plain", "not found", StandardCharsets.UTF_8));
    server.start();
    base = "\"http://127.0.0.1:" + server.getAddress().getPort();
    Config.FILESYSTEM_ENABLED = true;
  }

  private static void respond(HttpExchange exchange, int status, String contentType, String body,
      Charset charset) throws IOException {
    byte[] bytes = body.getBytes(charset);
    exchange.getResponseHeaders().set("Content-Type", contentType);
    exchange.sendResponseHeaders(status, bytes.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(bytes);
    }
  }

  @AfterEach
  public void stopServer() {
    server.stop(0);
    Config.FILESYSTEM_ENABLED = fileSystemEnabled;
    evaluator.getEvalEngine().setFileSandboxRoot(null);
  }

  @Test
  public void testJSONContentTypeImportsRules() {
    check("URLExecute(" + base + "/json\")", //
        "{tag1->1234,tag2->{subTag1->42,subTag2->{1,2.5,True}},name->Euler}");
    // a media type with the +json suffix is JSON too
    check("URLExecute(" + base + "/problem\")", //
        "{status->400}");
  }

  @Test
  public void testOtherContentTypeIsAString() {
    check("URLExecute(" + base + "/text\")", //
        "{\"tag1\":1234}");
    check("StringQ(URLExecute(" + base + "/text\"))", //
        "True");
    // decoded in the charset the server named
    check("URLExecute(" + base + "/latin1\")", //
        "Café");
  }

  @Test
  public void testFormat() {
    check("URLExecute(" + base + "/text\", {}, \"JSON\")", //
        "{tag1->1234}");
    check("URLExecute(" + base + "/text\", {}, \"RawJSON\")", //
        "<|tag1->1234|>");
    check("URLExecute(" + base + "/json\", {}, \"Text\")", //
        "{\"tag1\":1234,\"tag2\":{\"subTag1\":42,\"subTag2\":[1,2.5,true]},\"name\":\"Euler\"}");
  }

  @Test
  public void testAnswerIsNeverEvaluatedAsCode() {
    check("URLExecute(" + base + "/code\")", //
        "Print(\"evaluated\")");
    // "String" would parse the answer into an expression, and an unknown format name means it
    // URLExecute: Cannot import data as String format.
    check("Head(URLExecute(" + base + "/code\", {}, \"String\"))", //
        "URLExecute");
    // URLExecute: Cannot import data as NoSuchFormat format.
    check("Head(URLExecute(" + base + "/code\", {}, \"NoSuchFormat\"))", //
        "URLExecute");
    // URLExecute: Cannot import data as ExpressionJSON format.
    check("Head(URLExecute(" + base + "/json\", {}, \"ExpressionJSON\"))", //
        "URLExecute");
  }

  @Test
  public void testParameters() {
    check("URLExecute(" + base + "/echo\", {\"q\" -> \"Leonhard Euler\", \"n\" -> 10})", //
        "q=Leonhard%20Euler&n=10");
    check("URLExecute(" + base + "/echo\", <|\"a&b\" -> \"x=1+2\", \"c\" -> \"\u00e9\"|>)", //
        "a%26b=x%3D1%2B2&c=%C3%A9");
    // appended to a query the address already has, and before a fragment
    check("URLExecute(" + base + "/echo?x=1#top\", {\"y\" -> 2})", //
        "x=1&y=2");
    check("URLExecute(" + base + "/echo?\", {\"y\" -> 2})", //
        "y=2");
    check("URLExecute(" + base + "/echo?x=1\", {})", //
        "x=1");
  }

  @Test
  public void testUserAgent() {
    // some servers refuse the JVM's default Java/<version>, the Wikipedia API among them
    check("StringStartsQ(URLExecute(" + base + "/agent\"), \"Symja\")", //
        "True");
  }

  @Test
  public void testFailures() {
    // URLExecute: Cannot open http://127.0.0.1:port/missing.
    check("Head(URLExecute(" + base + "/missing\"))", //
        "URLExecute");
    // ImportString: Unexpected end-of-input ...
    // URLExecute: Cannot import data as JSON format.
    check("Head(URLExecute(" + base + "/badjson\"))", //
        "URLExecute");
    // URLExecute: (42) is neither a list of replacement rules nor a valid dispatch table and cannot
    // be used for replacing.
    check("Head(URLExecute(" + base + "/echo\", 42))", //
        "URLExecute");
    // URLExecute: Cannot open ftp://127.0.0.1/.
    check("URLExecute(\"ftp://127.0.0.1/\")", //
        "URLExecute(ftp://127.0.0.1/)");
    // URLExecute: String expected at position 1 in URLExecute(42).
    check("URLExecute(42)", //
        "URLExecute(42)");
  }

  @Test
  public void testSandboxReachesOnlyThePublicInternet(@TempDir Path root) {
    evaluator.getEvalEngine().setFileSandboxRoot(root);
    // URLExecute: Cannot open http://127.0.0.1:port/text.
    check("Head(URLExecute(" + base + "/text\"))", //
        "URLExecute");
  }
}
