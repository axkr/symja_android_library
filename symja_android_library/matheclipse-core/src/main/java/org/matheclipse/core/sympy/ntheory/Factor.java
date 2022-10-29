package org.matheclipse.core.sympy.ntheory;

import java.math.BigInteger;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.sympy.exception.ValueError;

public class Factor {
  public static IPair perfectPower(ISignedNumber n) {
    return perfectPower(n, F.NIL, true, true);
  }

  public static IPair perfectPower(ISignedNumber n, IAST candidates) {
    return perfectPower(n, candidates, true, true);
  }

  public static IPair perfectPower(ISignedNumber n, IAST candidates, boolean big, boolean factor) {
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
    if (n.isFraction()) {
      IInteger p = ((IFraction) n).numerator();
      IInteger q = ((IFraction) n).denominator();
      IPair pp = F.NIL;
      if (p.isOne()) {
        pp = perfectPower(q);
        if (pp.isPresent()) {
          pp = F.pair(F.C1.divide(pp.first()), pp.second());
        }
      } else {
        pp = perfectPower(p);
        if (pp.isPresent()) {
          IExpr num = pp.first();
          IExpr e = pp.second();
          IPair pq = perfectPower(q, F.List(e));
          if (pq.isPresent()) {
            IExpr denom = pq.first();
            pp = F.pair(num.divide(denom), e);
          }
        }
      }
      return pp;
    }

    if (n.isInteger()) {
      IInteger ni = (IInteger) n;
      if (ni.isNegative()) {
        IPair pp = perfectPower(ni.negate());
        if (pp.isPresent()) {
          IExpr b = pp.first();
          IExpr e = pp.second();
          if (((IInteger) e).isOdd()) {
            return F.pair(b.negate(), e);
          }
        }
        return F.NIL;
      }
      if (ni.isLE(F.C3)) {
        // no unique exponent for 0, 1
        // 2 and 3 have exponents of 1
        return F.NIL;
      }

      // logn = math.log(n, 2)
      long logn = ni.bitLength();
      long maxPossible = logn + 2;
      // n % 10 in [2, 3, 7, 8] # squares cannot end in 2, 3, 7, 8
      int notSquare = 0;
      int mod = ni.mod(F.C10).toIntDefault();
      if (mod == 2 || mod == 3 || mod == 7 || mod == 8) {
        notSquare = 1;
      }

      long minPossible = 2L + notSquare;
      if (maxPossible > 0) {
        if (candidates.isNIL()) {
          candidates = Generate.primeRange(minPossible, maxPossible);
        } else {
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
        for (int i = 1; i < candidates.size(); i++) {
          fac = Generate.nextPrime(fac);
          IExpr arg = candidates.get(i);
          if (!arg.isInteger()) {
            break;
          }
          IInteger e = (IInteger) arg;
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
              IInteger m = ni.iquo(fac).powerRational(ei);
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
            long ei = e.toLong();
            long lValue = logn / ei;
            if ((logn / ei) < 40) {
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
    for (int i = 1; i < factors.argSize(); i++) {
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
      if (divMod[1].isZero()) {
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
      return n.subtract(F.CN1);
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
