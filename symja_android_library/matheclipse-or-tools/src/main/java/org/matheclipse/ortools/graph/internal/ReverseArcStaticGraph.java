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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/graph.h (ReverseArcStaticGraph) C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

import java.util.Arrays;

/**
 * A static directed graph in which every arc <code>a</code> in <code>[0, numArcs)</code> has an
 * opposite (reverse) arc with a negative index in <code>[-numArcs, 0)</code>.
 *
 * <p>
 * Arcs are added with {@link #addArc(int, int)}; the graph must then be finalized with
 * {@link #build()}, which sorts the arcs by tail and may therefore renumber them (the permutation
 * is returned). After the build the outgoing arcs of a node are a contiguous range, as are the
 * opposite arcs of its incoming arcs (sorted by head). The method
 * {@link #ooiBegin(int)}/{@link #ooiNext(int, int)}/{@link #directArcLimit(int)} iterate first over
 * the opposite incoming arcs and then over the outgoing arcs of a node, exactly like the C++
 * <code>OutgoingOrOppositeIncomingArcIterator</code>.
 *
 * <p>
 * Internally the arc-indexed arrays have length <code>2 * numArcs</code> and arc <code>a</code> is
 * stored at position <code>a + numArcs</code>.
 */
public final class ReverseArcStaticGraph {

  /** Constant that is never a valid arc. */
  public static final int NIL_ARC = Integer.MAX_VALUE;

  /** Constant that is never a valid node. */
  public static final int NIL_NODE = Integer.MAX_VALUE;

  private int numNodes;
  private int numArcs;
  private boolean built;

  // Data collected before build().
  private int[] addedTails;
  private int[] addedHeads;

  // Data available after build().
  private int[] start;
  private int[] reverseStart;
  private int[] head;
  private int[] opposite;

  /**
   * Creates a graph with <code>numNodes</code> nodes and room for <code>arcCapacity</code> arcs.
   */
  public ReverseArcStaticGraph(int numNodes, int arcCapacity) {
    this.addedTails = new int[Math.max(arcCapacity, 4)];
    this.addedHeads = new int[Math.max(arcCapacity, 4)];
    if (numNodes > 0) {
      addNode(numNodes - 1);
    }
  }

  /** Makes sure that <code>node</code> is a valid node of the graph. */
  public void addNode(int node) {
    if (node < numNodes) {
      return;
    }
    numNodes = node + 1;
  }

  /**
   * Adds an arc from <code>tail</code> to <code>head</code> and returns its index before
   * {@link #build()}.
   */
  public int addArc(int tail, int head) {
    if (built) {
      throw new IllegalStateException("the graph is already built");
    }
    if (tail < 0 || head < 0) {
      throw new IllegalArgumentException("negative node index");
    }
    addNode(tail > head ? tail : head);
    if (numArcs == addedTails.length) {
      int newLength = Math.max(4, 2 * addedTails.length);
      addedTails = Arrays.copyOf(addedTails, newLength);
      addedHeads = Arrays.copyOf(addedHeads, newLength);
    }
    addedTails[numArcs] = tail;
    addedHeads[numArcs] = head;
    return numArcs++;
  }

  /**
   * Finalizes the graph. Returns the arc permutation: the new index of former arc
   * <code>i</code> is <code>permutation[i]</code> if <code>i &lt; permutation.length</code>, or is
   * unchanged otherwise (the returned array is empty when no arc was moved).
   */
  public int[] build() {
    if (built) {
      return new int[0];
    }
    built = true;
    final int n = numNodes;
    final int m = numArcs;
    head = new int[2 * m];
    opposite = new int[2 * m];
    if (n == 0) {
      start = new int[1];
      reverseStart = new int[1];
      addedTails = null;
      addedHeads = null;
      return new int[0];
    }
    int[] permutation = buildStartAndForwardHead();

    // Computes incoming degree of each node.
    reverseStart = new int[n + 1];
    for (int i = 0; i < m; ++i) {
      reverseStart[head[i + m]]++;
    }
    computeCumulativeSum(reverseStart);

    // Computes the reverse arcs of the forward arcs.
    // Note that this sorts the reverse arcs with the same tail by head.
    for (int i = 0; i < m; ++i) {
      opposite[i + m] = reverseStart[head[i + m]]++ - m;
    }

    // Computes in reverseStart the start index of the reverse arcs.
    reverseStart[n] = 0; // Sentinel.
    for (int i = n - 1; i > 0; --i) {
      reverseStart[i] = reverseStart[i - 1] - m;
    }
    reverseStart[0] = -m;

    // Fill reverse arc information.
    for (int i = 0; i < m; ++i) {
      opposite[opposite[i + m] + m] = i;
    }
    for (int node = 0; node < n; ++node) {
      for (int arc = start[node]; arc < start[node + 1]; ++arc) {
        head[opposite[arc + m] + m] = node;
      }
    }
    addedTails = null;
    addedHeads = null;
    return permutation;
  }

  // Computes the cumulative sum of the entries in v (of size numNodes + 1).
  private void computeCumulativeSum(int[] v) {
    int sum = 0;
    for (int i = 0; i < numNodes; ++i) {
      int temp = v[i];
      v[i] = sum;
      sum += temp;
    }
    v[numNodes] = sum; // Sentinel.
  }

  // Reorders the arcs by increasing tail, puts the heads in their final position and computes
  // start[]. Returns the permutation (empty if none is needed).
  private int[] buildStartAndForwardHead() {
    final int n = numNodes;
    final int m = numArcs;
    start = new int[n + 1];
    int lastTailSeen = 0;
    boolean permutationNeeded = false;
    for (int i = 0; i < m; ++i) {
      int tail = addedTails[i];
      if (!permutationNeeded) {
        permutationNeeded = tail < lastTailSeen;
        lastTailSeen = tail;
      }
      start[tail]++;
    }
    computeCumulativeSum(start);

    if (!permutationNeeded) {
      for (int i = 0; i < m; ++i) {
        head[i + m] = addedHeads[i];
      }
      return new int[0];
    }

    // Computes the forward arc permutation. This temporarily alters start[].
    int[] perm = new int[m];
    for (int i = 0; i < m; ++i) {
      perm[i] = start[addedTails[i]]++;
    }
    // Restore in start[i] the index of the first arc with tail >= i.
    for (int i = n - 1; i > 0; --i) {
      start[i] = start[i - 1];
    }
    start[0] = 0;

    // Permutes the heads into their final position.
    for (int i = 0; i < m; ++i) {
      head[perm[i] + m] = addedHeads[i];
    }
    return perm;
  }

  /** Returns true after {@link #build()}. */
  public boolean isBuilt() {
    return built;
  }

  /** The number of nodes. */
  public int numNodes() {
    return numNodes;
  }

  /** The number of (direct) arcs. */
  public int numArcs() {
    return numArcs;
  }

  /** Returns true if <code>node</code> is in <code>[0, numNodes)</code>. */
  public boolean isNodeValid(int node) {
    return node >= 0 && node < numNodes;
  }

  /** Returns true if <code>arc</code> is in <code>[-numArcs, numArcs)</code>. */
  public boolean isArcValid(int arc) {
    return -numArcs <= arc && arc < numArcs;
  }

  /** The head of an arc (direct or reverse). */
  public int head(int arc) {
    return head[arc + numArcs];
  }

  /** The tail of an arc (direct or reverse). */
  public int tail(int arc) {
    return head[opposite[arc + numArcs] + numArcs];
  }

  /** The opposite of an arc. */
  public int oppositeArc(int arc) {
    return opposite[arc + numArcs];
  }

  /** The first outgoing arc of <code>node</code>. */
  public int start(int node) {
    return start[node];
  }

  /** One past the last outgoing arc of <code>node</code>. */
  public int directArcLimit(int node) {
    return start[node + 1];
  }

  /** The first opposite incoming arc of <code>node</code>. */
  public int reverseStart(int node) {
    return reverseStart[node];
  }

  /** One past the last opposite incoming arc of <code>node</code>. */
  public int reverseArcLimit(int node) {
    return reverseStart[node + 1];
  }

  /** The number of outgoing arcs of <code>node</code>. */
  public int outDegree(int node) {
    return start[node + 1] - start[node];
  }

  /** The number of incoming arcs of <code>node</code>. */
  public int inDegree(int node) {
    return reverseStart[node + 1] - reverseStart[node];
  }

  /**
   * The first arc of the outgoing-or-opposite-incoming iteration of <code>node</code>; equal to
   * {@link #directArcLimit(int)} if the node has no incident arc.
   */
  public int ooiBegin(int node) {
    int index = reverseStart[node];
    if (index == reverseStart[node + 1]) {
      index = start[node];
    }
    return index;
  }

  /**
   * The starting point of an outgoing-or-opposite-incoming iteration from <code>arc</code>:
   * {@link #NIL_ARC} is mapped to the end of the iteration.
   */
  public int ooiFrom(int node, int arc) {
    return arc == NIL_ARC ? start[node + 1] : arc;
  }

  /** The arc following <code>arc</code> in the outgoing-or-opposite-incoming iteration. */
  public int ooiNext(int node, int arc) {
    int next = arc + 1;
    if (next == reverseStart[node + 1]) {
      next = start[node];
    }
    return next;
  }
}
