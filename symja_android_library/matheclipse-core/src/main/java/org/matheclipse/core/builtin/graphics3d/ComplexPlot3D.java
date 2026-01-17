package org.matheclipse.core.builtin.graphics3d;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class ComplexPlot3D extends AbstractFunctionOptionEvaluator {

  public ComplexPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    IExpr function = ast.arg1();
    IExpr rangeSpec = ast.arg2();

    int plotPoints = 50;
    int plotPointsOptions = options[0].toIntDefault();
    if (plotPointsOptions > 10)
      plotPoints = plotPointsOptions;

    // Default Clipping
    double zMinClip = 0.0;
    double zMaxClip = 3.0;

    // Parse PlotRange
    IExpr plotRangeOpt = options[1];
    if (plotRangeOpt.isNumber()) {
      zMaxClip = plotRangeOpt.evalDouble();
    } else if (plotRangeOpt.isList()) {
      IAST prList = (IAST) plotRangeOpt;
      if (prList.size() == 3 && prList.get(1).isNumber() && prList.get(2).isNumber()) {
        zMinClip = prList.get(1).evalDouble();
        zMaxClip = prList.get(2).evalDouble();
      } else if (prList.size() == 4) {
        IExpr zRange = prList.get(3);
        if (zRange.isList() && ((IAST) zRange).size() == 3) {
          zMinClip = ((IAST) zRange).get(1).evalDouble();
          zMaxClip = ((IAST) zRange).get(2).evalDouble();
        } else if (zRange.isNumber()) {
          zMaxClip = zRange.evalDouble();
        }
      }
    } else if (plotRangeOpt.equals(S.All) || plotRangeOpt.equals(S.Full)) {
      zMaxClip = 20.0;
    }

    // Check PlotLegends
    boolean showLegend = false;
    if (options.length > 4) {
      showLegend = options[4].equals(S.Automatic);
    }

    ISymbol variable = null;
    double minRe = -2.0, maxRe = 2.0;
    double minIm = -2.0, maxIm = 2.0;

    if (rangeSpec.isList()) {
      IAST list = (IAST) rangeSpec;
      if (list.argSize() >= 1 && list.arg1().isSymbol()) {
        variable = (ISymbol) list.arg1();
        if (list.argSize() == 2) {
          double n = list.arg2().evalDouble();
          minRe = -n;
          maxRe = n;
          minIm = -n;
          maxIm = n;
        } else if (list.argSize() == 3) {
          IComplexNum zMin = toComplex(list.arg2());
          IComplexNum zMax = toComplex(list.arg3());
          if (zMin != null && zMax != null) {
            minRe = zMin.reDoubleValue();
            minIm = zMin.imDoubleValue();
            maxRe = zMax.reDoubleValue();
            maxIm = zMax.imDoubleValue();
          } else
            return F.NIL;
        }
      }
    }

    if (variable == null)
      return F.NIL;

    return createGraphicsComplex(ast, engine, function, plotPoints, zMinClip, zMaxClip, variable,
        minRe, maxRe, minIm, maxIm, showLegend);
  }

  private IExpr createGraphicsComplex(IAST ast, final EvalEngine engine, IExpr function,
      int plotPoints, double zMinClip, double zMaxClip, ISymbol variable, double minRe,
      double maxRe, double minIm, double maxIm, boolean showLegend) {

    int numVertices = plotPoints * plotPoints;
    IASTAppendable points = F.ListAlloc(numVertices);
    IASTAppendable colors = F.ListAlloc(numVertices);

    double dx = (maxRe - minRe) / (plotPoints - 1);
    double dy = (maxIm - minIm) / (plotPoints - 1);

    for (int i = 0; i < plotPoints; i++) {
      double y = minIm + i * dy;
      for (int j = 0; j < plotPoints; j++) {
        double x = minRe + j * dx;
        IComplexNum zVal = F.complexNum(x, y);

        IExpr substExpr = F.subst(function, F.List(F.Rule(variable, zVal)));
        IExpr result = engine.evaluate(substExpr);

        double abs = 0.0;
        double arg = 0.0;

        try {
          if (result instanceof INumber) {
            INumber num = (INumber) result;
            abs = num.abs().evalfc().getReal();
            arg = num.complexArg().evalfc().getArgument();
          } else {
            Complex c = result.evalfc();
            abs = c.abs().getReal();
            arg = c.getArgument();
          }
        } catch (RuntimeException e) {
          abs = Double.NaN;
        }

        double finalZ = abs;
        IExpr finalColor;

        if (Double.isNaN(abs) || Double.isInfinite(abs) || abs > zMaxClip) {
          finalZ = zMaxClip;
          finalColor = S.White;
        } else if (abs < zMinClip) {
          finalZ = zMinClip;
          finalColor = S.White;
        } else {
          finalZ = abs;
          // Map phase (-pi to pi) to Hue (0 to 1)
          double hue = (arg + Math.PI) / (2.0 * Math.PI);
          if (hue < 0.0)
            hue = 0.0;
          if (hue > 1.0)
            hue = 1.0;

          finalColor = F.Hue(F.num(hue), F.num(1.0), F.num(1.0));
        }

        points.append(F.List(F.num(x), F.num(y), F.num(finalZ)));
        colors.append(finalColor);
      }
    }

    // Optimization: Create a single Polygon containing list of faces (Multi-Polygon)
    IASTAppendable faceList = F.ListAlloc((plotPoints - 1) * (plotPoints - 1));
    for (int i = 0; i < plotPoints - 1; i++) {
      for (int j = 0; j < plotPoints - 1; j++) {
        int p1 = (i * plotPoints) + j + 1;
        int p2 = p1 + 1;
        int p3 = p2 + plotPoints;
        int p4 = p1 + plotPoints;
        faceList.append(F.List(F.ZZ(p1), F.ZZ(p2), F.ZZ(p3), F.ZZ(p4)));
      }
    }

    IExpr graphicsComplex =
        F.GraphicsComplex(points, F.List(F.Polygon(faceList)), F.Rule(S.VertexColors, colors));

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);

    result.append(F.Rule(S.PlotRange, S.Automatic));
    if (ast.argSize() > 2) {
      for (int i = 3; i < ast.size(); i++) {
        if (ast.get(i).isRuleAST()) {
          result.append(ast.get(i));
        }
      }
    }

    // Wrap in Legended if requested
    if (showLegend) {
      return F.Legended(result, S.Automatic);
    }

    return result;
  }

  private IComplexNum toComplex(IExpr expr) {
    if (expr instanceof INumber) {
      if (expr instanceof IComplexNum)
        return (IComplexNum) expr;
      return F.complexNum(((INumber) expr).reDoubleValue(), 0.0);
    }
    try {
      Complex val = expr.evalfc();
      return F.complexNum(val);
    } catch (Exception e) {
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.DataRange,
            S.PlotLegends},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.None});
  }
}