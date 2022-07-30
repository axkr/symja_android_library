// code by jph
package org.matheclipse.core.tensor.sca;

import java.util.Objects;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * Clip encodes a non-empty, closed interval in an ordered set of scalars.
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * Clip clip = Clips.interval(5, 10);
 * clip.apply(3) == 5
 * clip.apply(5) == 5
 * clip.apply(6) == 6
 * clip.apply(10) == 10
 * clip.apply(20) == 10
 * </pre>
 * 
 * <p>
 * {@code Clip} also works for intervals defined by {@link Quantity}.
 * 
 * <p>
 * An instance of {@link Clip} is immutable. {@link Clip} implements {@link #hashCode()}, and
 * {@link #equals(Object)}. {@link Clip} does not implement {@link #toString()}.
 */
public interface Clip extends Function<IExpr, IExpr> {
  /** @param tensor
   * @return tensor with all entries of given tensor applied to clip function */
  IAST of(IAST tensor);

  /** @param scalar
   * @return true if given scalar is invariant under this clip, i.e. the evaluation
   * of the condition min <= scalar <= max. */
  boolean isInside(ISignedNumber scalar);

  /** Remark: Functionality inspired by {@link Objects#requireNonNull(Object)}
   * 
   * @param scalar
   * @return scalar that is guaranteed to be invariant under this clip
   * @throws Exception if given scalar is not invariant under this clip */
  ISignedNumber requireInside(ISignedNumber scalar);

  /** @param scalar
   * @return true if given scalar is not invariant under this clip, i.e. the evaluation
   * of the condition scalar < min or max < scalar. */
  boolean isOutside(ISignedNumber scalar);

  /**
   * If max - min > 0, the given scalar is clipped to the [min max] interval, then min is subtracted
   * and the result divided by width. If max == min the result is always RealScalar.ZERO.
   * 
   * <p>
   * When using Clip with {@link Quantity}s, all three scalars min, max, and the given scalar, must
   * be of identical unit. {@link #rescale(ISignedNumber)} always returns a {@link RealScalar}.
   * 
   * @param scalar
   * @return value in interval [0, 1] relative to position of scalar in clip interval. If the clip
   *         interval width is zero, the return value is zero. If the given scalar is outside the
   *         clip interval, the return value is either 0, or 1.
   */
  IExpr rescale(ISignedNumber scalar);

  /** @return lower bound of clip interval */
  ISignedNumber min();

  /** @return upper bound of clip interval */
  ISignedNumber max();

  /** @return difference between upper and lower bound of clip interval */
  ISignedNumber width();

  @Override // from Object
  int hashCode();

  @Override // from Object
  boolean equals(Object object);
}
