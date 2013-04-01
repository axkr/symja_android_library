package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Euler phi function 
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Euler%27s_totient_function">Euler's totient function</a>
 */
public class EulerPhi extends AbstractTrigArg1 {

	public EulerPhi() {
	}

	@Override
	public IExpr numericEvalD1(final Num arg1) {
		return null;
	}

	@Override
	public IExpr numericEvalDC1(final ComplexNum arg1) {
		return null;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			try {
				return ((IntegerSym) arg1).eulerPhi();
			} catch (ArithmeticException e) {
				// integer to large?
			}
		}
		return null;
	}

	@Override
  public void setUp(final ISymbol symbol) throws SyntaxError {
    symbol.setAttributes(ISymbol.LISTABLE);
    super.setUp(symbol);
  }
}
