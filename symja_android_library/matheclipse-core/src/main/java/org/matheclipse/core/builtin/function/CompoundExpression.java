package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class CompoundExpression implements IFunctionEvaluator {

	public CompoundExpression() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() > 1) {
			final EvalEngine engine = EvalEngine.get();
			for (int i = 1; i < ast.size() - 1; i++) {
				// as sideeffect evaluate the i-th argument
				engine.evaluate(ast.get(i));
			}
			return engine.evaluate(ast.get(ast.size() - 1));
		}
		return F.Null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
