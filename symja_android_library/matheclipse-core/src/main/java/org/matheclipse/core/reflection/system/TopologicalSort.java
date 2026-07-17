package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns a topological ordering of the vertices in a directed acyclic graph. If the graph contains
 * cycles, returns $Failed.
 */
public class TopologicalSort extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    Graph<IExpr, ?> g = gex.toData();

    try {
      TopologicalOrderIterator<IExpr, ?> iterator = new TopologicalOrderIterator<>(g);
      IASTAppendable result = F.ListAlloc();
      while (iterator.hasNext()) {
        result.append(iterator.next());
      }
      return result;
    } catch (IllegalArgumentException e) {
      // Topological sort is only possible for DAGs
      return S.$Failed;
    }

  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
