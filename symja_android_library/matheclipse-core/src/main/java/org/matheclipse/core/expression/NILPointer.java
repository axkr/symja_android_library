package org.matheclipse.core.expression;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The class <code>NILPointer</code> implements the constant object <code>F.NIL</code> (not in list), which indicates in
 * the evaluation process that no evaluation was possible (i.e. no further definition was found to create a new
 * expression from the existing one).
 * <p>
 * Almost every modifying method in this class throws an <tt>UnsupportedOperationException</tt>, almost every predicate
 * returns <code>false</code>. The main method to check if the object is valid is the <code>isPresent()</code>
 * method. The method is similar to <code>java.util.Optional#isPresent()</code>.
 * </p>
 * 
 * @see F#NIL
 * @see java.util.Optional#isPresent
 */
public class NILPointer extends AbstractAST implements IASTAppendable {

	private static final long serialVersionUID = -3552302876858011292L;

	private final static int SIZE = 0;

	/**
	 * The class <code>NILPointer</code> implements the constant object <code>F#NIL</code> (not in list), which
	 * indicates in the evaluation process that no evaluation was possible (i.e. no further definition was found to
	 * create a new expression from the existing one).
	 * 
	 * @see F#NIL
	 */
	protected NILPointer() {
	}

	@Override
	public boolean append(IExpr object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void append(int location, IExpr object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean appendAll(Collection<? extends IExpr> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean appendAll(IAST ast, int startPosition, int endPosition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean appendAll(int location, Collection<? extends IExpr> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean appendArgs(IAST ast) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean appendArgs(IAST ast, int untilPosition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IASTAppendable appendArgs(int start, int end, IntFunction<IExpr> function) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IASTAppendable appendArgs(int end, IntFunction<IExpr> function) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IAST appendOneIdentity(IAST subAST) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr arg1() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr arg2() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr arg3() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr arg4() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IExpr arg5() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<IExpr> asSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IASTAppendable clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(final Object obj) {
		return this == obj;
	}

	@Override
	public final IExpr evaluate(EvalEngine engine) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return -1;
	}

	@Override
	public final IExpr head() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean isAST() {
		return false;
	}

	@Override
	public final boolean isAST(final IExpr header) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isList() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isListOfLists() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int[] isMatrix(boolean setMatrixFormat) {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumericFunction() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isNumericMode() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isOneIdentityAST1() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPlus() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isPower() {
		return false;
	}

	@Override
	public final boolean isPresent() {
		return false;
	}

	@Override
	public boolean isSameHead(IExpr head) {
		return head().equals(head);
	}

	@Override
	public boolean isSameHead(IExpr head, int length) {
		return false;
	}

	@Override
	public boolean isSameHead(IExpr head, int minLength, int maxLength) {
		return false;
	}

	@Override
	public boolean isSameHeadSizeGE(IExpr head, int length) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isTimes() {
		return false;
	}

	@Override
	public final IExpr orElse(final IExpr other) {
		return other;
	}

	@Override
	public final IExpr orElseGet(Supplier<? extends IExpr> other) {
		return other.get();
	}

	@Override
	public final <X extends Throwable> IExpr orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		throw exceptionSupplier.get();
	}

	private Object readResolve() throws ObjectStreamException {
		return F.NIL;
	}

	@Override
	public IExpr remove(int location) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes the objects in the specified range from the start to the end, but not including the end index.
	 * 
	 * @param start
	 *            the index at which to start removing.
	 * @param end
	 *            the index one after the end of the range to remove.
	 * @throws IndexOutOfBoundsException
	 *             when {@code start < 0, start > end} or {@code end > size()}
	 */
	public void removeRange(int start, int end) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Replaces the element at the specified location in this {@code ArrayList} with the specified object.
	 * 
	 * @param location
	 *            the index at which to put the specified object.
	 * @param object
	 *            the object to add.
	 * @return the previous element at the index.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || >= size()}
	 */
	@Override
	public IExpr set(int location, IExpr object) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the number of elements in this {@code ArrayList}.
	 * 
	 * @return the number of elements in this {@code ArrayList}.
	 */
	@Override
	public int size() {
		return SIZE;
	}

	/**
	 * Returns a new array containing all elements contained in this {@code ArrayList}.
	 * 
	 * @return an array of the elements from this {@code ArrayList}
	 */
	@Override
	public IExpr[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "ExprNull";
	}
}
