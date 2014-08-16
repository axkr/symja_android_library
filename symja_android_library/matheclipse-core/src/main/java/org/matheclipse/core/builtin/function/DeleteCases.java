package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.VisitorLevelSpecification;

import com.google.common.base.Function;

/**
 * TODO see Cases
 * 
 */
public class DeleteCases extends AbstractCoreFunctionEvaluator {

	public DeleteCases() {
	}

	private static class CasesPatternMatcherFunctor implements Function<IExpr, IExpr> {
		protected final PatternMatcher matcher;
		protected IAST resultCollection;

		public CasesPatternMatcherFunctor(final PatternMatcher matcher, IAST resultCollection) {
			this.matcher = matcher;
			this.resultCollection = resultCollection;
		}

		@Override
		public IExpr apply(final IExpr arg) {
			if (arg.isAST()) {
				IAST ast = (IAST) arg;
				IAST[] results = ast.split(matcher);
				resultCollection.addAll(results[1]);
				// ast.filter(resultCollection, matcher);
			}
			return null;
		}

	}

	private static class CasesRulesFunctor implements Function<IExpr, IExpr> {
		protected final Function<IExpr, IExpr> function;
		protected IAST resultCollection;

		public CasesRulesFunctor(final Function<IExpr, IExpr> function, IAST resultCollection) {
			this.function = function;
			this.resultCollection = resultCollection;
		}

		@Override
		public IExpr apply(final IExpr arg) {
			if (arg.isAST()) {
				IAST ast = (IAST) arg;
				IAST[] results = ast.split(function);
				resultCollection.addAll(results[1]);
			}
			return null;
		}

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
				return deleteCases((IAST) ast.arg1(), ast.arg2());
			}
		}
		return null;
	}

	public static IAST deleteCases(final IAST ast, final IExpr pattern) {
		if (pattern.isRuleAST()) {
			Function<IExpr, IExpr> function = Functors.rules((IAST) pattern);
			IAST[] results = ast.split(function);
			return results[1];
		}
		final PatternMatcher matcher = new PatternMatcher(pattern);
		IAST[] results = ast.split(matcher);
		return results[1];

	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
