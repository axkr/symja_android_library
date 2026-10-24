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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/algorithms/knapsack_solver.cc and ortools/util/bitset.h C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.algorithms.internal;

/** Arithmetic helpers of the knapsack solvers. */
final class KnapsackMath {
  private KnapsackMath() {}

  /**
   * The position of the most significant bit of <code>n</code> read as an unsigned number, 0 for
   * <code>n == 0</code>.
   */
  static int mostSignificantBitPosition64(long n) {
    return n == 0 ? 0 : 63 - Long.numberOfLeadingZeros(n);
  }

  /** Returns true when <code>value1 * value2</code> may overflow a <code>long</code>. */
  static boolean willProductOverflow(long value1, long value2) {
    final int mostSignificantBitPosition1 = mostSignificantBitPosition64(value1);
    final int mostSignificantBitPosition2 = mostSignificantBitPosition64(value2);
    // The sum should be less than 61 to be safe as only the most significant bit is considered
    // and the values are signed.
    final int overflow = 61;
    return mostSignificantBitPosition1 + mostSignificantBitPosition2 > overflow;
  }

  /** An upper bound of <code>(numerator1 * numerator2) / denominator</code>. */
  static long upperBoundOfRatio(long numerator1, long numerator2, long denominator) {
    if (!willProductOverflow(numerator1, numerator2)) {
      final long numerator = numerator1 * numerator2;
      // Round to zero.
      return numerator / denominator;
    }
    final double ratio = ((double) numerator1 * (double) numerator2) / (double) denominator;
    // Round near.
    return (long) Math.floor(ratio + 0.5);
  }
}
