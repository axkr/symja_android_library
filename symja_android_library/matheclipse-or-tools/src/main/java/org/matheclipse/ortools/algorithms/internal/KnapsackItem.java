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
 * Pairs an item weight with its profit. Items are sorted by decreasing efficiency (profit per unit
 * of weight).
 */
public final class KnapsackItem {
  /** The id of the item in the problem, used to communicate with propagators and state. */
  public final int id;
  /** The weight of the item in one dimension. */
  public final long weight;
  /** The profit of the item. */
  public final long profit;

  /** Creates an item. */
  public KnapsackItem(int id, long weight, long profit) {
    this.id = id;
    this.weight = weight;
    this.profit = profit;
  }

  /**
   * The profit per unit of weight, or <code>profitMax</code> for items of weight zero.
   */
  public double getEfficiency(long profitMax) {
    return (weight > 0) ? (double) profit / (double) weight : (double) profitMax;
  }
}
