package org.matheclipse.core.bridge.usr;

import java.awt.Color;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.bridge.fig.Histogram;
import org.matheclipse.core.bridge.fig.VisualSet;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.J;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;

public class HistogramDemo {

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = EvalEngine.get();
    VisualSet visualSet = new VisualSet();
    IAST l1 = (IAST) engine.evaluate(F.List(//
        F.List(J.quantity(2, "m"), J.quantity(3, "s")), //
        F.List(J.quantity(3, "m"), J.quantity(0, "s")), //
        F.List(J.quantity(4, "m"), J.quantity(3, "s")), //
        F.List(J.quantity(5, "m"), J.quantity(1, "s")) //
    ));
    visualSet.add(l1).setLabel("first");
    IAST l2 = (IAST) engine.evaluate(F.List(//
        F.List(J.quantity(3, "m"), J.quantity(2, "s")), //
        F.List(J.quantity(4, "m"), J.quantity(2.5, "s")), //
        F.List(J.quantity(5, "m"), J.quantity(2, "s")) //
    ));
    visualSet.add(l2).setLabel("second");
    // visualSet.add(Tensors.fromString("{{2[m],3[s]}, {3[m],0[s]}, {4[m],3[s]},
    // {5[m],1[s]}}")).setLabel("first");
    // visualSet.add(Tensors.fromString("{{3[m],2[s]}, {4[m],2.5[s]},
    // {5[m],2[s]}}")).setLabel("second");
    visualSet.setPlotLabel(Histogram.class.getSimpleName());
    JFreeChart jFreeChart = Histogram.histogram(visualSet);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG( //
        HomeDirectory.Pictures(Histogram.class.getSimpleName() + ".png"), //
        jFreeChart, //
        DemoHelper.DEMO_W, //
        DemoHelper.DEMO_H);
  }
}
