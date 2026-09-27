package org.matheclipse.graphtheory.reflection;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.graphtheory.eval.GraphUtil;

/**
 * <code>HararyGraph(k, n)</code> - the Harary graph <code>H(k,n)</code>, the <code>k</code>-connected
 * graph on <code>n</code> vertices with the fewest edges.
 */
public class HararyGraph extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int k = ast.arg1().toIntDefault();
    if (k < 2) {
      // Integer greater than `1` expected at position `2` in `3`.
      return Errors.printMessage(ast.topHead(), "intg", F.list(F.C1, F.C1, ast), engine);
    }
    int n = ast.arg2().toIntDefault();
    if (n <= k) {
      return F.NIL;
    }
    List<int[]> pairs = new ArrayList<int[]>();
    // i joined to i+1, ..., i+floor(k/2) (mod n)
    for (int j = 1; j <= k / 2; j++) {
      for (int i = 0; i < n; i++) {
        pairs.add(new int[] {i + 1, (i + j) % n + 1});
      }
    }
    if (k % 2 == 1) {
      if (n % 2 == 0) {
        // and to the opposite vertex
        for (int i = 0; i < n / 2; i++) {
          pairs.add(new int[] {i + 1, i + n / 2 + 1});
        }
      } else {
        for (int i = 0; i <= (n - 1) / 2; i++) {
          pairs.add(new int[] {i + 1, (i + (n + 1) / 2) % n + 1});
        }
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
