package org.matheclipse.core.bridge.demo.pdf;

import java.awt.Color;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.J;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.image.bridge.fig.ListPlot;
import org.matheclipse.image.bridge.fig.VisualSet;

public class ErfcDemo {

  public static void main(String[] args) throws IOException {
    EvalEngine engine = EvalEngine.get();
    IAST domain = (IAST) F.eval(J.subdivide(-5, 5, 300));
    VisualSet visualSet = new VisualSet();
    visualSet.add(domain, domain.map(x -> engine.evalN(F.Erfc(x))));
    JFreeChart jFreeChart = ListPlot.listPlot(visualSet, true);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("Erfc.png"), jFreeChart, 640, 480);
  }
}
