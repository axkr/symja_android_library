package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.visit.VisitorLevelSpecification;

import com.google.common.base.Function;

/**
 * 
 * 
 */
public class Count implements IFunctionEvaluator {
	private static class CountFunctor implements Function<IExpr, IExpr> {
		protected final IPatternMatcher matcher;
		protected int counter;

		/**
		 * @return the counter
		 */
		public int getCounter() {
			return counter;
		}

		public CountFunctor(final IExpr pattern) {
			this.matcher = new PatternMatcher(pattern);
			counter = 0;
		}

		@Override
		public IExpr apply(final IExpr arg) {
			if (matcher.apply(arg)) {
				counter++;
			}
			return null;
		}

	}

	public Count() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 4);

		final VisitorLevelSpecification level;
		CountFunctor mf = new CountFunctor(ast.get(2));
		if (ast.size() == 4) {
			level = new VisitorLevelSpecification(mf, ast.get(3), false);
		} else {
			level = new VisitorLevelSpecification(mf, 1);
		}
		ast.arg1().accept(level);
		return F.integer(mf.getCounter());
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDREST);
	}

}
