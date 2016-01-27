package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * See:
 * <a href="http://en.wikipedia.org/wiki/Polynomial_long_division">Wikipedia:
 * Polynomial long division</a>
 * 
 * @see org.matheclipse.core.reflection.system.PolynomialRemainder
 * @see org.matheclipse.core.reflection.system.PolynomialQuotientRemainder
 */
public class PolynomialQuotient extends PolynomialQuotientRemainder {

	public PolynomialQuotient() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 4, 5);
		ISymbol variable = Validate.checkSymbolType(ast, 3);
		IExpr arg1 = F.evalExpandAll(ast.arg1());
		IExpr arg2 = F.evalExpandAll(ast.arg2());

		if (ast.size() == 5) {
			final Options options = new Options(ast.topHead(), ast, 4, engine);
			IExpr option = options.getOption("Modulus");
			if (option != null && option.isSignedNumber()) {
				IExpr[] result = quotientRemainderModInteger(arg1, arg2, variable, option);
				if (result == null) {
					return null;
				}
				return result[0];
			}
			return F.NIL;
		}
		IExpr[] result = quotientRemainder(arg1, arg2, variable);
		if (result == null) {
			return F.NIL;
		}
		return result[0];
	}

}