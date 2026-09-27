package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>GraphTriangleCount(g)</code> - the number of triangles of <code>g</code>: the 3-cliques of an
 * undirected graph, the directed 3-cycles of a directed one.
 */
public class GraphTriangleCount extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    if (view.isMixed()) {
      return F.NIL;
    }
    boolean directed = view.hasDirectedEdge();
    int[][] neighbours = directed ? view.successors() : view.undirectedNeighbours();
    java.util.Set<Long> arcs = new java.util.HashSet<Long>();
    for (int u = 0; u < view.n; u++) {
      for (int w : neighbours[u]) {
        arcs.add(((long) u << 32) | w);
      }
    }
    long count = 0;
    for (int a = 0; a < view.n; a++) {
      for (int b : neighbours[a]) {
        if (b <= a) {
          continue;
        }
        for (int c : neighbours[b]) {
          // undirected: a < b < c; directed: the cycle a -> b -> c -> a from its smallest vertex a
          if ((directed ? c > a && c != b : c > b) && arcs.contains(((long) c << 32) | a)) {
            count++;
          }
        }
      }
    }
    return F.ZZ(count);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
