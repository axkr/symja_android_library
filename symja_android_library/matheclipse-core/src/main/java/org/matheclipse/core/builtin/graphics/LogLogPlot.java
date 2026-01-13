package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class LogLogPlot extends Plot {

  public LogLogPlot() {

  }
  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options,
      final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(options, engine);
    graphicsOptions.setXFunction(x -> F.Log10(x));
    graphicsOptions.setYFunction(y -> F.Log10(y));
    graphicsOptions.setXScale("Log10");
    graphicsOptions.setYScale("Log10");
    graphicsOptions.setJoined(true);
    return graphicsOptions;
  }

  // @Override
  // protected IAST listOfOptionRules(GraphicsOptions listPlotOptions) {
  // IAST listOfOptions = F.List(//
  // F.Rule(S.$Scaling, //
  // F.List(F.stringx("Log10"), //
  // F.stringx("Log10"))), //
  // F.Rule(S.Axes, S.True), //
  // listPlotOptions.plotRange());
  // return listOfOptions;
  // }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
}
