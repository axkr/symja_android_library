package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public interface IMatch {

  default IExpr match2(IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  default IExpr match3(IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  default IExpr match4(IAST ast, EvalEngine engine) {
    return F.NIL;
  }

  default IExpr match5(IAST ast, EvalEngine engine) {
    return F.NIL;
  }
}
