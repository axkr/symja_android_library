package org.matheclipse.ortools.symja;

import org.matheclipse.core.optimization.IGraphFlowSolver;
import org.matheclipse.ortools.graph.MaxFlow;
import org.matheclipse.ortools.graph.MinCostFlow;
import org.matheclipse.ortools.graph.MinCostFlowBase;

/**
 * {@link IGraphFlowSolver} with the push-relabel <code>MaxFlow</code> and the cost scaling
 * <code>MinCostFlow</code> of the OR-Tools port.
 */
public class OrToolsGraphFlows implements IGraphFlowSolver {

  @Override
  public FlowResult maxFlow(int numNodes, int[] tails, int[] heads, long[] capacities,
      int source, int sink) {
    final int m = tails.length;
    if (!isNode(source, tails, heads) || !isNode(sink, tails, heads) || source == sink) {
      // no arc touches the source or the sink: nothing can flow
      return new FlowResult(Status.OPTIMAL, 0L, new long[m]);
    }
    MaxFlow maxFlow = new MaxFlow();
    for (int a = 0; a < m; a++) {
      maxFlow.addArcWithCapacity(tails[a], heads[a], capacities[a]);
    }
    MaxFlow.Status status = maxFlow.solve(source, sink);
    switch (status) {
      case OPTIMAL: {
        long[] flow = new long[m];
        for (int a = 0; a < m; a++) {
          flow[a] = maxFlow.getFlow(a);
        }
        return new FlowResult(Status.OPTIMAL, maxFlow.getOptimalFlow(), flow);
      }
      case POSSIBLE_OVERFLOW:
        return new FlowResult(Status.POSSIBLE_OVERFLOW, 0L, null);
      case BAD_INPUT:
        return new FlowResult(Status.BAD_INPUT, 0L, null);
      default:
        return new FlowResult(Status.BAD_RESULT, 0L, null);
    }
  }

  @Override
  public FlowResult minCostFlow(int numNodes, int[] tails, int[] heads, long[] capacities,
      long[] unitCosts, long[] supplies) {
    final int m = tails.length;
    MinCostFlow minCostFlow = new MinCostFlow();
    for (int a = 0; a < m; a++) {
      minCostFlow.addArcWithCapacityAndUnitCost(tails[a], heads[a], capacities[a], unitCosts[a]);
    }
    boolean allZero = true;
    for (int v = 0; v < supplies.length; v++) {
      if (supplies[v] != 0) {
        allZero = false;
        if (!isNode(v, tails, heads)) {
          // a supply or demand at a node without arcs can't be met
          return new FlowResult(Status.INFEASIBLE, 0L, null);
        }
        minCostFlow.setNodeSupply(v, supplies[v]);
      }
    }
    if (allZero) {
      return new FlowResult(Status.OPTIMAL, 0L, new long[m]);
    }
    MinCostFlowBase.Status status = minCostFlow.solve();
    switch (status) {
      case OPTIMAL: {
        long[] flow = new long[m];
        for (int a = 0; a < m; a++) {
          flow[a] = minCostFlow.getFlow(a);
        }
        return new FlowResult(Status.OPTIMAL, minCostFlow.getOptimalCost(), flow);
      }
      case INFEASIBLE:
        return new FlowResult(Status.INFEASIBLE, 0L, null);
      case UNBALANCED:
        return new FlowResult(Status.UNBALANCED, 0L, null);
      case BAD_COST_RANGE:
        return new FlowResult(Status.POSSIBLE_OVERFLOW, 0L, null);
      default:
        return new FlowResult(Status.BAD_RESULT, 0L, null);
    }
  }

  private static boolean isNode(int v, int[] tails, int[] heads) {
    for (int a = 0; a < tails.length; a++) {
      if (tails[a] == v || heads[a] == v) {
        return true;
      }
    }
    return false;
  }
}
