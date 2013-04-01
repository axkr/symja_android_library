package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 *
 */
public class IsUnaryVariable<E extends IExpr> implements Predicate<E> {
	public IsUnaryVariable() {
	}

	public boolean apply(final IExpr firstArg) {
		return (firstArg instanceof ISymbol);
	}

}
