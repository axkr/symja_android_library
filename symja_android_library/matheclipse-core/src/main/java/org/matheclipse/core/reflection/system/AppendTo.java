package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

public class AppendTo extends AbstractFunctionEvaluator {

	class AppendToFunction implements Function<IExpr, IExpr> {
		private final IExpr value;

		public AppendToFunction(final IExpr value) {
			this.value = value;
		}

		@Override
		public IExpr apply(final IExpr symbolValue) {
			if (!symbolValue.isAST()) {
				return null;
			}
			final IAST f0 = ((IAST) symbolValue).clone();
			f0.add(value);
			return f0;
		}

	}

	public AppendTo() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if ((ast.size() == 3) && (ast.get(1).isSymbol())) {
			Function<IExpr, IExpr> function = new AppendToFunction(ast.get(2));
			final ISymbol sym = (ISymbol) ast.get(1);
			IExpr[] results = sym.reassignSymbolValue(function);
			if (results != null) {
				return results[1];
			}
		}

		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDFIRST);
	}
}