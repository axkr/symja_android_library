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

/**
 * For[] loop
 * 
 * Example: For[$j = 1, $j <= 10, $j++, Print[$j]]
 * 
 */
public class For extends AbstractCoreFunctionEvaluator {

	public For() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 4, 5);
		final EvalEngine engine = EvalEngine.get();
		final int iterationLimit = engine.getIterationLimit();
		int iterationCounter = 1;

		// For(start, test, incr, body)
		engine.evaluate(ast.arg1()); // start
		IExpr test = ast.arg2();
		IExpr incr = ast.arg3();
		IExpr body = F.Null;
		if (ast.size() == 5) {
			body = ast.arg4();
		}
		boolean exit = false;
		while (true) {
			try {
				if (!engine.evaluate(test).isTrue()) {
					exit = true;
					return F.Null;
				}
				if (ast.size() == 5) {
					engine.evaluate(body);
				}
				if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					IterationLimitExceeded.throwIt(iterationCounter, ast);
				}
			} catch (final BreakException e) {
				exit = true;
				return F.Null;
			} catch (final ContinueException e) {
				if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					IterationLimitExceeded.throwIt(iterationCounter, ast);
				}
				continue;
			} catch (final ReturnException e) {
				return e.getValue();
			} finally {
				if (!exit) {
					engine.evaluate(incr);
				}
			}
		}
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
