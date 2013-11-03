package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Factorial;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.integer;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Stirling numbers of the second kind.
 * 
 * See <a href="http://en.wikipedia.org/wiki/Stirling_numbers_of_the_second_kind" >Wikipedia - Stirling numbers of the second
 * kind</a>
 */
public class StirlingS2 extends AbstractFunctionEvaluator {

	public StirlingS2() {
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		try {
			if (ast.arg1().isInteger() && ast.arg2().isInteger()) {
				if (ast.arg1().equals(ast.arg2())) {
					// {n,n}==1
					return C1;
				}
				IAST temp = F.Plus();
				int k = ((IInteger) ast.arg2()).toInt();
				if (k == 1) {
					// {n,1}==1
					return C1;
				}
				if (k == 2) {
					// {n,2}==2^(n-1)-1
					return Subtract(Power(C2, Subtract(ast.arg1(), C1)), C1);
				}
				for (int j = 0; j < k; j++) {
					if ((j & 1) == 1) {
						temp.add(Times(Times(CN1, Binomial(ast.arg2(), integer(j))),
								Power(Plus(ast.arg2(), integer(-j)), ast.arg1())));
					} else {
						temp.add(Times(Times(Binomial(ast.arg2(), integer(j))), Power(Plus(ast.arg2(), integer(-j)), ast.arg1())));
					}
				}
				return Times(Power(Factorial(ast.arg2()), CN1), temp);
			}
		} catch (ArithmeticException ae) {
			// because of toInt() method
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE);
		super.setUp(symbol);
	}
}
