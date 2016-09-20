package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class CompoundExpression extends AbstractCoreFunctionEvaluator {

	public CompoundExpression() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() > 1) {
			for (int i = 1; i < ast.size() - 1; i++) {
				// as sideeffect evaluate the i-th argument
				engine.evaluate(ast.get(i));
			}
			return engine.evaluate(ast.get(ast.size() - 1));
		}
		return F.Null;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
