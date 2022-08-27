// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Suggested base class for implementations of {@link Interpolation}
 */
public abstract class AbstractInterpolation implements Interpolation {
  @Override // from Interpolation
  public final IExpr Get(IAST index) {
    return get(index);
  }

  @Override // from Interpolation
  public final IExpr At(IExpr index) {
    return at(index);
  }
}
