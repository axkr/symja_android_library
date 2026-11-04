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
 * <code>IncidenceList(g, v)</code> - the edges of <code>g</code> which have <code>v</code> as an
 * end point, in the order of <code>EdgeList(g)</code>; <code>IncidenceList(g, {v1, v2, ...})</code>
 * the edges incident to any of the vertices. The graph may also be given as a list of edges or
 * rules.
 */
public class IncidenceList extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    boolean[] selected = new boolean[view.n];
    IExpr arg2 = ast.arg2();
    if (arg2.isList()) {
      for (IExpr v : (IAST) arg2) {
        if (!select(view, v, selected)) {
          return F.NIL;
        }
      }
    } else if (!select(view, arg2, selected)) {
      return F.NIL;
    }
    IAST edges = GraphExpr.edgesToIExpr(gex.toData())[0];
    IASTAppendable result = F.ListAlloc();
    for (int e = 0; e < view.m; e++) {
      if (selected[view.source[e]] || selected[view.target[e]]) {
        result.append(edges.get(e + 1));
      }
    }
    return result;
  }

  private static boolean select(GraphView view, IExpr v, boolean[] selected) {
    Integer i = view.index.get(v);
    if (i == null) {
      return false;
    }
    selected[i] = true;
    return true;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
