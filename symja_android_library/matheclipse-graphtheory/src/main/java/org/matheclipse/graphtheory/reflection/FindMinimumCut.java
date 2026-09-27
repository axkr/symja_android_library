package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindMinimumCut(g)</code> - <code>{value, {shore, rest}}</code>, a minimum cut of
 * <code>g</code> weighted by the edge weights; of the minimum cuts the smallest shore containing
 * the last vertex.
 */
public class FindMinimumCut extends AbstractFunctionEvaluator {

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
    Object[] cut = GraphUtil.globalMinimumCut(view);
    if (cut == null) {
      return F.NIL;
    }
    boolean[] shore = (boolean[]) cut[1];
    IASTAppendable part1 = F.ListAlloc();
    IASTAppendable part2 = F.ListAlloc();
    for (int v = 0; v < view.n; v++) {
      (shore[v] ? part1 : part2).append(view.vertex(v));
    }
    return F.list(GraphUtil.weightNumber((Double) cut[0], GraphUtil.hasIntegerWeights(gex)),
        F.list(part1, part2));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
