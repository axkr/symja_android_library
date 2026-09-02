package org.matheclipse.graphtheory.reflection;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>IndependentVertexSetQ(graph, vertices)</code> - <code>True</code> if <code>vertices</code>
 * is an independent vertex set of <code>graph</code>, a set of vertices never incident to the same
 * edge, and <code>False</code> otherwise.
 *
 * <p>
 * Every entry has to be a vertex of the graph and no two of them may be joined by an edge, in either
 * direction. A repeated vertex makes the list something other than a set and is rejected. The empty
 * list is an independent vertex set of every graph. A self loop is ignored, so a vertex carrying one
 * can still belong to a set - which is what keeps this in agreement with
 * {@code FindIndependentVertexSet}.
 */
public class IndependentVertexSetQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg2 = ast.arg2();
    if (!arg2.isList()) {
      return S.False;
    }
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return S.False;
    }
    Graph<IExpr, ?> g = gex.toData();

    IAST vertices = (IAST) arg2;
    Set<IExpr> seen = new HashSet<IExpr>();
    for (int i = 1; i < vertices.size(); i++) {
      IExpr vertex = vertices.get(i);
      if (!g.containsVertex(vertex) || !seen.add(vertex)) {
        return S.False;
      }
    }
    for (int i = 1; i < vertices.size(); i++) {
      for (int j = i + 1; j < vertices.size(); j++) {
        IExpr u = vertices.get(i);
        IExpr v = vertices.get(j);
        // an edge makes its two endpoints dependent whichever way it points
        if (g.containsEdge(u, v) || g.containsEdge(v, u)) {
          return S.False;
        }
      }
    }
    return S.True;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
