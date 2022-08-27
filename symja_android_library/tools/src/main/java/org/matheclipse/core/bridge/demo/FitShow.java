package org.matheclipse.core.bridge.demo;

import java.awt.Color;
import java.io.IOException;
import java.util.function.UnaryOperator;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.ListPlot;
import org.matheclipse.core.bridge.fig.VisualSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.HomeDirectory;
import org.matheclipse.core.tensor.qty.IQuantity;



/* package */ class FitShow {

  public static void main(String[] args) throws IOException {
    EvalEngine engine = EvalEngine.get();
    for (int degree = 1; degree <= 4; ++degree) {
      IAST x = F.List(IQuantity.of(100, "K"), IQuantity.of(110.0, "K"), IQuantity.of(120, "K"),
          IQuantity.of(133, "K"), IQuantity.of(140, "K"), IQuantity.of(15, "K"));
      IAST y = F.List(IQuantity.of(10, "bar"), IQuantity.of(20, "bar"), IQuantity.of(22, "bar"),
          IQuantity.of(23, "bar"), IQuantity.of(25, "bar"), IQuantity.of(26.0, "bar"));
      IExpr fit1 = F.Fit.of(engine, F.Transpose(F.list(x, y)), degree, F.Slot1);
      UnaryOperator<IExpr> x_to_y =
          arg -> engine.evalN(F.unaryAST1(F.Function(fit1), F.QuantityMagnitude(arg)));
      IExpr fit2 = F.Fit.of(engine, F.Transpose(F.list(y, x)), degree, F.Slot1);
      UnaryOperator<IExpr> y_to_x =
          arg -> engine.evalN(F.unaryAST1(F.Function(fit2), F.QuantityMagnitude(arg)));
      IAST samples_x =
          (IAST) S.Subdivide.of(engine, IQuantity.of(100, "K"), IQuantity.of(150, "K"), 300);
      IAST samples_y =
          (IAST) S.Subdivide.of(engine, IQuantity.of(10, "bar"), IQuantity.of(26, "bar"), 300);
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
