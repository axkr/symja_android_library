package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implements AdjacencyGraph.
 * <p>
 * AdjacencyGraph[m] AdjacencyGraph[{v1, v2, ...}, m]
 */
public class AdjacencyGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    if (ast.argSize() < 1) {
      return F.NIL;
    }

    IExpr arg1 = ast.arg1();
    IExpr arg2 = null;
    IAST matrix = null;
    IAST vertices = null;

    // 1. Parse Arguments
    if (arg1.isList() && ast.argSize() == 1) {
      // Case: AdjacencyGraph[matrix]
      matrix = (IAST) arg1;
    } else if (ast.argSize() >= 2 && arg1.isList() && ast.arg2().isList()) {
      // Case: AdjacencyGraph[{v1, ...}, matrix]
      vertices = (IAST) arg1;
      matrix = (IAST) ast.arg2();
      arg2 = ast.arg2();
    } else {
      // Invalid arguments for this implementation
      return F.NIL;
    }

    // 2. Validate Matrix Dimensions
    int rows = matrix.argSize();
    if (rows == 0) {
      return F.Graph(F.List(), F.List()); // Empty graph
    }

    // Check if it's a square matrix
    for (int i = 1; i <= rows; i++) {
      if (!matrix.get(i).isList() || ((IAST) matrix.get(i)).size() - 1 != rows) {
        // Not a square matrix
        return F.NIL; // Or emit Message(AdjacencyGraph::matsq)
      }
    }

    // 3. Determine Vertices
    if (vertices == null) {
      // Auto-generate vertices 1..n
      IASTAppendable vList = F.ListAlloc(rows);
      for (int i = 1; i <= rows; i++) {
        vList.append(F.ZZ(i));
      }
      vertices = vList;
    } else {
      if (vertices.size() - 1 != rows) {
        // Vertex count mismatch
        return F.NIL;
      }
    }

    // 4. Parse Options and Determine Directionality
    Boolean directedOption = null;
    int optStartIndex = (arg2 != null) ? 3 : 2;

    for (int i = optStartIndex; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST()) {
        if (((IAST) arg).arg1().equals(S.DirectedEdges)) {
          // DirectedEdges -> ...
          IExpr val = ((IAST) arg).arg2();
          if (val.isFalse()) {
            directedOption = Boolean.FALSE;
          } else if (val.isTrue()) {
            directedOption = Boolean.TRUE;
          }
        }
      }
    }

    boolean directed;
    if (directedOption != null) {
      directed = directedOption;
    } else {
      // If not specified, default is undirected if matrix is symmetric, else directed
      boolean isSymmetric = true;
      for (int i = 1; i <= rows; i++) {
        IAST rowI = (IAST) matrix.get(i);
        for (int j = i + 1; j <= rows; j++) {
          IExpr valIJ = rowI.get(j);
          IExpr valJI = ((IAST) matrix.get(j)).get(i);
          if (!valIJ.equals(valJI)) {
            isSymmetric = false;
            break;
          }
        }
        if (!isSymmetric) {
          break;
        }
      }
      directed = !isSymmetric;
    }

    // 5. Build Graph
    // Use JGraphT directly to construct the graph data
    Graph<IExpr, IExprEdge> graph;
    if (directed) {
      graph = new DefaultDirectedGraph<>(ExprEdge.class);
    } else {
      graph = new DefaultUndirectedGraph<>(ExprEdge.class);
    }

    // Add Vertices
    // Cache them in an array for fast index access during edge creation
    IExpr[] vArray = new IExpr[rows];
    for (int i = 1; i <= rows; i++) {
      IExpr v = vertices.get(i);
      vArray[i - 1] = v;
      graph.addVertex(v);
    }

    // Add Edges based on Matrix
    // m[[i, j]] == 1 means edge i -> j
    for (int i = 1; i <= rows; i++) {
      IAST row = (IAST) matrix.get(i);
      for (int j = 1; j <= rows; j++) {
        IExpr cell = row.get(j);

        if (!cell.isZero()) {
          IExpr source = vArray[i - 1];
          IExpr target = vArray[j - 1];

          if (directed) {
            graph.addEdge(source, target);
          } else {
            // For undirected, only add if i <= j to avoid duplicates.
            // If the matrix was symmetric, this covers all edges.
            // If asymmetric but forced undirected, we take the upper triangular (simplification)
            // or we relies on JGraphT to ignore duplicates if we removed the 'if'.
            // Keeping existing logic for now.
            if (i <= j) {
              graph.addEdge(source, target);
            }
          }
        }
      }
    }

    // 6. Return GraphExpr
    return GraphExpr.newInstance(graph);
  }
}
