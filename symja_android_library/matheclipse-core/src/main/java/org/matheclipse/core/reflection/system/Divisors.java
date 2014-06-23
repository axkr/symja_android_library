package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
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
			IntegerSym i = (IntegerSym) arg1;
			if (i.isNegative()) {
				i = (IntegerSym) i.negate();
			}
			return i.divisors();
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}

}
