package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.ExprWeightedEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns a graph with the specified vertices removed.
 */
public class VertexDelete extends AbstractEvaluator {

  @Override
  public IExpr evalCatched(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 2) {
      return F.NIL;
    }

    // Using newInstance based on your snippet, though GraphFunctions.getGraphExpr(ast.arg1())
    // is often preferred if you want to strictly prevent edge re-parsing side effects.
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    IExpr arg2 = ast.arg2();

    // 1. Handle Weighted Graphs
    if (gex.isWeightedGraph()) {
      @SuppressWarnings("unchecked")
      Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
      Graph<IExpr, ExprWeightedEdge> resultGraph;

      if (g.getType().isDirected()) {
        resultGraph = new DefaultDirectedWeightedGraph<>(ExprWeightedEdge.class);
      } else {
        resultGraph = new DefaultUndirectedWeightedGraph<>(ExprWeightedEdge.class);
      }

      Graphs.addAllVertices(resultGraph, g.vertexSet());
      Graphs.addAllEdges(resultGraph, g, g.edgeSet());

      if (arg2.isList()) {
        for (IExpr v : (IAST) arg2) {
          resultGraph.removeVertex(v);
        }
      } else {
        resultGraph.removeVertex(arg2);
      }

      return GraphExpr.newInstance(resultGraph);
    }

    // 2. Handle Unweighted Graphs
    else {
      @SuppressWarnings("unchecked")
      Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
      Graph<IExpr, ExprEdge> resultGraph;

      if (g.getType().isDirected()) {
        resultGraph = new DefaultDirectedGraph<>(ExprEdge.class);
      } else {
        resultGraph = new DefaultUndirectedGraph<>(ExprEdge.class);
      }

      Graphs.addAllVertices(resultGraph, g.vertexSet());
      Graphs.addAllEdges(resultGraph, g, g.edgeSet());

      if (arg2.isList()) {
        for (IExpr v : (IAST) arg2) {
          resultGraph.removeVertex(v);
        }
      } else {
        resultGraph.removeVertex(arg2);
      }

      return GraphExpr.newInstance(resultGraph);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}