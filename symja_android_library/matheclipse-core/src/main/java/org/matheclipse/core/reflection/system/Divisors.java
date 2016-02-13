package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Return the divisors of an integer number.
 * 
 * <pre>
 * divisors(24) ==> {1,2,3,4,6,8,12,24}
 * </pre>
 */
public class Divisors extends AbstractTrigArg1 {

	public Divisors() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			IInteger i = (IInteger) arg1;
			if (i.isNegative()) {
				i = i.negate();
			}
			return i.divisors();
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}

}
