package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@code ContourPlot3D[f == g, {x, ..}, {y, ..}, {z, ..}]} - the surface on which a function of
 * three variables takes a given value, extracted with marching cubes.
 */
public class ContourPlot3D extends AbstractFunctionOptionEvaluator {

  /**
   * Samples per axis by default. The cost is cubic, so this is the point where the surface is
   * smooth enough to read while the plot still appears at once; the previous default of 45 meant
   * ninety thousand evaluations for every call.
   */
  private static final int DEFAULT_PLOT_POINTS = 25;

  /** An upper bound, because a cubic grid gets out of hand quickly. */
  private static final int MAX_PLOT_POINTS = 80;

  public ContourPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // a display wrapper comes off before the argument's shape is read, so a labelled dataset is
    // still recognised as a dataset; Plot3DTools.graphics3D puts the label back on the finished
    // primitives, reading it from the original call
    ast = PlotWrapper.stripFirstArgument(ast);
    if (argSize < 4 || !Plot3DTools.validRanges3D(ast, S.ContourPlot3D, engine)) {
      return F.NIL;
    }

    // a list of functions or equations has one surface or set of surfaces for each; a function
    // in a list stands for its zero surface unless Contours says otherwise
    boolean several = ast.arg1().isList();
    IAST functions = several ? (IAST) ast.arg1() : F.List(ast.arg1());
    if (functions.isEmpty()) {
      return F.NIL;
    }

    ISymbol[] vars = new ISymbol[3];
    double[] min = new double[3];
    double[] max = new double[3];
    if (!Plot3DTools.readBox(ast, S.ContourPlot3D, vars, min, max, engine)) {
      return F.NIL;
    }

    int points = plotPoints(options[Plot3DTools.X_PLOT_POINTS]);
    double[][] axis = axis(min, max, new int[] {points, points, points});
    // the region cuts the surfaces at its edge rather than leaving out the grid cells it touches,
    // so the samples are taken everywhere and the region only decides which levels are chosen
    RegionFunctionFilter region =
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);
    double[][][][] grids = new double[functions.argSize()][][][];
    double[][] levelSets = new double[functions.argSize()][];
    double lowestLevel = Double.POSITIVE_INFINITY;
    double highestLevel = Double.NEGATIVE_INFINITY;
    int surfaceCount = 0;
    for (int f = 0; f < grids.length; f++) {
      IExpr expr = functions.get(f + 1);
      boolean isEquation = expr.isAST(S.Equal, 3);
      if (isEquation) {
        expr = F.Subtract(((IAST) expr).arg1(), ((IAST) expr).arg2());
      }
      grids[f] = MarchingCubes.sample(expr, vars, min, max,
          options[Plot3DTools.X_EVALUATION_MONITOR], points, engine, null);
      if (grids[f] == null) {
        return F.NIL;
      }
      levelSets[f] = contourLevels(options[Plot3DTools.X_CONTOURS],
          region == null ? grids[f] : inRegion(grids[f], axis, region), isEquation || several);
      if (levelSets[f].length == 0) {
        return F.NIL;
      }
      lowestLevel = Math.min(lowestLevel, levelSets[f][0]);
      highestLevel = Math.max(highestLevel, levelSets[f][levelSets[f].length - 1]);
      surfaceCount += levelSets[f].length;
    }

    // ContourStyle is what styles the contour surfaces; PlotStyle only gets a say when no
    // ContourStyle was given, so that a plot written either way looks the same
    IExpr contourStyle = options[Plot3DTools.X_CONTOUR_STYLE];
    IExpr styleSource =
        contourStyle == S.Automatic ? options[Plot3DTools.X_PLOT_STYLE] : contourStyle;

    // The colour function of a contour surface is given the point and the value there. Marching
    // cubes produces a triangle soup rather than a grid, so a level is coloured as a whole, from
    // the middle of the box and the level's own value - which is exactly what a colour scheme
    // name asks for, since it reads the fourth argument.
    PlotColorFunction levelColors = Plot3DTools
        .plotColors(PlotColorFunction.Family.CONTOUR_3D, options, S.ContourPlot3D, engine)
        .ranges(min[0], max[0], min[1], max[1], min[2], max[2], lowestLevel, highestLevel)
        .build();

    // With nothing asked for, every surface is lit by lights of its own colour, as Mathematica
    // lights its contour surfaces, and the picture carries no Lighting option of its own.
    boolean ownLights =
        levelColors == null && (styleSource == null || styleSource.isAutomatic());
    IExpr boundaryStyle = options[Plot3DTools.X_BOUNDARY_STYLE];
    IASTAppendable surfaces = F.ListAlloc(surfaceCount);
    int surfaceIndex = 0;
    for (int f = 0; f < grids.length; f++) {
      for (double value : levelSets[f]) {
        IExpr style =
            levelStyle(surfaceIndex++, value, levelColors, ownLights, styleSource, min, max);
        GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, false);
        Plot3DTools.applyStyle(builder, style, options[Plot3DTools.X_MESH]);
        if (region == null) {
          MarchingCubes.marchingCubes(builder, grids[f], value, axis);
        } else {
          GraphicsComplexBuilder whole = new GraphicsComplexBuilder(true, false);
          MarchingCubes.marchingCubes(whole, grids[f], value, axis);
          clipToRegion(whole, builder, region, value, engine);
        }
        addMesh(builder, axis, options[Plot3DTools.X_MESH], options[Plot3DTools.X_MESH_STYLE]);
        addRim(builder, boundaryStyle);
        IExpr complex = builder.build();
        if (complex.isPresent()) {
          surfaces.append(complex);
        }
      }
    }
    if (surfaces.argSize() == 0) {
      return F.NIL;
    }

    IExpr[] defaults = Plot3DTools.cubeDefaults(min, max, !ownLights);
    return Plot3DTools.graphics3D(surfaces, originalAST, argSize, defaults);
  }

  /**
   * Whether a contour surface is outlined where it ends, at the box or at the edge of the region,
   * when <code>BoundaryStyle</code> is <code>Automatic</code>: in black.
   */
  private static final boolean OUTLINED_BY_DEFAULT = true;

  /** The number of halvings which place the edge of the region on an edge of the surface. */
  private static final int REGION_EDGE_ITERATIONS = 24;

  /**
   * A copy of <code>grid</code> with the samples outside the region replaced by <code>NaN</code>.
   */
  private static double[][][] inRegion(double[][][] grid, double[][] axis,
      RegionFunctionFilter region) {
    double[][][] inside = new double[grid.length][][];
    for (int i = 0; i < grid.length; i++) {
      inside[i] = new double[grid[i].length][];
      for (int j = 0; j < grid[i].length; j++) {
        inside[i][j] = grid[i][j].clone();
        for (int k = 0; k < grid[i][j].length; k++) {
          double value = grid[i][j][k];
          if (Double.isFinite(value)
              && !region.accepts(axis[0][i], axis[1][j], axis[2][k], value)) {
            inside[i][j][k] = Double.NaN;
          }
        }
      }
    }
    return inside;
  }

  /**
   * Copy the polygons of <code>whole</code> into <code>clipped</code>, each cut to the part inside
   * the region. Where an edge of a polygon leaves the region the edge of the region is found by
   * halving the edge, always from its inner end, so that the polygons on either side of the edge
   * get the same point and stay joined. The surface takes the value <code>level</code> at every
   * point, which is the value the region function is given.
   */
  private static void clipToRegion(GraphicsComplexBuilder whole, GraphicsComplexBuilder clipped,
      RegionFunctionFilter region, double level, EvalEngine engine) {
    java.util.Map<Integer, Boolean> inside = new java.util.HashMap<>();
    for (int p = 1; p <= whole.polygonCount(); p++) {
      engine.checkTimeLimit();
      int[] corners = whole.polygon(p);
      int n = corners.length;
      boolean[] in = new boolean[n];
      boolean all = true;
      boolean none = true;
      for (int c = 0; c < n; c++) {
        double[] point = whole.coordinatesOf(corners[c]);
        in[c] = inside.computeIfAbsent(corners[c],
            v -> region.accepts(point[0], point[1], point[2], level));
        all &= in[c];
        none &= !in[c];
      }
      if (none) {
        continue;
      }
      java.util.List<Integer> indices = new java.util.ArrayList<>(n + 2);
      for (int c = 0; c < n; c++) {
        int a = corners[c];
        int b = corners[(c + 1) % n];
        if (in[c]) {
          indices.add(copyVertex(whole, clipped, a));
        }
        if (!all && in[c] != in[(c + 1) % n]) {
          indices.add(regionEdge(whole, clipped, in[c] ? a : b, in[c] ? b : a, region, level));
        }
      }
      // a point of the region edge which is welded onto a corner adds no corner
      for (int k = indices.size() - 1; k >= 0 && indices.size() > 1; k--) {
        if (indices.get(k).equals(indices.get((k + 1) % indices.size()))) {
          indices.remove(k);
        }
      }
      if (indices.size() >= 3) {
        int[] polygon = new int[indices.size()];
        for (int k = 0; k < polygon.length; k++) {
          polygon[k] = indices.get(k);
        }
        if (polygon.length == 3) {
          clipped.addPolygon(polygon[0], polygon[1], polygon[2]);
        } else {
          clipped.addPolygon(polygon);
        }
      }
    }
  }

  /** Vertex <code>index</code> of <code>from</code> added to <code>to</code>. */
  private static int copyVertex(GraphicsComplexBuilder from, GraphicsComplexBuilder to,
      int index) {
    double[] point = from.coordinatesOf(index);
    return to.addVertex(point[0], point[1], point[2], from.normalOf(index), null);
  }

  /**
   * The point where the edge from vertex <code>inner</code>, inside the region, to vertex
   * <code>outer</code> leaves the region, added to <code>to</code> with an interpolated normal.
   */
  private static int regionEdge(GraphicsComplexBuilder from, GraphicsComplexBuilder to, int inner,
      int outer, RegionFunctionFilter region, double level) {
    double[] a = from.coordinatesOf(inner);
    double[] b = from.coordinatesOf(outer);
    double lo = 0.0;
    double hi = 1.0;
    for (int i = 0; i < REGION_EDGE_ITERATIONS; i++) {
      double mid = 0.5 * (lo + hi);
      if (region.accepts(a[0] + mid * (b[0] - a[0]), a[1] + mid * (b[1] - a[1]),
          a[2] + mid * (b[2] - a[2]), level)) {
        lo = mid;
      } else {
        hi = mid;
      }
    }
    double t = lo;
    double[] na = from.normalOf(inner);
    double[] nb = from.normalOf(outer);
    double[] normal = null;
    if (na != null && nb != null) {
      normal = new double[3];
      double length = 0.0;
      for (int c = 0; c < 3; c++) {
        normal[c] = na[c] + t * (nb[c] - na[c]);
        length += normal[c] * normal[c];
      }
      length = Math.sqrt(length);
      if (length > 0.0) {
        for (int c = 0; c < 3; c++) {
          normal[c] /= length;
        }
      } else {
        normal = na;
      }
    }
    return to.addVertex(a[0] + t * (b[0] - a[0]), a[1] + t * (b[1] - a[1]),
        a[2] + t * (b[2] - a[2]), normal, null);
  }

  /**
   * Outline a contour surface where it ends, at the box or at the edge of the region, unless
   * <code>BoundaryStyle</code> says not to: one <code>Line</code> of vertex numbers in the
   * <code>GraphicsComplex</code>, each open curve of the rim joined into one list.
   */
  static void addRim(GraphicsComplexBuilder builder, IExpr boundaryStyle) {
    if (Plot3DTools.drawsBoundary(boundaryStyle, OUTLINED_BY_DEFAULT)) {
      IExpr rim = builder.openEdgeLine();
      if (rim.isPresent()) {
        builder.addPrimitive(F.List(Plot3DTools.boundaryDirective(boundaryStyle), rim));
      }
    }
  }

  /**
   * The style of contour surface number <code>surfaceIndex</code> at level <code>value</code>: from
   * the colour function, else lit in its own colour, else from the style option.
   */
  static IExpr levelStyle(int surfaceIndex, double value, PlotColorFunction levelColors,
      boolean ownLights, IExpr styleSource, double[] min, double[] max) {
    if (levelColors != null) {
      return levelColors.color((min[0] + max[0]) / 2.0, (min[1] + max[1]) / 2.0,
          (min[2] + max[2]) / 2.0, value);
    }
    if (ownLights) {
      return litSurfaceStyle(surfaceIndex);
    }
    return Plot3DTools.surfaceStyle(surfaceIndex, styleSource);
  }

  /**
   * The <code>GraphicsComplex</code> of the surface where the samples of <code>grid</code>, taken
   * at the coordinates <code>axis</code>, take the value <code>value</code>; {@link F#NIL} if there
   * is none.
   */
  static IExpr surface(double[][][] grid, double[][] axis, double value, IExpr style,
      IExpr meshOption, IExpr meshStyle, IExpr boundaryStyle) {
    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, false);
    Plot3DTools.applyStyle(builder, style, meshOption);
    MarchingCubes.marchingCubes(builder, grid, value, axis);
    addMesh(builder, axis, meshOption, meshStyle);
    addRim(builder, boundaryStyle);
    return builder.build();
  }

  /**
   * The mesh of a contour surface: the curves in which planes perpendicular to the x, y and z axis
   * cut it. The curves of one axis form one {@code Line} of coordinate lists after the style of
   * that axis, the styles of a {@code MeshStyle} list being taken in turn.
   *
   * <p>
   * {@code Mesh -> n} cuts each axis at n evenly spaced positions strictly inside the plotted
   * box, {@code Mesh -> {mx, my, mz}} gives each axis its own count or list of positions. Without
   * an explicit mesh specification the surface is drawn without mesh lines.
   */
  private static void addMesh(GraphicsComplexBuilder builder, double[][] axis, IExpr meshOption,
      IExpr meshStyle) {
    if (meshOption == null || meshStyle == null || meshStyle.isNone() || meshStyle.isFalse()) {
      return;
    }
    for (int c = 0; c < 3; c++) {
      double[] positions = meshPositions(meshOption, c, axis[c][0], axis[c][axis[c].length - 1]);
      if (positions.length == 0) {
        continue;
      }
      IASTAppendable curves = F.ListAlloc();
      for (double position : positions) {
        for (java.util.List<double[]> curve : builder.planeSections(c, position)) {
          IASTAppendable line = F.ListAlloc(curve.size());
          for (double[] point : curve) {
            line.append(F.List(F.num(point[0]), F.num(point[1]), F.num(point[2])));
          }
          curves.append(line);
        }
      }
      if (curves.argSize() > 0) {
        builder.addPrimitive(F.List(meshAxisStyle(meshStyle, c), F.Line(curves)));
      }
    }
  }

  /** The style of the mesh lines across axis {@code c}. */
  private static IExpr meshAxisStyle(IExpr meshStyle, int c) {
    if (meshStyle.isAutomatic()) {
      return Plot3DTools.MESH_STYLE;
    }
    if (meshStyle.isList() && meshStyle.argSize() > 0) {
      return meshStyle.getAt(c % meshStyle.argSize() + 1);
    }
    return meshStyle;
  }

  /** The positions along axis {@code c}, from {@code min} to {@code max}, where it is cut. */
  static double[] meshPositions(IExpr meshOption, int c, double min, double max) {
    IExpr entry = meshOption;
    if (meshOption.isList()) {
      if (meshOption.argSize() != 3) {
        return new double[0];
      }
      entry = meshOption.getAt(c + 1);
      if (entry.isList()) {
        IAST list = (IAST) entry;
        double[] explicit = new double[list.argSize()];
        int n = 0;
        for (int i = 1; i <= list.argSize(); i++) {
          double v = list.get(i).evalfNaN();
          if (Double.isFinite(v)) {
            explicit[n++] = v;
          }
        }
        return java.util.Arrays.copyOf(explicit, n);
      }
    }
    if (!entry.isInteger()) {
      return new double[0];
    }
    int count = entry.toIntDefault(0);
    if (count < 1 || !(max > min)) {
      return new double[0];
    }
    count = Math.min(count, 100);
    double[] positions = new double[count];
    for (int k = 1; k <= count; k++) {
      positions[k - 1] = min + (max - min) * k / (count + 1.0);
    }
    return positions;
  }

  /** The coordinates of <code>counts[c]</code> evenly spaced samples from min to max per axis. */
  static double[][] axis(double[] min, double[] max, int[] counts) {
    double[][] axis = new double[3][];
    for (int c = 0; c < 3; c++) {
      axis[c] = new double[counts[c]];
      double step = (max[c] - min[c]) / (counts[c] - 1);
      for (int i = 0; i < counts[c]; i++) {
        axis[c][i] = min[c] + i * step;
      }
    }
    return axis;
  }

  /**
   * The lights Mathematica gives each of the first six contour surfaces, probed on 2026-09-11
   * (<code>Cases[ContourPlot3D[x^3 + y^2 - z^2, ..., Contours -> 6], Directive[___, Lighting -> _,
   * ___], Infinity]</code>). They follow no formula - the directional light is 0.3, 0.42, 0.25,
   * 0.25, 0.5 and 0.25 of the surface colour - so they are written out. Surface <code>i</code> is
   * {@link Plot3DTools#surfaceColor(int, IExpr) surface colour} <code>i</code>.
   */
  private static final double[][] AMBIENT = { //
      {0.30100577, 0.224146685, 0.090484535}, //
      {0.196998383, 0.252204821, 0.333209402}, //
      {0.1830429875, 0.2142476375, 0.0962851875}, //
      {0.30756835, 0.18676585, 0.147065275}, //
      {0.4113952, 0.3882496, 0.4805404}, //
      {0.3544158, 0.2863108, 0.2204774}};

  private static final double[][] DIRECTIONAL = { //
      {0.2642166, 0.1833123, 0.0426153}, //
      {0.15473514, 0.21284718, 0.29811516}, //
      {0.14004525, 0.17289225, 0.04872125}, //
      {0.2306315, 0.0964065, 0.05229475}, //
      {0.264244, 0.235312, 0.3506755}, //
      {0.19301975, 0.1078885, 0.02559675}};

  /** The exponent of the white highlight: from the fourth surface on it is tighter. */
  private static final int[] SPECULAR_EXPONENT = {3, 3, 3, 6, 6, 6};

  /** Contour surface number <code>level</code> in Mathematica's default style. */
  static IExpr litSurfaceStyle(int level) {
    if (level >= AMBIENT.length) {
      return Plot3DTools.surfaceStyle(level, S.Automatic);
    }
    IExpr directional = rgb(DIRECTIONAL[level]);
    // the light from above the middle of the box: the surface's own colour for the first three,
    // then a grey, a violet hue and a grey again
    IExpr middle = level == 4 ? F.ternaryAST3(S.Hue, F.num(0.7), F.C1, F.C1)
        : level >= 3 ? F.GrayLevel(F.num(0.3)) : directional;
    IExpr lights = F.List( //
        F.List(F.stringx("Ambient"), rgb(AMBIENT[level])), //
        F.List(F.stringx("Directional"), directional, imageScaled(0, 2, 2)), //
        F.List(F.stringx("Directional"), middle, imageScaled(2, 2, 2)), //
        F.List(F.stringx("Directional"), directional, imageScaled(2, 0, 2)));
    return F.Directive(
        F.binaryAST2(S.Specularity, F.GrayLevel(F.C1), F.ZZ(SPECULAR_EXPONENT[level])),
        Plot3DTools.surfaceColor(level, S.Automatic), F.Rule(S.Lighting, lights));
  }

  private static IExpr rgb(double[] c) {
    return F.RGBColor(c[0], c[1], c[2]);
  }

  private static IExpr imageScaled(int x, int y, int z) {
    return F.unaryAST1(S.ImageScaled, F.List(F.ZZ(x), F.ZZ(y), F.ZZ(z)));
  }

  private static int plotPoints(IExpr option) {
    int points = Plot3DTools.plotPoints(option, DEFAULT_PLOT_POINTS)[0];
    return Math.max(2, Math.min(points, MAX_PLOT_POINTS));
  }

  /**
   * The values the surfaces are drawn at.
   *
   * <p>
   * An equation fixes the level at zero. For a bare function {@code Contours} chooses, defaulting
   * to a few levels spread through the range the function actually takes.
   */
  static double[] contourLevels(IExpr option, double[][][] grid, boolean isEquation) {
    if (option != null && option.isList()) {
      IAST list = (IAST) option;
      double[] levels = new double[list.argSize()];
      int count = 0;
      for (int i = 1; i <= list.argSize(); i++) {
        double value = list.get(i).evalfNaN();
        if (Double.isFinite(value)) {
          levels[count++] = value;
        }
      }
      if (count > 0) {
        double[] trimmed = new double[count];
        System.arraycopy(levels, 0, trimmed, 0, count);
        return trimmed;
      }
    }
    int requested = option != null && option.isInteger() ? option.toIntDefault(0) : 0;
    if (requested <= 0) {
      if (isEquation) {
        return new double[] {0.0};
      }
      requested = 3;
    }
    double lo = Double.MAX_VALUE;
    double hi = -Double.MAX_VALUE;
    for (double[][] plane : grid) {
      for (double[] row : plane) {
        for (double value : row) {
          if (Double.isFinite(value)) {
            lo = Math.min(lo, value);
            hi = Math.max(hi, value);
          }
        }
      }
    }
    if (lo > hi) {
      return new double[0];
    }
    if (hi - lo < 1e-12) {
      return new double[] {lo};
    }
    double[] levels = new double[requested];
    for (int i = 0; i < requested; i++) {
      levels[i] = lo + (hi - lo) * (i + 1.0) / (requested + 1.0);
    }
    return levels;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return new int[] {4, Integer.MAX_VALUE};
  }

  // CPD-OFF the sampling settings of a plot function, the same as those of Plot3D
  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  protected int dependentRangeOffset() {
    return 0;
  }

  @Override
  protected boolean samplesFirstArgument() {
    // the function is evaluated at the sample points: a function which iterates until its values
    // agree never stops on symbolic coordinates
    return true;
  }

  @Override
  protected long symbolicFirstArgumentMillis() {
    // D(f, x) or an expansion is still done once with symbolic coordinates, if it finishes
    return PLOT_SYMBOLIC_MILLIS;
  }

  @Override
  public boolean localIterators() {
    // the plot variable is local to the plot
    return true;
  }

  @Override
  protected boolean compoundIteratorVariables() {
    // a plot variable like x(1) is replaced by a unique symbol
    return true;
  }
  // CPD-ON

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options =
        Plot3DTools.frameExtras(Plot3DTools.surfaceExtras(Plot3DTools.base3D()).add(S.Automatic,
            S.Contours, S.ContourStyle, S.RegionBoundaryStyle));
    // the mesh lines are cut by planes across the x, y and z axis
    options.override(S.MeshFunctions, F.List(F.Function(F.Slot1), F.Function(F.Slot2),
        F.Function(F.Slot3)));
    setOptions(newSymbol, options.keys(), options.values());
  }
}
