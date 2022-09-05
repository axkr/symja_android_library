// code by jph
package org.matheclipse.core.bridge.demo;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.bridge.fig.VisualSet;
import org.matheclipse.core.bridge.usr.DemoHelper;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.reflection.system.ListLogLogPlot;
import org.matheclipse.core.tensor.ext.HomeDirectory;

public class ListLogLogPlotDemo {

  public static JFreeChart create() {
    IAST domain = (IAST) S.Range.of(EvalEngine.get(), 1, 21);
    // S.ListLogLogPlot.of(EvalEngine.get(), S.Power.of(EvalEngine.get(), domain, 3));
    // IAST list = F.List(F.ZZ(2147483647), F.ZZ(2147483647));
    // IExpr res = S.ListLogLogPlot.of(EvalEngine.get(), list);
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel(ListLogLogPlot.class.getSimpleName());
    visualSet.add(domain, domain.map(x -> x.pow(3)));
    return ListLogLogPlot.listLogLogPlot(visualSet, false);
  }

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = EvalEngine.get();
    JFreeChart jFreeChart = create();
    jFreeChart.setBackgroundPaint(Color.WHITE);
    File file = HomeDirectory.Pictures(ListLogLogPlotDemo.class.getSimpleName() + ".png");
    ChartUtils.saveChartAsPNG(file, jFreeChart, DemoHelper.DEMO_W, DemoHelper.DEMO_H);
  }
}
