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
// Modified for the Symja matheclipse-or-tools module: ported from the CompleteBipartiteGraph
// class of ortools/graph/graph.h (OR-Tools 10.0) C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

/**
 * The complete bipartite graph from the left nodes <code>[0, leftNodes)</code> to the right nodes
 * <code>[leftNodes, leftNodes + rightNodes)</code>, without storage: the arc from
 * <code>left</code> to <code>right</code> is <code>left * rightNodes + (right - leftNodes)</code>.
 * The outgoing arcs of a left node are iterated by increasing head; right nodes have none.
 */
public final class CompleteBipartiteGraph implements OutgoingArcGraph {
  private final int leftNodes;
  private final int rightNodes;

  /**
   * Creates the graph; <code>leftNodes * rightNodes</code> must fit in an <code>int</code>.
   */
  public CompleteBipartiteGraph(int leftNodes, int rightNodes) {
    if (leftNodes < 0 || rightNodes < 0
        || (long) leftNodes * (long) rightNodes >= Integer.MAX_VALUE) {
      throw new IllegalArgumentException(
          "invalid complete bipartite graph " + leftNodes + "x" + rightNodes);
    }
    this.leftNodes = leftNodes;
    this.rightNodes = rightNodes;
  }

  @Override
  public int numNodes() {
    return leftNodes + rightNodes;
  }

  @Override
  public int numArcs() {
    return leftNodes * rightNodes;
  }

  /** The arc from <code>leftNode</code> to <code>rightNode</code> (a node index &gt;= leftNodes). */
  public int getArc(int leftNode, int rightNode) {
    return leftNode * rightNodes + (rightNode - leftNodes);
  }

  @Override
  public int head(int arc) {
    return leftNodes + arc % rightNodes;
  }

  /** The tail of an arc. */
  public int tail(int arc) {
    return arc / rightNodes;
  }

  @Override
  public int firstOutgoingArc(int node) {
    return node < leftNodes && rightNodes > 0 ? node * rightNodes : NIL_ARC;
  }

  @Override
  public int nextOutgoingArc(int arc) {
    final int next = arc + 1;
    return next % rightNodes == 0 ? NIL_ARC : next;
  }
}
