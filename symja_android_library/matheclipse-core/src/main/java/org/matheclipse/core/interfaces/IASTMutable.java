package org.matheclipse.core.interfaces;

public interface IASTMutable extends IAST {

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
