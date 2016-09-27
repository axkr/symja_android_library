package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * The Carmichael function.
 * </p>
 * <p>
 * See: <a href="https://en.wikipedia.org/wiki/Carmichael_function">Wikipedia -
 * Carmichael function</a>
 * </p>
 *
 */
public class CarmichaelLambda extends AbstractTrigArg1 {

	public CarmichaelLambda() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger() && !arg1.isNegative()) {
			try {
				return ((IInteger) arg1).charmichaelLambda();
			} catch (ArithmeticException ae) {

			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
