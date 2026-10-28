package org.matheclipse.core.graphics;

import static org.matheclipse.core.graphics.Vec3.add;
import static org.matheclipse.core.graphics.Vec3.cross;
import static org.matheclipse.core.graphics.Vec3.dot;
import static org.matheclipse.core.graphics.Vec3.length;
import static org.matheclipse.core.graphics.Vec3.normalize;
import static org.matheclipse.core.graphics.Vec3.scale;
import static org.matheclipse.core.graphics.Vec3.subtract;

/**
 * The rings of points a tube is built from: one circle round each point of its path.
 *
 * <p>
 * The frame each circle is laid out in is carried along the path rather than chosen afresh at
 * every point. Picking an arbitrary perpendicular each time lets the frame spin between one ring
 * and the next, and the quads joining them come out twisted into bow ties instead of a tube wall.
 * Carried once round a closed path, the frame comes back turned by however much the path twists;
 * that turn is spread evenly over the whole tube, so the last ring meets the first without a kink.
 */
public final class TubeRings {

  /** The rings round each point of the path, and the outward normal at each of their points. */
  public static final class Rings {
    /** <code>points[i][j]</code>: the point at angle <code>j</code> of the ring round point i. */
    public final double[][][] points;
    /** The unit outward normal at the same place. */
    public final double[][][] normals;

    Rings(double[][][] points, double[][][] normals) {
      this.points = points;
      this.normals = normals;
    }
  }

  /**
   * @param path the points of the path, at least two; for a closed path the last one is not
   *        repeated at the end
   * @param radius the radius of the tube
   * @param sides the number of points on each ring
   * @param closed whether the path runs on from its last point to its first
   */
  public static Rings of(double[][] path, double radius, int sides, boolean closed) {
    int n = path.length;
    double[][] tangents = new double[n][];
    double[][] us = new double[n][];
    double[] carried = null;
    for (int i = 0; i < n; i++) {
      double[] tangent;
      if (closed) {
        tangent = normalize(subtract(path[(i + 1) % n], path[(i + n - 1) % n]));
      } else {
        tangent = normalize(i == 0 ? subtract(path[1], path[0]) : subtract(path[i], path[i - 1]));
      }
      double[] u = carried == null ? perpendicular(tangent)
          : subtract(carried, scale(tangent, dot(carried, tangent)));
      if (length(u) < 1e-9) {
        u = perpendicular(tangent);
      }
      u = normalize(u);
      carried = u;
      tangents[i] = tangent;
      us[i] = u;
    }
    double twist = 0;
    if (closed) {
      double[] t0 = tangents[0];
      double[] back = subtract(carried, scale(t0, dot(carried, t0)));
      if (length(back) > 1e-9) {
        back = normalize(back);
        twist = Math.atan2(dot(cross(us[0], back), t0), dot(us[0], back));
      }
    }
    double[][][] points = new double[n][sides][];
    double[][][] normals = new double[n][sides][];
    for (int i = 0; i < n; i++) {
      double[] v = cross(tangents[i], us[i]);
      double turn = -twist * i / n;
      for (int j = 0; j < sides; j++) {
        double a = 2 * Math.PI * j / sides + turn;
        double[] normal = add(scale(us[i], Math.cos(a)), scale(v, Math.sin(a)));
        normals[i][j] = normal;
        points[i][j] = add(path[i], scale(normal, radius));
      }
    }
    return new Rings(points, normals);
  }

  private static double[] perpendicular(double[] axis) {
    double[] candidate = Math.abs(axis[0]) < 0.9 ? new double[] {1, 0, 0} : new double[] {0, 1, 0};
    return normalize(cross(axis, candidate));
  }

  private TubeRings() {}
}
