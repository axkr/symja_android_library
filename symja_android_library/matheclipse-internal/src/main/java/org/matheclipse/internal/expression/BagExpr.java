package org.matheclipse.internal.expression;

import java.util.ArrayList;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * An expression bag <code>Internal`Bag[...]</code>: a list which <code>Internal`StuffBag</code>
 * appends to in place. Two bags are the same expression only if they are the same object, and a
 * bag assigned to two variables is shared by both.
 */
public class BagExpr extends DataExpr<ArrayList<IExpr>> {
  private static final long serialVersionUID = -2410658384713417806L;

  /**
   * @param head the symbol <code>Internal`Bag</code>
   * @param elements the initial elements; the bag owns the list from now on
   */
  public BagExpr(IBuiltInSymbol head, ArrayList<IExpr> elements) {
    super(head, elements);
  }

  /** The number of elements in the bag. */
  public int length() {
    return fData.size();
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr instanceof BagExpr) {
      return Integer.compare(System.identityHashCode(this), System.identityHashCode(expr));
    }
    return super.compareTo(expr);
  }

  /** Identity, not contents: a bag is a container which changes in place. */
  @Override
  public boolean equals(final Object obj) {
    return this == obj;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public String fullFormString() {
    return toString();
  }

  @Override
  public String toString() {
    return fHead.fullFormString() + "[<" + fData.size() + ">]";
  }
}
