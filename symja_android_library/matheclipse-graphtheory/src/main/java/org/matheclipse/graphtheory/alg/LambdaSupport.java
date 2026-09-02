package org.matheclipse.graphtheory.alg;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.flow.GusfieldGomoryHuCutTree;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.ExprEdge;

/**
 * The lambda components behind {@code LambdaComponents}, and the extra test that narrows them to the
 * Luccio-Sami components.
 *
 * <p>
 * Writing <code>lambda(u,v)</code> for the number of edge-independent paths between two vertices,
 * <code>lambda(u,w) &gt;= min(lambda(u,v), lambda(v,w))</code> holds, so "<code>lambda</code> is at
 * least <code>t</code>" is an equivalence relation for every <code>t</code> and its classes are the
 * lambda components. A Gomory-Hu tree carries every pairwise value at once, and cutting it at each
 * of its edge weights produces those classes.
 */
public final class LambdaSupport {

  /**
   * Above this many vertices a candidate is not tested for the Luccio-Sami property, because the
   * test walks every proper subset of it.
   */
  public static final int MAX_LUCCIO_SAMI_SIZE = 18;

  private LambdaSupport() {}

  /**
   * The lambda components, largest sets first is <i>not</i> guaranteed here - the caller orders
   * them. Isolated vertices come back as singletons.
   */
  public static List<Set<IExpr>> lambdaComponents(Graph<IExpr, ExprEdge> undirected) {
    List<Set<IExpr>> components = new ArrayList<Set<IExpr>>();
    for (Set<IExpr> connected : new ConnectivityInspector<IExpr, ExprEdge>(
        undirected).connectedSets()) {
      if (connected.size() == 1) {
        components.add(connected);
        continue;
      }
      collectFromComponent(new AsSubgraph<IExpr, ExprEdge>(undirected, connected), components);
    }
    // the same set turns up at every threshold that does not split it
    return new ArrayList<Set<IExpr>>(new LinkedHashSet<Set<IExpr>>(components));
  }

  private static void collectFromComponent(Graph<IExpr, ExprEdge> component,
      List<Set<IExpr>> collector) {
    SimpleWeightedGraph<IExpr, DefaultWeightedEdge> tree =
        new GusfieldGomoryHuCutTree<IExpr, ExprEdge>(component).getGomoryHuTree();

    TreeSet<Double> thresholds = new TreeSet<Double>();
    for (DefaultWeightedEdge edge : tree.edgeSet()) {
      thresholds.add(Double.valueOf(tree.getEdgeWeight(edge)));
    }
    for (Double threshold : thresholds.descendingSet()) {
      Set<DefaultWeightedEdge> kept = new LinkedHashSet<DefaultWeightedEdge>();
      for (DefaultWeightedEdge edge : tree.edgeSet()) {
        if (tree.getEdgeWeight(edge) >= threshold.doubleValue()) {
          kept.add(edge);
        }
      }
      AsSubgraph<IExpr, DefaultWeightedEdge> forest =
          new AsSubgraph<IExpr, DefaultWeightedEdge>(tree, tree.vertexSet(), kept);
      for (Set<IExpr> set : new ConnectivityInspector<IExpr, DefaultWeightedEdge>(
          forest).connectedSets()) {
        if (set.size() > 1) {
          collector.add(set);
        }
      }
    }
  }

  /**
   * Whether every proper non-empty subset of <code>set</code> has strictly more edges to the rest of
   * <code>set</code> than to the vertices outside it - the Luccio-Sami, or LS, condition.
   *
   * <p>
   * A one vertex set has no proper non-empty subset and passes vacuously. When nothing lies outside
   * the set the condition asks only that no subset be cut off from the rest, which is to say that
   * the set is connected - that shortcut is what lets a whole graph be tested without walking its
   * subsets.
   *
   * @return <code>null</code> when the set is too large to test, otherwise the verdict
   */
  public static Boolean isLuccioSami(Graph<IExpr, ExprEdge> graph, Set<IExpr> set) {
    int n = set.size();
    if (n <= 1) {
      return Boolean.TRUE;
    }
    List<IExpr> members = new ArrayList<IExpr>(set);
    int[] external = new int[n];
    int[][] inside = new int[n][];
    boolean anyExternal = false;
    for (int i = 0; i < n; i++) {
      IExpr vertex = members.get(i);
      List<Integer> neighbours = new ArrayList<Integer>();
      for (ExprEdge edge : graph.edgesOf(vertex)) {
        IExpr other = Graphs.getOppositeVertex(graph, edge, vertex);
        int index = members.indexOf(other);
        if (index < 0) {
          external[i]++;
          anyExternal = true;
        } else if (index != i) {
          neighbours.add(Integer.valueOf(index));
        }
      }
      int[] row = new int[neighbours.size()];
      for (int j = 0; j < row.length; j++) {
        row[j] = neighbours.get(j).intValue();
      }
      inside[i] = row;
    }

    if (!anyExternal) {
      // nothing outside to lose ties to, so the condition reduces to connectedness
      return Boolean.valueOf(isConnected(inside, n));
    }
    if (n > MAX_LUCCIO_SAMI_SIZE) {
      return null;
    }
    for (int mask = 1; mask < (1 << n) - 1; mask++) {
      int internalTies = 0;
      int externalTies = 0;
      for (int i = 0; i < n; i++) {
        if ((mask & (1 << i)) == 0) {
          continue;
        }
        externalTies += external[i];
        for (int j : inside[i]) {
          if ((mask & (1 << j)) == 0) {
            internalTies++;
          }
        }
      }
      if (internalTies <= externalTies) {
        return Boolean.FALSE;
      }
    }
    return Boolean.TRUE;
  }

  private static boolean isConnected(int[][] inside, int n) {
    boolean[] seen = new boolean[n];
    int[] stack = new int[n];
    int top = 0;
    stack[top++] = 0;
    seen[0] = true;
    int reached = 1;
    while (top > 0) {
      int current = stack[--top];
      for (int next : inside[current]) {
        if (!seen[next]) {
          seen[next] = true;
          stack[top++] = next;
          reached++;
        }
      }
    }
    return reached == n;
  }
}
