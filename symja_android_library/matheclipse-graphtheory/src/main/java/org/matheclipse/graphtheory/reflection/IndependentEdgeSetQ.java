package org.matheclipse.graphtheory.reflection;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>IndependentEdgeSetQ(graph, edges)</code> - <code>True</code> if <code>edges</code> is an
 * independent edge set of <code>graph</code>, a set of edges never incident to the same vertex, and
 * <code>False</code> otherwise.
 *
 * <p>
 * Every edge has to be an edge of the graph, and no vertex may be touched twice - which rules out a
 * repeated edge and a self loop, both of which meet themselves at a vertex. The empty list is an
 * independent edge set of every graph.
 */
public class IndependentEdgeSetQ extends AbstractFunctionEvaluator {

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

    IAST edges = (IAST) arg2;
    Set<IExpr> touched = new HashSet<IExpr>();
    for (int i = 1; i < edges.size(); i++) {
      IExpr edge = edges.get(i);
      if (!edge.isEdge()) {
        return S.False;
      }
      IExpr u = edge.first();
      IExpr v = edge.second();
      if (!g.containsEdge(u, v)) {
        return S.False;
      }
      // a vertex touched twice breaks independence; a self loop does it on its own, and so does the
      // same edge listed twice
      if (!touched.add(u) || !touched.add(v)) {
        return S.False;
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
