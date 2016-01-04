package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Operator +=
 * 
 */
public class AddTo extends AbstractFunctionEvaluator {
	static class AddToFunction implements Function<IExpr, IExpr> {
		final EvalEngine engine;
		final IExpr value;

		public AddToFunction(final IExpr value, EvalEngine engine) {
			this.engine = engine;
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr assignedValue) {
			return engine.evaluate(F.Plus(assignedValue, value));
		}

	}

	/**
	 * Get the evaluation function for AddTo (similar for DivideBy, TimesBy and SubtractFrom).
	 * 
	 * @param value
	 *            the value which is assigned in this statement (i.e. <code>variable += value</code>)
	 * @return
	 */
	protected Function<IExpr, IExpr> getFunction(IExpr value, EvalEngine engine) {
		return new AddToFunction(value, engine);
	}

	public AddTo() {

	}

	protected ISymbol getFunctionSymbol() {
		return F.AddTo;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		ISymbol sym = Validate.checkSymbolType(ast, 1);
		IExpr arg2 = engine.evaluate(ast.arg2());
		IExpr[] results = sym.reassignSymbolValue(getFunction(engine.evaluate(arg2), engine), getFunctionSymbol());
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