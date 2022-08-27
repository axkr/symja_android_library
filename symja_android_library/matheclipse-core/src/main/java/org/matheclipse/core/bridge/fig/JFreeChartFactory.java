// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.util.function.Function;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.data.xy.TableXYDataset;
import org.matheclipse.core.interfaces.IExpr;

/** functionality to create an instance of a JFreeChart from a {@link VisualSet} */
/* package */ class JFreeChartFactory {

  public static JFreeChart barChart(VisualSet visualSet, boolean stacked,
      Function<IExpr, String> naming) {
    JFreeChart jFreeChart = ChartFactory.createBarChart( //
        visualSet.getPlotLabel(), //
        visualSet.getAxisX().getLabel(), // unit is handled by function naming
        visualSet.getAxisY().getAxisLabel(), //
        DatasetFactory.defaultCategoryDataset(visualSet, naming), //
        PlotOrientation.VERTICAL, visualSet.hasLegend(), true, false);
    BarRenderer barRenderer = stacked //
        ? new StackedBarRenderer()
        : new BarRenderer();
    barRenderer.setDrawBarOutline(true);
    formatLines(visualSet, barRenderer);
    CategoryPlot categoryPlot = (CategoryPlot) jFreeChart.getPlot();
    categoryPlot.setRenderer(barRenderer);
    return jFreeChart;
  }

  public static JFreeChart fromXYTable(VisualSet visualSet, boolean stacked,
      TableXYDataset tableXYDataset) {
    JFreeChart jFreeChart = stacked //
        ? ChartFactory.createStackedXYAreaChart( //
            visualSet.getPlotLabel(), //
            visualSet.getAxisX().getAxisLabel(), //
            visualSet.getAxisY().getAxisLabel(), //
            tableXYDataset, //
            PlotOrientation.VERTICAL, visualSet.hasLegend(), true, false)
        : ChartFactory.createXYLineChart( //
            visualSet.getPlotLabel(), //
            visualSet.getAxisX().getAxisLabel(), //
            visualSet.getAxisY().getAxisLabel(), //
            tableXYDataset, //
            PlotOrientation.VERTICAL, visualSet.hasLegend(), true, false);
    XYPlot xyPlot = (XYPlot) jFreeChart.getPlot();
    formatLines(visualSet, (AbstractXYItemRenderer) xyPlot.getRenderer());
    return jFreeChart;
  }

  // helper function
  private static void formatLines(VisualSet visualSet, AbstractRenderer abstractRenderer) {
    for (int index = 0; index < visualSet.visualRows().size(); ++index) {
      VisualRow visualRow = visualSet.getVisualRow(index);
      abstractRenderer.setSeriesPaint(index, visualRow.getColor());
      abstractRenderer.setSeriesStroke(index, visualRow.getStroke());
      abstractRenderer.setSeriesOutlinePaint(index, visualRow.getColor().darker());
    }
  }
}
