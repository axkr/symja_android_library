package org.matheclipse.core.reduce;

import java.math.BigInteger;

/**
 * Exact integer primitives shared by the integer decision procedures of this package.
 *
 * <p>
 * Every method works on {@link BigInteger}: an integer solver which converts a mathematical
 * coefficient through <code>int</code> or <code>long</code> silently answers a different question
 * than the one it was asked, so no method here accepts a machine sized integer.
 */
public final class IntegerMath {

  private IntegerMath() {}

  /** Greatest common divisor, always non negative. */
  public static BigInteger gcd(BigInteger a, BigInteger b) {
    return a.gcd(b);
  }

  /** Least common multiple, always non negative; <code>0</code> if an argument is zero. */
  public static BigInteger lcm(BigInteger a, BigInteger b) {
    if (a.signum() == 0 || b.signum() == 0) {
      return BigInteger.ZERO;
    }
    return a.divide(a.gcd(b)).multiply(b).abs();
  }

  /**
   * Extended Euclidean algorithm.
   *
   * @return <code>{g, s, t}</code> with <code>g == gcd(|a|, |b|) &gt;= 0</code> and
   *         <code>s*a + t*b == g</code>
   */
  public static BigInteger[] extendedGcd(BigInteger a, BigInteger b) {
    BigInteger oldR = a;
    BigInteger r = b;
    BigInteger oldS = BigInteger.ONE;
    BigInteger s = BigInteger.ZERO;
    BigInteger oldT = BigInteger.ZERO;
    BigInteger t = BigInteger.ONE;
    while (r.signum() != 0) {
      BigInteger quotient = oldR.divide(r);
      BigInteger tempR = r;
      r = oldR.subtract(quotient.multiply(r));
      oldR = tempR;
      BigInteger tempS = s;
      s = oldS.subtract(quotient.multiply(s));
      oldS = tempS;
      BigInteger tempT = t;
      t = oldT.subtract(quotient.multiply(t));
      oldT = tempT;
    }
    if (oldR.signum() < 0) {
      return new BigInteger[] {oldR.negate(), oldS.negate(), oldT.negate()};
    }
    return new BigInteger[] {oldR, oldS, oldT};
  }

  /** Largest integer <code>q</code> with <code>q*denominator &lt;= numerator</code>. */
  public static BigInteger floorDiv(BigInteger numerator, BigInteger denominator) {
    BigInteger quotient = numerator.divide(denominator);
    BigInteger remainder = numerator.subtract(quotient.multiply(denominator));
    if (remainder.signum() != 0 && remainder.signum() != denominator.signum()) {
      return quotient.subtract(BigInteger.ONE);
    }
    return quotient;
  }

  /** Smallest integer <code>q</code> with <code>q*denominator &gt;= numerator</code>. */
  public static BigInteger ceilDiv(BigInteger numerator, BigInteger denominator) {
    return floorDiv(numerator.negate(), denominator).negate();
  }

  /** The representative of <code>value</code> in <code>[0, |modulus|)</code>. */
  public static BigInteger euclideanMod(BigInteger value, BigInteger modulus) {
    BigInteger residue = value.remainder(modulus);
    return residue.signum() < 0 ? residue.add(modulus.abs()) : residue;
  }

  /**
   * Solve the linear congruence <code>a*x &equiv; b (mod m)</code>.
   *
   * @param modulus a positive modulus
   * @return <code>{residue, reducedModulus}</code> describing the solution set
   *         <code>x &equiv; residue (mod reducedModulus)</code> with
   *         <code>0 &lt;= residue &lt; reducedModulus</code>, or <code>null</code> if the congruence
   *         has no solution
   */
  public static BigInteger[] solveLinearCongruence(BigInteger a, BigInteger b, BigInteger modulus) {
    if (modulus.signum() <= 0) {
      return null;
    }
    BigInteger divisor = a.gcd(modulus);
    if (divisor.signum() == 0) {
      // 0*x == b : every x solves it iff m divides b
      return euclideanMod(b, modulus).signum() == 0
          ? new BigInteger[] {BigInteger.ZERO, BigInteger.ONE}
          : null;
    }
    if (euclideanMod(b, divisor).signum() != 0) {
      return null;
    }
    BigInteger reducedModulus = modulus.divide(divisor);
    if (reducedModulus.equals(BigInteger.ONE)) {
      return new BigInteger[] {BigInteger.ZERO, BigInteger.ONE};
    }
    BigInteger reducedA = euclideanMod(a.divide(divisor), reducedModulus);
    BigInteger reducedB = b.divide(divisor);
    BigInteger inverse = extendedGcd(reducedA, reducedModulus)[1];
    return new BigInteger[] {euclideanMod(reducedB.multiply(inverse), reducedModulus),
        reducedModulus};
  }

  /**
   * Chinese remainder theorem for two, possibly non coprime, moduli.
   *
   * @return <code>{residue, modulus}</code> of the combined congruence, or <code>null</code> if the
   *         two congruences contradict each other
   */
  public static BigInteger[] crtPair(BigInteger residue1, BigInteger modulus1, BigInteger residue2,
      BigInteger modulus2) {
    BigInteger[] extended = extendedGcd(modulus1, modulus2);
    BigInteger divisor = extended[0];
    BigInteger difference = residue2.subtract(residue1);
    if (euclideanMod(difference, divisor).signum() != 0) {
      return null;
    }
    BigInteger combined = lcm(modulus1, modulus2);
    BigInteger step = difference.divide(divisor).multiply(extended[1]);
    BigInteger residue = residue1.add(modulus1.multiply(step));
    return new BigInteger[] {euclideanMod(residue, combined), combined};
  }

  /** Test whether <code>value</code> is a perfect square. */
  public static boolean isSquare(BigInteger value) {
    if (value.signum() < 0) {
      return false;
    }
    BigInteger root = value.sqrt();
    return root.multiply(root).equals(value);
  }
}
