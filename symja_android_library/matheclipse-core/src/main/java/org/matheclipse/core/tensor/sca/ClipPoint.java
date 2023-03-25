// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.sca;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;

/**
 * clip to a single point
 * 
 * @implSpec This class is immutable and thread-safe.
 */
/* package */ class ClipPoint extends ClipInterval {

  public ClipPoint(IExpr value, IExpr width) {
    super(value, value, width);
  }

  @Override // from ClipInterval
  public IReal rescale(IExpr scalar) {
    apply(scalar);
    return F.C0;
  }
}
