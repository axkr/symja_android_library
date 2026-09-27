package org.matheclipse.graphtheory.reflection;

import java.util.List;
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
 * <code>FindPath(g, s, t)</code> - a path from <code>s</code> to <code>t</code>;
 * <code>FindPath(g, s, t, kspec)</code> - one of length <code>k</code> at most, <code>{k}</code>
 * exactly or <code>{kmin, kmax}</code>; <code>FindPath(g, s, t, kspec, n)</code> - at most
 * <code>n</code> paths (<code>All</code>), the shorter ones first. The length of a path in a
 * weighted graph is its weight.
 */
public class FindPath extends AbstractFunctionEvaluator {

  /** The number of paths after which the search gives up. */
  private static final int MAX_PATHS = 1_000_000;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    GraphExpr<?> gex = GraphExpr.newInstance(ast.arg1());
    if (gex == null) {
      return F.NIL;
    }
    GraphView view = GraphView.of(gex.toData());
    Integer s = view.index.get(ast.arg2());
    Integer t = view.index.get(ast.arg3());
    if (s == null || t == null) {
      return F.NIL;
    }
    if (s.equals(t)) {
      return F.CEmptyList;
    }
    double minLength = 0.0;
    double maxLength = Double.POSITIVE_INFINITY;
    if (ast.argSize() >= 4) {
      IExpr kspec = ast.arg4();
      if (kspec.isList1()) {
        minLength = maxLength = kspec.first().evalfNaN();
      } else if (kspec.isList2()) {
        minLength = kspec.first().evalfNaN();
        maxLength =
            kspec.second().isInfinity() ? Double.POSITIVE_INFINITY : kspec.second().evalfNaN();
      } else if (!kspec.isInfinity()) {
        maxLength = kspec.evalfNaN();
      }
      if (Double.isNaN(minLength) || Double.isNaN(maxLength)) {
        return F.NIL;
      }
    }
    int count = 1;
    if (ast.argSize() == 5) {
      IExpr arg5 = ast.arg5();
      count = (arg5 == S.All || arg5.isInfinity()) ? Integer.MAX_VALUE : arg5.toIntDefault();
      if (count <= 0) {
        return F.NIL;
      }
    }
    final boolean weighted = view.weighted && ast.argSize() >= 4;
    if (count == 1) {
      // the first path of the depth first search
      List<int[]> paths = GraphUtil.simplePaths(view, s, t, minLength, maxLength, weighted, 1);
      return pathList(view, paths, 1);
    }
    List<int[]> paths =
        GraphUtil.simplePaths(view, s, t, minLength, maxLength, weighted, MAX_PATHS);
    if (paths.size() >= MAX_PATHS) {
      return F.NIL;
    }
    // order fewer edges first, then descending in the vertex positions
    paths.sort((a, b) -> {
      if (a.length != b.length) {
        return Integer.compare(a.length, b.length);
      }
      for (int i = 0; i < a.length; i++) {
        if (a[i] != b[i]) {
          return Integer.compare(b[i], a[i]);
        }
      }
      return 0;
    });
    return pathList(view, paths, count);
  }

  private static IExpr pathList(GraphView view, List<int[]> paths, int count) {
    IASTAppendable result = F.ListAlloc(Math.min(paths.size(), count));
    for (int i = 0; i < paths.size() && i < count; i++) {
      int[] path = paths.get(i);
      result.append(F.mapRange(0, path.length, j -> view.vertex(path[j])));
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_5;
  }
}
