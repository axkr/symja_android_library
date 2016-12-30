package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is a <code>Missing()</code>
 * expression; <code>False</code> otherwise
 */
public class MissingQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static MissingQ CONST = new MissingQ();

	public MissingQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		return arg1.isAST(F.Missing, 2);
	}

	@Override
	public boolean test(final IExpr expr) {
		return expr.isAST(F.Missing, 2);
	}
}
