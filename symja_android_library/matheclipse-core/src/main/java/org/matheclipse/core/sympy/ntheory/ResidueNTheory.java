package org.matheclipse.core.sympy.ntheory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class ResidueNTheory {

  /**
   * Computes the modular inverse of a modulo m. Corresponds to PowerMod(a, -1, m).
   *
   * @param a the base integer
   * @param m the modulus
   * @return the modular inverse, or F.NIL if it does not exist
   */
  public static IExpr modInverse(IInteger a, IInteger m) {
    try {
      return a.modInverse(m);
    } catch (ArithmeticException e) {
      return F.NIL;
    }
  }

  /**
   * Find a root of x^2 = a mod p. Corresponds to PowerMod(a, 1/2, p). Uses the Tonelli-Shanks
   * algorithm for prime moduli.
   *
   * @param a the quadratic residue
   * @param p the prime modulus
   * @return the smallest square root, or F.NIL if none exists
   */
  public static IExpr sqrtMod(IInteger a, IInteger p) {
    BigInteger n = a.toBigNumerator();
    BigInteger pVal = p.toBigNumerator().abs();

    n = n.mod(pVal);
    if (n.equals(BigInteger.ZERO)) {
      return F.C0;
    }
    if (pVal.equals(BigInteger.TWO)) {
      return F.ZZ(n);
    }

    // Check Euler's criterion: (n/p) = 1
    BigInteger pMinusOne = pVal.subtract(BigInteger.ONE);
    if (!n.modPow(pMinusOne.divide(BigInteger.TWO), pVal).equals(BigInteger.ONE)) {
      return F.NIL; // No square root exists
    }

    BigInteger q = pMinusOne;
    int s = 0;
    while (q.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
      q = q.divide(BigInteger.TWO);
      s++;
    }

    if (s == 1) {
      BigInteger r = n.modPow(pVal.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), pVal);
      return F.ZZ(r);
    }

    // Find a quadratic non-residue z
    BigInteger z = BigInteger.TWO;
    while (z.modPow(pMinusOne.divide(BigInteger.TWO), pVal).equals(BigInteger.ONE)) {
      z = z.add(BigInteger.ONE);
    }

    BigInteger c = z.modPow(q, pVal);
    BigInteger r = n.modPow(q.add(BigInteger.ONE).divide(BigInteger.TWO), pVal);
    BigInteger t = n.modPow(q, pVal);
    BigInteger m = BigInteger.valueOf(s);

    while (!t.equals(BigInteger.ONE)) {
      BigInteger t2i = t;
      int i = 0;
      for (i = 1; i < m.intValue(); i++) {
        t2i = t2i.modPow(BigInteger.TWO, pVal);
        if (t2i.equals(BigInteger.ONE)) {
          break;
        }
      }

      BigInteger exponent = BigInteger.TWO.pow(m.intValue() - i - 1);
      BigInteger b = c.modPow(exponent, pVal);
      r = r.multiply(b).mod(pVal);
      c = b.multiply(b).mod(pVal);
      t = t.multiply(c).mod(pVal);
      m = BigInteger.valueOf(i);
    }

    // Return the smaller root
    BigInteger root2 = pVal.subtract(r);
    return r.compareTo(root2) <= 0 ? F.ZZ(r) : F.ZZ(root2);
  }

  /**
   * Find a root of x^n = a mod p. Corresponds to PowerMod(a, 1/n, p).
   *
   * @param a the nth-power residue
   * @param n the root to extract
   * @param p the modulus
   * @return the smallest root, or F.NIL if not computable
   */
  public static IExpr nthRootMod(IInteger a, IInteger n, IInteger p) {
    BigInteger aVal = a.toBigNumerator();
    BigInteger nVal = n.toBigNumerator();
    BigInteger pVal = p.toBigNumerator().abs();

    if (nVal.equals(BigInteger.ONE)) {
      return F.ZZ(aVal.mod(pVal));
    }
    if (nVal.equals(BigInteger.TWO)) {
      return sqrtMod(a, p);
    }

    BigInteger pMinusOne = pVal.subtract(BigInteger.ONE);

    // Fast path: gcd(n, p-1) == 1
    if (nVal.gcd(pMinusOne).equals(BigInteger.ONE)) {
      try {
        BigInteger u = nVal.modInverse(pMinusOne);
        return F.ZZ(aVal.modPow(u, pVal));
      } catch (ArithmeticException e) {
        return F.NIL;
      }
    }

    // General case reduction for nth roots modulo a prime
    BigInteger aModP = aVal.mod(pVal);
    if (aModP.equals(BigInteger.ZERO)) {
      return F.C0;
    }

    // Direct resolution if n completely divides p - 1
    if (pMinusOne.mod(nVal).equals(BigInteger.ZERO)) {
      try {
        return F.ZZ(nthrootMod1(aModP, nVal, pVal));
      } catch (Exception e) {
        return F.NIL; // Fallback to un-evaluated if log fails or unsupported size
      }
    } else {
      // Euclidean algorithm reduction logic for polynomials equivalent to _nthroot_mod_prime_power
      BigInteger pa = nVal;
      BigInteger pb = pMinusOne;
      BigInteger b = BigInteger.ONE;

      if (pa.compareTo(pb) < 0) {
        BigInteger temp = aModP;
        aModP = b;
        b = temp;

        temp = pa;
        pa = pb;
        pb = temp;
      }

      while (!pb.equals(BigInteger.ZERO)) {
        BigInteger[] divmod = pa.divideAndRemainder(pb);
        BigInteger q = divmod[0];
        BigInteger pc = divmod[1];

        // equivalent to: c = pow(b, -q, p) * a_mod_p % p
        BigInteger qMod = q.mod(pMinusOne);
        BigInteger minusQ = pMinusOne.subtract(qMod).mod(pMinusOne);
        BigInteger c = b.modPow(minusQ, pVal).multiply(aModP).mod(pVal);

        pa = pb;
        pb = pc;
        aModP = b;
        b = c;
      }

      if (pa.equals(BigInteger.ONE)) {
        return F.ZZ(aModP);
      } else if (pa.equals(BigInteger.TWO)) {
        return sqrtMod(F.ZZ(aModP), p);
      } else {
        try {
          return F.ZZ(nthrootMod1(aModP, pa, pVal));
        } catch (Exception e) {
          return F.NIL;
        }
      }
    }
  }

  /**
   * Root of x^q = s mod p, where p is prime and q divides p - 1.
   */
  private static BigInteger nthrootMod1(BigInteger s, BigInteger q, BigInteger p) {
    BigInteger g = primitiveRoot(p);
    BigInteger r = s;
    Map<BigInteger, Integer> factorsOfQ = factorInteger(q);
    BigInteger pMinus1 = p.subtract(BigInteger.ONE);

    for (Map.Entry<BigInteger, Integer> entry : factorsOfQ.entrySet()) {
      BigInteger qx = entry.getKey();
      int ex = entry.getValue();

      BigInteger qxPowEx = qx.pow(ex);
      BigInteger f = pMinus1.divide(qxPowEx);

      while (f.mod(qx).equals(BigInteger.ZERO)) {
        f = f.divide(qx);
      }

      BigInteger minusFModQx = f.negate().mod(qx);
      if (minusFModQx.compareTo(BigInteger.ZERO) < 0) {
        minusFModQx = minusFModQx.add(qx);
      }
      BigInteger inv = minusFModQx.modInverse(qx);
      BigInteger z = f.multiply(inv);
      BigInteger x = BigInteger.ONE.add(z).divide(qx);

      // t = discrete_log(p, r^f % p, g^(f*qx) % p)
      BigInteger t = discreteLog(p, r.modPow(f, p), g.modPow(f.multiply(qx), p));

      for (int i = 0; i < ex; i++) {
        BigInteger exp2 = z.multiply(t).negate().mod(pMinus1);
        if (exp2.compareTo(BigInteger.ZERO) < 0) {
          exp2 = exp2.add(pMinus1);
        }

        BigInteger part1 = r.modPow(x, p);
        BigInteger part2 = g.modPow(exp2, p);
        r = part1.multiply(part2).mod(p);

        t = t.divide(qx);
      }
    }

    // Isolate the minimum root
    BigInteger h = g.modPow(pMinus1.divide(q), p);
    BigInteger hx = r;
    BigInteger minRoot = r;

    for (long i = 0; i < q.longValueExact() - 1; i++) {
      hx = hx.multiply(h).mod(p);
      if (hx.compareTo(minRoot) < 0) {
        minRoot = hx;
      }
    }
    return minRoot;
  }

  /**
   * Generates the smallest primitive root for a prime p.
   */
  private static BigInteger primitiveRoot(BigInteger p) {
    if (p.equals(BigInteger.TWO)) {
      return BigInteger.ONE;
    }
    BigInteger pMinus1 = p.subtract(BigInteger.ONE);
    Map<BigInteger, Integer> factors = factorInteger(pMinus1);

    BigInteger g = BigInteger.TWO;
    while (g.compareTo(p) < 0) {
      boolean isPrimitive = true;
      for (BigInteger q : factors.keySet()) {
        if (g.modPow(pMinus1.divide(q), p).equals(BigInteger.ONE)) {
          isPrimitive = false;
          break;
        }
      }
      if (isPrimitive) {
        return g;
      }
      g = g.add(BigInteger.ONE);
    }
    return BigInteger.TWO;
  }

  /**
   * Baby-step giant-step algorithm for computing discrete log of a to base b modulo p.
   */
  private static BigInteger discreteLog(BigInteger p, BigInteger a, BigInteger b) {
    // Basic limit to prevent exhausting heap space on extremely large moduli
    if (p.compareTo(BigInteger.valueOf(1000000000L)) > 0) {
      throw new UnsupportedOperationException("Discrete log for large p not currently supported.");
    }
    long pLong = p.longValue();
    long m = (long) Math.ceil(Math.sqrt(pLong));

    Map<BigInteger, Long> table = new HashMap<>();
    BigInteger cur = BigInteger.ONE;
    for (long i = 0; i < m; i++) {
      table.putIfAbsent(cur, i);
      cur = cur.multiply(b).mod(p);
    }

    BigInteger z = b.modInverse(p).modPow(BigInteger.valueOf(m), p);
    cur = a;
    for (long i = 0; i < m; i++) {
      if (table.containsKey(cur)) {
        return BigInteger.valueOf(i * m + table.get(cur));
      }
      cur = cur.multiply(z).mod(p);
    }
    throw new IllegalArgumentException("Log does not exist");
  }

  /**
   * Prime factorization mapping for intermediate bounds.
   */
  private static Map<BigInteger, Integer> factorInteger(BigInteger n) {
    Map<BigInteger, Integer> factors = new HashMap<>();
    BigInteger d = BigInteger.TWO;
    while (n.compareTo(BigInteger.ONE) > 0 && d.multiply(d).compareTo(n) <= 0) {
      int count = 0;
      while (n.mod(d).equals(BigInteger.ZERO)) {
        count++;
        n = n.divide(d);
      }
      if (count > 0) {
        factors.put(d, count);
      }
      d = d.add(BigInteger.ONE);
    }
    if (n.compareTo(BigInteger.ONE) > 0) {
      factors.put(n, 1);
    }
    return factors;
  }
}
