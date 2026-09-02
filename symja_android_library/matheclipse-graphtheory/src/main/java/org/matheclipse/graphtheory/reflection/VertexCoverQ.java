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
import org.matheclipse.graphtheory.expression.data.IExprEdge;

/**
 * <code>VertexCoverQ(graph, vertices)</code> - <code>True</code> if <code>vertices</code> is a
 * vertex cover of <code>graph</code>, a set of vertices incident to every edge, and
 * <code>False</code> otherwise.
 *
 * <p>
 * Every entry has to be a vertex of the graph, and every edge has to have at least one of its ends
 * among them. Edge direction does not matter: an edge is covered by either of its endpoints. A self
 * loop is covered only by its own vertex, and a repeated vertex is harmless, since covering an edge
 * twice breaks nothing.
 *
 * <p>
 * The empty list covers a graph only when the graph has no edges. A set is a vertex cover exactly
 * when the vertices it leaves out form an independent set.
 */
public class VertexCoverQ extends AbstractFunctionEvaluator {

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
    Set<IExpr> cover = new HashSet<IExpr>();
    for (int i = 1; i < vertices.size(); i++) {
      IExpr vertex = vertices.get(i);
      if (!g.containsVertex(vertex)) {
        return S.False;
      }
      cover.add(vertex);
    }

    @SuppressWarnings("unchecked")
    Set<IExprEdge> edges = (Set<IExprEdge>) g.edgeSet();
    for (IExprEdge edge : edges) {
      if (!cover.contains(edge.lhs()) && !cover.contains(edge.rhs())) {
        return S.False;
      }
    }
    return F.booleSymbol(true);
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
