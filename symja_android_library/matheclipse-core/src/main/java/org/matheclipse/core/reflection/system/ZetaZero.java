package org.matheclipse.core.reflection.system;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.LossOfPrecisionException;
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

public class ZetaZero extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final int k = ast.arg1().toMachineInt();
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
        double tMinDouble = ast.arg2().evalfNaN();
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
   * Find the imaginary part of the {@code index}-th nontrivial zeta zero. A cheap
   * machine-precision Riemann-Siegel locate phase brackets the zero and seeds a high-precision root
   * polish; if that fast path fails to produce a valid bracket the method falls back to the
   * arbitrary-precision scan-and-bisect in {@link #zzFindZeroApfloat}.
   */
  private static Apfloat zzFindZero(FixedPrecisionApfloatHelper h, int index, Apfloat half,
      Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    double[] bracket = ZetaZero.locateBracketDouble(index);
    if (bracket != null) {
      Apfloat root =
          ZetaZero.zzPolish(h, bracket[0], bracket[1], half, quarter, two, logPi, tol, precision);
      if (root != null) {
        return root;
      }
    }
    return ZetaZero.zzFindZeroApfloat(h, index, half, quarter, two, logPi, tol, precision);
  }

  /**
   * Machine-precision locate phase: bracket the {@code index}-th zeta zero by scanning the
   * double-precision Riemann-Siegel {@code Z(t)} around the asymptotic estimate for a sign change.
   * The returned {@code {a, b}} bracket is kept deliberately wider than the Riemann-Siegel
   * truncation error so the sign change survives at full precision. Returns {@code null} if no sign
   * change is found within the widened window, so the caller can fall back to the
   * arbitrary-precision search.
   */
  private static double[] locateBracketDouble(int index) {
    double t0 = ZetaJS.zetaZeroEstimate(index);
    double lnArg = Math.log(t0 / (2.0 * Math.PI));
    double gap = 2.0 * Math.PI / Math.max(lnArg, 0.3);
    double step = gap / ZetaJS.ZZ_SCAN_SEGMENTS;

    for (int widen = 0; widen < ZetaJS.ZZ_MAX_WIDEN; widen++) {
      double lo = t0 - gap;
      if (lo <= 0.0) {
        lo = 0.1;
      }
      double hi = t0 + gap;
      double prev = lo;
      double fprev = ZetaJS.riemannSiegelZDouble(prev);
      double bestA = Double.NaN;
      double bestB = Double.NaN;
      double bestDist = Double.MAX_VALUE;
      int segments = (int) Math.ceil((hi - lo) / step);
      for (int i = 1; i <= segments; i++) {
        double cur = lo + step * i;
        if (cur > hi) {
          cur = hi;
        }
        double fcur = ZetaJS.riemannSiegelZDouble(cur);
        if (fprev != 0.0 && fcur != 0.0 && Math.signum(fprev) != Math.signum(fcur)) {
          double mid = 0.5 * (prev + cur);
          double dist = Math.abs(mid - t0);
          if (dist < bestDist) {
            bestDist = dist;
            bestA = prev;
            bestB = cur;
          }
        }
        prev = cur;
        fprev = fcur;
      }
      if (!Double.isNaN(bestA)) {
        return ZetaZero.refineBracketDouble(bestA, bestB);
      }
      gap *= 2.0;
    }
    return null;
  }

  /**
   * Tighten a double-precision sign-change bracket by bisection, but stop well above the
   * Riemann-Siegel truncation error (~{@code t^(-3/4)}) so the reported endpoints keep reliable
   * (opposite) signs when handed to the full-precision polish.
   */
  private static double[] refineBracketDouble(double a, double b) {
    double fa = ZetaJS.riemannSiegelZDouble(a);
    double t = Math.max(0.5 * (a + b), 1.0);
    double target = Math.max(10.0 * Math.pow(t, -0.75), 1e-9);
    for (int i = 0; i < 60 && (b - a) > target; i++) {
      double m = 0.5 * (a + b);
      double fm = ZetaJS.riemannSiegelZDouble(m);
      if (fm == 0.0) {
        break;
      }
      if (Math.signum(fm) == Math.signum(fa)) {
        a = m;
        fa = fm;
      } else {
        b = m;
      }
    }
    return new double[] {a, b};
  }

  /**
   * Machine-precision estimate of the imaginary part of the {@code index}-th zeta zero, used to
   * calibrate indices against a lower bound without paying for an arbitrary-precision root polish.
   */
  private static double zeroImagDouble(int index) {
    double[] bracket = ZetaZero.locateBracketDouble(index);
    if (bracket == null) {
      return ZetaJS.zetaZeroEstimate(index);
    }
    return 0.5 * (bracket[0] + bracket[1]);
  }

  /**
   * Polish a zero to full precision from a double-precision bracket using the secant method with a
   * bisection safeguard on the arbitrary-precision Riemann-Siegel {@code Z(t)}. The secant iterates
   * converge superlinearly (order ~1.6), so only a handful of arbitrary-precision zeta evaluations
   * are needed instead of the ~{@code 3.3 * precision} of a bisection. The bracket {@code [a, b]} is
   * kept straddling the root and a bisection step is taken whenever a secant step would leave it.
   * Returns {@code null} if the bracket does not straddle a sign change at full precision, so the
   * caller can fall back to the arbitrary-precision search.
   */
  private static Apfloat zzPolish(FixedPrecisionApfloatHelper h, double aDouble, double bDouble,
      Apfloat half, Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    Apfloat a = new Apfloat(aDouble, precision);
    Apfloat b = new Apfloat(bDouble, precision);
    Apfloat fa;
    Apfloat fb;
    try {
      fa = ZetaZero.zzZ(h, a, half, quarter, two, logPi);
      if (fa.signum() == 0) {
        return a;
      }
      fb = ZetaZero.zzZ(h, b, half, quarter, two, logPi);
      if (fb.signum() == 0) {
        return b;
      }
    } catch (LossOfPrecisionException lop) {
      // an endpoint sits on the zero: zeta(1/2 + I*t) underflowed to zero at working precision
      return b;
    }
    if (fa.signum() == fb.signum()) {
      // the double-precision bracket did not straddle a sign change at full precision
      return null;
    }
    // secant memory: the two most recent iterates (x0, x1); the bracket [a, b] is the safeguard
    Apfloat x0 = a;
    Apfloat fx0 = fa;
    Apfloat x1 = b;
    Apfloat fx1 = fb;
    long maxIter = Math.min(100000L, precision * 4 + 80);
    for (long i = 0; i < maxIter; i++) {
      Apfloat denom = fx1.subtract(fx0);
      Apfloat x2;
      if (denom.signum() != 0) {
        x2 = x1.subtract(fx1.multiply(x1.subtract(x0)).divide(denom));
        if (x2.compareTo(a) <= 0 || x2.compareTo(b) >= 0) {
          x2 = a.add(b).divide(two); // secant would leave the bracket: bisect instead
        }
      } else {
        x2 = a.add(b).divide(two);
      }
      Apfloat fx2;
      try {
        fx2 = ZetaZero.zzZ(h, x2, half, quarter, two, logPi);
      } catch (LossOfPrecisionException lop) {
        // zeta(1/2 + I*x2) underflowed to zero at working precision: x2 is the zero
        return x2;
      }
      if (fx2.signum() == 0 || ApfloatMath.abs(x2.subtract(x1)).compareTo(tol) < 0) {
        return x2;
      }
      // keep [a, b] straddling the root by replacing the like-signed endpoint
      if (fx2.signum() == fa.signum()) {
        a = x2;
        fa = fx2;
      } else {
        b = x2;
        fb = fx2;
      }
      x0 = x1;
      fx0 = fx1;
      x1 = x2;
      fx1 = fx2;
    }
    return x1;
  }

  /**
   * Arbitrary-precision fallback: find the imaginary part of the {@code index}-th nontrivial zeta
   * zero by scanning a bracket around the asymptotic estimate for a sign change of Z(t) and
   * refining it by bisection. The bracket is widened if no sign change is found. Used only when the
   * machine-precision locate phase fails to bracket the zero.
   */
  private static Apfloat zzFindZeroApfloat(FixedPrecisionApfloatHelper h, int index, Apfloat half,
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

    // estimate the number of zeros with imaginary part <= tMin via N(t) ~ theta(t)/Pi + 1, then
    // calibrate the index entirely in machine precision (tMin is a double bound). Only the final
    // selected zero is polished to full precision, so the calibration is essentially free.
    double tMinDouble = tMin.doubleValue();
    double nApprox = ZetaJS.riemannSiegelThetaDouble(tMinDouble) / Math.PI + 1.0;
    long m = Math.max(1, Math.round(nApprox) + 1);

    // correct the index so that zero m is the first zero strictly greater than tMin
    while (m > 1 && ZetaZero.zeroImagDouble((int) (m - 1)) > tMinDouble) {
      m--;
    }
    while (ZetaZero.zeroImagDouble((int) m) <= tMinDouble) {
      m++;
    }
    return ZetaZero.zzFindZero(h, (int) (m + k - 1), half, quarter, two, logPi, tol, precision);
  }
}
