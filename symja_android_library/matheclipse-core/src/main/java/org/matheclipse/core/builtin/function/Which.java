package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Which extends AbstractCoreFunctionEvaluator {

	public Which() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkEven(ast);
		
		for (int i = 1; i < ast.size(); i += 2) {
			IExpr temp = engine.evaluate(ast.get(i));
			if (temp.isFalse()) {
				continue;
			}
			if (temp.isTrue()) {
				if ((i + 1 < ast.size())) {
					return engine.evaluate(ast.get(i + 1));
				}
				continue;
			}
			if (i==1) {
				return F.NIL;
			}
			return F.ast(ast, ast.head(), true, i, ast.size());
		}
		return F.Null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
