package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

/**
 * Operator +=
 * 
 */
public class AddTo extends AbstractArg2 {
	class AddToFunction implements Function<IExpr, IExpr> {
		final IExpr value;

		public AddToFunction(final IExpr value) {
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr assignedValue) {
			return F.eval(F.Plus(assignedValue, value));
		}

	}

	protected Function<IExpr, IExpr> getFunction(IExpr value) {
		return new AddToFunction(value);
	}

	public AddTo() {

	}

	protected ISymbol getFunctionSymbol() {
		return F.AddTo;
	}

	@Override
	public IExpr e2ObjArg(final IExpr o0, final IExpr o1) {
		final EvalEngine engine = EvalEngine.get();
		if (o0.isSymbol()) {
			final IExpr v1 = engine.evaluate(o1);
			final ISymbol sym = (ISymbol) o0;

			IExpr[] results = sym.reassignSymbolValue(getFunction(v1), getFunctionSymbol());
			if (results != null) {
				return results[1];
			}
		}

		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}