// code by jph
package org.matheclipse.core.tensor.sca;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISignedNumber;

/** clip to a single point
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class ClipPoint extends ClipInterval {
  public ClipPoint(ISignedNumber value, ISignedNumber width) {
    super(value, value, width);
  }

  @Override // from ClipInterval
  public ISignedNumber rescale(ISignedNumber scalar) {
    apply(scalar);
    return F.C0;
  }
}
