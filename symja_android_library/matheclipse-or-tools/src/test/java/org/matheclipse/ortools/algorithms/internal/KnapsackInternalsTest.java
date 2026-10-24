package org.matheclipse.ortools.algorithms.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.util.TimeLimit;

/** Invariants of the knapsack internals. */
public class KnapsackInternalsTest {

  private static KnapsackSearchNode node(long upperBound, long currentProfit) {
    KnapsackSearchNode node = new KnapsackSearchNode(null, new KnapsackAssignment(0, true));
    node.setProfitUpperBound(upperBound);
    node.setCurrentProfit(currentProfit);
    return node;
  }

  private static int compare(KnapsackSearchNode a, KnapsackSearchNode b) {
    int c = Long.compare(a.profitUpperBound(), b.profitUpperBound());
    return c != 0 ? c : Long.compare(a.currentProfit(), b.currentProfit());
  }

  @Test
  public void testSearchQueueIsAMaxHeap() {
    Random random = new Random(8L);
    KnapsackGenericSolver.SearchQueue queue = new KnapsackGenericSolver.SearchQueue();
    List<KnapsackSearchNode> reference = new ArrayList<>();
    for (int step = 0; step < 5000; ++step) {
      if (reference.isEmpty() || random.nextInt(3) != 0) {
        KnapsackSearchNode n = node(random.nextInt(20), random.nextInt(5));
        queue.push(n);
        reference.add(n);
      } else {
        KnapsackSearchNode top = queue.top();
        for (KnapsackSearchNode other : reference) {
          assertTrue(compare(top, other) >= 0);
        }
        queue.pop();
        assertTrue(reference.remove(top));
      }
      assertEquals(reference.size(), queue.size());
    }
  }

  @Test
  public void testSearchQueueTieOrder() {
    // Equal keys come out in the order of the libstdc++ heap algorithms.
    KnapsackGenericSolver.SearchQueue queue = new KnapsackGenericSolver.SearchQueue();
    KnapsackSearchNode[] nodes = new KnapsackSearchNode[5];
    for (int i = 0; i < nodes.length; ++i) {
      nodes[i] = node(7, 1);
      queue.push(nodes[i]);
    }
    // push_heap never moves an equal element up, so the heap is in insertion order; pop_heap
    // moves the hole down along the right child on ties (__adjust_heap prefers the left child
    // only if the right one is strictly smaller).
    assertTrue(queue.top() == nodes[0]);
    queue.pop();
    assertTrue(queue.top() == nodes[2]);
    queue.pop();
    assertTrue(queue.top() == nodes[4]);
  }

  @Test
  public void testStateAndPath() {
    KnapsackState state = new KnapsackState();
    state.init(3);
    assertEquals(3, state.getNumberOfItems());
    assertTrue(state.updateState(false, new KnapsackAssignment(1, true)));
    assertTrue(state.isBound(1));
    assertTrue(state.isIn(1));
    assertFalse(state.updateState(false, new KnapsackAssignment(1, false)));
    assertTrue(state.updateState(true, new KnapsackAssignment(1, true)));
    assertFalse(state.isBound(1));

    KnapsackSearchNode root = new KnapsackSearchNode(null, new KnapsackAssignment(-1, true));
    KnapsackSearchNode a = new KnapsackSearchNode(root, new KnapsackAssignment(0, true));
    KnapsackSearchNode b = new KnapsackSearchNode(a, new KnapsackAssignment(1, false));
    KnapsackSearchNode c = new KnapsackSearchNode(root, new KnapsackAssignment(0, false));
    KnapsackSearchPath path = new KnapsackSearchPath(b, c);
    path.init();
    assertTrue(path.via() == root);
    KnapsackSearchPath down = new KnapsackSearchPath(a, b);
    down.init();
    assertTrue(down.via() == a);
    assertEquals(2, b.depth());
  }

  @Test
  public void testUpperBoundOfRatio() {
    assertEquals(3, KnapsackMath.upperBoundOfRatio(7, 3, 6));
    assertEquals(0, KnapsackMath.mostSignificantBitPosition64(0));
    assertEquals(63, KnapsackMath.mostSignificantBitPosition64(-1));
    assertTrue(KnapsackMath.willProductOverflow(1L << 31, 1L << 31));
    assertFalse(KnapsackMath.willProductOverflow(1L << 30, 1L << 30));
    // Large products are computed in double and rounded to nearest.
    assertEquals(1L << 40, KnapsackMath.upperBoundOfRatio(1L << 40, 1L << 30, 1L << 30));
  }

  @Test
  public void testTimeLimit() {
    TimeLimit infinite = new TimeLimit(Double.POSITIVE_INFINITY);
    assertFalse(infinite.limitReached());
    assertEquals(Double.POSITIVE_INFINITY, infinite.getTimeLeft());
    TimeLimit zero = new TimeLimit(0.0);
    assertTrue(zero.limitReached());
    assertTrue(zero.limitReached());
    assertEquals(0.0, zero.getTimeLeft());
    TimeLimit tenSeconds = new TimeLimit(10.0);
    assertFalse(tenSeconds.limitReached());
    assertTrue(tenSeconds.getTimeLeft() > 5.0);
  }
}
