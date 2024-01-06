package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.core.Expr;

public interface ITrigonometricFunction {
  default IExpr _period(IAST self, IExpr general_period) {
    return _period(self, general_period, null);
  }

  default IExpr _period(IAST self, IExpr general_period, ISymbol symbol) {
    IExpr f = F.evalExpand(self.first());
    if (symbol == null) {
      // symbol = tuple(f.free_symbols)[0]
      return F.NIL;
    }
    if (f.isFree(symbol)) {
      return F.C0;
    }

    if (f == symbol) {
      return general_period;
    }

    VariablesSet vars = new VariablesSet(f);
    if (vars.contains(symbol)) {

      if (f.isTimes()) {
        Pair pf = Expr.asIndependent(f, F.List(symbol));
        IExpr g = pf.first();
        IExpr h = pf.second();
        if (h == symbol) {
          return F.eval(F.Divide(general_period, F.Abs(g)));
        }
      } else if (f.isPlus()) {
        Pair pf = Expr.asIndependent(f, F.List(symbol));
        IExpr a = pf.first();
        IExpr h = pf.second();
        Pair ph = Expr.asIndependent(h, F.List(symbol));
        IExpr g = ph.first();
        h = ph.second();
        if (h == symbol) {
          return F.eval(F.Divide(general_period, F.Abs(g)));
        }
      }
    }
    throw new UnsupportedOperationException("Use the periodicity function instead.");
  }

  default IExpr period(IAST self, IExpr general_period, ISymbol symbol) {
    return F.C0;
  }

}
