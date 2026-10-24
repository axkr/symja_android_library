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

/** Pairs an item with its assignment (in or out of the knapsack); used by search nodes. */
public final class KnapsackAssignment {
  /** The item id. */
  public final int itemId;
  /** True if the item is in the knapsack. */
  public final boolean isIn;

  /** Creates the assignment of <code>itemId</code>. */
  public KnapsackAssignment(int itemId, boolean isIn) {
    this.itemId = itemId;
    this.isIn = isIn;
  }
}
