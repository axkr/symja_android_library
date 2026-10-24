package org.matheclipse.ortools.symja;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.optimization.IRoutingSolver;
import org.matheclipse.ortools.constraintsolver.Assignment;
import org.matheclipse.ortools.routing.FirstSolutionStrategy;
import org.matheclipse.ortools.routing.Globals;
import org.matheclipse.ortools.routing.IndexManager;
import org.matheclipse.ortools.routing.LocalSearchMetaheuristic;
import org.matheclipse.ortools.routing.Model;
import org.matheclipse.ortools.routing.RoutingSearchParameters;
import org.matheclipse.ortools.util.Duration;

/**
 * {@link IRoutingSolver} with the routing library of the OR-Tools port: one vehicle starting and
 * ending at node <code>0</code>, a cheapest arc first tour improved by local search. Up to
 * {@link #GREEDY_SIZE} nodes the local search stops in its first local optimum; larger problems
 * continue with guided local search until the time limit.
 */
public class OrToolsRouting implements IRoutingSolver {

  /** The largest number of nodes which is solved by greedy descent only. */
  static final int GREEDY_SIZE = 12;

  @Override
  public int[] shortestTour(long[][] distances, double timeLimit) {
    final int n = distances.length;
    if (n == 0) {
      return null;
    }
    if (n <= 3) {
      // every closed tour through at most 3 nodes has the same length in both directions
      int[] order = new int[n];
      for (int i = 0; i < n; i++) {
        order[i] = i;
      }
      return order;
    }
    final IndexManager manager = new IndexManager(n, 1, 0);
    Model routing = new Model(manager);
    int transit = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
      return distances[manager.indexToNode(fromIndex)][manager.indexToNode(toIndex)];
    });
    routing.setArcCostEvaluatorOfAllVehicles(transit);
    RoutingSearchParameters.Builder builder = Globals.defaultRoutingSearchParameters().toBuilder()
        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC);
    if (n <= GREEDY_SIZE) {
      builder.setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GREEDY_DESCENT);
    } else {
      builder.setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH);
      long millis = (long) Math.ceil(Math.max(0.1, timeLimit) * 1000.0);
      builder.setTimeLimit(Duration.newBuilder().setSeconds(millis / 1000)
          .setNanos((int) ((millis % 1000) * 1_000_000L)).build());
    }
    Assignment solution = routing.solveWithParameters(builder.build());
    if (solution == null) {
      return null;
    }
    List<Integer> order = new ArrayList<Integer>(n);
    long index = routing.start(0);
    while (!routing.isEnd(index)) {
      order.add(manager.indexToNode(index));
      index = solution.value(routing.nextVar(index));
    }
    if (order.size() != n) {
      return null;
    }
    int[] result = new int[n];
    for (int i = 0; i < n; i++) {
      result[i] = order.get(i);
    }
    return result;
  }
}
