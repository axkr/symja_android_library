package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns the global clustering coefficient for a graph. This is the ratio of the total number of
 * actual edges between all neighbors to the total number of possible edges between all neighbors
 * (closed triplets / all triplets).
 */
public class GlobalClusteringCoefficient extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();
      boolean isDirected = g.getType().isDirected();

      long totalNum = 0;
      long totalDen = 0;

      for (IExpr v : g.vertexSet()) {
        Set<IExpr> nSet = new HashSet<>();
        if (isDirected) {
          nSet.addAll(Graphs.predecessorListOf(g, v));
          nSet.addAll(Graphs.successorListOf(g, v));
        } else {
          nSet.addAll(Graphs.neighborListOf(g, v));
        }
        nSet.remove(v); // Ignore self-loops

        int k = nSet.size();
        if (k < 2)
          continue;

        int actualEdges = 0;
        List<IExpr> nList = new ArrayList<>(nSet);

        if (isDirected) {
          for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
              if (i != j && g.containsEdge(nList.get(i), nList.get(j))) {
                actualEdges++;
              }
            }
          }
        } else {
          for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
              if (g.containsEdge(nList.get(i), nList.get(j))) {
                actualEdges++;
              }
            }
          }
        }

        totalNum += actualEdges;
        totalDen += isDirected ? (long) k * (k - 1) : (long) k * (k - 1) / 2L;
      }

      if (totalDen == 0) {
        return F.C0;
      }
      return F.fraction(totalNum, totalDen);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}