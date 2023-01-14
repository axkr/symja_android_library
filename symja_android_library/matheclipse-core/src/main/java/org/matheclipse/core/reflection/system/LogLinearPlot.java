package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;

public class LogLinearPlot extends Plot {



  @Override
  protected void setGraphicOptions(GraphicsOptions graphicsOptions) {
    graphicsOptions.setXFunction(x -> F.Log(x));
    graphicsOptions.setXScale("Log");
    graphicsOptions.setJoined(true);
  }

  @Override
  protected IAST listOfOptionRules(GraphicsOptions listPlotOptions) {
    IAST listOfOptions = F.List(//
        F.Rule(S.ScalingFunctions, //
            F.List(F.stringx("Log"), S.None)), //
        F.Rule(S.Axes, S.True), //
        listPlotOptions.plotRange());
    return listOfOptions;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
}
