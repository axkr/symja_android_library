// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.gfx;

import java.awt.geom.AffineTransform;
import org.matheclipse.core.interfaces.IAST;

public class AffineTransforms {

  /**
   * function helps to draw a transformed BufferedImage in a Graphics object
   * 
   * @param matrix 3 x 3 in SE2
   * @return java::AffineTransform
   * @see GfxMatrix
   */
  public static AffineTransform of(IAST matrix) {
    return new AffineTransform( //
        matrix.getPart(1, 1).evalDouble(), //
        matrix.getPart(2, 1).evalDouble(), //
        matrix.getPart(1, 2).evalDouble(), //
        matrix.getPart(2, 2).evalDouble(), //
        matrix.getPart(1, 3).evalDouble(), //
        matrix.getPart(2, 3).evalDouble());
  }
}
