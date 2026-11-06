package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>VectorPlot[{vx, vy}, {x, xmin, xmax}, {y, ymin, ymax}]</code> and
 * <code>VectorPlot3D[{vx, vy, vz}, {x, ...}, {y, ...}, {z, ...}]</code> - the vector field drawn as
 * arrows.
 *
 * <p>
 * In the plane the arrows sit on a staggered (hexagonal) lattice of <code>VectorPoints -> n</code>
 * points along the longer side, 16 by default, and all have the same length on the screen unless
 * <code>VectorScaling -> Automatic</code> makes them grow with the field's strength. In space they
 * sit on a regular grid and the longest one spans <code>VectorScale</code> of the grid spacing, so
 * the arrows never overlap. They are centred on their points and coloured by the field's strength
 * unless <code>VectorColorFunction -> None</code>. Points where the field has no numeric value, and
 * points where it is zero, get no arrow.
 *
 * <p>
 * Options: <code>VectorPoints -> n | {nx, ny[, nz]} | {p1, p2, ...}</code>, <code>VectorScale ->
 * s</code>, <code>VectorScaling -> Automatic</code>, <code>VectorColorFunction -> None</code>,
 * <code>VectorStyle -> style</code>; any other option is handed on to the
 * <code>Graphics</code>/<code>Graphics3D</code>. An explicit list of points puts one arrow at each
 * of them, and a colour in <code>VectorStyle</code> draws every arrow in it rather than colouring
 * them by their strength.
 */
public class VectorPlot extends AbstractFunctionEvaluator {

  /** How much of the grid spacing, or the spacing between points, the longest arrow spans. */
  static final double DEFAULT_SCALE = 0.9;

  /** 2 for VectorPlot, 3 for VectorPlot3D. */
  private final int dimension;

  public VectorPlot(int dimension) {
    this.dimension = dimension;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < dimension + 1) {
      return F.NIL;
    }
    if (PlotEndpoints.degenerate((ISymbol) ast.topHead(), ast, 2, dimension + 1, false, engine)) {
      return F.NIL;
    }
    IExpr field = ast.arg1();
    ISymbol[] variables = new ISymbol[dimension];
    double[] min = new double[dimension];
    double[] max = new double[dimension];
    for (int d = 0; d < dimension; d++) {
      IExpr iterator = ast.get(d + 2);
      if (!iterator.isList3() || !iterator.first().isSymbol()) {
        return F.NIL;
      }
      variables[d] = (ISymbol) iterator.first();
      try {
        min[d] = engine.evalDouble(((IAST) iterator).arg2());
        max[d] = engine.evalDouble(((IAST) iterator).arg3());
      } catch (RuntimeException rex) {
        return limitingValue((ISymbol) ast.topHead(), (IAST) iterator, engine);
      }
      if (!(max[d] > min[d])) {
        return F.NIL;
      }
    }

    int[] points = vectorPoints(ast, engine);
    double[][] explicitPoints = explicitVectorPoints(ast, engine);
    double scale = DEFAULT_SCALE;
    boolean colored = true;
    boolean colorFunctionGiven = false;
    // VectorScaling -> Automatic: the arrows' lengths grow with the field's strength
    boolean scaledLengths = false;
    IExpr style = F.NIL;
    IExpr colorFunctionOption = S.Automatic;
    IExpr colorFunctionScaling = S.True;
    RegionFunctionFilter region = null;
    IASTAppendable graphicsOptions = F.ListAlloc();
    // CPD-OFF the vector plots handle different option keys
    for (int i = dimension + 2; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      IExpr key = option.first();
      if (key == S.VectorPoints) {
        continue;
      }
      if (key == S.VectorScale) {
        scale = VectorPlot.vectorScale(engine.evaluate(option.second()), scale);
        continue;
      }
      if (key == S.VectorColorFunction) {
        colored = !engine.evaluate(option.second()).isNone();
        // CPD-ON
        colorFunctionGiven = true;
        continue;
      }
      if (key == S.RegionFunction) {
        region = RegionFunctionFilter.of(engine.evaluate(option.second()), engine);
        continue;
      }
      if (key == S.VectorStyle) {
        style = engine.evaluate(option.second());
        continue;
      }
      if (dimension == 3 && (key == S.ColorFunction || key == S.ColorFunctionScaling)) {
        if (key == S.ColorFunction) {
          colorFunctionOption = engine.evaluate(option.second());
        } else {
          colorFunctionScaling = engine.evaluate(option.second());
        }
        continue;
      }
      if (key == S.VectorScaling) {
        scaledLengths = engine.evaluate(option.second()) == S.Automatic;
        continue;
      }
      graphicsOptions.append(option);
    }
    boolean styleColored = style.isPresent() && containsColor(style);
    if (styleColored && !colorFunctionGiven) {
      // the arrows are drawn in the colour asked for, not coloured by their length over it
      colored = false;
    }
    if (dimension == 2 && explicitPoints == null) {
      return planeGraphics(point -> fieldAt(field, variables, point, engine), min, max,
          planeVectorPoints(ast, 4, engine), scale, scaledLengths, !styleColored, style, colored,
          region, graphicsOptions, engine);
    }

    // in space a list of fields draws each of them, every one in a colour of its own
    IAST fields = dimension == 3 && isFieldList(field) ? (IAST) field : F.List(field);
    // sample the fields, on the grid or at the points asked for
    double[][] samples = explicitPoints != null ? explicitPoints : gridPoints(points, min, max);
    int perField = samples.length;
    int total = perField * fields.argSize();
    double[][] tails = new double[total][];
    double[][] vectors = new double[total][];
    double longest = 0.0;
    for (int k = 0; k < total; k++) {
      engine.checkTimeLimit();
      double[] point = samples[k % perField];
      double[] vector = fieldAt(fields.get(k / perField + 1), variables, point, engine);
      if (vector == null) {
        continue;
      }
      double length = norm(vector);
      if (length == 0.0 || Double.isNaN(length) || Double.isInfinite(length)) {
        continue;
      }
      if (region != null) {
        // the point, the vector and its length: x, y, vx, vy, norm
        double[] arguments = new double[2 * dimension + 1];
        System.arraycopy(point, 0, arguments, 0, dimension);
        System.arraycopy(vector, 0, arguments, dimension, dimension);
        arguments[2 * dimension] = length;
        if (!region.accepts(arguments)) {
          continue;
        }
      }
      tails[k] = point;
      vectors[k] = vector;
      longest = Math.max(longest, length);
    }

    // the longest arrow spans `scale` of the smallest grid spacing, or of the smallest distance
    // between two of the points asked for
    double spacing = explicitPoints != null ? smallestDistance(explicitPoints)
        : Double.MAX_VALUE;
    if (explicitPoints == null) {
      for (int d = 0; d < dimension; d++) {
        if (points[d] > 1) {
          spacing = Math.min(spacing, (max[d] - min[d]) / (points[d] - 1));
        }
      }
    }
    if (spacing == Double.MAX_VALUE) {
      spacing = max[0] - min[0];
    }
    double factor = longest > 0.0 ? scale * spacing / longest : 0.0;
    PlotColorFunction colorFunction = colored && fields.argSize() == 1
        ? vectorColors(colorFunctionOption, colorFunctionScaling, min, max, vectors, engine)
        : null;

    IASTAppendable primitives = F.ListAlloc();
    if (dimension == 2) {
      // the head's size is a share of the plot's width: 0.45 / 16 for the default 15 points
      primitives.append(arrowheads(0.45 / (points[0] + 1)));
    } else {
      primitives.append(arrowheads(ARROWHEAD_3D));
    }
    if (!colored && !styleColored) {
      primitives.append(plainColor());
    }
    if (style.isPresent()) {
      // after the defaults, so that its colour and its Arrowheads are the ones in force; a list of
      // directives is spread out, since one inside a sublist would not reach the arrows beside it
      if (style.isList()) {
        primitives.appendArgs((IAST) style);
      } else {
        primitives.append(style);
      }
    }
    for (int k = 0; k < total; k++) {
      if (vectors[k] == null) {
        continue;
      }
      double[] p = tails[k];
      double[] v = vectors[k];
      // CPD-OFF the vector plots handle different option keys
      IASTAppendable from = F.ListAlloc(dimension);
      IASTAppendable to = F.ListAlloc(dimension);
      for (int d = 0; d < dimension; d++) {
        from.append(F.num(p[d] - v[d] * factor / 2));
        to.append(F.num(p[d] + v[d] * factor / 2));
        // CPD-ON
      }
      IAST arrow = arrow(from, to, dimension);
      if (!colored) {
        primitives.append(arrow);
      } else if (fields.argSize() > 1) {
        primitives.append(F.list(fieldColor(k / perField), arrow));
      } else if (colorFunction != null) {
        primitives.append(F.list(
            engine.evaluate(F.N(colorFunction.color(p[0], p[1], p[2], v[0], v[1], v[2], norm(v)))),
            arrow));
      } else {
        primitives.append(F.list(color(norm(v) / longest), arrow));
      }
    }

    IASTAppendable range = F.ListAlloc(dimension);
    for (int d = 0; d < dimension; d++) {
      // in 3D the arrows at the edge reach half a spacing beyond it, and the box is kept that big
      double pad = dimension == 3 && explicitPoints == null && points[d] > 1
          ? (max[d] - min[d]) / (points[d] - 1) / 2
          : 0.0;
      range.append(F.list(F.num(min[d] - pad), F.num(max[d] + pad)));
    }
    IASTAppendable result = dimension == 3 ? F.Graphics3D(primitives) : F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!hasOption(graphicsOptions, S.PlotRange)) {
      result.append(F.Rule(S.PlotRange, range));
    }
    if (dimension == 2) {
      if (!hasOption(graphicsOptions, S.Frame)) {
        result.append(F.Rule(S.Frame, S.True));
      }
    } else {
      if (!hasOption(graphicsOptions, S.Axes)) {
        result.append(F.Rule(S.Axes, S.True));
      }
      if (!hasOption(graphicsOptions, S.BoxRatios)) {
        result.append(F.Rule(S.BoxRatios, F.list(F.C1, F.C1, F.C1)));
      }
    }
    return result;
  }

  /**
   * The sample points of a plane field for <code>VectorPoints</code>: <code>Automatic</code> is 16
   * points along the longer side of the plot, <code>Coarse</code> 10 and <code>Fine</code> 25;
   * <code>{nx, ny}</code> puts the larger count along its axis and spreads the smaller count of
   * rows across the other one.
   *
   * @return <code>{n, rows, alongX}</code>: <code>rows</code> is 0 for rows as far apart as in a
   *         hexagonal lattice, <code>alongX</code> is 1 or 0 for the axis of the <code>n</code>
   *         points and -1 for the longer side
   */
  static double[] planeVectorPoints(IAST ast, int start, EvalEngine engine) {
    double[] result = {16, 0, -1};
    for (int i = start; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST() || option.first() != S.VectorPoints) {
        continue;
      }
      IExpr value = engine.evaluate(option.second());
      String name = value.isString() || value.isSymbol() ? value.toString() : "";
      if (name.equalsIgnoreCase("Coarse")) {
        result[0] = 10;
      } else if (name.equalsIgnoreCase("Fine")) {
        result[0] = 25;
      } else if (value.isList2()) {
        int nx = value.first().toIntDefault();
        int ny = value.second().toIntDefault();
        if (nx >= 1 && ny >= 1) {
          result[0] = Math.max(nx, ny);
          result[1] = Math.min(nx, ny);
          result[2] = nx >= ny ? 1 : 0;
        }
      } else {
        int n = value.toIntDefault();
        if (n >= 1) {
          result[0] = n;
        }
      }
    }
    // some 50000 arrows are plenty
    result[0] = Math.min(result[0], 200);
    result[1] = Math.min(result[1], 250);
    return result;
  }

  /**
   * The <code>Graphics</code> of a plane field over the rectangle from <code>min</code> to
   * <code>max</code>, with the arrows of {@link #planeArrows}.
   *
   * @param field the vector at a point, or <code>null</code> where there is none
   * @param plain whether the arrows which aren't coloured get the plain colour
   * @param style the <code>VectorStyle</code>, or {@link F#NIL}
   */
  static IAST planeGraphics(java.util.function.Function<double[], double[]> field, double[] min,
      double[] max, double[] lattice, double scale, boolean scaledLengths, boolean plain,
      IExpr style, boolean colored, RegionFunctionFilter region, IAST graphicsOptions,
      EvalEngine engine) {
    IASTAppendable primitives = F.ListAlloc();
    if (!colored && plain) {
      primitives.append(plainColor());
    }
    if (style.isPresent()) {
      if (style.isList()) {
        primitives.appendArgs((IAST) style);
      } else {
        primitives.append(style);
      }
    }
    // Arrowheads in the VectorStyle are the ones in force, so the arrows get none of their own
    boolean ownArrowheads =
        !style.isPresent() || style.isFree(x -> x.isAST(S.Arrowheads), false);
    planeArrows(field, min, max, lattice, scale, scaledLengths, colored, ownArrowheads, region,
        graphicsOptions, primitives, engine);
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!hasOption(graphicsOptions, S.PlotRange)) {
      result.append(F.Rule(S.PlotRange,
          F.list(F.list(F.num(min[0]), F.num(max[0])), F.list(F.num(min[1]), F.num(max[1])))));
    }
    if (!hasOption(graphicsOptions, S.Frame)) {
      result.append(F.Rule(S.Frame, S.True));
    }
    if (!hasOption(graphicsOptions, S.AspectRatio)) {
      result.append(F.Rule(S.AspectRatio, F.C1));
    }
    return result;
  }

  /**
   * The arrows of a plane field, appended to <code>primitives</code>.
   *
   * <p>
   * The points form a staggered (hexagonal) lattice along the longer side of the square plot: rows
   * of <code>n</code> points at the cell centres alternate with rows of <code>n - 1</code> at the
   * cell edges, beginning with a full row. Without a row count the rows are <code>Sqrt[3]/2</code>
   * cells apart and centred across the plot. Every arrow is <code>scale / Max[n, 12]</code> of the
   * plot's side long on the screen - with <code>VectorScaling -> Automatic</code> between a third
   * of that and all of it, by the field's strength - centred on its point and pointing along the
   * vector as the axes show it, and it carries its head of half its length and its colour.
   */
  private static void planeArrows(java.util.function.Function<double[], double[]> field,
      double[] min, double[] max, double[] lattice, double scale, boolean scaledLengths,
      boolean colored, boolean ownArrowheads, RegionFunctionFilter region, IAST graphicsOptions,
      IASTAppendable primitives, EvalEngine engine) {
    double width = max[0] - min[0];
    double height = max[1] - min[1];
    // the plot's sides on the screen: square unless an AspectRatio is given
    double screenWidth = 1.0;
    double screenHeight = 1.0;
    for (IExpr option : graphicsOptions) {
      if (option.isRuleAST() && option.first() == S.AspectRatio) {
        double ratio = option.second().evalfNaN();
        if (ratio > 0 && Double.isFinite(ratio)) {
          screenHeight = ratio;
        } else if (option.second() == S.Automatic) {
          screenHeight = height / width;
        }
      }
    }
    int n = (int) lattice[0];
    int rowCount = (int) lattice[1];
    boolean alongX = lattice[2] < 0 ? screenWidth >= screenHeight : lattice[2] > 0;
    double longSide = Math.max(screenWidth, screenHeight);
    double shortSide = Math.min(screenWidth, screenHeight);
    double[] rows;
    if (rowCount == 0) {
      double step = longSide / n * Math.sqrt(3.0) / 2.0 / shortSide;
      int count = Math.max(1, (int) Math.ceil(1.0 / step - 1e-9));
      double first = (1.0 - (count - 1) * step) / 2.0;
      rows = new double[count];
      for (int j = 0; j < count; j++) {
        rows[j] = first + j * step;
      }
    } else {
      double edge = 0.5 / n;
      rows = new double[rowCount];
      for (int j = 0; j < rowCount; j++) {
        rows[j] = rowCount == 1 ? 0.5 : edge + j * (1.0 - 2.0 * edge) / (rowCount - 1);
      }
    }

    // the field at the points, as {x, y, vx, vy, norm}
    java.util.List<double[]> samples = new java.util.ArrayList<>();
    double low = Double.MAX_VALUE;
    double high = -Double.MAX_VALUE;
    for (int j = 0; j < rows.length; j++) {
      engine.checkTimeLimit();
      int from = j % 2 == 0 ? 0 : 1;
      for (int i = from; i < n; i++) {
        double along = j % 2 == 0 ? (i + 0.5) / n : (double) i / n;
        double u = alongX ? along : rows[j];
        double v = alongX ? rows[j] : along;
        double[] point = {min[0] + u * width, min[1] + v * height};
        double[] vector = field.apply(point);
        if (vector == null) {
          continue;
        }
        double length = norm(vector);
        if (!Double.isFinite(length)) {
          continue;
        }
        double[] sample = {point[0], point[1], vector[0], vector[1], length};
        if (region != null && !region.accepts(sample)) {
          continue;
        }
        samples.add(sample);
        low = Math.min(low, length);
        high = Math.max(high, length);
      }
    }

    double arrowLength = scale / Math.max(n, 12) * longSide;
    // CPD-OFF the vector plots handle different option keys
    for (double[] sample : samples) {
      double strength = high > low ? (sample[4] - low) / (high - low) : 0.0;
      double sx = sample[2] * screenWidth / width;
      double sy = sample[3] * screenHeight / height;
      double screen = Math.hypot(sx, sy);
      if (sample[4] == 0.0 || !(screen > 0.0) || Double.isInfinite(screen)) {
        // a zero vector has no direction and gets no arrow
        continue;
      }
      double length = arrowLength;
      if (scaledLengths) {
        length *= 1.0 / 3.0 + 2.0 / 3.0 * (Math.sqrt(1.0 + 80.0 * strength) - 1.0) / 8.0;
      }
      double half = length / 2.0 / screen;
      double dx = sample[2] * half;
      double dy = sample[3] * half;
      IAST arrow = F.Arrow(F.list(F.list(F.num(sample[0] - dx), F.num(sample[1] - dy)),
          F.list(F.num(sample[0] + dx), F.num(sample[1] + dy))));
      // CPD-ON
      IAST drawn = colored ? F.list(color(strength), arrow) : arrow;
      primitives.append(
          ownArrowheads ? F.list(arrowheads(length / 2.0 / screenWidth), drawn) : drawn);
    }
  }

  /**
   * The message for a range with a bound which is no number, as the bound <code>x</code> of
   * <code>{y, 0, x}</code>: a field plot takes no range that depends on another variable.
   *
   * @return {@link F#NIL}
   */
  static IExpr limitingValue(ISymbol head, IAST iterator, EvalEngine engine) {
    IExpr limit = iterator.arg2().isNumericFunction(true) ? iterator.arg3() : iterator.arg2();
    // Limiting value `1` in `2` is not a machine-size real number.
    return Errors.printMessage(head, "plln", F.list(limit, iterator), engine);
  }

  /** The size of a three dimensional arrow's head. */
  static final double ARROWHEAD_3D = 0.045;

  /** The radius of a three dimensional arrow's tube. */
  static final double TUBE_RADIUS_3D = 0.015;

  /**
   * One arrow: <code>Arrow[{from, to}]</code> in the plane, <code>Arrow[Tube[{from, to}, r]]</code>
   * in space draws a three dimensional field.
   */
  static IAST arrow(IAST from, IAST to, int dimension) {
    return dimension == 3 ? F.Arrow(F.binaryAST2(S.Tube, F.list(from, to), F.num(TUBE_RADIUS_3D)))
        : F.Arrow(F.list(from, to));
  }

  /** The number of grid points along each axis. */
  private int[] vectorPoints(IAST ast, EvalEngine engine) {
    int[] points = new int[dimension];
    java.util.Arrays.fill(points, dimension == 3 ? 6 : 15);
    for (int i = dimension + 2; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (option.isRuleAST() && option.first() == S.VectorPoints) {
        IExpr value = engine.evaluate(option.second());
        if (value.isList() && value.argSize() == dimension) {
          for (int d = 0; d < dimension; d++) {
            int n = value.getAt(d + 1).toIntDefault();
            if (n >= 1) {
              points[d] = n;
            }
          }
        } else {
          int n = value.toIntDefault();
          if (n >= 1) {
            java.util.Arrays.fill(points, n);
          }
        }
      }
    }
    return points;
  }

  /** The points of the regular grid, the last coordinate running fastest. */
  private double[][] gridPoints(int[] points, double[] min, double[] max) {
    int total = 1;
    for (int n : points) {
      total *= n;
    }
    double[][] result = new double[total][];
    int[] index = new int[dimension];
    for (int k = 0; k < total; k++) {
      int rest = k;
      double[] point = new double[dimension];
      for (int d = dimension - 1; d >= 0; d--) {
        index[d] = rest % points[d];
        rest /= points[d];
        point[d] = points[d] == 1 ? (min[d] + max[d]) / 2
            : min[d] + (max[d] - min[d]) * index[d] / (points[d] - 1);
      }
      result[k] = point;
    }
    return result;
  }

  /**
   * The points of <code>VectorPoints -> {p1, p2, ...}</code>, or <code>null</code> when the option
   * asks for a grid - a number, or one number per axis - or is not given.
   */
  private double[][] explicitVectorPoints(IAST ast, EvalEngine engine) {
    for (int i = dimension + 2; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST() || option.first() != S.VectorPoints) {
        continue;
      }
      IExpr value = engine.evaluate(option.second());
      if (!value.isListOfLists()) {
        return null;
      }
      IAST list = (IAST) value;
      double[][] result = new double[list.argSize()][];
      for (int k = 1; k <= list.argSize(); k++) {
        IAST point = (IAST) list.get(k);
        if (point.argSize() != dimension) {
          return null;
        }
        result[k - 1] = new double[dimension];
        for (int d = 0; d < dimension; d++) {
          double c = point.get(d + 1).evalfNaN();
          if (!Double.isFinite(c)) {
            return null;
          }
          result[k - 1][d] = c;
        }
      }
      return result;
    }
    return null;
  }

  /** Whether a field is a list of fields, each a list of components. */
  private boolean isFieldList(IExpr field) {
    return field.isList() && field.argSize() > 0
        && ((IAST) field).forAll(f -> f.isList() && f.argSize() == dimension);
  }

  /** The colour of field number <code>index</code> of several, from the default plot colours. */
  private static IAST fieldColor(int index) {
    float[] rgb = GraphicsOptions.plotStyleColor(index, F.NIL).getRGBColorComponents(null);
    // the shortest decimal of each float component
    return F.RGBColor(Double.parseDouble(Float.toString(rgb[0])),
        Double.parseDouble(Float.toString(rgb[1])), Double.parseDouble(Float.toString(rgb[2])));
  }

  /**
   * The <code>ColorFunction</code> of a field in space, handed the point, the vector and its
   * length, each scaled over the range the samples span; <code>null</code> for the default
   * colouring by the field's strength.
   */
  private static PlotColorFunction vectorColors(IExpr option, IExpr scaling, double[] min,
      double[] max, double[][] vectors, EvalEngine engine) {
    if (option.isAutomatic() || option.isNone()) {
      return null;
    }
    double[] low = new double[4];
    double[] high = new double[4];
    java.util.Arrays.fill(low, Double.MAX_VALUE);
    java.util.Arrays.fill(high, -Double.MAX_VALUE);
    for (double[] v : vectors) {
      if (v == null) {
        continue;
      }
      double[] parts = {v[0], v[1], v[2], norm(v)};
      for (int i = 0; i < 4; i++) {
        low[i] = Math.min(low[i], parts[i]);
        high[i] = Math.max(high[i], parts[i]);
      }
    }
    if (low[3] > high[3]) {
      return null;
    }
    return PlotColorFunction
        .of(PlotColorFunction.Family.VECTOR_3D, option, scaling, S.VectorPlot3D, engine)
        .ranges(min[0], max[0], min[1], max[1], min[2], max[2], low[0], high[0], low[1], high[1],
            low[2], high[2], low[3], high[3])
        .sink(PlotColorFunction.Sink.FLAT).build();
  }

  /** The smallest distance between two different points, or {@link Double#MAX_VALUE}. */
  private static double smallestDistance(double[][] points) {
    double smallest = Double.MAX_VALUE;
    for (int i = 0; i < points.length; i++) {
      for (int j = i + 1; j < points.length; j++) {
        double sum = 0.0;
        for (int d = 0; d < points[i].length; d++) {
          double diff = points[i][d] - points[j][d];
          sum += diff * diff;
        }
        if (sum > 0.0) {
          smallest = Math.min(smallest, Math.sqrt(sum));
        }
      }
    }
    return smallest;
  }

  /** Whether a style names a colour anywhere in it. */
  private static boolean containsColor(IExpr style) {
    if (org.matheclipse.core.graphics.GraphicsOptions.isColorExpr(style)
        && !style.isAST(S.Opacity, 2)) {
      return true;
    }
    if (style.isList() || style.isAST(S.Directive)) {
      return ((IAST) style).exists(VectorPlot::containsColor);
    }
    return false;
  }

  /** The field's value at <code>point</code>, or <code>null</code> where it is not numeric. */
  static double[] fieldAt(IExpr field, ISymbol[] variables, double[] point, EvalEngine engine) {
    final int dimension = variables.length;
    IASTAppendable assignments = F.ListAlloc(dimension);
    for (int d = 0; d < dimension; d++) {
      assignments.append(F.Set(variables[d], F.num(point[d])));
    }
    IExpr value;
    try {
      value = engine.evalQuiet(F.N(F.Block(assignments, field)));
    } catch (RuntimeException rex) {
      return null;
    }
    if (!value.isList() || value.argSize() != dimension) {
      return null;
    }
    double[] vector = new double[dimension];
    for (int d = 0; d < dimension; d++) {
      IExpr component = value.getAt(d + 1);
      if (!component.isReal()) {
        return null;
      }
      vector[d] = component.evalf();
    }
    return vector;
  }

  /**
   * The length of the longest arrow given by a {@code VectorScale} value: a number, or the first
   * element of a list; {@code fallback} for anything else.
   */
  static double vectorScale(IExpr value, double fallback) {
    IExpr first = value.isList() && value.argSize() > 0 ? value.first() : value;
    return first.isReal() ? first.evalf() : fallback;
  }

  public static double norm(double[] vector) {
    double sum = 0.0;
    for (double c : vector) {
      sum += c * c;
    }
    return Math.sqrt(sum);
  }

  /**
   * The default gradient of vector and stream plots in L*a*b*, scaled as <code>LABColor</code>:
   * the stops are the RGB colours <code>{0, 0, 0.7}</code>, <code>{0.6, 0.2, 0.6}</code>,
   * <code>{1, 0.43, 0}</code> and <code>{1, 0.75, 0}</code>, from the weakest field to the
   * strongest, spaced evenly and blended in L*a*b*.
   */
  private static final double[][] GRADIENT = { //
      {0.1886675222501131, 0.5225961012766809, -0.8572107619122568},
      {0.4023290342656328, 0.5222145896456903, -0.34657139672158205},
      {0.644456290545989, 0.537992026017925, 0.7321115671273679},
      {0.8167969189733053, 0.14500114740169545, 0.8282532063436999}};

  /** The arrowheads of a two dimensional vector plot. */
  static IAST arrowheads(double size) {
    return F.unaryAST1(S.Arrowheads, F.list(F.list(F.num(size), F.num(1.0))));
  }

  /**
   * The colour for the field's strength <code>t</code>, 0 the weakest and 1 the strongest: a
   * <code>LABColor</code> blended between the stops of {@link #GRADIENT}.
   */
  public static IAST color(double t) {
    t = Double.isNaN(t) ? 0.0 : Math.max(0.0, Math.min(1.0, t));
    double position = t * (GRADIENT.length - 1);
    int k = Math.min((int) Math.floor(position), GRADIENT.length - 2);
    double s = position - k;
    double[] a = GRADIENT[k];
    double[] b = GRADIENT[k + 1];
    return F.ternaryAST3(S.LABColor, F.num(a[0] + s * (b[0] - a[0])),
        F.num(a[1] + s * (b[1] - a[1])), F.num(a[2] + s * (b[2] - a[2])));
  }

  /** The colour of arrows which aren't coloured by the field's strength: the weakest one's. */
  static IAST plainColor() {
    return F.RGBColor(F.num(0.0), F.num(0.0), F.num(0.7));
  }

  /** <code>true</code> if <code>options</code> contains a rule with the left-hand side key. */
  static boolean hasOption(IAST options, IExpr key) {
    for (IExpr option : options) {
      if (option.isRuleAST() && option.first() == key) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
