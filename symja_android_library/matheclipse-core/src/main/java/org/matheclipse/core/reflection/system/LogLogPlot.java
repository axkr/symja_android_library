package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;

public class LogLogPlot extends Plot {

  public LogLogPlot() {

  }

  @Override
  protected void setGraphicOptions(GraphicsOptions graphicsOptions) {
    graphicsOptions.setXFunction(x -> F.Log10(x));
    graphicsOptions.setYFunction(y -> F.Log10(y));
    graphicsOptions.setXScale("Log10");
    graphicsOptions.setYScale("Log10");
    graphicsOptions.setJoined(true);
  }

  @Override
  protected IAST listOfOptionRules(GraphicsOptions listPlotOptions) {
    IAST listOfOptions = F.List(//
        F.Rule(S.$Scaling, //
            F.List(F.stringx("Log10"), //
                F.stringx("Log10"))), //
        F.Rule(S.Axes, S.True), //
        listPlotOptions.plotRange());
    return listOfOptions;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
}
