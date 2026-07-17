package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns True if the graph is acyclic (a forest for undirected graphs, or a DAG for directed
 * graphs), False otherwise.
 */
public class AcyclicGraphQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.False;
    }

    @SuppressWarnings("unchecked")
    Graph<IExpr, ?> g = gex.toData();

    if (g.getType().isDirected()) {
      try {
        // Directed: Use TopologicalOrderIterator to detect cycles
        TopologicalOrderIterator<IExpr, ?> iterator = new TopologicalOrderIterator<>(g);
        while (iterator.hasNext()) {
          iterator.next();
        }
        return S.True;
      } catch (IllegalArgumentException e) {
        return S.False;
      }
    } else {
      // Undirected: A graph is acyclic (a forest) if it has no cycles.
      // JGraphT's GraphTests.isTree(g) checks for connectivity + acyclic.
      // For general forest detection, we check if the graph contains any cycles.
      // A common property: Cycle-free if EdgeCount == VertexCount - ConnectedComponentsCount
      return GraphTests.isTree(g) || isForest(g) ? S.True : S.False;
    }
  }

  private static boolean isForest(Graph<?, ?> g) {
    // A graph is a forest if it contains no cycles.
    // JGraphT doesn't have a direct "isForest" method, so we use CycleBasis.
    try {
      return new org.jgrapht.alg.cycle.PatonCycleBase<>(g).getCycleBasis().getCycles().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}