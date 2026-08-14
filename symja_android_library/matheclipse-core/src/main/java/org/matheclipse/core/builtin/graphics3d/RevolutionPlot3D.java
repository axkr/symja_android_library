package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@code RevolutionPlot3D[f, {t, tmin, tmax}]} - the surface swept out by rotating a curve about an
 * axis.
 *
 * <p>
 * The curve may be given as a height {@code fz}, as a pair {@code {fx, fz}} in the plane that is
 * being rotated, or as a full space curve {@code {fx, fy, fz}}.
 */
public class RevolutionPlot3D extends AbstractFunctionOptionEvaluator {

  public RevolutionPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2 || !ast.arg2().isList()) {
      return F.NIL;
    }
    double[] t = iterator(ast.arg2(), 0.0, 1.0, engine);
    if (t == null) {
      return F.NIL;
    }
    IExpr tVar = ((IAST) ast.arg2()).arg1();

    double[] theta = {0.0, 2.0 * Math.PI};
    if (argSize > 2 && ast.arg3().isList()) {
      double[] parsed = iterator(ast.arg3(), 0.0, 2.0 * Math.PI, engine);
      if (parsed == null) {
        return F.NIL;
      }
      theta = parsed;
    }

    double[] axis = revolutionAxis(options[Plot3DTools.X_REVOLUTION_AXIS]);

    List<IExpr> functions = new ArrayList<>();
    IExpr funcExpr = ast.arg1();
    if (funcExpr.isList()) {
      IAST listArg = (IAST) funcExpr;
      if (listArg.argSize() > 0 && listArg.arg1().isList()) {
        for (int i = 1; i <= listArg.argSize(); i++) {
          functions.add(listArg.get(i));
        }
      } else {
        functions.add(listArg);
      }
    } else {
      functions.add(funcExpr);
    }

    int[] samples = Plot3DTools.plotPoints(options[Plot3DTools.X_PLOT_POINTS], 40);
    Plot3DTools.ColorMap colorMap = Plot3DTools.colorMap(options[Plot3DTools.X_COLOR_FUNCTION],
        options[Plot3DTools.X_COLOR_FUNCTION_SCALING], engine);

    IASTAppendable graphicsList = F.ListAlloc(functions.size());
    for (int k = 0; k < functions.size(); k++) {
      IExpr surface = buildSurface(functions.get(k), k, tVar, t, theta, axis, samples, options,
          colorMap, engine);
      if (surface.isPresent()) {
        graphicsList.append(surface);
      }
    }
    if (graphicsList.argSize() == 0) {
      return F.NIL;
    }
    return Plot3DTools.graphics3D(graphicsList, originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
            F.Rule(S.BoxRatios, options[Plot3DTools.X_BOX_RATIOS]), F.Rule(S.Axes, S.True),
            F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
  }

  private static double[] iterator(IExpr expr, double defaultMin, double defaultMax,
      EvalEngine engine) {
    IAST list = (IAST) expr;
    if (list.argSize() < 2) {
      return null;
    }
    double min = defaultMin;
    double max;
    if (list.argSize() >= 3) {
      min = list.arg2().evalfNaN();
      max = list.arg3().evalfNaN();
    } else {
      max = list.arg2().evalfNaN();
    }
    if (!Double.isFinite(min) || !Double.isFinite(max) || max <= min) {
      return null;
    }
    return new double[] {min, max};
  }

  /** {@code RevolutionAxis}, normalised; the default is the z axis. */
  private static double[] revolutionAxis(IExpr option) {
    double[] axis = {0, 0, 1};
    if (option != null && option.isList() && ((IAST) option).argSize() >= 3) {
      IAST list = (IAST) option;
      double x = list.arg1().evalfNaN();
      double y = list.arg2().evalfNaN();
      double z = list.arg3().evalfNaN();
      if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length > 0) {
          axis = new double[] {x / length, y / length, z / length};
        }
      }
    }
    return axis;
  }

  private static IExpr buildSurface(IExpr func, int index, IExpr tVar, double[] t, double[] theta,
      double[] axis, int[] samples, IExpr[] options, Plot3DTools.ColorMap colorMap,
      EvalEngine engine) {
    int nT = samples[0];
    int nTheta = samples[1];
    double tStep = (t[1] - t[0]) / (nT - 1);
    double thetaStep = (theta[1] - theta[0]) / (nTheta - 1);

    // an orthonormal frame in the plane the curve is rotated through
    double[] u = perpendicular(axis);
    double[] w = cross(axis, u);

    double[][][] grid = new double[nT][nTheta][];
    boolean any = false;
    double zMin = Double.MAX_VALUE;
    double zMax = -Double.MAX_VALUE;

    for (int i = 0; i < nT; i++) {
      double tValue = t[0] + i * tStep;
      double[] curve =
          evaluateCurve(func, tVar, tValue, engine, options[Plot3DTools.X_EVALUATION_MONITOR]);
      if (curve == null) {
        continue;
      }
      double radius = curve[0];
      double height = curve[1];
      for (int j = 0; j < nTheta; j++) {
        double angle = theta[0] + j * thetaStep;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double[] point = new double[3];
        for (int c = 0; c < 3; c++) {
          point[c] = radius * (cos * u[c] + sin * w[c]) + height * axis[c];
        }
        grid[i][j] = point;
        zMin = Math.min(zMin, point[2]);
        zMax = Math.max(zMax, point[2]);
        any = true;
      }
    }
    if (!any) {
      return F.NIL;
    }

    IExpr[][] colors = null;
    if (colorMap != null) {
      colors = new IExpr[nT][nTheta];
      for (int i = 0; i < nT; i++) {
        for (int j = 0; j < nTheta; j++) {
          double[] p = grid[i][j];
          if (p == null) {
            continue;
          }
          double z = colorMap.isScaled() && zMax > zMin ? (p[2] - zMin) / (zMax - zMin) : p[2];
          colors[i][j] = colorMap.apply(p[0], p[1], z);
        }
      }
    }

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, colors != null);
    Plot3DTools.applyStyle(builder,
        Plot3DTools.surfaceStyle(index, options[Plot3DTools.X_PLOT_STYLE]),
        options[Plot3DTools.X_MESH]);
    boolean wrapTheta = Math.abs((theta[1] - theta[0]) - 2 * Math.PI) < 1e-9;
    Plot3DTools.addSurface(builder, grid, false, wrapTheta, colors, true,
        options[Plot3DTools.X_MESH], options[Plot3DTools.X_MESH_STYLE]);
    return builder.build();
  }

  /**
   * The curve point as {@code {radius, height}} in the rotation plane.
   *
   * <p>
   * A bare {@code fz} is a height at radius {@code t}; a pair is {@code {radius, height}}; a triple
   * is a space curve, whose distance from the axis becomes the radius.
   */
  private static double[] evaluateCurve(IExpr func, IExpr tVar, double t, EvalEngine engine,
      IExpr monitor) {
    Plot3DTools.monitor(monitor, engine);
    try {
      IExpr value = engine.evaluate(F.subst(func, F.List(F.Rule(tVar, F.num(t)))));
      if (value.isList()) {
        IAST list = (IAST) value;
        if (list.argSize() == 2) {
          double r = list.arg1().evalfNaN();
          double z = list.arg2().evalfNaN();
          return Double.isFinite(r) && Double.isFinite(z) ? new double[] {r, z} : null;
        }
        if (list.argSize() >= 3) {
          double x = list.arg1().evalfNaN();
          double y = list.arg2().evalfNaN();
          double z = list.arg3().evalfNaN();
          if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
            return new double[] {Math.hypot(x, y), z};
          }
          return null;
        }
        return null;
      }
      double z = value.evalfNaN();
      return Double.isFinite(z) ? new double[] {t, z} : null;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
  }

  /** Any unit vector perpendicular to {@code axis}. */
  private static double[] perpendicular(double[] axis) {
    double[] candidate = Math.abs(axis[0]) < 0.9 ? new double[] {1, 0, 0} : new double[] {0, 1, 0};
    double[] p = cross(axis, candidate);
    double length = Math.sqrt(p[0] * p[0] + p[1] * p[1] + p[2] * p[2]);
    return new double[] {p[0] / length, p[1] / length, p[2] / length};
  }

  private static double[] cross(double[] a, double[] b) {
    return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.frameExtras(Plot3DTools
        .surfaceExtras(Plot3DTools.base3D()).add(F.List(F.C0, F.C0, F.C1), S.RevolutionAxis));
    setOptions(newSymbol, options.keys(), options.values());
  }
}
