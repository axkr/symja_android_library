package org.matheclipse.core.generic;

import java.util.function.Predicate;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 */
public class IsUnaryVariableOrPattern<E extends IExpr> implements Predicate<E> {
	public IsUnaryVariableOrPattern() {
	}

	public boolean test(final IExpr firstArg) {
		if (firstArg instanceof ISymbol) {
			return true;
		}
		if (firstArg instanceof IPattern) {
			return true;
		}
		return false;
	}

}
