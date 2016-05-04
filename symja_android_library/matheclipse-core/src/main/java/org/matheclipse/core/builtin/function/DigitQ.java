package org.matheclipse.core.builtin.function;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCorePredicateEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Returns <code>True</code>, if the given expression is a string which only
 * contains digits.
 * 
 */
public class DigitQ extends AbstractCorePredicateEvaluator implements Predicate<IExpr> {

	public DigitQ() {
	}

	@Override
	public boolean evalArg1Boole(final IExpr arg1, EvalEngine engine) {
		if (arg1 instanceof IStringX) {
			return test(arg1);
		}
		return false;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public boolean test(final IExpr obj) {
		final String str = obj.toString();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (!((ch >= '0') && (ch <= '9'))) {
				return false;
			}
		}
		return true;
	}
}
