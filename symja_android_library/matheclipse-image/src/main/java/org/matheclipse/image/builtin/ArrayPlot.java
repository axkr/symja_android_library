package org.matheclipse.image.builtin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ImageFormat;
import org.matheclipse.image.bridge.fig.BufferedImagePlot;
import org.matheclipse.image.bridge.fig.VisualImage;
import org.matheclipse.image.expression.data.ImageExpr;

public class ArrayPlot extends AbstractEvaluator {

  public ArrayPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dims = arg1.isMatrix();
    if ((ast.size() == 2) && dims != null) {
      try {
        arg1 = arg1.normal(false); // convert to normal form especially for ASTRealMatrix,
        // ASTRealVector, SparseArray etc.
        BufferedImage buffer = arrayPlot((IAST) arg1);
        if (buffer != null) {
          return new ImageExpr(buffer, null);
        }

      } catch (RuntimeException rex) {
        return Errors.printMessage(S.ArrayPlot, rex);
      }
    }
    return F.NIL;
  }

  public static BufferedImage arrayPlot(IAST matrix) throws IllegalArgumentException {
    BufferedImage bufferedImage =
        ImageFormat.toIntARGB(matrix.mapLeaf(S.List, ColorDataGradients.GRAYSCALE));
    VisualImage visualImage = new VisualImage(bufferedImage);
    JFreeChart jFreeChart = arrayPlot(visualImage);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    return jFreeChart.createBufferedImage(600, 480);
  }

  /**
   * @param visualImage
   * @return
   */
  public static JFreeChart arrayPlot(VisualImage visualImage) {
    JFreeChart jFreeChart = new JFreeChart( //
        visualImage.getPlotLabel(), //
        JFreeChart.DEFAULT_TITLE_FONT, //
        new BufferedImagePlot(visualImage), //
        false); // no legend
    ChartFactory.getChartTheme().apply(jFreeChart);
    return jFreeChart;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }
}
