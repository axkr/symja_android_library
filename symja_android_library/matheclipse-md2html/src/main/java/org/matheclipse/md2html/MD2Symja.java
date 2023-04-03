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
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.expression.F;
import org.matheclipse.gpl.numbertheory.BigIntegerPrimality;
import org.matheclipse.image.ImageInit;
import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class MD2Symja {

  final static String HTML_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
      + "\n" //
      + "<!DOCTYPE html PUBLIC\n" //
      + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n" //
      + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" //
      + "\n" //
      + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" //
      + "\n" //
      + "<head>\n" //
      + "\n" //
      + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n" //
      + "<title>Test Markdown to HTML</title>\n" //
      + " \n" //
      + "<meta name=\"theme-color\" content=\"#ffffff\" />\n" //
      + "    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/katex@0.16.3/dist/katex.min.css\" integrity=\"sha384-Juol1FqnotbkyZUT5Z7gUPjQ9gzlwCENvUZTpQBAPxtusdwFLRy382PSDx5UUJ4/\" crossorigin=\"anonymous\">\n" //
      + "    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/highlightjs@9.16.2/styles/default.css\">\n" //
      + "    <script defer src=\"https://cdn.jsdelivr.net/npm/katex@0.16.3/dist/katex.min.js\" integrity=\"sha384-97gW6UIJxnlKemYavrqDHSX3SiygeOwIZhwyOKRfSaf0JWKRVj9hLASHgFTzT+0O\" crossorigin=\"anonymous\"></script>\n" //
      + "    <script defer src=\"https://cdn.jsdelivr.net/npm/katex@0.16.3/dist/contrib/auto-render.min.js\" integrity=\"sha384-+VBxd3r6XgURycqtZ117nYw44OOcIax56Z4dCRWbxyPt0Koah1uHoK0o4+/RRE05\" crossorigin=\"anonymous\"\n" //
      + "        onload=\"renderMathInElement(document.body);\"></script>" //
      + "    <script src=\"https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.7.0/build/highlight.min.js\"></script>\n" //
      + "    <script src=\"https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.7.0/build/languages/mathematica.min.js\"></script>" //
      + "    <script>hljs.highlightAll();</script>\n" //
      + "\n" //
      + "</head>\n" //
      + "\n" //
      + "<body> \n" //
      + "<div id=\"document\">\n" //
      + "`1`\n" //
      + "</div>\n" //
      + "\n" //
      + "</body>\n" //
      + "</html>";

  public static void init() {
    // set for only small prime factorization
    // Config.PRIME_FACTORS = new Primality();

    // set for BigInteger prime factorization
    Config.PRIME_FACTORS = new BigIntegerPrimality();

    // initialize from module matheclipse-image:
    ImageInit.init();

    // S.Import.setEvaluator(new org.matheclipse.io.builtin.Import());
    // S.SemanticImport.setEvaluator(new org.matheclipse.io.builtin.SemanticImport());
    // S.SemanticImportString.setEvaluator(new org.matheclipse.io.builtin.SemanticImportString());
    // FileIOFunctions.initialize();
    // DynamicSwingFunctions.initialize();
    // SwingFunctions.initialize();
    // DatasetFunctions.initialize();
    // BioFunctions.initialize();

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

  private static String MARKDOWN_TEST = //
      "An example of a `ListPlot` function\n"//
          + "```mma\n"//
          + "ListPlot3D[{{1, 1, 1, 1}, {1, 2, 1, 2}, {1, 1, 3, 1}, {1, 2, 1, 4}}]\n"//
          + "```\n"//
          + "\n"//
          + "```mma\n"//
          + "Plot3D[Sin[x + y^2], {x, -3, 3}, {y, -2, 2}]\n"//
          + "```\n"//
          + "\n"//
          + "```mma\n"//
          + "Plot[Sin[x], {x, -Pi, Pi}]\n"//
          + "```\n"//
          + "\n"//
          + "Graphics3D object:\n"//
          + "```mma\n"//
          + "Graphics3D[Sphere[{0, 1, 0}]]\n"//
          + "```\n"//
          + "\n"//
          + "```mma\n"//
          + "Image[RandomVariate[NormalDistribution[.5, .1], {100, 100}]]\n"//
          + "```\n"//
          + "\n"//
          + "```mma\n"//
          + "Graph[{1,2,3,4,6,5,7,8},{1->2,1->3,1->4,2->6,5->3,5->7,5->8}]\n"//
          + "```\n"//
          + "\n"//
          + "```mma\n"//
          + "TreeForm[a+(b*q*s)^(2*y)+Sin[c]^(3-z)]\n"//
          + "```\n"//
          + "\n"//
          //
          + "use inline tex \\\\(\\sqrt x\\\\)\n"//
          + "or display form TeX:\n"//
          + "```tex\n"//
          + "1/3 + y/x > 0\n"//
          + "```";

  public static void main(String[] args) {
    init();

    CharSource source = null;
    if (args != null && args.length == 2 //
        && args[0].equals("-f") && args[1] instanceof String) {
      File file = new File(args[1]);
      source = Files.asCharSource(file, Charsets.UTF_8);
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
    // InputStream is =
    // MD2Symja.class.getClassLoader().getResourceAsStream("public/graphicstest.md");
    // if (is != null) {
    // BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    // StringBuilder sb = new StringBuilder();
    // while (true) {
    // try {
    // String readLine = in.readLine();
    // if (readLine != null) {
    // sb.append(readLine + "\n");
    // } else {
    // break;
    // }
    // } catch (IOException e) {
    // break;
    // }
    // }
    // // String html = generateHTMLString(MARKDOWN_TEST);
    // String html = generateHTMLString(sb.toString());
    //
    // html = IOFunctions.templateRender(HTML_TEMPLATE, new String[] {html});
    // // html = StringEscapeUtils.escapeHtml4(html);
    // System.out.println(html);
    // try {
    // F.openHTMLOnDesktop(html);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }


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

      html = IOFunctions.templateRender(HTML_TEMPLATE, new String[] {html});
      // html = StringEscapeUtils.escapeHtml4(html);
      System.out.println(html);
      try {
        F.openHTMLOnDesktop(html);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
