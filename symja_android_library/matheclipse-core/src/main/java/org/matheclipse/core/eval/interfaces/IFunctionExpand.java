package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public interface IFunctionExpand {

  public IExpr functionExpand(final IAST ast, EvalEngine engine);

  /**
   * Call {@link #functionExpand(IAST, EvalEngine)}; if the return result is present return the
   * result; if not return the <code>originalAST</code>
   * 
   * @param originalAST
   * @param engine
   * @return
   */
  default IExpr functionExpandOriginal(final IAST originalAST, EvalEngine engine) {
    IExpr result = functionExpand(originalAST, engine);
    if (result.isPresent()) {
      return result;
    }
    return originalAST;
  }
}
