package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of the Logarithm of the Barnes G-function.
 */
public class LogBarnesG extends AbstractFunctionEvaluator {


  public LogBarnesG() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg = ast.arg1();
    // LogBarnesG(1) = 0, LogBarnesG(2) = 0
    if (arg.isOne() || arg.isNumEqualInteger(F.C2)) {
      return F.C0;
    }
    if (arg.isInfinity()) {
      return F.CInfinity;
    }
    return F.NIL;
  }

  @Override
  public IExpr numericFunction(IAST ast, final EvalEngine engine) {
    if (ast.argSize() == 1) {
      try {
        IInexactNumber z = (IInexactNumber) ast.arg1();
        return z.logBarnesG();
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.LogBarnesG, rex);
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}
