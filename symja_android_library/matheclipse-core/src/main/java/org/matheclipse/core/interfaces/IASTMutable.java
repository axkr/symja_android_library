package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;

public interface IASTMutable extends IAST {


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
