package org.matheclipse.image.algo;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import boofcv.alg.interpolate.InterpolationType;
import boofcv.struct.ConnectRule;
import boofcv.struct.border.BorderType;

/**
 * Translates the option values the image built-ins share into the BoofCV enums behind them.
 *
 * <p>
 * Reading the options themselves is the caller's job - <code>OptionArgs</code> in
 * <code>matheclipse-core</code> already does that - so every method here takes the value of a
 * single option and never an <code>IAST</code>.
 */
public final class OptionsImg {

  private OptionsImg() {}

  /**
   * The border handling for <code>Padding -&gt; value</code>.
   *
   * <p>
   * The default is <code>"Fixed"</code>, which repeats the edge pixel, so <code>Automatic</code>
   * and an unrecognized value both map to {@link BorderType#EXTENDED}. <code>None</code> maps to
   * {@link BorderType#SKIP}: BoofCV leaves the border of the output untouched.
   */
  public static BorderType padding(IExpr value) {
    if (value.isString()) {
      String name = value.toString();
      if ("Fixed".equals(name)) {
        return BorderType.EXTENDED;
      }
      if ("Periodic".equals(name)) {
        return BorderType.WRAP;
      }
      if ("Reflected".equals(name) || "Reversed".equals(name)) {
        return BorderType.REFLECT;
      }
      if ("None".equals(name)) {
        return BorderType.SKIP;
      }
    }
    if (value == S.None) {
      return BorderType.SKIP;
    }
    if (value.isZero()) {
      return BorderType.ZERO;
    }
    return BorderType.EXTENDED;
  }

  /**
   * The connectivity for <code>CornerNeighbors -&gt; value</code>. <code>True</code>, means the
   * eight surrounding pixels count as neighbours.
   */
  public static ConnectRule cornerNeighbors(IExpr value) {
    return value.isFalse() ? ConnectRule.FOUR : ConnectRule.EIGHT;
  }

  /**
   * The interpolation for <code>Resampling -&gt; value</code> or
   * <code>Interpolation -&gt; value</code>. Integers are interpolation orders the way
   * <code>Interpolation</code> uses them, strings are the <code>Resampling</code> method names.
   */
  public static InterpolationType interpolation(IExpr value) {
    if (value.isString()) {
      String name = value.toString();
      if ("Nearest".equals(name) || "Constant".equals(name)) {
        return InterpolationType.NEAREST_NEIGHBOR;
      }
      if ("Linear".equals(name) || "Bilinear".equals(name)) {
        return InterpolationType.BILINEAR;
      }
      if ("Cubic".equals(name) || "Bicubic".equals(name)) {
        return InterpolationType.BICUBIC;
      }
      return InterpolationType.BILINEAR;
    }
    if (value == S.None) {
      return InterpolationType.NEAREST_NEIGHBOR;
    }
    int order = value.toIntDefault();
    if (order == Config.INVALID_INT) {
      return InterpolationType.BILINEAR;
    }
    if (order <= 0) {
      return InterpolationType.NEAREST_NEIGHBOR;
    }
    return order >= 3 ? InterpolationType.BICUBIC : InterpolationType.BILINEAR;
  }

  /**
   * The colour space name for <code>ColorSpace -&gt; value</code>, normalized to the spelling
   * <code>ImageColorSpace</code> reports. <code>Automatic</code> yields <code>null</code>, meaning
   * "keep whatever the image already has".
   */
  public static String colorSpace(IExpr value) {
    if (value.isString()) {
      String name = value.toString();
      if ("Gray".equalsIgnoreCase(name) || "Grayscale".equalsIgnoreCase(name)) {
        return "Grayscale";
      }
      return name;
    }
    if (value == S.Automatic) {
      return null;
    }
    return null;
  }
}
