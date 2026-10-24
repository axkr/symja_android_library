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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/min_cost_flow.h C++ to Java, API adapted from the OR-Tools Java wrapper, see NOTICE in the module root.

package org.matheclipse.ortools.graph;

/**
 * Base class holding the {@link Status} of the min cost flow solvers; users work with
 * {@link MinCostFlow}.
 */
public class MinCostFlowBase {

  /** Creates the base object. */
  public MinCostFlowBase() {}

  /** Does nothing; kept for compatibility with the OR-Tools Java wrapper. */
  public void delete() {}

  /** The status of a min cost flow solve. */
  public enum Status {
    /** The problem was not solved, or its data were edited. */
    NOT_SOLVED,
    /** An optimal flow was found. */
    OPTIMAL,
    /** A feasible, not necessarily optimal flow was found. */
    FEASIBLE,
    /** The supplies cannot be sent to the demands within the arc capacities. */
    INFEASIBLE,
    /** The sum of the supplies differs from the sum of the demands. */
    UNBALANCED,
    /** This should not happen: the result failed the optimality check. */
    BAD_RESULT,
    /**
     * An integer overflow occurred because of the cost magnitudes: each unit cost times
     * <code>(numNodes + 1)</code> must not overflow, and the node potentials can grow further, so
     * smaller costs may solve the problem.
     */
    BAD_COST_RANGE,
    /**
     * The arc capacities are too large: for some node the initial excess plus the sum of its
     * incoming capacities, or minus the sum of its outgoing capacities, overflows
     * <code>long</code>.
     *
     * <p>
     * This constant exists in C++ but not in the OR-Tools Java wrapper (where such a result would
     * raise an exception); it is appended so that the other constants keep their positions.
     */
    BAD_CAPACITY_RANGE;

    /** The value of the C++ enum constant. */
    public final int swigValue() {
      return ordinal();
    }

    /** The constant with the given C++ enum value. */
    public static Status swigToEnum(int swigValue) {
      Status[] values = values();
      if (swigValue >= 0 && swigValue < values.length) {
        return values[swigValue];
      }
      throw new IllegalArgumentException("No enum " + Status.class + " with value " + swigValue);
    }
  }
}
