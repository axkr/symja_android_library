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

/**
 * Base class for modelling and propagating a constraint given an assignment. When some work has to
 * be done both by the base and the derived class, a protected abstract method ending with
 * <code>Propagator</code> is defined.
 */
public abstract class KnapsackPropagator {
  private KnapsackItem[] items = new KnapsackItem[0];
  private long currentProfit;
  private long profitLowerBound;
  private long profitUpperBound;
  private final KnapsackState state;

  /** Creates a propagator reading the given state. */
  protected KnapsackPropagator(KnapsackState state) {
    this.currentProfit = 0;
    this.profitLowerBound = 0;
    this.profitUpperBound = Long.MAX_VALUE;
    this.state = state;
  }

  /** Initializes the items and then calls {@link #initPropagator()}. */
  public void init(long[] profits, long[] weights) {
    final int numberOfItems = profits.length;
    items = new KnapsackItem[numberOfItems];
    for (int i = 0; i < numberOfItems; ++i) {
      items[i] = new KnapsackItem(i, weights[i], profits[i]);
    }
    currentProfit = 0;
    profitLowerBound = Long.MIN_VALUE;
    profitUpperBound = Long.MAX_VALUE;
    initPropagator();
  }

  /**
   * Updates the data structure and then calls {@link #updatePropagator(boolean, KnapsackAssignment)}.
   * Returns false on failure.
   */
  public boolean update(boolean revert, KnapsackAssignment assignment) {
    if (assignment.isIn) {
      if (revert) {
        currentProfit -= items[assignment.itemId].profit;
      } else {
        currentProfit += items[assignment.itemId].profit;
      }
    }
    return updatePropagator(revert, assignment);
  }

  /** Sets the profit lower and upper bounds, which are constraint specific. */
  public abstract void computeProfitBounds();

  /** The id of the next item to assign, or -1 when all items are bound. */
  public abstract int getNextItemId();

  /** The profit of the bound items in the knapsack. */
  public long currentProfit() {
    return currentProfit;
  }

  /** The lower bound computed by {@link #computeProfitBounds()}. */
  public long profitLowerBound() {
    return profitLowerBound;
  }

  /** The upper bound computed by {@link #computeProfitBounds()}. */
  public long profitUpperBound() {
    return profitUpperBound;
  }

  /**
   * Copies the current state into <code>solution</code>, unbound items set to false. When
   * <code>hasOnePropagator</code> is true the partial solution is completed by
   * {@link #copyCurrentStateToSolutionPropagator(boolean[])}, which is valid because no other
   * constraint has to be checked.
   */
  public void copyCurrentStateToSolution(boolean hasOnePropagator, boolean[] solution) {
    for (KnapsackItem item : items) {
      final int itemId = item.id;
      solution[itemId] = state.isBound(itemId) && state.isIn(itemId);
    }
    if (hasOnePropagator) {
      copyCurrentStateToSolutionPropagator(solution);
    }
  }

  /** Initializes the derived data structures; called after the base initialization. */
  protected abstract void initPropagator();

  /** Updates the derived data structures incrementally. Returns false on failure. */
  protected abstract boolean updatePropagator(boolean revert, KnapsackAssignment assignment);

  /**
   * Copies the unbound items of a better solution than the blind instantiation to false into
   * <code>solution</code>.
   */
  protected abstract void copyCurrentStateToSolutionPropagator(boolean[] solution);

  /** The state read by this propagator. */
  protected KnapsackState state() {
    return state;
  }

  /** The items, indexed by id. */
  protected KnapsackItem[] items() {
    return items;
  }

  /** Sets the profit lower bound. */
  protected void setProfitLowerBound(long profit) {
    profitLowerBound = profit;
  }

  /** Sets the profit upper bound. */
  protected void setProfitUpperBound(long profit) {
    profitUpperBound = profit;
  }
}
