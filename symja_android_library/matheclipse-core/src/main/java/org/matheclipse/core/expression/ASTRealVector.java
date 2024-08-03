package org.matheclipse.core.expression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;
import java.util.RandomAccess;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import org.hipparchus.complex.Complex;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAtomicEvaluate;
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
public class ASTRealVector extends AbstractAST
    implements Externalizable, RandomAccess, IAtomicEvaluate {

  public ASTRealVector() {
    // When Externalizable objects are deserialized, they first need to be constructed by invoking
    // the void
    // constructor. Since this class does not have one, serialization and deserialization will fail
    // at runtime.
    setEvalFlags(IAST.CONTAINS_NUMERIC_ARG);
  }

  /**
   * Returns a new ASTRealVector where each element is mapped by the given function.
   *
   * @param astVector an AST which could be converted into <code>double[]</code>
   * @param function Function to apply to each entry.
   * @return a new vector.
   */
  public static ASTRealVector map(final IAST astVector, DoubleUnaryOperator function) {
    double[] vector = astVector.toDoubleVector();
    for (int i = 0; i < vector.length; i++) {
      vector[i] = function.applyAsDouble(vector[i]);
    }
    return new ASTRealVector(vector, false);
  }

  /** The underlying vector */
  RealVector vector;

  /**
   * @param vector
   * @param deepCopy if <code>true</code> allocate new memory and copy all elements from the vector
   */
  public ASTRealVector(double[] vector, boolean deepCopy) {
    if (Config.MAX_AST_SIZE < vector.length) {
      throw new ASTElementLimitExceeded(vector.length);
    }
    setEvalFlags(IAST.CONTAINS_NUMERIC_ARG);
    this.vector = new ArrayRealVector(vector, deepCopy);
  }

  /**
   * @param vector the vector which should be wrapped in this object.
   * @param deepCopy if <code>true</code> allocate new memory and copy all elements from the vector
   */
  public ASTRealVector(RealVector vector, boolean deepCopy) {
    if (Config.MAX_AST_SIZE < vector.getDimension()) {
      throw new ASTElementLimitExceeded(vector.getDimension());
    }
    setEvalFlags(IAST.CONTAINS_NUMERIC_ARG);
    if (deepCopy) {
      this.vector = vector.copy();
    } else {
      this.vector = vector;
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
    return F.num(vector.getEntry(0));
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
    return F.num(vector.getEntry(1));
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
    return F.num(vector.getEntry(2));
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
    return F.num(vector.getEntry(3));
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
    return F.num(vector.getEntry(4));
  }

  /** {@inheritDoc} */
  @Override
  public int argSize() {
    return vector.getDimension();
  }

  @Override
  public SortedSet<IExpr> asSortedSet(Comparator<? super IExpr> comparator) {
    int size = size();
    SortedSet<IExpr> set = new TreeSet<>(comparator);
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
  public ASTRealVector clone() {
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
  public ASTRealVector copy() {
    return new ASTRealVector(vector.copy(), false);
  }

  @Override
  public IASTAppendable copyAppendable() {
    return Convert.vector2List(vector, false);
  }

  @Override
  public IASTAppendable copyAppendable(int additionalCapacity) {
    return copyAppendable();
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
    // if ((getEvalFlags() & IAST.DEFER_AST) == IAST.DEFER_AST) {
    // return F.NIL;
    // }
    if (engine.isNumericMode() && engine.isArbitraryMode()) {
      IASTAppendable result = F.ListAlloc(vector.getDimension());
      for (int i = 0; i < vector.getDimension(); i++) {
        result.append(ApfloatNum.valueOf(vector.getEntry(i)));
      }
      return result;
    }
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
  public final String fullFormString() {
    return fullFormString(S.List);
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
    return (location == 0) ? head() : F.num(vector.getEntry(location - 1));
  }

  @Override
  public IAST getItems(int[] items, int length, int offset) {
    double[] v = new double[length];
    for (int i = 0; i < length; i++) {
      v[i] = vector.getEntry(items[i] + offset - 1);
    }
    return new ASTRealVector(v, false);
  }

  public double getEntry(int location) {
    return vector.getEntry(location - 1);
  }

  public RealVector getRealVector() {
    return vector;
  }

  @Override
  public int hashCode() {
    if (hashValue == 0 && vector != null) {
      hashValue = vector.hashCode();
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
    return size() == 1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST1() {
    return size() == 2;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST2() {
    return size() == 3;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAST3() {
    return size() == 4;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isList() {
    return true;
  }

  @Override
  public boolean isNaN() {
    return vector.isNaN();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRealVector() {
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
    return S.List == head && vector.getDimension() == length - 1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHead(ISymbol head, int minLength, int maxLength) {
    int size = vector.getDimension() + 1;
    return S.List == head && minLength <= size && maxLength >= size;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameHeadSizeGE(ISymbol head, int length) {
    return S.List == head && length <= vector.getDimension() + 1;
  }

  /** {@inheritDoc} */
  @Override
  public final int isVector() {
    return vector.getDimension();
  }

  /** {@inheritDoc} */
  @Override
  public final int isInexactVector() {
    return vector.getDimension();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericAST() {
    return true;
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

  /**
   * Append a String composed of the elements of this vector joined together with the specified
   * {@code delimiter}.
   *
   * @param builder join the elements as strings
   * @param delimiter the delimiter that separates each element
   */
  @Override
  public void joinToString(StringBuilder builder, CharSequence delimiter) {
    Convert.joinToString(vector.toArray(), builder, delimiter);
  }

  @Override
  public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
    this.fEvalFlags = objectInput.readShort();
    this.vector = (RealVector) objectInput.readObject();
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
    if (object instanceof Num) {
      double value = vector.getEntry(location - 1);
      vector.setEntry(location - 1, ((Num) object).reDoubleValue());
      return F.num(value);
    }
    throw new IndexOutOfBoundsException(
        "Index: " + Integer.valueOf(location) + ", Size: " + (vector.getDimension() + 1));
  }

  @Override
  public IASTMutable setAtCopy(int i, IExpr expr) {
    if (expr instanceof Num) {
      IASTMutable ast = copy();
      ast.set(i, expr);
      return ast;
    }
    IASTAppendable ast = copyAppendable();
    ast.set(i, expr);
    return ast;
  }

  public void setEntry(int location, double value) {
    hashValue = 0;
    vector.setEntry(location - 1, value);
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

  public ASTRealVector subtract(ASTRealVector that) {
    return new ASTRealVector(vector.subtract(that.vector), false);
  }

  /**
   * Returns a new array containing all elements contained in this {@code ArrayList}.
   *
   * @return an array of the elements from this {@code ArrayList}
   */
  @Override
  public IExpr[] toArray() {
    final int dimension = vector.getDimension();
    IExpr[] result = new IExpr[dimension + 1];
    result[0] = S.List;
    for (int i = 0; i < dimension; i++) {
      result[i + 1] = F.num(vector.getEntry(i));
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public double[] toDoubleVector() {
    return vector.toArray();
  }

  @Override
  public Complex[] toComplexVector() {
    double[] array = vector.toArray();
    final int size = array.length;
    Complex[] result = new Complex[size];
    for (int i = 0; i < size; i++) {
      result[i] = Complex.valueOf(array[i]);
    }
    return result;
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
      // `1`.
      Errors.printMessage(S.List, "error", F.List("IOException in ASTRealVector#toString()"));
    }
  }

  @Override
  public void writeExternal(ObjectOutput objectOutput) throws IOException {
    objectOutput.writeShort(fEvalFlags);
    objectOutput.writeObject(vector);
  }
}
