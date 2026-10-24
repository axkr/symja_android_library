package org.matheclipse.ortools.graph;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.graph.LinearSumAssignment.Status;

/** Edge cases and random instances of {@link LinearSumAssignment}. */
public class LinearSumAssignmentTest {

  @Test
  public void testEmptyProblem() {
    LinearSumAssignment assignment = new LinearSumAssignment();
    assertEquals(Status.OPTIMAL, assignment.solve());
    assertEquals(0, assignment.getOptimalCost());
    assertEquals(0, assignment.getNumNodes());
  }

  @Test
  public void testAccessors() {
    LinearSumAssignment assignment = new LinearSumAssignment();
    assertEquals(0, assignment.addArcWithCost(0, 1, 5));
    assertEquals(1, assignment.addArcWithCost(1, 0, -7));
    assertEquals(2, assignment.getNumNodes());
    assertEquals(2, assignment.getNumArcs());
    assertEquals(1, assignment.getLeftNode(1));
    assertEquals(0, assignment.getRightNode(1));
    assertEquals(-7, assignment.getCost(1));
    assertEquals(Status.OPTIMAL, assignment.solve());
    assertEquals(-2, assignment.getOptimalCost());
    assertEquals(1, assignment.getRightMate(0));
    assertEquals(0, assignment.getRightMate(1));
    assertEquals(5, assignment.getAssignmentCost(0));
    assertThrows(IllegalArgumentException.class, () -> assignment.addArcWithCost(-1, 0, 1));
  }

  @Test
  public void testInfeasible() {
    // Left node 1 has no arc.
    LinearSumAssignment noArc = new LinearSumAssignment();
    noArc.addArcWithCost(0, 0, 1);
    noArc.addArcWithCost(0, 1, 1);
    assertEquals(Status.INFEASIBLE, noArc.solve());
    assertEquals(0, noArc.getOptimalCost());
    assertThrows(IndexOutOfBoundsException.class, () -> noArc.getRightMate(0));

    // Both left nodes can only use right node 0.
    LinearSumAssignment conflict = new LinearSumAssignment();
    conflict.addArcWithCost(0, 0, 1);
    conflict.addArcWithCost(1, 0, 2);
    conflict.addArcWithCost(1, 1, 0);
    conflict.addArcWithCost(2, 0, 3);
    conflict.addArcWithCost(2, 1, 3);
    conflict.addArcWithCost(0, 2, 0);
    conflict.addArcWithCost(1, 2, 0);
    // A perfect matching exists: 0->2? then 1->1, 2->0.
    assertEquals(Status.OPTIMAL, conflict.solve());
    assertEquals(0 + 0 + 3, conflict.getOptimalCost());

    LinearSumAssignment hall = new LinearSumAssignment();
    hall.addArcWithCost(0, 0, 1);
    hall.addArcWithCost(1, 0, 1);
    hall.addArcWithCost(2, 1, 1);
    hall.addArcWithCost(2, 2, 1);
    assertEquals(Status.INFEASIBLE, hall.solve());
  }

  @Test
  public void testPossibleOverflow() {
    LinearSumAssignment big = new LinearSumAssignment();
    big.addArcWithCost(0, 0, Long.MAX_VALUE / 2);
    assertEquals(Status.POSSIBLE_OVERFLOW, big.solve());

    LinearSumAssignment negative = new LinearSumAssignment();
    negative.addArcWithCost(0, 0, Long.MIN_VALUE / 2);
    assertEquals(Status.POSSIBLE_OVERFLOW, negative.solve());

    // Scaled costs fit, but the price bounds are not representable.
    LinearSumAssignment bounds = new LinearSumAssignment();
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        bounds.addArcWithCost(i, j, Long.MAX_VALUE / 6);
      }
    }
    assertEquals(Status.POSSIBLE_OVERFLOW, bounds.solve());
  }

  @Test
  public void testStatusValues() {
    assertEquals(2, Status.POSSIBLE_OVERFLOW.swigValue());
    assertEquals(Status.INFEASIBLE, Status.swigToEnum(1));
    assertEquals(MaxFlow.Status.BAD_RESULT, MaxFlow.Status.swigToEnum(3));
  }

  @Test
  @SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE",
      justification = "seeded generator reused in the loop")
  public void testRandomProblems() {
    Random random = new Random(7L);
    for (int round = 0; round < 400; ++round) {
      final int n = 1 + random.nextInt(7);
      final boolean dense = random.nextBoolean();
      long[][] cost = new long[n][n];
      LinearSumAssignment assignment = new LinearSumAssignment();
      for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
          if (dense || random.nextInt(3) != 0) {
            cost[i][j] = random.nextInt(200) - 50;
            assignment.addArcWithCost(i, j, cost[i][j]);
          } else {
            cost[i][j] = Long.MIN_VALUE;
          }
        }
      }
      // Make sure node n - 1 exists on both sides so that getNumNodes() == n.
      if (assignment.getNumNodes() < n) {
        cost[n - 1][n - 1] = 0;
        assignment.addArcWithCost(n - 1, n - 1, 0);
      }
      final Long expected = GraphReference.assignment(cost);
      final Status status = assignment.solve();
      if (expected == null) {
        assertEquals(Status.INFEASIBLE, status, "round " + round);
        continue;
      }
      assertEquals(Status.OPTIMAL, status, "round " + round);
      assertEquals(expected.longValue(), assignment.getOptimalCost(), "round " + round);
      boolean[] used = new boolean[n];
      long sum = 0;
      for (int i = 0; i < n; ++i) {
        final int mate = assignment.getRightMate(i);
        assertTrue(!used[mate]);
        used[mate] = true;
        assertEquals(cost[i][mate], assignment.getAssignmentCost(i));
        sum += assignment.getAssignmentCost(i);
      }
      assertEquals(expected.longValue(), sum);
    }
  }

  @Test
  public void testDuplicateArcs() {
    LinearSumAssignment assignment = new LinearSumAssignment();
    assignment.addArcWithCost(0, 0, 10);
    assignment.addArcWithCost(0, 0, 3);
    assignment.addArcWithCost(1, 1, 4);
    assignment.addArcWithCost(1, 1, 8);
    assertEquals(Status.OPTIMAL, assignment.solve());
    assertEquals(7, assignment.getOptimalCost());
    assertEquals(3, assignment.getAssignmentCost(0));
    assertEquals(4, assignment.getAssignmentCost(1));
  }

  // OptimumMatchingOutOfOrder of ortools/graph/assignment_test.cc (OR-Tools 10.0): a complete
  // bipartite problem whose arcs are not given in tail-major order.
  @Test
  public void testOptimumMatchingOutOfOrder() {
    LinearSumAssignment assignment = new LinearSumAssignment();
    assertEquals(0, assignment.addArcWithCost(0, 0, 0));
    assertEquals(1, assignment.addArcWithCost(1, 0, 3));
    assertEquals(2, assignment.addArcWithCost(1, 1, 4));
    assertEquals(3, assignment.addArcWithCost(0, 1, 2));
    assertEquals(2, assignment.getNumNodes());
    assertEquals(4, assignment.getNumArcs());
    assertEquals(Status.OPTIMAL, assignment.solve());
    assertEquals(4, assignment.getOptimalCost());
    assertEquals(0, assignment.getRightMate(0));
    assertEquals(0, assignment.getAssignmentCost(0));
    assertEquals(1, assignment.getRightMate(1));
    assertEquals(4, assignment.getAssignmentCost(1));
  }

  // OptimumMatchingMultiArcs of ortools/graph/assignment_test.cc (OR-Tools 10.0): n * n arcs with
  // a duplicate, which is not a complete bipartite graph.
  @Test
  public void testOptimumMatchingMultiArcs() {
    LinearSumAssignment assignment = new LinearSumAssignment();
    assertEquals(0, assignment.addArcWithCost(0, 0, 0));
    assertEquals(1, assignment.addArcWithCost(1, 0, 3));
    assertEquals(2, assignment.addArcWithCost(1, 1, 4));
    assertEquals(3, assignment.addArcWithCost(1, 1, 2));
    assertEquals(2, assignment.getNumNodes());
    assertEquals(4, assignment.getNumArcs());
    assertEquals(Status.OPTIMAL, assignment.solve());
    assertEquals(2, assignment.getOptimalCost());
    assertEquals(0, assignment.getRightMate(0));
    assertEquals(0, assignment.getAssignmentCost(0));
    assertEquals(1, assignment.getRightMate(1));
    assertEquals(2, assignment.getAssignmentCost(1));
  }

  /**
   * A complete bipartite problem is solved on a complete bipartite graph (OR-Tools 10.0
   * SolveIfCompleteBipartite), whose arcs are identified by their left and right node: with equal
   * costs the returned optimal assignment doesn't depend on the order in which the arcs were
   * added. Which of the tied assignments it is was not compared with a C++ build, so the test
   * asserts only the order independence, the optimal cost and that the mates are a permutation.
   */
  @Test
  public void testCompleteBipartiteTieBreaking() {
    for (int n = 2; n <= 4; ++n) {
      int[][] mates = new int[2][n];
      for (int r = 0; r < 2; ++r) {
        final boolean reversed = r == 1;
        LinearSumAssignment assignment = new LinearSumAssignment();
        for (int i = 0; i < n * n; ++i) {
          final int k = reversed ? n * n - 1 - i : i;
          assignment.addArcWithCost(k / n, k % n, 7);
        }
        assertEquals(Status.OPTIMAL, assignment.solve());
        assertEquals(7L * n, assignment.getOptimalCost());
        boolean[] used = new boolean[n];
        for (int left = 0; left < n; ++left) {
          mates[r][left] = assignment.getRightMate(left);
          assertTrue(mates[r][left] >= 0 && mates[r][left] < n && !used[mates[r][left]],
              "n=" + n + " reversed=" + reversed + " left=" + left);
          used[mates[r][left]] = true;
        }
      }
      assertArrayEquals(mates[0], mates[1], "n=" + n);
    }
  }
}
