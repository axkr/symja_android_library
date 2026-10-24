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
import java.util.Comparator;
import org.matheclipse.ortools.util.TimeLimit;

/**
 * Solves the one dimension 0-1 knapsack problem with at most 64 items by a depth-first branch and
 * bound on a 64-bit state. Items are sorted by decreasing efficiency; bit <code>i</code> of the
 * state tells whether the <code>i</code>-th sorted item is in. Bounds come from prefix sums of the
 * sorted profits and weights (binary search of the break item).
 */
public final class Knapsack64ItemsSolver extends BaseKnapsackSolver {
  /** The maximal number of items. */
  public static final int MAX_NUMBER_OF_64_ITEMS = 64;

  // An item with its efficiency stored, as the solver has only one dimension.
  private static final class KnapsackItemWithEfficiency {
    final int id;
    final long profit;
    final long weight;
    final double efficiency;

    KnapsackItemWithEfficiency(int id, long profit, long weight, long profitMax) {
      this.id = id;
      this.profit = profit;
      this.weight = weight;
      this.efficiency = (weight > 0) ? (double) profit / (double) weight : (double) profitMax;
    }
  }

  private KnapsackItemWithEfficiency[] sortedItems = new KnapsackItemWithEfficiency[0];
  private long[] sumProfits = new long[0];
  private long[] sumWeights = new long[0];
  private long capacity;
  private long state;
  private int stateDepth;

  private long bestSolutionProfit;
  private long bestSolution;
  private int bestSolutionDepth;

  // Sum of the weights of the included items in state.
  private long stateWeight;
  // Sum of the profits of the non included items in state.
  private long rejectedItemsProfit;
  // Sum of the weights of the non included items in state.
  private long rejectedItemsWeight;

  /** Creates the solver. */
  public Knapsack64ItemsSolver(String solverName) {
    super(solverName);
  }

  private static long oneBit64(int pos) {
    return 1L << pos;
  }

  @Override
  public void init(long[] profits, long[][] weights, long[] capacities) {
    if (weights.length != 1) {
      throw new IllegalArgumentException("Brute force solver only works with one dimension.");
    }
    if (capacities.length != weights.length) {
      throw new IllegalArgumentException("capacities and weights must have the same size");
    }
    capacity = capacities[0];
    final int numItems = profits.length;
    if (numItems > MAX_NUMBER_OF_64_ITEMS) {
      throw new IllegalArgumentException("To use Knapsack64ItemsSolver the number of items "
          + "should be less than " + MAX_NUMBER_OF_64_ITEMS + ". Current value: " + numItems
          + ".");
    }
    long profitMax = Long.MIN_VALUE;
    for (long profit : profits) {
      profitMax = Math.max(profitMax, profit);
    }

    sortedItems = new KnapsackItemWithEfficiency[numItems];
    for (int i = 0; i < numItems; ++i) {
      sortedItems[i] = new KnapsackItemWithEfficiency(i, profits[i], weights[0][i], profitMax);
    }
    // Decreasing efficiency (upstream uses the unstable std::sort; this sort is stable).
    Arrays.sort(sortedItems, new Comparator<KnapsackItemWithEfficiency>() {
      @Override
      public int compare(KnapsackItemWithEfficiency item1, KnapsackItemWithEfficiency item2) {
        return Double.compare(item2.efficiency, item1.efficiency);
      }
    });

    sumProfits = new long[numItems + 1];
    sumWeights = new long[numItems + 1];
    long sumProfit = 0;
    long sumWeight = 0;
    for (int i = 0; i < numItems; ++i) {
      sumProfit += sortedItems[i].profit;
      sumWeight += sortedItems[i].weight;
      sumProfits[i + 1] = sumProfit;
      sumWeights[i + 1] = sumWeight;
    }
  }

  @Override
  public long solve(TimeLimit timeLimit, double timeLimitInSeconds, boolean[] isSolutionOptimal) {
    isSolutionOptimal[0] = true;
    final int numItems = sortedItems.length;
    bestSolutionProfit = 0L;
    bestSolution = 0L;
    bestSolutionDepth = 0;
    if (numItems == 0) {
      // Upstream reads the first item unconditionally.
      return 0L;
    }
    state = 1L;
    stateDepth = 0;
    stateWeight = sortedItems[0].weight;
    rejectedItemsProfit = 0L;
    rejectedItemsWeight = 0L;

    final long[] bounds = new long[2];
    boolean fail;
    while (stateDepth >= 0) {
      fail = false;
      if (stateWeight > capacity || stateDepth >= numItems) {
        fail = true;
      } else {
        getLowerAndUpperBound(bounds);
        if (bestSolutionProfit < bounds[0]) {
          bestSolutionProfit = bounds[0];
          bestSolution = state;
          bestSolutionDepth = stateDepth;
        }
      }
      fail = fail || bestSolutionProfit >= bounds[1];
      goToNextState(fail);
    }

    buildBestSolution();
    return bestSolutionProfit;
  }

  @Override
  public boolean bestSolution(int itemId) {
    return (bestSolution & oneBit64(itemId)) != 0L;
  }

  // The index of the first prefix sum greater than capacity, minus one.
  private int getBreakItemId(long capacity) {
    // std::upper_bound on the sorted prefix sums.
    int low = 0;
    int high = sumWeights.length;
    while (low < high) {
      final int mid = (low + high) >>> 1;
      if (capacity < sumWeights[mid]) {
        high = mid;
      } else {
        low = mid + 1;
      }
    }
    return low - 1;
  }

  // The bounds of the current state (not cached: experiments show no gain).
  private void getLowerAndUpperBound(long[] bounds) {
    final long availableCapacity = capacity + rejectedItemsWeight;
    final int breakItemId = getBreakItemId(availableCapacity);
    final int numItems = sortedItems.length;
    if (breakItemId >= numItems) {
      bounds[0] = sumProfits[numItems] - rejectedItemsProfit;
      bounds[1] = bounds[0];
      return;
    }

    bounds[0] = sumProfits[breakItemId] - rejectedItemsProfit;
    bounds[1] = bounds[0];
    final long consumedCapacity = sumWeights[breakItemId];
    final long remainingCapacity = availableCapacity - consumedCapacity;
    final double efficiency = sortedItems[breakItemId].efficiency;
    final long additionalProfit = (long) (remainingCapacity * efficiency);
    bounds[1] += additionalProfit;
  }

  // Moves to the next state: deeper with the next item in, or backtracks to the last item in and
  // removes it.
  private void goToNextState(boolean hasFailed) {
    long mask = oneBit64(stateDepth);
    if (!hasFailed) { // Go to the next item.
      ++stateDepth;
      state = state | (mask << 1);
      if (stateDepth < sortedItems.length) {
        stateWeight += sortedItems[stateDepth].weight;
      }
    } else {
      // Backtrack to the last item in.
      while ((state & mask) == 0L && stateDepth >= 0) {
        final KnapsackItemWithEfficiency item = sortedItems[stateDepth];
        rejectedItemsProfit -= item.profit;
        rejectedItemsWeight -= item.weight;
        --stateDepth;
        mask = mask >>> 1;
      }

      if ((state & mask) != 0L) { // The item was in, remove it.
        state = state & ~mask;
        final KnapsackItemWithEfficiency item = sortedItems[stateDepth];
        rejectedItemsProfit += item.profit;
        rejectedItemsWeight += item.weight;
        stateWeight -= item.weight;
      }
    }
  }

  private void buildBestSolution() {
    long remainingCapacity = capacity;
    long checkProfit = 0L;

    // The remaining capacity at bestSolutionDepth, to redo the bound computation.
    for (int i = 0; i <= bestSolutionDepth; ++i) {
      if ((bestSolution & oneBit64(i)) != 0) {
        remainingCapacity -= sortedItems[i].weight;
        checkProfit += sortedItems[i].profit;
      }
    }

    // Add all items till the break item.
    final int numItems = sortedItems.length;
    for (int i = bestSolutionDepth + 1; i < numItems; ++i) {
      final long weight = sortedItems[i].weight;
      if (remainingCapacity >= weight) {
        remainingCapacity -= weight;
        checkProfit += sortedItems[i].profit;
        bestSolution = bestSolution | oneBit64(i);
      } else {
        bestSolution = bestSolution & ~oneBit64(i);
      }
    }
    if (bestSolutionProfit != checkProfit) {
      throw new IllegalStateException(
          "best solution profit " + bestSolutionProfit + " != " + checkProfit);
    }

    // The items were sorted by efficiency; map the solution back to the user order.
    long tmpSolution = 0L;
    for (int i = 0; i < numItems; ++i) {
      if ((bestSolution & oneBit64(i)) != 0) {
        final int originalId = sortedItems[i].id;
        tmpSolution = tmpSolution | oneBit64(originalId);
      }
    }
    bestSolution = tmpSolution;
  }
}
