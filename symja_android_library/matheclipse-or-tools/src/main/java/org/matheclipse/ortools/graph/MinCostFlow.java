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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/min_cost_flow.h/.cc C++ to Java, API adapted from the OR-Tools Java wrapper, see NOTICE in the module root.

package org.matheclipse.ortools.graph;

import java.util.Arrays;
import java.util.logging.Logger;
import org.matheclipse.ortools.graph.internal.GenericMaxFlow;
import org.matheclipse.ortools.graph.internal.GenericMinCostFlow;
import org.matheclipse.ortools.graph.internal.ReverseArcStaticGraph;

/**
 * A simple minimum cost flow interface (<code>SimpleMinCostFlow</code> in C++).
 *
 * <p>
 * Nodes are created lazily by {@link #addArcWithCapacityAndUnitCost(int, int, long, long)} and
 * {@link #setNodeSupply(int, long)}, so the valid nodes are always <code>[0, getNumNodes())</code>.
 * {@link #solve()} first checks feasibility with a maximum flow from a super source (linked to the
 * supply nodes) to a super sink (linked from the demand nodes), then runs a cost-scaling
 * push-relabel algorithm on the same augmented graph.
 */
public class MinCostFlow extends MinCostFlowBase {
  private static final Logger LOGGER = Logger.getLogger(MinCostFlow.class.getName());

  private int numArcs;
  private int[] arcTail;
  private int[] arcHead;
  private long[] arcCapacity;
  private long[] arcCost;
  private long[] nodeSupply;
  private int numNodes;
  private int[] arcPermutation = new int[0];
  private long[] arcFlow = new long[0];
  private long optimalCost;
  private long maximumFlow;

  /**
   * Creates an empty problem, reserving memory for the given number of nodes and arcs.
   */
  public MinCostFlow(int reserve_num_nodes, int reserve_num_arcs) {
    final int arcReserve = Math.max(8, reserve_num_arcs);
    arcTail = new int[arcReserve];
    arcHead = new int[arcReserve];
    arcCapacity = new long[arcReserve];
    arcCost = new long[arcReserve];
    nodeSupply = new long[Math.max(8, reserve_num_nodes)];
  }

  /** Creates an empty problem, reserving memory for the given number of nodes. */
  public MinCostFlow(int reserve_num_nodes) {
    this(reserve_num_nodes, 0);
  }

  /** Creates an empty problem. */
  public MinCostFlow() {
    this(0, 0);
  }

  /** Does nothing; kept for compatibility with the OR-Tools Java wrapper. */
  @Override
  public void delete() {}

  /**
   * Adds a directed arc from tail to head with a capacity and a cost per unit of flow.
   *
   * <ul>
   * <li>Node indices and the capacity must be non-negative (&gt;= 0).
   * <li>The unit cost can take any integer value (even negative).
   * <li>Self-looping and duplicate arcs are supported.
   * <li>After the method finishes, <code>getNumArcs() == the returned arc index + 1</code>.
   * </ul>
   *
   * @return the index of the new arc
   */
  public int addArcWithCapacityAndUnitCost(int tail, int head, long capacity, long unit_cost) {
    if (tail < 0 || head < 0) {
      throw new IllegalArgumentException("negative node index");
    }
    resizeNodeVectors(Math.max(tail, head));
    final int arc = numArcs;
    if (numArcs == arcTail.length) {
      int newLength = 2 * numArcs;
      arcTail = Arrays.copyOf(arcTail, newLength);
      arcHead = Arrays.copyOf(arcHead, newLength);
      arcCapacity = Arrays.copyOf(arcCapacity, newLength);
      arcCost = Arrays.copyOf(arcCost, newLength);
    }
    arcTail[arc] = tail;
    arcHead[arc] = head;
    arcCapacity[arc] = capacity;
    arcCost[arc] = unit_cost;
    numArcs++;
    return arc;
  }

  /** Modifies the capacity of an arc returned by a previous call to the arc adder. */
  public void setArcCapacity(int arc, long capacity) {
    checkArc(arc);
    arcCapacity[arc] = capacity;
  }

  /**
   * Sets the supply of a node (non-negative index). Implicitly created nodes have supply 0. A
   * demand is a negative supply.
   */
  public void setNodeSupply(int node, long supply) {
    if (node < 0) {
      throw new IllegalArgumentException("negative node index");
    }
    resizeNodeVectors(node);
    nodeSupply[node] = supply;
  }

  /**
   * Solves the problem. The sum of the supplies must equal the sum of the demands and the network
   * must be able to send all supplies to all demands.
   *
   * @return {@link Status#OPTIMAL}, {@link Status#UNBALANCED} if the supplies don't match the
   *         demands, {@link Status#INFEASIBLE} if they cannot be sent, {@link Status#BAD_COST_RANGE}
   *         or {@link Status#BAD_CAPACITY_RANGE} if the magnitudes are too large (a negative
   *         capacity is also reported as {@link Status#BAD_CAPACITY_RANGE})
   */
  public MinCostFlowBase.Status solve() {
    return solveWithPossibleAdjustment(false);
  }

  /**
   * Same as {@link #solve()}, but the supplies need not match the demands nor be sendable: this
   * computes a maximum flow with minimum cost, the value of the maximum flow is
   * {@link #getMaximumFlow()}.
   */
  public MinCostFlowBase.Status solveMaxFlowWithMinCost() {
    return solveWithPossibleAdjustment(true);
  }

  /** The cost of the flow found by the last solve when it returned {@link Status#OPTIMAL}. */
  public long getOptimalCost() {
    return optimalCost;
  }

  /** The total flow found by the last solve when it returned {@link Status#OPTIMAL}. */
  public long getMaximumFlow() {
    return maximumFlow;
  }

  /**
   * The flow on an arc; only meaningful after a successful solve. The algorithm is deterministic,
   * so a problem always gets the same solution among the optimal ones.
   */
  public long getFlow(int arc) {
    checkArc(arc);
    // Zero when the last solve did not compute a flow.
    return arc < arcFlow.length ? arcFlow[arc] : 0;
  }

  /** The number of nodes. */
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

  /** The supply of a node as given by the user. */
  public long getSupply(int node) {
    if (node < 0 || node >= numNodes) {
      throw new IndexOutOfBoundsException("node " + node + " not in [0, " + numNodes + ")");
    }
    return nodeSupply[node];
  }

  /** The unit cost of an arc as given by the user. */
  public long getUnitCost(int arc) {
    checkArc(arc);
    return arcCost[arc];
  }

  private int permutedArc(int arc) {
    return arc < arcPermutation.length ? arcPermutation[arc] : arc;
  }

  private MinCostFlowBase.Status solveWithPossibleAdjustment(boolean adjust) {
    optimalCost = 0;
    maximumFlow = 0;
    arcFlow = new long[0];
    final int nodes = numNodes;
    if (nodes == 0) {
      return Status.OPTIMAL;
    }
    for (int arc = 0; arc < numArcs; ++arc) {
      // A precondition violation in C++ (CHECK failure).
      if (arcCapacity[arc] < 0) {
        LOGGER.severe("negative capacity on arc " + arc);
        return Status.BAD_CAPACITY_RANGE;
      }
    }

    int supplyNodeCount = 0;
    int demandNodeCount = 0;
    long totalSupply = 0;
    long totalDemand = 0;
    try {
      for (int node = 0; node < nodes; ++node) {
        if (nodeSupply[node] > 0) {
          ++supplyNodeCount;
          totalSupply = Math.addExact(totalSupply, nodeSupply[node]);
        } else if (nodeSupply[node] < 0) {
          ++demandNodeCount;
          totalDemand = Math.subtractExact(totalDemand, nodeSupply[node]);
        }
      }
    } catch (ArithmeticException e) {
      LOGGER.severe("Input consistency error: sum of supplies or demands overflow");
      return Status.BAD_CAPACITY_RANGE;
    }
    if (!adjust && totalSupply != totalDemand) {
      return Status.UNBALANCED;
    }

    // Feasibility checking and supply/demand adjustment: a new source is linked to the supply
    // nodes and the demand nodes to a new sink (capacity = supply or demand), the max flow between
    // them is computed and then used as the supply of the source and the demand of the sink of a
    // min cost flow on the same augmented graph.
    final int augmentedNumArcs = numArcs + supplyNodeCount + demandNodeCount;
    final int source = nodes;
    final int sink = nodes + 1;
    final int augmentedNumNodes = nodes + 2;

    ReverseArcStaticGraph graph = new ReverseArcStaticGraph(augmentedNumNodes, augmentedNumArcs);
    for (int arc = 0; arc < numArcs; ++arc) {
      graph.addArc(arcTail[arc], arcHead[arc]);
    }
    for (int node = 0; node < nodes; ++node) {
      if (nodeSupply[node] > 0) {
        graph.addArc(source, node);
      } else if (nodeSupply[node] < 0) {
        graph.addArc(node, sink);
      }
    }
    arcPermutation = graph.build();

    {
      GenericMaxFlow maxFlow = new GenericMaxFlow(graph, source, sink);
      int arc;
      for (arc = 0; arc < numArcs; ++arc) {
        maxFlow.setArcCapacity(permutedArc(arc), arcCapacity[arc]);
      }
      for (int node = 0; node < nodes; ++node) {
        if (nodeSupply[node] != 0) {
          maxFlow.setArcCapacity(permutedArc(arc), Math.abs(nodeSupply[node]));
          ++arc;
        }
      }
      if (!maxFlow.solve()) {
        LOGGER.severe("Max flow could not be computed.");
        if (maxFlow.status() == GenericMaxFlow.Status.NOT_SOLVED) {
          return Status.NOT_SOLVED;
        }
        return Status.BAD_RESULT;
      }
      maximumFlow = maxFlow.getOptimalFlow();
    }

    if (!adjust && maximumFlow != totalSupply) {
      return Status.INFEASIBLE;
    }

    GenericMinCostFlow minCostFlow = new GenericMinCostFlow(graph);
    int arc;
    for (arc = 0; arc < numArcs; ++arc) {
      final int permuted = permutedArc(arc);
      minCostFlow.setArcUnitCost(permuted, arcCost[arc]);
      minCostFlow.setArcCapacity(permuted, arcCapacity[arc]);
    }
    for (int node = 0; node < nodes; ++node) {
      if (nodeSupply[node] != 0) {
        final int permuted = permutedArc(arc);
        minCostFlow.setArcCapacity(permuted, Math.abs(nodeSupply[node]));
        minCostFlow.setArcUnitCost(permuted, 0);
        ++arc;
      }
    }
    minCostFlow.setNodeSupply(source, maximumFlow);
    minCostFlow.setNodeSupply(sink, -maximumFlow);
    minCostFlow.setCheckFeasibility(false);

    arcFlow = new long[numArcs];
    if (minCostFlow.solve()) {
      optimalCost = minCostFlow.getOptimalCost();
      for (arc = 0; arc < numArcs; ++arc) {
        arcFlow[arc] = minCostFlow.flow(permutedArc(arc));
      }
    }
    return minCostFlow.status();
  }

  private void resizeNodeVectors(int node) {
    if (node < numNodes) {
      return;
    }
    if (node >= nodeSupply.length) {
      nodeSupply = Arrays.copyOf(nodeSupply, Math.max(node + 1, 2 * nodeSupply.length));
    }
    numNodes = node + 1;
  }

  private void checkArc(int arc) {
    if (arc < 0 || arc >= numArcs) {
      throw new IndexOutOfBoundsException("arc " + arc + " not in [0, " + numArcs + ")");
    }
  }
}
