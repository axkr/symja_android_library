package org.matheclipse.core.reflection.system;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Function;

public class Increment extends Decrement {
	class IncrementFunction implements Function<IExpr, IExpr> {
		@Override
		public IExpr apply(final IExpr assignedValue) {
			return F.eval(F.Plus(assignedValue, F.C1));
		}

	}

	protected Function<IExpr, IExpr> getFunction() {
		return new IncrementFunction();
	}

}