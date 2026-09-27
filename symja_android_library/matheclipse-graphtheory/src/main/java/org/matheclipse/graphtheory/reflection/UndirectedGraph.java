package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphView;
import org.matheclipse.graphtheory.expression.data.GraphExpr;

/**
 * <code>UndirectedGraph(g)</code> - the graph with every directed edge replaced by an undirected
 * one. The edges <code>u-&gt;v</code> and <code>v-&gt;u</code> merge; their weights add up.
 */
public class UndirectedGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    if (!view.hasDirectedEdge()) {
      return gex;
    }
    // order: for each vertex its in-neighbours, then its out-neighbours, each pair
    // once
    Map<Long, Integer> position = new HashMap<Long, Integer>();
    List<int[]> pairs = new ArrayList<int[]>();
    List<List<Integer>> merged = new ArrayList<List<Integer>>();
    for (int v = 0; v < view.n; v++) {
      for (int[] edges : new int[][] {view.in[v], view.out[v]}) {
        for (int e : edges) {
          int w = view.other(e, v);
          long key = ((long) Math.min(v, w) << 32) | Math.max(v, w);
          Integer p = position.get(key);
          if (p == null) {
            position.put(key, pairs.size());
            pairs.add(new int[] {v, w, e});
            List<Integer> list = new ArrayList<Integer>();
            list.add(e);
            merged.add(list);
          } else if (!merged.get(p).contains(e)) {
            merged.get(p).add(e);
          }
        }
      }
    }
    return GraphTransforms.newUndirectedGraph(gex, view, pairs, merged);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
