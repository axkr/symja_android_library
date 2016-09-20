package org.matheclipse.core.reflection.system;

import java.util.function.DoubleUnaryOperator;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Returns the Gamma function value.
 * </p>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Gamma_function">Gamma function</a>
 * and <a href=
 * "https://en.wikipedia.org/wiki/Particular_values_of_the_Gamma_function">
 * Particular values of the Gamma function</a>
 * 
 */
public class Gamma extends AbstractTrigArg1 implements DoubleUnaryOperator {

	/**
	 * Implement: <code>Gamma(x_Integer) := (x-1)!</code>
	 * 
	 * @param x
	 * @return
	 */
	public static IInteger gamma(final IInteger x) {
		return Factorial.factorial(x.subtract(F.C1));
	}

	public Gamma() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return org.apache.commons.math4.special.Gamma.gamma(operand);
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		double gamma = org.apache.commons.math4.special.Gamma.gamma(arg1);
		return F.num(gamma);
	}
	
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 4);

		if (ast.isAST1()) {
			return evaluateArg1(ast.arg1());
		}
		return F.NIL;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			return gamma((IInteger) arg1);
		}
		if (arg1.isFraction() && arg1.isPositive()) {
			IFraction frac = (IFraction) arg1;
			if (frac.getDenominator().equals(F.C2)) {
				IInteger n = frac.getNumerator();
				// Sqrt(Pi) * (n-2)!! / 2^((n-1)/2)
				return F.Times(F.Sqrt(F.Pi), F.Factorial2(n.subtract(F.C2)),
						F.Power(F.C2, F.Times(F.C1D2, F.Subtract(F.C1, n))));

			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
