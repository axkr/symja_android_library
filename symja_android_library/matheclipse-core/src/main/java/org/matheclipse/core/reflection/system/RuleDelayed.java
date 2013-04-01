package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.RuleDelayed;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;

public class RuleDelayed extends AbstractFunctionEvaluator {

	public RuleDelayed() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		final EvalEngine engine = EvalEngine.get();
		IExpr leftHandSide = ast.get(1);
		leftHandSide = PatternMatcher.evalLeftHandSide(leftHandSide, engine);
		if (!leftHandSide.equals(ast.get(1))) {
			return RuleDelayed(leftHandSide, ast.get(2));
		}

		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
