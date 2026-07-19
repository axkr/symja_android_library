package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implements the DiscreteRatio function.
 * <p>
 * DiscreteRatio[expr, k] gives the ratio expr / (expr /. k -> k-1). DiscreteRatio[expr, k, m]
 * applies the ratio sequentially.
 * </p>
 */
public class DiscreteRatio extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr result = ast.arg1();

    // Iterate over variables: DiscreteRatio(expr, n, m) -> loop n, then m
    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);

      ISymbol variable = null;
      int order = 1;
      IExpr step = F.C1;

      // Parse argument: "k", "{k, order}" or "{k, order, step}"
      if (arg.isSymbol()) {
        variable = (ISymbol) arg;
      } else if (arg.isList()) {
        IAST list = (IAST) arg;
        if (list.size() >= 2 && list.arg1().isSymbol()) {
          variable = (ISymbol) list.arg1();
          if (list.size() >= 3) {
            order = list.arg2().toIntDefault();
          }
          if (list.size() >= 4) {
            step = list.arg3();
          }
        }
      }

      if (variable == null || order < 0) {
        return F.NIL;
      }
      if (order == 0) {
        continue;
      }

      // Apply the ratio operator R[g] = g(k + step) / g(k) 'order' times
      for (int k = 0; k < order; k++) {
        result = computeSingleRatio(result, variable, step, engine);
      }
    }

    // Reduce to wolframscript's canonical single-fraction ratio form. FunctionExpand collapses
    // factorial/Gamma/Pochhammer/Binomial ratios (e.g. DiscreteRatio(n!, n) -> 1 + n), and
    // Together cancels common polynomial factors (e.g. DiscreteRatio(n^2 + n, n) -> (2 + n)/n)
    // while keeping a single grouped fraction (Cancel would rewrite (1 + n)^2/n^2 as (1 + 1/n)^2).
    // Only adopt the FunctionExpand result when it fully reduces (no Gamma/Factorial residue left,
    // e.g. a scaled argument like (2 n)! that Symja cannot reduce further).
    IExpr expanded = engine.evaluate(F.Together(F.FunctionExpand(result)));
    if (expanded.isFree(S.Gamma, true) && expanded.isFree(S.Factorial, true)) {
      return expanded;
    }
    return engine.evaluate(F.Together(result));
  }

  /**
   * Compute the single-step ratio <code>f(k + step) / f(k)</code>.
   */
  private static IExpr computeSingleRatio(IExpr expr, ISymbol variable, IExpr step,
      EvalEngine engine) {
    IExpr shifted = F.subst(expr, variable, F.Plus(variable, step));
    return engine.evaluate(F.Divide(shifted, expr));
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }
}
