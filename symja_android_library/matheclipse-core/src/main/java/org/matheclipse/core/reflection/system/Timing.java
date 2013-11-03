package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Calculate the time needed for evaluating a n expression
 * 
 */
public class Timing implements IFunctionEvaluator {

	public Timing() {
	}

	public IExpr evaluate(final IAST ast) {
		if (ast.size() == 2) {
			final long begin = System.currentTimeMillis();
			final EvalEngine engine = EvalEngine.get();
			final IExpr result = engine.evaluate(ast.arg1());
			return List(Times(FractionSym.valueOf((System
					.currentTimeMillis() - begin), 1000L),
					F.Second), result);
		}

		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
