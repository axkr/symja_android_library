package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Equations which factor as a product in the function and its derivatives.
 *
 * <p>
 * When the residual splits as <code>F1*F2*... == 0</code>, anything solving one of the factors
 * solves the product, so each factor is handed back to the cascade as an equation of its own and
 * the branches are collected. This runs before the methods which recognize a normal form, so that a
 * product is split rather than matched whole.
 */
final class DSolveFactorable {

  private DSolveFactorable() {}

  /** How many branches are worth returning; a product of many factors is not what this is for. */
  private static final int MAX_BRANCHES = 12;

  /**
   * The branches of the solution, or {@link F#NIL} if the equation does not factor.
   */
  static IExpr solve(IExpr lhs, IExpr yFunction, IExpr xVar, int n, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (n < 1) {
      return F.NIL;
    }

    // The derivatives become plain symbols, because Factor both misreads and, on a coefficient
    // which is not a polynomial, does not finish on an expression built from Derivative.
    IExpr[] derivatives = new IExpr[n + 1];
    IExpr[] symbols = new IExpr[n + 1];
    IExpr substituted = lhs;
    for (int k = 0; k <= n; k++) {
      derivatives[k] =
          k == 0 ? yFunction : engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(k))));
      symbols[k] = F.Dummy("fD" + k);
    }
    // Downwards, so that the highest derivative is replaced before the ones it contains.
    for (int k = n; k >= 0; k--) {
      substituted = F.subst(substituted, derivatives[k], symbols[k]);
    }
    substituted = engine.evaluate(substituted);
    if (!substituted.isFree(yFunction.head(), true)) {
      return F.NIL;
    }

    if (!splitsInTheDerivatives(substituted, symbols, n, engine)) {
      return F.NIL;
    }

    IASTAppendable variables = F.ListAlloc(n + 1);
    for (int k = 0; k <= n; k++) {
      variables.append(symbols[k]);
    }
    if (!engine.evaluate(F.PolynomialQ(substituted, variables)).isTrue()) {
      return F.NIL;
    }

    IExpr factorList = engine.evaluate(F.FactorList(substituted));
    if (!factorList.isList()) {
      return F.NIL;
    }

    IAST factors = (IAST) factorList;
    IASTAppendable differential = F.ListAlloc(factors.argSize());
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr entry = factors.get(i);
      if (!entry.isList() || entry.size() < 2) {
        continue;
      }
      IExpr factor = entry.first();
      boolean hasDerivative = false;
      for (int k = 1; k <= n && !hasDerivative; k++) {
        hasDerivative = !factor.isFree(symbols[k], true);
      }
      if (!hasDerivative) {
        // A factor in the bare function alone is an algebraic constraint, not an equation to
        // solve, and returning its root as a branch is what breaks the methods which recurse
        // here and read the first one.
        continue;
      }
      for (int k = 0; k <= n; k++) {
        factor = F.subst(factor, symbols[k], derivatives[k]);
      }
      differential.append(engine.evaluate(factor));
    }
    if (differential.argSize() < 2) {
      return F.NIL;
    }

    // The factors are alternatives rather than parts of one solution, so each is a general
    // solution in its own right and names its constants from the same place the equation would
    // have named them.
    IASTAppendable results = F.ListAlloc(differential.argSize());
    int firstConstant = engine.getConstantCounter();
    int lastConstant = firstConstant;
    for (int i = 1; i <= differential.argSize() && results.argSize() < MAX_BRANCHES; i++) {
      if (ctx.expired()) {
        break;
      }
      engine.setConstantCounter(firstConstant);
      IAST branches =
          DSolveODE.solveSubODE(F.Equal(differential.get(i), F.C0), xVar, yFunction, c_n, ctx);
      lastConstant = Math.max(lastConstant, engine.getConstantCounter());
      for (int j = 1; j <= branches.argSize() && results.argSize() < MAX_BRANCHES; j++) {
        results.append(branches.get(j));
      }
    }
    engine.setConstantCounter(lastConstant);
    return results.argSize() == 0 ? F.NIL
        : results.argSize() == 1 ? results.arg1() : results;
  }

  /**
   * Whether the total degree in the derivatives is at least two.
   *
   * <p>
   * Each factor of a genuine split carries a derivative, so anything of lower degree — every linear
   * equation among it — cannot factor. The test is worth making before <code>FactorList</code>,
   * which on the high degree numerator of a linear equation of order two runs for a very long time.
   */
  private static boolean splitsInTheDerivatives(IExpr substituted, IExpr[] symbols, int n,
      EvalEngine engine) {
    IExpr marker = F.Dummy("fT");
    IExpr marked = substituted;
    for (int k = 1; k <= n; k++) {
      marked = F.subst(marked, symbols[k], F.Times(marker, symbols[k]));
    }
    IExpr degree = engine.evaluate(F.Exponent(engine.evaluate(marked), marker));
    return degree.isInteger() && degree.greaterEqualThan(F.C2).isTrue();
  }
}
