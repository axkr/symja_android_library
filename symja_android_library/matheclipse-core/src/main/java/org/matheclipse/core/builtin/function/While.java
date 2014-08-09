package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class While extends AbstractCoreFunctionEvaluator {

	public While() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 2, 3);
		final EvalEngine engine = EvalEngine.get();

		final int iterationLimit = engine.getIterationLimit();
		int iterationCounter = 1;
		// While(test, body)
		IExpr test = ast.arg1();
		IExpr body = F.Null;
		if (ast.size() == 3) {
			body = ast.arg2();
		}

		while (engine.evaluate(test).isTrue()) {
			try {
				if (ast.size() == 3) {
					engine.evaluate(body);
				}
				if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					IterationLimitExceeded.throwIt(iterationCounter, ast);
				}
			} catch (final BreakException e) {
				return F.Null;
			} catch (final ContinueException e) {
				continue;
			} catch (final ReturnException e) {
				return e.getValue();
			}
		}

		return F.Null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
