package org.matheclipse.core.reflection.system;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.matheclipse.core.eval.Errors;
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
 * Implements TreeGraph.
 * <p>
 * Supported usage: TreeGraph[{e1, e2, ...}] TreeGraph[{v1, ...}, {e1, ...}] TreeGraph[{v1, ...},
 * {u1, ...}] (Predecessors)
 */
public class TreeGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    if (ast.argSize() < 1) {
      return F.NIL;
    }

    IExpr arg1 = ast.arg1();
    IExpr arg2 = (ast.argSize() >= 2) ? ast.arg2() : null;

    IAST vertices = null;
    IAST edgesOrPreds = null;
    IASTAppendable options = F.ListAlloc();
    boolean isPredecessorForm = false;

    // 1. Parse Arguments
    if (ast.argSize() == 1 && arg1.isList()) {
      // TreeGraph[{e1, ...}]
      vertices = F.List(); // Auto-detect
      edgesOrPreds = (IAST) arg1;
    } else if (ast.argSize() >= 2 && arg1.isList() && arg2.isList()) {
      vertices = (IAST) arg1;
      edgesOrPreds = (IAST) arg2;

      // Distinguish Case: {v...}, {e...} vs {v...}, {u...}
      // If the second list contains Rules or DirectedEdges, it's an edge list.
      // Otherwise, it's a predecessor list.
      if (edgesOrPreds.argSize() > 0) {
        IExpr first = edgesOrPreds.arg1();
        if (!first.isRuleAST() && !first.isAST(S.DirectedEdge) && !first.isAST(S.UndirectedEdge)) {
          isPredecessorForm = true;
        }
      }

      // Collect options starting from arg 3
      for (int i = 3; i < ast.size(); i++) {
        if (ast.get(i).isRuleAST())
          options.append(ast.get(i));
      }
    } else {
      // Check for TreeGraph[{rules...}] single argument case handled above
      // If arguments don't match list patterns
      return F.NIL;
    }

    // Also check options if argSize=1 (TreeGraph[{e}, opts...])
    if (ast.argSize() > 1 && arg1.isList() && !ast.arg2().isList()) {
      edgesOrPreds = (IAST) arg1;
      vertices = F.List();
      for (int i = 2; i <= ast.size(); i++) {
        if (ast.get(i).isRuleAST())
          options.append(ast.get(i));
      }
    }

    // 2. Build the Graph
    // TreeGraph usually defaults to Directed unless UndirectedEdge is used.
    Graph<IExpr, IExprEdge> graph = new DefaultDirectedGraph<>(ExprEdge.class);

    // Check for "DirectedEdges -> False" option or UndirectedEdge presence to switch type?
    // Wolfram TreeGraph creates DirectedGraph by default for rules/predecessors.

    Set<IExpr> vSet = new HashSet<>();
    if (vertices != null && vertices.argSize() > 0) {
      for (IExpr v : vertices) {
        graph.addVertex(v);
        vSet.add(v);
      }
    }

    if (isPredecessorForm) {
      // TreeGraph[{v1, ...}, {u1, ...}] -> u_i is predecessor of v_i
      // Lengths must match
      if (vertices.size() != edgesOrPreds.size()) {
        // "Vertex and predecessor lists must have the same length."
        return F.NIL;
      }
      for (int i = 1; i < vertices.size(); i++) {
        IExpr v = vertices.get(i);
        IExpr u = edgesOrPreds.get(i);

        // If v or u not in graph, add them (though v should be there from above)
        if (!vSet.contains(u)) {
          graph.addVertex(u);
          vSet.add(u);
        }
        if (!vSet.contains(v)) {
          graph.addVertex(v);
          vSet.add(v);
        }

        // Add Edge u -> v
        graph.addEdge(u, v);
      }
    } else {
      // Standard Edge List
      for (IExpr e : edgesOrPreds) {
        if (e.isRuleAST() || e.isAST(S.DirectedEdge, 3)) {
          IExpr u = ((IAST) e).arg1();
          IExpr v = ((IAST) e).arg2();
          if (!vSet.contains(u)) {
            graph.addVertex(u);
            vSet.add(u);
          }
          if (!vSet.contains(v)) {
            graph.addVertex(v);
            vSet.add(v);
          }
          graph.addEdge(u, v);
        } else if (e.isAST(S.UndirectedEdge, 3)) {
          // If we encounter UndirectedEdge, we might need an UndirectedGraph container.
          // For simplicity here, we stick to Directed or require mixed graph support.
          // Symja GraphExpr often wraps JGraphT which can be strictly directed/undirected.
          // If mixed, we treat as directed pair?
          // Let's assume input matches the container or fail.
          IExpr u = ((IAST) e).arg1();
          IExpr v = ((IAST) e).arg2();
          if (!vSet.contains(u)) {
            graph.addVertex(u);
            vSet.add(u);
          }
          if (!vSet.contains(v)) {
            graph.addVertex(v);
            vSet.add(v);
          }
          graph.addEdge(u, v);
        }
      }
    }

    // Validate Tree Property
    // A finite graph is a tree if and only if:
    // 1. It is non-empty (usually V >= 1)
    // 2. It is connected (weakly connected for directed)
    // 3. |E| = |V| - 1

    int vCount = graph.vertexSet().size();
    int eCount = graph.edgeSet().size();

    if (vCount == 0) {
      // Empty graph is usually NOT a tree in strict definitions (requires 1 node),
      // but return empty Graph[].
      return GraphExpr.newInstance(graph);
    }

    if (eCount != vCount - 1) {
      return Errors.printMessage(S.TreeGraph, "The graph is not a tree.");
    }

    ConnectivityInspector<IExpr, IExprEdge> inspector = new ConnectivityInspector<>(graph);
    if (!inspector.isConnected()) {
      return Errors.printMessage(S.TreeGraph, "The graph is not connected.");
    }

    return GraphExpr.newInstance(graph, options, "LayeredEmbedding");
  }
}
