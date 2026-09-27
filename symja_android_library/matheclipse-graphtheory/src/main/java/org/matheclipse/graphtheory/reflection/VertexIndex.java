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
 * <code>VertexIndex(g, v)</code> - the position of vertex <code>v</code> in
 * <code>VertexList(g)</code>; a list of vertices gives a list of positions.
 */
public class VertexIndex extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    IExpr arg2 = ast.arg2();
    if (view.index.containsKey(arg2)) {
      return F.ZZ(view.index.get(arg2) + 1);
    }
    if (arg2.isList()) {
      IASTAppendable result = F.ListAlloc(arg2.argSize());
      for (IExpr v : (IAST) arg2) {
        Integer i = view.index.get(v);
        if (i == null) {
          return F.NIL;
        }
        result.append(F.ZZ(i + 1));
      }
      return result;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
