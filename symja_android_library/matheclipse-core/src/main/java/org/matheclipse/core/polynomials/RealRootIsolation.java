package org.matheclipse.core.polynomials;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exact isolation of the real roots of a square-free polynomial with integer coefficients.
 *
 * <p>
 * The roots are separated with Descartes' rule of signs and bisection (the method of Collins and
 * Akritas): the number of sign changes in the coefficients of <code>(x+1)^n * p(1/(x+1))</code> is
 * an upper bound for the number of roots of <code>p</code> in <code>(0, 1)</code>, and it is exact
 * when it is 0 or 1. An interval with more sign changes is halved. All arithmetic is in
 * {@link BigInteger}, so the number of real roots is exact - unlike a numeric root finder, which
 * has to decide with a tolerance whether a small imaginary part is zero.
 */
public final class RealRootIsolation {

  /** How often an interval is halved before a polynomial is given up as not square-free. */
  private static final int MAX_DEPTH = 2048;

  private RealRootIsolation() {}

  /**
   * An interval <code>[lower, upper]</code> with the end points
   * <code>numerator / 2^exponent</code>, which contains exactly one root. It is the root itself if
   * the end points are equal; otherwise the root lies strictly inside and no end point is a root.
   */
  public static final class RootInterval {
    final BigInteger lower;
    final BigInteger upper;
    /** The end points are <code>lower / 2^exponent</code> and <code>upper / 2^exponent</code>. */
    final int exponent;

    RootInterval(BigInteger lower, BigInteger upper, int exponent) {
      this.lower = lower;
      this.upper = upper;
      this.exponent = exponent;
    }

    public boolean isExact() {
      return lower.equals(upper);
    }

    /** The numerator of the lower end point over {@link #denominator()}. */
    public BigInteger lowerNumerator() {
      return exponent >= 0 ? lower : lower.shiftLeft(-exponent);
    }

    /** The numerator of the upper end point over {@link #denominator()}. */
    public BigInteger upperNumerator() {
      return exponent >= 0 ? upper : upper.shiftLeft(-exponent);
    }

    /** The common denominator of the two end points, a power of 2. */
    public BigInteger denominator() {
      return exponent >= 0 ? BigInteger.ONE.shiftLeft(exponent) : BigInteger.ONE;
    }

    /** The middle of the interval as a <code>double</code>. */
    public double doubleValue() {
      BigDecimal sum = new BigDecimal(lowerNumerator().add(upperNumerator()));
      BigDecimal twice = new BigDecimal(denominator().shiftLeft(1));
      return sum.divide(twice, MathContext.DECIMAL64).doubleValue();
    }

    @Override
    public String toString() {
      return "[" + lowerNumerator() + "/" + denominator() + ", " + upperNumerator() + "/"
          + denominator() + "]";
    }
  }

  /**
   * The isolating intervals of the real roots of a square-free polynomial, in ascending order.
   *
   * @param coefficients the coefficient of <code>x^i</code> at index <code>i</code>; the leading
   *        coefficient is not zero
   * @return <code>null</code> if the polynomial is zero, or if the bisection does not end - which
   *         happens for a polynomial with a multiple root
   */
  public static List<RootInterval> isolate(BigInteger[] coefficients) {
    int degree = coefficients.length - 1;
    while (degree >= 0 && coefficients[degree].signum() == 0) {
      degree--;
    }
    if (degree < 0) {
      return null;
    }
    int low = 0;
    while (coefficients[low].signum() == 0) {
      low++;
    }
    BigInteger[] p = new BigInteger[degree - low + 1];
    System.arraycopy(coefficients, low, p, 0, p.length);

    List<RootInterval> result = new ArrayList<RootInterval>();
    // the negative roots are the positive roots of p(-x)
    BigInteger[] mirrored = p.clone();
    for (int i = 1; i < mirrored.length; i += 2) {
      mirrored[i] = mirrored[i].negate();
    }
    List<RootInterval> negative = positiveRoots(mirrored);
    if (negative == null) {
      return null;
    }
    Collections.reverse(negative);
    for (RootInterval interval : negative) {
      result.add(new RootInterval(interval.upper.negate(), interval.lower.negate(),
          interval.exponent));
    }
    if (low > 0) {
      result.add(new RootInterval(BigInteger.ZERO, BigInteger.ZERO, 0));
    }
    List<RootInterval> positive = positiveRoots(p);
    if (positive == null) {
      return null;
    }
    result.addAll(positive);

    // no end point of an interval is a root, so that a sign change marks the root inside
    BigInteger[] all = new BigInteger[degree + 1];
    System.arraycopy(coefficients, 0, all, 0, degree + 1);
    for (int i = 0; i < result.size(); i++) {
      RootInterval interval = result.get(i);
      while (!interval.isExact() && (signAt(all, interval.lower, interval.exponent) == 0
          || signAt(all, interval.upper, interval.exponent) == 0)) {
        interval = halve(all, interval);
      }
      result.set(i, interval);
    }
    return result;
  }

  /**
   * The isolating intervals of the positive roots of <code>p</code>, which has a constant term
   * different from zero.
   */
  private static List<RootInterval> positiveRoots(BigInteger[] p) {
    List<RootInterval> result = new ArrayList<RootInterval>();
    final int n = p.length - 1;
    if (n < 1 || signChanges(p) == 0) {
      return result;
    }
    // all roots are smaller than 2^k: Cauchy's bound 1 + Max(|a_i| / |a_n|)
    BigInteger max = BigInteger.ZERO;
    for (int i = 0; i < n; i++) {
      max = max.max(p[i].abs());
    }
    int k = max.divide(p[n].abs()).add(BigInteger.TWO).bitLength();
    // q(x) = p(2^k * x) has its positive roots in (0, 1)
    BigInteger[] q = new BigInteger[n + 1];
    for (int i = 0; i <= n; i++) {
      q[i] = p[i].shiftLeft(k * i);
    }
    if (!bisect(q, BigInteger.ZERO, 0, k, result, 0)) {
      return null;
    }
    return result;
  }

  /**
   * Isolate the roots of <code>q</code> in <code>(0, 1)</code>, which stands for the interval
   * <code>(c / 2^depth, (c + 1) / 2^depth) * 2^k</code> of the polynomial it was derived from.
   *
   * @return <code>false</code> if the bisection goes deeper than a square-free polynomial needs
   */
  private static boolean bisect(BigInteger[] q, BigInteger c, int depth, int k,
      List<RootInterval> result, int level) {
    final int n = q.length - 1;
    // (x+1)^n * q(1/(x+1)): the coefficients in reverse order, shifted by 1
    BigInteger[] reversed = new BigInteger[n + 1];
    for (int i = 0; i <= n; i++) {
      reversed[i] = q[n - i];
    }
    int changes = signChanges(taylorShift(reversed));
    if (changes == 0) {
      return true;
    }
    if (changes == 1) {
      result.add(new RootInterval(c, c.add(BigInteger.ONE), depth - k));
      return true;
    }
    if (level > MAX_DEPTH) {
      return false;
    }
    // left half: 2^n * q(x/2); right half: the left half shifted by 1
    BigInteger[] left = new BigInteger[n + 1];
    for (int i = 0; i <= n; i++) {
      left[i] = q[i].shiftLeft(n - i);
    }
    BigInteger twice = c.shiftLeft(1);
    if (!bisect(left, twice, depth + 1, k, result, level + 1)) {
      return false;
    }
    BigInteger[] right = taylorShift(left);
    BigInteger middle = twice.add(BigInteger.ONE);
    if (right[0].signum() == 0) {
      // the middle of the interval is a root
      result.add(new RootInterval(middle, middle, depth + 1 - k));
      BigInteger[] deflated = new BigInteger[n];
      System.arraycopy(right, 1, deflated, 0, n);
      right = deflated;
    }
    return bisect(right, middle, depth + 1, k, result, level + 1);
  }

  /** The coefficients of <code>p(x + 1)</code>. */
  private static BigInteger[] taylorShift(BigInteger[] p) {
    BigInteger[] shifted = p.clone();
    final int n = shifted.length - 1;
    for (int i = 0; i < n; i++) {
      for (int j = n - 1; j >= i; j--) {
        shifted[j] = shifted[j].add(shifted[j + 1]);
      }
    }
    return shifted;
  }

  private static int signChanges(BigInteger[] p) {
    int changes = 0;
    int last = 0;
    for (BigInteger coefficient : p) {
      int sign = coefficient.signum();
      if (sign != 0) {
        if (last != 0 && sign != last) {
          changes++;
        }
        last = sign;
      }
    }
    return changes;
  }

  /** The sign of <code>p(numerator / 2^exponent)</code>. */
  private static int signAt(BigInteger[] p, BigInteger numerator, int exponent) {
    final int n = p.length - 1;
    if (exponent <= 0) {
      BigInteger x = numerator.shiftLeft(-exponent);
      BigInteger value = p[n];
      for (int i = n - 1; i >= 0; i--) {
        value = value.multiply(x).add(p[i]);
      }
      return value.signum();
    }
    // 2^(exponent*n) * p(numerator / 2^exponent) by Horner's rule
    BigInteger value = p[n];
    for (int i = n - 1; i >= 0; i--) {
      value = value.multiply(numerator).add(p[i].shiftLeft(exponent * (n - i)));
    }
    return value.signum();
  }

  /** The sign of <code>p</code> just right of the lower end (or just left of the upper end). */
  private static int signInside(BigInteger[] p, BigInteger numerator, int exponent,
      boolean lowerEnd) {
    int sign = signAt(p, numerator, exponent);
    if (sign != 0) {
      return sign;
    }
    // at a root of the square-free p the derivative decides
    BigInteger[] derivative = new BigInteger[Math.max(1, p.length - 1)];
    derivative[0] = BigInteger.ZERO;
    for (int i = 1; i < p.length; i++) {
      derivative[i - 1] = p[i].multiply(BigInteger.valueOf(i));
    }
    int slope = signAt(derivative, numerator, exponent);
    return lowerEnd ? slope : -slope;
  }

  /** The half of the interval which contains its root. */
  private static RootInterval halve(BigInteger[] p, RootInterval interval) {
    if (interval.isExact()) {
      return interval;
    }
    BigInteger lower = interval.lower.shiftLeft(1);
    BigInteger upper = interval.upper.shiftLeft(1);
    int exponent = interval.exponent + 1;
    BigInteger middle = lower.add(upper).shiftRight(1);
    int sign = signAt(p, middle, exponent);
    if (sign == 0) {
      return new RootInterval(middle, middle, exponent);
    }
    if (sign == signInside(p, lower, exponent, true)) {
      return new RootInterval(middle, upper, exponent);
    }
    return new RootInterval(lower, middle, exponent);
  }

  /**
   * Narrow an isolating interval of {@link #isolate(BigInteger[])} until the root is known as a
   * <code>double</code>: its width is below <code>2^-60</code> of its distance from zero.
   *
   * @param coefficients the coefficients the interval was isolated for
   */
  public static RootInterval refine(BigInteger[] coefficients, RootInterval interval) {
    int degree = coefficients.length - 1;
    while (degree > 0 && coefficients[degree].signum() == 0) {
      degree--;
    }
    BigInteger[] p = new BigInteger[degree + 1];
    System.arraycopy(coefficients, 0, p, 0, degree + 1);
    RootInterval current = interval;
    for (int i = 0; i < 4 * MAX_DEPTH && !current.isExact(); i++) {
      BigInteger width = current.upper.subtract(current.lower);
      BigInteger nearest = current.lower.abs().min(current.upper.abs());
      if (nearest.signum() != 0 && width.shiftLeft(60).compareTo(nearest) <= 0) {
        break;
      }
      current = halve(p, current);
    }
    return current;
  }

  /**
   * The real roots of a square-free polynomial as <code>double</code> values in ascending order.
   *
   * @return <code>null</code> if {@link #isolate(BigInteger[])} gives none
   */
  public static double[] realRoots(BigInteger[] coefficients) {
    List<RootInterval> intervals = isolate(coefficients);
    if (intervals == null) {
      return null;
    }
    double[] roots = new double[intervals.size()];
    for (int i = 0; i < roots.length; i++) {
      roots[i] = refine(coefficients, intervals.get(i)).doubleValue();
    }
    return roots;
  }
}
