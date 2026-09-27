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
 * <code>PageRankCentrality(g)</code>, <code>PageRankCentrality(g, a)</code> - the page rank of the
 * vertices of <code>g</code> with the damping factor <code>a</code> (default <code>0.85</code>): the
 * solution of <code>x = a P^T x + (1-a)/n</code>, where a vertex without outgoing edges jumps to
 * every vertex alike.
 */
public class PageRankCentrality extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    double alpha = 0.85;
    if (ast.isAST2()) {
      alpha = ast.arg2().evalfNaN();
      if (Double.isNaN(alpha) || alpha < 0.0 || alpha > 1.0) {
        return F.NIL;
      }
    }
    GraphView view = GraphView.of(gex.toData());
    final int n = view.n;
    if (n == 0) {
      return F.CEmptyList;
    }
    double[] x = new double[n];
    double[] y = new double[n];
    java.util.Arrays.fill(x, 1.0 / n);
    for (int iteration = 0; iteration < 10000; iteration++) {
      double dangling = 0.0;
      java.util.Arrays.fill(y, 0.0);
      for (int u = 0; u < n; u++) {
        int out = view.out[u].length;
        if (out == 0) {
          dangling += x[u];
          continue;
        }
        double share = x[u] / out;
        for (int e : view.out[u]) {
          y[view.other(e, u)] += share;
        }
      }
      double base = (alpha * dangling + (1.0 - alpha)) / n;
      double sum = 0.0;
      for (int v = 0; v < n; v++) {
        y[v] = alpha * y[v] + base;
        sum += y[v];
      }
      double change = 0.0;
      for (int v = 0; v < n; v++) {
        y[v] /= sum;
        change += Math.abs(y[v] - x[v]);
      }
      double[] t = x;
      x = y;
      y = t;
      if (change < 1.0e-15) {
        break;
      }
    }
    final double[] result = x;
    return F.mapRange(0, n, v -> F.num(result[v]));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
