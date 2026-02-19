package org.matheclipse.core.reflection.system;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ExprEdge;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.IExprEdge;
import org.matheclipse.core.graphics.GraphGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implements KaryTree.
 * <p>
 * KaryTree[n] gives a binary tree with n vertices.
 * <p>
 * KaryTree[n, k] gives a k-ary tree with n vertices.
 */
public class KaryTree extends CompleteKaryTree {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    if (ast.argSize() < 1) {
      return F.NIL;
    }

    // Unlike CompleteKaryTree, n here is the vertex count, not levels.
    int totalVertices;
    IExpr arg1 = ast.arg1();
    totalVertices = arg1.toIntDefault(-1);
    if (totalVertices < 0) {
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

    Graph<IExpr, IExprEdge> graph;
    if (directed) {
      graph = new DefaultDirectedGraph<>(ExprEdge.class);
    } else {
      graph = new DefaultUndirectedGraph<>(ExprEdge.class);
    }

    if (totalVertices == 0) {
      return GraphExpr.newInstance(graph, optionsList);
    }

    for (int i = 1; i <= totalVertices; i++) {
      graph.addVertex(F.ZZ(i));
    }

    // Fill the tree level by level.
    // The parent of node 'c' in a k-ary tree is floor((c - 2) / k) + 1.
    // Alternatively, we iterate parents and attach k children until we run out of vertices.

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

        if (directed) {
          graph.addEdge(u, v);
        } else {
          graph.addEdge(u, v);
        }

        currentChild++;
      }
    }

    return GraphExpr.newInstance(graph, optionsList);
  }
}