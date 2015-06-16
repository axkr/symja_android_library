package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcher;

/**
 * Match an expression against a given pattern.
 * 
 */
public class MatchQ implements IFunctionEvaluator {

	public MatchQ() {
	}

	public IExpr evaluate(final IAST ast) {
		if ((ast.size() == 3)) {
			final EvalEngine engine = EvalEngine.get();
			final IExpr arg1 = engine.evaluate(ast.arg1());
			return F.bool(engine.evalPatternMatcher(ast.arg2()).apply(arg1));
		}
		return F.False;
	} 

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDREST);
	}

}
