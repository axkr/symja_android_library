package org.matheclipse.core.builtin.function;

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

/**
 * For[] loop
 * 
 * Example: For[$j = 1, $j <= 10, $j++, Print[$j]]
 * 
 */
public class For implements ICoreFunctionEvaluator {

	public For() {
		super();
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 5);
		final EvalEngine engine = EvalEngine.get();

		IExpr temp = F.Null;
		engine.evaluate(ast.get(1));
		final int iterationLimit = engine.getIterationLimit();
		int iterationCounter = 1;
		
		while (true) {
			try {
				if (!engine.evaluate(ast.get(2)).equals(F.True)) {
					return temp;
				}
				temp = engine.evaluate(ast.get(4));
				
				if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					IterationLimitExceeded.throwIt(iterationCounter, ast);
				}
			} catch (final BreakException e) {
				return F.Null;
			} catch (final ContinueException e) {
				if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					IterationLimitExceeded.throwIt(iterationCounter, ast);
				}
				continue;
			} finally {
				engine.evaluate(ast.get(3));
			}
		}
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}
