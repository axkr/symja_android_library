package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Hold implements IFunctionEvaluator {

  public Hold() {
  }


  /* (non-Javadoc)
   * @see org.matheclipse.core.eval.interfaces.IFunctionEvaluator#evaluate(org.matheclipse.core.eval.EvalEngine, org.matheclipse.parser.interfaces.IAST)
   */
  @Override
public IExpr evaluate(final IAST functionList) {
    return null;
  }

  /* (non-Javadoc)
   * @see org.matheclipse.core.eval.interfaces.IFunctionEvaluator#numericEval(org.matheclipse.core.eval.EvalEngine, org.matheclipse.parser.interfaces.IAST)
   */
  @Override
public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
  }

  /* (non-Javadoc)
   * @see org.matheclipse.parser.interfaces.IEvaluator#setUp(org.matheclipse.parser.interfaces.ISymbol)
   */
  @Override
public void setUp(final ISymbol symbol) {
    symbol.setAttributes(ISymbol.HOLDALL);
  }

}
