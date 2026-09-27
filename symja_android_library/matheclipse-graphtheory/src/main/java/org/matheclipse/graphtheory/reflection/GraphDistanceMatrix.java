package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>GraphDistanceMatrix(g)</code> - the matrix of the distances between the vertices of
 * <code>g</code>, <code>Infinity</code> where there is no path;
 * <code>GraphDistanceMatrix(g, d)</code> keeps the distances up to <code>d</code> only.
 */
public class GraphDistanceMatrix extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    double bound = Double.POSITIVE_INFINITY;
    if (ast.isAST2()) {
      bound = ast.arg2().isInfinity() ? Double.POSITIVE_INFINITY : ast.arg2().evalfNaN();
      if (Double.isNaN(bound)) {
        return F.NIL;
      }
    }
    GraphView view = GraphView.of(gex.toData());
    if (view.hasNegativeWeight()) {
      return F.NIL;
    }
    final double b = bound;
    IASTAppendable matrix = F.ListAlloc(view.n);
    for (int v = 0; v < view.n; v++) {
      double[] distance = view.distances(v);
      matrix.append(F.mapRange(0, view.n, w -> {
        double d = distance[w];
        if (Double.isInfinite(d) || d > b + 1.0e-12) {
          return S.Infinity;
        }
        // machine reals for a weighted graph v
        return view.weighted ? F.num(d) : F.ZZ((long) d);
      }));
    }
    return matrix;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
