package org.matheclipse.core.generic;

import java.util.function.Predicate;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 */
public class IsUnaryVariable<E extends IExpr> implements Predicate<E> {
	public IsUnaryVariable() {
	}

	public boolean test(final IExpr firstArg) {
		return (firstArg instanceof ISymbol);
	}

}
