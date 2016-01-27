package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
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
public class Implies extends AbstractCoreFunctionEvaluator {
	public Implies() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		IExpr arg1 = engine.evaluate(ast.arg1());
		if (arg1.isTrue()) {
			return ast.arg2();
		}
		if (arg1.isFalse()) {
			return F.True;
		}
		IExpr arg2 = engine.evaluate(ast.arg2());
		if (arg2.isTrue()) {
			return F.True;
		}
		if (arg2.isFalse()) {
			return F.Not(arg1);
		}
		if (arg1.equals(arg2)) {
			return F.True;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
