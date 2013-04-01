package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Polynomial_long_division">Wikipedia:Polynomial long division</a>
 * 
 * @see org.matheclipse.core.reflection.system.PolynomialRemainder
 * @see org.matheclipse.core.reflection.system.PolynomialQuotientRemainder
 */
public class PolynomialQuotient extends PolynomialQuotientRemainder {

	public PolynomialQuotient() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		IExpr[] result = quotientRemainder(ast);
		if (result == null) {
			return null;
		}
		return result[0];
	}

}