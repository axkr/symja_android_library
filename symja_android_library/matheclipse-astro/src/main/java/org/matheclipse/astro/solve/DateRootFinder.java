package org.matheclipse.astro.solve;

import org.hipparchus.util.FastMath;
import org.orekit.time.AbsoluteDate;

/**
 * Finds zero crossings and extrema of a scalar function of time.
 *
 * <p>
 * Orekit's {@link org.orekit.propagation.events.EventDetector} family expects a
 * <code>double g(SpacecraftState)</code> and therefore needs a propagator to be driven. Everything
 * this module searches for - rise, set, culmination, moon phase, equinox - is a function of the
 * date alone, so the detectors would only add machinery. A scan followed by a bisection on the date
 * axis is both simpler and enough: the functions involved are smooth and their period is known
 * within a factor of two, which is what fixes the scan step.
 */
public class DateRootFinder {

  /** A scalar function of time. */
  @FunctionalInterface
  public interface DateFunction {
    /**
     * @return the value at {@code date}
     */
    double value(AbsoluteDate date);
  }

  /** Absolute accuracy of the returned dates, in seconds. */
  public static final double DEFAULT_ACCURACY = 1.0e-3;

  /** Maximum number of bisection steps, a backstop against a non converging function. */
  private static final int MAX_ITERATIONS = 100;

  private DateRootFinder() {}

  /**
   * Search the first date at which {@code function} changes sign.
   *
   * @param function the function to search, evaluated at increasing (or decreasing) dates
   * @param start where the search begins
   * @param stepSeconds the scan step; must be small enough that no two roots fall inside one step
   * @param maxSeconds how far to scan away from {@code start}
   * @param direction <code>1</code> to search forwards in time, <code>-1</code> backwards
   * @return the crossing date, or <code>null</code> if the function does not change sign within
   *         {@code maxSeconds}
   */
  public static AbsoluteDate findCrossing(DateFunction function, AbsoluteDate start,
      double stepSeconds, double maxSeconds, int direction) {
    return findCrossing(function, start, stepSeconds, maxSeconds, direction,
        Double.POSITIVE_INFINITY);
  }

  /**
   * Search the first date at which {@code function} changes sign, ignoring jumps.
   *
   * <p>
   * An angle which has been wrapped into an interval changes sign twice per period: once at the
   * root, and once at the branch cut, where it jumps from one end of the interval to the other.
   * {@code maxJump} tells the two apart - a sign change across a step wider than that is a
   * discontinuity and is skipped.
   *
   * @param maxJump the largest change in the function value which still counts as continuous
   */
  public static AbsoluteDate findCrossing(DateFunction function, AbsoluteDate start,
      double stepSeconds, double maxSeconds, int direction, double maxJump) {
    double step = direction < 0 ? -stepSeconds : stepSeconds;
    AbsoluteDate previousDate = start;
    double previousValue = function.value(previousDate);
    for (double elapsed = 0.0; elapsed < maxSeconds; elapsed += stepSeconds) {
      AbsoluteDate date = previousDate.shiftedBy(step);
      double value = function.value(date);
      if (previousValue == 0.0) {
        return previousDate;
      }
      if (previousValue * value < 0.0 && FastMath.abs(value - previousValue) <= maxJump) {
        return direction < 0 ? bisect(function, date, previousDate)
            : bisect(function, previousDate, date);
      }
      previousDate = date;
      previousValue = value;
    }
    return null;
  }

  /**
   * Bisect a bracketing interval down to {@link #DEFAULT_ACCURACY}.
   *
   * @param low the earlier end of the bracket
   * @param high the later end of the bracket; {@code function} must have opposite signs at the ends
   */
  public static AbsoluteDate bisect(DateFunction function, AbsoluteDate low, AbsoluteDate high) {
    AbsoluteDate lowDate = low;
    AbsoluteDate highDate = high;
    double lowValue = function.value(lowDate);
    for (int i = 0; i < MAX_ITERATIONS; i++) {
      double duration = highDate.durationFrom(lowDate);
      if (duration <= DEFAULT_ACCURACY) {
        break;
      }
      AbsoluteDate middleDate = lowDate.shiftedBy(duration / 2.0);
      double middleValue = function.value(middleDate);
      if (middleValue == 0.0) {
        return middleDate;
      }
      if (lowValue * middleValue < 0.0) {
        highDate = middleDate;
      } else {
        lowDate = middleDate;
        lowValue = middleValue;
      }
    }
    return lowDate.shiftedBy(highDate.durationFrom(lowDate) / 2.0);
  }

  /**
   * Search the first date at which {@code function} reaches a local maximum.
   *
   * <p>
   * Implemented as a crossing search on the numerical derivative, so the accuracy of the returned
   * date is that of {@link #findCrossing} and not that of the maximum value itself.
   *
   * @param maximum <code>true</code> for a maximum, <code>false</code> for a minimum
   * @return the extremum date, or <code>null</code> if none was found within {@code maxSeconds}
   */
  public static AbsoluteDate findExtremum(DateFunction function, AbsoluteDate start,
      double stepSeconds, double maxSeconds, int direction, boolean maximum) {
    // a tenth of the scan step keeps the difference quotient well inside the smooth part of the
    // function while staying far above the date resolution
    double delta = stepSeconds / 10.0;
    double sign = maximum ? 1.0 : -1.0;
    DateFunction derivative = date -> sign
        * (function.value(date.shiftedBy(delta)) - function.value(date.shiftedBy(-delta)));
    AbsoluteDate previousDate = start;
    double previousValue = derivative.value(previousDate);
    double step = direction < 0 ? -stepSeconds : stepSeconds;
    for (double elapsed = 0.0; elapsed < maxSeconds; elapsed += stepSeconds) {
      AbsoluteDate date = previousDate.shiftedBy(step);
      double value = derivative.value(date);
      // only a descending derivative is a maximum, so require the sign change in that direction
      if (previousValue > 0.0 && value <= 0.0) {
        return direction < 0 ? bisect(derivative, date, previousDate)
            : bisect(derivative, previousDate, date);
      }
      previousDate = date;
      previousValue = value;
    }
    return null;
  }
}
