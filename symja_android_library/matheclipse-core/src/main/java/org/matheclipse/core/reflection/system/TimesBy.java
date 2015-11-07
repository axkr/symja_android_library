package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Operator *=
 * 
 */
public class TimesBy extends AddTo {
	static class TimesByFunction implements Function<IExpr, IExpr> {
		final IExpr value;

		public TimesByFunction(final IExpr value) {
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr assignedValue) {
			return F.eval(F.Times(assignedValue, value));
		}


		protected ISymbol getFunctionSymbol() {
			return F.TimesBy;
		}
	}

	@Override
	protected Function<IExpr, IExpr> getFunction(IExpr value) {
		return new TimesByFunction(value);
	}

}