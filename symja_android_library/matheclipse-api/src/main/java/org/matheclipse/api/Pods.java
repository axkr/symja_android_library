package org.matheclipse.api;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.matheclipse.api.parser.FuzzyParser;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.data.ElementData1;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.form.tex.TeXParser;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDistribution;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.logging.ThreadLocalNotifyingAppender;
import org.matheclipse.logging.ThreadLocalNotifyingAppender.ThreadLocalNotifierClosable;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;
import org.owasp.encoder.Encode;
import com.baeldung.algorithms.romannumerals.RomanArabicConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Suppliers;

public class Pods {
  private static final Logger LOGGER = LogManager.getLogger();

  public static final String JSON = "JSON";

  /**
   * From the docs: "Mapper instances are fully thread-safe provided that ALL configuration of the
   * instance occurs before ANY read or write calls."
   */
  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  private static class LevenshteinDistanceComparator implements Comparator<IPod> {
    static final LevenshteinDistance ld = new LevenshteinDistance(128);

    String str;

    public LevenshteinDistanceComparator(String str) {
      this.str = str;
    }

    @Override
    public int compare(IPod arg0, IPod arg1) {
      int d0 = ld.apply(str, arg0.keyWord());
      int d1 = ld.apply(str, arg1.keyWord());
      return d0 > d1 ? 1 : d0 < d1 ? -1 : 0;
    }
  }

  // output formats
  public static final String HTML_STR = "html";
  public static final String PLAIN_STR = "plaintext";
  public static final String SYMJA_STR = "sinput";
  public static final String MATHML_STR = "mathml";
  public static final String LATEX_STR = "latex";
  public static final String MARKDOWN_STR = "markdown";
  public static final String MATHCELL_STR = "mathcell";
  public static final String JSXGRAPH_STR = "jsxgraph";
  public static final String PLOTLY_STR = "plotly";

  public static final String VISJS_STR = "visjs";
  public static final int HTML = 0x0001;
  public static final int PLAIN = 0x0002;
  public static final int SYMJA = 0x0004;
  public static final int MATHML = 0x0008;
  public static final int LATEX = 0x0010;
  public static final int MARKDOWN = 0x0020;
  public static final int MATHCELL = 0x0040;
  public static final int JSXGRAPH = 0x0080;
  public static final int PLOTLY = 0x0100;
  public static final int VISJS = 0x0200;

  public static final Soundex SOUNDEX = new Soundex();
  public static final TrieBuilder<String, ArrayList<IPod>, ArrayList<ArrayList<IPod>>> builder =
      TrieBuilder.create();
  public static final Trie<String, ArrayList<IPod>> SOUNDEX_MAP =
      builder.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  static com.google.common.base.Supplier<Trie<String, ArrayList<IPod>>> SOUNDEX_SUPPLIER =
      new com.google.common.base.Supplier<Trie<String, ArrayList<IPod>>>() {

        @Override
        public Trie<String, ArrayList<IPod>> get() {
          // Map<String, String> map = AST2Expr.PREDEFINED_SYMBOLS_MAP;

          IAST[] list = ElementData1.ELEMENTS;
          for (int i = 0; i < list.length; i++) {
            String keyWord = list[i].arg3().toString();
            addElementData(list[i].arg2().toString().toLowerCase(), keyWord);
            soundexElementData(list[i].arg3().toString(), keyWord);
          }
          for (int i = 0; i < ID.Zeta; i++) {
            ISymbol sym = S.symbol(i);
            soundexHelp(sym.toString().toLowerCase(), sym);
          }
          // for (Map.Entry<String, String> entry : map.entrySet()) {
          // soundexHelp(entry.getKey(), entry.getKey());
          // }
          // appendSoundex();
          soundexHelp("cosine", S.Cos);
          soundexHelp("sine", S.Sin);
          soundexHelp("integral", S.Integrate);

          return SOUNDEX_MAP;
        }
      };
  private static com.google.common.base.Supplier<Trie<String, ArrayList<IPod>>> LAZY_SOUNDEX =
      Suppliers.memoize(SOUNDEX_SUPPLIER);

  static final String JSXGRAPH_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>JSXGraph</title>\n" + "\n"
          + "<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n" + "\n"
          + "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn.jsdelivr.net/npm/jsxgraph@1.4.5/distrib/jsxgraph.css\" />\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.9/build/math.js\"></script>\n"
          + "<script src=\"https://cdn.jsdelivr.net/npm/jsxgraph@1.4.9/distrib/jsxgraphcore.js\"\n"
          + "        type=\"text/javascript\"></script>\n" + "\n"
          + "<div id=\"jxgbox\" class=\"jxgbox\" style=\"display: flex; width:99%; height:99%; margin: 0; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n" + "`1`\n" + "</script>\n" + "</div>\n" + "\n" + "</body>\n" + "</html>";

  protected static final String MATHCELL_IFRAME = //
      // "<html style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>MathCell</title>\n" + "</head>\n"
          + "\n" + "<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n" + "\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/math@1.4.8/build/math.js\"></script>\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.9.3/build/mathcell.js\"></script>\n"
          + "<script src=\"https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML\"></script>"
          + "\n"
          + "<div class=\"mathcell\" style=\"display: flex; width: 100%; height: 100%; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
          + "<script>\n" + "\n" + "var parent = document.currentScript.parentNode;\n" + "\n"
          + "var id = generateId();\n" + "parent.id = id;\n" + "\n" + "`1`\n" + "\n"
          + "parent.update( id );\n" + "\n" + "</script>\n" + "</div>\n" + "\n" + "</body>\n"
          + "</html>";

  protected static final String PLOTLY_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Plotly</title>\n" + "\n"
          + "   <script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>\n" + "</head>\n"
          + "<body>\n" + "<div id='plotly' ></div>\n" + "`1`\n" + "</body>\n" + "</html>"; //

  protected static final String VISJS_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>VIS-NetWork</title>\n" + "\n"
          + "  <script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/npm/vis-network@6.0.0/dist/vis-network.min.js\"></script>\n"
          + "</head>\n" + "<body>\n" + "\n"
          + "<div id=\"vis\" style=\"width: 600px; height: 400px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
          + "<script type=\"text/javascript\">\n" + "`1`\n"
          + "  var container = document.getElementById('vis');\n" + "  var data = {\n"
          + "    nodes: nodes,\n" + "    edges: edges\n" + "  };\n" + "`2`\n"
          + "  var network = new vis.Network(container, data, options);\n" + "</script>\n"
          + "</div>\n" + "</body>\n" + "</html>";

  protected static final String HIGHLIGHT_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>Highlight</title>\n" + "\n"
          + "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/styles/default.min.css\" />\n"
          + "  <script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/10.1.1/highlight.min.js\"></script>\n"
          + "<script>hljs.initHighlightingOnLoad();</script></head>\n"
          + "<body style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n" + "\n"
          + "<div id=\"highlight\" style=\"width: 600px; height: 800px; margin: 0;  padding: .25in .5in .5in .5in; flex-direction: column; overflow: hidden\">\n"
          + "<pre><code class=\"javascript\">\n" + "`1`\n" + "</code></pre>\n" + "</div>\n"
          + "</body>\n" + "</html>"; //

  private static void addElementData(String soundex, String value) {
    ArrayList<IPod> list = SOUNDEX_MAP.get(soundex);
    if (list == null) {
      list = new ArrayList<IPod>();
      list.add(new ElementDataPod(value));
      SOUNDEX_MAP.put(soundex, list);
    } else {
      list.add(new ElementDataPod(value));
    }
  }

  /** package private */
  static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String title, String scanner,
      int formats, EvalEngine engine) {
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    ObjectNode subpodsResult = JSON_OBJECT_MAPPER.createObjectNode();
    subpodsResult.put("title", title);
    subpodsResult.put("scanner", scanner);
    subpodsResult.put("error", "false");
    subpodsResult.put("numsubpods", 1);
    subpodsResult.putPOJO("subpods", temp);
    podsArray.add(subpodsResult);

    ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
    temp.add(node);
    createJSONFormat(node, engine, outExpr, formats);
  }

  /** package private */
  static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String plaintext,
      String title, String scanner, int formats, EvalEngine engine) {
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    ObjectNode subpodsResult = JSON_OBJECT_MAPPER.createObjectNode();
    subpodsResult.put("title", title);
    subpodsResult.put("scanner", scanner);
    subpodsResult.put("error", "false");
    subpodsResult.put("numsubpods", 1);
    subpodsResult.putPOJO("subpods", temp);
    podsArray.add(subpodsResult);

    ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
    temp.add(node);
    createJSONFormat(node, engine, outExpr, plaintext, "", formats);
  }

  /** package private */
  static void addPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String plaintext,
      String sinput, String title, String scanner, int formats, EvalEngine engine) {
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    ObjectNode subpodsResult = JSON_OBJECT_MAPPER.createObjectNode();
    subpodsResult.put("title", title);
    subpodsResult.put("scanner", scanner);
    subpodsResult.put("error", "false");
    subpodsResult.put("numsubpods", 1);
    subpodsResult.putPOJO("subpods", temp);
    podsArray.add(subpodsResult);

    ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
    temp.add(node);
    createJSONFormat(node, engine, outExpr, plaintext, sinput, formats);
  }

  /** package private */
  static void addSymjaPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String title,
      String scanner, int formats, EvalEngine engine) {
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    ObjectNode subpodsResult = JSON_OBJECT_MAPPER.createObjectNode();
    subpodsResult.put("title", title);
    subpodsResult.put("scanner", scanner);
    subpodsResult.put("error", "false");
    subpodsResult.put("numsubpods", 1);
    subpodsResult.putPOJO("subpods", temp);
    podsArray.add(subpodsResult);

    ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
    temp.add(node);
    createJSONFormat(node, engine, StringFunctions.inputForm(inExpr), outExpr, formats);
  }

  /** package private */
  static void addSymjaPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String plaintext,
      String title, String scanner, int formats, EvalEngine engine) {
    addSymjaPod(podsArray, inExpr, outExpr, plaintext, title, scanner, formats, engine, false);
  }

  /** package private */
  static void addSymjaPod(ArrayNode podsArray, IExpr inExpr, IExpr outExpr, String plaintext,
      String title, String scanner, int formats, EvalEngine engine, boolean error) {
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    ObjectNode subpodsResult = JSON_OBJECT_MAPPER.createObjectNode();
    subpodsResult.put("title", title);
    subpodsResult.put("scanner", scanner);
    subpodsResult.put("error", error ? "true" : "false");
    subpodsResult.put("numsubpods", 1);
    subpodsResult.putPOJO("subpods", temp);
    podsArray.add(subpodsResult);

    ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
    temp.add(node);
    createJSONFormat(node, engine, plaintext, StringFunctions.inputForm(inExpr), outExpr, formats);
  }

  /**
   * Create a truth table for logic formulas with less equal 5 variables
   *
   * @param podsArray
   * @param booleanFormula
   * @param variables
   * @param formats
   * @param engine
   * @return
   */
  private static int booleanPods(ArrayNode podsArray, IExpr booleanFormula, IAST variables,
      int formats, EvalEngine engine) {
    int htmlFormats = formats | HTML;
    if ((htmlFormats & PLAIN) == PLAIN) {
      htmlFormats ^= PLAIN;
    }
    int numpods = 0;
    if (variables.argSize() > 0 && variables.argSize() <= 5) {
      IExpr outExpr = F.BooleanTable(F.Append(variables, booleanFormula), variables);
      IExpr podOut = engine.evaluate(outExpr);

      int[] dim = podOut.isMatrix();
      if (dim != null && dim[0] > 0 && dim[1] > 0) {
        IAST matrix = (IAST) podOut;
        int rowDimension = dim[0];
        int columnDimension = dim[1];
        StringBuilder tableForm = new StringBuilder();
        tableForm.append("<table style=\"border:solid 1px;\">");
        tableForm.append("<thead><tr>");
        for (int i = 1; i < variables.size(); i++) {
          tableForm.append("<th>");
          tableForm.append(variables.get(i).toString());
          tableForm.append("</th>");
        }
        tableForm.append("<th>");
        tableForm.append(booleanFormula.toString());
        tableForm.append("</th>");
        tableForm.append("</tr></thead>");

        tableForm.append("<tbody><tr>");
        for (int i = 0; i < rowDimension; i++) {
          tableForm.append("<tr>");
          for (int j = 0; j < columnDimension; j++) {
            IExpr x = matrix.getPart(i + 1, j + 1);
            tableForm.append("<td>");
            tableForm.append(x.isTrue() ? "T" : x.isFalse() ? "F" : x.toString());
            tableForm.append("</td>");
          }
          tableForm.append("</tr>");
        }
        tableForm.append("</tr></tbody>");
        tableForm.append("</table>");
        addSymjaPod(podsArray, outExpr, podOut, tableForm.toString(), "Truth table", "Boolean",
            htmlFormats, engine);
        numpods++;
      }
    }

    IExpr outExpr = F.SatisfiabilityInstances(booleanFormula, variables, F.C1);
    IExpr podOut = engine.evaluate(outExpr);
    int[] dim = podOut.isMatrix();
    if (dim != null && dim[0] > 0 && dim[1] > 0) {
      IAST matrix = (IAST) podOut;
      int rowDimension = dim[0];
      int columnDimension = dim[1];

      StringBuilder tableForm = new StringBuilder();
      tableForm.append("<table style=\"border:solid 1px;\">");
      tableForm.append("<thead><tr>");
      for (int i = 1; i < variables.size(); i++) {
        tableForm.append("<th>");
        tableForm.append(variables.get(i).toString());
        tableForm.append("</th>");
      }
      tableForm.append("</tr></thead>");

      tableForm.append("<tbody><tr>");
      for (int i = 0; i < rowDimension; i++) {
        tableForm.append("<tr>");
        for (int j = 0; j < columnDimension; j++) {
          IExpr x = matrix.getPart(i + 1, j + 1);
          tableForm.append("<td>");
          tableForm.append(x.isTrue() ? "T" : x.isFalse() ? "F" : x.toString());
          tableForm.append("</td>");
        }
        tableForm.append("</tr>");
      }
      tableForm.append("</tr></tbody>");
      tableForm.append("</table>");

      addSymjaPod(podsArray, outExpr, podOut, tableForm.toString(), "Satisfiability instance",
          "Boolean", htmlFormats, engine);
      numpods++;
    }

    return numpods;
  }

  static ObjectNode createJSONErrorString(String str) {
    ObjectNode outJSON = JSON_OBJECT_MAPPER.createObjectNode();
    outJSON.put("prefix", "Error");
    outJSON.put("message", Boolean.TRUE);
    outJSON.put("tag", "syntax");
    outJSON.put("symbol", "General");
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
    return json;
  }

  private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr,
      int formats) {
    createJSONFormat(json, engine, outExpr, null, "", formats);
  }

  /**
   * @param json
   * @param engine
   * @param outExpr
   * @param plainText text which should obligatory be used for plaintext format
   * @param sinput Symja input string
   * @param formats
   */
  private static void createJSONFormat(ObjectNode json, EvalEngine engine, IExpr outExpr,
      String plainText, String sinput, int formats) {

    String encodedPlainText = plainText == null ? null : Encode.forHtmlContent(plainText);
    if ((formats & HTML) != 0x00) {
      if (encodedPlainText != null && encodedPlainText.length() > 0) {
        json.put(HTML_STR, encodedPlainText);
      }
    }

    if ((formats & PLAIN) != 0x00) {
      if (encodedPlainText != null && encodedPlainText.length() > 0) {
        json.put(PLAIN_STR, encodedPlainText);
      } else {
        if (outExpr.isPresent()) {
          String exprStr = Encode.forHtmlContent(outExpr.toString());
          json.put(PLAIN_STR, exprStr);
        }
      }
    }
    if ((formats & SYMJA) != 0x00) {
      if (sinput != null && sinput.length() > 0) {
        String encodedSInput = Encode.forHtmlContent(sinput);
        json.put(SYMJA_STR, encodedSInput);
      }
    }
    if ((formats & MATHML) != 0x00) {
      if (outExpr.isPresent()) {
        StringWriter stw = new StringWriter();
        MathMLUtilities mathUtil = new MathMLUtilities(engine, false, false);
        if (!mathUtil.toMathML(F.HoldForm(outExpr), stw, true)) {
          // return createJSONErrorString("Max. output size exceeded " +
          // Config.MAX_OUTPUT_SIZE);
        } else {
          json.put(MATHML_STR, stw.toString());
        }
      }
    }
    if ((formats & LATEX) != 0x00) {
      if (outExpr.isPresent()) {
        StringWriter stw = new StringWriter();
        TeXUtilities texUtil = new TeXUtilities(engine, engine.isRelaxedSyntax());
        if (!texUtil.toTeX(F.HoldForm(outExpr), stw)) {
          //
        } else {
          json.put(LATEX_STR, stw.toString());
        }
      }
    }
    if ((formats & MARKDOWN) != 0x00) {
      if (encodedPlainText != null && encodedPlainText.length() > 0) {
        json.put(MARKDOWN_STR, encodedPlainText);
      } else {

      }
    }
    if ((formats & MATHCELL) != 0x00) {
      if (plainText != null && plainText.length() > 0) {
        try {
          String html = JSBuilder.buildMathcell(JSBuilder.MATHCELL_IFRAME_TEMPLATE, plainText);
          // String html = MATHCELL_IFRAME;
          // html = StringUtils.replace(html, "`1`", plainText);
          html = StringEscapeUtils.escapeHtml4(html);
          html = "<iframe srcdoc=\"" + html
              + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
          json.put(MATHCELL_STR, html);
        } catch (Exception ex) {
          LOGGER.debug("Pods.createJSONFormat() failed", ex);
        }

      } else {

      }
    }
    if ((formats & JSXGRAPH) != 0x00) {
      if (plainText != null && plainText.length() > 0) {
        try {
          String html = JSBuilder.buildJSXGraph(JSBuilder.JSXGRAPH_IFRAME_TEMPLATE, plainText);
          // String html = JSXGRAPH_IFRAME;
          // html = StringUtils.replace(html, "`1`", plainText);
          html = StringEscapeUtils.escapeHtml4(html);
          html = "<iframe srcdoc=\"" + html
              + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
          json.put(JSXGRAPH_STR, html);
        } catch (Exception ex) {
          LOGGER.debug("ChineseRemainder.chineseRemainderBigInteger() failed", ex);
        }

      } else {

      }
    }

    if ((formats & PLOTLY) != 0x00) {
      if (plainText != null && plainText.length() > 0) {
        try {
          String html = JSBuilder.buildPlotly(JSBuilder.PLOTLY_IFRAME_TEMPLATE, plainText);
          // String html = PLOTLY_IFRAME;
          // html = StringUtils.replace(html, "`1`", plainText);
          html = StringEscapeUtils.escapeHtml4(html);
          html = "<iframe srcdoc=\"" + html
              + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
          json.put(PLOTLY_STR, html);
        } catch (Exception ex) {
          LOGGER.debug("Pods.createJSONFormat() failed", ex);
        }

      } else {

      }
    }

    if ((formats & VISJS) != 0x00) {
      if (plainText != null && plainText.length() > 0) {
        try {
          String html = VISJS_IFRAME;
          html = StringUtils.replace(html, "`1`", plainText);
          html = StringUtils.replace(html, "`2`", //
              "  var options = {\n" + "		  edges: {\n" + "              smooth: {\n"
                  + "                  type: 'cubicBezier',\n"
                  + "                  forceDirection:  'vertical',\n"
                  + "                  roundness: 0.4\n" + "              }\n" + "          },\n"
                  + "          layout: {\n" + "              hierarchical: {\n"
                  + "                  direction: \"UD\"\n" + "              }\n" + "          },\n"
                  + "          nodes: {\n" + "            shape: 'box'\n" + "          },\n"
                  + "          physics:false\n" + "      }; " //
          );
          html = StringEscapeUtils.escapeHtml4(html);
          html = "<iframe srcdoc=\"" + html
              + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
          json.put(VISJS_STR, html);
        } catch (Exception ex) {
          LOGGER.debug("Pods.createJSONFormat() failed", ex);
        }

      } else {

      }
    }
  }

  private static void createJSONFormat(ObjectNode json, EvalEngine engine, String sinput,
      IExpr outExpr, int formats) {
    createJSONFormat(json, engine, outExpr, null, sinput, formats);
  }

  private static void createJSONFormat(ObjectNode json, EvalEngine engine, String plaintext,
      String sinput, IExpr outExpr, int formats) {
    createJSONFormat(json, engine, outExpr, plaintext, sinput, formats);
  }

  public static ObjectNode createResult(String inputStr, int formats, boolean strictSymja) {
    ObjectNode messageJSON = JSON_OBJECT_MAPPER.createObjectNode();

    ObjectNode queryresult = JSON_OBJECT_MAPPER.createObjectNode();
    messageJSON.putPOJO("queryresult", queryresult);
    queryresult.put("success", "false");
    queryresult.put("error", "false");
    queryresult.put("numpods", 0);
    queryresult.put("version", "0.1");

    boolean error = false;
    int numpods = 0;
    IExpr inExpr = S.Null;
    IExpr outExpr = S.Null;
    EvalEngine engine = EvalEngine.get();

    ArrayNode podsArray = JSON_OBJECT_MAPPER.createArrayNode();
    if (strictSymja) {
      engine.setPackageMode(false);
      final ExprParser parser = new ExprParser(engine, true);
      try {
        inExpr = parser.parse(inputStr);
        if (inExpr.isPresent()) {
          long numberOfLeaves = inExpr.leafCount();
          if (numberOfLeaves < Config.MAX_INPUT_LEAVES) {
            outExpr = inExpr;

            final StringWriter errorWriter = new StringWriter();
            WriterOutputStream werrors = new WriterOutputStream(errorWriter);
            PrintStream errors = new PrintStream(werrors);
            IExpr firstEval = F.NIL;
            try (ThreadLocalNotifierClosable c = setLogEventNotifier(errors)) {
              engine.setErrorPrintStream(errors);
              firstEval = engine.evaluateNIL(inExpr);
            } finally {
              engine.setErrorPrintStream(null);
            }
            addSymjaPod(podsArray, inExpr, inExpr, "Input", "Identity", formats, engine);
            numpods++;
            String errorString = "";
            if (firstEval.isPresent()) {
              outExpr = firstEval;
            } else {
              errorString = errorWriter.toString().trim();
            }
            outExpr = engine.evaluate(inExpr);
            if (outExpr instanceof GraphExpr) {
              String javaScriptStr = GraphFunctions.graphToJSForm((GraphExpr) outExpr);
              if (javaScriptStr != null) {
                String html = VISJS_IFRAME;
                html = StringUtils.replace(html, "`1`", javaScriptStr);
                html = StringUtils.replace(html, "`2`", //
                    "  var options = { };\n" //
                );
                // html = StringEscapeUtils.escapeHtml4(html);
                int form = internFormat(SYMJA, "visjs");
                addPod(podsArray, inExpr, outExpr, html, "Graph data", "Graph", form, engine);
                numpods++;
              } else {
                addSymjaPod(podsArray, inExpr, outExpr, errorString, "Evaluated result",
                    "Expression", formats, engine, true);
                numpods++;
              }
            } else {
              addSymjaPod(podsArray, inExpr, outExpr, errorString, "Evaluated result", "Expression",
                  formats, engine, true);
              numpods++;
            }
            resultStatistics(queryresult, error, numpods, podsArray);
            return messageJSON;
          }
        }
      } catch (SyntaxError serr) {
        // this includes syntax errors
        LOGGER.debug("Pods.createResult() failed", serr);

        return errorJSON("0", serr.getMessage());
      }
      queryresult.put("error", error ? "true" : "false");
      return messageJSON;
    }
    inExpr = parseInput(inputStr, engine);
    if (inExpr.isPresent()) {
      long numberOfLeaves = inExpr.leafCount();
      if (numberOfLeaves < Config.MAX_INPUT_LEAVES) {
        outExpr = inExpr;

        final StringWriter errorWriter = new StringWriter();
        WriterOutputStream werrors = new WriterOutputStream(errorWriter);
        PrintStream errors = new PrintStream(werrors);
        IExpr firstEval = F.NIL;
        try (ThreadLocalNotifierClosable c = setLogEventNotifier(errors)) {
          engine.setErrorPrintStream(errors);
          firstEval = engine.evaluateNIL(inExpr);
        } finally {
          engine.setErrorPrintStream(null);
        }
        addSymjaPod(podsArray, inExpr, inExpr, "Input", "Identity", formats, engine);
        numpods++;
        String errorString = "";
        if (firstEval.isPresent()) {
          outExpr = firstEval;
        } else {
          errorString = errorWriter.toString().trim();
        }
        IExpr podOut = outExpr;

        IExpr numExpr = F.NIL;
        IExpr evaledNumExpr = F.NIL;
        if (outExpr.isNumericFunction(true)) {
          numExpr = inExpr.isAST(S.N) ? inExpr : F.N(inExpr);
          evaledNumExpr = engine.evaluate(F.N(outExpr));
        }
        if (outExpr.isNumber() || outExpr.isQuantity()) {
          if (outExpr.isInteger()) {
            numpods += integerPods(podsArray, inExpr, (IInteger) outExpr, formats, engine);
            resultStatistics(queryresult, error, numpods, podsArray);
            return messageJSON;
          } else {
            podOut = outExpr;
            if (outExpr.isRational()) {
              addSymjaPod(podsArray, inExpr, podOut, "Exact result", "Rational", formats, engine);
              numpods++;
            }

            if (numExpr.isPresent() && //
                (evaledNumExpr.isInexactNumber() || evaledNumExpr.isQuantity())) {
              addSymjaPod(podsArray, numExpr, evaledNumExpr, "Decimal form", "Numeric", formats,
                  engine);
              numpods++;
              if (!outExpr.isRational()) {
                if (evaledNumExpr.isInexactNumber()) {
                  inExpr = F.Rationalize(evaledNumExpr);
                  podOut = engine.evaluate(inExpr);
                  addSymjaPod(podsArray, inExpr, podOut, "Rational form", "Numeric", formats,
                      engine);
                  numpods++;
                }
              }
            }

            if (outExpr.isFraction()) {
              IFraction frac = (IFraction) outExpr;
              if (!frac.integerPart().equals(F.C0)) {
                inExpr = F.List(F.IntegerPart(outExpr), F.FractionalPart(outExpr));
                podOut = engine.evaluate(inExpr);
                String plaintext = podOut.first().toString() + " " + podOut.second().toString();
                addSymjaPod(podsArray, inExpr, podOut, plaintext, "Mixed fraction", "Rational",
                    formats, engine);
                numpods++;

                inExpr = F.ContinuedFraction(outExpr);
                podOut = engine.evaluate(inExpr);
                StringBuilder plainBuf = new StringBuilder();
                if (podOut.isNonEmptyList()) {
                  IAST list = (IAST) podOut;
                  plainBuf.append('[');
                  plainBuf.append(list.arg1().toString());
                  plainBuf.append(';');
                  for (int i = 2; i < list.size(); i++) {
                    plainBuf.append(' ');
                    plainBuf.append(list.get(i).toString());
                    if (i < list.size() - 1) {
                      plainBuf.append(',');
                    }
                  }
                  plainBuf.append(']');
                }
                addSymjaPod(podsArray, inExpr, podOut, plainBuf.toString(), "Continued fraction",
                    "ContinuedFraction", formats, engine);
                numpods++;
              }
            }

            resultStatistics(queryresult, error, numpods, podsArray);
            return messageJSON;
          }
        } else {
          if (outExpr.isAST(S.Plot, 2) && outExpr.first().isList()) {
            outExpr = outExpr.first();
          }
          if (outExpr.isList()) {
            IAST list = (IAST) outExpr;
            ListPod listPod = new ListPod(list);
            numpods += listPod.addJSON(podsArray, formats, engine);
          }

          if (numExpr.isPresent() && //
              (evaledNumExpr.isInexactNumber() || evaledNumExpr.isQuantity())) {
            addSymjaPod(podsArray, numExpr, evaledNumExpr, "Decimal form", "Numeric", formats,
                engine);
            numpods++;
          }

          if (outExpr.isSymbol() || outExpr.isString()) {
            String inputWord = outExpr.toString();
            StringBuilder buf = new StringBuilder();
            // if (outExpr.isBuiltInSymbol()) {
            // inExpr = F.FunctionURL(outExpr);
            // podOut = engine.evaluate(inExpr);
            // if (podOut.isString()) {
            // int htmlFormats = formats | HTML;
            // if ((htmlFormats & PLAIN) == PLAIN) {
            // htmlFormats ^= PLAIN;
            // }
            // if ((htmlFormats & LATEX) == LATEX) {
            // htmlFormats ^= LATEX;
            // }
            // StringBuilder html = new StringBuilder();
            // html.append("<a href=\"");
            // html.append(podOut.toString());
            // html.append("\">");
            // html.append(outExpr.toString());
            // html.append(" - Symja Java function definition</a>");
            // addSymjaPod(podsArray, inExpr, podOut, html.toString(), "Git source code",
            // "FunctionURL", htmlFormats, engine);
            // numpods++;
            // }
            // }
            if (outExpr.isSymbol() && Documentation.getMarkdown(buf, inputWord)) {

              numpods += DocumentationPod.addDocumentationPod(
                  new DocumentationPod((ISymbol) outExpr), podsArray, buf, formats);
              resultStatistics(queryresult, error, numpods, podsArray);
              return messageJSON;
            } else {
              if (outExpr.isString()) {
                int mimeTyp = ((IStringX) outExpr).getMimeType();
                if (mimeTyp == IStringX.APPLICATION_SYMJA || mimeTyp == IStringX.APPLICATION_JAVA
                    || mimeTyp == IStringX.APPLICATION_JAVASCRIPT) {
                  String html = toHighligthedCode(outExpr.toString());
                  addSymjaPod(podsArray, inExpr, F.NIL, html, "Result", "String form", HTML,
                      engine);
                  numpods++;
                  resultStatistics(queryresult, error, numpods, podsArray);
                  return messageJSON;
                } else if (outExpr.isString()) {
                  podOut = outExpr;
                  addSymjaPod(podsArray, inExpr, podOut, "String form", "String", formats, engine);
                  numpods++;
                }
              }

              ArrayList<IPod> soundsLike = listOfPods(inputWord);
              if (soundsLike != null) {
                boolean evaled = false;
                for (int i = 0; i < soundsLike.size(); i++) {
                  IPod pod = soundsLike.get(i);
                  if (pod.keyWord().equalsIgnoreCase(inputWord)) {
                    int numberOfEntries = pod.addJSON(podsArray, formats, engine);
                    if (numberOfEntries > 0) {
                      numpods += numberOfEntries;
                      evaled = true;
                      break;
                    }
                  }
                }
                if (!evaled) {
                  for (int i = 0; i < soundsLike.size(); i++) {
                    IPod pod = soundsLike.get(i);
                    int numberOfEntries = pod.addJSON(podsArray, formats, engine);
                    if (numberOfEntries > 0) {
                      numpods += numberOfEntries;
                    }
                  }
                }
                resultStatistics(queryresult, error, numpods, podsArray);
                return messageJSON;
              }
            }
          } else {
            if (inExpr.isAST(S.D, 2, 3)) {
              if (inExpr.isAST1()) {
                VariablesSet varSet = new VariablesSet(inExpr.first());
                IAST variables = varSet.getVarList();
                IASTAppendable result = ((IAST) inExpr).copyAppendable();
                result.appendArgs(variables);
                inExpr = result;
              }
              outExpr = engine.evaluate(inExpr);
              podOut = outExpr;
              addSymjaPod(podsArray, inExpr, podOut, "Derivative", "Derivative", formats, engine);
              numpods++;

              if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
                inExpr = F.TrigToExp(outExpr);
                podOut = engine.evaluate(inExpr);
                // if (!S.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr))) {
                if (!podOut.equals(outExpr)) {
                  addSymjaPod(podsArray, //
                      inExpr, podOut, "Alternate form", "Simplification", formats, engine);
                  numpods++;
                }
              }
              resultStatistics(queryresult, error, numpods, podsArray);
              return messageJSON;
            } else if (inExpr.isAST(S.Integrate, 2, 3)) {
              if (inExpr.isAST1()) {
                VariablesSet varSet = new VariablesSet(inExpr.first());
                IAST variables = varSet.getVarList();
                IASTAppendable result = ((IAST) inExpr).copyAppendable();
                result.appendArgs(variables);
                inExpr = result;
              }
              outExpr = engine.evaluate(inExpr);
              podOut = outExpr;
              addSymjaPod(podsArray, inExpr, podOut, "Integration", "Integral", formats, engine);
              numpods++;

              if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
                inExpr = F.TrigToExp(outExpr);
                podOut = engine.evaluate(inExpr);
                if (!podOut.equals(outExpr)) {
                  addSymjaPod(podsArray, inExpr, podOut, "Alternate form", "Simplification",
                      formats, engine);
                  numpods++;
                }
              }
              resultStatistics(queryresult, error, numpods, podsArray);
              return messageJSON;
            } else if (inExpr.isAST(S.Solve, 2, 4)) {
              if (inExpr.isAST1()) {
                VariablesSet varSet = new VariablesSet(inExpr.first());
                IAST variables = varSet.getVarList();
                IASTAppendable result = ((IAST) inExpr).copyAppendable();
                result.append(variables);
                inExpr = result;
              }
              outExpr = engine.evaluate(inExpr);
              podOut = outExpr;
              addSymjaPod(podsArray, inExpr, podOut, "Solve equation", "Solver", formats, engine);
              numpods++;

              if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
                inExpr = F.TrigToExp(outExpr);
                podOut = engine.evaluate(inExpr);
                // if (!S.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr))) {
                if (!podOut.equals(outExpr)) {
                  addSymjaPod(podsArray, //
                      inExpr, podOut, "Alternate form", "Simplification", formats, engine);
                  numpods++;
                }
              }
              resultStatistics(queryresult, error, numpods, podsArray);
              return messageJSON;
            } else {
              IExpr expr = inExpr;
              // outExpr = engine.evaluate(expr);

              if (outExpr.isAST(S.JSFormData, 3)) {
                podOut = outExpr;
                int form = internFormat(SYMJA, podOut.second().toString());
                addPod(podsArray, inExpr, podOut, podOut.first().toString(),
                    StringFunctions.inputForm(inExpr), "Function", "Plotter", form, engine);
                numpods++;
              } else if (outExpr instanceof GraphExpr) {
                String javaScriptStr = GraphFunctions.graphToJSForm((GraphExpr) outExpr);
                if (javaScriptStr != null) {
                  String html = VISJS_IFRAME;
                  html = StringUtils.replace(html, "`1`", javaScriptStr);
                  html = StringUtils.replace(html, "`2`", //
                      "  var options = { };\n" //
                  );
                  // html = StringEscapeUtils.escapeHtml4(html);
                  int form = internFormat(SYMJA, "visjs");
                  addPod(podsArray, inExpr, podOut, html, "Graph data", "Graph", form, engine);
                  numpods++;
                }
              } else {
                IExpr head = outExpr.head();
                if (head instanceof IBuiltInSymbol && outExpr.size() > 1) {
                  IEvaluator evaluator = ((IBuiltInSymbol) head).getEvaluator();
                  if (evaluator instanceof IDistribution) {
                    // if (evaluator instanceof IDiscreteDistribution) {
                    int snumpods =
                        statisticsPods(podsArray, (IAST) outExpr, podOut, formats, engine);
                    numpods += snumpods;
                  }
                }

                VariablesSet varSet = new VariablesSet(outExpr);
                IAST variables = varSet.getVarList();
                if (outExpr.isBooleanFormula()) {
                  numpods += booleanPods(podsArray, outExpr, variables, formats, engine);
                }
                if (outExpr.isAST(S.Equal, 3)) {
                  IExpr arg1 = outExpr.first();
                  IExpr arg2 = outExpr.second();
                  if (arg1.isNumericFunction(varSet) //
                      && arg2.isNumericFunction(varSet)) {
                    if (variables.argSize() == 1) {
                      IExpr plot2D = F.Plot(F.List(arg1, arg2),
                          F.List(variables.arg1(), F.num(-20), F.num(20)));
                      podOut = engine.evaluate(plot2D);
                      if (podOut.isAST(S.JSFormData, 3)) {
                        int form = internFormat(SYMJA, podOut.second().toString());
                        addPod(podsArray, inExpr, podOut, podOut.first().toString(),
                            StringFunctions.inputForm(plot2D), "Function", "Plotter", form, engine);
                        numpods++;
                      }
                    }
                    if (!arg1.isZero() && !arg2.isZero()) {
                      inExpr = F.Equal(engine.evaluate(F.Subtract(arg1, arg2)), F.C0);
                      podOut = inExpr;
                      addSymjaPod(podsArray, inExpr, podOut, "Alternate form", "Simplification",
                          formats, engine);
                      numpods++;
                    }
                    inExpr = F.Solve(F.binaryAST2(S.Equal, arg1, arg2), variables);
                    podOut = engine.evaluate(inExpr);
                    addSymjaPod(podsArray, inExpr, podOut, "Solution", "Reduce", formats, engine);
                    numpods++;
                  }

                  resultStatistics(queryresult, error, numpods, podsArray);
                  return messageJSON;
                } else {
                  if (!inExpr.equals(outExpr)) {
                    addSymjaPod(podsArray, inExpr, outExpr, "Result", "Identity", formats, engine);
                    numpods++;
                  }
                }

                boolean isNumericFunction = outExpr.isNumericFunction(varSet);
                if (isNumericFunction) {
                  if (variables.argSize() == 1) {
                    IExpr plot2D = F.Plot(outExpr, F.List(variables.arg1(), F.num(-7), F.num(7)));
                    podOut = engine.evaluate(plot2D);
                    if (podOut.isAST(S.JSFormData, 3)) {
                      int form = internFormat(SYMJA, podOut.second().toString());
                      addPod(podsArray, inExpr, podOut, podOut.first().toString(),
                          StringFunctions.inputForm(plot2D), "Function", "Plotter", form, engine);
                      numpods++;
                    }
                  } else if (variables.argSize() == 2) {
                    IExpr plot3D =
                        F.Plot3D(outExpr, F.List(variables.arg1(), F.num(-3.5), F.num(3.5)),
                            F.List(variables.arg2(), F.num(-3.5), F.num(3.5)));
                    podOut = engine.evaluate(plot3D);
                    if (podOut.isAST(S.JSFormData, 3)) {
                      int form = internFormat(SYMJA, podOut.second().toString());
                      addPod(podsArray, inExpr, podOut, podOut.first().toString(),
                          StringFunctions.inputForm(plot3D), "3D plot", "Plot", form, engine);
                      numpods++;
                    }
                  }
                }
                if (!outExpr.isFreeAST(x -> x.isTrigFunction())) {
                  inExpr = F.TrigToExp(outExpr);
                  podOut = engine.evaluate(inExpr);
                  // if (!S.PossibleZeroQ.ofQ(engine, F.Subtract(podOut, outExpr)))
                  // {
                  if (!podOut.equals(outExpr)) {
                    addSymjaPod(podsArray, inExpr, podOut, "Alternate form", "Simplification",
                        formats, engine);
                    numpods++;
                  }
                }

                if (isNumericFunction && variables.argSize() == 1) {
                  if (outExpr.isPolynomial(variables) && !outExpr.isAtom()) {
                    inExpr = F.Factor(outExpr);
                    podOut = engine.evaluate(inExpr);
                    addSymjaPod(podsArray, inExpr, podOut, "Factor", "Polynomial", formats, engine);
                    numpods++;

                    IExpr x = variables.first();
                    inExpr = F.Minimize(outExpr, x);
                    podOut = engine.evaluate(inExpr);
                    if (podOut.isAST(S.List, 3) && podOut.first().isNumber()
                        && podOut.second().isAST(S.List, 2)) {
                      IExpr rule = podOut.second().first();
                      if (rule.isRule()) {
                        StringBuilder buf = new StringBuilder();
                        buf.append("min{");
                        buf.append(outExpr.toString());
                        buf.append("} = ");
                        buf.append(podOut.first());
                        buf.append(" at ");
                        buf.append(rule.first().toString());
                        buf.append(" = ");
                        buf.append(rule.second().toString());
                        addSymjaPod(podsArray, inExpr, podOut, buf.toString(), "GlobalExtrema",
                            "GlobalMinimum", formats, engine);
                        numpods++;
                      }
                    }

                    inExpr = F.Maximize(outExpr, x);
                    podOut = engine.evaluate(inExpr);
                    if (podOut.isAST(S.List, 3) && podOut.first().isNumber()
                        && podOut.second().isAST(S.List, 2)) {
                      IExpr rule = podOut.second().first();
                      if (rule.isRule()) {
                        StringBuilder buf = new StringBuilder();
                        buf.append("max{");
                        buf.append(outExpr.toString());
                        buf.append("} = ");
                        buf.append(podOut.first());
                        buf.append(" at ");
                        buf.append(rule.first().toString());
                        buf.append(" = ");
                        buf.append(rule.second().toString());
                        addSymjaPod(podsArray, inExpr, podOut, buf.toString(), "GlobalExtrema",
                            "GlobalMaximum", formats, engine);
                        numpods++;
                      }
                    }
                  }
                  inExpr = F.D(outExpr, variables.arg1());
                  podOut = engine.evaluate(inExpr);
                  addSymjaPod(podsArray, inExpr, podOut, "Derivative", "Derivative", formats,
                      engine);
                  numpods++;

                  inExpr = F.Integrate(outExpr, variables.arg1());
                  podOut = engine.evaluate(inExpr);
                  addSymjaPod(podsArray, inExpr, podOut, "Indefinite integral", "Integral", formats,
                      engine);
                  numpods++;
                }
              }
              if (numpods == 1) {
                // only Identity pod was appended
                if (errorString.length() == 0 && //
                    !firstEval.isPresent()) {
                  addSymjaPod(podsArray, expr, outExpr, "Evaluated result", "Expression", formats,
                      engine);
                  numpods++;
                } else {
                  addSymjaPod(podsArray, expr, outExpr, errorString, "Evaluated result",
                      "Expression", formats, engine, true);
                  numpods++;
                }
              }

              resultStatistics(queryresult, error, numpods, podsArray);
              return messageJSON;
            }
          }
        }
        if (numpods > 0) {
          resultStatistics(queryresult, error, numpods, podsArray);
          return messageJSON;
        }
      }
    }

    queryresult.put("error", error ? "true" : "false");
    return messageJSON;
  }

  private static ThreadLocalNotifierClosable setLogEventNotifier(PrintStream errors) {

    return ThreadLocalNotifyingAppender.addLogEventNotifier(e -> {
      if (e.getLevel().isMoreSpecificThan(Level.ERROR)) {
        StringBuilder msg = new StringBuilder();
        Message logMessage = e.getMessage();
        if (logMessage != null) {
          msg.append(logMessage.getFormattedMessage());
        }
        Throwable thrown = e.getThrown();
        if (thrown != null) {
          msg.append(": ").append(thrown.getMessage());
        }
        errors.println(msg.toString());
      }
    });
  }

  public static String errorJSONString(String code, String msg) {
    ObjectNode messageJSON = errorJSON(code, msg);
    return messageJSON.toString();
  }

  public static ObjectNode errorJSON(String code, String msg) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode messageJSON = mapper.createObjectNode();
    ObjectNode queryresult = mapper.createObjectNode();
    messageJSON.putPOJO("queryresult", queryresult);
    queryresult.put("success", "false");
    ObjectNode error = mapper.createObjectNode();
    queryresult.putPOJO("error", error);
    error.put("code", code);
    error.put("msg", msg);
    queryresult.put("numpods", 0);
    queryresult.put("version", "0.1");
    return messageJSON;
  }

  private static IASTAppendable flattenTimes(final IAST ast) {
    if (ast.isTimes() && ast.isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
      IASTAppendable result = flattenTimesRecursive(ast);
      if (result.isPresent()) {
        result.addEvalFlags(IAST.IS_FLATTENED);
        return result;
      }
    }
    ast.addEvalFlags(IAST.IS_FLATTENED);
    return F.NIL;
  }

  public static IASTAppendable flattenTimesRecursive(final IAST ast) {
    int[] newSize = new int[1];
    newSize[0] = 0;
    boolean[] flattened = new boolean[] {false};
    ast.forEach(expr -> {
      if (ast.isTimes() && ast.isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
        flattened[0] = true;
        newSize[0] += ast.size();
      } else {
        newSize[0]++;
      }
    });
    if (flattened[0]) {
      IASTAppendable result = F.ast(ast.head(), newSize[0], false);
      ast.forEach(expr -> {
        if (expr.isTimes() && ((IAST) expr).isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
          result.appendArgs(flattenTimesRecursive((IAST) expr).orElse((IAST) expr));
        } else {
          result.append(expr);
        }
      });
      return result;
    }
    return F.NIL;
  }

  private static String getStemForm(String term) {

    StandardTokenizer stdToken = new StandardTokenizer();
    stdToken.setReader(new StringReader(term));

    try (TokenStream tokenStream = new PorterStemFilter(stdToken)) {
      tokenStream.reset();

      // eliminate duplicate tokens by adding them to a set
      Set<String> stems = new HashSet<>();

      CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

      while (tokenStream.incrementToken()) {
        stems.add(token.toString());
      }

      // if stem form was not found or more than 2 stems have been found, return null
      if (stems.size() != 1) {
        return null;
      }

      String stem = stems.iterator().next();

      // if the stem form has non-alphanumerical chars, return null
      if (!stem.matches("[a-zA-Z0-9-]+")) {
        return null;
      }

      return stem;
    } catch (IOException ioe) {
    }
    return null;
  }

  // private static Trie<String, ArrayList<IPod>> initSoundex() {
  // Map<String, String> map = AST2Expr.PREDEFINED_SYMBOLS_MAP;
  //
  // IAST[] list = ElementData1.ELEMENTS;
  // for (int i = 0; i < list.length; i++) {
  // String keyWord = list[i].arg3().toString();
  // addElementData(list[i].arg2().toString().toLowerCase(), keyWord);
  // soundexElementData(list[i].arg3().toString(), keyWord);
  // }
  // for (int i = 0; i < ID.Zeta; i++) {
  // ISymbol sym = F.symbol(i);
  // soundexHelp(sym.toString().toLowerCase(), sym);
  // }
  // // for (Map.Entry<String, String> entry : map.entrySet()) {
  // // soundexHelp(entry.getKey(), entry.getKey());
  // // }
  // // appendSoundex();
  // soundexHelp("cosine", F.Cos);
  // soundexHelp("sine", F.Sin);
  // soundexHelp("integral", F.Integrate);
  //
  // return SOUNDEX_MAP;
  // }

  private static int integerPods(ArrayNode podsArray, IExpr inExpr, final IInteger outExpr,
      int formats, EvalEngine engine) {
    int htmlFormats = formats | HTML;
    if ((htmlFormats & PLAIN) == PLAIN) {
      htmlFormats ^= PLAIN;
    }
    int numpods = 0;
    int intValue = outExpr.toIntDefault();
    IInteger n = outExpr;

    if (!inExpr.equals(outExpr)) {
      addSymjaPod(podsArray, inExpr, outExpr, "Result", "Simplification", formats, engine);
      numpods++;
    }
    inExpr = outExpr;

    inExpr = F.IntegerName(n, F.stringx("Words"));
    IExpr podOut = engine.evaluateNIL(inExpr);
    if (podOut.isPresent()) {
      addSymjaPod(podsArray, inExpr, podOut, "Number name", "Integer", formats, engine);
      numpods++;
    }

    if (intValue >= RomanArabicConverter.MIN_VALUE && intValue <= RomanArabicConverter.MAX_VALUE) {
      inExpr = F.RomanNumeral(n);
      podOut = engine.evaluate(inExpr);
      addSymjaPod(podsArray, inExpr, podOut, "Roman numerals", "Integer", formats, engine);
      numpods++;
    }

    inExpr = F.BaseForm(outExpr, F.C2);
    podOut = engine.evaluate(inExpr);
    StringBuilder plainText = new StringBuilder();
    if (podOut.isAST(S.Subscript, 3)) {
      plainText.append(podOut.first().toString());
      plainText.append("_");
      plainText.append(podOut.second().toString());
    }
    addSymjaPod(podsArray, inExpr, podOut, plainText.toString(), "Binary form", "Integer", formats,
        engine);
    numpods++;

    if (n.bitLength() < Config.MAX_BIT_LENGTH / 1000) {
      inExpr = F.FactorInteger(n);
      podOut = engine.evaluate(inExpr);
      int[] matrixDimension = podOut.isMatrix();
      plainText = new StringBuilder();
      if (n.isProbablePrime()) {
        plainText.append(n.toString());
        plainText.append(" is a prime number.");
        addSymjaPod(podsArray, inExpr, podOut, plainText.toString(), "Prime factorization",
            "Integer", formats, engine);
        numpods++;
      } else {
        if (matrixDimension[1] == 2) {
          IAST list = (IAST) podOut;
          IASTAppendable times = F.TimesAlloc(podOut.size());
          for (int i = 1; i < list.size(); i++) {
            IExpr arg1 = list.get(i).first();
            IExpr arg2 = list.get(i).second();
            plainText.append(arg1.toString());
            if (!arg2.isOne()) {
              times.append(F.Power(arg1, arg2));
              plainText.append("^");
              plainText.append(arg2.toString());
            } else {
              times.append(arg1);
            }
            if (i < list.size() - 1) {
              plainText.append("*");
            }
          }
          addSymjaPod(podsArray, inExpr, times.oneIdentity0(), plainText.toString(),
              "Prime factorization", "Integer", formats, engine);
          numpods++;
        }
      }
    }

    IAST range = (IAST) S.Range.of(F.C2, F.C9);
    inExpr = F.Mod(n, range);
    podOut = engine.evaluate(inExpr);
    // StringBuilder tableForm = new StringBuilder();
    // if (podOut.isList()) {
    // IAST list = (IAST) podOut;
    // tableForm.append("|m|");
    // for (int i = 1; i < range.size(); i++) {
    // tableForm.append(range.get(i).toString());
    //// if (i < range.size() - 1) {
    // tableForm.append("|");
    //// }
    // }
    // tableForm.append("\n|-|-|-|-|-|-|-|-|-|\n|");
    // tableForm.append(n.toString());
    // tableForm.append(" mod m |");
    //
    // for (int i = 1; i < list.size(); i++) {
    // tableForm.append(list.get(i).toString());
    //// if (i < range.size() - 1) {
    // tableForm.append("|");
    //// }
    // }
    // }

    if (podOut.isList()) {
      StringBuilder tableForm = new StringBuilder();
      tableForm.append("<table style=\"border:solid 1px;\">");
      tableForm.append("<thead><tr>");

      IAST list = (IAST) podOut;
      tableForm.append("<th>m</th>");
      for (int i = 1; i < range.size(); i++) {
        tableForm.append("<th>");
        tableForm.append(range.get(i).toString());
        tableForm.append("</th>");
      }
      tableForm.append("</tr></thead>");

      tableForm.append("<tbody><tr><td>");
      tableForm.append(n.toString());
      tableForm.append(" mod m");
      tableForm.append("</td>");
      for (int i = 1; i < list.size(); i++) {
        tableForm.append("<td>");
        tableForm.append(list.get(i).toString());
        tableForm.append("</td>");
      }
      tableForm.append("</tr></tbody>");
      tableForm.append("</table>");

      addSymjaPod(podsArray, inExpr, podOut, tableForm.toString(), "Residues modulo small integers",
          "Integer", htmlFormats, engine);
      numpods++;
    }

    integerPropertiesPod(podsArray, outExpr, podOut, "Properties", "Integer", formats, engine);
    numpods++;

    if (n.isPositive() && n.isLT(F.ZZ(100))) {
      inExpr = F.Union(F.PowerMod(F.Range(F.C0, F.QQ(n, F.C2)), F.C2, n));
      podOut = engine.evaluate(inExpr);
      addSymjaPod(podsArray, inExpr, podOut, "Quadratic residues modulo " + n.toString(), "Integer",
          formats, engine);
      numpods++;

      if (n.isProbablePrime()) {
        inExpr = F.Select(F.Range(n.add(F.CN1)),
            F.Function(F.Equal(F.MultiplicativeOrder(F.Slot1, n), F.EulerPhi(n))));
        podOut = engine.evaluate(inExpr);
        addSymjaPod(podsArray, inExpr, podOut, "Primitive roots modulo " + n.toString(), "Integer",
            formats, engine);
        numpods++;
      }
    }
    return numpods;
  }

  static void integerPropertiesPod(ArrayNode podsArray, IInteger inExpr, IExpr outExpr,
      String title, String scanner, int formats, EvalEngine engine) {
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    int numsubpods = 0;
    ObjectNode subpodsResult = JSON_OBJECT_MAPPER.createObjectNode();
    subpodsResult.put("title", title);
    subpodsResult.put("scanner", scanner);
    subpodsResult.put("error", "false");
    subpodsResult.put("numsubpods", numsubpods);
    subpodsResult.putPOJO("subpods", temp);
    podsArray.add(subpodsResult);

    try {
      if (inExpr.isEven()) {
        ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
        temp.add(node);
        createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an even number.",
            StringFunctions.inputForm(F.EvenQ(inExpr)), formats);

        numsubpods++;
      } else {
        ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
        temp.add(node);
        createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is an odd number.",
            StringFunctions.inputForm(F.OddQ(inExpr)), formats);

        numsubpods++;
      }
      if (inExpr.isProbablePrime()) {
        IExpr primePiExpr = F.PrimePi(inExpr);
        IExpr primePi = engine.evaluate(primePiExpr);
        if (primePi.isInteger() && inExpr.isPositive()) {
          ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
          temp.add(node);
          createJSONFormat(node, engine, F.NIL,
              inExpr.toString() + " is the " + primePi.toString() + "th prime number.",
              StringFunctions.inputForm(primePiExpr), formats);
          numsubpods++;
        } else {
          ObjectNode node = JSON_OBJECT_MAPPER.createObjectNode();
          temp.add(node);
          createJSONFormat(node, engine, F.NIL, inExpr.toString() + " is a prime number.",
              StringFunctions.inputForm(primePiExpr), formats);
          numsubpods++;
        }
      }
    } finally {
      subpodsResult.put("numsubpods", numsubpods);
    }
  }

  /** package private */
  static int internFormat(int intern, String str) {
    if (str.equals(HTML_STR)) {
      intern |= HTML;
    } else if (str.equals(PLAIN_STR)) {
      intern |= PLAIN;
    } else if (str.equals(SYMJA_STR)) {
      intern |= SYMJA;
    } else if (str.equals(MATHML_STR)) {
      intern |= MATHML;
    } else if (str.equals(LATEX_STR)) {
      intern |= LATEX;
    } else if (str.equals(MARKDOWN_STR)) {
      intern |= MARKDOWN;
    } else if (str.equals(MATHCELL_STR)) {
      intern |= MATHCELL;
    } else if (str.equals(JSXGRAPH_STR)) {
      intern |= JSXGRAPH;
    } else if (str.equals(PLOTLY_STR)) {
      intern |= PLOTLY;
    } else if (str.equals(VISJS_STR) || str.equals("treeform")) {
      intern |= VISJS;
    }
    return intern;
  }

  public static int internFormat(String[] formats) {
    int intern = 0;
    for (String str : formats) {
      intern = internFormat(intern, str);
    }
    return intern;
  }

  private static ArrayList<IPod> listOfPods(String inputWord) {
    Map<String, ArrayList<IPod>> map = LAZY_SOUNDEX.get();
    ArrayList<IPod> soundsLike = map.get(inputWord.toLowerCase());
    if (soundsLike == null) {
      soundsLike = map.get(SOUNDEX.encode(inputWord));
    }
    if (soundsLike != null) {
      LevenshteinDistanceComparator ldc = new LevenshteinDistanceComparator(inputWord);
      Collections.sort(soundsLike, ldc);
    }
    return soundsLike;
  }

  /** package private */
  static IExpr parseInput(String inputStr, EvalEngine engine) {
    engine.setPackageMode(false);
    IExpr inExpr = F.NIL;
    // try {
    // inExpr = Ask.ask(inputStr, engine);
    // } catch (Exception ex) {
    // // this includes syntax errors
    // LOGGER.debug("Pods: Ask.ask() failed", ex);
    // }
    if (!inExpr.isPresent()) {
      final FuzzyParser parser = new FuzzyParser(engine);
      try {
        inExpr = parser.parseFuzzyList(inputStr);
      } catch (SyntaxError serr) {
        // this includes syntax errors
        LOGGER.debug("Pods: FuzzyParser.parseFuzzyList() failed", serr);
        TeXParser texConverter = new TeXParser(engine);
        inExpr = texConverter.toExpression(inputStr);
      }
    }

    if (inExpr == S.$Aborted) {
      return F.NIL;
    }
    if (inExpr.isList() && inExpr.size() == 2) {
      inExpr = inExpr.first();
    }
    if (inExpr.isTimes() && !inExpr.isNumericFunction(true) && inExpr.argSize() <= 4) {
      if (((IAST) inExpr).isEvalFlagOn(IAST.TIMES_PARSED_IMPLICIT)) {
        inExpr = flattenTimes((IAST) inExpr).orElse(inExpr);
        IAST rest = ((IAST) inExpr).setAtClone(0, S.List);
        IASTAppendable specialFunction = F.NIL;
        String stemForm = getStemForm(rest.arg1().toString().toLowerCase());
        IExpr head = rest.head();
        if (stemForm != null) {
          head = STEM.getSymbol(stemForm);
          if (head != null) {
            specialFunction = rest.setAtClone(0, head);
            specialFunction.remove(1);
          }
        }
        if (!specialFunction.isPresent()) {
          stemForm = getStemForm(rest.last().toString().toLowerCase());
          if (stemForm != null) {
            head = STEM.getSymbol(stemForm);
            if (head != null) {
              specialFunction = rest.setAtClone(0, head);
              specialFunction.remove(rest.size() - 1);
            }
          }
        }
        if (specialFunction.isPresent()) {

          if (head != null) {
            if (head == S.UnitConvert) {
              IExpr temp = unitConvert(engine, rest.rest());
              if (temp.isPresent()) {
                return temp;
              }
            } else {

              int i = 1;
              while (i < specialFunction.size()) {
                String argStr = specialFunction.get(i).toString().toLowerCase();
                if (argStr.equalsIgnoreCase("by") || argStr.equalsIgnoreCase("for")) {
                  specialFunction.remove(i);
                  continue;
                }
                i++;
              }

              return specialFunction;
            }
          }
        }

        if (rest.arg1().toString().equalsIgnoreCase("convert")) {
          rest = inExpr.rest();
        }
        if (rest.argSize() > 2) {
          rest = rest.removeIf(x -> x.toString().equals("in"));
        }
        IExpr temp = unitConvert(engine, rest);
        if (temp.isPresent()) {
          return temp;
        }
      }
    }
    return inExpr;
  }

  private static void resultStatistics(ObjectNode queryresult, boolean error, int numpods,
      ArrayNode podsArray) {
    queryresult.putPOJO("pods", podsArray);
    queryresult.put("success", "true");
    queryresult.put("error", error ? "true" : "false");
    queryresult.put("numpods", numpods);
  }

  private static void soundexElementData(String key, String value) {
    String soundex = SOUNDEX.encode(key);
    addElementData(soundex, value);
  }

  private static void soundexHelp(String key, ISymbol value) {
    String soundex = SOUNDEX.encode(key);
    ArrayList<IPod> list = SOUNDEX_MAP.get(soundex);
    if (list == null) {
      list = new ArrayList<IPod>();
      list.add(new DocumentationPod(value));
      SOUNDEX_MAP.put(soundex, list);
    } else {
      list.add(new DocumentationPod(value));
    }
  }

  private static int statisticsPods(ArrayNode podsArray, IAST inExpr, IExpr outExpr, int formats,
      EvalEngine engine) {
    int numpods = 0;
    int htmlFormats = formats | HTML;
    if ((htmlFormats & PLAIN) == PLAIN) {
      htmlFormats ^= PLAIN;
    }
    IExpr mean = S.Mean.ofNIL(engine, inExpr);
    if (mean.isPresent()) {
      // IExpr mode = F.Mode.of(engine, inExpr);
      IExpr standardDeviation = S.StandardDeviation.of(engine, inExpr);
      IExpr variance = S.Variance.of(engine, inExpr);
      IExpr skewness = S.Skewness.of(engine, inExpr);
      IExpr podOut = F.List( //
          F.Mean(inExpr), //
          F.StandardDeviation(inExpr), //
          F.Variance(inExpr), //
          F.Skewness(inExpr));

      StringBuilder tableForm = new StringBuilder();
      tableForm.append("<table style=\"border:solid 1px;\">");
      tableForm.append("<tbody>");

      tableForm.append("<tr>");
      tableForm.append("<td>mean</td>");
      tableForm.append("<td>");
      tableForm.append(mean.toString());
      tableForm.append("</td>");
      tableForm.append("<tr>");

      tableForm.append("<tr>");
      tableForm.append("<td>standard deviation</td>");
      tableForm.append("<td>");
      tableForm.append(standardDeviation.toString());
      tableForm.append("</td>");
      tableForm.append("<tr>");

      tableForm.append("<tr>");
      tableForm.append("<td>variance</td>");
      tableForm.append("<td>");
      tableForm.append(variance.toString());
      tableForm.append("</td>");
      tableForm.append("<tr>");

      tableForm.append("<tr>");
      tableForm.append("<td>skewness</td>");
      tableForm.append("<td>");
      tableForm.append(skewness.toString());
      tableForm.append("</td>");
      tableForm.append("<tr>");

      tableForm.append("</tbody>");
      tableForm.append("</table>");

      addSymjaPod(podsArray, F.List(), podOut, tableForm.toString(), "Statistical properties",
          "Statistics", htmlFormats, engine);
      numpods++;

      inExpr = F.PDF(outExpr, S.x);
      podOut = engine.evaluate(inExpr);
      addSymjaPod(podsArray, inExpr, podOut, "Probability density function (PDF)", "Statistics",
          formats, engine);
      numpods++;

      inExpr = F.CDF(outExpr, S.x);
      podOut = engine.evaluate(inExpr);
      addSymjaPod(podsArray, inExpr, podOut, "Cumulative distribution function (CDF)", "Statistics",
          formats, engine);
    }
    numpods++;
    return numpods;
  }

  /**
   * Present the source code in an HTML iframe-srcdoc with highligthed key words
   *
   * @param sourceCode
   * @return
   */
  private static String toHighligthedCode(String sourceCode) {
    try {
      String html = HIGHLIGHT_IFRAME;
      html = StringUtils.replace(html, "`1`", sourceCode);
      html = StringEscapeUtils.escapeHtml4(html);
      html = "<iframe srcdoc=\"" + html
          + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>";
      return html;
    } catch (Exception ex) {
      LOGGER.debug("Pods.toHighligthedCode() failed", ex);
    }
    return sourceCode;
  }

  /**
   * Test if this is a unit conversion question? If YES return <code>F.UnitConvert(...)</code>
   * expression
   *
   * @param engine
   * @param rest
   * @return <code>F.NIL</code> if it's not a <code>F.UnitConvert(...)</code> expression
   */
  private static IExpr unitConvert(EvalEngine engine, IAST rest) {
    if (rest.argSize() == 3) {
      // check("UnitConvert(Quantity(10^(-6), \"MOhm\"),\"Ohm\" )", //
      // "1[Ohm]");
      // check("UnitConvert(Quantity(1, \"nmi\"),\"km\" )", //
      // "463/250[km]");
      IExpr inExpr = F.Quantity(rest.arg1(), F.stringx(rest.arg2().toString()));
      IExpr q1 = engine.evaluate(inExpr);
      if (q1.isQuantity()) {
        return F.UnitConvert(inExpr, F.stringx(rest.last().toString()));
      }
    }
    return F.NIL;
  }
}
