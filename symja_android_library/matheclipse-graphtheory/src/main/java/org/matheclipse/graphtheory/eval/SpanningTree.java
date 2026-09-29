package org.matheclipse.graphtheory.eval;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;

/**
 * <code>FindSpanningTree</code> as WMA computes it, on the full form
 * <code>Graph(vertices, edges, {EdgeWeight -> weights})</code> of a graph:
 *
 * <ul>
 * <li>undirected and weighted: Kruskal over the exact weights, ties broken by vertex position - a
 * minimum spanning forest;
 * <li>undirected and unweighted: a BFS forest;
 * <li>directed and weighted: the minimum arborescence (Chu-Liu/Edmonds), or the lightest branching
 * with the fewest roots;
 * <li>directed and unweighted: a BFS branching, rooted in decreasing DFS finishing time.
 * </ul>
 *
 * The tree keeps the input's vertex order and the input's edges, an undirected edge written with
 * the lower position first; its edges are sorted by position. <code>{g, v}</code> grows the tree
 * from <code>v</code>. A graph mixing directed and undirected edges, or a weight which isn't a real
 * number, gives {@link F#NIL}.
 */
public final class SpanningTree {

  private static final MathContext PRECISION = new MathContext(60);

  private final IAST vertices;
  private final int n;
  private final int m;
  private final int[] source;
  private final int[] target;
  private final boolean directed;
  /** the exact weights as given, <code>null</code> for an unweighted graph */
  private final IExpr[] weightExprs;
  /** the weights for comparisons, exact for rational and machine numbers */
  private final BigDecimal[] weights;

  private SpanningTree(IAST vertices, int[] source, int[] target, boolean directed,
      IExpr[] weightExprs, BigDecimal[] weights) {
    this.vertices = vertices;
    this.n = vertices.argSize();
    this.m = source.length;
    this.source = source;
    this.target = target;
    this.directed = directed;
    this.weightExprs = weightExprs;
    this.weights = weights;
  }

  /**
   * @param graph the full form <code>Graph(vertices, edges)</code> or
   *        <code>Graph(vertices, edges, {EdgeWeight -> weights, ...})</code>
   * @param root the vertex to grow the tree from, or {@link F#NIL}
   * @return the spanning tree as a <code>Graph(...)</code> expression to evaluate, or
   *         {@link F#NIL}
   */
  public static IExpr of(IAST graph, IExpr root, EvalEngine engine) {
    if (graph.argSize() < 2 || !graph.arg1().isList() || !graph.arg2().isList()) {
      return F.NIL;
    }
    IAST vertices = (IAST) graph.arg1();
    IAST edges = (IAST) graph.arg2();
    Map<IExpr, Integer> index = new HashMap<IExpr, Integer>();
    for (int i = 1; i < vertices.size(); i++) {
      index.put(vertices.get(i), i - 1);
    }
    int edgeCount = edges.argSize();
    int[] source = new int[edgeCount];
    int[] target = new int[edgeCount];
    int directedEdges = 0;
    for (int i = 1; i <= edgeCount; i++) {
      IExpr edge = edges.get(i);
      if (!(edge.isAST(S.DirectedEdge, 3) || edge.isAST(S.UndirectedEdge, 3))) {
        return F.NIL;
      }
      if (edge.isAST(S.DirectedEdge, 3)) {
        directedEdges++;
      }
      Integer s = index.get(edge.first());
      Integer t = index.get(edge.second());
      if (s == null || t == null) {
        return F.NIL;
      }
      source[i - 1] = s;
      target[i - 1] = t;
    }
    if (directedEdges != 0 && directedEdges != edgeCount) {
      // WMA leaves a mixed graph unevaluated
      return F.NIL;
    }
    IExpr[] weightExprs = null;
    BigDecimal[] weights = null;
    if (graph.argSize() >= 3 && graph.arg3().isList()) {
      for (IExpr option : (IAST) graph.arg3()) {
        if (option.isRuleAST() && option.first() == S.EdgeWeight && option.second().isList()
            && option.second().argSize() == edgeCount) {
          IAST list = (IAST) option.second();
          weightExprs = new IExpr[edgeCount];
          weights = new BigDecimal[edgeCount];
          for (int i = 0; i < edgeCount; i++) {
            weightExprs[i] = list.get(i + 1);
            weights[i] = decimal(weightExprs[i], engine);
            if (weights[i] == null) {
              // a weight which isn't a real number
              return F.NIL;
            }
          }
        }
      }
    }
    int rootIndex = -1;
    if (root.isPresent()) {
      Integer r = index.get(root);
      if (r == null) {
        return F.NIL;
      }
      rootIndex = r;
    }
    SpanningTree tree = new SpanningTree(vertices, source, target, directedEdges > 0,
        weightExprs, weights);
    return tree.compute(rootIndex);
  }

  /** The value of a real weight for comparisons: exact for rationals and machine numbers. */
  private static BigDecimal decimal(IExpr weight, EvalEngine engine) {
    try {
      if (weight.isRational()) {
        IRational r = (IRational) weight;
        return new BigDecimal(r.toBigNumerator()).divide(new BigDecimal(r.toBigDenominator()),
            PRECISION);
      }
      if (weight instanceof INum) {
        return new BigDecimal(((INum) weight).getRealPart());
      }
      if (weight.isRealResult()) {
        IExpr value = engine.evalN(weight, 60);
        if (value.isReal()) {
          return new BigDecimal(((IReal) value).apfloatValue().toString(true));
        }
      }
    } catch (RuntimeException rex) {
      // not a decimal number
    }
    return null;
  }

  private IExpr compute(int root) {
    boolean[] inTree = new boolean[m];
    boolean[] keepVertex = new boolean[n];
    Arrays.fill(keepVertex, true);
    if (directed) {
      if (root >= 0) {
        boolean[] reached = reachable(root);
        keepVertex = reached;
      }
      if (weights != null) {
        if (!minimumBranching(root, keepVertex, inTree)) {
          return F.NIL;
        }
      } else {
        bfsBranching(root, keepVertex, inTree);
      }
    } else {
      if (root >= 0) {
        keepVertex = reachable(root);
      }
      if (weights != null) {
        kruskal(keepVertex, inTree);
      } else {
        bfsForest(root, keepVertex, inTree);
      }
    }
    return result(keepVertex, inTree);
  }

  /** The vertices reachable from <code>root</code>, along the edges' directions. */
  private boolean[] reachable(int root) {
    boolean[] seen = new boolean[n];
    int[][] adjacency = adjacency(false);
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    seen[root] = true;
    queue.add(root);
    while (!queue.isEmpty()) {
      int v = queue.poll();
      for (int e : adjacency[v]) {
        int w = other(e, v);
        if (!seen[w]) {
          seen[w] = true;
          queue.add(w);
        }
      }
    }
    return seen;
  }

  private int other(int e, int v) {
    return source[e] == v ? target[e] : source[e];
  }

  /**
   * The edges leaving each vertex, in edge order; for an undirected graph (or with
   * <code>reverse</code>) both ends.
   */
  private int[][] adjacency(boolean reverse) {
    List<List<Integer>> lists = new ArrayList<List<Integer>>(n);
    for (int v = 0; v < n; v++) {
      lists.add(new ArrayList<Integer>());
    }
    for (int e = 0; e < m; e++) {
      if (!directed) {
        lists.get(source[e]).add(e);
        if (target[e] != source[e]) {
          lists.get(target[e]).add(e);
        }
      } else {
        lists.get(reverse ? target[e] : source[e]).add(e);
      }
    }
    int[][] result = new int[n][];
    for (int v = 0; v < n; v++) {
      // neighbours in vertex position order
      final int from = v;
      result[v] = lists.get(v).stream()
          .sorted(Comparator.comparingInt((Integer e) -> other(e, from)).thenComparingInt(e -> e))
          .mapToInt(Integer::intValue).toArray();
    }
    return result;
  }

  private void kruskal(boolean[] keepVertex, boolean[] inTree) {
    Integer[] order = new Integer[m];
    for (int e = 0; e < m; e++) {
      order[e] = e;
    }
    // ties broken by vertex position
    Arrays.sort(order, Comparator.comparing((Integer e) -> weights[e])
        .thenComparingInt(e -> Math.min(source[e], target[e]))
        .thenComparingInt(e -> Math.max(source[e], target[e])).thenComparingInt(e -> e));
    int[] parent = new int[n];
    for (int v = 0; v < n; v++) {
      parent[v] = v;
    }
    for (int e : order) {
      if (!keepVertex[source[e]] || !keepVertex[target[e]]) {
        continue;
      }
      int a = find(parent, source[e]);
      int b = find(parent, target[e]);
      if (a != b) {
        parent[a] = b;
        inTree[e] = true;
      }
    }
  }

  private static int find(int[] parent, int v) {
    while (parent[v] != v) {
      parent[v] = parent[parent[v]];
      v = parent[v];
    }
    return v;
  }

  private void bfsForest(int root, boolean[] keepVertex, boolean[] inTree) {
    int[][] adjacency = adjacency(false);
    boolean[] seen = new boolean[n];
    if (root >= 0) {
      bfs(root, adjacency, seen, inTree);
    }
    for (int v = 0; v < n; v++) {
      if (keepVertex[v] && !seen[v]) {
        bfs(v, adjacency, seen, inTree);
      }
    }
  }

  private void bfs(int start, int[][] adjacency, boolean[] seen, boolean[] inTree) {
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    seen[start] = true;
    queue.add(start);
    while (!queue.isEmpty()) {
      int v = queue.poll();
      for (int e : adjacency[v]) {
        int w = other(e, v);
        if (!seen[w]) {
          seen[w] = true;
          inTree[e] = true;
          queue.add(w);
        }
      }
    }
  }

  /** BFS from the vertices in decreasing DFS finishing time - WMA's unweighted branching. */
  private void bfsBranching(int root, boolean[] keepVertex, boolean[] inTree) {
    int[][] adjacency = adjacency(false);
    boolean[] seen = new boolean[n];
    if (root >= 0) {
      bfs(root, adjacency, seen, inTree);
      return;
    }
    // DFS finishing order
    boolean[] visited = new boolean[n];
    List<Integer> finished = new ArrayList<Integer>(n);
    for (int v = 0; v < n; v++) {
      if (!visited[v]) {
        dfs(v, adjacency, visited, finished);
      }
    }
    for (int i = finished.size() - 1; i >= 0; i--) {
      int v = finished.get(i);
      if (keepVertex[v] && !seen[v]) {
        bfs(v, adjacency, seen, inTree);
      }
    }
  }

  private void dfs(int start, int[][] adjacency, boolean[] visited, List<Integer> finished) {
    // iterative, a deep graph mustn't overflow the stack
    ArrayDeque<int[]> stack = new ArrayDeque<int[]>();
    visited[start] = true;
    stack.push(new int[] {start, 0});
    while (!stack.isEmpty()) {
      int[] frame = stack.peek();
      int v = frame[0];
      if (frame[1] < adjacency[v].length) {
        int w = other(adjacency[v][frame[1]++], v);
        if (!visited[w]) {
          visited[w] = true;
          stack.push(new int[] {w, 0});
        }
      } else {
        stack.pop();
        finished.add(v);
      }
    }
  }

  /**
   * The minimum arborescence from <code>root</code> over the vertices it reaches, or - without a
   * root - the lightest branching with the fewest roots: a super root with an edge to every vertex
   * that costs more than any two branchings can differ by.
   */
  private boolean minimumBranching(int root, boolean[] keepVertex, boolean[] inTree) {
    final boolean rooted = root >= 0;
    final int nodes = rooted ? n : n + 1;
    final int arcs = rooted ? m : m + n;
    int[] src = new int[arcs];
    int[] dst = new int[arcs];
    BigDecimal[] w = new BigDecimal[arcs];
    boolean[] skip = new boolean[nodes];
    for (int e = 0; e < m; e++) {
      src[e] = source[e];
      dst[e] = target[e];
      w[e] = weights[e];
    }
    if (rooted) {
      for (int v = 0; v < n; v++) {
        skip[v] = !keepVertex[v];
      }
    } else {
      BigDecimal heavy = BigDecimal.ZERO;
      for (int e = 0; e < m; e++) {
        heavy = heavy.add(weights[e].abs());
      }
      heavy = heavy.add(heavy).add(BigDecimal.ONE);
      for (int v = 0; v < n; v++) {
        src[m + v] = n;
        dst[m + v] = v;
        w[m + v] = heavy;
      }
      root = n;
    }
    int[] inEdge = directedMST(nodes, root, arcs, src, dst, w, skip);
    if (inEdge == null) {
      return false;
    }
    for (int v = 0; v < n; v++) {
      if (inEdge[v] >= 0 && inEdge[v] < m) {
        inTree[inEdge[v]] = true;
      }
    }
    return true;
  }

  /** A node of a leftist heap of incoming edges, with a lazy additive delta on its key. */
  private static final class HeapNode {
    BigDecimal key;
    BigDecimal delta = BigDecimal.ZERO;
    final int edge;
    int rank = 1;
    HeapNode left;
    HeapNode right;

    HeapNode(BigDecimal key, int edge) {
      this.key = key;
      this.edge = edge;
    }
  }

  private static void propagate(HeapNode a) {
    if (a.delta.signum() == 0) {
      return;
    }
    a.key = a.key.add(a.delta);
    if (a.left != null) {
      a.left.delta = a.left.delta.add(a.delta);
    }
    if (a.right != null) {
      a.right.delta = a.right.delta.add(a.delta);
    }
    a.delta = BigDecimal.ZERO;
  }

  /** <code>a</code> before <code>b</code>: the smaller key, then the higher tail - WMA's choice. */
  private static boolean before(int[] src, HeapNode a, HeapNode b) {
    int c = a.key.compareTo(b.key);
    if (c != 0) {
      return c < 0;
    }
    return src[a.edge] > src[b.edge];
  }

  private static HeapNode merge(int[] src, HeapNode a, HeapNode b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    propagate(a);
    propagate(b);
    if (before(src, b, a)) {
      HeapNode t = a;
      a = b;
      b = t;
    }
    a.right = merge(src, a.right, b);
    if (a.left == null || a.left.rank < a.right.rank) {
      HeapNode t = a.left;
      a.left = a.right;
      a.right = t;
    }
    a.rank = (a.right != null ? a.right.rank : 0) + 1;
    return a;
  }

  /**
   * Chu-Liu/Edmonds in the O(E log V) formulation (Tarjan; Gabow et al.): mergeable leftist heaps
   * of each super-vertex's incoming edges and a rollback union-find for the contracted cycles. Ties
   * between equal keys prefer the higher tail position, which reproduces WMA's choices.
   *
   * @return the chosen incoming edge of every node (<code>-1</code> for the root and skipped
   *         nodes), or <code>null</code> if a node can't be reached
   */
  private static int[] directedMST(int nodes, int root, int arcs, int[] src, int[] dst,
      BigDecimal[] w, boolean[] skip) {
    HeapNode[] heap = new HeapNode[nodes];
    for (int k = 0; k < arcs; k++) {
      if (!skip[dst[k]] && !skip[src[k]] && dst[k] != root && src[k] != dst[k]) {
        heap[dst[k]] = merge(src, heap[dst[k]], new HeapNode(w[k], k));
      }
    }
    // rollback union-find: union by size, no path compression
    int[] uf = new int[nodes];
    Arrays.fill(uf, -1);
    int[] historyNode = new int[2 * nodes];
    int[] historyValue = new int[2 * nodes];
    int[] historySize = new int[1];
    int[] seen = new int[nodes];
    int[] inEdge = new int[nodes];
    for (int v = 0; v < nodes; v++) {
      seen[v] = skip[v] ? v : -1;
      inEdge[v] = -1;
    }
    seen[root] = root;
    int[] path = new int[nodes];
    int[] queue = new int[nodes];
    List<int[]> cycles = new ArrayList<int[]>();
    for (int s = 0; s < nodes; s++) {
      int u = s;
      int qi = 0;
      while (seen[u] < 0) {
        if (heap[u] == null) {
          return null;
        }
        propagate(heap[u]);
        HeapNode top = heap[u];
        int e = top.edge;
        // every remaining incoming edge of u is now priced relative to e
        top.delta = top.delta.subtract(top.key);
        propagate(top);
        heap[u] = merge(src, top.left, top.right);
        queue[qi] = e;
        path[qi++] = u;
        seen[u] = s;
        u = findRollback(uf, src[e]);
        if (seen[u] == s) {
          // a cycle: contract it
          HeapNode cycle = null;
          int end = qi;
          int time = historySize[0];
          int x;
          do {
            x = path[--qi];
            cycle = merge(src, cycle, heap[x]);
          } while (join(uf, u, x, historyNode, historyValue, historySize));
          u = findRollback(uf, u);
          heap[u] = cycle;
          seen[u] = -1;
          int[] record = new int[3 + end - qi];
          record[0] = u;
          record[1] = time;
          record[2] = end - qi;
          for (int i = qi; i < end; i++) {
            record[3 + i - qi] = queue[i];
          }
          cycles.add(record);
        }
      }
      for (int i = 0; i < qi; i++) {
        inEdge[findRollback(uf, dst[queue[i]])] = queue[i];
      }
    }
    // expand the cycles, newest first
    for (int c = cycles.size() - 1; c >= 0; c--) {
      int[] record = cycles.get(c);
      rollback(uf, record[1], historyNode, historyValue, historySize);
      int in = inEdge[record[0]];
      for (int i = 0; i < record[2]; i++) {
        int edge = record[3 + i];
        inEdge[findRollback(uf, dst[edge])] = edge;
      }
      if (in >= 0) {
        inEdge[findRollback(uf, dst[in])] = in;
      }
    }
    inEdge[root] = -1;
    for (int v = 0; v < nodes; v++) {
      if (skip[v]) {
        inEdge[v] = -1;
      }
    }
    return inEdge;
  }

  /** The representative in the rollback union-find (no path compression). */
  private static int findRollback(int[] uf, int x) {
    while (uf[x] >= 0) {
      x = uf[x];
    }
    return x;
  }

  private static boolean join(int[] uf, int a, int b, int[] historyNode, int[] historyValue,
      int[] historySize) {
    a = findRollback(uf, a);
    b = findRollback(uf, b);
    if (a == b) {
      return false;
    }
    if (uf[a] > uf[b]) {
      int t = a;
      a = b;
      b = t;
    }
    historyNode[historySize[0]] = a;
    historyValue[historySize[0]++] = uf[a];
    historyNode[historySize[0]] = b;
    historyValue[historySize[0]++] = uf[b];
    uf[a] += uf[b];
    uf[b] = a;
    return true;
  }

  private static void rollback(int[] uf, int time, int[] historyNode, int[] historyValue,
      int[] historySize) {
    while (historySize[0] > time) {
      historySize[0]--;
      uf[historyNode[historySize[0]]] = historyValue[historySize[0]];
    }
  }

  /** The tree as <code>Graph(vertices, edges, {EdgeWeight -> weights})</code>. */
  private IExpr result(boolean[] keepVertex, boolean[] inTree) {
    IASTAppendable vertexList = F.ListAlloc(n);
    for (int v = 0; v < n; v++) {
      if (keepVertex[v]) {
        vertexList.append(vertices.get(v + 1));
      }
    }
    List<Integer> treeEdges = new ArrayList<Integer>();
    for (int e = 0; e < m; e++) {
      if (inTree[e]) {
        treeEdges.add(e);
      }
    }
    // sorted by position, an undirected edge written with the lower position first
    treeEdges.sort(Comparator
        .comparingInt((Integer e) -> directed ? source[e] : Math.min(source[e], target[e]))
        .thenComparingInt(e -> directed ? target[e] : Math.max(source[e], target[e])));
    IASTAppendable edgeList = F.ListAlloc(treeEdges.size());
    IASTAppendable weightList = F.ListAlloc(treeEdges.size());
    for (int e : treeEdges) {
      IExpr s = vertices.get(source[e] + 1);
      IExpr t = vertices.get(target[e] + 1);
      if (directed) {
        edgeList.append(F.DirectedEdge(s, t));
      } else if (source[e] <= target[e]) {
        edgeList.append(F.UndirectedEdge(s, t));
      } else {
        edgeList.append(F.UndirectedEdge(t, s));
      }
      if (weightExprs != null) {
        weightList.append(weightExprs[e]);
      }
    }
    if (weightExprs != null) {
      return F.function(S.Graph, vertexList, edgeList,
          F.list(F.Rule(S.EdgeWeight, weightList)));
    }
    return F.function(S.Graph, vertexList, edgeList);
  }
}
