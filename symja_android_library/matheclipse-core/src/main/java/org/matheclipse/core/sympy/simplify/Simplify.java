package org.matheclipse.core.sympy.simplify;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class Simplify {
  public static IExpr logCombine(IExpr expr) {
    return logCombine(expr, false);
  }

  public static IExpr logCombine(IExpr expr, boolean force) {
    if (expr.isPlus()) {
      return expr;
    }
    if (expr.isTimes()) {
      IAST timesAST = (IAST) expr;
      int indexOf = timesAST.indexOf(IExpr::isLog);
      if (indexOf < 0) {
        return expr;
      }
      if (timesAST.get(indexOf).first().isPositiveResult()) {
        IASTMutable timesReduced = timesAST.removeAtCopy(indexOf);
        for (int i = 1; i < timesReduced.size(); i++) {
          IExpr arg = timesReduced.get(i);
          if (arg.isRealResult()) {

          }
        }
      }
    }
    return expr;
  }
}
