package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

/**
 * Operator /=
 * 
 */
public class DivideBy extends AddTo {
	class DivideByFunction implements Function<IExpr, IExpr> {
		final IExpr value;

		public DivideByFunction(final IExpr value) {
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr assignedValue) {
			return F.eval(F.Times(assignedValue, F.Power(value, F.CN1)));
		}

	}

	@Override
	protected Function<IExpr, IExpr> getFunction(IExpr value) {
		return new DivideByFunction(value);
	}

	protected ISymbol getFunctionSymbol() {
		return F.DivideBy;
	}
	
}