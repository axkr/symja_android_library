package org.matheclipse.core.sympy.series;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class Order {
  public static IExpr order(IExpr expr) {
    return order(expr, F.NIL);
  }

  public static IExpr order(IExpr expr, IExpr args) {
    // TODO
    if (args.isNIL()) {

    }
    return F.O(expr);
  }

  public boolean contains(Order expr) {
    // TODO
    return false;
  }

  public IExpr getOrder() {
    // TODO
    return F.C0;
  }
}
