package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.BinaryNumerical;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionClip;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/** {@code Plot3D[f, {x, xmin, xmax}, {y, ymin, ymax}]} - a surface over a rectangular domain. */
public class Plot3D extends AbstractFunctionOptionEvaluator {

  private static final int X_PLOT_POINTS = Plot3DTools.X_PLOT_POINTS;
  private static final int X_PLOT_RANGE = Plot3DTools.X_PLOT_RANGE;
  private static final int X_COLOR_FUNCTION = Plot3DTools.X_COLOR_FUNCTION;
  private static final int X_PLOT_STYLE = Plot3DTools.X_PLOT_STYLE;
  private static final int X_BOX_RATIOS = Plot3DTools.X_BOX_RATIOS;
  private static final int X_MESH = Plot3DTools.X_MESH;
  private static final int X_COLOR_FUNCTION_SCALING = Plot3DTools.X_COLOR_FUNCTION_SCALING;

  public Plot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (ast.argSize() < 3 || !ast.arg2().isList() || !ast.arg3().isList()) {
      return F.NIL;
    }
    return createGraphicsComplex(ast, argSize, originalAST, options, engine);
  }

  private static IExpr createGraphicsComplex(IAST ast, int argSize, IAST originalAST,
      final IExpr[] options, final EvalEngine engine) {
    try {
      final IAST lst1 = (IAST) ast.arg2();
      final IAST lst2 = (IAST) ast.arg3();
      if (!lst1.isAST3() || !lst2.isAST3() || !lst1.arg1().isSymbol() || !lst2.arg1().isSymbol()) {
        return F.NIL;
      }

      // a quantity valued range is stripped to magnitudes; the variable stays a plain number
      IExpr rangeUnits = GraphicsOptions.optionValue(originalAST, S.TargetUnits, S.Automatic);
      IAST xRange = QuantityFunctions.quantityPlotRange(engine.evaluate(lst1.arg2()),
          engine.evaluate(lst1.arg3()), rangeUnits, engine);
      IAST yRange = QuantityFunctions.quantityPlotRange(engine.evaluate(lst2.arg2()),
          engine.evaluate(lst2.arg3()), rangeUnits, engine);
      final IExpr xMin = engine.evalN(xRange.isPresent() ? xRange.arg1() : lst1.arg2());
      final IExpr xMax = engine.evalN(xRange.isPresent() ? xRange.arg2() : lst1.arg3());
      final IExpr yMin = engine.evalN(yRange.isPresent() ? yRange.arg1() : lst2.arg2());
      final IExpr yMax = engine.evalN(yRange.isPresent() ? yRange.arg2() : lst2.arg3());
      if ((!(xMin instanceof INum)) || (!(xMax instanceof INum)) || (!(yMin instanceof INum))
          || (!(yMax instanceof INum))) {
        return F.NIL;
      }
      final double xMinD = ((INum) xMin).getRealPart();
      final double xMaxD = ((INum) xMax).getRealPart();
      final double yMinD = ((INum) yMin).getRealPart();
      final double yMaxD = ((INum) yMax).getRealPart();
      if (xMaxD <= xMinD || yMaxD <= yMinD) {
        return F.NIL;
      }

      final ISymbol xVar = (ISymbol) lst1.arg1();
      final ISymbol yVar = (ISymbol) lst2.arg1();
      final IExpr functions = ast.arg1().makeList();

      int[] samples = Plot3DTools.plotPoints(options[X_PLOT_POINTS], 40);
      final int nx = samples[0];
      final int ny = samples[1];
      Plot3DTools.ColorMap colorMap = Plot3DTools.colorMap(options[X_COLOR_FUNCTION],
          options[X_COLOR_FUNCTION_SCALING], engine);

      final IASTAppendable surfaces = F.ListAlloc(functions.argSize());
      final IExpr targetUnits = GraphicsOptions.optionValue(originalAST, S.TargetUnits, S.Automatic);
      final IAST samplePoint = F.List(F.Rule(xVar, F.num((xMinD + xMaxD) / 2.0)),
          F.Rule(yVar, F.num((yMinD + yMaxD) / 2.0)));
      for (int f = 1; f <= functions.argSize(); f++) {
        // a quantity valued function is plotted by its magnitude
        IExpr plotted = QuantityFunctions.quantityPlotFunction(((IAST) functions).get(f),
            samplePoint, targetUnits, engine);
        IExpr surface = buildSurface(plotted, f - 1, xVar, yVar, xMinD, xMaxD, yMinD,
            yMaxD, nx, ny, options, colorMap, engine);
        if (surface.isPresent()) {
          surfaces.append(surface);
        }
      }

      IExpr boxRatios =
          options[X_BOX_RATIOS].isList() ? options[X_BOX_RATIOS] : Plot3DTools.FLAT_BOX_RATIOS;
      return Plot3DTools.graphics3D(surfaces, originalAST, argSize,
          new IExpr[] {F.Rule(S.PlotRange, options[X_PLOT_RANGE]), F.Rule(S.BoxRatios, boxRatios),
              F.Rule(S.Axes, S.True), F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Plot3D, rex, engine);
    }
  }

  private static IExpr buildSurface(IExpr function, int index, ISymbol xVar, ISymbol yVar,
      double xMinD, double xMaxD, double yMinD, double yMaxD, int nx, int ny, final IExpr[] options,
      Plot3DTools.ColorMap colorMap, EvalEngine engine) {
    // nx, ny and the steps below are not final: MaxRecursion replaces the grid with a finer one
    double xStep = (xMaxD - xMinD) / (nx - 1);
    double yStep = (yMaxD - yMinD) / (ny - 1);
    final BinaryNumerical hbn = new BinaryNumerical(function, xVar, yVar, engine);

    double[][] z = sample(hbn, xMinD, xStep, yMinD, yStep, nx, ny, options, engine);

    // MaxRecursion refines the whole grid, once per level, and stops as soon as a refinement
    // stops telling us anything new. It only runs when the call asks for it: the sampling here
    // is already dense by default, so refining every plot would cost four times the samples per
    // level for a picture nobody asked to be finer.
    int maxRecursion = options[Plot3DTools.X_MAX_RECURSION].toIntDefault(0);
    for (int level = 0; level < maxRecursion; level++) {
      int fineNx = nx * 2 - 1;
      int fineNy = ny * 2 - 1;
      if ((long) fineNx * fineNy > MAX_REFINED_SAMPLES) {
        break;
      }
      double fineXStep = (xMaxD - xMinD) / (fineNx - 1);
      double fineYStep = (yMaxD - yMinD) / (fineNy - 1);
      double[][] fine =
          sample(hbn, xMinD, fineXStep, yMinD, fineYStep, fineNx, fineNy, options, engine);
      boolean converged = isResolved(z, fine, nx, ny);
      z = fine;
      nx = fineNx;
      ny = fineNy;
      xStep = fineXStep;
      yStep = fineYStep;
      if (converged) {
        break;
      }
    }

    // an Exclusions curve runs through the surface and takes the samples on it with it, the same
    // way a value that is not a number is left out
    applyExclusions(z, xMinD, xStep, yMinD, yStep, nx, ny, xVar, yVar,
        options[Plot3DTools.X_EXCLUSIONS], engine);

    // The RegionFunction is answered per sample but the values are kept: a cell the edge of the
    // region runs through is cut along that edge rather than dropped, and cutting it interpolates
    // across the cell, so it needs the height at every corner. What the region decides is which
    // samples are part of the picture, which is what the visible band is then measured over.
    RegionFunctionFilter region =
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);
    boolean[][] inside =
        applyRegionFunction(z, xMinD, xStep, yMinD, yStep, nx, ny, region, engine);

    int finiteCount = 0;
    for (int i = 0; i < nx; i++) {
      for (int j = 0; j < ny; j++) {
        if (inside[i][j]) {
          finiteCount++;
        }
      }
    }
    if (finiteCount == 0) {
      return F.NIL;
    }

    // a function with a pole would otherwise stretch the box over its whole range and flatten
    // everything else into the floor, so the visible band is narrowed to the body of the data
    double[] valid = new double[finiteCount];
    int v = 0;
    for (int i = 0; i < nx; i++) {
      for (int j = 0; j < ny; j++) {
        if (inside[i][j]) {
          valid[v++] = z[i][j];
        }
      }
    }
    double[] range = GraphicsUtil.automaticPlotRange3D(valid);
    double zMin = range[1] > range[0] ? range[0] : -1.0;
    double zMax = range[1] > range[0] ? range[1] : 1.0;

    boolean clipToNothing = options[Plot3DTools.X_CLIPPING_STYLE].isNone();
    double[][][] grid = new double[nx][ny][];
    // the same points, region or no region, so that a cell the boundary crosses is still a cell
    double[][][] unmasked = region == null ? null : new double[nx][ny][];
    IExpr[][] colors = colorMap == null ? null : new IExpr[nx][ny];
    for (int i = 0; i < nx; i++) {
      double x = xMinD + i * xStep;
      for (int j = 0; j < ny; j++) {
        double y = yMinD + j * yStep;
        double value = z[i][j];
        if (!Double.isFinite(value)) {
          // leave a hole where the function has no value rather than a wall at the top of the box
          continue;
        }
        // a sample beyond the visible band is pinned to it, which is what keeps a steep but finite
        // slope from escaping the box; a sample that is not finite at all is dropped above.
        // ClippingStyle -> None asks for what lies beyond the band to be left out instead, so the
        // surface is open where it leaves the box rather than capped flat against it
        if (clipToNothing && (value < zMin || value > zMax)) {
          continue;
        }
        double clamped = Math.max(zMin, Math.min(zMax, value));
        double[] point = new double[] {x, y, clamped};
        if (unmasked != null) {
          unmasked[i][j] = point;
        }
        if (inside[i][j]) {
          grid[i][j] = point;
        }
        if (colors != null) {
          double cz = colorMap.isScaled() ? scale(clamped, zMin, zMax) : clamped;
          double cx = colorMap.isScaled() ? scale(x, xMinD, xMaxD) : x;
          double cy = colorMap.isScaled() ? scale(y, yMinD, yMaxD) : y;
          colors[i][j] = colorMap.apply(cx, cy, cz);
        }
      }
    }

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, colors != null);
    Plot3DTools.applyStyle(builder, Plot3DTools.surfaceStyle(index, options[X_PLOT_STYLE]),
        options[X_MESH]);
    Plot3DTools.addSurface(builder, grid, false, false, colors, true, options[X_MESH],
        options[Plot3DTools.X_MESH_STYLE], unmasked, inside,
        regionEdge(unmasked, region, zMin, zMax));
    IExpr complex = builder.build();

    if (complex.isNIL()) {
      return complex;
    }

    // the lines are kept outside the GraphicsComplex so that they carry their own colour rather
    // than being shaded along with the surface they lie on
    IASTAppendable decorated = F.ListAlloc(6);
    decorated.append(complex);

    IAST meshLines = meshFunctionLines(grid, nx, ny, options[Plot3DTools.X_MESH_FUNCTIONS],
        options[X_MESH], engine);
    if (meshLines.argSize() > 0) {
      IExpr meshStyle = options[Plot3DTools.X_MESH_STYLE];
      decorated.append(meshStyle == S.Automatic ? S.Black : meshStyle);
      decorated.append(meshLines);
    }

    IExpr boundaryStyle = options[Plot3DTools.X_BOUNDARY_STYLE];
    if (Plot3DTools.drawsBoundary(boundaryStyle)) {
      IAST boundary = Plot3DTools.surfaceBoundary(grid);
      if (boundary.argSize() > 0) {
        decorated.append(boundaryStyle);
        decorated.append(boundary);
      }
    }
    return decorated.argSize() == 1 ? complex : decorated;
  }

  /** An upper bound on refinement, so a large MaxRecursion cannot build a mesh nothing renders. */
  private static final long MAX_REFINED_SAMPLES = 250_000L;

  /** How far a refined sample may sit from the estimate before the grid counts as unresolved. */
  private static final double FLATNESS_TOLERANCE = 0.01;

  private static double[][] sample(BinaryNumerical hbn, double xMin, double xStep, double yMin,
      double yStep, int nx, int ny, final IExpr[] options, EvalEngine engine) {
    double[][] z = new double[nx][ny];
    for (int i = 0; i < nx; i++) {
      double x = xMin + i * xStep;
      for (int j = 0; j < ny; j++) {
        Plot3DTools.monitor(options[Plot3DTools.X_EVALUATION_MONITOR], engine);
        z[i][j] = hbn.value(x, yMin + j * yStep);
      }
    }
    return z;
  }

  /**
   * Whether the finer grid agrees with what the coarse one predicted.
   *
   * <p>
   * Every point of the coarse grid is also a point of the finer one, so the points in between are
   * the new information. Each is compared with the straight line between the two coarse samples it
   * sits between: where the surface is flat the two agree, and where it bends they do not. When
   * every new point lands within the tolerance, another doubling would only repeat what is already
   * drawn.
   */
  private static boolean isResolved(double[][] coarse, double[][] fine, int nx, int ny) {
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;
    for (int i = 0; i < nx; i++) {
      for (int j = 0; j < ny; j++) {
        double value = coarse[i][j];
        if (Double.isFinite(value)) {
          min = Math.min(min, value);
          max = Math.max(max, value);
        }
      }
    }
    if (min > max) {
      return true; // nothing finite to resolve
    }
    double span = max > min ? max - min : 1.0;

    for (int i = 0; i < nx - 1; i++) {
      for (int j = 0; j < ny; j++) {
        double a = coarse[i][j];
        double b = coarse[i + 1][j];
        double actual = fine[2 * i + 1][2 * j];
        if (!Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(actual)) {
          // a hole appearing between two samples is exactly what refinement is for
          if (Double.isFinite(a) != Double.isFinite(actual)) {
            return false;
          }
          continue;
        }
        if (Math.abs(actual - (a + b) / 2.0) > FLATNESS_TOLERANCE * span) {
          return false;
        }
      }
    }
    for (int i = 0; i < nx; i++) {
      for (int j = 0; j < ny - 1; j++) {
        double a = coarse[i][j];
        double b = coarse[i][j + 1];
        double actual = fine[2 * i][2 * j + 1];
        if (!Double.isFinite(a) || !Double.isFinite(b) || !Double.isFinite(actual)) {
          if (Double.isFinite(a) != Double.isFinite(actual)) {
            return false;
          }
          continue;
        }
        if (Math.abs(actual - (a + b) / 2.0) > FLATNESS_TOLERANCE * span) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Punches out every sample the {@code RegionFunction} does not accept.
   *
   * <p>
   * The predicate is given the point as {@code region[x, y, z]} and anything but {@code True}
   * leaves a hole, which is the same thing that happens where the function has no value, so the
   * surface simply stops at the edge of the region.
   */
  private static boolean[][] applyRegionFunction(double[][] z, double xMin, double xStep,
      double yMin, double yStep, int nx, int ny, RegionFunctionFilter region, EvalEngine engine) {
    boolean[][] inside = new boolean[nx][ny];
    for (int i = 0; i < nx; i++) {
      double x = xMin + i * xStep;
      for (int j = 0; j < ny; j++) {
        inside[i][j] = Double.isFinite(z[i][j])
            && (region == null || region.accepts(x, yMin + j * yStep, z[i][j]));
      }
    }
    return inside;
  }

  /**
   * Where the edge of the region crosses a grid line, as a point on the surface.
   *
   * <p>
   * The predicate answers yes or no and nothing else, so the crossing is found by halving the grid
   * line rather than by interpolating a value. The height along it is interpolated between the two
   * samples: the boundary is already being followed to first order inside one cell, and evaluating
   * the plotted function again at every step of every bisection would cost more than the whole
   * sampling did.
   */
  private static Plot3DTools.RegionEdge regionEdge(double[][][] unmasked,
      RegionFunctionFilter region, double zMin, double zMax) {
    if (unmasked == null || region == null) {
      return null;
    }
    return (i1, j1, i2, j2) -> {
      double[] from = unmasked[i1][j1];
      double[] to = unmasked[i2][j2];
      if (from == null || to == null) {
        return null;
      }
      double[] at = RegionClip.crossing((px, py) -> {
        double t = Math.abs(to[0] - from[0]) > Math.abs(to[1] - from[1])
            ? (px - from[0]) / (to[0] - from[0])
            : (py - from[1]) / (to[1] - from[1]);
        return region.accepts(px, py, from[2] + t * (to[2] - from[2]));
      }, from[0], from[1], to[0], to[1]);
      double t = Math.abs(to[0] - from[0]) > Math.abs(to[1] - from[1])
          ? (at[0] - from[0]) / (to[0] - from[0])
          : (at[1] - from[1]) / (to[1] - from[1]);
      double height = Math.max(zMin, Math.min(zMax, from[2] + t * (to[2] - from[2])));
      return new double[] {at[0], at[1], height};
    };
  }

  /**
   * Cuts the surface along the curves {@code Exclusions} names.
   *
   * <p>
   * An exclusion is an equation in the plot variables, such as {@code x == 0}. The grid almost
   * never lands exactly on such a curve, so what is looked for is the crossing: where the two sides
   * of the equation swap over between one sample and the next, both samples are dropped and the
   * surface is left open along the curve rather than being stitched across it.
   *
   * <p>
   * {@code Automatic} means the curves are found by sampling alone, which is what dropping every
   * value that is not a number already does.
   */
  private static void applyExclusions(double[][] z, double xMin, double xStep, double yMin,
      double yStep, int nx, int ny, ISymbol xVar, ISymbol yVar, IExpr exclusions,
      EvalEngine engine) {
    if (exclusions.isNone() || exclusions == S.Automatic || !exclusions.isPresent()) {
      return;
    }
    IAST list = exclusions.isList() ? (IAST) exclusions : F.list(exclusions);
    for (int k = 1; k < list.size(); k++) {
      IExpr difference = asDifference(list.get(k));
      if (difference.isNIL()) {
        continue;
      }
      double[][] sign = new double[nx][ny];
      for (int i = 0; i < nx; i++) {
        double x = xMin + i * xStep;
        for (int j = 0; j < ny; j++) {
          double y = yMin + j * yStep;
          IExpr value = engine
              .evalN(F.subst(difference, F.List(F.Rule(xVar, F.num(x)), F.Rule(yVar, F.num(y)))));
          sign[i][j] = value.isNumber() ? value.evalfNaN() : Double.NaN;
        }
      }
      boolean[][] cut = new boolean[nx][ny];
      for (int i = 0; i < nx; i++) {
        for (int j = 0; j < ny; j++) {
          if (crosses(sign, i, j, i + 1, j, nx, ny) || crosses(sign, i, j, i, j + 1, nx, ny)) {
            cut[i][j] = true;
          }
        }
      }
      for (int i = 0; i < nx; i++) {
        for (int j = 0; j < ny; j++) {
          if (cut[i][j]) {
            z[i][j] = Double.NaN;
          }
        }
      }
    }
  }

  private static boolean crosses(double[][] sign, int i1, int j1, int i2, int j2, int nx, int ny) {
    if (i2 >= nx || j2 >= ny) {
      return false;
    }
    double a = sign[i1][j1];
    double b = sign[i2][j2];
    if (!Double.isFinite(a) || !Double.isFinite(b)) {
      return false;
    }
    return a == 0.0 || b == 0.0 || (a < 0.0) != (b < 0.0);
  }

  /** An exclusion as an expression that is zero on the excluded curve. */
  private static IExpr asDifference(IExpr exclusion) {
    if (exclusion.isEqual() && ((IAST) exclusion).argSize() == 2) {
      return F.Subtract(((IAST) exclusion).arg1(), ((IAST) exclusion).arg2());
    }
    if (exclusion.isAST(S.Unequal, 3)) {
      return F.Subtract(((IAST) exclusion).arg1(), ((IAST) exclusion).arg2());
    }
    return exclusion.isNumber() ? F.NIL : exclusion;
  }

  /** How many mesh levels a {@code MeshFunctions} entry draws when {@code Mesh} does not say. */
  private static final int MESH_FUNCTION_LEVELS = 8;

  /**
   * The mesh lines {@code MeshFunctions} asks for, as levels of each function it names.
   *
   * <p>
   * {@code Automatic} keeps the lines on the sampling grid, which the surface builder already
   * draws, so only an explicit list produces anything here.
   */
  private static IAST meshFunctionLines(double[][][] grid, int nx, int ny, IExpr meshFunctions,
      IExpr meshOption, EvalEngine engine) {
    if (meshFunctions == S.Automatic || meshFunctions.isNone() || meshOption.isNone()) {
      return F.CEmptyList;
    }
    IAST list = meshFunctions.isList() ? (IAST) meshFunctions : F.list(meshFunctions);
    int levels = meshOption.toIntDefault(MESH_FUNCTION_LEVELS);
    if (levels < 1) {
      levels = MESH_FUNCTION_LEVELS;
    }
    IASTAppendable all = F.ListAlloc(list.argSize() * 8);
    for (int k = 1; k < list.size(); k++) {
      IExpr meshFunction = list.get(k);
      double[][] values = new double[nx][ny];
      for (int i = 0; i < nx; i++) {
        for (int j = 0; j < ny; j++) {
          double[] point = grid[i][j];
          if (point == null) {
            values[i][j] = Double.NaN;
            continue;
          }
          IExpr value = engine.evalN(
              F.ternaryAST3(meshFunction, F.num(point[0]), F.num(point[1]), F.num(point[2])));
          values[i][j] = value.isNumber() ? value.evalfNaN() : Double.NaN;
        }
      }
      all.appendArgs(Plot3DTools.meshLines(grid, values, levels));
    }
    return all;
  }

  private static double scale(double value, double min, double max) {
    return max > min ? (value - min) / (max - min) : 0.5;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
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
