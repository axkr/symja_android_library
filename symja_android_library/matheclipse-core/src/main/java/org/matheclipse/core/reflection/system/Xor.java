package org.matheclipse.core.reflection.system;

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
public class Xor extends AbstractFunctionEvaluator {
	public Xor() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() == 1) {
			return F.False;
		}
		if (ast.size() == 2) {
			return ast.arg1();
		}
		boolean result = false;

		for (int i = 1; i < ast.size(); i++) {
			if (ast.get(i).isTrue()) {
				result = !result;
			} else if (ast.get(i).isFalse()) {
			} else {
				return F.NIL;
			}
		}

		return F.bool(result);
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.ORDERLESS | ISymbol.ONEIDENTITY | ISymbol.FLAT);
	}
}
