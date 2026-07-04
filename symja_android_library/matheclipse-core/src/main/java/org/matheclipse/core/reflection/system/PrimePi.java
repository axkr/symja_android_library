package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.roaringbitmap.RoaringBitmap;

public class PrimePi extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = ast.arg1();
    if (arg1.isNegative() || arg1.isOne() || arg1.isZero()) {
      return F.C0;
    }

    IExpr x = F.NIL;
    if (arg1.isInteger()) {
      x = arg1;
    } else if (arg1.isReal() && arg1.isPositive()) {
      x = engine.evaluate(((IReal) arg1).floorFraction());
    } else {
      IExpr sn = engine.evaluate(F.N(arg1));
      if (sn instanceof IReal) {
        x = engine.evaluate(((IReal) sn).floorFraction());
      }
    }

    if (x.isInteger() && x.isPositive()) {
      long maxK = -1;
      if (x instanceof IInteger) {
        try {
          maxK = Long.parseLong(x.toString());
        } catch (NumberFormatException e) {
          // Number exceeds Long.MAX_VALUE
        }
      } else {
        maxK = x.toIntDefault();
      }

      if (maxK >= 0) {
        // Parse the Method option
        IExpr method = options[0];
        String methodName = "Automatic";
        if (method.isString()) {
          methodName = method.toString();
        } else if (method.isSymbol()) {
          methodName = ((ISymbol) method).getSymbolName();
        }

        long result = 0;
        if ("DirectSieve".equalsIgnoreCase(methodName) || "Sieve".equalsIgnoreCase(methodName)) {
          int iterationLimit = engine.getIterationLimit();
          if (iterationLimit >= 0 && maxK > Math.max(iterationLimit, 1_000_001)) {
            IterationLimitExceeded.throwIt((int) Math.min(Integer.MAX_VALUE, maxK), ast);
          }
          result = directSieve(maxK);
        } else if ("LucyHedgehog".equalsIgnoreCase(methodName)) {
          long limit = 10_000_000_000_000L * 5;
          if (maxK <= limit) {
            result = lucyHedgehog(maxK);
          } else {
            return Errors.printMessage(S.PrimePi, "zznotimpl2",
                F.List(S.PrimePi, F.stringx("first argument exceeding 5*10^13")));
          }
        } else if ("DelegliseRivat".equalsIgnoreCase(methodName)
            || "Meissel".equalsIgnoreCase(methodName)
            || "MeisselLehmer".equalsIgnoreCase(methodName)) {
          // Extended limit via RoaringBitmap memory compression
          long limit = 10_000_000_000_000L;
          if (maxK <= limit) {
            result = delegliseRivat(maxK, ast, engine);
          } else {
            return Errors.printMessage(S.PrimePi, "zznotimpl2", F.List(S.PrimePi,
                F.stringx("first argument exceeding 10^13 for combinatorial method")));
          }
        } else {
          if (method != S.Automatic) {
            return F.NIL;
          }
          // Automatic mode relies on size heuristics to balance memory and CPU constraints.
          if (maxK <= 1_000_000L) {
            result = directSieve(maxK);
          } else {
            long limit = 1_000_000_000L;
            if (maxK <= limit) {
              result = lucyHedgehog(maxK);
            } else {
              limit = 10_000_000_000_000L;
              if (maxK <= limit) {
                result = delegliseRivat(maxK, ast, engine);
              } else {
                return Errors.printMessage(S.PrimePi, "zznotimpl2",
                    F.List(S.PrimePi, F.stringx("first argument exceeding 5*10^13")));
              }
            }
          }
        }
        return F.ZZ(result);
      }
    }

    return F.NIL;
  }

  /**
   * Direct Sieve of Eratosthenes to count primes up to the given limit efficiently for smaller
   * inputs. Uses primitive arrays for L1 cache speed on small domains.
   *
   * @param limit the upper bound (inclusive)
   * @return the number of primes less than or equal to the limit
   */
  private static long directSieve(long limit) {
    int n = (int) limit;
    if (n < 2)
      return 0;
    boolean[] isPrime = new boolean[n + 1];
    for (int i = 2; i <= n; i++) {
      isPrime[i] = true;
    }
    for (int p = 2; p * p <= n; p++) {
      if (isPrime[p]) {
        for (int i = p * p; i <= n; i += p) {
          isPrime[i] = false;
        }
      }
    }
    long count = 0;
    for (int i = 2; i <= n; i++) {
      if (isPrime[i]) {
        count++;
      }
    }
    return count;
  }

  /**
   * Combinatorial approach for prime counting based on the Deleglise-Rivat algorithm. Structurally
   * ported from Mathilda's count_dr. Utilizes RoaringBitmap to aggressively limit memory footprint,
   * solving the JVM array limits.
   *
   * @param n the upper bound (inclusive)
   * @param ast the original abstract syntax tree
   * @param engine the evaluation engine
   * @return the number of primes less than or equal to n
   */
  private static long delegliseRivat(long n, IAST ast, EvalEngine engine) {
    if (n < 2) {
      return 0;
    }
    if (n <= 1_000_000L) {
      return directSieve(n);
    }

    int z = (int) Math.sqrt(n);
    int[] primes = getPrimesRoaring(z, ast, engine);

    // a = pi(x^(1/3))
    int y = (int) Math.cbrt(n);
    int a = 0;
    while (a < primes.length && primes[a] <= y) {
      a++;
    }

    // Use a primitive map strategy (Long) instead of Strings to prevent heap fragmentation
    long phi = phiCalc(n, a, primes, new HashMap<>());
    long p2 = 0;

    // Calculate P2 (ordinary leaves) matching count_dr
    for (int i = a; i < primes.length; i++) {
      int p = primes[i];
      if ((long) p * p > n) {
        break;
      }
      long limit = n / p;
      int pi_n_p = 0;
      if (limit <= z) {
        // Binary search is slightly faster for lookup in our array than linear counting
        int idx = java.util.Arrays.binarySearch(primes, (int) limit);
        pi_n_p = idx >= 0 ? idx + 1 : -(idx + 1);
      } else {
        pi_n_p = (int) lucyHedgehog(limit);
      }
      p2 += (pi_n_p - i);
    }

    return phi + a - 1 - p2;
  }

  /**
   * Helper function for the Combinatorial method: Partial sieve function phi(x, a). Calculates the
   * number of integers <= x with no prime factors <= P_a.
   */
  private static long phiCalc(long x, int a, int[] primes, Map<Long, Long> memo) {
    if (a == 0) {
      return x;
    }
    if (a == 1) {
      return x - (x >> 1);
    }
    if (a == 2) {
      return x - (x >> 1) - x / 3 + x / 6;
    }
    if (x <= primes[a - 1]) {
      return 1;
    }

    // Pack x and a into a single long to prevent String object creation overhead.
    // Assuming 'a' easily fits in the lower 16 bits.
    long key = (x << 16) ^ a;
    if (memo.containsKey(key)) {
      return memo.get(key);
    }

    long result = phiCalc(x, a - 1, primes, memo) - phiCalc(x / primes[a - 1], a - 1, primes, memo);
    memo.put(key, result);
    return result;
  }

  /**
   * Generates a strict array of prime numbers up to the limit utilizing RoaringBitmap to save
   * massive amounts of memory during the initialization sweep.
   */
  private static int[] getPrimesRoaring(int limit, IAST ast, EvalEngine engine) {
    RoaringBitmap primes = new RoaringBitmap();
    // Add all potential candidates initially
    primes.add(2L, limit + 1L);

    int p = 2;
    while (p * p <= limit) {
      for (long i = p * p; i <= limit; i += p) {
        primes.remove((int) i);
      }
      // Efficiently jump to the next set bit (prime)
      long temp = primes.nextValue(p + 1);
      if (temp < 0) {
        break;
      }
      if (temp > Integer.MAX_VALUE) {
        IterationLimitExceeded.throwIt(Integer.MAX_VALUE, ast);
      }
      p = (int) temp;
    }
    // Dumps directly to an int array native memory, consuming exactly what is needed
    return primes.toArray();
  }

  /**
   * Lucy Hedgehog dynamic programming algorithm to evaluate the prime counting function
   * efficiently. Extremely fast for ranges vastly exceeding direct sieving constraints.
   *
   * Time complexity is O(N^(3/4)) and space complexity is O(N^(1/2)).
   *
   * @param n the upper bound (inclusive)
   * @return the number of primes less than or equal to n
   */
  private static long lucyHedgehog(long n) {
    if (n < 2) {
      return 0;
    }
    int r = (int) Math.sqrt(n);
    long[] V = new long[r * 2 + 1];
    long[] S = new long[r * 2 + 1];
    int k = 0;

    // Initialize the V array with values n/i
    for (long i = 1; i <= r; i++) {
      V[k++] = n / i;
    }
    // Initialize the remaining part of V with values i down to 1
    for (int i = r; i >= 1; i--) {
      if (V[k - 1] != i) {
        V[k++] = i;
      }
    }

    // S[i] represents the number of elements in the corresponding range minus 1
    for (int i = 0; i < k; i++) {
      S[i] = V[i] - 1;
    }

    // Main DP loop evaluating prime distributions based on previous sieve sizes
    for (int p = 2; p <= r; p++) {
      int pIdx = k - p;
      if (S[pIdx] > S[pIdx + 1]) { // If p is identified as a prime
        long sp = S[pIdx + 1]; // Count of primes smaller than p
        long p2 = (long) p * p;
        for (int i = 0; i < k; i++) {
          if (V[i] >= p2) {
            long temp = V[i] / p;
            int idx;
            // Determine the precise index of 'temp' in the dynamically sized map V
            if (temp <= r) {
              idx = k - (int) temp;
            } else {
              idx = (int) (n / temp) - 1;
            }
            // Subtract the number of pseudo-primes scaled back recursively
            S[i] -= (S[idx] - sp);
          } else {
            break;
          }
        }
      }
    }
    return S[0];
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Method}, //
        new IExpr[] {S.Automatic});
  }

}