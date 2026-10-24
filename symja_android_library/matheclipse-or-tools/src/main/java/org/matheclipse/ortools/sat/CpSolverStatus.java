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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/sat/cp_model.proto to Java (a plain Java class replaces the generated protobuf message), see NOTICE in the module root.

package org.matheclipse.ortools.sat;

/**
 * <pre>
 * The status returned by a solver trying to solve a CpModelProto.
 * </pre>
 */
public enum CpSolverStatus {
  /**
   * <pre>
   * The status of the model is still unknown. A search limit has been reached
   * before any of the statuses below could be determined.
   * </pre>
   */
  UNKNOWN(0),
  /**
   * <pre>
   * The given CpModelProto didn't pass the validation step. You can get a
   * detailed error by calling ValidateCpModel(model_proto).
   * </pre>
   */
  MODEL_INVALID(1),
  /**
   * <pre>
   * A feasible solution has been found. But the search was stopped before we
   * could prove optimality or before we enumerated all solutions of a
   * feasibility problem (if asked).
   * </pre>
   */
  FEASIBLE(2),
  /**
   * <pre>
   * The problem has been proven infeasible.
   * </pre>
   */
  INFEASIBLE(3),
  /**
   * <pre>
   * An optimal feasible solution has been found.
   *
   * More generally, this status represents a success. So we also return OPTIMAL
   * if we find a solution for a pure feasibility problem or if a gap limit has
   * been specified and we return a solution within this limit. In the case
   * where we need to return all the feasible solutions, this status will only
   * be returned if we enumerated all of them; If we stopped before, we will
   * return FEASIBLE.
   * </pre>
   */
  OPTIMAL(4),
  /** A value unknown to this version of the enum. */
  UNRECOGNIZED(-1);

  /** The number of {@link #UNKNOWN}. */
  public static final int UNKNOWN_VALUE = 0;
  /** The number of {@link #MODEL_INVALID}. */
  public static final int MODEL_INVALID_VALUE = 1;
  /** The number of {@link #FEASIBLE}. */
  public static final int FEASIBLE_VALUE = 2;
  /** The number of {@link #INFEASIBLE}. */
  public static final int INFEASIBLE_VALUE = 3;
  /** The number of {@link #OPTIMAL}. */
  public static final int OPTIMAL_VALUE = 4;

  private final int value;

  CpSolverStatus(int value) {
    this.value = value;
  }

  /** Returns the proto number of this value. */
  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /** Returns the value with the given proto number, or {@code null} if there is none. */
  public static CpSolverStatus forNumber(int value) {
    switch (value) {
      case 0:
        return UNKNOWN;
      case 1:
        return MODEL_INVALID;
      case 2:
        return FEASIBLE;
      case 3:
        return INFEASIBLE;
      case 4:
        return OPTIMAL;
      default:
        return null;
    }
  }

  /**
   * Returns the value with the given proto number, or {@code null} if there is none.
   *
   * @deprecated use {@link #forNumber(int)}
   */
  @Deprecated
  public static CpSolverStatus valueOf(int value) {
    return forNumber(value);
  }
}
