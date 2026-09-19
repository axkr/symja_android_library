package org.matheclipse.core.graphics;

import org.matheclipse.core.interfaces.IExpr;

/**
 * The settings of the {@code IntervalMarkers} option: how an uncertain coordinate ({@link
 * UncertainValue}) is marked around its centre.
 */
public enum IntervalMarkerType {
  /** Draw the centre only. */
  NONE,
  /** A line from the lower to the upper limit on each uncertain axis. */
  BARS,
  /** {@link #BARS} with a short perpendicular cap at each closed limit. */
  FENCES,
  /** A point at each limit. */
  POINTS,
  /** An ellipse inscribed in the box spanned by the limits. */
  ELLIPSES,
  /** A translucent band enclosing the limits of consecutive points. */
  BANDS,
  /** 3D: a tube around each bar. */
  TUBES;

  /** {@code Automatic}, and any setting not understood, draws bars. */
  public static IntervalMarkerType of(IExpr value) {
    if (value == null) {
      return BARS;
    }
    if (value.isNone() || value.isFalse()) {
      return NONE;
    }
    if (value.isString()) {
      switch (value.toString()) {
        case "Bars":
          return BARS;
        case "Fences":
          return FENCES;
        case "Points":
          return POINTS;
        case "Ellipses":
          return ELLIPSES;
        case "Bands":
          return BANDS;
        case "Tubes":
          return TUBES;
        default:
          break;
      }
    }
    return BARS;
  }
}
