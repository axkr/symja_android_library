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

import java.util.Arrays;
import java.util.Comparator;

/**
 * A {@link KnapsackPropagator} enforcing one capacity constraint.
 *
 * <p>
 * It works like a 0-1 knapsack solver: items are scanned in decreasing efficiency order; the break
 * item is the first unbound item that does not fit in the remaining capacity, and it is chosen as
 * the next item to assign (Greenberg and Hegerich). The upper bound adds to the profit of the
 * items before the break item the Martello-Toth bound U2 on the remaining capacity. When there is
 * only one propagator, all unbound items before the break item form a better lower bound and a
 * better solution.
 */
public final class KnapsackCapacityPropagator extends KnapsackPropagator {
  private final long capacity;
  private long consumedCapacity;
  private int breakItemId;
  private KnapsackItem[] sortedItems = new KnapsackItem[0];
  private long profitMax;

  /** Creates the propagator of a capacity constraint. */
  public KnapsackCapacityPropagator(KnapsackState state, long capacity) {
    super(state);
    this.capacity = capacity;
    this.consumedCapacity = 0;
    this.breakItemId = KnapsackSearchNode.NO_SELECTION;
    this.profitMax = 0;
  }

  @Override
  public void computeProfitBounds() {
    setProfitLowerBound(currentProfit());
    breakItemId = KnapsackSearchNode.NO_SELECTION;

    long remainingCapacity = capacity - consumedCapacity;
    int breakSortedItemId = KnapsackSearchNode.NO_SELECTION;
    final int numberOfSortedItems = sortedItems.length;
    for (int sortedId = 0; sortedId < numberOfSortedItems; ++sortedId) {
      final KnapsackItem item = sortedItems[sortedId];
      if (!state().isBound(item.id)) {
        breakItemId = item.id;

        if (remainingCapacity >= item.weight) {
          remainingCapacity -= item.weight;
          setProfitLowerBound(profitLowerBound() + item.profit);
        } else {
          breakSortedItemId = sortedId;
          break;
        }
      }
    }

    setProfitUpperBound(profitLowerBound());

    if (breakSortedItemId != KnapsackSearchNode.NO_SELECTION) {
      final long additionalProfit = getAdditionalProfit(remainingCapacity, breakSortedItemId);
      setProfitUpperBound(profitUpperBound() + additionalProfit);
    }
  }

  @Override
  public int getNextItemId() {
    return breakItemId;
  }

  @Override
  protected void initPropagator() {
    consumedCapacity = 0;
    breakItemId = KnapsackSearchNode.NO_SELECTION;
    sortedItems = items().clone();
    profitMax = 0;
    for (KnapsackItem item : sortedItems) {
      profitMax = Math.max(profitMax, item.profit);
    }
    ++profitMax;
    final long profitMaxForCompare = profitMax;
    // Stable sort by decreasing efficiency.
    Arrays.sort(sortedItems, new Comparator<KnapsackItem>() {
      @Override
      public int compare(KnapsackItem item1, KnapsackItem item2) {
        return Double.compare(item2.getEfficiency(profitMaxForCompare),
            item1.getEfficiency(profitMaxForCompare));
      }
    });
  }

  @Override
  protected boolean updatePropagator(boolean revert, KnapsackAssignment assignment) {
    if (assignment.isIn) {
      if (revert) {
        consumedCapacity -= items()[assignment.itemId].weight;
      } else {
        consumedCapacity += items()[assignment.itemId].weight;
        if (consumedCapacity > capacity) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  protected void copyCurrentStateToSolutionPropagator(boolean[] solution) {
    long remainingCapacity = capacity - consumedCapacity;
    for (KnapsackItem item : sortedItems) {
      if (!state().isBound(item.id)) {
        if (remainingCapacity >= item.weight) {
          remainingCapacity -= item.weight;
          solution[item.id] = true;
        } else {
          return;
        }
      }
    }
  }

  // Martello-Toth bound U2: integrality is enforced on the break item, i.e. the linear relaxation
  // is done either on the item after the break item (break item out) or on the item before it
  // (break item in).
  private long getAdditionalProfit(long remainingCapacity, int breakItemId) {
    final int afterBreakItemId = breakItemId + 1;
    long additionalProfitWhenNoBreakItem = 0;
    if (afterBreakItemId < sortedItems.length) {
      // As items are sorted by decreasing profit / weight ratio and the current weight is
      // non-zero, the next weight is non-zero too.
      final long nextWeight = sortedItems[afterBreakItemId].weight;
      final long nextProfit = sortedItems[afterBreakItemId].profit;
      additionalProfitWhenNoBreakItem =
          KnapsackMath.upperBoundOfRatio(remainingCapacity, nextProfit, nextWeight);
    }

    final int beforeBreakItemId = breakItemId - 1;
    long additionalProfitWhenBreakItem = 0;
    if (beforeBreakItemId >= 0) {
      final long previousWeight = sortedItems[beforeBreakItemId].weight;
      // previousWeight == 0 means the total capacity is smaller than the weight of the current
      // item, which then cannot be part of a solution of the one dimension problem.
      if (previousWeight != 0) {
        final long previousProfit = sortedItems[beforeBreakItemId].profit;
        final long overusedCapacity = sortedItems[breakItemId].weight - remainingCapacity;
        final long ratio =
            KnapsackMath.upperBoundOfRatio(overusedCapacity, previousProfit, previousWeight);
        additionalProfitWhenBreakItem = sortedItems[breakItemId].profit - ratio;
      }
    }

    final long additionalProfit =
        Math.max(additionalProfitWhenNoBreakItem, additionalProfitWhenBreakItem);
    if (additionalProfit < 0) {
      throw new IllegalStateException(
          "negative additional profit " + additionalProfit + " (negative profits or weights?)");
    }
    return additionalProfit;
  }
}
