package org.matheclipse.core.graphics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flat pictures of a convex polyhedron: the Schlegel diagram of its skeleton and an unfolding of
 * its surface into a net.
 *
 * <p>
 * The polyhedron is given by machine-number vertex coordinates and its faces as 0-based vertex
 * index cycles, each counterclockwise seen from outside. Both layouts only work for a convex solid
 * with convex faces.
 */
public final class PolyhedronLayouts {

  /** Coordinates closer than this are the same point of a net. */
  private static final double SAME_POINT = 1.0e-9;

  /** Polygons of a net which overlap by less than this only touch. */
  private static final double OVERLAP_SLACK = 1.0e-7;

  private PolyhedronLayouts() {}

  /** An unfolded surface: the 2D points and the faces as 0-based index cycles into them. */
  public static final class Net {
    public final double[][] points;
    public final List<int[]> faces;

    Net(double[][] points, List<int[]> faces) {
      this.points = points;
      this.faces = faces;
    }
  }

  /**
   * The Schlegel diagram: the vertices seen in perspective from a point just outside the largest
   * face, projected into the plane of that face. That face becomes the outer boundary and the edges
   * do not cross.
   *
   * @return the 2D coordinates of the vertices, in the order of <code>vertices</code>
   */
  public static double[][] schlegel(double[][] vertices, List<int[]> faces) {
    double[] solidCentre = centre(vertices);
    int outer = 0;
    for (int f = 1; f < faces.size(); f++) {
      if (faces.get(f).length > faces.get(outer).length) {
        outer = f;
      }
    }
    int[] face = faces.get(outer);
    double[] c = centroid(vertices, face);
    double[] n = outwardNormal(vertices, face, solidCentre);
    // the eye has to stay inside the half-space of every other face
    double limit = Vec3.length(Vec3.subtract(c, solidCentre)) + 1.0;
    for (int f = 0; f < faces.size(); f++) {
      if (f == outer) {
        continue;
      }
      double[] normal = outwardNormal(vertices, faces.get(f), solidCentre);
      double slope = Vec3.dot(normal, n);
      if (slope > 1.0e-12) {
        double room = Vec3.dot(normal, Vec3.subtract(vertices[faces.get(f)[0]], c)) / slope;
        limit = Math.min(limit, room);
      }
    }
    double d = limit / 2.0;
    double[] u = Vec3.normalize(Vec3.subtract(vertices[face[0]], c));
    double[] w = Vec3.cross(n, u);
    double[][] result = new double[vertices.length][];
    for (int i = 0; i < vertices.length; i++) {
      double[] radial = Vec3.subtract(vertices[i], c);
      double depth = d - Vec3.dot(radial, n);
      double t = d / depth;
      result[i] = new double[] {t * Vec3.dot(radial, u), t * Vec3.dot(radial, w)};
    }
    return result;
  }

  /**
   * A net of the surface: the faces unfolded along the edges of a breadth-first spanning tree of
   * the face adjacency, tried from every face in turn until no two faces of the unfolding overlap.
   *
   * @return <code>null</code> if no such unfolding was found
   */
  public static Net unfold(double[][] vertices, List<int[]> faces) {
    double[] solidCentre = centre(vertices);
    Map<Long, List<Integer>> facesOfEdge = new HashMap<Long, List<Integer>>();
    for (int f = 0; f < faces.size(); f++) {
      int[] face = faces.get(f);
      for (int i = 0; i < face.length; i++) {
        facesOfEdge.computeIfAbsent(edgeKey(face[i], face[(i + 1) % face.length]),
            k -> new ArrayList<Integer>()).add(Integer.valueOf(f));
      }
    }
    for (int root = 0; root < faces.size(); root++) {
      double[][][] placed = unfoldFrom(root, vertices, faces, facesOfEdge, solidCentre);
      if (placed != null && !overlapping(placed)) {
        return shareCorners(placed, faces);
      }
    }
    return null;
  }

  /**
   * The faces laid out in the plane, <code>placed[f][k]</code> the 2D point of the k-th corner of
   * face f, or <code>null</code> if the faces are not connected by edges.
   */
  private static double[][][] unfoldFrom(int root, double[][] vertices, List<int[]> faces,
      Map<Long, List<Integer>> facesOfEdge, double[] solidCentre) {
    double[][][] placed = new double[faces.size()][][];
    int[] rootFace = faces.get(root);
    double[] origin = vertices[rootFace[0]];
    double[] n = outwardNormal(vertices, rootFace, solidCentre);
    double[] u = Vec3.normalize(Vec3.subtract(vertices[rootFace[1]], origin));
    double[] w = Vec3.cross(n, u);
    placed[root] = new double[rootFace.length][];
    for (int k = 0; k < rootFace.length; k++) {
      double[] r = Vec3.subtract(vertices[rootFace[k]], origin);
      placed[root][k] = new double[] {Vec3.dot(r, u), Vec3.dot(r, w)};
    }
    ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
    queue.add(Integer.valueOf(root));
    int count = 1;
    while (!queue.isEmpty()) {
      int f = queue.poll().intValue();
      int[] face = faces.get(f);
      for (int k = 0; k < face.length; k++) {
        int a = face[k];
        int b = face[(k + 1) % face.length];
        for (Integer neighbour : facesOfEdge.get(edgeKey(a, b))) {
          int g = neighbour.intValue();
          if (placed[g] == null) {
            placed[g] = hinge(vertices, faces.get(g), a, b, placed[f][k],
                placed[f][(k + 1) % face.length], centroid2D(placed[f]));
            queue.add(neighbour);
            count++;
          }
        }
      }
    }
    return count == faces.size() ? placed : null;
  }

  /**
   * Turn the face <code>face</code> about its edge <code>a, b</code> into the plane, where the
   * edge already lies at <code>a2, b2</code> and the face on the other side has its centre at
   * <code>parentCentre</code>.
   */
  private static double[][] hinge(double[][] vertices, int[] face, int a, int b, double[] a2,
      double[] b2, double[] parentCentre) {
    double[] e1 = Vec3.normalize(Vec3.subtract(vertices[b], vertices[a]));
    double[] toCentre = Vec3.subtract(centroid(vertices, face), vertices[a]);
    double[] e2 =
        Vec3.normalize(Vec3.subtract(toCentre, Vec3.scale(e1, Vec3.dot(toCentre, e1))));
    double[] f1 = normalize2D(subtract2D(b2, a2));
    double[] f2 = {-f1[1], f1[0]};
    if (dot2D(f2, subtract2D(parentCentre, a2)) > 0) {
      f2 = new double[] {f1[1], -f1[0]};
    }
    double[][] result = new double[face.length][];
    for (int k = 0; k < face.length; k++) {
      double[] r = Vec3.subtract(vertices[face[k]], vertices[a]);
      double x = Vec3.dot(r, e1);
      double y = Vec3.dot(r, e2);
      result[k] = new double[] {a2[0] + x * f1[0] + y * f2[0], a2[1] + x * f1[1] + y * f2[1]};
    }
    return result;
  }

  /** Whether the interiors of two of the convex polygons intersect. */
  private static boolean overlapping(double[][][] placed) {
    for (int i = 0; i < placed.length; i++) {
      for (int j = i + 1; j < placed.length; j++) {
        if (!separated(placed[i], placed[j]) && !separated(placed[j], placed[i])) {
          return true;
        }
      }
    }
    return false;
  }

  /** Whether an edge line of <code>p</code> has all of <code>q</code> on its outer side. */
  private static boolean separated(double[][] p, double[][] q) {
    double[] centre = centroid2D(p);
    for (int k = 0; k < p.length; k++) {
      double[] from = p[k];
      double[] to = p[(k + 1) % p.length];
      double[] normal = normalize2D(new double[] {to[1] - from[1], from[0] - to[0]});
      if (dot2D(normal, subtract2D(centre, from)) > 0) {
        normal = new double[] {-normal[0], -normal[1]};
      }
      double nearest = Double.MAX_VALUE;
      for (double[] point : q) {
        nearest = Math.min(nearest, dot2D(normal, subtract2D(point, from)));
      }
      if (nearest > -OVERLAP_SLACK) {
        return true;
      }
    }
    return false;
  }

  /** One point for the corners of different faces which the unfolding put at the same place. */
  private static Net shareCorners(double[][][] placed, List<int[]> faces) {
    List<double[]> points = new ArrayList<double[]>();
    List<int[]> netFaces = new ArrayList<int[]>(faces.size());
    for (double[][] polygon : placed) {
      int[] indices = new int[polygon.length];
      for (int k = 0; k < polygon.length; k++) {
        int found = -1;
        for (int p = 0; p < points.size(); p++) {
          double[] d = subtract2D(points.get(p), polygon[k]);
          if (Math.hypot(d[0], d[1]) < SAME_POINT) {
            found = p;
            break;
          }
        }
        if (found < 0) {
          found = points.size();
          points.add(polygon[k]);
        }
        indices[k] = found;
      }
      netFaces.add(indices);
    }
    return new Net(points.toArray(new double[points.size()][]), netFaces);
  }

  private static Long edgeKey(int a, int b) {
    return Long.valueOf(((long) Math.min(a, b) << 32) | Math.max(a, b));
  }

  /** The unit normal of a face, pointing away from the centre of the solid. */
  private static double[] outwardNormal(double[][] vertices, int[] face, double[] solidCentre) {
    double[] middle = centroid(vertices, face);
    double[] normal = new double[3];
    for (int k = 0; k < face.length; k++) {
      normal = Vec3.add(normal, Vec3.cross(Vec3.subtract(vertices[face[k]], middle),
          Vec3.subtract(vertices[face[(k + 1) % face.length]], middle)));
    }
    normal = Vec3.normalize(normal);
    return Vec3.dot(normal, Vec3.subtract(middle, solidCentre)) < 0 ? Vec3.scale(normal, -1.0)
        : normal;
  }

  /** The mean of all the vertices. */
  private static double[] centre(double[][] vertices) {
    double[] sum = new double[3];
    for (double[] v : vertices) {
      sum = Vec3.add(sum, v);
    }
    return Vec3.scale(sum, 1.0 / vertices.length);
  }

  /** The mean of the corners of a face. */
  private static double[] centroid(double[][] vertices, int[] face) {
    double[] sum = new double[3];
    for (int index : face) {
      sum = Vec3.add(sum, vertices[index]);
    }
    return Vec3.scale(sum, 1.0 / face.length);
  }

  private static double[] centroid2D(double[][] polygon) {
    double x = 0;
    double y = 0;
    for (double[] p : polygon) {
      x += p[0];
      y += p[1];
    }
    return new double[] {x / polygon.length, y / polygon.length};
  }

  private static double[] subtract2D(double[] u, double[] v) {
    return new double[] {u[0] - v[0], u[1] - v[1]};
  }

  private static double dot2D(double[] u, double[] v) {
    return u[0] * v[0] + u[1] * v[1];
  }

  private static double[] normalize2D(double[] u) {
    double length = Math.hypot(u[0], u[1]);
    return length == 0 ? u : new double[] {u[0] / length, u[1] / length};
  }
}
