package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

/**
 * Operator +=
 * 
 */
public class AddTo extends AbstractFunctionEvaluator {
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

	/**
	 * Get the evaluation function for AddTo (similar for DivideBy, TimesBy and SubtractFrom).
	 * 
	 * @param value
	 *            the value which is assigned in this statement (i.e. <code>variable += value</code>)
	 * @return
	 */
	protected Function<IExpr, IExpr> getFunction(IExpr value) {
		return new AddToFunction(value);
	}

	public AddTo() {

	}

	protected ISymbol getFunctionSymbol() {
		return F.AddTo;
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		ISymbol sym = Validate.checkSymbolType(ast, 1);
		IExpr arg2 = F.eval(ast.arg2());
		IExpr[] results = ((ISymbol) sym).reassignSymbolValue(getFunction(F.eval(arg2)), getFunctionSymbol());
		if (results != null) {
			return results[1];
		} 
		return null;
	}
	
	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}