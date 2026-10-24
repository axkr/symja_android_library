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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/lp_data/lp_utils.h,
// ortools/util/fp_utils.h and ortools/base/accurate_sum.h C++ to Java, see NOTICE in the module
// root.

package org.matheclipse.ortools.lpdata;

/** Numerical helpers shared by the LP data layer and Glop. */
public final class LpUtils {

  private LpUtils() {}

  /** Kahan-style accurate summation (C++ <code>AccurateSum</code>/<code>KahanSum</code>). */
  public static final class KahanSum {
    private double sum;
    private double errorSum;

    /** Adds <code>value</code> to the sum. */
    public void add(double value) {
      errorSum += value;
      final double newSum = sum + errorSum;
      errorSum += sum - newSum;
      sum = newSum;
    }

    /** The current value of the sum. */
    public double value() {
      return sum;
    }
  }

  /**
   * A sum where infinite terms of one sign are counted separately so that the sum without one given
   * term can be computed. With <code>positive</code> the supported infinity is +inf, otherwise -inf.
   */
  public static final class SumWithOneMissing {
    private final boolean positive;
    private int numInfinities;
    private final KahanSum sum = new KahanSum();

    /** Creates an empty sum supporting +inf (positive) or -inf terms. */
    public SumWithOneMissing(boolean positive) {
      this.positive = positive;
    }

    private double infinity() {
      return positive ? LpTypes.INFINITY : -LpTypes.INFINITY;
    }

    /** Adds a term. */
    public void add(double x) {
      if (!LpTypes.isFinite(x)) {
        ++numInfinities;
        return;
      }
      if (!LpTypes.isFinite(sum.value())) {
        return;
      }
      sum.add(x);
    }

    /** Removes one infinite term. */
    public void removeOneInfinity() {
      --numInfinities;
    }

    /** The total sum. */
    public double sum() {
      if (numInfinities > 0) {
        return infinity();
      }
      return sum.value();
    }

    /** The sum without the term <code>x</code>. */
    public double sumWithout(double x) {
      if (LpTypes.isFinite(x)) {
        if (numInfinities > 0) {
          return infinity();
        }
        return sum.value() - x;
      }
      if (numInfinities > 1) {
        return infinity();
      }
      return sum.value();
    }

    /** Defensive version of {@link #sumWithout(double)} for lower bounds. */
    public double sumWithoutLb(double c) {
      if (!LpTypes.isFinite(c)) {
        return sumWithout(c);
      }
      return sumWithout(c) - Math.abs(c) * 1e-12;
    }

    /** Defensive version of {@link #sumWithout(double)} for upper bounds. */
    public double sumWithoutUb(double c) {
      if (!LpTypes.isFinite(c)) {
        return sumWithout(c);
      }
      return sumWithout(c) + Math.abs(c) * 1e-12;
    }
  }

  /** x * x. */
  public static double square(double x) {
    return x * x;
  }

  /** Returns true if x is at most y up to the given absolute/relative tolerance. */
  public static boolean isSmallerWithinTolerance(double x, double y, double tolerance) {
    if (Double.isInfinite(y)) {
      return x <= y;
    }
    return x <= y + tolerance * Math.max(1.0, Math.min(Math.abs(x), Math.abs(y)));
  }

  /** Returns true if x is within tolerance of an integer; false for infinities. */
  public static boolean isIntegerWithinTolerance(double x, double tolerance) {
    if (Double.isInfinite(x)) {
      return false;
    }
    return Math.abs(x - Math.rint(x)) <= tolerance;
  }

  /** |x - y| &lt;= tolerance, infinities must be equal. */
  public static boolean areWithinAbsoluteTolerance(double x, double y, double tolerance) {
    if (Double.isInfinite(x) || Double.isInfinite(y)) {
      return x == y;
    }
    return Math.abs(x - y) <= tolerance;
  }

  /** Absolute or relative closeness, infinities must be equal. */
  public static boolean areWithinAbsoluteOrRelativeTolerances(double x, double y,
      double relativeTolerance, double absoluteTolerance) {
    if (Double.isInfinite(x) || Double.isInfinite(y)) {
      return x == y;
    }
    final double difference = Math.abs(x - y);
    if (difference <= absoluteTolerance) {
      return true;
    }
    final double largestMagnitude = Math.max(Math.abs(x), Math.abs(y));
    return difference <= largestMagnitude * relativeTolerance;
  }

  /** The C++ <code>std::round</code>: rounds half away from zero. */
  public static double round(double x) {
    if (Double.isNaN(x) || Double.isInfinite(x)) {
      return x;
    }
    return x < 0 ? -Math.floor(-x + 0.5) : Math.floor(x + 0.5);
  }

  /** The dense scalar product of two vectors of the same length. */
  public static double scalarProduct(double[] u, double[] v) {
    double sum = 0.0;
    for (int i = 0; i < u.length; ++i) {
      sum += u[i] * v[i];
    }
    return sum;
  }

  /** The scalar product with a scattered vector, using its non-zeros when available. */
  public static double scalarProduct(double[] u, ScatteredVector v) {
    if (v.numNonZeros == 0) {
      double sum = 0.0;
      final int n = Math.min(u.length, v.values.length);
      for (int i = 0; i < n; ++i) {
        sum += u[i] * v.values[i];
      }
      return sum;
    }
    double sum = 0.0;
    for (int i = 0; i < v.numNonZeros; ++i) {
      final int index = v.nonZeros[i];
      sum += u[index] * v.values[index];
    }
    return sum;
  }

  /** Kahan summed dense scalar product. */
  public static double preciseScalarProduct(double[] u, double[] v) {
    KahanSum sum = new KahanSum();
    for (int i = 0; i < u.length; ++i) {
      sum.add(u[i] * v[i]);
    }
    return sum.value();
  }

  /** Kahan summed squared norm. */
  public static double preciseSquaredNorm(double[] v) {
    KahanSum sum = new KahanSum();
    for (double x : v) {
      sum.add(x * x);
    }
    return sum.value();
  }

  /** The squared norm of a dense vector. */
  public static double squaredNorm(double[] v) {
    double sum = 0.0;
    for (double x : v) {
      sum += x * x;
    }
    return sum;
  }

  /** The largest magnitude of a dense vector. */
  public static double infinityNorm(double[] v) {
    double norm = 0.0;
    for (double x : v) {
      norm = Math.max(norm, Math.abs(x));
    }
    return norm;
  }

  /** Negates all entries. */
  public static void changeSign(double[] v) {
    for (int i = 0; i < v.length; ++i) {
      v[i] = -v[i];
    }
  }

  /** Returns true if all entries are zero. */
  public static boolean isAllZero(double[] v) {
    for (double x : v) {
      if (x != 0.0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Applies a column permutation to a row-indexed vector: <code>v[perm[i]] = old v[i]</code>; used to
   * keep row-indexed data in sync with a permuted basis.
   */
  public static void applyColumnPermutationToRowIndexedVector(int[] perm, double[] v) {
    final double[] tmp = v.clone();
    for (int i = 0; i < perm.length; ++i) {
      v[perm[i]] = tmp[i];
    }
  }

  /** Same as {@link #applyColumnPermutationToRowIndexedVector(int[], double[])} for int arrays. */
  public static void applyColumnPermutationToRowIndexedVector(int[] perm, int[] v) {
    final int[] tmp = v.clone();
    for (int i = 0; i < perm.length; ++i) {
      v[perm[i]] = tmp[i];
    }
  }
}
