package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.S;

/**
 * A symbolic array variable of known rank and (possibly symbolic) dimensions.
 *
 * <p>
 * Implemented by the data expressions which
 * <a href="https://reference.wolfram.com/language/ref/VectorSymbol.html">VectorSymbol</a>,
 * <a href="https://reference.wolfram.com/language/ref/MatrixSymbol.html">MatrixSymbol</a> and
 * <a href="https://reference.wolfram.com/language/ref/ArraySymbol.html">ArraySymbol</a> evaluate to.
 * Such an object stands for a whole vector, matrix or array without naming its components, so it is
 * a non-scalar variable: it never combines with an explicit {@link S#List} in arithmetic (see
 * {@link ISymbol#NONTHREADABLE}), and functions like {@link S#Dot} or {@link S#D} treat it as an
 * array of the {@link #getDimensions()} it declares.
 */
public interface IArraySymbol extends IExpr {

  /**
   * The dimensions of this array. Each element is a positive integer or a symbolic expression.
   *
   * @return a {@link S#List} of dimensions, for example <code>{m,n}</code>
   */
  public IAST getDimensions();

  /**
   * The domain the elements of this array are known to be in.
   *
   * @return {@link S#Complexes}, {@link S#Integers}, {@link S#Reals}, {@link S#NonNegativeReals} or
   *         {@link S#PositiveReals}
   */
  public IExpr getDomain();

  /**
   * The name of this array symbol. May be any expression, typically a {@link IStringX} or an
   * {@link ISymbol}.
   *
   * @return the name expression
   */
  public IExpr getName();

  /**
   * The symmetry of this array under permutation of its slots.
   *
   * @return {@link S#None} if no symmetry was declared, otherwise a symmetry specification like
   *         <code>Symmetric({1,2})</code> or <code>Antisymmetric({1,2})</code>
   */
  public IExpr getSymmetry();

  /**
   * Test if the elements of this array are known to be real valued. Only then may
   * {@link S#Conjugate} be dropped from this array.
   *
   * @return <code>true</code> if the declared domain is a subset of {@link S#Reals}
   */
  default boolean hasRealDomain() {
    IExpr domain = getDomain();
    return domain == S.Reals || domain == S.Integers || domain == S.NonNegativeReals
        || domain == S.PositiveReals;
  }

  /**
   * Test if this array is a square matrix, i.e. has rank <code>2</code> and two structurally equal
   * dimensions.
   *
   * @return <code>true</code> if this is a square matrix
   */
  default boolean isSquareMatrix() {
    IAST dimensions = getDimensions();
    return dimensions.argSize() == 2 && dimensions.arg1().equals(dimensions.arg2());
  }

  /**
   * The rank (or depth) of this array. A vector has rank <code>1</code>, a matrix rank
   * <code>2</code>.
   *
   * @return the number of dimensions of this array
   */
  default int rank() {
    return getDimensions().argSize();
  }
}
