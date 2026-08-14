package org.matheclipse.core.graphics.webgl;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A homogeneous 4x4 transformation, stored row major.
 *
 * <p>
 * A transformation is passed to the renderer rather than baked into the coordinates, because the
 * primitives that are described parametrically ({@code Sphere}, {@code Cylinder}, the regular
 * polyhedra) have no coordinate list to bake it into: a scaled sphere is an ellipsoid, which only
 * exists once the geometry has been built.
 */
public final class Transform3D {

  public static final Transform3D IDENTITY =
      new Transform3D(new double[] {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1});

  private final double[] m;

  private Transform3D(double[] m) {
    this.m = m;
  }

  public boolean isIdentity() {
    for (int i = 0; i < 16; i++) {
      if (Math.abs(m[i] - IDENTITY.m[i]) > 1e-12) {
        return false;
      }
    }
    return true;
  }

  /** The matrix in the column major order {@code THREE.Matrix4.fromArray} expects. */
  public double[] columnMajor() {
    double[] out = new double[16];
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++) {
        out[col * 4 + row] = m[row * 4 + col];
      }
    }
    return out;
  }

  /** The image of a point under this transformation. */
  public double[] apply(double[] p) {
    return new double[] { //
        m[0] * p[0] + m[1] * p[1] + m[2] * p[2] + m[3], //
        m[4] * p[0] + m[5] * p[1] + m[6] * p[2] + m[7], //
        m[8] * p[0] + m[9] * p[1] + m[10] * p[2] + m[11]};
  }

  /** The largest factor by which this transformation can stretch a length. */
  public double maxScale() {
    double best = 0;
    for (int col = 0; col < 3; col++) {
      double sum = 0;
      for (int row = 0; row < 3; row++) {
        double v = m[row * 4 + col];
        sum += v * v;
      }
      best = Math.max(best, Math.sqrt(sum));
    }
    return best <= 0 ? 1.0 : best;
  }

  public Transform3D times(Transform3D other) {
    double[] out = new double[16];
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++) {
        double sum = 0;
        for (int k = 0; k < 4; k++) {
          sum += m[row * 4 + k] * other.m[k * 4 + col];
        }
        out[row * 4 + col] = sum;
      }
    }
    return new Transform3D(out);
  }

  public static Transform3D translation(double[] v) {
    return new Transform3D(new double[] {1, 0, 0, v[0], 0, 1, 0, v[1], 0, 0, 1, v[2], 0, 0, 0, 1});
  }

  public static Transform3D scaling(double[] s) {
    return new Transform3D(new double[] {s[0], 0, 0, 0, 0, s[1], 0, 0, 0, 0, s[2], 0, 0, 0, 0, 1});
  }

  /** A rotation of {@code angle} radians about {@code axis}, by Rodrigues' formula. */
  public static Transform3D rotation(double angle, double[] axis) {
    double length = Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
    if (!(length > 0)) {
      return IDENTITY;
    }
    double x = axis[0] / length;
    double y = axis[1] / length;
    double z = axis[2] / length;
    double c = Math.cos(angle);
    double s = Math.sin(angle);
    double t = 1 - c;
    return new Transform3D(new double[] { //
        t * x * x + c, t * x * y - s * z, t * x * z + s * y, 0, //
        t * x * y + s * z, t * y * y + c, t * y * z - s * x, 0, //
        t * x * z - s * y, t * y * z + s * x, t * z * z + c, 0, //
        0, 0, 0, 1});
  }

  /**
   * The transformation a {@code GeometricTransformation} second argument denotes: a
   * {@code TransformationFunction}, a bare matrix, a {@code {matrix, vector}} pair, or a vector
   * standing for a translation.
   *
   * @return {@code null} when the expression is not a transformation
   */
  public static Transform3D fromExpr(IExpr expr) {
    if (expr.isAST(S.TransformationFunction, 2)) {
      return fromMatrix(((IAST) expr).arg1());
    }
    if (!expr.isList()) {
      return null;
    }
    IAST list = (IAST) expr;
    double[] vector = GraphicsOptions3D.vector(expr);
    if (vector != null) {
      return translation(vector);
    }
    if (list.argSize() == 2 && list.arg1().isList() && list.arg2().isList()) {
      Transform3D linear = fromMatrix(list.arg1());
      double[] shift = GraphicsOptions3D.vector(list.arg2());
      if (linear != null && shift != null) {
        return translation(shift).times(linear);
      }
      return linear;
    }
    return fromMatrix(expr);
  }

  /** A 3x3 linear map or a 4x4 homogeneous matrix, written as nested lists. */
  private static Transform3D fromMatrix(IExpr expr) {
    if (!expr.isList()) {
      return null;
    }
    IAST rows = (IAST) expr;
    int n = rows.argSize();
    if (n != 3 && n != 4) {
      return null;
    }
    double[] out = IDENTITY.m.clone();
    for (int row = 0; row < n; row++) {
      IExpr rowExpr = rows.get(row + 1);
      if (!rowExpr.isList() || ((IAST) rowExpr).argSize() < n) {
        return null;
      }
      IAST cells = (IAST) rowExpr;
      for (int col = 0; col < n; col++) {
        double value = ColorUtil.dbl(cells.get(col + 1), Double.NaN);
        if (!Double.isFinite(value)) {
          return null;
        }
        out[row * 4 + col] = value;
      }
    }
    return new Transform3D(out);
  }
}
