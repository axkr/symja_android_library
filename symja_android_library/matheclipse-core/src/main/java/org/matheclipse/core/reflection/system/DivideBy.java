package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import java.util.function.Function;

/**
 * Operator /=
 * 
 */
public class DivideBy extends AddTo {
	static class DivideByFunction implements Function<IExpr, IExpr> {
		final EvalEngine engine; 
		final IExpr value;

		public DivideByFunction(final IExpr value, EvalEngine engine) {
			this.engine = engine;
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr assignedValue) {
			return engine.evaluate(F.Times(assignedValue, F.Power(value, F.CN1)));
		}

	}

	@Override
	protected Function<IExpr, IExpr> getFunction(IExpr value, EvalEngine engine) {
		return new DivideByFunction(value, engine);
	}

	@Override
	protected ISymbol getFunctionSymbol() {
		return F.DivideBy;
	}
	
}