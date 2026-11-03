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

  /** Real and imaginary part in hundredths, one point in each quadrant. */
  private static final int[][] OFF_LINE_POINTS =
      {{43, 45}, {-67, 140}, {-140, -183}, {250, -75}};

  private static final int OFF_LINE_DIGITS = 30;

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

  /**
   * Diff-back check of a stage result: <code>D(result, x) - integrand</code> must vanish under
   * <code>Together</code> or <code>Simplify</code>.
   *
   * @return <code>false</code> for a missing result, one that still contains <code>Integrate</code>
   *         or special values, and a difference that is not zero
   */
  static boolean verifySymbolic(IExpr result, IExpr integrand, IExpr x, EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr diff = engine.evaluate(F.Together(F.Subtract(F.D(result, x), integrand)));
      if (diff.isZero()) {
        return true;
      }
      return engine.evaluate(F.Simplify(diff)).isZero();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /**
   * Compare <code>D(antiderivative, x)</code> with <code>integrand</code> at points off the real
   * line, where an antiderivative with a factor like <code>Sqrt(t^2)/t</code> may be wrong although
   * it is right on the real line. A machine value which agrees decides; one which doesn't may have
   * lost its digits in a large answer, so that {@link #OFF_LINE_DIGITS} digits at the exact point
   * decide.
   *
   * @param points the number of points, at most 4
   * @return <code>true</code> if at least two points give numbers, and these agree
   */
  public static boolean agreesOffTheRealLine(IExpr antiderivative, IExpr integrand, IExpr x,
      int points, EvalEngine engine) {
    IExpr derivative = engine.evaluate(F.D(antiderivative, x));
    int compared = 0;
    for (int i = 0; i < points && i < OFF_LINE_POINTS.length; i++) {
      int[] point = OFF_LINE_POINTS[i];
      IExpr machine = F.complexNum(point[0] / 100.0, point[1] / 100.0);
      IExpr.COMPARE_TERNARY agrees = compareAt(derivative, integrand, x, machine, 0, 1e-8, engine);
      if (agrees != IExpr.COMPARE_TERNARY.TRUE) {
        IExpr exact = F.complex(F.QQ(point[0], 100), F.QQ(point[1], 100));
        agrees = compareAt(derivative, integrand, x, exact, OFF_LINE_DIGITS, 1e-15, engine);
      }
      if (agrees == IExpr.COMPARE_TERNARY.FALSE) {
        return false;
      }
      if (agrees == IExpr.COMPARE_TERNARY.TRUE) {
        compared++;
      }
    }
    return compared >= 2;
  }

  /** The values at <code>z</code>, with <code>digits</code> digits or machine values for 0. */
  private static IExpr.COMPARE_TERNARY compareAt(IExpr derivative, IExpr integrand, IExpr x, IExpr z,
      int digits, double tolerance, EvalEngine engine) {
    IExpr f = engine.evaluate(numeric(F.subst(integrand, x, z), digits));
    IExpr d = engine.evaluate(numeric(F.subst(derivative, x, z), digits));
    if (!f.isNumber() || !d.isNumber() || !f.isSpecialsFree() || !d.isSpecialsFree()) {
      return IExpr.COMPARE_TERNARY.UNDECIDABLE;
    }
    double deviation = engine.evaluate(F.Abs(F.Subtract(d, f))).evalfNaN();
    double scale = engine.evaluate(F.Abs(f)).evalfNaN();
    return Double.isNaN(deviation) || deviation > tolerance * (1.0 + scale)
        ? IExpr.COMPARE_TERNARY.FALSE
        : IExpr.COMPARE_TERNARY.TRUE;
  }

  private static IExpr numeric(IExpr expr, int digits) {
    return digits > 0 ? F.N(expr, F.ZZ(digits)) : F.N(expr);
  }

  /**
   * Diff-back check of a stage result: real sample points ({@link #agrees}), then
   * <code>Together</code> and <code>Simplify</code> of <code>D(result, x) - integrand</code>, and
   * last the difference at <code>points</code> ({@link #vanishesNumerically}).
   *
   * @return <code>false</code> for a missing result, one that still contains <code>Integrate</code>
   *         or special values, and a difference that is not zero
   */
  static boolean verifyWithNumericFallback(IExpr result, IExpr integrand, IExpr x,
      double[] points, EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      if (agrees(result, integrand, x, engine)) {
        return true;
      }
      IExpr diff = F.Subtract(F.D(result, x), integrand);
      if (engine.evaluate(F.Together(diff)).isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(diff)).isZero()) {
        return true;
      }
      return vanishesNumerically(diff, x, points, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /**
   * True iff {@code expr} numerically vanishes at the {@code points} where it can be evaluated to a
   * number at all (at least two of them must be usable).
   */
  static boolean vanishesNumerically(IExpr expr, IExpr x, double[] points, EvalEngine engine) {
    int evaluated = 0;
    for (double point : points) {
      IExpr value;
      try {
        value = engine.evaluate(F.N(F.ReplaceAll(expr, F.Rule(x, F.num(point)))));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return false;
      }
      if (!value.isNumber() || !value.isSpecialsFree()) {
        continue;
      }
      evaluated++;
      if (!engine.evaluate(F.Less(F.Abs(value), F.num(1.0e-8))).isTrue()) {
        return false;
      }
    }
    return evaluated >= 2;
  }
}
