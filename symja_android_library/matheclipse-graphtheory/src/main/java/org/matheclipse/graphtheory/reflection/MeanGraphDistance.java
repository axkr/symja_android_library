package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>MeanGraphDistance(g)</code> - the mean distance between the ordered pairs of distinct
 * vertices of <code>g</code>; <code>Infinity</code> if a vertex can't be reached from another.
 */
public class MeanGraphDistance extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    if (view.n < 2 || view.hasNegativeWeight()) {
      return F.NIL;
    }
    double sum = 0.0;
    for (int v = 0; v < view.n; v++) {
      for (double d : view.distances(v)) {
        if (Double.isInfinite(d)) {
          return S.Infinity;
        }
        sum += d;
      }
    }
    long pairs = (long) view.n * (view.n - 1);
    if (!view.weighted) {
      return F.QQ(Math.round(sum), pairs);
    }
    return F.num(sum / pairs);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
