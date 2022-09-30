package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A pair of values extnding the {@link IAST} interface. Create pairs with
 * {@link F#pair(IExpr, IExpr)} method.
 *
 */
public class Pair extends B2 {

  public Pair() {
    super();
  }

  Pair(IExpr arg1, IExpr arg2) {
    super(arg1, arg2);
  }

  @Override
  public final IBuiltInSymbol head() {
    return S.List;
  }

  @Override
  public final IBuiltInSymbol topHead() {
    return S.List;
  }

  @Override
  public IASTMutable copy() {
    return new Pair(arg1, arg2);
  }
}
