package org.matheclipse.ortools.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.graph.MinCostFlowBase.Status;

/** Edge cases and random instances of {@link MinCostFlow}. */
public class MinCostFlowTest {

  @Test
  public void testEmptyProblem() {
    MinCostFlow minCostFlow = new MinCostFlow();
    assertEquals(Status.OPTIMAL, minCostFlow.solve());
    assertEquals(0, minCostFlow.getOptimalCost());
    assertEquals(0, minCostFlow.getMaximumFlow());
    assertEquals(0, minCostFlow.getNumNodes());
  }

  @Test
  public void testConstructorsAndAccessors() {
    MinCostFlow minCostFlow = new MinCostFlow(10, 20);
    assertEquals(0, minCostFlow.addArcWithCapacityAndUnitCost(2, 5, 7, -3));
    minCostFlow.setNodeSupply(8, 4);
    assertEquals(9, minCostFlow.getNumNodes());
    assertEquals(1, minCostFlow.getNumArcs());
    assertEquals(2, minCostFlow.getTail(0));
    assertEquals(5, minCostFlow.getHead(0));
    assertEquals(7, minCostFlow.getCapacity(0));
    assertEquals(-3, minCostFlow.getUnitCost(0));
    assertEquals(4, minCostFlow.getSupply(8));
    assertEquals(0, minCostFlow.getSupply(3));
    minCostFlow.setArcCapacity(0, 9);
    assertEquals(9, minCostFlow.getCapacity(0));
    assertNotNull(new MinCostFlow(3));
    assertThrows(IllegalArgumentException.class,
        () -> minCostFlow.addArcWithCapacityAndUnitCost(-1, 0, 1, 1));
    assertThrows(IndexOutOfBoundsException.class, () -> minCostFlow.getTail(1));
  }

  @Test
  public void testUnbalanced() {
    MinCostFlow minCostFlow = new MinCostFlow();
    minCostFlow.addArcWithCapacityAndUnitCost(0, 1, 10, 1);
    minCostFlow.setNodeSupply(0, 5);
    minCostFlow.setNodeSupply(1, -4);
    assertEquals(Status.UNBALANCED, minCostFlow.solve());
    assertEquals(0, minCostFlow.getOptimalCost());

    // The max flow with min cost sends what can be sent.
    assertEquals(Status.OPTIMAL, minCostFlow.solveMaxFlowWithMinCost());
    assertEquals(4, minCostFlow.getMaximumFlow());
    assertEquals(4, minCostFlow.getOptimalCost());
    assertEquals(4, minCostFlow.getFlow(0));
  }

  @Test
  public void testInfeasible() {
    MinCostFlow minCostFlow = new MinCostFlow();
    minCostFlow.addArcWithCapacityAndUnitCost(0, 1, 3, 1);
    minCostFlow.addArcWithCapacityAndUnitCost(1, 2, 2, 1);
    minCostFlow.setNodeSupply(0, 3);
    minCostFlow.setNodeSupply(2, -3);
    assertEquals(Status.INFEASIBLE, minCostFlow.solve());

    assertEquals(Status.OPTIMAL, minCostFlow.solveMaxFlowWithMinCost());
    assertEquals(2, minCostFlow.getMaximumFlow());
    assertEquals(4, minCostFlow.getOptimalCost());

    // Supplies without arcs.
    MinCostFlow noArcs = new MinCostFlow();
    noArcs.setNodeSupply(0, 1);
    noArcs.setNodeSupply(1, -1);
    assertEquals(Status.INFEASIBLE, noArcs.solve());
  }

  @Test
  public void testNegativeCostsAndSelfLoops() {
    MinCostFlow minCostFlow = new MinCostFlow();
    minCostFlow.addArcWithCapacityAndUnitCost(0, 1, 5, -2);
    minCostFlow.addArcWithCapacityAndUnitCost(0, 2, 5, 1);
    minCostFlow.addArcWithCapacityAndUnitCost(1, 3, 3, 1);
    minCostFlow.addArcWithCapacityAndUnitCost(2, 3, 5, -1);
    minCostFlow.addArcWithCapacityAndUnitCost(3, 3, 5, -10);
    minCostFlow.setNodeSupply(0, 6);
    minCostFlow.setNodeSupply(3, -6);
    assertEquals(Status.OPTIMAL, minCostFlow.solve());
    // 3 units via node 1 (cost -1 each) and 3 via node 2 (cost 0 each), and the negative
    // self-loop is a negative cycle that is saturated (5 * -10).
    assertEquals(5, minCostFlow.getFlow(4));
    assertEquals(-3 - 50, minCostFlow.getOptimalCost());
    assertEquals(3, minCostFlow.getFlow(0));
    assertEquals(3, minCostFlow.getFlow(2));
    assertEquals(3, minCostFlow.getFlow(1));
    assertEquals(3, minCostFlow.getFlow(3));
  }

  @Test
  public void testBadCostRange() {
    MinCostFlow minCostFlow = new MinCostFlow();
    minCostFlow.addArcWithCapacityAndUnitCost(0, 1, 1, Long.MAX_VALUE / 2);
    minCostFlow.setNodeSupply(0, 1);
    minCostFlow.setNodeSupply(1, -1);
    assertEquals(Status.BAD_COST_RANGE, minCostFlow.solve());
    assertEquals(0, minCostFlow.getOptimalCost());

    MinCostFlow negative = new MinCostFlow();
    negative.addArcWithCapacityAndUnitCost(0, 1, 1, -(Long.MAX_VALUE / 4));
    negative.setNodeSupply(0, 1);
    negative.setNodeSupply(1, -1);
    assertEquals(Status.BAD_COST_RANGE, negative.solve());
  }

  @Test
  public void testBadCapacityRange() {
    MinCostFlow minCostFlow = new MinCostFlow();
    minCostFlow.addArcWithCapacityAndUnitCost(0, 2, Long.MAX_VALUE, 1);
    minCostFlow.addArcWithCapacityAndUnitCost(1, 2, Long.MAX_VALUE, 1);
    minCostFlow.setNodeSupply(0, Long.MAX_VALUE);
    minCostFlow.setNodeSupply(1, Long.MAX_VALUE);
    minCostFlow.setNodeSupply(2, Long.MIN_VALUE);
    assertEquals(Status.BAD_CAPACITY_RANGE, minCostFlow.solve());

    MinCostFlow negativeCapacity = new MinCostFlow();
    negativeCapacity.addArcWithCapacityAndUnitCost(0, 1, -1, 1);
    assertEquals(Status.BAD_CAPACITY_RANGE, negativeCapacity.solve());
  }

  @Test
  public void testLargeCapacitiesAreCapped() {
    // An "infinite" capacity out of a supply node is capped instead of overflowing.
    MinCostFlow minCostFlow = new MinCostFlow();
    minCostFlow.addArcWithCapacityAndUnitCost(0, 1, Long.MAX_VALUE, 2);
    minCostFlow.addArcWithCapacityAndUnitCost(1, 2, 4, 1);
    minCostFlow.setNodeSupply(0, 4);
    minCostFlow.setNodeSupply(2, -4);
    assertEquals(Status.OPTIMAL, minCostFlow.solve());
    assertEquals(4 * 2 + 4 * 1, minCostFlow.getOptimalCost());

    // Two consecutive "infinite" arcs through a transit node: the capping of the first node is
    // not propagated to the bounds already computed for the transit node, so (like upstream) the
    // problem is rejected.
    MinCostFlow chain = new MinCostFlow();
    chain.addArcWithCapacityAndUnitCost(0, 1, Long.MAX_VALUE, 2);
    chain.addArcWithCapacityAndUnitCost(0, 2, Long.MAX_VALUE, 3);
    chain.addArcWithCapacityAndUnitCost(1, 3, 4, 0);
    chain.addArcWithCapacityAndUnitCost(2, 3, Long.MAX_VALUE, 0);
    chain.setNodeSupply(0, 10);
    chain.setNodeSupply(3, -10);
    assertEquals(Status.BAD_CAPACITY_RANGE, chain.solve());

    // Two "infinite" arcs into a demand node.
    MinCostFlow incoming = new MinCostFlow();
    incoming.addArcWithCapacityAndUnitCost(0, 1, Long.MAX_VALUE, 1);
    incoming.addArcWithCapacityAndUnitCost(2, 1, Long.MAX_VALUE, 2);
    incoming.setNodeSupply(0, 1);
    incoming.setNodeSupply(2, 1);
    incoming.setNodeSupply(1, -2);
    assertEquals(Status.OPTIMAL, incoming.solve());
    assertEquals(3, incoming.getOptimalCost());
  }

  @Test
  public void testStatusValues() {
    assertEquals(7, Status.BAD_CAPACITY_RANGE.swigValue());
    assertEquals(Status.BAD_COST_RANGE, Status.swigToEnum(6));
    assertEquals(Status.NOT_SOLVED, MinCostFlow.Status.swigToEnum(0));
    assertThrows(IllegalArgumentException.class, () -> Status.swigToEnum(8));
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRandomProblems() {
    Random random = new Random(42L);
    for (int round = 0; round < 300; ++round) {
      final int numNodes = 2 + random.nextInt(9);
      // Acyclic arcs (tail < head) so that negative costs never form a negative cycle.
      final int numArcs = random.nextInt(4 * numNodes);
      int[] tails = new int[numArcs];
      int[] heads = new int[numArcs];
      long[] capacities = new long[numArcs];
      long[] costs = new long[numArcs];
      MinCostFlow minCostFlow = new MinCostFlow();
      for (int i = 0; i < numArcs; ++i) {
        int a = random.nextInt(numNodes);
        int b = random.nextInt(numNodes - 1);
        if (b >= a) {
          b++;
        }
        tails[i] = Math.min(a, b);
        heads[i] = Math.max(a, b);
        capacities[i] = random.nextInt(10);
        costs[i] = random.nextInt(21) - 5;
        minCostFlow.addArcWithCapacityAndUnitCost(tails[i], heads[i], capacities[i], costs[i]);
      }
      long[] supplies = new long[numNodes];
      // A few balanced transfers.
      for (int k = random.nextInt(4); k >= 0; --k) {
        int from = random.nextInt(numNodes);
        int to = random.nextInt(numNodes);
        long amount = random.nextInt(6);
        supplies[from] += amount;
        supplies[to] -= amount;
      }
      for (int v = 0; v < numNodes; ++v) {
        minCostFlow.setNodeSupply(v, supplies[v]);
      }
      final Status status = minCostFlow.solve();
      final int graphNodes = minCostFlow.getNumNodes();
      final Long expected = GraphReference.minCostFlow(graphNodes, tails, heads, capacities,
          costs, java.util.Arrays.copyOf(supplies, graphNodes));
      if (expected == null) {
        assertEquals(Status.INFEASIBLE, status, "round " + round);
        continue;
      }
      assertEquals(Status.OPTIMAL, status, "round " + round);
      assertEquals(expected.longValue(), minCostFlow.getOptimalCost(), "round " + round);
      long[] flows = new long[numArcs];
      long cost = 0;
      for (int i = 0; i < numArcs; ++i) {
        flows[i] = minCostFlow.getFlow(i);
        cost += flows[i] * costs[i];
      }
      assertEquals(expected.longValue(), cost);
      GraphReference.assertFlowConservation(graphNodes, tails, heads, capacities, flows,
          java.util.Arrays.copyOf(supplies, graphNodes));
    }
  }
}
