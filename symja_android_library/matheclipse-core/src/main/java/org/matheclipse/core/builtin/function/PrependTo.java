package org.matheclipse.core.builtin.function;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class PrependTo extends AbstractCoreFunctionEvaluator {

	static class PrependToFunction implements Function<IExpr, IExpr> {
		private final IExpr value;

		public PrependToFunction(final IExpr value) {
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr symbolValue) {
			if (!symbolValue.isAST()) {
				return F.NIL;
			}
			return ((IAST) symbolValue).addAtClone(1, value);
		}

	}

	public PrependTo() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		ISymbol sym = Validate.checkSymbolType(ast, 1);
		IExpr arg2 = engine.evaluate(ast.arg2());
		Function<IExpr, IExpr> function = new PrependToFunction(arg2);
		IExpr[] results = sym.reassignSymbolValue(function, F.PrependTo);
		if (results != null) {
			return results[1];
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDFIRST);
	}
}