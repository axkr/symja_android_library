package org.matheclipse.io.servlet;

import java.io.StringWriter;
import org.apache.commons.text.StringEscapeUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONBuilder {

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  public static String createJSONErrorString(String str) {
    ObjectNode outJSON = JSON_OBJECT_MAPPER.createObjectNode();
    outJSON.put("prefix", "Error");
    outJSON.put("message", Boolean.TRUE);
    outJSON.put("tag", "syntax");
    outJSON.put("symbol", "General");
    str = StringEscapeUtils.escapeHtml4(str);
    outJSON.put("text", "<math><mrow><mtext>" + str + "</mtext></mrow></math>");

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
    str = StringEscapeUtils.escapeHtml4(str);
    str = str.replace(" ", "&nbsp;");
    String[] strs = str.split("\\n");
    StringBuilder mtext = new StringBuilder();
    for (int i = 0; i < strs.length; i++) {
      mtext.append("<mtext mathvariant=\"courier\">");
      mtext.append(strs[i]);
      mtext.append("</mtext>");
      if (i < strs.length - 1) {
        mtext.append("<mspace linebreak='newline' />");
      }
    }
    outJSON.put("text", "<math><mrow>" + mtext + "</mrow></math>");

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
    resultsJSON.put("line", Integer.valueOf(21));
    resultsJSON.put("result", script);

    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"mathml", json.toString()};
  }

  public static String[] createJSONShow(EvalEngine engine, IAST show) {
    StringBuilder stw = new StringBuilder();
    stw.append("<math><mtable><mtr><mtd>");
    if (show.isAST() && show.size() > 1 && show.arg1().isAST(S.Graphics,2)) {
      StringBuilder buf = new StringBuilder(2048);
      GraphicsFunctions.graphicsToSVG((IAST) ((IAST) show).arg1(), stw);
    }
    stw.append("</mtd></mtr></mtable></math>");

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", Integer.valueOf(21));
    resultsJSON.put("result", stw.toString());
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"mathml", json.toString()};
  }

  public static String[] createJSONResult(
      EvalEngine engine, IExpr outExpr, StringWriter outWriter, StringWriter errorWriter) {
    // DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    // DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
    MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
    StringWriter stw = new StringWriter();
    if (!mathUtil.toMathML(outExpr, stw, true)) {
      return createJSONError("Max. output size exceeded " + Config.MAX_OUTPUT_SIZE);
    }

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", Integer.valueOf(21));
    resultsJSON.put("result", stw.toString());
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();

    String message = errorWriter.toString();
    if (message.length() > 0) {
      ObjectNode messageJSON = JSON_OBJECT_MAPPER.createObjectNode();
      messageJSON.put("prefix", "Error");
      messageJSON.put("message", Boolean.TRUE);
      messageJSON.put("tag", "evaluation");
      messageJSON.put("symbol", "General");
      messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
      temp.add(messageJSON);
    }

    message = outWriter.toString();
    if (message.length() > 0) {
      ObjectNode messageJSON = JSON_OBJECT_MAPPER.createObjectNode();
      messageJSON.put("prefix", "Output");
      messageJSON.put("message", Boolean.TRUE);
      messageJSON.put("tag", "evaluation");
      messageJSON.put("symbol", "General");
      messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
      temp.add(messageJSON);
    }
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"mathml", json.toString()};
  }

  public static String[] createJSONHTML(
      EvalEngine engine, String html, StringWriter outWriter, StringWriter errorWriter) {
    // DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    // DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
    //    MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
    //	    StringWriter stw = new StringWriter();
    //	    stw.append(html);

    ObjectNode resultsJSON = JSON_OBJECT_MAPPER.createObjectNode();
    resultsJSON.put("line", Integer.valueOf(21));
    resultsJSON.put("result", html);
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();

    String message = errorWriter.toString();
    if (message.length() > 0) {
      ObjectNode messageJSON = JSON_OBJECT_MAPPER.createObjectNode();
      messageJSON.put("prefix", "Error");
      messageJSON.put("message", Boolean.TRUE);
      messageJSON.put("tag", "evaluation");
      messageJSON.put("symbol", "General");
      messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
      temp.add(messageJSON);
    }

    message = outWriter.toString();
    if (message.length() > 0) {
      ObjectNode messageJSON = JSON_OBJECT_MAPPER.createObjectNode();
      messageJSON.put("prefix", "Output");
      messageJSON.put("message", Boolean.TRUE);
      messageJSON.put("tag", "evaluation");
      messageJSON.put("symbol", "General");
      messageJSON.put("text", "<math><mrow><mtext>" + message + "</mtext></mrow></math>");
      temp.add(messageJSON);
    }
    resultsJSON.putPOJO("out", temp);

    temp = JSON_OBJECT_MAPPER.createArrayNode();
    temp.add(resultsJSON);
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    json.putPOJO("results", temp);

    return new String[] {"mathml", json.toString()};
  }

  /**
   * Create a JSON mathml output <code>new String[] {"mathml", json.toString()}</code>.
   *
   * @param html
   * @param manipulateStr
   * @return
   */
  public static String[] createJSONIFrame(String html, String manipulateStr) {
    html = IOFunctions.templateRender(html, new String[] {manipulateStr});
    html = StringEscapeUtils.escapeHtml4(html);
    return createJSONJavaScript(
        "<iframe srcdoc=\""
            + html
            + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
  }

  static final String JSXGRAPH_IFRAME = //
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
          + "\n"
          + "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.1/jsxgraph.min.css\" />\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.6/build/math.js\"></script>\n"
          + "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.1/jsxgraphcore.min.js\"\n"
          + "        type=\"text/javascript\"></script>\n"
          + "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jsxgraph/1.2.1/geonext.min.js\"\n"
          + "        type=\"text/javascript\"></script>\n"
          + "\n"
          + "<div id=\"jxgbox\" class=\"jxgbox\" style=\"display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          + "`1`\n"
          + "</script>\n"
          + "</div>\n"
          + "\n"
          + "</body>\n"
          + "</html>";

  protected static final String MATHCELL_IFRAME = //
      // "<html style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "\n"
          + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n"
          + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n"
          + "<meta charset=\"utf-8\">\n"
          + "<title>MathCell</title>\n"
          + "</head>\n"
          + "\n"
          + "<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.6/build/math.js\"></script>\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.9.2/build/mathcell.js\"></script>\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML\"></script>"
          + "\n"
          + "<div class=\"mathcell\" style=\"display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n"
          + "\n"
          + "var parent = document.currentScript.parentNode;\n"
          + "\n"
          + "var id = generateId();\n"
          + "parent.id = id;\n"
          + "\n"
          + "`1`\n"
          + "\n"
          + "parent.update( id );\n"
          + "\n"
          + "</script>\n"
          + "</div>\n"
          + "\n"
          + "</body>\n"
          + "</html>";

  protected static final String PLOTLY_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "\n"
          + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n"
          + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n"
          + "<meta charset=\"utf-8\">\n"
          + "<title>Plotly</title>\n"
          + "\n"
          + "   <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>\n"
          + "</head>\n"
          + "<body>\n"
          + "<div id='plotly' ></div>\n"
          + "`1`\n"
          + "</body>\n"
          + "</html>";
}
