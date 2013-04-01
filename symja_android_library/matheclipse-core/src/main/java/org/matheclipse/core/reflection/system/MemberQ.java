package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 */
public class MemberQ implements IFunctionEvaluator {

	public MemberQ() {
	}

	public IExpr evaluate(final IAST ast) {
		if ((ast.size() == 3) && (ast.get(1).isAST())) {
			final IAST arg1 = (IAST) ast.get(1);
			final IExpr arg2 = ast.get(2);
			return F.bool(isMember(arg1, arg2));
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

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
	}

}
