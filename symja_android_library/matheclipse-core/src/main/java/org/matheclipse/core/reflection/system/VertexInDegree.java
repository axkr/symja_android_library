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
 * Returns the in-degree of a vertex in a graph, or a list of in-degrees for all vertices. For
 * undirected graphs, this corresponds to the standard vertex degree.
 */
public class VertexInDegree extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();

      if (ast.argSize() == 1) {
        // Return a list of in-degrees for all vertices
        IAST vertices = GraphExpr.vertexToIExpr(g);
        IASTAppendable result = F.ListAlloc(vertices.argSize());
        for (int i = 1; i <= vertices.argSize(); i++) {
          result.append(F.ZZ(g.inDegreeOf(vertices.get(i))));
        }
        return result;
      } else {
        // Return the in-degree of the specific vertex provided in the second argument
        IExpr v = ast.arg2();
        if (g.containsVertex(v)) {
          return F.ZZ(g.inDegreeOf(v));
        }
      }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}