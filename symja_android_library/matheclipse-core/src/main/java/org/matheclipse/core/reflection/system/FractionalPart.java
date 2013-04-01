package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.INum;

import java.math.BigInteger;

/**
 * Get the fractional part auf a number
 */
public class FractionalPart extends AbstractFunctionEvaluator {

	public FractionalPart() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		
		IExpr expr = ast.get(1);
		if (expr.isSignedNumber()) {
			if (expr.isInteger()) {
				return F.C0;
			} else if (expr.isFraction()) {
				IFraction fr = (IFraction) expr;
				BigInteger num = fr.getBigNumerator();
				BigInteger den = fr.getBigDenominator();
				BigInteger div = num.divide(den);
				if (div.equals(BigInteger.ZERO)) {
					return F.C0;
				}
				return F.fraction(div, den);
			} else if (expr instanceof INum) {
				// TODO
				// INum num = (INum) expr;
				// num.getRealPart();

			}
		}
		return null;
	}
}