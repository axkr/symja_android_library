package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
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
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.ZetaJS;

public class ZetaZero extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    final IExpr arg1 = ast.arg1();
    final int index = arg1.isInteger() ? arg1.toIntDefault() : 0;
    if (index == 0 || index == Integer.MIN_VALUE) {
      if (arg1.isNumber()) {
        // ZetaZero(0) and ZetaZero(3.0) - Nonzero integer expected at position `1` in `2`.
        return Errors.printMessage(S.ZetaZero, "intnz", F.List(F.C1, S.ZetaZero), engine);
      }
      return F.NIL;
    }
    // ZetaZero(-k) is the conjugate of the k-th zero
    final boolean conjugate = index < 0;
    final int k = Math.abs(index);
    if (conjugate && ast.isAST2()) {
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

      Apfloat imaginaryPart = ZetaZero.zetaZeroImaginaryPart(h, k, tMin);
      if (conjugate) {
        imaginaryPart = imaginaryPart.negate();
      }
      if (arbitrary) {
        final Apfloat half = new Apfloat("0.5", precision);
        return F.complexNum(new Apcomplex(half, imaginaryPart.precision(precision)));
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
    newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NHOLDFIRST, Attribute.NUMERICFUNCTION);
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
   * Find the imaginary part of the {@code index}-th nontrivial zeta zero. A cheap machine-precision
   * Riemann-Siegel locate phase brackets the zero and seeds a high-precision root polish; if that
   * fast path fails to produce a valid bracket the method falls back to the arbitrary-precision
   * scan-and-bisect in {@link #zzFindZeroApfloat}.
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

  /** Above this height the O(t) double-precision Z gives way to the O(sqrt(t)) Riemann-Siegel Z. */
  private static final double EM_DOUBLE_LIMIT = 2.0e6;

  /**
   * Machine-precision {@code Z(t)}. Up to {@link #EM_DOUBLE_LIMIT} it is computed from zeta(1/2 +
   * I*t) by Euler-Maclaurin summation, accurate to about {@code 1e-9} at {@code t ~ 1e5} - enough to
   * see the tiny extremum between the two zeros of a Lehmer pair, which the leading Riemann-Siegel
   * term alone cannot; above it the Riemann-Siegel formula takes over.
   */
  static double zDouble(double t) {
    if (t > EM_DOUBLE_LIMIT) {
      return ZetaJS.riemannSiegelZDouble(t);
    }
    // N direct terms with t/(2 Pi N) = 1/Pi, so the Bernoulli tail shrinks by 1/Pi^2 per term
    int n = (int) Math.ceil(0.5 * t) + 30;
    double re = 0.0;
    double im = 0.0;
    for (int k = 1; k < n; k++) {
      double lnk = Math.log(k);
      double mag = 1.0 / Math.sqrt(k);
      double phase = t * lnk;
      re += mag * Math.cos(phase);
      im -= mag * Math.sin(phase);
    }
    double lnN = Math.log(n);
    double magN = 1.0 / Math.sqrt(n);
    // N^(-s)
    double nsRe = magN * Math.cos(t * lnN);
    double nsIm = -magN * Math.sin(t * lnN);
    // N^(1-s)/(s-1) with s-1 = -1/2 + I*t
    double aRe = n * nsRe;
    double aIm = n * nsIm;
    double den = 0.25 + t * t;
    re += (aRe * -0.5 + aIm * t) / den;
    im += (aIm * -0.5 - aRe * t) / den;
    re += 0.5 * nsRe;
    im += 0.5 * nsIm;
    // Bernoulli tail: B_2j/(2j)! * s(s+1)...(s+2j-2) * N^(-s-2j+1)
    double riseRe = 0.5;
    double riseIm = t;
    double powRe = nsRe / n;
    double powIm = nsIm / n;
    for (int j = 1; j <= 10; j++) {
      double c = BERNOULLI_OVER_FACTORIAL[j - 1];
      re += c * (riseRe * powRe - riseIm * powIm);
      im += c * (riseRe * powIm + riseIm * powRe);
      // next rising factorial: times (s+2j-1)(s+2j)
      for (int m = 2 * j - 1; m <= 2 * j; m++) {
        double fRe = 0.5 + m;
        double nRe = riseRe * fRe - riseIm * t;
        double nIm = riseRe * t + riseIm * fRe;
        riseRe = nRe;
        riseIm = nIm;
      }
      powRe /= (double) n * n;
      powIm /= (double) n * n;
    }
    double theta = ZetaJS.riemannSiegelThetaDouble(t);
    return Math.cos(theta) * re - Math.sin(theta) * im;
  }

  /** B_2j / (2j)! for j = 1..10. */
  private static final double[] BERNOULLI_OVER_FACTORIAL = {1.0 / 12.0, -1.0 / 720.0,
      1.0 / 30240.0, -1.0 / 1209600.0, 1.0 / 47900160.0, -691.0 / 1307674368000.0,
      7.0 / 523069747200.0, -3617.0 / 10670622842880000.0, 43867.0 / 5109094217170944000.0,
      -174611.0 / 802857662698291200000.0};

  /** The Gram point g_n, where theta(g_n) = n*Pi, for {@code n >= -1}. */
  static double gramPoint(int n) {
    double t = Math.max(9.0, ZetaJS.zetaZeroEstimate(n + 1));
    for (int i = 0; i < 60; i++) {
      double f = ZetaJS.riemannSiegelThetaDouble(t) - n * Math.PI;
      double dt = f / (0.5 * Math.log(t / (2.0 * Math.PI)));
      t -= dt;
      if (Math.abs(dt) <= 1e-13 * t) {
        break;
      }
    }
    return t;
  }

  /** Whether g_n is a good Gram point, (-1)^n Z(g_n) > 0; then N(g_n) = n + 1 (Rosser's rule). */
  private static boolean isGoodGramPoint(int n, double g) {
    double z = zDouble(g);
    return (n % 2 == 0) ? z > 0.0 : z < 0.0;
  }

  /**
   * Machine-precision locate phase: bracket the {@code index}-th zeta zero by counting.
   *
   * <p>
   * The zero is looked for between two good Gram points g_a < g_b around it, where the number of
   * zeros below each is known: {@code N(g_n) = n + 1} (Rosser's rule). The sign changes of Z in
   * between are counted, and when there are fewer than {@code b - a} a pair of zeros is hiding in
   * a step - found where Z comes close to the axis and turns back. The {@code index}-th zero is
   * then picked by its position in the count, not by its distance to an estimate, which gets a
   * close pair (the Lehmer pair #6709/#6710, 0.038 apart) and a zero off its Gram interval right.
   *
   * @return the bracket, or {@code null} to fall back to the arbitrary-precision search
   */
  private static double[] locateBracketDouble(int index) {
    int b = index - 1;
    double gb = gramPoint(b);
    for (int i = 0; i < 100 && !isGoodGramPoint(b, gb); i++) {
      gb = gramPoint(++b);
    }
    int a = index - 2;
    double ga = a >= -1 ? gramPoint(a) : 1.0;
    for (int i = 0; i < 100 && a >= 0 && !isGoodGramPoint(a, ga); i++) {
      ga = gramPoint(--a);
    }
    // below g_{-1} ~ 9.67 there is no zero at all
    int zerosBelowLeft = Math.max(a + 1, 0);
    int expected = (b + 1) - zerosBelowLeft;
    double gap = 2.0 * Math.PI / Math.max(Math.log(gb / (2.0 * Math.PI)), 0.3);
    double step = gap / 8.0;
    List<double[]> brackets = null;
    for (int refine = 0; refine < 8; refine++) {
      brackets = signChanges(ga, gb, step, expected);
      if (brackets.size() >= expected) {
        break;
      }
      step *= 0.5;
    }
    int position = index - zerosBelowLeft - 1;
    if (brackets.size() != expected || position < 0 || position >= brackets.size()) {
      return null;
    }
    double[] bracket = brackets.get(position);
    return ZetaZero.refineBracketDouble(bracket[0], bracket[1]);
  }

  /**
   * The sign-change brackets of Z in {@code [lo, hi]}, sampled with {@code step}; when fewer than
   * {@code expected} are seen, each place where Z approaches the axis and turns back is searched
   * for a hidden pair.
   */
  private static List<double[]> signChanges(double lo, double hi, double step, int expected) {
    int n = Math.max(2, (int) Math.ceil((hi - lo) / step));
    double[] x = new double[n + 1];
    double[] z = new double[n + 1];
    for (int i = 0; i <= n; i++) {
      x[i] = i == n ? hi : lo + (hi - lo) * i / n;
      z[i] = zDouble(x[i]);
    }
    List<double[]> brackets = new ArrayList<double[]>();
    for (int i = 1; i <= n; i++) {
      if (z[i - 1] != 0.0 && z[i] != 0.0 && Math.signum(z[i - 1]) != Math.signum(z[i])) {
        brackets.add(new double[] {x[i - 1], x[i]});
      }
    }
    if (brackets.size() >= expected) {
      return brackets;
    }
    // candidates for a hidden pair: a sample closer to the axis than both its neighbours
    List<Integer> candidates = new ArrayList<Integer>();
    for (int i = 1; i < n; i++) {
      double s = Math.signum(z[i]);
      if (s != 0 && Math.signum(z[i - 1]) == s && Math.signum(z[i + 1]) == s
          && Math.abs(z[i]) <= Math.abs(z[i - 1]) && Math.abs(z[i]) <= Math.abs(z[i + 1])) {
        candidates.add(i);
      }
    }
    candidates.sort((p, q) -> Double.compare(Math.abs(z[p]), Math.abs(z[q])));
    for (int i : candidates) {
      if (brackets.size() >= expected) {
        break;
      }
      double sign = Math.signum(z[i]);
      double m = closestToAxis(x[i - 1], x[i + 1], sign);
      double zm = zDouble(m);
      if (zm != 0.0 && Math.signum(zm) != sign) {
        brackets.add(new double[] {x[i - 1], m});
        brackets.add(new double[] {m, x[i + 1]});
      }
    }
    brackets.sort((p, q) -> Double.compare(p[0], q[0]));
    return brackets;
  }

  /** Golden-section search for the point of {@code [a, b]} where {@code sign * Z} is least. */
  private static double closestToAxis(double a, double b, double sign) {
    final double r = 0.5 * (Math.sqrt(5.0) - 1.0);
    double c = b - r * (b - a);
    double d = a + r * (b - a);
    double fc = sign * zDouble(c);
    double fd = sign * zDouble(d);
    for (int i = 0; i < 60 && (b - a) > 1e-12 * Math.max(1.0, Math.abs(b)); i++) {
      if (fc < 0.0) {
        return c;
      }
      if (fd < 0.0) {
        return d;
      }
      if (fc < fd) {
        b = d;
        d = c;
        fd = fc;
        c = b - r * (b - a);
        fc = sign * zDouble(c);
      } else {
        a = c;
        c = d;
        fc = fd;
        d = a + r * (b - a);
        fd = sign * zDouble(d);
      }
    }
    return fc < fd ? c : d;
  }

  /**
   * Tighten a double-precision sign-change bracket by bisection, down to a width of about
   * {@code 1e-12 t} - well above the error of {@link #zDouble(double)}, so the endpoints keep
   * reliable opposite signs at full precision, and narrow enough that the full precision polish
   * needs only a few evaluations. Above {@link #EM_DOUBLE_LIMIT} the Riemann-Siegel truncation
   * error (~{@code t^(-3/4)}) sets the width instead.
   */
  private static double[] refineBracketDouble(double a, double b) {
    double fa = zDouble(a);
    double t = Math.max(0.5 * (a + b), 1.0);
    double target = t > EM_DOUBLE_LIMIT ? Math.max(10.0 * Math.pow(t, -0.75), 1e-9)
        : 1e-12 * Math.max(t, 100.0);
    for (int i = 0; i < 80 && (b - a) > target; i++) {
      double m = 0.5 * (a + b);
      double fm = zDouble(m);
      if (fm == 0.0) {
        return new double[] {m - target, m + target};
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
   * are needed instead of the ~{@code 3.3 * precision} of a bisection. The bracket {@code [a, b]}
   * is kept straddling the root and a bisection step is taken whenever a secant step would leave
   * it. Returns {@code null} if the bracket does not straddle a sign change at full precision, so
   * the caller can fall back to the arbitrary-precision search.
   */
  private static Apfloat zzPolish(FixedPrecisionApfloatHelper h, double aDouble, double bDouble,
      Apfloat half, Apfloat quarter, Apfloat two, Apfloat logPi, Apfloat tol, long precision) {
    // above CRITICAL_LINE_LIMIT the zeta values come from the summation of CriticalLineZ, which
    // is much faster at large t than the general arbitrary precision zeta
    java.util.function.UnaryOperator<Apfloat> z = bDouble >= CRITICAL_LINE_LIMIT
        ? new CriticalLineZ(precision, bDouble)::z
        : x -> ZetaZero.zzZ(h, x, half, quarter, two, logPi);
    Apfloat a = new Apfloat(aDouble, precision);
    Apfloat b = new Apfloat(bDouble, precision);
    // converged at a couple of units in the last place of t: a secant step that small cannot be
    // resolved at this precision, and a looser test leaves the last digits unfinished
    tol = ApfloatMath.pow(new Apfloat(10, precision), -precision)
        .multiply(new Apfloat(2.0 * Math.max(1.0, bDouble), precision));
    Apfloat fa;
    Apfloat fb;
    try {
      fa = z.apply(a);
      if (fa.signum() == 0) {
        return a;
      }
      fb = z.apply(b);
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
        if (ApfloatMath.abs(x2.subtract(x1)).compareTo(tol) < 0) {
          // converged - possibly onto the endpoint the last step became, which the bracket test
          // below would take for leaving the bracket and answer with a slow bisection
          return x2;
        }
        if (x2.compareTo(a) <= 0 || x2.compareTo(b) >= 0) {
          x2 = a.add(b).divide(two); // secant would leave the bracket: bisect instead
        }
      } else {
        x2 = a.add(b).divide(two);
      }
      Apfloat fx2;
      try {
        fx2 = z.apply(x2);
      } catch (LossOfPrecisionException lop) {
        // zeta(1/2 + I*x2) underflowed to zero at working precision: x2 is the zero
        return x2;
      }
      if (fx2.signum() == 0 || ApfloatMath.abs(x2.subtract(x1)).compareTo(tol) < 0) {
        return x2;
      }
      if (ApfloatMath.abs(fx2).compareTo(ApfloatMath.abs(fx1)) >= 0
          && ApfloatMath.abs(x2.subtract(x1)).compareTo(tol.multiply(new Apfloat(1000))) < 0) {
        // Z no longer shrinks: the iterates are at the noise floor of the working precision
        return x1;
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
   * @return the imaginary part {@code t_k} such that {@code zeta(1/2 + I*t_k) == 0}, with three
   *         guard digits beyond the helper's precision for the caller to round
   */
  private static Apfloat zetaZeroImaginaryPart(FixedPrecisionApfloatHelper h, int k, Apfloat tMin) {
    // polished with guard digits: evaluated at exactly the requested precision the noise of Z
    // leaves the last digit or two of the zero undecided
    return zetaZeroImaginaryPartAt(new FixedPrecisionApfloatHelper(h.precision() + 3), k, tMin);
  }

  private static Apfloat zetaZeroImaginaryPartAt(FixedPrecisionApfloatHelper h, int k,
      Apfloat tMin) {
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

  /** From this height on the zero is polished with {@link CriticalLineZ}. */
  private static final double CRITICAL_LINE_LIMIT = 200.0;

  /**
   * The Riemann-Siegel {@code Z(t)} at arbitrary precision for large {@code t}, from zeta(1/2 +
   * I*t) by Euler-Maclaurin summation.
   *
   * <p>
   * The direct sum runs to {@code N ~ t/2}, so that {@code t/(2 Pi N) = 1/Pi} and every Bernoulli
   * term of the tail is about {@code 1/Pi^2} of the one before. Its terms {@code k^(-1/2 - I*t)}
   * are completely multiplicative, so only the primes need a complex exponential; every other term
   * is the product of two earlier ones. The logarithms and square roots of the primes do not depend
   * on {@code t} and are kept for the few evaluations a root polish makes. The work precision has
   * guard digits for the phase {@code t*log(k)} of every term.
   */
  static final class CriticalLineZ {
    private final long precision;
    private final int n;
    private final FixedPrecisionApfloatHelper hw;
    private final int[] smallestFactor;
    private final Apfloat[] logPrime;
    private final Apfloat[] invSqrtPrime;
    private final Apfloat half;
    private final Apfloat quarter;
    private final Apfloat two;
    private final Apfloat logPi;
    private final Apfloat epsilon;
    private final List<Apfloat> bernoulliOverFactorial = new ArrayList<Apfloat>();

    CriticalLineZ(long precision, double tMax) {
      this.precision = precision;
      this.n = (int) Math.ceil(0.5 * tMax) + 30;
      long work =
          precision + (long) Math.ceil(Math.log10(tMax * Math.log(n) + 10.0)) + 6;
      this.hw = new FixedPrecisionApfloatHelper(work);
      this.half = new Apfloat("0.5", work);
      this.quarter = new Apfloat("0.25", work);
      this.two = new Apfloat(2, work);
      this.logPi = ApfloatMath.log(ApfloatMath.pi(work));
      this.epsilon = ApfloatMath.pow(new Apfloat(10, work), -work);
      smallestFactor = new int[n + 1];
      logPrime = new Apfloat[n + 1];
      invSqrtPrime = new Apfloat[n + 1];
      for (int k = 2; k <= n; k++) {
        if (smallestFactor[k] == 0) {
          for (long m = k; m <= n; m += k) {
            if (smallestFactor[(int) m] == 0) {
              smallestFactor[(int) m] = k;
            }
          }
          Apfloat pk = new Apfloat(k, work);
          logPrime[k] = hw.log(pk);
          invSqrtPrime[k] = hw.inverseRoot(pk, 2);
        }
      }
    }

    /** The coefficient B_2j / (2j)! of the Euler-Maclaurin tail. */
    private Apfloat bernoulliOverFactorial(int j) {
      while (bernoulliOverFactorial.size() < j) {
        int m = 2 * (bernoulliOverFactorial.size() + 1);
        org.matheclipse.core.interfaces.IRational b =
            org.matheclipse.core.expression.AbstractFractionSym.bernoulliNumber(m);
        java.math.BigInteger factorial = java.math.BigInteger.ONE;
        for (int i = 2; i <= m; i++) {
          factorial = factorial.multiply(java.math.BigInteger.valueOf(i));
        }
        Apfloat numerator = new Apfloat(b.toBigNumerator(), hw.precision());
        Apfloat denominator =
            new Apfloat(b.toBigDenominator().multiply(factorial), hw.precision());
        bernoulliOverFactorial.add(hw.divide(numerator, denominator));
      }
      return bernoulliOverFactorial.get(j - 1);
    }

    /** Z(t) at the precision this object was made for. */
    Apfloat z(Apfloat tIn) {
      Apfloat t = tIn.precision(hw.precision());
      Apcomplex[] power = new Apcomplex[n + 1];
      power[1] = Apcomplex.ONE;
      Apcomplex sum = Apcomplex.ONE;
      for (int k = 2; k <= n; k++) {
        int p = smallestFactor[k];
        if (p == k) {
          Apfloat phase = hw.multiply(t, logPrime[k]);
          power[k] = new Apcomplex(hw.multiply(invSqrtPrime[k], hw.cos(phase)),
              hw.multiply(invSqrtPrime[k], hw.sin(phase)).negate());
        } else {
          power[k] = hw.multiply(power[p], power[k / p]);
        }
        if (k < n) {
          sum = hw.add(sum, power[k]);
        }
      }
      Apcomplex s = new Apcomplex(half, t);
      Apfloat bigN = new Apfloat(n, hw.precision());
      Apcomplex powerN = power[n];
      // N^(1-s)/(s-1) + N^(-s)/2
      sum = hw.add(sum,
          hw.divide(hw.multiply(powerN, bigN), hw.subtract(s, Apcomplex.ONE)));
      sum = hw.add(sum, hw.divide(powerN, two));
      // Bernoulli tail: B_2j/(2j)! * s(s+1)...(s+2j-2) * N^(-s-2j+1)
      Apcomplex rising = s;
      Apcomplex tailPower = hw.divide(powerN, bigN);
      Apfloat nSquared = hw.multiply(bigN, bigN);
      Apfloat previous = null;
      for (int j = 1; j <= 400; j++) {
        Apcomplex term = hw.multiply(hw.multiply(rising, tailPower), bernoulliOverFactorial(j));
        Apfloat size = hw.abs(term);
        sum = hw.add(sum, term);
        if (size.compareTo(epsilon) < 0 || (previous != null && size.compareTo(previous) > 0)) {
          break;
        }
        previous = size;
        rising = hw.multiply(hw.multiply(rising, hw.add(s, new Apfloat(2 * j - 1))),
            hw.add(s, new Apfloat(2 * j)));
        tailPower = hw.divide(tailPower, nSquared);
      }
      Apfloat theta = RiemannSiegelTheta.zzTheta(hw, t, quarter, two, logPi);
      Apfloat value = hw.subtract(hw.multiply(hw.cos(theta), sum.real()),
          hw.multiply(hw.sin(theta), sum.imag()));
      return value.precision(precision);
    }
  }

}
