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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/lp_data/lp_types.h C++
// to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

/**
 * Basic constants and helpers of the linear programming data layer.
 *
 * <p>
 * The C++ code uses strong integer types <code>RowIndex</code>, <code>ColIndex</code> and
 * <code>EntryIndex</code> and the floating point type <code>Fractional</code>. In this port they are
 * plain <code>int</code> and <code>double</code>; dense rows and columns are <code>double[]</code>
 * arrays indexed by column respectively row.
 */
public final class LpTypes {

  /** The largest finite value. */
  public static final double RANGE_MAX = Double.MAX_VALUE;

  /** Positive infinity, used for absent bounds. */
  public static final double INFINITY = Double.POSITIVE_INFINITY;

  /** The machine epsilon of <code>double</code>. */
  public static final double EPSILON = Math.ulp(1.0);

  /** The invalid row index. */
  public static final int INVALID_ROW = -1;

  /** The invalid column index. */
  public static final int INVALID_COL = -1;

  private LpTypes() {}

  /** Returns true if <code>value</code> is neither infinite nor NaN. */
  public static boolean isFinite(double value) {
    return value > -INFINITY && value < INFINITY;
  }

  /** Converts a number of floating point operations into a deterministic time. */
  public static double deterministicTimeForFpOperations(long n) {
    final double kConversionFactor = 2e-9;
    return kConversionFactor * n;
  }

  /** Returns the constraint status corresponding to a variable status. */
  public static ConstraintStatus variableToConstraintStatus(VariableStatus status) {
    switch (status) {
      case BASIC:
        return ConstraintStatus.BASIC;
      case AT_LOWER_BOUND:
        return ConstraintStatus.AT_LOWER_BOUND;
      case AT_UPPER_BOUND:
        return ConstraintStatus.AT_UPPER_BOUND;
      case FIXED_VALUE:
        return ConstraintStatus.FIXED_VALUE;
      case FREE:
      default:
        return ConstraintStatus.FREE;
    }
  }
}
