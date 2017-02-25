package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.integer;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Stirling numbers of the second kind.
 * 
 * See
 * <a href="http://en.wikipedia.org/wiki/Stirling_numbers_of_the_second_kind" >
 * Wikipedia - Stirling numbers of the second kind</a>
 */
public class StirlingS2 extends AbstractFunctionEvaluator {

	public StirlingS2() {
		// default ctor
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr nArg1 = ast.arg1();
		IExpr kArg2 = ast.arg2();
		if (nArg1.isNegative() || kArg2.isNegative()) {
			return F.NIL;
		}
		if (nArg1.isZero() && kArg2.isZero()) {
			return F.C1;
		}
		if (nArg1.isInteger() && kArg2.isInteger()) {
			if (kArg2.greaterThan(nArg1).isTrue()) {
				return C0;
			}
			if (kArg2.isZero()) {
				return C0;
			}
			if (kArg2.isOne()) {
				// {n,1}==1
				return C1;
			}
			if (kArg2.equals(C2)) {
				// {n,2}==2^(n-1)-1
				return Subtract(Power(C2, Subtract(nArg1, C1)), C1);
			}

			try {
				int k = ((ISignedNumber) kArg2).toInt();
				return stirlingS2((IInteger) nArg1, (IInteger) kArg2, k);
			} catch (ArithmeticException ae) {
				// because of toInt() method
			}
		}

		return F.NIL;
	}

	private static IExpr stirlingS2(IInteger nArg1, IInteger kArg2, int k) {
		IAST temp = F.Plus();
		for (int i = 0; i < k; i++) {
			if ((i & 1) == 1) { // isOdd(i) ?
				temp.append(Times(Negate(Binomial(kArg2, integer(i))), Power(Plus(kArg2, integer(-i)), nArg1)));
			} else {
				temp.append(Times(Times(Binomial(kArg2, integer(i))), Power(Plus(kArg2, integer(-i)), nArg1)));
			}
		}
		return Times(Power(Factorial(kArg2), CN1), temp);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
