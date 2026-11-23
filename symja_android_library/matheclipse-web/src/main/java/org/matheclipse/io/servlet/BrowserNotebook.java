package org.matheclipse.io.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * The servlets of the notebook page, answering without a servlet container: for the notebook
 * kernel that runs in the browser (see <code>org.matheclipse.web.NotebookKernel</code> in this
 * module), where there is no HTTP and the page hands its requests to Java directly.
 *
 * <p>
 * Every request goes through the very servlet that answers it on the server - its own
 * <code>doGet</code> or <code>doPost</code>, reachable from here because this class is in the
 * servlets' package - with a request, a session, an uploaded part and a response that are
 * stand-ins implementing just what those methods use. Nothing of the servlets is re-implemented, so
 * an answer, an error text, a widget and a time limit are exactly what the page gets from a server.
 * The alternative, calling the evaluation methods underneath, would have meant copying each
 * servlet's handling of its parameters, and those copies would drift.
 *
 * <p>
 * The stand-ins are {@link Proxy} instances: the servlet interfaces have some seventy methods each,
 * of which the servlets call a handful. A method nobody implemented answers the default value of its
 * return type (<code>null</code>, <code>0</code>, <code>false</code>), which is what an absent
 * header or attribute answers in a container.
 *
 * <p>
 * There is one session per JVM - one browser tab, one kernel - with a fixed id. Safe to call from
 * several threads: the servlets serialize the evaluations of a session themselves, and each call
 * has a response of its own.
 */
public final class BrowserNotebook {

  /**
   * The id of the one session. It names the session's sandbox directory too, see
   * {@link SessionSandbox}, so it stays the same across page loads; that directory is emptied when
   * the kernel starts, as a server sweeps the directories of sessions it no longer knows.
   */
  static final String SESSION_ID = "notebook";

  /** The answer of a request that the servlets give no body for, such as a 404. */
  public static final class Answer {
    /** The HTTP status; 200 unless the servlet called <code>sendError</code>. */
    public final int status;
    /** The content type the servlet set, or <code>null</code>. */
    public final String contentType;
    /** The body as text, as the servlet wrote it through its writer. */
    public final String text;
    /** The body as bytes, as the servlet wrote it through its output stream. */
    public final byte[] bytes;

    Answer(int status, String contentType, String text, byte[] bytes) {
      this.status = status;
      this.contentType = contentType;
      this.text = text;
      this.bytes = bytes;
    }
  }

  private final AJAXQueryServlet query;
  private final AJAXNotebookServlet notebook = new AJAXNotebookServlet();
  private final AJAXManipulateServlet manipulate = new AJAXManipulateServlet();
  private final AJAXDynamicServlet dynamic = new AJAXDynamicServlet();
  private final AJAXTraceDialogServlet traceDialog = new AJAXTraceDialogServlet();
  private final AJAXUploadServlet upload = new AJAXUploadServlet();
  private final AJAXDownloadServlet download = new AJAXDownloadServlet();

  /** The session every stand-in request belongs to. */
  private final HttpSession session = proxy(HttpSession.class, (method, args) -> {
    switch (method.getName()) {
      case "getId":
        return SESSION_ID;
      case "isNew":
        return Boolean.FALSE;
      default:
        return null;
    }
  });

  private BrowserNotebook(AJAXQueryServlet query) {
    this.query = query;
  }

  /**
   * Start the kernel - this is the query servlet's <code>init()</code>, which initializes Symja and
   * the optional modules once per JVM - and open the session.
   *
   * <p>
   * The switches that must be set before Symja's classes load (<code>Config.CHEEPRJ</code> and the
   * like) are the caller's business: by the time this class is loaded, it is too late for some of
   * them.
   *
   * @param relaxedSyntax <code>true</code> for <code>sin(x)</code>, as the notebook page uses it
   */
  public static BrowserNotebook start(boolean relaxedSyntax) throws ServletException {
    AJAXQueryServlet query = relaxedSyntax ? new AJAXQueryServlet() : new MMAAJAXQueryServlet();
    // the servlet is not deployed; its init() is what starts the kernel
    query.init();
    return new BrowserNotebook(query);
  }

  /**
   * Answer a request to <code>/ajax/&lt;endpoint&gt;/</code> with the servlet mapped there.
   *
   * @param endpoint <code>query</code>, <code>notebook</code>, <code>manipulate</code>,
   *        <code>dynamic</code>, <code>tracedialog</code> or <code>download</code>; upload has
   *        {@link #upload(String, byte[])}, because it carries a file rather than parameters
   * @param get <code>true</code> for a GET, <code>false</code> for a POST
   * @param parameters the request parameters, as the page posts them or puts them in the query
   *        string
   * @return the answer, or <code>null</code> for an endpoint no servlet is mapped to
   */
  public Answer request(String endpoint, boolean get, Map<String, String> parameters)
      throws ServletException, IOException {
    HttpServlet servlet = servletFor(endpoint);
    if (servlet == null) {
      return null;
    }
    HttpServletRequest request = request(parameters, null);
    Response response = new Response();
    HttpServletResponse stub = response.proxy();
    // Each servlet is called the way the container would dispatch to it. A servlet that has no
    // doGet (or no doPost) answers the method with HttpServlet's 405, as it does on the server.
    if (servlet == query) {
      if (get) {
        query.doGet(request, stub);
      } else {
        query.doPost(request, stub);
      }
    } else if (servlet == notebook) {
      if (get) {
        notebook.doGet(request, stub);
      } else {
        notebook.doPost(request, stub);
      }
    } else if (servlet == manipulate) {
      if (get) {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      } else {
        manipulate.doPost(request, stub);
      }
    } else if (servlet == dynamic) {
      if (get) {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      } else {
        dynamic.doPost(request, stub);
      }
    } else if (servlet == traceDialog) {
      if (get) {
        traceDialog.doGet(request, stub);
      } else {
        traceDialog.doPost(request, stub);
      }
    } else if (servlet == download) {
      if (get) {
        download.doGet(request, stub);
      } else {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
      }
    }
    return response.answer();
  }

  /**
   * Answer <code>POST /ajax/upload/</code> for one file, as the page's file input posts it.
   *
   * @param fileName the name of the file on the visitor's machine
   * @param bytes its contents
   */
  public Answer upload(String fileName, byte[] bytes) throws ServletException, IOException {
    final byte[] content = bytes == null ? new byte[0] : bytes;
    Part part = proxy(Part.class, (method, args) -> {
      switch (method.getName()) {
        case "getSubmittedFileName":
          return fileName;
        case "getName":
          return "file";
        case "getInputStream":
          return new ByteArrayInputStream(content);
        case "getSize":
          return Long.valueOf(content.length);
        default:
          return null;
      }
    });
    Response response = new Response();
    upload.doPost(request(Collections.emptyMap(), part), response.proxy());
    return response.answer();
  }

  /**
   * Forget what the session has evaluated, as <code>AJAXQueryServlet.resetSession</code> does for
   * a front end that evaluates a whole document again from the top. The files stay.
   */
  public void reset() {
    AJAXQueryServlet.resetSession(request(Collections.emptyMap(), null));
  }

  /** The servlet <code>UndertowLauncher</code> maps to <code>/ajax/&lt;endpoint&gt;/</code>. */
  private HttpServlet servletFor(String endpoint) {
    if (endpoint == null) {
      return null;
    }
    switch (endpoint) {
      case "query":
        return query;
      case "notebook":
        return notebook;
      case "manipulate":
        return manipulate;
      case "dynamic":
        return dynamic;
      case "tracedialog":
        return traceDialog;
      case "download":
        return download;
      default:
        return null;
    }
  }

  /**
   * A request of the session with these parameters.
   *
   * @param part the file of an upload, or <code>null</code>
   */
  private HttpServletRequest request(Map<String, String> parameters, Part part) {
    return proxy(HttpServletRequest.class, (method, args) -> {
      switch (method.getName()) {
        case "getParameter":
          return parameters.get((String) args[0]);
        case "getSession":
          // getSession() and getSession(create) alike: the session always exists
          return session;
        case "getRequestedSessionId":
          return SESSION_ID;
        case "getPart":
          return part != null && "file".equals(args[0]) ? part : null;
        case "getMethod":
          return "POST";
        case "getCharacterEncoding":
          return "UTF-8";
        default:
          return null;
      }
    });
  }

  /** What a servlet writes into its response: the status, the content type and the body. */
  private static final class Response {
    private int status = HttpServletResponse.SC_OK;
    private String contentType;
    private StringWriter text;
    private PrintWriter writer;
    private ByteArrayOutputStream bytes;

    void sendError(int code) {
      status = code;
    }

    /** The response the servlet is handed. */
    HttpServletResponse proxy() {
      return BrowserNotebook.proxy(HttpServletResponse.class, (method, args) -> {
        switch (method.getName()) {
          case "getWriter":
            if (writer == null) {
              text = new StringWriter();
              writer = new PrintWriter(text);
            }
            return writer;
          case "getOutputStream":
            if (bytes == null) {
              bytes = new ByteArrayOutputStream();
            }
            return new BytesOutputStream(bytes);
          case "setContentType":
            contentType = (String) args[0];
            return null;
          case "getContentType":
            return contentType;
          case "sendError":
            status = (Integer) args[0];
            return null;
          case "setStatus":
            status = (Integer) args[0];
            return null;
          case "getStatus":
            return Integer.valueOf(status);
          case "getCharacterEncoding":
            return "UTF-8";
          default:
            // headers, content length and the character encoding change nothing here: the body is
            // handed over as a Java string or byte array, not as an HTTP message
            return null;
        }
      });
    }

    Answer answer() {
      String body = null;
      if (writer != null) {
        writer.flush();
        body = stripLineEnd(text.toString());
      }
      return new Answer(status, contentType, body, bytes == null ? null : bytes.toByteArray());
    }
  }

  /**
   * The body without the line separator the servlets' <code>println</code> ends it with. The page
   * parses the text as JSON either way; without it, it is the same string on every platform.
   */
  static String stripLineEnd(String body) {
    if (body.endsWith("\r\n")) {
      return body.substring(0, body.length() - 2);
    }
    if (body.endsWith("\n")) {
      return body.substring(0, body.length() - 1);
    }
    return body;
  }

  /** The output stream of a download: the file's bytes go into the answer. */
  private static final class BytesOutputStream extends ServletOutputStream {
    private final ByteArrayOutputStream target;

    BytesOutputStream(ByteArrayOutputStream target) {
      this.target = target;
    }

    @Override
    public void write(int b) {
      target.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
      target.write(b, off, len);
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
      // the stream never blocks, so there is nothing to tell a listener
    }
  }

  /** What a stand-in does for each method; <code>args</code> is never <code>null</code>. */
  @FunctionalInterface
  private interface Handler {
    Object invoke(Method method, Object[] args) throws Exception;
  }

  /**
   * A stand-in for <code>type</code>. The methods of {@link Object} behave as they do for any object
   * (identity), and a <code>null</code> from the handler is turned into the default of a primitive
   * return type, since a proxy must not return <code>null</code> for an <code>int</code>.
   */
  static <T> T proxy(Class<T> type, Handler handler) {
    InvocationHandler invocationHandler = (self, method, args) -> {
      Object[] arguments = args == null ? new Object[0] : args;
      switch (method.getName()) {
        case "hashCode":
          if (arguments.length == 0) {
            return Integer.valueOf(System.identityHashCode(self));
          }
          break;
        case "equals":
          if (arguments.length == 1) {
            return Boolean.valueOf(self == arguments[0]);
          }
          break;
        case "toString":
          if (arguments.length == 0) {
            return type.getSimpleName() + " of the browser notebook";
          }
          break;
        default:
          break;
      }
      Object value = handler.invoke(method, arguments);
      return value != null ? value : defaultOf(method.getReturnType());
    };
    return type.cast(Proxy.newProxyInstance(BrowserNotebook.class.getClassLoader(),
        new Class<?>[] {type}, invocationHandler));
  }

  /** The value an unset field of this type has. */
  private static Object defaultOf(Class<?> type) {
    if (!type.isPrimitive() || type == void.class) {
      return null;
    }
    if (type == boolean.class) {
      return Boolean.FALSE;
    }
    if (type == char.class) {
      return Character.valueOf('\0');
    }
    if (type == long.class) {
      return Long.valueOf(0L);
    }
    if (type == float.class) {
      return Float.valueOf(0f);
    }
    if (type == double.class) {
      return Double.valueOf(0d);
    }
    if (type == byte.class) {
      return Byte.valueOf((byte) 0);
    }
    if (type == short.class) {
      return Short.valueOf((short) 0);
    }
    return Integer.valueOf(0);
  }
}
