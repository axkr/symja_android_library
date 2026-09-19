package org.matheclipse.core.graphics.svg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.IntervalMarkerType;
import org.matheclipse.core.graphics.UncertainValue;

/**
 * The interval markers ({@code IntervalMarkers} option) of points whose coordinates are uncertain,
 * built from the primitives the renderers already know, so SVG and every JavaScript renderer draw
 * them without a primitive of their own.
 *
 * <p>
 * Like the whiskers of {@code BoxWhiskerChart}, bars are plain lines and fence caps are short lines
 * sized in data units. A cap is sized from the plot range, which is only known once every
 * primitive is collected, so the bars carry {@link Prim2D.LinePrim#fenceCaps} and
 * {@link #addFenceCaps} turns them into lines afterwards.
 */
final class IntervalMarkers2D {

  /** Half the length of a fence cap, as a fraction of the plot range across the bar. */
  private static final double CAP_FRACTION = 0.012;

  /** The opacity a band is filled with, unless {@code IntervalMarkersStyle} sets one. */
  private static final double BAND_OPACITY = 0.3;

  /** One point of a {@code Point} or {@code Line}, with the uncertainty of each coordinate. */
  static final class Marker {
    final UncertainValue x;
    final UncertainValue y;

    Marker(UncertainValue x, UncertainValue y) {
      this.x = x;
      this.y = y;
    }

    boolean hasExtent() {
      return x.hasExtent() || y.hasExtent();
    }
  }

  private IntervalMarkers2D() {}

  /**
   * The marker primitives for {@code group}, the points of one {@code Point} or of one segment of
   * a {@code Line}.
   *
   * @param style the style of the marked primitive, with {@code IntervalMarkersStyle} applied
   * @param bandStyle the style a band is filled with
   * @param sortForBand whether a band must first sort the points by x, true for unconnected points
   */
  static List<Prim2D> build(List<Marker> group, IntervalMarkerType type, Style2D style,
      Style2D bandStyle, boolean sortForBand) {
    List<Prim2D> out = new ArrayList<>();
    boolean any = false;
    for (Marker m : group) {
      if (m.hasExtent()) {
        any = true;
        break;
      }
    }
    if (!any || type == IntervalMarkerType.NONE) {
      return out;
    }
    if (type == IntervalMarkerType.BANDS) {
      if (group.size() >= 2) {
        out.add(band(group, bandStyle, sortForBand));
        return out;
      }
      type = IntervalMarkerType.BARS;
    }
    switch (type) {
      case POINTS: {
        List<double[]> limits = new ArrayList<>();
        for (Marker m : group) {
          if (m.x.hasExtent()) {
            limits.add(new double[] {m.x.lo, m.y.center});
            limits.add(new double[] {m.x.hi, m.y.center});
          }
          if (m.y.hasExtent()) {
            limits.add(new double[] {m.x.center, m.y.lo});
            limits.add(new double[] {m.x.center, m.y.hi});
          }
        }
        out.add(new Prim2D.PointsPrim(limits, style));
        break;
      }
      case ELLIPSES:
        for (Marker m : group) {
          if (m.hasExtent()) {
            out.add(new Prim2D.EllipsePrim((m.x.lo + m.x.hi) / 2.0, (m.y.lo + m.y.hi) / 2.0,
                (m.x.hi - m.x.lo) / 2.0, (m.y.hi - m.y.lo) / 2.0, 0, 0, 0, null, false, style));
          }
        }
        break;
      default: {
        // BARS, FENCES and the 3D only TUBES
        boolean fences = type == IntervalMarkerType.FENCES;
        List<List<double[]>> bars = new ArrayList<>();
        List<boolean[]> caps = fences ? new ArrayList<>() : null;
        for (Marker m : group) {
          if (m.x.hasExtent()) {
            bars.add(segment(m.x.lo, m.y.center, m.x.hi, m.y.center));
            if (fences) {
              caps.add(new boolean[] {m.x.loClosed, m.x.hiClosed});
            }
          }
          if (m.y.hasExtent()) {
            bars.add(segment(m.x.center, m.y.lo, m.x.center, m.y.hi));
            if (fences) {
              caps.add(new boolean[] {m.y.loClosed, m.y.hiClosed});
            }
          }
        }
        out.add(new Prim2D.LinePrim(bars, false, style, caps));
        break;
      }
    }
    return out;
  }

  /** The style of a band: the colour of the line it belongs to, translucent and without edges. */
  static Style2D bandStyle(Style2D style) {
    Style2D band = style.clone();
    band.faceColor = null;
    band.fillColor = style.strokeColor;
    band.edgeFormSet = false;
    band.edgeColor = null;
    band.opacity = style.opacity * BAND_OPACITY;
    return band;
  }

  /** The upper limits from left to right and the lower limits back, as one polygon. */
  private static Prim2D band(List<Marker> group, Style2D style, boolean sort) {
    List<Marker> points = new ArrayList<>(group);
    if (sort) {
      points.sort(Comparator.comparingDouble(m -> m.x.center));
    }
    List<double[]> outline = new ArrayList<>(2 * points.size());
    for (Marker m : points) {
      outline.add(new double[] {m.x.center, m.y.hi});
    }
    for (int i = points.size() - 1; i >= 0; i--) {
      Marker m = points.get(i);
      outline.add(new double[] {m.x.center, m.y.lo});
    }
    return new Prim2D.PolygonPrim(outline, style);
  }

  private static List<double[]> segment(double x1, double y1, double x2, double y2) {
    List<double[]> seg = new ArrayList<>(2);
    seg.add(new double[] {x1, y1});
    seg.add(new double[] {x2, y2});
    return seg;
  }

  /**
   * Add a cap line after each fenced bar in {@code primitives}, perpendicular to it and sized from
   * {@code bounds}, the data range of the whole picture. The cap is laid out where the axes are
   * linear, after their {@code ScalingFunctions}, so it keeps its length along a log axis.
   */
  static List<Prim2D> addFenceCaps(List<Prim2D> primitives, Bounds2D bounds,
      GraphicsOptions2D options) {
    boolean fenced = false;
    for (Prim2D p : primitives) {
      if (p instanceof Prim2D.LinePrim && ((Prim2D.LinePrim) p).fenceCaps != null) {
        fenced = true;
        break;
      }
    }
    if (!fenced) {
      return primitives;
    }
    DoubleUnaryOperator fx = GraphicsOptions.getScalingFunction(options.scalingX);
    DoubleUnaryOperator fy = GraphicsOptions.getScalingFunction(options.scalingY);
    DoubleUnaryOperator gx = GraphicsOptions.getInverseScalingFunction(options.scalingX);
    DoubleUnaryOperator gy = GraphicsOptions.getInverseScalingFunction(options.scalingY);
    double rangeX = bounds.isEmpty() ? 0
        : Math.abs(fx.applyAsDouble(bounds.xMax) - fx.applyAsDouble(bounds.xMin));
    double rangeY = bounds.isEmpty() ? 0
        : Math.abs(fy.applyAsDouble(bounds.yMax) - fy.applyAsDouble(bounds.yMin));
    if (!Double.isFinite(rangeX)) {
      rangeX = 0;
    }
    if (!Double.isFinite(rangeY)) {
      rangeY = 0;
    }
    List<Prim2D> out = new ArrayList<>(primitives.size() + 4);
    for (Prim2D p : primitives) {
      out.add(p);
      if (!(p instanceof Prim2D.LinePrim) || ((Prim2D.LinePrim) p).fenceCaps == null) {
        continue;
      }
      Prim2D.LinePrim bars = (Prim2D.LinePrim) p;
      List<List<double[]>> caps = new ArrayList<>();
      for (int i = 0; i < bars.segments.size() && i < bars.fenceCaps.size(); i++) {
        List<double[]> seg = bars.segments.get(i);
        if (seg.size() < 2) {
          continue;
        }
        double[] a = scaled(seg.get(0), fx, fy);
        double[] b = scaled(seg.get(seg.size() - 1), fx, fy);
        double dx = b[0] - a[0];
        double dy = b[1] - a[1];
        double length = Math.hypot(dx, dy);
        if (length == 0 || !Double.isFinite(length)) {
          continue;
        }
        // the unit normal, scaled per axis by that axis' range so a cap looks the same length
        // whatever the aspect ratio of the data
        double nx = -dy / length;
        double ny = dx / length;
        double hx = nx * CAP_FRACTION * (rangeX > 0 ? rangeX : 10 * length);
        double hy = ny * CAP_FRACTION * (rangeY > 0 ? rangeY : 10 * length);
        boolean[] ends = bars.fenceCaps.get(i);
        if (ends[0]) {
          caps.add(unscaledSegment(a, hx, hy, gx, gy));
        }
        if (ends[1]) {
          caps.add(unscaledSegment(b, hx, hy, gx, gy));
        }
      }
      if (!caps.isEmpty()) {
        out.add(new Prim2D.LinePrim(caps, false, bars.style));
      }
    }
    return out;
  }

  private static double[] scaled(double[] p, DoubleUnaryOperator fx, DoubleUnaryOperator fy) {
    return new double[] {fx.applyAsDouble(p[0]), fy.applyAsDouble(p[1])};
  }

  /** The cap through the scaled point {@code p}, back in data coordinates. */
  private static List<double[]> unscaledSegment(double[] p, double hx, double hy,
      DoubleUnaryOperator gx, DoubleUnaryOperator gy) {
    return segment(gx.applyAsDouble(p[0] - hx), gy.applyAsDouble(p[1] - hy),
        gx.applyAsDouble(p[0] + hx), gy.applyAsDouble(p[1] + hy));
  }
}
