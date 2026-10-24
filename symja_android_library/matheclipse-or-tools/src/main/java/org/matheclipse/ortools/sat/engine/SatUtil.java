// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/sat/util.cc C++ to Java (the modular arithmetic and size two Diophantine equation helpers used by the model checker; int128 arithmetic by BigInteger), see NOTICE in the module root.

package org.matheclipse.ortools.sat.engine;

import java.math.BigInteger;
import org.matheclipse.ortools.util.Domain;

/** Modular arithmetic and Diophantine equation helpers of the CP-SAT model checker. */
final class SatUtil {
  private SatUtil() {}

  /**
   * Returns the inverse of x modulo m, i.e. k in [0, m) such that x * k = 1 modulo m, or 0 if x
   * and m are not coprime. Requires 0 &lt;= x &lt; m. Uses the extended Euclidean algorithm.
   */
  static long modularInverse(long x, long m) {
    long[] r = {m, x};
    long[] t = {0, 1};
    int i = 0;
    for (; r[i ^ 1] != 0; i ^= 1) {
      long q = r[i] / r[i ^ 1];
      r[i] = r[i] % r[i ^ 1];
      t[i] -= t[i ^ 1] * q;
    }
    if (r[i] != 1) {
      return 0;
    }
    if (t[i] < 0) {
      t[i] += m;
    }
    return t[i];
  }

  /** Returns x modulo m in [0, m) for m &gt; 0. */
  static long positiveMod(long x, long m) {
    long r = x % m;
    return r < 0 ? r + m : r;
  }

  /**
   * Returns X in [0, |mod|) such that X * coeff = rhs modulo mod. Requires coeff != 0, mod != 0
   * and gcd(|coeff|, |mod|) == 1.
   */
  static long productWithModularInverse(long coeff, long mod, long rhs) {
    mod = Math.abs(mod);
    if (rhs == 0 || mod == 1) {
      return 0;
    }
    coeff = positiveMod(coeff, mod);
    rhs = positiveMod(rhs, mod);
    long inverse = modularInverse(coeff, mod);
    if (inverse == 0) {
      throw new IllegalStateException("productWithModularInverse: no inverse");
    }
    return BigInteger.valueOf(inverse).multiply(BigInteger.valueOf(rhs))
        .mod(BigInteger.valueOf(mod)).longValue();
  }

  /**
   * Solves a * X + b * Y = cte. Returns null if there is no integer solution, otherwise
   * {a', b', cte', x0, y0}: the equation divided by gcd(a, b) and one solution; all solutions are
   * X = x0 + b' * Z, Y = y0 - a' * Z. Requires a, b != 0 and != Long.MIN_VALUE.
   */
  static long[] solveDiophantineEquationOfSizeTwo(long a, long b, long cte) {
    long gcd = gcd(Math.abs(a), Math.abs(b));
    if (cte % gcd != 0) {
      return null;
    }
    a /= gcd;
    b /= gcd;
    cte /= gcd;
    if (cte == 0) {
      return new long[] {a, b, cte, 0, 0};
    }
    // a valid x0 in [0, b) by considering the equation mod b
    long x0 = productWithModularInverse(a, b, cte);
    // x0 of the same sign as cte
    if (cte < 0 && x0 != 0) {
      x0 -= Math.abs(b);
    }
    BigInteger t = BigInteger.valueOf(cte)
        .subtract(BigInteger.valueOf(a).multiply(BigInteger.valueOf(x0)));
    long y0 = t.divide(BigInteger.valueOf(b)).longValue();
    return new long[] {a, b, cte, x0, y0};
  }

  /** Returns true if a * X + b * Y = cte has an integer solution with X in x and Y in y. */
  static boolean diophantineEquationOfSizeTwoHasSolutionInDomain(Domain x, long a, Domain y,
      long b, long cte) {
    if (x.isEmpty() || y.isEmpty()) {
      return false;
    }
    if (a == 0 && b == 0) {
      return cte == 0;
    }
    if (a == 0) {
      long div = cte / b;
      if (b * div != cte) {
        return false;
      }
      return y.contains(div);
    }
    if (b == 0) {
      long div = cte / a;
      if (a * div != cte) {
        return false;
      }
      return x.contains(div);
    }
    if (a == b) {
      long div = cte / a;
      if (a * div != cte) {
        return false;
      }
      return !x.negation().additionWith(new Domain(div)).intersectionWith(y).isEmpty();
    }
    long[] s = solveDiophantineEquationOfSizeTwo(a, b, cte);
    if (s == null) {
      return false;
    }
    a = s[0];
    b = s[1];
    long x0 = s[3];
    long y0 = s[4];
    Domain z1 = x.additionWith(new Domain(-x0)).inverseMultiplicationBy(b);
    Domain z2 = y.additionWith(new Domain(-y0)).inverseMultiplicationBy(-a);
    return !z1.intersectionWith(z2).isEmpty();
  }

  static long gcd(long a, long b) {
    while (b != 0) {
      long t = a % b;
      a = b;
      b = t;
    }
    return a;
  }
}
