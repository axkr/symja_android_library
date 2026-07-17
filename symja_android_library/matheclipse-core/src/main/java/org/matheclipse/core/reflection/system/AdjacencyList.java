package org.matheclipse.core.reflection.system;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * Returns the adjacency list of a graph or the neighbors of a specific vertex. For a directed
 * graph, this returns the out-component (successors). Vertices are sorted by their index in the
 * graph's vertex list.
 */
public class AdjacencyList extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 1 || ast.argSize() == 2) {
      GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
      if (gex == null) {
        return F.NIL;
      }

      @SuppressWarnings("unchecked")
      Graph<IExpr, ?> g = gex.toData();
      IAST vertices = GraphExpr.vertexToIExpr(g);

      // Map each vertex to its parsing order index to ensure deterministic, sorted output
      Map<IExpr, Integer> vToIndex = new HashMap<>();
      for (int i = 1; i <= vertices.argSize(); i++) {
        vToIndex.put(vertices.get(i), i);
      }

      // Sort neighbors according to their order in VertexList
      Comparator<IExpr> vertexComparator = (v1, v2) -> {
        Integer idx1 = vToIndex.get(v1);
        Integer idx2 = vToIndex.get(v2);
        if (idx1 == null)
          idx1 = Integer.MAX_VALUE;
        if (idx2 == null)
          idx2 = Integer.MAX_VALUE;
        return idx1.compareTo(idx2);
      };

      if (ast.argSize() == 1) {
        // Return adjacency lists for all vertices
        IASTAppendable result = F.ListAlloc(vertices.argSize());
        for (int i = 1; i <= vertices.argSize(); i++) {
          IExpr v = vertices.get(i);
          List<IExpr> successors = Graphs.successorListOf(g, v);
          successors.sort(vertexComparator);

          IASTAppendable neighbors = F.ListAlloc(successors.size());
          for (IExpr neighbor : successors) {
            neighbors.append(neighbor);
          }
          result.append(neighbors);
        }
        return result;
      } else {
        // Return adjacency list for a specific vertex
        IExpr v = ast.arg2();
        if (g.containsVertex(v)) {
          List<IExpr> successors = Graphs.successorListOf(g, v);
          successors.sort(vertexComparator);

          IASTAppendable neighbors = F.ListAlloc(successors.size());
          for (IExpr neighbor : successors) {
            neighbors.append(neighbor);
          }
          return neighbors;
        }
      }
    }
    return F.NIL;
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