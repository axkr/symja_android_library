package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 * 
 * 
 */
public class Nand extends AbstractCoreFunctionEvaluator {
	public Nand() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 1) {
			return F.False;
		}
		if (ast.size() == 2) {
			return F.Not(ast.arg1());
		}
		IAST result = ast.copyHead();
		boolean evaled = false;

		for (int i = 1; i < ast.size(); i++) {
			IExpr temp = engine.evaluate(ast.get(i));
			if (temp.isFalse()) {
				return F.True;
			} else if (temp.isTrue()) {
				evaled = true;
			} else {
				result.add(temp);
			}
		}
		if (evaled) {
			if (result.size() == 1) {
				return F.False;
			}
			if (result.size() == 2) {
				return F.Not(result.arg1());
			}
			return result;
		}
		return F.UNEVALED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
