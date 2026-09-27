package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>EccentricityCentrality(g)</code> - <code>1/e(v)</code> for the vertices of <code>g</code>,
 * where the eccentricity <code>e(v)</code> is measured over the vertices reachable from
 * <code>v</code>; <code>0</code> if <code>e(v)</code> is 0.
 */
public class EccentricityCentrality extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    if (view.hasNegativeWeight()) {
      return F.NIL;
    }
    return F.mapRange(0, view.n, v -> {
      double max = 0.0;
      for (double d : view.distances(v)) {
        if (!Double.isInfinite(d)) {
          max = Math.max(max, d);
        }
      }
      return F.num(max > 0.0 ? 1.0 / max : 0.0);
    });
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
