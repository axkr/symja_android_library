package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;

public class LogPlot extends Plot {

  @Override
  protected IAST listOfOptionRules(GraphicsOptions listPlotOptions) {
    IAST listOfOptions = F.List(//
        F.Rule(S.ScalingFunctions, //
            F.List(S.None, F.stringx("Log"))), //
        F.Rule(S.Axes, S.True), //
        listPlotOptions.plotRange());
    return listOfOptions;
  }

  @Override
  protected void setGraphicOptions(GraphicsOptions graphicsOptions) {
    graphicsOptions.setYFunction(y -> F.Log(y));
    graphicsOptions.setJoined(true);
  }


}
