package org.matheclipse.graphtheory.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>EdgeIndex(g, e)</code> - the position of edge <code>e</code> in <code>EdgeList(g)</code>; a
 * list of edges gives a list of positions. An undirected edge matches either way round, and
 * <code>u -&gt; v</code> matches an undirected edge as well.
 */
public class EdgeIndex extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    IExpr arg2 = ast.arg2();
    if (isEdge(arg2)) {
      int e = edgeIndex(view, arg2);
      return e < 0 ? F.NIL : F.ZZ(e + 1);
    }
    if (arg2.isList()) {
      IASTAppendable result = F.ListAlloc(arg2.argSize());
      for (IExpr edge : (IAST) arg2) {
        int e = isEdge(edge) ? edgeIndex(view, edge) : -1;
        if (e < 0) {
          return F.NIL;
        }
        result.append(F.ZZ(e + 1));
      }
      return result;
    }
    return F.NIL;
  }

  private static boolean isEdge(IExpr edge) {
    return edge.isAST(S.DirectedEdge, 3) || edge.isAST(S.UndirectedEdge, 3) || edge.isRuleAST()
        || edge.isAST(S.TwoWayRule, 3);
  }

  private static int edgeIndex(GraphView view, IExpr edge) {
    Integer u = view.index.get(edge.first());
    Integer v = view.index.get(edge.second());
    if (u == null || v == null) {
      return -1;
    }
    boolean undirectedQuery = edge.isAST(S.UndirectedEdge, 3) || edge.isAST(S.TwoWayRule, 3);
    for (int e = 0; e < view.m; e++) {
      if (view.undirected[e]) {
        if ((view.source[e] == u && view.target[e] == v)
            || (view.source[e] == v && view.target[e] == u)) {
          return e;
        }
      } else if (!undirectedQuery && view.source[e] == u && view.target[e] == v) {
        return e;
      }
    }
    return -1;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
