package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is a list expression; <code>False</code> otherwise
 */
public class ListQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static ListQ CONST = new ListQ();

	public ListQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		return arg1.isList();
	}

	@Override
	public boolean test(final IExpr expr) {
		return expr.isList();
	}
}
