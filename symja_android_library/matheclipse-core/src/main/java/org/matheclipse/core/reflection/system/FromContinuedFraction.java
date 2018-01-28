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
 * <pre>
 * FromContinuedFraction({n1, n2, ...})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * return the number which represents the continued fraction list <code>{n1, n2, ...}</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; FromContinuedFraction({2,3,4,5})
 * 157/68
 * 
 * &gt;&gt; ContinuedFraction(157/68)
 * {2,3,4,5}
 * </pre>
 */
public class FromContinuedFraction extends AbstractEvaluator {

	public FromContinuedFraction() {
	}

	/**
	 * Convert a list of numbers to a fraction. See <a href="http://en.wikipedia.org/wiki/Continued_fraction">Continued
	 * fraction</a>
	 * 
	 * @see org.matheclipse.core.reflection.system.ContinuedFraction
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		if (!ast.arg1().isList()) {
			throw new WrongNumberOfArguments(ast, 1, ast.argSize());
		}
		IAST list = (IAST) ast.arg1();
		if (list.size() < 2) {
			return F.NIL;
		}
		int size = list.argSize();
		IExpr result = list.get(size--);
		for (int i = size; i >= 1; i--) {
			result = F.Plus(list.get(i), F.Power(result, F.CN1));
		}
		return result;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
