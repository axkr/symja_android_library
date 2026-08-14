package org.matheclipse.core.graphics.svg;

/**
 * A 2D affine map {@code p -> m . p + v}, used to implement {@code Rotate}, {@code Translate},
 * {@code Scale} and {@code GeometricTransformation}.
 *
 * <p>
 * Applying the map to the collected primitives, rather than emitting an SVG {@code transform}
 * attribute, keeps the result correct when the two axes have different scales: a rotated square in
 * a plot whose x and y pixel scales differ is not a square on screen.
 */
public final class AffineMap2D {

  public final double m00;
  public final double m01;
  public final double m10;
  public final double m11;
  public final double vx;
  public final double vy;

  public AffineMap2D(double m00, double m01, double m10, double m11, double vx, double vy) {
    this.m00 = m00;
    this.m01 = m01;
    this.m10 = m10;
    this.m11 = m11;
    this.vx = vx;
    this.vy = vy;
  }

  public static AffineMap2D identity() {
    return new AffineMap2D(1, 0, 0, 1, 0, 0);
  }

  public static AffineMap2D translation(double dx, double dy) {
    return new AffineMap2D(1, 0, 0, 1, dx, dy);
  }

  /** Rotation by {@code angle} radians counterclockwise about {@code (cx, cy)}. */
  public static AffineMap2D rotation(double angle, double cx, double cy) {
    double c = Math.cos(angle);
    double s = Math.sin(angle);
    return new AffineMap2D(c, -s, s, c, cx - c * cx + s * cy, cy - s * cx - c * cy);
  }

  /** Scaling by {@code (sx, sy)} about {@code (cx, cy)}. */
  public static AffineMap2D scaling(double sx, double sy, double cx, double cy) {
    return new AffineMap2D(sx, 0, 0, sy, cx - sx * cx, cy - sy * cy);
  }

  public double[] apply(double x, double y) {
    return new double[] {m00 * x + m01 * y + vx, m10 * x + m11 * y + vy};
  }

  public double[] apply(double[] p) {
    return apply(p[0], p[1]);
  }

  /** {@code this} followed by {@code outer}. */
  public AffineMap2D andThen(AffineMap2D outer) {
    return new AffineMap2D(//
        outer.m00 * m00 + outer.m01 * m10, //
        outer.m00 * m01 + outer.m01 * m11, //
        outer.m10 * m00 + outer.m11 * m10, //
        outer.m10 * m01 + outer.m11 * m11, //
        outer.m00 * vx + outer.m01 * vy + outer.vx, //
        outer.m10 * vx + outer.m11 * vy + outer.vy);
  }

  public boolean isIdentity() {
    return m00 == 1 && m01 == 0 && m10 == 0 && m11 == 1 && vx == 0 && vy == 0;
  }

  /**
   * True when the map is a rotation combined with a uniform scale and a translation, i.e. it takes
   * circles to circles. Such a map can be pushed into an ellipse primitive exactly; anything else
   * has to be flattened into a polygon first.
   */
  public boolean isSimilarity() {
    double tol = 1e-9;
    return Math.abs(m00 - m11) < tol && Math.abs(m01 + m10) < tol
        && Math.abs(m00 * m00 + m10 * m10) > tol;
  }

  /** True when the map scales the axes independently without rotating or shearing. */
  public boolean isAxisAligned() {
    return Math.abs(m01) < 1e-9 && Math.abs(m10) < 1e-9;
  }

  /** The rotation angle in radians, meaningful when {@link #isSimilarity()} holds. */
  public double rotationAngle() {
    return Math.atan2(m10, m00);
  }

  /** The uniform scale factor, meaningful when {@link #isSimilarity()} holds. */
  public double uniformScale() {
    return Math.hypot(m00, m10);
  }

  public double scaleX() {
    return Math.hypot(m00, m10);
  }

  public double scaleY() {
    return Math.hypot(m01, m11);
  }
}
