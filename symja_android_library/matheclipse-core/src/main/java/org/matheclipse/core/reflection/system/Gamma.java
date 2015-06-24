package org.matheclipse.core.reflection.system;

import java.math.BigInteger;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * <p>
 * Returns the Gamma function value.
 * </p>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Gamma_function">Gamma function</a> and <a
 * href="https://en.wikipedia.org/wiki/Particular_values_of_the_Gamma_function">Particular values of the Gamma function</a>
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
		double gamma = org.apache.commons.math3.special.Gamma.gamma(arg1);
		return F.num(gamma);
	}

	/**
	 * Implement: <code>Gamma(x_Integer) := (x-1)!</code>
	 * 
	 * @param x
	 * @return
	 */
	public static BigInteger gamma(final BigInteger x) {
		return Factorial.factorial(x.subtract(BigInteger.ONE));
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			BigInteger fac = gamma(((IInteger) arg1).getBigNumerator());
			return F.integer(fac);
		}
		if (arg1.isFraction() && arg1.isPositive()) {
			IFraction frac = (IFraction) arg1;
			if (frac.getDenominator().equals(F.C2)) {
				IInteger n = frac.getNumerator();
				return F.Times(F.Sqrt(F.Pi), F.Factorial2(n.subtract(F.C2)), F.Power(F.C2, F.Times(F.C1D2, F.Subtract(F.C1, n))));

			}
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
