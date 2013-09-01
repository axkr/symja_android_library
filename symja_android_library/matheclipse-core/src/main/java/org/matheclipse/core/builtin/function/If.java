package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class If extends AbstractCoreFunctionEvaluator {

	public If() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 3, 5);
		final EvalEngine engine = EvalEngine.get();

		final IExpr temp = engine.evaluate(ast.get(1));

		if (temp.equals(F.False)) {
			if (ast.size() >= 4) {
				return ast.get(3);
			}

			return F.Null;
		}

		if (temp.equals(F.True)) {
			return ast.get(2);
		}

		if (ast.size() == 5) {
			return ast.get(4);
		}

		return F.Null;
	} 

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
