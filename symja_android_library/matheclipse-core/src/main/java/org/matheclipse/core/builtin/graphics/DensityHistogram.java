package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating 2D Density Histograms.
 * <p>
 */
public class DensityHistogram extends ListPlot {

  public DensityHistogram() {}

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

    // both options are declared on the chart family now, so they are read the ordinary way
    IExpr colorFunctionOpt = GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic);
    IExpr colorFunctionScalingOpt =
        GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True);
    boolean colorFunctionScaling = !colorFunctionScalingOpt.isFalse();

    IAST dataList = (IAST) dataArg;
    int n = dataList.argSize();
    if (n == 0)
      return F.NIL;

    double[] xValues = new double[n];
    double[] yValues = new double[n];
    int count = 0;

    double minX = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;

    for (int i = 1; i <= n; i++) {
      IExpr row = dataList.get(i);
      if (row.isList() && row.size() >= 2) {
        try {
          double x = ((IAST) row).get(1).evalfNaN();
          double y = ((IAST) row).get(2).evalfNaN();
          if (Double.isFinite(x) && Double.isFinite(y)) {
            xValues[count] = x;
            yValues[count] = y;
            if (x < minX)
              minX = x;
            if (x > maxX)
              maxX = x;
            if (y < minY)
              minY = y;
            if (y > maxY)
              maxY = y;
            count++;
          }
        } catch (Exception e) {
        }
      }
    }

    if (count == 0)
      return F.NIL;

    xValues = Arrays.copyOf(xValues, count);
    yValues = Arrays.copyOf(yValues, count);

    StandardDeviation stdDev = new StandardDeviation();
    double sigmaX = stdDev.evaluate(xValues);
    double sigmaY = stdDev.evaluate(yValues);

    double hX, hY;
    if (sigmaX == 0 || Double.isNaN(sigmaX))
      hX = (maxX - minX) / 10.0;
    else
      hX = 3.5 * sigmaX / Math.pow(count, 1.0 / 3.0);

    if (sigmaY == 0 || Double.isNaN(sigmaY))
      hY = (maxY - minY) / 10.0;
    else
      hY = 3.5 * sigmaY / Math.pow(count, 1.0 / 3.0);

    if (hX == 0)
      hX = 1.0;
    if (hY == 0)
      hY = 1.0;

    int numBinsX = (int) Math.ceil((maxX - minX) / hX) + 1;
    int numBinsY = (int) Math.ceil((maxY - minY) / hY) + 1;

    if (numBinsX > 500) {
      numBinsX = 500;
      hX = (maxX - minX) / 500.0;
    }
    if (numBinsY > 500) {
      numBinsY = 500;
      hY = (maxY - minY) / 500.0;
    }

    int[][] binCounts = new int[numBinsX][numBinsY];
    int maxCount = 0;

    for (int i = 0; i < count; i++) {
      int bx = (int) ((xValues[i] - minX) / hX);
      int by = (int) ((yValues[i] - minY) / hY);

      if (bx >= numBinsX)
        bx = numBinsX - 1;
      if (by >= numBinsY)
        by = numBinsY - 1;
      if (bx < 0)
        bx = 0;
      if (by < 0)
        by = 0;

      binCounts[bx][by]++;
      if (binCounts[bx][by] > maxCount)
        maxCount = binCounts[bx][by];
    }

    // Explicitly handle ChartLegends -> Automatic
    IExpr legends = graphicsOptions.chartLegends();
    if (legends.isAutomatic()) {
      // Create BarLegend[{"Sunset", {0, maxCount}}]
      IExpr range = F.List(F.C0, F.num(maxCount));
      IExpr barLegend = F.BarLegend(F.List(F.stringx("Sunset"), range));
      graphicsOptions.setPlotLegends(barLegend);
    }

    // the bin count is what a colour function is given, over the range the bins actually reach
    PlotColorFunction colorMap = PlotColorFunction
        .of(PlotColorFunction.Family.CHART, colorFunctionOpt, colorFunctionScalingOpt,
            S.DensityHistogram, engine)
        .range(1, 0, maxCount).sink(PlotColorFunction.Sink.FLAT)
        .fallback(GraphicsOptions::getSunsetColor).build();

    IASTAppendable primitives = F.ListAlloc();
    primitives.append(F.EdgeForm(S.None));

    graphicsOptions
        .setBoundingBox(new double[] {minX, minX + numBinsX * hX, minY, minY + numBinsY * hY});

    for (int i = 0; i < numBinsX; i++) {
      for (int j = 0; j < numBinsY; j++) {
        int c = binCounts[i][j];
        if (c > 0) {
          double x0 = minX + i * hX;
          double y0 = minY + j * hY;
          double x1 = x0 + hX;
          double y1 = y0 + hY;

          IExpr color = colorMap != null ? colorMap.color(c)
              : GraphicsOptions.getSunsetColor(
                  colorFunctionScaling && maxCount > 0 ? (double) c / (double) maxCount : c);
          primitives.append(color);
          primitives
              .append(F.Rectangle(F.List(F.num(x0), F.num(y0)), F.List(F.num(x1), F.num(y1))));
        }
      }
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
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

    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
