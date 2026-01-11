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
 * A data expression representing a symbolic matrix variable.
 */
public class MatrixSymbolExpr extends DataExpr<Object> implements IArraySymbol {

  /**
   * The unique serial version UID.
   */
  private static final long serialVersionUID = 1L;

  private final IExpr fName;
  private final IAST fDimensions;
  private final IExpr fDomain;
  private final IExpr fSymmetry;

  /**
   * @param name the name of the matrix (e.g., "a" or Symbol "x")
   * @param dimensions the dimensions list (e.g., {m, n})
   * @param domain the domain (e.g., Reals, Complexes)
   * @param symmetry the symmetry specification (e.g., Symmetric[{1,2}])
   */
  public MatrixSymbolExpr(IExpr name, IAST dimensions, IExpr domain, IExpr symmetry) {
    super(S.MatrixSymbol, null); // No generic backing object needed, we store fields directly
    this.fName = name;
    this.fDimensions = dimensions;
    this.fDomain = domain;
    this.fSymmetry = symmetry;
  }

  @Override
  public IExpr copy() {
    // shallow copy
    return new MatrixSymbolExpr(fName, fDimensions, fDomain, fSymmetry);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof MatrixSymbolExpr) {
      MatrixSymbolExpr other = (MatrixSymbolExpr) obj;
      return fName.equals(other.fName) && fDimensions.equals(other.fDimensions)
          && fDomain.equals(other.fDomain) && fSymmetry.equals(other.fSymmetry);
    }
    return false;
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    return F.NIL;
  }

  /**
   * Returns the dimensions list.
   *
   * @return an IAST representing the dimensions (e.g., {2, 2})
   */
  @Override
  public IAST getDimensions() {
    return fDimensions;
  }

  /**
   * Returns the domain of the matrix elements.
   *
   * @return the domain expression
   */
  public IExpr getDomain() {
    return fDomain;
  }

  /**
   * Returns the name expression of the MatrixSymbol.
   *
   * @return the name expression
   */
  public IExpr getName() {
    return fName;
  }

  /**
   * Returns the symmetry specification.
   *
   * @return the symmetry expression
   */
  @Override
  public IAST getSymmetry() {
    if (fSymmetry.isNone()) {
      return F.CEmptyList;
    }
    if (fSymmetry.isAST()) {
      return (IAST) fSymmetry;
    }
    return F.NIL;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 37 * result + fName.hashCode();
    result = 37 * result + fDimensions.hashCode();
    result = 37 * result + fDomain.hashCode();
    result = 37 * result + fSymmetry.hashCode();
    return result;
  }

  @Override
  public int hierarchy() {
    return MARIXSYMBOLID;
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
    // Convert the data object back to a standard AST: MatrixSymbol[name, dims, domain, symmetry]
    IASTAppendable appendable = F.ast(S.MatrixSymbol);
    appendable.append(fName);
    appendable.append(fDimensions);
    // Only append domain/symmetry if necessary or always append for full reconstruction
    if (!fDomain.equals(S.Reals) || !fSymmetry.equals(S.None)) {
      appendable.append(fDomain);
      if (!fSymmetry.equals(S.None)) {
        appendable.append(fSymmetry);
      }
    }
    return appendable;
  }

  @Override
  public String fullFormString() {
    return normal(false).fullFormString();
  }

  @Override
  public String toString() {
    // Returns the standard form: MatrixSymbol[name, {dims}, domain, symmetry]
    // We omit trailing optional arguments if they are defaults to keep it clean,
    // mimicking standard behavior if desired, or output full form.
    // Here we output the full construction for clarity.
    StringBuilder sb = new StringBuilder();
    sb.append("MatrixSymbol(");
    sb.append(fName.toString());
    sb.append(", ");
    sb.append(fDimensions.toString());
    if (!fDomain.equals(S.Reals) || !fSymmetry.equals(S.None)) {
      sb.append(", ");
      sb.append(fDomain.toString());
      if (!fSymmetry.equals(S.None)) {
        sb.append(", ");
        sb.append(fSymmetry.toString());
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
