package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class LogLogPlot extends Plot {

  public LogLogPlot() {}

  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options, final EvalEngine engine) {
    GraphicsOptions graphicsOptions =
        setGraphicsOptions(options, GraphicsOptions.listPlotDefaultOptionKeys(), engine);
    graphicsOptions.setXScale("Log");
    return graphicsOptions;
  }

  @Override
  protected IExpr createGraphicsFunction(IAST graphicsPrimitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    // Use Natural Log "Log"
    graphicsOptions.addOption(F.Rule(S.$Scaling, F.List(F.stringx("Log"), F.stringx("Log"))));
    return super.createGraphicsFunction(graphicsPrimitives, graphicsOptions, plotAST);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
}
