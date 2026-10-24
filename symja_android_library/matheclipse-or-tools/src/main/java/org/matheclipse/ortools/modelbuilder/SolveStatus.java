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
// Modified for the Symja matheclipse-or-tools module: ported from
// ortools/linear_solver/wrappers/model_builder_helper.h C++ to Java (the SWIG generated enum is
// replaced), see NOTICE in the module root.

package org.matheclipse.ortools.modelbuilder;

/** The status of a solve of the ModelBuilder API. */
public enum SolveStatus {
  OPTIMAL,
  FEASIBLE,
  INFEASIBLE,
  UNBOUNDED,
  ABNORMAL,
  NOT_SOLVED,
  MODEL_IS_VALID,
  CANCELLED_BY_USER,
  UNKNOWN_STATUS,
  MODEL_INVALID,
  INVALID_SOLVER_PARAMETERS,
  SOLVER_TYPE_UNAVAILABLE,
  INCOMPATIBLE_OPTIONS;

  /** The C++ value of the enum. */
  public final int swigValue() {
    return ordinal();
  }

  /** The enum with the given C++ value. */
  public static SolveStatus swigToEnum(int swigValue) {
    final SolveStatus[] values = values();
    if (swigValue >= 0 && swigValue < values.length) {
      return values[swigValue];
    }
    throw new IllegalArgumentException("No enum " + SolveStatus.class + " with value " + swigValue);
  }
}
