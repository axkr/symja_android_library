package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Solve.SolveData;

/** Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions). */
public class NSolve extends AbstractFunctionOptionEvaluator {
  public NSolve() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    SolveData sd = new SolveData(options);
    return sd.of(ast, true, engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, S.GenerateConditions, S.False);
  }
}
