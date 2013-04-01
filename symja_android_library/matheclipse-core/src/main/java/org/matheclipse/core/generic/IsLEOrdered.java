package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.interfaces.BiPredicate;

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