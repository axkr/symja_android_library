package org.matheclipse.core.doctools;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;
import org.xhtmlrenderer.pdf.ITextRenderer;
import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.DocumentLimits;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.view.FloatSize;

/**
 * Converts the Symja Markdown documentation files into a single PDF. It processes both general docs
 * and function reference docs, converting them into HTML and then rendering them as a PDF.
 */
public class MD2PDF {

  public MD2PDF() {}

  public static void generatePDF(final File sourceLocationDocs, final File sourceLocationFunction,
      File targetLocation) {
    List<Extension> EXTENSIONS = Arrays.asList( //
        TablesExtension.create());
    Parser parser = Parser.builder() //
        .extensions(EXTENSIONS).build();

    StringBuilder htmlBuilder = new StringBuilder();

    htmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    htmlBuilder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ")
        .append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
    htmlBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
    htmlBuilder.append("<head>\n<style>\n");
    htmlBuilder.append("body { font-family: sans-serif; line-height: 1.5; }\n");
    htmlBuilder
        .append("code { font-family: monospace; background-color: #f4f4f4; padding: 2px; }\n");
    htmlBuilder
        .append("pre { background-color: #f4f4f4; padding: 10px; border: 1px solid #ddd; }\n");
    htmlBuilder.append("h1, h2, h3 { color: #333; }\n");
    htmlBuilder.append("a { color: #0366d6; text-decoration: none; }\n"); // Add styling for links
    htmlBuilder.append("</style>\n</head>\n<body>\n");

    // Create a custom renderer that intercepts links and converts them to internal anchors
    HtmlRenderer renderer =
        HtmlRenderer.builder().attributeProviderFactory(new AttributeProviderFactory() {
          @Override
          public AttributeProvider create(AttributeProviderContext context) {
            return new AttributeProvider() {
              @Override
              public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
                if (node instanceof Link) {
                  String href = attributes.get("href");
                  if (href != null && href.endsWith(".md")) {
                    // Convert "NIntegrate.md" or "../functions/Sin.md" to "#NIntegrate"
                    String target = href.substring(href.lastIndexOf('/') + 1, href.length() - 3);
                    attributes.put("href", "#" + target);
                  }
                }
              }
            };
          }
        }).build();

    try {
      // 1. Process General Docs
      if (sourceLocationDocs.exists()) {
        String[] files = sourceLocationDocs.list();
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".md")) {
              File sourceFile = new File(sourceLocationDocs, files[i]);
              String docName = files[i].substring(0, files[i].length() - 3);
              appendMarkdownAsHtml(sourceFile, docName, parser, renderer, htmlBuilder);
            }
          }
        }
      }


      // 2. Process Functions
      if (sourceLocationFunction.exists()) {
        String[] files = sourceLocationFunction.list();
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".md")) {
              File sourceFile = new File(sourceLocationFunction, files[i]);
              String functionName = files[i].substring(0, files[i].length() - 3);
              String identifier = F.symbolNameNormalized(functionName);
              ISymbol symbol = Context.SYSTEM.get(identifier);

              if (symbol != null) {
                htmlBuilder.append("<div style=\"page-break-before: always;\"></div>\n");
                appendMarkdownAsHtml(sourceFile, functionName, parser, renderer, htmlBuilder);
              }
            }
          }
        }
      }

      htmlBuilder.append("</body>\n</html>");

      if (!targetLocation.exists()) {
        targetLocation.mkdirs();
      }

      File outputPdf = new File(targetLocation, "SymjaDocumentation.pdf");
      try (OutputStream os = new FileOutputStream(outputPdf)) {
        ITextRenderer textRenderer = new ITextRenderer();

        String finalHtml = htmlBuilder.toString().replace("<br>", "<br/>").replace("<hr>", "<hr/>")
            .replace("img", "img alt=\"\"");

        // Convert Unicode entities to Twemoji image tags
        finalHtml = finalHtml.replace("&#x2705;",
            "<img src=\"https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/72x72/2705.png\" style=\"width:12px; height:12px;\" alt=\"Supported\"/>")
            .replace("&#x2611;",
                "<img src=\"https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/72x72/2611.png\" style=\"width:12px; height:12px;\" alt=\"Partial\"/>")
            .replace("&#x274C;",
                "<img src=\"https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/72x72/274c.png\" style=\"width:12px; height:12px;\" alt=\"Unsupported\"/>")
            .replace("&#x26A0;",
                "<img src=\"https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/72x72/26a0.png\" style=\"width:12px; height:12px;\" alt=\"Deprecated\"/>")
            .replace("&#x1F9EA;",
                "<img src=\"https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/72x72/1f9ea.png\" style=\"width:12px; height:12px;\" alt=\"Experimental\"/>")
            .replace("&#x2615;",
                "<img src=\"https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/72x72/2615.png\" style=\"width:12px; height:12px;\" alt=\"JVM\"/>")
            .replace("&#x1F504;",
                "<img src=\"https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/72x72/1f504.png\" style=\"width:12px; height:12px;\" alt=\"Alias\"/>");

        finalHtml = finalHtml.replace("&#x229E;", "[+]");

        // NEW: Intercept and convert any inline <svg> blocks into <img src="file://..."> pointing
        // to generated PNGs
        finalHtml = processInlineSvgs(finalHtml, targetLocation);

        textRenderer.setDocumentFromString(finalHtml);
        textRenderer.layout();
        textRenderer.createPDF(os);

        System.out.println("PDF successfully created at: " + outputPdf.getAbsolutePath());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Scans the HTML string for inline <svg>...</svg> blocks, parses them using jsvg, saves them as
   * temporary PNGs, and replaces the inline SVG with an standard HTML <img> tag.
   */
  private static String processInlineSvgs(String html, File tempImageDirectory) {
    // (?s) allows the dot to match newlines, grabbing multi-line <svg> blocks
    Pattern svgPattern = Pattern.compile("(?s)<svg.*?>.*?</svg>");
    Matcher matcher = svgPattern.matcher(html);
    StringBuffer resultHtml = new StringBuffer();

    SVGLoader loader = new SVGLoader();

    DocumentLimits limits = new DocumentLimits(//
        DocumentLimits.DEFAULT_MAX_NESTING_DEPTH, //
        DocumentLimits.DEFAULT_MAX_USE_NESTING_DEPTH, //
        100000);

    LoaderContext context = LoaderContext.builder().documentLimits(limits).build();

    while (matcher.find()) {
      String svgContent = matcher.group();

      try {
        // Read the SVG string directly into jsvg
        ByteArrayInputStream inputStream =
            new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8));
        // SVGDocument svgDocument = loader.load(inputStream);
        SVGDocument svgDocument = loader.load(inputStream, null, context);

        if (svgDocument != null) {
          // Get natural dimensions
          FloatSize size = svgDocument.size();
          int width = (int) Math.ceil(size.width);
          int height = (int) Math.ceil(size.height);

          // Fallback if dimensions are missing/invalid in the SVG string
          if (width <= 0 || height <= 0) {
            width = 300;
            height = 300;
          }

          // Rasterize to BufferedImage
          BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          Graphics2D g2d = image.createGraphics();

          try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            svgDocument.render(null, g2d);
          } finally {
            g2d.dispose();
          }

          // Save the rasterized PNG to the output directory using a unique ID
          File tempPngFile =
              new File(tempImageDirectory, "inline_svg_" + UUID.randomUUID().toString() + ".png");
          ImageIO.write(image, "png", tempPngFile);

          // Replace the <svg> block with an <img> tag pointing to the new PNG URI
          String imgReplacement = "<img src=\"" + tempPngFile.toURI().toString() + "\" width=\""
              + width + "\" height=\"" + height + "\" alt=\"SVG Graphic\" />";

          // Use Matcher.quoteReplacement to safely append without regex group conflicts
          matcher.appendReplacement(resultHtml, Matcher.quoteReplacement(imgReplacement));
        } else {
          // If parsing fails silently, put the original content back
          matcher.appendReplacement(resultHtml, Matcher.quoteReplacement(svgContent));
        }
      } catch (Exception e) {
        System.err.println("Failed to rasterize inline SVG. Leaving original code block.");
        e.printStackTrace();
        matcher.appendReplacement(resultHtml, Matcher.quoteReplacement(svgContent));
      }
    }

    matcher.appendTail(resultHtml);
    return resultHtml.toString();
  }

  /**
   * Reads a Markdown file, parses it, and appends the rendered HTML to the StringBuilder. Wraps the
   * content in a div with an ID so internal PDF links can jump here.
   */
  private static void appendMarkdownAsHtml(File markdownFile, String sectionId, Parser parser,
      HtmlRenderer renderer, StringBuilder htmlBuilder) throws IOException {
    String content = new String(Files.readAllBytes(markdownFile.toPath()), StandardCharsets.UTF_8);
    Node document = parser.parse(content);

    // Inject the sectionId as the id attribute of the wrapper div
    htmlBuilder.append("<div class=\"section\" id=\"").append(sectionId).append("\">\n");
    htmlBuilder.append(renderer.render(document));
    htmlBuilder.append("\n</div>\n<hr/>\n");
  }

  /** Add a github link to the implementation of a Symja function. */
  public static void main(final String[] args) {
    F.initSymja();

    System.out.println("Create PDF from Markdown files.");

    String userHome = System.getProperty("user.home");
    File sourceLocationDocs =
        new File(userHome + "\\git\\symja_android_library\\symja_android_library\\doc");
    File sourceLocation =
        new File(userHome + "\\git\\symja_android_library\\symja_android_library\\doc\\functions");
    File targetLocation =
        new File(userHome + "\\git\\symja_android_library\\symja_android_library\\pdf");

    generatePDF(sourceLocationDocs, sourceLocation, targetLocation);
  }
}
