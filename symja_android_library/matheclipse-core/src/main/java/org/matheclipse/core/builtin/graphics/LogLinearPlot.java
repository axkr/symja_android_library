package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class LogLinearPlot extends Plot {
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
    graphicsOptions.addOption(F.Rule(S.$Scaling, F.List(F.stringx("Log"), yScaling())));
    return super.createGraphicsFunction(graphicsPrimitives, graphicsOptions, plotAST);
  }

  /**
   * The scaling of the y-axis; the x-axis is always scaled logarithmically.
   *
   * @return {@link S#None} for a linear y-axis
   */
  protected IExpr yScaling() {
    return S.None;
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
