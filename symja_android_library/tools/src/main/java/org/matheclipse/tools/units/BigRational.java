package org.matheclipse.tools.units;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Immutable arbitrary-precision rational number. Decimal literals parse exactly (0.3048 ==
 * 381/1250), which is what keeps the generated unit database exact.
 */
public final class BigRational implements Comparable<BigRational> {
  public static final BigRational ZERO = new BigRational(BigInteger.ZERO, BigInteger.ONE);
  public static final BigRational ONE = new BigRational(BigInteger.ONE, BigInteger.ONE);

  private final BigInteger num;
  private final BigInteger den; // always > 0

  private BigRational(BigInteger num, BigInteger den) {
    this.num = num;
    this.den = den;
  }

  public static BigRational of(BigInteger n, BigInteger d) {
    if (d.signum() == 0) {
      throw new ArithmeticException("division by zero");
    }
    if (d.signum() < 0) {
      n = n.negate();
      d = d.negate();
    }
    BigInteger g = n.gcd(d);
    if (!g.equals(BigInteger.ONE)) {
      n = n.divide(g);
      d = d.divide(g);
    }
    return new BigRational(n, d);
  }

  public static BigRational of(long n) {
    return of(BigInteger.valueOf(n), BigInteger.ONE);
  }

  public static BigRational of(long n, long d) {
    return of(BigInteger.valueOf(n), BigInteger.valueOf(d));
  }

  /** Parses {@code "p"} or {@code "p/q"} (exact integers, optional leading sign). */
  public static BigRational parse(String s) {
    s = s.trim();
    int slash = s.indexOf('/');
    if (slash < 0) {
      return of(new BigInteger(s), BigInteger.ONE);
    }
    return of(new BigInteger(s.substring(0, slash).trim()),
        new BigInteger(s.substring(slash + 1).trim()));
  }

  /**
   * Parses an integer, decimal, or scientific literal exactly: {@code 123}, {@code 1.5},
   * {@code 1e-30}, {@code 6.62607015e-34}, {@code .5}, optional leading sign.
   */
  public static BigRational parseDecimal(String s) {
    s = s.trim();
    boolean negative = false;
    int i = 0;
    if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
      negative = s.charAt(i) == '-';
      i++;
    }
    int e = -1;
    for (int j = i; j < s.length(); j++) {
      char c = s.charAt(j);
      if (c == 'e' || c == 'E') {
        e = j;
        break;
      }
    }
    String mantissa = e < 0 ? s.substring(i) : s.substring(i, e);
    int exp10 = e < 0 ? 0 : Integer.parseInt(s.substring(e + 1).trim());
    int dot = mantissa.indexOf('.');
    String digits = dot < 0 ? mantissa : mantissa.substring(0, dot) + mantissa.substring(dot + 1);
    if (digits.isEmpty()) {
      throw new NumberFormatException("empty number: '" + s + "'");
    }
    int scale = dot < 0 ? 0 : mantissa.length() - dot - 1;
    BigInteger n = new BigInteger(digits);
    if (negative) {
      n = n.negate();
    }
    int netExp = exp10 - scale;
    if (netExp >= 0) {
      return of(n.multiply(BigInteger.TEN.pow(netExp)), BigInteger.ONE);
    }
    return of(n, BigInteger.TEN.pow(-netExp));
  }

  public BigRational add(BigRational o) {
    return of(num.multiply(o.den).add(o.num.multiply(den)), den.multiply(o.den));
  }

  public BigRational subtract(BigRational o) {
    return add(o.negate());
  }

  public BigRational multiply(BigRational o) {
    return of(num.multiply(o.num), den.multiply(o.den));
  }

  public BigRational divide(BigRational o) {
    return of(num.multiply(o.den), den.multiply(o.num));
  }

  public BigRational negate() {
    return new BigRational(num.negate(), den);
  }

  public BigRational pow(int e) {
    if (e == 0) {
      return ONE;
    }
    if (e > 0) {
      return of(num.pow(e), den.pow(e));
    }
    return of(den.pow(-e), num.pow(-e));
  }

  public boolean isInteger() {
    return den.equals(BigInteger.ONE);
  }

  public boolean isZero() {
    return num.signum() == 0;
  }

  public boolean isOne() {
    return num.equals(BigInteger.ONE) && den.equals(BigInteger.ONE);
  }

  public int signum() {
    return num.signum();
  }

  public BigInteger numerator() {
    return num;
  }

  public BigInteger denominator() {
    return den;
  }

  public int intValueExact() {
    if (!isInteger()) {
      throw new ArithmeticException("not an integer: " + this);
    }
    return num.intValueExact();
  }

  /** Exact n-th root of a non-negative BigInteger, or {@code null} if none exists. */
  public static BigInteger nthRoot(BigInteger v, int n) {
    if (v.signum() < 0 || n <= 0) {
      return null;
    }
    if (v.signum() == 0 || v.equals(BigInteger.ONE) || n == 1) {
      return v;
    }
    // Newton iteration on x -> ((n-1)x + v/x^(n-1))/n, starting above the root.
    BigInteger x = BigInteger.ONE.shiftLeft(v.bitLength() / n + 1);
    BigInteger nBig = BigInteger.valueOf(n);
    BigInteger nMinus1 = BigInteger.valueOf(n - 1L);
    while (true) {
      BigInteger next = x.multiply(nMinus1).add(v.divide(x.pow(n - 1))).divide(nBig);
      if (next.compareTo(x) >= 0) {
        break;
      }
      x = next;
    }
    return x.pow(n).equals(v) ? x : null;
  }

  /**
   * Computes {@code this^e} exactly as a rational if possible (e = p/q, exact q-th root of both
   * numerator and denominator), otherwise returns {@code null}. Requires {@code this > 0}.
   */
  public BigRational tryRootPow(BigRational e) {
    if (signum() <= 0) {
      return null;
    }
    if (e.isInteger()) {
      return pow(e.intValueExact());
    }
    int q = e.den.intValueExact();
    BigInteger rn = nthRoot(num, q);
    if (rn == null) {
      return null;
    }
    BigInteger rd = nthRoot(den, q);
    if (rd == null) {
      return null;
    }
    return of(rn, rd).pow(e.num.intValueExact());
  }

  @Override
  public int compareTo(BigRational o) {
    return num.multiply(o.den).compareTo(o.num.multiply(den));
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BigRational)) {
      return false;
    }
    BigRational r = (BigRational) o;
    return num.equals(r.num) && den.equals(r.den);
  }

  @Override
  public int hashCode() {
    return Objects.hash(num, den);
  }

  @Override
  public String toString() {
    return isInteger() ? num.toString() : num + "/" + den;
  }
}
