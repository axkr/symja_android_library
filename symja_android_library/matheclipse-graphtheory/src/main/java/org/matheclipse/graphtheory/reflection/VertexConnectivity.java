package org.matheclipse.graphtheory.reflection;

import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.KConnectivityFlowAlgorithm;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>VertexConnectivity(g)</code> gives the smallest number of vertices whose removal
 * disconnects the graph <code>g</code>. <code>VertexConnectivity(g, s, t)</code> gives the smallest
 * number of vertices whose removal disconnects the vertex <code>t</code> from the vertex
 * <code>s</code>.
 */
public class VertexConnectivity extends AbstractFunctionEvaluator {

  public VertexConnectivity() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    Graph<IExpr, ?> g = gex.toData();
    if (ast.isAST1()) {
      return F.ZZ(new KConnectivityFlowAlgorithm<>(g).getVertexConnectivity());
    }
    if (!ast.isAST3()) {
      return F.NIL;
    }
    IExpr s = ast.arg2();
    IExpr t = ast.arg3();
    if (!g.containsVertex(s) || !g.containsVertex(t) || s.equals(t)) {
      return F.NIL;
    }
    if (g.containsEdge(s, t)) {
      // WMA: an edge from s to t can't be cut by removing vertices; the result is 0
      return F.C0;
    }
    return F.ZZ(localVertexConnectivity(g, s, t));
  }

  /**
   * The number of internally vertex-disjoint paths from <code>s</code> to <code>t</code> (Menger):
   * the maximum flow in the network where every vertex <code>v</code> is split into
   * <code>v_in -&gt; v_out</code> with capacity 1. An edge from <code>t</code> back to
   * <code>s</code> is left out.
   */
  private static <E> int localVertexConnectivity(Graph<IExpr, E> g, IExpr s, IExpr t) {
    final int n = g.vertexSet().size();
    final double infinity = n + 1.0;
    // vertex v gets the integers 2*i (in) and 2*i+1 (out)
    java.util.Map<IExpr, Integer> index = new java.util.HashMap<>();
    DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> network =
        new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    int i = 0;
    for (IExpr v : g.vertexSet()) {
      index.put(v, i);
      network.addVertex(2 * i);
      network.addVertex(2 * i + 1);
      network.setEdgeWeight(network.addEdge(2 * i, 2 * i + 1),
          v.equals(s) || v.equals(t) ? infinity : 1.0);
      i++;
    }
    final boolean directed = g.getType().isDirected();
    for (E edge : g.edgeSet()) {
      IExpr u = g.getEdgeSource(edge);
      IExpr v = g.getEdgeTarget(edge);
      if ((u.equals(s) && v.equals(t)) || (u.equals(t) && v.equals(s))) {
        continue;
      }
      addArc(network, index.get(u), index.get(v), infinity);
      if (!directed) {
        addArc(network, index.get(v), index.get(u), infinity);
      }
    }
    double flow = new EdmondsKarpMFImpl<>(network).getMaximumFlowValue(2 * index.get(s) + 1,
        2 * index.get(t));
    return (int) Math.round(flow);
  }

  private static void addArc(DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> network,
      int from, int to, double capacity) {
    if (from != to && !network.containsEdge(2 * from + 1, 2 * to)) {
      network.setEdgeWeight(network.addEdge(2 * from + 1, 2 * to), capacity);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
