package org.matheclipse.core.graphics.svg;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** Tick placement and label formatting for linear and logarithmic axes. */
public final class TickGenerator {

  private TickGenerator() {}

  /** A tick mark: a position in data coordinates and the text drawn next to it. */
  public static final class Tick {
    public final double value;
    public final String label;
    /** True for a labelled major tick, false for an unlabelled minor one. */
    public final boolean major;

    public Tick(double value, String label, boolean major) {
      this.value = value;
      this.label = label;
      this.major = major;
    }
  }

  /**
   * Ticks for a linear axis, chosen at "nice" round positions.
   *
   * @param min lower end of the visible range
   * @param max upper end of the visible range
   */
  public static List<Tick> linear(double min, double max) {
    List<Tick> ticks = new ArrayList<>();
    if (!Double.isFinite(min) || !Double.isFinite(max) || max <= min) {
      return ticks;
    }
    double step = niceNumber((max - min) / 5.0, true);
    if (!(step > 0) || !Double.isFinite(step)) {
      return ticks;
    }
    // walking by an index avoids the drift that repeated addition accumulates
    long firstIndex = (long) Math.ceil(min / step - 1e-9);
    long lastIndex = (long) Math.floor(max / step + 1e-9);
    if (lastIndex - firstIndex > 1000) {
      return ticks;
    }
    for (long i = firstIndex; i <= lastIndex; i++) {
      double value = i * step;
      if (value < min - 1e-12 || value > max + 1e-12) {
        continue;
      }
      // -0.0 and values that are zero within rounding both print as "0"
      if (Math.abs(value) < step * 1e-9) {
        value = 0.0;
      }
      ticks.add(new Tick(value, format(value, step), true));
    }
    return ticks;
  }

  /** Ticks for a logarithmic axis, at powers of ten and, when there is room, at 2 and 5. */
  public static List<Tick> logarithmic(double min, double max) {
    List<Tick> ticks = new ArrayList<>();
    double lo = min <= 0 ? Viewport2D.LOG_MIN_CLAMP : min;
    if (!Double.isFinite(lo) || !Double.isFinite(max) || max <= lo) {
      return ticks;
    }
    double logMin = Math.log10(lo);
    double logMax = Math.log10(max);
    double span = logMax - logMin;
    int startPow = (int) Math.floor(logMin);
    int endPow = (int) Math.ceil(logMax);
    if (endPow - startPow > 400) {
      return ticks;
    }
    if (span >= 3.0) {
      int stride = span >= 8.0 ? (int) Math.ceil(span / 8.0) : 1;
      for (int p = startPow; p <= endPow; p++) {
        if (Math.floorMod(p, stride) != 0) {
          continue;
        }
        double value = Math.pow(10, p);
        if (value >= lo && value <= max) {
          ticks.add(new Tick(value, powerLabel(p), true));
        }
      }
    } else {
      double[] mantissas = {1.0, 2.0, 5.0};
      for (int p = startPow; p <= endPow; p++) {
        double base = Math.pow(10, p);
        for (double m : mantissas) {
          double value = m * base;
          if (value >= lo && value <= max) {
            ticks
                .add(new Tick(value, m == 1.0 ? powerLabel(p) : format(value, value / 10.0), true));
          }
        }
      }
    }
    Collections.sort(ticks, (a, b) -> Double.compare(a.value, b.value));
    return ticks;
  }

  /**
   * Interpret an explicit tick specification: a list whose entries are a bare position,
   * {@code {position, label}}, or {@code {position, label, length}}.
   *
   * @return the ticks, or {@code null} when {@code spec} is not an explicit list
   */
  public static List<Tick> explicit(IExpr spec) {
    if (spec == null || !spec.isList()) {
      return null;
    }
    List<Tick> ticks = new ArrayList<>();
    IAST list = (IAST) spec;
    for (int i = 1; i <= list.argSize(); i++) {
      IExpr entry = list.get(i);
      if (entry.isList() && ((IAST) entry).argSize() >= 1) {
        IAST pair = (IAST) entry;
        double value = ColorUtil.dbl(pair.arg1(), Double.NaN);
        if (Double.isNaN(value)) {
          continue;
        }
        String label =
            pair.argSize() >= 2 ? PrimitiveCollector.unquote(pair.arg2().toString()) : trim(value);
        ticks.add(new Tick(value, label, true));
      } else {
        double value = ColorUtil.dbl(entry, Double.NaN);
        if (!Double.isNaN(value)) {
          ticks.add(new Tick(value, trim(value), true));
        }
      }
    }
    return ticks;
  }

  /**
   * Format a tick label for an axis whose ticks are {@code step} apart, so that neighbouring labels
   * carry just enough decimals to be distinct and no trailing zeros beyond that.
   */
  public static String format(double value, double step) {
    if (value == 0.0) {
      return "0";
    }
    double absStep = Math.abs(step);
    int decimals = 0;
    if (absStep > 0 && absStep < 1) {
      decimals = (int) Math.ceil(-Math.log10(absStep) - 1e-9);
      decimals = Math.max(0, Math.min(10, decimals));
    }
    double absValue = Math.abs(value);
    if (absValue >= 1e6 || (absValue < 1e-4 && absValue > 0)) {
      return scientific(value);
    }
    BigDecimal rounded = BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP);
    return trimZeros(rounded.toPlainString());
  }

  /** Format a value with no particular step in mind. */
  public static String trim(double value) {
    if (!Double.isFinite(value)) {
      return "";
    }
    if (value == 0.0) {
      return "0";
    }
    double absValue = Math.abs(value);
    if (absValue >= 1e6 || absValue < 1e-4) {
      return scientific(value);
    }
    BigDecimal rounded = new BigDecimal(value, new MathContext(6, RoundingMode.HALF_UP));
    return trimZeros(rounded.toPlainString());
  }

  private static String scientific(double value) {
    String s = String.format(Locale.US, "%.3g", value);
    // %g keeps trailing zeros in the mantissa, which read as false precision
    if (s.contains("e")) {
      String[] parts = s.split("e");
      return trimZeros(parts[0]) + "e" + Integer.parseInt(parts[1]);
    }
    return trimZeros(s);
  }

  private static String trimZeros(String s) {
    if (s.indexOf('.') < 0) {
      return s;
    }
    String out = s;
    while (out.endsWith("0")) {
      out = out.substring(0, out.length() - 1);
    }
    if (out.endsWith(".")) {
      out = out.substring(0, out.length() - 1);
    }
    return out.isEmpty() || out.equals("-") ? "0" : out;
  }

  /** A power of ten, as a superscript when the exponent is large enough to be worth it. */
  private static String powerLabel(int exponent) {
    if (exponent >= -3 && exponent <= 3) {
      return trim(Math.pow(10, exponent));
    }
    return "10^" + exponent;
  }

  /** True when a label produced by {@link #logarithmic} needs superscript markup. */
  public static boolean isPowerLabel(String label) {
    return label != null && label.startsWith("10^");
  }

  /** The exponent part of a {@code 10^n} label. */
  public static String powerExponent(String label) {
    return label.substring(3);
  }

  /** Round a value to a "nice" number: 1, 2, 5 or 10 times a power of ten. */
  static double niceNumber(double range, boolean round) {
    if (!(range > 0) || !Double.isFinite(range)) {
      return 0;
    }
    double exponent = Math.floor(Math.log10(range));
    double fraction = range / Math.pow(10, exponent);
    double niceFraction;
    if (round) {
      if (fraction < 1.5) {
        niceFraction = 1;
      } else if (fraction < 3) {
        niceFraction = 2;
      } else if (fraction < 7) {
        niceFraction = 5;
      } else {
        niceFraction = 10;
      }
    } else {
      if (fraction <= 1) {
        niceFraction = 1;
      } else if (fraction <= 2) {
        niceFraction = 2;
      } else if (fraction <= 5) {
        niceFraction = 5;
      } else {
        niceFraction = 10;
      }
    }
    return niceFraction * Math.pow(10, exponent);
  }
}
