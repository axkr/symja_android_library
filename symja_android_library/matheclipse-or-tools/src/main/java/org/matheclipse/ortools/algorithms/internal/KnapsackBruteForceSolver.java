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

import org.matheclipse.ortools.util.TimeLimit;

/**
 * Solves the one dimension 0-1 knapsack problem with at most 30 items by enumerating all states
 * in binary counting order; each state differs from the previous one in the bits of
 * <code>state ^ (state - 1)</code>, so the profit and weight sums are updated incrementally.
 */
public final class KnapsackBruteForceSolver extends BaseKnapsackSolver {
  /** The maximal number of items. */
  public static final int MAX_NUMBER_OF_BRUTE_FORCE_ITEMS = 30;

  private int numItems;
  private final long[] profitsWeights = new long[MAX_NUMBER_OF_BRUTE_FORCE_ITEMS * 2];
  private long capacity;
  private long bestSolutionProfit;
  private int bestSolution;

  /** Creates the solver. */
  public KnapsackBruteForceSolver(String solverName) {
    super(solverName);
  }

  @Override
  public void init(long[] profits, long[][] weights, long[] capacities) {
    if (weights.length != 1) {
      throw new IllegalArgumentException("Brute force solver only works with one dimension.");
    }
    if (capacities.length != weights.length) {
      throw new IllegalArgumentException("capacities and weights must have the same size");
    }
    numItems = profits.length;
    if (numItems != weights[0].length) {
      throw new IllegalArgumentException("weights[0] must have one entry per item");
    }
    if (numItems > MAX_NUMBER_OF_BRUTE_FORCE_ITEMS) {
      throw new IllegalArgumentException("To use KnapsackBruteForceSolver the number of items "
          + "should be less than " + MAX_NUMBER_OF_BRUTE_FORCE_ITEMS + ". Current value: "
          + numItems + ".");
    }
    for (int i = 0; i < numItems; ++i) {
      profitsWeights[i * 2] = profits[i];
      profitsWeights[i * 2 + 1] = weights[0][i];
    }
    capacity = capacities[0];
  }

  @Override
  public long solve(TimeLimit timeLimit, double timeLimitInSeconds, boolean[] isSolutionOptimal) {
    isSolutionOptimal[0] = true;
    bestSolutionProfit = 0L;
    bestSolution = 0;

    final int numStates = 1 << numItems;
    int prevState = 0;
    // Unsigned sums, as upstream.
    long sumProfit = 0L;
    long sumWeight = 0L;
    // The loop starts at 1, because state 0 (no item, sum_profit = 0) is the initial best.
    for (int state = 1; state < numStates; ++state, ++prevState) {
      int diffState = state ^ prevState;
      int localState = state;
      int itemId = 0;
      while (diffState != 0) {
        if ((diffState & 1) != 0) { // There is a diff.
          if ((localState & 1) != 0) { // This item is now in the knapsack.
            sumProfit += profitsWeights[itemId];
            sumWeight += profitsWeights[itemId + 1];
          } else { // This item has been removed from the knapsack.
            sumProfit -= profitsWeights[itemId];
            sumWeight -= profitsWeights[itemId + 1];
          }
        }
        itemId += 2;
        localState = localState >>> 1;
        diffState = diffState >>> 1;
      }

      if (Long.compareUnsigned(sumWeight, capacity) <= 0
          && Long.compareUnsigned(bestSolutionProfit, sumProfit) < 0) {
        bestSolutionProfit = sumProfit;
        bestSolution = state;
      }
    }
    return bestSolutionProfit;
  }

  @Override
  public boolean bestSolution(int itemId) {
    return (bestSolution & (1 << itemId)) != 0;
  }
}
