package org.matheclipse.core.sympy.series;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class Limitseq {
  public static IExpr limit_seq(IExpr expr, IExpr n) {
    return limit_seq(expr, n, 5);
  }

  public static IExpr limit_seq(IExpr expr, IExpr n, int trials) {
    EvalEngine engine = new EvalEngine();
    IExpr lim_val =
        engine.evaluateNIL(F.Limit(expr, F.Rule(n, F.oo)));
    return lim_val;
  }
}
