package org.matheclipse.graphtheory.eval;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.jgrapht.Graph;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.ExprEdge;

/**
 * An index based snapshot of a JGraphT graph: the vertices in <code>VertexList</code> order, the
 * edges in <code>EdgeList</code> order, and the adjacency in both directions.
 *
 * <p>
 * A mixed graph is stored by {@link org.matheclipse.graphtheory.expression.data.GraphExpr} as a
 * directed pseudograph whose undirected edges carry the {@link ExprEdge#isUndirected()} flag, so
 * JGraphT's algorithms see them as one way arcs. This view reads the flag: an undirected edge can be
 * traversed both ways, whatever the graph type.
 */
public final class GraphView {
  /** The vertices in <code>VertexList</code> order. */
  public final List<IExpr> vertices;
  /** The 0-based position of each vertex in {@link #vertices}. */
  public final Map<IExpr, Integer> index;
  /** The number of vertices. */
  public final int n;
  /** The number of edges. */
  public final int m;
  /** Source of each edge, in <code>EdgeList</code> order. */
  public final int[] source;
  /** Target of each edge, in <code>EdgeList</code> order. */
  public final int[] target;
  /** Whether an edge is undirected. */
  public final boolean[] undirected;
  /** The JGraphT weight of each edge (<code>1.0</code> for an unweighted graph). */
  public final double[] weight;
  /** Whether the graph carries edge weights. */
  public final boolean weighted;
  /**
   * <code>out[v]</code> the edges which leave <code>v</code>: its outgoing directed edges and its
   * undirected edges, in <code>EdgeList</code> order.
   */
  public final int[][] out;
  /** <code>in[v]</code> the edges which enter <code>v</code>, in <code>EdgeList</code> order. */
  public final int[][] in;

  private GraphView(Graph<IExpr, ?> graph) {
    vertices = new ArrayList<IExpr>(graph.vertexSet());
    n = vertices.size();
    index = new HashMap<IExpr, Integer>();
    for (int i = 0; i < n; i++) {
      index.put(vertices.get(i), i);
    }
    List<?> edges = new ArrayList<Object>(graph.edgeSet());
    m = edges.size();
    source = new int[m];
    target = new int[m];
    undirected = new boolean[m];
    weight = new double[m];
    weighted = graph.getType().isWeighted();
    final boolean undirectedGraph = graph.getType().isUndirected();
    int[] outCount = new int[n];
    int[] inCount = new int[n];
    for (int e = 0; e < m; e++) {
      Object edge = edges.get(e);
      @SuppressWarnings("unchecked")
      Graph<IExpr, Object> g = (Graph<IExpr, Object>) graph;
      source[e] = index.get(g.getEdgeSource(edge));
      target[e] = index.get(g.getEdgeTarget(edge));
      undirected[e] =
          undirectedGraph || (edge instanceof ExprEdge && ((ExprEdge) edge).isUndirected());
      weight[e] = weighted ? g.getEdgeWeight(edge) : 1.0;
      outCount[source[e]]++;
      inCount[target[e]]++;
      if (undirected[e] && source[e] != target[e]) {
        outCount[target[e]]++;
        inCount[source[e]]++;
      }
    }
    out = new int[n][];
    in = new int[n][];
    for (int v = 0; v < n; v++) {
      out[v] = new int[outCount[v]];
      in[v] = new int[inCount[v]];
    }
    Arrays.fill(outCount, 0);
    Arrays.fill(inCount, 0);
    for (int e = 0; e < m; e++) {
      out[source[e]][outCount[source[e]]++] = e;
      in[target[e]][inCount[target[e]]++] = e;
      if (undirected[e] && source[e] != target[e]) {
        out[target[e]][outCount[target[e]]++] = e;
        in[source[e]][inCount[source[e]]++] = e;
      }
    }
  }

  public static GraphView of(Graph<IExpr, ?> graph) {
    return new GraphView(graph);
  }

  /** The vertex at the 0-based position <code>v</code>. */
  public IExpr vertex(int v) {
    return vertices.get(v);
  }

  /** The other end of edge <code>e</code> seen from vertex <code>v</code>. */
  public int other(int e, int v) {
    return source[e] == v ? target[e] : source[e];
  }

  /** Whether the graph has both directed and undirected edges. */
  public boolean isMixed() {
    return hasDirectedEdge() && hasUndirectedEdge();
  }

  /** Whether some edge is directed. */
  public boolean hasDirectedEdge() {
    for (int e = 0; e < m; e++) {
      if (!undirected[e]) {
        return true;
      }
    }
    return false;
  }

  /** Whether some edge is undirected. */
  public boolean hasUndirectedEdge() {
    for (int e = 0; e < m; e++) {
      if (undirected[e]) {
        return true;
      }
    }
    return false;
  }

  /**
   * The number of edges from <code>v</code> to other vertices along the edge direction, the
   * <code>BFS</code> distance.
   *
   * @return <code>-1</code> for a vertex which can't be reached from <code>v</code>
   */
  public int[] bfsDistances(int v) {
    int[] distance = new int[n];
    Arrays.fill(distance, -1);
    distance[v] = 0;
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    queue.add(v);
    while (!queue.isEmpty()) {
      int u = queue.poll();
      for (int e : out[u]) {
        int w = other(e, u);
        if (distance[w] < 0) {
          distance[w] = distance[u] + 1;
          queue.add(w);
        }
      }
    }
    return distance;
  }

  /**
   * The weighted distances from <code>v</code> along the edge direction (Dijkstra; the edge weights
   * must not be negative).
   *
   * @return <code>Double.POSITIVE_INFINITY</code> for a vertex which can't be reached
   */
  public double[] weightedDistances(int v) {
    double[] distance = new double[n];
    Arrays.fill(distance, Double.POSITIVE_INFINITY);
    distance[v] = 0.0;
    PriorityQueue<double[]> queue =
        new PriorityQueue<double[]>((a, b) -> Double.compare(a[0], b[0]));
    queue.add(new double[] {0.0, v});
    while (!queue.isEmpty()) {
      double[] top = queue.poll();
      int u = (int) top[1];
      if (top[0] > distance[u]) {
        continue;
      }
      for (int e : out[u]) {
        int w = other(e, u);
        double d = distance[u] + weight[e];
        if (d < distance[w]) {
          distance[w] = d;
          queue.add(new double[] {d, w});
        }
      }
    }
    return distance;
  }

  /** Whether an edge weight is negative, which Dijkstra's algorithm doesn't allow. */
  public boolean hasNegativeWeight() {
    for (int e = 0; e < m; e++) {
      if (weight[e] < 0.0) {
        return true;
      }
    }
    return false;
  }

  /**
   * The distances from <code>v</code> along the edge directions: edge counts for an unweighted
   * graph, weight sums for a weighted one.
   *
   * @return <code>Double.POSITIVE_INFINITY</code> for a vertex which can't be reached
   */
  public double[] distances(int v) {
    if (weighted) {
      return weightedDistances(v);
    }
    int[] d = bfsDistances(v);
    double[] result = new double[n];
    for (int i = 0; i < n; i++) {
      result[i] = d[i] < 0 ? Double.POSITIVE_INFINITY : d[i];
    }
    return result;
  }

  /**
   * The eccentricity of every vertex, the largest distance to another vertex, and
   * <code>Double.POSITIVE_INFINITY</code> where some vertex can't be reached.
   */
  public double[] eccentricities() {
    double[] result = new double[n];
    for (int v = 0; v < n; v++) {
      double max = 0.0;
      for (double d : distances(v)) {
        max = Math.max(max, d);
      }
      result[v] = max;
    }
    return result;
  }

  /** Whether every vertex can be reached from every other one along the edge directions. */
  public boolean isStronglyConnected() {
    if (n == 0) {
      return true;
    }
    for (int v = 0; v < n; v++) {
      for (int d : bfsDistances(v)) {
        if (d < 0) {
          return false;
        }
      }
      if (!hasDirectedEdge()) {
        // one search from a single vertex decides an undirected graph
        return true;
      }
    }
    return true;
  }

  /** Whether the graph is connected when the edge directions are ignored. */
  public boolean isWeaklyConnected() {
    if (n == 0) {
      return true;
    }
    boolean[] seen = new boolean[n];
    ArrayDeque<Integer> stack = new ArrayDeque<Integer>();
    stack.push(0);
    seen[0] = true;
    int count = 1;
    while (!stack.isEmpty()) {
      int u = stack.pop();
      for (int[] edges : new int[][] {out[u], in[u]}) {
        for (int e : edges) {
          int w = other(e, u);
          if (!seen[w]) {
            seen[w] = true;
            count++;
            stack.push(w);
          }
        }
      }
    }
    return count == n;
  }

  /**
   * The neighbours of every vertex with the edge directions ignored, without repetitions and
   * without the vertex itself, in ascending <code>VertexList</code> order.
   */
  public int[][] undirectedNeighbours() {
    int[][] result = new int[n][];
    for (int v = 0; v < n; v++) {
      boolean[] mark = new boolean[n];
      for (int e : out[v]) {
        mark[other(e, v)] = true;
      }
      for (int e : in[v]) {
        mark[other(e, v)] = true;
      }
      mark[v] = false;
      int count = 0;
      for (boolean b : mark) {
        if (b) {
          count++;
        }
      }
      result[v] = new int[count];
      count = 0;
      for (int w = 0; w < n; w++) {
        if (mark[w]) {
          result[v][count++] = w;
        }
      }
    }
    return result;
  }

  /**
   * The successors of every vertex along the edge directions, without repetitions, in ascending
   * <code>VertexList</code> order.
   */
  public int[][] successors() {
    int[][] result = new int[n][];
    for (int v = 0; v < n; v++) {
      boolean[] mark = new boolean[n];
      for (int e : out[v]) {
        mark[other(e, v)] = true;
      }
      int count = 0;
      for (boolean b : mark) {
        if (b) {
          count++;
        }
      }
      result[v] = new int[count];
      count = 0;
      for (int w = 0; w < n; w++) {
        if (mark[w]) {
          result[v][count++] = w;
        }
      }
    }
    return result;
  }
}
