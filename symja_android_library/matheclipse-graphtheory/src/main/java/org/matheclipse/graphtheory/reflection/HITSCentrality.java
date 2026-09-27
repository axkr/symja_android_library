package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>HITSCentrality(g)</code> - the list <code>{authorities, hubs}</code> of the HITS
 * centralities of the vertices of <code>g</code>.
 */
public class HITSCentrality extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    double[][] hits = GraphUtil.hitsCentrality(view);
    if (hits == null) {
      return F.NIL;
    }
    return F.list(F.mapRange(0, view.n, v -> F.num(hits[0][v])),
        F.mapRange(0, view.n, v -> F.num(hits[1][v])));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
