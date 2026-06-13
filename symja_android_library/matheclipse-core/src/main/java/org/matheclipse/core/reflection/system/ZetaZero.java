package org.matheclipse.core.reflection.system;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.ZetaJS;

/**
 * <code>ZetaZero(k)</code> represents the <code>k</code>-th zero of the Riemann zeta function on
 * the critical line. <code>ZetaZero(k, t)</code> represents the <code>k</code>-th zero with
 * imaginary part greater than <code>t</code>.
 *
 * <p>
 * See <a href="https://reference.wolfram.com/language/ref/ZetaZero.html">ZetaZero</a>.
 */
public class ZetaZero extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final int k = ast.arg1().toIntDefault();
    if (k <= 0) {
      if (ast.arg1().isNumber()) {
        // Nonzero integer expected at position `1` in `2`.
        return Errors.printMessage(S.ZetaZero, "intnz", F.List(F.C1, S.ZetaZero), engine);
      }
      // unevaluated for k == 0 and negative k
      return F.NIL;
    }

    final boolean arbitrary = engine.isArbitraryMode();
    final boolean doubleMode = engine.isDoubleMode();
    if (!arbitrary && !doubleMode) {
      // keep symbolic if not evaluated numerically (e.g. via N(...))
      return F.NIL;
    }

    try {
      final FixedPrecisionApfloatHelper h =
          arbitrary ? EvalEngine.getApfloat() : EvalEngine.getApfloatDouble();
      final long precision = h.precision();

      Apfloat tMin = null;
      if (ast.isAST2()) {
        double tMinDouble;
        try {
          tMinDouble = ast.arg2().evalf();
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return F.NIL;
        }
        if (Double.isNaN(tMinDouble) || Double.isInfinite(tMinDouble)) {
          return F.NIL;
        }
        tMin = new Apfloat(tMinDouble, precision);
      }

      final Apfloat imaginaryPart = ZetaZero.zetaZeroImaginaryPart(h, k, tMin);
      if (arbitrary) {
        final Apfloat half = new Apfloat("0.5", precision);
        return F.complexNum(new Apcomplex(half, imaginaryPart));
      }
      return F.complexNum(0.5, imaginaryPart.doubleValue());
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.ZetaZero, rex, engine);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDFIRST | ISymbol.NUMERICFUNCTION);
  }

  /** Riemann-Siegel Z function at {@code t} (real-valued). */
  public static Apfloat zzZ(FixedPrecisionApfloatHelper h, Apfloat t, Apfloat half, Apfloat quarter,
      Apfloat two, Apfloat logPi) {
    Apfloat theta = RiemannSiegelTheta.zzTheta(h, t, quarter, two, logPi);
    Apcomplex s = new Apcomplex(half, t);
    Apcomplex zeta = h.zeta(s);
    Apcomplex factor = h.exp(new Apcomplex(Apfloat.ZERO, theta));
    return h.multiply(factor, zeta).real();
  }

  /** Refine a sign-change bracket {@code [a,b]} of Z(t) by bisection. */
  private static Apfloat zzBisect(FixedPrecisionApfloatHelper h, Apfloat a, Apfloat b, Apfloat half,
      Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    Apfloat fa = ZetaZero.zzZ(h, a, half, quarter, two, logPi);
    long maxIter = Math.min(100000L, precision * 4 + 80);
    Apfloat m = a.add(b).divide(two);
    for (long i = 0; i < maxIter; i++) {
      m = a.add(b).divide(two);
      Apfloat fm = ZetaZero.zzZ(h, m, half, quarter, two, logPi);
      if (fm.signum() == 0 || ApfloatMath.abs(b.subtract(a)).compareTo(tol) < 0) {
        return m;
      }
      if (fm.signum() == fa.signum()) {
        a = m;
        fa = fm;
      } else {
        b = m;
      }
    }
    return m;
  }

  /**
   * Find the imaginary part of the {@code index}-th nontrivial zeta zero by scanning a bracket
   * around the asymptotic estimate for a sign change of Z(t) and refining it. The bracket is
   * widened if no sign change is found.
   */
  private static Apfloat zzFindZero(FixedPrecisionApfloatHelper h, int index, Apfloat half,
      Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    double t0d = ZetaJS.zetaZeroEstimate(index);
    double lnArg = Math.log(t0d / (2.0 * Math.PI));
    double gapd = 2.0 * Math.PI / Math.max(lnArg, 0.3);
    Apfloat t0 = new Apfloat(t0d, precision);
    Apfloat gap = new Apfloat(gapd, precision);
    Apfloat segments = new Apfloat(ZetaJS.ZZ_SCAN_SEGMENTS, precision);

    for (int widen = 0; widen < ZetaJS.ZZ_MAX_WIDEN; widen++) {
      Apfloat lo = t0.subtract(gap);
      if (lo.signum() <= 0) {
        lo = new Apfloat("0.1", precision);
      }
      Apfloat hi = t0.add(gap);
      Apfloat step = hi.subtract(lo).divide(segments);

      Apfloat prev = lo;
      Apfloat fprev = ZetaZero.zzZ(h, prev, half, quarter, two, logPi);
      Apfloat bestA = null;
      Apfloat bestB = null;
      double bestDist = Double.MAX_VALUE;
      for (int i = 1; i <= ZetaJS.ZZ_SCAN_SEGMENTS; i++) {
        Apfloat cur = lo.add(step.multiply(new Apfloat(i, precision)));
        Apfloat fcur = ZetaZero.zzZ(h, cur, half, quarter, two, logPi);
        if (fprev.signum() != 0 && fcur.signum() != 0 && fprev.signum() != fcur.signum()) {
          double mid = prev.add(cur).divide(two).doubleValue();
          double dist = Math.abs(mid - t0d);
          if (dist < bestDist) {
            bestDist = dist;
            bestA = prev;
            bestB = cur;
          }
        }
        prev = cur;
        fprev = fcur;
      }
      if (bestA != null) {
        return ZetaZero.zzBisect(h, bestA, bestB, half, quarter, two, logPi, tol, precision);
      }
      gap = gap.multiply(two);
    }
    throw new ArgumentTypeException("ZetaZero: unable to bracket zero for index " + index);
  }

  /**
   * Compute the imaginary part of a nontrivial zeta zero on the critical line.
   *
   * @param h a fixed precision helper configured to the requested numeric precision
   * @param k the (1-based) zero index; for {@code tMin == null} this selects the k-th zero with
   *        smallest positive imaginary part
   * @param tMin if non-null, the result is the k-th zero whose imaginary part is greater than
   *        {@code tMin}
   * @return the imaginary part {@code t_k} such that {@code zeta(1/2 + I*t_k) == 0}
   */
  private static Apfloat zetaZeroImaginaryPart(FixedPrecisionApfloatHelper h, int k, Apfloat tMin) {
    long precision = h.precision();
    Apfloat two = new Apfloat(2, precision);
    Apfloat half = new Apfloat("0.5", precision);
    Apfloat quarter = new Apfloat("0.25", precision);
    Apfloat pi = ApfloatMath.pi(precision);
    Apfloat logPi = ApfloatMath.log(pi);
    Apfloat tol = ApfloatMath.pow(new Apfloat(10, precision), -(precision - 2));

    if (tMin == null) {
      return ZetaZero.zzFindZero(h, k, half, quarter, two, logPi, tol, precision);
    }

    // estimate the number of zeros with imaginary part <= tMin via N(t) ~ theta(t)/Pi + 1
    Apfloat thetaMin = RiemannSiegelTheta.zzTheta(h, tMin, quarter, two, logPi);
    double nApprox = thetaMin.doubleValue() / Math.PI + 1.0;
    long m = Math.max(1, Math.round(nApprox) + 1);

    // correct the index of the first zero strictly greater than tMin
    while (m > 1) {
      Apfloat zPrev =
          ZetaZero.zzFindZero(h, (int) (m - 1), half, quarter, two, logPi, tol, precision);
      if (zPrev.compareTo(tMin) > 0) {
        m--;
      } else {
        break;
      }
    }
    Apfloat zm = ZetaZero.zzFindZero(h, (int) m, half, quarter, two, logPi, tol, precision);
    while (zm.compareTo(tMin) <= 0) {
      m++;
      zm = ZetaZero.zzFindZero(h, (int) m, half, quarter, two, logPi, tol, precision);
    }
    return ZetaZero.zzFindZero(h, (int) (m + k - 1), half, quarter, two, logPi, tol, precision);
  }
}
