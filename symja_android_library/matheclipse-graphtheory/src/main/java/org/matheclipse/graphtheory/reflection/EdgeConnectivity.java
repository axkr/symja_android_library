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
import java.util.List;
import org.matheclipse.graphtheory.eval.FlowNetwork;

/**
 * <code>EdgeConnectivity(g)</code> - the smallest total weight of edges whose removal disconnects
 * <code>g</code> (strongly, for a directed graph); <code>EdgeConnectivity(g, s, t)</code> - the
 * smallest one which separates <code>t</code> from <code>s</code>.
 */
public class EdgeConnectivity extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    if (view.hasNegativeWeight()) {
      return F.NIL;
    }
    boolean integer = GraphUtil.hasIntegerWeights(gex);
    if (ast.isAST1()) {
      if (view.n < 2) {
        return F.C0;
      }
      Object[] cut = GraphUtil.globalMinimumCut(view);
      return GraphUtil.weightNumber((Double) cut[0], integer);
    }
    if (!ast.isAST3()) {
      return F.NIL;
    }
    Integer s = view.index.get(ast.arg2());
    Integer t = view.index.get(ast.arg3());
    if (s == null || t == null || s.equals(t)) {
      return F.NIL;
    }
    int[] forward = new int[view.m];
    int[] backward = new int[view.m];
    FlowNetwork network = GraphUtil.edgeNetwork(view, forward, backward);
    return GraphUtil.weightNumber(network.maxFlow(s, t), integer);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
