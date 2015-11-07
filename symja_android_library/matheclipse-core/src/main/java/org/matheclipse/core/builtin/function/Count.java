package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.visit.VisitorLevelSpecification;
 
import java.util.function.Function;

/**
 * Count the number of elements in an expression which match the given pattern.
 * 
 */
public class Count extends AbstractCoreFunctionEvaluator {
	private static class CountFunctor implements Function<IExpr, IExpr> {
		protected final IPatternMatcher matcher;
		protected int counter;

		/**
		 * @return the counter
		 */
		public int getCounter() {
			return counter;
		}

		public CountFunctor(final IPatternMatcher patternMatcher) {
			this.matcher = patternMatcher; //new PatternMatcher(pattern);
			counter = 0;
		}

		@Override
		public IExpr apply(final IExpr arg) {
			if (matcher.test(arg)) {
				counter++;
			}
			return null;
		}

	}

	public Count() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		final IExpr arg1 = engine.evaluate(ast.arg1());
		
		final VisitorLevelSpecification level;
		CountFunctor mf = new CountFunctor(engine.evalPatternMatcher(ast.arg2()));
		if (ast.size() == 4) {
			final IExpr arg3 = engine.evaluate(ast.arg3());
			level = new VisitorLevelSpecification(mf, arg3, false);
		} else {
			level = new VisitorLevelSpecification(mf, 1);
		}
		arg1.accept(level);
		return F.integer(mf.getCounter());
	}

}
