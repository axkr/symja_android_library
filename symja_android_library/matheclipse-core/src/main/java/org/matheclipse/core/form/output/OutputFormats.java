package org.matheclipse.core.form.output;

/**
 * The names of the output formats a result can be requested in or delivered as.
 *
 * <p>
 * The JavaScript library names double as the second argument of {@code JSFormData[js, "type"]};
 * the page for each of them is built by the optional <code>matheclipse-jsgraphics</code> module,
 * see {@link JSPageProvider}.
 */
public final class OutputFormats {
  public static final String HTML_STR = "html";
  public static final String PLAIN_STR = "plaintext";
  public static final String SYMJA_STR = "sinput";
  public static final String MATHML_STR = "mathml";
  public static final String LATEX_STR = "latex";
  public static final String MARKDOWN_STR = "markdown";

  /** The steps a <code>TraceForm(...)</code> collected, as their own tree. */
  public static final String STEPS_STR = "steps";

  /** A tree drawn with vis-network; the page is {@link HtmlTemplates#VISJS_IFRAME}. */
  public static final String TREEFORM_STR = "treeform";

  public static final String ECHARTS_STR = "echarts";
  public static final String JSXGRAPH_STR = "jsxgraph";
  public static final String MATHCELL_STR = "mathcell";
  public static final String MERMAID_STR = "mermaid";
  public static final String PLOTLY_STR = "plotly";

  private OutputFormats() {}
}
