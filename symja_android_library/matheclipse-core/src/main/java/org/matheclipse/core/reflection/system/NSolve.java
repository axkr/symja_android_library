package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions). */
public class NSolve extends AbstractFunctionEvaluator { 
  public NSolve() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return Solve.of(ast, true, engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }
}
