package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * The ExprID class holds an index for the <code>F.GLOBAL_IDS[]</code> array of predefined constant
 * expressions. The ExprID is especially used in the serialization and deserialization of an <code>
 * IExpr</code> object, by representing an index entry in the <code>F.GLOBAL_IDS[]</code> array.
 *
 * @see F#GLOBAL_IDS
 */
/* private package */ class ExprID implements IExpr {

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + fExprID;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ExprID other = (ExprID) obj;
    if (fExprID != other.fExprID)
      return false;
    return true;
  }

  /** */
  private static final long serialVersionUID = -2183178872222820512L;

  private short fExprID;

  /**
   * The ExprID class holds an index for the <code>F.GLOBAL_IDS[]</code> array of predefined
   * constant expressions. The ExprID is especially used in the serialization and deserialization of
   * an <code>IExpr</code> object, by representing an index entry in the <code>F.GLOBAL_IDS[]</code>
   * array.
   *
   * @param exprID the index in array <code>F.GLOBAL_IDS</code>
   * @see F#GLOBAL_IDS
   */
  public ExprID(short exprID) {
    fExprID = exprID;
  }

  @Override
  public IExpr accept(IVisitor visitor) {
    return null;
  }

  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return false;
  }

  @Override
  public int accept(IVisitorInt visitor) {
    return 0;
  }

  @Override
  public long accept(IVisitorLong visitor) {
    return 0;
  }

  @Override
  public IExpr copy() {
    return this;
  }

  /**
   * Get the index for the <code>F.GLOBAL_IDS[]</code> array of predefined constant expressions.
   *
   * @see F#GLOBAL_IDS
   */
  public short getExprID() {
    return fExprID;
  }

  @Override
  public ISymbol head() {
    return null;
  }

  @Override
  public int hierarchy() {
    return 0;
  }

  /**
   * In the deserialization process return the <code>F.GLOBAL_IDS[ExprID]</code> expression.
   *
   * @return the <code>F.GLOBAL_IDS[ExprID]</code> expression
   */
  public Object readResolve() {
    return S.exprID(fExprID); // F.GLOBAL_IDS[fExprID];
  }
}
