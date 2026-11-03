package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * <code>MultipleHarmonicNumber(n, {s1,...,sk})</code> is the sum of
 * <code>Product(1/ij^sj, {j,k})</code> over the strictly decreasing indices
 * <code>n &gt;= i1 &gt; ... &gt; ik &gt;= 1</code>,
 * <code>MultipleHarmonicNumber(n, {s1,...,sk}, {x1,...,xk})</code> has the factors
 * <code>xj^ij/ij^sj</code>.
 */
public class MultipleHarmonicNumber extends AbstractFunctionEvaluator {

  /** The largest <code>n</code> which is summed up. */
  private static final int MAX_N = 100000;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int n = ast.arg1().toIntDefault();
    if (n < 0 || n > MAX_N) {
      return F.NIL;
    }
    IAST weights = ast.arg2().isList() ? (IAST) ast.arg2() : F.list(ast.arg2());
    int k = weights.argSize();
    if (k == 0 || (ast.isAST3() && (!ast.arg3().isList() || ast.arg3().argSize() != k))) {
      return F.NIL;
    }
    // sums[m] is the sum over the inner indices, which are all <= m
    IExpr[] sums = new IExpr[n + 1];
    java.util.Arrays.fill(sums, F.C1);
    for (int j = k; j >= 1; j--) {
      IExpr[] next = new IExpr[n + 1];
      next[0] = F.C0;
      for (int m = 1; m <= n; m++) {
        engine.checkTimeLimit();
        IInteger index = F.ZZ(m);
        IExpr factor = F.Power(index, F.Negate(weights.get(j)));
        if (ast.isAST3()) {
          factor = F.Times(F.Power(ast.arg3().getAt(j), index), factor);
        }
        next[m] = engine.evaluate(F.Plus(next[m - 1], F.Times(factor, sums[m - 1])));
      }
      sums = next;
    }
    return sums[n];
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }
}
