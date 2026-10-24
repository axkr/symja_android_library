package org.matheclipse.ortools.graph;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Edge cases and random instances of {@link MaxFlow}. */
public class MaxFlowTest {

  @Test
  public void testEmptyProblem() {
    MaxFlow maxFlow = new MaxFlow();
    assertEquals(0, maxFlow.getNumNodes());
    assertEquals(0, maxFlow.getNumArcs());
    // Source and sink outside of the graph are disconnected.
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(0, 1));
    assertEquals(0, maxFlow.getOptimalFlow());
    assertEquals(0, maxFlow.getSourceSideMinCut().length);
  }

  @Test
  public void testBadInput() {
    MaxFlow maxFlow = new MaxFlow();
    maxFlow.addArcWithCapacity(0, 1, 5);
    assertEquals(MaxFlow.Status.BAD_INPUT, maxFlow.solve(0, 0));
    assertEquals(MaxFlow.Status.BAD_INPUT, maxFlow.solve(-1, 1));
    assertEquals(MaxFlow.Status.BAD_INPUT, maxFlow.solve(0, -1));
    maxFlow.setArcCapacity(0, -3);
    assertEquals(MaxFlow.Status.BAD_INPUT, maxFlow.solve(0, 1));
    maxFlow.setArcCapacity(0, 3);
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(0, 1));
    assertEquals(3, maxFlow.getOptimalFlow());
    // Sink outside of the graph.
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(0, 7));
    assertEquals(0, maxFlow.getOptimalFlow());
    assertEquals(0, maxFlow.getFlow(0));
  }

  @Test
  public void testAccessors() {
    MaxFlow maxFlow = new MaxFlow();
    assertEquals(0, maxFlow.addArcWithCapacity(3, 1, 7));
    assertEquals(1, maxFlow.addArcWithCapacity(1, 0, 2));
    assertEquals(4, maxFlow.getNumNodes());
    assertEquals(2, maxFlow.getNumArcs());
    assertEquals(3, maxFlow.getTail(0));
    assertEquals(1, maxFlow.getHead(0));
    assertEquals(7, maxFlow.getCapacity(0));
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(3, 0));
    assertEquals(2, maxFlow.getOptimalFlow());
    assertEquals(2, maxFlow.getFlow(0));
    assertEquals(2, maxFlow.getFlow(1));
  }

  @Test
  public void testSelfLoopsAndParallelArcs() {
    MaxFlow maxFlow = new MaxFlow();
    maxFlow.addArcWithCapacity(0, 0, 100);
    maxFlow.addArcWithCapacity(0, 1, 3);
    maxFlow.addArcWithCapacity(0, 1, 4);
    maxFlow.addArcWithCapacity(1, 1, 100);
    maxFlow.addArcWithCapacity(1, 2, 10);
    maxFlow.addArcWithCapacity(1, 0, 10);
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(0, 2));
    assertEquals(7, maxFlow.getOptimalFlow());
    // Self-arcs never carry flow.
    assertEquals(0, maxFlow.getFlow(0));
    assertEquals(0, maxFlow.getFlow(3));
    assertEquals(7, maxFlow.getFlow(1) + maxFlow.getFlow(2));
  }

  @Test
  public void testOverflow() {
    MaxFlow maxFlow = new MaxFlow();
    maxFlow.addArcWithCapacity(0, 1, Long.MAX_VALUE);
    maxFlow.addArcWithCapacity(0, 1, Long.MAX_VALUE);
    maxFlow.addArcWithCapacity(1, 2, Long.MAX_VALUE);
    maxFlow.addArcWithCapacity(1, 2, Long.MAX_VALUE);
    assertEquals(MaxFlow.Status.POSSIBLE_OVERFLOW, maxFlow.solve(0, 2));
    assertEquals(Long.MAX_VALUE, maxFlow.getOptimalFlow());

    // The total capacity out of the source overflows, but the max flow fits.
    MaxFlow fits = new MaxFlow();
    fits.addArcWithCapacity(0, 1, Long.MAX_VALUE);
    fits.addArcWithCapacity(0, 2, Long.MAX_VALUE);
    fits.addArcWithCapacity(1, 3, 5);
    fits.addArcWithCapacity(2, 3, 6);
    assertEquals(MaxFlow.Status.OPTIMAL, fits.solve(0, 3));
    assertEquals(11, fits.getOptimalFlow());
  }

  @Test
  public void testMinCut() {
    // 0 -> 1 (3), 0 -> 2 (2), 1 -> 3 (1), 2 -> 3 (5), 1 -> 2 (1)
    MaxFlow maxFlow = new MaxFlow();
    maxFlow.addArcWithCapacity(0, 1, 3);
    maxFlow.addArcWithCapacity(0, 2, 2);
    maxFlow.addArcWithCapacity(1, 3, 1);
    maxFlow.addArcWithCapacity(2, 3, 5);
    maxFlow.addArcWithCapacity(1, 2, 1);
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(0, 3));
    assertEquals(4, maxFlow.getOptimalFlow());
    int[] sourceSide = maxFlow.getSourceSideMinCut();
    Arrays.sort(sourceSide);
    assertArrayEquals(new int[] {0, 1}, sourceSide);
    int[] sinkSide = maxFlow.getSinkSideMinCut();
    Arrays.sort(sinkSide);
    assertArrayEquals(new int[] {2, 3}, sinkSide);
  }

  @Test
  public void testResolveAfterCapacityChange() {
    MaxFlow maxFlow = new MaxFlow();
    maxFlow.addArcWithCapacity(0, 1, 5);
    maxFlow.addArcWithCapacity(1, 2, 3);
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(0, 2));
    assertEquals(3, maxFlow.getOptimalFlow());
    maxFlow.setArcCapacity(1, 9);
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(0, 2));
    assertEquals(5, maxFlow.getOptimalFlow());
    // Reverse direction: no path.
    assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(2, 0));
    assertEquals(0, maxFlow.getOptimalFlow());
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRandomGraphs() {
    Random random = new Random(20261023L);
    for (int round = 0; round < 300; ++round) {
      final int numNodes = 2 + random.nextInt(12);
      final int numArcs = random.nextInt(4 * numNodes);
      int[] tails = new int[numArcs];
      int[] heads = new int[numArcs];
      long[] capacities = new long[numArcs];
      MaxFlow maxFlow = new MaxFlow();
      for (int i = 0; i < numArcs; ++i) {
        tails[i] = random.nextInt(numNodes);
        heads[i] = random.nextInt(numNodes);
        capacities[i] = random.nextInt(4) == 0 ? 0 : random.nextInt(20);
        assertEquals(i, maxFlow.addArcWithCapacity(tails[i], heads[i], capacities[i]));
      }
      final int source = random.nextInt(numNodes);
      int sink = random.nextInt(numNodes - 1);
      if (sink >= source) {
        sink++;
      }
      assertEquals(MaxFlow.Status.OPTIMAL, maxFlow.solve(source, sink));
      final int graphNodes = maxFlow.getNumNodes();
      final long expected =
          GraphReference.maxFlow(graphNodes, tails, heads, capacities, source, sink);
      assertEquals(expected, maxFlow.getOptimalFlow(), "round " + round);
      if (source >= graphNodes || sink >= graphNodes) {
        continue;
      }

      long[] flows = new long[numArcs];
      for (int i = 0; i < numArcs; ++i) {
        flows[i] = maxFlow.getFlow(i);
      }
      long[] supplies = new long[graphNodes];
      supplies[source] = expected;
      supplies[sink] = -expected;
      // A self-loop carries no flow, so its contribution cancels anyway.
      GraphReference.assertFlowConservation(graphNodes, tails, heads, capacities, flows,
          supplies);

      // The arcs leaving the source side form a cut of capacity equal to the flow.
      boolean[] inSourceSide = new boolean[graphNodes];
      for (int node : maxFlow.getSourceSideMinCut()) {
        inSourceSide[node] = true;
      }
      assertTrue(inSourceSide[source]);
      assertTrue(!inSourceSide[sink]);
      long cut = 0;
      for (int i = 0; i < numArcs; ++i) {
        if (inSourceSide[tails[i]] && !inSourceSide[heads[i]]) {
          cut += capacities[i];
        }
      }
      assertEquals(expected, cut, "round " + round);
    }
  }
}
