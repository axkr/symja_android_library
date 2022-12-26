package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Points as a single line */
public class ListLinePlot extends ListPlot {
  private static final Logger LOGGER = LogManager.getLogger();

  /** Constructor for the singleton */
  // public final static ListLinePlot CONST = new ListLinePlot();

  public ListLinePlot() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setJoined(true);
    IAST graphicsPrimitives = plot(ast, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      IAST listOfOptions = F.List(F.Rule(S.Axes, S.True), //
          graphicsOptions.plotRange());
      return createGraphicsFunction(graphicsPrimitives, listOfOptions, graphicsOptions);
    }

    return F.NIL;
  }


  @Override
  public void setUp(final ISymbol newSymbol) {}
}
