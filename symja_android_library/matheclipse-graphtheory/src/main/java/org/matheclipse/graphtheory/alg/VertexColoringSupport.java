package org.matheclipse.graphtheory.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.color.BrownBacktrackColoring;
import org.jgrapht.alg.color.ChordalGraphColoring;
import org.jgrapht.alg.color.GreedyColoring;
import org.jgrapht.alg.color.LargestDegreeFirstColoring;
import org.jgrapht.alg.color.RandomGreedyColoring;
import org.jgrapht.alg.color.SaturationDegreeColoring;
import org.jgrapht.alg.color.SmallestDegreeLastColoring;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.IExprEdge;

/**
 * Bridges a Symja graph to {@link ExactVertexColoring} and to the JGraphT coloring algorithms, and
 * holds the pieces that {@code FindVertexColoring} and {@code VertexChromaticNumber} would otherwise
 * duplicate.
 *
 * <p>
 * Vertices are indexed in {@code VertexList} order, which is the order every coloring result has to
 * be reported in. Edge direction and edge weights are ignored - an edge constrains both of its
 * endpoints whichever way it points - and self loops are dropped here, so a caller that cares about
 * them has to test for them first.
 */
public final class VertexColoringSupport {

  /** The vertex ordering plus the adjacency lists a coloring algorithm needs. */
  public static final class GraphData {
    /** The vertices in {@code VertexList} order, as a 1-based {@link IAST}. */
    public final IAST vertexList;

    /** Symmetric adjacency lists over <code>0 .. n-1</code>, self loops removed. */
    public final int[][] adjacency;

    /** Maps a vertex to its 0-based index. */
    public final Map<IExpr, Integer> index;

    private GraphData(IAST vertexList, int[][] adjacency, Map<IExpr, Integer> index) {
      this.vertexList = vertexList;
      this.adjacency = adjacency;
      this.index = index;
    }

    public int vertexCount() {
      return vertexList.argSize();
    }
  }

  private VertexColoringSupport() {}

  /**
   * Index the vertices in {@code VertexList} order and build symmetric adjacency lists from the edge
   * set, dropping self loops, duplicate edges and edge directions.
   */
  public static GraphData normalize(Graph<IExpr, ?> graph) {
    IAST vertexList = org.matheclipse.graphtheory.expression.data.GraphExpr.vertexToIExpr(graph);
    int n = vertexList.argSize();
    Map<IExpr, Integer> index = new HashMap<IExpr, Integer>(n * 2);
    for (int i = 1; i <= n; i++) {
      index.put(vertexList.get(i), i - 1);
    }

    // gather each vertex's neighbors in a set first, so a multigraph and a pair of opposite
    // directed edges both collapse to a single adjacency
    @SuppressWarnings("unchecked")
    Set<? extends IExprEdge> edges = (Set<? extends IExprEdge>) graph.edgeSet();
    java.util.Set<Integer>[] neighbors = newSetArray(n);
    for (IExprEdge e : edges) {
      Integer u = index.get(e.lhs());
      Integer v = index.get(e.rhs());
      if (u == null || v == null || u.intValue() == v.intValue()) {
        continue;
      }
      neighbors[u.intValue()].add(v);
      neighbors[v.intValue()].add(u);
    }

    int[][] adjacency = new int[n][];
    for (int v = 0; v < n; v++) {
      int[] row = new int[neighbors[v].size()];
      int k = 0;
      for (Integer u : neighbors[v]) {
        row[k++] = u.intValue();
      }
      adjacency[v] = row;
    }
    return new GraphData(vertexList, adjacency, index);
  }

  /** The line graph of a Symja graph, plus the maximum degree of the original graph. */
  public static final class LineGraphData {
    /** Symmetric adjacency lists over the edge indices <code>0 .. m-1</code>. */
    public final int[][] adjacency;

    /**
     * The largest number of edges incident to one vertex. Every one of those edges touches every
     * other, so the chromatic index is never smaller than this.
     */
    public final int maxDegree;

    private LineGraphData(int[][] adjacency, int maxDegree) {
      this.adjacency = adjacency;
      this.maxDegree = maxDegree;
    }

    public int edgeCount() {
      return adjacency.length;
    }
  }

  /**
   * Build the line graph: one vertex per edge, joined when the two edges share an endpoint. Coloring
   * it is coloring the edges of the original graph.
   *
   * <p>
   * Every edge of the graph becomes one vertex here, in <code>edgeSet()</code> order - which is the
   * order <code>EdgeList</code> reports, so a coloring can be handed back positionally. A self loop
   * is an edge like any other: it is incident to its vertex once, so it conflicts with the other
   * edges there but not with itself. Edge direction is ignored, so a pair of opposite directed edges
   * counts as two distinct edges sharing both endpoints - they touch, and so they need different
   * colors.
   */
  public static LineGraphData lineGraph(Graph<IExpr, ?> graph) {
    @SuppressWarnings("unchecked")
    Set<? extends IExprEdge> edgeSet = (Set<? extends IExprEdge>) graph.edgeSet();
    List<IExprEdge> edges = new ArrayList<IExprEdge>(edgeSet);
    int m = edges.size();
    if (m == 0) {
      return new LineGraphData(new int[0][], 0);
    }

    // the edges incident to each vertex; those form a clique in the line graph
    Map<IExpr, List<Integer>> incident = new HashMap<IExpr, List<Integer>>();
    for (int i = 0; i < m; i++) {
      IExprEdge e = edges.get(i);
      addIncident(incident, e.lhs(), i);
      if (!e.lhs().equals(e.rhs())) {
        addIncident(incident, e.rhs(), i);
      }
    }

    java.util.Set<Integer>[] neighbors = newSetArray(m);
    int maxDegree = 0;
    for (List<Integer> group : incident.values()) {
      if (group.size() > maxDegree) {
        maxDegree = group.size();
      }
      for (int a = 0; a < group.size(); a++) {
        for (int b = a + 1; b < group.size(); b++) {
          neighbors[group.get(a).intValue()].add(group.get(b));
          neighbors[group.get(b).intValue()].add(group.get(a));
        }
      }
    }

    int[][] adjacency = new int[m][];
    for (int i = 0; i < m; i++) {
      int[] row = new int[neighbors[i].size()];
      int k = 0;
      for (Integer u : neighbors[i]) {
        row[k++] = u.intValue();
      }
      adjacency[i] = row;
    }
    return new LineGraphData(adjacency, maxDegree);
  }

  private static void addIncident(Map<IExpr, List<Integer>> incident, IExpr vertex, int edgeIndex) {
    List<Integer> group = incident.get(vertex);
    if (group == null) {
      group = new ArrayList<Integer>();
      incident.put(vertex, group);
    }
    group.add(Integer.valueOf(edgeIndex));
  }

  @SuppressWarnings("unchecked")
  private static java.util.Set<Integer>[] newSetArray(int n) {
    java.util.Set<Integer>[] sets = new java.util.Set[n];
    for (int i = 0; i < n; i++) {
      sets[i] = new java.util.LinkedHashSet<Integer>();
    }
    return sets;
  }

  /**
   * Renumber a coloring to <code>1, 2, 3, ...</code> in order of first appearance, so that the
   * result of every method starts at color 1 and is stable across runs.
   */
  public static int[] canonicalize(int[] colors) {
    int[] result = new int[colors.length];
    Map<Integer, Integer> canonical = new HashMap<Integer, Integer>();
    int next = 1;
    for (int i = 0; i < colors.length; i++) {
      Integer raw = Integer.valueOf(colors[i]);
      Integer mapped = canonical.get(raw);
      if (mapped == null) {
        mapped = Integer.valueOf(next++);
        canonical.put(raw, mapped);
      }
      result[i] = mapped.intValue();
    }
    return result;
  }

  /** The number of distinct colors in a coloring. */
  public static int usedColors(int[] colors) {
    int max = 0;
    for (int c : colors) {
      if (c > max) {
        max = c;
      }
    }
    return max;
  }

  /** <code>true</code> if no edge joins two vertices carrying the same color. */
  public static boolean isProperColoring(GraphData data, int[] colors) {
    for (int v = 0; v < data.adjacency.length; v++) {
      for (int u : data.adjacency[v]) {
        if (colors[v] == colors[u]) {
          return false;
        }
      }
    }
    return true;
  }

  /** Build a simple undirected copy for the JGraphT coloring algorithms. */
  public static Graph<IExpr, ExprEdge> toUndirected(Graph<IExpr, ?> graph) {
    Graph<IExpr, ExprEdge> undirected = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    Graphs.addAllVertices(undirected, graph.vertexSet());
    @SuppressWarnings("unchecked")
    Set<? extends IExprEdge> edges = (Set<? extends IExprEdge>) graph.edgeSet();
    for (IExprEdge e : edges) {
      IExpr u = e.lhs();
      IExpr v = e.rhs();
      if (!u.equals(v)) {
        undirected.addEdge(u, v);
      }
    }
    return undirected;
  }

  /**
   * Run one of the JGraphT coloring algorithms.
   *
   * @param method one of the JGraphT {@code Method} names; see
   *        {@link #isJGraphTMethod(String)}
   * @return 1-based colors in {@code VertexList} index order, or <code>null</code> when the
   *         algorithm declined to color this graph (only {@code "Chordal"} does, on a graph that is
   *         not chordal)
   */
  public static int[] jgraphtColoring(Graph<IExpr, ?> graph, GraphData data, String method,
      EvalEngine engine) {
    int n = data.vertexCount();
    if (n <= 1) {
      // BrownBacktrackColoring indexes past the end of its own arrays for a one-vertex graph, and
      // the answer is the same for every algorithm anyway
      return n == 0 ? new int[0] : new int[] {1};
    }
    Graph<IExpr, ExprEdge> undirected = toUndirected(graph);
    VertexColoringAlgorithm<IExpr> alg;
    if ("BrownBacktrack".equalsIgnoreCase(method)) {
      alg = new BrownBacktrackColoring<IExpr, ExprEdge>(undirected);
    } else if ("Greedy".equalsIgnoreCase(method)) {
      alg = new GreedyColoring<IExpr, ExprEdge>(undirected);
    } else if ("SaturationDegree".equalsIgnoreCase(method) || "DSatur".equalsIgnoreCase(method)) {
      alg = new SaturationDegreeColoring<IExpr, ExprEdge>(undirected);
    } else if ("LargestDegreeFirst".equalsIgnoreCase(method)) {
      alg = new LargestDegreeFirstColoring<IExpr, ExprEdge>(undirected);
    } else if ("SmallestDegreeLast".equalsIgnoreCase(method)) {
      alg = new SmallestDegreeLastColoring<IExpr, ExprEdge>(undirected);
    } else if ("RandomGreedy".equalsIgnoreCase(method)) {
      alg = new RandomGreedyColoring<IExpr, ExprEdge>(undirected, engine.getRandom());
    } else if ("Chordal".equalsIgnoreCase(method)) {
      alg = new ChordalGraphColoring<IExpr, ExprEdge>(undirected);
    } else {
      return null;
    }

    VertexColoringAlgorithm.Coloring<IExpr> coloring = alg.getColoring();
    if (coloring == null) {
      return null; // ChordalGraphColoring on a graph that is not chordal
    }
    Map<IExpr, Integer> colorMap = coloring.getColors();
    int[] colors = new int[n];
    for (int i = 1; i <= n; i++) {
      Integer raw = colorMap.get(data.vertexList.get(i));
      colors[i - 1] = raw == null ? 1 : raw.intValue() + 1;
    }
    return colors;
  }

  /** <code>true</code> for the {@code Method} names {@link #jgraphtColoring} understands. */
  public static boolean isJGraphTMethod(String method) {
    return "BrownBacktrack".equalsIgnoreCase(method) //
        || "Greedy".equalsIgnoreCase(method) //
        || "SaturationDegree".equalsIgnoreCase(method) //
        || "DSatur".equalsIgnoreCase(method) //
        || "LargestDegreeFirst".equalsIgnoreCase(method) //
        || "SmallestDegreeLast".equalsIgnoreCase(method) //
        || "RandomGreedy".equalsIgnoreCase(method) //
        || "Chordal".equalsIgnoreCase(method);
  }

  /** The coloring as a list of integers. */
  public static IAST toIntegerList(int[] colors) {
    IASTAppendable result = F.ListAlloc(colors.length);
    for (int i = 0; i < colors.length; i++) {
      result.append(F.ZZ(colors[i]));
    }
    return result;
  }

  /**
   * The coloring as a list of the caller's own color expressions, mapping color <code>i</code> to
   * <code>palette.get(i)</code>.
   */
  public static IAST toPaletteList(int[] colors, IAST palette) {
    IASTAppendable result = F.ListAlloc(colors.length);
    for (int i = 0; i < colors.length; i++) {
      result.append(palette.get(colors[i]));
    }
    return result;
  }
}
