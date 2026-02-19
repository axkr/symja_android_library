package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of SphericalPlot3D. Converts spherical coordinates (r, theta, phi) to Cartesian
 * (x, y, z) and generates a GraphicsComplex. Supports multiple surfaces with cyclic coloring.
 */
public class SphericalPlot3D extends AbstractFunctionOptionEvaluator {

  public SphericalPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 3) {
      return F.NIL;
    }

    // 1. Parse Arguments
    IExpr radiusFn = ast.arg1();
    IExpr thetaRange = ast.arg2();
    IExpr phiRange = ast.arg3();

    if (!thetaRange.isList() || !phiRange.isList()) {
      return F.NIL;
    }

    // 2. Parse Options
    int plotPoints = 40;
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    } else if (options[0].isList()) {
      plotPoints = ((IAST) options[0]).arg1().toIntDefault(40);
    }
    if (plotPoints < 5)
      plotPoints = 5;

    IExpr plotStyle = options[3];

    // 3. Handle Single vs Multiple Functions
    IAST functions;
    if (radiusFn.isList()) {
      functions = (IAST) radiusFn;
    } else {
      functions = F.List(radiusFn);
    }

    // 4. Parse Ranges
    ISymbol thetaVar;
    double thetaMin = 0.0, thetaMax;
    ISymbol phiVar;
    double phiMin = 0.0, phiMax;

    try {
      thetaVar = (ISymbol) ((IAST) thetaRange).arg1();
      if (((IAST) thetaRange).size() == 3) {
        thetaMax = ((IAST) thetaRange).arg2().evalf();
      } else {
        thetaMin = ((IAST) thetaRange).arg2().evalf();
        thetaMax = ((IAST) thetaRange).arg3().evalf();
      }

      phiVar = (ISymbol) ((IAST) phiRange).arg1();
      if (((IAST) phiRange).size() == 3) {
        phiMax = ((IAST) phiRange).arg2().evalf();
      } else {
        phiMin = ((IAST) phiRange).arg2().evalf();
        phiMax = ((IAST) phiRange).arg3().evalf();
      }
    } catch (RuntimeException rex) {
      return F.NIL;
    }

    // 5. Generate Graphics3D
    return createSphericalPlot(functions, thetaVar, thetaMin, thetaMax, phiVar, phiMin, phiMax,
        plotPoints, plotStyle, engine);
  }

  private IExpr createSphericalPlot(IAST functions, ISymbol thetaVar, double thetaMin,
      double thetaMax, ISymbol phiVar, double phiMin, double phiMax, int plotPoints,
      IExpr plotStyle, EvalEngine engine) {

    IASTAppendable allPoints = F.ListAlloc();
    IASTAppendable primitives = F.ListAlloc();

    int globalPointIndex = 0;

    double thetaStep = (thetaMax - thetaMin) / (plotPoints - 1);
    double phiStep = (phiMax - phiMin) / (plotPoints - 1);

    // Prepare styles list if explicit
    IAST explicitStyles = F.NIL;
    if (plotStyle.isList()) {
      explicitStyles = (IAST) plotStyle;
    }

    // Loop over each function r1, r2...
    for (int k = 1; k < functions.size(); k++) {
      IExpr rExpr = functions.get(k);
      int startPointIndex = globalPointIndex;

      // --- Generate Grid Points ---
      for (int i = 0; i < plotPoints; i++) {
        double theta = thetaMin + i * thetaStep;
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);

        for (int j = 0; j < plotPoints; j++) {
          double phi = phiMin + j * phiStep;
          try {
            IExpr subst =
                F.subst(rExpr, F.List(F.Rule(thetaVar, F.num(theta)), F.Rule(phiVar, F.num(phi))));
            IExpr rValExpr = engine.evaluate(subst);
            double r = rValExpr.evalf();

            double x = r * sinTheta * Math.cos(phi);
            double y = r * sinTheta * Math.sin(phi);
            double z = r * cosTheta;

            allPoints.append(F.List(F.num(x), F.num(y), F.num(z)));
            globalPointIndex++;
          } catch (ArgumentTypeException ate) {
            // don't append if exception occurs
          }
        }
      }

      // --- Generate Faces (Optimized Multi-Polygon) ---
      int numFaces = (plotPoints - 1) * (plotPoints - 1);
      IASTAppendable faceList = F.ListAlloc(numFaces);

      for (int i = 0; i < plotPoints - 1; i++) {
        for (int j = 0; j < plotPoints - 1; j++) {
          int p1 = startPointIndex + (i * plotPoints) + j + 1;
          int p2 = p1 + 1;
          int p3 = p2 + plotPoints;
          int p4 = p1 + plotPoints;

          faceList.append(F.List(F.ZZ(p1), F.ZZ(p2), F.ZZ(p3), F.ZZ(p4)));
        }
      }


      IExpr currentStyle;
      if (explicitStyles.size() > 1) {
        // Explicit list {Red, Green...}
        int styleIdx = (k - 1) % (explicitStyles.size() - 1) + 1;
        currentStyle = explicitStyles.get(styleIdx);
      } else if (plotStyle.isAST() && !plotStyle.isList()) {
        // Explicit single style
        currentStyle = plotStyle;
      } else {
        // Automatic: Cycle default colors
        int colorIdx = GraphicsOptions.incColorIndex(k - 1);
        currentStyle = GraphicsOptions.plotStyleColorExpr(colorIdx, F.NIL);
      }

      // Create Group {Style, Polygon[Faces]}
      IASTAppendable group = F.ListAlloc(2);
      group.append(currentStyle);
      group.append(F.Polygon(faceList));

      primitives.append(group);
    }

    // Assemble Result
    IExpr graphicsComplex = F.GraphicsComplex(allPoints, primitives);

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);
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
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.PlotStyle, S.BoxRatios},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic});
  }
}
