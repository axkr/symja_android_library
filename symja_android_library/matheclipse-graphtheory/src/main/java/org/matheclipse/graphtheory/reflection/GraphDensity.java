package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>GraphDensity(g)</code> - the number of edges divided by the largest possible number,
 * <code>(directed edges + 2*undirected edges)/(n*(n-1))</code>.
 */
public class GraphDensity extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    if (view.n < 2) {
      return F.NIL;
    }
    long arcs = 0;
    for (int e = 0; e < view.m; e++) {
      arcs += view.undirected[e] ? 2 : 1;
    }
    return F.QQ(arcs, (long) view.n * (view.n - 1));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
