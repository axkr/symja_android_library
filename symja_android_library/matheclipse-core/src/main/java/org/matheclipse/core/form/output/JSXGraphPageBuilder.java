package org.matheclipse.core.form.output;

import org.matheclipse.core.builtin.IOFunctions;

/**
 * Build a page to show a JSXGraph graphic.
 *
 * <p>See: <a href="https://www.jsxgraph.org">JSXGraph.org</a>
 */
public class JSXGraphPageBuilder {
  /** Maximum size in byte for jsFiddle */
  private static final int MAX_JSFIDDLE_SOURCE_CODE = 50000;

  public static final String JSXGRAPH_IFRAME_TEMPLATE = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "\n"
          + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n"
          + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n"
          + "<meta charset=\"utf-8\">\n"
          + "<title>JSXGraph</title>\n"
          + "\n"
          + "<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          // 1 - libraries
          + "`1`\n"
          + "<div id=\"jxgbox\" class=\"jxgbox\" style=\"display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n"
          + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n"
          + "</div>\n"
          + "</body>\n"
          + "</html>";

  /** HTML template for JSXGraph */
  public static final String JSXGRAPH_TEMPLATE = //
      "<html>\n"
          + "<head>\n"
          + "<meta charset=\"utf-8\">\n"
          + "<title>JSXGraph</title>\n"
          // 1 - libraries
          + "`1`\n"
          + "</head>\n"
          + "<body>\n"
          + "<div id=\"jxgbox\" class=\"jxgbox\" style=\"display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          // 2 - JavaScript string
          + "`2`\n"
          + "</script>\n"
          // 3 - JSFiddle string
          + "`3`\n"
          + "</div>\n"
          + "</body>\n"
          + "</html>"; //

  /** CSS JavaScript libraries */
  private static final String[] CSS_CDN_STR = {
    "https://cdn.jsdelivr.net/npm/jsxgraph@1.3.2/distrib/jsxgraph.css"
  };

  /** CDN JavaScript libraries */
  private static final String[] JS_CDN_STR = {
    "https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.6/build/math.js",
    "https://cdn.jsdelivr.net/npm/jsxgraph@1.3.2/distrib/jsxgraphcore.js"
  };

  private static final String JSFIDDLE_STR =
      "&nbsp;<form method='post' action='http://jsfiddle.net/api/post/mootools/1.3/dependencies/more/' target='check'>\n"
          + "<button type='submit' style='background-color:lightblue;'>JSFiddle</button>\n"
          // + "<input type='hidden' name=\"panel_html\" value=\"0\">"
          + "<textarea name='html' style='display:none;' ><div id=\"jxgbox\" class=\"jxgbox\" style=\"width:600px; height:400px;\"></div></textarea>\n"
          + "<textarea name='js' style='display:none;'>`1`</textarea>\n"
          // + "<textarea name='css' style='display:none;'></textarea>\n"
          + "<textarea name='resources' style='display:none;'>`2`</textarea>\n"
          + "</form>";

  private JSXGraphPageBuilder() {}

  public static String build(String pageTemplate, String manipulateStr) {
    String[] jsxGraphArgs = new String[3];
    StringBuilder libraries = new StringBuilder();

    // print CDN CSS files
    for (int i = 0; i < CSS_CDN_STR.length; i++) {
      libraries.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
      libraries.append(CSS_CDN_STR[i]);
      libraries.append("\"/>\n");
    }

    // print CDN JavaScript libraries
    for (int i = 0; i < JS_CDN_STR.length; i++) {
      libraries.append("<script src=\"");
      libraries.append(JS_CDN_STR[i]);
      libraries.append("\"></script>\n");
    }

    jsxGraphArgs[0] = libraries.toString();
    jsxGraphArgs[1] = manipulateStr;
    jsxGraphArgs[2] = "";
    if (manipulateStr.length() < MAX_JSFIDDLE_SOURCE_CODE) {
      String[] jsFiddleArgs = new String[2];
      jsFiddleArgs[0] = manipulateStr;
      jsFiddleArgs[1] = String.join(",", JS_CDN_STR);

      jsxGraphArgs[2] = IOFunctions.templateRender(JSFIDDLE_STR, jsFiddleArgs);
    }
    return IOFunctions.templateRender(pageTemplate, jsxGraphArgs);
  }
}
