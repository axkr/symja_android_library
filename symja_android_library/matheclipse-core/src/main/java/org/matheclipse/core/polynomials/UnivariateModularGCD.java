package org.matheclipse.core.polynomials;

import java.math.BigInteger;
import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;

/**
 * The greatest common divisor of two polynomials in one variable with integer coefficients, by the
 * modular algorithm: the Euclidean algorithm on dense arrays modulo primes below
 * <code>2^31</code>, the Chinese remainder theorem, and a trial division over the integers which
 * proves the result.
 *
 * <p>
 * A prime which doesn't divide both leading coefficients gives a degree at least that of the GCD, so
 * a constant GCD modulo one prime proves that the polynomials are coprime, and a smaller degree
 * discards the primes before it.
 *
 * <p>
 * A polynomial is an array of coefficients, the constant term first and the last one not zero.
 */
public final class UnivariateModularGCD {

  /** More primes than this are not tried. */
  private static final int MAX_PRIMES = 200000;

  private UnivariateModularGCD() {}

  /**
   * @param a a polynomial without a common factor of its coefficients
   * @param b a polynomial without a common factor of its coefficients
   * @return the GCD with a positive leading coefficient, or <code>null</code> if none was found
   *         with {@link #MAX_PRIMES} primes
   */
  public static BigInteger[] gcd(BigInteger[] a, BigInteger[] b, EvalEngine engine) {
    if (a.length == 1 || b.length == 1) {
      return new BigInteger[] {BigInteger.ONE};
    }
    final BigInteger leading = a[a.length - 1].gcd(b[b.length - 1]);
    int degree = Math.min(a.length, b.length);
    BigInteger[] lifted = null;
    BigInteger modulus = BigInteger.ONE;
    long p = (1L << 31) + 1L;
    for (int i = 0; i < MAX_PRIMES; i++) {
      engine.checkTimeLimit();
      p = previousPrime(p);
      BigInteger prime = BigInteger.valueOf(p);
      long scale = leading.mod(prime).longValue();
      if (scale == 0L) {
        continue;
      }
      long[] image = gcdModulo(reduce(a, prime), reduce(b, prime), p, engine);
      int imageDegree = image.length - 1;
      if (imageDegree == 0) {
        return new BigInteger[] {BigInteger.ONE};
      }
      if (imageDegree > degree) {
        continue;
      }
      if (imageDegree < degree) {
        degree = imageDegree;
        lifted = null;
      }
      if (lifted == null) {
        lifted = new BigInteger[image.length];
        Arrays.fill(lifted, BigInteger.ZERO);
        modulus = BigInteger.ONE;
      }
      boolean changed = false;
      BigInteger inverse = BigInteger.valueOf(inverse(modulus.mod(prime).longValue(), p));
      BigInteger half = modulus.multiply(prime).shiftRight(1);
      BigInteger nextModulus = modulus.multiply(prime);
      for (int k = 0; k < image.length; k++) {
        long value = image[k] * scale % p;
        BigInteger step = BigInteger.valueOf(value).subtract(lifted[k]).multiply(inverse).mod(prime);
        if (step.signum() != 0) {
          changed = true;
          BigInteger next = lifted[k].add(modulus.multiply(step));
          lifted[k] = next.compareTo(half) > 0 ? next.subtract(nextModulus) : next;
        }
      }
      modulus = nextModulus;
      if (!changed) {
        BigInteger[] candidate = primitive(lifted);
        if (divides(candidate, a, engine) && divides(candidate, b, engine)) {
          return candidate;
        }
      }
    }
    return null;
  }

  /**
   * The monic GCD modulo the prime <code>p</code> below <code>2^31</code>.
   *
   * @param a a polynomial, each coefficient in <code>[0, p)</code>
   * @param b a polynomial, each coefficient in <code>[0, p)</code>
   * @return the coefficients, the constant term first
   */
  public static long[] gcdModulo(long[] a, long[] b, long p, EvalEngine engine) {
    long[] u = a;
    int du = degree(u, u.length - 1);
    long[] v = b;
    int dv = degree(v, v.length - 1);
    if (du < dv) {
      long[] t = u;
      u = v;
      v = t;
      int d = du;
      du = dv;
      dv = d;
    }
    u = u.clone();
    v = v.clone();
    while (dv >= 0) {
      engine.checkTimeLimit();
      long inverse = inverse(v[dv], p);
      for (int i = du; i >= dv; i--) {
        long c = u[i];
        if (c == 0L) {
          continue;
        }
        long factor = p - c * inverse % p;
        int shift = i - dv;
        for (int j = 0; j <= dv; j++) {
          u[j + shift] = (u[j + shift] + factor * v[j]) % p;
        }
      }
      long[] t = u;
      u = v;
      v = t;
      du = dv;
      dv = degree(v, dv - 1);
    }
    if (du < 0) {
      return new long[] {0L};
    }
    long inverse = inverse(u[du], p);
    long[] monic = new long[du + 1];
    for (int i = 0; i <= du; i++) {
      monic[i] = u[i] * inverse % p;
    }
    return monic;
  }

  /** The largest index up to <code>from</code> of a coefficient which isn't 0, or -1. */
  private static int degree(long[] polynomial, int from) {
    int degree = from;
    while (degree >= 0 && polynomial[degree] == 0L) {
      degree--;
    }
    return degree;
  }

  private static long[] reduce(BigInteger[] polynomial, BigInteger prime) {
    long[] result = new long[polynomial.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = polynomial[i].mod(prime).longValue();
    }
    return result;
  }

  /** The polynomial without the common factor of its coefficients, its leading one positive. */
  private static BigInteger[] primitive(BigInteger[] polynomial) {
    BigInteger content = BigInteger.ZERO;
    for (BigInteger coefficient : polynomial) {
      content = content.gcd(coefficient);
      if (content.equals(BigInteger.ONE)) {
        break;
      }
    }
    if (polynomial[polynomial.length - 1].signum() < 0) {
      content = content.negate();
    }
    BigInteger[] result = new BigInteger[polynomial.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = polynomial[i].divide(content);
    }
    return result;
  }

  private static boolean divides(BigInteger[] divisor, BigInteger[] polynomial,
      EvalEngine engine) {
    return quotient(polynomial, divisor, engine) != null;
  }

  /**
   * The exact quotient over the integers.
   *
   * @return <code>null</code> if <code>divisor</code> doesn't divide <code>polynomial</code>
   */
  private static BigInteger[] quotient(BigInteger[] polynomial, BigInteger[] divisor,
      EvalEngine engine) {
    if (divisor.length > polynomial.length) {
      return null;
    }
    BigInteger[] rest = polynomial.clone();
    BigInteger leading = divisor[divisor.length - 1];
    int degree = divisor.length - 1;
    BigInteger[] result = new BigInteger[polynomial.length - degree];
    Arrays.fill(result, BigInteger.ZERO);
    for (int i = rest.length - 1; i >= degree; i--) {
      engine.checkTimeLimit();
      if (rest[i].signum() == 0) {
        continue;
      }
      BigInteger[] quotientRemainder = rest[i].divideAndRemainder(leading);
      if (quotientRemainder[1].signum() != 0) {
        return null;
      }
      int shift = i - degree;
      result[shift] = quotientRemainder[0];
      for (int j = 0; j <= degree; j++) {
        if (divisor[j].signum() != 0) {
          rest[j + shift] = rest[j + shift].subtract(quotientRemainder[0].multiply(divisor[j]));
        }
      }
    }
    for (int i = 0; i < degree; i++) {
      if (rest[i].signum() != 0) {
        return null;
      }
    }
    return result;
  }

  private static BigInteger[] product(BigInteger[] a, BigInteger[] b, EvalEngine engine) {
    BigInteger[] result = new BigInteger[a.length + b.length - 1];
    Arrays.fill(result, BigInteger.ZERO);
    for (int i = 0; i < a.length; i++) {
      engine.checkTimeLimit();
      if (a[i].signum() == 0) {
        continue;
      }
      for (int j = 0; j < b.length; j++) {
        if (b[j].signum() != 0) {
          result[i + j] = result[i + j].add(a[i].multiply(b[j]));
        }
      }
    }
    return result;
  }

  private static BigInteger[] dense(GenPolynomial<edu.jas.arith.BigInteger> polynomial) {
    BigInteger[] result = new BigInteger[(int) polynomial.degree(0) + 1];
    Arrays.fill(result, BigInteger.ZERO);
    for (Monomial<edu.jas.arith.BigInteger> term : polynomial) {
      result[(int) term.exponent().getVal(0)] = term.coefficient().getVal();
    }
    return result;
  }

  private static GenPolynomial<edu.jas.arith.BigInteger> sparse(BigInteger[] polynomial,
      GenPolynomialRing<edu.jas.arith.BigInteger> ring) {
    GenPolynomial<edu.jas.arith.BigInteger> result = ring.getZERO().copy();
    for (int i = 0; i < polynomial.length; i++) {
      if (polynomial[i].signum() != 0) {
        result.doPutToMap(ExpVector.create(1, 0, i), new edu.jas.arith.BigInteger(polynomial[i]));
      }
    }
    return result;
  }

  private static boolean isUnivariate(GenPolynomial<edu.jas.arith.BigInteger> a,
      GenPolynomial<edu.jas.arith.BigInteger> b) {
    return a.ring.nvar == 1 && !a.isZERO() && !b.isZERO();
  }

  /**
   * The GCD of two polynomials in one variable without a common factor of their coefficients.
   *
   * @return <code>null</code> if they are no such polynomials, or no GCD was found
   */
  public static GenPolynomial<edu.jas.arith.BigInteger> gcd(
      GenPolynomial<edu.jas.arith.BigInteger> a, GenPolynomial<edu.jas.arith.BigInteger> b,
      EvalEngine engine) {
    if (!isUnivariate(a, b)) {
      return null;
    }
    BigInteger[] gcd = gcd(dense(a), dense(b), engine);
    return gcd == null ? null : sparse(gcd, a.ring);
  }

  /**
   * The least common multiple of two polynomials in one variable: <code>a*(b/gcd)</code>.
   *
   * @param gcd the GCD of <code>a</code> and <code>b</code>
   */
  public static GenPolynomial<edu.jas.arith.BigInteger> lcm(
      GenPolynomial<edu.jas.arith.BigInteger> a, GenPolynomial<edu.jas.arith.BigInteger> b,
      GenPolynomial<edu.jas.arith.BigInteger> gcd, EvalEngine engine) {
    return sparse(product(dense(a), quotient(dense(b), dense(gcd), engine), engine), a.ring);
  }

  /** The largest odd prime below an odd number. */
  static long previousPrime(long candidate) {
    long p = candidate - 2L;
    while (!BigInteger.valueOf(p).isProbablePrime(30)) {
      p -= 2L;
    }
    return p;
  }

  /** The inverse of <code>a</code> modulo the prime <code>p</code>. */
  static long inverse(long a, long p) {
    long t = 0L;
    long newT = 1L;
    long r = p;
    long newR = a;
    while (newR != 0L) {
      long q = r / newR;
      long temp = t - q * newT;
      t = newT;
      newT = temp;
      temp = r - q * newR;
      r = newR;
      newR = temp;
    }
    return t < 0L ? t + p : t;
  }
}
