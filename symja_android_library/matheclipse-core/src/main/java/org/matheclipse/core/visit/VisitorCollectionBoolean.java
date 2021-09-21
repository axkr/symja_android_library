package org.matheclipse.core.visit;

import java.util.Collection;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public abstract class VisitorCollectionBoolean<T extends IExpr> extends AbstractVisitorBoolean {
  protected int fHeadOffset;

  protected Collection<T> fCollection;

  public VisitorCollectionBoolean(Collection<T> collection) {
    super();
    fHeadOffset = 1;
    fCollection = collection;
  }

  public VisitorCollectionBoolean(int hOffset, Collection<T> collection) {
    super();
    fHeadOffset = hOffset;
    fCollection = collection;
  }

  @Override
  public boolean visit(IAST list) {
    list.forEach(fHeadOffset, list.size(), x -> x.accept(this));
    return false;
  }
}
