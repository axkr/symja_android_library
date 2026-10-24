package org.matheclipse.ortools.graph.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Invariants of the graph storage, the restricted priority queue and saturated arithmetic. */
public class GraphInternalsTest {

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testReverseArcStaticGraph() {
    Random random = new Random(11L);
    for (int round = 0; round < 200; ++round) {
      final int numNodes = 1 + random.nextInt(8);
      final int numArcs = random.nextInt(20);
      ReverseArcStaticGraph graph = new ReverseArcStaticGraph(numNodes, numArcs);
      int[] tails = new int[numArcs];
      int[] heads = new int[numArcs];
      for (int i = 0; i < numArcs; ++i) {
        tails[i] = random.nextInt(numNodes);
        heads[i] = random.nextInt(numNodes);
        assertEquals(i, graph.addArc(tails[i], heads[i]));
      }
      int[] permutation = graph.build();
      assertEquals(numNodes, graph.numNodes());
      assertEquals(numArcs, graph.numArcs());
      for (int i = 0; i < numArcs; ++i) {
        final int arc = i < permutation.length ? permutation[i] : i;
        assertEquals(tails[i], graph.tail(arc));
        assertEquals(heads[i], graph.head(arc));
        final int opposite = graph.oppositeArc(arc);
        assertTrue(opposite < 0);
        assertEquals(arc, graph.oppositeArc(opposite));
        assertEquals(heads[i], graph.tail(opposite));
        assertEquals(tails[i], graph.head(opposite));
      }
      int totalOut = 0;
      int totalIn = 0;
      for (int node = 0; node < numNodes; ++node) {
        // Outgoing arcs are sorted by tail.
        for (int arc = graph.start(node); arc < graph.directArcLimit(node); ++arc) {
          assertEquals(node, graph.tail(arc));
        }
        int previousHead = -1;
        for (int arc = graph.reverseStart(node); arc < graph.reverseArcLimit(node); ++arc) {
          assertEquals(node, graph.tail(arc));
          // The opposite incoming arcs are sorted by head.
          assertTrue(graph.head(arc) >= previousHead);
          previousHead = graph.head(arc);
        }
        // The outgoing-or-opposite-incoming iteration visits both ranges, opposite arcs first.
        List<Integer> visited = new ArrayList<>();
        final int limit = graph.directArcLimit(node);
        for (int arc = graph.ooiBegin(node); arc != limit; arc = graph.ooiNext(node, arc)) {
          visited.add(arc);
        }
        assertEquals(graph.outDegree(node) + graph.inDegree(node), visited.size());
        for (int k = 0; k < visited.size(); ++k) {
          assertEquals(k < graph.inDegree(node), visited.get(k) < 0);
        }
        totalOut += graph.outDegree(node);
        totalIn += graph.inDegree(node);
      }
      assertEquals(numArcs, totalOut);
      assertEquals(numArcs, totalIn);
    }
  }

  @Test
  public void testListGraphOrder() {
    ListGraph graph = new ListGraph(3, 2);
    graph.addArc(0, 1);
    graph.addArc(0, 2);
    graph.addArc(5, 0);
    assertEquals(6, graph.numNodes());
    // Most recent arc first.
    assertEquals(1, graph.firstOutgoingArc(0));
    assertEquals(0, graph.nextOutgoingArc(1));
    assertEquals(ListGraph.NIL_ARC, graph.nextOutgoingArc(0));
    assertEquals(ListGraph.NIL_ARC, graph.firstOutgoingArc(3));
    assertEquals(5, graph.tail(2));
  }

  @Test
  public void testPriorityQueueWithRestrictedPush() {
    PriorityQueueWithRestrictedPush queue = new PriorityQueueWithRestrictedPush();
    assertTrue(queue.isEmpty());
    queue.push(10, 0);
    queue.push(11, 1);
    queue.push(12, 1);
    queue.push(13, 2);
    queue.push(14, 1);
    queue.push(15, 3);
    assertEquals(15, queue.pop());
    assertEquals(13, queue.pop());
    // Same priority: LIFO.
    assertEquals(14, queue.pop());
    assertEquals(12, queue.pop());
    assertEquals(11, queue.pop());
    assertEquals(10, queue.pop());
    assertTrue(queue.isEmpty());
    for (int i = 0; i < 100; ++i) {
      queue.push(i, i);
    }
    for (int i = 99; i >= 0; --i) {
      assertEquals(i, queue.pop());
    }
  }

  @Test
  public void testSaturatedArithmetic() {
    assertEquals(Long.MAX_VALUE, SaturatedArithmetic.capAdd(Long.MAX_VALUE, 1));
    assertEquals(Long.MIN_VALUE, SaturatedArithmetic.capAdd(Long.MIN_VALUE, -1));
    assertEquals(5, SaturatedArithmetic.capAdd(2, 3));
    assertEquals(Long.MIN_VALUE, SaturatedArithmetic.capSub(Long.MIN_VALUE, 1));
    assertEquals(Long.MAX_VALUE, SaturatedArithmetic.capSub(Long.MAX_VALUE, -1));
    assertEquals(Long.MAX_VALUE, SaturatedArithmetic.capSub(0, Long.MIN_VALUE));
    assertEquals(-1, SaturatedArithmetic.capSub(2, 3));
    assertEquals(Long.MAX_VALUE, SaturatedArithmetic.capProd(Long.MAX_VALUE, 2));
    assertEquals(Long.MIN_VALUE, SaturatedArithmetic.capProd(Long.MAX_VALUE, -2));
    assertEquals(Long.MAX_VALUE, SaturatedArithmetic.capProd(Long.MIN_VALUE, -1));
    assertEquals(Long.MIN_VALUE, SaturatedArithmetic.capProd(Long.MIN_VALUE, 1));
    assertEquals(-12, SaturatedArithmetic.capProd(3, -4));
    assertEquals(0, SaturatedArithmetic.capProd(0, Long.MIN_VALUE));
  }
}
