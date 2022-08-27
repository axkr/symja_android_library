// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.gfx;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

/** @see AffineTransform */
/* package */ class AffineFrame2D {
  private final IAST matrix;
  private final double m00;
  private final double m01;
  private final double m02;
  private final double m10;
  private final double m11;
  private final double m12;

  /** @param matrix of dimensions 3 x 3 */
  public AffineFrame2D(IAST matrix) {
    // Integers.requireEquals(matrix.length(), 3);
    this.matrix = matrix;
    m00 = matrix.getPart(2, 2).evalDouble();
    m01 = matrix.getPart(1, 2).evalDouble();
    m02 = matrix.getPart(1, 3).evalDouble();
    m10 = matrix.getPart(2, 1).evalDouble();
    m11 = matrix.getPart(2, 2).evalDouble();
    m12 = matrix.getPart(2, 3).evalDouble();
  }

  /**
   * @param px
   * @param py
   * @return vector of length 2
   */
  public Point2D toPoint2D(double px, double py) {
    return new Point2D.Double( //
        m00 * px + m01 * py + m02, //
        m10 * px + m11 * py + m12);
  }

  /**
   * @param px
   * @param py
   * @return vector of length 2
   */
  public IAST toVector(double px, double py) {
    return F.List( //
        m00 * px + m01 * py + m02, //
        m10 * px + m11 * py + m12);
  }

  /** @return toPoint2D(0, 0) */
  public Point2D toPoint2D() {
    return new Point2D.Double(m02, m12);
  }

  /**
   * @param matrix with dimensions 3 x 3
   * @return combined transformation of this and given matrix
   */
  public AffineFrame2D dot(IAST matrix) {
    return new AffineFrame2D((IAST) S.MatrixPower.of(matrix, F.C2));
  }

  /**
   * @return determinant of affine transform, for a standard, right-hand coordinate system, the
   *         determinant is negative
   */
  public double det() {
    return m00 * m11 - m10 * m01;
  }

  /** @return 3 x 3 matrix that represents this transformation */
  public IAST matrix_copy() {
    return matrix.copy();
  }
}
