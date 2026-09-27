package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>FindVertexCut(g)</code> - a minimum set of vertices whose removal disconnects
 * <code>g</code>; <code>FindVertexCut(g, s, t)</code> - a minimum set separating <code>s</code> and
 * <code>t</code>, the one closest to <code>t</code>, and <code>{}</code> for adjacent vertices. The
 * edge directions are ignored.
 */
public class FindVertexCut extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    int[] cut;
    if (ast.isAST1()) {
      if (view.n == 0) {
        return F.CEmptyList;
      }
      cut = GraphUtil.globalMinimumVertexCut(view);
    } else if (ast.isAST3()) {
      Integer s = view.index.get(ast.arg2());
      Integer t = view.index.get(ast.arg3());
      if (s == null || t == null || s.equals(t)) {
        return F.NIL;
      }
      cut = GraphUtil.minimumVertexSeparator(view, s, t);
      if (cut == null) {
        return F.CEmptyList;
      }
    } else {
      return F.NIL;
    }
    final int[] c = cut;
    return F.mapRange(0, c.length, i -> view.vertex(c[i]));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
