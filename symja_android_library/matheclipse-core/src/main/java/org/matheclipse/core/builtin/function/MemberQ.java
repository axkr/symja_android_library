package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 */
public class MemberQ extends AbstractCoreFunctionEvaluator {

	public MemberQ() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		final IExpr arg1 = F.eval(ast.get(1));
		final IExpr arg2 = F.eval(ast.get(2));
		if (arg1.isAST()) {
			return F.bool(isMember((IAST) arg1, arg2));
		}
		return F.False;
	}

	/**
	 * Check if the given expression is a member of the given AST.
	 * 
	 * @param ast
	 * @param expr
	 * @return
	 */
	public static boolean isMember(final IAST ast, final IExpr expr) {
		final PatternMatcher matcher = new PatternMatcher(expr);
		for (int i = 1; i < ast.size(); i++) {
			if (matcher.apply(ast.get(i))) {
				return true;
			}
		}
		return false;
	}

}
