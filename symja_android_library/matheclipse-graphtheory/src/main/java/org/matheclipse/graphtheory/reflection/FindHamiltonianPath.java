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
 * <code>FindHamiltonianPath(g)</code> - a path visiting every vertex of <code>g</code> once, or
 * <code>{}</code>; <code>FindHamiltonianPath(g, s, t)</code> - one from <code>s</code> to
 * <code>t</code>.
 */
public class FindHamiltonianPath extends AbstractFunctionEvaluator {

  /** The size of the search tree after which the search gives up. */
  private static final long MAX_NODES = 20_000_000L;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    int s = -1;
    int t = -1;
    if (ast.isAST3()) {
      Integer source = view.index.get(ast.arg2());
      Integer target = view.index.get(ast.arg3());
      if (source == null || target == null) {
        return F.NIL;
      }
      s = source;
      t = target;
    } else if (!ast.isAST1()) {
      return F.NIL;
    }
    int[] path = GraphUtil.hamiltonianPath(view, s, t, MAX_NODES);
    if (path == null) {
      return F.NIL;
    }
    return F.mapRange(0, path.length, i -> view.vertex(path[i]));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
