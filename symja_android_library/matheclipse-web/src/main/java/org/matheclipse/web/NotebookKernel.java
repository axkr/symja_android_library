package org.matheclipse.web;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.io.servlet.BrowserNotebook;
import org.matheclipse.io.servlet.JSONBuilder;
import org.matheclipse.parser.client.ParserConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The kernel of the notebook page, running in the visitor's browser on CheerpJ (a WebAssembly
 * JVM, Java 21) instead of on a server.
 *
 * <p>
 * The page (<code>index.html</code> and <code>media/js/*</code> of matheclipse-io) talks to its
 * kernel through the <code>/ajax/*</code> servlets. On a static site there is no server, so the
 * page's JavaScript hands each request to the static methods of this class instead, through
 * CheerpJ's library mode, and gets back the very body the servlet would have sent:
 * {@link #call(String, String)} for <code>query</code>, <code>notebook</code>,
 * <code>manipulate</code>, <code>dynamic</code> and <code>tracedialog</code>,
 * {@link #upload(String, byte[])} and {@link #download(String)} for the files of the session. The
 * answers are made by the servlets themselves (see {@link BrowserNotebook}), compiled into this
 * module.
 *
 * <p>
 * Only strings and byte arrays cross the boundary: CheerpJ passes a JavaScript string as a
 * {@link String} and an <code>Int8Array</code> as a <code>byte[]</code>, and anything richer would
 * need a Java object proxy on the JavaScript side. Every call is asynchronous there.
 *
 * <p>
 * No method throws. A failure - an {@link Error} such as {@link StackOverflowError} included - is
 * answered as the error JSON the servlets use, because an exception thrown into JavaScript only
 * surfaces as an opaque rejected promise.
 *
 * <p>
 * The evaluations run on the calling thread under Symja's own time limit, not on an executor: in
 * the browser all Java threads share the page's one thread and nothing preempts a busy one, so a
 * <code>Future.get(timeout)</code> never times out. The servlets do this whenever
 * {@link Config#CHEEPRJ} is set, which {@link #start()} does.
 */
public final class NotebookKernel {

  /** Reads the parameter objects of {@link #call(String, String)}. */
  private static final ObjectMapper PARAMETERS = new ObjectMapper();

  /** Guards {@link #notebook} and {@link #startJson}. */
  private static final Object LOCK = new Object();

  /** The servlets of the one session; <code>null</code> until {@link #start()} succeeded. */
  private static BrowserNotebook notebook;

  /** The answer of the successful {@link #start()}, given again by every later call. */
  private static String startJson;

  /**
   * The answer of the last failed {@link #start()}; what a call answers whose own attempt to start
   * the kernel failed, rather than a second attempt in the same call.
   */
  private static String startFailure;

  /** Static entry points only. */
  private NotebookKernel() {}

  /**
   * Start Symja and open the session of the page. Idempotent: after a successful start it answers
   * the first result again without doing anything; a failed start is not remembered, so a later
   * call tries again. The other methods call it themselves, so calling it first is only a way to
   * have the start-up happen at a moment of the page's choosing (and to time it).
   *
   * @return <code>{"ok":true,"initMillis":..,"symjaVersion":..}</code>, or
   *         <code>{"ok":false,"error":"&lt;ExceptionClass&gt;: &lt;message&gt;","initMillis":..}</code>
   */
  public static String start() {
    synchronized (LOCK) {
      if (notebook != null && startJson != null) {
        return startJson;
      }
      long started = System.currentTimeMillis();
      try {
        // First, before anything logs: log4j-core with the cell notifier of the server but a quiet
        // console (ERROR only, no stack traces), see BrowserLogging. Not SymjaCheerpJ's
        // SimpleLoggerContextFactory: the ERROR events that ServletLogging copies into a cell
        // (Import("missing.csv") -> "file missing.csv not found!") would be lost.
        BrowserLogging.configure();

        // The static initializer of F reads Config.CHEEPRJ, so this has to be set before anything
        // loads the class F - and it switches the servlets to evaluating on the calling thread.
        Config.CHEEPRJ = true;
        // JAS (Factor and friends) computes on the calling thread instead of its thread pool, and
        // TimeLimits starts no watchdog thread: threads are no help where they all share one
        Config.JAS_NO_THREADS = true;
        // the servlets' relaxed syntax, sin(x) as well as Sin(x)
        ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
        useBrowserFiles();

        BrowserNotebook opened = BrowserNotebook.start(true);
        notebook = opened;
        long millis = System.currentTimeMillis() - started;
        startJson = "{\"ok\":true,\"initMillis\":" + millis + ",\"symjaVersion\":"
            + PARAMETERS.writeValueAsString(Config.VERSION) + "}";
        return startJson;
      } catch (Throwable t) {
        notebook = null;
        startJson = null;
        startFailure = "{\"ok\":false,\"error\":" + quote(describe(t)) + ",\"initMillis\":"
            + (System.currentTimeMillis() - started) + "}";
        return startFailure;
      }
    }
  }

  /**
   * Answer a request to <code>/ajax/&lt;endpoint&gt;/</code> as its servlet does.
   *
   * @param endpoint <code>query</code>, <code>notebook</code>, <code>manipulate</code>,
   *        <code>dynamic</code> or <code>tracedialog</code>
   * @param paramsJSON a JSON object of parameter name to string value: the form fields the page
   *        posts, or its query string. A value that is not a string (a number, an object) is passed
   *        as its JSON text, which is what the page would have sent for it. The entry
   *        <code>"__method"</code>, <code>"GET"</code> or <code>"POST"</code> (the default), says
   *        which method the page used; it matters for <code>notebook</code>, whose GET answers the
   *        startup notebook and whose POST renders Markdown.
   * @return the body the servlet sends, without the line end it ends it with; for an unknown
   *         endpoint or a failure the error JSON of the servlets
   */
  public static String call(String endpoint, String paramsJSON) {
    try {
      BrowserNotebook kernel = kernel();
      if (kernel == null) {
        return startFailure;
      }
      Map<String, String> parameters = parameters(paramsJSON);
      boolean get = "GET".equalsIgnoreCase(parameters.remove("__method"));
      if ("upload".equals(endpoint) || "download".equals(endpoint)) {
        // their bodies are files, not text; see upload() and download()
        return JSONBuilder.createJSONErrorString(
            "Use NotebookKernel." + endpoint + "() for /ajax/" + endpoint + "/");
      }
      BrowserNotebook.Answer answer = kernel.request(endpoint, get, parameters);
      if (answer == null) {
        return JSONBuilder.createJSONErrorString("Unknown endpoint: " + endpoint);
      }
      if (answer.text == null) {
        // the servlet refused the method or failed before it wrote anything
        return JSONBuilder.createJSONErrorString("HTTP " + answer.status + " from /ajax/"
            + endpoint + "/");
      }
      return answer.text;
    } catch (Throwable t) {
      return errorJson(t);
    }
  }

  /**
   * Store a file in the session's directory, as <code>POST /ajax/upload/</code> does with the file
   * the page posts, so that <code>Import("data.csv")</code> can read it. In the browser the
   * directory is under <code>/files/</code>, CheerpJ's persistent file system.
   *
   * @param fileName the name of the file on the visitor's machine; it is sanitized as on the server
   * @param bytes its contents (an <code>Int8Array</code> on the JavaScript side)
   * @return the body <code>/ajax/upload/</code> answers: <code>{"name": .., "size": ..}</code> or
   *         the error JSON
   */
  public static String upload(String fileName, byte[] bytes) {
    try {
      BrowserNotebook kernel = kernel();
      if (kernel == null) {
        return startFailure;
      }
      BrowserNotebook.Answer answer = kernel.upload(fileName, bytes);
      return answer.text != null ? answer.text
          : JSONBuilder.createJSONErrorString("HTTP " + answer.status + " from /ajax/upload/");
    } catch (Throwable t) {
      return errorJson(t);
    }
  }

  /**
   * A file of the session's directory, as <code>GET /ajax/download/?name=..</code> streams it: an
   * uploaded file, or one an <code>ExportForm</code> result wrote (its link in the cell is
   * <code>/ajax/download/?name=..</code>, which the page has to route here).
   *
   * @param name the file name, as in the link
   * @return the bytes (an <code>Int8Array</code> on the JavaScript side), or <code>null</code> where
   *         the server answers 404 - no such file, or a name that leaves the directory
   */
  public static byte[] download(String name) {
    try {
      BrowserNotebook kernel = kernel();
      if (kernel == null) {
        return null;
      }
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      if (name != null) {
        parameters.put("name", name);
      }
      BrowserNotebook.Answer answer = kernel.request("download", true, parameters);
      return answer == null || answer.status != 200 ? null : answer.bytes;
    } catch (Throwable t) {
      return null;
    }
  }

  /**
   * Forget the definitions, the <code>Manipulate</code> widgets and the live <code>Dynamic</code>
   * cells of the session, as <code>AJAXQueryServlet.resetSession</code> does; the next cell is
   * evaluated in a new engine. The session's files stay.
   *
   * @return <code>{"ok":true}</code>, or <code>{"ok":false,"error":..}</code>
   */
  public static String reset() {
    try {
      BrowserNotebook kernel = kernel();
      if (kernel == null) {
        return startFailure;
      }
      kernel.reset();
      return "{\"ok\":true}";
    } catch (Throwable t) {
      return "{\"ok\":false,\"error\":" + quote(describe(t)) + "}";
    }
  }

  /**
   * The servlets of the session, starting the kernel first if that has not happened yet.
   *
   * @return <code>null</code> if the start failed; {@link #startJson} is then <code>null</code>
   *         too and the next call tries again
   */
  private static BrowserNotebook kernel() {
    synchronized (LOCK) {
      if (notebook == null) {
        start();
      }
      return notebook;
    }
  }

  /**
   * Point <code>java.io.tmpdir</code> into <code>/files/</code> when it exists - CheerpJ's
   * persistent, writable mount, which a desktop JVM does not have. The servlets keep the files of a
   * session in a directory under the temporary directory (see <code>SessionSandbox</code>), and the
   * default temporary directory of CheerpJ is not one Java can write to. The kernel is the only
   * Java program of the page, so moving it changes nothing for anybody else.
   */
  private static void useBrowserFiles() {
    File files = new File("/files");
    if (!files.isDirectory() || !files.canWrite()) {
      return;
    }
    File tmp = new File(files, "tmp");
    if (tmp.isDirectory() || tmp.mkdirs()) {
      System.setProperty("java.io.tmpdir", tmp.getPath());
    }
  }

  /**
   * The request parameters of a JSON object: a string is taken as it is, anything else as its JSON
   * text, and <code>null</code> is left out as an absent parameter would be.
   */
  private static Map<String, String> parameters(String paramsJSON) throws Exception {
    Map<String, String> parameters = new LinkedHashMap<String, String>();
    if (paramsJSON == null || paramsJSON.trim().isEmpty()) {
      return parameters;
    }
    JsonNode object = PARAMETERS.readTree(paramsJSON);
    if (object == null || !object.isObject()) {
      throw new IllegalArgumentException("the parameters are not a JSON object");
    }
    for (Iterator<Map.Entry<String, JsonNode>> it = object.fields(); it.hasNext();) {
      Map.Entry<String, JsonNode> field = it.next();
      JsonNode value = field.getValue();
      if (value == null || value.isNull()) {
        continue;
      }
      parameters.put(field.getKey(), value.isTextual() ? value.textValue() : value.toString());
    }
    return parameters;
  }

  /**
   * The error JSON of the servlets for a failure of the kernel itself; it says "Exception: .." as
   * <code>AJAXQueryServlet.doPost</code> does when its evaluation throws.
   */
  private static String errorJson(Throwable t) {
    try {
      return JSONBuilder.createJSONErrorString("Exception: " + describe(t));
    } catch (Throwable again) {
      // JSONBuilder itself cannot load: write the same shape by hand
      return "{\"results\":[{\"line\":null,\"result\":null,\"out\":[{\"prefix\":\"Error\","
          + "\"message\":true,\"tag\":\"syntax\",\"symbol\":\"General\",\"format\":\"text\","
          + "\"text\":" + quote("Exception: " + describe(t)) + "}]}]}";
    }
  }

  /** <code>ExceptionClass: message</code>, the way the spike reports a failure. */
  private static String describe(Throwable t) {
    String message = t.getMessage();
    return t.getClass().getName() + (message == null ? "" : ": " + message);
  }

  /**
   * A JSON string literal, written by hand: it is also needed where Jackson might be what failed
   * to load.
   */
  private static String quote(String text) {
    if (text == null) {
      return "null";
    }
    StringBuilder buf = new StringBuilder(text.length() + 2).append('"');
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      switch (ch) {
        case '"':
          buf.append("\\\"");
          break;
        case '\\':
          buf.append("\\\\");
          break;
        case '\n':
          buf.append("\\n");
          break;
        case '\r':
          buf.append("\\r");
          break;
        case '\t':
          buf.append("\\t");
          break;
        default:
          if (ch < 0x20) {
            buf.append(String.format("\\u%04x", (int) ch));
          } else {
            buf.append(ch);
          }
      }
    }
    return buf.append('"').toString();
  }
}
