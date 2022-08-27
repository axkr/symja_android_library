// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.sca;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.tensor.qty.QuantityUnit;

/**
 * factory for the creation of {@link Clip}
 * 
 * Remark: A {@link Clip} represents a non-empty, closed interval of the form [min, max]. The values
 * min and max can be real numbers, or instances of {@link Quantity} with identical {@link Unit}.
 */
public class Clips {

  /**
   * clips in the interval [min, ..., max]
   * 
   * @param min
   * @param max equals or greater than given min
   * @return function that clips the input to the closed interval [min, max]
   * @throws Exception if min is greater than max
   * @throws Exception if min and max give different {@link QuantityUnit}
   */
  public static Clip interval(IExpr min, IExpr max) {
    // Scalars.compare(min, max); // assert that min and max have identical units
    return create(min, max);
  }

  /**
   * @param min
   * @param max equals or greater than given min
   * @return function that clips the input to the closed interval [min, max]
   * @throws Exception if min is greater than max
   */
  public static Clip interval(Number min, Number max) {
    return create(F.num(min.doubleValue()), F.num(max.doubleValue()));
  }

  /**
   * clips in the interval [0, ..., max]
   * 
   * @param max non-negative
   * @return function that clips the input to the closed interval [0, max]
   * @throws Exception if max is negative
   */
  public static Clip positive(ISignedNumber max) {
    return create(max.zero(), max);
  }

  /**
   * @param max non-negative
   * @return function that clips the input to the closed interval [0, max]
   * @throws Exception if max is negative
   */
  public static Clip positive(Number max) {
    return positive(F.num(max.doubleValue()));
  }

  // ---
  /**
   * clips in the interval [-max, ..., max]
   * 
   * @param max non-negative
   * @return function that clips the input to the closed interval [-max, max]
   * @throws Exception if max is negative
   */
  public static Clip absolute(ISignedNumber max) {
    return create(max.negate(), max);
  }

  /**
   * @param max non-negative
   * @return function that clips the input to the closed interval [-max, max]
   * @throws Exception if max is negative
   */
  public static Clip absolute(Number max) {
    return absolute(F.num(max.doubleValue()));
  }

  // ---
  private static final Clip UNIT = positive(1);
  private static final Clip ABSOLUTE_ONE = absolute(1);

  /** @return function that clips a scalar to the unit interval [0, 1] */
  public static Clip unit() {
    return UNIT;
  }

  /** @return function that clips a scalar to the interval [-1, 1] */
  public static Clip absoluteOne() {
    return ABSOLUTE_ONE;
  }

  // ---
  /**
   * @param clip1
   * @param clip2
   * @return [max(clip1.min, clip2.min), min(clip1.max, clip2.max)], i.e. the largest interval that
   *         is covered by both input intervals
   * @throws Exception if resulting intersection is empty
   */
  public static Clip intersection(Clip clip1, Clip clip2) {
    return create( //
        S.Max.of(clip1.min(), clip2.min()), //
        S.Min.of(clip1.max(), clip2.max()));
  }

  /**
   * @param clip1
   * @param clip2
   * @return [min(clip1.min, clip2.min), max(clip1.max, clip2.max)], i.e. the smallest interval that
   *         covers both input intervals
   */
  public static Clip cover(Clip clip1, Clip clip2) {
    return create( //
        S.Min.of(clip1.min(), clip2.min()), //
        S.Max.of(clip1.max(), clip2.max()));
  }

  private static Clip create(IExpr min, IExpr max) {
    IExpr width = max.subtract(min);
    if (min.equals(max)) {
      return new ClipPoint(min, width);
    }
    if (width.isPositive()) {
      return new ClipInterval(min, max, width);
    }
    return null;
  }
}
