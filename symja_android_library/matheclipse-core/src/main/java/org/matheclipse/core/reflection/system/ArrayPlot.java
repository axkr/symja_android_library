package org.matheclipse.core.reflection.system;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.hipparchus.linear.RealMatrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.BufferedImagePlot;
import org.matheclipse.core.bridge.fig.VisualImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ImageExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ImageFormat;

public class ArrayPlot extends AbstractEvaluator {

  public ArrayPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dims = arg1.isMatrix();
    if ((ast.size() == 2) && dims != null) {
      if (arg1.isSparseArray()) {
        arg1 = arg1.normal(false);
      }
      RealMatrix realMatrix = ((IAST) arg1).toRealMatrix();
      if (realMatrix != null) {
        BufferedImage buffer = arrayPlot(new ASTRealMatrix(realMatrix, false));
        if (buffer != null) {
          return new ImageExpr(buffer, null);
        }
      }
    }
    return F.NIL;
  }

  public static BufferedImage arrayPlot(final IAST matrix) {
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
