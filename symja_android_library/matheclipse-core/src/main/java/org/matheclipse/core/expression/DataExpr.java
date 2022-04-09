package org.matheclipse.core.expression;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;

/**
 * A concrete IDataExpr implementation. Container for a header and data object.
 *
 * @see org.matheclipse.core.interfaces.IDataExpr
 */
public abstract class DataExpr<T> implements IDataExpr<T> {

  private static final long serialVersionUID = 4987827851920443376L;

  protected IBuiltInSymbol fHead;

  protected T fData;

  protected DataExpr(final IBuiltInSymbol head, final T data) {
    fHead = head;
    fData = data;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return 0L;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DataExpr) {
      return fData.equals(((DataExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof DataExpr) {
      DataExpr<T> de = ((DataExpr<T>) expr);
      if (fData != null) {
        if (de.fData != null) {
          if (fData.getClass() == de.fData.getClass()) {
            if (fData instanceof Comparable) {
              return ((Comparable<T>) fData).compareTo(de.fData);
            }
          }
          final int x = hierarchy();
          final int y = expr.hierarchy();
          return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
        return 1;
      }
      return -1;
    }
    if (expr.isAST()) {
      // if (expr.isDirectedInfinity()) {
      return -1 * expr.compareTo(this);
      // }
    }
    final int x = hierarchy();
    final int y = expr.hierarchy();
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fHead + "(" + fData.toString() + ")";
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 181 : 181 + fData.hashCode();
  }

  @Override
  public ISymbol head() {
    return fHead;
  }

  @Override
  public int hierarchy() {
    return DATAID;
  }

  @Override
  public T toData() {
    return fData;
  }

  @Override
  public String toString() {
    return fHead + "[" + fData.toString() + "]";
  }
}
