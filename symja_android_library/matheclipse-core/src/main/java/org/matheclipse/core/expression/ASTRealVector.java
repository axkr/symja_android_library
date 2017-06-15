package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

import jdk.nashorn.internal.runtime.regexp.joni.Config;

/**
 * <p>
 * Immutable (A)bstract (S)yntax (T)ree of a given function with <b>no argument</b>.
 * </p>
 * 
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic structure of the Symja
 * source code. Each node of the tree denotes a construct occurring in the source code. The syntax is 'abstract' in the
 * sense that it does not represent every detail that appears in the real syntax. For instance, grouping parentheses are
 * implicit in the tree structure, and a syntactic construct such as a <code>Sin[x]</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument <code>x</code>.
 * </p>
 * 
 * Internally an AST is represented as a <code>java.util.List</code> which contains
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus, Times,...) at index
 * <code>0</code> and</li>
 * <li>the <code>n</code> arguments of a function in the index <code>1 to n</code></li>
 * </ul>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>.
 * 
 * @see AST
 */
public class ASTRealVector extends AbstractAST implements Cloneable, Externalizable, RandomAccess {

	public ASTRealVector() {
		// When Externalizable objects are deserialized, they first need to be constructed by invoking the void
		// constructor. Since this class does not have one, serialization and deserialization will fail at runtime.
	}

	/**
	 * 
	 * Returns a new ASTRealVector where each element is mapped by the given function.
	 *
	 * @param astVector
	 *            an AST which could be converted into <code>double[]</code>
	 * @param function
	 *            Function to apply to each entry.
	 * @return a new vector.
	 */
	public static ASTRealVector map(final IAST astVector, DoubleUnaryOperator function) {
		double[] vector = astVector.toDoubleVector();
		for (int i = 0; i < vector.length; i++) {
			vector[i] = function.applyAsDouble(vector[i]);
		}
		return new ASTRealVector(vector, false);
	}

	/**
	 * The underlying matrix
	 */
	RealVector vector;

	/**
	 * 
	 * @param vector
	 * @param deepCopy
	 */
	public ASTRealVector(double[] vector, boolean deepCopy) {
		this.vector = new ArrayRealVector(vector, deepCopy);
	}

	/**
	 * 
	 * 
	 * @param vector
	 *            the vector which should be wrapped in this object.
	 * @param deepCopy
	 *            TODO
	 */
	public ASTRealVector(RealVector vector, boolean deepCopy) {
		if (deepCopy) {
			this.vector = vector.copy();
		} else {
			this.vector = vector;
		}
	}

	/**
	 * Adds the objects in the specified collection to this {@code ArrayList}.
	 * 
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
	 */
	@Override
	public boolean appendAll(Collection<? extends IExpr> collection) {
		hashValue = 0;
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean appendAll(IAST ast, int startPosition, int endPosition) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts the objects in the specified collection at the specified location in this List. The objects are added in
	 * the order they are returned from the collection's iterator.
	 * 
	 * @param location
	 *            the index at which to insert.
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || > size()}
	 */
	@Override
	public boolean appendAll(int location, Collection<? extends IExpr> collection) {
		hashValue = 0;
		throw new UnsupportedOperationException();
	}

	// @Override
	// public boolean addAll(List<? extends IExpr> list) {
	// throw new UnsupportedOperationException();
	// }

	@Override
	public boolean appendAll(List<? extends IExpr> ast, int startPosition, int endPosition) {
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
	public IAST appendOneIdentity(IAST subAST) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds the specified object at the end of this {@code ArrayList}.
	 * 
	 * @param object
	 *            the object to add.
	 * @return always true
	 */
	@Override
	public boolean append(IExpr object) {
		hashValue = 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts the specified object into this {@code ArrayList} at the specified location. The object is inserted before
	 * any previous element at the specified location. If the location is equal to the size of this {@code ArrayList},
	 * the object is added at the end.
	 * 
	 * @param location
	 *            the index at which to insert the object.
	 * @param object
	 *            the object to add.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || > size()}
	 */
	@Override
	public void append(int location, IExpr object) {
		hashValue = 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the first argument (i.e. the second element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(1) ). <br />
	 * <b>Example:</b> for the AST representing the expression <code>Sin(x)</code>, <code>arg1()</code> returns
	 * <code>x</code>.
	 * 
	 * @return the first argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg1() {
		return F.num(vector.getEntry(0));
	}

	/**
	 * Get the second argument (i.e. the third element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(2) ). <br />
	 * <b>Example:</b> for the AST representing the expression <code>x^y</code> (i.e. <code>Power(x, y)</code>),
	 * <code>arg2()</code> returns <code>y</code>.
	 * 
	 * @return the second argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg2() {
		return F.num(vector.getEntry(1));
	}

	/**
	 * Get the third argument (i.e. the fourth element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(3) ).<br />
	 * <b>Example:</b> for the AST representing the expression <code>f(a, b, c)</code>, <code>arg3()</code> returns
	 * <code>c</code>.
	 * 
	 * @return the third argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg3() {
		return F.num(vector.getEntry(2));
	}

	/**
	 * Get the fourth argument (i.e. the fifth element of the underlying list structure) of the <code>AST</code>
	 * function (i.e. get(4) ).<br />
	 * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d)</code>, <code>arg4()</code> returns
	 * <code>d</code>.
	 * 
	 * @return the fourth argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg4() {
		return F.num(vector.getEntry(3));
	}

	/**
	 * Get the fifth argument (i.e. the sixth element of the underlying list structure) of the <code>AST</code> function
	 * (i.e. get(5) ).<br />
	 * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d, e)</code>, <code>arg5()</code>
	 * returns <code>e</code> .
	 * 
	 * @return the fifth argument of the function represented by this <code>AST</code>.
	 * @see IExpr#head()
	 */
	@Override
	public IExpr arg5() {
		return F.num(vector.getEntry(4));
	}

	@Override
	public Set<IExpr> asSet() {
		throw new UnsupportedOperationException();
		// empty set:
		// return new HashSet<IExpr>();
	}

	/**
	 * Removes all elements from this {@code ArrayList}, leaving it empty.
	 * 
	 * @see #isEmpty
	 * @see #size
	 */
	@Override
	public void clear() {
		hashValue = 0;
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a new {@code HMArrayList} with the same elements, the same size and the same capacity as this
	 * {@code HMArrayList}.
	 * 
	 * @return a shallow copy of this {@code ArrayList}
	 * @see java.lang.Cloneable
	 */
	@Override
	public IAST clone() {
		return new ASTRealVector(vector.copy(), false);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object object) {
		if (object instanceof Num || object instanceof Double) {
			double d = ((Number) object).doubleValue();
			for (int i = 0; i < vector.getDimension(); i++) {
				if (vector.getEntry(i) == d) {
					return true;
				}
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IAST copy() {
		return new ASTRealVector(vector.copy(), false);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ASTRealVector) {
			if (obj == this) {
				return true;
			}
			return vector.equals(((ASTRealVector) obj).vector);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST filter(IAST filterAST, IAST restAST, final Function<IExpr, IExpr> function) {
		final int size = size();
		for (int i = 1; i < size; i++) {
			IExpr expr = function.apply(get(i));
			if (expr.isPresent()) {
				filterAST.append(expr);
			} else {
				restAST.append(get(i));
			}
		}
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, IAST restAST, Predicate<? super IExpr> predicate) {
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IAST filterAST, Predicate<? super IExpr> predicate) {
		return filterAST;
	}

	@Override
	public IExpr get(int location) {
		double val = vector.getEntry(location - 1);
		return F.num(val);
	}

	public RealVector getRealVector() {
		return vector;
	}

	@Override
	public int hashCode() {
		if (hashValue == 0) {
			hashValue = vector.hashCode();
		}
		return hashValue;
	}

	@Override
	public final IExpr head() {
		return F.$RealVector;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST0() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST1() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST2() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAST3() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isList() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRealVector() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(IExpr head) {
		return F.$RealVector.equals(head);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(IExpr head, int length) {
		return F.$RealVector.equals(head) && vector.getDimension() == length - 1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(IExpr head, int minLength, int maxLength) {
		int size = vector.getDimension() + 1;
		return F.$RealVector.equals(head) && minLength <= size && maxLength >= size;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHeadSizeGE(IExpr head, int length) {
		return F.$RealVector.equals(head) && length <= vector.getDimension() + 1;
	}

	/** {@inheritDoc} */
	@Override
	public final int isVector() {
		return vector.getDimension();
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.fEvalFlags = objectInput.readShort();

		int size;
		byte attributeFlags = objectInput.readByte();
		if (attributeFlags != 0) {
			size = attributeFlags;
			int exprIDSize = objectInput.readByte();
			for (int i = 0; i < exprIDSize; i++) {
				set(i, F.GLOBAL_IDS[objectInput.readShort()]);
			}
			for (int i = exprIDSize; i < size; i++) {
				set(i, (IExpr) objectInput.readObject());
			}
			return;
		}

		size = objectInput.readInt();
		for (int i = 0; i < size; i++) {
			set(i, (IExpr) objectInput.readObject());
		}
	}

	/**
	 * Removes the object at the specified location from this list.
	 * 
	 * @param location
	 *            the index of the object to remove.
	 * @return the removed object.
	 * @throws IndexOutOfBoundsException
	 *             when {@code location < 0 || >= size()}
	 */
	@Override
	public IExpr remove(int location) {
		hashValue = 0;
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
	protected void removeRange(int start, int end) {
		hashValue = 0;
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
		hashValue = 0;
		if (object instanceof Num) {
			double value = vector.getEntry(location - 1);
			vector.setEntry(location - 1, ((Num) object).getReal());
			return F.num(value);
		}
		throw new IndexOutOfBoundsException(
				"Index: " + Integer.valueOf(location) + ", Size: " + (vector.getDimension() + 1));
	}

	/**
	 * Returns the number of elements in this {@code ArrayList}.
	 * 
	 * @return the number of elements in this {@code ArrayList}.
	 */
	@Override
	public int size() {
		return vector.getDimension() + 1;
	}

	/**
	 * Returns a new array containing all elements contained in this {@code ArrayList}.
	 * 
	 * @return an array of the elements from this {@code ArrayList}
	 */
	@Override
	public IExpr[] toArray() {
		IExpr[] result = new IExpr[vector.getDimension()];
		for (int i = 0; i < result.length; i++) {
			result[i] = F.num(vector.getEntry(i));

		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public double[] toDoubleVector() {
		return vector.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public RealVector toRealVector() {
		return vector;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		toString(buf);
		return buf.toString();
	}

	public void toString(Appendable buf) {
		try {
			buf.append('{');
			int size = vector.getDimension();
			for (int i = 0; i < size; i++) {
				buf.append(Double.toString(vector.getEntry(i)));
				if (i < size - 1) {
					buf.append(",");
				}
			}
			buf.append('}');
		} catch (IOException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeShort(fEvalFlags);

		int size = size();
		byte attributeFlags = (byte) 0;

		ExprID temp = F.GLOBAL_IDS_MAP.get(head());
		if (temp != null) {
			short exprID = temp.getExprID();
			if (exprID <= Short.MAX_VALUE) {
				int exprIDSize = 1;
				short[] exprIDArray = new short[size];
				exprIDArray[0] = exprID;
				for (int i = 1; i < size; i++) {
					temp = F.GLOBAL_IDS_MAP.get(get(i));
					if (temp == null) {
						break;
					}
					exprID = temp.getExprID();
					if (exprID <= Short.MAX_VALUE) {
						exprIDArray[i] = exprID;
						exprIDSize++;
					} else {
						break;
					}
				}
				// optimized path
				attributeFlags = (byte) size;
				objectOutput.writeByte(attributeFlags);
				objectOutput.writeByte((byte) exprIDSize);
				for (int i = 0; i < exprIDSize; i++) {
					objectOutput.writeShort(exprIDArray[i]);
				}
				for (int i = exprIDSize; i < size; i++) {
					objectOutput.writeObject(get(i));
				}
				return;
			}
		}

		objectOutput.writeByte(attributeFlags);
		objectOutput.writeInt(size);
		for (int i = 0; i < size; i++) {
			objectOutput.writeObject(get(i));
		}
	}

	private Object writeReplace() throws ObjectStreamException {
		return optional(F.GLOBAL_IDS_MAP.get(this));
	}
}
