package org.matheclipse.core.builtin.graphics3d;

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
 * Implementation of SphericalPlot3D. Converts spherical coordinates (r, theta, phi) to Cartesian
 * (x, y, z) and generates optimized GraphicsComplex objects via GraphicsComplexBuilder. Supports
 * multiple surfaces with cyclic coloring.
 */
public class SphericalPlot3D extends AbstractFunctionOptionEvaluator {

  public SphericalPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 3) {
      return F.NIL;
    }

    IExpr radiusFn = ast.arg1();
    IExpr thetaRange = ast.arg2();
    IExpr phiRange = ast.arg3();

    if (!thetaRange.isList() || !phiRange.isList()) {
      return F.NIL;
    }

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

    IAST functions;
    if (radiusFn.isList()) {
      functions = (IAST) radiusFn;
    } else {
      functions = F.List(radiusFn);
    }

    ISymbol thetaVar;
    double thetaMin = 0.0, thetaMax;
    ISymbol phiVar;
    double phiMin = 0.0, phiMax;

    try {
      thetaVar = (ISymbol) ((IAST) thetaRange).arg1();
      if (((IAST) thetaRange).argSize() == 2) {
        thetaMax = ((IAST) thetaRange).arg2().evalf();
      } else {
        thetaMin = ((IAST) thetaRange).arg2().evalf();
        thetaMax = ((IAST) thetaRange).arg3().evalf();
      }

      phiVar = (ISymbol) ((IAST) phiRange).arg1();
      if (((IAST) phiRange).argSize() == 2) {
        phiMax = ((IAST) phiRange).arg2().evalf();
      } else {
        phiMin = ((IAST) phiRange).arg2().evalf();
        phiMax = ((IAST) phiRange).arg3().evalf();
      }
    } catch (RuntimeException rex) {
      return F.NIL;
    }

    return createSphericalPlot(functions, thetaVar, thetaMin, thetaMax, phiVar, phiMin, phiMax,
        plotPoints, plotStyle, options, engine);
  }

  private IExpr createSphericalPlot(IAST functions, ISymbol thetaVar, double thetaMin,
      double thetaMax, ISymbol phiVar, double phiMin, double phiMax, int plotPoints,
      IExpr plotStyle, final IExpr[] options, EvalEngine engine) {

    IASTAppendable graphicsList = F.ListAlloc();

    double thetaStep = (thetaMax - thetaMin) / (plotPoints - 1);
    double phiStep = (phiMax - phiMin) / (plotPoints - 1);

    IAST explicitStyles = F.NIL;
    if (plotStyle.isList()) {
      explicitStyles = (IAST) plotStyle;
    }

    for (int k = 1; k <= functions.argSize(); k++) {
      IExpr rExpr = functions.get(k);

      IExpr currentStyle;
      if (explicitStyles.argSize() >= 1) {
        int styleIdx = (k - 1) % explicitStyles.argSize() + 1;
        currentStyle = explicitStyles.get(styleIdx);
      } else if (plotStyle.isAST() && !plotStyle.isList()) {
        currentStyle = plotStyle;
      } else {
        int colorIdx = GraphicsOptions.incColorIndex(k - 1);
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
        double theta = thetaMin + i * thetaStep;
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);

        for (int j = 0; j < plotPoints; j++) {
          double phi = phiMin + j * phiStep;
          indices[i][j] = -1;
          try {
            IExpr subst =
                F.subst(rExpr, F.List(F.Rule(thetaVar, F.num(theta)), F.Rule(phiVar, F.num(phi))));
            IExpr rValExpr = engine.evaluate(subst);

            if (rValExpr.isNumber()) {
              double r = rValExpr.evalf();
              if (!Double.isNaN(r) && !Double.isInfinite(r)) {
                double x = r * sinTheta * Math.cos(phi);
                double y = r * sinTheta * Math.sin(phi);
                double z = r * cosTheta;
                indices[i][j] = builder.addVertex(x, y, z, null, null);
              }
            }
          } catch (Exception e) {
            // Point failed to evaluate; leave index as -1
          }
        }
      }

      for (int i = 0; i < plotPoints - 1; i++) {
        for (int j = 0; j < plotPoints - 1; j++) {
          int p1 = indices[i][j];
          int p2 = indices[i + 1][j];
          int p3 = indices[i + 1][j + 1];
          int p4 = indices[i][j + 1];

          // Only form a polygon if all 4 corners were successfully evaluated
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
    result.append(F.Rule(S.BoxRatios, F.List(F.num(1), F.num(1), F.num(1))));

    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
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
