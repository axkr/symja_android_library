package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

import com.google.common.base.Function;

/**
 * TODO see Cases
 * 
 */
public class DeleteCases extends AbstractCoreFunctionEvaluator {

	public DeleteCases() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		final EvalEngine engine = EvalEngine.get();
		final IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			if (ast.size() == 4) {
				// TODO see Cases implementation
				Validate.checkSize(ast, 3);

			} else {
				return deleteCases((IAST) arg1, ast.arg2());
			}
		}
		return null;
	}

	public static IAST deleteCases(final IAST ast, final IExpr pattern) {
		if (pattern.isRuleAST()) {
			Function<IExpr, IExpr> function = Functors.rules((IAST) pattern);
			IAST[] results = ast.filter(function);
			return results[1];
		}
		final PatternMatcher matcher = new PatternMatcher(pattern);
		IAST[] results = ast.filter(matcher);
		return results[1];

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
