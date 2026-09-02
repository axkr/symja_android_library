package org.matheclipse.core.graphics;

import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IASTDataset;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Options for the 2D Graphics object.
 */
public class GraphicsOptions {

  /**
   * The data a chart should plot.
   *
   * <p>
   * A <code>Dataset</code> becomes its rows, which for the one-column dataset a chart is usually
   * handed - <code>planets[All, "radius"]</code> - is already the bare list of numbers the chart
   * wants. An <code>Association</code> becomes its values, and its keys are collected into
   * <code>labelsOut</code> so that <code>PieChart[&lt;|"a" -&gt; 1, "b" -&gt; 2|&gt;]</code> labels its
   * slices the way the reference does. Anything else is handed back untouched.
   *
   * <p>
   * The charts live in <code>matheclipse-core</code> and cannot see <code>ASTDataset</code>, which
   * is in <code>matheclipse-dataset</code>; they do not need to, because
   * {@link IASTDataset#normalizeDataset(IExpr)} is the contract core owns.
   *
   * @param labelsOut collects the keys of an association, in order; left alone otherwise
   * @return the data to plot
   */
  public static IExpr chartData(IExpr arg, IASTAppendable labelsOut) {
    IExpr data = IASTDataset.normalizeDataset(arg);
    if (data.isAssociation()) {
      IAssociation assoc = (IAssociation) data;
      IASTAppendable values = F.ListAlloc(assoc.argSize());
      for (int i = 1; i < assoc.size(); i++) {
        IAST rule = assoc.getRule(i);
        labelsOut.append(rule.first());
        values.append(rule.second());
      }
      return values;
    }
    return data;
  }

  /**
   * The labels to draw: whatever <code>ChartLabels</code> was given, or the keys an association
   * brought with it. An explicit option wins - the keys are a default, not an override.
   */
  public static IExpr chartLabels(IExpr chartLabels, IASTAppendable keyLabels) {
    if (chartLabels == S.None && keyLabels.argSize() > 0) {
      return keyLabels;
    }
    return chartLabels;
  }


  /**
   * Holder for the lazily created JSON object mapper.
   *
   * <p>
   * This class is initialized during start-up, because the <code>setUp()</code> methods of the plot
   * functions read the default option values from here. Creating the {@link ObjectMapper} eagerly
   * would load about 380 Jackson classes at that point, although JSON is only needed when graphics
   * are really rendered.
   */
  private static final class MapperHolder {
    static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();
  }

  /**
   * The JSON object mapper used to create the graphics output. The Jackson databind subsystem is
   * initialized on the first call.
   *
   * @return the JSON object mapper for graphics output
   */
  public static ObjectMapper jsonObjectMapper() {
    return MapperHolder.JSON_OBJECT_MAPPER;
  }

  public final static int X_JSFORM = 0;
  public final static int X_FILLING = 1;
  public final static int X_AXES = 2;
  public final static int X_PLOTRANGE = 3;
  public final static int X_$SCALING = 4;
  public final static int X_JOINED = 5;
  public final static int X_PLOTLEGENDS = 6;
  public final static int X_PLOTLABEL = 7;
  public final static int X_AXESLABEL = 8;
  public final static int X_PLOTSTYLE = 9;
  public final static int X_GRIDLINES = 10;
  public final static int X_PLOTLABELS = 11;
  public final static int X_FILLINGSTYLE = 12;
  public final static int X_ASPECTRATIO = 13;
  public final static int X_FRAME = 14;
  public final static int X_DATARANGE = 15;
  public final static int X_CHARTLEGENDS = 16;
  public final static int X_FRAMETICKS = 17;
  public final static int X_BACKGROUND = 18;
  public final static int X_EPILOG = 19;
  /**
   * Index of {@code PlotRangeClipping} in the option arrays. It sits after the block appended for
   * the presentation options, so the earlier indices keep their values.
   */
  public final static int X_PLOTRANGECLIPPING = 30;

  /*
   * Sunset color map control points (RGB) for Density/Contour plots
   * 
   */
  public static final double[][] SUNSET_COLORS = {//
      {0.10, 0.05, 0.30}, // Dark Indigo
      {0.35, 0.10, 0.55}, // Purple
      {0.75, 0.15, 0.45}, // Red-Magenta
      {0.95, 0.55, 0.15}, // Orange
      {0.98, 0.90, 0.60} // Pale Yellow
  };

  // MatrixPlot color map (Light Yellow -> Orange -> Red)
  public static final double[][] MATRIX_COLORS = {//
      {0.98, 0.98, 0.90}, // Pale Yellow/White
      {1.00, 0.85, 0.40}, // Yellow/Orange
      {0.95, 0.50, 0.10}, // Orange
      {0.80, 0.10, 0.05}, // Red
      {0.40, 0.00, 0.00} // Dark Red/Brown
  };

  /**
   * Interpolates colors using the "Sunset" gradient.
   * 
   * @param t value between 0.0 and 1.0
   */

  public static IAST getSunsetColor(double t) {
    return interpolateColor(t, SUNSET_COLORS);
  }

  public static IAST getMatrixColor(double t) {
    return interpolateColor(t, MATRIX_COLORS);
  }

  private static IAST interpolateColor(double t, double[][] map) {
    if (t < 0)
      t = 0;
    if (t > 1)
      t = 1;
    int n = map.length - 1;
    double pos = t * n;
    int idx = (int) pos;
    if (idx >= n) {
      double[] c = map[n];
      return F.RGBColor(c[0], c[1], c[2]);
    }
    double frac = pos - idx;
    double[] c1 = map[idx];
    double[] c2 = map[idx + 1];
    double r = c1[0] + (c2[0] - c1[0]) * frac;
    double g = c1[1] + (c2[1] - c1[1]) * frac;
    double b = c1[2] + (c2[2] - c1[2]) * frac;
    return F.RGBColor(r, g, b);
  }

  public static IAST BLACK = F.RGBColor(F.C0, F.C0, F.C0);

  public static double TINY_POINTSIZE = 0.005;
  public static double SMALL_POINTSIZE = 0.01;
  public static double MEDIUM_POINTSIZE = 0.015;
  public static double LARGE_POINTSIZE = 0.025;

  public static int TINY_FONTSIZE = 4;
  public static int SMALL_FONTSIZE = 8;
  public static int MEDIUM_FONTSIZE = 12;
  public static int LARGE_FONTSIZE = 24;

  public static double TINY_THICKNESS = 0.005;
  public static double SMALL_THICKNESS = 0.01;
  public static double MEDIUM_THICKNESS = 0.02;
  public static double LARGE_THICKNESS = 0.03;

  /**
   * The colour cycle a plot walks through for successive curves or data sets.
   */
  public static final RGBColor[] PLOT_COLORS =
      new RGBColor[] {new RGBColor(0.24f, 0.6f, 0.8f), new RGBColor(0.95f, 0.627f, 0.1425f),
          new RGBColor(0.455f, 0.7f, 0.21f), new RGBColor(0.922526f, 0.385626f, 0.209179f),
          new RGBColor(0.578f, 0.51f, 0.85f), new RGBColor(0.772079f, 0.431554f, 0.102387f),
          new RGBColor(0.0f, 0.6f, 0.6f), new RGBColor(0.9f, 0.36f, 0.684f),
          new RGBColor(0.56f, 0.7f, 0.0f), new RGBColor(0.9f, 0.54f, 0.0f),
          new RGBColor(0.585f, 0.5976f, 0.9f), new RGBColor(0.9f, 0.45f, 0.45f),
          new RGBColor(0.7632186717367253f, 0.8174402144034898f, 0.0f),
          new RGBColor(0.8554632469481959f, 0.24885384718327969f, 0.833405387394855f),
          new RGBColor(0.032972823413648836f, 0.7396719749139476f, 0.534700104525218f)};

  /**
   * The colour cycle the chart functions use for successive bars, sectors or boxes. It is a
   * different sequence from {@link #PLOT_COLORS}, starting at amber rather than blue.
   */
  public static final RGBColor[] CHART_COLORS =
      new RGBColor[] {new RGBColor(1.0f, 0.835f, 0.25f), new RGBColor(0.0f, 0.665f, 0.95f),
          new RGBColor(0.375f, 0.75f, 0.0f), new RGBColor(1.0f, 0.37f, 0.1f),
          new RGBColor(0.725f, 0.45f, 1.0f), new RGBColor(0.85f, 0.51f, 0.0f),
          new RGBColor(0.0f, 0.7f, 0.7f), new RGBColor(1.0f, 0.4f, 0.76f),
          new RGBColor(0.64f, 0.8f, 0.0f), new RGBColor(1.0f, 0.61f, 0.0f),
          new RGBColor(0.65f, 0.664f, 1.0f), new RGBColor(1.0f, 0.5f, 0.5f),
          new RGBColor(0.7792381093092542f, 0.8593242721122579f, 0.14568974765050105f),
          new RGBColor(0.8795508847078471f, 0.35632892385929255f, 0.8609048228736547f),
          new RGBColor(0.15342460575502262f, 0.7830599790949564f, 0.6021943875429379f)};

  /** Stroke width of a plotted curve, in printer's points. */
  public static final double PLOT_THICKNESS = 2.0;

  /** Stroke width of a chart element's edge, in printer's points. */
  public static final double CHART_THICKNESS = 1.5;

  // ---------------------------------------------------------------------------------------------
  // Per family option sets
  //
  // Every 2D plot accepts the same base block, whose first entries are read positionally through
  // the X_* constants above, and then whatever its own family adds. Keeping the names and their
  // default values in one structure means the two can never fall out of step: a plot that
  // registers a shorter list than its evaluator reads used to index past the end of the value
  // array and take the whole plot down with it.
  // ---------------------------------------------------------------------------------------------

  /**
   * An ordered set of option names with their default values. Adding a name that is already present
   * keeps the first default, so a family may safely repeat one from the base block.
   */
  public static final class OptionSet {
    private final java.util.LinkedHashMap<IBuiltInSymbol, IExpr> entries =
        new java.util.LinkedHashMap<>();

    public OptionSet add(IBuiltInSymbol[] keys, IExpr[] values) {
      int count = Math.min(keys.length, values.length);
      for (int i = 0; i < count; i++) {
        entries.putIfAbsent(keys[i], values[i]);
      }
      return this;
    }

    /** Add names that all share one default value. */
    public OptionSet add(IExpr defaultValue, IBuiltInSymbol... keys) {
      for (IBuiltInSymbol key : keys) {
        entries.putIfAbsent(key, defaultValue);
      }
      return this;
    }

    /** Replace the default of an option that is already present. */
    public OptionSet override(IBuiltInSymbol key, IExpr value) {
      if (entries.containsKey(key)) {
        entries.put(key, value);
      }
      return this;
    }

    public IBuiltInSymbol[] keys() {
      return entries.keySet().toArray(new IBuiltInSymbol[0]);
    }

    public IExpr[] values() {
      return entries.values().toArray(new IExpr[0]);
    }
  }

  /** The option block every 2D plot starts with, in the order the X_* constants index it. */
  public static OptionSet base2D(boolean jsForm, boolean joined) {
    return new OptionSet().add(listPlotDefaultOptionKeys(),
        listPlotDefaultOptionValues(jsForm, joined));
  }

  /** Options of the plots that sample a function over a range. */
  public static OptionSet functionPlotExtras(OptionSet set) {
    return set
        .add(S.Automatic, S.PlotPoints, S.MaxRecursion, S.WorkingPrecision, S.PlotTheme,
            S.LabelStyle, S.ColorFunction, S.ColorFunctionScaling, S.ScalingFunctions,
            S.PerformanceGoal, S.ClippingStyle, S.RegionFunction, S.ExclusionsStyle,
            S.EvaluationMonitor, S.MeshFunctions, S.MeshShading, S.MeshStyle)
        .add(S.None, S.Mesh, S.Exclusions);
  }

  /** Options of the plots that take explicit data. */
  public static OptionSet listPlotExtras(OptionSet set) {
    return set.add(S.Automatic, S.PlotMarkers, S.InterpolationOrder, S.PlotTheme, S.LabelStyle,
        S.ColorFunction, S.ColorFunctionScaling, S.ScalingFunctions, S.ClippingStyle,
        S.LabelingFunction, S.LabelingSize, S.MeshStyle, S.PerformanceGoal, S.TargetUnits)
        .add(S.None, S.Mesh);
  }

  /** Options of the bar, pie, histogram and box whisker charts. */
  public static OptionSet chartExtras(OptionSet set) {
    return set
        .add(S.Automatic, S.ChartStyle, S.ChartBaseStyle, S.ChartElementFunction, S.ChartElements,
            S.ChartLayout, S.BarOrigin, S.BarSpacing, S.SectorOrigin, S.SectorSpacing,
            S.LabelingFunction, S.LabelingSize, S.PlotTheme, S.LabelStyle, S.PerformanceGoal,
            S.TargetUnits)
        .add(S.None, S.ChartLabels);
  }

  /** Options of the contour plots. */
  public static OptionSet contourExtras(OptionSet set) {
    return set
        .add(S.Automatic, S.Contours, S.ContourStyle, S.ContourLabels, S.ColorFunction,
            S.PlotPoints, S.MaxRecursion, S.RegionFunction, S.BoundaryStyle, S.MeshStyle,
            S.PlotTheme, S.LabelStyle, S.MaxPlotPoints, S.PerformanceGoal)
        .add(S.True, S.ContourShading, S.ContourLines, S.ColorFunctionScaling).add(S.None, S.Mesh);
  }

  /** Options of the density plots. */
  public static OptionSet densityExtras(OptionSet set) {
    return set
        .add(S.Automatic, S.ColorFunction, S.PlotPoints, S.MaxRecursion, S.RegionFunction,
            S.BoundaryStyle, S.MeshStyle, S.PlotTheme, S.LabelStyle, S.PerformanceGoal)
        .add(S.True, S.ColorFunctionScaling).add(S.None, S.Mesh);
  }

  /** Options of the plots that paint a grid of cells. */
  public static OptionSet rasterExtras(OptionSet set) {
    return set.add(S.Automatic, S.ColorFunction, S.ColorRules, S.MaxPlotPoints, S.MeshStyle,
        S.PlotTheme, S.LabelStyle).add(S.True, S.ColorFunctionScaling).add(S.None, S.Mesh);
  }

  /** Options of the polar plots. */
  public static OptionSet polarExtras(OptionSet set) {
    return set.add(S.Automatic, S.PolarAxes, S.PolarGridLines, S.PolarTicks, S.PlotPoints,
        S.MaxRecursion, S.MeshStyle, S.PlotTheme, S.LabelStyle).add(S.None, S.Mesh);
  }

  /** Options of {@code WordCloud}. */
  public static OptionSet wordCloudExtras(OptionSet set) {
    return set.add(S.Automatic, S.WordOrientation, S.WordSpacings, S.WordSelectionFunction,
        S.ColorFunction, S.ScalingFunctions, S.PlotTheme, S.LabelStyle);
  }

  /**
   * The value an option was given in the call itself.
   *
   * <p>
   * The {@code options} array handed to an evaluator holds resolved option <em>values</em>,
   * positionally, not the rules the user wrote. Scanning that array for {@code Rule} expressions
   * therefore never matches anything, which is how several chart options came to be accepted and
   * then silently ignored. Reading them back off the original expression avoids depending on the
   * position an option happens to occupy.
   *
   * @param originalAST the unevaluated call
   * @param name the option name to look for
   * @param defaultValue returned when the call does not mention the option
   */
  public static IExpr optionValue(IAST originalAST, ISymbol name, IExpr defaultValue) {
    if (originalAST == null) {
      return defaultValue;
    }
    for (int i = 1; i < originalAST.size(); i++) {
      IExpr arg = originalAST.get(i);
      if (arg.isRuleAST() && arg.first().equals(name)) {
        return ((IAST) arg).second();
      }
      if (arg.isList()) {
        // options may arrive wrapped in a list
        IExpr nested = optionValue((IAST) arg, name, null);
        if (nested != null) {
          return nested;
        }
      }
    }
    return defaultValue;
  }

  /**
   * A {@code Raster} covering the rectangle from {@code (x0, y0)} to {@code (x1, y1)}, painted from
   * a grid of colours given with the top row first.
   *
   * <p>
   * A grid of cells is one raster rather than one rectangle each. That is what the reference
   * rendering produces, and it keeps the output small: a 100x100 matrix is a single primitive
   * instead of ten thousand, which also stays well clear of the element limit the SVG rasterizer
   * imposes. {@code Raster} counts its first row from the bottom, so the rows are reversed here.
   *
   * @param rowsTopFirst colours per row, starting with the row drawn at the top; a {@code null}
   *        cell is left transparent
   */
  public static IAST rasterTopFirst(IExpr[][] rowsTopFirst, double x0, double y0, double x1,
      double y1) {
    IASTAppendable data = F.ListAlloc(rowsTopFirst.length);
    for (int r = rowsTopFirst.length - 1; r >= 0; r--) {
      IExpr[] row = rowsTopFirst[r];
      IASTAppendable rowList = F.ListAlloc(row.length);
      for (IExpr cell : row) {
        rowList.append(cell == null ? TRANSPARENT_CELL : cell);
      }
      data.append(rowList);
    }
    return F.binaryAST2(S.Raster, data,
        F.List(F.List(F.num(x0), F.num(y0)), F.List(F.num(x1), F.num(y1))));
  }

  /** Fully transparent, used for cells a raster has no value for. */
  private static final IAST TRANSPARENT_CELL = F.RGBColor(F.C0, F.C0, F.C0, F.C0);

  /**
   * Resolve a {@code ColorFunction} option into a mapping from a value in 0..1 to a colour.
   *
   * <p>
   * A gradient name such as {@code "Rainbow"}, a {@code ColorData[...]} object, or any function of
   * one argument. Anything that does not evaluate to a colour falls back to the plot's own default,
   * so a misspelled gradient degrades to the normal appearance instead of a blank picture.
   *
   * @param spec the option value, possibly {@link S#Automatic}
   * @param engine used to apply the function
   * @param fallback the colouring to use when {@code spec} does not name one
   */
  public static java.util.function.DoubleFunction<IExpr> colorFunction(IExpr spec,
      EvalEngine engine, java.util.function.DoubleFunction<IExpr> fallback) {
    if (spec == null || spec == S.Automatic || spec.isNone()) {
      return fallback;
    }
    final IExpr function = spec.isString() ? F.ColorData(spec) : spec;
    return t -> {
      try {
        IExpr color = engine.evaluate(F.unaryAST1(function, F.num(t)));
        return isColorExpr(color) ? color : fallback.apply(t);
      } catch (RuntimeException rex) {
        return fallback.apply(t);
      }
    };
  }

  /** True when the expression is one of the colour space heads. */
  public static boolean isColorExpr(IExpr expr) {
    if (expr == null || !expr.isAST()) {
      return false;
    }
    IExpr head = expr.head();
    if (!head.isBuiltInSymbol()) {
      return false;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.RGBColor:
      case ID.Hue:
      case ID.GrayLevel:
      case ID.CMYKColor:
        return true;
      default:
        return false;
    }
  }

  /**
   * Resolve a {@code ColorRules} option, which maps particular data values to particular colours.
   *
   * @param spec a list of {@code value -> colour} rules, or anything else for no mapping
   * @return the colour for an exact value match, or {@code null}
   */
  public static IExpr colorRule(IExpr spec, IExpr value) {
    if (spec == null || !spec.isList()) {
      return null;
    }
    IAST rules = (IAST) spec;
    for (int i = 1; i < rules.size(); i++) {
      IExpr rule = rules.get(i);
      if (rule.isRuleAST() && rule.first().equals(value)) {
        IExpr color = ((IAST) rule).second();
        if (isColorExpr(color)) {
          return color;
        }
      }
    }
    return null;
  }

  protected static void addPadding(double[] boundingbox) {
    double xPadding = (boundingbox[1] - boundingbox[0]) / 20.0;
    double yPadding = (boundingbox[3] - boundingbox[2]) / 20.0;
    if (F.isZero(xPadding))
      xPadding = 0.05;
    if (F.isZero(yPadding))
      yPadding = 0.05;
    boundingbox[0] -= xPadding;
    boundingbox[1] += xPadding;
    boundingbox[2] -= yPadding;
    boundingbox[3] += yPadding;
  }

  public static DoubleUnaryOperator getScalingFunction(String scale) {
    if (scale == null)
      return x -> x;
    if (scale.equalsIgnoreCase("Log10"))
      return Math::log10;
    if (scale.equalsIgnoreCase("Log"))
      return Math::log;
    return x -> x;
  }

  public static Function<IExpr, IExpr> getScaling(ArrayNode array, IExpr scale) {
    if (scale.isString()) {
      String scaleStr = scale.toString();
      String lower = scaleStr.toLowerCase(Locale.US);
      if (scaleStr.equals("Log")) {
        array.add(lower);
        return x -> F.Log(x);
      }
      if (scaleStr.equals("Log2")) {
        array.add(lower);
        return x -> F.Log(x, F.C2);
      }
      if (scaleStr.equals("Log10")) {
        array.add(lower);
        return x -> F.Log(x, F.C10);
      }
    }
    array.add("none");
    return x -> x;
  }

  public static Function<IExpr, IExpr> getScaling(IExpr scale) {
    if (scale.isString()) {
      String scaleStr = scale.toString();
      if (scaleStr.equals("Log"))
        return x -> F.Log(x);
      if (scaleStr.equals("Log2"))
        return x -> F.Log(x, F.C2);
      if (scaleStr.equals("Log10"))
        return x -> F.Log(x, F.C10);
    }
    return x -> x;
  }

  public static void optionBoolean(ArrayNode arrayNode, String optionName, boolean value) {
    ObjectNode jsonDefaults = GraphicsOptions.jsonObjectMapper().createObjectNode();
    jsonDefaults.put("option", optionName);
    jsonDefaults.put("value", value);
    arrayNode.add(jsonDefaults);
  }

  public static void optionDouble(ArrayNode arrayNode, String optionName, double value) {
    ObjectNode jsonDefaults = arrayNode.objectNode();
    optionDouble(jsonDefaults, optionName, value);
    arrayNode.add(jsonDefaults);
  }

  private static void optionDouble(ObjectNode objectNode, String optionName, double value) {
    objectNode.put("option", optionName);
    objectNode.put("value", value);
  }

  public static void optionInt(ArrayNode arrayNode, String optionName, int value) {
    ObjectNode jsonDefaults = GraphicsOptions.jsonObjectMapper().createObjectNode();
    jsonDefaults.put("option", optionName);
    jsonDefaults.put("value", value);
    arrayNode.add(jsonDefaults);
  }

  public static IExpr getPlotStyle(IExpr plotStyleOption, int index) {
    if (plotStyleOption.isList()) {
      return plotStyleOption.get((index % plotStyleOption.argSize()) + 1);
    }
    return plotStyleOption;
  }

  /**
   * The colour {@code PlotStyle} gives curve number {@code functionColorNumber}, falling back to
   * the default palette when the style at that position is not a colour.
   *
   * <p>
   * This used to index the list with the raw, zero-based {@code functionColorNumber} - but
   * {@link IAST#get} is one-based, so {@code get(0)} read the list's head instead of its first
   * element. The first curve therefore never saw its requested style, every later curve was shifted
   * onto its neighbour's style, and a style list exactly as long as the number of curves left the
   * last style unused entirely. {@link #getPlotStyle}, used throughout the rest of this package,
   * already does the one-based lookup - with wrap around, when there are more curves than styles -
   * so it is reused here instead of repeating the same mistake.
   */
  public static RGBColor plotStyleColor(int functionColorNumber, IExpr plotStyle) {
    if (plotStyle != null && plotStyle.isPresent()
        && (!plotStyle.isList() || plotStyle.argSize() > 0)) {
      IExpr temp = getPlotStyle(plotStyle, functionColorNumber);
      if (temp.isASTSizeGE(S.Directive, 2)) {
        IAST directive = (IAST) temp;
        for (int j = 1; j < directive.size(); j++) {
          temp = directive.get(j);
          RGBColor color = Convert.toAWTColor(temp);
          if (color != null)
            return color;
        }
      } else {
        RGBColor color = Convert.toAWTColor(temp);
        if (color != null)
          return color;
      }
    }
    return PLOT_COLORS[Math.floorMod(functionColorNumber, PLOT_COLORS.length)];
  }

  public static IAST plotStyleColorExpr(int functionColorNumber, IAST plotStyle) {
    RGBColor color = GraphicsOptions.plotStyleColor(functionColorNumber, plotStyle);
    float[] rgbComponents = color.getRGBColorComponents(null);
    return F.RGBColor(rgbComponents[0], rgbComponents[1], rgbComponents[2]);
  }

  /**
   * The full default style of a plotted curve: its colour from the cycle together with the stroke
   * width. Without the width a curve is drawn one pixel wide, which is markedly thinner than the
   * reference rendering.
   *
   * @param functionColorNumber index into the colour cycle
   * @param plotStyle a user supplied {@code PlotStyle} value, or {@link F#NIL}
   * @param absoluteThickness stroke width in printer's points
   */
  public static IAST plotStyleDirective(int functionColorNumber, IAST plotStyle,
      double absoluteThickness) {
    return F.Directive(plotStyleColorExpr(functionColorNumber, plotStyle),
        F.AbsoluteThickness(F.num(absoluteThickness)));
  }

  /** The colour a chart element takes from the chart cycle. */
  public static IAST chartStyleColorExpr(int elementNumber) {
    RGBColor color = CHART_COLORS[Math.floorMod(elementNumber, CHART_COLORS.length)];
    float[] rgbComponents = color.getRGBColorComponents(null);
    return F.RGBColor(rgbComponents[0], rgbComponents[1], rgbComponents[2]);
  }

  private static void setColor(ObjectNode json, double red, double green, double blue) {
    ArrayNode arrayNode = json.arrayNode();
    arrayNode.add(red);
    arrayNode.add(green);
    arrayNode.add(blue);
    json.set("color", arrayNode);
  }

  public static void setColor(ObjectNode json, IAST color, IAST defaultColor, boolean color3D) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
        double red = color.arg1().toDoubleDefault(0.0);
        double green = color.arg2().toDoubleDefault(0.0);
        double blue = color.arg3().toDoubleDefault(0.0);

        ArrayNode arrayNode = json.arrayNode();
        arrayNode.add(red);
        arrayNode.add(green);
        arrayNode.add(blue);
        json.set("color", arrayNode);
        return;
      } else if (color.isAST(S.RGBColor, 1) && color.arg1().isAST(S.List, 4, 5)) {
        IAST list = (IAST) color.arg1();
        // if (color.size() == 5) {
        // double opacity = list.arg4().toDoubleDefault(1.0);
        // json.put("opacity", opacity);
        // }
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        ArrayNode arrayNode = json.arrayNode();
        arrayNode.add(red);
        arrayNode.add(green);
        arrayNode.add(blue);
        json.set("color", arrayNode);
        return;
      }
    }
    if (defaultColor.isAST(S.RGBColor, 4, 5)) {
      double red = defaultColor.arg1().toDoubleDefault(0.0);
      double green = defaultColor.arg2().toDoubleDefault(0.0);
      double blue = defaultColor.arg3().toDoubleDefault(0.0);
      ArrayNode arrayNode = json.arrayNode();
      arrayNode.add(red);
      arrayNode.add(green);
      arrayNode.add(blue);
      json.set("color", arrayNode);
    } else {
      // black
      ArrayNode arrayNode = json.arrayNode();
      if (color3D) {
        arrayNode.add(1.0);
        arrayNode.add(0.5);
        arrayNode.add(0.0);
      } else {
        arrayNode.add(0.0);
        arrayNode.add(0.0);
        arrayNode.add(0.0);
      }
      json.set("color", arrayNode);
    }

  }

  public static void setColorOption(ObjectNode json, double red, double green, double blue) {
    ArrayNode arrayNode = json.arrayNode();
    arrayNode.add(red);
    arrayNode.add(green);
    arrayNode.add(blue);
    json.put("option", "color");
    json.set("value", arrayNode);
  }

  public static void setColorOption(ArrayNode arrayNode, IAST color) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
        double red = color.arg1().toDoubleDefault(0.0);
        double green = color.arg2().toDoubleDefault(0.0);
        double blue = color.arg3().toDoubleDefault(0.0);
        ObjectNode g = arrayNode.objectNode();
        setColorOption(g, red, green, blue);
        arrayNode.add(g);
        double opacity = 1.0;
        if (color.argSize() == 4) {
          opacity = color.arg4().toDoubleDefault(1.0);
        }
        optionDouble(arrayNode, "opacity", opacity);
        return;
      } else if (color.isAST(S.RGBColor, 1) && color.arg1().isAST(S.List, 4, 5)) {
        IAST list = (IAST) color.arg1();
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        ObjectNode g = arrayNode.objectNode();
        setColorOption(g, red, green, blue);
        arrayNode.add(g);
        double opacity = 1.0;
        if (list.argSize() == 4) {
          opacity = list.arg4().toDoubleDefault(1.0);
        }

        GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
        return;
      }
    }
    // black
    ObjectNode g = arrayNode.objectNode();
    setColorOption(g, 0.0, 0.0, 0.0);
    arrayNode.add(g);
  }

  public static boolean setGrayLevel(ObjectNode g, IAST grayLevel) {
    RGBColor rgb = null;
    if (grayLevel.isAST1() || grayLevel.isAST2()) {
      double level = grayLevel.arg1().evalfNaN();
      if (Double.isNaN(level)) {
        return false;
      }
      rgb = RGBColor.getGrayLevel((float) level);
    }
    if (rgb != null) {
      setColorOption(g, rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0);
      return true;
    }
    return false;
  }

  public boolean setHueColor(ArrayNode arrayNode, IAST hueColor) {
    RGBColor rgb = RGBColor.hueToRGB(hueColor);
    if (hueColor.argSize() == 4) {
      opacity = hueColor.arg4().toDoubleDefault(1.0);
    }
    if (rgb != null) {
      ObjectNode g = arrayNode.objectNode();
      setColorOption(g, rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0);
      arrayNode.add(g);
      optionDouble(arrayNode, "opacity", opacity);
      return true;
    }
    return false;
  }

  double opacity = 1.0;
  double pointSize = GraphicsOptions.MEDIUM_POINTSIZE;
  double thickness = GraphicsOptions.MEDIUM_THICKNESS;
  int fontSize = GraphicsOptions.MEDIUM_FONTSIZE;
  IAST rgbColor;
  int colorIndex = 0;
  boolean joined = false;
  IExpr axes = S.False;
  IExpr axesLabel = S.None;
  IExpr background = S.None;
  IExpr chartLegends = S.None;
  IExpr epilog = F.CEmptyList;
  IExpr frame = S.False;
  IExpr frameTicks = S.None;
  IExpr plotLabel = S.None;
  IExpr plotLegends = S.None;
  IExpr plotRange = S.Automatic;
  IExpr filling = S.None;
  IExpr fillingStyle = S.Automatic;
  IExpr aspectRatio = S.Automatic;
  IExpr dataRange = S.Automatic;

  double[] boundingbox =
      new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE};
  String xScale = "";
  String yScale = "";
  Function<IExpr, IExpr> xFunction;
  Function<IExpr, IExpr> yFunction;

  @Deprecated
  OptionArgs options;

  IASTAppendable optionRules = F.NIL;

  public GraphicsOptions(EvalEngine engine) {
    xFunction = x -> x;
    yFunction = y -> y;
    rgbColor = GraphicsOptions.BLACK;
    options = new OptionArgs(engine);
    thickness = TINY_THICKNESS;
  }

  public void addOption(IExpr rule) {
    if (optionRules == F.NIL) {
      optionRules = F.ListAlloc();
    }
    for (int i = 1; i < optionRules.size(); i++) {
      IAST optionRule = (IAST) optionRules.get(i);
      if (optionRule.first().equals(rule.first())) {
        optionRules.set(i, rule);
        return;
      }
    }
    optionRules.append(rule);
  }

  public void addPadding() {
    addPadding(this.boundingbox);
  }

  /** {@code PlotMarkers} value, or {@link S#Automatic} for the plain point markers. */
  private IExpr plotMarkers = S.Automatic;

  /** {@code Mesh} value, or {@link S#None}. */
  private IExpr mesh = S.None;

  public void setPlotMarkers(IExpr plotMarkers) {
    this.plotMarkers = plotMarkers == null ? S.Automatic : plotMarkers;
  }

  public void setMesh(IExpr mesh) {
    this.mesh = mesh == null ? S.None : mesh;
  }

  public IExpr mesh() {
    return mesh;
  }

  /** True when {@code Mesh} asks for the sample positions to be marked. */
  public boolean hasMesh() {
    return mesh != null && !mesh.isNone() && !mesh.isFalse() && mesh != S.Automatic;
  }

  /**
   * The marker to draw at each data point of the given curve, or {@link F#NIL} when the plain point
   * marker should be used.
   *
   * <p>
   * A string, or a list of them, is drawn as a label centred on the point; the list cycles over the
   * curves the way the colours do.
   */
  private IExpr markerFor(int curveIndex) {
    if (plotMarkers == null || plotMarkers == S.Automatic || plotMarkers.isNone()) {
      return F.NIL;
    }
    if (plotMarkers.isList() && ((IAST) plotMarkers).argSize() > 0) {
      IAST list = (IAST) plotMarkers;
      IExpr marker = list.get(Math.floorMod(curveIndex, list.argSize()) + 1);
      // a {marker, size} pair names the marker in its first element
      if (marker.isList() && ((IAST) marker).argSize() >= 1) {
        marker = ((IAST) marker).arg1();
      }
      return marker;
    }
    return plotMarkers;
  }

  /**
   * Turn a curve's points into primitives: a line when the curve is joined, markers when
   * {@code PlotMarkers} asks for them, and points otherwise. {@code Mesh} adds the sample positions
   * on top of a joined curve, which would not otherwise show where the samples fall.
   */
  public IAST addPoints(IAST pointPrimitives) {
    IExpr marker = markerFor(colorIndex);
    boolean showSamples = hasMesh();
    // a ColorFunction paints the curve along its length, so it replaces the single line or the
    // single point set with one piece per step
    IAST body = colorFunctionCurve(pointPrimitives);
    if (body.isNIL() && joined) {
      body = interpolatedCurve(pointPrimitives);
    }
    if (body.isNIL()) {
      body = joined ? F.Line(pointPrimitives) : F.Point(pointPrimitives);
    }
    if (marker.isNIL() && !showSamples) {
      return body;
    }
    IASTAppendable out = F.ListAlloc(pointPrimitives.size() + 2);
    if (joined) {
      out.append(body);
    }
    if (marker.isPresent()) {
      // A continuous curve is sampled adaptively and can carry a thousand points; a marker on each
      // one is an unreadable smear. Space them out instead, following Mesh when it says how many.
      IAST markerPoints =
          joined ? meshSubset(pointPrimitives, showSamples ? meshCount() : DEFAULT_MARKER_COUNT)
              : pointPrimitives;
      for (int i = 1; i < markerPoints.size(); i++) {
        IExpr point = markerPoints.get(i);
        if (point.isList()) {
          out.append(F.Text(marker, (IAST) point));
        }
      }
    } else if (joined) {
      // Mesh on a joined curve: mark where the samples actually are
      out.append(F.Point(meshSubset(pointPrimitives, meshCount())));
    } else {
      out.append(body);
    }
    return out;
  }

  /** How many markers a joined curve gets when nothing asked for a particular number. */
  private static final int DEFAULT_MARKER_COUNT = 16;

  /** {@code ColorFunction}, or {@link S#Automatic} when the curve keeps its own colour. */
  private IExpr colorFunction = S.Automatic;
  /** {@code ColorFunctionScaling}: whether the function sees positions scaled into 0..1. */
  private boolean colorFunctionScaling = true;

  /**
   * Read {@code ColorFunction} and {@code ColorFunctionScaling} off the call.
   *
   * <p>
   * Both sit outside the positional option block, so they have to be read from the original
   * expression rather than by index.
   *
   * @param originalAST the unevaluated call
   */
  public void readColorFunction(IAST originalAST) {
    colorFunction = optionValue(originalAST, S.ColorFunction, S.Automatic);
    colorFunctionScaling = !optionValue(originalAST, S.ColorFunctionScaling, S.True).isFalse();
    interpolationOrder =
        optionValue(originalAST, S.InterpolationOrder, S.Automatic).toIntDefault(-1);
  }

  /**
   * {@code InterpolationOrder}: how the points of a joined curve are connected; -1 for the usual
   * straight segments.
   */
  private int interpolationOrder = -1;

  /**
   * Join the points of a curve the way {@code InterpolationOrder} asks.
   *
   * <p>
   * Order zero holds each value until the next one, which draws the curve as steps. Order one is
   * the straight segments a plot normally uses. Anything higher is drawn as a spline through the
   * points, which is as close as this renderer gets to a higher order interpolation.
   *
   * @param points the curve
   * @return the primitive to draw, or {@link F#NIL} to use the usual line
   */
  private IAST interpolatedCurve(IAST points) {
    if (interpolationOrder < 0 || interpolationOrder == 1 || points.argSize() < 2) {
      return F.NIL;
    }
    if (interpolationOrder >= 2) {
      return F.BSplineCurve(points);
    }
    // order zero: hold each value until the next point's position
    IASTAppendable steps = F.ListAlloc(points.argSize() * 2);
    for (int i = 1; i < points.size(); i++) {
      IExpr point = points.get(i);
      if (!point.isList() || ((IAST) point).argSize() < 2) {
        return F.NIL;
      }
      steps.append(point);
      if (i + 1 < points.size() && points.get(i + 1).isList()) {
        steps.append(F.List(((IAST) points.get(i + 1)).arg1(), ((IAST) point).arg2()));
      }
    }
    return F.Line(steps);
  }

  /**
   * Apply a named {@code PlotTheme}.
   *
   * <p>
   * A theme replaces the plot's own defaults for framing, grid lines and line weight, but loses to
   * anything the caller wrote in the same call -- which is why it is read from the original
   * expression: a setting that is absent there came from the defaults and the theme may have it.
   *
   * <p>
   * These are approximations. Themes also carry colour palettes, tick placement and font choices
   * that this renderer has no equivalent for; what is reproduced here is the framing and weight,
   * which is what distinguishes them at a glance.
   *
   * @param originalAST the unevaluated call
   */
  public void applyPlotTheme(IAST originalAST) {
    IExpr theme = optionValue(originalAST, S.PlotTheme, F.NIL);
    if (!theme.isPresent()) {
      return;
    }
    if (theme.isList() && theme.size() > 1) {
      // a list of themes: the first one that is known wins
      IAST list = (IAST) theme;
      for (int i = 1; i < list.size(); i++) {
        if (applyNamedTheme(originalAST, list.get(i))) {
          return;
        }
      }
      return;
    }
    applyNamedTheme(originalAST, theme);
  }

  /** @return whether the name was one of the themes this renderer knows */
  private boolean applyNamedTheme(IAST originalAST, IExpr theme) {
    if (!theme.isString()) {
      return false;
    }
    String name = theme.toString();
    boolean frame;
    boolean grid;
    double thickness;
    if ("Detailed".equalsIgnoreCase(name) || "Scientific".equalsIgnoreCase(name)) {
      frame = true;
      grid = "Detailed".equalsIgnoreCase(name);
      thickness = PLOT_THICKNESS;
    } else if ("Business".equalsIgnoreCase(name) || "Web".equalsIgnoreCase(name)) {
      frame = false;
      grid = true;
      thickness = 3.0;
    } else if ("Minimal".equalsIgnoreCase(name)) {
      frame = false;
      grid = false;
      thickness = 1.0;
    } else if ("Classic".equalsIgnoreCase(name) || "Default".equalsIgnoreCase(name)) {
      frame = false;
      grid = false;
      thickness = PLOT_THICKNESS;
    } else {
      return false;
    }

    IExpr callerFrame = optionValue(originalAST, S.Frame, F.NIL);
    if (!callerFrame.isPresent()) {
      // as a rule rather than through setFrame: the field it sets is never emitted, so the
      // positional Frame value would survive and the theme would draw no frame
      addOption(F.Rule(S.Frame, frame ? S.True : S.False));
    }
    // A framed plot draws its scale on the frame, so the axes would only repeat it. This follows
    // the frame the plot actually ends up with: a caller who turned the frame off still wants the
    // axes, whatever the theme would have done on its own.
    boolean framed = callerFrame.isPresent() ? callerFrame.isTrue() : frame;
    if (!optionValue(originalAST, S.Axes, F.NIL).isPresent()) {
      setAxes(framed ? S.False : S.True);
    }
    if (!optionValue(originalAST, S.GridLines, F.NIL).isPresent()) {
      addOption(F.Rule(S.GridLines, grid ? S.Automatic : S.None));
    }
    if (!optionValue(originalAST, S.PlotStyle, F.NIL).isPresent()) {
      setCurveThickness(thickness);
    }
    return true;
  }

  /**
   * A curve painted along its length by {@code ColorFunction}.
   *
   * <p>
   * Each step of the curve becomes its own piece, coloured from where that piece sits. A named
   * gradient is a function of one argument and gets the position along x; anything else is offered
   * both coordinates first and one after, so that {@code Function[{x, y}, ...]} and {@code Hue}
   * both work. The positions are scaled into 0..1 over the curve unless
   * {@code ColorFunctionScaling -> False} asks for the raw values.
   *
   * @param points the curve, as a list of {@code {x, y}} pairs
   * @return the coloured pieces, or {@link F#NIL} when no colour function applies
   */
  private IAST colorFunctionCurve(IAST points) {
    if (colorFunction == null || colorFunction == S.Automatic || colorFunction.isNone()
        || points.argSize() < 1) {
      return F.NIL;
    }
    double minX = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;
    for (int i = 1; i < points.size(); i++) {
      IExpr point = points.get(i);
      if (!point.isList() || ((IAST) point).argSize() < 2) {
        return F.NIL;
      }
      double x = ((IAST) point).arg1().evalfNaN();
      double y = ((IAST) point).arg2().evalfNaN();
      if (!Double.isFinite(x) || !Double.isFinite(y)) {
        return F.NIL;
      }
      minX = Math.min(minX, x);
      maxX = Math.max(maxX, x);
      minY = Math.min(minY, y);
      maxY = Math.max(maxY, y);
    }
    double spanX = maxX - minX;
    double spanY = maxY - minY;

    java.util.Map<Long, IExpr> cache = new java.util.HashMap<>();
    IASTAppendable out = F.ListAlloc(Math.max(1, points.argSize()));
    boolean painted = false;
    int last = joined ? points.argSize() - 1 : points.argSize();
    for (int i = 1; i <= last; i++) {
      IAST from = (IAST) points.get(i);
      double x = from.arg1().evalfNaN();
      double y = from.arg2().evalfNaN();
      if (joined) {
        // colour a step from its middle, so the two ends of it agree
        IAST to = (IAST) points.get(i + 1);
        x = (x + to.arg1().evalfNaN()) / 2.0;
        y = (y + to.arg2().evalfNaN()) / 2.0;
      }
      double u = colorFunctionScaling ? (spanX > 0 ? (x - minX) / spanX : 0.0) : x;
      double v = colorFunctionScaling ? (spanY > 0 ? (y - minY) / spanY : 0.0) : y;
      IExpr color = cachedColor(cache, u, v);
      if (color.isNIL()) {
        return F.NIL;
      }
      painted = true;
      out.append(F.List(color, joined ? F.Line(F.List(from, points.get(i + 1))) : F.Point(from)));
    }
    return painted ? out : F.NIL;
  }

  /**
   * A curve painted by {@code ColorFunction}, for a plot that builds its own line rather than going
   * through {@link #addPoints}.
   *
   * @param points the curve, as a list of {@code {x, y}} pairs
   * @return the coloured pieces, or {@link F#NIL} when no colour function applies
   */
  public IAST colorCurve(IAST points) {
    return colorFunctionCurve(points);
  }

  /**
   * The colour at a position, remembering the ones already worked out.
   *
   * <p>
   * An adaptively sampled curve carries hundreds of steps, and evaluating a colour expression for
   * every one of them is the slow part. Positions are rounded to a step finer than the eye can
   * follow before the lookup, so a smooth curve needs only a few hundred evaluations at most.
   */
  private IExpr cachedColor(java.util.Map<Long, IExpr> cache, double u, double v) {
    long key = ((long) Math.round(u * 512) << 32) ^ (Math.round(v * 512) & 0xffffffffL);
    IExpr cached = cache.get(key);
    if (cached != null) {
      return cached;
    }
    IExpr color = evalColor(u, v);
    cache.put(key, color);
    return color;
  }

  private IExpr evalColor(double u, double v) {
    IExpr function =
        colorFunction.isString() ? F.ColorData(colorFunction) : colorFunction;
    if (!colorFunction.isString()) {
      // a function of the position gets both coordinates when it can take them
      IExpr both = tryColor(F.binaryAST2(function, F.num(u), F.num(v)));
      if (both.isPresent()) {
        return both;
      }
    }
    return tryColor(F.unaryAST1(function, F.num(u)));
  }

  private IExpr tryColor(IExpr call) {
    try {
      IExpr color = EvalEngine.get().evaluate(call);
      return isColorExpr(color) ? color : F.NIL;
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /** The count {@code Mesh} asks for, or -1 for every sample. */
  private int meshCount() {
    return mesh == null ? -1 : mesh.toIntDefault(-1);
  }

  /**
   * The grid {@code Mesh} draws over a surface or a raster: a line per cell boundary.
   *
   * <p>
   * On a grid of cells {@code Mesh} means something different from what it means on a curve. There
   * it marks the sample positions; here it outlines the cells, so the resolution of the underlying
   * grid becomes visible.
   *
   * @param mesh the option value; {@code All} outlines every cell, an integer draws that many lines
   * @param x0 left edge, {@code x1} right edge, {@code y0} bottom, {@code y1} top
   * @param columns number of cells across, {@code rows} number down
   * @return the mesh primitives, or {@link F#NIL} when no mesh was asked for
   */
  public static IExpr meshGrid(IExpr mesh, double x0, double y0, double x1, double y1, int columns,
      int rows) {
    if (mesh == null || mesh.isNone() || mesh.isFalse() || mesh == S.Automatic) {
      return F.NIL;
    }
    int requested = mesh.toIntDefault(-1);
    int verticals = requested > 0 ? requested : columns;
    int horizontals = requested > 0 ? requested : rows;
    if (verticals <= 0 && horizontals <= 0) {
      return F.NIL;
    }
    // a mesh over hundreds of cells would be a solid block of ink rather than a grid
    verticals = Math.min(verticals, 200);
    horizontals = Math.min(horizontals, 200);

    IASTAppendable lines = F.ListAlloc(verticals + horizontals + 2);
    lines.append(F.RGBColor(0.5, 0.5, 0.5));
    lines.append(F.AbsoluteThickness(F.num(0.5)));
    for (int i = 0; i <= verticals; i++) {
      double x = x0 + (x1 - x0) * i / verticals;
      lines.append(F.Line(F.List(F.List(F.num(x), F.num(y0)), F.List(F.num(x), F.num(y1)))));
    }
    for (int j = 0; j <= horizontals; j++) {
      double y = y0 + (y1 - y0) * j / horizontals;
      lines.append(F.Line(F.List(F.List(F.num(x0), F.num(y)), F.List(F.num(x1), F.num(y)))));
    }
    return lines;
  }

  /** {@code BarOrigin -> Bottom}: bars grow upwards from the x axis. This is the default. */
  public static final int BAR_ORIGIN_BOTTOM = 0;
  /** {@code BarOrigin -> Top}: bars hang downwards from the top. */
  public static final int BAR_ORIGIN_TOP = 1;
  /** {@code BarOrigin -> Left}: bars grow rightwards, categories run up the y axis. */
  public static final int BAR_ORIGIN_LEFT = 2;
  /** {@code BarOrigin -> Right}: bars grow leftwards, categories run up the y axis. */
  public static final int BAR_ORIGIN_RIGHT = 3;

  /**
   * Which edge the bars of a chart grow from.
   *
   * @param value the {@code BarOrigin} option value
   * @return one of the {@code BAR_ORIGIN_} constants, {@link #BAR_ORIGIN_BOTTOM} when the value
   *         names no edge
   */
  public static int barOrigin(IExpr value) {
    if (value == null) {
      return BAR_ORIGIN_BOTTOM;
    }
    if (value == S.Top) {
      return BAR_ORIGIN_TOP;
    }
    if (value == S.Left) {
      return BAR_ORIGIN_LEFT;
    }
    if (value == S.Right) {
      return BAR_ORIGIN_RIGHT;
    }
    return BAR_ORIGIN_BOTTOM;
  }

  /** Whether the bars of this origin run along the x axis, putting the categories on the y axis. */
  public static boolean barsAreHorizontal(int barOrigin) {
    return barOrigin == BAR_ORIGIN_LEFT || barOrigin == BAR_ORIGIN_RIGHT;
  }

  /**
   * Whether the value axis of this origin counts away from zero in the negative direction.
   *
   * <p>
   * Bars hanging from the top, or growing leftwards, are drawn at negated coordinates so that the
   * picture comes out the right way round. The axis then needs labels that read positive, which is
   * what {@link #reversedValueTicks} produces.
   */
  public static boolean barsAreReversed(int barOrigin) {
    return barOrigin == BAR_ORIGIN_TOP || barOrigin == BAR_ORIGIN_RIGHT;
  }

  /**
   * One bar, oriented as {@code BarOrigin} asks.
   *
   * @param barOrigin one of the {@code BAR_ORIGIN_} constants
   * @param from where the bar starts along the category axis
   * @param to where it ends along the category axis
   * @param base the value the bar grows from, usually zero
   * @param value the value the bar reaches
   */
  public static IAST barRectangle(int barOrigin, double from, double to, double base,
      double value) {
    double sign = barsAreReversed(barOrigin) ? -1.0 : 1.0;
    double v0 = sign * base;
    double v1 = sign * value;
    if (barsAreHorizontal(barOrigin)) {
      return F.Rectangle(F.List(F.num(v0), F.num(from)), F.List(F.num(v1), F.num(to)));
    }
    return F.Rectangle(F.List(F.num(from), F.num(v0)), F.List(F.num(to), F.num(v1)));
  }

  /**
   * Tick marks for a value axis whose coordinates run negative, labelled with the values the bars
   * actually stand for.
   *
   * @param maxValue the largest bar value, in positive terms
   * @return an explicit tick specification of {@code {position, label}} pairs
   */
  public static IExpr reversedValueTicks(double maxValue) {
    if (!Double.isFinite(maxValue) || maxValue <= 0) {
      return S.Automatic;
    }
    java.util.List<org.matheclipse.core.graphics.svg.TickGenerator.Tick> ticks =
        org.matheclipse.core.graphics.svg.TickGenerator.linear(0, maxValue);
    IASTAppendable spec = F.ListAlloc(ticks.size());
    for (org.matheclipse.core.graphics.svg.TickGenerator.Tick tick : ticks) {
      if (!tick.major) {
        continue;
      }
      spec.append(F.List(F.num(-tick.value), F.stringx(tick.label)));
    }
    return spec.argSize() > 0 ? spec : S.Automatic;
  }

  /**
   * The style of a chart element, combining {@code ChartBaseStyle} with the per-element colour.
   *
   * <p>
   * Whichever is written last wins where the two set the same property. An explicit
   * {@code ChartStyle} is meant to override {@code ChartBaseStyle}, so its colour goes last. A
   * colour that merely came from the default palette does not override anything the caller asked
   * for, so there the base style goes last instead -- otherwise {@code ChartBaseStyle -> Red} would
   * be swallowed by the palette and appear to do nothing.
   *
   * @param baseStyle the {@code ChartBaseStyle} value, or {@link F#NIL} when unset
   * @param color the colour worked out for this element, or {@link F#NIL} for none
   * @param colorIsExplicit whether the colour came from a {@code ChartStyle} the caller wrote
   * @return the style to prepend to the element, or {@link F#NIL} when there is none
   */
  public static IExpr chartElementStyle(IExpr baseStyle, IExpr color, boolean colorIsExplicit) {
    boolean hasBase = baseStyle != null && baseStyle.isPresent() && baseStyle != S.Automatic
        && !baseStyle.isNone();
    boolean hasColor = color != null && color.isPresent() && color != S.Automatic;
    if (!hasBase) {
      return hasColor ? color : F.NIL;
    }
    if (!hasColor) {
      return baseStyle.isAST(S.Directive) ? baseStyle : F.Directive(baseStyle);
    }
    IASTAppendable directive = F.ast(S.Directive, 3);
    if (colorIsExplicit) {
      appendStyle(directive, baseStyle);
      directive.append(color);
    } else {
      directive.append(color);
      appendStyle(directive, baseStyle);
    }
    return directive;
  }

  /** Add a style to a directive, flattening a {@code Directive} rather than nesting it. */
  private static void appendStyle(IASTAppendable directive, IExpr style) {
    if (style.isAST(S.Directive)) {
      directive.appendArgs((IAST) style);
    } else {
      directive.append(style);
    }
  }

  /** {@code LabelingFunction} placed the label above the element. */
  public static final int LABELING_ABOVE = 0;
  /** {@code LabelingFunction} placed the label below the element. */
  public static final int LABELING_BELOW = 1;
  /** {@code LabelingFunction} placed the label inside the element. */
  public static final int LABELING_CENTER = 2;
  /** {@code LabelingFunction} asked for no label. */
  public static final int LABELING_NONE = 3;

  /**
   * Where {@code LabelingFunction} wants element labels drawn.
   *
   * @param value the option value
   * @return one of the {@code LABELING_} constants; {@link #LABELING_NONE} for {@code None} and for
   *         {@code Automatic}, which labels nothing unless labels were asked for separately
   */
  public static int labelingPlacement(IExpr value) {
    if (value == null || value == S.Automatic || value.isNone() || !value.isSymbol()) {
      return LABELING_NONE;
    }
    // Above and Below are not builtin symbols, so the placements are matched by name. The
    // comparison ignores case because the relaxed parser lower-cases symbols it does not know.
    String name = value.toString();
    if ("Above".equalsIgnoreCase(name) || "Top".equalsIgnoreCase(name)) {
      return LABELING_ABOVE;
    }
    if ("Below".equalsIgnoreCase(name) || "Bottom".equalsIgnoreCase(name)) {
      return LABELING_BELOW;
    }
    if ("Center".equalsIgnoreCase(name)) {
      return LABELING_CENTER;
    }
    return LABELING_NONE;
  }

  /**
   * The text of an element label, after any labelling function has had a look at it.
   *
   * @param labelingFunction the {@code LabelingFunction} value; a function is applied to the value
   * @param value the value the element stands for
   * @return the expression to draw, or {@link F#NIL} when nothing should be drawn
   */
  public static IExpr labelingText(IExpr labelingFunction, IExpr value, EvalEngine engine) {
    if (labelingFunction == null || labelingFunction.isNone() || labelingFunction == S.Automatic) {
      return F.NIL;
    }
    if (labelingPlacement(labelingFunction) != LABELING_NONE) {
      return value;
    }
    // anything else is taken to be a function of the value
    IExpr applied = engine.evaluate(F.unaryAST1(labelingFunction, value));
    return applied.isPresent() ? applied : value;
  }

  /**
   * Options the converter reads that the positional option block does not carry.
   *
   * <p>
   * The per-family option sets accept more names than {@code listPlotDefaultOptionKeys} lists, and
   * only the listed ones are paired back into rules. Anything here is copied out of the call itself
   * so that it reaches the {@code Graphics} that gets rendered.
   */
  private static final ISymbol[] FORWARDED_OPTIONS =
      {S.LabelStyle, S.ClippingStyle, S.ScalingFunctions};

  /**
   * Carry the options the converter understands, but the positional block drops, into the emitted
   * rules.
   *
   * @param originalAST the unevaluated call
   */
  public void forwardOptions(IAST originalAST) {
    for (ISymbol name : FORWARDED_OPTIONS) {
      IExpr value = optionValue(originalAST, name, F.NIL);
      if (value.isPresent()) {
        addOption(F.Rule(name, value));
      }
    }
  }

  /**
   * Thin a matrix down to at most {@code maxPoints} rows and columns.
   *
   * <p>
   * This is what {@code MaxPlotPoints} asks for: a picture of a large matrix that is drawn from a
   * sample of it rather than from every entry. Rows and columns are taken at even spacing, keeping
   * the first and last of each so the picture still covers the whole matrix.
   *
   * @param matrix a list of rows
   * @param maxPoints the most rows and columns to keep, or a value below 1 to keep everything
   * @return the thinned matrix, or {@link F#NIL} when nothing needed removing
   */
  public static IAST downsampleMatrix(IAST matrix, int maxPoints) {
    if (maxPoints < 1 || matrix.argSize() < 1) {
      return F.NIL;
    }
    int widest = 0;
    for (int i = 1; i < matrix.size(); i++) {
      if (matrix.get(i).isList()) {
        widest = Math.max(widest, ((IAST) matrix.get(i)).argSize());
      }
    }
    if (matrix.argSize() <= maxPoints && widest <= maxPoints) {
      return F.NIL;
    }
    int[] rows = evenIndices(matrix.argSize(), maxPoints);
    IASTAppendable out = F.ListAlloc(rows.length);
    for (int row : rows) {
      IExpr rowExpr = matrix.get(row);
      if (!rowExpr.isList()) {
        out.append(rowExpr);
        continue;
      }
      IAST rowAst = (IAST) rowExpr;
      int[] columns = evenIndices(rowAst.argSize(), maxPoints);
      IASTAppendable thinned = F.ListAlloc(columns.length);
      for (int column : columns) {
        thinned.append(rowAst.get(column));
      }
      out.append(thinned);
    }
    return out;
  }

  /** {@code count} one-based indices spread evenly over {@code total}, ends included. */
  private static int[] evenIndices(int total, int count) {
    if (total <= count) {
      int[] all = new int[total];
      for (int i = 0; i < total; i++) {
        all[i] = i + 1;
      }
      return all;
    }
    int[] picked = new int[count];
    for (int i = 0; i < count; i++) {
      picked[i] = count == 1 ? 1 : 1 + (int) Math.round((double) i * (total - 1) / (count - 1));
    }
    return picked;
  }

  /**
   * The datasets of a chart, whether the caller passed one or several.
   *
   * @param data the first argument of the chart
   * @return a list of lists, each a dataset
   */
  public static IAST chartDatasets(IAST data) {
    for (int i = 1; i < data.size(); i++) {
      if (data.get(i).isList()) {
        // a list among the entries means these are datasets rather than values
        return data;
      }
    }
    return F.List(data);
  }

  /**
   * An evenly spaced subset of the points.
   *
   * @param requested how many to keep, or a value below 1 to keep them all
   */
  private IAST meshSubset(IAST points, int requested) {
    int total = points.argSize();
    if (requested <= 0 || requested >= total) {
      return points;
    }
    IASTAppendable subset = F.ListAlloc(requested);
    for (int i = 0; i < requested; i++) {
      int index = 1 + (int) Math.round((double) i * (total - 1) / Math.max(1, requested - 1));
      subset.append(points.get(Math.min(total, index)));
    }
    return subset;
  }

  public double[] boundingBox() {
    return boundingbox;
  }

  public IExpr chartLegends() {
    return chartLegends;
  }

  public GraphicsOptions copy() {
    GraphicsOptions graphicsOptions = new GraphicsOptions(EvalEngine.get());
    graphicsOptions.boundingbox = new double[] {this.boundingbox[0], this.boundingbox[1],
        this.boundingbox[2], this.boundingbox[3]};
    graphicsOptions.axes = this.axes;
    graphicsOptions.axesLabel = this.axesLabel;
    graphicsOptions.boundingbox = new double[boundingbox.length];
    System.arraycopy(this.boundingbox, 0, graphicsOptions.boundingbox, 0, boundingbox.length);
    graphicsOptions.colorIndex = this.colorIndex;
    graphicsOptions.filling = this.filling;
    graphicsOptions.fontSize = this.fontSize;
    graphicsOptions.frame = this.frame;
    graphicsOptions.joined = this.joined;
    graphicsOptions.opacity = this.opacity;
    graphicsOptions.options = this.options;
    graphicsOptions.rgbColor = this.rgbColor;
    graphicsOptions.pointSize = this.pointSize;
    graphicsOptions.plotLabel = this.plotLabel;
    graphicsOptions.plotLegends = this.plotLegends;
    graphicsOptions.thickness = this.thickness;
    graphicsOptions.xFunction = this.xFunction;
    graphicsOptions.yFunction = this.yFunction;
    graphicsOptions.optionRules = optionRules.copyAppendable();
    graphicsOptions.aspectRatio = this.aspectRatio;
    graphicsOptions.dataRange = this.dataRange;
    graphicsOptions.chartLegends = this.chartLegends;
    graphicsOptions.plotMarkers = this.plotMarkers;
    graphicsOptions.mesh = this.mesh;
    graphicsOptions.colorFunction = this.colorFunction;
    graphicsOptions.colorFunctionScaling = this.colorFunctionScaling;
    graphicsOptions.curveThickness = this.curveThickness;
    graphicsOptions.interpolationOrder = this.interpolationOrder;
    return graphicsOptions;
  }

  public IExpr filling() {
    return filling;
  }

  public IExpr fillingStyle() {
    return fillingStyle;
  }

  public int fontSize() {
    return fontSize;
  }

  public int getColorIndex() {
    return colorIndex;
  }


  public boolean graphics2DAxes(ObjectNode axes) {
    // OptionArgs options = options();

    ArrayNode scalingArray = null;
    IExpr scalingFunctions = options.getOption(S.$Scaling);
    if (scalingFunctions.isPresent()) {
      if (scalingFunctions.isList1()) {
        scalingArray = GraphicsOptions.jsonObjectMapper().createArrayNode();
        setXFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions.first()));
        scalingArray.add("none");
        setYFunction(y -> y);
      } else if (scalingFunctions.isList2()) {
        scalingArray = GraphicsOptions.jsonObjectMapper().createArrayNode();
        setXFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions.first()));
        setYFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions.second()));
      } else if (!scalingFunctions.isList()) {
        scalingArray = GraphicsOptions.jsonObjectMapper().createArrayNode();
        scalingArray.add("none");
        setXFunction(x -> x);
        setYFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions));
      } else {
        return false;
      }
    }

    ObjectNode g = GraphicsOptions.jsonObjectMapper().createObjectNode();
    IExpr axesOptions = this.axes; // options.getOption(S.Axes);
    if (!axesOptions.isPresent()) {
      axesOptions = S.False;
    }
    if (axesOptions.isList2()) {
      IExpr a1 = axesOptions.first();
      IExpr a2 = axesOptions.second();
      hasAxesJSON(g, a1, a2);
    } else if (axesOptions.isTrue()) {
      hasAxesJSON(g, S.True, S.True);
    } else if (axesOptions.isFalse()) {
      hasAxesJSON(g, S.False, S.False);
    } else {
      return false;
    }

    if (scalingArray != null) {
      g.set("scaling", scalingArray);
    }
    axes.set("axes", g);
    return true;
  }

  public IAST getListOfRules() {
    IASTAppendable rules = F.ListAlloc(32);
    // Explicit fields take precedence
    rules.append(F.Rule(S.Axes, axes));
    rules.append(F.Rule(S.AxesLabel, axesLabel));
    rules.append(F.Rule(S.PlotLabel, plotLabel));
    rules.append(F.Rule(S.PlotLegends, plotLegends));
    rules.append(F.Rule(S.Filling, filling));
    rules.append(F.Rule(S.AspectRatio, aspectRatio));

    if (optionRules.isPresent()) {
      for (int i = 1; i < optionRules.size(); i++) {
        IExpr rule = optionRules.get(i);
        if (rule.isRuleAST()) {
          IExpr key = ((IAST) rule).arg1();
          // Filter out keys we already added explicitly to avoid overriding with defaults
          if (key == S.Axes || key == S.AxesLabel || key == S.PlotLabel
              || key == S.PlotLegends || key == S.Filling || key == S.AspectRatio) {
            continue;
          }
        }
        rules.append(rule);
      }
    }
    return rules;
  }

  private static void hasAxesJSON(ObjectNode g, IExpr a1, IExpr a2) {
    ArrayNode an = GraphicsOptions.jsonObjectMapper().createArrayNode();
    if (a1.isTrue()) {
      an.add(true);
    } else {
      an.add(false);
    }
    if (a2.isTrue()) {
      an.add(true);
    } else {
      an.add(false);
    }
    g.set("hasaxes", an);
  }

  public void graphics2DFilling(ArrayNode arrayNode, OptionArgs options) {
    IExpr filling = options.getOption(S.Filling);
    if (filling.isPresent()) {
      ObjectNode g = GraphicsOptions.jsonObjectMapper().createObjectNode();
      g.put("option", "filling");
      // if (filling.isNone()) {
      // g.put("value", "none");
      // } else
      if (filling == S.Axis) {
        g.put("value", "axis");
      } else if (filling == S.Top) {
        g.put("value", "top");
      } else if (filling == S.Bottom) {
        g.put("value", "bottom");
      } else {
        return;
      }
      arrayNode.add(g);
    }
  }

  public boolean graphicsExtent2D(ObjectNode objectNode, IExpr plotRange) {
    if (plotRange.isList2()) {
      IExpr arg1 = plotRange.first();
      IExpr arg2 = plotRange.second();
      if (arg1.isList2() && arg2.isList2()) {
        double xMin = arg1.first().evalfNaN();
        double xMax = arg1.second().evalfNaN();
        double yMin = arg2.first().evalfNaN();
        double yMax = arg2.second().evalfNaN();
        if (Double.isNaN(xMin) || Double.isNaN(xMax) || Double.isNaN(yMin) || Double.isNaN(yMax)) {
          return false;
        }
        boundingbox[0] = xMin;
        boundingbox[1] = xMax;
        boundingbox[2] = yMin;
        boundingbox[3] = yMax;
        objectNode.put("xmin", boundingbox[0]);
        objectNode.put("xmax", boundingbox[1]);
        objectNode.put("ymin", boundingbox[2]);
        objectNode.put("ymax", boundingbox[3]);
        return true;
      } else {
        objectNode.put("xmin", boundingbox[0]);
        objectNode.put("xmax", boundingbox[1]);
        double yMin = arg1.evalfNaN();
        double yMax = arg2.evalfNaN();
        if (Double.isNaN(yMin) || Double.isNaN(yMax)) {
          return false;
        }
        boundingbox[2] = yMin;
        boundingbox[3] = yMax;
        objectNode.put("ymin", boundingbox[2]);
        objectNode.put("ymax", boundingbox[3]);
        return true;
      }
    }
    return false;
  }

  public boolean graphicsExtent2D(ObjectNode objectNode) {
    objectNode.put("xmin", boundingbox[0]);
    objectNode.put("xmax", boundingbox[1]);
    objectNode.put("ymin", boundingbox[2]);
    objectNode.put("ymax", boundingbox[3]);
    return true;
  }

  public int incColorIndex() {
    if (PLOT_COLORS.length - 1 == colorIndex) {
      colorIndex = 1;
      return PLOT_COLORS.length - 1;
    }
    return colorIndex++;
  }

  public static int incColorIndex(int colorIndex) {
    if (PLOT_COLORS.length - 1 == colorIndex) {
      colorIndex = 1;
      return PLOT_COLORS.length - 1;
    }
    return ++colorIndex;
  }

  public boolean isJoined() {
    return joined;
  }


  public void mergeOptions(IAST listOfOptions, double[] yMinMax) {
    for (int i = 1; i < listOfOptions.size(); i++) {
      if (listOfOptions.get(i).isRuleAST()) {
        IExpr option = listOfOptions.get(i).first();
        if (option == S.PlotRange) {
          IExpr plotRange = listOfOptions.get(i).second();
          if (plotRange.isList2()) {
            IExpr arg1 = plotRange.first();
            IExpr arg2 = plotRange.second();
            if (arg1.isList2() && arg2.isList2()) {
              double xMin = arg1.first().evalfNaN();
              double xMax = arg1.second().evalfNaN();
              double yMin = arg2.first().evalfNaN();
              double yMax = arg2.second().evalfNaN();
              // only assign if every bound is numeric - ignore false plot ranges
              if (!Double.isNaN(xMin) && !Double.isNaN(xMax) && !Double.isNaN(yMin)
                  && !Double.isNaN(yMax)) {
                boundingbox[0] = xMin;
                boundingbox[1] = xMax;
                boundingbox[2] = yMin;
                boundingbox[3] = yMax;
                yMinMax[0] = yMin;
                yMinMax[1] = yMax;
              }
            } else {
              double yMin = arg1.evalfNaN();
              double yMax = arg2.evalfNaN();
              if (!Double.isNaN(yMin) && !Double.isNaN(yMax)) {
                boundingbox[2] = yMin;
                boundingbox[3] = yMax;
                yMinMax[0] = yMin;
                yMinMax[1] = yMax;
              }
            }
          }
        }
      }
    }
  }

  public double opacity() {
    return opacity;
  }

  @Deprecated
  public OptionArgs options() {
    return options;
  }

  public IExpr aspectRatio() {
    return aspectRatio;
  }

  public IExpr axes() {
    return axes;
  }

  public IExpr frame() {
    return frame;
  }

  public IExpr axesLabel() {
    return axesLabel;
  }

  public IExpr plotLabel() {
    return plotLabel;
  }

  public IExpr plotLegends() {
    return plotLegends;
  }

  public IExpr plotRange() {
    return plotRange;
  }

  /**
   * Create <code>PlotRange->{{x1,x2},{y1,y2}}</code> rule from bounding box.
   * 
   */
  public IAST createPlotRangeFromBoundingBox() {
    return F.Rule(S.PlotRange, F.List(F.List(F.num(boundingbox[0]), F.num(boundingbox[1])),
        F.List(F.num(boundingbox[2]), F.num(boundingbox[3]))));
  }

  public IAST point(double x, double y) {
    return F.List(F.num(x), F.num(y));
  }

  public double pointSize() {
    return pointSize;
  }

  public double pointSize(IAST pointSizeAST) {
    if (pointSizeAST.isAST(S.PointSize, 2)) {
      IExpr arg1 = pointSizeAST.arg1();
      if (arg1 == S.Large) {
        pointSize = LARGE_POINTSIZE;
      } else if (arg1 == S.Medium) {
        pointSize = MEDIUM_POINTSIZE;
      } else if (arg1 == S.Small) {
        pointSize = SMALL_POINTSIZE;
      } else if (arg1 == S.Tiny) {
        pointSize = TINY_POINTSIZE;
      } else {
        double size = arg1.evalfNaN();
        pointSize = Double.isNaN(size) ? MEDIUM_POINTSIZE : size;
      }
    }
    return pointSize;
  }

  public void setAxes(IExpr axes) {
    this.axes = axes;
  }

  public void setFrame(IExpr frame) {
    this.frame = frame;
  }

  public void setAxesLabel(IExpr axesLabel) {
    this.axesLabel = axesLabel;
  }

  public void setBoundingBox(double[] boundingbox) {
    this.boundingbox = boundingbox;
  }

  public void setBoundingBoxScaled(double[] boundingbox) {
    // first do all evaluations, so if one of them isn't numeric no value is changed
    double x0 = xFunction.apply(F.num(boundingbox[0])).evalfNaN();
    double x1 = xFunction.apply(F.num(boundingbox[1])).evalfNaN();
    double y0 = yFunction.apply(F.num(boundingbox[2])).evalfNaN();
    double y1 = yFunction.apply(F.num(boundingbox[3])).evalfNaN();
    if (Double.isNaN(x0) || Double.isNaN(x1) || Double.isNaN(y0) || Double.isNaN(y1)) {
      return;
    }
    if (x0 < this.boundingbox[0]) {
      this.boundingbox[0] = x0;
    }
    if (x1 > this.boundingbox[1]) {
      this.boundingbox[1] = x1;
    }
    if (y0 < this.boundingbox[2]) {
      this.boundingbox[2] = y0;
    }
    if (y1 > this.boundingbox[3]) {
      this.boundingbox[3] = y1;
    }
  }

  public void setBoundingBoxScaled(double x, double y) {
    // first do all evaluations, so if one of them isn't numeric no value is changed
    double xValue = xFunction.apply(F.num(x)).evalfNaN();
    double yValue = yFunction.apply(F.num(y)).evalfNaN();
    if (Double.isNaN(xValue) || Double.isNaN(yValue)) {
      return;
    }
    if (xValue < this.boundingbox[0]) {
      this.boundingbox[0] = xValue;
    }
    if (xValue > this.boundingbox[1]) {
      this.boundingbox[1] = xValue;
    }

    if (yValue < this.boundingbox[2]) {
      this.boundingbox[2] = yValue;
    }
    if (yValue > this.boundingbox[3]) {
      this.boundingbox[3] = yValue;
    }
  }

  public void setColor(ObjectNode json) {
    if (rgbColor.isPresent()) {
      if (rgbColor.isAST(S.RGBColor, 4, 5)) {
        double red = rgbColor.arg1().toDoubleDefault(0.0);
        double green = rgbColor.arg2().toDoubleDefault(0.0);
        double blue = rgbColor.arg3().toDoubleDefault(0.0);
        setColor(json, red, green, blue);
        //
        return;
      } else if (rgbColor.isAST(S.RGBColor, 1) && rgbColor.arg1().isList3()) {
        IAST list = (IAST) rgbColor.arg1();
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        setColor(json, red, green, blue);
        return;
      }
    }
    // black
    setColor(json, 0.0, 0.0, 0.0);
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  public void setJoined(boolean joined) {
    this.joined = joined;
  }

  public void setOpacity(double opacity) {
    this.opacity = opacity;
  }

  @Deprecated
  public void setOptions(OptionArgs options) {
    this.options = options;
  }

  public void setPlotLabel(IExpr plotLabel) {
    this.plotLabel = plotLabel;
  }

  public void setChartLegends(IExpr chartLegends) {
    this.chartLegends = chartLegends;
  }

  public void setPlotLegends(IExpr plotLegends) {
    this.plotLegends = plotLegends;
  }

  public void setPointSize(double pointSize) {
    this.pointSize = pointSize;
  }


  public void setRGBColor(IAST color) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
        double red = color.arg1().toDoubleDefault(0.0);
        double green = color.arg2().toDoubleDefault(0.0);
        double blue = color.arg3().toDoubleDefault(0.0);
        rgbColor = F.RGBColor(red, green, blue);
        if (color.argSize() == 4) {
          opacity = color.arg4().toDoubleDefault(1.0);
        }
        return;
      } else if (color.isAST(S.RGBColor, 1) && color.arg1().isList3()) {
        IAST list = (IAST) color.arg1();
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        rgbColor = F.RGBColor(red, green, blue);
        return;
      }
    }
    // black
    rgbColor = F.RGBColor(0.0, 0.0, 0.0);
  }

  public void setScalingFunctions(IExpr[] options) {
    IExpr scalingFunctions = options[GraphicsOptions.X_$SCALING];
    if (scalingFunctions.isPresent())
      setScalingFunctions(scalingFunctions);
  }

  // public void setScalingFunctions(IExpr scalingFunctions) {
  // if (scalingFunctions.isList1()) {
  // setXFunction(getScaling(scalingFunctions.first()));
  // setYFunction(y -> y);
  // } else if (scalingFunctions.isList2()) {
  // setXFunction(getScaling(scalingFunctions.first()));
  // setYFunction(getScaling(scalingFunctions.second()));
  // } else if (!scalingFunctions.isList()) {
  // setXFunction(x -> x);
  // setYFunction(getScaling(scalingFunctions));
  // }
  // }

  public void setScalingFunctions(IExpr scalingFunctions) {
    if (scalingFunctions.isList1()) {
      setXScale(scalingStr(scalingFunctions.first()));
      setXFunction(getScaling(scalingFunctions.first()));
      setYScale("Linear");
      setYFunction(y -> y);
    } else if (scalingFunctions.isList2()) {
      setXScale(scalingStr(scalingFunctions.first()));
      setYScale(scalingStr(scalingFunctions.second()));
      setXFunction(getScaling(scalingFunctions.first()));
      setYFunction(getScaling(scalingFunctions.second()));
    } else if (!scalingFunctions.isList()) {
      setXScale("Linear");
      setXFunction(x -> x);
      setYScale(scalingStr(scalingFunctions));
      setYFunction(getScaling(scalingFunctions));
    }
  }

  private String scalingStr(IExpr scale) {
    if (scale.isString()) {
      String s = scale.toString();
      if (s.equals("Log") || s.equals("Log10") || s.equals("Log2"))
        return s;
    }
    return "Linear";
  }

  public void setFilling(IExpr filling) {
    this.filling = filling;
  }

  public void setFillingStyle(IExpr fillingStyle) {
    this.fillingStyle = fillingStyle;
  }

  public void setAspectRatio(IExpr aspectRatio) {
    this.aspectRatio = aspectRatio;
  }

  /** Stroke width of a plotted curve, which {@code PlotTheme} may change. */
  private double curveThickness = PLOT_THICKNESS;

  /** Stroke width of a plotted curve, in printer's points. */
  public double curveThickness() {
    return curveThickness;
  }

  public void setCurveThickness(double curveThickness) {
    this.curveThickness = curveThickness;
  }

  public void setThickness(double thickness) {
    this.thickness = thickness;
  }

  public void setXFunction(Function<IExpr, IExpr> xFunction) {
    this.xFunction = xFunction;
  }

  public void setXScale(String xScale) {
    this.xScale = xScale;
  }

  public void setYFunction(Function<IExpr, IExpr> yFunction) {
    this.yFunction = yFunction;
  }

  public void setYScale(String yScale) {
    this.yScale = yScale;
  }

  public double thickness() {
    return thickness;
  }

  public Function<IExpr, IExpr> xFunction() {
    return xFunction;
  }

  public String xScale() {
    return xScale;
  }

  public Function<IExpr, IExpr> yFunction() {
    return yFunction;
  }

  public String yScale() {
    return yScale;
  }

  /**
   * The y axis scaling that will actually be emitted.
   *
   * <p>
   * The log plots leave their data linear and declare the scaling as a {@code $Scaling} option rule
   * for the converter to apply. They add that rule late, after {@link #setGraphicOptions} has
   * already reset the {@link #yScale} field from the (still {@code Automatic}) option value, so the
   * field cannot be trusted once the rule is present. Anything that needs to know whether the axis
   * is logarithmic, such as choosing a fill baseline, has to ask here.
   */
  public String effectiveYScale() {
    if (optionRules.isPresent()) {
      for (int i = 1; i < optionRules.size(); i++) {
        IExpr rule = optionRules.get(i);
        if (rule.isRuleAST() && rule.first() == S.$Scaling) {
          IExpr value = ((IAST) rule).second();
          if (value.isList() && ((IAST) value).argSize() >= 2) {
            return ((IAST) value).arg2().toString().replace("\"", "");
          }
          if (value.isString()) {
            return value.toString().replace("\"", "");
          }
        }
      }
    }
    return yScale;
  }

  public void setDataRange(IExpr d) {
    this.dataRange = d;
  }

  public IExpr dataRange() {
    return dataRange;
  }

  public void setFrameTicks(IExpr t) {
    this.frameTicks = t;
  }

  public IExpr frameTicks() {
    return frameTicks;
  }

  public void setBackground(IExpr b) {
    this.background = b;
  }

  public IExpr background() {
    return background;
  }

  public void setEpilog(IExpr b) {
    this.epilog = b;
  }

  public IExpr epilog() {
    return epilog;
  }

  /**
   * @param optionSymbols the option names the calling plot declares, in the order its evaluator
   *        reads them
   * @param options the values registered for the symbol, positionally matching
   *        {@code optionSymbols}
   */
  public void setGraphicOptions(final IBuiltInSymbol[] optionSymbols, IExpr[] options,
      EvalEngine engine) {
    // A plot whose setUp registers a shorter list than its evaluator reads would otherwise index
    // past the end here. The two are meant to agree; where they do not, fall back to the defaults
    // for the missing tail rather than failing the whole plot.
    if (options.length <= X_EPILOG) {
      readOptionRules(optionSymbols, options);
      return;
    }
    if (!options[X_AXES].isFalse())
      setAxes(options[X_AXES]);
    if (options[X_AXESLABEL] != S.None)
      setAxesLabel(options[X_AXESLABEL]);
    if (options[X_PLOTRANGE] != S.Automatic)
      setPlotRange(options[X_PLOTRANGE]);
    if (options[X_JOINED].isTrue())
      setJoined(true);
    if (options[X_PLOTLEGENDS] != S.None)
      setPlotLegends(options[X_PLOTLEGENDS]);
    if (options[X_PLOTLABEL] != S.None)
      setPlotLabel(options[X_PLOTLABEL]);
    if (options[X_PLOTLABELS] != S.None)
      setPlotLegends(options[X_PLOTLABELS]);
    if (options[X_FILLING] != S.None)
      setFilling(options[X_FILLING]);
    if (options[X_FILLINGSTYLE] != S.Automatic)
      setFillingStyle(options[X_FILLINGSTYLE]);
    if (options[X_ASPECTRATIO] != S.Automatic)
      setAspectRatio(options[X_ASPECTRATIO]);
    if (options[X_FRAME].isTrue())
      setFrame(options[X_FRAME]);
    if (options[X_DATARANGE] != S.Automatic)
      setDataRange(options[X_DATARANGE]);
    if (options[X_CHARTLEGENDS] != S.None)
      setChartLegends(options[X_CHARTLEGENDS]);
    if (options[X_FRAMETICKS] != S.None)
      setFrameTicks(options[X_FRAMETICKS]);
    if (options.length > X_BACKGROUND && options[X_BACKGROUND] != S.None)
      setBackground(options[X_BACKGROUND]);
    if (!options[X_EPILOG].equals(F.CEmptyList))
      setEpilog(options[X_EPILOG]);
    setScalingFunctions(options);
    readOptionRules(optionSymbols, options);
  }

  /**
   * Pair each declared option name with its registered value, stopping at the shorter of the two.
   */
  private void readOptionRules(final IBuiltInSymbol[] optionSymbols, IExpr[] options) {
    int count = Math.min(optionSymbols.length, options.length);
    if (count > 0) {
      optionRules = F.ListAlloc(count);
      for (int i = 0; i < count; i++) {
        optionRules.append(F.Rule(optionSymbols[i], options[i]));
      }
    }
  }

  private void setPlotRange(IExpr expr) {
    plotRange = expr;
  }

  public static IBuiltInSymbol[] contourPlotDefaultOptionKeys() {
    return new IBuiltInSymbol[] {S.JSForm, S.Filling, S.Axes, S.PlotRange, S.$Scaling, S.Joined,
        S.PlotLegends, S.PlotLabel, S.AxesLabel, S.PlotStyle, S.GridLines, S.PlotLabels,
        S.FillingStyle, S.AspectRatio, S.Frame, S.ContourShading, S.ColorFunctionScaling,
        S.ContourStyle, S.Background, S.Epilog, //
        S.ImageSize, S.Ticks, S.AxesOrigin, S.AxesStyle, S.FrameStyle, S.FrameLabel,
        S.GridLinesStyle, S.ImagePadding, S.PlotRangePadding, S.Prolog, S.PlotRangeClipping};
  }

  public static IExpr[] contourPlotDefaultOptionValues(boolean jsForm, boolean joined) {
    return new IExpr[] {jsForm ? S.True : S.False, S.None, S.True, S.Automatic, S.Automatic,
        joined ? S.True : S.False, S.None, S.None, S.None, S.None, S.None, S.None, S.Automatic,
        F.Power(S.GoldenRatio, F.CN1), S.False, S.Automatic, S.True, S.Automatic, S.None,
        F.CEmptyList, //
        S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.None, S.Automatic,
        S.Automatic, S.Automatic, F.CEmptyList, S.True};
  }

  public static IBuiltInSymbol[] listPlotDefaultOptionKeys() {
    return new IBuiltInSymbol[] {S.JSForm, S.Filling, S.Axes, S.PlotRange, S.$Scaling, S.Joined,
        S.PlotLegends, S.PlotLabel, S.AxesLabel, S.PlotStyle, S.GridLines, S.PlotLabels,
        S.FillingStyle, S.AspectRatio, S.Frame, S.DataRange, S.ChartLegends, S.FrameTicks,
        S.Background, S.Epilog, //
        S.ImageSize, S.Ticks, S.AxesOrigin, S.AxesStyle, S.FrameStyle, S.FrameLabel,
        S.GridLinesStyle, S.ImagePadding, S.PlotRangePadding, S.Prolog, S.PlotRangeClipping};
  }

  public static IExpr[] listPlotDefaultOptionValues(boolean jsForm, boolean joined) {
    return new IExpr[] {jsForm ? S.True : S.False, S.None, S.True, S.All, S.Automatic,
        joined ? S.True : S.False, S.None, S.None, S.None, S.None, S.None, S.None, S.Automatic,
        // FrameTicks is Automatic so that a framed plot is numbered: with the axes off, which is
        // what a frame is usually paired with, the frame is the only thing left to carry the scale
        F.Power(S.GoldenRatio, F.CN1), S.False, S.Automatic, S.None, S.Automatic, S.None,
        F.CEmptyList, //
        // presentation options the converter understands; the defaults are all neutral, so adding
        // them here only makes a user supplied value reach the Graphics that gets rendered
        S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.None, S.Automatic,
        S.Automatic, S.Automatic, F.CEmptyList, S.True};
  }
}
