package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The Möbius function.
 * 
 * See <a
 * href="http://en.wikipedia.org/wiki/M%C3%B6bius_function">Wikipedia:Möbius
 * function</a>
 */
public class MoebiusMu extends AbstractTrigArg1 {

	public MoebiusMu() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			try {
				return ((IInteger)arg1).moebiusMu(); 
			} catch (ArithmeticException e) {
				// integer to large?
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
