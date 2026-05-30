package org.matheclipse.core.sympy.ntheory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.numbertheory.Primality;

/**
 * Modular arithmetic primitives backing {@code PowerMod}.
 *
 * <p>
 * Square roots and n-th roots are computed for general (composite) moduli by factoring the modulus
 * with {@link Primality#factorInteger(BigInteger)}, solving each prime-power component via
 * Tonelli-Shanks / discrete-log + Hensel lifting, and recombining the results with the Chinese
 * Remainder Theorem. All sign combinations across components are enumerated so that the smallest
 * non-negative root is returned.
 */
public class ResidueNTheory {

  private static final BigInteger TWO = BigInteger.valueOf(2);
  private static final BigInteger FOUR = BigInteger.valueOf(4);
  private static final BigInteger EIGHT = BigInteger.valueOf(8);

  /** Upper bound on CRT combinations enumerated when selecting the smallest root. */
  private static final long MAX_ROOTS = 1L << 20;

  /**
   * Computes the modular inverse of {@code a} modulo {@code m}. Corresponds to
   * {@code PowerMod(a, -1, m)}.
   *
   * @return the modular inverse, or {@link F#NIL} if it does not exist
   */
  public static IExpr modInverse(IInteger a, IInteger m) {
    try {
      return a.modInverse(m);
    } catch (ArithmeticException e) {
      return F.NIL;
    }
  }

  /**
   * Find a root of <code>x^2 == a (mod m)</code>. Corresponds to <code>PowerMod(a, 1/2, m)</code>.
   *
   * <p>
   * Supports general composite moduli: {@code m} is factored, the equation is solved modulo each
   * prime-power component (Tonelli-Shanks + Hensel lifting), and the per-component roots are
   * recombined via CRT. All sign combinations are enumerated and the smallest non-negative
   * representative is returned.
   *
   * @return the smallest square root, or {@link F#NIL} if none exists or the enumeration limit is
   *         exceeded
   */
  public static IExpr sqrtMod(IInteger a, IInteger m) {
    BigInteger mVal = m.toBigNumerator().abs();
    if (mVal.signum() == 0) {
      return F.NIL;
    }
    if (mVal.equals(BigInteger.ONE)) {
      return F.C0;
    }
    BigInteger aVal = a.toBigNumerator().mod(mVal);

    SortedMap<BigInteger, Integer> factors = new Primality().factorInteger(mVal);
    List<BigInteger> moduli = new ArrayList<>(factors.size());
    List<List<BigInteger>> rootsPerComponent = new ArrayList<>(factors.size());
    long totalCombinations = 1L;
    for (Map.Entry<BigInteger, Integer> e : factors.entrySet()) {
      BigInteger p = e.getKey();
      int k = e.getValue();
      BigInteger pk = p.pow(k);
      List<BigInteger> roots = sqrtModPrimePower(aVal.mod(pk), p, k);
      if (roots.isEmpty()) {
        return F.NIL;
      }
      moduli.add(pk);
      rootsPerComponent.add(roots);
      totalCombinations *= roots.size();
      if (totalCombinations > MAX_ROOTS || totalCombinations < 0) {
        return F.NIL;
      }
    }
    BigInteger best = smallestCrt(rootsPerComponent, moduli);
    return best == null ? F.NIL : F.ZZ(best);
  }

  /**
   * Find a root of <code>x^n == a (mod m)</code>. Corresponds to <code>PowerMod(a, 1/n, m)</code>.
   *
   * <p>
   * Supports composite moduli when {@code gcd(n, p) == 1} for every prime divisor {@code p} of
   * {@code m} (the common case). When that condition fails for some prime-power component the
   * algorithm falls back to the prime-only single-root path; if even that fails {@link F#NIL} is
   * returned.
   */
  public static IExpr nthRootMod(IInteger a, IInteger n, IInteger m) {
    BigInteger nVal = n.toBigNumerator();
    BigInteger mVal = m.toBigNumerator().abs();
    if (mVal.signum() == 0) {
      return F.NIL;
    }
    if (mVal.equals(BigInteger.ONE)) {
      return F.C0;
    }
    if (nVal.equals(BigInteger.ONE)) {
      return F.ZZ(a.toBigNumerator().mod(mVal));
    }
    if (nVal.equals(TWO)) {
      return sqrtMod(a, m);
    }
    BigInteger aVal = a.toBigNumerator().mod(mVal);

    SortedMap<BigInteger, Integer> factors = new Primality().factorInteger(mVal);

    // Fast path: m is itself prime.
    if (factors.size() == 1 && factors.values().iterator().next() == 1) {
      return nthRootModPrime(aVal, nVal, mVal);
    }

    List<BigInteger> moduli = new ArrayList<>(factors.size());
    List<List<BigInteger>> rootsPerComponent = new ArrayList<>(factors.size());
    long totalCombinations = 1L;
    for (Map.Entry<BigInteger, Integer> e : factors.entrySet()) {
      BigInteger p = e.getKey();
      int k = e.getValue();
      BigInteger pk = p.pow(k);
      List<BigInteger> roots = nthRootModPrimePower(aVal.mod(pk), nVal, p, k);
      if (roots.isEmpty()) {
        return F.NIL;
      }
      moduli.add(pk);
      rootsPerComponent.add(roots);
      totalCombinations *= roots.size();
      if (totalCombinations > MAX_ROOTS || totalCombinations < 0) {
        return F.NIL;
      }
    }
    BigInteger best = smallestCrt(rootsPerComponent, moduli);
    return best == null ? F.NIL : F.ZZ(best);
  }

  // ---------------------------------------------------------------------------------------------
  // Square roots
  // ---------------------------------------------------------------------------------------------

  /** Tonelli-Shanks square root modulo prime {@code p}. {@code null} when no root exists. */
  private static BigInteger sqrtModPrimeSingle(BigInteger n, BigInteger p) {
    n = n.mod(p);
    if (n.signum() == 0) {
      return BigInteger.ZERO;
    }
    if (p.equals(TWO)) {
      return n;
    }
    BigInteger pMinusOne = p.subtract(BigInteger.ONE);
    if (!n.modPow(pMinusOne.divide(TWO), p).equals(BigInteger.ONE)) {
      return null;
    }
    BigInteger q = pMinusOne;
    int s = 0;
    while (q.mod(TWO).signum() == 0) {
      q = q.divide(TWO);
      s++;
    }
    if (s == 1) {
      return n.modPow(p.add(BigInteger.ONE).divide(FOUR), p);
    }
    BigInteger z = TWO;
    while (z.modPow(pMinusOne.divide(TWO), p).equals(BigInteger.ONE)) {
      z = z.add(BigInteger.ONE);
    }
    BigInteger c = z.modPow(q, p);
    BigInteger r = n.modPow(q.add(BigInteger.ONE).divide(TWO), p);
    BigInteger t = n.modPow(q, p);
    int mm = s;
    while (!t.equals(BigInteger.ONE)) {
      BigInteger t2i = t;
      int i = 1;
      for (; i < mm; i++) {
        t2i = t2i.modPow(TWO, p);
        if (t2i.equals(BigInteger.ONE)) {
          break;
        }
      }
      BigInteger b = c.modPow(TWO.pow(mm - i - 1), p);
      r = r.multiply(b).mod(p);
      c = b.multiply(b).mod(p);
      t = t.multiply(c).mod(p);
      mm = i;
    }
    return r;
  }

  /** All distinct square roots of {@code a} modulo {@code p^k}. Empty when no root exists. */
  private static List<BigInteger> sqrtModPrimePower(BigInteger a, BigInteger p, int k) {
    BigInteger pk = p.pow(k);
    a = a.mod(pk);
    List<BigInteger> result = new ArrayList<>();

    if (a.signum() == 0) {
      // x^2 == 0 (mod p^k) iff x == 0 (mod p^ceil(k/2)).
      BigInteger step = p.pow((k + 1) / 2);
      for (BigInteger x = BigInteger.ZERO; x.compareTo(pk) < 0; x = x.add(step)) {
        result.add(x);
      }
      return result;
    }

    if (p.equals(TWO)) {
      return sqrtModPow2(a, k);
    }

    // p odd.
    if (a.mod(p).signum() == 0) {
      // a = p^t * a' with gcd(a', p) = 1: a root exists iff t is even.
      int t = 0;
      BigInteger aReduced = a;
      while (aReduced.mod(p).signum() == 0) {
        aReduced = aReduced.divide(p);
        t++;
      }
      if (t >= k) {
        BigInteger step = p.pow((k + 1) / 2);
        for (BigInteger x = BigInteger.ZERO; x.compareTo(pk) < 0; x = x.add(step)) {
          result.add(x);
        }
        return result;
      }
      if ((t & 1) != 0) {
        return result;
      }
      List<BigInteger> ySolutions = sqrtModPrimePower(aReduced, p, k - t);
      if (ySolutions.isEmpty()) {
        return result;
      }
      BigInteger phalf = p.pow(t / 2);
      BigInteger step = p.pow(k - t / 2);
      BigInteger fanout = p.pow(t / 2);
      TreeSet<BigInteger> uniq = new TreeSet<>();
      for (BigInteger y : ySolutions) {
        BigInteger base = phalf.multiply(y).mod(pk);
        BigInteger x = base;
        for (BigInteger j = BigInteger.ZERO; j.compareTo(fanout) < 0; j = j.add(BigInteger.ONE)) {
          uniq.add(x.mod(pk));
          x = x.add(step);
        }
      }
      result.addAll(uniq);
      return result;
    }

    // gcd(a, p) = 1: Tonelli-Shanks then Hensel lift.
    BigInteger r = sqrtModPrimeSingle(a.mod(p), p);
    if (r == null) {
      return result;
    }
    BigInteger px = p;
    for (int j = 2; j <= k; j++) {
      BigInteger pj = px.multiply(p);
      BigInteger num = r.multiply(r).subtract(a).mod(pj);
      BigInteger inv2r = r.add(r).modInverse(pj);
      r = r.subtract(num.multiply(inv2r)).mod(pj);
      if (r.signum() < 0) {
        r = r.add(pj);
      }
      px = pj;
    }
    BigInteger r2 = pk.subtract(r);
    result.add(r);
    if (!r.equals(r2)) {
      result.add(r2);
    }
    return result;
  }

  /** All square roots of odd {@code a} modulo {@code 2^k}. */
  private static List<BigInteger> sqrtModPow2(BigInteger a, int k) {
    List<BigInteger> result = new ArrayList<>();
    BigInteger pk = TWO.pow(k);
    if (k == 1) {
      result.add(a.mod(TWO));
      return result;
    }
    if (k == 2) {
      if (a.mod(FOUR).equals(BigInteger.ONE)) {
        result.add(BigInteger.ONE);
        result.add(BigInteger.valueOf(3));
      }
      return result;
    }
    if (!a.mod(EIGHT).equals(BigInteger.ONE)) {
      return result;
    }
    BigInteger r = BigInteger.ONE;
    for (int j = 4; j <= k; j++) {
      BigInteger pj = TWO.pow(j);
      BigInteger aModPj = a.mod(pj);
      BigInteger cand1 = r;
      BigInteger cand2 = r.add(TWO.pow(j - 2));
      r = cand1.multiply(cand1).mod(pj).equals(aModPj) ? cand1 : cand2;
    }
    TreeSet<BigInteger> uniq = new TreeSet<>();
    BigInteger half = TWO.pow(k - 1);
    uniq.add(r);
    uniq.add(pk.subtract(r).mod(pk));
    uniq.add(half.add(r).mod(pk));
    BigInteger fourth = half.subtract(r).mod(pk);
    if (fourth.signum() < 0) {
      fourth = fourth.add(pk);
    }
    uniq.add(fourth);
    result.addAll(uniq);
    return result;
  }

  /** Smallest root of <code>x^n == a (mod p)</code> with {@code p} prime, or {@link F#NIL}. */
  private static IExpr nthRootModPrime(BigInteger aVal, BigInteger nVal, BigInteger pVal) {
    if (nVal.equals(BigInteger.ONE)) {
      return F.ZZ(aVal.mod(pVal));
    }
    if (nVal.equals(TWO)) {
      List<BigInteger> roots = sqrtModPrimePower(aVal.mod(pVal), pVal, 1);
      return roots.isEmpty() ? F.NIL : F.ZZ(min(roots));
    }
    BigInteger pMinusOne = pVal.subtract(BigInteger.ONE);
    if (nVal.gcd(pMinusOne).equals(BigInteger.ONE)) {
      try {
        BigInteger u = nVal.modInverse(pMinusOne);
        return F.ZZ(aVal.modPow(u, pVal));
      } catch (ArithmeticException e) {
        return F.NIL;
      }
    }
    BigInteger aModP = aVal.mod(pVal);
    if (aModP.signum() == 0) {
      return F.C0;
    }
    if (pMinusOne.mod(nVal).signum() == 0) {
      try {
        return F.ZZ(nthrootMod1(aModP, nVal, pVal));
      } catch (Exception e) {
        return F.NIL;
      }
    }
    // Euclidean reduction (SymPy _nthroot_mod_prime_power for k = 1).
    BigInteger pa = nVal;
    BigInteger pb = pMinusOne;
    BigInteger b = BigInteger.ONE;
    if (pa.compareTo(pb) < 0) {
      BigInteger tmp = aModP;
      aModP = b;
      b = tmp;
      tmp = pa;
      pa = pb;
      pb = tmp;
    }
    while (pb.signum() != 0) {
      BigInteger[] qr = pa.divideAndRemainder(pb);
      BigInteger q = qr[0];
      BigInteger pc = qr[1];
      BigInteger minusQ = pMinusOne.subtract(q.mod(pMinusOne)).mod(pMinusOne);
      BigInteger c = b.modPow(minusQ, pVal).multiply(aModP).mod(pVal);
      pa = pb;
      pb = pc;
      aModP = b;
      b = c;
    }
    if (pa.equals(BigInteger.ONE)) {
      return F.ZZ(aModP);
    }
    if (pa.equals(TWO)) {
      List<BigInteger> roots = sqrtModPrimePower(aModP.mod(pVal), pVal, 1);
      return roots.isEmpty() ? F.NIL : F.ZZ(min(roots));
    }
    try {
      return F.ZZ(nthrootMod1(aModP, pa, pVal));
    } catch (Exception e) {
      return F.NIL;
    }
  }

  /**
   * All distinct n-th roots of {@code a} modulo {@code p^k}. Empty when no root exists or the
   * computation cannot be performed.
   */
  private static List<BigInteger> nthRootModPrimePower(BigInteger a, BigInteger n, BigInteger p,
      int k) {
    BigInteger pk = p.pow(k);
    a = a.mod(pk);
    List<BigInteger> result = new ArrayList<>();

    if (n.equals(TWO)) {
      return sqrtModPrimePower(a, p, k);
    }

    if (a.signum() == 0) {
      result.add(BigInteger.ZERO);
      return result;
    }

    // gcd(n, p) > 1: fall back to single-root prime path; only succeeds when k == 1.
    if (n.mod(p).signum() == 0) {
      if (k != 1) {
        return result;
      }
      IExpr single = nthRootModPrime(a, n, p);
      if (single.isPresent() && single.isInteger()) {
        result.add(((IInteger) single).toBigNumerator());
      }
      return result;
    }

    // gcd(n, p) == 1: enumerate roots mod p, then Hensel-lift each.
    List<BigInteger> rootsModP = allNthRootsModPrime(a.mod(p), n, p);
    if (rootsModP.isEmpty()) {
      return result;
    }
    if (k == 1) {
      TreeSet<BigInteger> uniq = new TreeSet<>(rootsModP);
      result.addAll(uniq);
      return result;
    }
    TreeSet<BigInteger> uniq = new TreeSet<>();
    for (BigInteger r0 : rootsModP) {
      BigInteger r = r0;
      BigInteger px = p;
      for (int j = 2; j <= k; j++) {
        BigInteger pj = px.multiply(p);
        BigInteger num = r.modPow(n, pj).subtract(a).mod(pj);
        BigInteger deriv = n.multiply(r.modPow(n.subtract(BigInteger.ONE), pj)).mod(pj);
        try {
          BigInteger invDeriv = deriv.modInverse(pj);
          r = r.subtract(num.multiply(invDeriv)).mod(pj);
          if (r.signum() < 0) {
            r = r.add(pj);
          }
        } catch (ArithmeticException e) {
          return new ArrayList<>();
        }
        px = pj;
      }
      uniq.add(r);
    }
    result.addAll(uniq);
    return result;
  }

  /** Enumerate every n-th root of {@code a} modulo prime {@code p}. */
  private static List<BigInteger> allNthRootsModPrime(BigInteger a, BigInteger n, BigInteger p) {
    List<BigInteger> result = new ArrayList<>();
    BigInteger aMod = a.mod(p);
    if (aMod.signum() == 0) {
      result.add(BigInteger.ZERO);
      return result;
    }
    IExpr oneRoot = nthRootModPrime(aMod, n, p);
    if (!oneRoot.isPresent() || !oneRoot.isInteger()) {
      return result;
    }
    BigInteger r0 = ((IInteger) oneRoot).toBigNumerator().mod(p);
    BigInteger pMinusOne = p.subtract(BigInteger.ONE);
    BigInteger d = n.gcd(pMinusOne);
    if (d.equals(BigInteger.ONE)) {
      result.add(r0);
      return result;
    }
    BigInteger g = primitiveRoot(p);
    BigInteger h = g.modPow(pMinusOne.divide(d), p);
    TreeSet<BigInteger> uniq = new TreeSet<>();
    BigInteger cur = r0;
    long dLong = d.longValueExact();
    for (long i = 0; i < dLong; i++) {
      uniq.add(cur);
      cur = cur.multiply(h).mod(p);
    }
    result.addAll(uniq);
    return result;
  }

  /** Root of <code>x^q == s (mod p)</code> with {@code p} prime and {@code q | p-1}. */
  private static BigInteger nthrootMod1(BigInteger s, BigInteger q, BigInteger p) {
    BigInteger g = primitiveRoot(p);
    BigInteger r = s;
    Map<BigInteger, Integer> factorsOfQ = factorIntegerMap(q);
    BigInteger pMinus1 = p.subtract(BigInteger.ONE);

    for (Map.Entry<BigInteger, Integer> entry : factorsOfQ.entrySet()) {
      BigInteger qx = entry.getKey();
      int ex = entry.getValue();

      BigInteger qxPowEx = qx.pow(ex);
      BigInteger f = pMinus1.divide(qxPowEx);

      while (f.mod(qx).signum() == 0) {
        f = f.divide(qx);
      }

      BigInteger minusFModQx = f.negate().mod(qx);
      if (minusFModQx.signum() < 0) {
        minusFModQx = minusFModQx.add(qx);
      }
      BigInteger inv = minusFModQx.modInverse(qx);
      BigInteger z = f.multiply(inv);
      BigInteger x = BigInteger.ONE.add(z).divide(qx);

      BigInteger t = discreteLog(p, r.modPow(f, p), g.modPow(f.multiply(qx), p));

      for (int i = 0; i < ex; i++) {
        BigInteger exp2 = z.multiply(t).negate().mod(pMinus1);
        if (exp2.signum() < 0) {
          exp2 = exp2.add(pMinus1);
        }
        BigInteger part1 = r.modPow(x, p);
        BigInteger part2 = g.modPow(exp2, p);
        r = part1.multiply(part2).mod(p);
        t = t.divide(qx);
      }
    }

    BigInteger h = g.modPow(pMinus1.divide(q), p);
    BigInteger hx = r;
    BigInteger minRoot = r;
    long qLong = q.longValueExact();
    for (long i = 0; i < qLong - 1; i++) {
      hx = hx.multiply(h).mod(p);
      if (hx.compareTo(minRoot) < 0) {
        minRoot = hx;
      }
    }
    return minRoot;
  }

  /** Smallest primitive root for a prime {@code p}. */
  private static BigInteger primitiveRoot(BigInteger p) {
    if (p.equals(TWO)) {
      return BigInteger.ONE;
    }
    BigInteger pMinus1 = p.subtract(BigInteger.ONE);
    Map<BigInteger, Integer> factors = factorIntegerMap(pMinus1);
    BigInteger g = TWO;
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
    return TWO;
  }

  /** Baby-step giant-step discrete logarithm of {@code a} to base {@code b} modulo {@code p}. */
  private static BigInteger discreteLog(BigInteger p, BigInteger a, BigInteger b) {
    if (p.compareTo(BigInteger.valueOf(1000000000L)) > 0) {
      throw new UnsupportedOperationException("Discrete log for large p not currently supported.");
    }
    long pLong = p.longValueExact();
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

  /** Adapter to {@link Primality#factorInteger(BigInteger)} returning a plain {@code Map}. */
  private static Map<BigInteger, Integer> factorIntegerMap(BigInteger n) {
    return new HashMap<>(new Primality().factorInteger(n));
  }

  /** Minimum of a non-empty list of {@code BigInteger}s. */
  private static BigInteger min(List<BigInteger> values) {
    BigInteger m = values.get(0);
    for (BigInteger v : values) {
      if (v.compareTo(m) < 0) {
        m = v;
      }
    }
    return m;
  }

  /**
   * Enumerate every combination of one residue per component, CRT-combine and return the smallest
   * non-negative representative.
   */
  private static BigInteger smallestCrt(List<List<BigInteger>> rootsPerComponent,
      List<BigInteger> moduli) {
    if (rootsPerComponent.isEmpty()) {
      return null;
    }
    BigInteger M = BigInteger.ONE;
    for (BigInteger mi : moduli) {
      M = M.multiply(mi);
    }
    int t = moduli.size();
    BigInteger[] coeffs = new BigInteger[t];
    for (int i = 0; i < t; i++) {
      BigInteger Mi = M.divide(moduli.get(i));
      coeffs[i] = Mi.multiply(Mi.modInverse(moduli.get(i))).mod(M);
    }
    int[] idx = new int[t];
    BigInteger best = null;
    while (true) {
      BigInteger acc = BigInteger.ZERO;
      for (int i = 0; i < t; i++) {
        acc = acc.add(rootsPerComponent.get(i).get(idx[i]).multiply(coeffs[i]));
      }
      BigInteger r = acc.mod(M);
      if (best == null || r.compareTo(best) < 0) {
        best = r;
      }
      int i = 0;
      while (i < t) {
        idx[i]++;
        if (idx[i] < rootsPerComponent.get(i).size()) {
          break;
        }
        idx[i] = 0;
        i++;
      }
      if (i == t) {
        break;
      }
    }
    return best;
  }
}