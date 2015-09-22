package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.visit.VisitorRemoveLevelSpecification;

import com.google.common.base.Function;

public class DeleteCases extends AbstractCoreFunctionEvaluator {

	private static class DeleteCasesPatternMatcherFunctor implements Function<IExpr, IExpr> {
		private final IPatternMatcher matcher;

		/**
		 * 
		 * @param matcher
		 *            the pattern-matcher
		 */
		public DeleteCasesPatternMatcherFunctor(final IPatternMatcher matcher) {
			this.matcher = matcher;
		}

		@Override
		public IExpr apply(final IExpr arg) {
			if (matcher.apply(arg)) {
				return F.Null;
			}
			return null;
		}

	}

	public DeleteCases() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 5);

		final IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			final IPatternMatcher matcher = engine.evalPatternMatcher(ast.arg2());
			if (ast.size() == 4 || ast.size() == 5) {
				final IExpr arg3 = engine.evaluate(ast.arg3());
				int maximumRemoveOperations = -1;
				if (ast.size() == 5) {
					maximumRemoveOperations = Validate.checkIntType(ast, 4);
				}
				IAST arg1RemoveClone = ((IAST) arg1).clone();

				try { 
					DeleteCasesPatternMatcherFunctor cpmf = new DeleteCasesPatternMatcherFunctor(matcher);
					VisitorRemoveLevelSpecification level = new VisitorRemoveLevelSpecification(cpmf, arg3, maximumRemoveOperations,
							false);
					arg1RemoveClone.accept(level);
					if (level.getRemovedCounter() == 0) {
						return arg1;
					}
					return arg1RemoveClone;
				} catch (VisitorRemoveLevelSpecification.StopException se) {
					// reached maximum number of results
				}
				return arg1RemoveClone;
			} else {
				return deleteCases((IAST) arg1, matcher);
			}
		}
		return null;
	}

	public static IAST deleteCases(final IAST ast, final IPatternMatcher matcher) {
//		final IPatternMatcher matcher = new PatternMatcher(pattern);
		IAST[] results = ast.filter(matcher);
		return results[1];

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
