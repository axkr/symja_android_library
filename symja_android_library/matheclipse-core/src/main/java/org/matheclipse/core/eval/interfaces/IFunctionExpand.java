package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public interface IFunctionExpand {

  default IExpr functionExpand(final IAST ast, EvalEngine engine) {
    return F.NIL;
  }

}
