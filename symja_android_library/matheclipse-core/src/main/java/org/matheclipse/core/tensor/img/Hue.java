// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.awt.Color;


/**
 * Standalone HSV to RGB conversion
 * 
 * an input scalar that does not satisfy {@link FiniteScalarQ} is mapped to a transparent color
 */
public class Hue {

  /**
   * when saturation is close or equal to zero, the rgb values equate to input val
   * 
   * @param hue is periodically mapped to [0, 1)
   * @param sat in [0, 1] as "saturation"
   * @param val in [0, 1] as "value"
   * @param alpha in [0, 1]
   * @return
   */
  public static Color of(double hue, double sat, double val, double alpha) {
    if (!Double.isFinite(hue))
      throw new IllegalArgumentException("hue=" + hue);
    final double r;
    final double g;
    final double b;
    hue %= 1;
    if (hue < 0)
      hue += 1;
    hue *= 6;
    int i = (int) hue; // if isNaN(h) then i == 0
    double f = hue - i;
    double aa = val * (1 - sat); // if s==0 then aa=v
    double bb = val * (1 - sat * f); // if s==0 then bb=v
    double cc = val * (1 - sat * (1 - f)); // if s==0 then cc=v
    switch (i) {
      case 0:
        r = val;
        g = cc;
        b = aa;
        break;
      case 1:
        r = bb;
        g = val;
        b = aa;
        break;
      case 2:
        r = aa;
        g = val;
        b = cc;
        break;
      case 3:
        r = aa;
        g = bb;
        b = val;
        break;
      case 4:
        r = cc;
        g = aa;
        b = val;
        break;
      case 5:
      default:
        r = val;
        g = aa;
        b = bb;
        break;
    }
    return new Color((float) r, (float) g, (float) b, (float) alpha);
  }
}
