package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class Switch implements ICoreFunctionEvaluator {

	public Switch() {
		super();
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 4);
		final EvalEngine engine = EvalEngine.get();
		IExpr arg1 = engine.evaluate(ast.get(1));
		PatternMatcher matcher;
		for (int i = 2; i < ast.size(); i += 2) {
			matcher = new PatternMatcher(ast.get(i));
			if (matcher.apply(arg1) && (i + 1 < ast.size())) {
				return engine.evaluate(ast.get(i + 1));
			}
		}
		return F.Null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
