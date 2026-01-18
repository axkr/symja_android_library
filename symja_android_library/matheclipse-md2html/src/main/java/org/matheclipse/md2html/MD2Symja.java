package org.matheclipse.md2html;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.image.ImageInit;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class MD2Symja {

  public static void init() {
    // set for only small prime factorization
    // Config.PRIME_FACTORS = new Primality();

    // set for BigInteger prime factorization
    Config.PRIME_FACTORS = new BigIntegerPrimality();

    // initialize from module matheclipse-image:
    ImageInit.init();
  }

  public static String generateHTMLString(final String markdownStr) {
    List<Extension> EXTENSIONS = Arrays.asList( //
        // TeXExtension.create(),
        TablesExtension.create());
    Parser parser = Parser.builder() //
        .extensions(EXTENSIONS).build();
    Node document = parser.parse(markdownStr);

    HtmlRenderer renderer = HtmlRenderer.builder() //
        .extensions(EXTENSIONS).nodeRendererFactory(new HtmlNodeRendererFactory() {
          @Override
          public NodeRenderer create(HtmlNodeRendererContext context) {
            return new DocNodeRenderer(context);
          }
        }).build();
    return renderer.render(document);
  }

  public static void main(String[] args) {
    init();

    CharSource source = null;
    if (args != null && args.length == 2 //
        && args[0].equals("-f") && args[1] instanceof String) {
      File file = new File(args[1]);
      source = Files.asCharSource(file, StandardCharsets.UTF_8);
    } else {
      URL resource = MD2Symja.class.getClassLoader().getResource("public/graphicstest.md");
      source = Resources.asCharSource(resource, StandardCharsets.UTF_8);
      renderHTML(source);

      resource = MD2Symja.class.getClassLoader().getResource("public/graphics3Dtest.md");
      source = Resources.asCharSource(resource, StandardCharsets.UTF_8);
      renderHTML(source);
      return;
    }
    renderHTML(source);
  }

  private static void renderHTML(CharSource source) {
    if (source != null) {
      String html;
      try {
        html = generateHTMLString(source.read());
      } catch (IOException ioe) {
        ioe.printStackTrace();
        return;
      }

      // Load the HTML template from the file "md2symja.html"
      // Ensure "md2symja.html" is in your classpath (e.g., src/main/resources/)
      URL templateUrl =
          MD2Symja.class.getClassLoader().getResource("public/template/md2symja.html");
      if (templateUrl == null) {
        System.err.println("Error: md2symja.html template not found in resources.");
        return;
      }

      String templateContent;
      try {
        templateContent = Resources.toString(templateUrl, StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }

      // Render the markdown content into the template
      html = Errors.templateRender(templateContent, new String[] {html});

      try {
        F.openHTMLOnDesktop(html);
      } catch (IOException e) {
        System.out.println(html);
        e.printStackTrace();
      }
    }
  }
}