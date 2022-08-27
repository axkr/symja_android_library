package org.matheclipse.core.reflection.system;

import java.awt.image.BufferedImage;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.Axis;
import org.matheclipse.core.bridge.fig.ListPlot;
import org.matheclipse.core.bridge.fig.VisualSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ImageExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ListLogPlot extends ListPlot {

  public ListLogPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if ((ast.size() == 2) && ast.arg1().isList()) {
      VisualSet visualSet = new VisualSet();
      visualSet.getAxisY().setType(Axis.Type.LOGARITHMIC);
      if (listPlot(ast,visualSet)) {
        BufferedImage buffer = jFreeChartImage(visualSet);
        if (buffer != null) {
          return new ImageExpr(buffer, null);
        }
      }
    }
    return F.NIL;
  }

  public static JFreeChart listLogPlot(VisualSet visualSet, boolean joined) {
    visualSet.getAxisY().setType(Axis.Type.LOGARITHMIC);
    return listPlot(visualSet, joined);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  // /** @param visualSet
  // * @param joined
  // * @return */
  // public static JFreeChart listLogPlot(VisualSet visualSet, boolean joined) {
  // visualSet.getAxisY().setType(Axis.Type.LOGARITHMIC);
  // return ListPlot.listPlot(visualSet, joined);
  // }
  //
  // /** @param visualSet
  // * @return */
  // public static JFreeChart listLogPlot(VisualSet visualSet) {
  // return listLogPlot(visualSet, false);
  // }
}
