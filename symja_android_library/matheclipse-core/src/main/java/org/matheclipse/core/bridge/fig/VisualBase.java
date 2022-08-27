// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import org.jfree.chart.ChartFactory;

public class VisualBase {
  static {
    // design is bad but instigated by the jfree library
    ChartFactory.setChartTheme(DefaultChartTheme.STANDARD);
    // BarRenderer.setDefaultBarPainter(new StandardBarPainter());
    // BarRenderer.setDefaultShadowsVisible(false);
  }
  // ---
  private final Axis axisX = new Axis();
  private final Axis axisY = new Axis();
  private String plotLabel = "";

  /** @param string to appear above plot */
  public final void setPlotLabel(String string) {
    plotLabel = string;
  }

  /** @return */
  public final String getPlotLabel() {
    return plotLabel;
  }

  public final Axis getAxisX() {
    return axisX;
  }

  public final Axis getAxisY() {
    return axisY;
  }
}
