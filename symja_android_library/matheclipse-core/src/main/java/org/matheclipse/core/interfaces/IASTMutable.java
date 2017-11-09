package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;

public interface IASTMutable extends IAST {

	/**
	 * Adds the specified expression at the end of this {@code List}.
	 * 
	 * @param expr
	 *            the object to add.
	 * @return always true.
	 * @throws UnsupportedOperationException
	 *             if adding to this {@code List} is not supported.
	 * @throws ClassCastException
	 *             if the class of the object is inappropriate for this {@code List}.
	 * @throws IllegalArgumentException
	 *             if the object cannot be added to this {@code List}.
	 */
	public boolean append(IExpr expr);
	
	
	/**
	 * Inserts the specified object into this {@code List} at the specified location. The object is inserted before the
	 * current element at the specified location. If the location is equal to the size of this {@code List}, the object
	 * is added at the end. If the location is smaller than the size of this {@code List}, then all elements beyond the
	 * specified location are moved by one position towards the end of the {@code List}.
	 * 
	 * @param location
	 *            the index at which to insert.
	 * @param object
	 *            the object to add.
	 * @throws UnsupportedOperationException
	 *             if adding to this {@code List} is not supported.
	 * @throws ClassCastException
	 *             if the class of the object is inappropriate for this {@code List}.
	 * @throws IllegalArgumentException
	 *             if the object cannot be added to this {@code List}.
	 * @throws IndexOutOfBoundsException
	 *             if {@code location < 0 || location > size()}
	 */
	public void append(int location, IExpr object);
	
	/**
	 * Appends all of the arguments (starting from offset <code>1</code>) in the specified AST to the end of this AST.
	 * 
	 * @param ast
	 *            AST containing elements to be added to this AST
	 * @return <tt>true</tt> if this AST changed as a result of the call
	 * 
	 */
	public boolean appendArgs(IAST ast);
	
	default boolean appendPlus(IExpr expr) {
		if (head().equals(F.Plus) && expr.head().equals(F.Plus)) {
			return appendArgs((IAST) expr);
		}
		return append(expr);
	}
	
	/**
	 * Replaces the element at the specified location in this {@code IAST} with the specified object. This operation
	 * does not change the size of the {@code IAST}.
	 * 
	 * @param i
	 *            the index at which to put the specified object.
	 * @param object
	 *            the object to insert.
	 * @return the previous element at the index.
	 * @throws UnsupportedOperationException
	 *             if replacing elements in this {@code IAST} is not supported.
	 * @throws ClassCastException
	 *             if the class of an object is inappropriate for this {@code IAST}.
	 * @throws IllegalArgumentException
	 *             if an object cannot be added to this {@code IAST}.
	 * @throws IndexOutOfBoundsException
	 *             if {@code location < 0 || >= size()}
	 */
	public IExpr set(int i, IExpr object);

}
