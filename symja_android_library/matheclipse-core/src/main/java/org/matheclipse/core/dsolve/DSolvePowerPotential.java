package org.matheclipse.core.dsolve;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * The equations <code>y^(n) == A*x^m*y</code> of order <code>n &gt;= 3</code>, which generalize
 * Airy's equation <code>y'' == x*y</code>.
 *
 * <p>
 * With <code>p == m+n</code> the power series <code>Sum(c(k)*x^(j+k*p), k)</code> solves the
 * equation for <code>j == 0, 1, ..., n-1</code> when
 * <code>c(k+1)/c(k) == A/(p^n*(k+1)*Product(k+1+(j-i)/p, i != j))</code>, so the fundamental set
 * is
 *
 * <pre>
 * x^j * HypergeometricPFQ({}, {1+(j-i)/p, i != j}, A*x^p/p^n)
 * </pre>
 *
 * <p>
 * For <code>m &gt; -1</code> every lower parameter lies in <code>(0,2)</code>, so none of them is
 * a pole of the series, and the leading powers <code>x^j</code> are distinct.
 */
final class DSolvePowerPotential {

  private DSolvePowerPotential() {}

  /**
   * The general solution of the equation, or {@link F#NIL} if it isn't
   * <code>y^(n) == A*x^m*y</code>.
   */
  static IExpr solve(LinearODEForm lf, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    final int n = lf.order;
    if (n < 3 || !lf.g.isZero() || lf.a[n].isZero()) {
      return F.NIL;
    }
    for (int k = 1; k < n; k++) {
      if (!lf.a[k].isZero()) {
        return F.NIL;
      }
    }
    // y^(n) == potential*y
    IExpr potential = engine.evaluate(F.Cancel(F.Together(F.Divide(F.Negate(lf.a[0]), lf.a[n]))));
    if (potential.isZero()) {
      return F.NIL;
    }
    IExpr m = engine.evaluate(F.Cancel(F.Together(
        F.Divide(F.Times(xVar, F.D(potential, xVar)), potential))));
    if (!m.isRational() || !((IRational) m).isGT(F.CN1)
        || m.isZero()) {
      // m == 0 is a linear equation with constant coefficients
      return F.NIL;
    }
    IExpr factor = engine.evaluate(F.Cancel(F.Together(F.Divide(potential, F.Power(xVar, m)))));
    if (!factor.isFree(xVar) || factor.isZero()) {
      return F.NIL;
    }
    IExpr p = engine.evaluate(F.Plus(m, F.ZZ(n)));
    IExpr argument = engine.evaluate(
        F.Times(factor, F.Power(xVar, p), F.Power(p, F.ZZ(-n))));
    IASTAppendable solution = F.PlusAlloc(n);
    for (int j = 0; j < n; j++) {
      IASTAppendable lower = F.ListAlloc(n - 1);
      for (int i = 0; i < n; i++) {
        if (i != j) {
          lower.append(F.Plus(F.C1, F.Divide(F.ZZ(j - i), p)));
        }
      }
      IExpr basis = F.Times(F.Power(xVar, F.ZZ(j)),
          F.HypergeometricPFQ(F.CEmptyList, engine.evaluate(F.Sort(lower)), argument));
      solution.append(F.Times(j == 0 ? c_n : ctx.nextConstant(), basis));
    }
    return engine.evaluate(solution);
  }
}
