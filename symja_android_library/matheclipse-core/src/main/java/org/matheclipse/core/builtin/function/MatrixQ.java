package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is a matrix; <code>False</code>
 * otherwise
 */
public class MatrixQ extends AbstractCorePredicateEvaluator {

	public MatrixQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		return arg1.isMatrix() != null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
	}

}
