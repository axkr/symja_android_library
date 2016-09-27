package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Euler phi function 
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Euler%27s_totient_function">Euler's totient function</a>
 */
public class EulerPhi extends AbstractTrigArg1 {

	public EulerPhi() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			try {
				return ((IInteger) arg1).eulerPhi();
			} catch (ArithmeticException e) {
				// integer to large?
			}
		}
		return F.NIL;
	}

	@Override
  public void setUp(final ISymbol newSymbol)  {
    newSymbol.setAttributes(ISymbol.LISTABLE);
  }
}
