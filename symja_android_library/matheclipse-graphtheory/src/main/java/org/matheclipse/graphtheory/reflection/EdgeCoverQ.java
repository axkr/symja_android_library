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
 * <code>EdgeCoverQ(graph, edges)</code> - <code>True</code> if <code>edges</code> is an edge cover
 * of <code>graph</code>, a set of edges incident to every vertex, and <code>False</code> otherwise.
 *
 * <p>
 * Every entry has to be an edge of the graph, and between them they have to touch every vertex. A
 * self loop is incident to its own vertex and covers it. Unlike
 * {@code IndependentEdgeSetQ}, a repeated edge is harmless here: that predicate rejects one because
 * the repetition touches a vertex twice and breaks the property it tests, while covering a vertex
 * twice breaks nothing.
 *
 * <p>
 * The empty list covers a graph only when the graph has no vertices at all.
 */
public class EdgeCoverQ extends AbstractFunctionEvaluator {

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
    Set<IExpr> covered = new HashSet<IExpr>();
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
      covered.add(u);
      covered.add(v);
    }
    return F.booleSymbol(covered.containsAll(g.vertexSet()));
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
