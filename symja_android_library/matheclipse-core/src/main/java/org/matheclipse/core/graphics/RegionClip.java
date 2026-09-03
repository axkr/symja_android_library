package org.matheclipse.core.graphics;

/**
 * Cuts a cell of a sampling grid along the edge of a {@code RegionFunction}.
 *
 * <p>
 * Masking a grid can only ever drop whole cells, so the edge of a region comes out as a staircase
 * with one step per sample. What follows the region instead is the line through the two points
 * where it crosses the cell's own edges: inside one cell a smooth boundary is very nearly straight,
 * so clipping the cell to that line is both cheap and, at any reasonable sampling density, visually
 * exact.
 *
 * <p>
 * The crossings themselves are found by bisection. A {@code RegionFunction} answers yes or no and
 * nothing else - there is no value to interpolate between, the way a contour level interpolates
 * between two heights - so the only way to place the boundary inside an edge is to keep halving it.
 */
public final class RegionClip {

  private RegionClip() {}

  /** How many times an edge is halved when locating the boundary on it. */
  public static final int CROSSING_ITERATIONS = 12;

  /** Whether a position belongs to the region. */
  @FunctionalInterface
  public interface Membership {
    boolean inside(double x, double y);
  }

  /**
   * The point on the segment between an accepted and a rejected position where the region ends.
   *
   * <p>
   * The two ends must disagree; the result is within {@code 2^-CROSSING_ITERATIONS} of the segment
   * length of the true crossing, which at the default twelve halvings is far below one pixel for
   * any grid a plot samples on.
   */
  public static double[] crossing(Membership member, double insideX, double insideY,
      double outsideX, double outsideY) {
    double lo = 0.0;
    double hi = 1.0;
    for (int k = 0; k < CROSSING_ITERATIONS; k++) {
      double mid = (lo + hi) / 2.0;
      if (member.inside(insideX + mid * (outsideX - insideX),
          insideY + mid * (outsideY - insideY))) {
        lo = mid;
      } else {
        hi = mid;
      }
    }
    double t = (lo + hi) / 2.0;
    return new double[] {insideX + t * (outsideX - insideX), insideY + t * (outsideY - insideY)};
  }

  /**
   * Which side of the line through {@code a} and {@code b} a point lies on.
   *
   * <p>
   * The sign is all that is used, so the un-normalised cross product is enough.
   */
  private static double side(double[] a, double[] b, double x, double y) {
    return (b[0] - a[0]) * (y - a[1]) - (b[1] - a[1]) * (x - a[0]);
  }

  /**
   * The part of a convex polygon on the region's side of the line through {@code a} and {@code b}.
   *
   * <p>
   * Sutherland-Hodgman against a single half plane. Every polygon this is used on is a grid cell or
   * a piece of one that marching squares already cut off with a straight line, so all of them are
   * convex and one pass suffices.
   *
   * @param polygon the corners, in order
   * @param a one point the region boundary passes through
   * @param b the other
   * @param refX a position known to be inside the region, which fixes which side to keep
   * @param refY see {@code refX}
   * @return the clipped corners, or {@code null} when nothing is left
   */
  public static double[][] clipPolygon(double[][] polygon, double[] a, double[] b, double refX,
      double refY) {
    double keep = side(a, b, refX, refY);
    if (keep == 0.0) {
      return polygon; // the reference point is on the line; nothing can be decided, keep it whole
    }
    int n = polygon.length;
    double[][] out = new double[n + 2][];
    int count = 0;
    for (int i = 0; i < n; i++) {
      double[] current = polygon[i];
      double[] next = polygon[(i + 1) % n];
      double sc = side(a, b, current[0], current[1]) * keep;
      double sn = side(a, b, next[0], next[1]) * keep;
      if (sc >= 0) {
        out[count++] = current;
      }
      if ((sc > 0 && sn < 0) || (sc < 0 && sn > 0)) {
        out[count++] = intersect(current, next, a, b);
      }
    }
    if (count < 3) {
      return null;
    }
    double[][] clipped = new double[count][];
    System.arraycopy(out, 0, clipped, 0, count);
    return clipped;
  }

  /**
   * The part of a segment on the region's side of the line through {@code a} and {@code b}.
   *
   * @return the clipped segment, or {@code null} when none of it is inside
   */
  public static double[][] clipSegment(double[] from, double[] to, double[] a, double[] b,
      double refX, double refY) {
    double keep = side(a, b, refX, refY);
    if (keep == 0.0) {
      return new double[][] {from, to};
    }
    double sf = side(a, b, from[0], from[1]) * keep;
    double st = side(a, b, to[0], to[1]) * keep;
    if (sf >= 0 && st >= 0) {
      return new double[][] {from, to};
    }
    if (sf < 0 && st < 0) {
      return null;
    }
    double[] cut = intersect(from, to, a, b);
    return sf >= 0 ? new double[][] {from, cut} : new double[][] {cut, to};
  }

  /** Where the segment {@code p -> q} meets the line through {@code a} and {@code b}. */
  private static double[] intersect(double[] p, double[] q, double[] a, double[] b) {
    double sp = side(a, b, p[0], p[1]);
    double sq = side(a, b, q[0], q[1]);
    double denominator = sp - sq;
    if (denominator == 0.0) {
      return p;
    }
    double t = sp / denominator;
    return new double[] {p[0] + t * (q[0] - p[0]), p[1] + t * (q[1] - p[1])};
  }

  /**
   * The line the region boundary follows through one cell, or {@code null} when the cell needs no
   * clipping or cannot be clipped by a single line.
   *
   * <p>
   * The corners are given in order round the cell. A cell whose inside corners are diagonally
   * opposite is crossed twice by the boundary and a single line cannot describe it; such a cell is
   * reported as unclippable, and the caller leaves it out as it did before. At any density where a
   * region is worth drawing these are rare, and each is one cell across.
   *
   * @param corners the four cell corners, in order round it
   * @param inside whether each corner belongs to the region, in the same order
   * @return {@code {pointA, pointB, referencePointInside}}, or {@code null}
   */
  public static double[][] cellBoundary(Membership member, double[][] corners, boolean[] inside) {
    double[][] crossings = new double[4][];
    int found = 0;
    for (int i = 0; i < 4; i++) {
      int next = (i + 1) % 4;
      if (inside[i] == inside[next]) {
        continue;
      }
      if (found == 2) {
        return null; // crossed more than twice: not one straight edge
      }
      crossings[found++] = inside[i]
          ? crossing(member, corners[i][0], corners[i][1], corners[next][0], corners[next][1])
          : crossing(member, corners[next][0], corners[next][1], corners[i][0], corners[i][1]);
    }
    if (found != 2) {
      return null;
    }
    double refX = 0.0;
    double refY = 0.0;
    int insideCount = 0;
    for (int i = 0; i < 4; i++) {
      if (inside[i]) {
        refX += corners[i][0];
        refY += corners[i][1];
        insideCount++;
      }
    }
    if (insideCount == 0) {
      return null;
    }
    return new double[][] {crossings[0], crossings[1],
        {refX / insideCount, refY / insideCount}};
  }
}
