package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 *
 * Returns <code>True</code> if the 1st argument is a vector;
 * <code>False</code> otherwise
 */
public class VectorQ extends AbstractCorePredicateEvaluator {

	public VectorQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		return arg1.isVector() != (-1);
	} 

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

}
