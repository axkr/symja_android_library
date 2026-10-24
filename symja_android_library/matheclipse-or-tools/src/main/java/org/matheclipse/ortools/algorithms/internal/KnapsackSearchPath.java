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
 * The path between two nodes of the search tree. The state of <code>to</code> is obtained from
 * the state of <code>from</code> by reverting the decisions from <code>from</code> up to their
 * common ancestor <code>via</code> and applying the decisions from <code>via</code> down to
 * <code>to</code>; consecutive nodes are usually close, so this is cheaper than rebuilding the
 * state from the root.
 */
public final class KnapsackSearchPath {
  private final KnapsackSearchNode from;
  private KnapsackSearchNode via; // Computed in init().
  private final KnapsackSearchNode to;

  /** Creates the path; {@link #init()} computes the common ancestor. */
  public KnapsackSearchPath(KnapsackSearchNode from, KnapsackSearchNode to) {
    this.from = from;
    this.via = null;
    this.to = to;
  }

  /** Computes the common ancestor of <code>from</code> and <code>to</code>. */
  public void init() {
    KnapsackSearchNode nodeFrom = moveUpToDepth(from, to.depth());
    KnapsackSearchNode nodeTo = moveUpToDepth(to, from.depth());
    if (nodeFrom.depth() != nodeTo.depth()) {
      throw new IllegalStateException("search path depth mismatch");
    }
    // Find the common parent.
    while (nodeFrom != nodeTo) {
      nodeFrom = nodeFrom.parent();
      nodeTo = nodeTo.parent();
    }
    via = nodeFrom;
  }

  /** The start node. */
  public KnapsackSearchNode from() {
    return from;
  }

  /** The common ancestor. */
  public KnapsackSearchNode via() {
    return via;
  }

  /** The end node. */
  public KnapsackSearchNode to() {
    return to;
  }

  /** The ancestor of <code>node</code> at the given depth (or the node itself). */
  public KnapsackSearchNode moveUpToDepth(KnapsackSearchNode node, int depth) {
    KnapsackSearchNode currentNode = node;
    while (currentNode.depth() > depth) {
      currentNode = currentNode.parent();
    }
    return currentNode;
  }
}
