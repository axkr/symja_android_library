package org.matheclipse.core.mathcell;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.ManipulateFunction;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.expression.data.ImageExpr;
import org.matheclipse.io.IOInit;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public abstract class BasePlotExample {
  static {
    Locale.setDefault(Locale.US);
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    ToggleFeature.COMPILE = true;
    ToggleFeature.COMPILE_PRINT = true;
    Config.JAVA_UNSAFE = true;
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.USE_VISJS = true;
    Config.FILESYSTEM_ENABLED = true;
    ManipulateFunction.AUTOSIZE = false;
    IOInit.init();
  }

  public String exampleFunction() {
    return "Manipulate(ListPlot(Table({Sin(t), Cos(t*a)}, {t, 100})), {a,1,4,1})";
  }

  public void generateHTML() {
    try {
      Config.FILESYSTEM_ENABLED = true;
      ExprEvaluator util = new ExprEvaluator();

      IExpr result = util.eval(exampleFunction());
      if (result instanceof ImageExpr) {
        ImageExpr imageExpr = (ImageExpr) result;
        // https://stackoverflow.com/a/22891895/24819
        BufferedImage bImage = imageExpr.getBufferedImage();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final OutputStream b64 = Base64.getEncoder().wrap(outputStream)) {
          ImageIO.write(bImage, "png", b64);
          String html = JSBuilder.IMAGE_TEMPLATE;
          String[] argsToRender = new String[3];
          argsToRender[0] = outputStream.toString();
          html = Errors.templateRender(html, argsToRender);
          System.out.println(html);
          F.openHTMLOnDesktop(html);
        }
      } else if (result.isAST(F.JSFormData, 3)) {
        String js;
        if (result.second().toString().equals("mathcell")) {
          String manipulateStr = ((IAST) result).arg1().toString();
          js = JSBuilder.buildMathcell(JSBuilder.MATHCELL_TEMPLATE, manipulateStr);
          // js = Config.MATHCELL_PAGE;
          // js = StringUtils.replace(js, "`1`", manipulateStr);
        } else if (result.second().toString().equals("treeform")) {
          String manipulateStr = ((IAST) result).arg1().toString();
          js = Config.VISJS_PAGE;
          js = StringUtils.replace(js, "`1`", manipulateStr);
          js = StringUtils.replace(js, "`2`", //
              "  var options = {\n" + "		  edges: {\n" + "              smooth: {\n"
                  + "                  type: 'cubicBezier',\n"
                  + "                  forceDirection:  'vertical',\n"
                  + "                  roundness: 0.4\n" + "              }\n" + "          },\n"
                  + "          layout: {\n" + "              hierarchical: {\n"
                  + "                  direction: \"UD\"\n" + "              }\n" + "          },\n"
                  + "          nodes: {\n" + "            shape: 'box'\n" + "          },\n"
                  + "          physics:false\n" + "      }; ");
        } else if (result.second().toString().equals("traceform")) {
          String jsStr = ((IAST) result).arg1().toString();
          js = Config.TRACEFORM_PAGE;
          js = StringUtils.replace(js, "`1`", jsStr);
        } else if (result.second().toString().equals("mermaid")) {
          String manipulateStr = ((IAST) result).arg1().toString();
          js = JSBuilder.buildMermaid(JSBuilder.MERMAID_TEMPLATE, manipulateStr);
        } else if (result.second().toString().equals("plotly")) {
          String manipulateStr = ((IAST) result).arg1().toString();
          js = JSBuilder.buildPlotly(JSBuilder.PLOTLY_TEMPLATE, manipulateStr);
          // js = Config.PLOTLY_PAGE;
          // js = StringUtils.replace(js, "`1`", manipulateStr);
        } else {
          String manipulateStr = ((IAST) result).arg1().toString();
          js = JSBuilder.buildJSXGraph(JSBuilder.JSXGRAPH_TEMPLATE, manipulateStr);
        }
        System.out.println(js);
        F.openHTMLOnDesktop(js);
      } else {
        IExpr outExpr = result;
        if (result.isAST(S.Graphics)//
            || result.isAST(F.Graphics3D)) {
          outExpr = F.Show(outExpr);
        }
        String html = F.show(outExpr);
        System.out.println(html);
         return;
         // if (result.isString()) {
         // IStringX str = (IStringX) result;
         // if (str.getMimeType() == IStringX.TEXT_HTML) {
         // String htmlSnippet = str.toString();
         // String htmlPage = Config.HTML_PAGE;
         // htmlPage = StringUtils.replace(htmlPage, "`1`", htmlSnippet);
         // System.out.println(htmlPage);
         // F.openHTMLOnDesktop(htmlPage);
         // return;
         // }
         // }
         // System.out.println(result.toString());
      }
    } catch (SyntaxError e) {
      // catch Symja parser errors here
      System.out.println(e.getMessage());
    } catch (MathException me) {
      // catch Symja math errors here
      System.out.println(me.getMessage());
      me.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } catch (final StackOverflowError soe) {
      System.out.println(soe.getMessage());
    } catch (final OutOfMemoryError oome) {
      System.out.println(oome.getMessage());
    }
  }


}
