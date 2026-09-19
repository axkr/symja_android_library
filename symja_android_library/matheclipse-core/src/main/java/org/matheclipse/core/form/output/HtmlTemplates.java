package org.matheclipse.core.form.output;

/**
 * HTML pages that need no JavaScript plotting library: an image, a vis-network tree and a JSON
 * viewer.
 *
 * <p>
 * Placeholders are written <code>`1`</code>, <code>`2`</code>, ... and filled in with
 * {@link org.matheclipse.core.eval.Errors#templateRender(String, String[])}. The pages for the
 * JavaScript plotting libraries (JSXGraph, ECharts, MathCell, ...) are built by the optional
 * <code>matheclipse-jsgraphics</code> module, see {@link JSPageProvider}.
 */
public final class HtmlTemplates {

  public static final String IMAGE_IFRAME_TEMPLATE = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%;margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Image</title>\n"//
          + "</head>\n" + "<body>\n"
          + "<div id=\"image\" style=\"width:100%; height:100%; margin: 0; padding: 0\">\n"
          + "    <img src=\"data:image/png;base64, `1`\"`2`/> \n" //
          + "</div>\n" //
          + "</body>"; //

  /**
   * <code>`1`</code> is the base64 encoded png and <code>`2`</code> is the <code>style</code>
   * attribute the image's <code>ImageSize</code>, <code>ImageResolution</code> and
   * <code>Magnification</code> options come to - empty where they ask for nothing in particular and
   * the image is shown pixel for pixel.
   */
  public static final String IMAGE_TEMPLATE = //
      "<html>\n" //
          + "<head>\n" //
          + "<meta charset=\"utf-8\">\n" //
          + "<title>Image</title>\n" //
          + "</head>\n" //
          + "<body>\n" //
          + "<div id=\"image\" style=\"width:100%; height:100%; margin: 0; padding: 0\">\n" //
          + "    <img src=\"data:image/png;base64, `1`\"`2`/> \n" //
          + "</div>\n" //
          + "</body>\n" + "</html>"; //

  public static final String VISJS_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
          "\n" + //
          "<!DOCTYPE html PUBLIC\n" + //
          "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n" + //
          "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + //
          "\n" + //
          "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + //
          "<head>\n" + //
          "<meta charset=\"utf-8\">\n" + //
          "<title>VIS-NetWork</title>\n" + //
          "\n" + //
          "  <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js\"></script>\n"
          + //
          "</head>\n" + //
          "<body>\n" + //
          "\n" + //
          "<div id=\"vis\" style=\"width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
          + //
          "<script type=\"text/javascript\">\n" + //
          "`1`\n" + //
          "  var container = document.getElementById('vis');\n" + //
          "  var data = {\n" + //
          "    nodes: nodes,\n" + //
          "    edges: edges\n" + //
          "  };\n" + //
          "`2`\n" + //
          "  var network = new vis.Network(container, data, options);\n" + //
          "</script>\n" + //
          "</div>\n" + //
          "</body>\n" + //
          "</html>"; //

  /**
   * Javascript library for displaying json data into a DOM.
   * <p>
   * See: <a href="https://github.com/pgrabovets/json-view">Github json-view</a>
   */
  public static final String JSON_HTML_VIEWER = "<!DOCTYPE html>\n" //
      + "<html>\n" //
      + "  <head>\n" //
      + "    <title>jsonview demo</title>\n" //
      // + " <link\n" //
      // + " href=\"https://fonts.googleapis.com/css?family=Open+Sans\"\n" //
      // + " rel=\"stylesheet\"\n" //
      // + " />\n" //
      + "  </head>\n" //
      + "  <body>\n" //
      + "    <div class=\"root\">  </div>\n" //
      + "\n" //
      + "    <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/@pgrabovets/json-view@2.7.6/dist/jsonview.min.js\"></script>\n" //
      + "    <script type=\"text/javascript\">\n" //
      + "    const data = \"`1`\";\n" //
      + "      const tree = jsonview.create(data);\n" //
      + "      jsonview.render(tree, document.querySelector(\".root\"));\n" //
      + "      jsonview.expand(tree);\n" //
      + "    </script>\n" //
      + "  </body>\n" //
      + "</html>";

  private HtmlTemplates() {}
}
