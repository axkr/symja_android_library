package org.matheclipse.core.graphics;

import static j2html.TagCreator.tag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

  /**
   * The camera: where it stands, which way it faces, and the frustum it sees through.
   *
   * <p>
   * The picture used to be framed by projecting everything and then fitting the result to the
   * canvas. That made a camera of sorts, but not one anything could be said about: a pan or a zoom
   * was computed and then undone by the fit, so {@code ViewCenter}, {@code ViewAngle},
   * {@code ViewRange} and the choice of projection had nothing to bite on, and the picture was
   * always parallel-projected however the scene said it should be seen. The frustum here is the
   * same one the interactive output builds, so the two frame a scene alike.
   */
  private static final class View {
    Vector3 eye;
    Vector3 right;
    Vector3 up;
    /** From the camera towards the scene. */
    Vector3 forward;

    /** Whether distant things are drawn smaller. */
    boolean perspective = true;
    /** Vertical field of view in radians, for a perspective camera. */
    double fieldOfView = Math.toRadians(35);
    /** Half the height the frustum covers, for a parallel one. */
    double halfHeight = 1;
    /** Nothing nearer than this or further than that is drawn. */
    double near = 0.01;
    double far = Double.MAX_VALUE;

    /**
     * An explicit camera from {@code ViewMatrix}: the transformation, then the projection.
     */
    double[] transform = null;
    double[] projection = null;

    /** Camera space: x to the right, y up, z away from the viewer into the scene. */
    Vector3 project(Vector3 p) {
      if (transform != null) {
        // {x,y,z,1} through the transformation, then the projection, then divided by its own
        // last component - which is what puts a point on the picture
        double[] v = apply(transform, p.x, p.y, p.z, 1);
        double depth = -v[2];
        double[] q = projection == null ? v : apply(projection, v[0], v[1], v[2], v[3]);
        double w = Math.abs(q[3]) < 1e-12 ? 1 : q[3];
        return new Vector3(q[0] / w, q[1] / w, depth);
      }
      Vector3 d = p.sub(eye);
      return new Vector3(d.dot(right), d.dot(up), d.dot(forward));
    }

    private static double[] apply(double[] m, double x, double y, double z, double w) {
      return new double[] {m[0] * x + m[1] * y + m[2] * z + m[3] * w,
          m[4] * x + m[5] * y + m[6] * z + m[7] * w, m[8] * x + m[9] * y + m[10] * z + m[11] * w,
          m[12] * x + m[13] * y + m[14] * z + m[15] * w};
    }

    /**
     * How far one unit at the given depth reaches across the picture, per unit of picture height.
     *
     * <p>
     * A parallel camera covers the same height whatever the depth; a perspective one covers less
     * the nearer it is, which is what makes distant things smaller.
     */
    double scaleAt(double depth, double pictureHeight) {
      if (transform != null) {
        // the matrices have already put the point on a picture running from -1 to 1
        return pictureHeight / 2;
      }
      if (!perspective) {
        return pictureHeight / (2 * halfHeight);
      }
      double reach = Math.max(1e-9, depth) * Math.tan(fieldOfView / 2);
      return pictureHeight / (2 * reach);
    }

    /** A direction given in camera space, expressed in world space. */
    Vector3 fromCamera(Vector3 cameraSpace) {
      return right.scale(cameraSpace.x).add(up.scale(cameraSpace.y))
          .add(forward.scale(-cameraSpace.z));
    }
  }

  /**
   * How a surface takes the light: its colour, the colour of its far side, and its highlight.
   *
   * <p>
   * This travels with the geometry because all three are wanted at the moment a face is shaded,
   * and only the scene knows them. {@code FaceForm[front, back]} gives the two sides their own
   * colours, and {@code Specularity} decides how strong and how tight the highlight is; both were
   * being dropped here while the interactive output honoured them.
   */
  private static final class Surface {
    final Color front;
    /** The colour of a face being looked at from behind, or {@code null} to use the front one. */
    final Color back;
    final double specularity;
    final double shininess;

    Surface(Color front, Color back, double specularity, double shininess) {
      this.front = front;
      this.back = back;
      this.specularity = specularity;
      this.shininess = shininess;
    }

    Surface(Color front) {
      this(front, null, DEFAULT_SPECULARITY, DEFAULT_SHININESS);
    }

    /** The same material in another colour, for the parts of a plot that carry their own. */
    Surface with(Color other) {
      return new Surface(other, back, specularity, shininess);
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

    /** The label of an enclosing {@code Tooltip}, or {@code null}. */
    String tooltip;

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

    Face(List<Vector3> points, Color color, double opacity) {
      this.points = points;
      this.color = color;
      this.opacity = opacity;
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
      // the outline a face carries is drawn by the crease pass, as lines of its own along the
      // edges of the shape rather than around every facet the shape was tessellated into
      DomContent polygon = tag("polygon").attr("points", path.toString().trim())
          .attr("fill", hex(color)).attr("fill-opacity", format(opacity)).attr("stroke", "none")
          .attr("stroke-width", "0")
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

    View view = camera(scene, center, maxDim, size, dataScale);
    List<Light> lights = lights(scene, view);

    // the elements are gathered into a list that also carries each one's creases, from which
    // the outline around a face is taken
    RenderList collected = new RenderList();
    if (scene.has("elements")) {
      for (JsonNode element : scene.get("elements")) {
        collect(element, dataScale, view, lights, diagonal, collected);
      }
    }
    List<Renderable> renderables = collected;
    // the scene is cut before the box and the axes are added, so the frame stays whole
    double[][] clip = clipPlanes(scene, view, dataScale);
    if (clip != null) {
      addClipPlaneSurfaces(scene, clip, min, max, view, lights, renderables);
      renderables = clipAll(renderables, clip);
    }
    // Everything in front of the camera and, when ViewRange says so, behind a far limit. A
    // perspective picture needs the near one whatever the call asked for: a face crossing the
    // plane of the camera has no projection, and drawing it anyway throws it across the picture.
    List<double[]> limits = new ArrayList<>(2);
    limits.add(new double[] {0, 0, 1, -view.near});
    if (view.far != Double.MAX_VALUE) {
      limits.add(new double[] {0, 0, -1, view.far});
    }
    renderables = clipAll(renderables, limits.toArray(new double[0][]));

    addBox(scene, min, max, view, renderables);
    addAxes(scene, ranges, min, max, dataScale, view, maxDim, renderables);

    return write(scene, renderables, width, height, view);
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
  private static View camera(ObjectNode scene, Vector3 center, double maxDim, Vector3 visualSize,
      Vector3 dataScale) {
    Vector3 direction = vector(scene.get("viewPoint"), new Vector3(1.3, -2.4, 2.0));
    if (direction.length() == 0) {
      direction = new Vector3(1.3, -2.4, 2.0);
    }
    Vector3 vertical = vector(scene.get("viewVertical"), new Vector3(0, 0, 1));

    // ViewCenter is the point put in the middle of the picture, in the data's own coordinates
    if (scene.has("viewCenter")) {
      Vector3 look = vector(scene.get("viewCenter"), null);
      if (look != null) {
        center = new Vector3(look.x * dataScale.x, look.y * dataScale.y, look.z * dataScale.z);
      }
    }

    View view = new View();
    view.perspective = !"Orthographic".equals(text(scene, "viewProjection"));
    if (scene.has("viewAngle")) {
      double degrees = scene.get("viewAngle").asDouble(35);
      if (degrees > 0 && degrees < 180) {
        view.fieldOfView = Math.toRadians(degrees);
      }
    }

    // SphericalRegion fits the sphere around the scene rather than the scene itself, so the
    // picture keeps its size however the scene is turned
    boolean spherical = scene.has("sphericalRegion") && scene.get("sphericalRegion").asBoolean();
    double radius = visualSize.length() / 2;
    double distance = direction.length() * maxDim;
    if (view.perspective) {
      double fit = spherical ? radius / Math.sin(view.fieldOfView / 2)
          : visualSize.length() / (2 * Math.tan(view.fieldOfView / 2));
      distance = Math.max(distance, fit * 1.05);
    } else {
      view.halfHeight = (spherical ? radius : visualSize.length() * 0.6) * 1.05;
    }

    view.eye = center.add(direction.normalize().scale(distance));
    // ViewVector places the camera outright rather than in units of the box
    if (scene.has("viewVector")) {
      Vector3 from = vector(scene.get("viewVector"), null);
      if (from != null) {
        view.eye = new Vector3(from.x * dataScale.x, from.y * dataScale.y, from.z * dataScale.z);
      }
    }
    view.forward = center.sub(view.eye).normalize();
    // ViewMatrix says outright what the other view options describe, so it replaces them
    if (scene.has("viewTransform")) {
      view.transform = numbers(scene.get("viewTransform"), 16);
      view.projection = scene.has("viewProjectionMatrix")
          ? numbers(scene.get("viewProjectionMatrix"), 16) : null;
      if (view.transform != null) {
        // The lights are placed against the camera, so where the camera is looking has to come
        // from the matrix as well. Otherwise the geometry obeys the matrix while the shading
        // still follows ViewPoint, and the same view comes out lit two different ways.
        double[] m = view.transform;
        view.right = new Vector3(m[0], m[1], m[2]);
        view.up = new Vector3(m[4], m[5], m[6]);
        // a viewing matrix looks down its own negative z
        view.forward = new Vector3(-m[8], -m[9], -m[10]);
        double tx = m[3];
        double ty = m[7];
        double tz = m[11];
        view.eye = view.right.scale(-tx).add(view.up.scale(-ty))
            .add(view.forward.scale(tz));
      }
    }
    view.near = Math.max(1e-6, maxDim * 0.01);
    view.far = Double.MAX_VALUE;
    // ViewRange keeps only what lies between two distances from the camera
    if (scene.has("viewRange")) {
      JsonNode range = scene.get("viewRange");
      if (range.size() >= 2) {
        view.near = Math.max(view.near, range.get(0).asDouble());
        view.far = range.get(1).asDouble();
      }
    }
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
  /** The material an element is drawn with, as the scene describes it. */
  private static Surface surfaceOf(JsonNode element) {
    Color front = new Color(element.get("color").asInt());
    Color back = element.has("backColor") ? new Color(element.get("backColor").asInt()) : null;
    double specularity =
        element.has("specularity") ? element.get("specularity").asDouble() : DEFAULT_SPECULARITY;
    double shininess = element.has("specularExponent") ? element.get("specularExponent").asDouble()
        : DEFAULT_SHININESS;
    return new Surface(front, back, specularity, Math.max(1.0, shininess));
  }

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
  /** The specular strength three.js gives a material that was not asked for a different one. */
  private static final double DEFAULT_SPECULARITY = 0x11 / 255.0;

  /** The matching shininess, which decides how tight the highlight is. */
  private static final double DEFAULT_SHININESS = 20.0;

  /**
   * The colour a face ends up with under the scene's lights.
   *
   * <p>
   * This has to agree with what the interactive output draws, because the two are meant to be the
   * same picture. That one is three.js, which works in linear light: every colour it is given is
   * taken out of sRGB first, the lighting is added up in linear, and the result is put back into
   * sRGB on the way to the screen. Multiplying the sRGB numbers together directly instead - which
   * is what this did - is not the same calculation and does not go wrong by a constant, it bends
   * the whole range: mid tones came out too dark and saturated, so the same scene drawn statically
   * and interactively did not match. A plain yellow floor is the easy one to see, coming out as
   * gold here and yellow there.
   *
   * <p>
   * The highlight is three.js's Blinn-Phong term with the same default specular colour and
   * shininess. It is a small contribution, but it is what keeps a curved solid from reading as
   * flatter here than it does in the interactive view.
   */
  private static Color shade(Surface surface, Vector3 normal, List<Light> lights, View view) {
    Vector3 n = normal.normalize();
    // a face whose normal points away is being looked at from behind, which is the side
    // FaceForm[front, back] gives its own colour to
    boolean fromBehind = n.dot(view.forward) > 0;
    if (fromBehind) {
      n = n.scale(-1);
    }
    Color base = fromBehind && surface.back != null ? surface.back : surface.front;
    double specular = toLinear(surface.specularity);
    double shininess = surface.shininess;
    // from the surface towards the camera
    Vector3 viewDir = view.forward.scale(-1).normalize();

    double diffuseR = 0;
    double diffuseG = 0;
    double diffuseB = 0;
    double specularR = 0;
    double specularG = 0;
    double specularB = 0;
    for (Light light : lights) {
      double lr = toLinear(light.color.getRed() / 255.0);
      double lg = toLinear(light.color.getGreen() / 255.0);
      double lb = toLinear(light.color.getBlue() / 255.0);
      if (light.ambient) {
        diffuseR += lr;
        diffuseG += lg;
        diffuseB += lb;
        continue;
      }
      double incidence = Math.max(0.0, n.dot(light.direction));
      if (incidence == 0) {
        continue;
      }
      diffuseR += lr * incidence;
      diffuseG += lg * incidence;
      diffuseB += lb * incidence;

      // three.js: F_Schlick(specular, dot(view, half)) * G(0.25) * D(Blinn-Phong), with the
      // reciprocal pi of the distribution cancelling the pi the light intensities carry
      Vector3 half = light.direction.add(viewDir).normalize();
      double highlight = Math.pow(Math.max(0.0, n.dot(half)), shininess);
      double fresnel = specular
          + (1.0 - specular) * Math.pow(1.0 - Math.max(0.0, viewDir.dot(half)), 5.0);
      double weight = fresnel * 0.25 * (0.5 * shininess + 1.0) * highlight * incidence;
      specularR += lr * weight;
      specularG += lg * weight;
      specularB += lb * weight;
    }

    double r = toLinear(base.getRed() / 255.0) * diffuseR + specularR;
    double g = toLinear(base.getGreen() / 255.0) * diffuseG + specularG;
    double b = toLinear(base.getBlue() / 255.0) * diffuseB + specularB;
    return new Color(clamp(toSRGB(r)), clamp(toSRGB(g)), clamp(toSRGB(b)));
  }

  /** sRGB to the linear light the lighting is added up in. */
  private static double toLinear(double c) {
    return c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
  }

  /** Linear light back to the sRGB an SVG colour is written in. */
  private static double toSRGB(double c) {
    if (c <= 0.0) {
      return 0.0;
    }
    return c <= 0.0031308 ? c * 12.92 : 1.055 * Math.pow(c, 1.0 / 2.4) - 0.055;
  }

  // --------------------------------------------------------------- primitives

  private static void collect(JsonNode element, Vector3 dataScale, View view, List<Light> lights,
      double diagonal, List<Renderable> out) {
    String type = element.get("type").asText();
    Surface color = surfaceOf(element);
    double opacity = element.has("opacity") ? element.get("opacity").asDouble(1) : 1;
    double[] matrix = matrix(element);

    RenderList sink = out instanceof RenderList ? (RenderList) out : null;
    if (sink != null) {
      sink.creases = creasesOf(element);
      sink.beginElement(element.has("tooltip") ? element.get("tooltip").asText() : null);
    }
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
        curves(element, type, color.front, opacity, matrix, dataScale, view, lights, diagonal, out);
        break;
      case "Point":
        dots(element, color.front, opacity, matrix, dataScale, view, diagonal, out);
        break;
      case "Text":
        text(element, color.front, opacity, matrix, dataScale, view, out);
        break;
      default:
        break;
    }
    if (sink != null && sink.creases != null) {
      sink.creases.emit(view, out);
      sink.creases = null;
    }
  }

  /**
   * The outline the element asks for, or {@code null} when it draws none.
   *
   * <p>
   * Every face carries one unless an {@code EdgeForm[None]} turned it off, which is what a
   * plotted surface does - its mesh is drawn as lines of its own instead.
   */
  private static Creases creasesOf(JsonNode element) {
    Color color = edgeColor(element);
    if (color == null) {
      return null;
    }
    double opacity = element.has("edgeOpacity") ? element.get("edgeOpacity").asDouble(1) : 1;
    double width = element.has("edgeThickness") ? element.get("edgeThickness").asDouble(1) : 1;
    double angle = element.has("edgeAngle") ? element.get("edgeAngle").asDouble(30) : 30;
    return new Creases(color, opacity, width, angle);
  }

  /** An indexed triangle mesh, one flat facet per triangle. */
  private static void polygons(JsonNode element, Surface color, double opacity, double[] matrix,
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
    JsonNode vertexNormals = element.get("vertexNormals");

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
      Surface base = color;
      if (vertexColors != null && vertexColors.size() >= (a + 1) * 3) {
        // one colour per facet, averaged over its corners, is as far as a flat fill can go
        base = color.with(average(vertexColors, a, b, c));
      }
      // A surface that was sampled smoothly carries a normal per vertex. Shading each facet by
      // its own flat normal instead makes a curved surface look faceted, and turns a highlight
      // into a scatter of bright triangles; averaging the three vertex normals follows the
      // surface the sampling meant, which is what the interactive output shows.
      Vector3 smooth = averageNormal(vertexNormals, a, b, c, matrix, dataScale);
      addShadedFace(out, view, lights, base, opacity, smooth, pa, pb, pc);
    }
  }

  /** The averaged vertex normal of one facet, or {@code null} when the scene supplied none. */
  private static Vector3 averageNormal(JsonNode normals, int a, int b, int c, double[] matrix,
      Vector3 dataScale) {
    if (normals == null || normals.size() < (Math.max(a, Math.max(b, c)) + 1) * 3) {
      return null;
    }
    Vector3 sum = new Vector3(0, 0, 0);
    for (int index : new int[] {a, b, c}) {
      sum = sum.add(new Vector3(normals.get(index * 3).asDouble(),
          normals.get(index * 3 + 1).asDouble(), normals.get(index * 3 + 2).asDouble()));
    }
    if (sum.length() == 0) {
      return null;
    }
    if (matrix != null) {
      // a direction is carried by the rotation part of the transformation only
      sum = new Vector3(matrix[0] * sum.x + matrix[4] * sum.y + matrix[8] * sum.z,
          matrix[1] * sum.x + matrix[5] * sum.y + matrix[9] * sum.z,
          matrix[2] * sum.x + matrix[6] * sum.y + matrix[10] * sum.z);
    }
    // the box scaling stretches the geometry, so a normal follows its reciprocal
    Vector3 scaled = new Vector3(dataScale.x == 0 ? sum.x : sum.x / dataScale.x,
        dataScale.y == 0 ? sum.y : sum.y / dataScale.y,
        dataScale.z == 0 ? sum.z : sum.z / dataScale.z);
    return scaled.length() == 0 ? null : scaled.normalize();
  }

  private static void sphere(Vector3 centre, double radius, Surface color, double opacity,
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
  private static void barrel(Vector3 start, Vector3 end, double radius, boolean cone,
      Surface color,
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
        addFace(out, view, lights, color, opacity, bottom.get(i), bottom.get(i + 1), top.get(i));
      } else {
        addFace(out, view, lights, color, opacity, bottom.get(i), bottom.get(i + 1),
            top.get(i + 1), top.get(i));
      }
    }
    cap(out, view, lights, color, opacity, bottom, place(start, matrix, dataScale));
    if (!cone) {
      cap(out, view, lights, color, opacity, top, place(end, matrix, dataScale));
    }
  }

  private static void cap(List<Renderable> out, View view, List<Light> lights, Surface color,
      double opacity, List<Vector3> rim, Vector3 centre) {
    for (int i = 0; i + 1 < rim.size(); i++) {
      addFace(out, view, lights, color, opacity, centre, rim.get(i), rim.get(i + 1));
    }
  }

  private static void cuboid(Vector3 min, Vector3 max, Surface color, double opacity, double[] matrix,
      Vector3 dataScale, View view, List<Light> lights, List<Renderable> out) {
    Vector3[] corner = new Vector3[8];
    for (int i = 0; i < 8; i++) {
      corner[i] = place(new Vector3((i & 1) == 0 ? min.x : max.x, (i & 2) == 0 ? min.y : max.y,
          (i & 4) == 0 ? min.z : max.z), matrix, dataScale);
    }
    int[][] faces =
        {{0, 2, 3, 1}, {4, 5, 7, 6}, {0, 1, 5, 4}, {2, 6, 7, 3}, {0, 4, 6, 2}, {1, 3, 7, 5}};
    for (int[] face : faces) {
      addFace(out, view, lights, color, opacity, corner[face[0]], corner[face[1]], corner[face[2]],
          corner[face[3]]);
    }
  }

  private static void polyhedron(JsonNode element, Surface color, double opacity, double[] matrix,
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
      addFace(out, view, lights, color, opacity, corners);
    }
  }

  private static void tube(JsonNode element, Surface color, double opacity, double[] matrix,
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
    // an arrowhead is a solid cone, drawn in the line's own colour
    barrel(base, tip, size * 0.35, true, new Surface(color), opacity, matrix, dataScale, view,
        lights, out);
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

  // ---------------------------------------------------------------- clipping

  /**
   * The clipping planes, moved into the space the geometry is already in.
   *
   * <p>
   * A plane arrives in the data's own coordinates, but the geometry has been through two changes
   * by the time it is here: the box scaling that squares the data up, and the camera. Both are
   * applied to the planes instead of undone on every vertex. Scaling a point by {@code s} turns
   * {@code n.p + d} into {@code (n/s).p' + d}, and the camera is a turn and a shift, so the normal
   * is read off in the camera's own directions and the offset follows the eye.
   *
   * @return one {@code {a, b, c, d}} per plane, keeping the side where the value is positive
   */
  private static double[][] clipPlanes(ObjectNode scene, View view, Vector3 dataScale) {
    if (!scene.has("clipPlanes")) {
      return null;
    }
    JsonNode node = scene.get("clipPlanes");
    List<double[]> planes = new ArrayList<>();
    for (JsonNode plane : node) {
      if (plane.size() < 4) {
        continue;
      }
      Vector3 n = new Vector3(plane.get(0).asDouble() / (dataScale.x == 0 ? 1 : dataScale.x),
          plane.get(1).asDouble() / (dataScale.y == 0 ? 1 : dataScale.y),
          plane.get(2).asDouble() / (dataScale.z == 0 ? 1 : dataScale.z));
      double d = plane.get(3).asDouble();
      if (n.length() < 1e-12) {
        continue;
      }
      Vector3 camera = new Vector3(n.dot(view.right), n.dot(view.up), n.dot(view.forward));
      planes.add(new double[] {camera.x, camera.y, camera.z, n.dot(view.eye) + d});
    }
    return planes.isEmpty() ? null : planes.toArray(new double[0][]);
  }

  /**
   * Draw the clipping planes themselves, which is what {@code ClipPlanesStyle} asks for.
   *
   * <p>
   * A plane has no edges of its own, so each is drawn as a square big enough to cross the whole
   * scene and then cut down by the box and by the other planes. Without a style it is not drawn at
   * all, which is what {@code ClipPlanesStyle} defaults to.
   */
  private static void addClipPlaneSurfaces(ObjectNode scene, double[][] planes, Vector3 min,
      Vector3 max, View view, List<Light> lights, List<Renderable> out) {
    if (!scene.has("clipPlanesStyle")) {
      return;
    }
    JsonNode styles = scene.get("clipPlanesStyle");
    Vector3 centre = min.add(max).scale(0.5);
    Vector3 cameraCentre = view.project(centre);
    double reach = max.sub(min).length();

    for (int i = 0; i < planes.length && i < styles.size(); i++) {
      double[] plane = planes[i];
      JsonNode style = styles.get(i);
      Color color = new Color(style.get("color").asInt());
      double opacity = style.has("opacity") ? style.get("opacity").asDouble(1) : 1;

      // a pair of directions lying in the plane, to sweep the square out along
      Vector3 normal = new Vector3(plane[0], plane[1], plane[2]).normalize();
      Vector3 u = normal.cross(new Vector3(0, 0, 1));
      if (u.length() < 1e-6) {
        u = normal.cross(new Vector3(0, 1, 0));
      }
      u = u.normalize();
      Vector3 v = normal.cross(u).normalize();
      // the point of the plane nearest the middle of the scene
      double distance = side(plane, cameraCentre);
      Vector3 origin = cameraCentre.sub(normal.scale(distance));

      List<Vector3> square = new ArrayList<>(4);
      square.add(origin.add(u.scale(reach)).add(v.scale(reach)));
      square.add(origin.add(u.scale(-reach)).add(v.scale(reach)));
      square.add(origin.add(u.scale(-reach)).add(v.scale(-reach)));
      square.add(origin.add(u.scale(reach)).add(v.scale(-reach)));

      // keep it inside the scene's own box, and on the kept side of every other plane
      for (int j = 0; j < planes.length; j++) {
        if (j != i) {
          square = clipPolygon(square, planes[j]);
        }
      }
      for (double[] wall : boxPlanes(min, max, view)) {
        square = clipPolygon(square, wall);
      }
      if (square.size() >= 3) {
        // The plane lies exactly in the cut it made, so which of the two is nearer is a coin
        // toss and the cut surface wins about half the time, leaving the plane in tatters. A
        // nudge towards the camera settles it, the same way the interactive renderer offsets a
        // mesh drawn over the surface it outlines.
        double nudge = reach * 1e-3;
        List<Vector3> lifted = new ArrayList<>(square.size());
        for (Vector3 corner : square) {
          lifted.add(new Vector3(corner.x, corner.y, corner.z - nudge));
        }
        out.add(new Face(lifted, color, opacity));
      }
    }
  }

  /** The six sides of the scene's box, as inward facing half spaces in camera space. */
  private static List<double[]> boxPlanes(Vector3 min, Vector3 max, View view) {
    List<double[]> walls = new ArrayList<>(6);
    Vector3[] normals = {new Vector3(1, 0, 0), new Vector3(-1, 0, 0), new Vector3(0, 1, 0),
        new Vector3(0, -1, 0), new Vector3(0, 0, 1), new Vector3(0, 0, -1)};
    Vector3[] points = {min, max, min, max, min, max};
    for (int i = 0; i < 6; i++) {
      Vector3 n = normals[i];
      Vector3 camera = new Vector3(n.dot(view.right), n.dot(view.up), n.dot(view.forward));
      Vector3 onPlane = view.project(points[i]);
      walls.add(new double[] {camera.x, camera.y, camera.z, -camera.dot(onPlane)});
    }
    return walls;
  }

  private static double side(double[] plane, Vector3 p) {
    return plane[0] * p.x + plane[1] * p.y + plane[2] * p.z + plane[3];
  }

  private static Vector3 crossing(Vector3 a, Vector3 b, double sa, double sb) {
    double t = sa / (sa - sb);
    return new Vector3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t);
  }

  /** Cut everything down to the half spaces the planes leave. */
  private static List<Renderable> clipAll(List<Renderable> renderables, double[][] planes) {
    List<Renderable> kept = new ArrayList<>(renderables.size());
    for (Renderable r : renderables) {
      if (r instanceof Face) {
        Face face = (Face) r;
        List<Vector3> points = face.points;
        for (double[] plane : planes) {
          points = clipPolygon(points, plane);
          if (points.size() < 3) {
            break;
          }
        }
        if (points.size() >= 3) {
          // cutting a face makes a new one, and anything the old one carried has to come with it
          Face clipped = new Face(points, face.color, face.opacity);
          clipped.tooltip = face.tooltip;
          kept.add(clipped);
        }
      } else if (r instanceof Polyline) {
        Polyline line = (Polyline) r;
        for (List<Vector3> piece : clipPolyline(line.points, planes)) {
          Polyline clipped =
              new Polyline(piece, line.color, line.opacity, line.width, line.dashArray);
          clipped.tooltip = line.tooltip;
          kept.add(clipped);
        }
      } else if (r instanceof Dot) {
        if (inside(((Dot) r).point, planes)) {
          kept.add(r);
        }
      } else if (r instanceof Label) {
        if (inside(((Label) r).point, planes)) {
          kept.add(r);
        }
      } else {
        kept.add(r);
      }
    }
    return kept;
  }

  private static boolean inside(Vector3 p, double[][] planes) {
    for (double[] plane : planes) {
      if (side(plane, p) < 0) {
        return false;
      }
    }
    return true;
  }

  /** Sutherland and Hodgman: walk the edges, keeping what is inside and cutting what crosses. */
  private static List<Vector3> clipPolygon(List<Vector3> points, double[] plane) {
    List<Vector3> out = new ArrayList<>(points.size() + 2);
    int n = points.size();
    for (int i = 0; i < n; i++) {
      Vector3 current = points.get(i);
      Vector3 previous = points.get((i + n - 1) % n);
      double sc = side(plane, current);
      double sp = side(plane, previous);
      if (sc >= 0) {
        if (sp < 0) {
          out.add(crossing(previous, current, sp, sc));
        }
        out.add(current);
      } else if (sp >= 0) {
        out.add(crossing(previous, current, sp, sc));
      }
    }
    return out;
  }

  /** A polyline may come back in several pieces, wherever it left and re-entered. */
  private static List<List<Vector3>> clipPolyline(List<Vector3> points, double[][] planes) {
    List<List<Vector3>> pieces = new ArrayList<>();
    List<Vector3> current = new ArrayList<>();
    for (int i = 0; i + 1 < points.size(); i++) {
      Vector3 a = points.get(i);
      Vector3 b = points.get(i + 1);
      double[] segment = clipSegment(a, b, planes);
      if (segment == null) {
        if (current.size() > 1) {
          pieces.add(current);
        }
        current = new ArrayList<>();
        continue;
      }
      Vector3 from = crossing(a, b, -segment[0], 1 - segment[0]);
      Vector3 to = crossing(a, b, -segment[1], 1 - segment[1]);
      if (current.isEmpty()) {
        current.add(from);
      }
      current.add(to);
      if (segment[1] < 1.0) {
        if (current.size() > 1) {
          pieces.add(current);
        }
        current = new ArrayList<>();
      }
    }
    if (current.size() > 1) {
      pieces.add(current);
    }
    return pieces;
  }

  /** The stretch of a segment that survives, as two fractions along it, or null if none does. */
  private static double[] clipSegment(Vector3 a, Vector3 b, double[][] planes) {
    double enter = 0.0;
    double leave = 1.0;
    for (double[] plane : planes) {
      double sa = side(plane, a);
      double sb = side(plane, b);
      if (sa < 0 && sb < 0) {
        return null;
      }
      if (sa < 0) {
        enter = Math.max(enter, sa / (sa - sb));
      } else if (sb < 0) {
        leave = Math.min(leave, sa / (sa - sb));
      }
    }
    return enter < leave ? new double[] {enter, leave} : null;
  }

  // ------------------------------------------------------------ depth sorting

  /**
   * Above this many faces the ordering is left to the depth sort alone.
   *
   * <p>
   * The repair below compares every pair of faces that overlap on screen. That is affordable for a
   * scene built from a few large shapes, which is exactly the scene the depth sort gets wrong. A
   * finely sampled surface has thousands of faces, but they are all small, and the depth of a small
   * face describes it well enough that there is nothing to repair.
   */
  private static final int MAX_REPAIR_FACES = 2000;

  /** Distances below this fraction of the scene count as "on the plane" rather than either side. */
  private static final double PLANE_TOLERANCE = 1e-6;

  /**
   * Puts faces the depth sort got the wrong way round back into order.
   *
   * <p>
   * Faces are painted back to front, ordered by the depth of their middle. That describes a face
   * only while the face is small. A floor drawn as two triangles across the whole picture has its
   * middle nearer the camera than a small box standing on it, so the floor was painted last and
   * covered the box: in the reference example a yellow wedge lay across the green cuboid.
   *
   * <p>
   * The middle of a face is the wrong thing to compare. What settles the order for two faces that
   * do not cut through each other is which side of the other's plane each lies on, and that answer
   * does not care how big either one is. Every pair that overlaps on screen is asked that question
   * here, and the answers are followed as an ordering. Nothing is cut up, so there are no new
   * seams; faces that genuinely pass through each other give no answer and keep the order the depth
   * sort gave them.
   */
  private static void repairOrder(List<Renderable> renderables) {
    List<Face> faces = new ArrayList<>();
    List<Integer> slots = new ArrayList<>();
    for (int i = 0; i < renderables.size(); i++) {
      if (renderables.get(i) instanceof Face) {
        faces.add((Face) renderables.get(i));
        slots.add(i);
      }
    }
    int n = faces.size();
    if (n < 2 || n > MAX_REPAIR_FACES) {
      return;
    }

    double[][] box = new double[n][];
    double[][] plane = new double[n][];
    for (int i = 0; i < n; i++) {
      box[i] = screenBox(faces.get(i));
      plane[i] = planeOf(faces.get(i));
    }
    double tolerance = sceneSize(box) * PLANE_TOLERANCE;

    // before[i] holds the faces that have to be painted before face i
    List<List<Integer>> before = new ArrayList<>(n);
    int[] waiting = new int[n];
    for (int i = 0; i < n; i++) {
      before.add(new ArrayList<>(2));
    }
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        if (!overlaps(box[i], box[j])) {
          continue;
        }
        int order = mustPaintFirst(faces.get(i), plane[i], faces.get(j), plane[j], tolerance);
        if (order < 0) {
          before.get(j).add(i);
          waiting[j]++;
        } else if (order > 0) {
          before.get(i).add(j);
          waiting[i]++;
        }
      }
    }

    // walk the faces in the order the depth sort gave, holding one back until everything that has
    // to come before it has been placed; a face caught in a cycle keeps its original place
    List<Integer> order = new ArrayList<>(n);
    boolean[] placed = new boolean[n];
    boolean progress = true;
    while (order.size() < n && progress) {
      progress = false;
      for (int i = 0; i < n; i++) {
        if (!placed[i] && waiting[i] == 0) {
          placed[i] = true;
          order.add(i);
          progress = true;
          for (int k = 0; k < n; k++) {
            if (!placed[k] && before.get(k).contains(i)) {
              waiting[k]--;
            }
          }
        }
      }
    }
    for (int i = 0; i < n; i++) {
      if (!placed[i]) {
        order.add(i);
      }
    }

    for (int k = 0; k < n; k++) {
      renderables.set(slots.get(k), faces.get(order.get(k)));
    }
  }

  /** The screen rectangle a face covers, as {@code minX, minY, maxX, maxY}. */
  private static double[] screenBox(Face face) {
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;
    for (Vector3 p : face.points) {
      minX = Math.min(minX, p.x);
      minY = Math.min(minY, p.y);
      maxX = Math.max(maxX, p.x);
      maxY = Math.max(maxY, p.y);
    }
    return new double[] {minX, minY, maxX, maxY};
  }

  private static double sceneSize(double[][] boxes) {
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = -Double.MAX_VALUE;
    double maxY = -Double.MAX_VALUE;
    for (double[] b : boxes) {
      minX = Math.min(minX, b[0]);
      minY = Math.min(minY, b[1]);
      maxX = Math.max(maxX, b[2]);
      maxY = Math.max(maxY, b[3]);
    }
    return Math.max(maxX - minX, maxY - minY);
  }

  private static boolean overlaps(double[] a, double[] b) {
    return a[0] <= b[2] && b[0] <= a[2] && a[1] <= b[3] && b[1] <= a[3];
  }

  /** The plane of a face as {@code nx, ny, nz, d}, or {@code null} if it has no area. */
  private static double[] planeOf(Face face) {
    List<Vector3> p = face.points;
    for (int i = 2; i < p.size(); i++) {
      Vector3 normal = p.get(i - 1).sub(p.get(0)).cross(p.get(i).sub(p.get(0)));
      if (normal.length() > 0) {
        Vector3 unit = normal.normalize();
        return new double[] {unit.x, unit.y, unit.z, unit.dot(p.get(0))};
      }
    }
    return null;
  }

  /**
   * Which of two faces has to be painted first.
   *
   * <p>
   * Camera space has the camera at the origin looking towards growing z, so of two things on the
   * same line of sight the one with the larger z is the one further away and has to be painted
   * first. A face that lies wholly on the far side of the other's plane is therefore the one that
   * goes first, and the far side is the side the camera is not on.
   *
   * @return a negative number if the first face goes first, a positive one if the second does, and
   *         zero when neither plane separates them - they cut through each other, or are coplanar
   */
  private static int mustPaintFirst(Face first, double[] firstPlane, Face second,
      double[] secondPlane, double tolerance) {
    int bySecond = sideOf(first, secondPlane, tolerance);
    if (bySecond != 0) {
      // the first face is wholly on one side of the second's plane
      return bySecond < 0 ? -1 : 1;
    }
    int byFirst = sideOf(second, firstPlane, tolerance);
    if (byFirst != 0) {
      return byFirst < 0 ? 1 : -1;
    }
    // Neither plane separates them. When they are the same plane, one solid is resting exactly on
    // another - a box standing on the floor it is drawn with - and there is no depth between them
    // to sort by. What settles it then is that a see through face is a tint over whatever it
    // covers, so the solid one has to be underneath it. Left to the depth order, the box's own
    // half transparent underside was painted between the floor's two triangles: it tinted the
    // first one green and the second was then painted over the top of that in solid yellow, and
    // the join between the two showed as a hard diagonal across the floor.
    if (coplanar(second, firstPlane, tolerance)) {
      boolean firstSolid = first.opacity >= 1.0;
      boolean secondSolid = second.opacity >= 1.0;
      if (firstSolid != secondSolid) {
        return firstSolid ? -1 : 1;
      }
    }
    return 0;
  }

  /** Whether every corner of a face lies in the given plane. */
  private static boolean coplanar(Face face, double[] plane, double tolerance) {
    if (plane == null) {
      return false;
    }
    for (Vector3 p : face.points) {
      double distance = plane[0] * p.x + plane[1] * p.y + plane[2] * p.z - plane[3];
      if (Math.abs(distance) > tolerance) {
        return false;
      }
    }
    return true;
  }

  /**
   * Whether a face lies wholly on the far side of a plane, wholly on the near side, or neither.
   *
   * @return -1 when every corner is further from the camera than the plane, 1 when every corner is
   *         nearer, and 0 when the face straddles the plane or lies in it
   */
  private static int sideOf(Face face, double[] plane, double tolerance) {
    if (plane == null) {
      return 0;
    }
    // the camera sits at the origin of this space, so the sign of -d says which side it is on
    double cameraSide = -plane[3];
    if (Math.abs(cameraSide) <= tolerance) {
      return 0;
    }
    boolean anyNear = false;
    boolean anyFar = false;
    for (Vector3 p : face.points) {
      double distance = plane[0] * p.x + plane[1] * p.y + plane[2] * p.z - plane[3];
      if (Math.abs(distance) <= tolerance) {
        continue;
      }
      if (distance * cameraSide > 0) {
        anyNear = true;
      } else {
        anyFar = true;
      }
    }
    if (anyNear && !anyFar) {
      return 1;
    }
    if (anyFar && !anyNear) {
      return -1;
    }
    return 0;
  }

  // -------------------------------------------------------------- SVG writing

  private static String write(ObjectNode scene, List<Renderable> renderables, double width,
      double height, View view) {
    Collections.sort(renderables);
    repairOrder(renderables);

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
    // ImagePadding is room kept inside the picture for whatever sticks out of the drawing, in
    // printer's points; without one the renderer keeps its own modest allowance for tick labels
    double[] padding = insets(scene, "imagePadding", PADDING);
    // ImageMargins is room kept outside the picture, so the canvas grows by it and the drawing
    // stays the size it was asked to be
    double[] margins = insets(scene, "imageMargins", 0);

    double areaLeft = margins[0] + padding[0];
    double areaBottom = margins[2] + padding[2];
    double areaWidth = Math.max(1.0, width - padding[0] - padding[1]);
    double areaHeight = Math.max(1.0, height - padding[2] - padding[3]);

    // PlotRegion narrows the drawing to a part of that area, in scaled coordinates of it
    if (scene.has("plotRegion")) {
      JsonNode region = scene.get("plotRegion");
      double x0 = region.get(0).get(0).asDouble();
      double x1 = region.get(0).get(1).asDouble();
      double y0 = region.get(1).get(0).asDouble();
      double y1 = region.get(1).get(1).asDouble();
      areaLeft += x0 * areaWidth;
      areaBottom += y0 * areaHeight;
      areaWidth = Math.max(1.0, (x1 - x0) * areaWidth);
      areaHeight = Math.max(1.0, (y1 - y0) * areaHeight);
    }

    double canvasHeight = height + margins[2] + margins[3];
    double centreX = areaLeft + areaWidth / 2.0;
    double centreY = areaBottom + areaHeight / 2.0;

    // The camera decides how big things come out; the drawing area only says where the middle of
    // the picture is and how much of it there is to fill.
    for (Vector3 p : all) {
      double scale = view.scaleAt(p.z, areaHeight);
      p.x = centreX + p.x * scale;
      // SVG counts y downwards
      p.y = canvasHeight - (centreY + p.y * scale);
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
    // Prolog is drawn under the scene and Epilog over it. Both arrive already drawn, as a
    // complete SVG picture the size of this one, so the inner <svg> is placed on top of this one
    // rather than having its contents merged in: it keeps its own coordinates that way, and the
    // interactive output can lay the very same picture over its canvas.
    addOverlay(scene, "prolog", width, height, content);
    // An SVG title is what a viewer shows on hover, and it has to be a child of the element it
    // describes, so a labelled facet needs a group to hang it on. The facets are in drawing order
    // by then, and the facets of one surface mostly stay together in it, so a run that shares a
    // label shares its group: a labelled surface would otherwise repeat the same title once per
    // facet. Only a run is merged, never a reordering, so what is drawn is unchanged.
    for (int i = 0; i < renderables.size();) {
      Renderable first = renderables.get(i);
      String tooltip = first.tooltip;
      if (tooltip == null || tooltip.isEmpty()) {
        content.add(first.toSVG());
        i++;
        continue;
      }
      int end = i;
      while (end < renderables.size() && tooltip.equals(renderables.get(end).tooltip)) {
        end++;
      }
      j2html.tags.ContainerTag<?> group = tag("g").with(tag("title").withText(tooltip));
      for (int k = i; k < end; k++) {
        group.with(renderables.get(k).toSVG());
      }
      content.add(group);
      i = end;
    }
    if (scene.has("plotLabel")) {
      content
          .add(tag("text").attr("x", format(width / 2)).attr("y", format(PADDING * 0.7))
              .attr("text-anchor", "middle").attr("font-family", "Arial, sans-serif")
              .attr("font-size", format(
                  scene.has("labelFontSize") ? scene.get("labelFontSize").asDouble(12) * 1.2 : 14))
              .with(new UnescapedText(escape(scene.get("plotLabel").asText()))));
    }
    addOverlay(scene, "epilog", width, height, content);

    double[] canvasMargins = insets(scene, "imageMargins", 0);
    double canvasWidth = width + canvasMargins[0] + canvasMargins[1];
    double canvasTotalHeight = height + canvasMargins[2] + canvasMargins[3];
    return tag("svg").with(content).attr("xmlns", "http://www.w3.org/2000/svg")
        .attr("width", canvasWidth).attr("height", canvasTotalHeight)
        .attr("viewBox", "0 0 " + canvasWidth + " " + canvasTotalHeight).render();
  }

  /**
   * Four insets from the scene as left, right, bottom, top, or the same default on every side.
   */
  private static double[] insets(ObjectNode scene, String name, double fallback) {
    if (scene.has(name)) {
      JsonNode node = scene.get(name);
      if (node.size() >= 4) {
        return new double[] {node.get(0).asDouble(), node.get(1).asDouble(),
            node.get(2).asDouble(), node.get(3).asDouble()};
      }
    }
    return new double[] {fallback, fallback, fallback, fallback};
  }

  /** Place a {@code Prolog} or {@code Epilog} picture over the drawing, at its own size. */
  private static void addOverlay(ObjectNode scene, String name, double width, double height,
      List<DomContent> content) {
    if (!scene.has(name)) {
      return;
    }
    String svg = scene.get(name).asText();
    if (svg == null || svg.isEmpty()) {
      return;
    }
    // the overlay is already a complete picture of the right size, so it goes in as it is
    content.add(new UnescapedText(svg));
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

  private static void addFace(List<Renderable> out, View view, List<Light> lights, Surface base,
      double opacity, Vector3... corners) {
    addShadedFace(out, view, lights, base, opacity, null, corners);
  }

  /**
   * Adds one face, lit by {@code shadingNormal} when the scene supplied one.
   *
   * <p>
   * The shape of the face still comes from its corners; only the direction it is lit from is
   * taken from the sampling, so that a smoothly sampled surface is shaded as the curve it stands
   * for rather than as the flat triangles it is drawn with.
   */
  private static void addShadedFace(List<Renderable> out, View view, List<Light> lights,
      Surface base, double opacity, Vector3 shadingNormal, Vector3... corners) {
    if (corners.length < 3) {
      return;
    }
    Vector3 flat = corners[1].sub(corners[0]).cross(corners[2].sub(corners[0]));
    if (flat.length() == 0) {
      return;
    }
    if (out instanceof RenderList && ((RenderList) out).creases != null) {
      // the facet is handed on in world space so that the outline can be taken from the creases
      // of the shape rather than from every seam of its tessellation. The crease is a fold in
      // the geometry, so it is the flat normal that decides it and not the one the sampling
      // supplied for shading, which is smooth across the fold by design.
      ((RenderList) out).creases.add(flat, corners);
    }
    Vector3 normal = flat;
    if (shadingNormal != null) {
      // keep the side the flat face is facing, so a back face still takes the back colour
      normal = shadingNormal.dot(normal) < 0 ? shadingNormal.scale(-1) : shadingNormal;
    }
    Color lit = shade(base, normal, lights, view);
    List<Vector3> projected = new ArrayList<>(corners.length);
    for (Vector3 corner : corners) {
      projected.add(view.project(corner));
    }
    out.add(new Face(projected, lit, opacity));
  }

  /** Turn a grid of points into quads, as a tessellated sphere or tube produces. */
  private static void quads(Vector3[][] grid, Surface color, double opacity, View view,
      List<Light> lights, List<Renderable> out) {
    for (int i = 0; i + 1 < grid.length; i++) {
      for (int j = 0; j + 1 < grid[i].length; j++) {
        addFace(out, view, lights, color, opacity, grid[i][j], grid[i + 1][j], grid[i + 1][j + 1],
            grid[i][j + 1]);
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

  /**
   * The renderables gathered so far, plus the creases of the element being tessellated.
   *
   * <p>
   * A primitive hands every facet it builds to {@link Creases} on its way into the list, which is
   * how the outline of a shape is recovered without every primitive having to know its own
   * silhouette. The field is null while an element that draws no outline is collected.
   */
  private static final class RenderList extends ArrayList<Renderable> {
    private static final long serialVersionUID = 1L;

    transient Creases creases;

    /**
     * The tooltip of the element being collected, stamped onto everything it adds.
     *
     * <p>
     * One element becomes many facets, and an SVG title has to be a child of the shape it
     * describes, so the label is copied onto each rather than held once for the element. The facets
     * cannot be gathered into one group here, because they are painted in depth order and interleave
     * with every other element; grouping them at collection time would put this element in front of
     * or behind things it should be woven into. The writer merges the runs that do end up next to
     * one another after sorting, which is where the markup is kept small: a forty by forty labelled
     * surface costs about thirty groups rather than sixteen hundred.
     */
    transient String tooltip;

    /** Start a new element, whose facets carry its label. */
    void beginElement(String elementTooltip) {
      this.tooltip = elementTooltip;
    }

    @Override
    public boolean add(Renderable renderable) {
      if (tooltip != null) {
        renderable.tooltip = tooltip;
      }
      return super.add(renderable);
    }
  }

  /**
   * The edges of one element's facets, from which the outline Wolfram draws around a face is
   * taken.
   *
   * <p>
   * The outline follows the shape rather than the tessellation: an edge is drawn where a facet has
   * no neighbour at all, or where the two facets that meet along it turn by more than the crease
   * angle. A {@code Cuboid} therefore shows its twelve edges, a {@code Cylinder} the two circles
   * where its caps meet the barrel, and a {@code Sphere} nothing - which is what Wolfram draws.
   * An explicit {@code EdgeForm} drops the angle to a hair above zero, and the mesh appears.
   */
  private static final class Creases {
    /** Beyond this many facets the outline is dropped rather than allowed to cost the picture. */
    private static final int MAX_FACETS = 200000;
    /** Corners are rounded to a ten millionth before facets are joined up along their edges. */
    private static final double QUANTUM = 1e7;

    private final Color color;
    private final double opacity;
    private final double width;
    private final double cosLimit;
    private final Map<String, Edge> edges = new LinkedHashMap<>();
    private int facets = 0;

    Creases(Color color, double opacity, double width, double angle) {
      this.color = color;
      this.opacity = opacity;
      this.width = width;
      this.cosLimit = Math.cos(Math.toRadians(Math.max(0.0, Math.min(180.0, angle))));
    }

    /** One facet, given by its world space corners and the normal they turned out to have. */
    void add(Vector3 normal, Vector3[] corners) {
      if (corners.length < 3 || ++facets > MAX_FACETS) {
        return;
      }
      Vector3 unit = normal.normalize();
      for (int i = 0; i < corners.length; i++) {
        Vector3 a = corners[i];
        Vector3 b = corners[(i + 1) % corners.length];
        String key = key(a, b);
        if (key == null) {
          // a degenerate edge, as the pole of a tessellated sphere produces
          continue;
        }
        Edge edge = edges.get(key);
        if (edge == null) {
          edges.put(key, new Edge(a, b, unit));
        } else {
          edge.meet(unit);
        }
      }
    }

    /** Draw the edges that survived, farthest first like everything else. */
    void emit(View view, List<Renderable> out) {
      if (facets > MAX_FACETS) {
        return;
      }
      for (Edge edge : edges.values()) {
        if (!edge.isOutline(cosLimit)) {
          continue;
        }
        Polyline line = new Polyline(List.of(view.project(edge.a), view.project(edge.b)), color,
            opacity, width, null);
        // an outline shares its place with the faces it borders, so it is nudged towards the
        // camera to settle which of them the painter draws last
        line.depth -= Math.abs(line.depth) * 1e-3;
        out.add(line);
      }
    }

    private static String key(Vector3 a, Vector3 b) {
      long ax = Math.round(a.x * QUANTUM);
      long ay = Math.round(a.y * QUANTUM);
      long az = Math.round(a.z * QUANTUM);
      long bx = Math.round(b.x * QUANTUM);
      long by = Math.round(b.y * QUANTUM);
      long bz = Math.round(b.z * QUANTUM);
      if (ax == bx && ay == by && az == bz) {
        return null;
      }
      // an edge is the same edge whichever facet walked it, so the ends are put in a fixed order
      boolean forward = ax < bx || (ax == bx && (ay < by || (ay == by && az < bz)));
      return forward ? ax + "," + ay + "," + az + "|" + bx + "," + by + "," + bz
          : bx + "," + by + "," + bz + "|" + ax + "," + ay + "," + az;
    }
  }

  /** One edge of a facet, with the facets that meet along it. */
  private static final class Edge {
    final Vector3 a;
    final Vector3 b;
    final Vector3 normal;
    Vector3 neighbour = null;
    int uses = 1;

    Edge(Vector3 a, Vector3 b, Vector3 normal) {
      this.a = a;
      this.b = b;
      this.normal = normal;
    }

    void meet(Vector3 other) {
      uses++;
      if (neighbour == null) {
        neighbour = other;
      }
    }

    boolean isOutline(double cosLimit) {
      if (uses != 2 || neighbour == null) {
        // a border of the surface, or a seam where more than two facets meet
        return true;
      }
      // the sign of a normal follows the winding, which two facets need not agree on, so it is
      // the angle between the planes that decides and not the one between the directions
      return Math.abs(normal.dot(neighbour)) < cosLimit;
    }
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

  /** A fixed length run of numbers from the scene, or {@code null} when it is not there. */
  private static double[] numbers(JsonNode node, int count) {
    if (node == null || node.size() != count) {
      return null;
    }
    double[] out = new double[count];
    for (int i = 0; i < count; i++) {
      out[i] = node.get(i).asDouble();
    }
    return out;
  }

  /** A string field of the scene, or {@code null}. */
  private static String text(ObjectNode scene, String name) {
    return scene.has(name) ? scene.get(name).asText() : null;
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
