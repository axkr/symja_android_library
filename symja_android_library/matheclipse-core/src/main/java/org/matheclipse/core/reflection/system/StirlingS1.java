package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Stirling numbers of the first kind.
 * 
 * See
 * <a href="https://en.wikipedia.org/wiki/Stirling_numbers_of_the_first_kind" >
 * Wikipedia - Stirling numbers of the first kind</a>
 */
public class StirlingS1 extends AbstractFunctionEvaluator {

	public StirlingS1() {
		// default ctor
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr nArg1 = ast.arg1();
		IExpr mArg2 = ast.arg2();
		if (nArg1.isNegative() || mArg2.isNegative()) {
			return F.NIL;
		}
		if (nArg1.isInteger() && mArg2.isInteger()) {
			return stirlingS1((IInteger) nArg1, (IInteger) mArg2);
		}

		return F.NIL;
	}

	private static IExpr stirlingS1(IInteger n, IInteger m) {
		if (n.isZero() && m.isPositive()) {
			return C0;
		}
		if (n.isPositive() && m.isOne()) {
			return Times(Power(F.CN1, Plus(F.CN1, n)), F.Factorial(Plus(F.CN1, n)));
		}
		if (n.isPositive() && m.equals(F.C2)) {
			return Times(Power(F.CN1, n), F.Factorial(Plus(F.CN1, n)), F.HarmonicNumber(Plus(F.CN1, n)));
		}
		
		IInteger nSubtractm = n.subtract(m);
		ISymbol k = new Symbol("§k");
		return Sum(
				Times(Power(F.CN1, k), Binomial(Plus(k, n, F.CN1), Plus(k, nSubtractm)),
						Binomial(Plus(n, nSubtractm), F.Subtract(nSubtractm, k)), F.StirlingS2(Plus(k, nSubtractm), k)),
				F.List(k, C0, nSubtractm));
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
