package org.matheclipse.core.numerics.series;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApcomplexMath;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The value of an infinite series <code>Sum(f(k), {k, a, Infinity})</code> with a given number of
 * digits. The terms are evaluated numerically with guard digits and added with a fixed precision.
 * <ul>
 * <li>Terms which decrease at least geometrically are added until they don't change the sum.
 * <li>The sum of real terms with alternating signs is the Cohen-Rodriguez Villegas-Zagier
 * acceleration of their first terms, checked against a longer acceleration.
 * <li>The sum of real terms of one sign is the Euler-Maclaurin formula: the first terms, the
 * integral of the tail and the derivative corrections at the start of the tail.
 * </ul>
 * A series which is none of these, or whose terms don't evaluate to numbers, has no value here.
 */
public final class PrecisionSum {

  /** The number of terms which are evaluated to classify the series. */
  private static final int PROBE = 16;

  /** The largest number of terms which are added one by one. */
  private static final long MAX_TERMS = 200_000L;

  /** A run of this many zero terms ends a sum of terms which decrease geometrically. */
  private static final int MAX_ZERO_RUN = 1000;

  /**
   * Terms which decrease more slowly than <code>k^-SLOW_DECAY</code> have a tail integral which is
   * hard to evaluate numerically.
   */
  private static final double SLOW_DECAY = 1.6;

  /** The time limit of the antiderivative of the tail and of its limit. */
  private static final long SYMBOLIC_SECONDS = 3L;

  /** The largest number of derivative corrections of the Euler-Maclaurin formula. */
  private static final int MAX_CORRECTIONS = 14;

  /** A derivative which grows beyond this leaf count ends the corrections. */
  private static final long MAX_DERIVATIVE_LEAVES = 4000L;

  private final IExpr function;
  private final ISymbol variable;
  /** the digits of the result */
  private final long digits;
  /** the digits of the terms and of the sum */
  private final long work;
  private final EvalEngine engine;
  private final FixedPrecisionApcomplexHelper helper;
  private final FixedPrecisionApfloatHelper realHelper;
  /** the exponent s of terms like k^-s, from {@link #decaysFasterThanHarmonic(long)} */
  private double decay = Double.POSITIVE_INFINITY;

  private PrecisionSum(IExpr function, ISymbol variable, long digits, EvalEngine engine) {
    this.function = function;
    this.variable = variable;
    this.digits = digits;
    this.work = digits + 10L;
    this.engine = engine;
    this.helper = new FixedPrecisionApcomplexHelper(work);
    this.realHelper = new FixedPrecisionApfloatHelper(work);
  }

  /**
   * The value of <code>Sum(function, {variable, lower, Infinity})</code> with <code>digits</code>
   * correct digits.
   *
   * @return <code>null</code> if the series isn't of a kind which is summed here
   */
  public static Apcomplex sum(IExpr function, ISymbol variable, long lower, long digits,
      EvalEngine engine) {
    return new PrecisionSum(function, variable, digits, engine).sum(lower);
  }

  /**
   * The value of <code>Sum(function, {variable, lower, Infinity})</code> for terms which don't
   * follow a smooth law of their own (an arithmetic function of the index), but are bounded in
   * size by the terms of <code>majorant</code>, which decrease geometrically: the terms are added
   * until those of the majorant don't change the sum.
   *
   * @return <code>null</code> if the majorant doesn't decrease geometrically
   */
  public static Apcomplex sumDominated(IExpr function, IExpr majorant, ISymbol variable,
      long lower, long digits, EvalEngine engine) {
    PrecisionSum bound = new PrecisionSum(majorant, variable, digits, engine);
    Apcomplex[] probe = new Apcomplex[PROBE];
    for (int i = 0; i < PROBE; i++) {
      probe[i] = bound.term(lower + i);
      if (probe[i] == null) {
        return null;
      }
    }
    if (!bound.decreasesGeometrically(probe) || !bound.decreasesGeometricallyFar(lower)) {
      return null;
    }
    return new PrecisionSum(function, variable, digits, engine).directDominated(lower, bound);
  }

  /** The terms added until three terms in a row of <code>bound</code> don't change the sum. */
  private Apcomplex directDominated(long lower, PrecisionSum bound) {
    Apcomplex sum = Apcomplex.ZERO;
    // the sum of the majorant: where the terms cancel to 0, the tail is small against it
    Apcomplex boundSum = Apcomplex.ZERO;
    int small = 0;
    for (long n = lower; n < lower + MAX_TERMS; n++) {
      if ((n & 63L) == 0L) {
        engine.checkTimeLimit();
      }
      Apcomplex t = term(n);
      Apcomplex b = bound.term(n);
      if (t == null || b == null) {
        return null;
      }
      sum = helper.add(sum, t);
      boundSum = helper.add(boundSum, b);
      if ((b.real().signum() == 0 && b.imag().signum() == 0) || negligible(b, sum)
          || (sum.real().signum() == 0 && sum.imag().signum() == 0
              && negligible(b, boundSum))) {
        if (++small >= 3) {
          return sum;
        }
      } else {
        small = 0;
      }
    }
    return null;
  }

  /** The numeric value of the term with index <code>n</code>, <code>null</code> if it has none. */
  private Apcomplex term(long n) {
    IExpr value = engine.evalN(F.subst(function, x -> x.equals(variable) ? F.ZZ(n) : F.NIL), work);
    if (value.isNumber()) {
      return helper.valueOf(((INumber) value).apcomplexValue());
    }
    return null;
  }

  private Apcomplex sum(long lower) {
    Apcomplex[] probe = new Apcomplex[PROBE];
    boolean real = true;
    for (int i = 0; i < PROBE; i++) {
      probe[i] = term(lower + i);
      if (probe[i] == null) {
        return null;
      }
      real &= probe[i].imag().signum() == 0;
    }
    if (decreasesGeometrically(probe) && decreasesGeometricallyFar(lower)) {
      return direct(lower);
    }
    if (!real) {
      // the Euler-Maclaurin formula holds for a smooth complex function too
      return withLevinFallback(eulerMaclaurin(lower), lower);
    }
    int signs = signPattern(probe);
    if (signs == 0) {
      return alternating(lower);
    }
    if (signs == 1) {
      return withLevinFallback(eulerMaclaurin(lower), lower);
    }
    return null;
  }

  /**
   * Test if the non-zero terms of the second half of the probe shrink by a factor of at most
   * <code>0.9</code> from one to the next.
   */
  private boolean decreasesGeometrically(Apcomplex[] probe) {
    Apfloat previous = null;
    int ratios = 0;
    for (int i = PROBE / 2; i < PROBE; i++) {
      Apfloat size = ApfloatMath.abs(probe[i].real()).add(ApfloatMath.abs(probe[i].imag()));
      if (size.signum() == 0) {
        continue;
      }
      if (previous != null) {
        if (size.doubleValue() > 0.9 * previous.doubleValue() && !tiny(size, previous)) {
          return false;
        }
        ratios++;
      }
      previous = size;
    }
    return ratios >= 3;
  }

  /**
   * Test if the terms shrink by a factor of at most <code>0.9</code> far beyond the probe too: the
   * ratio of the terms of a power <code>k^-s</code> approaches <code>1</code>, a few terms of it
   * shrink as fast as a geometric series.
   */
  private boolean decreasesGeometricallyFar(long lower) {
    long far = lower + 16L * PROBE;
    Apcomplex a = term(far);
    Apcomplex b = term(far + 1L);
    if (a == null || b == null) {
      return false;
    }
    Apfloat sizeA = ApfloatMath.abs(a.real()).add(ApfloatMath.abs(a.imag()));
    Apfloat sizeB = ApfloatMath.abs(b.real()).add(ApfloatMath.abs(b.imag()));
    if (sizeB.signum() == 0) {
      return true;
    }
    if (sizeA.signum() == 0) {
      return false;
    }
    return tiny(sizeB, sizeA) || sizeB.doubleValue() <= 0.9 * sizeA.doubleValue();
  }

  /** Test if the ratio of two magnitudes is below the range of a double. */
  private static boolean tiny(Apfloat size, Apfloat previous) {
    return size.scale() - previous.scale() < -300L;
  }

  /**
   * <code>0</code> if the real terms of the probe alternate in sign, <code>1</code> if they all
   * have the same sign, <code>-1</code> otherwise. A zero term is neither.
   */
  private static int signPattern(Apcomplex[] probe) {
    boolean alternating = true;
    boolean same = true;
    for (int i = 1; i < PROBE; i++) {
      int a = probe[i - 1].real().signum();
      int b = probe[i].real().signum();
      if (a == 0 || b == 0) {
        return -1;
      }
      alternating &= a == -b;
      same &= a == b;
    }
    return alternating ? 0 : same ? 1 : -1;
  }

  /** The terms added until three in a row don't change the sum. */
  private Apcomplex direct(long lower) {
    Apcomplex sum = Apcomplex.ZERO;
    int small = 0;
    int zeros = 0;
    for (long n = lower; n < lower + MAX_TERMS; n++) {
      if ((n & 63L) == 0L) {
        engine.checkTimeLimit();
      }
      Apcomplex t = term(n);
      if (t == null) {
        return null;
      }
      sum = helper.add(sum, t);
      if (t.real().signum() == 0 && t.imag().signum() == 0) {
        // a run of zero terms behind a negligible one, or a long run: the terms have ended
        zeros++;
        if ((small > 0 && zeros >= 3) || zeros >= MAX_ZERO_RUN) {
          return sum;
        }
        continue;
      }
      zeros = 0;
      if (negligible(t, sum)) {
        if (++small >= 3) {
          return sum;
        }
      } else {
        small = 0;
      }
    }
    return null;
  }

  /** Test if <code>t</code> is below the last working digit of <code>sum</code>. */
  private boolean negligible(Apcomplex t, Apcomplex sum) {
    long scale = Math.max(sum.real().scale(), sum.imag().scale());
    if (sum.real().signum() == 0 && sum.imag().signum() == 0) {
      return false;
    }
    long termScale = Math.max(t.real().signum() == 0 ? Long.MIN_VALUE : t.real().scale(),
        t.imag().signum() == 0 ? Long.MIN_VALUE : t.imag().scale());
    return termScale < scale - work;
  }

  /**
   * The Cohen-Rodriguez Villegas-Zagier acceleration with <code>n</code> terms of
   * <code>Sum((-1)^k a(k), {k, 0, Infinity})</code>, <code>a(k) = (-1)^k t(lower + k)</code>.
   */
  private Apfloat cvz(Apfloat[] a, int n) {
    FixedPrecisionApfloatHelper h = realHelper;
    Apfloat d = h.pow(h.add(new Apfloat(3, work), h.sqrt(new Apfloat(8, work))), n);
    d = h.divide(h.add(d, h.divide(Apfloat.ONE, d)), new Apfloat(2, work));
    Apfloat b = new Apfloat(-1, work);
    Apfloat c = h.negate(d);
    Apfloat s = Apfloat.ZERO;
    for (int k = 0; k < n; k++) {
      c = h.subtract(b, c);
      s = h.add(s, h.multiply(c, a[k]));
      // b = (k + n) (k - n) b / ((k + 1/2) (k + 1))
      b = h.divide(h.multiply(b, new Apfloat((long) (k + n) * (long) (k - n), work)),
          h.multiply(new Apfloat(2 * k + 1, work), new Apfloat(k + 1, work)));
      b = h.multiply(b, new Apfloat(2, work));
    }
    return h.divide(s, d);
  }

  private Apcomplex alternating(long lower) {
    int n1 = (int) Math.ceil(1.31 * work) + 4;
    int n2 = n1 + 12;
    Apfloat[] a = new Apfloat[n2];
    for (int k = 0; k < n2; k++) {
      if ((k & 63) == 0) {
        engine.checkTimeLimit();
      }
      Apcomplex t = term(lower + k);
      if (t == null || t.imag().signum() != 0) {
        return null;
      }
      a[k] = (k & 1) == 0 ? t.real() : realHelper.negate(t.real());
    }
    Apfloat s1 = cvz(a, n1);
    Apfloat s2 = cvz(a, n2);
    if (!agree(s1, s2)) {
      return null;
    }
    return s2;
  }

  /** Test if two values agree to the digits of the result and two more. */
  private boolean agree(Apfloat s1, Apfloat s2) {
    Apfloat difference = ApfloatMath.abs(realHelper.subtract(s1, s2));
    if (difference.signum() == 0) {
      return true;
    }
    Apfloat size = ApfloatMath.abs(s2);
    if (size.signum() == 0) {
      return false;
    }
    return difference.scale() < size.scale() - digits - 2;
  }

  /**
   * The Euler-Maclaurin formula
   * <code>Sum(f(k), {k, a, N-1}) + Integrate(f(x), {x, N, Infinity}) + f(N)/2 - Sum(B(2j)/(2j)! f^(2j-1)(N), {j, 1, m})</code>,
   * with <code>N</code> large enough that the corrections fall below the digits of the result.
   */
  private Apcomplex eulerMaclaurin(long lower) {
    if (!function.isFree(S.NSum, true) || !decaysFasterThanHarmonic(lower)) {
      // an inner numerical sum has no symbolic derivative
      return null;
    }
    // the odd derivatives f', f''', ... while they stay small
    // fewer corrections for fewer digits: a derivative costs more than the terms it saves
    final int corrections = (int) Math.max(4L, Math.min(MAX_CORRECTIONS, (work + 3L) / 4L));
    IExpr[] derivatives = new IExpr[corrections];
    int m = 0;
    IExpr derivative = engine.evaluate(F.D(function, variable));
    while (m < corrections) {
      if (!derivative.isFree(x -> x.isAST(S.Derivative) || x.isAST(S.D), true)
          || derivative.leafCount() > MAX_DERIVATIVE_LEAVES) {
        break;
      }
      derivatives[m++] = derivative;
      if (m < corrections) {
        engine.checkTimeLimit();
        derivative = engine.evaluate(F.D(derivative, F.list(variable, F.C2)));
      }
    }
    if (m < 2) {
      return null;
    }
    // the j-th correction of a power k^-s at N is about 2 ((2j)/(2 Pi e N))^(2j)
    double start = 2.0 * m * Math.pow(10.0, (work + 2.0) / (2.0 * m)) / (2.0 * Math.PI * Math.E);
    long tail = Math.max(lower + PROBE, (long) Math.ceil(start));
    if (tail - lower > MAX_TERMS) {
      return null;
    }
    Apcomplex sum = Apcomplex.ZERO;
    for (long n = lower; n < tail; n++) {
      if ((n & 63L) == 0L) {
        engine.checkTimeLimit();
      }
      Apcomplex t = term(n);
      if (t == null) {
        return null;
      }
      sum = helper.add(sum, t);
    }
    IExpr integral = tailIntegral(tail);
    if (!integral.isNumber()) {
      return null;
    }
    sum = helper.add(sum, helper.valueOf(((INumber) integral).apcomplexValue()));
    Apcomplex atTail = term(tail);
    if (atTail == null) {
      return null;
    }
    sum = helper.add(sum, helper.divide(atTail, new Apfloat(2, work)));
    Apcomplex last = Apcomplex.ZERO;
    for (int j = 1; j <= m; j++) {
      // B(2j)/(2j)!
      IExpr coefficient = engine.evalN(F.Divide(F.BernoulliB(F.ZZ(2 * j)), F.Factorial(F.ZZ(2 * j))), work);
      IExpr value = engine.evalN(F.subst(derivatives[j - 1], x -> x.equals(variable) ? F.ZZ(tail) : F.NIL),
          work);
      if (!coefficient.isNumber() || !value.isNumber()) {
        return null;
      }
      last = helper.multiply(helper.valueOf(((INumber) coefficient).apcomplexValue()),
          helper.valueOf(((INumber) value).apcomplexValue()));
      sum = helper.subtract(sum, last);
    }
    // the last correction bounds the error of the formula
    if (!negligibleCorrection(last, sum)) {
      return null;
    }
    return sum;
  }

  /**
   * <code>Integrate(f(x), {x, tail, Infinity})</code>. A tail which decreases slowly, like
   * <code>1/(x Log(x)^2)</code>, is the limit of an antiderivative at infinity, if it has one which
   * agrees with the numerical integral from <code>tail</code> to <code>2 tail</code>; else, and for
   * a fast decrease, it is the numerical integral.
   */
  private IExpr tailIntegral(long tail) {
    if (decay < SLOW_DECAY) {
      IExpr symbolic = symbolicTailIntegral(tail);
      if (symbolic.isPresent()) {
        return symbolic;
      }
    }
    IExpr numeric = engine.evalQuiet(F.NIntegrate(function,
        F.list(variable, F.ZZ(tail), F.CInfinity), F.Rule(S.WorkingPrecision, F.ZZ(work))));
    if (!numeric.isNumber()) {
      return F.NIL;
    }
    // a numerical integral without the working precision is no tail of the digits of the sum
    if (numeric.isInexactNumber()
        && ((INumber) numeric).apcomplexValue().precision() >= digits + 2L) {
      return numeric;
    }
    return decay < SLOW_DECAY ? F.NIL : symbolicTailIntegral(tail);
  }

  /**
   * The tail integral as the limit of an antiderivative at infinity, if the antiderivative agrees
   * with the numerical integral from <code>tail</code> to <code>2 tail</code>.
   *
   * @return {@link F#NIL} if there is no such antiderivative
   */
  private IExpr symbolicTailIntegral(long tail) {
    IExpr antiderivative = engine.evalQuiet(
        F.TimeConstrained(F.Integrate(function, variable), F.ZZ(SYMBOLIC_SECONDS)));
    if (antiderivative.isFree(x -> x.isAST(S.Integrate) || x == S.$Aborted, true)) {
      IExpr limit = engine.evalQuiet(F.TimeConstrained(
          F.Limit(antiderivative, F.Rule(variable, F.CInfinity)), F.ZZ(SYMBOLIC_SECONDS)));
      if (limit.isNumericFunction(true)) {
        IExpr atTail = F.subst(antiderivative, x -> x.equals(variable) ? F.ZZ(tail) : F.NIL);
        IExpr atDouble =
            F.subst(antiderivative, x -> x.equals(variable) ? F.ZZ(2L * tail) : F.NIL);
        IExpr check = engine.evalQuiet(F.NIntegrate(function,
            F.list(variable, F.ZZ(tail), F.ZZ(2L * tail))));
        IExpr difference = engine.evalN(F.Subtract(atDouble, atTail));
        if (check.isNumber() && difference.isNumber()) {
          double c = ((INumber) check).evalf();
          double d = ((INumber) difference).evalf();
          if (Math.abs(c - d) <= 1e-8 * Math.max(Math.abs(c), Double.MIN_NORMAL)) {
            IExpr value = engine.evalN(F.Subtract(limit, atTail), work);
            if (value.isNumber()) {
              return value;
            }
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * The value of the Euler-Maclaurin formula, or, if it has none because the terms have no
   * symbolic derivative (a function defined for numbers only), the Levin u-transform of the
   * partial sums.
   */
  private Apcomplex withLevinFallback(Apcomplex eulerMaclaurin, long lower) {
    if (eulerMaclaurin != null || !decaysFasterThanHarmonic(lower)) {
      return eulerMaclaurin;
    }
    return levin(lower);
  }

  /**
   * The Levin u-transform of the partial sums of the first terms: it accelerates the slow
   * convergence of a series of terms like <code>k^-s</code>, with half-integer powers in the
   * tail too, but it loses digits to cancellation. Two transforms of a different order have to
   * agree to the digits of the result, to 14 for a machine result.
   */
  private Apcomplex levin(long lower) {
    // a machine result shows all its digits: the transforms have to agree to nearly all of them
    final long need = digits <= 16L ? 14L : digits;
    final int order = (int) Math.min(80L, need + 14L);
    final long precision = 2L * work + order;
    final FixedPrecisionApcomplexHelper h = new FixedPrecisionApcomplexHelper(precision);
    Apcomplex[] terms = new Apcomplex[order + 1];
    for (int k = 0; k <= order; k++) {
      engine.checkTimeLimit();
      terms[k] = term(lower + k);
      if (terms[k] == null || (terms[k].real().signum() == 0 && terms[k].imag().signum() == 0)) {
        return null;
      }
    }
    Apcomplex first = levinU(terms, order, 1.0, h, precision);
    Apcomplex second = levinU(terms, order - 4, 1.0, h, precision);
    if (first == null || second == null) {
      return null;
    }
    Apfloat difference = ApcomplexMath.abs(h.subtract(first, second));
    Apfloat size = ApcomplexMath.abs(first);
    if (size.signum() == 0
        || (difference.signum() != 0 && difference.scale() >= size.scale() - need)) {
      return null;
    }
    return helper.valueOf(first);
  }

  /**
   * The Levin u-transform of order <code>k</code> of the partial sums of <code>terms</code>; the
   * remainder estimate of the term with index <code>j</code> is <code>(start + j) a(j)</code>, with
   * the index <code>start</code> of the first term.
   *
   * @return <code>null</code> if the transform has no value
   */
  static Apcomplex levinU(Apcomplex[] terms, int k, double start,
      FixedPrecisionApcomplexHelper h, long precision) {
    Apcomplex partial = Apcomplex.ZERO;
    Apcomplex numerator = Apcomplex.ZERO;
    Apcomplex denominator = Apcomplex.ZERO;
    Apfloat binomial = new Apfloat(1, precision);
    final Apfloat first = new Apfloat(start, precision);
    final Apfloat last = h.add(first, new Apfloat(k, precision)).real();
    for (int j = 0; j <= k; j++) {
      partial = h.add(partial, terms[j]);
      Apfloat beta = h.add(first, new Apfloat(j, precision)).real();
      // (-1)^j C(k, j) ((start + j)/(start + k))^(k - 1) / ((start + j) a_j)
      Apcomplex weight = h.divide(
          h.multiply(binomial, h.pow(h.divide(beta, last), (long) k - 1L)),
          h.multiply(beta, terms[j]));
      if ((j & 1) == 1) {
        weight = h.negate(weight);
      }
      numerator = h.add(numerator, h.multiply(weight, partial));
      denominator = h.add(denominator, weight);
      binomial = h.divide(h.multiply(binomial, new Apfloat(k - j, precision)),
          new Apfloat(j + 1, precision)).real();
    }
    if (denominator.real().signum() == 0 && denominator.imag().signum() == 0) {
      return null;
    }
    return h.divide(numerator, denominator);
  }

  /**
   * Test if the terms decrease like a power <code>k^-s</code> with <code>s</code> clearly above
   * <code>1</code>: from the ratio of the terms at <code>N</code> and <code>2 N</code>. A series
   * like <code>1/k</code> diverges, and one like <code>1/(k Log(k)^2)</code> converges too slowly
   * for the tail integral.
   */
  private boolean decaysFasterThanHarmonic(long lower) {
    long n = Math.max(lower, 1L) + 16L * PROBE;
    Apcomplex a = term(n);
    Apcomplex b = term(2L * n);
    if (a == null || b == null) {
      return false;
    }
    double sizeA = ApcomplexMath.abs(a).doubleValue();
    double sizeB = ApcomplexMath.abs(b).doubleValue();
    if (sizeB == 0.0) {
      return true;
    }
    if (!(sizeA > 0.0)) {
      return false;
    }
    // the exponent s of k^-s
    double s = Math.log(sizeA / sizeB) / Math.log(2.0);
    decay = s;
    return s > 1.05;
  }

  /** Test if the last correction is below the digits of the result. */
  private boolean negligibleCorrection(Apcomplex last, Apcomplex sum) {
    if (last.real().signum() == 0 && last.imag().signum() == 0) {
      return true;
    }
    if (sum.real().signum() == 0 && sum.imag().signum() == 0) {
      return false;
    }
    long scale = Math.max(sum.real().scale(), sum.imag().scale());
    long lastScale = Math.max(last.real().signum() == 0 ? Long.MIN_VALUE : last.real().scale(),
        last.imag().signum() == 0 ? Long.MIN_VALUE : last.imag().scale());
    return lastScale < scale - digits - 1;
  }
}
