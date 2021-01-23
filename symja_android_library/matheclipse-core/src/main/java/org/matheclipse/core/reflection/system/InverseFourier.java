package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class InverseFourier extends AbstractFunctionEvaluator {

  public InverseFourier() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr expr = ast.arg1();
    try {
      if (expr.isVector() >= 0) {
        final int n = ((IAST) expr).argSize();
        if (n == 0 || 0 != (n & (n - 1))) {
          return IOFunctions.printMessage(S.InverseFourier, "vpow2", F.List(expr), engine);
        }
        IAST result = org.matheclipse.core.reflection.system.Fourier.fourier((IAST) expr, -1);
        if (result.isPresent()) {
          return result;
        }
      }
    } catch (RuntimeException rex) {
      //
    }
    // Argument `1` is not a non-empty list or rectangular array of numeric quantities.
    return IOFunctions.printMessage(S.InverseFourier, "fftl", F.List(expr), engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
