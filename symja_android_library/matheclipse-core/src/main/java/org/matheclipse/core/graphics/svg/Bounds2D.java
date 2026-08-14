package org.matheclipse.core.graphics.svg;

/** An axis aligned bounding box in data coordinates. */
public final class Bounds2D {

  public double xMin = Double.MAX_VALUE;
  public double xMax = -Double.MAX_VALUE;
  public double yMin = Double.MAX_VALUE;
  public double yMax = -Double.MAX_VALUE;

  /**
   * The smallest strictly positive coordinate seen on each axis.
   *
   * <p>
   * A logarithmic axis has no room for zero or a negative number, and it cannot simply take the
   * overall minimum: one non positive value in the data would otherwise decide the bottom of the
   * axis and squash everything real into a sliver at the top. Spans the positive data instead, and
   * this is what that range is read from.
   */
  public double xMinPositive = Double.MAX_VALUE;
  public double yMinPositive = Double.MAX_VALUE;

  public boolean isEmpty() {
    return xMin > xMax || yMin > yMax;
  }

  /** Extend the box to contain a point. Non finite coordinates are ignored. */
  public void add(double x, double y) {
    if (!Double.isFinite(x) || !Double.isFinite(y)) {
      return;
    }
    if (x < xMin) {
      xMin = x;
    }
    if (x > 0 && x < xMinPositive) {
      xMinPositive = x;
    }
    if (x > xMax) {
      xMax = x;
    }
    if (y < yMin) {
      yMin = y;
    }
    if (y > 0 && y < yMinPositive) {
      yMinPositive = y;
    }
    if (y > yMax) {
      yMax = y;
    }
  }

  public void add(double[] point) {
    if (point != null && point.length >= 2) {
      add(point[0], point[1]);
    }
  }

  public void merge(Bounds2D other) {
    if (other == null || other.isEmpty()) {
      return;
    }
    add(other.xMin, other.yMin);
    add(other.xMax, other.yMax);
    if (other.xMinPositive < xMinPositive) {
      xMinPositive = other.xMinPositive;
    }
    if (other.yMinPositive < yMinPositive) {
      yMinPositive = other.yMinPositive;
    }
  }

  /** True when at least one strictly positive coordinate was seen on that axis. */
  public boolean hasPositiveX() {
    return xMinPositive != Double.MAX_VALUE;
  }

  public boolean hasPositiveY() {
    return yMinPositive != Double.MAX_VALUE;
  }

  public double width() {
    return isEmpty() ? 0.0 : xMax - xMin;
  }

  public double height() {
    return isEmpty() ? 0.0 : yMax - yMin;
  }

  public double centerX() {
    return isEmpty() ? 0.0 : (xMin + xMax) / 2.0;
  }

  public double centerY() {
    return isEmpty() ? 0.0 : (yMin + yMax) / 2.0;
  }

  @Override
  public String toString() {
    return isEmpty() ? "Bounds2D[empty]"
        : "Bounds2D[" + xMin + ".." + xMax + ", " + yMin + ".." + yMax + "]";
  }
}
