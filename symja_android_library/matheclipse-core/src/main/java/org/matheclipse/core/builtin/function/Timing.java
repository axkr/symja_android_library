package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Calculate the time needed for evaluating an expression
 * 
 */
public class Timing extends AbstractEvaluator {

	public Timing() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.size() == 2) {
			final long begin = System.currentTimeMillis();
			final IExpr result = engine.evaluate(ast.arg1());
			return List(Times(AbstractFractionSym.valueOf((System.currentTimeMillis() - begin), 1000L), F.Second), result);
		}

		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
