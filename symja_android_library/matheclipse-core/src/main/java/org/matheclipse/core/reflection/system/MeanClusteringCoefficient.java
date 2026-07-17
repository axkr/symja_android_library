package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns the mean clustering coefficient for a graph. This is computed as the arithmetic mean of
 * the local clustering coefficients of all the vertices in the graph.
 */
public class MeanClusteringCoefficient extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();
      boolean isDirected = g.getType().isDirected();

      IAST vertices = GraphExpr.vertexToIExpr(g);
      int n = vertices.argSize();
      if (n == 0) {
        return F.C0;
      }

      // Sum the individual local clustering coefficients
      IASTAppendable sumPlus = F.PlusAlloc(n);
      for (int i = 1; i <= n; i++) {
        sumPlus.append(LocalClusteringCoefficient.getLocalCC(g, vertices.get(i), isDirected));
      }

      // Automatically evaluate and simplify the fraction: (Sum / N)
      return engine.evaluate(F.Divide(sumPlus, F.ZZ(n)));
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}