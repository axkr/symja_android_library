package org.matheclipse.ortools.graph.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.graph.GraphReference;
import org.matheclipse.ortools.graph.MinCostFlowBase.Status;

/**
 * The generic solvers on larger random graphs, including the options that the simple interfaces
 * don't use (price updates, built-in feasibility check).
 */
public class GenericFlowsTest {

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testMaxFlowLargerGraphs() {
    Random random = new Random(77L);
    for (int round = 0; round < 30; ++round) {
      final int numNodes = 20 + random.nextInt(60);
      final int numArcs = numNodes * (2 + random.nextInt(6));
      int[] tails = new int[numArcs];
      int[] heads = new int[numArcs];
      long[] capacities = new long[numArcs];
      ReverseArcStaticGraph graph = new ReverseArcStaticGraph(numNodes, numArcs);
      for (int i = 0; i < numArcs; ++i) {
        tails[i] = random.nextInt(numNodes);
        heads[i] = random.nextInt(numNodes);
        capacities[i] = 1 + random.nextInt(100);
        graph.addArc(tails[i], heads[i]);
      }
      int[] permutation = graph.build();
      GenericMaxFlow maxFlow = new GenericMaxFlow(graph, 0, numNodes - 1);
      for (int i = 0; i < numArcs; ++i) {
        maxFlow.setArcCapacity(i < permutation.length ? permutation[i] : i, capacities[i]);
      }
      assertTrue(maxFlow.solve());
      assertEquals(GenericMaxFlow.Status.OPTIMAL, maxFlow.status());
      final long expected =
          GraphReference.maxFlow(numNodes, tails, heads, capacities, 0, numNodes - 1);
      assertEquals(expected, maxFlow.getOptimalFlow(), "round " + round);
      assertTrue(!maxFlow.augmentingPathExists());
      long[] flows = new long[numArcs];
      for (int i = 0; i < numArcs; ++i) {
        flows[i] = maxFlow.flow(i < permutation.length ? permutation[i] : i);
        assertEquals(capacities[i] * (tails[i] == heads[i] ? 0 : 1),
            maxFlow.capacity(i < permutation.length ? permutation[i] : i));
      }
      long[] supplies = new long[numNodes];
      supplies[0] = expected;
      supplies[numNodes - 1] = -expected;
      GraphReference.assertFlowConservation(numNodes, tails, heads, capacities, flows, supplies);
    }
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testMinCostFlowOptions() {
    Random random = new Random(78L);
    for (int round = 0; round < 60; ++round) {
      final int numNodes = 5 + random.nextInt(30);
      final int numArcs = numNodes * (1 + random.nextInt(5));
      int[] tails = new int[numArcs];
      int[] heads = new int[numArcs];
      long[] capacities = new long[numArcs];
      long[] costs = new long[numArcs];
      ReverseArcStaticGraph graph = new ReverseArcStaticGraph(numNodes, numArcs);
      for (int i = 0; i < numArcs; ++i) {
        tails[i] = random.nextInt(numNodes);
        heads[i] = random.nextInt(numNodes);
        capacities[i] = random.nextInt(15);
        // Non-negative costs: cycles are allowed, the reference stays exact.
        costs[i] = random.nextInt(50);
        graph.addArc(tails[i], heads[i]);
      }
      int[] permutation = graph.build();
      long[] supplies = new long[numNodes];
      for (int k = 1 + random.nextInt(5); k > 0; --k) {
        int from = random.nextInt(numNodes);
        int to = random.nextInt(numNodes);
        long amount = random.nextInt(8);
        supplies[from] += amount;
        supplies[to] -= amount;
      }
      final Long expected =
          GraphReference.minCostFlow(numNodes, tails, heads, capacities, costs, supplies);
      for (int option = 0; option < 4; ++option) {
        GenericMinCostFlow minCostFlow = new GenericMinCostFlow(graph);
        for (int i = 0; i < numArcs; ++i) {
          final int arc = i < permutation.length ? permutation[i] : i;
          minCostFlow.setArcUnitCost(arc, costs[i]);
          minCostFlow.setArcCapacity(arc, capacities[i]);
        }
        for (int v = 0; v < numNodes; ++v) {
          minCostFlow.setNodeSupply(v, supplies[v]);
        }
        minCostFlow.setUseUpdatePrices((option & 1) != 0);
        // Without price scaling the result is 1-optimal only; check it on feasibility alone.
        final boolean scale = (option & 2) == 0;
        minCostFlow.setPriceScaling(scale);
        final boolean solved = minCostFlow.solve();
        if (expected == null) {
          assertEquals(Status.INFEASIBLE, minCostFlow.status(), "round " + round);
          continue;
        }
        assertTrue(solved, "round " + round + " option " + option);
        assertEquals(Status.OPTIMAL, minCostFlow.status());
        long[] flows = new long[numArcs];
        long cost = 0;
        for (int i = 0; i < numArcs; ++i) {
          flows[i] = minCostFlow.flow(i < permutation.length ? permutation[i] : i);
          cost += flows[i] * costs[i];
        }
        assertEquals(cost, minCostFlow.getOptimalCost());
        GraphReference.assertFlowConservation(numNodes, tails, heads, capacities, flows,
            supplies);
        if (scale) {
          assertEquals(expected.longValue(), cost, "round " + round + " option " + option);
        } else {
          assertTrue(cost >= expected, "round " + round);
        }
      }
    }
  }
}
