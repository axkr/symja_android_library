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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/max_flow.h/.cc C++ to Java, API adapted from the OR-Tools Java wrapper, see NOTICE in the module root.

package org.matheclipse.ortools.graph;

import java.util.Arrays;
import org.matheclipse.ortools.graph.internal.GenericMaxFlow;
import org.matheclipse.ortools.graph.internal.ReverseArcStaticGraph;

/**
 * A simple maximum flow interface (<code>SimpleMaxFlow</code> in C++).
 *
 * <p>
 * Nodes are created lazily by {@link #addArcWithCapacity(int, int, long)}. {@link #solve(int, int)}
 * builds a static graph with reverse arcs and runs a push-relabel algorithm with global relabelling
 * on it.
 */
public class MaxFlow {
  private int numNodes;
  private int numArcs;
  private int[] arcTail = new int[8];
  private int[] arcHead = new int[8];
  private long[] arcCapacity = new long[8];
  private int[] arcPermutation = new int[0];
  private long[] arcFlow = new long[0];
  private long optimalFlow;
  private GenericMaxFlow underlyingMaxFlow;

  /** Creates an empty problem; node indices are created lazily. */
  public MaxFlow() {}

  /** Does nothing; kept for compatibility with the OR-Tools Java wrapper. */
  public void delete() {}

  /**
   * Adds a directed arc with the given capacity from tail to head.
   *
   * <ul>
   * <li>Node indices and capacity must be non-negative (&gt;= 0).
   * <li>Self-looping and duplicate arcs are supported.
   * <li>After the method finishes, <code>getNumArcs() == the returned arc index + 1</code>.
   * </ul>
   *
   * @return the index of the new arc
   */
  public int addArcWithCapacity(int tail, int head, long capacity) {
    final int arc = numArcs;
    numNodes = Math.max(numNodes, tail + 1);
    numNodes = Math.max(numNodes, head + 1);
    if (numArcs == arcTail.length) {
      int newLength = 2 * numArcs;
      arcTail = Arrays.copyOf(arcTail, newLength);
      arcHead = Arrays.copyOf(arcHead, newLength);
      arcCapacity = Arrays.copyOf(arcCapacity, newLength);
    }
    arcTail[arc] = tail;
    arcHead[arc] = head;
    arcCapacity[arc] = capacity;
    numArcs++;
    return arc;
  }

  /** The number of nodes: one more than the largest node index seen so far. */
  public int getNumNodes() {
    return numNodes;
  }

  /** The number of arcs. */
  public int getNumArcs() {
    return numArcs;
  }

  /** The tail of an arc as given by the user. */
  public int getTail(int arc) {
    checkArc(arc);
    return arcTail[arc];
  }

  /** The head of an arc as given by the user. */
  public int getHead(int arc) {
    checkArc(arc);
    return arcHead[arc];
  }

  /** The capacity of an arc as given by the user. */
  public long getCapacity(int arc) {
    checkArc(arc);
    return arcCapacity[arc];
  }

  /**
   * Changes the capacity of an arc. The next {@link #solve(int, int)} restarts from scratch.
   */
  public void setArcCapacity(int arc, long capacity) {
    checkArc(arc);
    arcCapacity[arc] = capacity;
  }

  /**
   * Finds the maximum flow from <code>source</code> to <code>sink</code>.
   *
   * @return {@link Status#OPTIMAL} (the flow may be 0 when the sink is not reachable),
   *         {@link Status#POSSIBLE_OVERFLOW} if the flow exceeds <code>Long.MAX_VALUE</code> (the
   *         solution then reaches that bound) or {@link Status#BAD_INPUT} if
   *         <code>source == sink</code>, one of them is negative, or an arc has a negative node
   *         or capacity
   */
  public MaxFlow.Status solve(int source, int sink) {
    arcFlow = new long[numArcs];
    underlyingMaxFlow = null;
    optimalFlow = 0;
    if (source == sink || source < 0 || sink < 0) {
      return Status.BAD_INPUT;
    }
    for (int arc = 0; arc < numArcs; ++arc) {
      // Negative values are a precondition violation in C++ (debug check only).
      if (arcTail[arc] < 0 || arcHead[arc] < 0 || arcCapacity[arc] < 0) {
        return Status.BAD_INPUT;
      }
    }
    if (source >= numNodes || sink >= numNodes) {
      return Status.OPTIMAL;
    }
    ReverseArcStaticGraph graph = new ReverseArcStaticGraph(numNodes, numArcs);
    graph.addNode(source);
    graph.addNode(sink);
    for (int arc = 0; arc < numArcs; ++arc) {
      graph.addArc(arcTail[arc], arcHead[arc]);
    }
    arcPermutation = graph.build();
    underlyingMaxFlow = new GenericMaxFlow(graph, source, sink);
    for (int arc = 0; arc < numArcs; ++arc) {
      underlyingMaxFlow.setArcCapacity(permutedArc(arc), arcCapacity[arc]);
    }
    if (underlyingMaxFlow.solve()) {
      optimalFlow = underlyingMaxFlow.getOptimalFlow();
      for (int arc = 0; arc < numArcs; ++arc) {
        arcFlow[arc] = underlyingMaxFlow.flow(permutedArc(arc));
      }
    }
    // Translate the status; NOT_SOLVED does not make sense in the simple API.
    switch (underlyingMaxFlow.status()) {
      case NOT_SOLVED:
        return Status.BAD_RESULT;
      case OPTIMAL:
        return Status.OPTIMAL;
      case INT_OVERFLOW:
        return Status.POSSIBLE_OVERFLOW;
      case BAD_INPUT:
        return Status.BAD_INPUT;
      case BAD_RESULT:
      default:
        return Status.BAD_RESULT;
    }
  }

  /** The maximum flow found by the last {@link #solve(int, int)}. */
  public long getOptimalFlow() {
    return optimalFlow;
  }

  /**
   * The flow on an arc in the last {@link #solve(int, int)}. The algorithm is deterministic, so a
   * problem always gets the same solution among the optimal ones.
   */
  public long getFlow(int arc) {
    checkArc(arc);
    // Zero when the last solve did not compute a flow.
    return arc < arcFlow.length ? arcFlow[arc] : 0;
  }

  /**
   * The nodes reachable from the source by non-saturated arcs; their outgoing arcs form a minimum
   * cut. Only meaningful after {@link #solve(int, int)} returned {@link Status#OPTIMAL}.
   *
   * <p>
   * This method is not exposed by the OR-Tools Java wrapper (only by the C++ API).
   *
   * @return the node indices (empty if no graph was solved)
   */
  public int[] getSourceSideMinCut() {
    if (underlyingMaxFlow == null) {
      return new int[0];
    }
    return underlyingMaxFlow.getSourceSideMinCut();
  }

  /**
   * The nodes that can reach the sink by non-saturated arcs; their incoming arcs form a minimum
   * cut. Only meaningful after {@link #solve(int, int)} returned {@link Status#OPTIMAL}.
   *
   * <p>
   * This method is not exposed by the OR-Tools Java wrapper (only by the C++ API).
   *
   * @return the node indices (empty if no graph was solved)
   */
  public int[] getSinkSideMinCut() {
    if (underlyingMaxFlow == null) {
      return new int[0];
    }
    return underlyingMaxFlow.getSinkSideMinCut();
  }

  private int permutedArc(int arc) {
    return arc < arcPermutation.length ? arcPermutation[arc] : arc;
  }

  private void checkArc(int arc) {
    if (arc < 0 || arc >= numArcs) {
      throw new IndexOutOfBoundsException("arc " + arc + " not in [0, " + numArcs + ")");
    }
  }

  /** The status of {@link MaxFlow#solve(int, int)}. */
  public enum Status {
    /**
     * Solve() was called and found an optimal solution. Note that the optimal flow may be 0 which
     * means that the sink is not reachable from the source.
     */
    OPTIMAL,
    /**
     * There is a flow greater than <code>Long.MAX_VALUE</code>; the solution contains a flow
     * reaching that bound.
     */
    POSSIBLE_OVERFLOW,
    /** The input is inconsistent (bad source or sink). */
    BAD_INPUT,
    /** This should not happen: there was an error in the algorithm. */
    BAD_RESULT;

    /** The value of the C++ enum constant. */
    public final int swigValue() {
      return ordinal();
    }

    /** The constant with the given C++ enum value. */
    public static Status swigToEnum(int swigValue) {
      Status[] values = values();
      if (swigValue >= 0 && swigValue < values.length) {
        return values[swigValue];
      }
      throw new IllegalArgumentException("No enum " + Status.class + " with value " + swigValue);
    }
  }
}
