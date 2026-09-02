package org.matheclipse.io.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.io.Extension;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * Takes a file from the browser into the session's own directory, so that <code>Import</code> has
 * something to read.
 *
 * <p>
 * A kernel answering HTTP cannot reach the caller's file system, and since the sandbox it can reach
 * is a directory of its own, a file has to be carried across. This is the carrying: the browser
 * posts the bytes here, they land in {@link SessionSandbox}, and the notebook then names the file
 * the way it would name a local one - <code>Import("data.csv")</code>.
 *
 * <p>
 * Only extensions {@link Extension} knows are accepted: a name the import functions could never
 * open has no business being written to disk.
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = SessionSandbox.MAX_FILE_BYTES,
    maxRequestSize = SessionSandbox.MAX_FILE_BYTES + 1024L * 1024L)
public class AJAXUploadServlet extends HttpServlet {

  private static final long serialVersionUID = 8140921540116280985L;

  private static final Logger LOGGER = LogManager.getLogger(AJAXUploadServlet.class);

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
    PrintWriter out = res.getWriter();

    HttpSession session = req.getSession();
    try {
      Part part = req.getPart("file");
      if (part == null) {
        out.println(JSONBuilder.createJSONErrorString("No file posted!"));
        return;
      }

      String name = SessionSandbox.safeName(part.getSubmittedFileName(), null);
      if (name == null) {
        out.println(JSONBuilder.createJSONErrorString("The file has no usable name!"));
        return;
      }
      int dot = name.lastIndexOf('.');
      if (dot < 1 || !Extension.isAllowedExtension(extensionOf(name, dot))) {
        out.println(JSONBuilder.createJSONErrorString(
            "Files of type " + name.substring(dot + 1) + " cannot be read!"));
        return;
      }

      byte[] bytes;
      try (InputStream inputStream = part.getInputStream()) {
        bytes = inputStream.readAllBytes();
      }
      Path stored = SessionSandbox.store(session.getId(), name, bytes);
      if (stored == null) {
        out.println(JSONBuilder.createJSONErrorString(
            "The file could not be stored - this session may be at its quota."));
        return;
      }
      LOGGER.debug("Session {} uploaded {} ({} bytes)", session.getId(), name, bytes.length);
      out.println("{\"name\": \"" + name + "\", \"size\": " + bytes.length + "}");
    } catch (Exception ex) {
      LOGGER.error("{}.doPost() failed", AJAXUploadServlet.class.getSimpleName(), ex);
      out.println(JSONBuilder.createJSONErrorString("Upload failed: " + ex.getMessage()));
    }
  }

  /** The extension, mapped through the aliases <code>Extension</code> accepts. */
  private static String extensionOf(String name, int dot) {
    String ext = name.substring(dot + 1);
    return ext.equalsIgnoreCase("jpg") ? "JPEG"
        : ext.equalsIgnoreCase("tif") ? "TIFF" //
            : ext.equalsIgnoreCase("xls") ? "XLSX" //
                : ext;
  }
}
