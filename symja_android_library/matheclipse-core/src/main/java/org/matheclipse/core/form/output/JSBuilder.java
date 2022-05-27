package org.matheclipse.core.form.output;

import org.matheclipse.core.builtin.IOFunctions;

/** Build a page to show a JavaScript graphic. */
public class JSBuilder {
  /** Maximum size in byte for jsFiddle */
  private static final int MAX_JSFIDDLE_SOURCE_CODE = 50000;

  /**
   * HTML template for the <a href="https://github.com/paulmasson/mathcell">MathCell</a> and
   * <a href="https://github.com/paulmasson/math">Math</a> JavaScript libraries.
   */
  public static final String MATHCELL_TEMPLATE = //
      "<html>\n" + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>MathCell</title>\n"
          + "</head>\n" + "\n" + "<body>\n"
          // 1 - libraries
          + "`1`\n"
          + "<div class=\"mathcell\" style=\"width: 100%; height: 100%; padding: .25in .5in .5in .5in;\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</div>\n" + "</body>\n" + "</html>";

  public static final String MATHCELL_IFRAME_TEMPLATE = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>MathCell</title>\n" + "</head>\n"
          + "\n" + "<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          // 1 - libraries
          + "`1`\n"
          + "<div class=\"mathcell\" style=\"display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</div>\n" + "</body>\n" + "</html>";

  public static final String GRAPHICS3D_IFRAME_TEMPLATE = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%;margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Graphics3D</title>\n" + "</head>\n"
          + "\n" + "<body>\n"
          // 1 - libraries
          + "`1`\n" + "<div id=\"graphics3d\"></div>\n" + "\n" + "<script type=\"module\"> \n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</body>"; //

  /** HTML template for Graphics3D */
  public static final String GRAPHICS3D_TEMPLATE = //
      "<html>\n" + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Graphics3D</title>\n"
      // 1 - libraries
          + "`1`\n" + "</head>\n" + "<body>\n"
          + "<div id=\"graphics3d\" style=\"width:100%; height:100%; margin: 0; padding: 0\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</div>\n" + "</body>\n" + "</html>"; //

  public static final String IMAGE_IFRAME_TEMPLATE = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%;margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Image</title>\n"//
          + "</head>\n"
          + "<body>\n"
          + "<div id=\"image\" style=\"width:100%; height:100%; margin: 0; padding: 0\">\n"
          + "    <img src=\"data:image/png;base64, `1`\"/> \n" //
          + "</div>\n" //
          + "</body>"; //

  public static final String IMAGE_TEMPLATE = //
      "<html>\n" + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Image</title>\n"
          + "</head>\n" + "<body>\n"
          + "<div id=\"image\" style=\"width:100%; height:100%; margin: 0; padding: 0\">\n"
          + "    <img src=\"data:image/png;base64, `1`\"/> \n" //
          + "</div>\n" //
          + "</body>\n" + "</html>"; //

  public static final String JSXGRAPH_IFRAME_TEMPLATE = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>JSXGraph</title>\n" + "\n"
          + "<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          // 1 - libraries
          + "`1`\n"
          + "<div id=\"jxgbox\" class=\"jxgbox\" style=\"display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</div>\n" + "</body>\n" + "</html>";

  /** HTML template for JSXGraph */
  public static final String JSXGRAPH_TEMPLATE = //
      "<html>\n" + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>JSXGraph</title>\n"
      // 1 - libraries
          + "`1`\n" + "</head>\n" + "<body>\n"
          + "<div id=\"jxgbox\" class=\"jxgbox\" style=\"display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</div>\n" + "</body>\n" + "</html>"; //

  public static final String PLOTLY_IFRAME_TEMPLATE = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Plotly</title>\n"
          // 1 - libraries
          + "`1`\n" + "</head>\n" + "<body>\n"
          + "<div id='plotly' style=\"display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</div>" + "</body>\n" + "</html>";

  public static final String PLOTLY_TEMPLATE = //
      "<html>\n" + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Plotly</title>\n"
      // 1 - libraries
          + "`1`\n" + "</head>\n" + "<body>\n"
          + "<div id='plotly' style=\"display: flex; width: 100%; height: 100%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n" + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n" + "</div>\n" + "</body>\n" + "</html>"; //

  /** CSS libraries */
  private static final String[] CSS_CDN_GRAPHICS3D = {};

  private static final String[] JS_CDN_GRAPHICS3D = { //
      "https://cdn.jsdelivr.net/npm/@mathicsorg/mathics-threejs-backend"};

  /** CSS libraries */
  private static final String[] CSS_CDN_JSXGRAPH =
      {"https://cdn.jsdelivr.net/npm/jsxgraph@1.4.4/distrib/jsxgraph.css"};

  /** CDN JavaScript libraries */
  private static final String[] JS_CDN_JSXGRAPH =
      {"https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.8/build/math.js",
          "https://cdn.jsdelivr.net/npm/jsxgraph@1.4.4/distrib/jsxgraphcore.js"};

  /** CSS libraries */
  private static final String[] CSS_CDN_MATHCELL = {};

  private static final String[] JS_CDN_MATHCELL =
      {"https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.8/build/math.js",
          "https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.9.2/build/mathcell.js",
          "https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML"};

  /** CSS libraries */
  private static final String[] CSS_CDN_PLOTLY = {};

  private static final String[] JS_CDN_PLOTLY = { //
      "https://cdn.plot.ly/plotly-latest.min.js"};

  private static final String JSFIDDLE_STR =
      "<form method='post' action='https://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check' style='margin-top: auto;'>\n"
          + "<button type='submit' style='background-color:lightblue;'>JSFiddle</button>\n"
          // + "<input type='hidden' name=\"panel_html\" value=\"0\">"
          + "<textarea name='html' style='display:none;'>`1`</textarea>\n"
          + "<textarea name='js' style='display:none;'>`2`</textarea>\n"
          // + "<textarea name='css' style='display:none;'></textarea>\n"
          + "<textarea name='resources' style='display:none;'>`3`</textarea>\n" + "</form>";

  private JSBuilder() {}

  public static String buildGraphics3D(String pageTemplate, String manipulateStr) {
    return build(pageTemplate, manipulateStr,
        "<div id='graphics3d' style=\"width:600px; height:400px;\"></div>", CSS_CDN_GRAPHICS3D,
        JS_CDN_GRAPHICS3D);
  }

  public static String buildJSXGraph(String pageTemplate, String manipulateStr) {
    return build(pageTemplate, manipulateStr,
        "<div id=\"jxgbox\" class=\"jxgbox\" style=\"width:600px; height:400px;\"></div>",
        CSS_CDN_JSXGRAPH, JS_CDN_JSXGRAPH);
  }

  public static String buildMathcell(String pageTemplate, String manipulateStr) {
    // don't append the cloding </div> for jsFiddle to work as expected
    return build(pageTemplate, manipulateStr,
        "<div class=\"mathcell\" style=\"width:600px; height:400px;\">", CSS_CDN_MATHCELL,
        JS_CDN_MATHCELL);
  }

  public static String buildPlotly(String pageTemplate, String manipulateStr) {
    return build(pageTemplate, manipulateStr,
        "<div id='plotly' style=\"width:600px; height:400px;\"></div>", CSS_CDN_PLOTLY,
        JS_CDN_PLOTLY);
  }

  private static String build(String pageTemplate, String manipulateStr, String htmlStr,
      String[] css, String[] libs) {
    String[] jsxGraphArgs = new String[3];
    StringBuilder libraries = new StringBuilder();

    // print CDN CSS files
    for (int i = 0; i < css.length; i++) {
      libraries.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
      libraries.append(css[i]);
      libraries.append("\"/>\n");
    }

    // print CDN JavaScript libraries
    for (int i = 0; i < libs.length; i++) {
      libraries.append("<script src=\"");
      libraries.append(libs[i]);
      libraries.append("\"></script>\n");
    }

    jsxGraphArgs[0] = libraries.toString();
    jsxGraphArgs[1] = manipulateStr;
    jsxGraphArgs[2] = "";
    if (manipulateStr.length() < MAX_JSFIDDLE_SOURCE_CODE) {
      String[] jsFiddleArgs = new String[3];
      jsFiddleArgs[0] = htmlStr;
      jsFiddleArgs[1] = manipulateStr;
      jsFiddleArgs[2] = String.join(",", libs);

      jsxGraphArgs[2] = IOFunctions.templateRender(JSFIDDLE_STR, jsFiddleArgs);
    }
    return IOFunctions.templateRender(pageTemplate, jsxGraphArgs);
  }
}
