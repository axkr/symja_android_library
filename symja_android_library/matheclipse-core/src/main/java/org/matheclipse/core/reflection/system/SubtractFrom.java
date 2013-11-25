package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

/**
 * Operator -=
 *
 */
public class SubtractFrom extends AddTo {
	class SubtractFromFunction implements Function<IExpr, IExpr> {
		final IExpr value;

		public SubtractFromFunction(final IExpr value) {
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr assignedValue) {
			return F.eval(F.Plus(assignedValue, F.Times(F.CN1, value)));
		}

		protected ISymbol getFunctionSymbol() {
			return F.SubtractFrom;
		}
	}

	@Override
	protected Function<IExpr, IExpr> getFunction(IExpr value) {
		return new SubtractFromFunction(value);
	}

}