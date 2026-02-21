package org.matheclipse.core.builtin.graphics3d;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of ComplexPlot3D that generates an optimized GraphicsComplex AST. Maps: - Height
 * (z) -> Magnitude |f(z)| - Color -> Argument Arg(f(z))
 */
public class ComplexPlot3D extends AbstractFunctionOptionEvaluator {

  public ComplexPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    IExpr function = ast.arg1();
    IExpr rangeSpec = ast.arg2();

    ISymbol zVar = null;
    double minRe = -2.0;
    double maxRe = 2.0;
    double minIm = -2.0;
    double maxIm = 2.0;

    if (rangeSpec.isList()) {
      IAST range = (IAST) rangeSpec;
      if (range.argSize() >= 1 && range.arg1().isSymbol()) {
        zVar = (ISymbol) range.arg1();

        if (range.argSize() == 3) {
          IComplexNum min = toComplex(engine.evalN(range.arg2()));
          IComplexNum max = toComplex(engine.evalN(range.arg3()));
          minRe = min.reDoubleValue();
          minIm = min.imDoubleValue();
          maxRe = max.reDoubleValue();
          maxIm = max.imDoubleValue();
        }
      }
    }

    if (zVar == null) {
      return F.NIL;
    }

    int plotPoints = 50;
    if (options[0].isInteger()) {
      int optPoints = options[0].toIntDefault();
      if (optPoints > 10) {
        plotPoints = optPoints;
      }
    }

    int rows = plotPoints;
    int cols = plotPoints;

    double dRe = (maxRe - minRe) / (cols - 1);
    double dIm = (maxIm - minIm) / (rows - 1);

    Vector3D[][] points3D = new Vector3D[rows][cols];
    IExpr[][] colorData = new IExpr[rows][cols];
    double[] zValues = new double[rows * cols];
    int zCount = 0;

    // Evaluate Function & Coordinates
    for (int i = 0; i < rows; i++) {
      double im = minIm + i * dIm;
      for (int j = 0; j < cols; j++) {
        double re = minRe + j * dRe;

        IExpr zVal = F.complexNum(re, im);
        IExpr fz = engine.evalN(F.subst(function, F.Rule(zVar, zVal)));

        double height = 0.0;
        double arg = 0.0;

        if (fz instanceof INumber) {
          IComplexNum cn = toComplex(fz);
          height = cn.dabs();
          arg = cn.complexArg().evalf();
        }

        if (Double.isFinite(height)) {
          zValues[zCount++] = height;
        }

        points3D[i][j] = new Vector3D(re, im, height);

        double hue = (arg + Math.PI) / (2 * Math.PI);
        colorData[i][j] = F.Hue(F.num(hue), F.num(0.6), F.num(1.0));
      }
    }

    // Determine max Z to clamp poles
    double maxZValue = 100.0;
    if (zCount > 0) {
      double[] validZ = new double[zCount];
      System.arraycopy(zValues, 0, validZ, 0, zCount);
      double[] range = GraphicsUtil.automaticPlotRange3D(validZ);
      if (range[1] > range[0]) {
        maxZValue = range[1];
      }
    }

    // Pass 1.5: Clamp Z in points3D
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Vector3D p = points3D[i][j];
        double h = p.getZ();
        if (Double.isInfinite(h) || Double.isNaN(h) || h > maxZValue) {
          points3D[i][j] = new Vector3D(p.getX(), p.getY(), maxZValue);
        }
      }
    }

    // Calculate Normals & Add to Builder
    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, true);

    IExpr meshOption = options[5];
    if (meshOption.isFalse() || meshOption.equals(S.None)) {
      IASTAppendable edgeForm = F.ast(S.EdgeForm);
      edgeForm.append(S.None);
      builder.setStyle(S.Orange, edgeForm);
    } else {
      builder.setStyle(S.Orange);
    }


    int[][] indices = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Vector3D p = points3D[i][j];

        int iPrev = (i > 0) ? i - 1 : i;
        int iNext = (i < rows - 1) ? i + 1 : i;
        int jPrev = (j > 0) ? j - 1 : j;
        int jNext = (j < cols - 1) ? j + 1 : j;

        Vector3D u = points3D[i][jNext].subtract(points3D[i][jPrev]);
        Vector3D v = points3D[iNext][j].subtract(points3D[iPrev][j]);

        Vector3D n;
        try {
          n = u.crossProduct(v).normalize();
          if (n.getZ() < 0) {
            n = n.negate();
          }
        } catch (Exception e) {
          n = new Vector3D(0, 0, 1);
        }

        double[] normalArr = new double[] {n.getX(), n.getY(), n.getZ()};

        indices[i][j] = builder.addVertex(p.getX(), p.getY(), p.getZ(), normalArr, colorData[i][j]);
      }
    }

    // Pass 3: Generate Quads
    for (int i = 0; i < rows - 1; i++) {
      for (int j = 0; j < cols - 1; j++) {
        int p1 = indices[i][j];
        int p2 = indices[i][j + 1];
        int p3 = indices[i + 1][j + 1];
        int p4 = indices[i + 1][j];

        if (p1 != -1 && p2 != -1 && p3 != -1 && p4 != -1) {
          builder.addPolygon(new int[] {p1, p2, p3, p4});
        }
      }
    }

    IExpr graphicsComplex = builder.build();
    if (graphicsComplex.equals(F.NIL)) {
      return F.NIL;
    }

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);

    result.append(F.Rule(S.PlotRange, S.Automatic));
    result.append(F.Rule(S.BoxRatios, F.List(F.num(1), F.num(1), F.num(0.4))));
    result.append(F.Rule(S.Axes, S.True));
    result.append(F.Rule(S.Lighting, F.stringx("Neutral")));

    if (ast.argSize() > 2) {
      for (int i = 3; i <= ast.argSize(); i++) {
        if (ast.get(i).isRuleAST()) {
          result.append(ast.get(i));
        }
      }
    }

    return result;
  }

  /**
   * Helper to convert various numeric types to IComplexNum
   */
  private IComplexNum toComplex(IExpr expr) {
    if (expr instanceof INumber) {
      if (expr instanceof IComplexNum) {
        return (IComplexNum) expr;
      }
      return F.complexNum(((INumber) expr).reDoubleValue(), 0.0);
    }
    try {
      if (expr.isNumber()) {
        return F.complexNum(expr.evalfc());
      }
    } catch (Exception e) {
      // fall through
    }
    return F.complexNum(0.0, 0.0);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.DataRange,
            S.PlotLegends, S.Mesh},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.None, S.True});
  }
}