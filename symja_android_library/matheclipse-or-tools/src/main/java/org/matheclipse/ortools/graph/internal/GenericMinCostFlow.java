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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/min_cost_flow.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

import static org.matheclipse.ortools.graph.internal.SaturatedArithmetic.capAdd;
import static org.matheclipse.ortools.graph.internal.SaturatedArithmetic.capProd;
import static org.matheclipse.ortools.graph.internal.SaturatedArithmetic.capSub;

import java.util.Arrays;
import java.util.logging.Logger;
import org.matheclipse.ortools.graph.MinCostFlowBase.Status;

/**
 * Cost-scaling push-relabel algorithm for the minimum cost flow problem (Goldberg 1997, "An
 * Efficient Implementation of a Scaling Minimum-Cost Flow Algorithm") on a
 * {@link ReverseArcStaticGraph}.
 *
 * <p>
 * The algorithm handles pseudo-flows and refines epsilon-optimal pseudo-flows: costs are scaled by
 * <code>numNodes + 1</code>, epsilon starts at the largest scaled cost and is divided by
 * <code>alpha = 5</code> at each refinement until it is 1, at which point the 1-optimal flow of the
 * scaled problem is optimal for the original costs. Each refinement saturates the admissible arcs
 * (positive residual capacity, negative reduced cost) and discharges the active nodes (positive
 * excess) with the push look-ahead heuristic and relabelling.
 */
public final class GenericMinCostFlow {
  private static final Logger LOGGER = Logger.getLogger(GenericMinCostFlow.class.getName());

  // The default of the min_cost_flow_alpha flag.
  private static final long DEFAULT_ALPHA = 5;
  // The default of the min_cost_flow_check_result flag.
  private static final boolean CHECK_RESULT = true;

  private final ReverseArcStaticGraph graph;
  private final int numNodes;
  private final int numArcs;

  // Supply (> 0) or demand (< 0) of each node.
  private final long[] nodeExcess;
  // The potential (price) of each node.
  private final long[] nodePotential;
  // residual[arc + numArcs] = capacity[arc] - flow[arc] for direct and reverse arcs.
  private final long[] residualArcCapacity;
  // The first admissible arc of each node.
  private final int[] firstAdmissibleArc;
  // Stack of active nodes.
  private final IntVector activeNodes = new IntVector();

  // Tolerance for optimality.
  private long epsilon = 0;
  // Factor by which epsilon is divided at each refinement.
  private final long alpha;
  // Scaling factor of the costs.
  private long costScalingFactor = 1;
  // The node potentials should stay >= this; crossing it means BAD_COST_RANGE.
  private long overflowThreshold;
  // Scaled unit cost of each arc.
  private final long[] scaledArcUnitCost;

  private Status status = Status.NOT_SOLVED;

  // The initial excesses (supplies), used by the max flow feasibility checker.
  private final long[] initialNodeExcess;

  // Number of relabel() since the last updatePrices().
  private int numRelabelsSinceLastPriceUpdate = 0;
  private boolean feasibilityChecked = false;
  private boolean usePriceUpdate = false;
  private boolean checkFeasibility = true;
  private boolean scalePrices = true;

  /** Initializes a min cost flow instance on a built graph. */
  public GenericMinCostFlow(ReverseArcStaticGraph graph) {
    if (!graph.isBuilt()) {
      throw new IllegalStateException("the graph must be built");
    }
    this.graph = graph;
    this.numNodes = graph.numNodes();
    this.numArcs = graph.numArcs();
    this.alpha = DEFAULT_ALPHA;
    firstAdmissibleArc = new int[numNodes];
    Arrays.fill(firstAdmissibleArc, ReverseArcStaticGraph.NIL_ARC);
    nodePotential = new long[numNodes];
    nodeExcess = new long[numNodes];
    initialNodeExcess = new long[numNodes];
    residualArcCapacity = new long[2 * numArcs];
    scaledArcUnitCost = new long[2 * numArcs];
  }

  /** The graph of this instance. */
  public ReverseArcStaticGraph graph() {
    return graph;
  }

  /** The status of the last call to {@link #solve()}. */
  public Status status() {
    return status;
  }

  /** Sets the supply of a node; a demand is a negative supply. */
  public void setNodeSupply(int node, long supply) {
    nodeExcess[node] = supply;
    initialNodeExcess[node] = supply;
    status = Status.NOT_SOLVED;
    feasibilityChecked = false;
  }

  /** Sets the unit cost of a direct arc. */
  public void setArcUnitCost(int arc, long unitCost) {
    scaledArcUnitCost[arc + numArcs] = unitCost;
    scaledArcUnitCost[opposite(arc) + numArcs] = -unitCost;
    status = Status.NOT_SOLVED;
    feasibilityChecked = false;
  }

  /** Sets the capacity of a direct arc, reducing its flow if needed. */
  public void setArcCapacity(int arc, long newCapacity) {
    final long freeCapacity = residualArcCapacity[arc + numArcs];
    final long capacityDelta = newCapacity - capacity(arc);
    if (capacityDelta == 0) {
      return; // Nothing to do.
    }
    status = Status.NOT_SOLVED;
    feasibilityChecked = false;
    final long newAvailability = freeCapacity + capacityDelta;
    if (newAvailability >= 0) {
      // Either the capacity grows, or it shrinks by less than the free capacity.
      residualArcCapacity[arc + numArcs] = newAvailability;
    } else {
      // Reduce the flow on the arc and update the excesses accordingly.
      final long flow = residualArcCapacity[opposite(arc) + numArcs];
      final long flowExcess = flow - newCapacity;
      residualArcCapacity[arc + numArcs] = 0;
      residualArcCapacity[opposite(arc) + numArcs] = newCapacity;
      nodeExcess[graph.tail(arc)] += flowExcess;
      nodeExcess[graph.head(arc)] -= flowExcess;
    }
  }

  /**
   * Whether to check the feasibility of the problem with a max flow before solving it (default
   * true). Without the check an infeasible problem may loop.
   */
  public void setCheckFeasibility(boolean value) {
    checkFeasibility = value;
  }

  /** Whether to use the price update heuristic (default false). */
  public void setUseUpdatePrices(boolean value) {
    usePriceUpdate = value;
  }

  /** Whether to scale the costs by <code>numNodes + 1</code> (default true). */
  public void setPriceScaling(boolean value) {
    scalePrices = value;
  }

  /** Returns true if {@link #solve()} found an optimal flow. */
  public boolean solve() {
    if (!checkInputConsistency()) {
      return false;
    }
    if (checkFeasibility && !checkFeasibility()) {
      status = Status.INFEASIBLE;
      return false;
    }

    status = Status.NOT_SOLVED;
    Arrays.fill(nodePotential, 0);

    resetFirstAdmissibleArcs();
    if (!scaleCosts()) {
      return false;
    }
    if (!optimize()) {
      return false;
    }
    status = Status.OPTIMAL;

    if (CHECK_RESULT && !checkResult()) {
      status = Status.BAD_RESULT;
      unscaleCosts();
      return false;
    }
    unscaleCosts();
    return true;
  }

  /**
   * The cost of the flow found by the last successful {@link #solve()} (0 otherwise). It is capped
   * at <code>Long.MAX_VALUE</code> if it overflows.
   */
  public long getOptimalCost() {
    if (status != Status.OPTIMAL) {
      return 0;
    }
    long totalFlowCost = 0;
    for (int arc = 0; arc < numArcs; ++arc) {
      final long flowOnArc = residualArcCapacity[opposite(arc) + numArcs];
      final long flowCost = capProd(scaledArcUnitCost[arc + numArcs], flowOnArc);
      if (flowCost == Long.MAX_VALUE || flowCost == Long.MIN_VALUE) {
        return Long.MAX_VALUE;
      }
      totalFlowCost = capAdd(flowCost, totalFlowCost);
      if (totalFlowCost == Long.MAX_VALUE || totalFlowCost == Long.MIN_VALUE) {
        return Long.MAX_VALUE;
      }
    }
    return totalFlowCost;
  }

  /** The flow on an arc. */
  public long flow(int arc) {
    if (isArcDirect(arc)) {
      return residualArcCapacity[opposite(arc) + numArcs];
    }
    return -residualArcCapacity[arc + numArcs];
  }

  /** The capacity of an arc (zero for reverse arcs); it may have been capped. */
  public long capacity(int arc) {
    if (isArcDirect(arc)) {
      return residualArcCapacity[arc + numArcs] + residualArcCapacity[opposite(arc) + numArcs];
    }
    return 0;
  }

  /** The unscaled unit cost of an arc. */
  public long unitCost(int arc) {
    return scaledArcUnitCost[arc + numArcs];
  }

  /** The supply of a node. */
  public long supply(int node) {
    return nodeExcess[node];
  }

  // Checks that the supplies balance the demands and that no node can overflow.
  private boolean checkInputConsistency() {
    final long maxFlow = Long.MAX_VALUE;

    long sumOfSupplies = 0;
    long sumOfDemands = 0;
    for (int node = 0; node < numNodes; ++node) {
      final long excess = nodeExcess[node];
      if (excess > 0) {
        sumOfSupplies = capAdd(sumOfSupplies, excess);
        if (sumOfSupplies >= maxFlow) {
          status = Status.BAD_CAPACITY_RANGE;
          LOGGER.severe("Input consistency error: sum of supplies overflow");
          return false;
        }
      } else if (excess < 0) {
        sumOfDemands = capAdd(sumOfDemands, -excess);
        if (sumOfDemands >= maxFlow) {
          status = Status.BAD_CAPACITY_RANGE;
          LOGGER.severe("Input consistency error: sum of demands overflow");
          return false;
        }
      }
    }
    if (sumOfSupplies != sumOfDemands) {
      status = Status.UNBALANCED;
      LOGGER.severe("Input consistency error: unbalanced problem");
      return false;
    }

    long[] maxNodeExcess = Arrays.copyOf(nodeExcess, numNodes);
    long[] minNodeExcess = Arrays.copyOf(nodeExcess, numNodes);
    for (int arc = 0; arc < numArcs; ++arc) {
      final long capacity = residualArcCapacity[arc + numArcs];
      if (capacity < 0) {
        throw new IllegalStateException("negative capacity on arc " + arc);
      }
      if (capacity == 0) {
        continue;
      }
      final int tail = graph.tail(arc);
      final int head = graph.head(arc);
      minNodeExcess[tail] = capSub(minNodeExcess[tail], capacity);
      maxNodeExcess[head] = capAdd(maxNodeExcess[head], capacity);
    }

    for (int node = 0; node < numNodes; ++node) {
      if (maxNodeExcess[node] >= maxFlow || minNodeExcess[node] <= -maxFlow) {
        // Try to fix it: some users use arcs with "infinite" capacity out of a source or into a
        // sink, so the capacities are capped.
        if (maxNodeExcess[node] < Long.MAX_VALUE) {
          // There is no point having an outgoing arc with more than this.
          final long upperBound = Math.max(0, maxNodeExcess[node]);

          // Adjust and recompute minNodeExcess[node].
          minNodeExcess[node] = nodeExcess[node];
          for (int arc = graph.start(node); arc < graph.directArcLimit(node); ++arc) {
            residualArcCapacity[arc + numArcs] =
                Math.min(residualArcCapacity[arc + numArcs], upperBound);
            minNodeExcess[node] = capSub(minNodeExcess[node], residualArcCapacity[arc + numArcs]);
          }
          if (minNodeExcess[node] > -maxFlow) {
            continue;
          }
        }
        if (minNodeExcess[node] > -Long.MAX_VALUE) {
          // There is no point having an incoming arc with more than this.
          final long upperBound = Math.max(0, -minNodeExcess[node]);

          // Adjust and recompute maxNodeExcess[node].
          maxNodeExcess[node] = nodeExcess[node];
          for (int oppositeArc = graph.reverseStart(node); oppositeArc < graph
              .reverseArcLimit(node); ++oppositeArc) {
            final int arc = opposite(oppositeArc);
            residualArcCapacity[arc + numArcs] =
                Math.min(residualArcCapacity[arc + numArcs], upperBound);
            maxNodeExcess[node] = capAdd(maxNodeExcess[node], residualArcCapacity[arc + numArcs]);
          }
          if (maxNodeExcess[node] < maxFlow) {
            continue;
          }
        }

        status = Status.BAD_CAPACITY_RANGE;
        LOGGER.severe("Maximum in or out flow of node + excess " + node
            + " overflow the FlowQuantity type (int64_t).");
        return false;
      }
    }
    return true;
  }

  // Checks that every arc with residual capacity has a reduced cost >= -epsilon and that all
  // excesses are zero.
  private boolean checkResult() {
    for (int node = 0; node < numNodes; ++node) {
      if (nodeExcess[node] != 0) {
        LOGGER.severe("node_excess_[" + node + "] != 0");
        return false;
      }
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
        boolean ok = true;
        if (residualArcCapacity[arc + numArcs] < 0) {
          LOGGER.severe("residual_arc_capacity_[" + arc + "] < 0");
          ok = false;
        }
        if (residualArcCapacity[arc + numArcs] > 0 && reducedCost(arc) < -epsilon) {
          LOGGER.severe("residual_arc_capacity_[" + arc + "] > 0 && ReducedCost(" + arc + ") < "
              + (-epsilon) + ". (epsilon_ = " + epsilon + ").");
          ok = false;
        }
        if (!ok) {
          return false;
        }
      }
    }
    return true;
  }

  // Relabel precondition: the excess is non-negative and the node has no admissible arc.
  private boolean checkRelabelPrecondition(int node) {
    if (nodeExcess[node] < 0) {
      return false;
    }
    final int limit = graph.directArcLimit(node);
    for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
      if (isAdmissible(arc)) {
        return false;
      }
    }
    return true;
  }

  // Checks with a max flow on an augmented graph (a super source linked to the supply nodes and
  // the demand nodes linked to a super sink) that all supplies can be sent.
  private boolean checkFeasibility() {
    feasibilityChecked = false;
    int numExtraArcs = 0;
    for (int node = 0; node < numNodes; ++node) {
      if (initialNodeExcess[node] != 0) {
        ++numExtraArcs;
      }
    }
    final int numNodesInMaxFlow = numNodes + 2;
    final int numArcsInMaxFlow = numArcs + numExtraArcs;
    final int source = numNodesInMaxFlow - 2;
    final int sink = numNodesInMaxFlow - 1;
    ReverseArcStaticGraph checkerGraph =
        new ReverseArcStaticGraph(numNodesInMaxFlow, numArcsInMaxFlow);
    long[] capacities = new long[numArcsInMaxFlow];
    for (int arc = 0; arc < numArcs; ++arc) {
      final int newArc = checkerGraph.addArc(graph.tail(arc), graph.head(arc));
      capacities[newArc] = capacity(arc);
    }
    long totalDemand = 0;
    long totalSupply = 0;
    for (int node = 0; node < numNodes; ++node) {
      final long supply = initialNodeExcess[node];
      if (supply > 0) {
        final int newArc = checkerGraph.addArc(source, node);
        capacities[newArc] = supply;
        totalSupply += supply;
      } else if (supply < 0) {
        final int newArc = checkerGraph.addArc(node, sink);
        capacities[newArc] = -supply;
        totalDemand -= supply;
      }
    }
    if (totalSupply != totalDemand) {
      LOGGER.severe("total_supply(" + totalSupply + ") != total_demand(" + totalDemand + ").");
      return false;
    }
    int[] permutation = checkerGraph.build();
    GenericMaxFlow checker = new GenericMaxFlow(checkerGraph, source, sink);
    for (int arc = 0; arc < numArcsInMaxFlow; ++arc) {
      checker.setArcCapacity(arc < permutation.length ? permutation[arc] : arc, capacities[arc]);
    }
    if (!checker.solve()) {
      LOGGER.severe("Max flow could not be computed.");
      return false;
    }
    final long optimalMaxFlow = checker.getOptimalFlow();
    feasibilityChecked = true;
    return optimalMaxFlow == totalSupply;
  }

  private boolean isAdmissible(int arc) {
    return fastIsAdmissible(arc, nodePotential[graph.tail(arc)]);
  }

  private boolean fastIsAdmissible(int arc, long tailPotential) {
    return residualArcCapacity[arc + numArcs] > 0 && fastReducedCost(arc, tailPotential) < 0;
  }

  private boolean isActive(int node) {
    return nodeExcess[node] > 0;
  }

  private long reducedCost(int arc) {
    return fastReducedCost(arc, nodePotential[graph.tail(arc)]);
  }

  // No overflow can happen as long as the potentials are in [overflowThreshold, 0].
  private long fastReducedCost(int arc, long tailPotential) {
    return scaledArcUnitCost[arc + numArcs] + tailPotential - nodePotential[graph.head(arc)];
  }

  private int getFirstOutgoingOrOppositeIncomingArc(int node) {
    return graph.ooiBegin(node);
  }

  private void resetFirstAdmissibleArcs() {
    for (int node = 0; node < numNodes; ++node) {
      firstAdmissibleArc[node] = getFirstOutgoingOrOppositeIncomingArc(node);
    }
  }

  // Multiplies the costs by (numNodes + 1). Returns false on integer overflow.
  private boolean scaleCosts() {
    costScalingFactor = scalePrices ? numNodes + 1 : 1;
    epsilon = 1L;
    final long threshold = Long.MAX_VALUE / (2 * costScalingFactor);
    for (int arc = 0; arc < numArcs; ++arc) {
      if (scaledArcUnitCost[arc + numArcs] > threshold
          || scaledArcUnitCost[arc + numArcs] < -threshold) {
        status = Status.BAD_COST_RANGE;
        return false;
      }
      final long cost = scaledArcUnitCost[arc + numArcs] * costScalingFactor;
      scaledArcUnitCost[arc + numArcs] = cost;
      scaledArcUnitCost[opposite(arc) + numArcs] = -cost;
      epsilon = Math.max(epsilon, Math.abs(cost));
    }

    // epsilon is now the largest scaled cost: as long as the potentials stay above this
    // threshold, potential - epsilon and cost + potential difference are computed exactly.
    overflowThreshold = Long.MIN_VALUE + epsilon;
    return true;
  }

  private void unscaleCosts() {
    for (int arc = 0; arc < numArcs; ++arc) {
      final long cost = scaledArcUnitCost[arc + numArcs] / costScalingFactor;
      scaledArcUnitCost[arc + numArcs] = cost;
      scaledArcUnitCost[opposite(arc) + numArcs] = -cost;
    }
    costScalingFactor = 1;
  }

  // Divides epsilon by alpha and refines until epsilon is 1. Returns false on overflow.
  private boolean optimize() {
    final long epsilonMin = 1L;
    numRelabelsSinceLastPriceUpdate = 0;
    do {
      // Avoid epsilon == 0.
      epsilon = Math.max(epsilon / alpha, epsilonMin);
      if (!refine()) {
        return false;
      }
    } while (epsilon != 1L);
    return true;
  }

  // Saturates all the admissible arcs.
  private void saturateAdmissibleArcs() {
    for (int node = 0; node < numNodes; ++node) {
      final long tailPotential = nodePotential[node];
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiFrom(node, firstAdmissibleArc[node]); arc != limit; arc =
          graph.ooiNext(node, arc)) {
        if (fastIsAdmissible(arc, tailPotential)) {
          fastPushFlow(residualArcCapacity[arc + numArcs], arc, node);
        }
      }

      // All the admissible arcs are saturated; an arc that later gets residual capacity again is
      // not admissible until the node is relabelled.
      firstAdmissibleArc[node] = ReverseArcStaticGraph.NIL_ARC;
    }
  }

  private void fastPushFlow(long flow, int arc, int tail) {
    residualArcCapacity[arc + numArcs] -= flow;
    final int opposite = opposite(arc);
    residualArcCapacity[opposite + numArcs] += flow;
    nodeExcess[tail] -= flow;
    nodeExcess[graph.head(arc)] += flow;
  }

  private void initializeActiveNodeStack() {
    for (int node = 0; node < numNodes; ++node) {
      if (isActive(node)) {
        activeNodes.add(node);
      }
    }
  }

  // Price update heuristic (Goldberg 1997). Returns false on overflow or infeasibility.
  private boolean updatePrices() {
    // The set S: starts with the nodes of negative excess and grows along reverse admissible
    // arcs; the potentials of the nodes not in S are decreased.
    IntVector bfsQueue = new IntVector();
    boolean[] nodeInQueue = new boolean[numNodes];

    final long minCostValue = Long.MIN_VALUE;
    long[] minNonAdmissiblePotential = new long[numNodes];
    Arrays.fill(minNonAdmissiblePotential, minCostValue);
    IntVector nodesToProcess = new IntVector();

    // Sum of the positive excesses out of S, used for early exit.
    long remainingExcess = 0;

    for (int node = 0; node < numNodes; ++node) {
      if (nodeExcess[node] < 0) {
        bfsQueue.add(node);
        nodeInQueue[node] = true;
        // This uses the fact that the sum of excesses is always 0.
        remainingExcess -= nodeExcess[node];
      }
    }

    // All the nodes not yet in bfsQueue get their potential changed by potentialDelta, applied
    // when a node enters the queue or at the end for the unprocessed nodes.
    long potentialDelta = 0;

    int queueIndex = 0;
    while (remainingExcess > 0) {
      for (; queueIndex < bfsQueue.size(); ++queueIndex) {
        final int node = bfsQueue.get(queueIndex);
        final int limit = graph.directArcLimit(node);
        for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
          final int head = graph.head(arc);
          if (nodeInQueue[head]) {
            continue;
          }
          final int oppositeArc = opposite(arc);
          if (residualArcCapacity[oppositeArc + numArcs] > 0) {
            nodePotential[head] += potentialDelta;
            if (nodePotential[head] < overflowThreshold) {
              status = Status.BAD_COST_RANGE;
              return false;
            }
            if (reducedCost(oppositeArc) < 0) {
              remainingExcess -= nodeExcess[head];
              if (remainingExcess == 0) {
                nodePotential[head] -= potentialDelta;
                break;
              }
              bfsQueue.add(head);
              nodeInQueue[head] = true;
              if (potentialDelta < 0) {
                firstAdmissibleArc[head] = getFirstOutgoingOrOppositeIncomingArc(head);
              }
            } else {
              // The opposite arc is not admissible but in the residual graph.
              nodePotential[head] -= potentialDelta;
              if (minNonAdmissiblePotential[head] == minCostValue) {
                nodesToProcess.add(head);
              }
              minNonAdmissiblePotential[head] = Math.max(minNonAdmissiblePotential[head],
                  nodePotential[node] - scaledArcUnitCost[oppositeArc + numArcs]);
            }
          }
        }
        if (remainingExcess == 0) {
          break;
        }
      }
      if (remainingExcess == 0) {
        break;
      }

      // Decrease by as much as possible instead of by epsilon.
      long maxPotentialDiff = minCostValue;
      for (int i = 0; i < nodesToProcess.size(); ++i) {
        final int node = nodesToProcess.get(i);
        if (nodeInQueue[node]) {
          continue;
        }
        maxPotentialDiff =
            Math.max(maxPotentialDiff, minNonAdmissiblePotential[node] - nodePotential[node]);
        if (maxPotentialDiff == potentialDelta) {
          break;
        }
      }
      potentialDelta = maxPotentialDiff - epsilon;

      // Remove the nodes already in the queue, add to the queue the nodes connected to S by an
      // admissible arc after the relabelling, keep the others for later.
      int index = 0;
      for (int i = 0; i < nodesToProcess.size(); ++i) {
        final int node = nodesToProcess.get(i);
        if (nodeInQueue[node]) {
          continue;
        }
        if (nodePotential[node] + potentialDelta < minNonAdmissiblePotential[node]) {
          nodePotential[node] += potentialDelta;
          if (nodePotential[node] < overflowThreshold) {
            status = Status.BAD_COST_RANGE;
            return false;
          }
          firstAdmissibleArc[node] = getFirstOutgoingOrOppositeIncomingArc(node);
          bfsQueue.add(node);
          nodeInQueue[node] = true;
          remainingExcess -= nodeExcess[node];
          continue;
        }
        nodesToProcess.set(index, node);
        ++index;
      }
      nodesToProcess.truncate(index);
    }

    // Update the potentials of the nodes not yet processed.
    if (potentialDelta == 0) {
      return true;
    }
    for (int node = 0; node < numNodes; ++node) {
      if (!nodeInQueue[node]) {
        nodePotential[node] += potentialDelta;
        if (nodePotential[node] < overflowThreshold) {
          status = Status.BAD_COST_RANGE;
          return false;
        }
        firstAdmissibleArc[node] = getFirstOutgoingOrOppositeIncomingArc(node);
      }
    }
    return true;
  }

  // An epsilon-optimization step. Returns false on overflow or infeasibility.
  private boolean refine() {
    saturateAdmissibleArcs();
    initializeActiveNodeStack();

    while (!activeNodes.isEmpty()) {
      if (numRelabelsSinceLastPriceUpdate >= numNodes) {
        numRelabelsSinceLastPriceUpdate = 0;
        if (usePriceUpdate) {
          if (!updatePrices()) {
            return false;
          }
        }
      }
      final int node = activeNodes.popBack();
      if (!discharge(node)) {
        return false;
      }
    }
    return true;
  }

  // Discharges an active node. Returns false on overflow or infeasibility.
  private boolean discharge(int node) {
    while (true) {
      // The node is active; exit as soon as it becomes inactive.
      final long tailPotential = nodePotential[node];
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiFrom(node, firstAdmissibleArc[node]); arc != limit; arc =
          graph.ooiNext(node, arc)) {
        if (!fastIsAdmissible(arc, tailPotential)) {
          continue;
        }

        // Look ahead: if the head cannot accept the flow it would need a relabel later, so do it
        // now (this is skipped for self-loops).
        final int head = graph.head(arc);
        if (nodeExcess[head] >= 0 && !nodeHasAdmissibleArc(head)) {
          // Relabel head and skip if the arc is not admissible any more.
          if (!relabel(head)) {
            return false;
          }
          if (!fastIsAdmissible(arc, tailPotential)) {
            continue;
          }
        }

        final long delta = Math.min(nodeExcess[node], residualArcCapacity[arc + numArcs]);
        final boolean headActiveBeforePush = isActive(head);
        fastPushFlow(delta, arc, node);
        if (isActive(head) && !headActiveBeforePush) {
          activeNodes.add(head);
        }

        if (nodeExcess[node] == 0) {
          // arc may still be admissible.
          firstAdmissibleArc[node] = arc;
          return true;
        }
      }
      if (!relabel(node)) {
        return false;
      }
    }
  }

  // Part of the push look-ahead heuristic: returns true iff the node has an admissible arc.
  private boolean nodeHasAdmissibleArc(int node) {
    final long tailPotential = nodePotential[node];
    final int limit = graph.directArcLimit(node);
    for (int arc = graph.ooiFrom(node, firstAdmissibleArc[node]); arc != limit; arc =
        graph.ooiNext(node, arc)) {
      if (fastIsAdmissible(arc, tailPotential)) {
        firstAdmissibleArc[node] = arc;
        return true;
      }
    }
    return false;
  }

  // Decreases the potential of a node that has no admissible arc while keeping the
  // epsilon-optimality of the pseudo-flow. Returns false on overflow or infeasibility.
  private boolean relabel(int node) {
    assert checkRelabelPrecondition(node);
    ++numRelabelsSinceLastPriceUpdate;

    // Setting the potential to guaranteedNewPotential keeps epsilon-optimality; we check whether
    // it yields an admissible arc, and if not decrease the potential as much as possible.
    final long guaranteedNewPotential = nodePotential[node] - epsilon;
    if (guaranteedNewPotential < overflowThreshold) {
      status = Status.BAD_COST_RANGE;
      return false;
    }

    // The minimum node potential for which the node has no admissible arc.
    final long minCostValue = Long.MIN_VALUE;
    long minNonAdmissiblePotential = minCostValue;

    // Used to set firstAdmissibleArc[node] better than to the first incident arc.
    long previousMinNonAdmissiblePotential = minCostValue;
    int firstArc = ReverseArcStaticGraph.NIL_ARC;

    final int limit = graph.directArcLimit(node);
    for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
      if (residualArcCapacity[arc + numArcs] > 0) {
        final long minNonAdmissiblePotentialForArc =
            nodePotential[graph.head(arc)] - scaledArcUnitCost[arc + numArcs];
        if (minNonAdmissiblePotentialForArc > guaranteedNewPotential) {
          // An admissible arc for guaranteedNewPotential: stop right now.
          nodePotential[node] = guaranteedNewPotential;
          firstAdmissibleArc[node] = arc;
          return true;
        }
        if (minNonAdmissiblePotentialForArc > minNonAdmissiblePotential) {
          previousMinNonAdmissiblePotential = minNonAdmissiblePotential;
          minNonAdmissiblePotential = minNonAdmissiblePotentialForArc;
          firstArc = arc;
        }
      }
    }

    // No residual arc leaves this node.
    if (minNonAdmissiblePotential == minCostValue) {
      if (nodeExcess[node] != 0) {
        // This infeasibility detection is incomplete; only a max flow detects all cases.
        status = Status.INFEASIBLE;
        return false;
      } else {
        // This node saturates all its arcs: its potential can be decreased at will.
        nodePotential[node] = guaranteedNewPotential;
        firstAdmissibleArc[node] = ReverseArcStaticGraph.NIL_ARC;
      }
      return true;
    }

    // Decrease the potential as much as possible.
    if (minNonAdmissiblePotential < overflowThreshold) {
      status = Status.BAD_COST_RANGE;
      return false;
    }
    final long newPotential = minNonAdmissiblePotential - epsilon;
    if (newPotential < overflowThreshold) {
      status = Status.BAD_COST_RANGE;
      return false;
    }
    nodePotential[node] = newPotential;
    if (previousMinNonAdmissiblePotential <= newPotential) {
      firstAdmissibleArc[node] = firstArc;
    } else {
      // No indication of what the first admissible arc may be.
      firstAdmissibleArc[node] = getFirstOutgoingOrOppositeIncomingArc(node);
    }
    return true;
  }

  private int opposite(int arc) {
    return graph.oppositeArc(arc);
  }

  private boolean isArcDirect(int arc) {
    return arc >= 0;
  }

  /** Returns true if the feasibility was checked by the last solve. */
  boolean isFeasibilityChecked() {
    return feasibilityChecked;
  }
}
