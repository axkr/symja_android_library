package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.RegionClip;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The parts every {@code *Plot3D} builtin needs: reading the options they share, turning a sampled
 * grid into a {@code GraphicsComplex}, and assembling the {@code Graphics3D} that comes out.
 *
 * <p>
 * Before this existed each plot had its own copy of the sampling loop, and they had drifted: three
 * different winding conventions, four different rules for which colour the first surface gets, and
 * two different ways of dealing with a sample that came back {@code NaN}. Going through one place
 * is what makes {@code Plot3D} and {@code SphericalPlot3D} look like they belong to the same
 * system.
 */
public final class Plot3DTools {

  private Plot3DTools() {}

  // ---------------------------------------------------------------------------------------------
  // The option block every 3D plot starts with, in the order the X_* constants index it.
  //
  // Every Graphics3D option has to be declared even though the plot itself does not read most of
  // them. The engine strips options by scanning backwards from the last argument and stopping at
  // the first rule the symbol does not declare, so a single undeclared name silently swallows
  // every option written before it: Plot3D[f, {x,..}, {y,..}, PlotPoints -> 3, Axes -> False] used
  // to sample at the default 40 because Axes stopped the scan before PlotPoints was reached.
  // ---------------------------------------------------------------------------------------------

  public static final int X_AXES = 0;
  public static final int X_AXES_LABEL = 1;
  public static final int X_AXES_EDGE = 2;
  public static final int X_AXES_STYLE = 3;
  public static final int X_BACKGROUND = 4;
  public static final int X_BOXED = 5;
  public static final int X_BOX_RATIOS = 6;
  public static final int X_BOX_STYLE = 7;
  public static final int X_FACE_GRIDS = 8;
  public static final int X_IMAGE_SIZE = 9;
  public static final int X_LIGHTING = 10;
  public static final int X_PLOT_LABEL = 11;
  public static final int X_PLOT_RANGE = 12;
  public static final int X_TICKS = 13;
  public static final int X_TICKS_STYLE = 14;
  public static final int X_LABEL_STYLE = 15;
  public static final int X_VIEW_POINT = 16;
  public static final int X_VIEW_VERTICAL = 17;
  public static final int X_VIEW_ANGLE = 18;
  public static final int X_VIEW_CENTER = 19;
  public static final int X_VIEW_PROJECTION = 20;
  public static final int X_VIEW_RANGE = 21;
  public static final int X_SPHERICAL_REGION = 22;
  public static final int X_SCALING_FUNCTIONS = 23;
  public static final int X_PLOT_THEME = 24;
  public static final int X_PERFORMANCE_GOAL = 25;

  // added by surfaceExtras, in this order
  public static final int X_PLOT_POINTS = 26;
  public static final int X_PLOT_STYLE = 27;
  public static final int X_COLOR_FUNCTION = 28;
  public static final int X_COLOR_FUNCTION_SCALING = 29;
  public static final int X_MESH = 30;
  public static final int X_MESH_STYLE = 31;
  public static final int X_MAX_RECURSION = 32;
  public static final int X_REGION_FUNCTION = 33;
  public static final int X_BOUNDARY_STYLE = 34;
  public static final int X_NORMALS_FUNCTION = 35;
  public static final int X_PLOT_LEGENDS = 36;
  public static final int X_WORKING_PRECISION = 37;
  public static final int X_EXCLUSIONS = 38;
  public static final int X_MESH_FUNCTIONS = 39;
  public static final int X_MESH_SHADING = 40;
  public static final int X_FILLING = 41;
  public static final int X_FILLING_STYLE = 42;
  public static final int X_CLIPPING_STYLE = 43;
  public static final int X_EVALUATION_MONITOR = 44;
  public static final int X_EXCLUSIONS_STYLE = 45;
  public static final int X_PLOT_LABELS = 46;
  public static final int X_TEXTURE_COORDINATE_FUNCTION = 47;
  public static final int X_TEXTURE_COORDINATE_SCALING = 48;

  /** Added by {@code RevolutionPlot3D} alone, after the surface block. */
  public static final int X_REVOLUTION_AXIS = 49;

  /** Added by {@code ContourPlot3D} alone, after the surface block. */
  public static final int X_CONTOURS = 49;
  public static final int X_CONTOUR_STYLE = 50;
  public static final int X_REGION_BOUNDARY_STYLE = 51;

  /** Added by the plots that take explicit data, after the surface block. */
  public static final int X_DATA_RANGE = 49;
  public static final int X_INTERPOLATION_ORDER = 50;
  public static final int X_MAX_PLOT_POINTS = 51;

  /** The {@code Graphics3D} options every 3D plot accepts and passes on. */
  public static GraphicsOptions.OptionSet base3D() {
    return new GraphicsOptions.OptionSet()
        .add(S.Automatic, S.Axes, S.AxesLabel, S.AxesEdge, S.AxesStyle, S.Background)
        .add(S.True, S.Boxed) //
        .add(S.Automatic, S.BoxRatios, S.BoxStyle) //
        .add(S.None, S.FaceGrids) //
        .add(S.Automatic, S.ImageSize, S.Lighting) //
        .add(S.None, S.PlotLabel) //
        .add(S.Automatic, S.PlotRange, S.Ticks, S.TicksStyle, S.LabelStyle, S.ViewPoint,
            S.ViewVertical, S.ViewAngle, S.ViewCenter, S.ViewProjection) //
        .add(S.All, S.ViewRange) //
        .add(S.Automatic, S.SphericalRegion) //
        .add(S.None, S.ScalingFunctions) //
        .add(S.Automatic, S.PlotTheme, S.PerformanceGoal);
  }

  /** The options of a plot that samples a surface. */
  public static GraphicsOptions.OptionSet surfaceExtras(GraphicsOptions.OptionSet set) {
    return set.add(S.Automatic, S.PlotPoints, S.PlotStyle, S.ColorFunction) //
        .add(S.True, S.ColorFunctionScaling) //
        .add(S.Automatic, S.Mesh, S.MeshStyle, S.MaxRecursion, S.RegionFunction, S.BoundaryStyle,
            S.NormalsFunction) //
        .add(S.None, S.PlotLegends) //
        .add(S.MachinePrecision, S.WorkingPrecision) //
        .add(S.Automatic, S.Exclusions, S.MeshFunctions) //
        .add(S.None, S.MeshShading, S.Filling) //
        .add(S.Automatic, S.FillingStyle, S.ClippingStyle) //
        .add(S.None, S.EvaluationMonitor, S.ExclusionsStyle, S.PlotLabels) //
        .add(S.Automatic, S.TextureCoordinateFunction) //
        .add(S.True, S.TextureCoordinateScaling);
  }

  /**
   * The options every graphic documents but no plot here reads.
   *
   * <p>
   * They are declared because of how options are stripped: the engine scans backwards from the last
   * argument and stops at the first rule the symbol does not declare, so one unknown name silently
   * swallows every option written before it. A call that ends in {@code Epilog -> ...} would
   * otherwise lose its {@code PlotPoints}. This block goes on last so that it can grow without
   * moving any of the positions the {@code X_*} constants name.
   */
  public static GraphicsOptions.OptionSet frameExtras(GraphicsOptions.OptionSet set) {
    return set //
        .add(S.Automatic, S.AlignmentPoint, S.AspectRatio, S.AxesOrigin, S.BaselinePosition,
            S.ClipPlanesStyle, S.ContentSelectable, S.ControllerPath, S.FormatType, S.ImagePadding,
            S.LabelingFunction, S.LabelingSize, S.Method, S.PlotRangePadding, S.PlotRegion,
            S.PreserveImageOptions, S.RotationAction) //
        .add(S.None, S.ClipPlanes) //
        .add(F.CEmptyList, S.BaseStyle, S.Epilog, S.Prolog, S.FaceGridsStyle) //
        .add(F.C0, S.ImageMargins) //
        .add(S.False, S.ControllerLinking, S.TouchscreenAutoZoom);
  }

  /**
   * The {@code Graphics3D} block appended after a plot's own positional options.
   *
   * <p>
   * Used by the plots that read their options by a fixed index and so cannot have the shared block
   * put in front of them.
   */
  public static GraphicsOptions.OptionSet discreteExtras(GraphicsOptions.OptionSet set) {
    return frameExtras(set.add(base3D().keys(), base3D().values()) //
        .add(S.Automatic, S.PlotStyle, S.PlotTheme, S.ExtentElementFunction, S.ColorFunctionScaling,
            S.ClippingStyle, S.RegionFunction, S.LabelingFunction, S.LabelingSize) //
        // PlotMarkers is Automatic, not None: the default appearance is a stem with a marker on
        // top, and declaring it None took the marker away from every plot that never asked
        .add(S.Automatic, S.PlotMarkers) //
        .add(S.None, S.ExtentMarkers, S.EvaluationMonitor) //
        .add(S.False, S.Joined) //
        .add(S.MachinePrecision, S.WorkingPrecision) //
        .add(S.Automatic, S.Filling, S.FillingStyle));
  }

  /** The options of a plot that takes explicit data rather than a function. */
  public static GraphicsOptions.OptionSet listExtras(GraphicsOptions.OptionSet set) {
    return surfaceExtras(set).add(S.Automatic, S.DataRange, S.InterpolationOrder, S.MaxPlotPoints);
  }

  /**
   * Where an option sits in a block, so a plot can read it without counting positions by hand.
   *
   * <p>
   * The blocks that are assembled from several pieces, and the ones whose own options come first,
   * are laid out too awkwardly to name with a literal constant. Looking the position up from the
   * same block the symbol was registered with cannot drift out of step with it.
   *
   * @throws IllegalArgumentException when the block does not declare the option, which is a
   *         programming error rather than anything a user can cause
   */
  public static int indexOf(GraphicsOptions.OptionSet set, IBuiltInSymbol option) {
    IBuiltInSymbol[] keys = set.keys();
    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == option) {
        return i;
      }
    }
    throw new IllegalArgumentException("option " + option + " is not declared in this block");
  }

  /** The whole option table of a plot that samples a surface. */
  public static GraphicsOptions.OptionSet surfacePlot() {
    return frameExtras(surfaceExtras(base3D()));
  }

  /** The whole option table of a plot that takes explicit data. */
  public static GraphicsOptions.OptionSet listPlot() {
    return frameExtras(listExtras(base3D()));
  }

  /**
   * The lighting a plotted surface is lit by.
   *
   * <p>
   * Lights each surface with lights tinted to that surface's own colour, which comes out as the
   * surface colour rendered faithfully with a white highlight. The renderer installs one set of
   * lights for the whole scene and so cannot tint per surface; white light is the setting that
   * reproduces the same result, and it is what keeps two surfaces in one picture showing the two
   * different colours they were given. The coloured lights of {@code Lighting -> Automatic} stay
   * the default for a hand written {@code Graphics3D}.
   */
  public static final IExpr PLOT_LIGHTING = F.stringx("Neutral");

  /** The mesh line colour which is drawn on a surface. */
  public static final IAST MESH_STYLE = F.GrayLevel(F.num(0.2));

  /**
   * The surface colours for a 3D plot, read off its own {@code InputForm} output.
   *
   * <p>
   * A surface does not start from the same colour a curve does: {@code Plot3D[f, ..]} comes out
   * gold, not blue, and that gold is the second entry of the standard colour cycle. The rest of the
   * cycle follows it, so several surfaces in one picture stay far apart in hue.
   */
  private static final double[][] SURFACE_COLORS = { //
      {0.880722, 0.611041, 0.142051}, // the colour a single surface gets
      {0.368417, 0.506779, 0.709798}, //
      {0.560181, 0.691569, 0.194885}, //
      {0.922526, 0.385626, 0.209179}, //
      {0.528488, 0.470624, 0.701351}, //
      {0.772079, 0.431554, 0.102387}, //
      {0.363898, 0.618501, 0.782349}, //
      {1.0, 0.75, 0.0}, //
      {0.647624, 0.37816, 0.614037}, //
      {0.571589, 0.586483, 0.0}, //
      {0.915, 0.3325, 0.2125}, //
      {0.400822, 0.522007, 0.85}, //
      {0.972829, 0.621644, 0.073362}, //
      {0.736783, 0.358, 0.503027}, //
      {0.280264, 0.715, 0.429209}};

  /**
   * The white highlight on a plotted surface.
   *
   * <p>
   * Without it a surface reads as flat paint. The exponent of 3 is a broad, soft highlight rather
   * than the tight one a polished solid would have.
   */
  public static final IAST SURFACE_SPECULARITY =
      F.binaryAST2(S.Specularity, F.GrayLevel(F.C1), F.C3);

  /** How much darker than its palette entry a chart element is drawn. */
  private static final double CHART_DARKEN = 0.1;

  /** How much lighter than the element colour the translucent face of a bar is. */
  private static final double CHART_FACE_LIGHTEN = 0.3;

  /** The opacity so that bars behind it stay readable. */
  private static final double CHART_FACE_OPACITY = 0.5;

  /** {@code BoxRatios} for a plot over a rectangular domain. */
  public static final IAST FLAT_BOX_RATIOS = F.List(F.C1, F.C1, F.num(0.4));

  /** Read {@code PlotPoints}, which may be a single count or one per direction. */
  public static int[] plotPoints(IExpr option, int defaultCount) {
    int u = defaultCount;
    int v = defaultCount;
    if (option.isList() && ((IAST) option).argSize() >= 2) {
      IAST list = (IAST) option;
      u = list.arg1().toIntDefault(defaultCount);
      v = list.arg2().toIntDefault(defaultCount);
    } else if (option.isList() && ((IAST) option).argSize() == 1) {
      u = ((IAST) option).arg1().toIntDefault(defaultCount);
      v = u;
    } else {
      int n = option.toIntDefault(defaultCount);
      u = n;
      v = n;
    }
    return new int[] {clampPlotPoints(u, defaultCount), clampPlotPoints(v, defaultCount)};
  }

  private static int clampPlotPoints(int value, int defaultCount) {
    if (value < 2) {
      return Math.max(2, defaultCount);
    }
    // an upper bound keeps a mistyped PlotPoints from building a mesh nothing can render
    return Math.min(value, 400);
  }

  /**
   * Iso lines of a scalar over the sampled grid, as {@code Line} primitives in three dimensions.
   *
   * <p>
   * This is what {@code MeshFunctions} draws: instead of following the sampling grid, a mesh line
   * follows a level of some function of the point. Each cell of the grid is walked and the level is
   * traced across it wherever it enters and leaves, so the lines come out continuous and sit on the
   * surface rather than beside it.
   *
   * @param grid the sampled surface, {@code null} at a point that has no value
   * @param values the mesh function at each of those points
   * @param levels how many evenly spaced levels to draw
   */
  public static IASTAppendable meshLines(double[][][] grid, double[][] values, int levels) {
    int nx = grid.length;
    int ny = nx > 0 ? grid[0].length : 0;
    IASTAppendable lines = F.ListAlloc(levels * 8);
    if (nx < 2 || ny < 2 || levels < 1) {
      return lines;
    }
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;
    for (int i = 0; i < nx; i++) {
      for (int j = 0; j < ny; j++) {
        if (grid[i][j] != null && Double.isFinite(values[i][j])) {
          min = Math.min(min, values[i][j]);
          max = Math.max(max, values[i][j]);
        }
      }
    }
    if (!(max > min)) {
      return lines;
    }
    for (int level = 1; level <= levels; level++) {
      double target = min + (max - min) * level / (levels + 1.0);
      for (int i = 0; i < nx - 1; i++) {
        for (int j = 0; j < ny - 1; j++) {
          traceCell(lines, grid, values, i, j, target);
        }
      }
    }
    return lines;
  }

  /**
   * The outline of a sampled surface: its rim, and the rim of every hole in it.
   *
   * <p>
   * This is what {@code BoundaryStyle} draws. A grid edge is on the outline when both its ends
   * carry a value but the surface stops there - either because the edge is on the border of the
   * sampled rectangle, or because one of the two cells beside it is missing a corner. That covers
   * the edge a {@code RegionFunction} cuts and the seam an {@code Exclusions} curve opens without
   * either of them having to say so.
   */
  /**
   * Whether {@code BoundaryStyle} asks for an outline at all.
   *
   * <p>
   * {@code Automatic} means no outline: a surface is read by its shading, and a rim drawn round
   * every plot that never asked for one would be noise. {@code None} says the same thing
   * explicitly. Anything else is a style to draw the outline in.
   */
  public static boolean drawsBoundary(IExpr boundaryStyle) {
    return boundaryStyle != null && boundaryStyle.isPresent() && boundaryStyle != S.Automatic
        && !boundaryStyle.isAutomatic() && !boundaryStyle.isNone();
  }

  /**
   * The surface with its outline appended, when {@code BoundaryStyle} asks for one.
   *
   * <p>
   * The outline is kept outside the {@code GraphicsComplex} so that it carries its own colour
   * rather than being shaded along with the surface it lies on. When nothing is to be drawn the
   * surface is returned exactly as it came in, so a plot that was never given a
   * {@code BoundaryStyle} keeps the shape its callers already expect.
   *
   * @param complex the built surface
   * @param grid the sampled points the surface was built from, {@code null} where it has none
   * @param boundaryStyle the {@code BoundaryStyle} option value
   */
  public static IExpr withBoundary(IExpr complex, double[][][] grid, IExpr boundaryStyle) {
    if (complex.isNIL() || grid == null || !drawsBoundary(boundaryStyle)) {
      return complex;
    }
    IAST boundary = surfaceBoundary(grid);
    if (boundary.argSize() == 0) {
      return complex;
    }
    return F.List(complex, boundaryStyle, boundary);
  }

  public static IASTAppendable surfaceBoundary(double[][][] grid) {
    int nx = grid.length;
    int ny = nx > 0 ? grid[0].length : 0;
    IASTAppendable lines = F.ListAlloc(Math.max(4, nx + ny));
    if (nx < 2 || ny < 2) {
      return lines;
    }
    for (int i = 0; i < nx; i++) {
      for (int j = 0; j < ny; j++) {
        if (grid[i][j] == null) {
          continue;
        }
        if (i + 1 < nx && grid[i + 1][j] != null //
            && (!cellComplete(grid, i, j - 1) || !cellComplete(grid, i, j))) {
          lines.append(segment(grid[i][j], grid[i + 1][j]));
        }
        if (j + 1 < ny && grid[i][j + 1] != null //
            && (!cellComplete(grid, i - 1, j) || !cellComplete(grid, i, j))) {
          lines.append(segment(grid[i][j], grid[i][j + 1]));
        }
      }
    }
    return lines;
  }

  /** Whether the cell whose lower corner is {@code (i, j)} exists and has all four corners. */
  private static boolean cellComplete(double[][][] grid, int i, int j) {
    if (i < 0 || j < 0 || i + 1 >= grid.length || j + 1 >= grid[0].length) {
      return false;
    }
    return grid[i][j] != null && grid[i + 1][j] != null && grid[i][j + 1] != null
        && grid[i + 1][j + 1] != null;
  }

  private static IExpr segment(double[] from, double[] to) {
    return F.Line(F.List(//
        F.List(F.num(from[0]), F.num(from[1]), F.num(from[2])), //
        F.List(F.num(to[0]), F.num(to[1]), F.num(to[2]))));
  }

  /** The segment of one level inside one cell of the grid, if the level passes through it. */
  private static void traceCell(IASTAppendable lines, double[][][] grid, double[][] values, int i,
      int j, double target) {
    double[][] corners = {grid[i][j], grid[i + 1][j], grid[i + 1][j + 1], grid[i][j + 1]};
    double[] at = {values[i][j], values[i + 1][j], values[i + 1][j + 1], values[i][j + 1]};
    for (int c = 0; c < 4; c++) {
      if (corners[c] == null || !Double.isFinite(at[c])) {
        return; // an incomplete cell has no interior to trace
      }
    }
    IASTAppendable crossings = F.ListAlloc(2);
    for (int edge = 0; edge < 4; edge++) {
      int a = edge;
      int b = (edge + 1) % 4;
      double va = at[a];
      double vb = at[b];
      if ((va < target) == (vb < target)) {
        continue;
      }
      double t = (target - va) / (vb - va);
      crossings.append(F.List(//
          F.num(corners[a][0] + t * (corners[b][0] - corners[a][0])), //
          F.num(corners[a][1] + t * (corners[b][1] - corners[a][1])), //
          F.num(corners[a][2] + t * (corners[b][2] - corners[a][2]))));
    }
    // two crossings is a segment; four means the level passes through twice and the cell is too
    // coarse to say how, so it is left out rather than guessed at
    if (crossings.argSize() == 2) {
      lines.append(F.Line(crossings));
    }
  }

  /**
   * The style directive for surface number {@code index}, counting from zero.
   *
   * <p>
   * An index of zero has to come out as the first plot colour. The shared counter that the 2D code
   * uses is stateful, and calling it with an explicit index used to skip past the first entry, so
   * every 3D surface came out orange while the equivalent 2D curve came out blue.
   */
  public static IExpr surfaceStyle(int index, IExpr plotStyle) {
    return F.Directive(SURFACE_SPECULARITY, surfaceColor(index, plotStyle));
  }

  /** The colour of surface number {@code index}, without the highlight that goes with it. */
  public static IExpr surfaceColor(int index, IExpr plotStyle) {
    IExpr explicit = explicitStyle(index, plotStyle);
    if (explicit.isPresent()) {
      return explicit;
    }
    double[] rgb = SURFACE_COLORS[Math.floorMod(index, SURFACE_COLORS.length)];
    return F.RGBColor(rgb[0], rgb[1], rgb[2]);
  }

  /**
   * The colour of curve or point number {@code index}.
   *
   * <p>
   * A line or a scatter of points follows the ordinary plot cycle, which starts at blue; only a
   * surface starts at gold.
   */
  public static IExpr curveStyle(int index, IExpr plotStyle) {
    IExpr explicit = explicitStyle(index, plotStyle);
    return explicit.isPresent() ? explicit : GraphicsOptions.plotStyleColorExpr(index, F.NIL);
  }

  /** The style the user asked for, or {@link F#NIL} when they left it automatic. */
  private static IExpr explicitStyle(int index, IExpr plotStyle) {
    if (plotStyle == null || plotStyle.isAutomatic()) {
      return F.NIL;
    }
    if (plotStyle.isList() && ((IAST) plotStyle).argSize() >= 1) {
      IAST styles = (IAST) plotStyle;
      return styles.get(Math.floorMod(index, styles.argSize()) + 1);
    }
    if (plotStyle.isAST() || plotStyle.isBuiltInSymbol()) {
      return plotStyle;
    }
    return F.NIL;
  }

  /**
   * The colour of chart element number {@code index}: a stem, a point marker, or a bar edge.
   *
   * <p>
   * Darkens the palette entry slightly for the parts of a chart that are drawn solid, and uses
   * {@link #chartFaceStyle} for the translucent body of a bar, so a bar reads as a tinted volume
   * with a firmer outline rather than as a block of flat colour.
   */
  public static IExpr chartStyle(int index, IExpr plotStyle) {
    IExpr explicit = explicitStyle(index, plotStyle);
    if (explicit.isPresent()) {
      return explicit;
    }
    return darker(
        GraphicsOptions.PLOT_COLORS[Math.floorMod(index, GraphicsOptions.PLOT_COLORS.length)],
        CHART_DARKEN);
  }

  /** The translucent face of a bar, lighter than {@link #chartStyle} and half transparent. */
  public static IExpr chartFaceStyle(int index, IExpr plotStyle) {
    IExpr base = chartStyle(index, plotStyle);
    if (!base.isAST(S.RGBColor, 4)) {
      return F.Directive(base, F.Opacity(F.num(CHART_FACE_OPACITY)));
    }
    IAST color = (IAST) base;
    IAST lightened = F.RGBColor(lighten(color.arg1().evalfNaN()), lighten(color.arg2().evalfNaN()),
        lighten(color.arg3().evalfNaN()));
    return F.Directive(lightened, F.Opacity(F.num(CHART_FACE_OPACITY)));
  }

  private static double lighten(double channel) {
    return channel + CHART_FACE_LIGHTEN * (1.0 - channel);
  }

  private static IAST darker(org.matheclipse.core.convert.RGBColor color, double fraction) {
    float[] rgb = color.getRGBColorComponents(null);
    double scale = 1.0 - fraction;
    return F.RGBColor(rgb[0] * scale, rgb[1] * scale, rgb[2] * scale);
  }

  /**
   * Install the surface style and the mesh directive on a builder.
   *
   * <p>
   * The mesh decision is made here rather than left to the renderer's default, so that what the
   * expression says is what gets drawn: a {@code Graphics3D} the user assembled by hand keeps
   * edgeless polygons, and a plot that wants a mesh says so with an explicit {@code EdgeForm}.
   */
  public static void applyStyle(GraphicsComplexBuilder builder, IExpr style, IExpr meshOption) {
    // Mesh lines are drawn as their own lines along the grid, not as an edge on every quad, so
    // the polygons themselves never carry edges. Outlining each quad puts one line per sample,
    // which at the default sampling covers the surface in a dark grid; draws about
    // fifteen lines each way whatever the sampling is.
    builder.setStyle(style, F.EdgeForm(S.None));
  }

  /** Whether a {@code Mesh} option value asks for mesh lines. */
  public static boolean showMesh(IExpr meshOption) {
    if (meshOption == null) {
      return true;
    }
    if (meshOption.isFalse() || meshOption.isNone()) {
      return false;
    }
    if (meshOption.isInteger()) {
      return meshOption.toIntDefault(0) > 0;
    }
    return true;
  }

  /** The number of mesh lines drawn in each direction when {@code Mesh} is automatic. */
  private static final int AUTOMATIC_MESH_LINES = 15;

  /**
   * How many sample steps lie between two mesh lines.
   *
   * <p>
   * {@code Mesh -> n} asks for n lines, {@code Mesh -> All} for one at every sample, and
   * {@code Automatic} for fifteen. Deriving the spacing from the request rather than from the
   * sampling is what keeps the mesh looking the same when {@code PlotPoints} changes.
   */
  public static int meshStride(IExpr meshOption, int samples) {
    if (meshOption == null || samples < 3) {
      return 1;
    }
    if (meshOption.isSymbol() && "All".equals(meshOption.toString())) {
      return 1;
    }
    int lines = AUTOMATIC_MESH_LINES;
    if (meshOption.isInteger()) {
      lines = meshOption.toIntDefault(AUTOMATIC_MESH_LINES);
    }
    if (lines <= 0) {
      return 1;
    }
    return Math.max(1, (int) Math.round((samples - 1.0) / lines));
  }

  /**
   * The bounding box of a sampled grid, as {@code {xMin, xMax, yMin, yMax, zMin, zMax}}.
   *
   * <p>
   * This is the range a colour function's coordinates are scaled over: the extent the surface
   * actually reaches, rather than the box the plot happens to be drawn in.
   *
   * @param grid {@code [i][j]} coordinate triples, {@code null} where there is no sample
   */
  public static double[] extentOf(double[][][] grid) {
    double[] bounds = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE,
        Double.MAX_VALUE, -Double.MAX_VALUE};
    for (double[][] row : grid) {
      for (double[] p : row) {
        if (p == null) {
          continue;
        }
        for (int c = 0; c < 3; c++) {
          bounds[c * 2] = Math.min(bounds[c * 2], p[c]);
          bounds[c * 2 + 1] = Math.max(bounds[c * 2 + 1], p[c]);
        }
      }
    }
    return bounds;
  }

  /**
   * The {@code ColorFunction} of a surface, or {@code null} when it keeps its own flat colour.
   *
   * <p>
   * The caller finishes the builder with the range each coordinate spans, so that a colour
   * function sees positions rather than raw units.
   *
   * @param family which tuple this plot hands over; a plain surface passes {@code x, y, z}
   */
  public static PlotColorFunction.Builder plotColors(PlotColorFunction.Family family,
      IExpr[] options, ISymbol plotSymbol, EvalEngine engine) {
    return PlotColorFunction
        .of(family, options[X_COLOR_FUNCTION], options[X_COLOR_FUNCTION_SCALING], plotSymbol,
            engine)
        .sink(PlotColorFunction.Sink.FLAT);
  }

  /**
   * Turn a sampled grid of points into a {@code GraphicsComplex} of quads.
   *
   * <p>
   * A row of the grid is one value of the first parameter. A {@code null} entry marks a sample that
   * could not be evaluated: the quads that touch it are left out, which leaves a hole with the
   * shape of the singularity rather than a wall stretched up to the top of the box.
   *
   * @param grid {@code [i][j]} coordinate triples, {@code null} where the function has no value
   * @param wrapU whether the first parameter closes on itself, as an angle does
   * @param wrapV whether the second parameter closes on itself
   * @param colors optional colour per grid point, indexed the same way as {@code grid}
   * @param smooth whether to compute vertex normals for smooth shading
   */
  public static void addSurface(GraphicsComplexBuilder builder, double[][][] grid, boolean wrapU,
      boolean wrapV, IExpr[][] colors, boolean smooth) {
    addSurface(builder, grid, wrapU, wrapV, colors, smooth, null, null);
  }

  /**
   * As {@link #addSurface}, additionally drawing mesh lines along the grid.
   *
   * @param meshOption the {@code Mesh} option, or {@code null} to draw none
   * @param meshStyle the {@code MeshStyle} option, or {@code null} for the default grey
   */
  public static void addSurface(GraphicsComplexBuilder builder, double[][][] grid, boolean wrapU,
      boolean wrapV, IExpr[][] colors, boolean smooth, IExpr meshOption, IExpr meshStyle) {
    addSurface(builder, grid, wrapU, wrapV, colors, smooth, meshOption, meshStyle, null, null,
        null);
  }

  /**
   * Where the edge of a region crosses a grid line.
   *
   * <p>
   * The plot knows how to place a point between two of its own samples; this hands that back to
   * {@link #addSurface} so that a cell the region only partly covers can be cut along the boundary
   * instead of being dropped whole.
   */
  @FunctionalInterface
  public interface RegionEdge {
    /**
     * The point where the region ends between the accepted node {@code (i1, j1)} and the rejected
     * node {@code (i2, j2)}, or {@code null} when it cannot be placed.
     */
    double[] crossing(int i1, int j1, int i2, int j2);
  }

  /**
   * As {@link #addSurface}, cutting the cells the edge of a region runs through.
   *
   * <p>
   * A masked grid can only drop whole cells, which leaves the edge of a region as a staircase with
   * one step per sample. The cells the boundary crosses are drawn here instead as the polygon that
   * survives it: the corners that are inside, and the points where the boundary meets the cell's
   * own edges. They are walked in the same order the quads above are, so the winding - and with it
   * which face the lights see - stays the same.
   *
   * @param unmasked the sampled points for every node the function has a value at, region or no
   *        region; {@code null} to do no clipping
   * @param inside which nodes belong to the region
   * @param edge where the boundary crosses a grid line
   */
  public static void addSurface(GraphicsComplexBuilder builder, double[][][] grid, boolean wrapU,
      boolean wrapV, IExpr[][] colors, boolean smooth, IExpr meshOption, IExpr meshStyle,
      double[][][] unmasked, boolean[][] inside, RegionEdge edge) {
    int rows = grid.length;
    if (rows == 0) {
      return;
    }
    int cols = grid[0].length;
    double[][][] normals = smooth ? vertexNormals(grid, wrapU, wrapV) : null;

    int[][] indices = new int[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        double[] p = grid[i][j];
        if (p == null) {
          indices[i][j] = -1;
          continue;
        }
        double[] normal = normals == null ? null : normals[i][j];
        IExpr color = colors == null ? null : colors[i][j];
        indices[i][j] = builder.addVertex(p[0], p[1], p[2], normal, color);
      }
    }

    int lastRow = wrapU ? rows : rows - 1;
    int lastCol = wrapV ? cols : cols - 1;
    for (int i = 0; i < lastRow; i++) {
      int i2 = (i + 1) % rows;
      for (int j = 0; j < lastCol; j++) {
        int j2 = (j + 1) % cols;
        int p1 = indices[i][j];
        int p2 = indices[i2][j];
        int p3 = indices[i2][j2];
        int p4 = indices[i][j2];
        if (p1 < 0 || p2 < 0 || p3 < 0 || p4 < 0) {
          continue;
        }
        // One winding for every 3D plot: counter-clockwise seen from increasing z, so that the
        // face the winding calls the front is the one the vertex normals point out of. When the
        // two disagree the renderer treats every visible fragment as a back face and flips the
        // normal it was given, which leaves the whole surface facing away from the lights.
        builder.addPolygon(new int[] {p1, p2, p3, p4});
      }
    }

    // Every grid line the region boundary crosses gets one vertex, shared by the two cells that
    // meet along it and by the mesh line that runs down it. Placing it once is what keeps the cut
    // cells from cracking apart and the mesh from stopping a sample short of the edge.
    int[][][] crossings = unmasked != null && inside != null && edge != null
        ? regionCrossings(builder, unmasked, inside, normals, colors, edge, rows, cols)
        : null;
    if (crossings != null) {
      addRegionEdgeCells(builder, unmasked, inside, indices, crossings, rows, cols);
    }

    if (showMesh(meshOption)) {
      addMeshLines(builder, indices, crossings, rows, cols, wrapU, wrapV, meshOption, meshStyle);
    }
  }

  /** A surface sampled at arbitrary parameter values, for placing a region boundary on it. */
  public interface SurfaceSampler {
    /** The point at the given parameters, or {@code null} when the surface has none there. */
    double[] point(double u, double v);

    /** Whether that point belongs to the region. */
    boolean inside(double[] point, double u, double v);
  }

  /**
   * A {@link RegionEdge} that places the boundary by halving the parameters, not the coordinates.
   *
   * <p>
   * A parametric surface can fold over itself, so a straight line between two of its points is not
   * generally on the surface at all. Between two neighbouring samples the parameters, on the other
   * hand, run straight by construction, so bisecting there keeps every trial point on the surface.
   * The predicate answers yes or no and nothing else, which is why this is a bisection rather than
   * an interpolation.
   *
   * @param sampler evaluates the surface and asks the region about it
   * @param u0 the first parameter at index zero, and {@code uStep} the distance between samples
   */
  public static RegionEdge parameterEdge(SurfaceSampler sampler, double u0, double uStep, double v0,
      double vStep) {
    return (i1, j1, i2, j2) -> {
      double au = u0 + i1 * uStep;
      double av = v0 + j1 * vStep;
      double bu = u0 + i2 * uStep;
      double bv = v0 + j2 * vStep;
      double[] best = sampler.point(au, av);
      if (best == null) {
        return null;
      }
      double lo = 0.0;
      double hi = 1.0;
      for (int k = 0; k < RegionClip.CROSSING_ITERATIONS; k++) {
        double mid = (lo + hi) / 2.0;
        double mu = au + mid * (bu - au);
        double mv = av + mid * (bv - av);
        double[] point = sampler.point(mu, mv);
        if (point != null && sampler.inside(point, mu, mv)) {
          lo = mid;
          best = point;
        } else {
          hi = mid;
        }
      }
      return best;
    };
  }

  /**
   * One vertex per grid line the region boundary crosses.
   *
   * @return {@code {alongU, alongV}}, where {@code alongU[i][j]} is the vertex on the line from
   *         node {@code (i, j)} to {@code (i + 1, j)} and {@code alongV[i][j]} the one from
   *         {@code (i, j)} to {@code (i, j + 1)}, each {@code -1} where the boundary does not cross
   */
  private static int[][][] regionCrossings(GraphicsComplexBuilder builder, double[][][] unmasked,
      boolean[][] inside, double[][][] normals, IExpr[][] colors, RegionEdge edge, int rows,
      int cols) {
    int[][] alongU = new int[rows][cols];
    int[][] alongV = new int[rows][cols];
    for (int[] row : alongU) {
      java.util.Arrays.fill(row, -1);
    }
    for (int[] row : alongV) {
      java.util.Arrays.fill(row, -1);
    }
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (i + 1 < rows) {
          alongU[i][j] =
              crossingVertex(builder, unmasked, inside, normals, colors, edge, i, j, i + 1, j);
        }
        if (j + 1 < cols) {
          alongV[i][j] =
              crossingVertex(builder, unmasked, inside, normals, colors, edge, i, j, i, j + 1);
        }
      }
    }
    return new int[][][] {alongU, alongV};
  }

  /**
   * The vertex where the boundary crosses one grid line, or {@code -1}.
   *
   * <p>
   * The normal and the colour come from the sample the crossing was walked away from, so the strip
   * along the edge is lit and coloured like the surface it belongs to.
   */
  private static int crossingVertex(GraphicsComplexBuilder builder, double[][][] unmasked,
      boolean[][] inside, double[][][] normals, IExpr[][] colors, RegionEdge edge, int i1, int j1,
      int i2, int j2) {
    if (inside[i1][j1] == inside[i2][j2] || unmasked[i1][j1] == null || unmasked[i2][j2] == null) {
      return -1;
    }
    int fi = inside[i1][j1] ? i1 : i2;
    int fj = inside[i1][j1] ? j1 : j2;
    int ti = inside[i1][j1] ? i2 : i1;
    int tj = inside[i1][j1] ? j2 : j1;
    double[] point = edge.crossing(fi, fj, ti, tj);
    if (point == null) {
      return -1;
    }
    return builder.addVertex(point[0], point[1], point[2],
        normals == null ? null : normals[fi][fj], colors == null ? null : colors[fi][fj]);
  }

  /**
   * Draws the part of each straddled cell that lies inside the region.
   *
   * <p>
   * The corners that are inside and the points where the boundary meets the cell's own edges,
   * walked in the order the quads are, so the winding - and with it which face the lights see -
   * stays the same. A cell whose inside corners are diagonally opposite is crossed twice and no
   * single polygon describes it, so it is left out the way it was before there was any clipping.
   * At any density worth plotting at those cells are rare and each is one sample across.
   */
  private static void addRegionEdgeCells(GraphicsComplexBuilder builder, double[][][] unmasked,
      boolean[][] inside, int[][] indices, int[][][] crossings, int rows, int cols) {
    int[][] alongU = crossings[0];
    int[][] alongV = crossings[1];
    for (int i = 0; i < rows - 1; i++) {
      for (int j = 0; j < cols - 1; j++) {
        int[] ci = {i, i + 1, i + 1, i};
        int[] cj = {j, j, j + 1, j + 1};
        boolean complete = true;
        boolean mixed = false;
        for (int k = 0; k < 4; k++) {
          if (unmasked[ci[k]][cj[k]] == null) {
            complete = false;
            break;
          }
          mixed |= inside[ci[k]][cj[k]] != inside[ci[0]][cj[0]];
        }
        if (!complete || !mixed) {
          continue;
        }
        if (inside[i][j] == inside[i + 1][j + 1] && inside[i + 1][j] == inside[i][j + 1]) {
          continue; // the two inside corners are diagonally opposite
        }
        // the crossing on each of the four cell edges, in the order they are walked
        int[] onEdge = {alongU[i][j], alongV[i + 1][j], alongU[i][j + 1], alongV[i][j]};
        int[] polygon = new int[8];
        int count = 0;
        boolean known = true;
        for (int k = 0; k < 4; k++) {
          int next = (k + 1) % 4;
          if (inside[ci[k]][cj[k]]) {
            known &= indices[ci[k]][cj[k]] >= 0;
            polygon[count++] = indices[ci[k]][cj[k]];
          }
          if (inside[ci[k]][cj[k]] != inside[ci[next]][cj[next]]) {
            known &= onEdge[k] >= 0;
            polygon[count++] = onEdge[k];
          }
        }
        if (!known || count < 3) {
          continue;
        }
        int[] face = new int[count];
        System.arraycopy(polygon, 0, face, 0, count);
        builder.addPolygon(face);
      }
    }
  }

  /**
   * Mesh lines along the grid, spaced by {@link #meshStride}.
   *
   * <p>
   * The last line in each direction is always drawn, so the surface is bounded on all four sides
   * however the spacing divides the sampling.
   */
  private static void addMeshLines(GraphicsComplexBuilder builder, int[][] indices,
      int[][][] crossings, int rows, int cols, boolean wrapU, boolean wrapV, IExpr meshOption,
      IExpr meshStyle) {
    int strideU = meshStride(meshOption, rows);
    int strideV = meshStride(meshOption, cols);
    builder.addPrimitive(
        meshStyle != null && !meshStyle.isAutomatic() && !meshStyle.isNone() ? meshStyle
            : MESH_STYLE);

    for (int i = 0; i < rows; i++) {
      if (i % strideU != 0 && i != rows - 1) {
        continue;
      }
      addRun(builder, indices[i], crossings == null ? null : crossings[1][i], cols, wrapV);
    }
    for (int j = 0; j < cols; j++) {
      if (j % strideV != 0 && j != cols - 1) {
        continue;
      }
      int[] column = new int[rows];
      int[] columnCrossings = crossings == null ? null : new int[rows];
      for (int i = 0; i < rows; i++) {
        column[i] = indices[i][j];
        if (columnCrossings != null) {
          columnCrossings[i] = crossings[0][i][j];
        }
      }
      addRun(builder, column, columnCrossings, rows, wrapU);
    }
  }

  /** One mesh line, broken wherever the surface has a hole. */
  /**
   * One mesh line along a row or a column of the grid, broken wherever the surface is.
   *
   * <p>
   * {@code crossings[k]} is the vertex where the edge of a region cuts the grid line between
   * {@code k} and {@code k + 1}, or {@code -1}. Running out to it rather than stopping at the last
   * sample is what keeps the mesh from ending a cell short of a surface that was cut to the region.
   */
  private static void addRun(GraphicsComplexBuilder builder, int[] line, int[] crossings, int count,
      boolean wrap) {
    IASTAppendable current = F.ListAlloc(count);
    for (int k = 0; k <= count; k++) {
      int index = k == count ? (wrap ? line[0] : -1) : line[k];
      if (index >= 0) {
        if (current.argSize() == 0 && crossings != null && k > 0 && line[k - 1] < 0
            && crossings[k - 1] >= 0) {
          current.append(F.ZZ(crossings[k - 1]));
        }
        current.append(F.ZZ(index));
      } else {
        if (crossings != null && k > 0 && line[k - 1] >= 0 && crossings[k - 1] >= 0) {
          current.append(F.ZZ(crossings[k - 1]));
        }
        if (current.argSize() >= 2) {
          builder.addPrimitive(F.Line(current));
        }
        current = F.ListAlloc(count);
      }
      if (k == count) {
        break;
      }
    }
    if (current.argSize() >= 2) {
      builder.addPrimitive(F.Line(current));
    }
  }

  /**
   * Vertex normals, averaged from the four quads that meet at each grid point.
   *
   * <p>
   * Without these the renderer has to infer normals from the triangles, which makes a coarsely
   * sampled surface look faceted; supplying them is what lets a 20 by 20 {@code Plot3D}.
   */
  private static double[][][] vertexNormals(double[][][] grid, boolean wrapU, boolean wrapV) {
    int rows = grid.length;
    int cols = grid[0].length;
    double[][][] normals = new double[rows][cols][];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        double[] center = grid[i][j];
        if (center == null) {
          continue;
        }
        double[] du = difference(grid, i, j, 1, 0, rows, cols, wrapU, wrapV);
        double[] dv = difference(grid, i, j, 0, 1, rows, cols, wrapU, wrapV);
        if (du == null || dv == null) {
          continue;
        }
        // The normal is the cross product in the same order the quads are wound, so the two agree
        // by construction. Forcing it towards increasing z instead would be right for a height
        // field and wrong for anything that closes on itself: on the underside of a sphere the
        // outward normal points down, and flipping it there left those faces unlit.
        double[] n = cross(du, dv);
        double length = Math.sqrt(n[0] * n[0] + n[1] * n[1] + n[2] * n[2]);
        if (!(length > 1e-12)) {
          continue;
        }
        normals[i][j] = new double[] {n[0] / length, n[1] / length, n[2] / length};
      }
    }
    return normals;
  }

  /** A central difference along one grid direction, falling back to a one sided one at an edge. */
  private static double[] difference(double[][][] grid, int i, int j, int di, int dj, int rows,
      int cols, boolean wrapU, boolean wrapV) {
    double[] forward = at(grid, i + di, j + dj, rows, cols, wrapU, wrapV);
    double[] backward = at(grid, i - di, j - dj, rows, cols, wrapU, wrapV);
    double[] center = grid[i][j];
    if (forward != null && backward != null) {
      return subtract(forward, backward);
    }
    if (forward != null) {
      return subtract(forward, center);
    }
    if (backward != null) {
      return subtract(center, backward);
    }
    return null;
  }

  private static double[] at(double[][][] grid, int i, int j, int rows, int cols, boolean wrapU,
      boolean wrapV) {
    if (i < 0 || i >= rows) {
      if (!wrapU) {
        return null;
      }
      i = Math.floorMod(i, rows);
    }
    if (j < 0 || j >= cols) {
      if (!wrapV) {
        return null;
      }
      j = Math.floorMod(j, cols);
    }
    return grid[i][j];
  }

  private static double[] subtract(double[] a, double[] b) {
    return new double[] {a[0] - b[0], a[1] - b[1], a[2] - b[2]};
  }

  private static double[] cross(double[] a, double[] b) {
    return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]};
  }

  /**
   * Assemble the {@code Graphics3D} a plot returns.
   *
   * <p>
   * Options the user gave are copied on first, so that {@code PlotRange -> {0, 1}} beats the
   * default the plot would otherwise supply; the reader of an option list takes the first rule it
   * finds for a name.
   */
  public static IExpr graphics3D(IExpr content, IAST originalAST, int argSize, IExpr[] defaults) {
    return graphics3D(content, originalAST, argSize, defaults, true);
  }

  /**
   * The same, for a plot that has already read the wrapper on its first argument.
   *
   * <p>
   * Every three dimensional plot ends here, so this is the one place a {@code Tooltip} written
   * around a whole surface or dataset needs to be understood - a plot that gives no thought to
   * wrappers still supports one at that level. A plot which distributes the label over its own
   * curves has to say so, or the label it put on each would be overridden by the one put around
   * them all.
   *
   * @param applyArgumentWrapper whether to read a display wrapper off {@code originalAST}'s first
   *        argument and label the primitives with it
   */
  public static IExpr graphics3D(IExpr content, IAST originalAST, int argSize, IExpr[] defaults,
      boolean applyArgumentWrapper) {
    return graphics3D(content, originalAST, argSize, defaults, applyArgumentWrapper, null);
  }

  /**
   * The same, for a plot that can say what its axes are called.
   *
   * @param autoAxesLabels the three names {@code AxesLabel -> Automatic} resolves to, any of them
   *        {@link F#NIL} to leave that axis unlabelled, or {@code null} for a plot that cannot say
   */
  public static IExpr graphics3D(IExpr content, IAST originalAST, int argSize, IExpr[] defaults,
      boolean applyArgumentWrapper, IExpr[] autoAxesLabels) {
    IASTAppendable result = F.ast(S.Graphics3D, 4 + (defaults == null ? 0 : defaults.length));
    if (applyArgumentWrapper && originalAST != null && originalAST.size() > 1) {
      content = PlotWrapper.of(originalAST.arg1()).wrapTooltip(content);
    }
    result.append(content);
    forwardOptions(result, originalAST, argSize, autoAxesLabels);
    if (defaults != null) {
      for (IExpr rule : defaults) {
        if (rule != null && rule.isRuleAST() && !hasOption(result, ((IAST) rule).arg1())) {
          result.append(rule);
        }
      }
    }
    return legended(result, originalAST, argSize);
  }

  /**
   * Wraps the graphic in {@code Legended} when the call asked for a legend.
   *
   * <p>
   * This is the shape returned, and both renderers already read it: the scene builder takes the
   * legend out of the wrapper and the graphic out from under it, so the picture is the same either
   * way and the legend text travels with it.
   *
   * <p>
   * The option is read from the original call rather than from the parsed option array because
   * every plot passes through here, and what decides the wrapper is whether the user wrote the
   * option at all.
   */
  private static IExpr legended(IExpr graphic, IAST originalAST, int argSize) {
    if (originalAST == null) {
      return graphic;
    }
    for (int i = argSize + 1; i <= originalAST.argSize(); i++) {
      IExpr arg = originalAST.get(i);
      if (arg.isRuleAST() && ((IAST) arg).arg1() == S.PlotLegends) {
        IExpr legends = ((IAST) arg).arg2();
        if (!legends.isNone()) {
          return F.Legended(graphic, legends);
        }
      }
    }
    return graphic;
  }

  /**
   * Runs an {@code EvaluationMonitor} once, at a point where the plotted function was sampled.
   *
   * <p>
   * The option is written with {@code :>} so the expression arrives unevaluated and can count the
   * samples, which is what it is nearly always used for.
   */
  public static void monitor(IExpr evaluationMonitor, EvalEngine engine) {
    if (evaluationMonitor != null && evaluationMonitor.isPresent() && !evaluationMonitor.isNone()) {
      engine.evaluate(evaluationMonitor);
    }
  }

  /**
   * Copy the {@code Graphics3D} options out of the original call.
   *
   * <p>
   * Only the ones the renderer understands are copied. Passing everything through would put a plot
   * option such as {@code PlotPoints} into the graphic, where a later reader would find a rule it
   * has no meaning for.
   */
  private static void forwardOptions(IASTAppendable result, IAST originalAST, int argSize,
      IExpr[] autoAxesLabels) {
    if (originalAST == null) {
      return;
    }
    for (int i = argSize + 1; i <= originalAST.argSize(); i++) {
      IExpr arg = originalAST.get(i);
      if (!arg.isRuleAST()) {
        continue;
      }
      IExpr key = ((IAST) arg).arg1();
      if (isGraphics3DOption(key)) {
        result.append(resolveAxesLabel(key, (IAST) arg, autoAxesLabels));
      }
    }
  }

  /**
   * {@code AxesLabel -> Automatic} as the names the plot actually used.
   *
   * <p>
   * This has to happen while the user's rules are being copied rather than through the defaults
   * array: a rule the user wrote is copied first, and {@link #hasOption} then refuses any default
   * for the same name, so a resolved label appended later would never be reached.
   *
   * <p>
   * Reading the option array instead would be wrong for a subtler reason - {@code base3D()}
   * registers {@code AxesLabel} with a default of {@code Automatic}, so every three dimensional
   * plot would look like it had asked for labels and every one of them would get them. Only a rule
   * the caller actually wrote passes through here.
   */
  private static IExpr resolveAxesLabel(IExpr key, IAST rule, IExpr[] autoAxesLabels) {
    if (key != S.AxesLabel || autoAxesLabels == null || !rule.arg2().isAutomatic()) {
      return rule;
    }
    IASTAppendable labels = F.ListAlloc(3);
    for (int axis = 0; axis < 3; axis++) {
      IExpr label = axis < autoAxesLabels.length ? autoAxesLabels[axis] : F.NIL;
      // None leaves that axis alone, which is what an unnamable one needs
      labels.append(label != null && label.isPresent() ? label : S.None);
    }
    return F.Rule(S.AxesLabel, labels);
  }

  private static boolean hasOption(IAST ast, IExpr name) {
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && ((IAST) arg).arg1() == name) {
        return true;
      }
    }
    return false;
  }

  /** The options a {@code Graphics3D} carries, as opposed to the ones a plot consumes itself. */
  private static boolean isGraphics3DOption(IExpr key) {
    return key == S.Axes || key == S.AxesLabel || key == S.AxesEdge || key == S.AxesStyle
        || key == S.Background || key == S.Boxed || key == S.BoxStyle || key == S.BoxRatios
        || key == S.FaceGrids || key == S.ImageSize || key == S.Lighting || key == S.PlotLabel
        || key == S.PlotRange || key == S.PlotRangePadding || key == S.Ticks
        || key == S.TicksStyle || key == S.LabelStyle
        || key == S.ViewPoint || key == S.ViewVertical || key == S.ViewAngle || key == S.ViewCenter
        || key == S.ViewProjection || key == S.ViewRange || key == S.SphericalRegion
        || key == S.ScalingFunctions;
  }
}
