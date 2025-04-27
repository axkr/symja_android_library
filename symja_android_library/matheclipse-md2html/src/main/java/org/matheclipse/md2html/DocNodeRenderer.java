package org.matheclipse.md2html;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.image.expression.data.ImageExpr;

public class DocNodeRenderer extends CoreHtmlNodeRenderer {
  private static final Logger LOGGER = LogManager.getLogger(DocNodeRenderer.class);

  private final HtmlWriter html;

  // private boolean inHeader=false;
  public DocNodeRenderer(HtmlNodeRendererContext context) {
    super(context);
    this.html = context.getWriter();
  }

  @Override
  public Set<Class<? extends Node>> getNodeTypes() {
    // Return the node types we want to use this renderer for.
    Set<Class<? extends Node>> set = new HashSet<Class<? extends Node>>();
    set.add(FencedCodeBlock.class);
    set.add(Link.class);
    // set.add(TeX.class);
    return set;
  }

  @Override
  public void render(Node node) {
    if (node instanceof FencedCodeBlock) {
      fencedCodeBlock((FencedCodeBlock) node);
    } else if (node instanceof Link) {
      link((Link) node);
      // } else if (node instanceof TeX) {
      // tex((TeX) node);
    } else {
      IndentedCodeBlock codeBlock = (IndentedCodeBlock) node;
      html.line();
      html.tag("pre");
      html.text(codeBlock.getLiteral());
      html.tag("/pre");
      html.line();
    }
  }

  private void fencedCodeBlock(FencedCodeBlock fencedCodeBlock) {
    String info = fencedCodeBlock.getInfo();
    String codeStr = fencedCodeBlock.getLiteral();
    System.out.println(info + "\n" + codeStr);

    WolframFormFactory wolframForm = WolframFormFactory.get();
    String code = codeStr.trim();
    if (info.equals("mma") || info.equals("mathematica")) {
      Map<String, String> attributes = new LinkedHashMap<>();
      attributes.put("class", "language-mathematica");
      context.extendAttributes(fencedCodeBlock, "code", attributes);
      visit(fencedCodeBlock);
      if (renderMMA(wolframForm, code)) {
        return;
      }
    } else if (info.equals("tex")) {
      if (renderTeX(wolframForm, code)) {
        return;
      }
    }

    visit(fencedCodeBlock);
    html.text(code);
  }

  private boolean renderMMA(WolframFormFactory wolframForm, String code) {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);

    try {
      ExprParser parser = new ExprParser(engine, true);
      IExpr expr = parser.parse(code);
      if (expr != null) {
        IExpr result = F.eval(expr);
        if (result.isSameHeadSizeGE(S.Graphics, 2)) {
          StringBuilder buf = new StringBuilder();
          if (GraphicsFunctions.renderGraphics2D(buf, (IAST) result, EvalEngine.get())) {

            String graphicsStr = buf.toString();

            String htmlStr =
                JSBuilder.buildGraphics2D(JSBuilder.GRAPHICS2D_IFRAME_TEMPLATE, graphicsStr);
            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;

          }
          // return openSVGOnDesktop((IAST) expr);
        } else if (result.isSameHeadSizeGE(S.Graphics3D, 2)) {
          StringBuilder buf = new StringBuilder();
          if (GraphicsFunctions.renderGraphics3D(buf, (IAST) result, EvalEngine.get())) {

            String graphics3DStr = buf.toString();
            String htmlStr =
                JSBuilder.buildGraphics3D(JSBuilder.GRAPHICS3D_IFRAME_TEMPLATE, graphics3DStr);
            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;
          }
        } else if (result instanceof GraphExpr) {
          String javaScriptStr = ((GraphExpr) result).graphToJSForm();
          if (javaScriptStr != null) {
            String htmlStr = JSBuilder.VISJS_IFRAME;
            htmlStr = StringUtils.replace(htmlStr, "`1`", javaScriptStr);
            htmlStr = StringUtils.replace(htmlStr, "`2`", //
                "  var options = { };\n" //
            );

            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;
          }
        } else if (result instanceof ImageExpr) {
          ImageExpr imageExpr = (ImageExpr) result;
          byte[] data = imageExpr.toData();
          if (data != null) {
            String htmlStr = JSBuilder.IMAGE_IFRAME_TEMPLATE;
            String[] argsToRender = new String[3];
            argsToRender[0] = imageExpr.toBase64EncodedString();
            htmlStr = Errors.templateRender(htmlStr, argsToRender);

            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 100%; height: 1050%; border: none;\" ></iframe>");
            return true;
          }
        } else if (result.isAST(F.JSFormData, 3)) {
          IAST jsFormData = (IAST) result;
          if (jsFormData.arg2().toString().equals(JSBuilder.MATHCELL_STR)) {

            String htmlStr = JSBuilder.buildMathcell(JSBuilder.MATHCELL_IFRAME_TEMPLATE,
                jsFormData.arg1().toString());
            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;
          } else if (jsFormData.arg2().toString().equals(JSBuilder.ECHARTS_STR)) {

            String htmlStr = JSBuilder.buildECharts(JSBuilder.ECHARTS_IFRAME_TEMPLATE,
                jsFormData.arg1().toString());
            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;
          } else if (jsFormData.arg2().toString().equals(JSBuilder.JSXGRAPH_STR)) {

            String htmlStr = JSBuilder.buildJSXGraph(JSBuilder.JSXGRAPH_IFRAME_TEMPLATE,
                jsFormData.arg1().toString());
            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;

          } else if (jsFormData.arg2().toString().equals(JSBuilder.PLOTLY_STR)) {
            String htmlStr = JSBuilder.buildPlotly(JSBuilder.PLOTLY_IFRAME_TEMPLATE,
                jsFormData.arg1().toString());
            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;
          } else if (jsFormData.arg2().toString().equals(JSBuilder.TREEFORM_STR)) {
            String manipulateStr = jsFormData.arg1().toString();
            String htmlStr = JSBuilder.VISJS_IFRAME;
            htmlStr = StringUtils.replace(htmlStr, "`1`", manipulateStr);
            htmlStr = StringUtils.replace(htmlStr, "`2`", //
                "  var options = {\n" + //
                    "         edges: {\n" + //
                    "              smooth: {\n" + //
                    "                  type: 'cubicBezier',\n" + //
                    "                  forceDirection:  'vertical',\n" + //
                    "                  roundness: 0.4\n" + //
                    "              }\n" + //
                    "          },\n" + //
                    "          layout: {\n" + //
                    "              hierarchical: {\n" + //
                    "                  direction: \"UD\"\n" + //
                    "              }\n" + //
                    "          },\n" + //
                    "          nodes: {\n" + "            shape: 'box'\n" + "          },\n" + //
                    "          physics:false\n" + //
                    "      }; " //
            );
            htmlStr = StringEscapeUtils.escapeHtml4(htmlStr);
            html.raw("<iframe srcdoc=\"" + htmlStr
                + "\" style=\"display: block; width: 600px; height: 600px; border: none;\" ></iframe>");
            return true;

          }
        } else {
          html.tag("pre");
          code = wolframForm.toString(result);
          html.text(StringEscapeUtils.escapeEcmaScript(code));
          html.tag("/pre");
          return true;
        }

      }
    } catch (RuntimeException rex) {
      LOGGER.debug("DocNodeRenderer#renderMMA() failed", getClass().getSimpleName(), rex);
    }
    return false;
  }

  private boolean renderTeX(WolframFormFactory wolframForm, String code) {
    EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);

    try {
      ExprParser parser = new ExprParser(engine, true);
      IExpr expr = parser.parse(code);
      if (expr != null) {
        IExpr result = F.eval(F.TeXForm(expr));
        code = result.toString();
        html.text("$$" + code + "$$");
        return true;
      }
    } catch (RuntimeException rex) {
      LOGGER.debug("DocNodeRenderer#renderMMA() failed", getClass().getSimpleName(), rex);
    }
    return false;
  }

  private void link(Link link) {
    String destination = link.getDestination();
    int index = destination.indexOf(".md");
    if (index > 0) {
      String functionName = destination.substring(0, index);
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      try (InputStream is = classloader.getResourceAsStream("doc/functions/" + destination)) {
        if (is != null) {
          destination = "javascript:loadDoc('/functions/" + functionName + "')";
        } else {
          destination = "javascript:loadDoc('/" + functionName + "')";
        }
        link.setDestination(destination);

        Map<String, String> attrs = new HashMap<>();
        attrs.put("href", destination);
        html.tag("a", attrs);
        // html.text(link.getTitle());
        if (link.getFirstChild() != null) {
          super.render(link.getFirstChild());
        }
        html.tag("/a");
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      visit(link);
    }
  }

  // private void tex(TeX teXNode) {
  // Text text = (Text) teXNode.getFirstChild();
  // html.raw(text.getLiteral());
  // }

}

