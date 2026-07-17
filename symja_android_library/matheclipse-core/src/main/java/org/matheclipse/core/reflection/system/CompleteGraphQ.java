package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns True if the graph is a complete graph, False otherwise. A complete graph has every pair
 * of distinct vertices connected by a unique edge.
 */
public class CompleteGraphQ extends AbstractEvaluator {
  @Override
  public IExpr defaultReturn() {
    return F.False;
  }

  @Override
  public IExpr evalCatched(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    Graph<IExpr, ?> graph = gex.toData();
    int n = graph.vertexSet().size();

    // 1. Check for self-loops (complete graphs cannot have them)
    if (GraphTests.hasSelfLoops(graph)) {
      return F.False;
    }

    // 2. Check for multiple edges (complete graphs cannot have them)
    if (graph.getType().isAllowingMultipleEdges()) {
      return F.False;
    }

    int edgeCount = graph.edgeSet().size();

    if (graph.getType().isDirected()) {
      // Directed complete graph: n * (n - 1) edges
      return (edgeCount == n * (n - 1)) ? S.True : S.False;
    } else {
      // Undirected complete graph: n * (n - 1) / 2 edges
      return (edgeCount == n * (n - 1) / 2) ? S.True : S.False;
    }
    }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
    }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
    }
  }