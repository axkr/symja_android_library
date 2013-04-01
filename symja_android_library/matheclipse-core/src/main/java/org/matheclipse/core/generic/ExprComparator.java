package org.matheclipse.core.generic;

import java.util.Comparator;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Compares an expression with another expression for order. Returns a
 * negative integer, zero, or a positive integer if this expression is canonical
 * less than, equal to, or greater than the specified expression.
 */
public class ExprComparator implements Comparator<IExpr> {
	public final static ExprComparator CONS = new ExprComparator();

	public int compare(final IExpr o1, final IExpr o2) {
		return o1.compareTo(o2);
	}
}