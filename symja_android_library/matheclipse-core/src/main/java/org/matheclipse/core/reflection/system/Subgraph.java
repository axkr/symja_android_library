package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
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
 * Returns the subgraph induced by a specified set of vertices.
 */
public class Subgraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      IExpr arg2 = ast.arg2();
      Set<IExpr> vertexSet = new HashSet<>();

      // Extract the target vertices to form the induced subgraph
      if (arg2.isList()) {
        for (IExpr v : (IAST) arg2) {
          vertexSet.add(v);
        }
      } else {
        // Fallback for a single vertex argument
        vertexSet.add(arg2);
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

        // Add valid vertices to the subgraph
        for (IExpr v : vertexSet) {
          if (g.containsVertex(v)) {
            resultGraph.addVertex(v);
          }
        }

        // Add corresponding edges and preserve weights
        for (ExprWeightedEdge edge : g.edgeSet()) {
          IExpr source = g.getEdgeSource(edge);
          IExpr target = g.getEdgeTarget(edge);

          if (resultGraph.containsVertex(source) && resultGraph.containsVertex(target)) {
            ExprWeightedEdge newEdge = resultGraph.addEdge(source, target);
            if (newEdge != null) {
              resultGraph.setEdgeWeight(newEdge, g.getEdgeWeight(edge));
            }
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

        // Add valid vertices to the subgraph
        for (IExpr v : vertexSet) {
          if (g.containsVertex(v)) {
            resultGraph.addVertex(v);
          }
        }

        // Add corresponding edges
        for (ExprEdge edge : g.edgeSet()) {
          IExpr source = g.getEdgeSource(edge);
          IExpr target = g.getEdgeTarget(edge);

          if (resultGraph.containsVertex(source) && resultGraph.containsVertex(target)) {
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