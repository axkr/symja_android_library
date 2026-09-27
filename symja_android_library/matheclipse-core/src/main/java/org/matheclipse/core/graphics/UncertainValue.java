package org.matheclipse.core.graphics;

import org.matheclipse.core.builtin.AroundFunctions;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A graphics coordinate that carries an uncertainty: {@code Around(x, d)}, {@code Around(x, {dl,
 * du})}, {@code Interval({a, b}, ...)} or {@code IntervalData({a, rel, rel, b}, ...)}.
 *
 * <p>
 * Such a coordinate is drawn at its {@link #center} and the renderers add interval markers from
 * {@link #lo} to {@link #hi}, as selected by the {@code IntervalMarkers} option. A union of several
 * intervals is read as its hull. Reading is purely numeric and never evaluates, since it runs once
 * per coordinate inside the renderers.
 */
public final class UncertainValue {

  public final double center;
  public final double lo;
  public final double hi;
  /** Whether the lower limit belongs to the interval; only an open {@code IntervalData} end not. */
  public final boolean loClosed;
  public final boolean hiClosed;

  private UncertainValue(double center, double lo, double hi, boolean loClosed,
      boolean hiClosed) {
    this.center = center;
    this.lo = lo;
    this.hi = hi;
    this.loClosed = loClosed;
    this.hiClosed = hiClosed;
  }

  /** A certain value, whose limits coincide with its centre. */
  public static UncertainValue exact(double value) {
    return new UncertainValue(value, value, value, true, true);
  }

  /** Whether {@code expr} has one of the forms read by {@link #of(IExpr)}. */
  public static boolean isUncertain(IExpr expr) {
    return AroundFunctions.isAround(expr) || expr.isInterval() || expr.isIntervalData();
  }

  /**
   * The uncertain value of {@code expr}, or {@code null} when {@code expr} is not an uncertain form
   * or its parts are not finite numbers.
   */
  public static UncertainValue of(IExpr expr) {
    if (expr == null || !expr.isAST()) {
      return null;
    }
    if (AroundFunctions.isAround(expr)) {
      IAST around = (IAST) expr;
      double c = dbl(around.arg1());
      double dl = Math.abs(dbl(AroundFunctions.lower(around)));
      double du = Math.abs(dbl(AroundFunctions.upper(around)));
      return finite(c, c - dl, c + du, true, true);
    }
    if (expr.isInterval() && expr.argSize() > 0) {
      IAST interval = (IAST) expr;
      double lo = Double.POSITIVE_INFINITY;
      double hi = Double.NEGATIVE_INFINITY;
      for (int i = 1; i < interval.size(); i++) {
        IAST part = (IAST) interval.get(i);
        lo = Math.min(lo, dbl(part.arg1()));
        hi = Math.max(hi, dbl(part.arg2()));
      }
      return finite((lo + hi) / 2.0, lo, hi, true, true);
    }
    if (expr.isIntervalData() && expr.argSize() > 0) {
      IAST data = (IAST) expr;
      double lo = Double.POSITIVE_INFINITY;
      double hi = Double.NEGATIVE_INFINITY;
      boolean loClosed = true;
      boolean hiClosed = true;
      for (int i = 1; i < data.size(); i++) {
        // {min, leftRelation, rightRelation, max}
        IAST part = (IAST) data.get(i);
        double a = dbl(part.arg1());
        double b = dbl(part.arg4());
        if (a < lo) {
          lo = a;
          loClosed = part.arg2() == S.LessEqual;
        }
        if (b > hi) {
          hi = b;
          hiClosed = part.arg3() == S.LessEqual;
        }
      }
      return finite((lo + hi) / 2.0, lo, hi, loClosed, hiClosed);
    }
    return null;
  }

  /** The value a coordinate is drawn at: the centre of an uncertain form, else {@code NaN}. */
  public static double center(IExpr expr) {
    UncertainValue value = of(expr);
    return value == null ? Double.NaN : value.center;
  }

  /** Whether the limits differ from the centre, so that a marker has something to show. */
  public boolean hasExtent() {
    return lo < center || hi > center;
  }

  private static UncertainValue finite(double c, double lo, double hi, boolean loClosed,
      boolean hiClosed) {
    if (!Double.isFinite(c) || !Double.isFinite(lo) || !Double.isFinite(hi)) {
      return null;
    }
    return new UncertainValue(c, Math.min(lo, hi), Math.max(lo, hi), loClosed, hiClosed);
  }

  private static double dbl(IExpr expr) {
    return ColorUtil.dbl(expr, Double.NaN);
  }
}
