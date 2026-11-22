package org.matheclipse.core.builtin.graphics;

import java.math.BigInteger;
import java.util.Arrays;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Functions for generating Histograms.
 * <p>
 * Example: <code>Histogram[RandomReal[{0, 10}, 100]]</code>
 * <code>Histogram[data, Automatic, "PDF"]</code> (Basic binning support)
 */
public class Histogram extends AbstractChart {

  public Histogram() {}

  @Override
  @SuppressFBWarnings(value = "FE_FLOATING_POINT_EQUALITY",
      justification = "a scale of exactly 1.0 keeps the integer counts")
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    // a Dataset plots its rows and an Association its values, labelled by its keys
    IASTAppendable keyLabels = F.ListAlloc();
    // a wrapper around the whole argument is taken off before the data is read; its label is put
    // back on each bar, since a bin is what a reader can point at
    final PlotWrapper outerWrapper = PlotWrapper.of(engine.evaluate(ast.arg1()));
    IExpr dataArg =
        // CPD-OFF each chart reads its own option set
        GraphicsOptions.chartDataMagnitudes(outerWrapper.datum, originalAST, keyLabels, engine);
    if (dataArg.isNIL()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLabels = GraphicsOptions
        .chartLabels(GraphicsOptions.optionValue(originalAST, S.ChartLabels, S.None), keyLabels);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    IExpr layout = GraphicsOptions.optionValue(originalAST, S.ChartLayout, S.Automatic);
    IExpr elementFunction =
    // CPD-ON
        GraphicsOptions.optionValue(originalAST, S.ChartElementFunction, S.Automatic);


    IAST dataList = (IAST) dataArg;
    IAST datasets = GraphicsOptions.chartDatasets(dataList);
    int datasetCount = datasets.argSize();
    // a dataset may carry its own label, which then covers every bar that dataset fills
    IExpr[] datasetTooltips = new IExpr[datasetCount + 1];
    for (int d = 0; d < datasetCount; d++) {
      datasetTooltips[d + 1] = outerWrapper.tooltipOf(PlotWrapper.of(datasets.get(d + 1)));
    }

    // the bins are shared by every dataset, both because that is what makes the counts
    // comparable and because stacking them needs a common set of bin edges
    double[][] values = new double[datasetCount][];
    int pooled = 0;
    for (int d = 0; d < datasetCount; d++) {
      values[d] = finiteValues(datasets.get(d + 1));
      pooled += values[d].length;
    }
    if (pooled == 0) {
      return F.NIL;
    }
    double[] all = new double[pooled];
    int at = 0;
    for (double[] dataset : values) {
      System.arraycopy(dataset, 0, all, at, dataset.length);
      at += dataset.length;
    }

    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;
    for (double v : all) {
      min = Math.min(min, v);
      max = Math.max(max, v);
    }
    double h = binWidth(all, min, max);
    // the bins start at a multiple of their width: standard normal data binned at 0.5 then has
    // its edges at -3, -2.5, ... rather than at the smallest datum
    double origin = automaticBinOrigin(all, min, max, h, all.length, isIntegerData(datasets));
    int numBins = (int) Math.ceil((max + h * 0.001 - origin) / h);
    numBins = Math.max(1, Math.min(numBins, 1000));

    int[][] counts = new int[datasetCount][numBins];
    for (int d = 0; d < datasetCount; d++) {
      for (double v : values[d]) {
        int bin = (int) ((v - origin) / h);
        if (bin == numBins) {
          bin = numBins - 1;
        }
        if (bin >= 0 && bin < numBins) {
          counts[d][bin]++;
        }
      }
    }

    // Histogram[data, bins, "Probability"] asks for the share of the data in each bin and "PDF"
    // for a density; "Count", the default, leaves the bars as the number of values they hold
    double scale = 1.0;
    for (int i = 2; i <= argSize; i++) {
      IExpr spec = ast.get(i);
      if (spec.isString()) {
        String name = spec.toString();
        if (name.equals("Probability") || name.equals("Relative")) {
          scale = 1.0 / pooled;
        } else if (name.equals("PDF")) {
          scale = 1.0 / (pooled * h);
        }
      }
    }

    boolean stacked = layout.isString("Stacked");
    IASTAppendable primitives = F.ListAlloc();
    double maxY = 0;
    double[] stackBase = new double[numBins];

    // A ColorFunction colours each bar by its own height, so it takes the place of ChartStyle. The
    // tallest bin sets the top of the scale.
    double tallest = 0;
    for (int d = 0; d < datasetCount; d++) {
      for (int i = 0; i < numBins; i++) {
        tallest = Math.max(tallest, counts[d][i] * scale);
      }
    }
    PlotColorFunction barColors = PlotColorFunction.of(PlotColorFunction.Family.CHART,
        GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic),
        GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True), S.Histogram,
        engine).range(1, 0, tallest).build();

    // a legend entry names one dataset, in the fill its bars were drawn with
    GraphicsOptions.LegendColors legendColors =
        new GraphicsOptions.LegendColors(getChartStyle(chartStyle, 0));
    for (int d = 0; d < datasetCount; d++) {
      IExpr color = getChartStyle(chartStyle, d);
      legendColors.record(d, color);
      IExpr style = GraphicsOptions.chartElementStyle(baseStyle, color, !chartStyle.isAutomatic());

      IASTAppendable group = F.ListAlloc(numBins + 2);
      group.append(F.EdgeForm(S.Black));
      group.append(style.isPresent() ? style : F.RGBColor(0.9, 0.6, 0.3));
      for (int i = 0; i < numBins; i++) {
        if (counts[d][i] <= 0) {
          continue;
        }
        double x0 = origin + i * h;
        double base = stacked ? stackBase[i] : 0.0;
        double height = counts[d][i] * scale;
        double top = base + height;
        if (barColors != null) {
          // one directive per bar, which stays in force until the next one
          group.append(barColors.color(height));
        }
        IExpr bar = barPrimitive(elementFunction, x0, x0 + h, base, top,
            scale == 1.0 ? F.ZZ(counts[d][i]) : F.num(height), engine);
        IExpr barLabel = datasetTooltips[d + 1];
        if (barLabel != null && barLabel.isPresent()) {
          // a self labelling wrapper names the bar by what it is worth, which is its count
          bar = F.binaryAST2(S.Tooltip, bar,
              outerWrapper.tooltipLabelsItself ? F.ZZ(counts[d][i]) : barLabel);
        }
        group.append(bar);
        maxY = Math.max(maxY, top);
        if (stacked) {
          stackBase[i] = top;
        }
      }
      primitives.append(group);
    }

    // ChartLabels name the bins, so they sit just under the axis
    if (chartLabels.isList()) {
      IAST labels = (IAST) chartLabels;
      for (int i = 0; i < numBins && i < labels.argSize(); i++) {
        double center = origin + (i + 0.5) * h;
        primitives.append(F.List(S.Black,
            F.Text(labels.get(i + 1), F.List(F.num(center), F.C0), F.List(F.C0, F.C1))));
      }
    }

    graphicsOptions.setBoundingBox(new double[] {origin, origin + numBins * h, 0, maxY});

    // Explicitly set AxesOrigin to ensure axes (if enabled) start at min-x, 0
    // This reinforces the "left of histogram" look even if user toggles Axes->True
    graphicsOptions.addOption(F.Rule(S.AxesOrigin, F.List(F.num(origin), F.C0)));

    graphicsOptions.setChartLegend(chartLegends, legendColors);
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /**
   * One bar, through {@code ChartElementFunction} when the caller supplied one.
   *
   * <p>
   * The function is handed the bar's rectangle, what the bar is worth and its position, as
   * {@code f[{{x0, x1}, {y0, y1}}, height, {}]}, which is what lets a function that draws a bar
   * also collect it, as {@code Sow} does.
   */
  private static IExpr barPrimitive(IExpr elementFunction, double x0, double x1, double y0,
      double y1, IExpr height, EvalEngine engine) {
    IAST rectangle = F.Rectangle(F.List(F.num(x0), F.num(y0)), F.List(F.num(x1), F.num(y1)));
    if (elementFunction != null && elementFunction.isPresent() && !elementFunction.isString()
        && elementFunction != S.Automatic && !elementFunction.isNone()) {
      IAST extent = F.List(F.List(F.num(x0), F.num(x1)), F.List(F.num(y0), F.num(y1)));
      IExpr drawn = engine.evaluate(F.ternaryAST3(elementFunction, extent, height, F.List()));
      if (drawn.isPresent() && !drawn.isAST(elementFunction.head())) {
        return drawn;
      }
    }
    return rectangle;
  }

  /** Whether all the elements of the datasets are exact integers. */
  private static boolean isIntegerData(IAST datasets) {
    return datasets.forAll(dataset -> dataset.isList()
        && ((IAST) dataset).forAll(element -> PlotWrapper.strip(element).isInteger()));
  }

  /** The finite numbers of a dataset, in order. */
  private static double[] finiteValues(IExpr datasetExpr) {
    if (!datasetExpr.isList()) {
      return new double[0];
    }
    IAST dataset = (IAST) datasetExpr;
    double[] values = new double[dataset.argSize()];
    int count = 0;
    for (int i = 1; i < dataset.size(); i++) {
      IExpr elementValue = PlotWrapper.strip(dataset.get(i));
      try {
        double v =
            // a wrapper says how a value is shown, not what it is; one left on used to fail here
            // and the value then quietly took no part in any bin
            elementValue instanceof INumber ? ((INumber) elementValue).reDoubleValue()
                : elementValue.evalf();
        if (Double.isFinite(v)) {
          values[count++] = v;
        }
      } catch (RuntimeException e) {
        // a non-numeric entry simply does not take part in the histogram
      }
    }
    return Arrays.copyOf(values, count);
  }

  /** Above this many values the width comes from the number of bins rather than the spread. */
  private static final int LARGE_SAMPLE = 2500;

  /** The most bins an automatic width makes. */
  private static final int MAX_BINS = 1000;

  /**
   * A standard deviation this many times the interquartile range comes from a few far outliers
   * rather than from the bulk of the data.
   */
  private static final double HEAVY_TAIL_RATIO = 1.8;

  /** The interquartile range of a normal distribution in units of its standard deviation. */
  private static final double NORMAL_IQR = 1.349;

  /** {@link #binWidth(double[], double, double, int)} with one entry per value. */
  public static double binWidth(double[] values, double min, double max) {
    return binWidth(values, min, max, values.length);
  }

  /**
   * The width of a bin, rounded to a round number, for <code>n</code> entries of which
   * <code>values</code> are the numeric ones.
   * <ul>
   * <li>Two or three entries get two or three bins over the range.</li>
   * <li>Up to {@link #LARGE_SAMPLE} entries Scott's rule <code>3.5*sigma/n^(1/3)</code> applies,
   * with the sample standard deviation (divided by <code>n-1</code>): two hundred standard normal
   * values are binned at 0.5, a thousand at 0.2. When a few far outliers inflate the standard
   * deviation, the interquartile range estimates it instead.</li>
   * <li>Larger samples get the widest round width which still gives Sturges'
   * <code>ceiling(log2(n))+1</code> bins, so that ten thousand standard normal values are binned at
   * 0.5 again rather than in some forty narrow bins.</li>
   * </ul>
   * No automatic width makes more than {@link #MAX_BINS} bins. When every value is the same, the
   * width is 1.
   */
  public static double binWidth(double[] values, double min, double max, int n) {
    double range = max - min;
    if (!(range > 0) || !Double.isFinite(range)) {
      return 1.0;
    }
    double h;
    if (n <= 3) {
      h = roundNumber(range / Math.max(1, n - 1));
    } else if (n > LARGE_SAMPLE) {
      h = sturgesWidth(values, min, max, n);
    } else {
      double sigma = new StandardDeviation(true).evaluate(values);
      if (isHeavyTailed(values, sigma)) {
        sigma = interquartileRange(values) / NORMAL_IQR;
      }
      h = 3.5 * sigma / Math.cbrt(n);
      if (!(h > 0) || !Double.isFinite(h)) {
        return roundNumber(range / 10.0);
      }
      h = commensurateRoundNumber(h, values);
    }
    if (range / h > MAX_BINS) {
      // the quotient is rounded, so that a round width of exactly a thousandth isn't passed over
      h = roundNumberAbove(range / MAX_BINS * (1.0 - 1.0e-12));
    }
    return h;
  }

  /**
   * The widest round width which gives at least Sturges' <code>ceiling(log2(n))+1</code> bins
   * between <code>min</code> and <code>max</code>, counted on edges which are multiples of the
   * width. Integer data isn't binned finer than 1.
   */
  private static double sturgesWidth(double[] values, double min, double max, int n) {
    int bins = 64 - Long.numberOfLeadingZeros(n - 1L) + 1;
    boolean integerData = allMultiples(values, 1.0);
    double width = roundNumberAbove(max - min);
    while (Math.floor(max / width) - Math.floor(min / width) + 1 < bins) {
      double next = roundNumberBelow(width);
      if (integerData && next < 1.0) {
        return 1.0;
      }
      width = next;
    }
    return width;
  }

  /**
   * Whether the standard deviation <code>sigma</code> of <code>values</code> is
   * {@link #HEAVY_TAIL_RATIO} times their interquartile range or more.
   */
  private static boolean isHeavyTailed(double[] values, double sigma) {
    if (values.length < 4) {
      return false;
    }
    double iqr = interquartileRange(values);
    return iqr > 0 && sigma > HEAVY_TAIL_RATIO * iqr;
  }

  /**
   * The interquartile range, with the quartiles interpolated at the positions
   * <code>1/2 + n*q</code> of the sorted values.
   */
  private static double interquartileRange(double[] values) {
    double[] sorted = values.clone();
    Arrays.sort(sorted);
    return quartile(sorted, 0.75) - quartile(sorted, 0.25);
  }

  private static double quartile(double[] sorted, double q) {
    int n = sorted.length;
    double position = 0.5 + n * q;
    int lower = (int) Math.floor(position);
    double fraction = position - lower;
    lower = Math.max(1, Math.min(n, lower));
    int upper = Math.min(n, lower + 1);
    return sorted[lower - 1] + fraction * (sorted[upper - 1] - sorted[lower - 1]);
  }

  /**
   * The lower edge of the first automatic bin of width <code>width</code> for <code>n</code>
   * entries. Two or three values which the width divides evenly sit in the middle of their bins:
   * <code>{0., 10.}</code> is binned from -5 to 15. Integer data in bins one wide has its edges
   * halfway between the integers, unless a few far outliers set the width.
   */
  public static double automaticBinOrigin(double[] values, double min, double max, double width,
      int n, boolean integerData) {
    double steps = (max - min) / width;
    if (n <= 3 && steps > 0 && Math.abs(steps - Math.rint(steps)) < 1.0e-9 * (1.0 + steps)) {
      return min - 0.5 * width;
    }
    boolean centerIntegers = integerData
        && !isHeavyTailed(values, new StandardDeviation(true).evaluate(values));
    return binOrigin(min, width, centerIntegers);
  }

  /**
   * The round number for a width of about <code>h</code>. Of the two round numbers around
   * <code>h</code> the one the data is made of multiples of wins, whichever is nearer: integer data
   * then gets bins one apart rather than two.
   */
  private static double commensurateRoundNumber(double h, double[] values) {
    double magnitude = Math.pow(10, Math.floor(Math.log10(h)));
    double low = magnitude;
    double high = 10.0 * magnitude;
    for (double candidate : new double[] {2.0 * magnitude, 5.0 * magnitude}) {
      if (candidate < h) {
        low = candidate;
      } else if (candidate < high) {
        high = candidate;
      }
    }
    boolean lowFits = allMultiples(values, low);
    if (lowFits != allMultiples(values, high)) {
      return lowFits ? low : high;
    }
    return roundNumber(h);
  }

  private static boolean allMultiples(double[] values, double width) {
    for (double value : values) {
      double ratio = value / width;
      if (Math.abs(ratio - Math.rint(ratio)) >= 1.0e-9 * (1.0 + Math.abs(ratio))) {
        return false;
      }
    }
    return true;
  }

  /**
   * The nearest round number - 1, 2 or 5 times a power of ten - a reader can count in. Nearest
   * on the plain scale: 0.34 is binned at 0.2 and 0.36 at 0.5.
   */
  public static double roundNumber(double value) {
    double magnitude = Math.pow(10, Math.floor(Math.log10(value)));
    double scaled = value / magnitude;
    double rounded = scaled < 1.5 ? 1.0 : scaled < 3.5 ? 2.0 : scaled < 7.5 ? 5.0 : 10.0;
    return rounded * magnitude;
  }

  /** The smallest round number - 1, 2 or 5 times a power of ten - which isn't below the value. */
  public static double roundNumberAbove(double value) {
    double magnitude = Math.pow(10, Math.floor(Math.log10(value)));
    for (double factor : new double[] {1.0, 2.0, 5.0}) {
      if (factor * magnitude >= value) {
        return factor * magnitude;
      }
    }
    return 10.0 * magnitude;
  }

  /** The next smaller round number below the round number <code>value</code>: 5 -> 2 -> 1 -> 0.5. */
  private static double roundNumberBelow(double value) {
    double magnitude = Math.pow(10, Math.floor(Math.log10(value) + 1.0e-9));
    long mantissa = Math.round(value / magnitude);
    return mantissa >= 5 ? 2.0 * magnitude : mantissa >= 2 ? magnitude : 0.5 * magnitude;
  }

  /**
   * The lower edge of the first bin of width <code>width</code> below or at <code>min</code>. The
   * edges are multiples of the width; integer data in bins one wide has them halfway between the
   * integers, so that each integer has a bin of its own.
   */
  public static double binOrigin(double min, double width, boolean integerData) {
    double offset = integerData && width == 1.0 ? 0.5 : 0.0;
    double origin = Math.floor((min - offset) / width) * width + offset;
    // the quotient is rounded, so that min may lie on the next edge
    return origin + width <= min ? origin + width : origin;
  }

  /** The exact number a width from {@link #roundNumber(double)} stands for. */
  public static IRational exactWidth(double width) {
    int exponent = (int) Math.floor(Math.log10(width));
    IInteger mantissa = F.ZZ(Math.round(width / Math.pow(10, exponent)));
    IInteger power = F.ZZ(BigInteger.TEN.pow(Math.abs(exponent)));
    return exponent >= 0 ? mantissa.multiply(power) : F.QQ(mantissa, power);
  }

  private IExpr getChartStyle(IExpr styleOption, int index) {
    return GraphicsOptions.chartStyleColor(styleOption, index);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  protected void chartDefaults(IExpr[] defaults) {
    // a frame and no axes put the scales on the outside (left/bottom)
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.Power(S.GoldenRatio, F.CN1);
  }
}
