package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Base class of the three symbolic array variables {@link VectorSymbolExpr},
 * {@link MatrixSymbolExpr} and {@link ArraySymbolExpr}.
 *
 * <p>
 * All three carry the same state - a name, a list of (possibly symbolic) dimensions, an element
 * domain and a symmetry - and differ only in their head, their rank and in how the dimensions are
 * written back into the {@link #normal(boolean)} form: a vector prints its single dimension as a
 * scalar, a matrix and an array print a list.
 * </p>
 *
 * <p>
 * The default element domain is {@link S#Complexes}, as in the Wolfram language, and it is omitted
 * from the printed form. A domain has to be printed as soon as a symmetry follows it, because the
 * arguments are positional.
 * </p>
 */
public abstract class AbstractArraySymbolExpr extends DataExpr<Object>
    implements IArraySymbol, Externalizable {

  private static final long serialVersionUID = 8752452054104420937L;

  /** The name of this array symbol; may be any expression. */
  protected IExpr fName;

  /** The dimensions of this array symbol; always a {@link S#List}. */
  protected IAST fDimensions;

  /** The domain of the elements of this array symbol. */
  protected IExpr fDomain;

  /** The symmetry of this array symbol, or {@link S#None}. */
  protected IExpr fSymmetry;

  /** Constructor for {@link Externalizable}. */
  protected AbstractArraySymbolExpr(IBuiltInSymbol head) {
    super(head, null);
    this.fName = S.None;
    this.fDimensions = F.CEmptyList;
    this.fDomain = S.Complexes;
    this.fSymmetry = S.None;
  }

  protected AbstractArraySymbolExpr(IBuiltInSymbol head, IExpr name, IAST dimensions, IExpr domain,
      IExpr symmetry) {
    super(head, null);
    this.fName = name;
    this.fDimensions = dimensions;
    this.fDomain = domain;
    this.fSymmetry = symmetry;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof AbstractArraySymbolExpr) {
      AbstractArraySymbolExpr other = (AbstractArraySymbolExpr) expr;
      int result = hierarchy() - other.hierarchy();
      if (result != 0) {
        return result < 0 ? -1 : 1;
      }
      result = fName.compareTo(other.fName);
      if (result != 0) {
        return result;
      }
      result = fDimensions.compareTo(other.fDimensions);
      if (result != 0) {
        return result;
      }
      result = fDomain.compareTo(other.fDomain);
      if (result != 0) {
        return result;
      }
      return fSymmetry.compareTo(other.fSymmetry);
    }
    // DataExpr#compareTo() cannot order two data expressions which carry no data object; it would
    // answer -1 in both directions and break the canonical ordering of S.Orderless expressions
    if (expr.isAST()) {
      return -1 * expr.compareTo(this);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj != null && getClass() == obj.getClass()) {
      AbstractArraySymbolExpr other = (AbstractArraySymbolExpr) obj;
      return fName.equals(other.fName) && fDimensions.equals(other.fDimensions)
          && fDomain.equals(other.fDomain) && fSymmetry.equals(other.fSymmetry);
    }
    return false;
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IAST fullForm() {
    return normal(false);
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return normal(false).fullFormString();
  }

  /** {@inheritDoc} */
  @Override
  public IAST getDimensions() {
    return fDimensions;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getDomain() {
    return fDomain;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getName() {
    return fName;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getSymmetry() {
    return fSymmetry;
  }

  @Override
  public int hashCode() {
    int result = 17 + hierarchy();
    result = 37 * result + fName.hashCode();
    result = 37 * result + fDimensions.hashCode();
    result = 37 * result + fDomain.hashCode();
    result = 37 * result + fSymmetry.hashCode();
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    return normal(false).internalFormString(symbolsAsFactoryMethod, depth).toString();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isVariable(boolean polynomialQTest) {
    return true;
  }

  /**
   * The second argument of the {@link #normal(boolean)} form. A vector writes its single dimension
   * as a scalar, a matrix and an array write the dimensions list.
   */
  protected IExpr dimensionsArgument() {
    return fDimensions;
  }

  /** {@inheritDoc} */
  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    IASTAppendable result = F.ast(fHead, 4);
    result.append(fName);
    result.append(dimensionsArgument());
    // S.Complexes is the default domain and is omitted, but a symmetry can only follow a domain
    if (fDomain != S.Complexes || fSymmetry != S.None) {
      result.append(fDomain);
      if (fSymmetry != S.None) {
        result.append(fSymmetry);
      }
    }
    return result;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fName = (IExpr) in.readObject();
    fDimensions = (IAST) in.readObject();
    fDomain = (IExpr) in.readObject();
    fSymmetry = (IExpr) in.readObject();
  }

  @Override
  public String toString() {
    return normal(false).toString();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fName);
    output.writeObject(fDimensions);
    output.writeObject(fDomain);
    output.writeObject(fSymmetry);
  }
}
