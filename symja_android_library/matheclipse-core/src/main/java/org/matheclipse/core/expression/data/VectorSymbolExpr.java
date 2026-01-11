package org.matheclipse.core.expression.data;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A data expression representing a symbolic vector variable.
 */
public class VectorSymbolExpr extends DataExpr<Object> implements IArraySymbol {

  /**
   * The unique serial version UID.
   */
  private static final long serialVersionUID = 1L;

  private final IExpr fName;
  private final IExpr fDimension;
  private final IExpr fDomain;

  /**
   * @param name the name of the vector (e.g., "v" or Symbol "x")
   * @param dimension the dimension length (e.g., Integer 3 or Symbol n)
   * @param domain the domain (e.g., Reals, Complexes)
   */
  public VectorSymbolExpr(IExpr name, IExpr dimension, IExpr domain) {
    super(S.VectorSymbol, null);
    this.fName = name;
    this.fDimension = dimension;
    this.fDomain = domain;
  }

  @Override
  public IExpr copy() {
    // shallow copy
    return new VectorSymbolExpr(fName, fDimension, fDomain);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof VectorSymbolExpr) {
      VectorSymbolExpr other = (VectorSymbolExpr) obj;
      return fName.equals(other.fName) && fDimension.equals(other.fDimension)
          && fDomain.equals(other.fDomain);
    }
    return false;
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    return F.NIL;
  }

  @Override
  public String fullFormString() {
    return normal(false).fullFormString();
  }

  /**
   * Returns the dimension.
   *
   * @return an IExpr representing the dimension (e.g., 3 or n)
   */
  @Override
  public IAST getDimensions() {
    return F.List(fDimension);
  }

  /**
   * Returns the domain of the vector elements.
   *
   * @return the domain expression
   */
  public IExpr getDomain() {
    return fDomain;
  }

  public IAST getSymmetry() {
    return F.CEmptyList;
  }

  /**
   * Returns the name expression of the VectorSymbol.
   *
   * @return the name expression
   */
  public IExpr getName() {
    return fName;
  }

  @Override
  public int hashCode() {
    int result = 19;
    result = 37 * result + fName.hashCode();
    result = 37 * result + fDimension.hashCode();
    result = 37 * result + fDomain.hashCode();
    return result;
  }

  @Override
  public int hierarchy() {
    return VECTORSYMBOLID;
  }

  @Override
  public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    return toString();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isVariable(boolean polynomialQTest) {
    return true;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    // Convert back to standard AST: VectorSymbol[name, dim, domain]
    IASTAppendable appendable = F.ast(S.VectorSymbol);
    appendable.append(fName);
    appendable.append(fDimension);
    if (!fDomain.equals(S.Reals)) {
      appendable.append(fDomain);
    }
    return appendable;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("VectorSymbol(");
    sb.append(fName.toString());
    sb.append(", ");
    sb.append(fDimension.toString());
    if (!fDomain.equals(S.Reals)) {
      sb.append(", ");
      sb.append(fDomain.toString());
    }
    sb.append(")");
    return sb.toString();
  }
}
