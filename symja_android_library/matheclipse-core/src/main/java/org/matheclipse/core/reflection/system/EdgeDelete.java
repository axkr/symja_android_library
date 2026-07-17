package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
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
 * Returns a graph with the specified edges matching the pattern removed.
 */
public class EdgeDelete extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      IExpr arg2 = ast.arg2();
      IExpr pattern = arg2;

      // If a list of edges/patterns is provided, wrap it in Alternatives for MatchQ
      if (arg2.isList()) {
        pattern = F.Alternatives((IAST) arg2);
      }

      // 1. Handle Weighted Graphs
      if (gex.isWeightedGraph()) {
        @SuppressWarnings("unchecked")
        Graph<IExpr, ExprWeightedEdge> g = (Graph<IExpr, ExprWeightedEdge>) gex.toData();
        Graph<IExpr, ExprWeightedEdge> resultGraph;
        boolean isDirected = g.getType().isDirected();

        if (isDirected) {
          resultGraph = new DefaultDirectedWeightedGraph<>(ExprWeightedEdge.class);
        } else {
          resultGraph = new DefaultUndirectedWeightedGraph<>(ExprWeightedEdge.class);
        }

        // Copy vertices and edges
        Graphs.addAllVertices(resultGraph, g.vertexSet());
        Graphs.addAllEdges(resultGraph, g, g.edgeSet());

        List<ExprWeightedEdge> edgesToRemove = new ArrayList<>();
        for (ExprWeightedEdge edge : resultGraph.edgeSet()) {
          IExpr source = resultGraph.getEdgeSource(edge);
          IExpr target = resultGraph.getEdgeTarget(edge);

          // Construct canonical edge and syntactic sugar forms
          IAST edgeExprCanonical =
              isDirected ? F.DirectedEdge(source, target) : F.UndirectedEdge(source, target);
          IAST edgeExprSugar = isDirected ? F.Rule(source, target) : F.TwoWayRule(source, target);

          // Check if either form matches the user's pattern
          if (engine.evaluate(F.MatchQ(edgeExprCanonical, pattern)).isTrue()
              || engine.evaluate(F.MatchQ(edgeExprSugar, pattern)).isTrue()) {
            edgesToRemove.add(edge);
          }
        }

        // Apply deletion
        for (ExprWeightedEdge edge : edgesToRemove) {
          resultGraph.removeEdge(edge);
        }

        return GraphExpr.newInstance(resultGraph);
      }

      // 2. Handle Unweighted Graphs
      else {
        @SuppressWarnings("unchecked")
        Graph<IExpr, ExprEdge> g = (Graph<IExpr, ExprEdge>) gex.toData();
        Graph<IExpr, ExprEdge> resultGraph;
        boolean isDirected = g.getType().isDirected();

        if (isDirected) {
          resultGraph = new DefaultDirectedGraph<>(ExprEdge.class);
        } else {
          resultGraph = new DefaultUndirectedGraph<>(ExprEdge.class);
        }

        // Copy vertices and edges
        Graphs.addAllVertices(resultGraph, g.vertexSet());
        Graphs.addAllEdges(resultGraph, g, g.edgeSet());

        List<ExprEdge> edgesToRemove = new ArrayList<>();
        for (ExprEdge edge : resultGraph.edgeSet()) {
          IExpr source = resultGraph.getEdgeSource(edge);
          IExpr target = resultGraph.getEdgeTarget(edge);

          // Construct canonical edge and syntactic sugar forms
          IAST edgeExprCanonical =
              isDirected ? F.DirectedEdge(source, target) : F.UndirectedEdge(source, target);
          IAST edgeExprSugar = isDirected ? F.Rule(source, target) : F.TwoWayRule(source, target);

          // Check if either form matches the user's pattern
          if (engine.evaluate(F.MatchQ(edgeExprCanonical, pattern)).isTrue()
              || engine.evaluate(F.MatchQ(edgeExprSugar, pattern)).isTrue()) {
            edgesToRemove.add(edge);
          }
        }

        // Apply deletion
        for (ExprEdge edge : edgesToRemove) {
          resultGraph.removeEdge(edge);
        }

        return GraphExpr.newInstance(resultGraph);
      }
    }
    return F.NIL;
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