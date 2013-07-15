package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

public class PrependTo extends AbstractFunctionEvaluator {

	class PrependToFunction implements Function<IExpr, IExpr> {
		private final IExpr value;

		public PrependToFunction(final IExpr value) {
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr symbolValue) {
			if (!symbolValue.isAST()) {
				return null;
			}
			final IAST f0 = ((IAST) symbolValue).clone();
			f0.add(1, value);
			return f0;
		}

	}

	public PrependTo() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		ISymbol sym = Validate.checkSymbolType(ast, 1);

		Function<IExpr, IExpr> function = new PrependToFunction(ast.get(2));
		IExpr[] results = sym.reassignSymbolValue(function);
		if (results != null) {
			return results[1];
		}

		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}