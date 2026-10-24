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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/assignment.h/.cc C++ to Java, API adapted from the OR-Tools Java wrapper, see NOTICE in the module root.

package org.matheclipse.ortools.graph;

import java.util.Arrays;
import org.matheclipse.ortools.graph.internal.CompleteBipartiteGraph;
import org.matheclipse.ortools.graph.internal.LinearSumAssignmentEngine;
import org.matheclipse.ortools.graph.internal.ListGraph;

/**
 * A simple interface to the linear sum assignment problem (<code>SimpleLinearSumAssignment</code>
 * in C++): given N left nodes, N right nodes and a set of left-to-right arcs with integer costs,
 * find a perfect matching (each left node assigned to one right node) of minimum total cost.
 *
 * <p>
 * The problem is solved by a cost-scaling push-relabel algorithm; as in OR-Tools 10.0 a complete
 * bipartite problem (n * n distinct arcs) is solved on a complete bipartite graph, which breaks ties
 * between equal cost arcs by increasing right node instead of by decreasing arc index.
 */
public class LinearSumAssignment {
  private int numNodes;
  private int numArcs;
  private int[] arcTail = new int[8];
  private int[] arcHead = new int[8];
  private long[] arcCost = new long[8];
  private int[] assignmentArcs = new int[0];
  private long optimalCost;

  /** Creates an empty problem; node indices are created lazily. */
  public LinearSumAssignment() {}

  /** Does nothing; kept for compatibility with the OR-Tools Java wrapper. */
  public void delete() {}

  /**
   * Adds an arc from a left node to a right node with a given cost.
   *
   * <ul>
   * <li>Node indices must be non-negative (&gt;= 0). For a perfect matching to exist on n nodes,
   * the values taken by <code>left_node</code> must cover <code>[0, n)</code>, same for
   * <code>right_node</code>.
   * <li>The arc cost can be any integer, negative, positive or zero.
   * <li>After the method finishes, <code>getNumArcs() == the returned arc index + 1</code>.
   * </ul>
   *
   * @return the index of the new arc
   */
  public int addArcWithCost(int left_node, int right_node, long cost) {
    if (left_node < 0 || right_node < 0) {
      throw new IllegalArgumentException("negative node index");
    }
    final int arc = numArcs;
    numNodes = Math.max(numNodes, left_node + 1);
    numNodes = Math.max(numNodes, right_node + 1);
    if (numArcs == arcTail.length) {
      int newLength = 2 * numArcs;
      arcTail = Arrays.copyOf(arcTail, newLength);
      arcHead = Arrays.copyOf(arcHead, newLength);
      arcCost = Arrays.copyOf(arcCost, newLength);
    }
    arcTail[arc] = left_node;
    arcHead[arc] = right_node;
    arcCost[arc] = cost;
    numArcs++;
    return arc;
  }

  /**
   * The number of left nodes, which is the same as the number of right nodes: one more than the
   * largest node index seen so far.
   */
  public int getNumNodes() {
    return numNodes;
  }

  /** The number of arcs. */
  public int getNumArcs() {
    return numArcs;
  }

  /** The left node of an arc. */
  public int getLeftNode(int arc) {
    checkArc(arc);
    return arcTail[arc];
  }

  /** The right node of an arc. */
  public int getRightNode(int arc) {
    checkArc(arc);
    return arcHead[arc];
  }

  /** The cost of an arc. */
  public long getCost(int arc) {
    checkArc(arc);
    return arcCost[arc];
  }

  /**
   * Finds a perfect matching of minimum cost.
   *
   * @return {@link Status#OPTIMAL}, {@link Status#INFEASIBLE} if there is no perfect matching, or
   *         {@link Status#POSSIBLE_OVERFLOW} if some cost magnitude is too large
   */
  public LinearSumAssignment.Status solve() {
    optimalCost = 0;
    assignmentArcs = new int[0];
    if (numNodes == 0) {
      return Status.OPTIMAL;
    }
    // Detect overflows early: the cost of each arc is multiplied internally by (numNodes + 1)
    // without overflow checking. Upstream only checks the positive side; the negative side is
    // checked too because Math.abs() of the scaled cost must not overflow either.
    final long maxSupportedArcCost = Long.MAX_VALUE / (numNodes + 1L);
    for (int arc = 0; arc < numArcs; ++arc) {
      final long unscaledArcCost = arcCost[arc];
      if (unscaledArcCost > maxSupportedArcCost || unscaledArcCost < -maxSupportedArcCost) {
        return Status.POSSIBLE_OVERFLOW;
      }
    }

    // Fast path if the graph is complete.
    final Status completeStatus = solveIfCompleteBipartite();
    if (completeStatus != null) {
      return completeStatus;
    }

    ListGraph graph = new ListGraph(2 * numNodes, numArcs);
    for (int arc = 0; arc < numArcs; ++arc) {
      graph.addArc(arcTail[arc], numNodes + arcHead[arc]);
    }
    LinearSumAssignmentEngine assignment = new LinearSumAssignmentEngine(graph, numNodes);
    for (int arc = 0; arc < numArcs; ++arc) {
      assignment.setArcCost(arc, arcCost[arc]);
    }
    if (!assignment.finalizeSetup()) {
      return Status.POSSIBLE_OVERFLOW;
    }
    if (!assignment.computeAssignment()) {
      return Status.INFEASIBLE;
    }
    optimalCost = assignment.getCost();
    assignmentArcs = new int[numNodes];
    for (int node = 0; node < numNodes; ++node) {
      assignmentArcs[node] = assignment.getAssignmentArc(node);
    }
    return Status.OPTIMAL;
  }

  /**
   * If the arcs form a complete bipartite graph (numNodes * numNodes arcs, no duplicates), solves
   * on a {@link CompleteBipartiteGraph}, whose outgoing arcs are ordered by right node; returns
   * null otherwise.
   */
  private Status solveIfCompleteBipartite() {
    // Quick check: only do something if we have the correct number of arcs.
    if ((long) numArcs != (long) numNodes * numNodes) {
      return null;
    }
    // If there are no duplicate arcs, this is a complete bipartite graph; otherwise fall back to
    // the general case.
    final CompleteBipartiteGraph graph = new CompleteBipartiteGraph(numNodes, numNodes);
    final int[] reverseMapping = new int[numArcs];
    Arrays.fill(reverseMapping, -1);
    final LinearSumAssignmentEngine assignment = new LinearSumAssignmentEngine(graph, numNodes);
    for (int userIndex = 0; userIndex < numArcs; ++userIndex) {
      final int newIndex = graph.getArc(arcTail[userIndex], numNodes + arcHead[userIndex]);
      // Abort if we have duplicate arcs.
      if (reverseMapping[newIndex] != -1) {
        return null;
      }
      reverseMapping[newIndex] = userIndex;
      assignment.setArcCost(newIndex, arcCost[userIndex]);
    }
    if (!assignment.finalizeSetup()) {
      return Status.POSSIBLE_OVERFLOW;
    }
    if (!assignment.computeAssignment()) {
      return Status.INFEASIBLE;
    }
    optimalCost = assignment.getCost();
    assignmentArcs = new int[numNodes];
    for (int node = 0; node < numNodes; ++node) {
      assignmentArcs[node] = reverseMapping[assignment.getAssignmentArc(node)];
    }
    return Status.OPTIMAL;
  }

  /** The cost of a minimum cost assignment; 0 if the last solve didn't return OPTIMAL. */
  public long getOptimalCost() {
    return optimalCost;
  }

  /**
   * The right node assigned to the given left node in the last solution; only valid when
   * {@link #solve()} returned {@link Status#OPTIMAL}. The algorithm is deterministic, so a problem
   * always gets the same solution among the optimal ones.
   */
  public int getRightMate(int left_node) {
    return arcHead[assignedArc(left_node)];
  }

  /**
   * The cost of the arc used for the assignment of the given left node; only valid when
   * {@link #solve()} returned {@link Status#OPTIMAL}.
   */
  public long getAssignmentCost(int left_node) {
    return arcCost[assignedArc(left_node)];
  }

  private int assignedArc(int leftNode) {
    if (leftNode < 0 || leftNode >= assignmentArcs.length) {
      throw new IndexOutOfBoundsException(
          "left node " + leftNode + " has no assignment; solve() must return OPTIMAL first");
    }
    return assignmentArcs[leftNode];
  }

  private void checkArc(int arc) {
    if (arc < 0 || arc >= numArcs) {
      throw new IndexOutOfBoundsException("arc " + arc + " not in [0, " + numArcs + ")");
    }
  }

  /** The status of {@link LinearSumAssignment#solve()}. */
  public enum Status {
    /** The algorithm found a minimum-cost perfect matching. */
    OPTIMAL,
    /** The given problem admits no perfect matching. */
    INFEASIBLE,
    /** Some cost magnitude is too large. */
    POSSIBLE_OVERFLOW;

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
