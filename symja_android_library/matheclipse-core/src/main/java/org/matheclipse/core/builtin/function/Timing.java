package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Calculate the time needed for evaluating an expression
 * 
 */
public class Timing extends AbstractCoreFunctionEvaluator {

	public Timing() {
		// empty ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		final long begin = System.currentTimeMillis();
		final IExpr result = engine.evaluate(ast.arg1());
		return List(Divide(F.num(System.currentTimeMillis() - begin), F.integer(1000L)), F.Hold(result));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
