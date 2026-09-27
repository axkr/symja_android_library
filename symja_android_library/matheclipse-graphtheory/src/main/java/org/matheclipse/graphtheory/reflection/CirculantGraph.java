package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;

/**
 * <code>CirculantGraph(n, j)</code> - the circulant graph on <code>n</code> vertices in which each
 * vertex <code>i</code> is joined to <code>i+j</code> and <code>i-j (mod n)</code>;
 * <code>CirculantGraph(n, {j1, j2, ...})</code> joins it to <code>i+-j1, i+-j2, ...</code>.
 */
public class CirculantGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int n = ast.arg1().toIntDefault();
    if (n <= 0) {
      return F.NIL;
    }
    IAST jumps = ast.arg2().isList() ? (IAST) ast.arg2() : F.list(ast.arg2());
    List<int[]> pairs = new ArrayList<int[]>();
    for (IExpr jump : jumps) {
      int j = jump.toIntDefault();
      if (j < 0 || j == Integer.MIN_VALUE) {
        return F.NIL;
      }
      for (int i = 1; i <= n; i++) {
        pairs.add(new int[] {i, (i - 1 + j) % n + 1});
      }
    }
    GraphUtil.checkGraphSize(n, pairs.size());
    return GraphUtil.sortedPairGraph(n, pairs);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
