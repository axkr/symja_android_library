package org.matheclipse.core.graphics;

import java.util.Arrays;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A parsed {@code PlotRangePadding} setting: how much room to leave around the data, per axis and
 * per side.
 *
 * <p>
 * The same class serves the two dimensional and the three dimensional pipelines, which is why the
 * number of axes is a parameter rather than a constant. Instances are immutable, which matters
 * because the 2D viewport is configured twice for the same picture.
 *
 * <p>
 * Two rules of the Wolfram Language are worth spelling out, because neither is what a first reading
 * of the option name suggests.
 *
 * <ul>
 * <li>{@code Scaled[s]} is a fraction of the <em>finished</em> plot, not of the data. A plot padded
 * with {@code Scaled[s]} on both sides covers a fraction {@code 1 - 2 s} of the plotting area, so
 * the padding itself is {@code s * span / (1 - 2 s)}. Fractions that add up to one or more would
 * ask for an infinite plot; those fall back to a fraction of the data's own span so that a picture
 * still comes out.
 * <li>{@code Automatic} leaves 2% per side, but only where the {@code PlotRange} did not pin the
 * axis. An explicitly given range is taken at its word. An explicitly given padding always wins,
 * pinned or not.
 * </ul>
 */
public final class PlotRangePaddingSpec {

  /** The fraction of the finished plot that {@code Automatic} leaves on each unpinned side. */
  public static final double AUTOMATIC_FRACTION = 0.02;

  /** Below this the {@code 1 - sLo - sHi} denominator is treated as degenerate. */
  private static final double MIN_DENOMINATOR = 1e-9;

  /** What one side of one axis was asked for. */
  public enum Kind {
    NONE, AUTOMATIC, EXPLICIT
  }

  /** The padding of one side of one axis. */
  public static final class Amount {

    public static final Amount NONE = new Amount(Kind.NONE, 0, 0);
    public static final Amount AUTOMATIC = new Amount(Kind.AUTOMATIC, 0, 0);

    public final Kind kind;
    /** Coordinate units, from a plain number. */
    public final double absolute;
    /** A fraction of the finished plot, from {@code Scaled}. */
    public final double scaled;

    private Amount(Kind kind, double absolute, double scaled) {
      this.kind = kind;
      this.absolute = absolute;
      this.scaled = scaled;
    }

    public static Amount absolute(double value) {
      return new Amount(Kind.EXPLICIT, value, 0);
    }

    public static Amount scaled(double fraction) {
      return new Amount(Kind.EXPLICIT, 0, fraction);
    }

    /** The absolute part of this amount. Only an explicit setting has one. */
    double absoluteFor() {
      return kind == Kind.EXPLICIT ? absolute : 0;
    }

    /** The scaled part of this amount once the pinning rule has been applied. */
    double scaledFor(boolean pinned) {
      if (kind == Kind.EXPLICIT) {
        return scaled;
      }
      return kind == Kind.AUTOMATIC && !pinned ? AUTOMATIC_FRACTION : 0;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof Amount)) {
        return false;
      }
      Amount other = (Amount) obj;
      return kind == other.kind
          && Double.compare(absolute, other.absolute) == 0
          && Double.compare(scaled, other.scaled) == 0;
    }

    @Override
    public int hashCode() {
      return kind.hashCode() * 31 + Double.hashCode(absolute) * 7 + Double.hashCode(scaled);
    }

    @Override
    public String toString() {
      switch (kind) {
        case NONE:
          return "None";
        case AUTOMATIC:
          return "Automatic";
        default:
          return absolute + "+Scaled[" + scaled + "]";
      }
    }
  }

  /** {@code [axis][0]} is the low side, {@code [axis][1]} the high side. */
  private final Amount[][] amounts;

  private PlotRangePaddingSpec(Amount[][] amounts) {
    this.amounts = amounts;
  }

  /** The default: 2% per side of every axis the {@code PlotRange} did not pin. */
  public static PlotRangePaddingSpec automatic(int axisCount) {
    return uniform(Amount.AUTOMATIC, Amount.AUTOMATIC, axisCount);
  }

  /** No padding at all. */
  public static PlotRangePaddingSpec none(int axisCount) {
    return uniform(Amount.NONE, Amount.NONE, axisCount);
  }

  private static PlotRangePaddingSpec uniform(Amount low, Amount high, int axisCount) {
    Amount[][] amounts = new Amount[axisCount][];
    for (int i = 0; i < axisCount; i++) {
      amounts[i] = new Amount[] {low, high};
    }
    return new PlotRangePaddingSpec(amounts);
  }

  public int axisCount() {
    return amounts.length;
  }

  /** The setting of one side of one axis, as it was written. */
  public Amount amount(int axis, int side) {
    return amounts[axis][side];
  }

  /**
   * Read a {@code PlotRangePadding} value.
   *
   * @param axisCount 2 for a {@code Graphics}, 3 for a {@code Graphics3D}
   * @return {@code null} when the expression is not a padding specification
   */
  public static PlotRangePaddingSpec parse(IExpr value, int axisCount) {
    Amount[][] amounts = parseSpec(value, axisCount, false);
    return amounts == null ? null : new PlotRangePaddingSpec(amounts);
  }

  /**
   * The same, but a value that cannot be read falls back to {@code Automatic} rather than to no
   * padding at all: a typo should not silently crop the picture.
   */
  public static PlotRangePaddingSpec parseOrAutomatic(IExpr value, int axisCount) {
    PlotRangePaddingSpec spec = parse(value, axisCount);
    return spec == null ? automatic(axisCount) : spec;
  }

  /**
   * The padding in coordinate units that does not depend on the size of the range.
   *
   * @return {@code {low, high}}
   */
  public double[] absolutePad(int axis) {
    return new double[] {amounts[axis][0].absoluteFor(), amounts[axis][1].absoluteFor()};
  }

  /**
   * The padding that is a fraction of the finished plot.
   *
   * @param paddedSpan the extent of the axis with {@link #absolutePad} already added
   * @return {@code {low, high}}
   */
  public double[] scaledPad(int axis, double paddedSpan, boolean pinLow, boolean pinHigh) {
    double low = amounts[axis][0].scaledFor(pinLow);
    double high = amounts[axis][1].scaledFor(pinHigh);
    if (low == 0 && high == 0) {
      return new double[] {0, 0};
    }
    double denominator = 1.0 - low - high;
    // fractions that fill the whole plot would need an infinite range; fall back to a fraction of
    // the data's own span, which is finite and close to what was asked for
    double total = denominator > MIN_DENOMINATOR ? paddedSpan / denominator : paddedSpan;
    return new double[] {low * total, high * total};
  }

  /**
   * The whole padding of one axis, for a pipeline that has no scaling function between the data
   * and the picture.
   *
   * @param span the extent of the axis before any padding
   * @return {@code {low, high}} in the same units as {@code span}
   */
  public double[] resolve(int axis, double span, boolean pinLow, boolean pinHigh) {
    double[] absolute = absolutePad(axis);
    double[] scaled = scaledPad(axis, span + absolute[0] + absolute[1], pinLow, pinHigh);
    return new double[] {absolute[0] + scaled[0], absolute[1] + scaled[1]};
  }

  // ------------------------------------------------------------------ parsing

  /**
   * @param scaledContext true inside a {@code Scaled}, where a bare number is a fraction rather
   *        than a length
   */
  private static Amount[][] parseSpec(IExpr value, int axisCount, boolean scaledContext) {
    if (value == null) {
      return null;
    }
    if (value.isNone()) {
      return spread(new Amount[] {Amount.NONE, Amount.NONE}, axisCount);
    }
    if (value == S.Automatic) {
      return spread(new Amount[] {Amount.AUTOMATIC, Amount.AUTOMATIC}, axisCount);
    }
    if (value.isAST(S.Scaled, 2)) {
      return parseSpec(((IAST) value).arg1(), axisCount, true);
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      if (list.argSize() == axisCount) {
        Amount[][] amounts = new Amount[axisCount][];
        for (int i = 0; i < axisCount; i++) {
          amounts[i] = parseAxis(list.get(i + 1), scaledContext);
          if (amounts[i] == null) {
            return null;
          }
        }
        return amounts;
      }
      if (list.argSize() == 2 && axisCount == 3 && !list.arg1().isList() && !list.arg2().isList()) {
        // the two sides of every axis at once, which is how the three dimensional pipeline has
        // always read a pair
        Amount[] sides = parseAxis(list, scaledContext);
        return sides == null ? null : spread(sides, axisCount);
      }
      return null;
    }
    Amount side = parseSide(value, scaledContext);
    return side == null ? null : spread(new Amount[] {side, side}, axisCount);
  }

  private static Amount[][] spread(Amount[] sides, int axisCount) {
    Amount[][] amounts = new Amount[axisCount][];
    for (int i = 0; i < axisCount; i++) {
      amounts[i] = new Amount[] {sides[0], sides[1]};
    }
    return amounts;
  }

  /** One axis: a single setting for both sides, or a {@code {low, high}} pair. */
  private static Amount[] parseAxis(IExpr value, boolean scaledContext) {
    if (value.isAST(S.Scaled, 2)) {
      return parseAxis(((IAST) value).arg1(), true);
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      if (list.argSize() != 2) {
        return null;
      }
      Amount low = parseSide(list.arg1(), scaledContext);
      Amount high = parseSide(list.arg2(), scaledContext);
      return low == null || high == null ? null : new Amount[] {low, high};
    }
    Amount both = parseSide(value, scaledContext);
    return both == null ? null : new Amount[] {both, both};
  }

  /** One side of one axis. */
  private static Amount parseSide(IExpr value, boolean scaledContext) {
    if (value.isNone()) {
      return Amount.NONE;
    }
    if (value == S.Automatic) {
      return Amount.AUTOMATIC;
    }
    if (value.isAST(S.Scaled, 2)) {
      double fraction = ColorUtil.dbl(((IAST) value).arg1(), Double.NaN);
      return Double.isFinite(fraction) ? Amount.scaled(fraction) : null;
    }
    if (value.isList()) {
      return null;
    }
    double number = ColorUtil.dbl(value, Double.NaN);
    if (!Double.isFinite(number)) {
      return null;
    }
    return scaledContext ? Amount.scaled(number) : Amount.absolute(number);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PlotRangePaddingSpec)) {
      return false;
    }
    return Arrays.deepEquals(amounts, ((PlotRangePaddingSpec) obj).amounts);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(amounts);
  }

  @Override
  public String toString() {
    return Arrays.deepToString(amounts);
  }
}
