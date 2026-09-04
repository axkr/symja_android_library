package org.matheclipse.core.expression.data;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A symbolic matrix variable, the result of evaluating
 * <a href="https://reference.wolfram.com/language/ref/MatrixSymbol.html">MatrixSymbol</a>.
 *
 * <p>
 * It always has rank <code>2</code>. Its two dimensions may be positive integers or symbolic
 * expressions; a declared symmetry requires them to be structurally equal.
 * </p>
 */
public class MatrixSymbolExpr extends AbstractArraySymbolExpr {

  private static final long serialVersionUID = 1L;

  /** Constructor for {@link java.io.Externalizable}. */
  public MatrixSymbolExpr() {
    super(S.MatrixSymbol);
  }

  /**
   * @param name the name of the matrix (e.g. the string "a" or a symbol)
   * @param dimensions the dimensions list <code>{m, n}</code>
   * @param domain the domain of the elements (e.g. {@link S#Reals}, {@link S#Complexes})
   * @param symmetry the symmetry specification (e.g. <code>Symmetric({1,2})</code>) or
   *        {@link S#None}
   */
  public MatrixSymbolExpr(IExpr name, IAST dimensions, IExpr domain, IExpr symmetry) {
    super(S.MatrixSymbol, name, dimensions, domain, symmetry);
  }

  @Override
  public IExpr copy() {
    return new MatrixSymbolExpr(fName, fDimensions, fDomain, fSymmetry);
  }

  @Override
  public int hierarchy() {
    return MATRIXSYMBOLID;
  }

  /** {@inheritDoc} A matrix always has rank <code>2</code>. */
  @Override
  public int rank() {
    return 2;
  }
}
