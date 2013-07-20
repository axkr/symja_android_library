package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Which implements ICoreFunctionEvaluator {

	public Which() {
		super();
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 4);
		final EvalEngine engine = EvalEngine.get();
		for (int i = 1; i < ast.size(); i += 2) {
			IExpr temp = engine.evaluate(ast.get(i));
			if (temp.isTrue() && (i + 1 < ast.size())) {
				return engine.evaluate(ast.get(i + 1));
			}
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
