package org.matheclipse.core.expression.data;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A symbolic array variable of arbitrary rank, the result of evaluating
 * <a href="https://reference.wolfram.com/language/ref/ArraySymbol.html">ArraySymbol</a>.
 *
 * <p>
 * Its rank is the length of its dimensions list. A declared symmetry names the slots which may be
 * permuted; those slots must have structurally equal dimensions.
 * </p>
 */
public class ArraySymbolExpr extends AbstractArraySymbolExpr {

  private static final long serialVersionUID = 1L;

  /** Constructor for {@link java.io.Externalizable}. */
  public ArraySymbolExpr() {
    super(S.ArraySymbol);
  }

  /**
   * @param name the name of the array (e.g. the string "a" or a symbol)
   * @param dimensions the dimensions list <code>{n1, n2, ...}</code>
   * @param domain the domain of the elements (e.g. {@link S#Reals}, {@link S#Complexes})
   * @param symmetry the symmetry specification (e.g. <code>Symmetric({1,2})</code>) or
   *        {@link S#None}
   */
  public ArraySymbolExpr(IExpr name, IAST dimensions, IExpr domain, IExpr symmetry) {
    super(S.ArraySymbol, name, dimensions, domain, symmetry);
  }

  @Override
  public IExpr copy() {
    return new ArraySymbolExpr(fName, fDimensions, fDomain, fSymmetry);
  }

  @Override
  public int hierarchy() {
    return ARRAYSYMBOLID;
  }
}
