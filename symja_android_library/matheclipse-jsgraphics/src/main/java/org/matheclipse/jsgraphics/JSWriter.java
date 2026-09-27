package org.matheclipse.jsgraphics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.text.StringEscapeUtils;
import org.matheclipse.core.graphics.svg.Style2D;

/** Writes numbers, strings and colours as JavaScript literals. */
public final class JSWriter {

  private JSWriter() {}

  /**
   * A finite number as a short JavaScript literal: an integer where the value is one, otherwise
   * seven significant digits, which is below anything a picture can show.
   */
  public static String num(double v) {
    if (v == Math.rint(v) && Math.abs(v) < 1e15) {
      return Long.toString((long) v);
    }
    String s = String.format(Locale.US, "%.7g", v);
    int e = s.indexOf('e');
    String mantissa = e < 0 ? s : s.substring(0, e);
    String exponent = e < 0 ? "" : s.substring(e);
    if (mantissa.indexOf('.') >= 0) {
      int end = mantissa.length();
      while (mantissa.charAt(end - 1) == '0') {
        end--;
      }
      if (mantissa.charAt(end - 1) == '.') {
        end--;
      }
      mantissa = mantissa.substring(0, end);
    }
    return mantissa + exponent;
  }

  /** A single quoted JavaScript string literal. */
  public static String str(String s) {
    return "'" + StringEscapeUtils.escapeEcmaScript(s) + "'";
  }

  /** A colour as <code>'#rrggbb'</code>, or <code>'none'</code> for a transparent one. */
  public static String color(Color c) {
    if (c == null || c.getAlpha() == 0) {
      return "'none'";
    }
    return String.format(Locale.US, "'#%02x%02x%02x'", c.getRed(), c.getGreen(), c.getBlue());
  }

  /** The opacity of {@code c} under the {@code Opacity} directive of {@code style}, in 0..1. */
  public static double opacity(Color c, Style2D style) {
    if (c == null) {
      return 0.0;
    }
    double a = c.getAlpha() / 255.0 * style.opacity;
    return Double.isNaN(a) ? 1.0 : Math.max(0.0, Math.min(1.0, a));
  }

  public static boolean isFinite(double[] p) {
    return Double.isFinite(p[0]) && Double.isFinite(p[1]);
  }

  /**
   * The runs of finite points of a polyline. A point that is not finite - a pole of a plotted
   * function - ends one run and the next finite point starts another, which is how the SVG
   * renderer draws it too.
   */
  public static List<List<double[]>> finiteRuns(List<double[]> points) {
    List<List<double[]>> runs = new ArrayList<>();
    List<double[]> run = new ArrayList<>();
    for (double[] p : points) {
      if (isFinite(p)) {
        run.add(p);
      } else if (!run.isEmpty()) {
        runs.add(run);
        run = new ArrayList<>();
      }
    }
    if (!run.isEmpty()) {
      runs.add(run);
    }
    return runs;
  }

  /** <code>[x1,x2,...]</code> or <code>[y1,y2,...]</code> of a list of points. */
  public static String coordinate(List<double[]> points, int axis) {
    StringBuilder buf = new StringBuilder(points.size() * 8);
    buf.append('[');
    for (int i = 0; i < points.size(); i++) {
      if (i > 0) {
        buf.append(',');
      }
      buf.append(num(points.get(i)[axis]));
    }
    return buf.append(']').toString();
  }

  /** <code>[[x1,y1],[x2,y2],...]</code> of a list of points. */
  public static String pairs(List<double[]> points) {
    StringBuilder buf = new StringBuilder(points.size() * 16);
    buf.append('[');
    for (int i = 0; i < points.size(); i++) {
      if (i > 0) {
        buf.append(',');
      }
      double[] p = points.get(i);
      buf.append('[').append(num(p[0])).append(',').append(num(p[1])).append(']');
    }
    return buf.append(']').toString();
  }

  /**
   * A <code>BezierCurve</code> as a polyline: consecutive pieces of the given degree, each
   * evaluated with de Casteljau's algorithm.
   */
  public static List<double[]> bezier(List<double[]> control, int degree) {
    List<double[]> out = new ArrayList<>();
    if (control.size() < 2) {
      return out;
    }
    int d = Math.max(1, Math.min(degree, control.size() - 1));
    for (int start = 0; start + d < control.size(); start += d) {
      List<double[]> piece = control.subList(start, start + d + 1);
      int steps = 24;
      for (int i = start == 0 ? 0 : 1; i <= steps; i++) {
        out.add(deCasteljau(piece, (double) i / steps));
      }
    }
    return out;
  }

  private static double[] deCasteljau(List<double[]> points, double t) {
    int n = points.size();
    double[] x = new double[n];
    double[] y = new double[n];
    for (int i = 0; i < n; i++) {
      x[i] = points.get(i)[0];
      y[i] = points.get(i)[1];
    }
    for (int r = 1; r < n; r++) {
      for (int i = 0; i < n - r; i++) {
        x[i] = (1 - t) * x[i] + t * x[i + 1];
        y[i] = (1 - t) * y[i] + t * y[i + 1];
      }
    }
    return new double[] {x[0], y[0]};
  }
}
