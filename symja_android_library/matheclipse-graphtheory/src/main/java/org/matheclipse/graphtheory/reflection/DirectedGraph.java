package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>DirectedGraph(g)</code> - the graph with every undirected edge <code>u&lt;-&gt;v</code>
 * replaced by <code>u-&gt;v</code> and <code>v-&gt;u</code>;
 * <code>DirectedGraph(g, "Acyclic")</code> orients it from the earlier to the later vertex in
 * <code>VertexList</code> order.
 */
public class DirectedGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    boolean acyclic = false;
    if (ast.isAST2()) {
      if (!ast.arg2().isString("Acyclic")) {
        return F.NIL;
      }
      acyclic = true;
    }
    GraphView view = GraphView.of(gex.toData());
    if (!view.hasUndirectedEdge()) {
      return gex;
    }
    // {source, target, edge}
    List<int[]> arcs = new ArrayList<int[]>();
    for (int e = 0; e < view.m; e++) {
      int u = view.source[e];
      int v = view.target[e];
      if (!view.undirected[e]) {
        arcs.add(new int[] {u, v, e});
      } else if (acyclic || u == v) {
        arcs.add(new int[] {Math.min(u, v), Math.max(u, v), e});
      } else {
        arcs.add(new int[] {u, v, e});
        arcs.add(new int[] {v, u, e});
      }
    }
    if (acyclic || !view.hasDirectedEdge()) {
      // sort the edges; a mixed graph keeps them in place
      arcs.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
    }
    return GraphTransforms.newGraph(gex, view, arcs, S.DirectedEdge);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
