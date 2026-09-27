package org.matheclipse.graphtheory.eval;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.expression.data.IExprEdge;

public class GraphUtil {

  /**
   * The simple cycles of a graph as edge sequences. A directed edge is followed along its
   * direction, an undirected one either way, but an edge is never used twice in a cycle. A cycle of
   * undirected edges only is reported once, not once per direction.
   *
   * @param view the graph
   * @param minLength the smallest number of edges of a cycle
   * @param maxLength the largest number of edges of a cycle
   * @param limit the largest number of cycles to find
   * @param through the 0-based position of a vertex all cycles must pass through, or
   *        <code>-1</code>
   * @return a list of <code>{vertices, edges}</code> pairs; <code>vertices[i]</code> is where edge
   *         <code>edges[i]</code> starts
   */
  public static List<int[][]> simpleCycles(GraphView view, int minLength, int maxLength, int limit,
      int through) {
    List<int[][]> result = new ArrayList<int[][]>();
    CycleSearch search = new CycleSearch(view, minLength, maxLength, limit, result);
    if (through >= 0) {
      search.run(through, false);
    } else {
      for (int s = 0; s < view.n && result.size() < limit; s++) {
        search.run(s, true);
      }
    }
    return result;
  }

  /**
   * A Hamiltonian cycle search by backtracking, neighbours taken in <code>EdgeList</code> order.
   *
   * @param view the graph
   * @param limit the largest number of cycles to find
   * @param maxNodes the size of the search tree after which the search gives up
   * @return a list of <code>{vertices, edges}</code> pairs as {@link #simpleCycles}, or
   *         <code>null</code> if the search gave up
   */
  public static List<int[][]> hamiltonianCycles(GraphView view, int limit, long maxNodes) {
    List<int[][]> result = new ArrayList<int[][]>();
    if (view.n < 2) {
      return result;
    }
    for (int v = 0; v < view.n; v++) {
      // every vertex needs a way in and a way out along distinct edges
      int others = 0;
      for (int e : view.out[v]) {
        if (view.other(e, v) != v) {
          others++;
        }
      }
      if (others == 0 || view.in[v].length == 0 || (!view.hasDirectedEdge() && others < 2)) {
        return result;
      }
    }
    CycleSearch search = new CycleSearch(view, view.n, view.n, limit, result);
    search.maxNodes = maxNodes;
    search.run(0, false);
    return search.exhausted ? null : result;
  }

  /** Depth first search for simple cycles starting and ending at one vertex. */
  private static final class CycleSearch {
    final GraphView view;
    final int minLength;
    final int maxLength;
    final int limit;
    final List<int[][]> result;
    final boolean[] onPath;
    final int[] pathVertices;
    final int[] pathEdges;
    long maxNodes = Long.MAX_VALUE;
    long nodes = 0;
    boolean exhausted = false;
    int start;
    boolean higherOnly;

    CycleSearch(GraphView view, int minLength, int maxLength, int limit, List<int[][]> result) {
      this.view = view;
      this.minLength = Math.max(1, minLength);
      this.maxLength = Math.min(maxLength, Math.max(view.n, 1));
      this.limit = limit;
      this.result = result;
      onPath = new boolean[view.n];
      pathVertices = new int[view.n + 1];
      pathEdges = new int[view.n + 1];
    }

    /**
     * Find the cycles through <code>start</code>.
     *
     * @param higherOnly only visit vertices after <code>start</code>, so every cycle is found from
     *        its first vertex only
     */
    void run(int start, boolean higherOnly) {
      this.start = start;
      this.higherOnly = higherOnly;
      onPath[start] = true;
      pathVertices[0] = start;
      extend(start, 0);
      onPath[start] = false;
    }

    private void extend(int v, int length) {
      if (result.size() >= limit || exhausted) {
        return;
      }
      if (++nodes > maxNodes) {
        exhausted = true;
        return;
      }
      if ((nodes & 0xFFF) == 0) {
        org.matheclipse.core.basic.OperationSystem.checkInterrupt();
      }
      for (int e : view.out[v]) {
        if (length > 0 && e == pathEdges[length - 1]) {
          // an undirected edge can't be walked back
          continue;
        }
        int w = view.other(e, v);
        if (w == start) {
          if (length + 1 >= minLength && length + 1 <= maxLength) {
            pathEdges[length] = e;
            report(length + 1);
            if (result.size() >= limit) {
              return;
            }
          }
          continue;
        }
        if (onPath[w] || (higherOnly && w < start) || length + 1 >= maxLength) {
          continue;
        }
        onPath[w] = true;
        pathEdges[length] = e;
        pathVertices[length + 1] = w;
        extend(w, length + 1);
        onPath[w] = false;
        if (result.size() >= limit || exhausted) {
          return;
        }
      }
    }

    private void report(int length) {
      boolean allUndirected = true;
      for (int i = 0; i < length; i++) {
        if (!view.undirected[pathEdges[i]]) {
          allUndirected = false;
          break;
        }
      }
      if (allUndirected && length > 1) {
        // the reverse walk is the same cycle; keep the direction with the smaller first edge
        int first = pathEdges[0];
        int last = pathEdges[length - 1];
        if (first == last) {
          return;
        }
        if (first > last) {
          return;
        }
      }
      result.add(new int[][] {java.util.Arrays.copyOf(pathVertices, length),
          java.util.Arrays.copyOf(pathEdges, length)});
    }
  }

  /**
   * The edges of a cycle or path as <code>DirectedEdge</code> and <code>UndirectedEdge</code>
   * expressions, the undirected ones written in the walk direction.
   */
  public static IAST walkEdges(GraphView view, int[] vertices, int[] edges) {
    IASTAppendable result = F.ListAlloc(edges.length);
    for (int i = 0; i < edges.length; i++) {
      IExpr from = view.vertex(vertices[i]);
      IExpr to = view.vertex(view.other(edges[i], vertices[i]));
      result.append(
          view.undirected[edges[i]] ? F.UndirectedEdge(from, to) : F.DirectedEdge(from, to));
    }
    return result;
  }

  /**
   * A minimum vertex cover which is the lexicographically smallest one in <code>VertexList</code>
   * order. The edge directions are ignored; a vertex with a self-loop belongs to every cover.
   *
   * @param view the graph
   * @param maxNodes the size of the search tree after which the search gives up
   * @return the 0-based positions of the cover in ascending order, or <code>null</code> if the
   *         search gave up
   */
  public static int[] minimumVertexCover(GraphView view, long maxNodes) {
    VertexCoverSearch search = new VertexCoverSearch(view, maxNodes);
    int k = 0;
    while (!search.coverable(k)) {
      if (search.exhausted) {
        return null;
      }
      k++;
    }
    // prefer each vertex in turn: include it if some minimum cover still contains it
    for (int v = 0; v < view.n; v++) {
      if (search.state[v] != VertexCoverSearch.FREE) {
        continue;
      }
      search.state[v] = VertexCoverSearch.IN;
      if (!search.coverable(k - search.count(VertexCoverSearch.IN))) {
        search.state[v] = VertexCoverSearch.OUT;
      }
      if (search.exhausted) {
        return null;
      }
    }
    int[] result = new int[search.count(VertexCoverSearch.IN)];
    int i = 0;
    for (int v = 0; v < view.n; v++) {
      if (search.state[v] == VertexCoverSearch.IN) {
        result[i++] = v;
      }
    }
    return result;
  }

  /** Branch and bound search for vertex covers with some vertices forced in or out. */
  private static final class VertexCoverSearch {
    static final byte FREE = 0;
    static final byte IN = 1;
    static final byte OUT = 2;

    final int[] edgeA;
    final int[] edgeB;
    final byte[] state;
    final long maxNodes;
    long nodes = 0;
    boolean exhausted = false;

    VertexCoverSearch(GraphView view, long maxNodes) {
      this.maxNodes = maxNodes;
      state = new byte[view.n];
      java.util.Set<Long> pairs = new java.util.LinkedHashSet<Long>();
      for (int e = 0; e < view.m; e++) {
        int a = Math.min(view.source[e], view.target[e]);
        int b = Math.max(view.source[e], view.target[e]);
        pairs.add(((long) a << 32) | b);
      }
      edgeA = new int[pairs.size()];
      edgeB = new int[pairs.size()];
      int i = 0;
      for (long pair : pairs) {
        edgeA[i] = (int) (pair >>> 32);
        edgeB[i] = (int) pair;
        i++;
      }
    }

    int count(byte value) {
      int c = 0;
      for (byte b : state) {
        if (b == value) {
          c++;
        }
      }
      return c;
    }

    /**
     * Whether the current state can be completed to a cover using at most <code>budget</code> more
     * vertices.
     */
    boolean coverable(int budget) {
      if (budget < 0) {
        return false;
      }
      if (++nodes > maxNodes) {
        exhausted = true;
        return false;
      }
      int uncovered = -1;
      // a lower bound: a greedy matching of the uncovered edges needs one vertex per edge
      boolean[] matched = new boolean[state.length];
      int matching = 0;
      for (int e = 0; e < edgeA.length; e++) {
        int a = edgeA[e];
        int b = edgeB[e];
        if (state[a] == IN || state[b] == IN) {
          continue;
        }
        if (state[a] == OUT && state[b] == OUT) {
          return false;
        }
        if (uncovered < 0) {
          uncovered = e;
        }
        if (!matched[a] && !matched[b]) {
          matched[a] = true;
          matched[b] = true;
          matching++;
        }
      }
      if (uncovered < 0) {
        return true;
      }
      if (matching > budget) {
        return false;
      }
      int a = edgeA[uncovered];
      int b = edgeB[uncovered];
      if (state[a] == OUT || a == b) {
        return force(b, budget);
      }
      if (state[b] == OUT) {
        return force(a, budget);
      }
      // a in the cover, or a out and so all its neighbours in
      if (force(a, budget)) {
        return true;
      }
      if (exhausted) {
        return false;
      }
      state[a] = OUT;
      boolean result = coverable(budget);
      state[a] = FREE;
      return result;
    }

    private boolean force(int v, int budget) {
      state[v] = IN;
      boolean result = coverable(budget - 1);
      state[v] = FREE;
      return result;
    }
  }

  /**
   * A flow network with a node per vertex and an arc per edge direction, the capacity being the
   * edge weight (1 for an unweighted graph).
   *
   * @param forward gets the arc id of every edge in its direction
   * @param backward gets the arc id of the reverse direction of an undirected edge, else -1
   */
  public static FlowNetwork edgeNetwork(GraphView view, int[] forward, int[] backward) {
    FlowNetwork network = new FlowNetwork(view.n);
    for (int e = 0; e < view.m; e++) {
      int u = view.source[e];
      int v = view.target[e];
      forward[e] = -1;
      backward[e] = -1;
      if (u == v) {
        continue;
      }
      forward[e] = network.addArc(u, v, view.weight[e]);
      if (view.undirected[e]) {
        backward[e] = network.addArc(v, u, view.weight[e]);
      }
    }
    return network;
  }

  /**
   * A global minimum edge cut, weighted by the edge weights. Of the minimum cuts it returns the
   * smallest shore containing the last vertex, which is found by the maximum flows from the last
   * vertex to every other one. For a graph with directed edges the flows into the last vertex are
   * tried as well.
   *
   * @return <code>{value}</code> and the shore as a <code>boolean[]</code>; the edges counted leave
   *         the shore; <code>null</code> for fewer than 2 vertices
   */
  public static Object[] globalMinimumCut(GraphView view) {
    final int n = view.n;
    if (n < 2) {
      return null;
    }
    int[] forward = new int[view.m];
    int[] backward = new int[view.m];
    final int t = n - 1;
    double best = Double.POSITIVE_INFINITY;
    boolean[] bestShore = null;
    int bestSize = Integer.MAX_VALUE;
    for (int s = 0; s < n - 1; s++) {
      FlowNetwork network = edgeNetwork(view, forward, backward);
      double value = network.maxFlow(t, s);
      boolean[] shore = network.sourceSide();
      int size = count(shore);
      if (value < best - 1.0e-9 * Math.max(1.0, Math.abs(best))
          || (Math.abs(value - best) <= 1.0e-9 * Math.max(1.0, Math.abs(best))
              && size < bestSize)) {
        best = value;
        bestShore = shore;
        bestSize = size;
      }
    }
    if (view.hasDirectedEdge()) {
      for (int s = 0; s < n - 1; s++) {
        FlowNetwork network = edgeNetwork(view, forward, backward);
        double value = network.maxFlow(s, t);
        if (value < best - 1.0e-9 * Math.max(1.0, Math.abs(best))) {
          best = value;
          bestShore = network.sourceSide();
        }
      }
    }
    return new Object[] {best, bestShore};
  }

  /** The number of <code>true</code> entries. */
  public static int count(boolean[] set) {
    int c = 0;
    for (boolean b : set) {
      if (b) {
        c++;
      }
    }
    return c;
  }

  /**
   * The edges which leave the shore: directed edges from the shore to the rest and undirected edges
   * with one end in the shore, in <code>EdgeList</code> order.
   */
  public static List<Integer> cutEdges(GraphView view, boolean[] shore) {
    List<Integer> result = new ArrayList<Integer>();
    for (int e = 0; e < view.m; e++) {
      boolean a = shore[view.source[e]];
      boolean b = shore[view.target[e]];
      if (view.undirected[e] ? a != b : (a && !b)) {
        result.add(e);
      }
    }
    return result;
  }

  /**
   * A minimum set of vertices separating <code>s</code> from <code>t</code>, the one closest to
   * <code>t</code>, with the edge directions ignored.
   *
   * @return the 0-based positions in ascending order, <code>null</code> if <code>s</code> and
   *         <code>t</code> are adjacent (no set of vertices separates them)
   */
  public static int[] minimumVertexSeparator(GraphView view, int s, int t) {
    final int n = view.n;
    for (int e = 0; e < view.m; e++) {
      if ((view.source[e] == s && view.target[e] == t)
          || (view.source[e] == t && view.target[e] == s)) {
        return null;
      }
    }
    // vertex v: 2v -> 2v+1 with capacity 1 (unbounded for s and t)
    FlowNetwork network = new FlowNetwork(2 * n);
    for (int v = 0; v < n; v++) {
      network.addArc(2 * v, 2 * v + 1, v == s || v == t ? Double.POSITIVE_INFINITY : 1.0);
    }
    for (int e = 0; e < view.m; e++) {
      int u = view.source[e];
      int v = view.target[e];
      if (u != v) {
        network.addArc(2 * u + 1, 2 * v, Double.POSITIVE_INFINITY);
        network.addArc(2 * v + 1, 2 * u, Double.POSITIVE_INFINITY);
      }
    }
    network.maxFlow(2 * s + 1, 2 * t);
    boolean[] sinkSide = network.sinkSide();
    int size = 0;
    for (int v = 0; v < n; v++) {
      if (!sinkSide[2 * v] && sinkSide[2 * v + 1]) {
        size++;
      }
    }
    int[] result = new int[size];
    size = 0;
    for (int v = 0; v < n; v++) {
      if (!sinkSide[2 * v] && sinkSide[2 * v + 1]) {
        result[size++] = v;
      }
    }
    return result;
  }

  /**
   * A minimum vertex cut of the graph (edge directions ignored): the separators closest to a sink
   * are tried with the sinks taken from the last vertex backwards, the first smallest one wins. A
   * graph whose vertices are all adjacent gives all vertices but the last.
   */
  public static int[] globalMinimumVertexCut(GraphView view) {
    final int n = view.n;
    if (!view.isWeaklyConnected()) {
      return new int[0];
    }
    int[] best = null;
    for (int t = n - 1; t >= 0; t--) {
      for (int s = 0; s < n; s++) {
        if (s == t) {
          continue;
        }
        int[] separator = minimumVertexSeparator(view, s, t);
        if (separator != null && (best == null || separator.length < best.length)) {
          best = separator;
        }
      }
      if (best != null && best.length <= 1) {
        break;
      }
    }
    if (best == null) {
      best = new int[n - 1];
      for (int v = 0; v < n - 1; v++) {
        best[v] = v;
      }
    }
    return best;
  }

  /**
   * The simple paths from <code>s</code> to <code>t</code> whose length (edge count, or weight sum
   * if <code>weighted</code>) lies in <code>[minLength, maxLength]</code>, found depth first with
   * the neighbours in <code>EdgeList</code> order.
   *
   * @param limit stop after this many paths
   * @return the paths as 0-based vertex positions
   */
  public static List<int[]> simplePaths(GraphView view, int s, int t, double minLength,
      double maxLength, boolean weighted, int limit) {
    List<int[]> result = new ArrayList<int[]>();
    int[] path = new int[view.n];
    boolean[] onPath = new boolean[view.n];
    path[0] = s;
    onPath[s] = true;
    pathSearch(view, t, minLength, maxLength, weighted, limit, path, 1, 0.0, onPath, result);
    return result;
  }

  private static void pathSearch(GraphView view, int t, double minLength, double maxLength,
      boolean weighted, int limit, int[] path, int size, double length, boolean[] onPath,
      List<int[]> result) {
    int v = path[size - 1];
    if (v == t) {
      if (length >= minLength - 1.0e-12) {
        result.add(java.util.Arrays.copyOf(path, size));
      }
      return;
    }
    org.matheclipse.core.basic.OperationSystem.checkInterrupt();
    for (int e : view.out[v]) {
      int w = view.other(e, v);
      if (onPath[w]) {
        continue;
      }
      double newLength = length + (weighted ? view.weight[e] : 1.0);
      if (newLength > maxLength + 1.0e-12) {
        continue;
      }
      onPath[w] = true;
      path[size] = w;
      pathSearch(view, t, minLength, maxLength, weighted, limit, path, size + 1, newLength, onPath,
          result);
      onPath[w] = false;
      if (result.size() >= limit) {
        return;
      }
    }
  }

  /**
   * A Hamiltonian path, as <code>FindHamiltonianPath</code> returns it: for an undirected graph
   * with a Hamiltonian cycle <code>v1, v2, ..., vn</code> the path
   * <code>v2, v1, vn, ..., v3</code>; otherwise the first path found depth first, the start
   * vertices in <code>VertexList</code> order.
   *
   * @param s the 0-based start vertex or <code>-1</code>
   * @param t the 0-based end vertex or <code>-1</code>
   * @return <code>{}</code> if there is none, <code>null</code> if the search gave up
   */
  public static int[] hamiltonianPath(GraphView view, int s, int t, long maxNodes) {
    final int n = view.n;
    if (n < 2) {
      return new int[0];
    }
    if (s < 0 && !view.hasDirectedEdge()) {
      List<int[][]> cycles = hamiltonianCycles(view, 1, maxNodes);
      if (cycles == null) {
        return null;
      }
      if (!cycles.isEmpty()) {
        int[] c = cycles.get(0)[0];
        int[] path = new int[n];
        path[0] = c[1];
        path[1] = c[0];
        for (int i = 2; i < n; i++) {
          path[i] = c[n + 1 - i];
        }
        return path;
      }
    }
    long[] nodes = {0};
    int[] path = new int[n];
    boolean[] onPath = new boolean[n];
    for (int start = 0; start < n; start++) {
      if (s >= 0 && start != s) {
        continue;
      }
      path[0] = start;
      onPath[start] = true;
      Boolean found = hamiltonianPathSearch(view, t, path, 1, onPath, nodes, maxNodes);
      onPath[start] = false;
      if (found == null) {
        return null;
      }
      if (found) {
        return path;
      }
    }
    return new int[0];
  }

  private static Boolean hamiltonianPathSearch(GraphView view, int t, int[] path, int size,
      boolean[] onPath, long[] nodes, long maxNodes) {
    if (++nodes[0] > maxNodes) {
      return null;
    }
    int v = path[size - 1];
    if (size == view.n) {
      return t < 0 || v == t;
    }
    if (v == t) {
      return false;
    }
    if ((nodes[0] & 0xFFF) == 0) {
      org.matheclipse.core.basic.OperationSystem.checkInterrupt();
    }
    for (int e : view.out[v]) {
      int w = view.other(e, v);
      if (onPath[w]) {
        continue;
      }
      onPath[w] = true;
      path[size] = w;
      Boolean found = hamiltonianPathSearch(view, t, path, size + 1, onPath, nodes, maxNodes);
      onPath[w] = false;
      if (found == null || found) {
        return found;
      }
    }
    return false;
  }

  /**
   * Brandes' edge betweenness with the edge weights as lengths (Dijkstra); paths whose lengths
   * agree to a relative <code>1e-12</code> count as equally short. Ordered pairs of vertices are
   * counted.
   */
  public static double[] weightedEdgeBetweenness(GraphView view) {
    final int n = view.n;
    double[] score = new double[view.m];
    for (int s = 0; s < n; s++) {
      double[] distance = new double[n];
      java.util.Arrays.fill(distance, Double.POSITIVE_INFINITY);
      double[] sigma = new double[n];
      double[] delta = new double[n];
      boolean[] done = new boolean[n];
      int[] order = new int[n];
      int settled = 0;
      distance[s] = 0.0;
      sigma[s] = 1.0;
      java.util.PriorityQueue<double[]> queue =
          new java.util.PriorityQueue<double[]>((a, b) -> Double.compare(a[0], b[0]));
      queue.add(new double[] {0.0, s});
      while (!queue.isEmpty()) {
        double[] top = queue.poll();
        int u = (int) top[1];
        if (done[u]) {
          continue;
        }
        done[u] = true;
        order[settled++] = u;
        for (int e : view.out[u]) {
          int w = view.other(e, u);
          if (w == u) {
            continue;
          }
          double d = distance[u] + view.weight[e];
          double tolerance = 1.0e-12 * Math.max(1.0, Math.abs(d));
          if (d < distance[w] - tolerance) {
            distance[w] = d;
            sigma[w] = sigma[u];
            queue.add(new double[] {d, w});
          } else if (Math.abs(d - distance[w]) <= tolerance) {
            sigma[w] += sigma[u];
          }
        }
      }
      for (int i = settled - 1; i > 0; i--) {
        int w = order[i];
        for (int e : view.in[w]) {
          int v = view.other(e, w);
          if (v == w || Double.isInfinite(distance[v])) {
            continue;
          }
          double d = distance[v] + view.weight[e];
          if (Math.abs(d - distance[w]) <= 1.0e-12 * Math.max(1.0, Math.abs(d))) {
            double c = sigma[v] / sigma[w] * (1.0 + delta[w]);
            delta[v] += c;
            score[e] += c;
          }
        }
      }
    }
    return score;
  }

  /** Whether the graph is unweighted or all its edge weights are integers. */
  public static boolean hasIntegerWeights(GraphExpr<?> gex) {
    if (!gex.isWeightedGraph()) {
      return true;
    }
    for (Object edge : gex.toData().edgeSet()) {
      if (!(edge instanceof org.matheclipse.graphtheory.expression.data.ExprWeightedEdge)
          || !((org.matheclipse.graphtheory.expression.data.ExprWeightedEdge) edge).weightExpr()
              .isInteger()) {
        return false;
      }
    }
    return true;
  }

  /** A cut or flow value: an integer for integer weights, else a machine real. */
  public static IExpr weightNumber(double value, boolean integer) {
    if (Double.isInfinite(value)) {
      return org.matheclipse.core.expression.S.Infinity;
    }
    return integer ? F.ZZ(Math.round(value)) : F.num(value);
  }

  /**
   * Throw an {@link org.matheclipse.core.eval.exception.ASTElementLimitExceeded} if a generated
   * graph would have too many vertices or edges.
   */
  public static void checkGraphSize(long vertices, long edges) {
    if (vertices > org.matheclipse.core.basic.Config.MAX_GRAPH_VERTICES_SIZE) {
      org.matheclipse.core.eval.exception.ASTElementLimitExceeded.throwIt(vertices);
    }
    if (edges > 10L * org.matheclipse.core.basic.Config.MAX_GRAPH_VERTICES_SIZE) {
      org.matheclipse.core.eval.exception.ASTElementLimitExceeded.throwIt(edges);
    }
  }

  /**
   * An undirected graph on the vertices <code>1..n</code> whose edges are the given pairs, sorted
   * and without repetitions - such as <code>CirculantGraph</code>.
   *
   * @param pairs 1-based vertex pairs; a pair of equal vertices is ignored
   */
  public static GraphExpr<ExprEdge> sortedPairGraph(int n, java.util.Collection<int[]> pairs) {
    java.util.TreeSet<long[]> sorted = new java.util.TreeSet<long[]>(
        (x, y) -> x[0] != y[0] ? Long.compare(x[0], y[0]) : Long.compare(x[1], y[1]));
    for (int[] pair : pairs) {
      if (pair[0] != pair[1]) {
        sorted.add(new long[] {Math.min(pair[0], pair[1]), Math.max(pair[0], pair[1])});
      }
    }
    Graph<IExpr, ExprEdge> graph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    for (int i = 1; i <= n; i++) {
      graph.addVertex(F.ZZ(i));
    }
    for (long[] pair : sorted) {
      graph.addEdge(F.ZZ(pair[0]), F.ZZ(pair[1]));
    }
    return GraphExpr.newInstance(graph);
  }

  /**
   * The strongly connected components of a graph (an undirected edge joins its ends both ways).
   *
   * @return the component number of every vertex, numbered from 0 in the order the components are
   *         completed
   */
  public static int[] stronglyConnectedComponents(GraphView view) {
    final int n = view.n;
    int[] component = new int[n];
    java.util.Arrays.fill(component, -1);
    int[] index = new int[n];
    int[] low = new int[n];
    java.util.Arrays.fill(index, -1);
    boolean[] onStack = new boolean[n];
    int[] stack = new int[n];
    int stackSize = 0;
    int[] callVertex = new int[n];
    int[] callEdge = new int[n];
    int counter = 0;
    int components = 0;
    for (int root = 0; root < n; root++) {
      if (index[root] >= 0) {
        continue;
      }
      int depth = 0;
      callVertex[0] = root;
      callEdge[0] = 0;
      index[root] = low[root] = counter++;
      stack[stackSize++] = root;
      onStack[root] = true;
      while (depth >= 0) {
        int v = callVertex[depth];
        if (callEdge[depth] < view.out[v].length) {
          int w = view.other(view.out[v][callEdge[depth]++], v);
          if (index[w] < 0) {
            index[w] = low[w] = counter++;
            stack[stackSize++] = w;
            onStack[w] = true;
            depth++;
            callVertex[depth] = w;
            callEdge[depth] = 0;
          } else if (onStack[w]) {
            low[v] = Math.min(low[v], index[w]);
          }
        } else {
          if (low[v] == index[v]) {
            int w;
            do {
              w = stack[--stackSize];
              onStack[w] = false;
              component[w] = components;
            } while (w != v);
            components++;
          }
          depth--;
          if (depth >= 0) {
            int u = callVertex[depth];
            low[u] = Math.min(low[u], low[v]);
          }
        }
      }
    }
    return component;
  }

  /** A linear operator on vertex vectors, <code>y = M x</code>. */
  @FunctionalInterface
  public interface VertexOperator {
    void apply(double[] x, double[] y);
  }

  /**
   * The Perron vectors of a non-negative operator on its blocks, how
   * <code>EigenvectorCentrality</code> and <code>HITSCentrality</code> combine them: a block
   * <code>C</code> of two or more vertices gets its Perron vector scaled to the total
   * <code>(|C|-1)/sum(|C'|-1)</code>, a single vertex gets 0.
   *
   * @param n the number of vertices
   * @param block the block number of every vertex; the operator must not mix blocks
   * @param operator the non-negative operator
   * @return <code>null</code> if a power iteration didn't converge
   */
  public static double[] blockPerronVectors(int n, int[] block, VertexOperator operator) {
    int blocks = 0;
    for (int b : block) {
      blocks = Math.max(blocks, b + 1);
    }
    int[] size = new int[blocks];
    for (int b : block) {
      size[b]++;
    }
    int total = 0;
    for (int b = 0; b < blocks; b++) {
      if (size[b] > 1) {
        total += size[b] - 1;
      }
    }
    double[] result = new double[n];
    if (total == 0) {
      return result;
    }
    // power iteration on M + I for all blocks at once, each block normalized on its own
    double[] x = new double[n];
    double[] y = new double[n];
    for (int v = 0; v < n; v++) {
      x[v] = size[block[v]] > 1 ? 1.0 / size[block[v]] : 0.0;
    }
    double[] sum = new double[blocks];
    boolean converged = false;
    for (int iteration = 0; iteration < 200000; iteration++) {
      operator.apply(x, y);
      java.util.Arrays.fill(sum, 0.0);
      for (int v = 0; v < n; v++) {
        y[v] = size[block[v]] > 1 ? y[v] + x[v] : 0.0;
        sum[block[v]] += y[v];
      }
      double change = 0.0;
      for (int v = 0; v < n; v++) {
        if (sum[block[v]] > 0.0) {
          y[v] /= sum[block[v]];
        }
        change = Math.max(change, Math.abs(y[v] - x[v]));
      }
      double[] t = x;
      x = y;
      y = t;
      if (change < 1.0e-15) {
        converged = true;
        break;
      }
    }
    if (!converged) {
      return null;
    }
    for (int v = 0; v < n; v++) {
      if (size[block[v]] > 1) {
        result[v] = x[v] * (size[block[v]] - 1) / total;
      }
    }
    return result;
  }

  /**
   * Eigenvector centrality: the Perron vector of every strongly connected component, combined by
   * {@link #blockPerronVectors}.
   *
   * @param in <code>true</code> for the in-centrality (a vertex scores the sum over its
   *        predecessors), <code>false</code> for the out-centrality
   */
  public static double[] eigenvectorCentrality(GraphView view, boolean in) {
    int[] component = stronglyConnectedComponents(view);
    return blockPerronVectors(view.n, component, (x, y) -> {
      for (int v = 0; v < view.n; v++) {
        double s = 0.0;
        for (int e : in ? view.in[v] : view.out[v]) {
          int u = view.other(e, v);
          if (component[u] == component[v]) {
            s += x[u];
          }
        }
        y[v] = s;
      }
    });
  }

  /**
   * HITS centrality <code>{authorities, hubs}</code>: the authorities are the block Perron vectors
   * ({@link #blockPerronVectors}) of <code>A^T.A</code>, whose blocks are the classes of vertices
   * sharing an in-neighbour; the hubs are <code>A.authorities</code>.
   */
  public static double[][] hitsCentrality(GraphView view) {
    final int n = view.n;
    // union-find over "shares an in-neighbour"
    int[] parent = new int[n];
    for (int v = 0; v < n; v++) {
      parent[v] = v;
    }
    for (int u = 0; u < n; u++) {
      int first = -1;
      for (int e : view.out[u]) {
        int w = view.other(e, u);
        if (first < 0) {
          first = w;
        } else {
          int a = find(parent, first);
          int b = find(parent, w);
          if (a != b) {
            parent[a] = b;
          }
        }
      }
    }
    int[] block = new int[n];
    int[] number = new int[n];
    java.util.Arrays.fill(number, -1);
    int blocks = 0;
    for (int v = 0; v < n; v++) {
      int r = find(parent, v);
      if (number[r] < 0) {
        number[r] = blocks++;
      }
      block[v] = number[r];
    }
    // a vertex without an in-neighbour is a block of its own which scores 0
    final double[] z = new double[n];
    double[] authorities = blockPerronVectors(n, block, (x, y) -> {
      // z = A.x, y = A^T.z
      for (int u = 0; u < n; u++) {
        double s = 0.0;
        for (int e : view.out[u]) {
          s += x[view.other(e, u)];
        }
        z[u] = s;
      }
      for (int v = 0; v < n; v++) {
        double s = 0.0;
        for (int e : view.in[v]) {
          s += z[view.other(e, v)];
        }
        y[v] = s;
      }
    });
    if (authorities == null) {
      return null;
    }
    double[] hubs = new double[n];
    for (int u = 0; u < n; u++) {
      double s = 0.0;
      for (int e : view.out[u]) {
        s += authorities[view.other(e, u)];
      }
      hubs[u] = s;
    }
    return new double[][] {authorities, hubs};
  }

  private static int find(int[] parent, int v) {
    while (parent[v] != v) {
      parent[v] = parent[parent[v]];
      v = parent[v];
    }
    return v;
  }

  /**
   * Brandes' betweenness centrality with the edge weights ignored.
   *
   * <p>
   * Vertex scores count unordered pairs of vertices in a graph without directed edges and ordered
   * pairs otherwise; edge scores always count ordered pairs, as <code>BetweennessCentrality</code>
   * and <code>EdgeBetweennessCentrality</code> do.
   *
   * @param view the graph
   * @param edgeScore if not <code>null</code>, an array of length <code>view.m</code> which gets
   *        the edge betweenness
   * @return the vertex betweenness in <code>VertexList</code> order
   */
  public static double[] betweenness(GraphView view, double[] edgeScore) {
    final int n = view.n;
    double[] score = new double[n];
    int[] distance = new int[n];
    double[] sigma = new double[n];
    double[] delta = new double[n];
    int[] order = new int[n];
    for (int s = 0; s < n; s++) {
      java.util.Arrays.fill(distance, -1);
      java.util.Arrays.fill(sigma, 0.0);
      java.util.Arrays.fill(delta, 0.0);
      distance[s] = 0;
      sigma[s] = 1.0;
      int head = 0;
      int tail = 0;
      order[tail++] = s;
      while (head < tail) {
        int v = order[head++];
        for (int e : view.out[v]) {
          int w = view.other(e, v);
          if (distance[w] < 0) {
            distance[w] = distance[v] + 1;
            order[tail++] = w;
          }
          if (distance[w] == distance[v] + 1) {
            sigma[w] += sigma[v];
          }
        }
      }
      for (int i = tail - 1; i > 0; i--) {
        int w = order[i];
        for (int e : view.in[w]) {
          int v = view.other(e, w);
          if (distance[v] >= 0 && distance[v] == distance[w] - 1) {
            double c = sigma[v] / sigma[w] * (1.0 + delta[w]);
            delta[v] += c;
            if (edgeScore != null) {
              edgeScore[e] += c;
            }
          }
        }
        score[w] += delta[w];
      }
    }
    if (!view.hasDirectedEdge()) {
      for (int v = 0; v < n; v++) {
        score[v] /= 2.0;
      }
    }
    return score;
  }

  /**
   * Test if a weighted graph has an edge with a negative weight, which Dijkstra's algorithm doesn't
   * allow.
   *
   * @param graph the graph
   * @return <code>true</code> if the graph is weighted and an edge weight is negative
   */
  public static <E> boolean hasNegativeEdgeWeight(Graph<IExpr, E> graph) {
    if (graph.getType().isWeighted()) {
      for (E edge : graph.edgeSet()) {
        if (graph.getEdgeWeight(edge) < 0.0) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Computes the connected components of the given graph expression, optionally filtering them by a
   * pattern.
   * 
   * @param graphExpr
   * @param pattern {@link F#NIL} if no filtering is desired
   * @param engine
   * @return a list of connected components as GraphExpr instances
   */
  public static IAST connectedGraphComponents(GraphExpr graphExpr, IExpr pattern,
      EvalEngine engine) {
    Graph<IExpr, ? extends IExprEdge> jGraph =
        (Graph<IExpr, ? extends IExprEdge>) graphExpr.toData();
    List<Set<IExpr>> connectedSets = connectedSets(graphExpr, pattern, false, engine);

    // Returns a list of components {c1, c2, ...}, where each component is a Graph.
    IASTAppendable resultList = F.ListAlloc(connectedSets.size());
    for (Set<IExpr> componentVertices : connectedSets) {
      // Extract the subgraph corresponding to the component vertices
      GraphExpr subgraph = GraphUtil.subgraph(jGraph, componentVertices);
      if (subgraph != null) {
        resultList.append(subgraph);
      }
    }

    return resultList;
  }

  /**
   * Computes the connected components of the given graph expression as lists of vertices,
   * optionally filtering them by a pattern.
   *
   * @param graphExpr
   * @param pattern {@link F#NIL} if no filtering is desired
   * @param engine
   * @return a list of connected components, each component being a list of vertices
   */
  public static IAST connectedComponents(GraphExpr graphExpr, IExpr pattern, EvalEngine engine) {
    return connectedComponents(graphExpr, pattern, false, engine);
  }

  /**
   * Computes the connected components of the given graph expression as lists of vertices,
   * optionally filtering them by a pattern.
   *
   * @param graphExpr
   * @param pattern {@link F#NIL} if no filtering is desired
   * @param weak if <code>true</code> ignore the direction of the edges of a directed graph
   * @param engine
   * @return a list of connected components, each component being a list of vertices
   */
  public static IAST connectedComponents(GraphExpr graphExpr, IExpr pattern, boolean weak,
      EvalEngine engine) {
    List<Set<IExpr>> connectedSets = connectedSets(graphExpr, pattern, weak, engine);

    IASTAppendable resultList = F.ListAlloc(connectedSets.size());
    for (Set<IExpr> componentVertices : connectedSets) {
      resultList.append(F.mapSet(componentVertices, x -> x));
    }
    return resultList;
  }

  /**
   * The weakly connected components in WMA's order: a connected graph gives its vertex list; else
   * each component lists its vertices in the post-order of a depth-first search which ignores the
   * edge directions, larger components first.
   *
   * @param graph the graph
   */
  private static <E> List<Set<IExpr>> weaklyConnectedSets(Graph<IExpr, E> graph) {
    List<Set<IExpr>> result = new ArrayList<>();
    if (new ConnectivityInspector<>(graph).isConnected()) {
      if (!graph.vertexSet().isEmpty()) {
        result.add(new LinkedHashSet<>(graph.vertexSet()));
      }
      return result;
    }
    Set<IExpr> visited = new HashSet<>();
    for (IExpr root : graph.vertexSet()) {
      if (!visited.add(root)) {
        continue;
      }
      Set<IExpr> component = new LinkedHashSet<>();
      Deque<IExpr> vertexStack = new ArrayDeque<>();
      Deque<Iterator<IExpr>> neighborStack = new ArrayDeque<>();
      vertexStack.push(root);
      neighborStack.push(Graphs.neighborListOf(graph, root).iterator());
      while (!vertexStack.isEmpty()) {
        Iterator<IExpr> neighbors = neighborStack.peek();
        if (neighbors.hasNext()) {
          IExpr next = neighbors.next();
          if (visited.add(next)) {
            vertexStack.push(next);
            neighborStack.push(Graphs.neighborListOf(graph, next).iterator());
          }
        } else {
          neighborStack.pop();
          component.add(vertexStack.pop());
        }
      }
      result.add(component);
    }
    // stable: equal sizes keep the order of their first vertex
    result.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed());
    return result;
  }

  /**
   * Determine the connected components of a graph in the order defined for
   * <code>ConnectedComponents</code> and filter them by an optional pattern.
   *
   * @param graphExpr
   * @param pattern {@link F#NIL} if no filtering is desired
   * @param weak if <code>true</code> ignore the direction of the edges of a directed graph
   * @param engine
   */
  private static List<Set<IExpr>> connectedSets(GraphExpr graphExpr, IExpr pattern, boolean weak,
      EvalEngine engine) {
    Graph<IExpr, ? extends IExprEdge> jGraph =
        (Graph<IExpr, ? extends IExprEdge>) graphExpr.toData();
    List<Set<IExpr>> connectedSets;

    // 2. Compute Components using JGraphT
    if (weak) {
      connectedSets = weaklyConnectedSets(jGraph);
    } else if (jGraph.getType().isDirected()) {
      // For directed graphs, strongly connected components are computed.
      // WMA specifies: "given in an order such that there are no edges from ci to ci+1".
      // This implies a Reverse Topological Sort (Sink components first).
      // KosarajuStrongConnectivityInspector returns components in Topological Order (Source to
      // Sink).
      // Therefore, we reverse the list.
      KosarajuStrongConnectivityInspector<IExpr, ? extends IExprEdge> inspector =
          new KosarajuStrongConnectivityInspector<>(jGraph);
      connectedSets = new ArrayList<>(inspector.stronglyConnectedSets());
      Collections.reverse(connectedSets);

    } else {
      // For undirected graphs (or weak components), vertices are in the same component if there is
      // a path ignoring the edge directions.
      ConnectivityInspector<IExpr, ? extends IExprEdge> inspector =
          new ConnectivityInspector<>(jGraph);
      connectedSets = new ArrayList<>(inspector.connectedSets());
      connectedSets.sort(Comparator.<Set<IExpr>>comparingInt(Set::size).reversed());
    }

    // Filter Components (if argument exists)
    if (pattern.isPresent()) {
      IExpr arg2 = pattern;
      List<Set<IExpr>> filteredSets = new ArrayList<>();

      if (arg2.isList()) {
        // Case: ConnectedGraphComponents(g, {v1, v2, ...})
        // Keep component if it contains any of the specified vertices.
        Set<IExpr> filterVertices = new HashSet<>();
        for (IExpr v : ((IAST) arg2)) {
          filterVertices.add(v);
        }

        for (Set<IExpr> component : connectedSets) {
          if (!Collections.disjoint(component, filterVertices)) {
            filteredSets.add(component);
          }
        }
      } else {
        // Case: ConnectedGraphComponents(g, patt)
        // Keep component if any vertex matches the pattern.

        IPatternMatcher patt = engine.evalPatternMatcher(arg2);
        for (Set<IExpr> component : connectedSets) {
          boolean matchFound = false;
          for (IExpr vertex : component) {
            if (patt.test(vertex, engine)) {
              matchFound = true;
              break;
            }
          }
          if (matchFound) {
            filteredSets.add(component);
          }
        }
      }
      connectedSets = filteredSets;
    }

    return connectedSets;
  }

  private static GraphExpr subgraph(Graph<IExpr, ? extends IExprEdge> jGraph,
      Set<IExpr> componentVertices) {
    if (componentVertices.isEmpty()) {
      return null;
    }

    // 1. Create a new empty graph of the same type (directed/undirected/weighted/etc.)
    Graph<IExpr, ? extends IExprEdge> newGraph;
    GraphType t = jGraph.getType();
    if (t == null) {
      return null;
    }
    if (t.isDirected()) {
      newGraph = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    } else {
      newGraph = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    }

    for (IExpr vertex : componentVertices) {
      newGraph.addVertex(vertex);
    }

    for (IExprEdge edge : jGraph.edgeSet()) {
      IExpr source = edge.lhs();
      IExpr target = edge.rhs();

      if (componentVertices.contains(source) && componentVertices.contains(target)) {
        newGraph.addEdge(source, target);
      }
    }

    return GraphExpr.newInstance(newGraph);
  }

}
