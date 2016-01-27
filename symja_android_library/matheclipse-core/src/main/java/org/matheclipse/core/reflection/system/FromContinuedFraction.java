package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Convert a list of numbers to a fraction. See <a href="http://en.wikipedia.org/wiki/Continued_fraction">Continued fraction</a>
 * 
 * @see org.matheclipse.core.reflection.system.ContinuedFraction
 */
public class FromContinuedFraction extends AbstractEvaluator {

	public FromContinuedFraction() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		if (!ast.arg1().isList()) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		IAST list = (IAST) ast.arg1();
		if (list.size() < 2) {
			return F.NIL;
		}
		int size = list.size() - 1;
		IExpr result = list.get(size--);
		for (int i = size; i >= 1; i--) {
			result = F.Plus(list.get(i), F.Power(result, F.CN1));
		}
		return result;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
