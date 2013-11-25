package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

/**
 * Operator *=
 * 
 */
public class TimesBy extends AddTo {
	class TimesByFunction implements Function<IExpr, IExpr> {
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