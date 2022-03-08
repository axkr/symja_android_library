package org.matheclipse.api;

import static io.undertow.Handlers.resource;
import java.awt.Desktop;
import java.net.InetAddress;
import java.net.URI;
import java.util.Deque;
import java.util.Map;
import org.matheclipse.api.parser.FuzzyParserFactory;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

public class SymjaServer {
  /** If <code>true</code>, use localhost string */
  public static boolean LOCALHOST_STRING = false;

  static int PORT = 8080;

  static boolean TEST = false;

  private static final class APIHandler implements HttpHandler {
    @Override
    public void handleRequest(final HttpServerExchange exchange) {
      String jsonStr;
      HeaderMap responseHeaders = exchange.getResponseHeaders();
      responseHeaders.put(new HttpString("Access-Control-Allow-Origin"), "*");
      responseHeaders.put(Headers.CONTENT_TYPE, "application/json");

      Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
      String appid = getAppID(queryParameters, "appid");
      if (appid != null) {
        if (appid.equals("DEMO")) {
          String strict = SymjaServer.getParam(queryParameters, "strict", "s", "");
          String inputStr = SymjaServer.getParam(queryParameters, "input", "i", "");
          String[] formformatStrs =
              SymjaServer.getParams(queryParameters, "format", "f", Pods.PLAIN_STR);
          int formats = Pods.internFormat(formformatStrs);
          try {
            ObjectNode messageJSON = Pods.createResult(inputStr, formats, !strict.isEmpty());
            jsonStr = messageJSON.toString();
          } catch (RuntimeException rex) {
            rex.printStackTrace();
            jsonStr = Pods.errorJSONString("0", "JSON Export Failed");
          }
        } else {
          jsonStr = Pods.errorJSONString("1", "Invalid appid");
        }
      } else {
        jsonStr = Pods.errorJSONString("2", "Appid missing");
      }
      exchange.getResponseSender().send(jsonStr);
    }
  }

  public static void main(final String[] args) {
    try {
      if (setArgs("SymjaServer", args) < 0) {
        return;
      }
    } catch (RuntimeException rex) {
      return;
    }

    try {
      ToggleFeature.COMPILE = false;
      ToggleFeature.COMPILE_PRINT = true;
      Config.FUZZY_PARSER = true;
      Config.UNPROTECT_ALLOWED = false;
      Config.USE_MANIPULATE_JS = true;
      Config.JAS_NO_THREADS = false;
      // Config.THREAD_FACTORY =
      // com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
      Config.MATHML_TRIG_LOWERCASE = false;
      Config.MAX_AST_SIZE = 10000;
      Config.MAX_OUTPUT_SIZE = 10000;
      Config.MAX_BIT_LENGTH = 200000;
      Config.MAX_POLYNOMIAL_DEGREE = 100;
      Config.FILESYSTEM_ENABLED = false;
      Config.MAX_INPUT_LEAVES = 100L;
      Config.MAX_MATRIX_DIMENSION_SIZE = 100;
      EvalEngine.get().setPackageMode(true);
      F.initSymbols();
      FuzzyParserFactory.initialize();

      final APIHandler apiHandler = new APIHandler();

      PathHandler path = new PathHandler()
          .addPrefixPath("/",
              resource(new ClassPathResourceManager(SymjaServer.class.getClassLoader(),
                  SymjaServer.class.getPackage())).addWelcomeFiles("indexapi.html"))
          .addExactPath("/v1/api", apiHandler);
      // https://stackoverflow.com/a/41652378/24819
      String host = LOCALHOST_STRING ? "localhost" : InetAddress.getLocalHost().getHostAddress();
      Undertow server = Undertow.builder().addHttpListener(PORT, host).setHandler(path).build();
      server.start();

      System.out.println("\n>>> JSON API server started. <<<");
      System.out.println("Waiting for API calls at http://" + host + ":" + PORT + "/v1/api");
      System.out.println("Example client call:");
      System.out.println("http://" + host + ":" + PORT
          + "/v1/api?i=D(Sin(x)%2Cx)&f=latex&f=plaintext&f=sinput&appid=DEMO");

      URI uri = new URI("http://" + host + ":" + PORT + "/indexapi.html");

      System.out.println();
      System.out.println("To test the JSON API open page: " + uri.toString() + " in your browser.");
      if (TEST && Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(uri);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  protected static int setArgs(final String serverClass, final String args[]) {
    for (int i = 0; i < args.length; i++) {
      final String arg = args[i];

      if (arg.equals("-localhost") || arg.equals("-l")) {
        LOCALHOST_STRING = true;
      } else if (arg.equals("-port") || arg.equals("-p")) {
        if (i + 1 >= args.length) {
          System.out.println("You must specify a port number when using the -port argument");
          throw ReturnException.RETURN_FALSE;
        }

        String portStr = args[i + 1];
        i++;
        PORT = Integer.valueOf(portStr);
      } else if (arg.equals("-test") || arg.equals("-t")) {
        TEST = true;
      } else if (arg.equals("-help") || arg.equals("-h")) {
        printUsage(serverClass);
        return -1;

      } else if (arg.charAt(0) == '-') {
        // we don't have any more args to recognize!
        final String msg = "Unknown arg: " + arg;
        System.out.println(msg);
        printUsage(serverClass);
        return -4;
      }
    }
    printUsage(serverClass);
    return 1;
  }

  /** Prints the usage of how to use this class to stdout */
  private static void printUsage(final String serverClass) {
    final String lineSeparator = System.getProperty("line.separator");
    final StringBuilder msg = new StringBuilder();
    msg.append(Config.SYMJA);
    msg.append(Config.COPYRIGHT);
    msg.append("Symja JSON API Wiki: " + "https://github.com/axkr/symja_android_library/wiki/API"
        + lineSeparator);
    msg.append(lineSeparator);
    msg.append("org.matheclipse.api." + serverClass + " [options]" + lineSeparator);
    msg.append(lineSeparator);
    msg.append("Program arguments: " + lineSeparator);
    msg.append("  -h or -help print usage messages" + lineSeparator);
    msg.append("  -l or -localhost set the name to \"localhost\"" + lineSeparator);
    msg.append("         in the browser; the default is the IP address" + lineSeparator);
    msg.append("  -p or -port set the port (default port is 8080)" + lineSeparator);
    msg.append("  -t or -test open JSON API test page in browser " + lineSeparator);
    msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");

    System.out.println(msg.toString());
    System.out.flush();
  }

  static String getAppID(Map<String, Deque<String>> queryParameters, String shortParameter) {
    Deque<String> d = queryParameters.get(shortParameter);
    if (d != null && !d.isEmpty()) {
      return d.getFirst();
    }
    return null;
  }

  static String getParam(Map<String, Deque<String>> queryParameters, String longParameter,
      String shortParameter, String defaultStr) {
    Deque<String> d = queryParameters.get(shortParameter);
    if (d != null && !d.isEmpty()) {
      return d.getFirst();
    }
    d = queryParameters.get(longParameter);
    if (d != null && !d.isEmpty()) {
      return d.getFirst();
    }
    return defaultStr;
  }

  static String[] getParams(Map<String, Deque<String>> queryParameters, String longParameter,
      String shortParameter, String defaultStr) {
    Deque<String> d = queryParameters.get(shortParameter);
    if (d != null && !d.isEmpty()) {
      String[] result = d.toArray(new String[d.size()]);
      if (result.length == 1) {
        String[] split = result[0].split(",");
        return split;
      }
      return result;
    }
    d = queryParameters.get(longParameter);
    if (d != null && !d.isEmpty()) {
      String[] result = d.toArray(new String[d.size()]);
      if (result.length == 1) {
        String[] split = result[0].split(",");
        return split;
      }
      return result;
    }
    return new String[] {defaultStr};
  }
}
