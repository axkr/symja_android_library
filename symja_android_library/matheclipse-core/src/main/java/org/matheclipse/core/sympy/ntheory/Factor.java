package org.matheclipse.core.sympy.ntheory;

import edu.jas.arith.MachinePrime;
import java.math.BigInteger;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.sympy.exception.ValueError;

public class Factor {
  public static IPair perfectPower(IReal n) {
    return perfectPower(n, F.NIL, true, true);
  }

  public static IPair perfectPower(IReal n, IAST candidates) {
    return perfectPower(n, candidates, true, true);
  }

  public static IPair perfectPower(IReal n, IAST candidates, boolean big, boolean factor) {
    // https://github.com/sympy/sympy/blob/0c4cc831ab88b6ba39685540f74d9551544d06e5/sympy/ntheory/factor_.py#L400

    // Return ``(b, e)`` such that ``n`` == ``b**e`` if ``n`` is a unique
    // perfect power with ``e > 1``, else ``False`` (e.g. 1 is not a
    // perfect power). A ValueError is raised if ``n`` is not Rational.
    // By default, the base is recursively decomposed and the exponents
    // collected so the largest possible ``e`` is sought. If ``big=False``
    // then the smallest possible ``e`` (thus prime) will be chosen.
    // If ``factor=True`` then simultaneous factorization of ``n`` is
    // attempted since finding a factor indicates the only possible root
    // for ``n``. This is True by default since only a few small factors will
    // be tested in the course of searching for the perfect power.
    // The use of ``candidates`` is primarily for internal use; if provided,
    // False will be returned if ``n`` cannot be written as a power with one
    // of the candidates as an exponent and factoring (beyond testing for
    // a factor of 2) will not be attempted.
    // Examples
    // ========
    // >>> from sympy import perfect_power, Rational
    // >>> perfect_power(16)
    // (2, 4)
    // >>> perfect_power(16, big=False)
    // (4, 2)
    // Negative numbers can only have odd perfect powers:
    // >>> perfect_power(-4)
    // False
    // >>> perfect_power(-8)
    // (-2, 3)
    // Rationals are also recognized:
    // >>> perfect_power(Rational(1, 2)**3)
    // (1/2, 3)
    // >>> perfect_power(Rational(-3, 2)**3)
    // (-3/2, 3)
    // Notes
    // =====
    // To know whether an integer is a perfect power of 2 use
    // >>> is2pow = lambda n: bool(n and not n & (n - 1))
    // >>> [(i, is2pow(i)) for i in range(5)]
    // [(0, False), (1, True), (2, True), (3, False), (4, True)]
    // It is not necessary to provide ``candidates``. When provided
    // it will be assumed that they are ints. The first one that is
    // larger than the computed maximum possible exponent will signal
    // failure for the routine.
    // >>> perfect_power(3**8, [9])
    // False
    // >>> perfect_power(3**8, [2, 4, 8])
    // (3, 8)
    // >>> perfect_power(3**8, [4, 8], big=False)
    // (9, 4)
    // negative handling
    if (n.isNegative()) {
      IReal minusN = (IReal) n.negate();
      if (candidates.isNIL()) {
        IPair pp = perfectPower(minusN, F.NIL, true, factor);
        if (pp.isNIL()) {
          return F.NIL;
        }
        IExpr b = pp.first();
        long e = pp.second().toLongDefault();
        // e2 = e & (-e)
        long e2 = e & (-e);
        b = F.eval(F.Power(b, F.ZZ(e2)));
        e = e / e2;
        if (e <= 1) {
          return F.NIL;
        }
        if (big || MachinePrime.isPrime(e)) {
          return F.pair(b.negate(), F.ZZ(e));
        }
        for (long p = 3; p <= e; p += 2) {
          if (e % p == 0 && MachinePrime.isPrime(p)) {
            return F.pair(F.eval(F.Power(b, F.ZZ(e / p))).negate(), F.ZZ(p));
          }
        }
        return F.NIL;
      }
      // odd_candidates = {i for i in candidates if i % 2}
      IAST oddCandidates = candidates.select(x -> x.isInteger() && ((IInteger) x).isOdd());
      if (oddCandidates.argSize() == 0) {
        return F.NIL;
      }
      IPair pp = perfectPower(minusN, oddCandidates, big, factor);
      if (pp.isPresent()) {
        return F.pair(pp.first().negate(), pp.second());
      }
      return F.NIL;
    }

    // non-integer handling
    if (n.isFraction()) {
      IInteger p = ((IFraction) n).numerator();
      IInteger q = ((IFraction) n).denominator();
      if (p.isOne()) {
        IPair qq = perfectPower(q, candidates, big, factor);
        return qq.isPresent() ? F.pair(F.C1.divide(qq.first()), qq.second()) : F.NIL;
      }
      IPair pp = perfectPower(p, F.NIL, true, factor);
      if (pp.isNIL()) {
        return F.NIL;
      }
      IPair qq = perfectPower(q, F.NIL, true, factor);
      if (qq.isNIL()) {
        return F.NIL;
      }
      final IInteger numBase = (IInteger) pp.first();
      final long numExp = pp.second().toLongDefault();
      final IInteger denBase = (IInteger) qq.first();
      final long denExp = qq.second().toLongDefault();
      long e;
      if (candidates.isPresent()) {
        long best = -1;
        for (int i = 1; i < candidates.size(); i++) {
          long c = candidates.get(i).toLongDefault();
          if (c > 0 && numExp % c == 0 && denExp % c == 0) {
            if (best < 0 || (big ? c > best : c < best)) {
              best = c;
            }
          }
        }
        if (best < 0) {
          return F.NIL;
        }
        e = best;
      } else {
        long g = BigInteger.valueOf(numExp).gcd(BigInteger.valueOf(denExp)).longValue();
        if (g == 1) {
          return F.NIL;
        }
        e = big ? g : smallestPrimeFactor(g);
      }
      // compute_tuple(exponent)
      IInteger newNum = numBase.powerRational(numExp / e);
      IInteger newDen = denBase.powerRational(denExp / e);
      return F.pair(F.QQ(newNum, newDen), F.ZZ(e));
    }

    if (n.isInteger()) {
      IInteger ni = (IInteger) n;
      // positive integer handling
      if (candidates.isNIL() && big) {
        return perfectPowerPrivate(ni.toBigNumerator(), 2);
      }
      if (ni.isLE(F.C3)) {
        // no unique exponent for 0, 1
        // 2 and 3 have exponents of 1
        return F.NIL;
      }

      // logn = math.log(n, 2)
      long logn = ni.bitLength();
      final double log2 = log2(ni);
      long maxPossible = logn + 2;
      // n % 10 in [2, 3, 7, 8] # squares cannot end in 2, 3, 7, 8
      int notSquare = 0;
      int mod = ni.mod(F.C10).toIntDefault();
      if (mod == 2 || mod == 3 || mod == 7 || mod == 8) {
        notSquare = 1;
      }

      long minPossible = 2L + notSquare;
      if (maxPossible > 0) {
        if (candidates.isPresent()) {
          IASTAppendable newCandidates = F.ListAlloc();
          for (int i = 1; i < candidates.size(); i++) {
            int a = candidates.get(i).toIntDefault();
            if (minPossible <= a && a < maxPossible) {
              newCandidates.append(F.ZZ(a));
            }
          }
          candidates = newCandidates;
          if (ni.isEven()) {
            int e = trailing(ni);
            candidates = candidates.select(x -> e % ((IInteger) x).toInt() == 0);
          }
          if (big) {
            candidates = candidates.reverse(F.ListAlloc(candidates.argSize()));
          }
          for (int i = 1; i < candidates.size(); i++) {
            int e = ((IInteger) candidates.get(i)).toInt();
            try {
              IPair p = ni.nthRoot(e);
              if (p.second().isTrue()) {
                return F.pair(p.first(), candidates.get(i));
              }
            } catch (IllegalArgumentException | ArithmeticException ex) {
              return F.NIL;
            }
          }
          return F.NIL;
        }

        IInteger fac = ni.mod(2).add(F.C2);
        // candidates = primerange(min_possible, max_possible) as a generator
        for (BigInteger candidate = BigInteger.valueOf(minPossible - 1)
            .nextProbablePrime(); candidate.longValue() < maxPossible; candidate =
                candidate.nextProbablePrime()) {
          fac = Generate.nextPrime(fac);
          IInteger e = F.ZZ(candidate);
          // see if there is a factor present
          if (factor && ni.mod(fac).isZero()) {
            // find what the potential power is
            int ei;
            if (fac.equals(F.C2)) {
              ei = trailing(ni);
            } else {
              ei = multiplicity(fac, ni);
            }
            if (ei == 1) {
              return F.NIL;
            }

            // maybe the e-th root of n is exact
            IInteger r;
            boolean exact = false;
            try {
              IPair p = ni.nthRoot(ei);
              r = (IInteger) p.first();
              exact = p.second().isTrue();
              // return F.pair(p.first(), candidates.get(i));
            } catch (IllegalArgumentException | ArithmeticException ex) {
              return F.NIL;
            }
            if (!exact) {
              // Having a factor, we know that e is the maximal
              // possible value for a root of n.
              // If n = fac**e*m can be written as a perfect
              // power then see if m can be written as r**E where
              // gcd(e, E) != 1 so n = (fac**(e//E)*r)**E
              // m = n // fac**e
              IInteger m = ni.iquo(fac.powerRational(ei));
              IPair rE = perfectPower(m, divisors(F.ZZ(ei), true, false));
              if (rE.isNIL()) {
                return F.NIL;
              }
              r = (IInteger) rE.first();
              int E = rE.second().toIntDefault();
              r = fac.powerRational(ei / E).multiply(r);
              ei = E;
            }
            if (!big) {
              IASTAppendable e0 = primeFactors(ei);
              if (e0.argSize() > 0 //
                  && !e0.first().equals(F.ZZ(ei))) {
                int first = e0.first().toIntDefault();
                r = r.powerRational(ei / first);
                ei = first;
              }
            }
            if (r.equals(ni)) {
              return F.NIL;
            }
            return F.pair(r, F.ZZ(ei));
          }
          try {

            // Weed out downright impossible candidates
            // if logn/e < 40:
            // b = 2.0**(logn/e)
            // if abs(int(b + 0.5) - b) > 0.01:
            // continue
            long ei = e.toLong();
            double lValue = log2 / ei;
            if (lValue < 40) {
              double b = Math.pow(2.0, lValue);
              double intPart = b < 0.0 ? Math.ceil(b + 0.5) - b : Math.floor(b + 0.5) - b;
              if (Math.abs(intPart) > 0.01) {
                continue;
              }
            }

            // now see if the plausible e makes a perfect power

            IPair p = ni.nthRoot(e.toInt());
            if (p.second().isTrue()) {
              IInteger r = (IInteger) p.first();
              if (big) {
                IPair m = perfectPower(r, F.NIL, big, factor);
                if (m.isPresent()) {
                  return F.pair(m.first(), e.times(m.second()));
                }
              }
              return F.pair(r, e);
            }
          } catch (IllegalArgumentException | ArithmeticException ex) {
            return F.NIL;
          }
        }
      }
    }

    return F.NIL;
  }

  // private static IInteger _Factors(IInteger n) {
  // return Generate.nextPrime(n);
  // }

  private static long smallestPrimeFactor(long g) {
    for (long p = 2; p * p <= g; p++) {
      if (g % p == 0) {
        return p;
      }
    }
    return g;
  }

  /**
   * Integer n-th root.
   *
   * @return <code>{root, remainder}</code> with <code>root^e + remainder == n</code>
   */
  private static BigInteger[] iroot(BigInteger n, int e) {
    if (n.signum() == 0 || e == 1) {
      return new BigInteger[] {n, BigInteger.ZERO};
    }
    if (e == 2) {
      BigInteger[] sr = n.sqrtAndRemainder();
      return sr;
    }
    // Newton's iteration starting with 2^ceil(bitLength/e) >= root
    BigInteger x = BigInteger.ONE.shiftLeft((n.bitLength() + e - 1) / e);
    BigInteger eBig = BigInteger.valueOf(e);
    BigInteger eMinus1 = BigInteger.valueOf(e - 1L);
    while (true) {
      BigInteger y = eMinus1.multiply(x).add(n.divide(x.pow(e - 1))).divide(eBig);
      if (y.compareTo(x) >= 0) {
        break;
      }
      x = y;
    }
    return new BigInteger[] {x, n.subtract(x.pow(e))};
  }

  /** Divide out the factor <code>p</code> completely: <code>{n/p^t, t}</code> */
  private static long remove(BigInteger[] n, BigInteger p) {
    long t = 0;
    while (true) {
      BigInteger[] qr = n[0].divideAndRemainder(p);
      if (qr[1].signum() != 0) {
        return t;
      }
      n[0] = qr[0];
      t++;
    }
  }

  private static IPair done(BigInteger n, java.util.Map<BigInteger, Long> factors, long g,
      long multi) {
    g = BigInteger.valueOf(g).gcd(BigInteger.valueOf(multi)).longValue();
    if (g == 1) {
      return F.NIL;
    }
    factors.put(n, factors.getOrDefault(n, 0L) + multi);
    BigInteger result = BigInteger.ONE;
    for (java.util.Map.Entry<BigInteger, Long> entry : factors.entrySet()) {
      result = result.multiply(entry.getKey().pow((int) (entry.getValue() / g)));
    }
    return F.pair(F.ZZ(result), F.ZZ(g));
  }

  /**
   * Return <code>(b, e)</code> such that <code>n == b**e</code> if <code>n</code> is a unique
   * perfect power with <code>e &gt; 1</code>, else {@link F#NIL}. The largest possible
   * <code>e</code> is returned. Port of <code>_perfect_power()</code> of
   * <a href="https://github.com/sympy/sympy/blob/master/sympy/ntheory/factor_.py">factor_.py</a>
   *
   * @param n a positive integer
   * @param nextP the next prime to check as a factor
   */
  private static IPair perfectPowerPrivate(BigInteger n, long nextP) {
    if (n.compareTo(BigInteger.valueOf(3)) <= 0) {
      return F.NIL;
    }
    java.util.Map<BigInteger, Long> factors = new java.util.TreeMap<BigInteger, Long>();
    long g = 0;
    long multi = 1;
    // If n is small, only trial factoring is faster
    if (n.compareTo(BigInteger.valueOf(1_000_000L)) <= 0) {
      long m = n.longValue();
      for (long p = nextP; p * p <= m; p++) {
        long t = 0;
        while (m % p == 0) {
          m /= p;
          t++;
        }
        if (t > 0) {
          factors.put(BigInteger.valueOf(p), t);
          g = BigInteger.valueOf(g).gcd(BigInteger.valueOf(t)).longValue();
          if (g == 1) {
            return F.NIL;
          }
        }
      }
      if (m > 1) {
        return F.NIL;
      }
      BigInteger result = BigInteger.ONE;
      for (java.util.Map.Entry<BigInteger, Long> entry : factors.entrySet()) {
        result = result.multiply(entry.getKey().pow((int) (entry.getValue() / g)));
      }
      return F.pair(F.ZZ(result), F.ZZ(g));
    }
    // divide by 2
    if (nextP < 3) {
      g = n.getLowestSetBit();
      if (g > 0) {
        if (g == 1) {
          return F.NIL;
        }
        n = n.shiftRight((int) g);
        factors.put(BigInteger.TWO, g);
        if (n.equals(BigInteger.ONE)) {
          return F.pair(F.C2, F.ZZ(g));
        }
        // If `m**g`, then we have found perfect power.
        // Otherwise, there is no possibility of perfect power, especially if `g` is prime.
        BigInteger[] mr = iroot(n, (int) g);
        if (mr[1].signum() == 0) {
          return F.pair(F.ZZ(mr[0].shiftLeft(1)), F.ZZ(g));
        } else if (MachinePrime.isPrime(g)) {
          return F.NIL;
        }
      }
      nextP = 3;
    }
    // square number?
    while (n.testBit(0) && !n.testBit(1) && !n.testBit(2)) {
      // n % 8 == 1
      BigInteger[] mr = iroot(n, 2);
      if (mr[1].signum() == 0) {
        n = mr[0];
        multi <<= 1;
      } else {
        break;
      }
    }
    if (n.compareTo(BigInteger.valueOf(nextP).pow(3)) < 0) {
      return done(n, factors, g, multi);
    }
    // trial factoring
    // Since the maximum value an exponent can take is `log_{next_p}(n)`,
    // the number of exponents to be checked can be reduced by performing a trial factoring.
    long tfMax = n.bitLength() / 27 + 24;
    if (nextP < tfMax) {
      for (long p = nextP; p < tfMax; p++) {
        if (!MachinePrime.isPrime(p)) {
          continue;
        }
        BigInteger[] m = new BigInteger[] {n};
        long t = remove(m, BigInteger.valueOf(p));
        if (t > 0) {
          n = m[0];
          t *= multi;
          long g1 = BigInteger.valueOf(g).gcd(BigInteger.valueOf(t)).longValue();
          if (g1 == 1) {
            return F.NIL;
          }
          factors.put(BigInteger.valueOf(p), t);
          if (n.equals(BigInteger.ONE)) {
            BigInteger result = BigInteger.ONE;
            for (java.util.Map.Entry<BigInteger, Long> entry : factors.entrySet()) {
              result = result.multiply(entry.getKey().pow((int) (entry.getValue() / g1)));
            }
            return F.pair(F.ZZ(result), F.ZZ(g1));
          } else if (g == 0 || g1 < g) {
            // If g is updated
            g = g1;
            BigInteger[] mr = iroot(n.pow((int) multi), (int) g);
            if (mr[1].signum() == 0) {
              BigInteger result = mr[0];
              for (java.util.Map.Entry<BigInteger, Long> entry : factors.entrySet()) {
                result = result.multiply(entry.getKey().pow((int) (entry.getValue() / g)));
              }
              return F.pair(F.ZZ(result), F.ZZ(g));
            } else if (MachinePrime.isPrime(g)) {
              return F.NIL;
            }
          }
        }
      }
      nextP = tfMax;
    }
    if (n.compareTo(BigInteger.valueOf(nextP).pow(3)) < 0) {
      return done(n, factors, g, multi);
    }
    // check iroot
    java.util.List<Long> primes = new java.util.ArrayList<Long>();
    if (g != 0) {
      // If g is non-zero, the exponent is a divisor of g.
      // 2 can be omitted since it has already been checked.
      long odd = g >> Long.numberOfTrailingZeros(g);
      for (long p = 3; p <= odd; p += 2) {
        if (odd % p == 0) {
          primes.add(p);
          while (odd % p == 0) {
            odd /= p;
          }
        }
      }
    } else {
      // The maximum possible value of the exponent is `log_{next_p}(n)`.
      // To compensate for the presence of computational error, 2 is added.
      long maxExponent = (long) (log2(n) / (Math.log(nextP) / Math.log(2.0))) + 2;
      for (long p = 3; p < maxExponent; p += 2) {
        if (MachinePrime.isPrime(p)) {
          primes.add(p);
        }
      }
    }
    double logn = log2(n);
    // Threshold for direct calculation
    double threshold = logn / 40;
    for (long p : primes) {
      if (threshold < p) {
        // If p is large, find the power root p directly without `iroot`.
        while (true) {
          double b = Math.pow(2.0, logn / p);
          long rb = (long) (b + 0.5);
          if (Math.abs(rb - b) < 0.01 && BigInteger.valueOf(rb).pow((int) p).equals(n)) {
            n = BigInteger.valueOf(rb);
            multi *= p;
            logn = log2(n);
          } else {
            break;
          }
        }
      } else {
        while (true) {
          BigInteger[] mr = iroot(n, (int) p);
          if (mr[1].signum() == 0) {
            n = mr[0];
            multi *= p;
            logn = log2(n);
          } else {
            break;
          }
        }
      }
      if (n.compareTo(BigInteger.valueOf(nextP).pow((int) p + 2)) < 0) {
        break;
      }
    }
    return done(n, factors, g, multi);
  }

  private static double log2(BigInteger value) {
    int shift = Math.max(0, value.bitLength() - 60);
    return Math.log(value.shiftRight(shift).doubleValue()) / Math.log(2.0) + shift;
  }

  /** <code>math.log2(n)</code> for a positive integer <code>n</code> */
  private static double log2(IInteger n) {
    return log2(n.toBigNumerator());
  }

  public static IASTAppendable primeFactors(int n) {
    return primeFactors(F.ZZ(n), F.NIL, false);
  }

  public static IASTAppendable primeFactors(IInteger n) {
    return primeFactors(n, F.NIL, false);
  }

  public static IASTAppendable primeFactors(IInteger n, IExpr limit, boolean verbose) {
    // Return a sorted list of n's prime factors, ignoring multiplicity
    // and any composite factor that remains if the limit was set too low
    // for complete factorization. Unlike factorint(), primefactors() does
    // not return -1 or 0.
    // Examples
    // ========
    // >>> from sympy.ntheory import primefactors, factorint, isprime
    // >>> primefactors(6)
    // [2, 3]
    // >>> primefactors(-5)
    // [5]
    // >>> sorted(factorint(123456).items())
    // [(2, 6), (3, 1), (643, 1)]
    // >>> primefactors(123456)
    // [2, 3, 643]
    // >>> sorted(factorint(10000000001, limit=200).items())
    // [(101, 1), (99009901, 1)]
    // >>> isprime(99009901)
    // False
    // >>> primefactors(10000000001, limit=300)
    // [101]
    // See Also
    // ========
    // divisors
    IASTAppendable factors = n.factorInteger();
    IASTAppendable s = F.ListAlloc(factors.argSize());
    for (int i = 1; i < factors.size(); i++) {
      IAST pair = (IAST) factors.get(i);
      IExpr prime = pair.first();
      if (prime.isMinusOne()//
          || prime.isOne()//
          || prime.isZero()) {
        continue;
      }
      s.append(prime);
    }
    return s;
  }

  /**
   * Count the number of trailing zero digits in the binary representation of n, i.e. determine the
   * largest power of 2 that divides n.
   * 
   * <p>
   * Examples:
   * 
   * <pre>
   * >> trailing(128)
   * 7
   * 
   * >> trailing(63)
   * 0
   * </pre>
   * 
   * @param n
   * @return
   */
  public static int trailing(IInteger n) {
    // TODO
    n = n.abs();
    if (n.isZero()) {
      return 0;
    }
    BigInteger numer = n.toBigNumerator();
    return numer.getLowestSetBit();
    // final int bitLength = numer.bitLength();
    // int t = 0;
    // for (int i = 0; i < bitLength; i++) {
    // if (!numer.testBit(i)) {
    // t++;
    // }
    // }
    // return t;
  }

  public static IInteger divisorCount(IInteger n) {
    return divisorCount(n, 1, false);
  }

  public static IInteger divisorCount(IInteger n, int modulus, boolean proper) {
    // those that are divisible by ``modulus`` are counted. If ``proper`` is True
    // then the divisor of ``n`` will not be counted.
    // Examples
    // ========
    // >>> from sympy import divisor_count
    // >>> divisor_count(6)
    // 4
    // >>> divisor_count(6, 2)
    // 2
    // >>> divisor_count(6, proper=True)
    // 3

    if (modulus == 0) {
      return F.C0;
    }
    if (modulus != 1) {
      IInteger[] divMod = n.divideAndRemainder(F.ZZ(modulus));
      n = divMod[0];
      if (!divMod[1].isZero()) {
        return F.C0;
      }
    }
    if (n.isZero()) {
      return F.C0;
    }
    // TODO
    IASTAppendable factorInteger = n.factorInteger();
    IASTAppendable timesAST = F.TimesAlloc(factorInteger.argSize());
    for (int i = 1; i < factorInteger.size(); i++) {
      IInteger k = (IInteger) factorInteger.get(i).first();
      if (k.isGT(F.C1)) {
        IInteger v = (IInteger) factorInteger.get(i).second();
        timesAST.append(v.add(F.C1));
      }
    }
    n = (IInteger) F.eval(timesAST);
    if (!n.isZero() && proper) {
      return n.subtract(F.C1);
    }
    return n;
  }

  public static IAST divisors(IInteger n) {
    // Return the number of divisors of ``n``. If ``modulus`` is not 1 then only
    return divisors(n, false, false);
  }

  public static IAST divisors(IInteger n, boolean generator, boolean proper) {

    // Return all divisors of n sorted from 1..n by default.
    // If generator is ``True`` an unordered generator is returned.
    // The number of divisors of n can be quite large if there are many
    // prime factors (counting repeated factors). If only the number of
    // factors is desired use divisor_count(n).
    // Examples
    // ========
    // >>> from sympy import divisors, divisor_count
    // >>> divisors(24)
    // [1, 2, 3, 4, 6, 8, 12, 24]
    // >>> divisor_count(24)
    // 8
    // >>> list(divisors(120, generator=True))
    // [1, 2, 4, 8, 3, 6, 12, 24, 5, 10, 20, 40, 15, 30, 60, 120]
    // Notes
    // =====
    // This is a slightly modified version of Tim Peters referenced at:
    // https://stackoverflow.com/questions/1010381/python-factorization
    // See Also
    // ========
    // primefactors, factorint, divisor_count

    n = n.abs();
    if (n.isProbablePrime()) {
      if (proper) {
        return F.CListC1;
      }
      return F.List(F.C1, n);
    }
    if (n.isOne()) {
      if (proper) {
        return F.CEmptyList;
      }
      return F.CListC1;
    }
    if (n.isZero()) {
      return F.CEmptyList;
    }
    return n.divisors();
  }

  public static int multiplicity(IInteger p, IInteger n) {
    // https://github.com/sympy/sympy/blob/695f6fbe1c639059e31827620040f8322b39c7e1/sympy/ntheory/factor_.py#L248
    // int pi = 0;
    // int ni = 0;
    // try {
    // pi = p.toInt();
    // ni = n.toInt();
    // } catch (ArithmeticException ex) {
    // // TODO
    // throw new ValueError("Factor.multiplicity - TODO handle exception");
    // }
    if (n.isZero()) {
      throw new ValueError("no such integer exists: multiplicity of " + n + " is not-defined");
    }
    if (p.equals(F.C2)) {
      return trailing(n);
    }
    if (p.isLT(F.C2)) {
      throw new ValueError("p must be an integer, 2 or larger, but got " + p);
    }
    if (p.equals(n)) {
      return 1;
    }
    int m = 0;

    IInteger[] divMod = n.divideAndRemainder(p);
    n = divMod[0];
    IInteger rem = divMod[1];

    while (rem.isZero()) {
      m++;
      // if (m > 5) {
      // TODO fix bug for m>5

      // // The multiplicity could be very large. Better
      // // to increment in powers of two
      // int e = 2;
      // while (true) {
      // IInteger ppow = p.powerRational(e);
      // if (ppow.isLT(n)) {
      // divMod = n.divideAndRemainder(ppow);
      // IInteger nnew = divMod[0];
      // rem = divMod[1];
      // if (!rem.isZero()) {
      // m += e;
      // e *= 2;
      // n = nnew;
      // continue;
      // }
      // }
      // return m + multiplicity(p, n);
      // }
      // }
      divMod = n.divideAndRemainder(p);
      n = divMod[0];
      rem = divMod[1];
    }
    return m;
  }

}
