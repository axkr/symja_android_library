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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/linear_assignment.h (LinearSumAssignment) C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

import java.util.logging.Logger;

/**
 * Cost-scaling push-relabel algorithm for the linear sum assignment problem (Goldberg and
 * Kennedy, "An efficient cost scaling algorithm for the assignment problem", 1995) on a bipartite
 * graph ({@link ListGraph} or {@link CompleteBipartiteGraph}) whose left nodes are
 * <code>[0, numLeftNodes)</code> and right nodes <code>[numLeftNodes, 2 * numLeftNodes)</code>.
 * The ties between arcs are broken by the order of the outgoing arcs of the graph.
 *
 * <p>
 * Costs are scaled by <code>numLeftNodes + 1</code>. Each refinement divides epsilon by
 * <code>alpha = 5</code>, unmatches all nodes and repeatedly applies the double-push operation to
 * an unmatched left node: it is matched along its arc of minimum partial reduced cost (possibly
 * unmatching the previous mate of the right node) and the price of the right node is lowered by
 * the gap to the second best arc plus epsilon. Prices that fall below a computed lower bound prove
 * that no perfect matching exists. Unmatched left nodes are processed in stack order.
 */
public final class LinearSumAssignmentEngine {
  private static final Logger LOGGER = Logger.getLogger(LinearSumAssignmentEngine.class.getName());

  // The default of the assignment_alpha flag.
  private static final long DEFAULT_ALPHA = 5;
  private static final long MIN_EPSILON = 1;
  private static final int NIL_NODE = Integer.MAX_VALUE;

  private final OutgoingArcGraph graph;
  private final int numLeftNodes;
  // True when each left node has at least one incident arc.
  private boolean incidencePreconditionSatisfied;
  private boolean success;
  // Scaling factor of the arc costs: 1 + numLeftNodes.
  private final long costScalingFactor;
  // Divisor of epsilon at each iteration.
  private long alpha;
  // The current error parameter: the pseudo-flow is epsilon-optimal.
  private long epsilon;
  // A lower bound on the prices of right nodes; a price below proves infeasibility.
  private long priceLowerBound;
  // The bound on the price change of a node during a refinement.
  private long slackRelabelingPrice;
  private long largestScaledCostMagnitude;
  // The number of unmatched left nodes.
  private int totalExcess;
  // Prices of the right nodes (left node prices are implicit).
  private final long[] price;
  // The arc matching each left node, or NIL_ARC.
  private final int[] matchedArc;
  // The left node matched to each right node, or NIL_NODE.
  private final int[] matchedNode;
  private final long[] scaledArcCost;
  // Active (unmatched) left nodes, in stack order.
  private final IntVector activeNodes = new IntVector();

  // The gap computed by the last bestArcAndGap().
  private long bestGap;

  private long pushes;
  private long doublePushes;
  private long relabelings;
  private long refinements;

  /**
   * Creates the solver for the given bipartite graph; the costs must be set with
   * {@link #setArcCost(int, long)} for all arcs.
   */
  public LinearSumAssignmentEngine(OutgoingArcGraph graph, int numLeftNodes) {
    this.graph = graph;
    this.numLeftNodes = numLeftNodes;
    this.costScalingFactor = 1 + (long) numLeftNodes;
    this.alpha = DEFAULT_ALPHA;
    this.price = new long[2 * numLeftNodes];
    this.matchedArc = new int[numLeftNodes];
    this.matchedNode = new int[2 * numLeftNodes];
    this.scaledArcCost = new long[graph.numArcs()];
  }

  /** Sets the divisor of epsilon at each iteration (default 5). */
  public void setCostScalingDivisor(long factor) {
    alpha = factor;
  }

  /** The head of an arc. */
  public int head(int arc) {
    return graph.head(arc);
  }

  /** The original cost of an arc. */
  public long arcCost(int arc) {
    return scaledArcCost[arc] / costScalingFactor;
  }

  /** Sets the cost of an arc (the cost is scaled internally without overflow check). */
  public void setArcCost(int arc, long cost) {
    cost *= costScalingFactor;
    final long costMagnitude = Math.abs(cost);
    largestScaledCostMagnitude = Math.max(largestScaledCostMagnitude, costMagnitude);
    scaledArcCost[arc] = cost;
  }

  /** The number of nodes (left and right). */
  public int numNodes() {
    return graph.numNodes();
  }

  /** The number of left nodes. */
  public int numLeftNodes() {
    return numLeftNodes;
  }

  /** The arc matching the given left node in the last solution. */
  public int getAssignmentArc(int leftNode) {
    return matchedArc[leftNode];
  }

  /** The cost of the arc matching the given left node. */
  public long getAssignmentCost(int node) {
    return arcCost(getAssignmentArc(node));
  }

  /** The right node matched to the given left node. */
  public int getMate(int leftNode) {
    return head(getAssignmentArc(leftNode));
  }

  /** Statistics of the run. */
  public String statsString() {
    return refinements + " refinements; " + relabelings + " relabelings; " + doublePushes
        + " double pushes; " + pushes + " pushes";
  }

  // An upper bound on the change of a node price during a refinement from oldEpsilon to
  // newEpsilon; sets inRange[0] to false if it is not representable.
  private long priceChangeBound(long oldEpsilon, long newEpsilon, boolean[] inRange) {
    final long n = graph.numNodes();
    final double result =
        (double) Math.max(1L, n / 2 - 1) * ((double) oldEpsilon + (double) newEpsilon);
    final double limit = (double) Long.MAX_VALUE;
    if (result > limit) {
      if (inRange != null) {
        inRange[0] = false;
      }
      return Long.MAX_VALUE;
    }
    return (long) result;
  }

  private long newEpsilon(long currentEpsilon) {
    return Math.max(currentEpsilon / alpha, MIN_EPSILON);
  }

  private boolean updateEpsilon() {
    final long newEpsilon = newEpsilon(epsilon);
    slackRelabelingPrice = priceChangeBound(epsilon, newEpsilon, null);
    epsilon = newEpsilon;
    return true;
  }

  private boolean isActive(int leftNode) {
    return matchedArc[leftNode] == OutgoingArcGraph.NIL_ARC;
  }

  private boolean isActiveForDebugging(int node) {
    if (node < numLeftNodes) {
      return isActive(node);
    }
    return matchedNode[node] == NIL_NODE;
  }

  private void initializeActiveNodeContainer() {
    for (int node = 0; node < numLeftNodes; ++node) {
      if (isActive(node)) {
        activeNodes.add(node);
      }
    }
  }

  // Unmatches all the nodes: every left node gets an excess of one.
  private void saturateNegativeArcs() {
    totalExcess = 0;
    for (int node = 0; node < numLeftNodes; ++node) {
      if (isActive(node)) {
        totalExcess += 1;
      } else {
        totalExcess += 1;
        final int mate = getMate(node);
        matchedArc[node] = OutgoingArcGraph.NIL_ARC;
        matchedNode[mate] = NIL_NODE;
      }
    }
  }

  // Matches source along its best arc and relabels the new mate. Returns false if the new price
  // proves infeasibility.
  private boolean doublePush(int source) {
    final int bestArc = bestArcAndGap(source);
    if (bestArc == OutgoingArcGraph.NIL_ARC) {
      return false;
    }
    final int newMate = head(bestArc);
    final int toUnmatch = matchedNode[newMate];
    if (toUnmatch != NIL_NODE) {
      // Unmatch the previous mate of newMate: a double push.
      matchedArc[toUnmatch] = OutgoingArcGraph.NIL_ARC;
      activeNodes.add(toUnmatch);
      doublePushes += 1;
    } else {
      // newMate was unmatched: the total excess decreases.
      totalExcess -= 1;
      pushes += 1;
    }
    matchedArc[source] = bestArc;
    matchedNode[newMate] = source;
    relabelings += 1;
    final long newPrice = price[newMate] - bestGap - epsilon;
    price[newMate] = newPrice;
    return newPrice >= priceLowerBound;
  }

  private boolean refine() {
    saturateNegativeArcs();
    initializeActiveNodeContainer();
    while (totalExcess > 0) {
      final int node = activeNodes.popBack();
      if (!doublePush(node)) {
        if (refinements > 0) {
          LOGGER.severe("Infeasibility detection triggered after first iteration found "
              + "a feasible assignment!");
        }
        return false;
      }
    }
    refinements += 1;
    return true;
  }

  // The arc of minimum partial reduced cost leaving leftNode; stores in bestGap the difference to
  // the second minimum (capped).
  private int bestArcAndGap(int leftNode) {
    int arc = graph.firstOutgoingArc(leftNode);
    int bestArc = arc;
    long minPartialReducedCost = partialReducedCost(bestArc);
    // The best arc is the only admissible one when the second best is at least this much worse.
    final long maxGap = slackRelabelingPrice - epsilon;
    long secondMinPartialReducedCost = minPartialReducedCost + maxGap;
    for (arc = graph.nextOutgoingArc(arc); arc != OutgoingArcGraph.NIL_ARC; arc =
        graph.nextOutgoingArc(arc)) {
      final long partialReducedCost = partialReducedCost(arc);
      if (partialReducedCost < secondMinPartialReducedCost) {
        if (partialReducedCost < minPartialReducedCost) {
          bestArc = arc;
          secondMinPartialReducedCost = minPartialReducedCost;
          minPartialReducedCost = partialReducedCost;
        } else {
          secondMinPartialReducedCost = partialReducedCost;
        }
      }
    }
    bestGap = Math.min(secondMinPartialReducedCost - minPartialReducedCost, maxGap);
    return bestArc;
  }

  // The implicit price of a left node: minus the minimum partial reduced cost of its unmatched
  // arcs.
  private long implicitPrice(int leftNode) {
    int arc = graph.firstOutgoingArc(leftNode);
    int bestArc = arc;
    if (bestArc == matchedArc[leftNode]) {
      arc = graph.nextOutgoingArc(arc);
      if (arc != OutgoingArcGraph.NIL_ARC) {
        bestArc = arc;
      }
    }
    long minPartialReducedCost = partialReducedCost(bestArc);
    if (arc == OutgoingArcGraph.NIL_ARC) {
      return -(minPartialReducedCost + slackRelabelingPrice);
    }
    for (arc = graph.nextOutgoingArc(arc); arc != OutgoingArcGraph.NIL_ARC; arc =
        graph.nextOutgoingArc(arc)) {
      if (arc != matchedArc[leftNode]) {
        final long partialReducedCost = partialReducedCost(arc);
        if (partialReducedCost < minPartialReducedCost) {
          minPartialReducedCost = partialReducedCost;
        }
      }
    }
    return -minPartialReducedCost;
  }

  private long partialReducedCost(int arc) {
    return scaledArcCost[arc] - price[head(arc)];
  }

  // Returns true if all the nodes are matched.
  private boolean allMatched() {
    for (int node = 0; node < graph.numNodes(); ++node) {
      if (isActiveForDebugging(node)) {
        return false;
      }
    }
    return true;
  }

  /** Returns true if the current matching and prices are epsilon-optimal (a debug check). */
  public boolean epsilonOptimal() {
    for (int leftNode = 0; leftNode < numLeftNodes; ++leftNode) {
      final long leftNodePrice = implicitPrice(leftNode);
      for (int arc = graph.firstOutgoingArc(leftNode); arc != OutgoingArcGraph.NIL_ARC; arc =
          graph.nextOutgoingArc(arc)) {
        final long reducedCost = leftNodePrice + partialReducedCost(arc);
        if (matchedArc[leftNode] == arc) {
          if (reducedCost > epsilon) {
            return false;
          }
        } else {
          if (reducedCost < 0) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Completes the setup: initial epsilon, unmatched nodes, zero prices and the price lower bound.
   * Returns false if the price bounds are not representable, in which case arithmetic overflow
   * cannot be ruled out.
   */
  public boolean finalizeSetup() {
    incidencePreconditionSatisfied = true;
    epsilon = Math.max(largestScaledCostMagnitude, MIN_EPSILON + 1);
    for (int node = 0; node < numLeftNodes; ++node) {
      matchedArc[node] = OutgoingArcGraph.NIL_ARC;
      if (graph.firstOutgoingArc(node) == OutgoingArcGraph.NIL_ARC) {
        incidencePreconditionSatisfied = false;
      }
    }
    for (int node = numLeftNodes; node < graph.numNodes(); ++node) {
      price[node] = 0;
      matchedNode[node] = NIL_NODE;
    }
    boolean[] inRange = {true};
    double doublePriceLowerBound = 0.0;
    long newErrorParameter;
    long oldErrorParameter = epsilon;
    do {
      newErrorParameter = newEpsilon(oldErrorParameter);
      doublePriceLowerBound -=
          2.0 * (double) priceChangeBound(oldErrorParameter, newErrorParameter, inRange);
      oldErrorParameter = newErrorParameter;
    } while (newErrorParameter != MIN_EPSILON);
    final double limit = -(double) Long.MAX_VALUE;
    if (doublePriceLowerBound < limit) {
      inRange[0] = false;
      priceLowerBound = -Long.MAX_VALUE;
    } else {
      priceLowerBound = (long) doublePriceLowerBound;
    }
    if (!inRange[0]) {
      LOGGER.warning("Price change bound exceeds range of representable "
          + "costs; arithmetic overflow is not ruled out and "
          + "infeasibility might go undetected.");
    }
    return inRange[0];
  }

  /** Computes an optimal assignment; returns false if there is no perfect matching. */
  public boolean computeAssignment() {
    boolean ok = graph.numNodes() == 2 * numLeftNodes;
    if (!ok) {
      return false;
    }
    finalizeSetup();
    ok = ok && incidencePreconditionSatisfied;
    assert !ok || epsilonOptimal();
    while (ok && epsilon > MIN_EPSILON) {
      ok = ok && updateEpsilon();
      ok = ok && refine();
      assert !ok || epsilonOptimal();
      assert !ok || allMatched();
    }
    success = ok;
    return ok;
  }

  /** The cost of the optimal assignment. */
  public long getCost() {
    long cost = 0;
    for (int node = 0; node < numLeftNodes; ++node) {
      cost += getAssignmentCost(node);
    }
    return cost;
  }

  /** Returns true if the last {@link #computeAssignment()} succeeded. */
  public boolean success() {
    return success;
  }
}
