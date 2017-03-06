package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Conjugate;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Factorial2;
import static org.matheclipse.core.expression.F.Gamma;
import static org.matheclipse.core.expression.F.NIL;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.num;

import java.util.function.DoubleUnaryOperator;

import org.matheclipse.core.builtin.NumberTheoryDefinitions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
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
		return NumberTheoryDefinitions.factorial(x.subtract(C1));
	}

	public Gamma() {
	}

	@Override
	public double applyAsDouble(double operand) {
		return org.hipparchus.special.Gamma.gamma(operand);
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		double gamma = org.hipparchus.special.Gamma.gamma(arg1);
		return num(gamma);
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 4);

		if (ast.isAST1()) {
			return evaluateArg1(ast.arg1());
		}
		return NIL;
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		if (arg1.isInteger()) {
			return gamma((IInteger) arg1);
		}
		if (arg1.isFraction()) {
			IFraction frac = (IFraction) arg1;
			if (frac.getDenominator().equals(C2)) {
				IInteger n = frac.getNumerator();
				if (arg1.isNegative()) {
					n = n.negate();
					return Times(Power(CN1, Times(C1D2, Plus(C1, n))), Power(C2, n), Sqrt(Pi), Power(Factorial(n), -1),
							Factorial(Times(C1D2, Plus(CN1, n))));
				} else {
					// Sqrt(Pi) * (n-2)!! / 2^((n-1)/2)
					return Times(Sqrt(Pi), Factorial2(n.subtract(C2)), Power(C2, Times(C1D2, Subtract(C1, n))));
				}
			}
		}
		if (arg1.isAST()) {
			IAST z = (IAST) arg1;
			if (z.isAST(Conjugate, 2)) {
				// mirror symmetry for Conjugate()
				return Conjugate(Gamma(z.arg1()));
			}

		}
		return NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(newSymbol);
	}
}
