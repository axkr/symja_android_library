package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

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
				IntegerSym moebius = ((IntegerSym) arg1).moebiusMu();
				return moebius;
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
