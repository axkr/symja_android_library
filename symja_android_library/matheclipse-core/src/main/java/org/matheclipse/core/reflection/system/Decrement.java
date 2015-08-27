package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

public class Decrement extends AbstractArg1 {
	class DecrementFunction implements Function<IExpr, IExpr> {
		@Override
		public IExpr apply(final IExpr assignedValue) {
			return F.eval(F.Plus(assignedValue, F.CN1));
		}

	}

	public Decrement() {
		super();
	}

	@Override
	public IExpr e1ObjArg(final IExpr expr) {
		if (expr.isSymbol() && ((ISymbol) expr).hasAssignedSymbolValue()) {
			final ISymbol sym = (ISymbol) expr;
			IExpr[] results = sym.reassignSymbolValue(getFunction(), getFunctionSymbol());
			if (results != null) {
				return getResult(results[0], results[1]);
			}
			return null;
		} 
		return null;
	}

	protected Function<IExpr, IExpr> getFunction() {
		return new DecrementFunction();
	}

	protected ISymbol getFunctionSymbol() {
		return F.Decrement;
	}

	protected IExpr getResult(IExpr symbolValue, IExpr calculatedResult) {
		return symbolValue;
	}

	/**
	 * Evaluate <code>assignedValue - 1</code>. Override this method in subclasses.
	 * 
	 * @param assignedValue
	 *            the value currently assigned to the symbol
	 * @param engine
	 *            the evaluation engine
	 * @return
	 */
	protected IExpr apply(final IExpr assignedValue) {
		return F.eval(F.Plus(assignedValue, F.CN1));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}