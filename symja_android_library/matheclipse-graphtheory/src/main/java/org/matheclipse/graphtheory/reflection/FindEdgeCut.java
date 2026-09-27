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
 * <code>FindEdgeCut(g)</code> - the edges of a minimum cut of <code>g</code> (the one of
 * <code>FindMinimumCut</code>); <code>FindEdgeCut(g, s, t)</code> - the edges of the minimum cut
 * separating <code>t</code> from <code>s</code> which is closest to <code>s</code>. The edges come
 * in <code>EdgeList</code> order.
 */
public class FindEdgeCut extends AbstractFunctionEvaluator {

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
    boolean[] shore;
    if (ast.isAST1()) {
      Object[] cut = GraphUtil.globalMinimumCut(view);
      if (cut == null) {
        return F.NIL;
      }
      shore = (boolean[]) cut[1];
    } else if (ast.isAST3()) {
      Integer s = view.index.get(ast.arg2());
      Integer t = view.index.get(ast.arg3());
      if (s == null || t == null || s.equals(t)) {
        return F.NIL;
      }
      int[] forward = new int[view.m];
      int[] backward = new int[view.m];
      FlowNetwork network = GraphUtil.edgeNetwork(view, forward, backward);
      network.maxFlow(s, t);
      shore = network.sourceSide();
    } else {
      return F.NIL;
    }
    List<Integer> edges = GraphUtil.cutEdges(view, shore);
    IASTAppendable result = F.ListAlloc(edges.size());
    for (int e : edges) {
      result.append(F.binaryAST2(view.undirected[e] ? S.UndirectedEdge : S.DirectedEdge,
          view.vertex(view.source[e]), view.vertex(view.target[e])));
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
