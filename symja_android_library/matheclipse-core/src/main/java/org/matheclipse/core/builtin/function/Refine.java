package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Refine(expr, assumptions) - evaluate expression for the given assumptions
 * 
 */
public class Refine extends AbstractCoreFunctionEvaluator {

	public Refine() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 3);

		// final IExpr arg1 = F.eval(ast.arg1());
		final IExpr arg2 = F.eval(ast.arg2());
		IAssumptions assumptions = Assumptions.getInstance(arg2);
		if (assumptions != null) {
			EvalEngine engine = EvalEngine.get();
			try {
				engine.setAssumptions(assumptions);
				return engine.evalWithoutNumericReset(ast.arg1());
			} finally {
				engine.setAssumptions(null);
			}
		}
		return null;
	}

}