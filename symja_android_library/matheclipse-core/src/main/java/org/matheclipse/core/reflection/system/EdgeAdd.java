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
 * Returns a new graph with the specified edges added. If the edges contain vertices not present in
 * the original graph, those vertices are automatically added.
 */
public class EdgeAdd extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      IExpr arg2 = ast.arg2();
      IAST edgesToAdd;

      // Support either a single edge or a list of edges
      if (arg2.isList()) {
        edgesToAdd = (IAST) arg2;
      } else {
        edgesToAdd = F.List(arg2);
      }

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

        // Process new edges
        for (IExpr edgeExpr : edgesToAdd) {
          if (edgeExpr.isAST2()) { // Ensure it has a source and target (e.g., DirectedEdge, Rule)
            IAST edgeAst = (IAST) edgeExpr;
            IExpr source = edgeAst.first();
            IExpr target = edgeAst.second();

            // Implicitly add vertices if they don't exist
            resultGraph.addVertex(source);
            resultGraph.addVertex(target);

            // Add the edge
            resultGraph.addEdge(source, target);
          }
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

        // Process new edges
        for (IExpr edgeExpr : edgesToAdd) {
          if (edgeExpr.isAST2()) { // Ensure it has a source and target
            IAST edgeAst = (IAST) edgeExpr;
            IExpr source = edgeAst.first();
            IExpr target = edgeAst.second();

            // Implicitly add vertices if they don't exist
            resultGraph.addVertex(source);
            resultGraph.addVertex(target);

            // Add the edge
            resultGraph.addEdge(source, target);
          }
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