package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IExpr;

public class LogPlot extends Plot {

  // @Override
  // protected IAST listOfOptionRules(GraphicsOptions listPlotOptions) {
  // IAST listOfOptions = F.List(//
  // F.Rule(S.$Scaling, //
  // F.List(S.None, F.stringx("Log10"))), //
  // F.Rule(S.Axes, S.True), //
  // listPlotOptions.plotRange());
  // return listOfOptions;
  // }

  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(options, engine);
    graphicsOptions.setYFunction(y -> F.Log(y));
    graphicsOptions.setYScale("Log10");
    graphicsOptions.setJoined(true);
    return graphicsOptions;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
