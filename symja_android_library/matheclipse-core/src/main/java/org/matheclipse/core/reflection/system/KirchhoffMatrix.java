package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
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
 * Returns the Kirchhoff matrix (also known as Laplacian matrix) of a graph. For an undirected
 * graph, the Kirchhoff matrix is given by D - A, where D is the diagonal degree matrix and A is the
 * adjacency matrix. For a directed graph, the out-degree is used for the diagonal degree matrix.
 */
public class KirchhoffMatrix extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }

    Graph<IExpr, Object> g = (Graph<IExpr, Object>) gex.toData();
    GraphType type = g.getType();

    // Retrieve the vertices in their original parsing order
    IAST vertices = GraphExpr.vertexToIExpr(g);
    int n = vertices.argSize();

    if (n == 0) {
      return F.sparseArray(F.List(), new int[] {0, 0});
    }

    // Map each vertex to its 0-based index
    Map<IExpr, Integer> vToIndex = new HashMap<>();
    for (int i = 1; i <= n; i++) {
      vToIndex.put(vertices.get(i), i - 1);
    }

    boolean isWeighted = gex.isWeightedGraph();
    IASTAppendable rules = F.ListAlloc();

    if (isWeighted) {
      // Use a list of maps to represent sparse rows and avoid O(N^2) memory footprint
      List<Map<Integer, Double>> wMatrix = new ArrayList<>(n);
      for (int i = 0; i < n; i++) {
        wMatrix.add(new HashMap<>());
      }

      // Populate adjacency weights (off-diagonal elements)
      for (Object edgeObj : g.edgeSet()) {
        IExpr source = g.getEdgeSource(edgeObj);
        IExpr target = g.getEdgeTarget(edgeObj);
        double w = g.getEdgeWeight(edgeObj);

        Integer iIndex = vToIndex.get(source);
        Integer jIndex = vToIndex.get(target);

        if (iIndex != null && jIndex != null) {
          int i = iIndex;
          int j = jIndex;
          // Self-loops do not contribute to the off-diagonal Laplacian
          if (i != j) {
            wMatrix.get(i).put(j, wMatrix.get(i).getOrDefault(j, 0.0) - w);
            if (!type.isDirected()) {
              wMatrix.get(j).put(i, wMatrix.get(j).getOrDefault(i, 0.0) - w);
            }
          }
        }
      }

      // Calculate the diagonal (Degree matrix)
      // The diagonal is the sum of the out-degrees, which elegantly corresponds
      // to the negative sum of the non-diagonal elements in the same row.
      for (int i = 0; i < n; i++) {
        double sum = 0.0;
        for (Map.Entry<Integer, Double> entry : wMatrix.get(i).entrySet()) {
          int j = entry.getKey();
          double val = entry.getValue();
          sum -= val;
          if (val != 0.0) {
            rules.append(F.Rule(F.List(F.ZZ(i + 1), F.ZZ(j + 1)), F.num(val)));
          }
        }
        if (sum != 0.0) {
          rules.append(F.Rule(F.List(F.ZZ(i + 1), F.ZZ(i + 1)), F.num(sum)));
        }
      }

    } else {
      // Use a list of maps to represent sparse rows and avoid O(N^2) memory footprint
      List<Map<Integer, Integer>> matrix = new ArrayList<>(n);
      for (int i = 0; i < n; i++) {
        matrix.add(new HashMap<>());
      }

      // Populate adjacency bounds (off-diagonal elements)
      for (Object edgeObj : g.edgeSet()) {
        IExpr source = g.getEdgeSource(edgeObj);
        IExpr target = g.getEdgeTarget(edgeObj);

        Integer iIndex = vToIndex.get(source);
        Integer jIndex = vToIndex.get(target);

        if (iIndex != null && jIndex != null) {
          int i = iIndex;
          int j = jIndex;
          // Self-loops do not contribute to the off-diagonal Laplacian
          if (i != j) {
            matrix.get(i).put(j, matrix.get(i).getOrDefault(j, 0) - 1);
            if (!type.isDirected()) {
              matrix.get(j).put(i, matrix.get(j).getOrDefault(i, 0) - 1);
            }
          }
        }
      }

      // Calculate the diagonal (Degree matrix)
      // The diagonal is the sum of the out-degrees, which elegantly corresponds
      // to the negative sum of the non-diagonal elements in the same row.
      for (int i = 0; i < n; i++) {
        int sum = 0;
        for (Map.Entry<Integer, Integer> entry : matrix.get(i).entrySet()) {
          int j = entry.getKey();
          int val = entry.getValue();
          sum -= val;
          if (val != 0) {
            rules.append(F.Rule(F.List(F.ZZ(i + 1), F.ZZ(j + 1)), F.ZZ(val)));
          }
        }
        if (sum != 0) {
          rules.append(F.Rule(F.List(F.ZZ(i + 1), F.ZZ(i + 1)), F.ZZ(sum)));
        }
      }
    }

    return F.sparseArray(rules, new int[] {n, n});
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