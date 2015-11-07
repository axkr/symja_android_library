package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Match an expression against a given pattern.
 * 
 */
public class MatchQ extends AbstractCoreFunctionEvaluator {

	public MatchQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if ((ast.size() == 3)) {
			final IExpr arg1 = engine.evaluate(ast.arg1());
			return F.bool(engine.evalPatternMatcher(ast.arg2()).test(arg1));
		}
		return F.False;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDREST);
	}

}
