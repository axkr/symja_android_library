package org.matheclipse.graphtheory.reflection;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.expression.data.ExprEdge;
import org.matheclipse.graphtheory.expression.data.ExprWeightedEdge;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>VertexReplace(g, rules)</code> - the graph with its vertices renamed by
 * <code>Replace(v, rules)</code>. Vertices renamed to the same name merge; an edge between them
 * becomes a self-loop.
 */
public class VertexReplace extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    IExpr rules = ast.arg2();
    Graph<IExpr, ?> g = gex.toData();
    Map<IExpr, IExpr> rename = new HashMap<IExpr, IExpr>();
    for (IExpr v : g.vertexSet()) {
      rename.put(v, engine.evaluate(F.Replace(v, rules)));
    }
    if (gex.isWeightedGraph()) {
      @SuppressWarnings("unchecked")
      Graph<IExpr, ExprWeightedEdge> source = (Graph<IExpr, ExprWeightedEdge>) g;
      Graph<IExpr, ExprWeightedEdge> result = g.getType().isDirected()
          ? new DefaultDirectedWeightedGraph<IExpr, ExprWeightedEdge>(ExprWeightedEdge.class)
          : new DefaultUndirectedWeightedGraph<IExpr, ExprWeightedEdge>(ExprWeightedEdge.class);
      for (IExpr v : g.vertexSet()) {
        result.addVertex(rename.get(v));
      }
      for (ExprWeightedEdge edge : source.edgeSet()) {
        ExprWeightedEdge newEdge =
            result.addEdge(rename.get(edge.lhs()), rename.get(edge.rhs()));
        if (newEdge != null) {
          result.setEdgeWeight(newEdge, source.getEdgeWeight(edge));
          ExprWeightedEdge.copyExactWeight(edge, newEdge);
        }
      }
      return GraphExpr.newInstance(result);
    }
    @SuppressWarnings("unchecked")
    Graph<IExpr, ExprEdge> source = (Graph<IExpr, ExprEdge>) g;
    final boolean mixed = GraphExpr.isMixedGraph(g);
    Graph<IExpr, ExprEdge> result;
    if (mixed) {
      result = new DirectedPseudograph<IExpr, ExprEdge>(ExprEdge.class);
    } else if (g.getType().isDirected()) {
      result = new DefaultDirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    } else {
      result = new DefaultUndirectedGraph<IExpr, ExprEdge>(ExprEdge.class);
    }
    for (IExpr v : g.vertexSet()) {
      result.addVertex(rename.get(v));
    }
    int id = 1;
    for (ExprEdge edge : source.edgeSet()) {
      IExpr u = rename.get(edge.lhs());
      IExpr v = rename.get(edge.rhs());
      if (mixed) {
        result.addEdge(u, v, new ExprEdge(edge.isUndirected(), id++));
      } else {
        result.addEdge(u, v);
      }
    }
    return GraphExpr.newInstance(result);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
