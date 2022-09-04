package org.matheclipse.core.bridge.demo;

import java.awt.Color;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.Plot;
import org.matheclipse.core.bridge.fig.VisualSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;

public class PlotDemo {

  public static void main(String[] args) throws IOException {
    EvalEngine engine = EvalEngine.get();

    IAST functions = F.List(F.Cot(F.x), F.Tan(F.x), F.Sin(F.x), F.Cos(F.x));
    IAST domain = F.List(F.x, F.CNPi, S.Pi);
    IAST plot = F.Plot(functions, domain);
    VisualSet visualSet = Plot.plot(functions, domain, plot, engine);
    JFreeChart jFreeChart = Plot.listPlot(visualSet, true);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("Plot.png"), jFreeChart, 640, 480);
  }
}
