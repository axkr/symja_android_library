package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
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
		if (n.isZero() && m.isZero()) {
			return F.C1;
		}
		if (n.isZero() && m.isPositive()) {
			return C0;
		}
		IInteger nSubtract1 = n.subtract(F.C1);
		if (n.isPositive() && m.isOne()) {
			return Times(Power(F.CN1, nSubtract1), F.Factorial(nSubtract1));
		}
		IInteger factorPlusMinus1;
		if (n.isPositive() && m.equals(F.C2)) {
			if (n.isOdd()) {
				factorPlusMinus1 = F.CN1;
			} else {
				factorPlusMinus1 = F.C1;
			}
			return Times(factorPlusMinus1, F.Factorial(nSubtract1), F.HarmonicNumber(nSubtract1));
		}

		IInteger nSubtractm = n.subtract(m);

		IInteger nTimes2Subtractm = n.add(n.subtract(m));
		IAST temp = F.Plus();

		try {
			int counter = nSubtractm.toInt() + 1;
			IInteger k;
			for (int i = 0; i < counter; i++) {
				k = F.integer(i);
				if ((i & 1) == 1) { // isOdd(i) ?
					factorPlusMinus1 = F.CN1;
				} else {
					factorPlusMinus1 = F.C1;
				}
				temp.append(Times(factorPlusMinus1, Binomial(Plus(k, nSubtract1), Plus(k, nSubtractm)),
						Binomial(nTimes2Subtractm, F.Subtract(nSubtractm, k)), F.StirlingS2(Plus(k, nSubtractm), k)));

			}
			return temp;
		} catch (ArithmeticException ae) {
			// because of toInt() method
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
