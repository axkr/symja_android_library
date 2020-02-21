package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.RandomAccess;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RealMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

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
public class ASTRealMatrix extends AbstractAST implements Cloneable, Externalizable, RandomAccess {

	public ASTRealMatrix() {
		// When Externalizable objects are deserialized, they first need to be constructed by invoking the void
		// constructor. Since this class does not have one, serialization and deserialization will fail at runtime.
	}

	/**
	 * 
	 * Returns a new ASTRealMatrix where each element is mapped by the given function.
	 *
	 * @param matrixAST
	 *            an AST which could be converted into a <code>double[][]</code> matrix array.
	 * @param function
	 *            Function to apply to each entry.
	 * @return a new matrix.
	 */
	public static ASTRealMatrix map(final IAST matrixAST, DoubleUnaryOperator function) {
		double[][] matrix = matrixAST.toDoubleMatrix();
		int rows = matrix.length;
		int cols = matrix[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = function.applyAsDouble(matrix[i][j]);
			}
		}
		return new ASTRealMatrix(matrix, false);
	}

	/**
	 * The underlying matrix
	 */
	RealMatrix matrix;

	/**
	 * 
	 * @param matrix
	 * @param deepCopy
	 *            if <code>true</code> allocate new memory and copy all elements from the matrix
	 */
	public ASTRealMatrix(double[][] matrix, boolean deepCopy) {
		if (Config.MAX_AST_SIZE < matrix.length|| //
				Config.MAX_AST_SIZE < matrix[0].length) {
			throw new ASTElementLimitExceeded(matrix.length * matrix[0].length);
		}
		this.matrix = new Array2DRowRealMatrix(matrix, deepCopy);
	}

	/**
	 * 
	 * 
	 * @param matrix
	 *            the matrix which should be wrapped in this object.
	 * @param deepCopy
	 *            if <code>true</code> allocate new memory and copy all elements from the matrix
	 */
	public ASTRealMatrix(RealMatrix matrix, boolean deepCopy) {
		if (Config.MAX_AST_SIZE < matrix.getRowDimension() || //
				Config.MAX_AST_SIZE < matrix.getColumnDimension()) {
			throw new ASTElementLimitExceeded(matrix.getRowDimension() * matrix.getColumnDimension());
		}
		if (deepCopy) {
			this.matrix = matrix.copy();
		} else {
			this.matrix = matrix;
		}
	}

	/**
	 * Adds the objects in the specified collection to this {@code ArrayList}.
	 * 
	 * @param collection
	 *            the collection of objects.
	 * @return {@code true} if this {@code ArrayList} is modified, {@code false} otherwise.
	 */
	// @Override
	// public boolean appendAll(Collection<? extends IExpr> collection) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean appendAll(IAST ast, int startPosition, int endPosition) {
	// throw new UnsupportedOperationException();
	// }

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
	// @Override
	// public boolean appendAll(int location, Collection<? extends IExpr> collection) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean addAll(List<? extends IExpr> ast) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean appendAll(List<? extends IExpr> list, int startPosition, int endPosition) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public boolean appendArgs(IAST ast) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public final boolean appendArgs(IAST ast, int untilPosition) {
	// throw new UnsupportedOperationException();
	// }

	// @Override
	// public IAST appendOneIdentity(IAST subAST) {
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Adds the specified object at the end of this {@code ArrayList}.
	 * 
	 * @param object
	 *            the object to add.
	 * @return always true
	 */
	// @Override
	// public boolean append(IExpr object) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

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
	// @Override
	// public void append(int location, IExpr object) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

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
		return new ASTRealVector(matrix.getRowVector(0), false);
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
		return new ASTRealVector(matrix.getRowVector(1), false);
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
		return new ASTRealVector(matrix.getRowVector(2), false);
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
		return new ASTRealVector(matrix.getRowVector(3), false);
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
		return new ASTRealVector(matrix.getRowVector(4), false);
	}

	/** {@inheritDoc} */
	public int argSize() {
		return matrix.getRowDimension();
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
	// @Override
	// public void clear() {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

	/**
	 * Returns a new {@code HMArrayList} with the same elements, the same size and the same capacity as this
	 * {@code HMArrayList}.
	 * 
	 * @return a shallow copy of this {@code ArrayList}
	 * @see java.lang.Cloneable
	 */
	@Override
	public IAST clone() {
		return Convert.matrix2List(matrix);
		// return new ASTRealMatrix(matrix.copy(), false);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object object) {
		// if (object instanceof Num || object instanceof Double) {
		// double d = ((Number) object).doubleValue();
		// for (int i = 0; i < matrix.getDimension(); i++) {
		// if (matrix.getEntry(i) == d) {
		// return true;
		// }
		// }
		// }
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IASTMutable copy() {
		return new ASTRealMatrix(matrix.copy(), false);
	}

	@Override
	public IASTAppendable copyAppendable() {
		return Convert.matrix2List(matrix);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ASTRealMatrix) {
			if (obj == this) {
				return true;
			}
			return matrix.equals(((ASTRealMatrix) obj).matrix);
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr evaluate(EvalEngine engine) {
		// if ((getEvalFlags() & IAST.DEFER_AST) == IAST.DEFER_AST) {
		// return F.NIL;
		// }
		return F.NIL;
	}

	/** {@inheritDoc} */
	@Override
	public final IAST filterFunction(IASTAppendable filterAST, IASTAppendable restAST,
			final Function<IExpr, IExpr> function) {
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
	public IAST filter(IASTAppendable filterAST, IASTAppendable restAST, Predicate<? super IExpr> predicate) {
		return filterAST;
	}

	/** {@inheritDoc} */
	@Override
	public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
		return filterAST;
	}

	@Override
	public IExpr get(int location) {
		return new ASTRealVector(matrix.getRowVector(location - 1), false);
	}
	
	@Override
	public IAST getItems(int[] items, int length) { 
		double[][] m = new double[length][];
		for (int i = 0; i < length; i++) {
			m[i] = matrix.getRow(items[i] - 1);
		}
		return new ASTRealMatrix(m, false);
	}

	@Override
	public final IExpr getPart(final int... positions) {
		if (positions.length == 2) {
			return F.num(matrix.getEntry(positions[0], positions[1]));
		}
		return super.getPart(positions);
	}

	public RealMatrix getRealMatrix() {
		return matrix;
	}

	@Override
	public int hashCode() {
		if (hashValue == 0 && matrix != null) {
			hashValue = matrix.hashCode();
		}
		return hashValue;
	}

	@Override
	public final IExpr head() {
		return F.$RealMatrix;
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
	public final int[] isMatrix(boolean setMatrixFormat) {
		int[] dim = new int[2];
		dim[0] = matrix.getRowDimension();
		dim[1] = matrix.getColumnDimension();
		return dim;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isRealMatrix() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(ISymbol head) {
		return F.$RealMatrix == head;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(ISymbol head, int length) {
		return F.$RealMatrix == head && matrix.getRowDimension() == length - 1;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
		int size = matrix.getRowDimension() + 1;
		return F.$RealMatrix.equals(head) && minLength <= size && maxLength >= size;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSameHeadSizeGE(ISymbol head, int length) {
		return F.$RealMatrix == head && length <= matrix.getRowDimension() + 1;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr mapMatrixColumns(int[] dim, Function<IExpr, IExpr> f) {
		final int columnSize = dim[1];
		IASTAppendable result = F.ListAlloc(columnSize);
		return result.appendArgs(0, columnSize, j -> f.apply(new ASTRealVector(matrix.getColumnVector(j), false)));
		// for (int j = 0; j < columnSize; j++) {
		// result.append(f.apply(new ASTRealVector(matrix.getColumnVector(j), false)));
		// }
		// return result;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
		this.fEvalFlags = objectInput.readShort();
		this.matrix = (RealMatrix) objectInput.readObject();
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
	// @Override
	// public IExpr remove(int location) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

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
	// protected void removeRange(int start, int end) {
	// hashValue = 0;
	// throw new UnsupportedOperationException();
	// }

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
		if (object instanceof ASTRealVector) {
			IExpr value = new ASTRealVector(matrix.getRowVector(location - 1), false);
			matrix.setRowVector(location - 1, ((ASTRealVector) object).vector);
			return value;
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the number of elements in this {@code ArrayList}.
	 * 
	 * @return the number of elements in this {@code ArrayList}.
	 */
	@Override
	public int size() {
		return matrix.getRowDimension() + 1;
	}

	/**
	 * Returns a new array containing all elements contained in this {@code ArrayList}.
	 * 
	 * @return an array of the elements from this {@code ArrayList}
	 */
	@Override
	public IExpr[] toArray() {
		IExpr[] result = new IExpr[matrix.getRowDimension()];
		for (int i = 0; i < result.length; i++) {
			result[i] = new ASTRealVector(matrix.getRowVector(i), false);
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public double[][] toDoubleMatrix() {
		return matrix.getData();
	}

	/** {@inheritDoc} */
	@Override
	public RealMatrix toRealMatrix() {
		return matrix;
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		toString(buf, false);
		return buf.toString();
	}

	public void toString(Appendable buf, boolean isEmpty) {
		try {
			if (!isEmpty) {
				buf.append('\n');
			}
			buf.append('{');

			int rows = matrix.getRowDimension();
			int cols = matrix.getColumnDimension();
			for (int i = 0; i < rows; i++) {
				if (i != 0) {
					buf.append(" ");
				}
				buf.append("{");
				for (int j = 0; j < cols; j++) {
					buf.append(Double.toString(matrix.getEntry(i, j)));
					if (j < cols - 1) {
						buf.append(",");
					}
				}
				buf.append('}');
				if (i < rows - 1) {
					buf.append(",");
					buf.append('\n');
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
		objectOutput.writeObject(matrix);
	}

}
