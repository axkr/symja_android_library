package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.graphics.GraphGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implements CompleteKaryTree.
 * <p>
 * CompleteKaryTree[n] gives the complete binary tree with n levels.
 * <p>
 * CompleteKaryTree[n, k] gives the complete k-ary tree with n levels.
 */
public class CompleteKaryTree extends AbstractFunctionOptionEvaluator {
  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    if (ast.argSize() < 1) {
      return F.NIL;
    }

    int levels;

    IExpr arg1 = ast.arg1();
    levels = arg1.toIntDefault(-1);
    if (levels < 0) {
      return F.NIL;
    }


    int k = 2;
    if (argSize >= 2) {
      IExpr arg2 = ast.arg2();
      k = arg2.toIntDefault();
      if (k < 0) {
        return F.NIL;
      }
    }

    IASTAppendable optionsList = F.ListAlloc(2);
    boolean directed = false;
    if (options[GraphGraphics.X_DIRECTED_EDGES].isTrue()) {
      optionsList.append(F.Rule(S.DirectedEdges, S.True));
      directed = true;
    }
    optionsList.append(F.Rule(S.GraphLayout, options[GraphGraphics.X_GRAPH_LAYOUT]));

    // Calculate total vertices
    long totalVertices;
    if (levels == 0) {
      totalVertices = 0;
    } else if (k == 1) {
      // Linear chain
      totalVertices = levels;
    } else {
      // Geometric sum: (k^n - 1) / (k - 1)
      // Note: Math.pow uses doubles, efficient integer power needed or cast
      try {
        // Check for overflow or extremely large graphs
        double val = (Math.pow(k, levels) - 1) / (k - 1);
        if (val > Integer.MAX_VALUE) {
          // Limit to reasonable Java array/collection size
          return F.NIL;
        }
        totalVertices = (long) val;
      } catch (ArithmeticException e) {
        return F.NIL;
      }
    }

    Graph<IExpr, IExprEdge> graph;
    if (directed) {
      graph = new DefaultDirectedGraph<>(ExprEdge.class);
    } else {
      graph = new DefaultUndirectedGraph<>(ExprEdge.class);
    }

    if (totalVertices == 0) {
      return GraphExpr.newInstance(graph, optionsList);
    }

    // Vertices are standard integers: 1, 2, 3, ...
    for (int i = 1; i <= totalVertices; i++) {
      graph.addVertex(F.ZZ(i));
    }

    // Add Edges
    // Strategy: Iterate parent index, add k children
    // Root is 1.
    // Children of node i are: k*(i-1)+2 ... k*(i-1)+k+1
    // (1-based indexing logic)

    long currentChild = 2;
    for (int parent = 1; parent <= totalVertices; parent++) {
      if (currentChild > totalVertices) {
        break;
      }

      IExpr u = F.ZZ(parent);
      for (int branch = 0; branch < k; branch++) {
        if (currentChild > totalVertices) {
          break;
        }
        IExpr v = F.ZZ(currentChild);

        // Edge u -> v
        if (directed) {
          graph.addEdge(u, v);
        } else {
          // For undirected, order doesn't strictly matter for JGraphT logic
          // but convention is often smaller -> larger for internal storage or arbitrary
          graph.addEdge(u, v);
        }

        currentChild++;
      }
    }

    return GraphExpr.newInstance(graph, optionsList);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphGraphics.defaultGraphOptionKeys(),
        GraphGraphics.defaultGraphOptionValues());
  }
}
