package org.matheclipse.core.builtin.graphics3d;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
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

/**
 * Implementation of ComplexPlot3D that generates a GraphicsComplex AST. * Maps: - Height (z) ->
 * Magnitude |f(z)| - Color -> Argument Arg(f(z))
 */
public class ComplexPlot3D extends AbstractFunctionOptionEvaluator {

  public ComplexPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    IExpr function = ast.arg1();
    IExpr rangeSpec = ast.arg2();

    // 1. Parse Range {z, min, max} or {z, max} (implied min -max-max I)
    ISymbol zVar = null;
    double minRe = -2.0;
    double maxRe = 2.0;
    double minIm = -2.0;
    double maxIm = 2.0;

    if (rangeSpec.isList()) {
      IAST range = (IAST) rangeSpec;
      if (range.size() > 1 && range.arg1().isSymbol()) {
        zVar = (ISymbol) range.arg1();

        if (range.size() == 3) {
          // {z, min, max} - strictly usually complex numbers
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

    // 2. Options
    int plotPoints = 50; // Resolution
    if (options[0].isInteger()) {
      int optPoints = options[0].toIntDefault();
      if (optPoints > 10)
        plotPoints = optPoints;
    }

    // Safety clamp for infinite poles
    double maxZValue = 100.0;

    // 3. Data Generation
    int rows = plotPoints;
    int cols = plotPoints;

    double dRe = (maxRe - minRe) / (cols - 1);
    double dIm = (maxIm - minIm) / (rows - 1);

    // Arrays to store intermediate values for Normal calculation
    Vector3D[][] points3D = new Vector3D[rows][cols];
    IExpr[][] colorData = new IExpr[rows][cols];

    // Pass 1: Evaluate Function & Coordinates
    for (int i = 0; i < rows; i++) {
      double im = minIm + i * dIm;
      for (int j = 0; j < cols; j++) {
        double re = minRe + j * dRe;

        IExpr zVal = F.complexNum(re, im);
        // Eval function with z = re + I*im
        IExpr fz = engine.evalN(F.subst(function, F.Rule(zVar, zVal)));

        double height = 0.0;
        double arg = 0.0;

        if (fz instanceof INumber) {
          IComplexNum cn = toComplex(fz);
          height = cn.dabs();
          arg = cn.complexArg().evalf(); // -Pi to Pi
        }

        // Handle poles
        if (Double.isInfinite(height) || Double.isNaN(height) || height > maxZValue) {
          height = maxZValue;
        }

        points3D[i][j] = new Vector3D(re, im, height);

        // Color Mapping: Hue determined by Argument
        // Normalize arg (-Pi, Pi) -> (0, 1)
        double hue = (arg + Math.PI) / (2 * Math.PI);
        // Standard Mathematica coloring often varies Saturation/Brightness based on magnitude too,
        // but Hue is the primary indicator.
        colorData[i][j] = F.Hue(F.num(hue), F.num(0.6), F.num(1.0));
      }
    }

    // 4. Construct AST Lists
    IASTAppendable pointsList = F.ListAlloc(rows * cols);
    IASTAppendable normalsList = F.ListAlloc(rows * cols);
    IASTAppendable colorsList = F.ListAlloc(rows * cols);
    IASTAppendable polygons = F.ListAlloc((rows - 1) * (cols - 1));

    // Flatten data and calculate normals
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Vector3D p = points3D[i][j];
        pointsList.append(F.List(F.num(p.getX()), F.num(p.getY()), F.num(p.getZ())));
        colorsList.append(colorData[i][j]);

        // Calculate Normal
        // Use central difference where possible, forward/backward at edges
        int iPrev = (i > 0) ? i - 1 : i;
        int iNext = (i < rows - 1) ? i + 1 : i;
        int jPrev = (j > 0) ? j - 1 : j;
        int jNext = (j < cols - 1) ? j + 1 : j;

        Vector3D u = points3D[i][jNext].subtract(points3D[i][jPrev]); // Tangent along Real axis
        Vector3D v = points3D[iNext][j].subtract(points3D[iPrev][j]); // Tangent along Imag axis

        // Normal is cross product of tangents
        Vector3D n = u.crossProduct(v).normalize();
        // If function is analytic, z should point generally up, ensure n.z > 0
        if (n.getZ() < 0)
          n = n.negate();

        normalsList.append(F.List(F.num(n.getX()), F.num(n.getY()), F.num(n.getZ())));
        }
    }

    // 5. Generate Indices (Quads)
    for (int i = 0; i < rows - 1; i++) {
      for (int j = 0; j < cols - 1; j++) {
        // 1-based indexing for Mathematica GraphicsComplex
        int p1 = (i * cols) + j + 1;
        int p2 = p1 + 1;
        int p3 = p2 + cols;
        int p4 = p1 + cols;

        polygons.append(F.Polygon(F.List(F.ZZ(p1), F.ZZ(p2), F.ZZ(p3), F.ZZ(p4))));
        }
    }

    // 6. Final Structure
    // Graphics3D[GraphicsComplex[pts, polygons, VertexColors->..., VertexNormals->...], opts]
    IExpr graphicsComplex = F.GraphicsComplex(pointsList, polygons,
        F.Rule(S.VertexColors, colorsList), F.Rule(S.VertexNormals, normalsList));

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);

    // Add default visual options
    result.append(F.Rule(S.PlotRange, S.Automatic));
    result.append(F.Rule(S.BoxRatios, F.List(F.num(1), F.num(1), F.num(0.4)))); // flattened box
                                                                                // usually looks
                                                                                // better
    result.append(F.Rule(S.Axes, S.True));
    result.append(F.Rule(S.Lighting, F.stringx("Neutral")));

    // Append user options
    if (ast.argSize() > 2) {
      for (int i = 3; i < ast.size(); i++) {
        if (ast.get(i).isRuleAST()) {
          result.append(ast.get(i));
        }
      }
    }
    // System.out.println(result);
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
    // Try evaluating again if it's not a number yet
    try {
      if (expr.isNumber()) { // symbolic number
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
            S.PlotLegends},
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.None});
  }
}