package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Generates a surface of revolution for scalar functions or parametric curves. Uses
 * GraphicsComplexBuilder for optimized geometry and smooth shading.
 */
public class RevolutionPlot3D extends AbstractFunctionOptionEvaluator {

  public RevolutionPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2) {
      return F.NIL;
    }

    IExpr funcExpr = ast.arg1();
    IExpr tRange = ast.arg2();
    IExpr thetaRange = (argSize > 2 && ast.arg3().isList()) ? ast.arg3() : null;

    int plotPoints = 40;
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    } else if (options[0].isList()) {
      plotPoints = ((IAST) options[0]).arg1().toIntDefault(40);
    }
    if (plotPoints < 5) {
      plotPoints = 5;
    }

    IExpr plotStyle = options[3];

    if (!tRange.isList() || ((IAST) tRange).argSize() < 2) {
      return F.NIL;
    }

    IExpr tVar = ((IAST) tRange).arg1();
    double tMin = 0.0, tMax = 1.0;
    try {
      if (((IAST) tRange).argSize() >= 3) {
        tMin = ((IAST) tRange).arg2().evalf();
        tMax = ((IAST) tRange).arg3().evalf();
      } else {
        tMax = ((IAST) tRange).arg2().evalf();
      }
    } catch (RuntimeException e) {
      return F.NIL;
    }

    double thetaMin = 0.0, thetaMax = 2.0 * Math.PI;
    if (thetaRange != null && thetaRange.isList()) {
      try {
        IAST tList = (IAST) thetaRange;
        if (tList.argSize() == 2) {
          thetaMax = tList.arg2().evalf();
        } else if (tList.argSize() >= 3) {
          thetaMin = tList.arg2().evalf();
          thetaMax = tList.arg3().evalf();
        }
      } catch (RuntimeException e) {
        return F.NIL;
      }
    }

    // Resolve parametric lists
    List<IExpr> functions = new ArrayList<>();
    if (funcExpr.isList()) {
      IAST listArg = (IAST) funcExpr;
      if (listArg.argSize() > 0 && listArg.arg1().isList()) {
        // e.g. {{fx1, fz1}, {fx2, fz2}}
        for (int i = 1; i <= listArg.argSize(); i++) {
          functions.add(listArg.get(i));
        }
      } else {
        // Single parametric list {fx, fz}
        functions.add(listArg);
      }
    } else {
      functions.add(funcExpr);
    }

    IASTAppendable graphicsList = F.ListAlloc();

    IAST explicitStyles = F.NIL;
    if (plotStyle.isList()) {
      explicitStyles = (IAST) plotStyle;
    }

    double tStep = (tMax - tMin) / (plotPoints - 1);
    double thetaStep = (thetaMax - thetaMin) / (plotPoints - 1);

    for (int k = 0; k < functions.size(); k++) {
      IExpr func = functions.get(k);
      boolean isParametric = func.isList();

      IExpr currentStyle;
      if (explicitStyles.argSize() >= 1) {
        int styleIdx = k % explicitStyles.argSize() + 1;
        currentStyle = explicitStyles.get(styleIdx);
      } else if (plotStyle.isAST() && !plotStyle.isList()) {
        currentStyle = plotStyle;
      } else {
        int colorIdx = GraphicsOptions.incColorIndex(k);
        currentStyle = GraphicsOptions.plotStyleColorExpr(colorIdx, F.NIL);
      }

      GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);

      IExpr meshOption = options[5];
      if (meshOption.isFalse() || meshOption.equals(S.None)) {
        IASTAppendable edgeForm = F.ast(S.EdgeForm);
        edgeForm.append(S.None);
        builder.setStyle(currentStyle, edgeForm);
      } else {
        builder.setStyle(currentStyle);
      }

      int[][] indices = new int[plotPoints][plotPoints];

      for (int i = 0; i < plotPoints; i++) {
        double t = tMin + i * tStep;
        double rVal = 0.0, zVal = 0.0;
        boolean validPoint = false;

        try {
          IExpr subst = F.subst(func, F.List(F.Rule(tVar, F.num(t))));
          IExpr val = engine.evaluate(subst);
          if (!val.isNumber() && !val.isList()) {
            val = engine.evalN(val);
          }

          if (isParametric) {
            if (val.isList() && ((IAST) val).argSize() >= 2) {
              rVal = ((IAST) val).arg1().evalf();
              zVal = ((IAST) val).arg2().evalf();
              validPoint = !Double.isNaN(rVal) && !Double.isNaN(zVal) && !Double.isInfinite(rVal)
                  && !Double.isInfinite(zVal);
            }
          } else {
            if (val.isNumber()) {
              zVal = val.evalf();
              rVal = t;
              validPoint = !Double.isNaN(zVal) && !Double.isInfinite(zVal);
            }
          }
        } catch (RuntimeException e) {
          // Point evaluation failed, validPoint remains false
        }

        for (int j = 0; j < plotPoints; j++) {
          indices[i][j] = -1;
          if (validPoint) {
            double theta = thetaMin + j * thetaStep;
            double x = rVal * Math.cos(theta);
            double y = rVal * Math.sin(theta);
            indices[i][j] = builder.addVertex(x, y, zVal, null, null);
          }
        }
      }

      // Compose the topology mappings
      for (int i = 0; i < plotPoints - 1; i++) {
        for (int j = 0; j < plotPoints - 1; j++) {
          int p1 = indices[i][j];
          int p2 = indices[i][j + 1];
          int p3 = indices[i + 1][j + 1];
          int p4 = indices[i + 1][j];

          // Form polygon only if all 4 corners evaluated successfully
          if (p1 != -1 && p2 != -1 && p3 != -1 && p4 != -1) {
            builder.addPolygon(new int[] {p1, p2, p3, p4});
          }
        }
      }

      IExpr complex = builder.build();
      if (!complex.equals(F.NIL)) {
        graphicsList.append(complex);
      }
    }

    if (graphicsList.argSize() == 0) {
      return F.NIL;
    }

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsList);
    result.append(F.Rule(S.PlotRange, S.Automatic));
    result.append(F.Rule(S.Axes, S.True));

    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.PlotStyle, S.BoxRatios,
            S.Mesh},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.True});
  }
}