package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * FiniteGroupCount[n] - returns the number of finite groups of order n. Uses a lookup table for
 * orders 1 through 2047.
 */
public class FiniteGroupCount extends AbstractEvaluator {

  // Lookup table for FiniteGroupCount[n] for n = 1..2047 (OEIS A000001)
  private static final long[] FINITE_GROUP_COUNT_TABLE = {1, 1, 1, 2, 1, 2, 1, 5, 2, 2, 1, 5, 1, 2,
      1, 14, 1, 5, 1, 5, 2, 2, 1, 15, 2, 2, 5, 4, 1, 4, 1, 51, 1, 2, 1, 14, 1, 2, 2, 14, 1, 6, 1, 4,
      2, 2, 1, 52, 2, 5, 1, 5, 1, 15, 2, 13, 2, 2, 1, 13, 1, 2, 4, 267, 1, 4, 1, 5, 1, 4, 1, 50, 1,
      2, 3, 4, 1, 6, 1, 52, 15, 2, 1, 15, 1, 2, 1, 12, 1, 10, 1, 4, 2, 2, 1, 231, 1, 5, 2, 16, 1, 4,
      1, 14, 2, 2, 1, 45, 1, 6, 2, 43, 1, 6, 1, 5, 4, 2, 1, 47, 2, 2, 1, 4, 5, 16, 1, 2328, 2, 4, 1,
      10, 1, 2, 5, 15, 1, 4, 1, 11, 1, 2, 1, 197, 1, 2, 6, 5, 1, 13, 1, 12, 2, 4, 2, 18, 1, 2, 1,
      238, 1, 55, 1, 5, 2, 2, 1, 57, 2, 4, 5, 4, 1, 4, 2, 42, 1, 2, 1, 37, 1, 4, 2, 12, 1, 6, 1, 4,
      13, 4, 1, 1543, 1, 2, 2, 12, 1, 10, 1, 52, 2, 2, 2, 12, 2, 2, 2, 51, 1, 12, 1, 5, 1, 2, 1,
      177, 1, 2, 2, 15, 1, 6, 1, 197, 6, 2, 1, 15, 1, 4, 2, 14, 1, 16, 1, 4, 2, 4, 1, 208, 1, 5, 67,
      5, 2, 4, 1, 12, 1, 15, 1, 46, 2, 2, 1, 56092, 1, 6, 1, 15, 2, 2, 1, 39, 1, 4, 1, 4, 1, 30, 1,
      54, 5, 2, 4, 10, 1, 2, 4, 40, 1, 4, 1, 4, 2, 4, 1, 1045, 2, 4, 2, 5, 1, 23, 1, 14, 5, 2, 1,
      49, 2, 2, 1, 42, 2, 10, 1, 9, 2, 6, 1, 61, 1, 2, 4, 4, 1, 4, 1, 1640, 1, 4, 1, 176, 2, 2, 2,
      15, 1, 12, 1, 4, 5, 2, 1, 228, 1, 5, 1, 15, 1, 18, 5, 12, 1, 2, 1, 12, 1, 10, 14, 195, 1, 4,
      2, 5, 2, 2, 1, 162, 2, 2, 3, 11, 1, 6, 1, 42, 2, 4, 1, 15, 1, 4, 7, 12, 1, 60, 1, 11, 2, 2, 1,
      20169, 2, 2, 4, 5, 1, 12, 1, 44, 1, 2, 1, 30, 1, 2, 5, 221, 1, 6, 1, 5, 16, 6, 1, 46, 1, 6, 1,
      4, 1, 10, 1, 235, 2, 4, 1, 41, 1, 2, 2, 14, 2, 4, 1, 4, 2, 4, 1, 775, 1, 4, 1, 5, 1, 6, 1, 51,
      13, 4, 1, 18, 1, 2, 1, 1396, 1, 34, 1, 5, 2, 2, 1, 54, 1, 2, 5, 11, 1, 12, 1, 51, 4, 2, 1, 55,
      1, 4, 2, 12, 1, 6, 2, 11, 2, 2, 1, 1213, 1, 2, 2, 12, 1, 261, 1, 14, 2, 10, 1, 12, 1, 4, 4,
      42, 2, 4, 1, 56, 1, 2, 2, 195, 2, 6, 6, 4, 1, 8, 1, 10494213, 15, 2, 1, 15, 1, 4, 1, 49, 1,
      10, 1, 4, 6, 2, 1, 170, 2, 4, 2, 9, 1, 4, 1, 12, 1, 2, 2, 119, 1, 2, 2, 246, 1, 24, 1, 5, 4,
      16, 1, 39, 1, 2, 2, 4, 1, 16, 1, 180, 1, 2, 1, 10, 1, 2, 49, 12, 1, 12, 1, 11, 1, 4, 2, 8681,
      1, 5, 2, 15, 1, 6, 1, 15, 4, 2, 1, 66, 1, 4, 1, 51, 1, 30, 1, 5, 2, 4, 1, 205, 1, 6, 4, 4, 7,
      4, 1, 195, 3, 6, 1, 36, 1, 2, 2, 35, 1, 6, 1, 15, 5, 2, 1, 260, 15, 2, 2, 5, 1, 32, 1, 12, 2,
      2, 1, 12, 2, 4, 2, 21541, 1, 4, 1, 9, 2, 4, 1, 757, 1, 10, 5, 4, 1, 6, 2, 53, 5, 4, 1, 40, 1,
      2, 2, 12, 1, 18, 1, 4, 2, 4, 1, 1280, 1, 2, 17, 16, 1, 4, 1, 53, 1, 4, 1, 51, 1, 15, 2, 42, 2,
      8, 1, 5, 4, 2, 1, 44, 1, 2, 1, 36, 1, 62, 1, 1387, 1, 2, 1, 10, 1, 6, 4, 15, 1, 12, 2, 4, 1,
      2, 1, 840, 1, 5, 2, 5, 2, 13, 1, 40, 504, 4, 1, 18, 1, 2, 6, 195, 2, 10, 1, 15, 5, 4, 1, 54,
      1, 2, 2, 11, 1, 39, 1, 42, 1, 4, 2, 189, 1, 2, 2, 39, 1, 6, 1, 4, 2, 2, 1, 1090235, 1, 12, 1,
      5, 1, 16, 4, 15, 5, 2, 1, 53, 1, 4, 5, 172, 1, 4, 1, 5, 1, 4, 2, 137, 1, 2, 1, 4, 1, 24, 1,
      1211, 2, 2, 1, 15, 1, 4, 1, 14, 1, 113, 1, 16, 2, 4, 1, 205, 1, 2, 11, 20, 1, 4, 1, 12, 5, 4,
      1, 30, 1, 4, 2, 1630, 2, 6, 1, 9, 13, 2, 1, 186, 2, 2, 1, 4, 2, 10, 2, 51, 2, 10, 1, 10, 1, 4,
      5, 12, 1, 12, 1, 11, 2, 2, 1, 4725, 1, 2, 3, 9, 1, 8, 1, 14, 4, 4, 5, 18, 1, 2, 1, 221, 1, 68,
      1, 15, 1, 2, 1, 61, 2, 4, 15, 4, 1, 4, 1, 19349, 2, 2, 1, 150, 1, 4, 7, 15, 2, 6, 1, 4, 2, 8,
      1, 222, 1, 2, 4, 5, 1, 30, 1, 39, 2, 2, 1, 34, 2, 2, 4, 235, 1, 18, 2, 5, 1, 2, 2, 222, 1, 4,
      2, 11, 1, 6, 1, 42, 13, 4, 1, 15, 1, 10, 1, 42, 1, 10, 2, 4, 1, 2, 1, 11394, 2, 4, 2, 5, 1,
      12, 1, 42, 2, 4, 1, 900, 1, 2, 6, 51, 1, 6, 2, 34, 5, 2, 1, 46, 1, 4, 2, 11, 1, 30, 1, 196, 2,
      6, 1, 10, 1, 2, 15, 199, 1, 4, 1, 4, 2, 2, 1, 954, 1, 6, 2, 13, 1, 23, 2, 12, 2, 2, 1, 37, 1,
      4, 2, 49487365422L, 4, 66, 2, 5, 19, 4, 1, 54, 1, 4, 2, 11, 1, 4, 1, 231, 1, 2, 1, 36, 2, 2,
      2, 12, 1, 40, 1, 4, 51, 4, 2, 1028, 1, 5, 1, 15, 1, 10, 1, 35, 2, 4, 1, 12, 1, 4, 4, 42, 1, 4,
      2, 5, 1, 10, 1, 583, 2, 2, 6, 4, 2, 6, 1, 1681, 6, 4, 1, 77, 1, 2, 2, 15, 1, 16, 1, 51, 2, 4,
      1, 170, 1, 4, 5, 5, 1, 12, 1, 12, 2, 2, 1, 46, 1, 4, 2, 1092, 1, 8, 1, 5, 14, 2, 2, 39, 1, 4,
      2, 4, 1, 254, 1, 42, 2, 2, 1, 41, 1, 2, 5, 39, 1, 4, 1, 11, 1, 10, 1, 157877, 1, 2, 4, 16, 1,
      6, 1, 49, 13, 4, 1, 18, 1, 4, 1, 53, 1, 32, 1, 5, 1, 2, 2, 279, 1, 4, 2, 11, 1, 4, 3, 235, 2,
      2, 1, 99, 1, 8, 2, 14, 1, 6, 1, 11, 14, 2, 1, 1040, 1, 2, 1, 13, 2, 16, 1, 12, 5, 27, 1, 12,
      1, 2, 69, 1387, 1, 16, 1, 20, 2, 4, 1, 164, 4, 2, 2, 4, 1, 12, 1, 153, 2, 2, 1, 15, 1, 2, 2,
      51, 1, 30, 1, 4, 1, 4, 1, 1460, 1, 55, 4, 5, 1, 12, 2, 14, 1, 4, 1, 131, 1, 2, 2, 42, 3, 6, 1,
      5, 5, 4, 1, 44, 1, 10, 3, 11, 1, 10, 1, 1116461, 5, 2, 1, 10, 1, 2, 4, 35, 1, 12, 1, 11, 1, 2,
      1, 3609, 1, 4, 2, 50, 1, 24, 1, 12, 2, 2, 1, 18, 1, 6, 2, 244, 1, 18, 1, 9, 2, 2, 1, 181, 1,
      2, 51, 4, 2, 12, 1, 42, 1, 8, 5, 61, 1, 4, 1, 12, 1, 6, 1, 11, 2, 4, 1, 11720, 1, 2, 1, 5, 1,
      112, 1, 52, 1, 2, 2, 12, 1, 4, 4, 245, 1, 4, 1, 9, 5, 2, 1, 211, 2, 4, 2, 38, 1, 6, 15, 195,
      15, 6, 2, 29, 1, 2, 1, 14, 1, 32, 1, 4, 2, 4, 1, 198, 1, 4, 8, 5, 1, 4, 1, 153, 1, 2, 1, 227,
      2, 4, 5, 19324, 1, 8, 1, 5, 4, 4, 1, 39, 1, 2, 2, 15, 4, 16, 1, 53, 6, 4, 1, 40, 1, 12, 5, 12,
      1, 4, 2, 4, 1, 2, 1, 5958, 1, 4, 5, 12, 2, 6, 1, 14, 4, 10, 1, 40, 1, 2, 2, 179, 1, 1798, 1,
      15, 2, 4, 1, 61, 1, 2, 5, 4, 1, 46, 1, 1387, 1, 6, 2, 36, 2, 2, 1, 49, 1, 24, 1, 11, 10, 2, 1,
      222, 1, 4, 3, 5, 1, 10, 1, 41, 2, 4, 1, 174, 1, 2, 2, 195, 2, 4, 1, 15, 1, 6, 1, 889, 1, 2, 2,
      4, 1, 12, 2, 178, 13, 2, 1, 15, 4, 4, 1, 12, 1, 20, 1, 4, 5, 4, 1, 408641062, 1, 2, 60, 36, 1,
      4, 1, 15, 2, 2, 1, 46, 1, 16, 1, 54, 1, 24, 2, 5, 2, 4, 1, 221, 1, 4, 1, 11, 1, 30, 1, 928, 2,
      4, 1, 10, 2, 2, 13, 14, 1, 4, 1, 11, 2, 6, 1, 697, 1, 4, 3, 5, 1, 8, 1, 12, 5, 2, 2, 64, 1, 4,
      2, 10281, 1, 10, 1, 5, 1, 4, 1, 54, 1, 8, 2, 11, 1, 4, 1, 51, 6, 2, 1, 477, 1, 2, 2, 56, 5, 6,
      1, 11, 5, 4, 1, 1213, 1, 4, 2, 5, 1, 72, 1, 68, 2, 2, 1, 12, 1, 2, 13, 42, 1, 38, 1, 9, 2, 2,
      2, 137, 1, 2, 5, 11, 1, 6, 1, 21507, 5, 10, 1, 15, 1, 4, 1, 34, 2, 60, 2, 4, 5, 2, 1, 1005, 2,
      5, 2, 5, 1, 4, 1, 12, 1, 10, 1, 30, 1, 10, 1, 235, 1, 6, 1, 50, 309, 4, 2, 39, 7, 2, 1, 11, 1,
      36, 2, 42, 2, 2, 5, 40, 1, 2, 2, 39, 1, 12, 1, 4, 3, 2, 1, 47937, 1, 4, 2, 5, 1, 13, 1, 35, 4,
      4, 1, 37, 1, 4, 2, 51, 1, 16, 1, 9, 1, 30, 2, 64, 1, 2, 14, 4, 1, 4, 1, 1285, 1, 2, 1, 228, 1,
      2, 5, 53, 1, 8, 2, 4, 2, 2, 4, 260, 1, 6, 1, 15, 1, 110, 1, 12, 2, 4, 1, 12, 1, 4, 5, 1083553,
      1, 12, 1, 5, 1, 4, 1, 749, 1, 4, 2, 11, 3, 30, 1, 54, 13, 6, 1, 15, 2, 2, 9, 12, 1, 10, 1, 35,
      2, 2, 1, 1264, 2, 4, 6, 5, 1, 18, 1, 14, 2, 4, 1, 117, 1, 2, 2, 178, 1, 6, 1, 5, 4, 4, 1, 162,
      2, 10, 1, 4, 1, 16, 1, 1630, 2, 2, 2, 56, 1, 10, 15, 15, 1, 4, 1, 4, 2, 12, 1, 1096, 1, 2, 21,
      9, 1, 6, 1, 39, 5, 2, 1, 18, 1, 4, 2, 195, 1, 120, 1, 9, 2, 2, 1, 54, 1, 4, 4, 36, 1, 4, 1,
      186, 2, 2, 1, 36, 1, 6, 15, 12, 1, 8, 1, 4, 5, 4, 1, 241004, 1, 5, 1, 15, 4, 10, 1, 15, 2, 4,
      1, 34, 1, 2, 4, 167, 1, 12, 1, 15, 1, 2, 1, 3973, 1, 4, 1, 4, 1, 40, 1, 235, 11, 2, 1, 15, 1,
      6, 1, 144, 1, 18, 1, 4, 2, 2, 2, 203, 1, 4, 15, 15, 1, 12, 2, 39, 1, 4, 1, 120, 1, 2, 2, 1388,
      1, 6, 1, 13, 4, 4, 1, 39, 1, 2, 5, 4, 1, 66, 1, 963, 1, 8, 1, 10, 2, 4, 4, 12, 2, 12, 1, 4, 2,
      4, 2, 6538, 1, 2, 2, 20, 1, 6, 2, 46, 63, 2, 1, 88, 1, 12, 1, 42, 1, 10, 2, 5, 5, 2, 1, 175,
      2, 2, 2, 11, 1, 12, 1};

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.arg1().isInteger()) {
      IInteger nExpr = (IInteger) ast.arg1();
      int n = nExpr.toIntDefault();
      if (F.isPresent(n)) {
        if (n <= 0) {
          // Positive integer expected at position `2` in `1`.
          return Errors.printMessage(S.FiniteGroupCount, "intp", F.List(ast, F.C1), engine);
        }
        if (n <= FINITE_GROUP_COUNT_TABLE.length) {
          return F.ZZ(FINITE_GROUP_COUNT_TABLE[n - 1]);
        }

        // Beyond the lookup table, apply theoretical algorithms based on factorization

        // Check if n is a "cyclic number" where GCD(n, EulerPhi(n)) == 1
        // Any integer with this property guarantees exactly 1 group (the cyclic group)
        IExpr eulerPhi = engine.evaluate(F.EulerPhi(nExpr));
        IExpr gcd = engine.evaluate(F.GCD(nExpr, eulerPhi));
        if (gcd.isOne()) {
          return F.C1;
        }

        // Extract the prime factorization signature: {{p1, e1}, {p2, e2}, ...}
        IExpr factored = engine.evaluate(F.FactorInteger(nExpr));
        if (factored.isList()) {
          IAST list = (IAST) factored;
          int numPrimeFactors = list.argSize();

          // Case: Single prime factor n = p^e
          if (numPrimeFactors == 1) {
            IAST pair = (IAST) list.arg1();
            IInteger p = (IInteger) pair.arg1();
            int e = pair.arg2().toIntDefault(-1);

            if (e == 1)
              return F.C1; // Prime p: 1 group
            if (e == 2)
              return F.C2; // p^2: 2 groups
            if (e == 3)
              return F.ZZ(5); // p^3: 5 groups
            if (e == 4) {
              // p^4: 14 groups for p=2, 15 groups for p > 2
              if (p.equals(F.C2)) {
                return F.ZZ(14);
              } else {
                return F.ZZ(15);
              }
            }
          }
          // Case: Two prime factors (semiprimes and similar cases)
          else if (numPrimeFactors == 2) {
            IAST pair1 = (IAST) list.arg1();
            IAST pair2 = (IAST) list.arg2();
            IInteger p1 = (IInteger) pair1.arg1();
            IInteger e1 = (IInteger) pair1.arg2();
            IInteger p2 = (IInteger) pair2.arg1();
            IInteger e2 = (IInteger) pair2.arg2();

            // Specifically handle n = p * q (both exponents are 1)
            if (e1.isOne() && e2.isOne()) {
              // FactorInteger sorts by primes ascending, so p2 > p1
              IInteger p = p2;
              IInteger q = p1;

              // Sylow theorem rule for semiprimes:
              // If q divides p - 1, there are 2 groups (1 abelian, 1 non-abelian).
              // Otherwise, there is only 1 group.
              IInteger pMinus1 = p.subtract(F.C1);
              IInteger[] divAndRem = pMinus1.divideAndRemainder(q);
              if (divAndRem[1].isZero()) {
                return F.C2;
              } else {
                return F.C1;
              }
            }
          }
        }
      }
    }

    // For numbers with complex prime signatures outside the algorithms and lookup table,
    // calculation is too exhaustive. We gracefully leave it unevaluated.
    // "Function `1` not implemented.",
    return Errors.printMessage(S.FiniteGroupCount, "zznotimpl", F.List(ast), engine);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
