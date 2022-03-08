package org.matheclipse.io.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.io.IOInit;
import org.matheclipse.io.expression.ASTDataset;
import org.matheclipse.logging.ThreadLocalNotifyingAppender.ThreadLocalNotifierClosable;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class AJAXQueryServlet extends HttpServlet {
  private static final long serialVersionUID = 6265703737413093134L;

  static final Map<String, EvalEngine> ENGINES = new HashMap<String, EvalEngine>();

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
          + "</div>\n" + "</body>\n" + "</html>"; //

  protected static final String HTML_IFRAME = //
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "\n" + "<!DOCTYPE html PUBLIC\n"
          + "  \"-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN\"\n"
          + "  \"http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd\">\n" + "\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\" style=\"width: 100%; height: 100%; margin: 0; padding: 0\">\n"
          + "<head>\n" + "<meta charset=\"utf-8\">\n" + "<title>HTML</title>\n" + "</head>\n"
          + "<body>\n" + "`1`\n" + "</body>\n" + "</html>";

  private static final Logger LOGGER = LogManager.getLogger();

  public static volatile boolean INITIALIZED = false;

  protected boolean isRelaxedSyntax() {
    return true;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    doPost(req, res);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("text/html; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
    PrintWriter out = res.getWriter();
    try {
      if (req == null) {
        out.println(JSONBuilder.createJSONErrorString("No input expression posted!"));
        return;
      }
      String name = "query";
      String value = req.getParameter(name);
      if (value == null) {
        out.println(JSONBuilder.createJSONErrorString("No input expression posted!"));
        return;
      }

      String numericModeValue = req.getParameter("mode");
      if (numericModeValue == null) {
        numericModeValue = "";
      }

      String functionValue = req.getParameter("function");
      if (functionValue == null) {
        functionValue = "";
      }
      value = value.trim();
      if (value.length() > Short.MAX_VALUE) {
        out.println(JSONBuilder.createJSONErrorString("Input expression to large!"));
        return;
      }

      String result = evaluate(req, value, numericModeValue, functionValue, 0);
      out.println(result);
    } catch (Exception e) {
      LOGGER.error("{}.doPost() failed", getClass().getSimpleName(), e);
      String msg = e.getMessage();
      if (msg != null) {
        out.println(JSONBuilder.createJSONErrorString("Exception: " + msg));
        return;
      }
      out.println(JSONBuilder.createJSONErrorString("Exception: " + e.getClass().getSimpleName()));
      return;
    }
  }

  private String evaluate(HttpServletRequest request, String expression, String numericMode,
      String function, int counter) {
    if (expression == null || expression.length() == 0) {
      return JSONBuilder.createJSONErrorString("No input expression posted!");
    }
    if (expression.trim().length() == 0) {
      return JSONBuilder.createJSONErrorString("No input expression posted!");
    } else if (expression.length() >= Short.MAX_VALUE) {
      return JSONBuilder.createJSONErrorString(
          "Input expression greater than: " + Short.MAX_VALUE + " characters!");
    }

    String[] result = null;
    HttpSession session = request.getSession();
    LOGGER.warn("({}) In::{}", session.getId(), expression);
    final StringWriter outWriter = new StringWriter();
    WriterOutputStream wouts = new WriterOutputStream(outWriter);
    final StringWriter errorWriter = new StringWriter();
    WriterOutputStream werrors = new WriterOutputStream(errorWriter);
    try (PrintStream outs = new PrintStream(wouts);
        PrintStream errors = new PrintStream(werrors);
        ThreadLocalNotifierClosable c = ServletServer.setLogEventNotifier(outs, errors);) {

      EvalEngine engine = ENGINES.get(session.getId());
      if (engine == null) {
        engine = new EvalEngine(session.getId(), 256, 256, outs, errors, isRelaxedSyntax());
        engine.setOutListDisabled(false, (short) 100);
        engine.setPackageMode(false);
        ENGINES.put(session.getId(), engine);
      } else {
        engine.setOutPrintStream(outs);
        engine.setErrorPrintStream(errors);
      }
      result = evaluateString(engine, expression, numericMode, function, outWriter, errorWriter);
    } finally {
      // tear down associated ThreadLocal from EvalEngine
      EvalEngine.remove();
    }
    if (result == null) {
      return JSONBuilder.createJSONError("Calculation result is undefined")[1];
    }
    return result[1].toString();
  }

  private String[] evaluateString(EvalEngine engine, final String inputString,
      final String numericMode, final String function, StringWriter outWriter,
      StringWriter errorWriter) {
    String input = inputString.trim();
    if (input.length() > 1 && input.charAt(0) == '?') {
      IExpr doc = Documentation.findDocumentation(input);
      return JSONBuilder.createJSONResult(engine, doc, outWriter, errorWriter);
    }
    try {
      EvalEngine.setReset(engine);
      ExprParser parser = new ExprParser(engine, isRelaxedSyntax());
      // throws SyntaxError exception, if syntax isn't valid
      IExpr inExpr = parser.parse(input);
      if (inExpr != null) {
        long numberOfLeaves = inExpr.leafCount();
        if (numberOfLeaves > Config.MAX_INPUT_LEAVES) {
          return JSONBuilder.createJSONError("Input expression too big!");
        }
        if (numericMode.equals("N")) {
          inExpr = F.N(inExpr);
        }
        // inExpr contains the user input from the web interface in
        // internal format now
        StringWriter outBuffer = new StringWriter();
        IExpr outExpr;
        outExpr = evalTopLevel(engine, outBuffer, inExpr);
        if (outExpr != null) {
          if (outExpr.isAST(S.Graphics)) {
            try {
              String html = Config.SVG_PAGE;
              StringBuilder stw = new StringBuilder();
              GraphicsFunctions.graphicsToSVG((IAST) outExpr, stw);
              html = StringUtils.replace(html, "`1`", stw.toString());
              html = StringEscapeUtils.escapeHtml4(html);
              return JSONBuilder.createJSONJavaScript("<iframe srcdoc=\"" + html
                  + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
            } catch (Exception ex) {
              LOGGER.debug("{}.evaluateString() failed", getClass().getSimpleName(), ex);
            }
          } else if (outExpr.isASTSizeGE(S.Graphics3D, 2)) {
            StringBuilder buf = new StringBuilder();
            if (GraphicsFunctions.renderGraphics3D(buf, (IAST) outExpr, engine)) {
              try {
                return JSONBuilder.createGraphics3DIFrame(JSBuilder.GRAPHICS3D_IFRAME_TEMPLATE,
                    buf.toString());
              } catch (Exception ex) {
                LOGGER.debug("{}.evaluateString() failed", getClass().getSimpleName(), ex);
              }
            }
          }
          if (outExpr.isASTSizeGE(S.Show, 2)) {
            IAST show = (IAST) outExpr;
            return JSONBuilder.createJSONShow(engine, show);
          } else if (outExpr instanceof GraphExpr) {
            String javaScriptStr = GraphFunctions.graphToJSForm((GraphExpr) outExpr);
            if (javaScriptStr != null) {
              String html = VISJS_IFRAME;
              html = StringUtils.replace(html, "`1`", javaScriptStr);
              html = StringUtils.replace(html, "`2`", //
                  "  var options = { };\n" //
              );
              html = StringEscapeUtils.escapeHtml4(html);
              return JSONBuilder.createJSONJavaScript("<iframe srcdoc=\"" + html
                  + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
            }
          } else if (outExpr instanceof ASTDataset) {
            String javaScriptStr = ((ASTDataset) outExpr).datasetToJSForm();
            if (javaScriptStr != null) {
              String htmlSnippet = javaScriptStr.trim();
              // String html = HTML_IFRAME;
              // html = StringUtils.replace(html, "`1`", htmlSnippet);
              // html = StringEscapeUtils.escapeHtml4(html);
              return JSONBuilder.createJSONHTML(engine, htmlSnippet, outWriter, errorWriter);
              // return JSONBuilder.createJSONJavaScript(
              // "<iframe srcdoc=\""
              // + html
              // + "\" style=\"display: block; width: 100%; height: 100%;
              // border: none;\"></iframe>");
            }
          } else if (outExpr.isAST(S.JSFormData, 3)) {
            IAST jsFormData = (IAST) outExpr;
            String jsLibraryType = jsFormData.arg2().toString();
            if (jsLibraryType.equals("mathcell")) {
              try {
                return JSONBuilder.createMathcellIFrame(JSBuilder.MATHCELL_IFRAME_TEMPLATE,
                    jsFormData.arg1().toString());
              } catch (Exception ex) {
                LOGGER.debug("{}.evaluateString() failed", getClass().getSimpleName(), ex);
              }
            } else if (jsLibraryType.equals("jsxgraph")) {
              try {
                return JSONBuilder.createJSXGraphIFrame(JSBuilder.JSXGRAPH_IFRAME_TEMPLATE,
                    jsFormData.arg1().toString());
              } catch (Exception ex) {
                LOGGER.debug("{}.evaluateString() failed", getClass().getSimpleName(), ex);
              }
            } else if (jsLibraryType.equals("plotly")) {
              try {
                return JSONBuilder.createPlotlyIFrame(JSBuilder.PLOTLY_IFRAME_TEMPLATE,
                    jsFormData.arg1().toString());
              } catch (Exception ex) {
                LOGGER.debug("{}.evaluateString() failed", getClass().getSimpleName(), ex);
              }
            } else if (jsLibraryType.equals("treeform")) {
              try {
                String manipulateStr = jsFormData.arg1().toString();
                String html = VISJS_IFRAME;
                html = StringUtils.replace(html, "`1`", manipulateStr);
                html = StringUtils.replace(html, "`2`", //
                    "  var options = {\n" + "		  edges: {\n" + "              smooth: {\n"
                        + "                  type: 'cubicBezier',\n"
                        + "                  forceDirection:  'vertical',\n"
                        + "                  roundness: 0.4\n" + "              }\n"
                        + "          },\n" + "          layout: {\n"
                        + "              hierarchical: {\n"
                        + "                  direction: \"UD\"\n" + "              }\n"
                        + "          },\n" + "          nodes: {\n" + "            shape: 'box'\n"
                        + "          },\n" + "          physics:false\n" + "      }; " //
                );
                html = StringEscapeUtils.escapeHtml4(html);
                return JSONBuilder.createJSONJavaScript("<iframe srcdoc=\"" + html
                    + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
              } catch (Exception ex) {
                LOGGER.debug("{}.evaluateString() failed", getClass().getSimpleName(), ex);
              }
            }
          } else if (outExpr.isString()) {
            IStringX str = (IStringX) outExpr;
            if (str.getMimeType() == IStringX.TEXT_HTML) {
              String htmlSnippet = str.toString();
              String htmlPage = HTML_IFRAME;
              htmlPage = StringUtils.replace(htmlPage, "`1`", htmlSnippet);
              return JSONBuilder.createJSONJavaScript("<iframe srcdoc=\"" + htmlPage
                  + "\" style=\"display: block; width: 100%; height: 100%; border: none;\" ></iframe>");
            }
          }
          return JSONBuilder.createJSONResult(engine, outExpr, outWriter, errorWriter);
        }
        return createOutput(outBuffer, null, engine, function);

      } else {
        return JSONBuilder.createJSONError("Input string parsed to null");
      }
    } catch (AbortException se) {
      return JSONBuilder.createJSONResult(engine, S.$Aborted, outWriter, errorWriter);
    } catch (FailedException se) {
      return JSONBuilder.createJSONResult(engine, S.$Failed, outWriter, errorWriter);
    } catch (SyntaxError se) {
      return JSONBuilder.createJSONSyntaxError(se.getMessage());
    } catch (MathException se) {
      return JSONBuilder.createJSONError(se.getMessage());
    } catch (IOException e) {
      String msg = e.getMessage();
      if (msg != null) {
        return JSONBuilder.createJSONError("IOException occured: " + msg);
      }
      return JSONBuilder.createJSONError("IOException occured");
    } catch (Exception e) {
      // error message
      LOGGER.error("{}.evaluateString() failed", getClass().getSimpleName(), e);
      String msg = e.getMessage();
      if (msg != null) {
        return JSONBuilder.createJSONError("Error in evaluateString: " + msg);
      }
      return JSONBuilder
          .createJSONError("Error in evaluateString: " + e.getClass().getSimpleName());
    }
  }

  private static IExpr evalTopLevel(EvalEngine engine, final StringWriter buf,
      final IExpr parsedExpression) {
    IExpr result;
    EvalEngine[] engineRef = new EvalEngine[] {engine};
    result = ExprEvaluator.evalTopLevel(parsedExpression, engineRef);
    engine = engineRef[0];
    if ((result != null) && !result.equals(S.Null)) {
      OutputFormFactory.get(engine.isRelaxedSyntax()).convert(buf, result);
    }
    return result;
  }

  private static String[] listUserVariables(String userId) {
    StringBuilder bldr = new StringBuilder();
    // boolean rest = false;
    bldr.append("{");
    // QueryResultIterable<UserSymbolEntity> qri = UserSymbolService.getAll(userId);
    // for (UserSymbolEntity userSymbolEntity : qri) {
    // if (rest) {
    // bldr.append(", ");
    // } else {
    // rest = true;
    // }
    // bldr.append(userSymbolEntity.getSymbolName());
    // }
    bldr.append("}");
    return new String[] {"expr", bldr.toString()};
  }

  private static String[] createOutput(StringWriter buffer, IExpr rhsExpr, EvalEngine engine,
      String function) {

    boolean textEval = true;
    // if (rhsExpr != null && rhsExpr instanceof IAST &&
    // rhsExpr.isAST(F.Show,
    // 2)) {
    // IAST ast = (IAST) rhsExpr;
    // if (ast.size() == 2 && ast.get(0).toString().equals("Show")) {
    // StringBufferWriter outBuffer = new StringBufferWriter();
    // outBuffer = new StringBufferWriter();
    // StringBufferWriter graphicBuf = new StringBufferWriter();
    // IExpr result = (IExpr) ast.get(1);
    // graphicBuf.setIgnoreNewLine(true);
    // OutputFormFactory outputFormFactory = OutputFormFactory.get();
    // outputFormFactory.convert(graphicBuf, result);
    // createJavaView(outBuffer, graphicBuf.toString());
    // textEval = false;
    // return new String[] { "applet", outBuffer.toString() };
    // }
    // }

    if (textEval) {
      String res = buffer.toString();
      if (function.length() > 0 && function.equals("$mathml")) {
        MathMLUtilities mathUtil = new MathMLUtilities(engine, false, true);
        StringWriter stw = new StringWriter();
        if (!mathUtil.toMathML(res, stw)) {
          return new String[] {"error", "Max. output size exceeded " + Config.MAX_OUTPUT_SIZE};
        }
        return new String[] {"mathml", stw.toString()};
      } else if (function.length() > 0 && function.equals("$tex")) {
        TeXUtilities texUtil = new TeXUtilities(engine, true);
        StringWriter stw = new StringWriter();
        if (!texUtil.toTeX(res, stw, false)) {
          return new String[] {"error", "Max. output size exceeded " + Config.MAX_OUTPUT_SIZE};
        }
        return new String[] {"tex", stw.toString()};
      } else {
        return new String[] {"expr", res};
      }
    }
    return new String[] {"error", "Error in createOutput"};
  }

  /**
   * Try to read an older evaluation from the Memcache
   *
   * @return null if there is no suitable evaluation stored in the memcache
   */
  // private static IExpr getFromMemcache(IExpr lhsExpr) {
  // try {
  // ArrayList list = new ArrayList<IExpr>();// F.ast(null);
  // Map<IExpr, IExpr> map = new HashMap<IExpr, IExpr>();
  // lhsExpr = lhsExpr.variables2Slots(map, list);
  // if (lhsExpr != null) {
  // String lhsString = lhsExpr.toString();
  // IExpr expr = (IExpr) cache.get(lhsString);
  // if (expr != null) {
  // if (list.size() > 0) {
  // IAST l=F.List();
  // l.addAll(list);
  // expr = Function.replaceSlots(expr, l);
  // }
  // return expr;
  // }
  //
  // }
  // } catch (Exception e) {
  // LOGGER.debug("{}.getFromMemcache() failed", getClass().getSimpleName(), e);
  // }
  // return null;
  // }

  /**
   * Save an evaluation in the memcache.
   *
   * @return false if the lhsExpr or rhsExpr expressions contain $-variables or patterns
   */
  // private static boolean putToMemcache(IExpr lhsExpr, IExpr rhsExpr) {
  // try {
  // ArrayList<IExpr> list = new ArrayList<IExpr>();
  // Map<IExpr, IExpr> map = new HashMap<IExpr, IExpr>();
  // lhsExpr = lhsExpr.variables2Slots(map, list);
  // rhsExpr = rhsExpr.variables2Slots(map, list);
  // if (lhsExpr != null && rhsExpr != null) {
  // String lhsString = lhsExpr.toString();
  // int lhsHash = lhsExpr.hashCode();
  // cache.put(lhsString, rhsExpr);
  // return true;
  // }
  // } catch (Exception e) {
  // LOGGER.debug("{}.putToMemcache() failed", getClass().getSimpleName(), e);
  // }
  // return false;
  // }

  public static String toHTML(String res) {
    if (res != null) {
      StringBuffer sbuf = new StringBuffer(res.length() + 50);

      char ch;
      for (int i = 0; i < res.length(); i++) {
        ch = res.charAt(i);
        switch (ch) {
          case '>':
            sbuf.append("&gt;");
            break;
          case '<':
            sbuf.append("&lt;");
            break;
          case '&':
            sbuf.append("&amp;");
            break;
          case '"':
            sbuf.append("&quot;");
            break;
          default:
            sbuf.append(res.charAt(i));
        }
      }
      return sbuf.toString();
    }
    return "";
  }

  public static String toHTMLNL(String res) {
    if (res != null) {
      StringBuffer sbuf = new StringBuffer(res.length() + 50);

      char ch;
      for (int i = 0; i < res.length(); i++) {
        ch = res.charAt(i);
        switch (ch) {
          case '>':
            sbuf.append("&gt;");
            break;
          case '<':
            sbuf.append("&lt;");
            break;
          case '&':
            sbuf.append("&amp;");
            break;
          case '"':
            sbuf.append("&quot;");
            break;
          case '\n':
            sbuf.append("<br/>");
            break;
          case ' ':
            sbuf.append("&nbsp;");
            break;
          default:
            sbuf.append(res.charAt(i));
        }
      }
      return sbuf.toString();
    }
    return "";
  }

  @Override
  public void init() throws ServletException {
    super.init();
    initialization();
  }

  protected synchronized void initialization() {
    if (INITIALIZED) {
      return;
    }
    INITIALIZED = true;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    ToggleFeature.COMPILE = true;
    ToggleFeature.COMPILE_PRINT = true;
    Config.UNPROTECT_ALLOWED = false;
    Config.USE_MANIPULATE_JS = true;
    // disable threads for JAS only on google appengine
    Config.JAS_NO_THREADS = false;
    Config.JAVA_UNSAFE = true;
    // Config.THREAD_FACTORY =
    // com.google.appengine.api.ThreadManager.currentRequestThreadFactory();
    Config.MATHML_TRIG_LOWERCASE = false;
    // Config.MAX_AST_SIZE = ((int) Short.MAX_VALUE) * 8;
    // Config.MAX_OUTPUT_SIZE = Short.MAX_VALUE;
    // Config.MAX_BIT_LENGTH = ((int) Short.MAX_VALUE) * 8;
    // Config.MAX_INPUT_LEAVES = 1000L;
    // Config.MAX_MATRIX_DIMENSION_SIZE = 100;
    // Config.MAX_POLYNOMIAL_DEGREE = 100;

    EvalEngine engine = new EvalEngine(isRelaxedSyntax());
    EvalEngine.set(engine);
    Config.FILESYSTEM_ENABLED = true;
    F.initSymbols();
    IOInit.init();
    engine.setRecursionLimit(Config.DEFAULT_RECURSION_LIMIT);
    engine.setIterationLimit(Config.DEFAULT_ITERATION_LIMIT);

    S.Plot.setEvaluator(org.matheclipse.core.reflection.system.Plot.CONST);
    S.Plot3D.setEvaluator(org.matheclipse.core.reflection.system.Plot3D.CONST);
    // F.Show.setEvaluator(org.matheclipse.core.builtin.graphics.Show.CONST);
    // Config.JAS_NO_THREADS = true;
    // AJAXQueryServlet.log.info(servlet + " initialized");
  }
}
