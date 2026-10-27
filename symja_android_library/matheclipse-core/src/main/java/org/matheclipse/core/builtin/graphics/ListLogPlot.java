package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class ListLogPlot extends ListPlot {

  public ListLogPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // the natural logarithm "Log", not "Log10"
    return logScaledListPlot(ast, argSize, options, engine, originalAST,
        F.List(S.None, F.stringx("Log")));
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
