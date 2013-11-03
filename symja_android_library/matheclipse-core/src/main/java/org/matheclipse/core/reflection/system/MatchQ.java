package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Match an expression against a given pattern.
 * 
 */
public class MatchQ implements IFunctionEvaluator {

	public MatchQ() {
	}

	public IExpr evaluate(final IAST ast) {
		if ((ast.size() == 3)) {
			return F.bool(matchQ(ast.arg1(), ast.arg2()));
		}
		return F.False;
	}

	public static boolean matchQ(final IExpr evalExpr, final IExpr pattern) {
		final IPatternMatcher matcher = new PatternMatcher(pattern);
		return matcher.apply(evalExpr);
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDREST);
	}

}
