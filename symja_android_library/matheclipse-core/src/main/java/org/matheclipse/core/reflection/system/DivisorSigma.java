package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>DivisorSigma(k,n)</code> - the sum of the <code>k</code>-th powers of the divisors of <code>n</code>.
 *
 */
public class DivisorSigma extends AbstractFunctionEvaluator {

	public DivisorSigma() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr arg1 = ast.arg1();
		IExpr arg2 = ast.arg2();
		if (arg2.isInteger() && arg2.isPositive()) {

			IInteger n = (IInteger) arg2;
			IAST list = n.divisors();
			if (list.isList()) {
				if (arg1.isInteger()) {
					// special formula if k is integer
					IInteger k = (IInteger) arg1;
					try {
						long kl = k.toLong();

						IInteger sum = F.C0;
						for (int i = 1; i < list.size(); i++) {
							sum = sum.add(((IInteger) list.get(i)).pow(kl));
						}
						return sum;
					} catch (ArithmeticException ae) {
						//
					}
				}
				// general formula
				IAST sum = F.PlusAlloc(list.size());
				for (int i = 1; i < list.size(); i++) {
					sum.append(F.Power(list.get(i), arg1));
				}
				return sum;
			}
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}
}
