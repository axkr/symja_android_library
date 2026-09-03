package org.matheclipse.chem.builtin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.data.ElementData;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>PeriodicTablePlot</code> - the periodic table as a <code>Graphics</code> object.
 *
 * <p>
 * Everything it draws comes from <code>ElementData</code>: the names and symbols in the cells, the
 * series each element belongs to and so the colour of its cell, and the values of whatever property
 * is being mapped. Nothing about the elements is tabulated here a second time - only the shape of
 * the table, which is a layout rather than a fact about chemistry.
 */
public class PeriodicTableFunctions {

  private static class Initializer {

    private static void init() {
      S.PeriodicTablePlot.setEvaluator(new PeriodicTablePlot());
    }
  }

  private static final int[][] COORDS = new int[119][2];
  static {
    // Build coordinate grid. Y coordinates are inverted for standard Graphics cartesian mapping
    COORDS[1] = new int[] {1, 10};
    COORDS[2] = new int[] {18, 10};
    COORDS[3] = new int[] {1, 9};
    COORDS[4] = new int[] {2, 9};
    for (int i = 5; i <= 10; i++)
      COORDS[i] = new int[] {i + 8, 9};
    COORDS[11] = new int[] {1, 8};
    COORDS[12] = new int[] {2, 8};
    for (int i = 13; i <= 18; i++)
      COORDS[i] = new int[] {i, 8};
    for (int i = 19; i <= 36; i++)
      COORDS[i] = new int[] {i - 18, 7};
    for (int i = 37; i <= 54; i++)
      COORDS[i] = new int[] {i - 36, 6};
    COORDS[55] = new int[] {1, 5};
    COORDS[56] = new int[] {2, 5};
    for (int i = 72; i <= 86; i++)
      COORDS[i] = new int[] {i - 68, 5};
    COORDS[87] = new int[] {1, 4};
    COORDS[88] = new int[] {2, 4};
    for (int i = 104; i <= 118; i++)
      COORDS[i] = new int[] {i - 100, 4};
    // Placing La-Lu & Ac-Lr starting from Column 3 to 17
    for (int i = 57; i <= 71; i++)
      COORDS[i] = new int[] {i - 54, 2};
    for (int i = 89; i <= 103; i++)
      COORDS[i] = new int[] {i - 86, 1};
  }

  /**
   * The options, as pairs, in the order and with the defaults of the reference.
   *
   * <p>
   * The framework wants two parallel arrays; keeping one table and splitting it means a new option
   * cannot be added to one array and forgotten in the other.
   */
  private static final Object[][] OPTIONS = { //
      {S.AlignmentPoint, S.Center}, {S.AspectRatio, S.Automatic}, {S.Axes, S.False},
      {S.AxesLabel, S.None}, {S.AxesOrigin, S.Automatic}, {S.AxesStyle, F.List()},
      {S.Background, S.None}, {S.BaselinePosition, S.Automatic}, {S.BaseStyle, F.List()},
      {S.ColorFunction, S.Automatic}, {S.ContentSelectable, S.Automatic},
      {S.CoordinatesToolOptions, S.Automatic}, {S.Epilog, F.List()},
      {S.FormatType, S.TraditionalForm}, {S.Frame, S.False}, {S.FrameLabel, S.None},
      {S.FrameStyle, F.List()}, {S.FrameTicks, S.Automatic}, {S.FrameTicksStyle, F.List()},
      {S.GridLines, S.None}, {S.GridLinesStyle, F.List()}, {S.ImageMargins, F.num(0.0)},
      {S.ImagePadding, S.All}, {S.ImageSize, S.Automatic}, {S.LabelStyle, F.List()},
      {S.Method, S.Automatic}, {S.PlotLabel, S.None}, {S.PlotLegends, S.Automatic},
      {S.PlotRange, S.All}, {S.PlotRangeClipping, S.False}, {S.PlotRangePadding, S.Automatic},
      {S.PlotRegion, S.Automatic}, {S.PreserveImageOptions, S.Automatic}, {S.Prolog, F.List()},
      {S.RotateLabel, S.True}, {S.Ticks, S.Automatic}, {S.TicksStyle, F.List()}};

  private static final IBuiltInSymbol[] OPTION_KEYS = optionKeys();

  private static final IExpr[] OPTION_VALUES = optionValues();

  /** The two options that are this function's own, and so are consumed rather than passed on. */
  private static final int COLOR_FUNCTION = optionIndex(S.ColorFunction);

  private static final int PLOT_LEGENDS = optionIndex(S.PlotLegends);

  /**
   * <code>PlotRange</code> defaults to <code>All</code> here and to <code>Automatic</code> in
   * <code>Graphics</code>, so it is the one option that has to be said even when it is the default.
   */
  private static final int PLOT_RANGE = optionIndex(S.PlotRange);

  private static IBuiltInSymbol[] optionKeys() {
    IBuiltInSymbol[] keys = new IBuiltInSymbol[OPTIONS.length];
    for (int i = 0; i < OPTIONS.length; i++) {
      keys[i] = (IBuiltInSymbol) OPTIONS[i][0];
    }
    return keys;
  }

  private static IExpr[] optionValues() {
    IExpr[] values = new IExpr[OPTIONS.length];
    for (int i = 0; i < OPTIONS.length; i++) {
      values[i] = (IExpr) OPTIONS[i][1];
    }
    return values;
  }

  private static int optionIndex(IBuiltInSymbol symbol) {
    for (int i = 0; i < OPTION_KEYS.length; i++) {
      if (OPTION_KEYS[i] == symbol) {
        return i;
      }
    }
    throw new IllegalStateException("PeriodicTablePlot: no option " + symbol);
  }

  /** The colour of a cell whose value the table does not have. */
  private static final IExpr NO_DATA_COLOR = rgb(220 / 255.0, 220 / 255.0, 220 / 255.0);

  /** The colour of a cell, by the class of elements its element belongs to. */
  private static final Map<String, IExpr> CLASS_COLOR = classColors();

  /** The colours of the reference, so that the two tables read alike side by side. */
  private static Map<String, IExpr> classColors() {
    Map<String, IExpr> map = new LinkedHashMap<String, IExpr>();
    map.put("Nonmetal", rgb(0.493332, 0.733333, 0.866667));
    map.put("AlkaliMetal", rgb(0.636667, 0.799999, 0.473333));
    map.put("AlkalineEarthMetal", rgb(0.94835, 0.590417, 0.472786));
    map.put("TransitionMetal", rgb(0.866667, 0.6, 0.84));
    map.put("PoorMetal", rgb(1.0, 0.833333, 0.333333));
    map.put("Metalloid", rgb(0.718667, 0.673333, 0.89999));
    map.put("Chalcogen", rgb(0.848053, 0.621035, 0.401591));
    map.put("Halogen", rgb(0.6, 0.76, 1.0));
    map.put("NobleGas", rgb(0.96666, 0.7513329, 0.4283329));
    map.put("Lanthanide", rgb(0.758, 0.766667, 0.333333));
    map.put("Actinide", rgb(0.943333, 0.55499999, 0.475));
    return map;
  }

  /**
   * Names, symbols and series, read once from <code>ElementData</code> rather than tabulated again.
   *
   * <p>
   * A holder class rather than a lazily filled field: class initialization is what publishes the
   * arrays safely to every thread that reads them afterwards.
   */
  private static final class Elements {

    static final String[] NAMES = new String[119];
    static final String[] SYMBOLS = new String[119];
    static final String[] SERIES = new String[119];

    /** The group of an element, or 0 for the f-block elements that are in none. */
    static final int[] GROUPS = new int[119];

    static final int[] PERIODS = new int[119];

    static {
      EvalEngine engine = EvalEngine.get();
      for (int z = 1; z <= 118; z++) {
        NAMES[z] = text(engine, z, "Name");
        SYMBOLS[z] = text(engine, z, "AtomicSymbol");
        SERIES[z] = text(engine, z, "Series");
        GROUPS[z] = number(engine, z, "Group");
        PERIODS[z] = number(engine, z, "Period");
      }
    }

    private static String text(EvalEngine engine, int z, String property) {
      IExpr value = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx(property)));
      return value.isString() ? unquote(value) : "";
    }

    private static int number(EvalEngine engine, int z, String property) {
      return engine.evaluate(F.ElementData(F.ZZ(z), F.stringx(property))).toIntDefault(0);
    }
  }

  /** The text of a string expression, and the printed form of anything else. */
  private static String unquote(IExpr expr) {
    return expr.toString().replace("\"", "");
  }

  private static IExpr rgb(double r, double g, double b) {
    return F.RGBColor(F.num(r), F.num(g), F.num(b));
  }

  private static double getDouble(IExpr expr) {
    double d = expr.evalfNaN();
    return Double.isNaN(d) ? 0.0 : d;
  }

  /** The red, green and blue of a colour in [0, 1], or <code>null</code> when it is not one. */
  private static double[] components(IExpr color) {
    if (color.isAST(S.RGBColor) && ((IAST) color).argSize() >= 3) {
      IAST rgb = (IAST) color;
      return new double[] {getDouble(rgb.arg1()), getDouble(rgb.arg2()), getDouble(rgb.arg3())};
    }
    if (color.isAST(S.GrayLevel, 2)) {
      double level = getDouble(((IAST) color).arg1());
      return new double[] {level, level, level};
    }
    return null;
  }

  private static IExpr getLighterColor(IExpr baseColor, double fraction) {
    double[] c = components(baseColor);
    if (c == null) {
      return baseColor;
    }
    return F.RGBColor(F.num(c[0] * (1.0 - fraction) + fraction),
        F.num(c[1] * (1.0 - fraction) + fraction), F.num(c[2] * (1.0 - fraction) + fraction));
  }

  /**
   * The colour writing on a cell is legible in.
   *
   * <p>
   * A colour scale runs from light to dark, and black lettering disappears into the dark end of it,
   * so the lettering follows the cell rather than being fixed.
   */
  private static IExpr contrastingText(IExpr background) {
    double[] c = components(background);
    if (c == null) {
      return S.Black;
    }
    double luminance = 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2];
    return luminance < 0.45 ? S.White : S.Black;
  }

  private static int nameToZ(String name) {
    if (name.isEmpty()) {
      return 0;
    }
    for (int i = 1; i <= 118; i++) {
      if (Elements.NAMES[i].equalsIgnoreCase(name) || Elements.SYMBOLS[i].equalsIgnoreCase(name)) {
        return i;
      }
    }
    return 0;
  }

  /** The number behind a value, unwrapping the unit of a measurement; NaN when there is none. */
  private static double magnitude(IExpr value) {
    if (value.isAST(S.Quantity, 3)) {
      return ((IAST) value).arg1().evalfNaN();
    }
    return value.evalfNaN();
  }

  /** True when a value is something to colour, rather than the table saying it has none. */
  private static boolean hasValue(IExpr value) {
    return value != null && value.isPresent() && !value.isAST(S.Missing) && !value.isIndeterminate();
  }

  /**
   * The colours a table is painted with.
   *
   * <p>
   * A scheme is either a scale, which a measurement is drawn along, or an indexed list, which gives
   * one colour to each distinct value. Which of the two the caller named decides how a property is
   * read: asking for an indexed scheme says the values are categories even when they are numbers,
   * which is what makes <code>ColorFunction -&gt; ColorData(97)</code> colour the groups rather than
   * shade them.
   */
  private static final class ColorScheme {

    /** The colour function, or {@link F#NIL} for <code>Automatic</code>. */
    private final IExpr function;

    /** True when the scheme is a list of colours indexed from one, rather than a scale. */
    final boolean indexed;

    private ColorScheme(IExpr function, boolean indexed) {
      this.function = function;
      this.indexed = indexed;
    }

    static final ColorScheme AUTOMATIC = new ColorScheme(F.NIL, false);

    /** The scale a measurement is drawn along when nothing else was asked for. */
    private static IExpr defaultGradient() {
      return F.ColorData(F.stringx("Temperature"));
    }

    /** The colours categories are given when nothing else was asked for. */
    private static IExpr defaultIndexed() {
      return F.ColorData(F.ZZ(97));
    }

    /**
     * The scheme a <code>ColorFunction</code> setting asks for.
     *
     * @return the scheme, or <code>null</code> when the setting is not one
     */
    static ColorScheme of(IExpr spec, EvalEngine engine) {
      if (spec == null || !spec.isPresent() || spec == S.Automatic) {
        return AUTOMATIC;
      }
      IExpr function = spec;
      if (spec.isString()) {
        // a scheme named as a string, the way ColorFunction -> "BrightBands" gives it
        function = engine.evaluate(F.ColorData(spec));
        if (!function.isAST(S.ColorDataFunction)) {
          return null;
        }
      }
      boolean indexed = isIndexed(function);
      ColorScheme scheme = new ColorScheme(function, indexed);
      // a setting that does not answer with a colour is no use, and saying so beats a grey table
      return scheme.apply(function, indexed ? F.C1 : F.num(0.5), engine).isPresent() ? scheme
          : null;
    }

    private static boolean isIndexed(IExpr function) {
      return function.isAST(S.ColorDataFunction) && ((IAST) function).argSize() >= 2
          && "Indexed".equals(unquote(((IAST) function).arg2()));
    }

    /** The colour at <code>fraction</code> of the scale, for a fraction in [0, 1]. */
    IExpr scaled(double fraction, EvalEngine engine) {
      if (Double.isNaN(fraction)) {
        return NO_DATA_COLOR;
      }
      double t = Math.max(0.0, Math.min(1.0, fraction));
      IExpr color = apply(function.isPresent() ? function : defaultGradient(), F.num(t), engine);
      return color.isPresent() ? color : NO_DATA_COLOR;
    }

    /**
     * The colour of one category out of <code>count</code>, counted from one.
     *
     * @param fallback the colour to use when nothing was asked for, or {@link F#NIL} for none
     */
    IExpr category(int index, int count, IExpr fallback, EvalEngine engine) {
      if (!function.isPresent() && fallback.isPresent()) {
        return fallback;
      }
      IExpr color;
      if (!function.isPresent()) {
        color = apply(defaultIndexed(), F.ZZ(index), engine);
      } else if (indexed) {
        color = apply(function, F.ZZ(index), engine);
      } else {
        // a scale asked to name categories is sampled evenly along its length
        double t = count <= 1 ? 0.0 : (double) (index - 1) / (count - 1);
        color = apply(function, F.num(t), engine);
      }
      return color.isPresent() ? color : NO_DATA_COLOR;
    }

    /** The colour function a <code>BarLegend</code> should paint itself with. */
    IExpr legendFunction() {
      return function.isPresent() ? function : defaultGradient();
    }

    /** @return the colour, or {@link F#NIL} when the function did not answer with one */
    private IExpr apply(IExpr colorFunction, IExpr argument, EvalEngine engine) {
      IExpr color;
      try {
        color = engine.evaluate(F.unaryAST1(colorFunction, argument));
      } catch (RuntimeException rex) {
        return F.NIL;
      }
      // one shared answer to "is this a colour", so the table cannot drift from the plots
      return GraphicsOptions.isColorExpr(color) ? color : F.NIL;
    }
  }

  private static class PeriodicTablePlot extends AbstractFunctionOptionEvaluator {

    /** The plain table. */
    private static final int MODE_DEFAULT = 0;

    /** Some elements picked out, the rest dimmed. */
    private static final int MODE_HIGHLIGHT = 1;

    /** Every element coloured by the value of a property. */
    private static final int MODE_PROPERTY = 2;

    private static Set<Integer> parseElements(IExpr expr, EvalEngine engine) {
      Set<Integer> set = new LinkedHashSet<>();
      if (expr.isList()) {
        for (IExpr e : (IAST) expr) {
          set.addAll(parseElements(e, engine));
        }
      } else if (expr.isAST(S.Entity, 3)) {
        IAST entity = (IAST) expr;
        if ("Element".equals(unquote(entity.arg1()))) {
          int z = nameToZ(unquote(entity.arg2()));
          if (z > 0) {
            set.add(z);
          }
        }
      } else if (expr.isString()) {
        int z = nameToZ(unquote(expr));
        if (z > 0) {
          set.add(z);
        }
      } else if (expr.isInteger()) {
        int z = expr.toIntDefault(0);
        if (z > 0 && z <= 118) {
          set.add(z);
        }
      } else if (expr.isAST(S.EntityClass, 3)) {
        // Safely delegate EntityClass resolution back to the Engine Evaluator pipeline
        IExpr list = engine.evaluate(F.EntityList(expr));
        if (list.isList()) {
          set.addAll(parseElements(list, engine));
        }
      }
      return set;
    }

    /**
     * The property an argument names, or {@link F#NIL} when it does not name one.
     *
     * <p>
     * A bare string is a property only when it is not the name or symbol of an element, so that
     * <code>PeriodicTablePlot("Iron")</code> picks out iron and
     * <code>PeriodicTablePlot("Phase")</code> maps a property.
     */
    private static IExpr propertyName(IExpr arg) {
      if (arg.isAST(S.EntityProperty, 3)) {
        IAST entityProperty = (IAST) arg;
        if ("Element".equals(unquote(entityProperty.arg1()))
            && entityProperty.arg2().isString()) {
          return entityProperty.arg2();
        }
        return F.NIL;
      }
      if (arg.isString()) {
        String text = unquote(arg);
        if (!text.equals("Element") && nameToZ(text) == 0) {
          return arg;
        }
      }
      return F.NIL;
    }

    private static boolean isKnownProperty(String property) {
      for (String known : ElementData.PROPERTIES_DATA) {
        if (known.equals(property)) {
          return true;
        }
      }
      return false;
    }

    private static IExpr createMarkerCell(int x, int y, String marker, IExpr textColor) {
      IASTAppendable cell = F.ListAlloc();
      cell.append(S.White); // Base canvas
      cell.append(F.Rectangle(F.List(F.num(x), F.num(y)), F.List(F.num(x + 0.9), F.num(y + 0.9))));
      cell.append(F.Text(F.Style(F.stringx(marker), F.Rule(S.FontSize, F.num(12)), textColor),
          F.List(F.num(x + 0.45), F.num(y + 0.45))));
      return F.List(cell);
    }

    @Override
    public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, IAST originalAST) {
      int mode = MODE_DEFAULT;
      Set<Integer> highlightedElements = new LinkedHashSet<Integer>();
      String property = null;

      if (argSize >= 1) {
        IExpr arg1 = ast.arg1();
        IExpr propertyExpr = propertyName(arg1);
        if (propertyExpr.isPresent()) {
          property = unquote(propertyExpr);
          if (!isKnownProperty(property)) {
            return Errors.printMessage(S.PeriodicTablePlot, "elemdprop", F.list(propertyExpr),
                engine);
          }
          mode = MODE_PROPERTY;
        } else if (!(arg1.isString() && "Element".equals(unquote(arg1)))) {
          highlightedElements = parseElements(arg1, engine);
          if (highlightedElements.isEmpty()) {
            return Errors.printMessage(S.PeriodicTablePlot, "elemdent", F.list(arg1), engine);
          }
          mode = MODE_HIGHLIGHT;
        }
      }

      ColorScheme scheme = ColorScheme.of(options[COLOR_FUNCTION], engine);
      if (scheme == null) {
        Errors.printMessage(S.PeriodicTablePlot, "bspec",
            F.list(options[COLOR_FUNCTION], F.stringx("ColorFunction")), engine);
        scheme = ColorScheme.AUTOMATIC;
      }

      // The values being mapped decide how they are coloured: a property with a handful of names
      // gets one colour each, a measurement gets a gradient. Giving 118 different melting points
      // 118 different colours from a ten colour cycle says nothing.
      IExpr[] values = new IExpr[119];
      boolean continuous = false;
      double low = Double.MAX_VALUE;
      double high = -Double.MAX_VALUE;
      if (mode == MODE_PROPERTY) {
        int numeric = 0;
        for (int z = 1; z <= 118; z++) {
          values[z] = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx(property)));
          double d = magnitude(values[z]);
          if (!Double.isNaN(d)) {
            numeric++;
            low = Math.min(low, d);
            high = Math.max(high, d);
          }
        }
        continuous = numeric > 0 && low < high && !scheme.indexed;
      }

      IExpr[] cellColor = new IExpr[119];
      List<IExpr> legendLabels = new ArrayList<IExpr>();
      List<IExpr> legendColors = new ArrayList<IExpr>();

      if (continuous) {
        for (int z = 1; z <= 118; z++) {
          double d = magnitude(values[z]);
          cellColor[z] =
              Double.isNaN(d) ? NO_DATA_COLOR : scheme.scaled((d - low) / (high - low), engine);
        }
      } else {
        // Every other table is a table of categories: the values of the property in one case, and
        // the series each element belongs to in the other, which is what the plain table shows.
        IExpr[] category = new IExpr[119];
        Map<IExpr, Integer> indexOfCategory = new LinkedHashMap<IExpr, Integer>();
        for (int z = 1; z <= 118; z++) {
          // a mapped property is coloured by its own values; the plain table by the class of
          // elements each one belongs to
          category[z] = mode == MODE_PROPERTY ? values[z] : elementClass(z);
          if (hasValue(category[z]) && !indexOfCategory.containsKey(category[z])) {
            indexOfCategory.put(category[z], Integer.valueOf(indexOfCategory.size() + 1));
          }
        }
        Map<IExpr, IExpr> colorOfCategory = new LinkedHashMap<IExpr, IExpr>();
        int count = indexOfCategory.size();
        for (Map.Entry<IExpr, Integer> entry : indexOfCategory.entrySet()) {
          IExpr fallback = mode == MODE_PROPERTY //
              ? F.NIL
              : classColor(unquote(entry.getKey()));
          IExpr color = scheme.category(entry.getValue().intValue(), count, fallback, engine);
          colorOfCategory.put(entry.getKey(), color);
          legendLabels.add(entry.getKey());
          legendColors.add(color);
        }
        for (int z = 1; z <= 118; z++) {
          cellColor[z] =
              hasValue(category[z]) ? colorOfCategory.get(category[z]) : NO_DATA_COLOR;
        }
      }

      IASTAppendable primitives = F.ListAlloc();
      for (int z = 1; z <= 118; z++) {
        IExpr bgColor = cellColor[z];
        IExpr textColor = contrastingText(bgColor);
        if (mode == MODE_HIGHLIGHT && !highlightedElements.contains(Integer.valueOf(z))) {
          bgColor = getLighterColor(bgColor, 0.85);
          textColor = F.GrayLevel(F.num(0.7));
        }
        primitives.append(cell(z, bgColor, textColor, mode == MODE_PROPERTY ? values[z] : F.NIL));
      }

      IExpr markerTextColor = (mode == MODE_HIGHLIGHT) ? F.GrayLevel(F.num(0.7)) : S.Black;
      primitives.append(createMarkerCell(3, 5, "*", markerTextColor));
      primitives.append(createMarkerCell(3, 4, "**", markerTextColor));
      primitives.append(createMarkerCell(2, 2, "*", markerTextColor));
      primitives.append(createMarkerCell(2, 1, "**", markerTextColor));

      IASTAppendable result = F.Graphics(primitives);
      for (int i = 0; i < OPTION_KEYS.length; i++) {
        if (i == COLOR_FUNCTION || i == PLOT_LEGENDS) {
          // this function's own options, which it answers itself rather than passing on
          continue;
        }
        IExpr value = options[i];
        if (value == null || !value.isPresent()) {
          continue;
        }
        if (i != PLOT_RANGE && value.equals(OPTION_VALUES[i])) {
          // untouched, and saying so again would only make the result harder to read
          continue;
        }
        result.append(F.Rule(OPTION_KEYS[i], value));
      }

      IExpr legends = legends(options[PLOT_LEGENDS], mode, continuous, legendLabels, legendColors,
          scheme, low, high);
      if (legends.isPresent()) {
        result.append(F.Rule(S.PlotLegends, legends));
      }
      return result;
    }

    /** One cell of the table: its colour, its box, and the symbol and number written on it. */
    private static IExpr cell(int z, IExpr bgColor, IExpr textColor, IExpr tooltipValue) {
      int x = COORDS[z][0];
      int y = COORDS[z][1];
      IASTAppendable cell = F.ListAlloc();
      cell.append(bgColor);
      cell.append(F.Rectangle(F.List(F.num(x), F.num(y)), F.List(F.num(x + 0.9), F.num(y + 0.9))));
      cell.append(
          F.Text(F.Style(F.stringx(Elements.SYMBOLS[z]), F.Rule(S.FontSize, F.num(12)), textColor),
              F.List(F.num(x + 0.45), F.num(y + 0.55))));
      cell.append(F.Text(F.Style(F.ZZ(z), F.Rule(S.FontSize, F.num(8)), textColor),
          F.List(F.num(x + 0.45), F.num(y + 0.2))));
      IExpr cellExpr = F.List(cell);
      return tooltipValue.isPresent() ? F.Tooltip(cellExpr, tooltipValue) : cellExpr;
    }

    /**
     * The class of elements a cell of the plain table is coloured by.
     *
     * <p>
     * The reference colours the table by a finer classification than the <code>Series</code> of
     * <code>ElementData</code>, and both refinements are read off the table rather than tabulated
     * here a second time.
     *
     * <p>
     * The chalcogens are a class of their own rather than being spread over the nonmetals, the
     * metalloids and the poor metals as their series has them - and they are exactly group 16 up to
     * period 6, livermorium below that being counted with the poor metals like the rest of its
     * period. Lutetium and lawrencium are counted with the transition metals rather than with the
     * lanthanides and actinides, and they are exactly the two f-block elements the table gives a
     * group at all. An element whose series is not known at all - the nine heaviest - takes the
     * class of its group, which is what its position in the table already says about it.
     *
     * @return the class, or {@link F#NIL} for an element that belongs to none
     */
    private static IExpr elementClass(int z) {
      int group = Elements.GROUPS[z];
      String series = Elements.SERIES[z];
      if (group == 16 && Elements.PERIODS[z] <= 6) {
        return F.stringx("Chalcogen");
      }
      if (group > 0 && ("Lanthanide".equals(series) || "Actinide".equals(series))) {
        return F.stringx("TransitionMetal");
      }
      if (!series.isEmpty()) {
        return F.stringx(series);
      }
      return classOfGroup(group);
    }

    /** What the position of an element says about it when its series is not known. */
    private static IExpr classOfGroup(int group) {
      if (group >= 3 && group <= 12) {
        return F.stringx("TransitionMetal");
      }
      if (group >= 13 && group <= 16) {
        return F.stringx("PoorMetal");
      }
      if (group == 17) {
        return F.stringx("Halogen");
      }
      if (group == 18) {
        return F.stringx("NobleGas");
      }
      return F.NIL;
    }

    /** The colour the plain table gives a class, or {@link F#NIL} for one it does not know. */
    private static IExpr classColor(String elementClass) {
      IExpr color = CLASS_COLOR.get(elementClass);
      return color == null ? F.NIL : color;
    }

    /**
     * The legend the table carries.
     *
     * <p>
     * <code>Automatic</code> names the categories of a property and puts a scale beside a
     * measurement, and leaves the plain table alone - a reader who wanted the series named can say
     * <code>PlotLegends -&gt; True</code> and have them. Anything that is already a legend is
     * passed on as it stands.
     *
     * @return the setting for <code>PlotLegends</code>, or {@link F#NIL} for no legend at all
     */
    private static IExpr legends(IExpr setting, int mode, boolean continuous,
        List<IExpr> labels, List<IExpr> colors, ColorScheme scheme, double low, double high) {
      if (setting == null || !setting.isPresent() || setting.isFalse() || setting.isNone()) {
        return F.NIL;
      }
      boolean automatic = setting == S.Automatic;
      if (!automatic && !setting.isTrue()) {
        // an explicit SwatchLegend, BarLegend or list of labels is the caller's business
        return setting;
      }
      if (automatic && mode != MODE_PROPERTY) {
        return F.NIL;
      }
      if (continuous) {
        return F.BarLegend(scheme.legendFunction(), F.List(F.num(low), F.num(high)));
      }
      if (labels.isEmpty()) {
        return F.NIL;
      }
      return F.binaryAST2(S.SwatchLegend, F.ListAlloc(colors), F.ListAlloc(labels));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IFunctionEvaluator.ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, OPTION_KEYS, OPTION_VALUES);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private PeriodicTableFunctions() {}
}
