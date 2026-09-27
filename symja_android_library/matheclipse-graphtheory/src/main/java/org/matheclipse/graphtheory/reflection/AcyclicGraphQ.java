package org.matheclipse.graphtheory.reflection;

import java.util.ArrayDeque;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.builtin.GraphFunctions;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * Returns True if the graph has no cycle: a forest for undirected graphs, a DAG for directed graphs.
 * In a mixed graph an undirected edge can be walked either way, but no edge twice.
 */
public class AcyclicGraphQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.False;
    }
    return F.booleSymbol(isAcyclic(GraphView.of(gex.toData())));
  }

  /**
   * A mixed graph has a cycle iff its undirected edges have one, a directed edge joins two vertices
   * of one undirected component, or the directed edges form a cycle between the components.
   */
  private static boolean isAcyclic(GraphView view) {
    final int n = view.n;
    int[] parent = new int[n];
    for (int v = 0; v < n; v++) {
      parent[v] = v;
    }
    for (int e = 0; e < view.m; e++) {
      if (view.source[e] == view.target[e]) {
        // a self-loop
        return false;
      }
      if (view.undirected[e]) {
        int a = find(parent, view.source[e]);
        int b = find(parent, view.target[e]);
        if (a == b) {
          return false;
        }
        parent[a] = b;
      }
    }
    // Kahn's algorithm on the components joined by the directed edges
    int[] inDegree = new int[n];
    java.util.List<java.util.List<Integer>> successors = new java.util.ArrayList<>(n);
    for (int v = 0; v < n; v++) {
      successors.add(new java.util.ArrayList<Integer>());
    }
    for (int e = 0; e < view.m; e++) {
      if (!view.undirected[e]) {
        int a = find(parent, view.source[e]);
        int b = find(parent, view.target[e]);
        if (a == b) {
          return false;
        }
        successors.get(a).add(b);
        inDegree[b]++;
      }
    }
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    int components = 0;
    for (int v = 0; v < n; v++) {
      if (find(parent, v) == v) {
        components++;
        if (inDegree[v] == 0) {
          queue.add(v);
        }
      }
    }
    int removed = 0;
    while (!queue.isEmpty()) {
      int c = queue.poll();
      removed++;
      for (int d : successors.get(c)) {
        if (--inDegree[d] == 0) {
          queue.add(d);
        }
      }
    }
    return removed == components;
  }

  private static int find(int[] parent, int v) {
    while (parent[v] != v) {
      parent[v] = parent[parent[v]];
      v = parent[v];
    }
    return v;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
