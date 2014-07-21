package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * 
 * See <a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Arithmetic mean</a>
 */
public class Mean extends AbstractTrigArg1 {
	public Mean() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isList()) {
			final IAST list = (IAST) arg1;
			return F.Times(list.apply(F.Plus), F.Power(F.integer(list.size() - 1),
					F.CN1));
		}
		return null;
	} 

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.NOATTRIBUTE);
	}
	
}
