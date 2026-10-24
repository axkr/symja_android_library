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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/algorithms/knapsack_solver.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.algorithms.internal;

import org.matheclipse.ortools.util.TimeLimit;

/** The base class of the knapsack solvers. */
public abstract class BaseKnapsackSolver {
  private final String solverName;

  /** Creates a solver with the given name. */
  protected BaseKnapsackSolver(String solverName) {
    this.solverName = solverName;
  }

  /**
   * Initializes the solver with the problem: <code>weights[d][i]</code> is the weight of item
   * <code>i</code> in dimension <code>d</code>.
   */
  public abstract void init(long[] profits, long[][] weights, long[] capacities);

  /**
   * Gets the lower bound (<code>bounds[0]</code>) and upper bound (<code>bounds[1]</code>) of the
   * profit when the item is in or out of the knapsack. Must not be called before
   * {@link #init(long[], long[][], long[])}. The default is <code>[0, Long.MAX_VALUE]</code>.
   */
  public void getLowerAndUpperBoundWhenItem(int itemId, boolean isItemIn, long[] bounds) {
    bounds[0] = 0L;
    bounds[1] = Long.MAX_VALUE;
  }

  /**
   * Solves the problem and returns the profit of the best solution found;
   * <code>isSolutionOptimal[0]</code> tells whether it is proven optimal.
   */
  public abstract long solve(TimeLimit timeLimit, double timeLimitInSeconds,
      boolean[] isSolutionOptimal);

  /** Returns true if the item is packed in the best solution. */
  public abstract boolean bestSolution(int itemId);

  /** The name given at construction. */
  public String getName() {
    return solverName;
  }
}
