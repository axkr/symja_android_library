package org.matheclipse.core.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A builder utility to construct highly optimized GraphicsComplex objects. Separating coordinates
 * from topology drastically reduces AST sizes and ensures smooth VertexNormal interpolation across
 * adjacent faces in WebGL.
 */
public class GraphicsComplexBuilder {

  private final IASTAppendable vertices;
  private final IASTAppendable normals;
  private final IASTAppendable colors;
  private final IASTAppendable polygonIndices;
  private final IASTAppendable otherPrimitives;
  private final Map<String, Integer> vertexMap;
  /** The coordinates of the vertices in the order they were added. */
  private final List<double[]> coordinates;

  private int vertexCounter;
  private final boolean useNormals;
  private final boolean useColors;
  private final IASTAppendable styles;

  /**
   * Defines styling primitives (e.g. RGBColor, EdgeForm) that should be prepended before the
   * geometry in the final GraphicsComplex.
   */
  public void setStyle(IExpr... styleExprs) {
    for (IExpr style : styleExprs) {
      styles.append(style);
    }
  }

  /**
   * Assembles the final GraphicsComplex AST.
   * 
   * @return The AST expression or F.NIL if no vertices were added.
   */
  public IExpr build() {
    if (vertices.argSize() == 0) {
      return F.NIL;
    }

    IASTAppendable gc = F.ast(S.GraphicsComplex);
    gc.append(vertices);

    IASTAppendable allPrimitives = F.ListAlloc();

    // Prepend styles (Color, EdgeForm, Opacity, etc.)
    for (int i = 1; i <= styles.argSize(); i++) {
      allPrimitives.append(styles.get(i));
    }

    // Append geometry
    if (polygonIndices.argSize() > 0) {
      allPrimitives.append(F.Polygon(polygonIndices));
    }
    for (int i = 1; i <= otherPrimitives.argSize(); i++) {
      allPrimitives.append(otherPrimitives.get(i));
    }

    gc.append(allPrimitives);

    if (useNormals && normals.argSize() > 0) {
      gc.append(F.Rule(S.VertexNormals, normals));
    }
    if (useColors && colors.argSize() > 0) {
      gc.append(F.Rule(S.VertexColors, colors));
    }

    return gc;
  }

  public GraphicsComplexBuilder(boolean useNormals, boolean useColors) {
    this.vertices = F.ListAlloc();
    this.normals = useNormals ? F.ListAlloc() : null;
    this.colors = useColors ? F.ListAlloc() : null;
    this.polygonIndices = F.ListAlloc();
    this.otherPrimitives = F.ListAlloc();
    this.vertexMap = new HashMap<>();
    this.coordinates = new ArrayList<>();
    this.vertexCounter = 1;
    this.useNormals = useNormals;
    this.useColors = useColors;
    this.styles = F.ListAlloc();
  }

  /**
   * Adds a vertex to the pool, automatically welding it to existing vertices to guarantee a
   * seamless continuous mesh for lighting calculation.
   */
  public int addVertex(double x, double y, double z, double[] normal, IExpr color) {
    // Scaled rounding eliminates microscopic floating-point seam gaps
    long kx = Math.round(x * 1e5);
    long ky = Math.round(y * 1e5);
    long kz = Math.round(z * 1e5);
    String key = kx + "_" + ky + "_" + kz;

    if (!vertexMap.containsKey(key)) {
      vertexMap.put(key, vertexCounter++);
      vertices.append(F.List(F.num(x), F.num(y), F.num(z)));
      coordinates.add(new double[] {x, y, z});

      // the normal and colour lists are indexed by vertex number, so a vertex without one still
      // has to contribute an entry or everything after it refers to the wrong point
      if (useNormals) {
        normals.append(normal != null && normal.length >= 3
            ? F.List(F.num(normal[0]), F.num(normal[1]), F.num(normal[2]))
            : F.List(F.C0, F.C0, F.C1));
      }
      if (useColors) {
        colors.append(color != null ? color : S.Automatic);
      }
    }
    return vertexMap.get(key);
  }

  /** The normal of vertex {@code index}, counted from one, or {@code null} when there is none. */
  public double[] normalOf(int index) {
    if (normals == null || index < 1 || index > normals.argSize()) {
      return null;
    }
    IAST normal = (IAST) normals.get(index);
    return new double[] {normal.arg1().evalf(), normal.arg2().evalf(), normal.arg3().evalf()};
  }

  /** The colour of vertex {@code index}, counted from one, or {@code null} when there is none. */
  public IExpr colorOf(int index) {
    if (colors == null || index < 1 || index > colors.argSize()) {
      return null;
    }
    IExpr color = colors.get(index);
    return color == S.Automatic ? null : color;
  }

  /**
   * Append a triangle face to the shared polygon group.
   */
  public void addPolygon(int v1, int v2, int v3) {
    polygonIndices.append(F.List(F.ZZ(v1), F.ZZ(v2), F.ZZ(v3)));
  }

  /**
   * Append an arbitrary n-gon face.
   */
  public void addPolygon(int[] indices) {
    IASTAppendable polyArgs = F.ListAlloc(indices.length);
    for (int idx : indices) {
      polyArgs.append(F.ZZ(idx));
    }
    polygonIndices.append(polyArgs);
  }

  /**
   * Append independent primitives like Point or Line that rely on the index map.
   */
  public void addPrimitive(IExpr primitive) {
    otherPrimitives.append(primitive);
  }

  /**
   * The curves in which the plane {@code coordinate[axis] == value} cuts the polygons added so far,
   * each as a list of coordinate triples.
   *
   * <p>
   * A polygon edge whose ends lie on different sides of the plane is cut at the point found by
   * linear interpolation; an edge is identified by its two vertex numbers, so the polygons on
   * either side of it share the cut point and the pieces join into curves.
   *
   * @param axis {@code 0}, {@code 1} or {@code 2} for the x, y or z coordinate
   * @return the curves, each with at least two points
   */
  public List<List<double[]>> planeSections(int axis, double value) {
    // the cut points by edge, and for every cut point the (at most two) cut points it is joined to
    Map<Long, double[]> points = new HashMap<>();
    Map<Long, List<Long>> neighbours = new LinkedHashMap<>();
    long[] cut = new long[2];
    for (int p = 1; p <= polygonIndices.argSize(); p++) {
      IAST polygon = (IAST) polygonIndices.get(p);
      int corners = polygon.argSize();
      int cuts = 0;
      for (int k = 1; k <= corners && cuts <= 2; k++) {
        int a = polygon.get(k).toIntDefault();
        int b = polygon.get(k % corners + 1).toIntDefault();
        double[] pa = coordinates.get(a - 1);
        double[] pb = coordinates.get(b - 1);
        double da = pa[axis] - value;
        double db = pb[axis] - value;
        if ((da < 0.0) == (db < 0.0)) {
          continue;
        }
        long key = ((long) Math.min(a, b) << 32) | Math.max(a, b);
        if (!points.containsKey(key)) {
          double t = da / (da - db);
          points.put(key, new double[] {pa[0] + t * (pb[0] - pa[0]), pa[1] + t * (pb[1] - pa[1]),
              pa[2] + t * (pb[2] - pa[2])});
        }
        if (cuts < 2) {
          cut[cuts] = key;
        }
        cuts++;
      }
      if (cuts == 2) {
        neighbours.computeIfAbsent(cut[0], k -> new ArrayList<>(2)).add(cut[1]);
        neighbours.computeIfAbsent(cut[1], k -> new ArrayList<>(2)).add(cut[0]);
      }
    }
    // join the segments into curves, starting from the open ends so that a curve that is not
    // closed is traced from one end to the other
    List<List<double[]>> curves = new ArrayList<>();
    java.util.Set<Long> visited = new java.util.HashSet<>();
    for (int pass = 0; pass < 2; pass++) {
      for (Map.Entry<Long, List<Long>> entry : neighbours.entrySet()) {
        Long start = entry.getKey();
        if (visited.contains(start) || (pass == 0 && entry.getValue().size() != 1)) {
          continue;
        }
        List<double[]> curve = new ArrayList<>();
        Long current = start;
        Long previous = null;
        while (current != null && visited.add(current)) {
          curve.add(points.get(current));
          Long next = null;
          for (Long candidate : neighbours.get(current)) {
            if (!candidate.equals(previous) && !visited.contains(candidate)) {
              next = candidate;
              break;
            }
          }
          if (next == null && pass == 1 && curve.size() > 2
              && neighbours.get(current).contains(start)) {
            // a closed curve returns to its first point
            curve.add(points.get(start));
          }
          previous = current;
          current = next;
        }
        if (curve.size() >= 2) {
          curves.add(curve);
        }
      }
    }
    return curves;
  }

  /**
   * The edges that belong to one polygon only, as pairs of vertex numbers: the rim of an open
   * surface. An edge two polygons share lies inside the surface; welding the vertices in
   * {@link #addVertex} is what makes neighbouring polygons share their edges at all.
   */
  public IASTAppendable openEdges() {
    Map<Long, Integer> uses = new LinkedHashMap<>();
    for (int p = 1; p <= polygonIndices.argSize(); p++) {
      IAST polygon = (IAST) polygonIndices.get(p);
      int corners = polygon.argSize();
      for (int k = 1; k <= corners; k++) {
        int a = polygon.get(k).toIntDefault();
        int b = polygon.get(k % corners + 1).toIntDefault();
        long key = ((long) Math.min(a, b) << 32) | Math.max(a, b);
        uses.merge(key, 1, Integer::sum);
      }
    }
    IASTAppendable edges = F.ListAlloc();
    for (Map.Entry<Long, Integer> entry : uses.entrySet()) {
      if (entry.getValue() == 1) {
        long key = entry.getKey();
        edges.append(F.List(F.ZZ((int) (key >>> 32)), F.ZZ((int) key)));
      }
    }
    return edges;
  }

}
