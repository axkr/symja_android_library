package org.matheclipse.core.interfaces;

public interface ITernaryComparator {

  /**
   *
   *
   * <ul>
   * <li>Return <code>TRUE</code> if the comparison is <code>true</code>
   * <li>Return <code>FALSE</code> if the comparison is <code>false</code>
   * <li>Return <code>UNDECIDABLE</code> if the comparison to true or false is undecidable (i.e.
   * could not be evaluated)
   * </ul>
   *
   * @param arg1
   * @param arg2
   * @return
   */
  IExpr.COMPARE_TERNARY compareTernary(IExpr arg1, IExpr arg2);
}
