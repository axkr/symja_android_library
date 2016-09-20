package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * TODO implement &quot;Defer&quot; mode
 * 
 */
public class Defer extends AbstractCoreFunctionEvaluator {

	public Defer() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (!ToggleFeature.DEFER) {
			return F.NIL;
		}
		Validate.checkSize(ast, 2);
		IExpr arg1 = engine.evaluate(ast.arg1());

		return arg1;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		if (!ToggleFeature.DEFER) {
			return;
		}
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
