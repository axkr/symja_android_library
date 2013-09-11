package org.matheclipse.core.generic;

import org.matheclipse.core.generic.interfaces.BiPredicate;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A binary function which compares the first expression with the second expression for order.
 * Returns true if the first expression is canonical less than or equal to the second
 * expression (&lt;= relation).
 *
 */
public class IsLEOrdered<E extends IExpr> implements  BiPredicate<E> {
	public boolean apply(final IExpr firstArg, final IExpr secondArg) {
		return firstArg.isLEOrdered(secondArg);
	}
}