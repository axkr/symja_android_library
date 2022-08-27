// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.awt.image.BufferedImage;

/* package */ class StaticHelper {

  /**
   * @param type
   * @return either byte_gray or int_argb
   */
  public static int type(int type) {
    return type == BufferedImage.TYPE_BYTE_GRAY //
        ? BufferedImage.TYPE_BYTE_GRAY
        : BufferedImage.TYPE_INT_ARGB;
  }
}
