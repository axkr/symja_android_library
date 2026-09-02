package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.matheclipse.graphtheory.builtin.GraphFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.graphtheory.expression.data.GraphExpr;
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
      // "The Kirchhoff matrix of a non-simple graph and its simple graph is the same", so
      // self-loops are ignored and parallel edges are collapsed into a single adjacency.
      List<Set<Integer>> adjacent = new ArrayList<>(n);
      // the degree of a vertex counts the incident edges in both directions
      List<Set<Integer>> incident = new ArrayList<>(n);
      for (int i = 0; i < n; i++) {
        adjacent.add(new TreeSet<>());
        incident.add(new TreeSet<>());
      }

      for (Object edgeObj : g.edgeSet()) {
        IExpr source = g.getEdgeSource(edgeObj);
        IExpr target = g.getEdgeTarget(edgeObj);

        Integer iIndex = vToIndex.get(source);
        Integer jIndex = vToIndex.get(target);

        if (iIndex != null && jIndex != null) {
          int i = iIndex;
          int j = jIndex;
          // Self-loops do not contribute to the Laplacian
          if (i != j) {
            adjacent.get(i).add(j);
            incident.get(i).add(j);
            incident.get(j).add(i);
            if (!type.isDirected()) {
              adjacent.get(j).add(i);
            }
          }
        }
      }

      // L = D - A where the diagonal entry d(i,i) is the degree of the vertex vi
      for (int i = 0; i < n; i++) {
        int degree = incident.get(i).size();
        if (degree != 0) {
          rules.append(F.Rule(F.List(F.ZZ(i + 1), F.ZZ(i + 1)), F.ZZ(degree)));
        }
        for (int j : adjacent.get(i)) {
          rules.append(F.Rule(F.List(F.ZZ(i + 1), F.ZZ(j + 1)), F.CN1));
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
