package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class While implements ICoreFunctionEvaluator {

	public While() {
		super();
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);
		final EvalEngine engine = EvalEngine.get();

		final int iterationLimit = engine.getIterationLimit();
		int iterationCounter = 1;

		while (engine.evaluate(ast.get(1)).equals(F.True)) {
			try {
				engine.evaluate(ast.get(2));

				if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					IterationLimitExceeded.throwIt(iterationCounter, ast);
				}
			} catch (final BreakException e) {
				return F.Null;
			} catch (final ContinueException e) {
				continue;
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
