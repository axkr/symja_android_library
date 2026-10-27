package org.matheclipse.core.integrate;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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

  /**
   * Differentiate-back check of a stage result: <code>D(result, x) - integrand</code> must vanish
   * under <code>Together</code>, <code>Simplify</code> or (with <code>tryTrigReduce</code>)
   * <code>Simplify(TrigReduce(...))</code>. If none of them proves it, a result whose difference
   * vanishes numerically at four sample points in <code>(0, Pi)</code> is accepted as well: that
   * accepts some results which no symbolic step proved.
   *
   * @return <code>false</code> for a missing result, one that still contains <code>Integrate</code>
   *         or special values, and a difference that is not zero
   */
  static boolean verifySymbolicOrSampled(IExpr result, IExpr integrand, IExpr x,
      boolean tryTrigReduce, EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr diff = F.Subtract(F.D(result, x), integrand);
      if (engine.evaluate(F.Together(diff)).isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(diff)).isZero()) {
        return true;
      }
      if (tryTrigReduce && engine.evaluate(F.Simplify(F.TrigReduce(diff))).isZero()) {
        return true;
      }
      // special function and half-angle antiderivatives often do not reduce symbolically (Log
      // branch identities, Tan(x/2)); the sample points decide
      return vanishesAtSamplePoints(diff, x, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /** True iff {@code expr} numerically vanishes at every sample point (all chosen in (0, Pi)). */
  static boolean vanishesAtSamplePoints(IExpr expr, IExpr x, EvalEngine engine) {
    final double[] points = {0.35, 0.9, 1.4, 2.3};
    for (double pt : points) {
      IExpr check;
      try {
        check =
            engine.evaluate(F.Less(F.Abs(F.ReplaceAll(expr, F.Rule(x, F.num(pt)))), F.num(1.0e-9)));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return false;
      }
      if (!check.isTrue()) {
        return false;
      }
    }
    return true;
  }
}
