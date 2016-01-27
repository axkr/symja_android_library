package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.BernoulliB;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.fraction;
import static org.matheclipse.core.expression.F.integer;

import org.apache.commons.math4.fraction.BigFraction;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Harmonic number of a given integer value
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Harmonic_number">Harmonic number</a>
 */
public class HarmonicNumber extends AbstractEvaluator {

	public HarmonicNumber() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		if (ast.size() == 3) {
			IExpr arg2 = ast.arg2();
			if (arg2.isOne()) {
				return F.HarmonicNumber(arg1);
			} else {
				// generalized harmonic number
				if (arg2.isInteger()) {
					if (arg1.isInfinity()) {
						if (arg2.isPositive() && ((IInteger) arg2).isEven()) {
							// Module({v=s/2},((2*Pi)^(2*v)*(-1)^(v+1)*BernoulliB(2*v))/(2*(2*v)!))
							IExpr v = Times(C1D2, arg2);
							return Times(Power(Times(C2, Pi), Times(C2, v)), Power(CN1, Plus(v, C1)), BernoulliB(Times(C2, v)),
									Power(Times(C2, Factorial(Times(C2, v))), CN1));
						}
						return F.NIL;
					}
				}
				if (arg1.isInteger()) {
					int n = Validate.checkIntType(ast, 1, Integer.MIN_VALUE);
					if (n < 0) {
						return F.NIL;
					}
					if (n == 0) {
						return C0;
					}
					IAST result = Plus();
					for (int i = 1; i <= n; i++) {
						result.add(Power(integer(i), Negate(arg2)));
					}
					return result;
				}
				return F.NIL;
			}
		}
		if (arg1.isInteger()) {

			int n = Validate.checkIntType(ast, 1, Integer.MIN_VALUE);
			if (n < 0) {
				return F.NIL;
			}
			if (n == 0) {
				return C0;
			}
			if (n == 1) {
				return C1;
			}

			return fraction(harmonicNumber(n));
		}

		return F.NIL;
	}

	/**
	 * The Harmonic number at the index specified
	 * 
	 * @param n
	 *            the index, non-negative.
	 * @return the H_1=1 for n=1, H_2=3/2 for n=2 etc. For values of n less than 1, zero is returned.
	 */
	public BigFraction harmonicNumber(int n) {
		if (n < 1)
			return BigFraction.ZERO;
		else {
			/*
			 * start with 1 as the result
			 */
			BigFraction a = new BigFraction(1, 1);

			/*
			 * add 1/i for i=2..n
			 */
			for (int i = 2; i <= n; i++) {
				a = a.sum(new BigFraction(1, i));
			}
			return a;
		}
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
