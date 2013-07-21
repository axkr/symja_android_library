package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.VisitorLevelSpecification;

import com.google.common.base.Function;

public class Cases implements ICoreFunctionEvaluator {
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
				ast.filter(resultCollection, matcher);
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
				resultCollection.addAll(results[0]);
			}
			return null;
		}

	}

	public Cases() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);
		final EvalEngine engine = EvalEngine.get();
		final IExpr arg1 = engine.evaluate(ast.get(1));
		if (arg1.isAST()) {
			if (ast.size() == 4) {

				IAST result = F.List();
				if (ast.get(2).isRuleAST()) {
					Function<IExpr, IExpr> function = Functors.rules((IAST) ast.get(2));
					CasesRulesFunctor crf = new CasesRulesFunctor(function, result);
					VisitorLevelSpecification level = new VisitorLevelSpecification(crf, ast.get(3), false);
					ast.get(1).accept(level);
					return result;
				}

				final PatternMatcher matcher = new PatternMatcher(ast.get(2));
				CasesPatternMatcherFunctor cpmf = new CasesPatternMatcherFunctor(matcher, result);
				VisitorLevelSpecification level = new VisitorLevelSpecification(cpmf, ast.get(3), false);
				ast.get(1).accept(level);
				return result;
			} else {
				return cases((IAST) ast.get(1), ast.get(2));
			}
		}
		return null;
	}

	public static IAST cases(final IAST ast, final IExpr pattern) {
		if (pattern.isRuleAST()) {
			Function<IExpr, IExpr> function = Functors.rules((IAST) pattern);
			IAST[] results = ast.split(function);
			return results[0];
		}
		final PatternMatcher matcher = new PatternMatcher(pattern);
		return ast.filter(ast.copyHead(), matcher);
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
