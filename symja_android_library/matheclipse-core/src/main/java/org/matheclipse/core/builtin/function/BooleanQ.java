package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the first argument is an integer;
 * <code>False</code> otherwise
 */
public class BooleanQ extends AbstractCorePredicateEvaluator {

	public BooleanQ() {
		// default ctor
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		return arg1.isTrue()||arg1.isFalse();
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
