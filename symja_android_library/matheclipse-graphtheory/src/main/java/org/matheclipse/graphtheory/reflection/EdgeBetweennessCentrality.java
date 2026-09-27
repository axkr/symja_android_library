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
 * <code>EdgeBetweennessCentrality(g)</code> - the betweenness of the edges of <code>g</code> in
 * <code>EdgeList</code> order: the shortest paths (by weight) between ordered pairs of vertices
 * through each edge.
 */
public class EdgeBetweennessCentrality extends AbstractFunctionEvaluator {

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
    double[] score;
    if (view.weighted) {
      score = GraphUtil.weightedEdgeBetweenness(view);
    } else {
      score = new double[view.m];
      GraphUtil.betweenness(view, score);
    }
    final double[] s = score;
    return F.mapRange(0, view.m, e -> F.num(s[e]));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
