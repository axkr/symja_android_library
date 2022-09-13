package org.matheclipse.core.bridge.demo;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.bridge.usr.DemoHelper;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.image.bridge.fig.VisualSet;
import org.matheclipse.image.builtin.ListLogPlot;

public class ListLogPlotDemo {

  public static JFreeChart create() {
    IAST domain = (IAST) S.Range.of(EvalEngine.get(), 1, 21);
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel(ListLogPlot.class.getSimpleName());
    visualSet.add(domain, domain.map(x -> S.Factorial.of(x)));
    return ListLogPlot.listLogPlot(visualSet, true);
  }

  public static JFreeChart create2() {
    IAST domain = (IAST) S.Range.of(EvalEngine.get(), 1, 21);
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel(ListLogPlot.class.getSimpleName());
    visualSet.add(domain, domain.map(x -> S.Factorial.of(x)));
    return ListLogPlot.listLogPlot(visualSet, false);
  }

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = EvalEngine.get();
    JFreeChart jFreeChart = create();
    jFreeChart.setBackgroundPaint(Color.WHITE);
    File file = HomeDirectory.Pictures(ListLogPlotDemo.class.getSimpleName() + ".png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, DemoHelper.DEMO_W, DemoHelper.DEMO_H);
  }
}
