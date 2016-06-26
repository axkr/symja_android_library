package org.matheclipse.core.builtin.function;

import java.util.function.Function;

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
import org.matheclipse.parser.client.math.MathException;
/** 
 * <p>
 * See the online Symja function reference: <a href="https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Cases">Cases</a>
 * </p>
 */
public class Cases extends AbstractCoreFunctionEvaluator {
	/**
	 * StopException will be thrown, if maximum number of Cases results are reached
	 *
	 */
	@SuppressWarnings("serial")
	private static class StopException extends MathException {
		public StopException() {
			super("Stop Cases() evaluation");
		}
	}

	private static class CasesPatternMatcherFunctor implements Function<IExpr, IExpr> {
		protected final PatternMatcher matcher;
		protected IAST resultCollection;
		final int maximumResults;
		private int resultsCounter;

		/**
		 * 
		 * @param matcher
		 *            the pattern-matcher
		 * @param resultCollection
		 * @param maximumResults
		 *            maximum number of results. -1 for for no limitation
		 */
		public CasesPatternMatcherFunctor(final PatternMatcher matcher, IAST resultCollection, int maximumResults) {
			this.matcher = matcher;
			this.resultCollection = resultCollection;
			this.maximumResults = maximumResults;
			this.resultsCounter = 0;
		}

		@Override
		public IExpr apply(final IExpr arg) throws StopException {
			if (matcher.test(arg)) {
				resultCollection.add(arg);
				if (maximumResults >= 0) {
					resultsCounter++;
					if (resultsCounter >= maximumResults) {
						throw new StopException();
					}
				}
			}
			return F.NIL;
		}

	}

	private static class CasesRulesFunctor implements Function<IExpr, IExpr> {
		protected final Function<IExpr, IExpr> function;
		protected IAST resultCollection;
		final int maximumResults;
		private int resultsCounter;

		/**
		 * 
		 * @param function
		 *            the funtion which should determine the results
		 * @param resultCollection
		 * @param maximumResults
		 *            maximum number of results. -1 for for no limitation
		 */
		public CasesRulesFunctor(final Function<IExpr, IExpr> function, IAST resultCollection, int maximumResults) {
			this.function = function;
			this.resultCollection = resultCollection;
			this.maximumResults = maximumResults;
		}

		@Override
		public IExpr apply(final IExpr arg) throws StopException {
			IExpr temp = function.apply(arg);
			if (temp.isPresent()) {
				resultCollection.add(temp);
				if (maximumResults >= 0) {
					resultsCounter++;
					if (resultsCounter >= maximumResults) {
						throw new StopException();
					}
				}
			}
			return F.NIL;
		}

	}

	public Cases() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 5);
		
		final IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isAST()) {
			final IExpr arg2 = engine.evalPattern(ast.arg2());
			if (ast.isAST3() || ast.size() == 5) {
				final IExpr arg3 = engine.evaluate(ast.arg3());
				int maximumResults = -1;
				if (ast.size() == 5) {
					maximumResults = Validate.checkIntType(ast, 4);
				}
				IAST result = F.List();
				if (arg2.isRuleAST()) {
					try {
						Function<IExpr, IExpr> function = Functors.rules((IAST) arg2);
						CasesRulesFunctor crf = new CasesRulesFunctor(function, result, maximumResults);
						VisitorLevelSpecification level = new VisitorLevelSpecification(crf, arg3 , false);
						arg1.accept(level);

					} catch (StopException se) {
						// reached maximum number of results
					}
					return result;
				}

				try {
					final PatternMatcher matcher = new PatternMatcher(arg2);
					CasesPatternMatcherFunctor cpmf = new CasesPatternMatcherFunctor(matcher, result, maximumResults);
					VisitorLevelSpecification level = new VisitorLevelSpecification(cpmf, arg3, false);
					arg1.accept(level);
				} catch (StopException se) {
					// reached maximum number of results
				}
				return result;
			} else {
				return cases((IAST) arg1, arg2);
			}
		}
		return F.NIL;
	}

	public static IAST cases(final IAST ast, final IExpr pattern) {
		if (pattern.isRuleAST()) {
			Function<IExpr, IExpr> function = Functors.rules((IAST) pattern);
			IAST[] results = ast.filter(function);
			return results[0];
		}
		final PatternMatcher matcher = new PatternMatcher(pattern);
		return ast.filter(ast.copyHead(), matcher);
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
