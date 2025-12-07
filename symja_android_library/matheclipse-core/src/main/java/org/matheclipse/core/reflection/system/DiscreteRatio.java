package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
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

      // Parse argument: either "k" or "{k, order}"
      if (arg.isSymbol()) {
        variable = (ISymbol) arg;
      } else if (arg.isList()) {
        IAST list = (IAST) arg;
        if (list.isAST2() && list.arg1().isSymbol()) {
          variable = (ISymbol) list.arg1();
          order = list.arg2().toIntDefault();
        }
      }

      if (variable == null || order < 0) {
        return F.NIL;
      }
      if (order == 0) {
        continue;
      }

      // Apply ratio logic 'order' times
      for (int k = 0; k < order; k++) {
        result = computeSingleRatio(result, variable, engine);
        // If result becomes exactly 0 or 1, we can often stop or optimize,
        // but for safety we continue (e.g. 0/0 cases typically don't occur here structurally).
      }
    }

    return result;
  }

  /**
   * Calculates Simplify( f(k) / f(k-1) )
   */
  private IExpr computeSingleRatio(IExpr expr, ISymbol variable, EvalEngine engine) {
    // 1. Calculate denominator: f(k-1)
    // We use subst to substitute k -> 1+k
    IExpr numerator = F.subst(expr, variable, F.Plus(F.C1, variable));

    // 2. Construct the division: expr / denominator
    IExpr fraction = F.Divide(numerator, expr);

    // 3. CRITICAL STEP: Simplify/Cancel the fraction.
    // Standard evaluate() is not enough to cancel terms like x^n / x^(n-1).
    // F.Cancel divides out common factors from numerator and denominator.
    // F.Simplify is more powerful but slower; usually Cancel is sufficient for Ratios.

    // We try Cancel first, as it handles the polynomial/multiplicative cancellation best.
    IExpr cancelled = engine.evaluate(F.FunctionExpand(fraction));

    // Fallback: If Cancel didn't reduce it effectively (e.g. Gamma functions),
    // Simplify or FunctionExpand might be needed.
    // For now, Cancel is the standard approach for rational simplification.
    return cancelled;
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
