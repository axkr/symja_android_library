package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;

public interface ITensorAccess extends IExpr {

  /**
   * Returns the element at the specified location in this {@link ISparseArray} or {@code IAST}. If
   * this is an {@link IAssociation} return the value of the rule at the specified location.
   *
   * @param location the index of the element to return.
   * @return the element at the specified location.
   * @throws IndexOutOfBoundsException if {@code location < 0 || >= size()}
   */
  public IExpr get(int position);

  /**
   * Low level access. It is assumed that <code>positions</code> is a full index within the
   * dimensions of this {@link ISparseArray} or {@link IAST} (vector/matrix/tensor) object.
   *
   * @param positions full index within the dimensions of this object
   * @return the expression at the given index or {@link F#NIL}
   */
  public IExpr getIndex(int... positions);

}
