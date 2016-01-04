package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Operator *=
 * 
 */
public class TimesBy extends AddTo {
	static class TimesByFunction implements Function<IExpr, IExpr> {
		final EvalEngine engine;
		final IExpr value;

		public TimesByFunction(final IExpr value, EvalEngine engine) {
			this.engine = engine;
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr assignedValue) {
			return engine.evaluate(F.Times(assignedValue, value));
		}

		protected ISymbol getFunctionSymbol() {
			return F.TimesBy;
		}
	}

	@Override
	protected Function<IExpr, IExpr> getFunction(IExpr value, EvalEngine engine) {
		return new TimesByFunction(value, engine);
	}

}