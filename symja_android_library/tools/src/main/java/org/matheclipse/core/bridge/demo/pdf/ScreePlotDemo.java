package org.matheclipse.core.bridge.demo.pdf;

import java.awt.Color;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.image.bridge.fig.ListPlot;
import org.matheclipse.image.bridge.fig.VisualSet;


/** inspired from strang's book */
public class ScreePlotDemo {

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = EvalEngine.get();
    IAST matrix = (IAST) engine.evalN(F.HilbertMatrix(F.ZZ(7)));
    VisualSet visualSet = new VisualSet();
    IAST values = (IAST) S.SingularValueList.of(engine, matrix);
    visualSet.add((IAST) S.Range.of(engine, 0, values.argSize() - 1),
        values.map(x -> S.Log.of(engine, 10, x)));
    JFreeChart jFreeChart = ListPlot.listPlot(visualSet, true);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures(ScreePlotDemo.class.getSimpleName() + "2.png"),
        jFreeChart, 640, 480);
  }
}
