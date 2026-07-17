package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.ExprWeightedEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns a new graph with the specified vertices added. If the vertices already exist in the
 * graph, they are ignored.
 */
public class VertexAdd extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
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

        // Copy existing vertices and edges
        Graphs.addAllVertices(resultGraph, g.vertexSet());
        Graphs.addAllEdges(resultGraph, g, g.edgeSet());

        // Add the new vertex or list of vertices
        if (arg2.isList()) {
          for (IExpr v : (IAST) arg2) {
            resultGraph.addVertex(v);
          }
        } else {
          resultGraph.addVertex(arg2);
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

        // Copy existing vertices and edges
        Graphs.addAllVertices(resultGraph, g.vertexSet());
        Graphs.addAllEdges(resultGraph, g, g.edgeSet());

        // Add the new vertex or list of vertices
        if (arg2.isList()) {
          for (IExpr v : (IAST) arg2) {
            resultGraph.addVertex(v);
          }
        } else {
          resultGraph.addVertex(arg2);
        }

        return GraphExpr.newInstance(resultGraph);
      }
    }
    return F.NIL;
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