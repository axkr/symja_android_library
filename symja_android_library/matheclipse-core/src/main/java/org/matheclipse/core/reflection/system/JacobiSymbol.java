package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * See <a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi
 * symbol</a>
 */
public class JacobiSymbol extends AbstractArg2 {

	public JacobiSymbol() {
	}

	@Override
	public IExpr e2IntArg(final IInteger i0, final IInteger i1) {
		try {
			IntegerSym jacobi = ((IntegerSym) i0).jacobiSymbol((IntegerSym) i1);
			return jacobi;
		} catch (ArithmeticException e) {
			// integer to large?
		}
		return null;
	}

}
