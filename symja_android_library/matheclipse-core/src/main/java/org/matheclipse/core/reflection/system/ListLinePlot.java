package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
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
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {

    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    if (options[0].isTrue()) {
      IExpr temp = S.Manipulate.of(engine, ast);
      if (temp.headID() == ID.JSFormData) {
        return temp;
      }
      return F.NIL;
    }

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
