package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@code SphericalPlot3D[r, {theta, tmin, tmax}, {phi, pmin, pmax}]} - the surface whose distance
 * from the origin in the direction {@code (theta, phi)} is {@code r}.
 *
 * <p>
 * {@code theta} is measured from the positive z axis and {@code phi} is the azimuth measured from
 * the positive x axis.
 */
public class SphericalPlot3D extends AbstractFunctionOptionEvaluator {

  public SphericalPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // a display wrapper comes off before the argument's shape is read, so a labelled dataset is
    // still recognised as a dataset; Plot3DTools.graphics3D puts the label back on the finished
    // primitives, reading it from the original call
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
    if (argSize < 3) {
      return F.NIL;
    }
    double[] theta = range(ast.arg2(), S.SphericalPlot3D, engine);
    double[] phi = range(ast.arg3(), S.SphericalPlot3D, engine);
    if (theta == null || phi == null) {
      return F.NIL;
    }
    ISymbol thetaVar = (ISymbol) ((IAST) ast.arg2()).arg1();
    ISymbol phiVar = (ISymbol) ((IAST) ast.arg3()).arg1();

    IAST functions = ast.arg1().isList() ? (IAST) ast.arg1() : F.List(ast.arg1());
    int[] samples = Plot3DTools.plotPoints(options[Plot3DTools.X_PLOT_POINTS], 40);
    PlotColorFunction.Builder colorBuilder = Plot3DTools
        .plotColors(PlotColorFunction.Family.SPHERICAL_3D, options, S.SphericalPlot3D, engine);

    IASTAppendable graphicsList = F.ListAlloc(functions.argSize());
    for (int k = 1; k <= functions.argSize(); k++) {
      IExpr surface = buildSurface(functions.get(k), k - 1, thetaVar, theta, phiVar, phi, samples,
          options, colorBuilder, engine);
      if (surface.isPresent()) {
        graphicsList.append(surface);
      }
    }
    if (graphicsList.argSize() == 0) {
      return F.NIL;
    }
    return Plot3DTools.graphics3D(graphicsList, originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
            F.Rule(S.BoxRatios, F.List(F.C1, F.C1, F.C1)), F.Rule(S.Axes, S.True),
            F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
  }

  /**
   * The {@code {min, max}} of an iterator, or {@code null} when it is not one.
   *
   * <p>
   * The shape is checked before anything is cast. The previous version cast the first element to a
   * symbol and read the third element on the strength of an {@code isList} test alone, so
   * {@code SphericalPlot3D[1, {t, Pi}, {p, 2 Pi}]} threw out of the evaluator.
   */
  private static double[] range(IExpr expr, ISymbol caller, EvalEngine engine) {
    if (!expr.isList() || ((IAST) expr).argSize() < 2 || !((IAST) expr).arg1().isSymbol()) {
      Errors.printMessage(caller, "pllim", F.list(expr), engine);
      return null;
    }
    IAST list = (IAST) expr;
    double min;
    double max;
    if (list.argSize() == 2) {
      min = 0.0;
      max = list.arg2().evalfNaN();
    } else {
      min = list.arg2().evalfNaN();
      max = list.arg3().evalfNaN();
    }
    if (!Double.isFinite(min) || !Double.isFinite(max) || max <= min) {
      Errors.printMessage(caller, "pllim", F.list(expr), engine);
      return null;
    }
    return new double[] {min, max};
  }

  private static IExpr buildSurface(IExpr radius, int index, ISymbol thetaVar, double[] theta,
      ISymbol phiVar, double[] phi, int[] samples, IExpr[] options,
      PlotColorFunction.Builder colorBuilder,
      EvalEngine engine) {
    int nTheta = samples[0];
    int nPhi = samples[1];
    double thetaStep = (theta[1] - theta[0]) / (nTheta - 1);
    double phiStep = (phi[1] - phi[0]) / (nPhi - 1);

    RegionFunctionFilter region =
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);
    // the same points, region or no region, so that a cell the boundary crosses is still a cell
    double[][][] unmasked = region == null ? null : new double[nTheta][nPhi][];
    boolean[][] insideGrid = region == null ? null : new boolean[nTheta][nPhi];
    double[][][] grid = new double[nTheta][nPhi][];
    double[][] radii = new double[nTheta][nPhi];
    double rMin = Double.MAX_VALUE;
    double rMax = -Double.MAX_VALUE;
    boolean any = false;

    for (int i = 0; i < nTheta; i++) {
      double t = theta[0] + i * thetaStep;
      double sinT = Math.sin(t);
      double cosT = Math.cos(t);
      for (int j = 0; j < nPhi; j++) {
        double p = phi[0] + j * phiStep;
        double r = Double.NaN;
        try {
          IExpr value = engine.evaluate(
              F.subst(radius, F.List(F.Rule(thetaVar, F.num(t)), F.Rule(phiVar, F.num(p)))));
          r = value.evalfNaN();
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
        if (!Double.isFinite(r)) {
          continue;
        }
        double px = r * sinT * Math.cos(p);
        double py = r * sinT * Math.sin(p);
        double pz = r * cosT;
        if (region != null) {
          unmasked[i][j] = new double[] {px, py, pz};
          insideGrid[i][j] = region.accepts(px, py, pz, t, p, r);
          if (!insideGrid[i][j]) {
            // the cells that touch it are cut along the edge of the region instead of drawn
            continue;
          }
        }
        radii[i][j] = r;
        rMin = Math.min(rMin, r);
        rMax = Math.max(rMax, r);
        grid[i][j] = new double[] {px, py, pz};
        any = true;
      }
    }
    if (!any) {
      return F.NIL;
    }

    double[] box = Plot3DTools.extentOf(grid);
    PlotColorFunction colorMap = colorBuilder
        .ranges(box[0], box[1], box[2], box[3], box[4], box[5], theta[0], theta[1], phi[0], phi[1],
            rMin, rMax)
        .build();
    IExpr[][] colors = null;
    if (colorMap != null) {
      colors = new IExpr[nTheta][nPhi];
      for (int i = 0; i < nTheta; i++) {
        for (int j = 0; j < nPhi; j++) {
          if (grid[i][j] == null) {
            continue;
          }
          double[] point = grid[i][j];
          // the whole tuple: where the point is, the two angles that put it there, and the radius
          colors[i][j] = colorMap.color(point[0], point[1], point[2], theta[0] + i * thetaStep,
              phi[0] + j * phiStep, radii[i][j]);
        }
      }
    }

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, colors != null);
    Plot3DTools.applyStyle(builder,
        Plot3DTools.surfaceStyle(index, options[Plot3DTools.X_PLOT_STYLE]),
        options[Plot3DTools.X_MESH]);
    // a full azimuth closes the surface, so the seam at phi = 2 Pi is welded to the one at 0
    boolean wrapPhi = Math.abs((phi[1] - phi[0]) - 2 * Math.PI) < 1e-9;
    // the boundary is placed by halving the angles rather than the coordinates, so that every
    // trial point stays on the surface
    Plot3DTools.RegionEdge edge = region == null ? null
        : Plot3DTools.parameterEdge(new Plot3DTools.SurfaceSampler() {
          @Override
          public double[] point(double t, double p) {
            double value = radiusAt(radius, thetaVar, t, phiVar, p, engine);
            if (!Double.isFinite(value)) {
              return null;
            }
            return new double[] {value * Math.sin(t) * Math.cos(p),
                value * Math.sin(t) * Math.sin(p), value * Math.cos(t)};
          }

          @Override
          public boolean inside(double[] point, double t, double p) {
            double value = Math.sqrt(
                point[0] * point[0] + point[1] * point[1] + point[2] * point[2]);
            return region.accepts(point[0], point[1], point[2], t, p, value);
          }
        }, theta[0], thetaStep, phi[0], phiStep);
    Plot3DTools.addSurface(builder, grid, false, wrapPhi, colors, true, options[Plot3DTools.X_MESH],
        options[Plot3DTools.X_MESH_STYLE], unmasked, insideGrid, edge);
    // the rim of the surface, and the rim of every hole a RegionFunction cut in it
    return Plot3DTools.withBoundary(builder.build(), grid,
        options[Plot3DTools.X_BOUNDARY_STYLE]);
  }

  /** The radius in one direction, or {@code NaN} where the function has no value there. */
  private static double radiusAt(IExpr radius, ISymbol thetaVar, double t, ISymbol phiVar, double p,
      EvalEngine engine) {
    try {
      return engine
          .evaluate(F.subst(radius, F.List(F.Rule(thetaVar, F.num(t)), F.Rule(phiVar, F.num(p)))))
          .evalfNaN();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Double.NaN;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // options are stripped before this is checked, but a plot that declares options must still
    // allow the arguments they occupy, or every option is an argument count error
    return ARGS_3_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.surfacePlot();
    setOptions(newSymbol, options.keys(), options.values());
  }
}
