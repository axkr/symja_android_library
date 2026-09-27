package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>DegreeCentrality(g)</code>, <code>DegreeCentrality(g, "In")</code>,
 * <code>DegreeCentrality(g, "Out")</code> - the (in, out) degrees of the vertices of <code>g</code>
 * in <code>VertexList</code> order. In a graph with directed edges an undirected edge counts once in
 * each direction.
 */
public class DegreeCentrality extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    int mode = 0;
    if (ast.isAST2()) {
      if (ast.arg2().isString("In")) {
        mode = 1;
      } else if (ast.arg2().isString("Out")) {
        mode = 2;
      } else {
        return F.NIL;
      }
    }
    GraphView view = GraphView.of(gex.toData());
    final boolean directed = view.hasDirectedEdge();
    final int m = mode;
    return F.mapRange(0, view.n, v -> {
      if (!directed) {
        // the degree; a self-loop counts twice
        int degree = view.out[v].length;
        for (int e : view.out[v]) {
          if (view.source[e] == view.target[e]) {
            degree++;
          }
        }
        return F.ZZ(degree);
      }
      int in = view.in[v].length;
      int out = view.out[v].length;
      return F.ZZ(m == 1 ? in : m == 2 ? out : in + out);
    });
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
