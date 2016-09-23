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
public class Nor extends AbstractCoreFunctionEvaluator {
	public Nor() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST0()) {
			return F.True;
		}
		if (ast.isAST1()) {
			return F.Not(ast.arg1());
		}
		IAST result = ast.copyHead();
		boolean evaled = false;

		for (int i = 1; i < ast.size(); i++) {
			IExpr temp = engine.evaluate(ast.get(i));
			if (temp.isTrue()) {
				return F.False;
			} else if (temp.isFalse()) {
				evaled = true;
			} else {
				result.append(temp);
			}
		}
		if (evaled) {
			if (result.isAST0()) {
				return F.True;
			}
			if (result.isAST1()) {
				return F.Not(result.arg1());
			}
			return result;
		}
		return F.NIL;
	}

	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
