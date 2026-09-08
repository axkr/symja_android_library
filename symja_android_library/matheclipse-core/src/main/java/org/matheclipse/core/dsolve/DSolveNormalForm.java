package org.matheclipse.core.dsolve;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The normal form of a linear equation of the second order, in which it has no first derivative.
 *
 * <p>
 * Writing <code>y == w*z</code> with <code>w == Exp(-Integrate(p/2, x))</code> turns
 * <code>y'' + p*y' + q*y == 0</code> into <code>z'' == r*z</code> with
 * <code>r == p^2/4 + p'/2 - q</code>. What is left is an equation of the same order with one term
 * fewer, and the methods which read a potential off an equation with no first derivative in it can
 * then have it; the solutions of the original are the solutions of that one multiplied by
 * <code>w</code>.
 *
 * <p>
 * Only worth having when <code>Integrate(p/2, x)</code> is elementary, which is why this is a
 * factory returning <code>null</code> rather than a constructor: the antiderivative is computed
 * and then differentiated back, because an integral the engine leaves unfinished, or finishes
 * wrongly, would otherwise be carried into every solution built from it.
 */
final class DSolveNormalForm {

  /** <code>Exp(-Integrate(p/2, x))</code>, the factor which takes the reduced equation back. */
  final IExpr recovery;

  /** <code>p^2/4 + p'/2 - q</code>, the potential of <code>z'' == r*z</code>. */
  final IExpr r;

  private DSolveNormalForm(IExpr recovery, IExpr r) {
    this.recovery = recovery;
    this.r = r;
  }

  /**
   * The normal form of <code>y'' + p*y' + q*y == 0</code>, or <code>null</code> if the
   * antiderivative of <code>p/2</code> is not one this can be built from.
   *
   * @param maxLeafCount how big an integrand is still worth integrating
   */
  static DSolveNormalForm of(IExpr p, IExpr q, IExpr xVar, int maxLeafCount, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr halfP = engine.evaluate(F.Divide(p, F.C2));
    IExpr integral = ctx.integrate(halfP, xVar, maxLeafCount);
    if (integral.isNIL() || !DSolveODE
        .isVanishing(engine.evaluate(F.Subtract(F.D(integral, xVar), halfP)), engine)) {
      return null;
    }
    IExpr recovery = engine.evaluate(F.Exp(F.Negate(integral)));
    IExpr r = engine.evaluate(F.Cancel(F.Together(
        F.Subtract(F.Plus(F.Divide(F.Sqr(p), F.C4), F.Divide(F.D(p, xVar), F.C2)), q))));
    return new DSolveNormalForm(recovery, r);
  }
}
