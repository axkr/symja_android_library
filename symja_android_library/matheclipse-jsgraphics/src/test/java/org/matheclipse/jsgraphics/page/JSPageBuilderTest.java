package org.matheclipse.jsgraphics.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.form.output.OutputFormats;
import org.matheclipse.jsgraphics.page.JSPageBuilder.Library;

public class JSPageBuilderTest {

  private static final String[] TYPES = {OutputFormats.ECHARTS_STR, OutputFormats.JSXGRAPH_STR,
      OutputFormats.MATHCELL_STR, OutputFormats.MERMAID_STR, OutputFormats.PLOTLY_STR};

  @Test
  public void everyTypeIsAnIFrameLoadingItsLibraryFromACdn() {
    for (String type : TYPES) {
      String iframe = JSPageBuilder.INSTANCE.iframe(type, "var x = 1;");
      assertNotNull(iframe, type);
      assertTrue(iframe.startsWith("<iframe srcdoc=\""), type);
      assertTrue(iframe.contains("sandbox=\"" + JSPageBuilder.SANDBOX + "\""), type);
      assertFalse(iframe.contains("allow-same-origin"), type);
      String page = StringEscapeUtils.unescapeHtml4(srcdoc(iframe));
      for (String src : Library.of(type).js) {
        assertTrue(src.startsWith("https://"), src);
        assertTrue(page.contains(src), type + " does not load " + src);
      }
      // never the servlet's own copies of a library
      assertFalse(page.contains("/media/js/"), type);
    }
  }

  @Test
  public void unknownTypeHasNoPage() {
    assertNull(JSPageBuilder.INSTANCE.page("treeform", "x"));
    assertNull(JSPageBuilder.INSTANCE.iframe("nonsense", "x"));
  }

  @Test
  public void onlyTheFramedPageReportsItsHeight() {
    String js = "var x = 1;";
    assertTrue(JSPageBuilder.INSTANCE.page(OutputFormats.JSXGRAPH_STR, js).contains("<div id=\"jxgbox\""));
    assertFalse(JSPageBuilder.INSTANCE.page(OutputFormats.JSXGRAPH_STR, js).contains("symjaFrameHeight"));
    String framed = StringEscapeUtils
        .unescapeHtml4(srcdoc(JSPageBuilder.INSTANCE.iframe(OutputFormats.JSXGRAPH_STR, js)));
    assertTrue(framed.contains("symjaFrameHeight"));
  }

  @Test
  public void scriptCannotCloseItsOwnElement() {
    String page = JSPageBuilder.INSTANCE.page(OutputFormats.ECHARTS_STR, "var s = '</script>';");
    assertTrue(page.contains("var s = '<\\/script>';"));
  }

  @Test
  public void jsFiddlePostsToThePureLibraryEndpoint() {
    boolean old = Config.DISPLAY_JSFIDDLE_BUTTON;
    try {
      Config.DISPLAY_JSFIDDLE_BUTTON = true;
      String page = JSPageBuilder.INSTANCE.page(OutputFormats.JSXGRAPH_STR, "var a = 1 < 2 && \"q\";");
      assertTrue(page.contains("action=\"https://jsfiddle.net/api/post/library/pure/\""));
      assertTrue(page.contains("<input type=\"hidden\" name=\"wrap\" value=\"b\">"));
      // the script is escaped inside its textarea, so the form survives any source text
      assertEquals("var a = 1 < 2 && \"q\";", textarea(page, "js"));
      assertEquals("<div id=\"jxgbox\" class=\"jxgbox\" style=\"width: 100%; max-width: 600px; aspect-ratio: 1 / 1;\"></div>",
          textarea(page, "html"));
      // style sheets and scripts, in the order the page loads them
      assertEquals(String.join(",", "https://cdn.jsdelivr.net/npm/jsxgraph@1.13.3/distrib/jsxgraph.css",
          "https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.11/build/math.js",
          "https://cdn.jsdelivr.net/npm/jsxgraph@1.13.3/distrib/jsxgraphcore.js"),
          textarea(page, "resources"));
    } finally {
      Config.DISPLAY_JSFIDDLE_BUTTON = old;
    }
  }

  @Test
  public void mathCellFiddleKeepsTheScriptInsideItsContainer() {
    boolean old = Config.DISPLAY_JSFIDDLE_BUTTON;
    try {
      Config.DISPLAY_JSFIDDLE_BUTTON = true;
      String page = JSPageBuilder.INSTANCE.page(OutputFormats.MATHCELL_STR, "MathCell(id, []);");
      String html = textarea(page, "html");
      assertTrue(html.startsWith("<div class=\"mathcell\""), html);
      assertTrue(html.contains("<script>\nMathCell(id, []);\n</script>\n</div>"), html);
      assertEquals("", textarea(page, "js"));
    } finally {
      Config.DISPLAY_JSFIDDLE_BUTTON = old;
    }
  }

  @Test
  public void mermaidImportsItsModuleAndHasAFiddle() {
    boolean old = Config.DISPLAY_JSFIDDLE_BUTTON;
    try {
      Config.DISPLAY_JSFIDDLE_BUTTON = true;
      String diagram = "<pre class=\"mermaid\">graph TD; A-->B;</pre>";
      String page = JSPageBuilder.INSTANCE.page(OutputFormats.MERMAID_STR, diagram);
      assertTrue(page.contains("<body>\n" + diagram));
      assertTrue(page.contains("import mermaid from \"https://cdn.jsdelivr.net/npm/mermaid@10"));
      String html = textarea(page, "html");
      assertTrue(html.startsWith(diagram) && html.contains("mermaid.initialize"), html);
      assertEquals("", textarea(page, "resources"));
    } finally {
      Config.DISPLAY_JSFIDDLE_BUTTON = old;
    }
  }

  @Test
  public void noFiddleWhenSwitchedOff() {
    boolean old = Config.DISPLAY_JSFIDDLE_BUTTON;
    try {
      Config.DISPLAY_JSFIDDLE_BUTTON = false;
      assertFalse(JSPageBuilder.INSTANCE.page(OutputFormats.ECHARTS_STR, "x").contains("jsfiddle"));
    } finally {
      Config.DISPLAY_JSFIDDLE_BUTTON = old;
    }
  }

  private static String srcdoc(String iframe) {
    int start = iframe.indexOf("srcdoc=\"") + "srcdoc=\"".length();
    return iframe.substring(start, iframe.indexOf('"', start));
  }

  /** The decoded content of the named textarea of the JSFiddle form. */
  private static String textarea(String page, String name) {
    String open = "<textarea name=\"" + name + "\" style=\"display: none;\">";
    int start = page.indexOf(open);
    assertTrue(start >= 0, "no textarea " + name);
    start += open.length();
    return StringEscapeUtils.unescapeHtml4(page.substring(start, page.indexOf("</textarea>", start)));
  }

  /**
   * The formulas in the labels of a MathCell page are set by KaTeX - two scripts - and no longer
   * by MathJax 2, which fetched its parts as it went. Only that page typesets anything.
   */
  @Test
  public void aMathCellPageSetsItsLabelsWithKaTeX() {
    String page = JSPageBuilder.INSTANCE.page(OutputFormats.MATHCELL_STR, "MathCell(id, []);");
    assertFalse(page.contains("MathJax"), page);
    assertTrue(page.contains("/katex@0.18.4/dist/katex.min.js"), page);
    assertTrue(page.contains("/katex@0.18.4/dist/contrib/auto-render.min.js"), page);
    assertTrue(page.contains("renderMathInElement(document.body"), page);
    assertTrue(page.contains("output: 'mathml'"), "no stylesheet and no fonts to fetch");
    assertTrue(page.contains("{ left: '\\\\(', right: '\\\\)', display: false }"), page);
    // the libraries first, the typesetting once the cell has built its controls
    assertTrue(page.indexOf("mathcell.js") < page.indexOf("katex.min.js"));
    assertTrue(page.indexOf("katex.min.js") < page.indexOf("renderMathInElement"));

    String other = JSPageBuilder.INSTANCE.page(OutputFormats.JSXGRAPH_STR, "var b = 1;");
    assertFalse(other.contains("katex"), other);
    assertFalse(other.contains("renderMathInElement"), other);
  }
}
