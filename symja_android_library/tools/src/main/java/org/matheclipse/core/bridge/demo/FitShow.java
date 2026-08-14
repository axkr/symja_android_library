package org.matheclipse.core.bridge.demo;

import java.awt.Color;
import java.io.IOException;
import java.util.function.UnaryOperator;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.image.bridge.fig.ListPlot;
import org.matheclipse.image.bridge.fig.VisualSet;



/* package */ class FitShow {

  public static void main(String[] args) throws IOException {
    EvalEngine engine = EvalEngine.get();
    for (int degree = 1; degree <= 4; ++degree) {
      IAST x = F.List(F.Quantity(F.num(100), F.stringx("Kelvins")), F.Quantity(F.num(110.0), F.stringx("Kelvins")), F.Quantity(F.num(120), F.stringx("Kelvins")),
          F.Quantity(F.num(133), F.stringx("Kelvins")), F.Quantity(F.num(140), F.stringx("Kelvins")), F.Quantity(F.num(15), F.stringx("Kelvins")));
      IAST y = F.List(F.Quantity(F.num(10), F.stringx("Bars")), F.Quantity(F.num(20), F.stringx("Bars")), F.Quantity(F.num(22), F.stringx("Bars")),
          F.Quantity(F.num(23), F.stringx("Bars")), F.Quantity(F.num(25), F.stringx("Bars")), F.Quantity(F.num(26.0), F.stringx("Bars")));
      IExpr fit1 = F.Fit.of(engine, F.Transpose(F.list(x, y)), degree, F.Slot1);
      UnaryOperator<IExpr> x_to_y =
          arg -> engine.evalN(F.unaryAST1(F.Function(fit1), F.QuantityMagnitude(arg)));
      IExpr fit2 = F.Fit.of(engine, F.Transpose(F.list(y, x)), degree, F.Slot1);
      UnaryOperator<IExpr> y_to_x =
          arg -> engine.evalN(F.unaryAST1(F.Function(fit2), F.QuantityMagnitude(arg)));
      IAST samples_x =
          (IAST) S.Subdivide.of(engine, F.Quantity(F.num(100), F.stringx("Kelvins")), F.Quantity(F.num(150), F.stringx("Kelvins")), 300);
      IAST samples_y =
          (IAST) S.Subdivide.of(engine, F.Quantity(F.num(10), F.stringx("Bars")), F.Quantity(F.num(26), F.stringx("Bars")), 300);
      // samples_x.map(x_to_y);
      // samples_y.map(y_to_x);
      VisualSet visualSet = new VisualSet();
      visualSet.add(samples_x, samples_x.map(x_to_y));
      visualSet.add(samples_y.map(y_to_x), samples_y);
      JFreeChart jFreeChart = ListPlot.listPlot(visualSet, true);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("here" + degree + ".png"), jFreeChart, 400,
          300);
    }
  }
}
