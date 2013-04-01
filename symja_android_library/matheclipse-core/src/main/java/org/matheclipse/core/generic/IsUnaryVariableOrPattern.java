package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 *
 */
public class IsUnaryVariableOrPattern<E extends IExpr> implements Predicate<E> {
	public IsUnaryVariableOrPattern() {
	}

	public boolean apply(final IExpr firstArg) {
		if (firstArg instanceof ISymbol) {
			return true;
		}
		if (firstArg instanceof IPattern) {
			return true;
		}
		return false;
	}

}
