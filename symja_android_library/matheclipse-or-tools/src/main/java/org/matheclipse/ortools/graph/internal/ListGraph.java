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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/graph.h (ListGraph) C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

import java.util.Arrays;

/**
 * A directed graph stored as linked lists of outgoing arcs. The outgoing arcs of a node are
 * iterated from the most recently added to the oldest:
 *
 * <pre>
 * for (int arc = g.firstOutgoingArc(node); arc != ListGraph.NIL_ARC; arc = g.nextOutgoingArc(arc))
 * </pre>
 */
public final class ListGraph implements OutgoingArcGraph {
  /** Constant that is never a valid arc. */
  public static final int NIL_ARC = OutgoingArcGraph.NIL_ARC;

  private int numNodes;
  private int numArcs;
  private int[] start;
  private int[] next;
  private int[] head;
  private int[] tail;

  /** Creates a graph with <code>numNodes</code> nodes and room for <code>arcCapacity</code> arcs. */
  public ListGraph(int numNodes, int arcCapacity) {
    final int capacity = Math.max(arcCapacity, 4);
    next = new int[capacity];
    head = new int[capacity];
    tail = new int[capacity];
    start = new int[Math.max(numNodes, 4)];
    if (numNodes > 0) {
      addNode(numNodes - 1);
    }
  }

  /** Makes sure that <code>node</code> is a valid node. */
  public void addNode(int node) {
    if (node < numNodes) {
      return;
    }
    if (node >= start.length) {
      start = Arrays.copyOf(start, Math.max(node + 1, 2 * start.length));
    }
    Arrays.fill(start, numNodes, node + 1, NIL_ARC);
    numNodes = node + 1;
  }

  /** Adds an arc and returns its index. */
  public int addArc(int tail, int head) {
    if (tail < 0 || head < 0) {
      throw new IllegalArgumentException("negative node index");
    }
    addNode(tail > head ? tail : head);
    if (numArcs == next.length) {
      int newLength = 2 * numArcs;
      next = Arrays.copyOf(next, newLength);
      this.head = Arrays.copyOf(this.head, newLength);
      this.tail = Arrays.copyOf(this.tail, newLength);
    }
    this.head[numArcs] = head;
    this.tail[numArcs] = tail;
    next[numArcs] = start[tail];
    start[tail] = numArcs;
    return numArcs++;
  }

  @Override
  public int numNodes() {
    return numNodes;
  }

  @Override
  public int numArcs() {
    return numArcs;
  }

  @Override
  public int head(int arc) {
    return head[arc];
  }

  /** The tail of an arc. */
  public int tail(int arc) {
    return tail[arc];
  }

  @Override
  public int firstOutgoingArc(int node) {
    return start[node];
  }

  @Override
  public int nextOutgoingArc(int arc) {
    return next[arc];
  }
}
