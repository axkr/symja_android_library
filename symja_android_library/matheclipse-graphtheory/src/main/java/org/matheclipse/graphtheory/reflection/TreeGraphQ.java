package org.matheclipse.graphtheory.reflection;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.builtin.GraphFunctions;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
import org.matheclipse.graphtheory.expression.data.IExprEdge;

/**
 * Returns True if the graph is a tree, False otherwise. A tree is a connected graph with no cycles.
 */
public class TreeGraphQ extends AbstractEvaluator {

  @Override
  public IExpr defaultReturn() {
    return F.False;
  }

  @Override
  public IExpr evalCatched(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.False;
    }

    Graph<IExpr, ? extends IExprEdge> graph = (Graph<IExpr, ? extends IExprEdge>) gex.toData();

    if (graph.getType().isDirected()) {
      // the graph with the edge directions ignored must be a tree, so a directed
      // graph with n vertices is a tree iff it is weakly connected and has n-1 edges
      return (GraphTests.isWeaklyConnected(graph)
          && graph.edgeSet().size() == graph.vertexSet().size() - 1) ? S.True : S.False;
    } else {
      // Undirected: JGraphT provides a direct check for trees
      return GraphTests.isTree(graph) ? S.True : S.False;
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
