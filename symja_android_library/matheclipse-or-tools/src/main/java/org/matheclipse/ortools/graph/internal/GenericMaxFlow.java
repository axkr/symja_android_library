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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/graph/generic_max_flow.h C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.graph.internal;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Push-relabel maximum flow (Goldberg and Tarjan) on a {@link ReverseArcStaticGraph}.
 *
 * <p>
 * The algorithm handles preflows: it saturates the arcs leaving the source, then repeatedly
 * discharges the active node (positive excess) of highest height, pushing flow on admissible arcs
 * and relabelling the node when it has none. A global update (reverse breadth-first search from
 * the sink in the residual graph) recomputes exact heights. It runs in two phases: the first one
 * only deals with nodes that can reach the sink and yields the maximum flow value and a minimum
 * cut, the second one returns the remaining excesses to the source (after cancelling flow cycles)
 * to obtain a proper flow.
 *
 * <p>
 * Only the direct arcs <code>[0, numArcs)</code> carry a capacity, their opposite arcs
 * <code>[-numArcs, 0)</code> have capacity zero. Flow quantities are <code>long</code>; if more
 * than <code>Long.MAX_VALUE</code> can be sent the status is {@link Status#INT_OVERFLOW} and the
 * flow reaches that bound.
 */
public final class GenericMaxFlow {
  private static final Logger LOGGER = Logger.getLogger(GenericMaxFlow.class.getName());

  /** The status of a max flow run. */
  public enum Status {
    /** The problem was not solved, or its data were edited. */
    NOT_SOLVED,
    /** Solve() was called and found an optimal solution. */
    OPTIMAL,
    /** There is a feasible flow greater than the maximum possible flow sum. */
    INT_OVERFLOW,
    /** Not used any more. */
    BAD_INPUT,
    /** Not used any more. */
    BAD_RESULT
  }

  // Maximum manageable flow.
  private static final long MAX_FLOW_SUM = Long.MAX_VALUE;

  private final ReverseArcStaticGraph graph;
  private final int numNodes;
  private final int numArcs;

  // The excess of each node. Always non-negative except at the source.
  private final long[] nodeExcess;
  // The height of each node; a lower bound on the residual distance to the sink.
  private final int[] nodePotential;
  // residual[arc + numArcs] = capacity[arc] - flow[arc] for direct and reverse arcs.
  private final long[] residualArcCapacity;
  // The first admissible arc of each node.
  private final int[] firstAdmissibleArc;
  // Active nodes by height.
  private final PriorityQueueWithRestrictedPush activeNodeByHeight =
      new PriorityQueueWithRestrictedPush();

  private final int source;
  private final int sink;
  private Status status = Status.NOT_SOLVED;

  // BFS queue used by globalUpdate() and computeReachableNodes().
  private boolean[] nodeInBfsQueue;
  private final IntVector bfsQueue;

  /**
   * Initializes a max flow instance on a built graph. <code>source</code> and <code>sink</code>
   * must be non-negative; they may be outside the graph, in which case they are disconnected.
   */
  public GenericMaxFlow(ReverseArcStaticGraph graph, int source, int sink) {
    if (!graph.isBuilt()) {
      throw new IllegalStateException("the graph must be built");
    }
    this.graph = graph;
    this.source = source;
    this.sink = sink;
    this.numNodes = graph.numNodes();
    this.numArcs = graph.numArcs();
    final int nodeArraySize = Math.max(numNodes, Math.max(source, sink) + 1);
    nodeExcess = new long[nodeArraySize];
    nodePotential = new int[nodeArraySize];
    firstAdmissibleArc = new int[nodeArraySize];
    residualArcCapacity = new long[2 * numArcs];
    nodeInBfsQueue = new boolean[nodeArraySize];
    bfsQueue = new IntVector(nodeArraySize);
  }

  /** The graph of this instance. */
  public ReverseArcStaticGraph graph() {
    return graph;
  }

  /** The status of the last call to {@link #solve()}. */
  public Status status() {
    return status;
  }

  /** The source node. */
  public int getSourceNodeIndex() {
    return source;
  }

  /** The sink node. */
  public int getSinkNodeIndex() {
    return sink;
  }

  /**
   * Sets the capacity of a direct arc. This is ignored for self-arcs, whose capacity stays zero.
   */
  public void setArcCapacity(int arc, long newCapacity) {
    if (head(arc) == tail(arc)) {
      return;
    }
    status = Status.NOT_SOLVED;
    residualArcCapacity[arc + numArcs] = newCapacity;
    residualArcCapacity[opposite(arc) + numArcs] = 0;
  }

  /** The total flow found by the algorithm. */
  public long getOptimalFlow() {
    return nodeExcess[sink];
  }

  /**
   * The flow on an arc. On a couple (arc, opposite arc) the flow only goes in the direction where
   * it is positive; the other direction has the negated flow.
   */
  public long flow(int arc) {
    if (isArcDirect(arc)) {
      return residualArcCapacity[opposite(arc) + numArcs];
    }
    return -residualArcCapacity[arc + numArcs];
  }

  /** The initial capacity of an arc (zero for reverse arcs). */
  public long capacity(int arc) {
    if (!isArcDirect(arc)) {
      return 0;
    }
    return residualArcCapacity[arc + numArcs] + residualArcCapacity[opposite(arc) + numArcs];
  }

  /**
   * The nodes reachable from the source in the residual graph; the outgoing arcs of this set form
   * a minimum cut.
   */
  public int[] getSourceSideMinCut() {
    return computeReachableNodes(source, false);
  }

  /**
   * The nodes that can reach the sink in the residual graph; the incoming arcs of this set form a
   * minimum cut.
   */
  public int[] getSinkSideMinCut() {
    return computeReachableNodes(sink, true);
  }

  /** Solves the problem; always returns true, see {@link #status()}. */
  public boolean solve() {
    status = Status.NOT_SOLVED;
    initializePreflow();

    // Source or sink outside of the graph: they are disconnected.
    if (sink >= numNodes || source >= numNodes) {
      status = Status.OPTIMAL;
      return true;
    }

    refineWithGlobalUpdate();

    status = Status.OPTIMAL;
    assert checkResult();

    if (getOptimalFlow() == MAX_FLOW_SUM && augmentingPathExists()) {
      // In this case, we are sure that the flow is > MAX_FLOW_SUM.
      status = Status.INT_OVERFLOW;
    }
    return true;
  }

  /** Returns true if there is a path from source to sink with remaining capacity. */
  public boolean augmentingPathExists() {
    boolean[] isReached = new boolean[numNodes];
    IntVector toProcess = new IntVector();
    toProcess.add(source);
    isReached[source] = true;
    while (!toProcess.isEmpty()) {
      final int node = toProcess.popBack();
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
        if (residualArcCapacity[arc + numArcs] > 0) {
          final int head = graph.head(arc);
          if (!isReached[head]) {
            isReached[head] = true;
            toProcess.add(head);
          }
        }
      }
    }
    return isReached[sink];
  }

  // Checks that node excesses are zero (except at source and sink), that residual capacities are
  // non-negative, and that the flow is maximal.
  private boolean checkResult() {
    if (nodeExcess[source] != -nodeExcess[sink]) {
      LOGGER.severe("-node_excess_[source_] = " + (-nodeExcess[source])
          + " != node_excess_[sink_] = " + nodeExcess[sink]);
      return false;
    }
    for (int node = 0; node < numNodes; ++node) {
      if (node != source && node != sink && nodeExcess[node] != 0) {
        LOGGER.severe("node_excess_[" + node + "] = " + nodeExcess[node] + " != 0");
        return false;
      }
    }
    for (int arc = 0; arc < numArcs; ++arc) {
      final int opposite = opposite(arc);
      final long directCapacity = residualArcCapacity[arc + numArcs];
      final long oppositeCapacity = residualArcCapacity[opposite + numArcs];
      if (directCapacity < 0 || oppositeCapacity < 0 || directCapacity + oppositeCapacity < 0) {
        LOGGER.severe("negative residual capacity on arc " + arc);
        return false;
      }
    }
    if (getOptimalFlow() < MAX_FLOW_SUM && augmentingPathExists()) {
      LOGGER.severe("The algorithm terminated, but the flow is not maximal!");
      return false;
    }
    return true;
  }

  private boolean isAdmissible(int tail, int arc) {
    return residualArcCapacity[arc + numArcs] > 0
        && nodePotential[tail] == nodePotential[graph.head(arc)] + 1;
  }

  private boolean isActive(int node) {
    return node != source && node != sink && nodeExcess[node] > 0;
  }

  private void pushActiveNode(int node) {
    activeNodeByHeight.push(node, nodePotential[node]);
  }

  private void initializePreflow() {
    Arrays.fill(nodeExcess, 0);

    // Restart from a clear state with no flow and the initial arc capacities.
    for (int arc = 0; arc < numArcs; ++arc) {
      final int oppositeArc = opposite(arc);
      residualArcCapacity[arc + numArcs] += residualArcCapacity[oppositeArc + numArcs];
      residualArcCapacity[oppositeArc + numArcs] = 0;
    }

    // All initial heights are zero except the source whose height is the number of nodes.
    Arrays.fill(nodePotential, 0);
    nodePotential[source] = numNodes;

    for (int node = 0; node < numNodes; ++node) {
      final int first = graph.ooiBegin(node);
      firstAdmissibleArc[node] =
          first != graph.directArcLimit(node) ? first : ReverseArcStaticGraph.NIL_ARC;
    }
  }

  // Clears the excess at each node by pushing the flow back to the source: a depth-first search
  // from the source in the direct graph cancels flow cycles, then the excesses are returned along
  // the depth-first search tree in reverse topological order.
  private void pushFlowExcessBackToSource() {
    // Stored nodes are settled nodes already stored in reverseTopologicalOrder (except the sink).
    boolean[] stored = new boolean[numNodes];
    stored[sink] = true;

    // The visited nodes that are not yet stored are the nodes of the current dfs branch.
    boolean[] visited = new boolean[numNodes];
    visited[sink] = true;

    // Stack of arcs to explore; the current node is head(arcStack.back()).
    IntVector arcStack = new IntVector();
    // Increasing list of indices into arcStack of the arcs of the current dfs branch.
    IntVector indexBranch = new IntVector();
    // Nodes in reverse topological order in the final dfs tree.
    IntVector reverseTopologicalOrder = new IntVector();

    for (int arc = graph.start(source); arc < graph.directArcLimit(source); ++arc) {
      if (flow(arc) > 0) {
        arcStack.add(arc);
      }
    }
    visited[source] = true;

    // Dfs on the subgraph formed by the direct arcs with positive flow.
    while (!arcStack.isEmpty()) {
      final int node = graph.head(arcStack.back());

      // A visited node has all its arcs explored: we just backtracked.
      if (visited[node]) {
        if (!stored[node]) {
          stored[node] = true;
          reverseTopologicalOrder.add(node);
          indexBranch.popBack();
        }
        arcStack.popBack();
        continue;
      }

      // A new unexplored node: push all its outgoing arcs with positive flow.
      visited[node] = true;
      indexBranch.add(arcStack.size() - 1);
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.start(node); arc < limit; ++arc) {
        final long flow = flow(arc);
        final int head = graph.head(arc);
        if (flow > 0 && !stored[head]) {
          if (!visited[head]) {
            arcStack.add(arc);
          } else {
            // There is a cycle; arcStack[indexBranch[cycleBegin]] is its first arc.
            int cycleBegin = indexBranch.size();
            while (cycleBegin > 0
                && graph.head(arcStack.get(indexBranch.get(cycleBegin - 1))) != head) {
              --cycleBegin;
            }

            // The maximum flow that can be cancelled on the cycle and the minimal index such that
            // arcStack[indexBranch[i]] will be saturated.
            long flowOnCycle = flow;
            int firstSaturatedIndex = indexBranch.size();
            for (int i = indexBranch.size() - 1; i >= cycleBegin; --i) {
              final int arcOnCycle = arcStack.get(indexBranch.get(i));
              final long arcFlow = flow(arcOnCycle);
              if (arcFlow <= flowOnCycle) {
                flowOnCycle = arcFlow;
                firstSaturatedIndex = i;
              }
            }

            // Cancel the flow on the cycle, and mark the nodes that will be backtracked over as
            // not visited.
            pushFlow(-flowOnCycle, node, arc);
            for (int i = indexBranch.size() - 1; i >= cycleBegin; --i) {
              final int arcOnCycle = arcStack.get(indexBranch.get(i));
              pushFlow(-flowOnCycle, graph.tail(arcOnCycle), arcOnCycle);
              if (i >= firstSaturatedIndex) {
                visited[graph.head(arcOnCycle)] = false;
              }
            }

            // Backtrack the dfs just before indexBranch[firstSaturatedIndex]. If the current
            // node is still active, there is nothing to do.
            if (firstSaturatedIndex < indexBranch.size()) {
              arcStack.truncate(indexBranch.get(firstSaturatedIndex));
              indexBranch.truncate(firstSaturatedIndex);
              // We backtracked over the current node, stop looping over its arcs.
              break;
            }
          }
        }
      }
    }

    // Return the flow to the source. The sink and the source are not in the order.
    for (int i = 0; i < reverseTopologicalOrder.size(); i++) {
      final int node = reverseTopologicalOrder.get(i);
      if (nodeExcess[node] == 0) {
        continue;
      }
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
        final long flow = flow(arc);
        if (flow < 0) {
          final long toPush = Math.min(nodeExcess[node], -flow);
          pushFlow(toPush, node, arc);
          if (nodeExcess[node] == 0) {
            break;
          }
        }
      }
    }
  }

  // Computes the best possible node potentials given the current flow using a reverse
  // breadth-first search from the sink in the residual graph (global update heuristic).
  private void globalUpdate() {
    bfsQueue.clear();
    int queueIndex = 0;
    Arrays.fill(nodeInBfsQueue, 0, numNodes, false);
    nodeInBfsQueue[sink] = true;

    // The source is marked so that it is never relabelled.
    nodeInBfsQueue[source] = true;
    bfsQueue.add(sink);

    while (queueIndex != bfsQueue.size()) {
      final int node = bfsQueue.get(queueIndex);
      ++queueIndex;
      final int candidateDistance = nodePotential[node] + 1;
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
        final int head = graph.head(arc);

        // Skip the arc if the height of head was already set.
        if (nodeInBfsQueue[head]) {
          continue;
        }

        final int oppositeArc = opposite(arc);
        if (residualArcCapacity[oppositeArc + numArcs] > 0) {
          // If head is active, steal some or all of its excess.
          if (nodeExcess[head] > 0) {
            pushAsMuchFlowAsPossible(head, oppositeArc);

            // If the arc became saturated, it is no longer in the residual graph.
            if (residualArcCapacity[oppositeArc + numArcs] == 0) {
              continue;
            }
          }
          nodePotential[head] = candidateDistance;
          nodeInBfsQueue[head] = true;
          bfsQueue.add(head);
        }
      }
    }

    // Nodes not reached cannot reach the sink or the source in the residual graph: give them an
    // unreachable height.
    for (int node = 0; node < numNodes; ++node) {
      if (!nodeInBfsQueue[node]) {
        nodePotential[node] = 2 * numNodes - 1;
      }
    }

    // Reset the active nodes in increasing order of height. bfsQueue[0] is the sink.
    for (int i = 1; i < bfsQueue.size(); ++i) {
      final int node = bfsQueue.get(i);
      if (nodeExcess[node] > 0) {
        pushActiveNode(node);
      }
    }
  }

  // Tries to saturate all the outgoing arcs of the source that can reach the sink, without making
  // the total flow exceed MAX_FLOW_SUM. Returns true if some flow was pushed.
  private boolean saturateOutgoingArcsFromSource() {
    if (nodeExcess[sink] == MAX_FLOW_SUM) {
      return false;
    }
    if (nodeExcess[source] == -MAX_FLOW_SUM) {
      return false;
    }

    boolean flowPushed = false;
    final int limit = graph.directArcLimit(source);
    for (int arc = graph.start(source); arc < limit; ++arc) {
      final long flow = residualArcCapacity[arc + numArcs];

      // Special admissibility condition for the source.
      if (flow == 0 || nodePotential[graph.head(arc)] >= numNodes) {
        continue;
      }

      // Avoid an overflow of the flow out of the source.
      final long currentFlowOutOfSource = -nodeExcess[source];
      final long cappedFlow = MAX_FLOW_SUM - currentFlowOutOfSource;
      if (cappedFlow < flow) {
        if (cappedFlow == 0) {
          return true;
        }
        pushFlow(cappedFlow, source, arc);
        return true;
      }
      pushFlow(flow, source, arc);
      flowPushed = true;
    }
    return flowPushed;
  }

  private void pushFlow(long flow, int tail, int arc) {
    residualArcCapacity[arc + numArcs] -= flow;
    residualArcCapacity[opposite(arc) + numArcs] += flow;
    nodeExcess[tail] -= flow;
    nodeExcess[graph.head(arc)] += flow;
  }

  // Pushes min(nodeExcess[tail], residual capacity of arc).
  private void pushAsMuchFlowAsPossible(int tail, int arc) {
    final long flow = Math.min(residualArcCapacity[arc + numArcs], nodeExcess[tail]);
    residualArcCapacity[arc + numArcs] -= flow;
    residualArcCapacity[opposite(arc) + numArcs] += flow;
    nodeExcess[tail] -= flow;
    nodeExcess[graph.head(arc)] += flow;
  }

  private void refineWithGlobalUpdate() {
    int[] skipActiveNode = new int[numNodes];

    // Usually saturateOutgoingArcsFromSource() saturates all the arcs from the source at once
    // and this loops only once; it loops more when more than MAX_FLOW_SUM can leave the source.
    while (saturateOutgoingArcsFromSource()) {
      int numSkipped;
      do {
        numSkipped = 0;
        Arrays.fill(skipActiveNode, 0);
        skipActiveNode[sink] = 2;
        skipActiveNode[source] = 2;
        globalUpdate();
        while (!activeNodeByHeight.isEmpty()) {
          final int node = activeNodeByHeight.pop();
          if (skipActiveNode[node] > 1) {
            if (node != sink && node != source) {
              ++numSkipped;
            }
            continue;
          }
          final int oldHeight = nodePotential[node];
          discharge(node);

          // A node whose height grows by more than one is likely to push flow back the way it
          // came; after this happened twice the node waits for the next global update.
          if (nodePotential[node] > oldHeight + 1) {
            ++skipActiveNode[node];
          }
        }
      } while (numSkipped > 0);

      // Second phase: obtain a flow from the preflow.
      pushFlowExcessBackToSource();
    }
  }

  // Discharges an active node by saturating its admissible arcs and relabelling it.
  private void discharge(int node) {
    while (true) {
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiFrom(node, firstAdmissibleArc[node]); arc != limit; arc =
          graph.ooiNext(node, arc)) {
        if (isAdmissible(node, arc)) {
          final int head = graph.head(arc);
          if (nodeExcess[head] == 0) {
            // The push below will make the node active (possibly the sink).
            pushActiveNode(head);
          }
          pushAsMuchFlowAsPossible(node, arc);
          if (nodeExcess[node] == 0) {
            firstAdmissibleArc[node] = arc; // arc may still be admissible.
            return;
          }
        }
      }
      relabel(node);

      // This node can no longer reach the sink, skip it until pushFlowExcessBackToSource().
      if (nodePotential[node] >= numNodes) {
        break;
      }
    }
  }

  // Relaxed relabel: the node is not relabelled if an admissible arc exists at its height.
  private void relabel(int node) {
    int minHeight = Integer.MAX_VALUE;
    int firstAdmissible = ReverseArcStaticGraph.NIL_ARC;
    final int limit = graph.directArcLimit(node);
    for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
      if (residualArcCapacity[arc + numArcs] > 0) {
        final int headHeight = nodePotential[graph.head(arc)];
        if (headHeight < minHeight) {
          minHeight = headHeight;
          firstAdmissible = arc;
          // An admissible arc at the current height: this is the true first admissible arc.
          if (minHeight + 1 == nodePotential[node]) {
            break;
          }
        }
      }
    }
    nodePotential[node] = minHeight + 1;
    firstAdmissibleArc[node] = firstAdmissible;
  }

  private int head(int arc) {
    return graph.head(arc);
  }

  private int tail(int arc) {
    return graph.tail(arc);
  }

  private int opposite(int arc) {
    return graph.oppositeArc(arc);
  }

  private boolean isArcDirect(int arc) {
    return graph.isArcValid(arc) && arc >= 0;
  }

  // The nodes reachable from start in the residual graph, or in the reverse residual graph.
  private int[] computeReachableNodes(int start, boolean reverse) {
    if (start >= numNodes) {
      return new int[] {start};
    }
    bfsQueue.clear();
    if (nodeInBfsQueue.length < numNodes) {
      nodeInBfsQueue = new boolean[numNodes];
    }
    Arrays.fill(nodeInBfsQueue, 0, numNodes, false);

    int queueIndex = 0;
    bfsQueue.add(start);
    nodeInBfsQueue[start] = true;
    while (queueIndex != bfsQueue.size()) {
      final int node = bfsQueue.get(queueIndex);
      ++queueIndex;
      final int limit = graph.directArcLimit(node);
      for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
        final int head = graph.head(arc);
        if (nodeInBfsQueue[head]) {
          continue;
        }
        if (residualArcCapacity[(reverse ? opposite(arc) : arc) + numArcs] == 0) {
          continue;
        }
        nodeInBfsQueue[head] = true;
        bfsQueue.add(head);
      }
    }
    return bfsQueue.toArray();
  }
}
