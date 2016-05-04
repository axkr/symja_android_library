package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Test if a number is prime. See:
 * <a href="http://en.wikipedia.org/wiki/Prime_number">Wikipedia:Prime
 * number</a>
 * 
 * @see org.matheclipse.core.reflection.system.NextPrime
 */
public class PrimeQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

	public PrimeQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		if (!arg1.isInteger()) {
			return false;
		}
		return test(arg1);
	}

	@Override
	public boolean test(final IExpr obj) {
		try {
			return ((IInteger) obj).isProbablePrime();
		} catch (final Exception e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}
}
