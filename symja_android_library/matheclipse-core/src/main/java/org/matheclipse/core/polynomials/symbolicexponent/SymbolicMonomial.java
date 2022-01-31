package org.matheclipse.core.polynomials.symbolicexponent;

import java.util.Map;
import org.matheclipse.core.interfaces.IExpr;

/** Monomial class. Represents pairs of exponent vectors and coefficients. Adaptor for Map.Entry. */
public final class SymbolicMonomial implements Comparable<SymbolicMonomial> {

  /** Exponent of monomial. */
  public final ExpVectorSymbolic e;

  /** Coefficient of monomial. */
  public final IExpr c;

  /**
   * Constructor of monomial.
   *
   * @param e exponent.
   * @param c coefficient.
   */
  public SymbolicMonomial(ExpVectorSymbolic e, IExpr c) {
    this.e = e;
    this.c = c;
  }

  /**
   * Constructor of monomial.
   *
   * @param me a MapEntry.
   */
  public SymbolicMonomial(Map.Entry<ExpVectorSymbolic, IExpr> me) {
    this(me.getKey(), me.getValue());
  }

  /**
   * Getter for coefficient.
   *
   * @return coefficient.
   */
  public IExpr coefficient() {
    return c;
  }

  @Override
  public int compareTo(SymbolicMonomial S) {
    if (S == null) {
      return 1;
    }
    int s = e.compareTo(S.e);
    if (s != 0) {
      return s;
    }
    return c.compareTo(S.c);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SymbolicMonomial other = (SymbolicMonomial) obj;
    if (c == null) {
      if (other.c != null)
        return false;
    } else if (!c.equals(other.c))
      return false;
    if (e == null) {
      if (other.e != null)
        return false;
    } else if (!e.equals(other.e))
      return false;
    return true;
  }

  /**
   * Getter for exponent.
   *
   * @return exponent.
   */
  public ExpVectorSymbolic exponent() {
    return e;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((c == null) ? 0 : c.hashCode());
    result = prime * result + ((e == null) ? 0 : e.hashCode());
    return result;
  }

  /**
   * String representation of Monomial.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return c.toString() + " " + e.toString();
  }
}
