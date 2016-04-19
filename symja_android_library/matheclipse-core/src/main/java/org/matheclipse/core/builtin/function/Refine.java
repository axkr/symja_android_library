package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Refine(expr, assumptions) - evaluate expression for the given assumptions
 * 
 */
public class Refine extends AbstractCoreFunctionEvaluator {

	public Refine() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		final IExpr arg2 = engine.evaluate(ast.arg2());
		IAssumptions assumptions = determineAssumptions(ast.topHead(), arg2, engine);
		if (assumptions != null) {
			return refineAssumptions(ast.arg1(), assumptions, engine);
		}
		return F.NIL;
	}

	public static IAssumptions determineAssumptions(final ISymbol symbol, final IExpr arg2, EvalEngine engine) {
		final Options options = new Options(symbol, arg2, engine);
		IExpr option = options.getOption("Assumptions");
		if (option.isPresent()) {
			return Assumptions.getInstance(option);
		} else {
			return Assumptions.getInstance(arg2);
		}
	}

	public static IExpr refineAssumptions(final IExpr expr, IAssumptions assumptions, EvalEngine engine) {
		try {
			engine.setAssumptions(assumptions);
			return engine.evalWithoutNumericReset(expr);
		} finally {
			engine.setAssumptions(null);
		}
	}

}