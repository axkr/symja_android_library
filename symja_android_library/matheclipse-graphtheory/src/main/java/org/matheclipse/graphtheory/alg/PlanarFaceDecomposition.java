package org.matheclipse.graphtheory.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.ExprEdge;

/**
 * The faces of a planar graph, and which of them touch.
 *
 * <p>
 * JGraphT's planarity inspector returns a <i>rotation system</i> - the edges around each vertex in
 * clockwise order - which is all an embedding is. The faces are then read off it by the classic
 * face-tracing walk: treat each edge as two darts, one per direction, and repeatedly follow a dart
 * <code>u -&gt; v</code> to the dart that leaves <code>v</code> along the edge <i>after</i> the one
 * it arrived on. Each orbit of that walk is one face, and the orbits partition the darts, so every
 * edge lies on exactly two of them (the same face twice when the edge is a bridge).
 *
 * <p>
 * Coloring the faces is then coloring the vertices of the dual: two faces are adjacent when they
 * share an edge.
 *
 * <p>
 * The outer face comes first in {@link #faces}, so that dropping it is dropping the head of the
 * list.
 */
public final class PlanarFaceDecomposition {

  /**
   * The vertex cycle of each face; the outer face first, the rest in the order they were traced.
   */
  public final List<List<IExpr>> faces;

  /** Symmetric adjacency lists over the face indices - the dual graph. */
  public final int[][] adjacency;

  private PlanarFaceDecomposition(List<List<IExpr>> faces, int[][] adjacency) {
    this.faces = faces;
    this.adjacency = adjacency;
  }

  public int faceCount() {
    return faces.size();
  }

  /**
   * Decompose a simple undirected graph into its faces.
   *
   * @return the decomposition, or <code>null</code> when the graph is not planar
   */
  public static PlanarFaceDecomposition of(Graph<IExpr, ExprEdge> graph) {
    PlanarityTestingAlgorithm<IExpr, ExprEdge> inspector =
        new BoyerMyrvoldPlanarityInspector<IExpr, ExprEdge>(graph);
    if (!inspector.isPlanar()) {
      return null;
    }
    List<ExprEdge> edges = new ArrayList<ExprEdge>(graph.edgeSet());
    int m = edges.size();
    if (m == 0) {
      return new PlanarFaceDecomposition(new ArrayList<List<IExpr>>(), new int[0][]);
    }
    Map<ExprEdge, Integer> edgeIndex = new HashMap<ExprEdge, Integer>(m * 2);
    for (int i = 0; i < m; i++) {
      edgeIndex.put(edges.get(i), Integer.valueOf(i));
    }

    PlanarityTestingAlgorithm.Embedding<IExpr, ExprEdge> embedding = inspector.getEmbedding();
    Map<IExpr, List<ExprEdge>> rotation = new HashMap<IExpr, List<ExprEdge>>();
    for (IExpr vertex : graph.vertexSet()) {
      rotation.put(vertex, embedding.getEdgesAround(vertex));
    }

    // dart 2*i leaves edges.get(i).lhs(), dart 2*i+1 leaves its rhs
    int[] faceOfDart = new int[2 * m];
    java.util.Arrays.fill(faceOfDart, -1);
    List<List<IExpr>> faces = new ArrayList<List<IExpr>>();

    for (int start = 0; start < 2 * m; start++) {
      if (faceOfDart[start] >= 0) {
        continue;
      }
      int face = faces.size();
      List<IExpr> cycle = new ArrayList<IExpr>();
      int dart = start;
      do {
        faceOfDart[dart] = face;
        cycle.add(tail(graph, edges, dart));
        dart = nextDart(graph, edges, edgeIndex, rotation, dart);
      } while (dart != start && dart >= 0 && faceOfDart[dart] < 0);
      faces.add(cycle);
    }

    // report the outer face first. Without vertex
    // coordinates there is nothing that singles the outer face out, so take the longest boundary
    // walk - right for a grid, a wheel or a tree, and an arbitrary but deterministic pick when the
    // faces are all the same size, as on a tetrahedron or a cube.
    int outer = 0;
    for (int i = 1; i < faces.size(); i++) {
      if (faces.get(i).size() > faces.get(outer).size()) {
        outer = i;
      }
    }
    if (outer != 0) {
      faces.add(0, faces.remove(outer));
      for (int d = 0; d < faceOfDart.length; d++) {
        int face = faceOfDart[d];
        faceOfDart[d] = face == outer ? 0 : (face < outer ? face + 1 : face);
      }
    }

    Set<Integer>[] neighbors = newSetArray(faces.size());
    for (int i = 0; i < m; i++) {
      int a = faceOfDart[2 * i];
      int b = faceOfDart[2 * i + 1];
      if (a != b) {
        // a bridge has the same face on both sides, and a face never conflicts with itself
        neighbors[a].add(Integer.valueOf(b));
        neighbors[b].add(Integer.valueOf(a));
      }
    }
    int[][] adjacency = new int[faces.size()][];
    for (int i = 0; i < faces.size(); i++) {
      int[] row = new int[neighbors[i].size()];
      int k = 0;
      for (Integer u : neighbors[i]) {
        row[k++] = u.intValue();
      }
      adjacency[i] = row;
    }
    return new PlanarFaceDecomposition(faces, adjacency);
  }

  /** The vertex a dart leaves. */
  private static IExpr tail(Graph<IExpr, ExprEdge> graph, List<ExprEdge> edges, int dart) {
    ExprEdge edge = edges.get(dart >>> 1);
    IExpr source = graph.getEdgeSource(edge);
    IExpr target = graph.getEdgeTarget(edge);
    return (dart & 1) == 0 ? source : target;
  }

  /** The vertex a dart enters. */
  private static IExpr head(Graph<IExpr, ExprEdge> graph, List<ExprEdge> edges, int dart) {
    ExprEdge edge = edges.get(dart >>> 1);
    IExpr source = graph.getEdgeSource(edge);
    IExpr target = graph.getEdgeTarget(edge);
    return (dart & 1) == 0 ? target : source;
  }

  /**
   * Follow a dart to the next one on the same face: arrive at its head, then leave along the edge
   * that comes after the arrival edge in that vertex's clockwise order.
   */
  private static int nextDart(Graph<IExpr, ExprEdge> graph, List<ExprEdge> edges,
      Map<ExprEdge, Integer> edgeIndex, Map<IExpr, List<ExprEdge>> rotation, int dart) {
    IExpr vertex = head(graph, edges, dart);
    ExprEdge arrival = edges.get(dart >>> 1);
    List<ExprEdge> around = rotation.get(vertex);
    if (around == null || around.isEmpty()) {
      return -1;
    }
    int position = around.indexOf(arrival);
    if (position < 0) {
      return -1;
    }
    ExprEdge departure = around.get((position + 1) % around.size());
    int index = edgeIndex.get(departure).intValue();
    // pick the direction of `departure` that leaves `vertex`
    return graph.getEdgeSource(departure).equals(vertex) ? 2 * index : 2 * index + 1;
  }

  @SuppressWarnings("unchecked")
  private static Set<Integer>[] newSetArray(int n) {
    Set<Integer>[] sets = new Set[n];
    for (int i = 0; i < n; i++) {
      sets[i] = new LinkedHashSet<Integer>();
    }
    return sets;
  }
}
