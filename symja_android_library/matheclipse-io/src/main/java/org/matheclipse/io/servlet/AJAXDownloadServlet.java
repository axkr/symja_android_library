package org.matheclipse.io.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.io.Extension;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Hands a file from the session's own directory to the browser.
 *
 * <p>
 * The counterpart of {@link AJAXUploadServlet}, and the second half of what <code>ExportForm</code>
 * means: <code>Export</code> writes a file on the kernel side - inside the sandbox - and
 * <code>ExportForm</code> says that the result of a cell is to be delivered as a file. The query
 * servlet writes the bytes and returns a link here; this streams them with a
 * <code>Content-Disposition</code> so the browser saves rather than renders them.
 *
 * <p>
 * A name that does not resolve inside the session's directory is a 404, so one session cannot read
 * another's files and no name can walk out of the sandbox.
 */
public class AJAXDownloadServlet extends HttpServlet {

  private static final long serialVersionUID = 2749061148146857463L;

  private static final Logger LOGGER = LogManager.getLogger(AJAXDownloadServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    HttpSession session = req.getSession();
    String name = SessionSandbox.safeName(req.getParameter("name"), null);
    Path file = name == null ? null : SessionSandbox.read(session.getId(), name);
    if (file == null) {
      res.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    try {
      res.setContentType(mimeType(name));
      res.setHeader("Cache-Control", "no-cache");
      res.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
      res.setContentLengthLong(Files.size(file));
      try (OutputStream out = res.getOutputStream()) {
        Files.copy(file, out);
      }
    } catch (IOException | RuntimeException ex) {
      LOGGER.error("Could not send {}", name, ex);
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /** The content type of a format, defaulting to bytes so the browser always saves. */
  static String mimeType(String name) {
    Extension format = Extension.importFilename(name);
    switch (format) {
      case CSV:
        return "text/csv";
      case TSV:
        return "text/tab-separated-values";
      case JSON:
      case RAWJSON:
      case EXPRESSIONJSON:
        return "application/json";
      case SVG:
        return "image/svg+xml";
      case PNG:
        return "image/png";
      case JPEG:
        return "image/jpeg";
      case GIF:
        return "image/gif";
      case XLSX:
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
      case TXT:
      case STRING:
      case TABLE:
      case DAT:
        return "text/plain";
      default:
        return "application/octet-stream";
    }
  }
}
