package org.matheclipse.io.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Serves the notebook support of the browser interface:
 *
 * <ul>
 * <li><code>GET /ajax/notebook/</code> returns the <code>*.ipynb</code> notebook that was named
 * with the <code>-notebook</code> command line argument, so that the page can show it right after
 * startup. Nothing is evaluated by this; the browser only receives the file.
 * <li><code>POST /ajax/notebook/</code> with a <code>markdown</code> parameter holding a JSON array
 * of strings renders those Markdown cell sources to HTML and returns them in the same order.
 * </ul>
 *
 * <p>
 * The Markdown is rendered with raw HTML escaped and link URLs sanitized, because a notebook may
 * come from somebody else. Without that a Markdown cell could smuggle a <code>&lt;script&gt;</code>
 * into the page, which then has the evaluation engine of this server one XMLHttpRequest away.
 */
public class AJAXNotebookServlet extends HttpServlet {

  private static final long serialVersionUID = 6171930745706873963L;

  private static final Logger LOGGER = LogManager.getLogger(AJAXNotebookServlet.class);

  /** Refuse to render a single Markdown cell that is larger than this. */
  private static final int MAX_MARKDOWN_LENGTH = 1024 * 1024;

  /** Refuse to serve a startup notebook that is larger than this. */
  private static final long MAX_NOTEBOOK_SIZE = 64L * 1024L * 1024L;

  /**
   * A Markdown cell is written the way a Jupyter one is: <code>$...$</code> is inline mathematics
   * and <code>$$...$$</code> is display mathematics. Unlike the documentation pages this needs no
   * guard against Symja's <code>$</code> system symbols, so <code>$x$</code> is a formula here.
   */
  private static final List<Extension> EXTENSIONS =
      Arrays.asList(MarkdownTeX.extension(false), TablesExtension.create());

  private static final Parser MARKDOWN_PARSER = Parser.builder().extensions(EXTENSIONS).build();

  private static final HtmlRenderer MARKDOWN_RENDERER = HtmlRenderer.builder() //
      .extensions(EXTENSIONS) //
      .nodeRendererFactory(MarkdownTeX.rendererFactory()) //
      .escapeHtml(true) //
      .sanitizeUrls(true) //
      .build();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    prepare(res);
    PrintWriter out = res.getWriter();
    ObjectNode json = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();

    String fileName = ServletServer.NOTEBOOK_FILE;
    if (fileName == null) {
      json.putNull("notebook");
      out.println(json.toString());
      return;
    }

    try {
      Path path = Paths.get(fileName);
      if (!Files.isRegularFile(path)) {
        throw new IOException("not a readable file");
      }
      if (Files.size(path) > MAX_NOTEBOOK_SIZE) {
        throw new IOException("larger than " + (MAX_NOTEBOOK_SIZE / (1024 * 1024)) + " MB");
      }
      // parsed here rather than passed through, so that a broken file is reported as such instead
      // of arriving in the browser as unparseable text
      JsonNode notebook =
          JSONBuilder.JSON_OBJECT_MAPPER.readTree(Files.readString(path, StandardCharsets.UTF_8));
      json.set("notebook", notebook);
      json.put("name", path.getFileName().toString());
    } catch (Exception ex) {
      LOGGER.warn("Cannot read notebook {}", fileName, ex);
      json.putNull("notebook");
      json.put("error", "Cannot read notebook '" + fileName + "': " + ex.getMessage());
    }
    out.println(json.toString());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    prepare(res);
    PrintWriter out = res.getWriter();
    ObjectNode json = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    ArrayNode html = JSONBuilder.JSON_OBJECT_MAPPER.createArrayNode();

    String markdown = req.getParameter("markdown");
    if (markdown == null) {
      json.put("error", "No markdown parameter posted!");
      json.set("html", html);
      out.println(json.toString());
      return;
    }

    try {
      JsonNode sources = JSONBuilder.JSON_OBJECT_MAPPER.readTree(markdown);
      if (!sources.isArray()) {
        throw new IOException("markdown parameter is not a JSON array");
      }
      for (JsonNode source : sources) {
        html.add(renderMarkdown(source.isTextual() ? source.textValue() : ""));
      }
    } catch (Exception ex) {
      LOGGER.warn("Cannot render markdown cells", ex);
      json.put("error", "Cannot render markdown: " + ex.getMessage());
    }
    json.set("html", html);
    out.println(json.toString());
  }

  /**
   * Render one Markdown cell source to HTML. Raw HTML in the source is escaped and link URLs are
   * sanitized, so the result carries no markup of its own.
   *
   * @param source the Markdown source of a notebook cell
   */
  public static String renderMarkdown(String source) {
    if (source.length() > MAX_MARKDOWN_LENGTH) {
      return "<p>Markdown cell is too large to render.</p>";
    }
    Node document = MARKDOWN_PARSER.parse(source);
    return MARKDOWN_RENDERER.render(document);
  }

  private static void prepare(HttpServletResponse res) {
    res.setContentType("application/json; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
  }
}
