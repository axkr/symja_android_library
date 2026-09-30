package org.matheclipse.core.polynomials;

import edu.jas.arith.MachinePrime;
import java.math.BigInteger;
import java.util.Map;
import org.matheclipse.core.interfaces.IRational;
import edu.jas.arith.BigRational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;

/**
 * Images of polynomials over a number field in <code>GF(p)[t]</code>, used by
 * {@link AlgebraicCoefficientGCD} to prove coprimality cheaply. All arithmetic is on
 * <code>long</code> values below a prime of about 15 bits, so products never overflow.
 */
final class ModularImage {

  /** The first prime tried. Small enough to find roots by search, large enough to be lucky. */
  private static final long FIRST_PRIME = 32003;

  /** Primes searched for a simple root before giving up. */
  private static final int MAX_PRIMES_SEARCHED = 64;

  private ModularImage() {}

  /**
   * Up to <code>count</code> pairs <code>{p, r}</code>: a prime <code>p</code> and a simple root
   * <code>r</code> of the monic polynomial with the given ascending coefficients modulo
   * <code>p</code>.
   */
  static long[][] primeRoots(IRational[] minimalPolynomial, int count) {
    int n = minimalPolynomial.length - 1;
    java.util.List<long[]> result = new java.util.ArrayList<long[]>();
    long p = FIRST_PRIME;
    for (int searched = 0; searched < MAX_PRIMES_SEARCHED && result.size() < count; searched++) {
      while (!MachinePrime.isPrime(p)) {
        p += 2;
      }
      long[] m = new long[n + 1];
      boolean ok = true;
      for (int i = 0; i <= n && ok; i++) {
        long c = reduce(minimalPolynomial[i].toBigNumerator(),
            minimalPolynomial[i].toBigDenominator(), p);
        ok = c >= 0;
        m[i] = c;
      }
      if (ok) {
        for (long r = 0; r < p; r++) {
          if (evaluate(m, r, p) == 0 && evaluate(derivative(m, p), r, p) != 0) {
            result.add(new long[] {p, r});
            break;
          }
        }
      }
      p += 2;
    }
    return result.toArray(new long[result.size()][]);
  }

  /**
   * The image of <code>q</code> in <code>GF(p)[t]</code> under generator -> <code>r</code> and
   * <code>x_i -> a_i + t*b_i</code>, or <code>null</code> if a denominator is divisible by
   * <code>p</code>.
   */
  static long[] onLine(GenPolynomial<AlgebraicNumber<BigRational>> q, long[] primeRoot, long[] a,
      long[] b, int fieldDegree) {
    long p = primeRoot[0];
    long[] rPowers = new long[fieldDegree];
    rPowers[0] = 1;
    for (int i = 1; i < fieldDegree; i++) {
      rPowers[i] = rPowers[i - 1] * primeRoot[1] % p;
    }
    int nvar = q.ring.nvar;
    long[] result = new long[(int) q.totalDegree() + 1];
    for (Map.Entry<ExpVector, AlgebraicNumber<BigRational>> term : q.getMap().entrySet()) {
      long c = 0;
      GenPolynomial<BigRational> val = term.getValue().val;
      for (Map.Entry<ExpVector, BigRational> coefficient : val.getMap().entrySet()) {
        long v = reduce(coefficient.getValue().num, coefficient.getValue().den, p);
        if (v < 0) {
          return null;
        }
        c = (c + v * rPowers[(int) coefficient.getKey().getVal(0)]) % p;
      }
      long[] product = {c};
      ExpVector e = term.getKey();
      for (int i = 0; i < nvar; i++) {
        // ExpVector stores the variables in reverse order; any fixed assignment is as good
        long exponent = e.getVal(i);
        for (long k = 0; k < exponent; k++) {
          product = multiplyLinear(product, a[i], b[i], p);
        }
      }
      for (int k = 0; k < product.length; k++) {
        result[k] = (result[k] + product[k]) % p;
      }
    }
    return result;
  }

  /** The degree of <code>f</code>; <code>-1</code> for zero. */
  static int degree(long[] f) {
    for (int i = f.length - 1; i >= 0; i--) {
      if (f[i] != 0) {
        return i;
      }
    }
    return -1;
  }

  /** The GCD of two polynomials over <code>GF(p)</code> (not normalised). */
  static long[] gcd(long[] f, long[] g, long p) {
    f = trim(f);
    g = trim(g);
    while (degree(g) >= 0) {
      long[] r = remainder(f, g, p);
      f = g;
      g = r;
    }
    return f;
  }

  private static long[] remainder(long[] f, long[] g, long p) {
    long[] r = f.clone();
    int dg = degree(g);
    long inverse = BigInteger.valueOf(g[dg]).modInverse(BigInteger.valueOf(p)).longValue();
    for (int d = degree(r); d >= dg; d = degree(r)) {
      long factor = r[d] * inverse % p;
      for (int i = 0; i <= dg; i++) {
        r[d - dg + i] = ((r[d - dg + i] - factor * g[i]) % p + p) % p;
      }
    }
    return trim(r);
  }

  private static long[] trim(long[] f) {
    return java.util.Arrays.copyOf(f, Math.max(degree(f) + 1, 0));
  }

  /** <code>f * (a + b*t)</code> */
  private static long[] multiplyLinear(long[] f, long a, long b, long p) {
    long[] result = new long[f.length + 1];
    for (int i = 0; i < f.length; i++) {
      result[i] = (result[i] + f[i] * a) % p;
      result[i + 1] = (result[i + 1] + f[i] * b) % p;
    }
    return result;
  }

  private static long evaluate(long[] f, long x, long p) {
    long v = 0;
    for (int i = f.length - 1; i >= 0; i--) {
      v = (v * x + f[i]) % p;
    }
    return v;
  }

  private static long[] derivative(long[] f, long p) {
    long[] result = new long[Math.max(f.length - 1, 1)];
    for (int i = 1; i < f.length; i++) {
      result[i - 1] = f[i] * i % p;
    }
    return result;
  }

  /** <code>num/den mod p</code>, or <code>-1</code> if <code>p</code> divides the denominator. */
  private static long reduce(BigInteger num, BigInteger den, long p) {
    BigInteger bp = BigInteger.valueOf(p);
    BigInteger d = den.mod(bp);
    if (d.signum() == 0) {
      return -1;
    }
    return num.mod(bp).multiply(d.modInverse(bp)).mod(bp).longValue();
  }
}
