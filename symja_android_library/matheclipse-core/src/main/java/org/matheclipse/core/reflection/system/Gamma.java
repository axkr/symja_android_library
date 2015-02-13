package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Returns the Gamma function value.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Gamma_function">Gamma functio</a>
 * 
 */
public class Gamma extends AbstractTrigArg1 {

	public Gamma() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 4);
		
		if (ast.size() == 2) {
			return evaluateArg1(ast.arg1());
		}
		return null;
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		if (arg1 > 0.0) {
			double gamma;
			gamma = org.apache.commons.math3.special.Gamma.logGamma(arg1);
			gamma = Math.exp(gamma);
			return F.num(gamma);
		}
		return null;
	}

	public static BigInteger gamma(final BigInteger biggi) {
		return Factorial.factorial(biggi.subtract(BigInteger.ONE));
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			BigInteger fac = gamma(((IInteger) arg1).getBigNumerator());
			return F.integer(fac);
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
