package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * 
 * 
 */
public class Nor extends AbstractFunctionEvaluator {
	public Nor() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 1) {
			return F.True;
		}
		if (ast.size() == 2) {
			return F.Not(ast.arg1());
		}
		IAST result = ast.copyHead();
		boolean evaled = false;

		for (int i = 1; i < ast.size(); i++) {
			IExpr temp = F.eval(ast.get(i));
			if (temp.isTrue()) {
				return F.False;
			} else if (temp.isFalse()) {
				evaled = true;
			} else {
				result.add(temp);
			}
		}
		if (evaled) {
			if (result.size() == 1) {
				return F.True;
			}
			if (result.size() == 2) {
				return F.Not(ast.arg1());
			}
			return result;
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
