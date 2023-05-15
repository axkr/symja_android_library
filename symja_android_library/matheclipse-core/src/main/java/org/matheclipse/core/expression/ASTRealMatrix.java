package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Immutable (A)bstract (S)yntax (T)ree of a given function with <b>no argument</b>.
 *
 * <p>
 * In Symja, an abstract syntax tree (AST), is a tree representation of the abstract syntactic
 * structure of the Symja source code. Each node of the tree denotes a construct occurring in the
 * source code. The syntax is 'abstract' in the sense that it does not represent every detail that
 * appears in the real syntax. For instance, grouping parentheses are implicit in the tree
 * structure, and a syntactic construct such as a <code>Sin[x]</code> expression will be denoted by
 * an AST with 2 nodes. One node for the header <code>Sin</code> and one node for the argument
 * <code>x</code>. Internally an AST is represented as a <code>java.util.List</code> which contains
 *
 * <ul>
 * <li>the operator of a function (i.e. the &quot;header&quot;-symbol: Sin, Cos, Inverse, Plus,
 * Times,...) at index <code>0</code> and
 * <li>the <code>n</code> arguments of a function in the index <code>1 to n</code>
 * </ul>
 *
 * See <a href="http://en.wikipedia.org/wiki/Abstract_syntax_tree">Abstract syntax tree</a>.
 *
 * @see AST
 */
public class ASTRealMatrix extends AbstractAST implements Externalizable, RandomAccess {
  private static final Logger LOGGER = LogManager.getLogger();

  public ASTRealMatrix() {
    // When Externalizable objects are deserialized, they first need to be constructed by invoking
    // the void
    // constructor. Since this class does not have one, serialization and deserialization will fail
    // at runtime.
  }

  /**
   * Returns a new ASTRealMatrix where each element is mapped by the given function.
   *
   * @param matrixAST an AST which could be converted into a <code>double[][]</code> matrix array.
   * @param function Function to apply to each entry.
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

  /** The underlying matrix */
  RealMatrix matrix;

  public int getColumnDimension() {
    return matrix.getColumnDimension();
  }

  public int getRowDimension() {
    return matrix.getRowDimension();
  }

  /**
   * @param matrix
   * @param deepCopy if <code>true</code> allocate new memory and copy all elements from the matrix
   */
  public ASTRealMatrix(double[][] matrix, boolean deepCopy) {
    if (Config.MAX_MATRIX_DIMENSION_SIZE < matrix.length
        || Config.MAX_MATRIX_DIMENSION_SIZE < matrix[0].length) {
      throw new ASTElementLimitExceeded(matrix.length, matrix[0].length);
    }
    this.matrix = new Array2DRowRealMatrix(matrix, deepCopy);
  }

  /**
   * @param matrix the matrix which should be wrapped in this object.
   * @param deepCopy if <code>true</code> allocate new memory and copy all elements from the matrix
   */
  public ASTRealMatrix(RealMatrix matrix, boolean deepCopy) {
    if (Config.MAX_MATRIX_DIMENSION_SIZE < matrix.getRowDimension()
        || Config.MAX_MATRIX_DIMENSION_SIZE < matrix.getColumnDimension()) {
      throw new ASTElementLimitExceeded(matrix.getRowDimension(), matrix.getColumnDimension());
    }
    if (deepCopy) {
      this.matrix = matrix.copy();
    } else {
      this.matrix = matrix;
    }
  }

  /**
   * Get the first argument (i.e. the second element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(1) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>Sin(x)</code>, <code>arg1()
   * </code> returns <code>x</code>.
   *
   * @return the first argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg1() {
    return new ASTRealVector(matrix.getRowVector(0), false);
  }

  /**
   * Get the second argument (i.e. the third element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(2) ). <br>
   * <b>Example:</b> for the AST representing the expression <code>x^y</code> (i.e. <code>
   * Power(x, y)</code>), <code>arg2()</code> returns <code>y</code>.
   *
   * @return the second argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg2() {
    return new ASTRealVector(matrix.getRowVector(1), false);
  }

  /**
   * Get the third argument (i.e. the fourth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(3) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b, c)</code>, <code>arg3()
   * </code> returns <code>c</code>.
   *
   * @return the third argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg3() {
    return new ASTRealVector(matrix.getRowVector(2), false);
  }

  /**
   * Get the fourth argument (i.e. the fifth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(4) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d)</code>, <code>
   * arg4()</code> returns <code>d</code>.
   *
   * @return the fourth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg4() {
    return new ASTRealVector(matrix.getRowVector(3), false);
  }

  /**
   * Get the fifth argument (i.e. the sixth element of the underlying list structure) of the <code>
   * AST</code> function (i.e. get(5) ).<br>
   * <b>Example:</b> for the AST representing the expression <code>f(a, b ,c, d, e)</code>, <code>
   * arg5()</code> returns <code>e</code> .
   *
   * @return the fifth argument of the function represented by this <code>AST</code>.
   * @see IExpr#head()
   */
  @Override
  public IExpr arg5() {
    return new ASTRealVector(matrix.getRowVector(4), false);
  }

  /** {@inheritDoc} */
  @Override
  public int argSize() {
    return matrix.getRowDimension();
  }

  @Override
  public Set<IExpr> asSet() {
    int size = size();
    Set<IExpr> set = new TreeSet<>();
    for (int i = 1; i < size; i++) {
      set.add(get(i));
    }
    return set;
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
   * Returns a new {@code HMArrayList} with the same elements, the same size and the same capacity
   * as this {@code HMArrayList}.
   *
   * @return a shallow copy of this {@code ArrayList}
   * @see java.lang.Cloneable
   */
  @Override
  public IAST clone() {
    return Convert.matrix2List(matrix, false);
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
    return Convert.matrix2List(matrix, false);
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return copyAppendable();
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

  @Override
  public IExpr evalEvaluate(EvalEngine engine) {
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
  public IAST filter(IASTAppendable filterAST, IASTAppendable restAST,
      Predicate<? super IExpr> predicate) {
    return filterAST;
  }

  /** {@inheritDoc} */
  @Override
  public IAST filter(IASTAppendable filterAST, Predicate<? super IExpr> predicate) {
    return filterAST;
  }

  @Override
  public IExpr get(int location) {
    return (location == 0) ? head() : new ASTRealVector(matrix.getRowVector(location - 1), false);
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
    return S.List;
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
  public boolean isListOfLists() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public final int[] isMatrix(boolean setMatrixFormat) {
    return new int[] {matrix.getRowDimension(), matrix.getColumnDimension()};
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericFunction(boolean allowList) {
    return allowList;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public int[] isPiecewise() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRealMatrix() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head) {
    return S.List == head;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int length) {
    return S.List == head && matrix.getRowDimension() == length - 1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
    int size = matrix.getRowDimension() + 1;
    return S.List == head && minLength <= size && maxLength >= size;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHeadSizeGE(ISymbol head, int length) {
    return S.List == head && length <= matrix.getRowDimension() + 1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr mapMatrixColumns(int[] dim, Function<IExpr, IExpr> f) {
    final int columnSize = dim[1];
    return F.mapRange(0, columnSize,
        j -> f.apply(new ASTRealVector(matrix.getColumnVector(j), false)));
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();
    this.matrix = (RealMatrix) objectInput.readObject();
  }

  /**
   * Replaces the element at the specified location in this {@code ArrayList} with the specified
   * object. Internally the <code>hashValue</code> will be reset to <code>0</code>.
   *
   * @param location the index at which to put the specified object.
   * @param object the object to add.
   * @return the previous element at the index.
   * @throws IndexOutOfBoundsException when {@code location < 0 || >= size()}
   */
  @Override
  public IExpr set(int location, IExpr object) {
    hashValue = 0;
    if (object instanceof ASTRealVector) {
      IExpr value = new ASTRealVector(matrix.getRowVector(location - 1), false);
      matrix.setRowVector(location - 1, ((ASTRealVector) object).vector);
      return value;
    }
    throw new IndexOutOfBoundsException(
        "Index: " + Integer.valueOf(location) + ", Size: " + (matrix.getRowDimension() + 1));
  }

  @Override
  public IASTMutable setAtCopy(int i, IExpr expr) {
    if (expr instanceof ASTRealVector) {
      IASTMutable ast = copy();
      ast.set(i, expr);
      return ast;
    }
    IASTAppendable ast = copyAppendable();
    ast.set(i, expr);
    return ast;
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
    final int rowDimension = matrix.getRowDimension();
    IExpr[] result = new IExpr[rowDimension + 1];
    result[0] = S.List;
    for (int i = 0; i < rowDimension; i++) {
      result[i + 1] = new ASTRealVector(matrix.getRowVector(i), false);
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
      LOGGER.debug("ASTRealMatrix.toString() failed", e);
    }
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);
    objectOutput.writeObject(matrix);
  }
}
