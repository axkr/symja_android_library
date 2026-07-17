package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Predicate that yields True if the graph is a directed graph, and False otherwise.
 */
public class DirectedGraphQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());

    // Predicate functions return False if the argument is not a valid graph
    if (gex == null) {
      return F.False;
    }

    Graph<IExpr, ?> g = gex.toData();

    return F.booleSymbol(g.getType().isDirected());
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