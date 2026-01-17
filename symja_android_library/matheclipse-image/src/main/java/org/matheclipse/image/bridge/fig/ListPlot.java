// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.image.bridge.fig;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.image.bridge.fig.Axis.Type;
import org.matheclipse.image.expression.data.ImageExpr;

public class ListPlot extends AbstractEvaluator {

  public ListPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1().normal(false);
    if (arg1.isList()) {
      VisualSet visualSet = new VisualSet();
      if (listPlot(ast, visualSet)) {
        BufferedImage buffer = jFreeChartImage(visualSet);
        if (buffer != null) {
          return new ImageExpr(buffer, null);
        }
      }
    }
    return F.NIL;
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
      // https://stackoverflow.com/a/70648615/24819
      // logAxis.setBase(10);
      // logAxis.setNumberFormatOverride(NumberFormat.getNumberInstance());
      // logAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      try {
        xyPlot.setRangeAxis(logAxis);
      } catch (IllegalArgumentException iae) {
        iae.printStackTrace();
        double maxY = Double.MIN_VALUE;
        double minY = 0.0;
        List<XYSeries> series = xySeriesCollection.getSeries();
        for (int i = 0; i < series.size(); i++) {
          XYSeries xySeries = series.get(i);
          double tempMaxY = xySeries.getMaxY();
          if (maxY <tempMaxY) {
            maxY = tempMaxY;
          }
          double tempMinY = xySeries.getMinY();
          if (minY > tempMinY) {
            minY = tempMinY;
          }
        }
        logAxis.setRange(minY, maxY);
      }
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

  public static boolean listPlot(final IAST ast, VisualSet visualSet) {
    double[] minMax =
        new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE};
    IASTAppendable[] pointSets =
        org.matheclipse.core.builtin.graphics.ListPlot.pointsOfListPlot(ast, minMax);
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

  public BufferedImage jFreeChartImage(VisualSet visualSet) {
    JFreeChart jFreeChart = listPlot(visualSet, false);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    return jFreeChart.createBufferedImage(600, 480);
  }

}
