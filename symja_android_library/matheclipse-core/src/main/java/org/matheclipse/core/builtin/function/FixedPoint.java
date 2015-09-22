package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;

public class FixedPoint extends AbstractCoreFunctionEvaluator {

	public FixedPoint() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		try {
			final int iterationLimit = engine.getIterationLimit();
			int iterationCounter = 1;

			IExpr f = ast.arg1();
			IExpr current = ast.arg2();
			int iterations = Integer.MAX_VALUE;
			if (ast.size() == 4) {
				iterations = Validate.checkIntType(ast, 3);
			}
			IExpr last;
			do {
				last = current;
				current = engine.evaluate(F.Apply(f, F.List(current)));
				if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					IterationLimitExceeded.throwIt(iterationCounter, ast);
				}
			} while ((!current.isSame(last)) && (--iterations > 0));
			return current;

		} finally {
			EvalEngine.get().setNumericMode(false);
		}

	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

}