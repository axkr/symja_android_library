package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class RevolutionPlot3D extends AbstractFunctionOptionEvaluator {

  private static final IAST DEFAULT_COLORS = F.List(F.RGBColor(1.0, 0.8, 0.4),
      F.RGBColor(0.4, 0.6, 1.0), F.RGBColor(0.6, 0.8, 0.4), F.RGBColor(1.0, 0.4, 0.4));

  public RevolutionPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2)
      return F.NIL;

    IExpr funcExpr = ast.arg1();
    IExpr tRange = ast.arg2();
    IExpr thetaRange = (argSize > 2 && ast.arg3().isList()) ? ast.arg3() : null;

    int plotPoints = 40;
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    }

    IExpr plotStyle = options[3];

    if (!tRange.isList() || ((IAST) tRange).argSize() < 2) {
      return F.NIL;
    }
    IExpr tVar = ((IAST) tRange).arg1();
    double tMin = 0.0, tMax = 1.0;
    try {
      if (tRange.isList3()) {
        tMin = ((IAST) tRange).arg2().evalf();
        tMax = ((IAST) tRange).arg3().evalf();
      } else {
        tMax = ((IAST) tRange).arg2().evalf();
      }
    } catch (ArgumentTypeException e) {
      return F.NIL;
    }

    double thetaMin = 0.0, thetaMax = 2.0 * Math.PI;
    if (thetaRange != null) {
      // ... parse theta range ...
    }

    IAST functionsList = funcExpr.isList() ? (IAST) funcExpr : F.List(funcExpr);
    // Simple logic: if argument is a list {f, g}, assume parametric {x(t), z(t)}
    boolean isParametric = functionsList.arg1().isList();

    IASTAppendable allPoints = F.ListAlloc();
    IASTAppendable primitives = F.ListAlloc();

    double tStep = (tMax - tMin) / (plotPoints - 1);
    double thetaStep = (thetaMax - thetaMin) / (plotPoints - 1);
    int globalPointIndex = 0;

    for (int k = 1; k < functionsList.size(); k++) {
      IExpr func = functionsList.get(k);
      int startPointIndex = globalPointIndex;

      for (int i = 0; i < plotPoints; i++) {
        double t = tMin + i * tStep;
        double rVal, zVal;

        // Robust Evaluation
        IExpr subst = F.subst(func, F.List(F.Rule(tVar, F.num(t))));
        IExpr val = engine.evaluate(subst); // Evaluate symbolic first
        if (!val.isNumber() && !val.isList()) {
          val = engine.evalN(val); // Try N if not simplified
        }

        try {
          if (isParametric) {
            if (val.isList() && ((IAST) val).size() >= 3) {
              rVal = ((IAST) val).arg1().evalDouble();
              zVal = ((IAST) val).arg2().evalDouble();
            } else {
              rVal = 0;
              zVal = 0;
            }
          } else {
            zVal = val.evalf();
            rVal = t; // Default r=t for function plot
          }
        } catch (ArgumentTypeException e) {
          // Evaluation failed (e.g. division by zero), handle gracefully
          zVal = 0.0;
          rVal = t;
        }

        for (int j = 0; j < plotPoints; j++) {
          double theta = thetaMin + j * thetaStep;
          allPoints.append(
              F.List(F.num(rVal * Math.cos(theta)), F.num(rVal * Math.sin(theta)), F.num(zVal)));
          globalPointIndex++;
        }
      }

      // Build Polygons for Grid
      IASTAppendable faceList = F.ListAlloc((plotPoints - 1) * (plotPoints - 1));
      for (int i = 0; i < plotPoints - 1; i++) {
        for (int j = 0; j < plotPoints - 1; j++) {
          int p1 = startPointIndex + (i * plotPoints) + j + 1;
          int p2 = p1 + 1;
          int p3 = p2 + plotPoints;
          int p4 = p1 + plotPoints;
          faceList.append(F.List(F.ZZ(p1), F.ZZ(p2), F.ZZ(p3), F.ZZ(p4)));
        }
      }

      // Add to Primitives
      IASTAppendable group = F.ListAlloc(2);
      // Style logic ...
      group.append(DEFAULT_COLORS.get(1)); // simplified
      group.append(F.Polygon(faceList));
      primitives.append(group);
    }

    IExpr graphicsComplex = F.GraphicsComplex(allPoints, primitives);
    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);
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
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.PlotStyle, S.BoxRatios},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic});
  }
}
