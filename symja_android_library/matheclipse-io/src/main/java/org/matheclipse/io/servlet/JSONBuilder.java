package org.matheclipse.io.servlet;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.text.StringEscapeUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONBuilder {

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  /** The string is MathML and is rendered by the browser itself. */
  public static final String FORMAT_MATHML = "mathml";

  /** The string is LaTeX and is rendered in the browser by KaTeX. */
  public static final String FORMAT_LATEX = "latex";

  /** The string is plain text. */
  public static final String FORMAT_TEXT = "text";

  /** The string is plain text that has to keep its spacing, such as a syntax error marker. */
  public static final String FORMAT_CODE = "code";

  /** The string is an HTML snippet - an SVG, an image, an iframe or a WebGL container. */
  public static final String FORMAT_HTML = "html";

  /** The result is an interactive widget: controls plus the first rendering of the body. */
  public static final String FORMAT_MANIPULATE = "manipulate";

  /**
   * Wrap a <code>Manipulate</code> widget: its controls, its options and the rendering of the body
   * for the initial control values.
   *
   * @param id the widget id, used by <code>/ajax/manipulate/</code> to find it again
   * @param spec the parsed specification
   * @param renderedBody the JSON that {@link AJAXQueryServlet#renderResult} produced for the body
   */
  public static String[] createJSONManipulate(String id,
      org.matheclipse.core.manipulate.ManipulateSpec spec, String renderedBody) {
    ObjectNode manipulate = spec.toJSON(JSON_OBJECT_MAPPER);
    manipulate.put("id", id);
    try {
      manipulate.set("body", JSON_OBJECT_MAPPER.readTree(renderedBody));
    } catch (Exception ex) {
      manipulate.putNull("body");
    }

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", 21);
    resultsJSON.put("result", "");
    resultsJSON.put("format", FORMAT_MANIPULATE);
    resultsJSON.set("manipulate", manipulate);
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {FORMAT_MANIPULATE, json.toString()};
  }

  /**
   * Add the messages that were printed while evaluating. These are prose, not mathematics, so they
   * travel as plain text; they used to be wrapped in <code>&lt;math&gt;&lt;mtext&gt;</code> only
   * because everything went through a MathML renderer.
   */
  private static void addMessages(ArrayNode out, StringBuilderWriter errorWriter,
      StringBuilderWriter outWriter) {
    addMessage(out, "Error", errorWriter.toString());
    addMessage(out, "Output", outWriter.toString());
  }

  /**
   * The plain <code>OutputForm</code> of an expression, or an empty string if it cannot be built.
   */
  private static String outputForm(EvalEngine engine, IExpr expr) {
    if (expr == null || expr.equals(S.Null)) {
      return "";
    }
    try {
      StringBuilderWriter buf = new StringBuilderWriter();
      OutputFormFactory.get(engine.isRelaxedSyntax()).convert(buf, expr);
      return buf.toString();
    } catch (RuntimeException rex) {
      return "";
    }
  }

  private static void addMessage(ArrayNode out, String prefix, String message) {
    if (message == null || message.length() == 0) {
      return;
    }
    ObjectNode messageJSON = JSON_OBJECT_MAPPER.createObjectNode();
    messageJSON.put("prefix", prefix);
    messageJSON.put("message", Boolean.TRUE);
    messageJSON.put("tag", "evaluation");
    messageJSON.put("symbol", "General");
    messageJSON.put("format", FORMAT_TEXT);
    messageJSON.put("text", message);
    out.add(messageJSON);
  }

  public static String createJSONErrorString(String str) {
    ObjectNode outJSON = JSON_OBJECT_MAPPER.createObjectNode();
    outJSON.put("prefix", "Error");
    outJSON.put("message", Boolean.TRUE);
    outJSON.put("tag", "syntax");
    outJSON.put("symbol", "General");
    outJSON.put("format", FORMAT_TEXT);
    outJSON.put("text", str);

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.putNull("line");
    resultsJSON.putNull("result");

    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(outJSON);
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return json.toString();
  }

  public static String[] createJSONError(String str) {
    return new String[] {"error", createJSONErrorString(str)};
  }

  /**
   * Pprint a syntax error message.
   *
   * @param str
   * @return
   */
  public static String createJSONSyntaxErrorString(String str) {
    ObjectNode outJSON = JSON_OBJECT_MAPPER.createObjectNode();
    outJSON.put("prefix", "Error");
    outJSON.put("message", Boolean.TRUE);
    outJSON.put("tag", "syntax");
    outJSON.put("symbol", "Syntax");
    // a syntax error points at a column with a caret, so the spacing has to survive
    outJSON.put("format", FORMAT_CODE);
    outJSON.put("text", str);

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.putNull("line");
    resultsJSON.putNull("result");

    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(outJSON);
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return json.toString();
  }

  public static String[] createJSONSyntaxError(String str) {
    return new String[] {"error", createJSONSyntaxErrorString(str)};
  }

  /**
   * Create a JSON mathml output <code>new String[] {"mathml", json.toString()}</code>.
   *
   * @param script
   * @return
   */
  public static String[] createJSONJavaScript(String script) {

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", 21);
    resultsJSON.put("result", script);
    resultsJSON.put("format", FORMAT_HTML);

    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"html", json.toString()};
  }

  public static String[] createJSONShow(EvalEngine engine, IAST show) {
    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", 21);
    resultsJSON.put("result", "");
    resultsJSON.put("format", FORMAT_HTML);
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"html", json.toString()};
  }

  public static String[] createJSONResult(EvalEngine engine, IExpr outExpr,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) {
    // DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    // DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
    // Results travel as MathML and are rendered by the browser itself. The built-in symbol
    // names come out the way WMA writes them - Sin[x] with WMA syntax,
    // Sin(x) with the relaxed Symja syntax - because both servlets set
    // Config.MATHML_TRIG_LOWERCASE to false. KaTeX is not used here: it reads LaTeX, not
    // MathML. It renders the LaTeX of the documentation pages and of Markdown cells.
    MathMLUtilities mathMLUtil = new MathMLUtilities(engine, false, false);
    StringBuilderWriter stw = new StringBuilderWriter();
    if (!outExpr.equals(S.Null) && !mathMLUtil.toMathML(outExpr, stw, true, true)) {
      return createJSONError("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
    }

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", 21);
    resultsJSON.put("result", stw.toString());
    resultsJSON.put("format", FORMAT_MATHML);
    // the ordinary OutputForm of the same result. A notebook saved as *.ipynb needs it for
    // its "text/plain" output, and the browser cannot reconstruct it from the MathML.
    resultsJSON.put("plaintext", outputForm(engine, outExpr));
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    addMessages(temp, errorWriter, outWriter);
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"mathml", json.toString()};
  }

  public static String[] createJSONHTML(EvalEngine engine, String html,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) {
    // DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    // DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
    // MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
    // StringBuilderWriter stw = new StringBuilderWriter();
    // stw.append(html);

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", 21);
    resultsJSON.put("result", html);
    resultsJSON.put("format", FORMAT_HTML);
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    addMessages(temp, errorWriter, outWriter);
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"html", json.toString()};
  }

  public static String[] createJSONSVG(EvalEngine engine, String svgStr,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) {
    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", 21);
    resultsJSON.put("result", svgStr);
    resultsJSON.put("format", FORMAT_HTML);
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    addMessages(temp, errorWriter, outWriter);
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"html", json.toString()};
  }

  /**
   * Create a JSON mathml output <code>new String[] {"mathml", json.toString()}</code>.
   *
   * @param html
   * @param manipulateStr
   * @return
   */
  public static String[] createJSONIFrame(String html, String manipulateStr) {
    html = Errors.templateRender(html, new String[] {manipulateStr});
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
  }

  public static String[] createJEChartsIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildECharts(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\"></iframe>");
  }

  public static String[] createGraphics2DIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildGraphics2D(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\"></iframe>");
  }

  public static String[] createGraphics3DIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildGraphics3D(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\"></iframe>");
  }

  public static String[] createMathcellIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildMathcell(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\"></iframe>");
  }

  public static String[] createEChartsIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildECharts(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\"></iframe>");
  }

  public static String[] createJSXGraphIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildJSXGraph(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\"></iframe>");
  }

  public static String[] createMermaidIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildMermaid(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\"></iframe>");
  }

  public static String[] createPlotlyIFrame(String html, String manipulateStr) {
    html = JSBuilder.buildPlotly(html, manipulateStr);
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript("<iframe srcdoc=\"" + html
        + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" scrolling=\"no\"></iframe>");
  }
}
