package org.matheclipse.core.reflection.system;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.VisualImage;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ImageExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ImageFormat;
import org.matheclipse.core.tensor.itp.BSplineInterpolation;

public class ListDensityPlot extends ArrayPlot {

  public ListDensityPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dims = arg1.isMatrix();
    if ((ast.argSize() >= 1) && dims != null) {
      if (arg1.isSparseArray()) {
        arg1 = arg1.normal(false);
      }
      int degree = 1;
      // TODO add option InterpolationOrder
      // if ((ast.argSize() >= 2)) {
      // degree = ast.arg2().toIntDefault();
      // if (degree < 0) {
      // degree = 1;
      // }
      // }
      BufferedImage buffer = listDensityPlot((IAST) arg1, degree, engine);
      if (buffer != null) {
        return new ImageExpr(buffer, null);
      }
    }
    return F.NIL;
  }

  public static BufferedImage listDensityPlot(IAST matrix, int degree, EvalEngine engine) {
    BSplineInterpolation interpolation = new BSplineInterpolation(degree, matrix, true);
    IAST x = F.subdivide(5, 50);
    IAST y = x.reverse(F.NIL);
    IAST eval = F.matrix((i, j) -> interpolation.get( //
        F.List(y.get(i + 1), x.get(j + 1))), //
        x.argSize(), //
        x.argSize());
    IAST tensor =
        ((IAST) S.Rescale.of(engine, eval)).mapLeaf(S.List, ColorDataGradients.SOUTH_WEST);
    BufferedImage bufferedImage = ImageFormat.toIntARGB(tensor);
    VisualImage visualImage = new VisualImage(bufferedImage);
    JFreeChart jFreeChart = arrayPlot(visualImage);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    return jFreeChart.createBufferedImage(600, 480);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }
}
