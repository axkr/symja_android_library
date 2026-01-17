package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class LogPlot extends Plot {

  @Override
  protected IExpr createGraphicsFunction(IAST graphicsPrimitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    // Force scaling option into the output Graphics object using Natural Log "Log"
    graphicsOptions.addOption(F.Rule(S.$Scaling, F.List(S.None, F.stringx("Log"))));
    return super.createGraphicsFunction(graphicsPrimitives, graphicsOptions, plotAST);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
