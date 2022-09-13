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
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.image.bridge.fig.ListPlot;
import org.matheclipse.image.bridge.fig.VisualSet;

public class HistogramDistributionDemo {

  public static void main(String[] args) throws IOException {
    IOFunctions.initialize();
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = EvalEngine.get();
    IExpr dist = S.NormalDistribution.of(1, 2);
    // HistogramDistribution distribution = (HistogramDistribution) //
    // S.HistogramDistribution.of(S.RandomVariate.of(engine, dist, 2000), F.num(0.25));
    // {
    // IAST domain = Subdivide.of(-5, 8, 300);
    // VisualSet visualSet = new VisualSet();
    // visualSet.add(domain, domain.map(distribution::at));
    // visualSet.add(domain, domain.map(distribution::p_lessEquals));
    // visualSet.add(domain, domain.map(dist::at));
    // JFreeChart jFreeChart = ListPlot.of(visualSet, true);
    // jFreeChart.setBackgroundPaint(Color.WHITE);
    // ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("hd.png"), jFreeChart, 640, 480);
    // }
    // {
    IAST domain = F.subdivide(0, 1, 300);
    IAST inv1 = (IAST) S.InverseCDF.of(engine, dist);
    IAST inv2 = (IAST) S.InverseCDF.of(engine, dist);
    VisualSet visualSet = new VisualSet();
    visualSet.add(domain, domain.map(x -> engine.evalN(F.Quantile(inv1, x))));
    visualSet.add(domain, domain.map(x -> engine.evalN(F.Quantile(inv2, x))));
    JFreeChart jFreeChart = ListPlot.listPlot(visualSet, true);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("hd_inv.png"), jFreeChart, 640, 480);
    // }
  }
}
