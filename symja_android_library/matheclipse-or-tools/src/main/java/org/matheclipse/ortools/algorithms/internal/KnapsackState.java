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
 * A partial solution of the knapsack problem: for each item whether it is bound (decided) and,
 * if so, whether it is in the knapsack.
 */
public final class KnapsackState {
  private boolean[] isBound = new boolean[0];
  private boolean[] isIn = new boolean[0];

  /** Initializes the state with <code>numberOfItems</code> unbound items. */
  public void init(int numberOfItems) {
    isBound = new boolean[numberOfItems];
    isIn = new boolean[numberOfItems];
  }

  /**
   * Applies or reverts a decision. Returns false when the decision is inconsistent with an already
   * bound item.
   */
  public boolean updateState(boolean revert, KnapsackAssignment assignment) {
    if (revert) {
      isBound[assignment.itemId] = false;
    } else {
      if (isBound[assignment.itemId] && isIn[assignment.itemId] != assignment.isIn) {
        return false;
      }
      isBound[assignment.itemId] = true;
      isIn[assignment.itemId] = assignment.isIn;
    }
    return true;
  }

  /** The number of items. */
  public int getNumberOfItems() {
    return isBound.length;
  }

  /** Returns true if the item is decided. */
  public boolean isBound(int id) {
    return isBound[id];
  }

  /** Returns true if the item is in the knapsack (only meaningful when bound). */
  public boolean isIn(int id) {
    return isIn[id];
  }
}
