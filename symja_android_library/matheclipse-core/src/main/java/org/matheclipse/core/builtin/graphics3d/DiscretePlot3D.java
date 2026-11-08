package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotShapeProbe;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotMarkersSpec;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

public class DiscretePlot3D extends AbstractFunctionOptionEvaluator {

  private static class PlotData {
    double x, y, z;
    IExpr color;

    public PlotData(double x, double y, double z, IExpr color) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.color = color;
    }
  }

  public DiscretePlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // a display wrapper comes off before the argument's shape is read, so a labelled dataset is
    // still recognised as a dataset; Plot3DTools.graphics3D puts the label back on the finished
    // primitives, reading it from the original call
    ast = PlotWrapper.stripFirstArgument(ast);

    // DiscretePlot3D[f, {i, ...}, {j, ...}]
    if (argSize < 2) {
      return F.NIL;
    }

    IExpr function = ast.arg1();
    IExpr iRange = ast.arg2();
    IExpr jRange = argSize >= 3 ? ast.arg3() : F.NIL;

    if (!iRange.isList() || iRange.argSize() < 2 || !iRange.first().isSymbol()) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.DiscretePlot3D, "pllim", F.list(iRange), engine);
    }
    if (jRange.isPresent()
        && (!jRange.isList() || jRange.argSize() < 2 || !jRange.first().isSymbol())) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.DiscretePlot3D, "pllim", F.list(jRange), engine);
    }

    // Parse Options
    //
    // Automatic draws a stem with a marker on top; None
    // asks for the markers alone; Full, All, Scaled and an explicit size give each point a box.
    //
    // Full and Scaled are measured in the spacing between neighbouring samples rather than in
    // data units. Resolving them against a spacing of one, as this used to, was only right when
    // the iterator happened to step by one: over {i,0,1,0.1} every bar came out ten times too
    // wide and swallowed its neighbours.
    boolean useThinLineStyle = false;
    boolean pointsOnly = false;
    double extentX = Double.NaN; // an absolute half width, or NaN to take it from the spacing
    double extentY = Double.NaN;
    double extentFractionX = 1.0;
    double extentFractionY = 1.0;
    // the bar lies right (1), left (-1) of its sample, or around it (0)
    double extentShift = 0.0;

    IExpr extentOption = engine.evaluate(options[0]); // ExtentSize
    if (extentOption.isNone()) {
      pointsOnly = true;
    } else if (extentOption == S.Automatic) {
      useThinLineStyle = true;
    } else if (extentOption == S.Right || extentOption == S.Left) {
      // a bar of the full spacing on one side of its sample
      extentShift = extentOption == S.Right ? 1.0 : -1.0;
    } else if (extentOption == S.Full || extentOption == S.All) {
      // the bars meet their neighbours, which is what the defaults above already say
    } else if (extentOption.isAST(S.Scaled, 2)) {
      IExpr scaled = extentOption.first();
      if (scaled.isList() && ((IAST) scaled).size() >= 3) {
        extentFractionX = toDouble(((IAST) scaled).arg1(), extentFractionX);
        extentFractionY = toDouble(((IAST) scaled).arg2(), extentFractionY);
      } else {
        extentFractionX = toDouble(scaled, extentFractionX);
        extentFractionY = extentFractionX;
      }
    } else if (extentOption.isNumber()) {
      double val = extentOption.evalfNaN();
      if (!Double.isNaN(val)) {
        extentX = val / 2.0;
        extentY = val / 2.0;
      }
    } else if (extentOption.isList() && ((IAST) extentOption).size() >= 3) {
      IExpr ex = ((IAST) extentOption).get(1);
      double exValue = ex.isNumber() ? ex.evalfNaN() : Double.NaN;
      if (!Double.isNaN(exValue))
        extentX = exValue / 2.0;
      IExpr ey = ((IAST) extentOption).get(2);
      double eyValue = ey.isNumber() ? ey.evalfNaN() : Double.NaN;
      if (!Double.isNaN(eyValue))
        extentY = eyValue / 2.0;
    }
    IExpr joined = options[X_JOINED];
    IExpr filling = options[X_FILLING];
    IExpr plotMarkers = options[X_PLOT_MARKERS];

    // Scaling Logic
    double zBase = 0.0;
    IExpr scalingOption = options[4]; // ScalingFunctions
    boolean isLogZ = false;

    if (scalingOption.isString() && "Log".equalsIgnoreCase(scalingOption.toString())) {
      isLogZ = true;
    } else if (scalingOption.isList() && ((IAST) scalingOption).size() >= 4) {
      IExpr zScale = ((IAST) scalingOption).get(3);
      if (zScale.isString() && "Log".equalsIgnoreCase(zScale.toString())) {
        isLogZ = true;
      }
    }

    if (isLogZ) {
      zBase = 1.0;
    }

    // Prepare iterators
    IASTAppendable graphicsList = F.ListAlloc();

    IAST functions;
    if (function.isList()) {
      functions = (IAST) function;
    } else {
      functions = F.List(function);
    }
    // f(i, j) with a value of {i j, i + j} is two sets of stems, which only its value can say
    if (iRange.isList() && (jRange.isList() || !jRange.isPresent())) {
      List<IExpr> written = new ArrayList<>();
      functions.forEach(written::add, 1);
      List<IExpr> split = PlotShapeProbe.split(written,
          PlotShapeProbe.iteratorProbes(
              jRange.isList() ? new IAST[] {(IAST) iRange, (IAST) jRange}
                  : new IAST[] {(IAST) iRange},
              engine),
          PlotShapeProbe.SCALAR, false, engine);
      if (split.size() != written.size()) {
        functions = F.List(split.toArray(new IExpr[0]));
      }
    }

    // Thd chart palette: the stems and markers take a slightly darkened palette entry and
    // the body of a bar a lightened, half transparent one, so overlapping bars stay readable
    IExpr plotStyle = options[X_PLOT_STYLE];
    IExpr[] defaultColors = new IExpr[functions.argSize()];
    IExpr[] faceColors = new IExpr[functions.argSize()];
    for (int c = 0; c < defaultColors.length; c++) {
      defaultColors[c] = Plot3DTools.chartStyle(c, plotStyle);
      faceColors[c] = Plot3DTools.chartFaceStyle(c, plotStyle);
    }

    try {
      List<INumber> iValues = parseIterator(iRange, engine);
      List<INumber> jValues = jRange.isList() ? parseIterator(jRange, engine) : null;

      if (Double.isNaN(extentX)) {
        extentX = extentFractionX * sampleSpacing(iValues) / 2.0;
      }
      if (Double.isNaN(extentY)) {
        extentY = extentFractionY * sampleSpacing(jValues == null ? iValues : jValues) / 2.0;
      }

      boolean autoPlotRange = options[1] == S.Automatic;

      // every function is sampled before anything is drawn: the levels Bottom and Top of the
      // filling are the lowest and highest value of all of them
      List<List<PlotData>> datasets = new ArrayList<>(functions.argSize());
      RegionFunctionFilter region = RegionFunctionFilter.of(options[X_REGION_FUNCTION], engine);
      double lowest = Double.POSITIVE_INFINITY;
      double highest = Double.NEGATIVE_INFINITY;
      for (int funcIdx = 1; funcIdx < functions.size(); funcIdx++) {
        IExpr color = defaultColors[(funcIdx - 1) % defaultColors.length];
        List<PlotData> data = sample(functions.get(funcIdx), iRange, iValues, jRange, jValues,
            region, color, options[X_EVALUATION_MONITOR], engine);
        for (PlotData pd : data) {
          lowest = Math.min(lowest, pd.z);
          highest = Math.max(highest, pd.z);
        }
        datasets.add(data);
      }
      // where the stems and bars start, or null for no stems and no bars
      final Double base = fillingLevel(engine.evaluate(filling), zBase, lowest, highest);

      for (int funcIdx = 1; funcIdx < functions.size(); funcIdx++) {
        IASTAppendable primitives = F.ListAlloc();

        // Bars are drawn in the lightened, half transparent face colour; stems and markers in
        // the solid one. It is what lets a row of bars behind another row still be read.
        int colorIdx = (funcIdx - 1) % defaultColors.length;
        IExpr color = defaultColors[colorIdx];
        primitives.append(useThinLineStyle || pointsOnly ? color : faceColors[colorIdx]);
        if (!useThinLineStyle && !pointsOnly) {
          primitives.append(F.EdgeForm(S.None));
        }

        List<PlotData> data = datasets.get(funcIdx - 1);

        // Apply Automatic PlotRange Clamping (Interquartile Range for extreme outliers)
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        // A sampled function needs its poles clamped away, because one asymptote flattens the
        // rest of the surface into the floor. Discrete data has no poles: every value is a
        // measurement, and the tall ones are usually the point of the plot. Clamping them here
        // truncated the peak of any probability mass function and left the bars standing through
        // the top of the box, so the range is simply the range of the data.
        if (autoPlotRange && !data.isEmpty()) {
          minZ = Double.MAX_VALUE;
          maxZ = -Double.MAX_VALUE;
          for (PlotData pd : data) {
            minZ = Math.min(minZ, pd.z);
            maxZ = Math.max(maxZ, pd.z);
          }
        }

        // A ColorFunction colours each element by where it stands, and so takes the place of the
        // chart palette. The extent is the data's own, which is only known now that every sample
        // has been taken.
        double dataMinX = Double.MAX_VALUE;
        double dataMaxX = -Double.MAX_VALUE;
        double dataMinY = Double.MAX_VALUE;
        double dataMaxY = -Double.MAX_VALUE;
        for (PlotData pd : data) {
          dataMinX = Math.min(dataMinX, pd.x);
          dataMaxX = Math.max(dataMaxX, pd.x);
          dataMinY = Math.min(dataMinY, pd.y);
          dataMaxY = Math.max(dataMaxY, pd.y);
        }
        PlotColorFunction elementColors = data.isEmpty() ? null
            : PlotColorFunction
                .of(PlotColorFunction.Family.SURFACE_3D, options[X_COLOR_FUNCTION],
                    options[X_COLOR_FUNCTION_SCALING], S.DiscretePlot3D, engine)
                .sink(PlotColorFunction.Sink.FLAT)
                .ranges(dataMinX, dataMaxX, dataMinY, dataMaxY, minZ, maxZ).build();

        // Construct graphics layout
        List<PlotData> drawn = new ArrayList<>();
        // the plain markers of the samples of one function go into a single point primitive,
        // unless every element takes its own colour
        IASTAppendable markerPoints =
            elementColors == null && plotMarkers.isAutomatic() ? F.ListAlloc(data.size()) : null;
        IASTAppendable markerTarget = markerPoints == null ? primitives : F.ListAlloc();
        // the stems of one function are two lines: those which reach up to the filling level and
        // those which stand above it, each from its value down to the level
        IASTAppendable lowStems = null;
        IASTAppendable highStems = null;
        if (useThinLineStyle && base != null && elementColors == null) {
          lowStems = F.ListAlloc();
          highStems = F.ListAlloc();
        }
        for (PlotData pd : data) {
          double z = pd.z;
          if (autoPlotRange) {
            if (z > maxZ)
              z = maxZ;
            if (z < minZ)
              z = minZ;
          }
          if (isLogZ && z <= 0)
            continue;

          double x = pd.x;
          double y = pd.y;

          drawn.add(new PlotData(x, y, z, color));
          if (elementColors != null) {
            // stands until the next one, which is the next element
            primitives.append(elementColors.color(x, y, z));
          }
          if (pointsOnly || useThinLineStyle) {
            if (useThinLineStyle && base != null) {
              // A stem with a marker on top. A stem is a line: drawing it as
              // a very thin cuboid instead gave a row of slivers that read as a barcode, and the
              // slivers had to be given a width in data units that means nothing to the reader.
              IAST stem = F.List(F.List(F.num(x), F.num(y), F.num(z)),
                  F.List(F.num(x), F.num(y), F.num(base)));
              if (lowStems == null) {
                // every element takes its own colour
                primitives.append(F.Line(stem));
              } else {
                (z <= base ? lowStems : highStems).append(stem);
              }
            }
            if (markerPoints != null) {
              markerPoints.append(F.List(F.num(x), F.num(y), F.num(z)));
            } else {
              appendMarker(markerTarget, plotMarkers, colorIdx, x, y, z);
            }
          } else if (base != null && (z != 0.0 || base != 0.0)) {
            // a bar from the value down to the filling level; a zero value on the level zero has
            // no bar, a value on any other level keeps its flat one
            double cx = x + extentShift * extentX;
            double cy = y + extentShift * extentY;
            IExpr top = F.List(F.num(cx - extentX), F.num(cy - extentY), F.num(z));
            IExpr bottom = F.List(F.num(cx + extentX), F.num(cy + extentY), F.num(base));
            primitives.append(F.Cuboid(top, bottom));
          }
        }
        if (lowStems != null) {
          primitives.append(F.binaryAST2(S.Line, lowStems, F.Rule(S.VertexColors, S.None)));
          primitives.append(F.binaryAST2(S.Line, highStems, F.Rule(S.VertexColors, S.None)));
        }
        if (markerPoints != null && markerPoints.argSize() > 0) {
          primitives.append(F.Point(markerPoints));
        }
        if (joined.isTrue()) {
          appendJoined(primitives, drawn, jValues != null);
        }
        graphicsList.append(primitives);
      }

    } catch (RuntimeException rex) {
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
      return Errors.printMessage(S.DiscretePlot3D, rex);
    }

    // this symbol is HOLDALL so its option values arrive unevaluated; a rule written straight
    // into the graphic would reach the renderer as an unevaluated expression
    return Plot3DTools.graphics3D(graphicsList, originalAST, argSize,
        new IExpr[] {F.Rule(S.BoxRatios, F.List(F.C1, F.C1, F.num(0.5))),
            F.Rule(S.ScalingFunctions, engine.evaluate(options[4])),
            F.Rule(S.PlotRange, engine.evaluate(options[1])),
            F.Rule(S.Axes, engine.evaluate(options[5]))});
  }

  /**
   * The values of <code>f</code> at the samples: a value for each pair of <code>i</code> and
   * <code>j</code> values, or a point <code>{x, y, z}</code> for each <code>i</code> value without
   * a <code>j</code> iterator.
   */
  private static List<PlotData> sample(IExpr f, IExpr iRange, List<INumber> iValues, IExpr jRange,
      List<INumber> jValues, RegionFunctionFilter region, IExpr color, IExpr monitor,
      EvalEngine engine) {
    ISymbol iVar = (ISymbol) ((IAST) iRange).arg1();
    List<PlotData> data = new ArrayList<>();
    // the iterators are assigned in a Block, because the function arrives unevaluated
    for (INumber iv : iValues) {
      IAST iSet = F.Set(iVar, iv);
      if (jValues != null) {
        ISymbol jVar = (ISymbol) ((IAST) jRange).arg1();
        for (INumber jv : jValues) {
          Plot3DTools.monitor(monitor, engine);
          IExpr result =
              engine.evalN(engine.evaluate(F.Block(F.List(iSet, F.Set(jVar, jv)), f)));
          if (result.isNumber()) {
            double z = result.evalf();
            double px = iv.evalf();
            double py = jv.evalf();
            if (region == null || region.accepts(px, py, z)) {
              data.add(new PlotData(px, py, z, color));
            }
          }
        }
      } else {
        Plot3DTools.monitor(monitor, engine);
        IExpr result = engine.evalN(engine.evaluate(F.Block(F.List(iSet), f)));
        if (result.isList() && result.argSize() >= 3) {
          double px = result.first().evalf();
          double py = result.second().evalf();
          double pz = ((IAST) result).arg3().evalf();
          if (region == null || region.accepts(px, py, pz)) {
            data.add(new PlotData(px, py, pz, color));
          }
        }
      }
    }
    return data;
  }

  /**
   * The level the stems and bars start from: the axis level for <code>Automatic</code> and
   * <code>Axis</code>, the lowest or highest value of all functions for <code>Bottom</code> and
   * <code>Top</code>, or a given height.
   *
   * @param filling the evaluated <code>Filling</code> option
   * @param axis the level of the axis
   * @return <code>null</code> for <code>None</code> and <code>False</code>, which draw neither
   *         stems nor bars
   */
  private static Double fillingLevel(IExpr filling, double axis, double lowest, double highest) {
    if (filling.isNone() || filling.isFalse()) {
      return null;
    }
    if (filling == S.Bottom && lowest <= highest) {
      return lowest;
    }
    if (filling == S.Top && lowest <= highest) {
      return highest;
    }
    if (filling.isReal()) {
      return filling.evalf();
    }
    return axis;
  }

  private static double toDouble(IExpr expr, double fallback) {
    double value = expr.evalfNaN();
    return Double.isNaN(value) ? fallback : value;
  }

  /**
   * The step between neighbouring samples, which is the unit {@code ExtentSize} is measured in.
   *
   * <p>
   * The smallest gap rather than the average one, so that an unevenly sampled row still has bars
   * that do not overlap.
   */
  private static double sampleSpacing(List<INumber> values) {
    if (values == null || values.size() < 2) {
      return 1.0;
    }
    double smallest = Double.MAX_VALUE;
    for (int i = 1; i < values.size(); i++) {
      double gap = Math.abs(values.get(i).evalf() - values.get(i - 1).evalf());
      if (gap > 0.0 && gap < smallest) {
        smallest = gap;
      }
    }
    return smallest == Double.MAX_VALUE ? 1.0 : smallest;
  }

  /**
   * The marker that sits on top of a stem, as {@code PlotMarkers} asks for.
   *
   * <p>
   * The settings are read the same way the 2D plots read them, so a list is one marker per dataset
   * and a pair whose second element is a size is one marker at that size. {@code Automatic} is the
   * exception: on a stem plot the standard marker is the plain point this draws by default, not
   * one of the 2D glyphs.
   */
  private static void appendMarker(IASTAppendable primitives, IExpr plotMarkers, int index,
      double x, double y, double z) {
    IAST position = F.List(F.num(x), F.num(y), F.num(z));
    if (plotMarkers.isNone()) {
      return;
    }
    if (plotMarkers.isAutomatic()) {
      primitives.append(F.Point(position));
      return;
    }
    PlotMarkersSpec spec = PlotMarkersSpec.of(plotMarkers);
    if (spec == null) {
      return;
    }
    IExpr marker = spec.markerAt(index);
    if (marker.isNIL()) {
      primitives.append(F.Point(position));
      return;
    }
    primitives.append(F.Text(marker, position));
  }

  /**
   * Joins the tops of the samples, as {@code Joined -> True} asks for.
   *
   * <p>
   * A grid is joined along both directions, so the tops read as a wireframe of the surface the
   * samples lie on; a plain sequence of points is joined in the order it was sampled. The points
   * are grouped by coordinate rather than by index, because a sample whose value did not evaluate
   * to a number never reached this list and would otherwise shift the whole row.
   */
  private static void appendJoined(IASTAppendable primitives, List<PlotData> data, boolean grid) {
    if (data.size() < 2) {
      return;
    }
    if (!grid) {
      IASTAppendable points = F.ListAlloc(data.size());
      for (PlotData pd : data) {
        points.append(F.List(F.num(pd.x), F.num(pd.y), F.num(pd.z)));
      }
      primitives.append(F.Line(points));
      return;
    }
    appendLanes(primitives, data, true);
    appendLanes(primitives, data, false);
  }

  private static void appendLanes(IASTAppendable primitives, List<PlotData> data, boolean byX) {
    Map<Double, IASTAppendable> lanes = new LinkedHashMap<>();
    for (PlotData pd : data) {
      lanes.computeIfAbsent(byX ? pd.x : pd.y, key -> F.ListAlloc())
          .append(F.List(F.num(pd.x), F.num(pd.y), F.num(pd.z)));
    }
    for (IASTAppendable lane : lanes.values()) {
      if (lane.argSize() > 1) {
        primitives.append(F.Line(lane));
      }
    }
  }

  private List<INumber> parseIterator(IExpr iter, EvalEngine engine) {
    List<INumber> values = new ArrayList<>();
    if (!iter.isList()) {
      return values;
    }
    IAST list = (IAST) iter;

    if (list.argSize() == 2) {
      IExpr arg2 = engine.evaluate(list.arg2());
      if (arg2.isList()) {
        IAST valList = (IAST) arg2;
        for (int k = 1; k < valList.size(); k++) {
          IExpr v = engine.evaluate(valList.get(k));
          if (v instanceof INumber) {
            values.add((INumber) v);
          }
        }
      } else {
        IInteger v = F.C1;
        if (arg2 instanceof INumber) {
          INumber max = (INumber) arg2;
          while (v.lessThan(max).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.inc();
          }
        }
      }
    } else if (list.argSize() >= 3) {
      IExpr arg2 = engine.evaluate(list.arg2());
      IExpr arg3 = engine.evaluate(list.arg3());
      if (arg2 instanceof INumber && arg3 instanceof INumber) {
        INumber min = (INumber) arg2;
        INumber max = (INumber) arg3;
        INumber step = F.C1;

        if (list.argSize() >= 4) {
          IExpr arg4 = engine.evaluate(list.arg4());
          if (arg4 instanceof INumber) {
            step = (INumber) arg4;
          }
        }

        INumber v = min;
        if (step.isNegative()) {
          while (max.lessThan(v).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.plus(step);
          }
        } else {
          while (v.lessThan(max).isTrue() || v.equals(max)) {
            values.add(v);
            v = v.plus(step);
          }
        }
      }
    }
    return values;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  /**
   * The option table, built fresh each time so the positions can be looked up rather than counted.
   *
   * <p>
   * The first six keep the positions this evaluator has always read by index; because an option
   * this symbol does not declare stops the option scan and takes the options written before it down
   * with it.
   */
  private static GraphicsOptions.OptionSet optionSet() {
    return Plot3DTools.discreteExtras(new GraphicsOptions.OptionSet() //
        .add(
            new IBuiltInSymbol[] {S.ExtentSize, S.PlotRange, S.ColorFunction, S.PlotLegends,
                S.ScalingFunctions, S.Axes},
            new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.None, S.None, S.True}));
  }

  private static final int X_PLOT_STYLE = Plot3DTools.indexOf(optionSet(), S.PlotStyle);
  private static final int X_REGION_FUNCTION = Plot3DTools.indexOf(optionSet(), S.RegionFunction);
  private static final int X_EVALUATION_MONITOR =
      Plot3DTools.indexOf(optionSet(), S.EvaluationMonitor);
  private static final int X_JOINED = Plot3DTools.indexOf(optionSet(), S.Joined);
  private static final int X_FILLING = Plot3DTools.indexOf(optionSet(), S.Filling);
  private static final int X_PLOT_MARKERS = Plot3DTools.indexOf(optionSet(), S.PlotMarkers);
  // this plot has its own option table, so the surface block's positions do not apply to it
  private static final int X_COLOR_FUNCTION = Plot3DTools.indexOf(optionSet(), S.ColorFunction);
  private static final int X_COLOR_FUNCTION_SCALING =
      Plot3DTools.indexOf(optionSet(), S.ColorFunctionScaling);

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = optionSet();
    setOptions(newSymbol, options.keys(), options.values());
    newSymbol.setAttributes(Attribute.HOLDALL);
  }
}
