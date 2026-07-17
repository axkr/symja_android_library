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
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

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
      // A directed tree (arborescence) must be weakly connected and acyclic
      return (GraphTests.isWeaklyConnected(graph) && isDirectedAcyclic(graph)) ? S.True : S.False;
    } else {
      // Undirected: JGraphT provides a direct check for trees
      return GraphTests.isTree(graph) ? S.True : S.False;
    }
  }

  private boolean isDirectedAcyclic(Graph<IExpr, ? extends IExprEdge> graph) {
    try {
      // Attempt to run a topological sort; if it fails, the graph contains a cycle
      new org.jgrapht.traverse.TopologicalOrderIterator<>(graph);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
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
