package org.matheclipse.core.expression.data;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A symbolic vector variable, the result of evaluating
 * <a href="https://reference.wolfram.com/language/ref/VectorSymbol.html">VectorSymbol</a>.
 *
 * <p>
 * It always has rank <code>1</code> and carries no symmetry. Unlike a matrix or an array it is
 * constructed from and printed with a scalar dimension: <code>VectorSymbol(v, n)</code>, not
 * <code>VectorSymbol(v, {n})</code>.
 * </p>
 */
public class VectorSymbolExpr extends AbstractArraySymbolExpr {

  private static final long serialVersionUID = 1L;

  /** Constructor for {@link java.io.Externalizable}. */
  public VectorSymbolExpr() {
    super(S.VectorSymbol);
  }

  /**
   * @param name the name of the vector (e.g. the string "v" or a symbol)
   * @param dimension the length of the vector (e.g. the integer <code>3</code> or a symbol)
   * @param domain the domain of the elements (e.g. {@link S#Reals}, {@link S#Complexes})
   */
  public VectorSymbolExpr(IExpr name, IExpr dimension, IExpr domain) {
    super(S.VectorSymbol, name, F.list(dimension), domain, S.None);
  }

  @Override
  public IExpr copy() {
    return new VectorSymbolExpr(fName, fDimensions.arg1(), fDomain);
  }

  /** {@inheritDoc} A vector prints its single dimension as a scalar. */
  @Override
  protected IExpr dimensionsArgument() {
    return fDimensions.arg1();
  }

  /**
   * The single dimension, i.e. the length of this vector.
   *
   * @return the length expression
   */
  public IExpr getDimension() {
    return fDimensions.arg1();
  }

  @Override
  public int hierarchy() {
    return VECTORSYMBOLID;
  }

  /** {@inheritDoc} A vector is never a square matrix. */
  @Override
  public boolean isSquareMatrix() {
    return false;
  }

  /** {@inheritDoc} A vector always has rank <code>1</code>. */
  @Override
  public int rank() {
    return 1;
  }
}
