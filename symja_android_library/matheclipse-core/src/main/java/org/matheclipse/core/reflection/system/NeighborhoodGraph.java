package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Set;
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
 * Returns the neighborhood graph for a given vertex. NeighborhoodGraph(graph, vertex) gives the
 * 1-neighborhood. NeighborhoodGraph(graph, vertex, d) gives the d-neighborhood.
 */
public class NeighborhoodGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    @SuppressWarnings("unchecked")
    Graph<IExpr, ?> baseGraph = gex.toData();

    IExpr vertex = ast.arg2();

    // If the vertex is not in the graph, return unevaluated
    if (!baseGraph.containsVertex(vertex)) {
      return F.NIL;
    }

    int d = 1;
    if (ast.argSize() == 3) {
      if (!ast.arg3().isInteger()) {
        return F.NIL;
      }
      d = ast.arg3().toIntDefault();
      if (d < 0) {
        return F.NIL;
      }
    }

    // Collect vertices up to distance d using Breadth-First traversal
    Set<IExpr> vertexSet = new HashSet<>();
    vertexSet.add(vertex);

    Set<IExpr> currentLayer = new HashSet<>();
    currentLayer.add(vertex);

    for (int i = 0; i < d; i++) {
      Set<IExpr> nextLayer = new HashSet<>();
      for (IExpr u : currentLayer) {
        if (baseGraph.getType().isDirected()) {
          nextLayer.addAll(Graphs.successorListOf(baseGraph, u));
          nextLayer.addAll(Graphs.predecessorListOf(baseGraph, u));
        } else {
          nextLayer.addAll(Graphs.neighborListOf(baseGraph, u));
        }
      }
      nextLayer.removeAll(vertexSet);
      vertexSet.addAll(nextLayer);
      currentLayer = nextLayer;
    }

    // 1. Handle Weighted Graphs
    if (gex.isWeightedGraph()) {
      @SuppressWarnings("unchecked")
      Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) baseGraph;
      Graph<IExpr, ExprWeightedEdge> resultGraph;

      if (g.getType().isDirected()) {
        resultGraph = new DefaultDirectedWeightedGraph<>(ExprWeightedEdge.class);
      } else {
        resultGraph = new DefaultUndirectedWeightedGraph<>(ExprWeightedEdge.class);
      }

      for (IExpr v : vertexSet) {
        resultGraph.addVertex(v);
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
    Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) baseGraph;
    Graph<IExpr, ExprEdge> resultGraph;

    if (g.getType().isDirected()) {
      resultGraph = new DefaultDirectedGraph<>(ExprEdge.class);
    } else {
      resultGraph = new DefaultUndirectedGraph<>(ExprEdge.class);
    }

    for (IExpr v : vertexSet) {
      resultGraph.addVertex(v);
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

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
