package org.matheclipse.core.sympy.core;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class Function {
  public static IExpr expandMul(IExpr expr) {
    return expandMul(expr, true);
  }

  public static IExpr expandMul(IExpr expr, boolean deep) {

    // Wrapper around expand that only uses the mul hint. See the expand
    // docstring for more information.
    //
    // Examples
    // ========
    //
    // >>> from sympy import symbols, expand_mul, exp, log
    // >>> x, y = symbols('x,y', positive=True)
    // >>> expand_mul(exp(x+y)*(x+y)*log(x*y**2))
    // x*exp(x + y)*log(x*y**2) + y*exp(x + y)*log(x*y**2)
    if (deep) {
      Traversal.bottomUp(expr, x -> {
        if (!x.isTimes()) {
          return F.NIL;
        }
        return F.eval(F.Distribute(x));
      });
    }
    if (expr.isTimes()) {
      return F.eval(F.Distribute(expr));
    }
    return expr;
    // return sympify(expr).expand(deep=deep, mul=True, power_exp=False,
    // power_base=False, basic=False, multinomial=False, log=False)
  }
}
