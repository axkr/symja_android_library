package org.matheclipse.core.reflection.system;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.hipparchus.linear.RealMatrix;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.VisualImage;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.ASTRealMatrix;
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
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dims = arg1.isMatrix();
    if ((ast.argSize() >= 1) && dims != null) {
      arg1 = arg1.normal(false);
      int degree = 1;
      final OptionArgs options = OptionArgs.createOptionArgs(ast, engine);
      if (options != null) {
        IExpr option = options.getOption(S.InterpolationOrder);
        if (option.isPresent()) {
          int intOption = option.toIntDefault();
          if (intOption >= 0 && intOption <= 6) {
            degree = intOption;
          } else {
            // Index `1` should be a machine sized integer between `2` and `3`.",
            return IOFunctions.printMessage(ast.topHead(), "invidx2",
                F.List(S.InterpolationOrder, F.C0, F.C6), engine);
          }
        }
        ast = ast.most();
        if (ast.argSize() < 1) {
          // `1` called with `2` arguments; `3` or more arguments are expected.
          return IOFunctions.printMessage(ast.topHead(), "argm", F.List(ast, F.C0, F.C1), engine);
        }
      }

      // TODO add option InterpolationOrder
      // if ((ast.argSize() >= 2)) {
      // degree = ast.arg2().toIntDefault();
      // if (degree < 0) {
      // degree = 1;
      // }
      // }
      RealMatrix realMatrix = ((IAST) arg1).toRealMatrix();
      if (realMatrix != null) {
        BufferedImage buffer =
            listDensityPlot(new ASTRealMatrix(realMatrix, false), degree, engine);
        if (buffer != null) {
          return new ImageExpr(buffer, null);
        }
      }
    }
    return F.NIL;
  }

  public static BufferedImage listDensityPlot(ASTRealMatrix matrix, int degree, EvalEngine engine) {
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
