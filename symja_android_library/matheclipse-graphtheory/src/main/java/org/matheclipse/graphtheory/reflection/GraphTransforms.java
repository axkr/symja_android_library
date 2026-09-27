package org.matheclipse.graphtheory.reflection;

import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.ExprWeightedEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/** Build the result graphs of <code>DirectedGraph</code>, <code>ReverseGraph</code> and <code>UndirectedGraph</code>. */
final class GraphTransforms {

  private GraphTransforms() {}

  /**
   * A graph with the vertices of <code>gex</code> and the given arcs <code>{source, target,
   * edge}</code>; an arc is written with <code>head</code>, or with the head of the original edge if
   * <code>head</code> is <code>null</code>. The weight of an arc is the weight of its edge.
   */
  static IExpr newGraph(GraphExpr<?> gex, GraphView view, List<int[]> arcs, ISymbol head) {
    IASTAppendable edges = F.ListAlloc(arcs.size());
    IASTAppendable weights = F.ListAlloc(arcs.size());
    for (int[] arc : arcs) {
      ISymbol h = head != null ? head : (view.undirected[arc[2]] ? S.UndirectedEdge : S.DirectedEdge);
      edges.append(F.binaryAST2(h, view.vertex(arc[0]), view.vertex(arc[1])));
      weights.append(weight(gex, arc[2]));
    }
    return graph(gex, view, edges, weights);
  }

  /**
   * An undirected graph with the edges <code>{v, w, edge}</code>; the weight of an edge is the sum of
   * the weights of the <code>merged</code> edges.
   */
  static IExpr newUndirectedGraph(GraphExpr<?> gex, GraphView view, List<int[]> pairs,
      List<List<Integer>> merged) {
    IASTAppendable edges = F.ListAlloc(pairs.size());
    IASTAppendable weights = F.ListAlloc(pairs.size());
    for (int i = 0; i < pairs.size(); i++) {
      int[] pair = pairs.get(i);
      edges.append(F.UndirectedEdge(view.vertex(pair[0]), view.vertex(pair[1])));
      IASTAppendable sum = F.PlusAlloc(merged.get(i).size());
      for (int e : merged.get(i)) {
        sum.append(weight(gex, e));
      }
      weights.append(F.eval(sum.oneIdentity0()));
    }
    return graph(gex, view, edges, weights);
  }

  private static IExpr weight(GraphExpr<?> gex, int e) {
    if (!gex.isWeightedGraph()) {
      return F.C1;
    }
    Object edge = new java.util.ArrayList<Object>(gex.toData().edgeSet()).get(e);
    return ((ExprWeightedEdge) edge).weightExpr();
  }

  private static IExpr graph(GraphExpr<?> gex, GraphView view, IAST edges, IAST weights) {
    IASTAppendable vertices = F.ListAlloc(view.n);
    for (int v = 0; v < view.n; v++) {
      vertices.append(view.vertex(v));
    }
    if (gex.isWeightedGraph()) {
      return F.eval(F.Graph(vertices, edges, F.list(F.Rule(S.EdgeWeight, weights))));
    }
    return F.eval(F.Graph(vertices, edges));
  }
}
