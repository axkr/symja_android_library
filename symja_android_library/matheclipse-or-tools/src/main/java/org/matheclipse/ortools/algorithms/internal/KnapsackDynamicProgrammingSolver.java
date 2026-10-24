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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/algorithms/knapsack_solver.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.algorithms.internal;

import java.util.Arrays;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * Solves the one dimension 0-1 knapsack problem by dynamic programming over the capacity
 * ('DP-3' in Kellerer, Pferschy and Pisinger, "Knapsack problems", Springer 2004). Time and space
 * are O(capacity * number of items) and O(capacity): the table keeps, for each used capacity, the
 * best profit and the last item selected; the solution is rebuilt by solving the sub-problem
 * before that item with the remaining capacity again.
 */
public final class KnapsackDynamicProgrammingSolver extends BaseKnapsackSolver {
  private long[] profits = new long[0];
  private long[] weights = new long[0];
  private long capacity;
  private long[] computedProfits = new long[0];
  private int[] selectedItemIds = new int[0];
  private boolean[] bestSolution = new boolean[0];

  /** Creates the solver. */
  public KnapsackDynamicProgrammingSolver(String solverName) {
    super(solverName);
  }

  @Override
  public void init(long[] profits, long[][] weights, long[] capacities) {
    if (weights.length != 1) {
      throw new IllegalArgumentException("Current implementation of the dynamic programming "
          + "solver only deals with one dimension.");
    }
    if (capacities.length != weights.length) {
      throw new IllegalArgumentException("capacities and weights must have the same size");
    }
    this.profits = profits.clone();
    this.weights = weights[0].clone();
    this.capacity = capacities[0];
  }

  // Fills the table for the first numItems items and returns the last item selected at capacity.
  private int solveSubProblem(long capacity, int numItems) {
    final int capacityPlus1 = (int) (capacity + 1);
    Arrays.fill(selectedItemIds, 0, capacityPlus1, 0);
    Arrays.fill(computedProfits, 0, capacityPlus1, 0L);
    for (int itemId = 0; itemId < numItems; ++itemId) {
      final long itemWeight = weights[itemId];
      final long itemProfit = profits[itemId];
      for (long usedCapacity = capacity; usedCapacity >= itemWeight; --usedCapacity) {
        if (computedProfits[(int) (usedCapacity - itemWeight)]
            + itemProfit > computedProfits[(int) usedCapacity]) {
          computedProfits[(int) usedCapacity] =
              computedProfits[(int) (usedCapacity - itemWeight)] + itemProfit;
          selectedItemIds[(int) usedCapacity] = itemId;
        }
      }
    }
    return selectedItemIds[(int) capacity];
  }

  @Override
  public long solve(TimeLimit timeLimit, double timeLimitInSeconds, boolean[] isSolutionOptimal) {
    isSolutionOptimal[0] = true;
    if (capacity < 0 || capacity >= Integer.MAX_VALUE - 8) {
      throw new IllegalArgumentException(
          "the dynamic programming solver needs a capacity in [0, 2^31 - 9): " + capacity);
    }
    final int capacityPlus1 = (int) (capacity + 1);
    selectedItemIds = new int[capacityPlus1];
    computedProfits = new long[capacityPlus1];

    long remainingCapacity = capacity;
    int numItems = profits.length;
    bestSolution = new boolean[numItems];

    // Two changes for items of weight zero: upstream returns computedProfits[capacity] after the
    // loop, but when a zero-weight item is selected the next sub-problem has the same capacity
    // and overwrites that entry with a smaller profit, so the optimum of the first (complete)
    // table is remembered instead; and upstream loops while the remaining capacity is positive,
    // which misses the zero-weight items of a sub-problem of capacity zero. The loop terminates
    // because the number of items strictly decreases.
    long optimalProfit = 0L;
    boolean firstSubProblem = true;
    while (remainingCapacity >= 0 && numItems > 0) {
      final int selectedItemId = solveSubProblem(remainingCapacity, numItems);
      if (firstSubProblem) {
        optimalProfit = computedProfits[(int) capacity];
        firstSubProblem = false;
      }
      remainingCapacity -= weights[selectedItemId];
      numItems = selectedItemId;
      if (remainingCapacity >= 0) {
        bestSolution[selectedItemId] = true;
      }
    }
    return firstSubProblem ? computedProfits[(int) capacity] : optimalProfit;
  }

  @Override
  public boolean bestSolution(int itemId) {
    return bestSolution[itemId];
  }
}
