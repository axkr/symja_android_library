// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.image.bridge.fig;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import org.hipparchus.stat.descriptive.moment.Mean;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeriesCollection;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.image.bridge.fig.Axis.Type;
import org.matheclipse.image.expression.data.ImageExpr;

public class Plot extends ListPlot {

  private static final int N = 100;

  public Plot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if ((ast.size() >= 3) && ast.arg2().isList()) {
      IExpr arg1 = ast.arg1();
      try {
        final IAST rangeList = (IAST) ast.arg2();
        if (rangeList.isList3()) {
          if (!rangeList.arg1().isSymbol()) {
            // `1` is not a valid variable.
            return Errors.printMessage(S.Plot, "ivar", F.list(rangeList.arg1()), engine);
          }
          VisualSet visualSet = plot(arg1, rangeList, ast, engine);
          if (visualSet != null) {
            BufferedImage buffer = jFreeChartImage(visualSet);
            if (buffer != null) {
              return new ImageExpr(buffer, null);
            }
          }
        }
      } catch (RuntimeException rex) {
        // LOGGER.debug("Plot.evaluate() failed", rex);
      }
    }
    return F.NIL;
  }

  public static VisualSet plot(IExpr arg1, final IAST rangeList, final IAST ast,
      EvalEngine engine) {

    final ISymbol x = (ISymbol) rangeList.arg1();
    final IExpr xMin = engine.evalN(rangeList.arg2());
    final IExpr xMax = engine.evalN(rangeList.arg3());
    if ((!(xMin instanceof INum)) || (!(xMax instanceof INum))) {
      return null;
    }
    final double xMinD = ((INum) xMin).getRealPart();
    final double xMaxd = ((INum) xMax).getRealPart();
    if (xMaxd <= xMinD) {
      return null;
    }
    double yMinD = 0.0f;
    double yMaxD = 0.0f;

    if (ast.isPresent() && (ast.isAST3()) && ast.arg3().isList()) {
      final IAST lsty = (IAST) ast.arg3();
      if (lsty.isAST2()) {
        final IExpr y0 = engine.evalN(lsty.arg1());
        final IExpr y1 = engine.evalN(lsty.arg2());
        if ((y0 instanceof INum) && (y1 instanceof INum)) {
          yMinD = ((INum) y0).getRealPart();
          yMaxD = ((INum) y1).getRealPart();
        }
      }
    }

    final IAST list = arg1.makeList();
    int size = list.size();
    VisualSet visualSet = new VisualSet();
    double[] yMinMax = new double[] {Double.MAX_VALUE, Double.MIN_VALUE};
    for (int i = 1; i < size; i++) {
      IExpr function = list.get(i);
      double[][] data = plotLine(xMinD, xMaxd, yMinD, yMaxD, function, x, engine);
      if (data != null) {
        double[] dataMinMax = automaticPlotRange(data);
        IAST temp = plotMatrix(visualSet, data, dataMinMax, function.toString());
        if (temp.isPresent()) {
          if (plotPoints(temp, visualSet)) {
            // double[] dataMinMax = automaticPlotRange(data);
            if (dataMinMax[0] < yMinMax[0]) {
              yMinMax[0] = dataMinMax[0];
            }
            if (dataMinMax[1] > yMinMax[1]) {
              yMinMax[1] = dataMinMax[1];
            }
          }
        }
      }
    }

    return visualSet;
  }

  public static IASTAppendable plotMatrix(VisualSet visualSet, double[][] allPoints,
      double[] dataMinMax, String label) {
    IASTAppendable points = F.NIL;
    int colorDataIndex = visualSet.getColorDataIndex();
    boolean first = true;
    if (allPoints != null && allPoints.length > 0) {
      points = F.ListAlloc(64);
      for (int i = 0; i < allPoints.length; i++) {
        double x = allPoints[i][0];
        double y = allPoints[i][1];
        if (y >= dataMinMax[0] && y < dataMinMax[1]) {
          points.append(F.list(F.num(x), F.num(y)));
        } else {
          visualSet.add(points, colorDataIndex, first, label);
          first = false;
          points = F.ListAlloc(64);
        }
      }
      if (points.argSize() > 0) {
        visualSet.add(points, colorDataIndex, first, label);
      }
    }
    return points;
  }

  /**
   * @param xMin the minimum x-range value
   * @param xMax the maximum x-range value
   * @param yMin if <code>yMin != 0 && yMax != 0</code> filter only results which are in the y-range
   *        and set yMin or yMax as plot result-range.
   * @param yMax if <code>yMin != 0 && yMax != 0</code> filter only results which are in the y-range
   *        and set yMin or yMax as plot result-range.
   * @param function the function which should be plotted
   * @param xVar the variable symbol
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> is no conversion of the data into an {@link IAST} was possible
   */
  public static double[][] plotLine(final double xMin, final double xMax, final double yMin,
      final double yMax, final IExpr function, final ISymbol xVar, final EvalEngine engine) {

    final double step = (xMax - xMin) / N;
    double y;

    final UnaryNumerical hun = new UnaryNumerical(function, xVar, engine);
    final double data[][] = new double[N + 1][2];
    double x = xMin;

    for (int i = 0; i < N + 1; i++) {
      y = hun.value(x);
      if ((yMin != 0.0) || (yMax != 0.0)) {
        if ((y >= yMin) && (y <= yMax)) {
          data[i][0] = x;
          data[i][1] = y;
        } else {
          if (y < yMin) {
            data[i][0] = x;
            data[i][1] = yMin;
          } else {
            data[i][0] = x;
            data[i][1] = yMax;
          }
        }
      } else {
        data[i][0] = x;
        data[i][1] = y;
      }
      x += step;
    }
    return data;
  }

  /**
   * Calculates mean and standard deviation, throwing away all points which are more than 'thresh'
   * number of standard deviations away from the mean. These are then used to find good vmin and
   * vmax values. These values can then be used to find Automatic Plotrange.
   *
   * @param values of the y-axe
   * @return vmin and vmax value of the range
   */
  private static double[] automaticPlotRange(final double[][] values) {

    double[] rawYValues = new double[values.length];
    int k = 0;
    int j = 0;
    while (k < values.length) {
      double v = values[k][1];
      if (Math.abs(v) < 1E09) {
        rawYValues[j++] = v;
      }
      k++;
    }
    double[] yValues = new double[j];
    System.arraycopy(rawYValues, 0, yValues, 0, j);
    Arrays.sort(yValues);
    double valavg = new Mean().evaluate(yValues);
    double valdev = new StandardDeviation().evaluate(yValues, valavg);


    final double thresh = 2.0;
    int n1 = 0;
    int n2 = yValues.length - 1;
    if (valdev != 0) {
      for (double v : yValues) {
        if (Math.abs(v - valavg) / valdev < thresh) {
          break;
        }
        n1 += 1;
      }
      for (int i = yValues.length - 1; i >= 0; i--) {
        double v = yValues[i];
        if (Math.abs(v - valavg) / valdev < thresh) {
          break;
        }
        n2 -= 1;
      }
    }

    double vrange = yValues[n2] - yValues[n1];
    double vmin = yValues[n1] - 0.05 * vrange; // 5% extra looks nice
    double vmax = yValues[n2] + 0.05 * vrange;
    return new double[] {vmin, vmax};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  /**
   * Remark: We would like to make joined property of VisualRow, but JFreeChart does not support
   * this granularity.
   * 
   * @param visualSet
   * @param joined for lines between coordinates, otherwise scattered points
   * @return
   */
  public static JFreeChart listPlot(VisualSet visualSet, boolean joined) {
    XYSeriesCollection xySeriesCollection = DatasetFactory.xySeriesCollection(visualSet);
    JFreeChart jFreeChart = joined //
        ? ChartFactory.createXYLineChart( //
            visualSet.getPlotLabel(), //
            visualSet.getAxisX().getAxisLabel(), //
            visualSet.getAxisY().getAxisLabel(), //
            xySeriesCollection, PlotOrientation.VERTICAL, //
            visualSet.hasLegend(), // legend
            false, // tooltips
            false) // urls
        : ChartFactory.createScatterPlot( //
            visualSet.getPlotLabel(), //
            visualSet.getAxisX().getAxisLabel(), //
            visualSet.getAxisY().getAxisLabel(), //
            xySeriesCollection, PlotOrientation.VERTICAL, //
            visualSet.hasLegend(), // legend
            false, // tooltips
            false); // urls
    XYPlot xyPlot = (XYPlot) jFreeChart.getPlot();
    XYItemRenderer xyItemRenderer = xyPlot.getRenderer();
    int limit = xySeriesCollection.getSeriesCount();
    for (int index = 0; index < limit; ++index) {
      VisualRow visualRow = visualSet.getVisualRow(index);
      xyItemRenderer.setSeriesPaint(index, visualRow.getColor());
      xyItemRenderer.setSeriesStroke(index, visualRow.getStroke());
    }
    // https://github.com/jfree/jfreechart/issues/301
    if (visualSet.getAxisX().getType().equals(Type.LOGARITHMIC)) {
      LogAxis logAxis = new LogAxis(visualSet.getAxisX().getAxisLabel());
      xyPlot.setDomainAxis(logAxis);
    }
    if (visualSet.getAxisY().getType().equals(Type.LOGARITHMIC)) {
      LogAxis logAxis = new LogAxis(visualSet.getAxisY().getAxisLabel());
      xyPlot.setRangeAxis(logAxis);
    }
    StaticHelper.setRange(visualSet.getAxisX(), xyPlot.getDomainAxis());
    StaticHelper.setRange(visualSet.getAxisY(), xyPlot.getRangeAxis());
    return jFreeChart;
  }

  /**
   * Tested with up to 10 million points - a little slow but possible.
   * 
   * @param visualSet
   * @return
   */
  public static JFreeChart listPlot(VisualSet visualSet) {
    return listPlot(visualSet, false);
  }

  public static boolean plotPoints(final IAST ast, VisualSet visualSet) {
    double[] minMax =
        new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE};
    IASTAppendable[] pointSets =
        org.matheclipse.core.builtin.graphics.ListPlot.pointsOfMatrix(ast, minMax);
    if (pointSets != null) {
      for (int i = 0; i < pointSets.length; i++) {
        IASTAppendable points = pointSets[i];
        if (points.isPresent()) {
          visualSet.add(points);
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public BufferedImage jFreeChartImage(VisualSet visualSet) {
    JFreeChart jFreeChart = listPlot(visualSet, true);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    return jFreeChart.createBufferedImage(600, 480);
  }

}
