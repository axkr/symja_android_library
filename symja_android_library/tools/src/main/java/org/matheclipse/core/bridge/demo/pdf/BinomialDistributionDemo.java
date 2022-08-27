package org.matheclipse.core.bridge.demo.pdf;

import java.awt.Color;
import java.io.IOException;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.ListPlot;
import org.matheclipse.core.bridge.fig.VisualSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.J;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.HomeDirectory;

public class BinomialDistributionDemo {

  public static void main(String[] args) throws IOException {
    int n = 50;
    EvalEngine engine = EvalEngine.get();
    IExpr binomialDistribution = F.eval(J.binomialDistribution(n, F.C1D2));
    IExpr pdf = F.eval(J.pdf(binomialDistribution));
    IExpr cdf = F.eval(J.cdf(binomialDistribution));
    VisualSet visualSet = new VisualSet();

    IAST domain = (IAST) F.eval(J.range(0, n + 1));
    visualSet.add(domain, domain.map(x -> engine.evalN(F.unaryAST1(pdf, x))));
    visualSet.add(domain, domain.map(x -> engine.evalN(F.unaryAST1(cdf, x))));

    JFreeChart jFreeChart = ListPlot.listPlot(visualSet, false);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("binomial_distr.png"), jFreeChart, 640, 480);
  }
}
