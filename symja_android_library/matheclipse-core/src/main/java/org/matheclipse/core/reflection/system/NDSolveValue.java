package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Solve a differential equation numerically and return the solution itself, where {@link NDSolve}
 * returns rules for the dependent functions.
 *
 * <p>
 * <code>NDSolveValue({y'(x)==-y(x),y(0)==1}, y, {x,0,10})</code> gives the interpolating function
 * directly, which saves the caller the <code>y /. Part(solution, 1)</code> that <code>NDSolve
 * </code> requires. With more than one dependent function the solutions are returned as a list, in
 * the order they were asked for.
 *
 * <p>
 * The options are the ones {@link NDSolve} takes.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Ordinary_differential_equation">Wikipedia:Ordinary
 *      differential equation</a>
 */
public class NDSolveValue extends AbstractFunctionOptionEvaluator {

  public NDSolveValue() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    return NDSolve.solve(ast, options, engine, false);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Method, S.AccuracyGoal, S.PrecisionGoal}, //
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic});
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // three arguments plus any trailing options
    return IFunctionEvaluator.ARGS_3_INFINITY;
  }
}
