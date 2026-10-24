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
 * A decision in the search tree: a pointer to the parent node and an assignment. The partial
 * solution is not stored; it is rebuilt by walking the tree (see {@link KnapsackSearchPath}).
 */
public final class KnapsackSearchNode {
  static final int NO_SELECTION = -1;

  // Used to navigate efficiently through the search tree.
  private final int depth;
  private final KnapsackSearchNode parent;
  private final KnapsackAssignment assignment;

  // Used to sort the search nodes in the priority queue and to stop the search when optimality is
  // proven.
  private long currentProfit;
  private long profitUpperBound;

  // Avoids a scan to find the next item to select; computed with the upper bound.
  private int nextItemId;

  /** Creates a node below <code>parent</code> (null for the root). */
  public KnapsackSearchNode(KnapsackSearchNode parent, KnapsackAssignment assignment) {
    this.depth = (parent == null) ? 0 : parent.depth() + 1;
    this.parent = parent;
    this.assignment = assignment;
    this.currentProfit = 0;
    this.profitUpperBound = Long.MAX_VALUE;
    this.nextItemId = NO_SELECTION;
  }

  /** The depth in the tree (0 for the root). */
  public int depth() {
    return depth;
  }

  /** The parent node, null for the root. */
  public KnapsackSearchNode parent() {
    return parent;
  }

  /** The decision of this node. */
  public KnapsackAssignment assignment() {
    return assignment;
  }

  /** The profit of the partial solution. */
  public long currentProfit() {
    return currentProfit;
  }

  /** Sets the profit of the partial solution. */
  public void setCurrentProfit(long profit) {
    currentProfit = profit;
  }

  /** An upper bound of the profit below this node. */
  public long profitUpperBound() {
    return profitUpperBound;
  }

  /** Sets the profit upper bound. */
  public void setProfitUpperBound(long profit) {
    profitUpperBound = profit;
  }

  /** The next item to assign below this node, or -1. */
  public int nextItemId() {
    return nextItemId;
  }

  /** Sets the next item to assign. */
  public void setNextItemId(int id) {
    nextItemId = id;
  }
}
