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
 * Solves the one dimension 0-1 knapsack problem by dynamic programming ('DP-2') with divide and
 * conquer for storage reduction (Kellerer, Pferschy and Pisinger, "Knapsack problems"): the items
 * are split in two halves, the best profit for every capacity is computed for each half, the best
 * split of the capacity between the halves is chosen and each half is solved recursively with its
 * share. Time O(capacity * number of items), space O(capacity + number of items).
 */
public final class KnapsackDivideAndConquerSolver extends BaseKnapsackSolver {
  private long[] profits = new long[0];
  private long[] weights = new long[0];
  private long capacity;
  private long[] computedProfitsStorage1 = new long[0];
  private long[] computedProfitsStorage2 = new long[0];
  private boolean[] bestSolution = new boolean[0];

  /** Creates the solver. */
  public KnapsackDivideAndConquerSolver(String solverName) {
    super(solverName);
  }

  @Override
  public void init(long[] profits, long[][] weights, long[] capacities) {
    if (weights.length != 1) {
      throw new IllegalArgumentException("Current implementation of the divide and conquer "
          + "solver only deals with one dimension.");
    }
    if (capacities.length != weights.length) {
      throw new IllegalArgumentException("capacities and weights must have the same size");
    }
    this.profits = profits.clone();
    this.weights = weights[0].clone();
    this.capacity = capacities[0];
  }

  // 'DP-2': the best profit of the items [startItem, endItem) for each capacity in [0, capacity].
  private void solveSubProblem(boolean firstStorage, long capacity, int startItem, int endItem) {
    final long[] computedProfitsStorage =
        firstStorage ? computedProfitsStorage1 : computedProfitsStorage2;
    final int capacityPlus1 = (int) (capacity + 1);
    Arrays.fill(computedProfitsStorage, 0, capacityPlus1, 0L);
    for (int itemId = startItem; itemId < endItem; ++itemId) {
      final long itemWeight = weights[itemId];
      final long itemProfit = profits[itemId];
      for (long usedCapacity = capacity; usedCapacity >= itemWeight; --usedCapacity) {
        if (computedProfitsStorage[(int) (usedCapacity - itemWeight)]
            + itemProfit > computedProfitsStorage[(int) usedCapacity]) {
          computedProfitsStorage[(int) usedCapacity] =
              computedProfitsStorage[(int) (usedCapacity - itemWeight)] + itemProfit;
        }
      }
    }
  }

  // Fills bestSolution for the items [startItem, endItem) and returns their best profit.
  private long divideAndConquer(long capacity, int startItem, int endItem) {
    final int itemBoundary = startItem + ((endItem - startItem) / 2);

    solveSubProblem(true, capacity, startItem, itemBoundary);
    solveSubProblem(false, capacity, itemBoundary, endItem);

    long maxSolution = 0;
    long capacity1 = 0;
    long capacity2 = 0;

    for (long capacityId = 0; capacityId <= capacity; capacityId++) {
      final long solution = computedProfitsStorage1[(int) capacityId]
          + computedProfitsStorage2[(int) (capacity - capacityId)];
      if (solution > maxSolution) {
        capacity1 = capacityId;
        capacity2 = capacity - capacityId;
        maxSolution = solution;
      }
    }

    if ((itemBoundary - startItem) == 1) {
      if (weights[startItem] <= capacity1) {
        bestSolution[startItem] = true;
      }
    } else if ((itemBoundary - startItem) > 1) {
      divideAndConquer(capacity1, startItem, itemBoundary);
    }

    if ((endItem - itemBoundary) == 1) {
      if (weights[itemBoundary] <= capacity2) {
        bestSolution[itemBoundary] = true;
      }
    } else if ((endItem - itemBoundary) > 1) {
      divideAndConquer(capacity2, itemBoundary, endItem);
    }
    return maxSolution;
  }

  @Override
  public long solve(TimeLimit timeLimit, double timeLimitInSeconds, boolean[] isSolutionOptimal) {
    isSolutionOptimal[0] = true;
    if (capacity < 0 || capacity >= Integer.MAX_VALUE - 8) {
      throw new IllegalArgumentException(
          "the divide and conquer solver needs a capacity in [0, 2^31 - 9): " + capacity);
    }
    final int capacityPlus1 = (int) (capacity + 1);
    computedProfitsStorage1 = new long[capacityPlus1];
    computedProfitsStorage2 = new long[capacityPlus1];
    bestSolution = new boolean[profits.length];

    return divideAndConquer(capacity, 0, profits.length);
  }

  @Override
  public boolean bestSolution(int itemId) {
    return bestSolution[itemId];
  }
}
