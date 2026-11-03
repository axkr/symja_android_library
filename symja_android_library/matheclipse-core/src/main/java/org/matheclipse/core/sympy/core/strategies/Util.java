package org.matheclipse.core.sympy.core.strategies;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.traversal.Traversal;
import org.matheclipse.core.sympy.core.traversal.Traversal.Fns;

/**
 * Port of <code>sympy.strategies.util</code>.
 */
public final class Util {

  /**
   * Rebuild nodes without evaluating them, the counterpart of sympy's <code>basic_fns</code>, whose
   * <code>new</code> is <code>Basic.__new__</code>.
   */
  public static final Fns BASIC_FNS = Traversal.BASIC_FNS;

  /**
   * Evaluate rebuilt nodes, the counterpart of sympy's <code>expr_fns</code>, whose <code>new</code>
   * calls the constructor of the operator.
   */
  public static final Fns EXPR_FNS = new Fns() {
    @Override
    public IExpr rebuild(IASTMutable ast) {
      return EvalEngine.get().evaluate(ast);
    }
  };

  private Util() {}
}
