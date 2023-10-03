package org.matheclipse.core.sympy.series;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.exception.ValueError;

public class Gruntz {
  /**
   * Returns &quot;&lt;&quot; if a&lt;b, &quot;=&quot; for a == b, &quot;&gt;&quot; for a&gt;b
   * 
   * @param a
   * @param b
   * @param x
   * @return
   */
  public static char compare(IExpr a, IExpr b, IExpr x) {
    // log(exp(...)) must always be simplified here for termination
    IExpr la = F.eval(F.Log(a));
    IExpr lb = F.eval(F.Log(b));
    // if isinstance(a, Basic) and (isinstance(a, exp) or (a.is_Pow and a.base == S.Exp1)):
    // la = a.exp
    // if isinstance(b, Basic) and (isinstance(b, exp) or (b.is_Pow and b.base == S.Exp1)):
    // lb = b.exp
    if (a.isPower() && a.base().equals(S.E)) {
      la = a.exponent();
    }
    if (b.isPower() && b.base().equals(S.E)) {
      lb = b.exponent();
    }
    IExpr div = F.eval(F.Divide(la, lb));
    IExpr c = limitinf(div, x);
    if (c.isZero()) {
      return '<';
    } else if (c.isInfinity()) {
      return '>';
    } else {
      return '=';
    }
  }

  /**
   * Limit e(x) for x-> oo.
   * 
   * @param e
   * @param x
   * @return
   */
  public static IExpr limitinf(IExpr e, IExpr x) {
    // rewrite e in terms of tractable functions only
    IExpr old = e;
    if (!e.has(x)) {
      return e; // e is a constant
    }
    EvalEngine engine = EvalEngine.get();
    IAssumptions oldAssumptions = engine.getAssumptions();
    try {
      // if e.has(Order):
      // e = e.expand().removeO()
      if (!x.isPositive() || x.isInteger()) {
        // We make sure that x.is_positive is True and x.is_integer is None
        // so we get all the correct mathematical behavior from the expression.
        // We need a fresh variable.
        ISymbol p = F.Dummy('p');
        IAssumptions.assign(p, F.Greater(p, F.C0), oldAssumptions, engine);
        e = F.subs(e, x, p);
        x = p;
      }
      // e = e.rewrite('tractable', deep=True, limitvar=x)
      // e = powdenest(e);
    } finally {
      engine.setAssumptions(oldAssumptions);
    }
    throw new ValueError("not implemented");
  }
}
