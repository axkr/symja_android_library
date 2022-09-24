package org.matheclipse.core.expression.sympy;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class ExprTools {

  /**
   * Compute the GCD of <code>terms</code> and put them together.
   * 
   * @param terms
   * @return
   */
  public static IExpr gcdTerms(IExpr terms) {
    return S.Cancel.of(EvalEngine.get(), terms);
  }
}
