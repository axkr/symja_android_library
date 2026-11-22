package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

/**
 * The real roots of a sparse polynomial: a few terms of a high degree, such as
 * <code>x^10000 - 3*x^7 + 1</code>. Between two real roots of its derivative a polynomial is
 * monotone and has at most one root there; the derivative of <code>x^-e1*p'(x)</code> has one
 * term less, so that the recursion over the terms finds every positive root by bisection in a few
 * steps, whatever the degree. The negative roots are the positive ones of <code>p(-x)</code>.
 */
public final class SparseRealRoots {

  /** The polynomials with more terms are left to the dense methods. */
  public static final int MAX_TERMS = 16;

  /** A root is located to this many digits by default. */
  public static final long DIGITS = 40;

  /** The working precision of the bisection has this many digits more than the roots. */
  private static final long GUARD_DIGITS = 20;

  /** The terms <code>c*x^e</code> of a polynomial with integer coefficients. */
  public static final class Terms {
    /** The exponents in ascending order. */
    final long[] exponents;
    final BigInteger[] coefficients;

    Terms(long[] exponents, BigInteger[] coefficients) {
      this.exponents = exponents;
      this.coefficients = coefficients;
    }

    public long degree() {
      return exponents[exponents.length - 1];
    }

    public int size() {
      return exponents.length;
    }

    /** The greatest common divisor of the exponents without the factor <code>x^e0</code>. */
    public long exponentGcd() {
      BigInteger g = BigInteger.ZERO;
      for (long e : exponents) {
        g = g.gcd(BigInteger.valueOf(e - exponents[0]));
      }
      return g.longValue();
    }

    /**
     * The degree in <code>y = x^g</code> of the polynomial without the factor <code>x^e0</code>,
     * where <code>g</code> is {@link #exponentGcd()}.
     */
    public long degreeInPower() {
      long g = exponentGcd();
      return g == 0 ? 0 : (degree() - exponents[0]) / g;
    }

    /** The multiplicity of the root 0. */
    public long zeroMultiplicity() {
      return exponents[0];
    }

    /**
     * <code>Function(c0*#1^e0 + c1*#1^e1 + ...)</code>, without the factor <code>#1^e0</code> if
     * <code>withoutZero</code>.
     */
    public IAST function(boolean withoutZero, EvalEngine engine) {
      IASTAppendable plus = F.PlusAlloc(exponents.length);
      for (int i = 0; i < exponents.length; i++) {
        long e = withoutZero ? exponents[i] - exponents[0] : exponents[i];
        plus.append(e == 0 ? F.ZZ(coefficients[i])
            : F.Times(F.ZZ(coefficients[i]), F.Power(F.Slot1, F.ZZ(e))));
      }
      return F.Function(engine.evaluate(plus));
    }

    /** Whether the rational number is a root, by exact arithmetic. */
    public boolean isRoot(IRational value) {
      BigInteger n = value.toBigNumerator();
      BigInteger d = value.toBigDenominator();
      if (n.signum() == 0) {
        return exponents[0] > 0;
      }
      if (n.bitLength() * degree() > 4_000_000L || d.bitLength() * degree() > 4_000_000L) {
        return false;
      }
      // d^degree * p(n/d)
      final long degree = degree();
      BigInteger sum = BigInteger.ZERO;
      for (int i = 0; i < exponents.length; i++) {
        sum = sum.add(coefficients[i].multiply(n.pow((int) exponents[i]))
            .multiply(d.pow((int) (degree - exponents[i]))));
      }
      return sum.signum() == 0;
    }

    private Terms reflected() {
      BigInteger[] c = coefficients.clone();
      for (int i = 0; i < c.length; i++) {
        if ((exponents[i] & 1L) == 1L) {
          c[i] = c[i].negate();
        }
      }
      return new Terms(exponents, c);
    }
  }

  private SparseRealRoots() {}

  /**
   * The terms of the polynomial <code>body</code> in <code>#1</code>, with integer coefficients
   * without a common factor and a positive leading coefficient.
   *
   * @return <code>null</code> if it isn't a polynomial with rational coefficients of at most
   *         {@link #MAX_TERMS} terms
   */
  public static Terms of(IExpr body) {
    try {
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, F.list(F.Slot1));
      ExprPolynomial polynomial = ring.create(body, false, true, false);
      if (polynomial.length() > MAX_TERMS || polynomial.degree(0) < 1) {
        return null;
      }
      TreeMap<Long, IRational> map = new TreeMap<>();
      BigInteger lcm = BigInteger.ONE;
      for (ExprMonomial monomial : polynomial) {
        IExpr c = monomial.coefficient();
        if (!c.isRational()) {
          return null;
        }
        IRational r = (IRational) c;
        if (!r.isZero()) {
          map.put(monomial.exponent().getVal(0), r);
          BigInteger d = r.toBigDenominator();
          lcm = lcm.divide(lcm.gcd(d)).multiply(d);
        }
      }
      if (map.isEmpty()) {
        return null;
      }
      long[] e = new long[map.size()];
      BigInteger[] c = new BigInteger[map.size()];
      int i = 0;
      BigInteger gcd = BigInteger.ZERO;
      for (Map.Entry<Long, IRational> entry : map.entrySet()) {
        IRational r = entry.getValue();
        e[i] = entry.getKey();
        c[i] = r.toBigNumerator().multiply(lcm.divide(r.toBigDenominator()));
        gcd = gcd.gcd(c[i]);
        i++;
      }
      boolean negate = c[c.length - 1].signum() < 0;
      for (int j = 0; j < c.length; j++) {
        c[j] = c[j].divide(gcd);
        if (negate) {
          c[j] = c[j].negate();
        }
      }
      return new Terms(e, c);
    } catch (RuntimeException rex) {
      return null;
    }
  }

  /**
   * The distinct nonzero real roots in ascending order, to {@link #DIGITS} digits.
   *
   * @return <code>null</code> if a root seems to be multiple, which the bisection can't tell
   */
  public static List<Apfloat> nonzeroRealRoots(Terms terms) {
    return nonzeroRealRoots(terms, DIGITS);
  }

  /**
   * The distinct nonzero real roots in ascending order, located to <code>digits</code> digits.
   *
   * @return <code>null</code> if a root seems to be multiple, which the bisection can't tell
   */
  public static List<Apfloat> nonzeroRealRoots(Terms terms, long digits) {
    EvalEngine engine = EvalEngine.get();
    final long precision = Math.max(digits, DIGITS) + GUARD_DIGITS;
    List<Apfloat> negative = positiveRoots(terms.reflected(), precision, engine);
    List<Apfloat> positive = positiveRoots(terms, precision, engine);
    if (negative == null || positive == null) {
      return null;
    }
    List<Apfloat> roots = new ArrayList<>(negative.size() + positive.size());
    for (int i = negative.size() - 1; i >= 0; i--) {
      roots.add(negative.get(i).negate());
    }
    roots.addAll(positive);
    return roots;
  }

  /** The distinct positive roots in ascending order, or <code>null</code>. */
  private static List<Apfloat> positiveRoots(Terms terms, long precision,
      EvalEngine engine) {
    final int m = terms.size();
    // without the factor x^e0, which is positive for x > 0
    long[] e = new long[m];
    for (int i = 0; i < m; i++) {
      e[i] = terms.exponents[i] - terms.exponents[0];
    }
    BigInteger[] c = terms.coefficients;
    List<Apfloat> roots = new ArrayList<>();
    if (m == 1) {
      return roots;
    }
    if (m == 2) {
      // c0 + c1*x^e1 == 0
      if (c[0].signum() == c[1].signum()) {
        return roots;
      }
      Apfloat ratio = new Apfloat(c[0].abs(), precision).divide(new Apfloat(c[1].abs(), precision));
      roots.add(ApfloatMath.pow(ratio, new Apfloat(1, precision).divide(new Apfloat(e[1]))));
      return roots;
    }
    // the critical points: the roots of x^(1-e1)*p'(x), which has one term less
    long[] de = new long[m - 1];
    BigInteger[] dc = new BigInteger[m - 1];
    for (int i = 1; i < m; i++) {
      de[i - 1] = e[i] - e[1];
      dc[i - 1] = c[i].multiply(BigInteger.valueOf(e[i]));
    }
    List<Apfloat> critical = positiveRoots(new Terms(de, dc), precision, engine);
    if (critical == null) {
      return null;
    }
    Apfloat bound = cauchyBound(c, precision);
    List<Apfloat> points = new ArrayList<>(critical.size() + 2);
    points.add(Apfloat.ZERO);
    for (Apfloat point : critical) {
      if (point.compareTo(bound) < 0) {
        points.add(point);
      }
    }
    points.add(bound);
    int previousSign = c[0].signum();
    for (int i = 1; i < points.size(); i++) {
      engine.checkTimeLimit();
      Apfloat b = points.get(i);
      int sign = sign(e, c, b, i < points.size() - 1, precision);
      if (sign == 2) {
        // a value at a critical point which is zero within the working precision: a multiple root
        return null;
      }
      if (previousSign * sign < 0) {
        roots.add(bisect(e, c, points.get(i - 1), b, previousSign, precision));
      }
      previousSign = sign;
    }
    return roots;
  }

  /** <code>1 + max|c_i/c_m|</code>, above every root. */
  private static Apfloat cauchyBound(BigInteger[] c, long precision) {
    Apfloat lead = new Apfloat(c[c.length - 1].abs(), precision);
    Apfloat max = Apfloat.ZERO;
    for (int i = 0; i < c.length - 1; i++) {
      Apfloat q = new Apfloat(c[i].abs(), precision).divide(lead);
      if (q.compareTo(max) > 0) {
        max = q;
      }
    }
    return max.add(new Apfloat(1, precision));
  }

  /**
   * The sign of the polynomial at <code>x &gt; 0</code>, or <code>2</code> at a critical point
   * where it vanishes within the working precision.
   */
  private static int sign(long[] e, BigInteger[] c, Apfloat x, boolean critical,
      long precision) {
    Apfloat sum = Apfloat.ZERO;
    Apfloat scale = Apfloat.ZERO;
    for (int i = 0; i < e.length; i++) {
      Apfloat term = new Apfloat(c[i], precision).multiply(ApfloatMath.pow(x, e[i]));
      sum = sum.add(term);
      scale = scale.add(ApfloatMath.abs(term));
    }
    if (critical && ApfloatMath.abs(sum)
        .compareTo(scale.multiply(ApfloatMath.pow(new Apfloat(10, precision), -(precision - 15))))
        <= 0) {
      return 2;
    }
    return sum.signum();
  }

  private static Apfloat bisect(long[] e, BigInteger[] c, Apfloat a, Apfloat b, int signA,
      long precision) {
    Apfloat two = new Apfloat(2, precision);
    Apfloat tolerance = ApfloatMath.pow(new Apfloat(10, precision), -(precision - GUARD_DIGITS));
    for (int i = 0; i < 4 * precision; i++) {
      Apfloat mid = a.add(b).divide(two);
      if (b.subtract(a).compareTo(tolerance.multiply(ApfloatMath.abs(mid))) <= 0) {
        return mid;
      }
      int s = sign(e, c, mid, false, precision);
      if (s == 0) {
        return mid;
      }
      if (s == signA) {
        a = mid;
      } else {
        b = mid;
      }
    }
    return a.add(b).divide(two);
  }
}
