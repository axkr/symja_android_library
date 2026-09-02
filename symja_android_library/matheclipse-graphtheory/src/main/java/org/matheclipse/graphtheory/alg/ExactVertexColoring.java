package org.matheclipse.graphtheory.alg;

import java.util.Arrays;
import java.util.BitSet;
import java.util.PriorityQueue;
import org.matheclipse.core.eval.exception.TimeoutException;

/**
 * Exact minimal vertex coloring of an undirected simple graph.
 *
 * <p>
 * The chromatic number is NP-hard, so a plain greedy pass is not enough: it returns a valid but
 * frequently larger coloring, and it fails silently. The search here is therefore bracketed between
 * two bounds and only searches the gap between them:
 *
 * <ul>
 * <li>{@link #dsatur()} is a good upper bound and a real coloring,
 * <li>{@link #cliqueLowerBound()} is a greedy-clique lower bound,
 * <li>when the two meet, the DSATUR coloring is already optimal and no search runs at all. This is
 * what makes a complete graph immediate instead of hopeless.
 * </ul>
 *
 * <p>
 * The graph is given as symmetric adjacency lists over the vertex indices <code>0 .. n-1</code>, so
 * the memory is <code>O(n + m)</code> and a large sparse graph can still be handled by the two
 * bounds alone. The branch-and-bound itself never runs above {@link #MAX_EXACT_VERTICES}
 * vertices.
 *
 * <p>
 * This class holds no {@code IExpr} state; {@link VertexColoringSupport} bridges a Symja graph to
 * it.
 */
public final class ExactVertexColoring {

  /**
   * Above this many vertices the branch-and-bound is skipped and the DSATUR coloring is returned
   * with {@link Result#proven} <code>false</code>. Bounds the recursion depth and the search
   * scratch. Note that trees, forests and bipartite graphs of <i>any</i> size never reach the search
   * at all: for them the clique bound already equals the DSATUR bound.
   */
  public static final int MAX_EXACT_VERTICES = 512;

  /**
   * A budget on branch-and-bound nodes. A node count rather than a wall clock deliberately: a
   * time-based cutoff would make the answer machine-dependent, whereas a node count returns the same
   * answer for the same graph on every machine. Responsiveness is
   * {@code TimeConstrained}'s job - see {@link #INTERRUPT_POLL_MASK}.
   */
  public static final long MAX_SEARCH_NODES = 8_000_000L;

  /** Poll {@link Thread#isInterrupted()} every 4096 search nodes. */
  private static final int INTERRUPT_POLL_MASK = 0xFFF;

  /** The largest number of highest-degree vertices used as greedy-clique starts. */
  private static final int MAX_CLIQUE_STARTS = 256;

  /** The outcome of {@link ExactVertexColoring#solve(int)}. */
  public static final class Result {
    /**
     * A proper coloring with 1-based color numbers in vertex-index order, or <code>null</code> when
     * no coloring within the requested color limit exists (or could be found).
     */
    public final int[] colors;

    /** The number of distinct colors in {@link #colors}, or <code>0</code>. */
    public final int usedColors;

    /**
     * <code>false</code> when the vertex cap or the node budget stopped the search, so
     * {@link #colors} is a valid coloring that was not proven minimal - or, for a bounded color
     * request, <code>null</code> without a proof that no such coloring exists.
     */
    public final boolean proven;

    private Result(int[] colors, int usedColors, boolean proven) {
      this.colors = colors;
      this.usedColors = usedColors;
      this.proven = proven;
    }
  }

  private final int n;
  private final int[][] adj;
  private final int[] degree;

  /* branch-and-bound state, valid only for the duration of a search */
  private int[] col;
  private int[] best;
  private int bestK;
  private int lowerBound;
  private boolean stopAtFirst;
  private long steps;
  private boolean aborted;
  private int[] undoStack;
  private int undoTop;
  private BitSet[] searchSat;
  private int[] searchSatCount;

  /**
   * @param n the number of vertices
   * @param adjacency symmetric adjacency lists over <code>0 .. n-1</code>, without self loops and
   *        without duplicate entries
   */
  public ExactVertexColoring(int n, int[][] adjacency) {
    this.n = n;
    this.adj = adjacency;
    this.degree = new int[n];
    for (int v = 0; v < n; v++) {
      degree[v] = adjacency[v].length;
      Arrays.sort(adjacency[v]);
    }
  }

  public int vertexCount() {
    return n;
  }

  /** Test adjacency by binary search in the shorter of the two neighbor lists. */
  public boolean isAdjacent(int u, int v) {
    int[] a = adj[u].length <= adj[v].length ? adj[u] : adj[v];
    int target = a == adj[u] ? v : u;
    return Arrays.binarySearch(a, target) >= 0;
  }

  /**
   * The DSATUR greedy coloring: repeatedly color the uncolored vertex whose neighbors already carry
   * the most distinct colors, breaking ties on degree and then on index so the result is
   * deterministic.
   *
   * @return 1-based colors in vertex-index order, or an empty array when <code>n == 0</code>
   */
  public int[] dsatur() {
    int[] colors = new int[n];
    if (n == 0) {
      return colors;
    }
    BitSet[] neighborColors = new BitSet[n];
    int[] saturation = new int[n];
    for (int v = 0; v < n; v++) {
      neighborColors[v] = new BitSet();
    }

    // lazy priority queue: an entry is stale when its saturation no longer matches
    PriorityQueue<int[]> queue = new PriorityQueue<>((x, y) -> {
      if (x[1] != y[1]) {
        return y[1] - x[1]; // higher saturation first
      }
      if (x[2] != y[2]) {
        return y[2] - x[2]; // then higher degree
      }
      return x[0] - y[0]; // then lower index
    });
    for (int v = 0; v < n; v++) {
      queue.add(new int[] {v, 0, degree[v]});
    }

    int colored = 0;
    while (colored < n) {
      int[] entry = queue.poll();
      if (entry == null) {
        break;
      }
      int v = entry[0];
      if (colors[v] != 0 || saturation[v] != entry[1]) {
        continue; // stale
      }
      int c = neighborColors[v].nextClearBit(1);
      colors[v] = c;
      colored++;
      for (int u : adj[v]) {
        if (colors[u] == 0 && !neighborColors[u].get(c)) {
          neighborColors[u].set(c);
          saturation[u]++;
          queue.add(new int[] {u, saturation[u], degree[u]});
        }
      }
    }
    return colors;
  }

  /**
   * A greedy clique grown from each of the highest-degree vertices in turn. Every clique needs one
   * color per member, so its size is a lower bound on the chromatic number.
   */
  public int cliqueLowerBound() {
    if (n == 0) {
      return 0;
    }
    Integer[] order = new Integer[n];
    for (int i = 0; i < n; i++) {
      order[i] = i;
    }
    Arrays.sort(order, (x, y) -> degree[x] != degree[y] ? degree[y] - degree[x] : x - y);

    int starts = Math.min(n, MAX_CLIQUE_STARTS);
    int bestSize = 1;
    int[] clique = new int[n];
    for (int s = 0; s < starts; s++) {
      int start = order[s];
      if (degree[start] + 1 <= bestSize) {
        break; // sorted by degree, so nothing further can beat the incumbent
      }
      int size = 0;
      clique[size++] = start;
      for (int i = 0; i < n; i++) {
        int v = order[i];
        if (v == start) {
          continue;
        }
        if (degree[v] + 1 <= size) {
          break;
        }
        boolean ok = true;
        for (int j = 0; j < size; j++) {
          if (!isAdjacent(v, clique[j])) {
            ok = false;
            break;
          }
        }
        if (ok) {
          clique[size++] = v;
        }
      }
      if (size > bestSize) {
        bestSize = size;
      }
    }
    return bestSize;
  }

  /**
   * Color the graph.
   *
   * @param limit the largest number of colors the caller will accept. Pass
   *        {@link Integer#MAX_VALUE} to ask for a <i>minimal</i> coloring; any smaller value turns
   *        this into a feasibility search that stops at the first coloring within the limit.
   */
  public Result solve(int limit) {
    if (n == 0) {
      return new Result(new int[0], 0, true);
    }
    boolean minimal = limit == Integer.MAX_VALUE;
    int[] greedy = dsatur();
    int ub = 0;
    for (int c : greedy) {
      if (c > ub) {
        ub = c;
      }
    }
    if (!minimal && ub <= limit) {
      // a coloring within the limit is all a bounded request promises
      return new Result(greedy, ub, true);
    }
    int lb = cliqueLowerBound();
    if (!minimal && lb > limit) {
      return new Result(null, 0, true);
    }
    if (lb >= ub) {
      // the clique needs lb colors and DSATUR exhibits ub of them, so ub is optimal
      return minimal ? new Result(greedy, ub, true) : new Result(null, 0, true);
    }
    if (n > MAX_EXACT_VERTICES) {
      return minimal ? new Result(greedy, ub, false) : new Result(null, 0, false);
    }

    buildSearchState(greedy, ub, lb, limit, !minimal);
    branchAndBound(0, 0);

    if (aborted) {
      return minimal ? new Result(best, bestK, false) : new Result(null, 0, false);
    }
    if (minimal) {
      return new Result(best, bestK, true);
    }
    return bestK <= limit ? new Result(best, bestK, true) : new Result(null, 0, true);
  }

  private void buildSearchState(int[] greedy, int ub, int lb, int limit, boolean bounded) {
    long totalDegree = 0;
    for (int v = 0; v < n; v++) {
      totalDegree += degree[v];
    }
    col = new int[n];
    searchSat = new BitSet[n];
    for (int v = 0; v < n; v++) {
      searchSat[v] = new BitSet();
    }
    searchSatCount = new int[n];
    undoStack = new int[(int) Math.min(totalDegree + n, Integer.MAX_VALUE - 8)];
    undoTop = 0;
    steps = 0;
    aborted = false;
    lowerBound = lb;
    stopAtFirst = bounded;
    if (bounded) {
      // a pure feasibility search: no incumbent, and any coloring with at most `limit` colors wins
      best = null;
      bestK = limit + 1;
    } else {
      best = greedy.clone();
      bestK = ub;
    }
  }

  private void branchAndBound(int ncolored, int used) {
    if (aborted || used >= bestK) {
      return;
    }
    if (best != null && bestK <= lowerBound) {
      return; // provably optimal
    }
    if (ncolored == n) {
      best = col.clone();
      bestK = used;
      return;
    }
    if (++steps > MAX_SEARCH_NODES) {
      aborted = true;
      return;
    }
    if ((steps & INTERRUPT_POLL_MASK) == 0 && Thread.currentThread().isInterrupted()) {
      // cooperate with TimeConstrained: the engine polls once per rewrite step, which never
      // happens inside a single long-running builtin, so the search has to poll for itself
      throw TimeoutException.TIMED_OUT;
    }

    // pick the uncolored vertex of maximum saturation, ties on degree then index
    int v = -1;
    int bestSat = -1;
    int bestDeg = -1;
    for (int u = 0; u < n; u++) {
      if (col[u] != 0) {
        continue;
      }
      int sat = searchSatCount[u];
      if (sat > bestSat || (sat == bestSat && degree[u] > bestDeg)) {
        v = u;
        bestSat = sat;
        bestDeg = degree[u];
      }
    }

    // colors are interchangeable labels, so opening color used+2 before used+1 would only rename a
    // branch already explored - hence the used+1 cap
    // with a bounded request bestK starts at limit+1, so this cap is also the color limit
    int maxColor = Math.min(used + 1, bestK - 1);
    for (int c = 1; c <= maxColor; c++) {
      if (searchSat[v].get(c)) {
        continue;
      }
      int mark = undoTop;
      assign(v, c);
      branchAndBound(ncolored + 1, c > used ? c : used);
      undo(v, mark);
      if (aborted || (best != null && (stopAtFirst || bestK <= lowerBound))) {
        return;
      }
    }
  }

  private void assign(int v, int c) {
    col[v] = c;
    for (int u : adj[v]) {
      if (col[u] == 0 && !searchSat[u].get(c)) {
        searchSat[u].set(c);
        searchSatCount[u]++;
        undoStack[undoTop++] = u;
      }
    }
  }

  private void undo(int v, int mark) {
    int c = col[v];
    while (undoTop > mark) {
      int u = undoStack[--undoTop];
      searchSat[u].clear(c);
      searchSatCount[u]--;
    }
    col[v] = 0;
  }
}
