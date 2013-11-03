package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class FreeQ extends AbstractCoreFunctionEvaluator {

	public FreeQ() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		IExpr arg1 = F.eval(ast.arg1());
		IExpr arg2 = F.eval(ast.arg2());
		final IPatternMatcher matcher = new PatternMatcher(arg2);
		if (matcher.isRuleWithoutPatterns()) {
			if (arg1.isOrderlessAST() && arg2.isOrderlessAST() && arg1.head().equals(arg2.head())) {
				IAST arg1AST = (IAST) arg1;
				IAST arg2AST = (IAST) arg2;
				if (containsOrderless(arg1AST, arg2AST)) {
					return F.False;
				}
			}
		}
		return F.bool(arg1.isFree(matcher, true));
	}

	/**
	 * Checks if <code>orderless1.size()</code> is greaterequal <code>orderless2.size()</code> and returns <code>true</code>, if
	 * every argument in <code>orderless2</code> equals an argument in <code>orderless1</code>. I.e. <code>orderless1</code>
	 * contains every argument of <code>orderless2</code>.
	 * 
	 * @param orderless1
	 * @param orderless2
	 * @return <code>true</code> if <code>orderless1.size()</code> is greaterequal <code>orderless2.size()</code> and if every
	 *         argument in <code>orderless2</code> equals an argument in <code>orderless1</code>
	 */
	public boolean containsOrderless(IAST orderless1, IAST orderless2) {
		IExpr temp;
		boolean evaled = false;
		if (orderless1.size() >= orderless2.size()) {
			int[] array = new int[orderless1.size()];
			for (int i = 1; i < orderless2.size(); i++) {
				temp = orderless2.get(i);
				evaled = false;
				for (int j = 1; j < orderless1.size(); j++) {
					if (array[j] != (-1) && temp.equals(orderless1.get(j))) {
						array[j] = -1;
						evaled = true;
						break;
					}
				}
				if (!evaled) {
					break;
				}
			}
			if (evaled) {
				return true;
			}
		}
		return false;
	}
}
