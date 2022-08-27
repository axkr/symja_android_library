// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;

/* package */ class Transparent {

  private static final IAST RGBA = F.List(0.0, 0.0, 0.0, 0.0);

  /** @return {0.0, 0.0, 0.0, 0.0} */
  public static IAST rgba() {
    return RGBA.copy();
  }
}
