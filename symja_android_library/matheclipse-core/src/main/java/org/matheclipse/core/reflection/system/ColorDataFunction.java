package org.matheclipse.core.reflection.system;

import java.util.Locale;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ColorDataIndexed;
import org.matheclipse.core.tensor.img.ColorDataLists;
import org.matheclipse.core.tensor.img.ColorFormat;

public class ColorDataFunction extends AbstractFunctionEvaluator {

  public ColorDataFunction() {}

  public static IAST applyGradient(ColorDataGradients gradientDetails, IExpr param) {
    IExpr result = gradientDetails.apply(param);
    if (result.isAST() && result.size() == 5) {
      return F.RGBColor(F.Divide(result.getAt(1), F.num(255)),
          F.Divide(result.getAt(2), F.num(255)), F.Divide(result.getAt(3), F.num(255)),
          F.Divide(result.getAt(4), F.num(255)));
    }
    return F.NIL;
  }

  public static int applyGradientToRGB(ColorDataGradients gradientDetails, IExpr param) {
    IExpr result = gradientDetails.apply(param);

    // Check if result is an AST with 4 arguments (Head + 4 args = size 5)
    if (result.isAST() && result.size() == 5) {
      IExpr rgbColor = EvalEngine.get()
          .evaluate(F.RGBColor(F.Divide(result.getAt(1), F.num(255)),
              F.Divide(result.getAt(2), F.num(255)), F.Divide(result.getAt(3), F.num(255)),
              F.Divide(result.getAt(4), F.num(255))));

      if (rgbColor.isAST(S.RGBColor)) {
        IAST ast = (IAST) rgbColor;

        if (ast.argSize() >= 3) {
          // Extract the normalized color components using evalf()
          float r = (float) ast.arg1().evalf();
          float g = (float) ast.arg2().evalf();
          float b = (float) ast.arg3().evalf();
          float a = ast.argSize() >= 4 ? (float) ast.arg4().evalf() : 1.0f;

          RGBColor color = new RGBColor(r, g, b, a);
          return color.getRGB();
        }
      }
    }
    return Integer.MIN_VALUE;
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.head().isAST() && ast.head().head() == S.ColorDataFunction) {
      return evaluateApplication(ast, (IAST) ast.head(), engine);
    }
    return F.NIL;
  }

  private IExpr evaluateApplication(IAST applicationAst, IAST functionAst, EvalEngine engine) {
    if (applicationAst.size() != 2) {
      return F.NIL;
    }
    if (functionAst.size() < 3) {
      return F.NIL;
    }

    IExpr arg1 = functionAst.arg1();
    IExpr arg2 = functionAst.arg2();

    if (!arg1.isString() || !arg2.isString()) {
      return F.NIL;
    }

    String name = arg1.toString();
    String type = arg2.toString();

    IExpr param = applicationAst.arg1();

    if (param.isString()) {
      String prop = param instanceof IStringX ? param.toString() : param.toString();
      if (prop.startsWith("\"") && prop.endsWith("\"")) {
        prop = prop.substring(1, prop.length() - 1);
      }

      if (prop.equals("Name")) {
        return arg1;
      }
      if (prop.equals("Range")) {
        if (functionAst.size() >= 4) {
          return functionAst.arg3();
        }
        if (type.equals("Gradients")) {
          return F.List(F.C0, F.C1);
        }
        if (type.equals("Indexed")) {
          try {
            ColorDataLists listDetails = ColorDataLists.valueOf(name);
            return F.List(F.C1, F.ZZ(listDetails.cyclic().length()));
          } catch (Exception e) {
          }
        }
      }
      return F.NIL;
    }

    if (type.equals("Gradients")) {
      return evaluateGradient(name, param);
    } else if (type.equals("Indexed")) {
      return evaluateIndexed(name, param);
    }

    return F.NIL;
  }

  private static IExpr evaluateGradient(String name, IExpr param) {
    try {
      ColorDataGradients gradientDetails = ColorDataGradients.valueOf(name.toUpperCase(Locale.US));
      return applyGradient(gradientDetails, param);
    } catch (IllegalArgumentException e) {
      String search = name.replace(" ", "").replace("_", "").toUpperCase(Locale.US);
      for (ColorDataGradients g : ColorDataGradients.values()) {
        String gName = g.name().replace("_", "");
        if (gName.equals(search)) {
          return applyGradient(g, param);
        }
      }
    }
    return F.NIL;
  }

  private IExpr evaluateIndexed(String name, IExpr param) {
    try {
      ColorDataLists listDetails = ColorDataLists.valueOf(name);
      ColorDataIndexed colorData = listDetails.cyclic();

      if (param instanceof INumber) {
        int index = param.toIntDefault(-1);
        RGBColor rgb = colorData.getRGBColor(index - 1);
        IAST res = ColorFormat.toVector(rgb);

        if (res.isAST() && res.size() == 5) {
          return F.RGBColor(F.Divide(res.getAt(1), F.num(255)), F.Divide(res.getAt(2), F.num(255)),
              F.Divide(res.getAt(3), F.num(255)), F.Divide(res.getAt(4), F.num(255)));
        }
      }
    } catch (Exception e) {
    }
    return F.NIL;
  }

}
