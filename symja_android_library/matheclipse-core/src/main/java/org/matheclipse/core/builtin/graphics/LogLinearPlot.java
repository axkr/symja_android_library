package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class LogLinearPlot extends Plot {

  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options,
      final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(options, engine);
    graphicsOptions.setXFunction(x -> F.Log(x));
    graphicsOptions.setXScale("Log10");
    graphicsOptions.setJoined(true);
    return graphicsOptions;
  }

  @Override
  public int status() {
    return ImplementationStatus.NO_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
}
