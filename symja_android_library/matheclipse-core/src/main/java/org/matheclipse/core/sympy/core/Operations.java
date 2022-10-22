package org.matheclipse.core.sympy.core;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Operations {
  /**
   * <p>
   * Return a sequence of elements `args` such that symbol(*args) == expr
   * 
   * <p>
   * Examples
   * 
   * <pre>
   * >> make_args(S.Times, F.Times(x,y)) 
   * List(x, y) 
   * 
   * >> make_args(S.Plus, F.Times(x,y))
   * List(F.Times(x,y))
   * </pre>
   * 
   * @param symbol
   * @param expr
   * @return
   */
  public static IASTMutable makeArgs(ISymbol symbol, IExpr expr) {
    if (expr.isAST(symbol)) {
      return ((IAST) expr).setAtCopy(0, S.List);
    }
    return F.unaryAST1(S.List, expr);
  }
}
