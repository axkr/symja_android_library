package org.matheclipse.core.reflection;

import java.awt.image.BufferedImage;
import org.jfree.chart.JFreeChart;
import org.matheclipse.core.bridge.fig.Axis;
import org.matheclipse.core.bridge.fig.ListPlot;
import org.matheclipse.core.bridge.fig.VisualSet;
import org.matheclipse.core.bridge.fig.Axis.Type;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ImageExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ListLogLogPlot extends ListPlot {

  public ListLogLogPlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if ((ast.size() == 2) && ast.arg1().isList()) {
      VisualSet visualSet = new VisualSet();
      visualSet.getAxisX().setType(Axis.Type.LOGARITHMIC);
      visualSet.getAxisY().setType(Axis.Type.LOGARITHMIC);
      if (listPlot(ast, visualSet)) {
        BufferedImage buffer = jFreeChartImage(visualSet);
        if (buffer != null) {
          return new ImageExpr(buffer, null);
        }
      }
    }
    return F.NIL;
  }

  public static JFreeChart listLogLogPlot(VisualSet visualSet, boolean joined) {
    visualSet.getAxisX().setType(Axis.Type.LOGARITHMIC);
    visualSet.getAxisY().setType(Axis.Type.LOGARITHMIC);
    return listPlot(visualSet, joined);
  }
  // /** @param visualSet
  // * @param joined
  // * @return */
  // public static JFreeChart of(VisualSet visualSet, boolean joined) {
  // visualSet.getAxisX().setType(Axis.Type.LOGARITHMIC);
  // visualSet.getAxisY().setType(Axis.Type.LOGARITHMIC);
  // return ListPlot.listPlot(visualSet, joined);
  // }
  //
  // /** @param visualSet
  // * @return */
  // public static JFreeChart of(VisualSet visualSet) {
  // return of(visualSet, false);
  // }
}
