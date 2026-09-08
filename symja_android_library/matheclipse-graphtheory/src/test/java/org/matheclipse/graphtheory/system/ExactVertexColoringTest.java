package org.matheclipse.graphtheory.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.graphtheory.alg.ExactVertexColoring;

/**
 * Solver level tests for {@link ExactVertexColoring} - the properties that are not observable
 * through the language, above all that the coloring really is minimal.
 */
public class ExactVertexColoringTest {

  /** Build symmetric adjacency lists from a list of {u, v} pairs. */
  private static int[][] adjacency(int n, int[][] edges) {
    List<List<Integer>> lists = new ArrayList<List<Integer>>();
    for (int i = 0; i < n; i++) {
      lists.add(new ArrayList<Integer>());
    }
    for (int[] e : edges) {
      if (e[0] == e[1] || lists.get(e[0]).contains(e[1])) {
        continue;
      }
      lists.get(e[0]).add(e[1]);
      lists.get(e[1]).add(e[0]);
    }
    int[][] adj = new int[n][];
    for (int i = 0; i < n; i++) {
      adj[i] = new int[lists.get(i).size()];
      for (int j = 0; j < adj[i].length; j++) {
        adj[i][j] = lists.get(i).get(j).intValue();
      }
    }
    return adj;
  }

  private static int[][] completeGraph(int n) {
    int[][] edges = new int[n * (n - 1) / 2][];
    int k = 0;
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        edges[k++] = new int[] {i, j};
      }
    }
    return adjacency(n, edges);
  }

  private static int[][] cycleGraph(int n) {
    int[][] edges = new int[n][];
    for (int i = 0; i < n; i++) {
      edges[i] = new int[] {i, (i + 1) % n};
    }
    return adjacency(n, edges);
  }

  private static void assertProper(int[][] adj, int[] colors) {
    for (int v = 0; v < adj.length; v++) {
      assertTrue(colors[v] >= 1, "colors are 1-based");
      for (int u : adj[v]) {
        assertTrue(colors[v] != colors[u], "adjacent vertices share a color");
      }
    }
  }

  /** Brute force chromatic number, for the differential test. Exponential - keep n small. */
  private static int bruteForceChromaticNumber(int[][] adj) {
    int n = adj.length;
    if (n == 0) {
      return 0;
    }
    for (int k = 1; k <= n; k++) {
      if (colorable(adj, new int[n], 0, k)) {
        return k;
      }
    }
    return n;
  }

  private static boolean colorable(int[][] adj, int[] colors, int v, int k) {
    if (v == adj.length) {
      return true;
    }
    for (int c = 1; c <= k; c++) {
      boolean ok = true;
      for (int u : adj[v]) {
        if (colors[u] == c) {
          ok = false;
          break;
        }
      }
      if (ok) {
        colors[v] = c;
        if (colorable(adj, colors, v + 1, k)) {
          return true;
        }
        colors[v] = 0;
      }
    }
    return false;
  }

  private static int usedColors(int[] colors) {
    int max = 0;
    for (int c : colors) {
      if (c > max) {
        max = c;
      }
    }
    return max;
  }

  @Test
  public void testClassicShapes() {
    assertEquals(0, new ExactVertexColoring(0, new int[0][]).solve(Integer.MAX_VALUE).usedColors);
    assertEquals(1, new ExactVertexColoring(1, adjacency(1, new int[0][])).solve(
        Integer.MAX_VALUE).usedColors);
    assertEquals(1, new ExactVertexColoring(4, adjacency(4, new int[0][])).solve(
        Integer.MAX_VALUE).usedColors);
    assertEquals(5, new ExactVertexColoring(5, completeGraph(5)).solve(
        Integer.MAX_VALUE).usedColors);
    assertEquals(2, new ExactVertexColoring(6, cycleGraph(6)).solve(Integer.MAX_VALUE).usedColors);
    assertEquals(3, new ExactVertexColoring(5, cycleGraph(5)).solve(Integer.MAX_VALUE).usedColors);

    // K(2,2): a greedy pass on an unlucky order says 3, a minimal search says 2
    int[][] k22 = adjacency(4, new int[][] {{0, 2}, {0, 3}, {1, 2}, {1, 3}});
    ExactVertexColoring.Result result = new ExactVertexColoring(4, k22).solve(Integer.MAX_VALUE);
    assertEquals(2, result.usedColors);
    assertProper(k22, result.colors);
  }

  @Test
  public void testBoundsBracketTheChromaticNumber() {
    int[][] c5 = cycleGraph(5);
    ExactVertexColoring solver = new ExactVertexColoring(5, c5);
    assertEquals(2, solver.cliqueLowerBound(), "the largest clique in C5 is an edge");
    assertTrue(usedColors(solver.dsatur()) >= 3, "DSATUR on C5 cannot beat chi = 3");
  }

  @Test
  public void testLargeGraphsAnswerFromTheBoundsAlone() {
    // the clique bound equals the DSATUR bound for both of these, so no search runs at all - this
    // is what keeps a complete graph from being hopeless
    int[][] k128 = completeGraph(128);
    ExactVertexColoring complete = new ExactVertexColoring(128, k128);
    assertEquals(128, complete.cliqueLowerBound());
    assertEquals(128, usedColors(complete.dsatur()));
    ExactVertexColoring.Result result = complete.solve(Integer.MAX_VALUE);
    assertEquals(128, result.usedColors);
    assertTrue(result.proven);
    assertProper(k128, result.colors);

    int[][] c128 = cycleGraph(128);
    ExactVertexColoring cycle = new ExactVertexColoring(128, c128);
    assertEquals(2, cycle.cliqueLowerBound());
    assertEquals(2, usedColors(cycle.dsatur()));
    assertEquals(2, cycle.solve(Integer.MAX_VALUE).usedColors);

    // above MAX_EXACT_VERTICES a bipartite graph still answers, because it never reaches the search
    int size = ExactVertexColoring.MAX_EXACT_VERTICES * 2;
    ExactVertexColoring big = new ExactVertexColoring(size, cycleGraph(size));
    ExactVertexColoring.Result bigResult = big.solve(Integer.MAX_VALUE);
    assertEquals(2, bigResult.usedColors);
    assertTrue(bigResult.proven);
  }

  @Test
  public void testBoundedColorRequest() {
    int[][] c5 = cycleGraph(5);
    assertNull(new ExactVertexColoring(5, c5).solve(2).colors, "C5 has no 2-coloring");
    assertNotNull(new ExactVertexColoring(5, c5).solve(3).colors);
    assertProper(c5, new ExactVertexColoring(5, c5).solve(3).colors);

    int[][] k5 = completeGraph(5);
    assertNull(new ExactVertexColoring(5, k5).solve(4).colors);
    assertEquals(5, new ExactVertexColoring(5, k5).solve(5).usedColors);
    // more colors than needed is still a valid request
    assertProper(k5, new ExactVertexColoring(5, k5).solve(9).colors);

    assertNull(new ExactVertexColoring(4, cycleGraph(4)).solve(0).colors);
    assertEquals(0, new ExactVertexColoring(0, new int[0][]).solve(0).usedColors);
  }

  /**
   * The differential test: minimality checked against an independent brute force chromatic number
   * over random graphs of every density. This is the assertion that fails first if the pruning is
   * ever made unsound.
   */
  @Test
  public void testMinimalityAgainstBruteForce() {
    Random random = new Random(42);
    for (int n = 1; n <= 10; n++) {
      for (int trial = 0; trial < 12; trial++) {
        double density = (trial + 1) / 13.0;
        List<int[]> edges = new ArrayList<int[]>();
        for (int i = 0; i < n; i++) {
          for (int j = i + 1; j < n; j++) {
            if (random.nextDouble() < density) {
              edges.add(new int[] {i, j});
            }
          }
        }
        int[][] adj = adjacency(n, edges.toArray(new int[edges.size()][]));
        ExactVertexColoring.Result result = new ExactVertexColoring(n, adj).solve(Integer.MAX_VALUE);
        assertTrue(result.proven);
        assertProper(adj, result.colors);
        assertEquals(bruteForceChromaticNumber(adj), result.usedColors,
            "chromatic number mismatch for n=" + n + " trial=" + trial);

        // a bounded request must agree with the minimal one about what is feasible
        int chi = result.usedColors;
        assertNotNull(new ExactVertexColoring(n, adj).solve(chi).colors);
        if (chi > 1) {
          assertNull(new ExactVertexColoring(n, adj).solve(chi - 1).colors);
        }
      }
    }
  }
}
