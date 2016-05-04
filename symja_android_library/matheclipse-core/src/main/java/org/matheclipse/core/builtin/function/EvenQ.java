package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Predicate function
 * 
 * Returns <code>True</code> if the 1st argument is an even integer number;
 * <code>False</code> otherwise
 */
public class EvenQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {
	/**
	 * Constructor for the unary predicate
	 */
	public final static EvenQ CONST = new EvenQ();

	public EvenQ() {
		// default ctor
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		return arg1.isInteger() && ((IInteger) arg1).isEven();
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public boolean test(final IExpr expr) {
		return (expr.isInteger()) && ((IInteger) expr).isEven();
	}
}
