package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.PrimeCount;

public class PrimePi extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = ast.arg1();
    if (arg1.isInfinity()) {
      return F.CInfinity;
    }
    if (arg1.isNegativeInfinity()) {
      return F.C0;
    }
    if (arg1.isNegative() || arg1.isOne() || arg1.isZero()) {
      return F.C0;
    }

    IExpr x = F.NIL;
    if (arg1.isInteger()) {
      x = arg1;
    } else if (arg1.isReal()) {
      x = ((IReal) arg1).floorFraction();
    } else if (arg1.isRealResult() && arg1.isNumericFunction()) {
      // exact numerics like Pi or E^10 are floored exactly
      x = engine.evaluate(F.Floor(arg1));
    }

    if (x.isInteger()) {
      if (!x.isPositive()) {
        return F.C0;
      }
      long maxK = ((IInteger) x).toLongDefault();
      if (maxK == Long.MIN_VALUE || maxK > PrimeCount.MAX_X) {
        return Errors.printMessage(S.PrimePi, "zznotimpl2",
            F.List(S.PrimePi, F.stringx("first argument exceeding 10^14")));
      }
      IExpr method = options[0];
      String methodName = "Automatic";
      if (method.isString()) {
        methodName = method.toString();
      } else if (method.isSymbol()) {
        methodName = ((ISymbol) method).getSymbolName();
      }
      if ("DirectSieve".equalsIgnoreCase(methodName) || "Sieve".equalsIgnoreCase(methodName)) {
        int iterationLimit = engine.getIterationLimit();
        if (iterationLimit >= 0 && maxK > Math.max(iterationLimit, 1_000_001)) {
          IterationLimitExceeded.throwIt((int) Math.min(Integer.MAX_VALUE, maxK), ast);
        }
        return F.ZZ(PrimeCount.countSieve(maxK));
      }
      if ("LucyHedgehog".equalsIgnoreCase(methodName)) {
        if (maxK > 50_000_000_000_000L) {
          return Errors.printMessage(S.PrimePi, "zznotimpl2",
              F.List(S.PrimePi, F.stringx("first argument exceeding 5*10^13")));
        }
        return F.ZZ(lucyHedgehog(maxK));
      }
      if (method == S.Automatic || "DelegliseRivat".equalsIgnoreCase(methodName)
          || "Meissel".equalsIgnoreCase(methodName)
          || "MeisselLehmer".equalsIgnoreCase(methodName)
          || "LMO".equalsIgnoreCase(methodName)) {
        return F.ZZ(PrimeCount.pi(maxK));
      }
      // unknown method
      return F.NIL;
    }

    return F.NIL;
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
    newSymbol.setAttributes(Attribute.LISTABLE);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Method}, //
        new IExpr[] {S.Automatic});
  }

}
