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
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Returns the local clustering coefficient for vertices in a graph. For a vertex with degree k, it
 * calculates the ratio of existing edges between its neighbors to the number of possible edges
 * between them.
 */
public class LocalClusteringCoefficient extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1 || ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();
      boolean isDirected = g.getType().isDirected();

      if (ast.argSize() == 1) {
        // Return a list of local clustering coefficients for all vertices
        IAST vertices = GraphExpr.vertexToIExpr(g);
        IASTAppendable result = F.ListAlloc(vertices.argSize());
        for (int i = 1; i <= vertices.argSize(); i++) {
          result.append(getLocalCC(g, vertices.get(i), isDirected));
        }
        return result;
      } else {
        // Return the local clustering coefficient for a specific vertex
        IExpr v = ast.arg2();
        if (g.containsVertex(v)) {
          return getLocalCC(g, v, isDirected);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Helper method to compute the exact fractional clustering coefficient for a single vertex.
   */
  public static IExpr getLocalCC(Graph<IExpr, ?> g, IExpr v, boolean isDirected) {
    Set<IExpr> nSet = new HashSet<>();
    if (isDirected) {
      nSet.addAll(Graphs.predecessorListOf(g, v));
      nSet.addAll(Graphs.successorListOf(g, v));
    } else {
      nSet.addAll(Graphs.neighborListOf(g, v));
    }
    nSet.remove(v); // Ignore self-loops

    int k = nSet.size();
    if (k < 2) {
      return F.C0;
    }

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

    long num = actualEdges;
    long den = isDirected ? (long) k * (k - 1) : (long) k * (k - 1) / 2L;

    return F.fraction(num, den);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}