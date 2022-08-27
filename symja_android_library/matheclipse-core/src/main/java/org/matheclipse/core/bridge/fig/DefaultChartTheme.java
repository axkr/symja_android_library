// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.awt.Color;
import java.awt.Font;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.api.RectangleInsets;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.pie.PieLabelLinkStyle;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;

/* package */ class DefaultChartTheme {

  private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

  private static StandardChartTheme getChartTheme(StandardChartTheme standardChartTheme) {
    /* plot label */
    standardChartTheme.setExtraLargeFont(new Font(Font.DIALOG, Font.BOLD, 14));
    /* for axes label */
    standardChartTheme.setLargeFont(new Font(Font.DIALOG, Font.PLAIN, 12));
    /* for ticks label, and legend */
    standardChartTheme.setRegularFont(new Font(Font.DIALOG, Font.PLAIN, 12));
    standardChartTheme.setSmallFont(new Font(Font.DIALOG, Font.PLAIN, 10));
    // ---
    standardChartTheme.setChartBackgroundPaint(TRANSPARENT);
    standardChartTheme.setPlotBackgroundPaint(TRANSPARENT);
    standardChartTheme.setLegendBackgroundPaint(TRANSPARENT);
    // ---
    standardChartTheme.setTitlePaint(Color.BLACK);
    standardChartTheme.setSubtitlePaint(Color.BLACK);
    standardChartTheme.setAxisLabelPaint(Color.BLACK);
    standardChartTheme.setLegendItemPaint(Color.BLACK);
    standardChartTheme.setTickLabelPaint(Color.BLACK);
    /* space between frame and axis ticks */
    standardChartTheme.setAxisOffset(new RectangleInsets(4, 4, 4, 4));
    // ---
    standardChartTheme.setDrawingSupplier(new DefaultDrawingSupplier());
    /* box around plot */
    standardChartTheme.setPlotOutlinePaint(Color.DARK_GRAY); // <- used to be black
    standardChartTheme.setLabelLinkStyle(PieLabelLinkStyle.STANDARD);
    /* vertical dashed lines */
    standardChartTheme.setDomainGridlinePaint(Color.LIGHT_GRAY);
    /* horizontal dashed lines */
    standardChartTheme.setRangeGridlinePaint(Color.LIGHT_GRAY);
    // ---
    standardChartTheme.setBaselinePaint(Color.BLACK);
    standardChartTheme.setCrosshairPaint(Color.BLACK);
    // ---
    standardChartTheme.setBarPainter(new StandardBarPainter());
    standardChartTheme.setXYBarPainter(new StandardXYBarPainter());
    // ---
    standardChartTheme.setItemLabelPaint(Color.BLACK);
    standardChartTheme.setThermometerPaint(Color.WHITE);
    standardChartTheme.setErrorIndicatorPaint(Color.RED);
    return standardChartTheme;
  }

  public static final StandardChartTheme STANDARD =
      getChartTheme(new StandardChartTheme("ch_alpine"));
}
