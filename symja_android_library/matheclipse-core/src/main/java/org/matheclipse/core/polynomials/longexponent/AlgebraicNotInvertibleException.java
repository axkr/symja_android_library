/*
 * $Id: AlgebraicNotInvertibleException.java 3472 2011-01-07 17:19:22Z kredel $
 */

package org.matheclipse.core.polynomials.longexponent;

import edu.jas.structure.NotInvertibleException;

/**
 * Algebraic number NotInvertibleException class. Runtime Exception to be thrown for not invertible
 * algebraic numbers. Container for the non-trivial factors found by the inversion algorithm.
 * <b>Note: </b> cannot be generic because of Throwable.
 */
public class AlgebraicNotInvertibleException extends NotInvertibleException {

  /** */
  private static final long serialVersionUID = 4734826103269124125L;

  public final ExprPolynomial f; // = f1 * f2

  public final ExprPolynomial f1;

  public final ExprPolynomial f2;

  public AlgebraicNotInvertibleException() {
    this(null, null, null);
  }

  public AlgebraicNotInvertibleException(String c) {
    this(c, null, null, null);
  }

  public AlgebraicNotInvertibleException(String c, Throwable t) {
    this(c, t, null, null, null);
  }

  public AlgebraicNotInvertibleException(Throwable t) {
    this(t, null, null, null);
  }

  /**
   * Constructor.
   *
   * @param f polynomial with f = f1 * f2.
   * @param f1 polynomial.
   * @param f2 polynomial.
   */
  public AlgebraicNotInvertibleException(ExprPolynomial f, ExprPolynomial f1, ExprPolynomial f2) {
    super("AlgebraicNotInvertibleException");
    this.f = f;
    this.f1 = f1;
    this.f2 = f2;
  }

  /**
   * Constructor.
   *
   * @param f polynomial with f = f1 * f2.
   * @param f1 polynomial.
   * @param f2 polynomial.
   */
  public AlgebraicNotInvertibleException(String c, ExprPolynomial f, ExprPolynomial f1,
      ExprPolynomial f2) {
    super(c);
    this.f = f;
    this.f1 = f1;
    this.f2 = f2;
  }

  /**
   * Constructor.
   *
   * @param f polynomial with f = f1 * f2.
   * @param f1 polynomial.
   * @param f2 polynomial.
   */
  public AlgebraicNotInvertibleException(String c, Throwable t, ExprPolynomial f, ExprPolynomial f1,
      ExprPolynomial f2) {
    super(c, t);
    this.f = f;
    this.f1 = f1;
    this.f2 = f2;
  }

  /**
   * Constructor.
   *
   * @param f polynomial with f = f1 * f2.
   * @param f1 polynomial.
   * @param f2 polynomial.
   */
  public AlgebraicNotInvertibleException(Throwable t, ExprPolynomial f, ExprPolynomial f1,
      ExprPolynomial f2) {
    super("AlgebraicNotInvertibleException", t);
    this.f = f;
    this.f1 = f1;
    this.f2 = f2;
  }

  /**
   * Get the String representation.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder(super.toString());
    if (f != null || f1 != null || f2 != null) {
      s.append(", f = ").append(f).append(", f1 = ").append(f1).append(", f2 = ").append(f2);
    }
    return s.toString();
  }
}
