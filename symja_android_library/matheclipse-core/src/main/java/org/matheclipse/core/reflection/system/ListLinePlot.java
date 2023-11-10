package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** Plot a list of Points as a single line */
public class ListLinePlot extends ListPlot {

  /** Constructor for the singleton */
  // public final static ListLinePlot CONST = new ListLinePlot();

  public ListLinePlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setJoined(true);
    IAST graphicsPrimitives = listPlot(ast, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      IAST listOfOptions = F.List(F.Rule(S.Axes, S.True), //
          graphicsOptions.plotRange());
      return createGraphicsFunction(graphicsPrimitives, listOfOptions, graphicsOptions);
    }

    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
