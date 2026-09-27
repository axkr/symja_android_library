package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>ReverseGraph(g)</code> - the graph with every directed edge reversed; undirected edges are
 * kept.
 */
public class ReverseGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    if (!view.hasDirectedEdge()) {
      return gex;
    }
    List<int[]> arcs = new ArrayList<int[]>();
    for (int e = 0; e < view.m; e++) {
      if (view.undirected[e]) {
        arcs.add(new int[] {view.source[e], view.target[e], e});
      } else {
        arcs.add(new int[] {view.target[e], view.source[e], e});
      }
    }
    if (!view.hasUndirectedEdge()) {
      // sort the edges of a directed graph; a mixed graph keeps them in place
      arcs.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
    }
    return GraphTransforms.newGraph(gex, view, arcs, null);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
