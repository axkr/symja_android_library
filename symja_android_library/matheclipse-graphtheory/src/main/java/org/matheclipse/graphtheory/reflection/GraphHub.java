package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>GraphHub(g)</code> - the vertices of <code>g</code> with the largest vertex degree;
 * <code>GraphHub(g, "In")</code> and <code>GraphHub(g, "Out")</code> use the in- and out-degree.
 * An undirected edge counts for both.
 */
public class GraphHub extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    boolean countIn = true;
    boolean countOut = true;
    if (ast.argSize() == 2) {
      String type = ast.arg2().isString() ? ast.arg2().toString() : "";
      if (type.equals("In")) {
        countOut = false;
      } else if (type.equals("Out")) {
        countIn = false;
      } else {
        return F.NIL;
      }
    }
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    int[] degree = new int[view.n];
    for (int e = 0; e < view.m; e++) {
      int s = view.source[e];
      int t = view.target[e];
      if (view.undirected[e]) {
        degree[s]++;
        degree[t]++;
      } else {
        if (countOut) {
          degree[s]++;
        }
        if (countIn) {
          degree[t]++;
        }
      }
    }
    int max = 0;
    for (int d : degree) {
      max = Math.max(max, d);
    }
    IASTAppendable result = F.ListAlloc();
    for (int v = 0; v < view.n; v++) {
      if (degree[v] == max) {
        result.append(view.vertex(v));
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
