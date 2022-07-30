// code by jph
package org.matheclipse.core.tensor.qty;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.sca.Clip;

public enum QuantityUnit {
  ;
  /** @param scalar non-null
   * @return unit associated with the specified scalar
   * @throws Exception if given scalar is null */
  public static IUnit of(IExpr scalar) {
    if (scalar instanceof IQuantity) {
      return ((IQuantity) scalar).unit();
    }
    return IUnit.ONE;
  }

  /** @param clip
   * @return shared unit of clip.min and clip.max */
  public static IUnit of(Clip clip) {
    return of(clip.min());
  }
}
