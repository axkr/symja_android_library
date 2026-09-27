package org.matheclipse.jsgraphics.page;

import org.apache.commons.text.StringEscapeUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.form.output.JSPageProvider;
import org.matheclipse.core.form.output.OutputFormats;

/**
 * Builds the HTML page that shows a JavaScript graphic, and the sandboxed
 * <code>&lt;iframe&gt;</code> that page is delivered in.
 *
 * <p>
 * This class is the only place that knows the libraries: for each one the element it draws into,
 * and the CDN addresses its scripts and style sheets are loaded from. The renderers of this module
 * produce nothing but the JavaScript that runs on the page. The same address list goes into the
 * JSFiddle button, so a fiddle loads exactly the libraries the page did.
 */
public final class JSPageBuilder implements JSPageProvider {

  public static final JSPageBuilder INSTANCE = new JSPageBuilder();

  /** Past this many characters of JavaScript no JSFiddle button is offered. */
  static final int MAX_JSFIDDLE_SOURCE_CODE = 200000;

  /** JSFiddle's endpoint for creating a fiddle from a POST request, without a framework. */
  static final String JSFIDDLE_URL = "https://jsfiddle.net/api/post/library/pure/";

  /**
   * What the generated script may do inside its frame: run, and open JSFiddle in a new window.
   * Deliberately not <code>allow-same-origin</code>, so the script runs in an opaque origin and
   * cannot reach the cookies or the DOM of the page the frame sits in.
   */
  static final String SANDBOX =
      "allow-scripts allow-forms allow-popups allow-popups-to-escape-sandbox";

  private static final String MATH_JS = "https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.11/build/math.js";

  /** A JavaScript library, the element it draws into and where it is loaded from. */
  enum Library {
    ECHARTS(OutputFormats.ECHARTS_STR, "ECharts", //
        "<div id=\"main\" style=\"width: 100%; height: 400px;\">", Body.SCRIPT_AFTER, 440, //
        new String[0], //
        new String[] {"https://cdn.jsdelivr.net/npm/echarts@6.0.0/dist/echarts.min.js"}),

    // the renderer sets the aspect ratio of the picture on the box before the board is created
    JSXGRAPH(OutputFormats.JSXGRAPH_STR, "JSXGraph", //
        "<div id=\"jxgbox\" class=\"jxgbox\" style=\"width: 100%; max-width: 600px; aspect-ratio: 1 / 1;\">",
        Body.SCRIPT_AFTER, 480, //
        new String[] {"https://cdn.jsdelivr.net/npm/jsxgraph@1.13.3/distrib/jsxgraph.css"}, //
        new String[] {MATH_JS, "https://cdn.jsdelivr.net/npm/jsxgraph@1.13.3/distrib/jsxgraphcore.js"}),

    // MathCell finds its container as the parent of the running script, so the script goes inside
    MATHCELL(OutputFormats.MATHCELL_STR, "MathCell", //
        "<div class=\"mathcell\" style=\"width: 100%; height: 400px;\">", Body.SCRIPT_INSIDE, 440, //
        new String[0], //
        new String[] {MATH_JS, "https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.10.4/build/mathcell.js",
            "https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML"}),

    // a diagram is markup, not a script: the body is put on the page as it is
    MERMAID(OutputFormats.MERMAID_STR, "Mermaid", //
        "", Body.MARKUP, 440, //
        new String[0], //
        new String[] {"https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs"}),

    PLOTLY(OutputFormats.PLOTLY_STR, "Plotly", //
        "<div id=\"plotly\" style=\"width: 100%; height: 400px;\">", Body.SCRIPT_AFTER, 440, //
        new String[0], //
        new String[] {"https://cdn.plot.ly/plotly-2.35.2.min.js"});

    final String type;
    final String title;
    /** The opening tag of the element the library draws into; empty for {@link Body#MARKUP}. */
    final String container;
    final Body body;
    /** Height of the frame before the page has reported its own, in pixels. */
    final int height;
    final String[] css;
    final String[] js;

    Library(String type, String title, String container, Body body, int height, String[] css,
        String[] js) {
      this.type = type;
      this.title = title;
      this.container = container;
      this.body = body;
      this.height = height;
      this.css = css;
      this.js = js;
    }

    static Library of(String type) {
      for (Library library : values()) {
        if (library.type.equals(type)) {
          return library;
        }
      }
      return null;
    }
  }

  /** How the text of a <code>JSFormData</code> goes onto the page. */
  enum Body {
    /** a script run after the container element */
    SCRIPT_AFTER,
    /** a script placed inside the container element */
    SCRIPT_INSIDE,
    /** HTML markup, placed as it is */
    MARKUP
  }

  /**
   * Reports the height of the page to the frame it is shown in, so the host page can size the frame
   * to the picture. A message is the only way: the sandbox keeps the host from reading the frame.
   */
  private static final String REPORT_HEIGHT = "<script>\n" //
      + "(function () {\n" //
      // MathCell's own examples declare a global "var parent", which hides window.parent
      + "  var host = window.parent && typeof window.parent.postMessage === 'function'\n" //
      + "      ? window.parent : window.top;\n" //
      + "  if (host === window) return;\n" //
      + "  function report() {\n" //
      + "    var h = Math.ceil(document.documentElement.getBoundingClientRect().height) + 2;\n" //
      + "    host.postMessage({ symjaFrameHeight: h }, '*');\n" //
      + "  }\n" //
      + "  window.addEventListener('load', function () { report(); setTimeout(report, 500); });\n" //
      + "})();\n" //
      + "</script>\n";

  private JSPageBuilder() {}

  /** Whether a page can be built for {@code type}. */
  public static boolean isSupported(String type) {
    return Library.of(type) != null;
  }

  @Override
  public String page(String type, String js) {
    Library library = Library.of(type);
    return library == null ? null : buildPage(library, js, false);
  }

  @Override
  public String iframe(String type, String js) {
    Library library = Library.of(type);
    if (library == null) {
      return null;
    }
    String page = buildPage(library, js, true);
    return "<iframe srcdoc=\"" + StringEscapeUtils.escapeHtml4(page) + "\" sandbox=\"" + SANDBOX
        + "\" class=\"symja-iframe\" data-height=\"" + library.height
        + "\" style=\"display: block; width: 100%; height: " + library.height
        + "px; border: none;\"></iframe>";
  }

  /**
   * @param embedded true for the page of an iframe, which reports its height to the host page
   */
  static String buildPage(Library library, String js, boolean embedded) {
    StringBuilder buf = new StringBuilder(js.length() + 2048);
    buf.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"utf-8\">\n<title>")
        .append(library.title).append("</title>\n")
        .append("<style>html, body { margin: 0; padding: 0; }")
        // the frame is sized to the page, so a scroll bar would only ever cover a few pixels
        .append(embedded ? " html { overflow: hidden; }" : "").append("</style>\n");
    for (String css : library.css) {
      buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(css)
          .append("\"/>\n");
    }
    if (library.body == Body.MARKUP) {
      buf.append(moduleImport(library));
    } else {
      for (String src : library.js) {
        buf.append("<script src=\"").append(src).append("\"></script>\n");
      }
    }
    buf.append("</head>\n<body>\n");
    buf.append(bodyHtml(library, js));
    if (Config.DISPLAY_JSFIDDLE_BUTTON && js.length() < MAX_JSFIDDLE_SOURCE_CODE) {
      buf.append(jsFiddleForm(library, js));
    }
    if (embedded) {
      buf.append(REPORT_HEIGHT);
    }
    buf.append("</body>\n</html>\n");
    return buf.toString();
  }

  private static String bodyHtml(Library library, String js) {
    switch (library.body) {
      case SCRIPT_INSIDE:
        return library.container + "\n<script>\n" + scriptText(js) + "\n</script>\n</div>\n";
      case MARKUP:
        return js + "\n";
      default:
        return library.container + "</div>\n<script>\n" + scriptText(js) + "\n</script>\n";
    }
  }

  /** ES modules cannot be listed as plain scripts; Mermaid is imported and started. */
  private static String moduleImport(Library library) {
    return "<script type=\"module\">\nimport mermaid from \"" + library.js[0] + "\";\n"
        + "mermaid.initialize({ startOnLoad: true });\n</script>\n";
  }

  /**
   * The script as it may stand between <code>&lt;script&gt;</code> tags: a
   * <code>&lt;/script</code> in a string literal (a label, say) would otherwise end the element.
   */
  static String scriptText(String js) {
    return js.replace("</script", "<\\/script").replace("</SCRIPT", "<\\/SCRIPT");
  }

  /**
   * A button that opens the page in JSFiddle, through JSFiddle's POST API.
   *
   * <p>
   * The HTML, script and resource list are posted separately, as JSFiddle keeps them in separate
   * panels. With <code>wrap=b</code> the script runs at the end of the body, after the container
   * element exists, which is where it runs on the page too. A MathCell script has to sit inside its
   * container, and a Mermaid diagram is markup with a module import, so for those two everything
   * goes into the HTML panel.
   */
  static String jsFiddleForm(Library library, String js) {
    String html;
    String script;
    String resources;
    switch (library.body) {
      case SCRIPT_INSIDE:
        html = library.container + "\n<script>\n" + scriptText(js) + "\n</script>\n</div>";
        script = "";
        resources = String.join(",", concat(library.css, library.js));
        break;
      case MARKUP:
        html = js + "\n" + moduleImport(library);
        script = "";
        resources = "";
        break;
      default:
        html = library.container + "</div>";
        script = js;
        resources = String.join(",", concat(library.css, library.js));
        break;
    }
    return "<form method=\"post\" action=\"" + JSFIDDLE_URL
        + "\" target=\"_blank\" style=\"margin: 0; padding: 4px 0;\">\n" //
        + "<input type=\"hidden\" name=\"title\" value=\"Symja - " + library.title + "\">\n" //
        + "<input type=\"hidden\" name=\"wrap\" value=\"b\">\n" //
        + "<textarea name=\"html\" style=\"display: none;\">"
        + StringEscapeUtils.escapeHtml4(html) + "</textarea>\n" //
        + "<textarea name=\"js\" style=\"display: none;\">"
        + StringEscapeUtils.escapeHtml4(script) + "</textarea>\n" //
        + "<textarea name=\"resources\" style=\"display: none;\">"
        + StringEscapeUtils.escapeHtml4(resources) + "</textarea>\n" //
        + "<button type=\"submit\" style=\"background-color: lightblue;\">JSFiddle</button>\n" //
        + "</form>\n";
  }

  private static String[] concat(String[] a, String[] b) {
    String[] result = new String[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }
}
