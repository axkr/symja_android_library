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
 * <code>TuranGraph(n, k)</code> - the Turan graph <code>T(n,k)</code>, the complete
 * <code>k</code>-partite graph on <code>n</code> vertices whose parts differ in size by at most 1;
 * the larger parts come first.
 */
public class TuranGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int n = ast.arg1().toIntDefault();
    int k = ast.arg2().toIntDefault();
    if (n <= 0 || k <= 0) {
      return F.NIL;
    }
    // part[v] the part of vertex v
    int[] part = new int[n];
    int v = 0;
    for (int p = 0; p < k && v < n; p++) {
      int size = n / k + (p < n % k ? 1 : 0);
      for (int i = 0; i < size; i++) {
        part[v++] = p;
      }
    }
    GraphUtil.checkGraphSize(n, (long) n * (n - 1) / 2);
    List<int[]> pairs = new ArrayList<int[]>();
    for (int a = 0; a < n; a++) {
      for (int b = a + 1; b < n; b++) {
        if (part[a] != part[b]) {
          pairs.add(new int[] {a + 1, b + 1});
        }
      }
    }
    return GraphUtil.sortedPairGraph(n, pairs);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
