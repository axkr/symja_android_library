package org.matheclipse.core.integrate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Numeric check of <code>D(antiderivative, x) == integrand</code>.
 *
 * <p>
 * The derivative and the integrand are compared at fixed sample points of both signs. Points where
 * the integrand is not a finite real number are skipped, because outside the real domain the two
 * sides may be continued on different branches. The stages call this before the symbolic
 * <code>Together</code>/<code>Simplify</code> checks: for an antiderivative with nested radical
 * constants those can take seconds, the numeric comparison milliseconds.
 */
public final class AntiderivativeCheck {

  public enum Verdict {
    /** every real sample point agrees, and there are enough of them */
    AGREES,
    /** at least one real sample point disagrees */
    DISAGREES,
    /** too few real sample points */
    INCONCLUSIVE
  }

  private static final double[] POINTS = {0.37, 0.61, 1.37, 2.19, -0.43, 0.17, 0.93, 2.9, -1.3};

  private AntiderivativeCheck() {}

  /**
   * Compare <code>D(antiderivative, x)</code> with <code>integrand</code> at the sample points.
   *
   * @param minPoints the number of agreeing real sample points needed for {@link Verdict#AGREES}
   */
  public static Verdict differentiatesBack(IExpr antiderivative, IExpr integrand, IExpr x,
      int minPoints, EvalEngine engine) {
    IExpr derivative = engine.evaluate(F.D(antiderivative, x));
    int checked = 0;
    for (double point : POINTS) {
      IExpr f = engine.evaluate(F.N(F.subst(integrand, x, F.num(point))));
      if (!f.isReal()) {
        continue;
      }
      double scale = Math.abs(f.evalf());
      if (Double.isNaN(scale) || Double.isInfinite(scale)) {
        continue;
      }
      IExpr d = engine.evaluate(F.N(F.subst(derivative, x, F.num(point))));
      if (!d.isNumber()) {
        return Verdict.DISAGREES;
      }
      double deviation = engine.evaluate(F.Abs(F.Subtract(d, f))).evalfNaN();
      if (Double.isNaN(deviation) || deviation > 1e-8 * (1.0 + scale)) {
        return Verdict.DISAGREES;
      }
      checked++;
    }
    return checked >= minPoints ? Verdict.AGREES : Verdict.INCONCLUSIVE;
  }

  /** <code>true</code> if at least three real sample points agree and none disagrees. */
  public static boolean agrees(IExpr antiderivative, IExpr integrand, IExpr x, EvalEngine engine) {
    return differentiatesBack(antiderivative, integrand, x, 3, engine) == Verdict.AGREES;
  }
}
