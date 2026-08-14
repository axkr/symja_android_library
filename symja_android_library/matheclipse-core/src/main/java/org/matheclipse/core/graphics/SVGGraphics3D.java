package org.matheclipse.core.graphics;

import static j2html.TagCreator.tag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.matheclipse.core.interfaces.IAST;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import j2html.tags.DomContent;
import j2html.tags.UnescapedText;

/**
 * Renders a {@link org.matheclipse.core.expression.S#Graphics3D} expression to a static SVG
 * picture.
 *
 * <p>
 * The scene comes from {@link WebGLGraphics3D#buildScene}, the same description the interactive
 * WebGL front end draws, so both show the same graphic: the same colours, the same lights, the same
 * camera, the same tick labels. This class only has to turn that description into flat geometry -
 * tessellate the solids, light each face, sort back to front and write the SVG. It used to walk the
 * {@code Graphics3D} expression itself with its own copies of the colour table, the lighting and
 * the option defaults, and those copies had drifted a long way from the ones the WebGL path uses.
 *
 * <p>
 * Shading is flat, one colour per face, because that is what an SVG polygon can express. A finely
 * sampled surface still reads as smooth; a coarse one shows its facets, which is the honest result
 * for this output format.
 */
public class SVGGraphics3D {

  /** Distance in pixels kept clear around the picture. */
  private static final double PADDING = 25.0;

  private static final int SPHERE_SEGMENTS = 24;
  private static final int SPHERE_RINGS = 16;
  private static final int TUBE_SEGMENTS = 24;
  private static final int TUBE_SIDES = 10;
  /** Points inserted between two given tube points, to match the curve the WebGL path sweeps. */
  private static final int TUBE_SMOOTHING = 8;

  // --------------------------------------------------------------------- maths

  private static final class Vector3 {
    double x;
    double y;
    double z;

    Vector3(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    Vector3 add(Vector3 v) {
      return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    Vector3 sub(Vector3 v) {
      return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    Vector3 scale(double s) {
      return new Vector3(x * s, y * s, z * s);
    }

    double dot(Vector3 v) {
      return x * v.x + y * v.y + z * v.z;
    }

    Vector3 cross(Vector3 v) {
      return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    double length() {
      return Math.sqrt(x * x + y * y + z * z);
    }

    Vector3 normalize() {
      double len = length();
      return len == 0 ? new Vector3(0, 0, 0) : new Vector3(x / len, y / len, z / len);
    }
  }

  /** The camera, as the basis it projects onto. */
  private static final class View {
    Vector3 eye;
    Vector3 right;
    Vector3 up;
    /** From the camera towards the scene. */
    Vector3 forward;

    /** Camera space: x to the right, y up, z towards the viewer. */
    Vector3 project(Vector3 p) {
      Vector3 d = p.sub(eye);
      return new Vector3(d.dot(right), d.dot(up), d.dot(forward));
    }

    /** A direction given in camera space, expressed in world space. */
    Vector3 fromCamera(Vector3 cameraSpace) {
      return right.scale(cameraSpace.x).add(up.scale(cameraSpace.y))
          .add(forward.scale(-cameraSpace.z));
    }
  }

  /** A light, already reduced to a world space direction. */
  private static final class Light {
    final Color color;
    final Vector3 direction;
    final boolean ambient;

    Light(Color color, Vector3 direction, boolean ambient) {
      this.color = color;
      this.direction = direction;
      this.ambient = ambient;
    }
  }

  // -------------------------------------------------------------- renderables

  /** Something to draw, carrying the depth it is sorted by. */
  private abstract static class Renderable implements Comparable<Renderable> {
    double depth;

    abstract DomContent toSVG();

    @Override
    public int compareTo(Renderable other) {
      // farthest first, so nearer things paint over them
      return Double.compare(other.depth, this.depth);
    }
  }

  private static final class Face extends Renderable {
    final List<Vector3> points;
    final Color color;
    final double opacity;
    final Color edgeColor;
    final double edgeWidth;

    Face(List<Vector3> points, Color color, double opacity, Color edgeColor, double edgeWidth) {
      this.points = points;
      this.color = color;
      this.opacity = opacity;
      this.edgeColor = edgeColor;
      this.edgeWidth = edgeWidth;
      double sum = 0;
      for (Vector3 p : points) {
        sum += p.z;
      }
      this.depth = sum / Math.max(1, points.size());
    }

    @Override
    DomContent toSVG() {
      StringBuilder path = new StringBuilder();
      for (Vector3 p : points) {
        path.append(format(p.x)).append(',').append(format(p.y)).append(' ');
      }
      DomContent polygon = tag("polygon").attr("points", path.toString().trim())
          .attr("fill", hex(color)).attr("fill-opacity", format(opacity))
          .attr("stroke", edgeColor == null ? "none" : hex(edgeColor))
          .attr("stroke-width", format(edgeWidth))
          // hairline seams between neighbouring facets show as a grid of white cracks
          .attr("shape-rendering", "crispEdges");
      return polygon;
    }
  }

  private static final class Polyline extends Renderable {
    final List<Vector3> points;
    final Color color;
    final double opacity;
    final double width;
    final String dashArray;

    Polyline(List<Vector3> points, Color color, double opacity, double width, String dashArray) {
      this.points = points;
      this.color = color;
      this.opacity = opacity;
      this.width = width;
      this.dashArray = dashArray;
      double sum = 0;
      for (Vector3 p : points) {
        sum += p.z;
      }
      this.depth = sum / Math.max(1, points.size());
    }

    @Override
    DomContent toSVG() {
      StringBuilder path = new StringBuilder();
      for (Vector3 p : points) {
        path.append(format(p.x)).append(',').append(format(p.y)).append(' ');
      }
      DomContent line = tag("polyline").attr("points", path.toString().trim()).attr("fill", "none")
          .attr("stroke", hex(color)).attr("stroke-opacity", format(opacity))
          .attr("stroke-width", format(width)).attr("stroke-linecap", "round")
          .attr("stroke-linejoin", "round");
      return dashArray == null ? line
          : ((j2html.tags.ContainerTag<?>) line).attr("stroke-dasharray", dashArray);
    }
  }

  private static final class Dot extends Renderable {
    final Vector3 point;
    final double radius;
    final Color color;
    final double opacity;

    Dot(Vector3 point, double radius, Color color, double opacity) {
      this.point = point;
      this.radius = radius;
      this.color = color;
      this.opacity = opacity;
      this.depth = point.z;
    }

    @Override
    DomContent toSVG() {
      return tag("circle").attr("cx", format(point.x)).attr("cy", format(point.y))
          .attr("r", format(radius)).attr("fill", hex(color)).attr("fill-opacity", format(opacity));
    }
  }

  private static final class Label extends Renderable {
    final Vector3 point;
    final String text;
    final Color color;
    final double fontSize;
    final String anchor;

    Label(Vector3 point, String text, Color color, double fontSize, String anchor) {
      this.point = point;
      this.text = text;
      this.color = color;
      this.fontSize = fontSize;
      this.anchor = anchor;
      // labels belong in front of the geometry they annotate
      this.depth = -Double.MAX_VALUE;
    }

    @Override
    DomContent toSVG() {
      // a tick on a log axis reads 10^n, which has to be set as a superscript rather than
      // printed with a caret; the 2D axes renderer writes the same markup
      String body = org.matheclipse.core.graphics.svg.TickGenerator.isPowerLabel(text)
          ? "10<tspan dy=\"-0.6em\" font-size=\"70%\">"
              + escape(org.matheclipse.core.graphics.svg.TickGenerator.powerExponent(text))
              + "</tspan>"
          : escape(text);
      return tag("text").attr("x", format(point.x)).attr("y", format(point.y))
          .attr("fill", hex(color)).attr("font-size", format(fontSize))
          .attr("font-family", "Arial, sans-serif").attr("text-anchor", anchor)
          .attr("dominant-baseline", "middle").with(new UnescapedText(body));
    }
  }

  // ------------------------------------------------------------------- render

  public static String toSVG(IAST graphics3D) {
    try {
      return render(WebGLGraphics3D.buildScene(graphics3D));
    } catch (RuntimeException rex) {
      return "";
    }
  }

  private static String render(ObjectNode scene) {
    double width = scene.has("imageSize") ? scene.get("imageSize").get(0).asDouble(360) : 360;
    double height = scene.has("imageSize") ? scene.get("imageSize").get(1).asDouble(360) : 360;

    double[][] ranges = ranges(scene);
    Vector3 dataScale = boxScale(scene, ranges);
    Vector3 min = new Vector3(ranges[0][0] * dataScale.x, ranges[1][0] * dataScale.y,
        ranges[2][0] * dataScale.z);
    Vector3 max = new Vector3(ranges[0][1] * dataScale.x, ranges[1][1] * dataScale.y,
        ranges[2][1] * dataScale.z);
    Vector3 center = min.add(max).scale(0.5);
    Vector3 size = max.sub(min);
    double maxDim = Math.max(size.x, Math.max(size.y, size.z));
    if (!(maxDim > 0)) {
      maxDim = 1;
    }
    double diagonal =
        scene.has("diagonal") ? scene.get("diagonal").asDouble(size.length()) : size.length();

    View view = camera(scene, center, maxDim);
    List<Light> lights = lights(scene, view);

    List<Renderable> renderables = new ArrayList<>();
    if (scene.has("elements")) {
      for (JsonNode element : scene.get("elements")) {
        collect(element, dataScale, view, lights, diagonal, renderables);
      }
    }
    addBox(scene, min, max, view, renderables);
    addAxes(scene, ranges, min, max, dataScale, view, maxDim, renderables);

    return write(scene, renderables, width, height);
  }

  /** The visible range per axis, as the converter resolved it. */
  private static double[][] ranges(ObjectNode scene) {
    double[][] ranges = {{-1, 1}, {-1, 1}, {-1, 1}};
    if (scene.has("plotRange")) {
      JsonNode node = scene.get("plotRange");
      for (int i = 0; i < 3 && i < node.size(); i++) {
        double lo = node.get(i).get(0).asDouble();
        double hi = node.get(i).get(1).asDouble();
        if (hi > lo) {
          ranges[i] = new double[] {lo, hi};
        }
      }
    }
    return ranges;
  }

  /** {@code BoxRatios} reshapes the box the data is drawn into, as it does in the WebGL path. */
  private static Vector3 boxScale(ObjectNode scene, double[][] ranges) {
    if (!scene.has("boxRatios")) {
      return new Vector3(1, 1, 1);
    }
    JsonNode ratios = scene.get("boxRatios");
    double[] extent =
        {ranges[0][1] - ranges[0][0], ranges[1][1] - ranges[1][0], ranges[2][1] - ranges[2][0]};
    double longest = Math.max(extent[0], Math.max(extent[1], extent[2]));
    double maxRatio = Math.max(ratios.get(0).asDouble(),
        Math.max(ratios.get(1).asDouble(), ratios.get(2).asDouble()));
    if (!(longest > 0) || !(maxRatio > 0)) {
      return new Vector3(1, 1, 1);
    }
    double[] scale = new double[3];
    for (int i = 0; i < 3; i++) {
      scale[i] =
          extent[i] > 1e-12 ? (ratios.get(i).asDouble() / maxRatio) * longest / extent[i] : 1;
    }
    return new Vector3(scale[0], scale[1], scale[2]);
  }

  /**
   * The camera. {@code ViewPoint} is written in a box whose longest side is one, so the distance
   * scales with the box.
   */
  private static View camera(ObjectNode scene, Vector3 center, double maxDim) {
    Vector3 direction = vector(scene.get("viewPoint"), new Vector3(1.3, -2.4, 2.0));
    if (direction.length() == 0) {
      direction = new Vector3(1.3, -2.4, 2.0);
    }
    Vector3 vertical = vector(scene.get("viewVertical"), new Vector3(0, 0, 1));

    View view = new View();
    view.eye = center.add(direction.scale(maxDim));
    view.forward = center.sub(view.eye).normalize();
    Vector3 right = view.forward.cross(vertical);
    if (right.length() < 1e-9) {
      // looking straight along the vertical, so any perpendicular will do
      right = view.forward.cross(new Vector3(1, 0, 0));
      if (right.length() < 1e-9) {
        right = view.forward.cross(new Vector3(0, 1, 0));
      }
    }
    view.right = right.normalize();
    view.up = view.right.cross(view.forward).normalize();
    return view;
  }

  /**
   * The lights, reduced to world space directions.
   *
   * <p>
   * A light the converter marked as travelling with the camera has its position in camera space,
   * which is what keeps a surface shaded the same way whichever side of it is being looked at.
   */
  private static List<Light> lights(ObjectNode scene, View view) {
    List<Light> lights = new ArrayList<>();
    if (!scene.has("lights")) {
      return lights;
    }
    for (JsonNode spec : scene.get("lights")) {
      Color color = new Color(spec.get("color").asInt());
      double intensity = spec.has("intensity") ? spec.get("intensity").asDouble(1) : 1;
      color = scale(color, intensity);
      String type = spec.get("type").asText();
      if ("AmbientLight".equals(type)) {
        lights.add(new Light(color, null, true));
        continue;
      }
      Vector3 position = vector(spec.get("position"), new Vector3(0, 0, 1));
      boolean fixedToCamera = spec.has("fixedToCamera") && spec.get("fixedToCamera").asBoolean();
      Vector3 direction =
          fixedToCamera ? view.fromCamera(position).normalize() : position.normalize();
      lights.add(new Light(color, direction, false));
    }
    return lights;
  }

  /**
   * Shade one face.
   *
   * <p>
   * The ambient term plus, for every light, its colour weighted by the cosine of the angle it meets
   * the surface at. The face is lit from whichever side is turned towards the viewer, because a
   * mathematical surface has no inside.
   */
  private static Color shade(Color base, Vector3 normal, List<Light> lights, View view) {
    Vector3 n = normal.normalize();
    if (n.dot(view.forward) > 0) {
      n = n.scale(-1);
    }
    double r = 0;
    double g = 0;
    double b = 0;
    for (Light light : lights) {
      double weight = 1.0;
      if (!light.ambient) {
        weight = Math.max(0.0, n.dot(light.direction));
        if (weight == 0) {
          continue;
        }
      }
      r += light.color.getRed() / 255.0 * weight;
      g += light.color.getGreen() / 255.0 * weight;
      b += light.color.getBlue() / 255.0 * weight;
    }
    return new Color(clamp(base.getRed() / 255.0 * r), clamp(base.getGreen() / 255.0 * g),
        clamp(base.getBlue() / 255.0 * b));
  }

  // --------------------------------------------------------------- primitives

  private static void collect(JsonNode element, Vector3 dataScale, View view, List<Light> lights,
      double diagonal, List<Renderable> out) {
    String type = element.get("type").asText();
    Color color = new Color(element.get("color").asInt());
    double opacity = element.has("opacity") ? element.get("opacity").asDouble(1) : 1;
    double[] matrix = matrix(element);

    switch (type) {
      case "Polygon":
        polygons(element, color, opacity, matrix, dataScale, view, lights, out);
        break;
      case "Sphere":
        for (Vector3 centre : points(element.get("centers"))) {
          sphere(centre, element.get("radius").asDouble(1), color, opacity, matrix, dataScale, view,
              lights, out);
        }
        break;
      case "Cylinder":
      case "Cone":
        barrel(vector(element.get("start"), new Vector3(0, 0, -1)),
            vector(element.get("end"), new Vector3(0, 0, 1)), element.get("radius").asDouble(1),
            "Cone".equals(type), color, opacity, matrix, dataScale, view, lights, out);
        break;
      case "Cuboid":
        cuboid(vector(element.get("min"), new Vector3(0, 0, 0)),
            vector(element.get("max"), new Vector3(1, 1, 1)), color, opacity, matrix, dataScale,
            view, lights, out);
        break;
      case "Polyhedron":
        polyhedron(element, color, opacity, matrix, dataScale, view, lights, out);
        break;
      case "Tube":
        tube(element, color, opacity, matrix, dataScale, view, lights, out);
        break;
      case "Line":
      case "Arrow":
      case "BSplineCurve":
      case "BezierCurve":
        curves(element, type, color, opacity, matrix, dataScale, view, lights, diagonal, out);
        break;
      case "Point":
        dots(element, color, opacity, matrix, dataScale, view, diagonal, out);
        break;
      case "Text":
        text(element, color, opacity, matrix, dataScale, view, out);
        break;
      default:
        break;
    }
  }

  /** An indexed triangle mesh, one flat facet per triangle. */
  private static void polygons(JsonNode element, Color color, double opacity, double[] matrix,
      Vector3 dataScale, View view, List<Light> lights, List<Renderable> out) {
    JsonNode pointData = element.get("points");
    JsonNode indices = element.get("indices");
    if (pointData == null || indices == null) {
      return;
    }
    List<Vector3> vertices = new ArrayList<>(pointData.size() / 3);
    for (int i = 0; i + 2 < pointData.size(); i += 3) {
      vertices.add(place(new Vector3(pointData.get(i).asDouble(), pointData.get(i + 1).asDouble(),
          pointData.get(i + 2).asDouble()), matrix, dataScale));
    }
    JsonNode vertexColors = element.get("vertexColors");
    Color edge = edgeColor(element);
    double edgeWidth = element.has("edgeThickness") ? element.get("edgeThickness").asDouble(1) : 1;

    for (int t = 0; t + 2 < indices.size(); t += 3) {
      int a = indices.get(t).asInt();
      int b = indices.get(t + 1).asInt();
      int c = indices.get(t + 2).asInt();
      if (a >= vertices.size() || b >= vertices.size() || c >= vertices.size()) {
        continue;
      }
      Vector3 pa = vertices.get(a);
      Vector3 pb = vertices.get(b);
      Vector3 pc = vertices.get(c);
      Color base = color;
      if (vertexColors != null && vertexColors.size() >= (a + 1) * 3) {
        // one colour per facet, averaged over its corners, is as far as a flat fill can go
        base = average(vertexColors, a, b, c);
      }
      addFace(out, view, lights, base, opacity, edge, edgeWidth, pa, pb, pc);
    }
  }

  private static void sphere(Vector3 centre, double radius, Color color, double opacity,
      double[] matrix, Vector3 dataScale, View view, List<Light> lights, List<Renderable> out) {
    Vector3[][] grid = new Vector3[SPHERE_RINGS + 1][SPHERE_SEGMENTS + 1];
    for (int i = 0; i <= SPHERE_RINGS; i++) {
      double phi = Math.PI * i / SPHERE_RINGS;
      for (int j = 0; j <= SPHERE_SEGMENTS; j++) {
        double theta = 2 * Math.PI * j / SPHERE_SEGMENTS;
        Vector3 p = new Vector3(centre.x + radius * Math.sin(phi) * Math.cos(theta),
            centre.y + radius * Math.sin(phi) * Math.sin(theta), centre.z + radius * Math.cos(phi));
        grid[i][j] = place(p, matrix, dataScale);
      }
    }
    quads(grid, color, opacity, view, lights, out);
  }

  /** A cylinder, or a cone when the far end is collapsed to a point. */
  private static void barrel(Vector3 start, Vector3 end, double radius, boolean cone, Color color,
      double opacity, double[] matrix, Vector3 dataScale, View view, List<Light> lights,
      List<Renderable> out) {
    Vector3 axis = end.sub(start);
    if (axis.length() == 0) {
      return;
    }
    Vector3 w = axis.normalize();
    Vector3 u = perpendicular(w);
    Vector3 v = w.cross(u);

    List<Vector3> bottom = new ArrayList<>();
    List<Vector3> top = new ArrayList<>();
    for (int i = 0; i <= TUBE_SEGMENTS; i++) {
      double a = 2 * Math.PI * i / TUBE_SEGMENTS;
      Vector3 offset = u.scale(radius * Math.cos(a)).add(v.scale(radius * Math.sin(a)));
      bottom.add(place(start.add(offset), matrix, dataScale));
      top.add(place(cone ? end : end.add(offset), matrix, dataScale));
    }
    for (int i = 0; i < TUBE_SEGMENTS; i++) {
      if (cone) {
        addFace(out, view, lights, color, opacity, null, 0, bottom.get(i), bottom.get(i + 1),
            top.get(i));
      } else {
        addFace(out, view, lights, color, opacity, null, 0, bottom.get(i), bottom.get(i + 1),
            top.get(i + 1), top.get(i));
      }
    }
    cap(out, view, lights, color, opacity, bottom, place(start, matrix, dataScale));
    if (!cone) {
      cap(out, view, lights, color, opacity, top, place(end, matrix, dataScale));
    }
  }

  private static void cap(List<Renderable> out, View view, List<Light> lights, Color color,
      double opacity, List<Vector3> rim, Vector3 centre) {
    for (int i = 0; i + 1 < rim.size(); i++) {
      addFace(out, view, lights, color, opacity, null, 0, centre, rim.get(i), rim.get(i + 1));
    }
  }

  private static void cuboid(Vector3 min, Vector3 max, Color color, double opacity, double[] matrix,
      Vector3 dataScale, View view, List<Light> lights, List<Renderable> out) {
    Vector3[] corner = new Vector3[8];
    for (int i = 0; i < 8; i++) {
      corner[i] = place(new Vector3((i & 1) == 0 ? min.x : max.x, (i & 2) == 0 ? min.y : max.y,
          (i & 4) == 0 ? min.z : max.z), matrix, dataScale);
    }
    int[][] faces =
        {{0, 2, 3, 1}, {4, 5, 7, 6}, {0, 1, 5, 4}, {2, 6, 7, 3}, {0, 4, 6, 2}, {1, 3, 7, 5}};
    for (int[] face : faces) {
      addFace(out, view, lights, color, opacity, null, 0, corner[face[0]], corner[face[1]],
          corner[face[2]], corner[face[3]]);
    }
  }

  private static void polyhedron(JsonNode element, Color color, double opacity, double[] matrix,
      Vector3 dataScale, View view, List<Light> lights, List<Renderable> out) {
    Vector3 centre = vector(element.get("center"), new Vector3(0, 0, 0));
    double scale = element.has("scale") ? element.get("scale").asDouble(1) : 1;
    Polyhedra shape = Polyhedra.of(element.get("kind").asText());
    for (int[] face : shape.faces) {
      Vector3[] corners = new Vector3[face.length];
      for (int i = 0; i < face.length; i++) {
        double[] v = shape.vertices[face[i]];
        corners[i] = place(
            new Vector3(centre.x + v[0] * scale, centre.y + v[1] * scale, centre.z + v[2] * scale),
            matrix, dataScale);
      }
      addFace(out, view, lights, color, opacity, null, 0, corners);
    }
  }

  private static void tube(JsonNode element, Color color, double opacity, double[] matrix,
      Vector3 dataScale, View view, List<Light> lights, List<Renderable> out) {
    double radius = element.has("radius") ? element.get("radius").asDouble(0.02) : 0.02;
    for (List<Vector3> raw : polylines(element)) {
      if (raw.size() < 2) {
        continue;
      }
      // the interactive renderer sweeps a tube along a Catmull-Rom curve through the given
      // points, so the same smoothing is applied here or a bent tube comes out visibly angular
      List<Vector3> path = smooth(raw);
      Vector3[][] grid = new Vector3[path.size()][TUBE_SIDES + 1];
      Vector3 carried = null;
      for (int i = 0; i < path.size(); i++) {
        Vector3 tangent =
            (i == 0 ? path.get(1).sub(path.get(0)) : path.get(i).sub(path.get(i - 1))).normalize();
        // The frame is carried along the path rather than chosen afresh at every ring. Picking
        // an arbitrary perpendicular each time lets the frame spin between one ring and the next,
        // and the quads joining them come out twisted into bow ties instead of a tube wall.
        Vector3 u = carried == null ? perpendicular(tangent)
            : carried.sub(tangent.scale(carried.dot(tangent)));
        if (u.length() < 1e-9) {
          u = perpendicular(tangent);
        }
        u = u.normalize();
        carried = u;
        Vector3 v = tangent.cross(u);
        for (int j = 0; j <= TUBE_SIDES; j++) {
          double a = 2 * Math.PI * j / TUBE_SIDES;
          Vector3 offset = u.scale(radius * Math.cos(a)).add(v.scale(radius * Math.sin(a)));
          grid[i][j] = place(path.get(i).add(offset), matrix, dataScale);
        }
      }
      quads(grid, color, opacity, view, lights, out);
    }
  }

  private static void curves(JsonNode element, String type, Color color, double opacity,
      double[] matrix, Vector3 dataScale, View view, List<Light> lights, double diagonal,
      List<Renderable> out) {
    double width = lineWidth(element, diagonal);
    String dash = dashArray(element, diagonal);
    List<List<Vector3>> paths =
        "BSplineCurve".equals(type) || "BezierCurve".equals(type) ? List.of(controlPolygon(element))
            : polylines(element);
    for (List<Vector3> path : paths) {
      if (path.size() < 2) {
        continue;
      }
      List<Vector3> placed = new ArrayList<>(path.size());
      for (Vector3 p : path) {
        placed.add(view.project(place(p, matrix, dataScale)));
      }
      out.add(new Polyline(placed, color, opacity, width, dash));
      if ("Arrow".equals(type)) {
        arrowhead(element, path, color, opacity, matrix, dataScale, view, lights, diagonal, out);
      }
    }
  }

  /** A cone at the tip of an arrow, sized as a fraction of the scene. */
  private static void arrowhead(JsonNode element, List<Vector3> path, Color color, double opacity,
      double[] matrix, Vector3 dataScale, View view, List<Light> lights, double diagonal,
      List<Renderable> out) {
    double size =
        (element.has("arrowheadSize") ? element.get("arrowheadSize").asDouble(0.04) : 0.04)
            * diagonal;
    Vector3 tip = path.get(path.size() - 1);
    Vector3 previous = path.get(path.size() - 2);
    Vector3 direction = tip.sub(previous);
    if (direction.length() == 0 || size <= 0) {
      return;
    }
    Vector3 base = tip.sub(direction.normalize().scale(size));
    barrel(base, tip, size * 0.35, true, color, opacity, matrix, dataScale, view, lights, out);
  }

  private static void dots(JsonNode element, Color color, double opacity, double[] matrix,
      Vector3 dataScale, View view, double diagonal, List<Renderable> out) {
    double radius = 0.5
        * (element.has("pointSizeScaled") ? element.get("pointSizeScaled").asDouble(0.01) * diagonal
            : element.has("pointSize") ? element.get("pointSize").asDouble(3) * diagonal * 0.004
                : 0.01 * diagonal);
    JsonNode data = element.get("points");
    if (data == null) {
      return;
    }
    for (int i = 0; i + 2 < data.size(); i += 3) {
      Vector3 p = place(new Vector3(data.get(i).asDouble(), data.get(i + 1).asDouble(),
          data.get(i + 2).asDouble()), matrix, dataScale);
      // the radius is a world length, so it has to be measured after projection too
      out.add(new Dot(view.project(p), radius, color, opacity));
    }
  }

  private static void text(JsonNode element, Color color, double opacity, double[] matrix,
      Vector3 dataScale, View view, List<Renderable> out) {
    Vector3 p = place(vector(element.get("position"), new Vector3(0, 0, 0)), matrix, dataScale);
    double fontSize = element.has("fontSize") ? element.get("fontSize").asDouble(12) : 12;
    out.add(new Label(view.project(p), element.get("text").asText(), color, fontSize, "middle"));
  }

  // ------------------------------------------------------------- box and axes

  private static void addBox(ObjectNode scene, Vector3 min, Vector3 max, View view,
      List<Renderable> out) {
    if (scene.has("boxed") && !scene.get("boxed").asBoolean()) {
      return;
    }
    Color color =
        scene.has("boxColor") ? new Color(scene.get("boxColor").asInt()) : new Color(0xA0A0A0);
    Vector3[] corner = new Vector3[8];
    for (int i = 0; i < 8; i++) {
      corner[i] = new Vector3((i & 1) == 0 ? min.x : max.x, (i & 2) == 0 ? min.y : max.y,
          (i & 4) == 0 ? min.z : max.z);
    }
    int[][] edges = {{0, 1}, {1, 3}, {3, 2}, {2, 0}, {4, 5}, {5, 7}, {7, 6}, {6, 4}, {0, 4}, {1, 5},
        {2, 6}, {3, 7}};
    for (int[] edge : edges) {
      out.add(new Polyline(List.of(view.project(corner[edge[0]]), view.project(corner[edge[1]])),
          color, 1.0, 1.0, null));
    }
  }

  /**
   * The axes, their ticks and their labels.
   *
   * <p>
   * The tick positions and their text come from the scene, so an axis reads the same here as it
   * does in the interactive output and as a 2D plot's axis does.
   */
  private static void addAxes(ObjectNode scene, double[][] ranges, Vector3 min, Vector3 max,
      Vector3 dataScale, View view, double maxDim, List<Renderable> out) {
    JsonNode axes = scene.get("axes");
    if (axes == null) {
      return;
    }
    Color color = scene.has("axesColor") ? new Color(scene.get("axesColor").asInt()) : Color.BLACK;
    double fontSize = scene.has("labelFontSize") ? scene.get("labelFontSize").asDouble(12) : 12;
    JsonNode ticks = scene.get("ticks");
    JsonNode labels = scene.get("axesLabel");
    Vector3 centre = min.add(max).scale(0.5);
    double tickLength = maxDim * 0.02;

    for (int axis = 0; axis < 3 && axis < axes.size(); axis++) {
      if (!axes.get(axis).asBoolean()) {
        continue;
      }
      // the axis is drawn on the box edge that sits lowest on screen
      Vector3[] edge = chooseEdge(axis, min, max, view);
      out.add(new Polyline(List.of(view.project(edge[0]), view.project(edge[1])), color, 1.0, 1.0,
          null));

      Vector3 outward = edge[0].add(edge[1]).scale(0.5).sub(centre);
      set(outward, axis, 0);
      if (outward.length() < 1e-12) {
        outward = new Vector3(axis == 2 ? -1 : 0, 0, axis == 2 ? 0 : -1);
      }
      outward = outward.normalize();

      if (ticks != null && axis < ticks.size()) {
        double lo = ranges[axis][0];
        double span = ranges[axis][1] - lo;
        for (JsonNode tick : ticks.get(axis)) {
          double fraction = span > 0 ? (tick.get("position").asDouble() - lo) / span : 0.5;
          Vector3 at = lerp(edge[0], edge[1], fraction);
          Vector3 tip = at.add(outward.scale(tickLength));
          out.add(
              new Polyline(List.of(view.project(at), view.project(tip)), color, 1.0, 1.0, null));
          out.add(new Label(view.project(at.add(outward.scale(tickLength * 2.6))),
              tick.get("label").asText(), color, fontSize, "middle"));
        }
      }
      if (labels != null && axis < labels.size() && !labels.get(axis).isNull()) {
        Vector3 at = edge[0].add(edge[1]).scale(0.5).add(outward.scale(tickLength * 6));
        out.add(new Label(view.project(at), labels.get(axis).asText(), color, fontSize * 1.15,
            "middle"));
      }
    }
  }

  /** Of the four box edges parallel to an axis, the one that reads best from where we are. */
  private static Vector3[] chooseEdge(int axis, Vector3 min, Vector3 max, View view) {
    int[][] signs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    Vector3[] best = null;
    double bestScore = -Double.MAX_VALUE;
    for (int[] sign : signs) {
      Vector3 start;
      Vector3 end;
      if (axis == 0) {
        start = new Vector3(min.x, pick(sign[0], min.y, max.y), pick(sign[1], min.z, max.z));
        end = new Vector3(max.x, start.y, start.z);
      } else if (axis == 1) {
        start = new Vector3(pick(sign[0], min.x, max.x), min.y, pick(sign[1], min.z, max.z));
        end = new Vector3(start.x, max.y, start.z);
      } else {
        start = new Vector3(pick(sign[0], min.x, max.x), pick(sign[1], min.y, max.y), min.z);
        end = new Vector3(start.x, start.y, max.z);
      }
      Vector3 mid = view.project(start.add(end).scale(0.5));
      double score = axis == 2 ? -mid.x : -mid.y;
      if (score > bestScore) {
        bestScore = score;
        best = new Vector3[] {start, end};
      }
    }
    return best;
  }

  // -------------------------------------------------------------- SVG writing

  private static String write(ObjectNode scene, List<Renderable> renderables, double width,
      double height) {
    Collections.sort(renderables);

    List<Vector3> all = new ArrayList<>();
    for (Renderable r : renderables) {
      if (r instanceof Face) {
        all.addAll(((Face) r).points);
      } else if (r instanceof Polyline) {
        all.addAll(((Polyline) r).points);
      } else if (r instanceof Dot) {
        all.add(((Dot) r).point);
      } else if (r instanceof Label) {
        all.add(((Label) r).point);
      }
    }
    double minX = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;
    for (Vector3 p : all) {
      minX = Math.min(minX, p.x);
      maxX = Math.max(maxX, p.x);
      minY = Math.min(minY, p.y);
      maxY = Math.max(maxY, p.y);
    }
    if (all.isEmpty()) {
      minX = -1;
      maxX = 1;
      minY = -1;
      maxY = 1;
    }
    double rangeX = maxX - minX <= 0 ? 1 : maxX - minX;
    double rangeY = maxY - minY <= 0 ? 1 : maxY - minY;
    double scale = Math.min((width - 2 * PADDING) / rangeX, (height - 2 * PADDING) / rangeY);
    double shiftX = PADDING + (width - 2 * PADDING - rangeX * scale) / 2.0;
    double shiftY = PADDING + (height - 2 * PADDING - rangeY * scale) / 2.0;

    for (Vector3 p : all) {
      p.x = (p.x - minX) * scale + shiftX;
      // SVG counts y downwards
      p.y = height - ((p.y - minY) * scale + shiftY);
    }
    // a dot's radius is a world length and has to follow the same scaling
    for (Renderable r : renderables) {
      if (r instanceof Dot) {
        ((Dot) r).point.z = 0;
      }
    }

    List<DomContent> content = new ArrayList<>();
    if (scene.has("background")) {
      content.add(tag("rect").attr("x", "0").attr("y", "0").attr("width", format(width))
          .attr("height", format(height))
          .attr("fill", hex(new Color(scene.get("background").asInt()))));
    }
    for (Renderable r : renderables) {
      content.add(r.toSVG());
    }
    if (scene.has("plotLabel")) {
      content
          .add(tag("text").attr("x", format(width / 2)).attr("y", format(PADDING * 0.7))
              .attr("text-anchor", "middle").attr("font-family", "Arial, sans-serif")
              .attr("font-size", format(
                  scene.has("labelFontSize") ? scene.get("labelFontSize").asDouble(12) * 1.2 : 14))
              .with(new UnescapedText(escape(scene.get("plotLabel").asText()))));
    }
    return tag("svg").with(content).attr("xmlns", "http://www.w3.org/2000/svg").attr("width", width)
        .attr("height", height).attr("viewBox", "0 0 " + width + " " + height).render();
  }

  // ------------------------------------------------------------------ helpers

  /** Apply an element's own transformation, then the box scaling. */
  private static Vector3 place(Vector3 p, double[] matrix, Vector3 dataScale) {
    Vector3 q = p;
    if (matrix != null) {
      // the converter writes the matrix column major, as the WebGL side expects it
      q = new Vector3(matrix[0] * p.x + matrix[4] * p.y + matrix[8] * p.z + matrix[12],
          matrix[1] * p.x + matrix[5] * p.y + matrix[9] * p.z + matrix[13],
          matrix[2] * p.x + matrix[6] * p.y + matrix[10] * p.z + matrix[14]);
    }
    return new Vector3(q.x * dataScale.x, q.y * dataScale.y, q.z * dataScale.z);
  }

  private static void addFace(List<Renderable> out, View view, List<Light> lights, Color base,
      double opacity, Color edge, double edgeWidth, Vector3... corners) {
    if (corners.length < 3) {
      return;
    }
    Vector3 normal = corners[1].sub(corners[0]).cross(corners[2].sub(corners[0]));
    if (normal.length() == 0) {
      return;
    }
    Color lit = shade(base, normal, lights, view);
    List<Vector3> projected = new ArrayList<>(corners.length);
    for (Vector3 corner : corners) {
      projected.add(view.project(corner));
    }
    out.add(new Face(projected, lit, opacity, edge, edgeWidth));
  }

  /** Turn a grid of points into quads, as a tessellated sphere or tube produces. */
  private static void quads(Vector3[][] grid, Color color, double opacity, View view,
      List<Light> lights, List<Renderable> out) {
    for (int i = 0; i + 1 < grid.length; i++) {
      for (int j = 0; j + 1 < grid[i].length; j++) {
        addFace(out, view, lights, color, opacity, null, 0, grid[i][j], grid[i + 1][j],
            grid[i + 1][j + 1], grid[i][j + 1]);
      }
    }
  }

  private static List<List<Vector3>> polylines(JsonNode element) {
    List<List<Vector3>> result = new ArrayList<>();
    JsonNode lines = element.get("polylines");
    if (lines != null) {
      for (JsonNode line : lines) {
        result.add(points(line));
      }
      return result;
    }
    JsonNode flat = element.get("points");
    if (flat != null) {
      result.add(points(flat));
    }
    return result;
  }

  /** A spline is drawn through its control points, which bound the curve it describes. */
  private static List<Vector3> controlPolygon(JsonNode element) {
    return points(element.get("points"));
  }

  private static List<Vector3> points(JsonNode flat) {
    List<Vector3> result = new ArrayList<>();
    if (flat == null) {
      return result;
    }
    for (int i = 0; i + 2 < flat.size(); i += 3) {
      result.add(new Vector3(flat.get(i).asDouble(), flat.get(i + 1).asDouble(),
          flat.get(i + 2).asDouble()));
    }
    return result;
  }

  private static double[] matrix(JsonNode element) {
    JsonNode node = element.get("matrix");
    if (node == null || node.size() != 16) {
      return null;
    }
    double[] matrix = new double[16];
    for (int i = 0; i < 16; i++) {
      matrix[i] = node.get(i).asDouble();
    }
    return matrix;
  }

  private static Color edgeColor(JsonNode element) {
    if (!element.has("showMesh") || !element.get("showMesh").asBoolean()) {
      return null;
    }
    return element.has("edgeColor") ? new Color(element.get("edgeColor").asInt())
        : new Color(0x333333);
  }

  private static double lineWidth(JsonNode element, double diagonal) {
    if (element.has("thicknessScaled")) {
      // a scaled width is a fraction of the picture, which is what the viewBox spans
      return Math.max(0.5, element.get("thicknessScaled").asDouble(0.002) * 500);
    }
    return Math.max(0.5, element.has("thickness") ? element.get("thickness").asDouble(1) : 1);
  }

  private static String dashArray(JsonNode element, double diagonal) {
    JsonNode dashing = element.get("dashing");
    if (dashing == null || dashing.size() < 2) {
      return null;
    }
    boolean scaled = !element.has("dashingScaled") || element.get("dashingScaled").asBoolean();
    StringBuilder pattern = new StringBuilder();
    for (JsonNode entry : dashing) {
      double length = scaled ? entry.asDouble() * 500 : entry.asDouble();
      pattern.append(format(Math.max(0.5, length))).append(' ');
    }
    return pattern.toString().trim();
  }

  private static Color average(JsonNode vertexColors, int... indices) {
    double r = 0;
    double g = 0;
    double b = 0;
    int count = 0;
    for (int index : indices) {
      if (vertexColors.size() < (index + 1) * 3) {
        continue;
      }
      r += vertexColors.get(index * 3).asDouble();
      g += vertexColors.get(index * 3 + 1).asDouble();
      b += vertexColors.get(index * 3 + 2).asDouble();
      count++;
    }
    if (count == 0) {
      return Color.WHITE;
    }
    return new Color(clamp(r / count), clamp(g / count), clamp(b / count));
  }

  /**
   * Resample a path along the Catmull-Rom curve through its points.
   *
   * <p>
   * The ends are extended by reflecting the neighbouring point, which is what keeps the curve
   * starting and finishing exactly where the path does.
   */
  private static List<Vector3> smooth(List<Vector3> path) {
    if (path.size() < 3) {
      return path;
    }
    List<Vector3> out = new ArrayList<>(path.size() * TUBE_SMOOTHING);
    for (int i = 0; i + 1 < path.size(); i++) {
      Vector3 p0 = i == 0 ? path.get(0).scale(2).sub(path.get(1)) : path.get(i - 1);
      Vector3 p1 = path.get(i);
      Vector3 p2 = path.get(i + 1);
      Vector3 p3 = i + 2 < path.size() ? path.get(i + 2)
          : path.get(path.size() - 1).scale(2).sub(path.get(path.size() - 2));
      int steps = i + 2 == path.size() ? TUBE_SMOOTHING : TUBE_SMOOTHING - 1;
      for (int step = 0; step <= steps; step++) {
        double t = (double) step / TUBE_SMOOTHING;
        out.add(catmullRom(p0, p1, p2, p3, t));
      }
    }
    return out;
  }

  private static Vector3 catmullRom(Vector3 p0, Vector3 p1, Vector3 p2, Vector3 p3, double t) {
    double t2 = t * t;
    double t3 = t2 * t;
    return p1.scale(2).add(p2.sub(p0).scale(t))
        .add(p0.scale(2).sub(p1.scale(5)).add(p2.scale(4)).sub(p3).scale(t2))
        .add(p1.scale(3).sub(p0).sub(p2.scale(3)).add(p3).scale(t3)).scale(0.5);
  }

  private static Vector3 perpendicular(Vector3 axis) {
    Vector3 candidate = Math.abs(axis.x) < 0.9 ? new Vector3(1, 0, 0) : new Vector3(0, 1, 0);
    return axis.cross(candidate).normalize();
  }

  private static Vector3 lerp(Vector3 a, Vector3 b, double t) {
    return a.add(b.sub(a).scale(t));
  }

  private static void set(Vector3 v, int axis, double value) {
    if (axis == 0) {
      v.x = value;
    } else if (axis == 1) {
      v.y = value;
    } else {
      v.z = value;
    }
  }

  private static double pick(int sign, double low, double high) {
    return sign > 0 ? high : low;
  }

  private static Vector3 vector(JsonNode node, Vector3 fallback) {
    if (node == null || node.size() < 3) {
      return fallback;
    }
    return new Vector3(node.get(0).asDouble(), node.get(1).asDouble(), node.get(2).asDouble());
  }

  private static Color scale(Color color, double factor) {
    return new Color(clamp(color.getRed() / 255.0 * factor),
        clamp(color.getGreen() / 255.0 * factor), clamp(color.getBlue() / 255.0 * factor));
  }

  private static float clamp(double value) {
    return (float) Math.max(0.0, Math.min(1.0, value));
  }

  private static String hex(Color c) {
    return String.format(Locale.US, "#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
  }

  private static String format(double value) {
    if (!Double.isFinite(value)) {
      return "0";
    }
    return String.format(Locale.US, "%.3f", value);
  }

  private static String escape(String text) {
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  /** The vertices and faces of the regular polyhedra, at circumradius one. */
  private static final class Polyhedra {
    final double[][] vertices;
    final int[][] faces;

    Polyhedra(double[][] vertices, int[][] faces) {
      this.vertices = vertices;
      this.faces = faces;
    }

    static Polyhedra of(String kind) {
      switch (kind) {
        case "Tetrahedron":
          return tetrahedron();
        case "Octahedron":
          return octahedron();
        case "Icosahedron":
          return icosahedron();
        case "Dodecahedron":
          return dodecahedron();
        default:
          return cube();
      }
    }

    private static Polyhedra tetrahedron() {
      double s = 1 / Math.sqrt(3);
      double[][] v = {{s, s, s}, {s, -s, -s}, {-s, s, -s}, {-s, -s, s}};
      int[][] f = {{0, 1, 2}, {0, 3, 1}, {0, 2, 3}, {1, 3, 2}};
      return new Polyhedra(v, f);
    }

    private static Polyhedra cube() {
      double s = 1 / Math.sqrt(3);
      double[][] v = {{-s, -s, -s}, {s, -s, -s}, {s, s, -s}, {-s, s, -s}, {-s, -s, s}, {s, -s, s},
          {s, s, s}, {-s, s, s}};
      int[][] f =
          {{0, 3, 2, 1}, {4, 5, 6, 7}, {0, 1, 5, 4}, {1, 2, 6, 5}, {2, 3, 7, 6}, {3, 0, 4, 7}};
      return new Polyhedra(v, f);
    }

    private static Polyhedra octahedron() {
      double[][] v = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
      int[][] f =
          {{0, 2, 4}, {2, 1, 4}, {1, 3, 4}, {3, 0, 4}, {2, 0, 5}, {1, 2, 5}, {3, 1, 5}, {0, 3, 5}};
      return new Polyhedra(v, f);
    }

    private static Polyhedra icosahedron() {
      double phi = (1 + Math.sqrt(5)) / 2;
      double n = Math.sqrt(1 + phi * phi);
      double a = 1 / n;
      double b = phi / n;
      double[][] v = {{-a, b, 0}, {a, b, 0}, {-a, -b, 0}, {a, -b, 0}, {0, -a, b}, {0, a, b},
          {0, -a, -b}, {0, a, -b}, {b, 0, -a}, {b, 0, a}, {-b, 0, -a}, {-b, 0, a}};
      int[][] f = {{0, 11, 5}, {0, 5, 1}, {0, 1, 7}, {0, 7, 10}, {0, 10, 11}, {1, 5, 9}, {5, 11, 4},
          {11, 10, 2}, {10, 7, 6}, {7, 1, 8}, {3, 9, 4}, {3, 4, 2}, {3, 2, 6}, {3, 6, 8}, {3, 8, 9},
          {4, 9, 5}, {2, 4, 11}, {6, 2, 10}, {8, 6, 7}, {9, 8, 1}};
      return new Polyhedra(v, f);
    }

    /**
     * The dodecahedron as the dual of the icosahedron.
     *
     * <p>
     * Its vertices are the centres of the icosahedron's faces, and each of its twelve pentagons
     * surrounds one icosahedron vertex. Building it this way avoids transcribing a face table by
     * hand, which is exactly the kind of table that goes wrong unnoticed.
     */
    private static Polyhedra dodecahedron() {
      Polyhedra ico = icosahedron();
      double[][] v = new double[ico.faces.length][3];
      for (int i = 0; i < ico.faces.length; i++) {
        double[] sum = new double[3];
        for (int corner : ico.faces[i]) {
          for (int c = 0; c < 3; c++) {
            sum[c] += ico.vertices[corner][c];
          }
        }
        double length = Math.sqrt(sum[0] * sum[0] + sum[1] * sum[1] + sum[2] * sum[2]);
        for (int c = 0; c < 3; c++) {
          v[i][c] = sum[c] / length;
        }
      }

      int[][] f = new int[ico.vertices.length][];
      for (int vertex = 0; vertex < ico.vertices.length; vertex++) {
        List<Integer> around = new ArrayList<>();
        for (int face = 0; face < ico.faces.length; face++) {
          for (int corner : ico.faces[face]) {
            if (corner == vertex) {
              around.add(face);
              break;
            }
          }
        }
        double[] axis = ico.vertices[vertex];
        Vector3 normal = new Vector3(axis[0], axis[1], axis[2]).normalize();
        Vector3 u = perpendicular(normal);
        Vector3 w = normal.cross(u);
        // sort the surrounding centres by their angle about the vertex, or the pentagon
        // would come out as a self crossing star
        around.sort((p, q) -> Double.compare(angle(v[p], u, w), angle(v[q], u, w)));
        int[] face = new int[around.size()];
        for (int i = 0; i < around.size(); i++) {
          face[i] = around.get(i);
        }
        f[vertex] = face;
      }
      return new Polyhedra(v, f);
    }

    private static double angle(double[] point, Vector3 u, Vector3 w) {
      Vector3 p = new Vector3(point[0], point[1], point[2]);
      return Math.atan2(p.dot(w), p.dot(u));
    }
  }
}
