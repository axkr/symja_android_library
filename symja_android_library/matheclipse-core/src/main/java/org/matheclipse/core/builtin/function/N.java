package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 */
public class N extends AbstractCoreFunctionEvaluator {

	public N() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		return numericEval(ast, engine);
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		final boolean numericMode = engine.isNumericMode();
		final int oldPrecision = engine.getNumericPrecision();
		try {
			int numericPrecision = EvalEngine.DOUBLE_PRECISION;
			if (ast.isAST2()) {
				numericPrecision = Validate.checkIntType(ast.arg2());
			}
			engine.setNumericMode(true, numericPrecision);
			return engine.evalWithoutNumericReset(ast.arg1());
		} finally {
			engine.setNumericMode(numericMode);
			engine.setNumericPrecision(oldPrecision);
		}
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
