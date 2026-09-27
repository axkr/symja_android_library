package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.builtin.GraphFunctions;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/** <code>SimpleGraphQ(g)</code> - <code>True</code> if the graph has no self-loops and no parallel edges. <code>False</code> for anything but a graph. */
public class SimpleGraphQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphFunctions.getGraphExpr(ast.arg1());
    if (gex == null) {
      return F.False;
    }
    GraphView view = GraphView.of(gex.toData());
    return F.booleSymbol(!hasSelfLoop(view) && !hasParallelEdges(view));
  }

  static boolean hasSelfLoop(GraphView view) {
    for (int e = 0; e < view.m; e++) {
      if (view.source[e] == view.target[e]) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasParallelEdges(GraphView view) {
    java.util.Set<Long> pairs = new java.util.HashSet<Long>();
    for (int e = 0; e < view.m; e++) {
      int a = view.source[e];
      int b = view.target[e];
      if (view.undirected[e] && a > b) {
        int t = a;
        a = b;
        b = t;
      }
      if (!pairs.add(((long) a << 32) | b)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
