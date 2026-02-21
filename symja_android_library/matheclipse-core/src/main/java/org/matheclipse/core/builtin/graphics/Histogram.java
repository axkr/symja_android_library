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

    IExpr chartStyle = S.Automatic;
    for (IExpr opt : options) {
      if (opt.isRuleAST() && ((IAST) opt).arg1().equals(S.ChartStyle)) {
        chartStyle = ((IAST) opt).arg2();
      }
    }

    IAST dataList = (IAST) dataArg;
    boolean isMultiDataset = false;
    if (dataList.size() > 1 && dataList.arg1().isList()) {
      isMultiDataset = true;
    }

    IASTAppendable primitives = F.ListAlloc();

    if (isMultiDataset) {
      for (int i = 1; i < dataList.size(); i++) {
        IExpr subData = dataList.get(i);
        if (subData.isList()) {
          IExpr style = getChartStyle(chartStyle, i - 1);
          createHistogramPrimitives(primitives, (IAST) subData, style, graphicsOptions);
        }
      }
    } else {
      IExpr style = getChartStyle(chartStyle, 0);
      createHistogramPrimitives(primitives, dataList, style, graphicsOptions);
    }

    // Explicitly set AxesOrigin to ensure axes (if enabled) start at min-x, 0
    // This reinforces the "left of histogram" look even if user toggles Axes->True
    double[] bbox = graphicsOptions.boundingBox();
    if (bbox[0] != Double.MAX_VALUE) {
      graphicsOptions.addOption(F.Rule(S.AxesOrigin, F.List(F.num(bbox[0]), F.C0)));
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  private IExpr getChartStyle(IExpr styleOption, int index) {
    if (styleOption.isAutomatic()) {
      if (index == 0)
        return F.RGBColor(0.98, 0.8, 0.6);
      return GraphicsOptions.plotStyleColorExpr(index, F.NIL);
    }
    if (styleOption.isList()) {
      return GraphicsOptions.getPlotStyle(styleOption, index);
    }
    return styleOption;
  }

  private void createHistogramPrimitives(IASTAppendable primitives, IAST data, IExpr style,
      GraphicsOptions opts) {
    double[] values = new double[data.size()];
    int count = 0;
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;

    for (int i = 1; i < data.size(); i++) {
      IExpr element = data.get(i);
      try {
        if (element instanceof INumber) {
          double v = ((INumber) element).reDoubleValue();
          if (Double.isFinite(v)) {
            values[count++] = v;
            if (v < min)
              min = v;
            if (v > max)
              max = v;
          }
        } else {
          double v = element.evalDouble();
          if (Double.isFinite(v)) {
            values[count++] = v;
            if (v < min)
              min = v;
            if (v > max)
              max = v;
          }
        }
      } catch (Exception e) {
      }
    }

    if (count == 0)
      return;

    double[] validValues = Arrays.copyOf(values, count);

    double sigma = new StandardDeviation().evaluate(validValues);
    double h;
    if (sigma == 0 || Double.isNaN(sigma)) {
      h = (max - min) / 10.0;
      if (h == 0)
        h = 1.0;
    } else {
      h = 3.5 * sigma / Math.pow(count, 1.0 / 3.0);
    }

    double binStart = min;
    double binEnd = max;
    binEnd += h * 0.001;

    int numBins = (int) Math.ceil((binEnd - binStart) / h);
    if (numBins < 1)
      numBins = 1;
    if (numBins > 1000)
      numBins = 1000;

    int[] binCounts = new int[numBins];

    for (double v : validValues) {
      int binIdx = (int) ((v - binStart) / h);
      if (binIdx >= 0 && binIdx < numBins) {
        binCounts[binIdx]++;
      } else if (binIdx == numBins) {
        binCounts[numBins - 1]++;
      }
    }

    double currentMaxY = 0;
    for (int c : binCounts)
      if (c > currentMaxY)
        currentMaxY = c;

    double[] bbox = opts.boundingBox();
    if (min < bbox[0])
      bbox[0] = min;
    if ((min + numBins * h) > bbox[1])
      bbox[1] = min + numBins * h;
    if (0 < bbox[2])
      bbox[2] = 0;
    if (currentMaxY > bbox[3])
      bbox[3] = currentMaxY;

    IASTAppendable group = F.ListAlloc();
    group.append(F.EdgeForm(S.Black));

    if (style.isAutomatic()) {
      group.append(F.RGBColor(0.9, 0.6, 0.3));
    } else {
      group.append(style);
    }

    for (int i = 0; i < numBins; i++) {
      if (binCounts[i] > 0) {
        double x0 = binStart + i * h;
        double x1 = x0 + h;
        double y = binCounts[i];
        group.append(F.Rectangle(F.List(F.num(x0), F.C0), F.List(F.num(x1), F.num(y))));
      }
    }

    primitives.append(group);
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

    // Updated Defaults: Frame->True, Axes->False to put scales on the outside (left/bottom)
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;

    defaults[GraphicsOptions.X_ASPECTRATIO] = F.Power(S.GoldenRatio, F.CN1);

    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
