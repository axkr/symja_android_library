package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Histograms.
 * <p>
 * Example: <code>Histogram[RandomReal[{0, 10}, 100]]</code>
 * <code>Histogram[data, Automatic, "PDF"]</code> (Basic binning support)
 */
public class Histogram extends ListPlot {

  public Histogram() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = engine.evaluate(ast.arg1());
    if (!dataArg.isList()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLabels = GraphicsOptions.optionValue(originalAST, S.ChartLabels, S.None);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    IExpr layout = GraphicsOptions.optionValue(originalAST, S.ChartLayout, S.Automatic);

    if (!chartLegends.isNone() && !chartLegends.isAutomatic()) {
      graphicsOptions.setPlotLegends(chartLegends);
    }

    IAST dataList = (IAST) dataArg;
    IAST datasets = GraphicsOptions.chartDatasets(dataList);
    int datasetCount = datasets.argSize();

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
    int numBins = (int) Math.ceil((max + h * 0.001 - min) / h);
    numBins = Math.max(1, Math.min(numBins, 1000));

    int[][] counts = new int[datasetCount][numBins];
    for (int d = 0; d < datasetCount; d++) {
      for (double v : values[d]) {
        int bin = (int) ((v - min) / h);
        if (bin == numBins) {
          bin = numBins - 1;
        }
        if (bin >= 0 && bin < numBins) {
          counts[d][bin]++;
        }
      }
    }

    boolean stacked = layout.isString("Stacked");
    IASTAppendable primitives = F.ListAlloc();
    double maxY = 0;
    double[] stackBase = new double[numBins];

    for (int d = 0; d < datasetCount; d++) {
      IExpr color = getChartStyle(chartStyle, d);
      IExpr style = GraphicsOptions.chartElementStyle(baseStyle, color, !chartStyle.isAutomatic());

      IASTAppendable group = F.ListAlloc(numBins + 2);
      group.append(F.EdgeForm(S.Black));
      group.append(style.isPresent() ? style : F.RGBColor(0.9, 0.6, 0.3));
      for (int i = 0; i < numBins; i++) {
        if (counts[d][i] <= 0) {
          continue;
        }
        double x0 = min + i * h;
        double base = stacked ? stackBase[i] : 0.0;
        double top = base + counts[d][i];
        group
            .append(F.Rectangle(F.List(F.num(x0), F.num(base)), F.List(F.num(x0 + h), F.num(top))));
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
        double center = min + (i + 0.5) * h;
        primitives.append(F.List(S.Black,
            F.Text(labels.get(i + 1), F.List(F.num(center), F.C0), F.List(F.C0, F.C1))));
      }
    }

    graphicsOptions.setBoundingBox(new double[] {min, min + numBins * h, 0, maxY});

    // Explicitly set AxesOrigin to ensure axes (if enabled) start at min-x, 0
    // This reinforces the "left of histogram" look even if user toggles Axes->True
    graphicsOptions.addOption(F.Rule(S.AxesOrigin, F.List(F.num(min), F.C0)));

    return createGraphicsFunction(primitives, graphicsOptions, ast);
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
      IExpr element = dataset.get(i);
      try {
        double v =
            element instanceof INumber ? ((INumber) element).reDoubleValue() : element.evalDouble();
        if (Double.isFinite(v)) {
          values[count++] = v;
        }
      } catch (RuntimeException e) {
        // a non-numeric entry simply does not take part in the histogram
      }
    }
    return Arrays.copyOf(values, count);
  }

  /** Bin width by the usual rule of thumb, falling back to a tenth of the range. */
  private static double binWidth(double[] values, double min, double max) {
    double sigma = new StandardDeviation().evaluate(values);
    if (sigma == 0 || Double.isNaN(sigma)) {
      double h = (max - min) / 10.0;
      return h == 0 ? 1.0 : h;
    }
    return 3.5 * sigma / Math.pow(values.length, 1.0 / 3.0);
  }

  private IExpr getChartStyle(IExpr styleOption, int index) {
    if (styleOption.isAutomatic()) {
      if (index == 0)
        return GraphicsOptions.chartStyleColorExpr(0);
      return GraphicsOptions.chartStyleColorExpr(index);
    }
    if (styleOption.isList()) {
      return GraphicsOptions.getPlotStyle(styleOption, index);
    }
    return styleOption;
  }

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions.getListOfRules());
    return result;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // charts and rasters draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;

    // Updated Defaults: Frame->True, Axes->False to put scales on the outside (left/bottom)
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;

    defaults[GraphicsOptions.X_ASPECTRATIO] = F.Power(S.GoldenRatio, F.CN1);

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
