package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class Switch extends AbstractCoreFunctionEvaluator {

	public Switch() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 4);
		final EvalEngine engine = EvalEngine.get();
		final IExpr arg1 = engine.evaluate(ast.arg1());
		IPatternMatcher matcher;
		for (int i = 2; i < ast.size(); i += 2) {
			matcher = engine.evalPatternMatcher(ast.get(i));
			if (matcher.apply(arg1) && (i + 1 < ast.size())) {
				return engine.evaluate(ast.get(i + 1));
			}
		}
		return F.Null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
