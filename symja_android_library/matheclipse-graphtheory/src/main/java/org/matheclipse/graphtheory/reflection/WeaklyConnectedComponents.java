package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>WeaklyConnectedComponents(g)</code> gives the weakly connected components of the graph
 * <code>g</code>, the connected components when the direction of the edges is ignored.
 * <code>WeaklyConnectedComponents(g, {v1, v2, ...})</code> gives only the components which contain
 * one of the vertices <code>vi</code>.
 */
public class WeaklyConnectedComponents extends AbstractFunctionEvaluator {

  public WeaklyConnectedComponents() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    IExpr pattern = ast.isAST2() ? ast.arg2() : F.NIL;
    return GraphUtil.connectedComponents(gex, pattern, true, engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
