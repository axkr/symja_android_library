package org.matheclipse.core.graphics.svg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import j2html.tags.ContainerTag;

/**
 * The intermediate representation a graphic is collected into before anything is drawn.
 *
 * <p>
 * Collecting into typed primitives first, instead of emitting SVG during the traversal, is what
 * makes the rest of the converter simple: the bounding box is derived from the same objects that
 * get drawn (so the two can never disagree), coordinate transformations are a map over the list,
 * and the style in force is captured by value at collection time.
 */
public abstract class Prim2D {

  /** The directives in force where this primitive was collected. Never {@code null}. */
  public final Style2D style;

  protected Prim2D(Style2D style) {
    this.style = style == null ? new Style2D() : style;
  }

  /** Extend {@code bounds} to contain this primitive. */
  public abstract void accumulate(Bounds2D bounds);

  /** This primitive with its coordinates put through {@code map}. */
  public abstract Prim2D mapped(AffineMap2D map);

  /** Draw into {@code parent}, dispatching to the matching method of {@code renderer}. */
  public abstract void render(SvgRenderer2D renderer, ContainerTag<?> parent);

  static List<double[]> mapPoints(List<double[]> points, AffineMap2D map) {
    List<double[]> out = new ArrayList<>(points.size());
    for (double[] p : points) {
      out.add(map.apply(p));
    }
    return out;
  }

  // ---------------------------------------------------------------- points

  /** {@code Point[...]}, one or many. */
  public static final class PointsPrim extends Prim2D {
    public final List<double[]> points;

    public PointsPrim(List<double[]> points, Style2D style) {
      super(style);
      this.points = points;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      for (double[] p : points) {
        bounds.add(p);
      }
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      return new PointsPrim(mapPoints(points, map), style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawPoints(this, parent);
    }
  }

  // ----------------------------------------------------------------- lines

  /** {@code Line[...]}, {@code JoinedCurve[...]} and the stroked outline of other shapes. */
  public static final class LinePrim extends Prim2D {
    public final List<List<double[]>> segments;
    public final boolean closed;

    public LinePrim(List<List<double[]>> segments, boolean closed, Style2D style) {
      super(style);
      this.segments = segments;
      this.closed = closed;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      for (List<double[]> seg : segments) {
        for (double[] p : seg) {
          bounds.add(p);
        }
      }
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      List<List<double[]>> out = new ArrayList<>(segments.size());
      for (List<double[]> seg : segments) {
        out.add(mapPoints(seg, map));
      }
      return new LinePrim(out, closed, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawLine(this, parent);
    }
  }

  // -------------------------------------------------------------- polygons

  /** {@code Polygon[...]}, including the {@code outer -> holes} form. */
  public static final class PolygonPrim extends Prim2D {
    public final List<double[]> outer;
    public final List<List<double[]>> holes;

    public PolygonPrim(List<double[]> outer, List<List<double[]>> holes, Style2D style) {
      super(style);
      this.outer = outer;
      this.holes = holes == null ? new ArrayList<>() : holes;
    }

    public PolygonPrim(List<double[]> outer, Style2D style) {
      this(outer, null, style);
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      for (double[] p : outer) {
        bounds.add(p);
      }
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      List<List<double[]>> mappedHoles = new ArrayList<>(holes.size());
      for (List<double[]> h : holes) {
        mappedHoles.add(mapPoints(h, map));
      }
      return new PolygonPrim(mapPoints(outer, map), mappedHoles, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawPolygon(this, parent);
    }
  }

  // ------------------------------------------------------------- rectangle

  /** {@code Rectangle[...]}, with an optional {@code RoundingRadius}. */
  public static final class RectPrim extends Prim2D {
    public final double x1;
    public final double y1;
    public final double x2;
    public final double y2;
    public final double rounding;

    public RectPrim(double x1, double y1, double x2, double y2, double rounding, Style2D style) {
      super(style);
      this.x1 = Math.min(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.x2 = Math.max(x1, x2);
      this.y2 = Math.max(y1, y2);
      this.rounding = rounding;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      bounds.add(x1, y1);
      bounds.add(x2, y2);
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      if (map.isAxisAligned()) {
        double[] a = map.apply(x1, y1);
        double[] b = map.apply(x2, y2);
        return new RectPrim(a[0], a[1], b[0], b[1], rounding, style);
      }
      // a rotated or sheared rectangle is no longer axis aligned, so it becomes a polygon
      List<double[]> corners = new ArrayList<>(4);
      corners.add(map.apply(x1, y1));
      corners.add(map.apply(x2, y1));
      corners.add(map.apply(x2, y2));
      corners.add(map.apply(x1, y2));
      return new PolygonPrim(corners, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawRect(this, parent);
    }
  }

  // --------------------------------------------------------------- ellipse

  /**
   * {@code Circle}, {@code Disk} and {@code Annulus} in all their argument forms: an optionally
   * filled elliptical ring segment.
   */
  public static final class EllipsePrim extends Prim2D {
    public final double cx;
    public final double cy;
    public final double rx;
    public final double ry;
    /** Inner radii, both zero for a plain disk or circle. */
    public final double innerRx;
    public final double innerRy;
    /** Rotation of the x semi axis, in radians. */
    public final double rotation;
    /** Angular extent; {@code null} for a full turn. */
    public final double[] angles;
    /** True for {@code Disk} and {@code Annulus}, false for the stroked {@code Circle}. */
    public final boolean filled;

    public EllipsePrim(double cx, double cy, double rx, double ry, double innerRx, double innerRy,
        double rotation, double[] angles, boolean filled, Style2D style) {
      super(style);
      this.cx = cx;
      this.cy = cy;
      this.rx = Math.abs(rx);
      this.ry = Math.abs(ry);
      this.innerRx = Math.abs(innerRx);
      this.innerRy = Math.abs(innerRy);
      this.rotation = rotation;
      this.angles = angles;
      this.filled = filled;
    }

    public boolean isFullTurn() {
      return angles == null || Math.abs(Math.abs(angles[1] - angles[0]) - 2 * Math.PI) < 1e-9
          || Math.abs(angles[1] - angles[0]) > 2 * Math.PI;
    }

    public boolean isAnnulus() {
      return innerRx > 0 || innerRy > 0;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      // the extent of a rotated ellipse, which is exact and cheap
      double ux = rx * Math.cos(rotation);
      double uy = rx * Math.sin(rotation);
      double vx = ry * Math.cos(rotation + Math.PI / 2);
      double vy = ry * Math.sin(rotation + Math.PI / 2);
      double halfW = Math.hypot(ux, vx);
      double halfH = Math.hypot(uy, vy);
      bounds.add(cx - halfW, cy - halfH);
      bounds.add(cx + halfW, cy + halfH);
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      double[] c = map.apply(cx, cy);
      if (map.isSimilarity()) {
        double s = map.uniformScale();
        return new EllipsePrim(c[0], c[1], rx * s, ry * s, innerRx * s, innerRy * s,
            rotation + map.rotationAngle(), angles, filled, style);
      }
      if (map.isAxisAligned() && Math.abs(rotation) < 1e-9) {
        return new EllipsePrim(c[0], c[1], rx * Math.abs(map.m00), ry * Math.abs(map.m11),
            innerRx * Math.abs(map.m00), innerRy * Math.abs(map.m11), 0, angles, filled, style);
      }
      // a general map can shear the ellipse into something SVG cannot express directly
      List<double[]> pts = mapPoints(flatten(64), map);
      return filled ? new PolygonPrim(pts, style)
          : new LinePrim(new ArrayList<>(Arrays.asList(pts)), true, style);
    }

    /** The outline as a point list, used when an exact ellipse cannot be kept. */
    public List<double[]> flatten(int steps) {
      double a0 = angles == null ? 0 : angles[0];
      double a1 = angles == null ? 2 * Math.PI : angles[1];
      List<double[]> pts = new ArrayList<>(steps + 1);
      double cos = Math.cos(rotation);
      double sin = Math.sin(rotation);
      for (int i = 0; i <= steps; i++) {
        double t = a0 + (a1 - a0) * i / steps;
        double ex = rx * Math.cos(t);
        double ey = ry * Math.sin(t);
        pts.add(new double[] {cx + ex * cos - ey * sin, cy + ex * sin + ey * cos});
      }
      return pts;
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawEllipse(this, parent);
    }
  }

  // ------------------------------------------------------------------ text

  /** {@code Text[...]} and the textual form of {@code Inset}. */
  public static final class TextPrim extends Prim2D {
    public final String text;
    public final double x;
    public final double y;
    /** Which point of the label box sits at {@code (x, y)}, each component in -1..1. */
    public final double offsetX;
    public final double offsetY;
    /** Text direction; {@code {1, 0}} is the usual left to right. */
    public final double dirX;
    public final double dirY;
    public final Color frameColor;
    public final Color background;

    public TextPrim(String text, double x, double y, double offsetX, double offsetY, double dirX,
        double dirY, Color frameColor, Color background, Style2D style) {
      super(style);
      this.text = text;
      this.x = x;
      this.y = y;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.dirX = dirX;
      this.dirY = dirY;
      this.frameColor = frameColor;
      this.background = background;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      bounds.add(x, y);
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      double[] p = map.apply(x, y);
      return new TextPrim(text, p[0], p[1], offsetX, offsetY, dirX, dirY, frameColor, background,
          style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawText(this, parent);
    }
  }

  // ----------------------------------------------------------------- arrow

  /** {@code Arrow[...]} with its setback and arrowhead placement. */
  public static final class ArrowPrim extends Prim2D {
    public final List<double[]> points;
    public final double setbackStart;
    public final double setbackEnd;

    public ArrowPrim(List<double[]> points, double setbackStart, double setbackEnd, Style2D style) {
      super(style);
      this.points = points;
      this.setbackStart = setbackStart;
      this.setbackEnd = setbackEnd;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      for (double[] p : points) {
        bounds.add(p);
      }
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      return new ArrowPrim(mapPoints(points, map), setbackStart, setbackEnd, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawArrow(this, parent);
    }
  }

  // ---------------------------------------------------------------- curves

  /** {@code BezierCurve[...]}. */
  public static final class BezierPrim extends Prim2D {
    public final List<double[]> points;
    public final int degree;
    public final boolean filled;

    public BezierPrim(List<double[]> points, int degree, boolean filled, Style2D style) {
      super(style);
      this.points = points;
      this.degree = degree;
      this.filled = filled;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      for (double[] p : points) {
        bounds.add(p);
      }
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      return new BezierPrim(mapPoints(points, map), degree, filled, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawBezier(this, parent);
    }
  }

  /** {@code BSplineCurve[...]}, already evaluated into a polyline by the collector. */
  public static final class BSplinePrim extends Prim2D {
    public final List<double[]> curve;
    public final boolean closed;
    public final boolean filled;

    public BSplinePrim(List<double[]> curve, boolean closed, boolean filled, Style2D style) {
      super(style);
      this.curve = curve;
      this.closed = closed;
      this.filled = filled;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      for (double[] p : curve) {
        bounds.add(p);
      }
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      return new BSplinePrim(mapPoints(curve, map), closed, filled, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawBSpline(this, parent);
    }
  }

  // ---------------------------------------------------------------- raster

  /**
   * {@code Raster[...]}. Rendered as a grid of rectangles while equal neighbours merge into few
   * enough of them, and as an embedded PNG once they do not.
   */
  public static final class RasterPrim extends Prim2D {
    /** Row 0 is the bottom row, in the coordinate convention. */
    public final Color[][] cells;
    public final double x1;
    public final double y1;
    public final double x2;
    public final double y2;
    /**
     * Whether the cells are samples of something continuous rather than values in their own right.
     *
     * <p>
     * An {@code ArrayPlot} draws one cell per datum and each cell is a fact, so its edges have to
     * stay where they are. A domain colouring is a picture of a function that was sampled on a
     * grid, and the grid is an artefact of the sampling - drawing it crisply shows the reader the
     * sampling instead of the function, so it is smoothed between the samples.
     */
    public final boolean smooth;

    public RasterPrim(Color[][] cells, double x1, double y1, double x2, double y2, Style2D style) {
      this(cells, x1, y1, x2, y2, false, style);
    }

    public RasterPrim(Color[][] cells, double x1, double y1, double x2, double y2, boolean smooth,
        Style2D style) {
      super(style);
      this.cells = cells;
      this.smooth = smooth;
      this.x1 = Math.min(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.x2 = Math.max(x1, x2);
      this.y2 = Math.max(y1, y2);
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      bounds.add(x1, y1);
      bounds.add(x2, y2);
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      double[] a = map.apply(x1, y1);
      double[] b = map.apply(x2, y2);
      return new RasterPrim(cells, a[0], a[1], b[0], b[1], style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawRaster(this, parent);
    }
  }

  // ----------------------------------------------------------------- inset

  /** A complete sub picture placed by {@code Inset[graphic, pos]}. */
  public static final class InsetPrim extends Prim2D {
    public final String svg;
    public final double x;
    public final double y;
    public final double width;
    public final double height;
    /** Displacement of the anchor inside the sub picture, in its own pixels. */
    public final double alignX;
    public final double alignY;

    public InsetPrim(String svg, double x, double y, double width, double height, double alignX,
        double alignY, Style2D style) {
      super(style);
      this.svg = svg;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.alignX = alignX;
      this.alignY = alignY;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      bounds.add(x, y);
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      double[] p = map.apply(x, y);
      return new InsetPrim(svg, p[0], p[1], width, height, alignX, alignY, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawInset(this, parent);
    }
  }

  // ------------------------------------------------------------- half plane

  /**
   * {@code HalfPlane}, {@code InfiniteLine} and {@code InfinitePlane}. The drawn polygon is built
   * at render time so that it always reaches past the visible plot range.
   */
  public static final class HalfPlanePrim extends Prim2D {
    public final double px;
    public final double py;
    public final double vx;
    public final double vy;
    /** Which side of the line is filled; zero length means "the whole plane". */
    public final double wx;
    public final double wy;
    public final boolean full;
    public final boolean lineOnly;

    public HalfPlanePrim(double px, double py, double vx, double vy, double wx, double wy,
        boolean full, boolean lineOnly, Style2D style) {
      super(style);
      this.px = px;
      this.py = py;
      this.vx = vx;
      this.vy = vy;
      this.wx = wx;
      this.wy = wy;
      this.full = full;
      this.lineOnly = lineOnly;
    }

    @Override
    public void accumulate(Bounds2D bounds) {
      // an unbounded region must not drive the plot range on its own; only the anchor counts
      bounds.add(px, py);
    }

    @Override
    public Prim2D mapped(AffineMap2D map) {
      double[] p = map.apply(px, py);
      double[] v = map.apply(px + vx, py + vy);
      double[] w = map.apply(px + wx, py + wy);
      return new HalfPlanePrim(p[0], p[1], v[0] - p[0], v[1] - p[1], w[0] - p[0], w[1] - p[1], full,
          lineOnly, style);
    }

    @Override
    public void render(SvgRenderer2D renderer, ContainerTag<?> parent) {
      renderer.drawHalfPlane(this, parent);
    }
  }
}
