package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Checks the static SVG rendering of {@code Graphics3D}.
 *
 * <p>
 * This renderer takes the scene {@link WebGLGraphics3D#buildScene} builds, so it inherits the
 * colours, the lights, the camera and the tick labels rather than deciding any of them again. What
 * is worth asserting here is that it really does consume that scene and turn every kind of element
 * into geometry: the two outputs used to be separate implementations that had drifted apart, and
 * nothing failed when they disagreed.
 */
public class SVGGraphics3DTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z,p)");
  }

  private static String svg(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result.isAST(), input + " did not evaluate to a graphic: " + result);
    return SVGGraphics3D.toSVG((IAST) result);
  }

  private static int count(String svg, String tag) {
    return svg.split("<" + tag + "\\b", -1).length - 1;
  }

  /** The distinct fill colours of the polygons, which is what lighting shows up as. */
  private static Set<String> fills(String svg) {
    Set<String> colours = new HashSet<>();
    Matcher m = Pattern.compile("<polygon[^>]*fill=\"(#[0-9a-f]{6})\"").matcher(svg);
    while (m.find()) {
      colours.add(m.group(1));
    }
    return colours;
  }

  @Test
  public void aRadiusListDrawsEachSphereAtItsOwnRadius() {
    assertEquals(
        svg("Graphics3D[{Sphere[{1,0,0},0.25],Sphere[{-1,0,0},0.75]},PlotRange->3]"),
        svg("Graphics3D[{Sphere[{{1,0,0},{-1,0,0}},{0.25,0.75}]},PlotRange->3]"));
  }

  /**
   * A {@code Scaled} radius is resolved against the scene diagonal, so the ball still has a size.
   */
  @Test
  public void aScaledSphereIsDrawn() {
    assertTrue(count(svg("Graphics3D[Sphere[{0,0,0},Scaled[0.2]],PlotRange->2]"), "polygon") > 50,
        "a scaled sphere is tessellated like any other");
  }

  @Test
  public void everySolidBecomesPolygons() {
    assertTrue(count(svg("Graphics3D[Cuboid[]]"), "polygon") >= 6, "a box has six faces");
    assertTrue(count(svg("Graphics3D[Sphere[]]"), "polygon") > 50, "a sphere is tessellated");
    assertTrue(count(svg("Graphics3D[Cylinder[]]"), "polygon") > 20);
    assertTrue(count(svg("Graphics3D[Cone[]]"), "polygon") > 20);
    assertTrue(count(svg("Graphics3D[Tetrahedron[]]"), "polygon") >= 4);
    assertTrue(count(svg("Graphics3D[Octahedron[]]"), "polygon") >= 8);
    assertTrue(count(svg("Graphics3D[Icosahedron[]]"), "polygon") >= 20);
    assertTrue(count(svg("Graphics3D[Dodecahedron[]]"), "polygon") >= 12,
        "a dodecahedron has twelve pentagons");
    assertTrue(count(svg("Graphics3D[Tube[{{0,0,0},{1,1,1},{2,0,1}}]]"), "polygon") > 100,
        "a tube is swept along a smoothed path, not the bare polyline");
  }

  /**
   * An unstyled solid is white and the coloured lights give it its shape.
   *
   * <p>
   * The three visible faces of a plain box come out in three different colours. If they were all
   * the same, the lighting would not be reaching this renderer at all - which is exactly what
   * happened while it kept its own copy of the light table.
   */
  @Test
  public void aPlainBoxIsShapedByTheColouredLights() {
    Set<String> colours = fills(svg("Graphics3D[Cuboid[]]"));
    assertTrue(colours.size() >= 3,
        "expected the faces of a box to be lit differently, got " + colours);
  }

  @Test
  public void surfacePlotsAndCurvesAreDrawn() {
    assertTrue(count(svg("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->6]"), "polygon") > 20);
    assertTrue(count(svg("ContourPlot3D[x^2+y^2+z^2==1,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->10]"),
        "polygon") > 20);
    assertTrue(
        count(svg("ParametricPlot3D[{Cos[t],Sin[t],t},{t,0,6},PlotPoints->20]"), "polyline") > 0);
    assertTrue(count(svg("ListPointPlot3D[{{1,1,1},{2,2,2},{3,1,2}}]"), "circle") >= 3,
        "each point becomes a dot");
  }

  /** Text, and the tick labels the scene supplies, both reach the picture. */
  @Test
  public void textAndTickLabelsAreWritten() {
    String withText = svg("Graphics3D[Text[\"hello\",{0,0,0}]]");
    assertTrue(withText.contains(">hello<"), "the label is missing: " + withText);

    String withAxes = svg("Graphics3D[Cuboid[],Axes->True]");
    assertTrue(count(withAxes, "text") >= 3, "an axis carries its tick labels");

    String labelled = svg("Graphics3D[Cuboid[],Axes->True,AxesLabel->{\"xx\",\"yy\",\"zz\"}]");
    assertTrue(labelled.contains(">xx<") && labelled.contains(">zz<"), "axis labels are missing");

    assertTrue(svg("Graphics3D[Sphere[],PlotLabel->\"title\"]").contains(">title<"));
  }

  /** An unstyled line is black, as it is in the interactive output. */
  @Test
  public void anUnstyledLineIsBlack() {
    String line = svg("Graphics3D[Line[{{0,0,0},{1,1,1}}]]");
    assertTrue(line.contains("stroke=\"#000000\""), "expected a black line: " + line);
    assertTrue(svg("Graphics3D[{Red,Line[{{0,0,0},{1,1,1}}]}]").contains("stroke=\"#ff0000\""));
  }

  /** Options that change the frame reach this renderer too. */
  @Test
  public void frameOptionsAreHonoured() {
    String sized = svg("Graphics3D[Sphere[],ImageSize->{640,480}]");
    assertTrue(sized.contains("width=\"640.0\"") && sized.contains("height=\"480.0\""),
        sized.substring(0, Math.min(200, sized.length())));

    String boxed = svg("Graphics3D[Sphere[]]");
    String unboxed = svg("Graphics3D[Sphere[],Boxed->False]");
    assertTrue(count(boxed, "polyline") > count(unboxed, "polyline"),
        "Boxed -> False removes the bounding box");

    assertTrue(svg("Graphics3D[Sphere[],Background->LightBlue]").contains("<rect"),
        "a background is painted behind the scene");
  }

  /** A transformation moves the geometry rather than being ignored. */
  @Test
  public void transformationsAreApplied() {
    String plain = svg("Graphics3D[Cuboid[]]");
    String moved = svg("Graphics3D[{Cuboid[],Translate[Cuboid[],{3,0,0}]}]");
    assertTrue(count(moved, "polygon") > count(plain, "polygon"),
        "the translated copy is drawn as well");
  }

  @Test
  public void anEmptyGraphicStillProducesAnSvg() {
    String svg = svg("Graphics3D[{}]");
    assertTrue(svg.startsWith("<svg"), svg);
    assertTrue(svg.contains("</svg>"));
  }

  /** Whatever the input, the output has to be well formed enough to embed. */
  @Test
  public void theOutputIsAWellFormedSvgElement() {
    for (String input : new String[] {"Graphics3D[Sphere[]]",
        "Plot3D[Sin[x y],{x,-1,1},{y,-1,1},PlotPoints->6]",
        "DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->Full]",
        "Graphics3D[{Text[\"a<b\",{0,0,0}]}]"}) {
      String svg = svg(input);
      assertTrue(svg.startsWith("<svg "), input + " -> " + svg.substring(0, 40));
      assertEquals(1, count(svg, "svg"), "exactly one svg element for " + input);
      assertTrue(!svg.contains("a<b"), "text has to be escaped: " + input);
    }
  }

  // ---------------------------------------------------------------- outline

  /**
   * Outlines a face whether or not an {@code EdgeForm} asked for one, and the outline follows the
   * boundary of the face rather than the triangles it was cut into.
   */
  @Test
  public void everyFaceCarriesAnOutline() {
    String quad = svg("Graphics3D[{Opacity[0.3], Blue,"
        + " Polygon[{{0,0,0},{1,0,0},{1,1,0},{0,1,0}}]}, Boxed->False]");
    assertEquals(4, count(quad, "polyline"),
        "a quad is outlined along its four sides, not across the diagonal it was cut along");
    assertEquals(12, count(svg("Graphics3D[Cuboid[], Boxed->False]"), "polyline"),
        "a box shows its twelve edges");
    assertEquals(6, count(svg("Graphics3D[Tetrahedron[], Boxed->False]"), "polyline"));
    assertEquals(0, count(svg("Graphics3D[{EdgeForm[], Cuboid[]}, Boxed->False]"), "polyline"),
        "EdgeForm[] means no edge, the same as in 2D");
    assertEquals(0, count(svg("Graphics3D[{EdgeForm[None], Cuboid[]}, Boxed->False]"), "polyline"));
  }

  /**
   * The outline is of the shape, not of the tessellation: a sphere is smooth all over and shows
   * none, while a cylinder shows the two circles where its caps meet the barrel but no seam along
   * it.
   */
  @Test
  public void theOutlineFollowsTheShapeAndNotTheTessellation() {
    assertEquals(0, count(svg("Graphics3D[Sphere[], Boxed->False]"), "polyline"),
        "a sphere has no crease to outline");
    int cylinder = count(svg("Graphics3D[Cylinder[], Boxed->False]"), "polyline");
    int cone = count(svg("Graphics3D[Cone[], Boxed->False]"), "polyline");
    assertTrue(cylinder > 0 && cylinder < count(svg("Graphics3D[Cylinder[]]"), "polygon"),
        "the two cap circles, and none of the facets of the barrel: " + cylinder);
    assertEquals(cylinder / 2, cone, "a cone has one cap where a cylinder has two");
  }

  /**
   * {@code Opacity} tints the face and leaves the outline opaque, which is what makes
   * {@code {Opacity[0], Cuboid[]}} the wireframe idiom.
   */
  @Test
  public void opacityTintsTheFaceAndLeavesTheOutlineAlone() {
    String wireframe = svg("Graphics3D[{Opacity[0], Cuboid[]}, Boxed->False]");
    assertEquals(12, count(wireframe, "polyline"), "the box is still there as a wireframe");
    assertTrue(wireframe.contains("fill-opacity=\"0.000\""), "the faces are invisible");
    assertTrue(wireframe.contains("stroke-opacity=\"1.000\""), "the outline is not");

    // an Opacity inside the EdgeForm is the way to fade the outline itself
    assertTrue(svg("Graphics3D[{EdgeForm[{Opacity[0.5], Black}], Cuboid[]}, Boxed->False]")
        .contains("stroke-opacity=\"0.500\""));
  }

  /**
   * A plotted surface asks for a clean skin, and its mesh is drawn as lines of its own.
   *
   * <p>
   * The only line round it is the rim Mathematica draws by default: on a 6 by 6 grid that is four
   * sides of five segments, where an outline round every facet would be well over a hundred.
   */
  @Test
  public void aPlottedSurfaceIsNotOutlined() {
    String meshed =
        svg("Plot3D[Sin[x y], {x,-1,1}, {y,-1,1}, PlotPoints->6, Boxed->False, Axes->False]");
    String plain = svg("Plot3D[Sin[x y], {x,-1,1}, {y,-1,1}, PlotPoints->6, Mesh->None,"
        + " Boxed->False, Axes->False]");
    String bare = svg("Plot3D[Sin[x y], {x,-1,1}, {y,-1,1}, PlotPoints->6, Mesh->None,"
        + " BoundaryStyle->None, Boxed->False, Axes->False]");
    assertEquals(20, count(plain, "polyline"), "only the rim, not an outline round every facet");
    assertEquals(0, count(bare, "polyline"), "and without the rim, no line at all");
    assertTrue(count(meshed, "polyline") > 20, "the mesh itself is still drawn");
  }
  /**
   * How many segments of the given stroke colour are painted before a face they lie on. A line on
   * a surface has to come after the faces under it, or the painter draws those faces over it and
   * the line comes out dashed. On a plane nothing hides anything else, so every face that contains
   * the middle of a segment on screen is one the segment lies on.
   */
  private static int buriedSegments(String svg, String stroke) {
    java.util.List<java.awt.geom.Path2D> faces = new java.util.ArrayList<>();
    java.util.List<Integer> faceAt = new java.util.ArrayList<>();
    Matcher polygon = Pattern.compile("<polygon points=\"([^\"]*)\"").matcher(svg);
    while (polygon.find()) {
      String[] corners = polygon.group(1).trim().split("\\s+");
      java.awt.geom.Path2D.Double path = new java.awt.geom.Path2D.Double();
      for (int k = 0; k < corners.length; k++) {
        String[] xy = corners[k].split(",");
        double x = Double.parseDouble(xy[0]);
        double y = Double.parseDouble(xy[1]);
        if (k == 0) {
          path.moveTo(x, y);
        } else {
          path.lineTo(x, y);
        }
      }
      path.closePath();
      faces.add(path);
      faceAt.add(polygon.start());
    }
    int buried = 0;
    int segments = 0;
    Matcher line = Pattern
        .compile("<polyline points=\"([^\"]*)\"[^>]*stroke=\"" + stroke + "\"").matcher(svg);
    while (line.find()) {
      String[] points = line.group(1).trim().split("\\s+");
      for (int k = 0; k + 1 < points.length; k++) {
        String[] a = points[k].split(",");
        String[] b = points[k + 1].split(",");
        double mx = (Double.parseDouble(a[0]) + Double.parseDouble(b[0])) / 2;
        double my = (Double.parseDouble(a[1]) + Double.parseDouble(b[1])) / 2;
        segments++;
        for (int f = 0; f < faces.size(); f++) {
          if (faceAt.get(f) > line.start() && faces.get(f).contains(mx, my)) {
            buried++;
            break;
          }
        }
      }
    }
    assertTrue(segments > 0, "no segment drawn in " + stroke);
    return buried;
  }

  /**
   * Lines that lie on a surface - mesh function levels, the sampling grid's mesh - are painted
   * after the faces they lie on. Sorted by their own middle like any other line, they came out
   * behind their faces at random and were drawn dashed.
   */
  @Test
  public void meshLinesAreNotBuriedInTheirSurface() {
    String levels = svg("Plot3D[0.3*x + 0.2*y, {x,-1,1}, {y,-1,1}, MeshFunctions -> {#1&}, "
        + "Mesh -> {{-0.5, 0, 0.5}}, MeshStyle -> Yellow]");
    assertEquals(0, buriedSegments(levels, "#ffff00"));
    String grid = svg("Plot3D[0.3*x + 0.2*y, {x,-1,1}, {y,-1,1}]");
    assertEquals(0, buriedSegments(grid, "#333333"));
    String parametric = svg("ParametricPlot3D[{u, v, 0.3*u + 0.2*v}, {u,-1,1}, {v,-1,1}, "
        + "MeshFunctions -> {#4&}, Mesh -> {{0}}, MeshStyle -> Yellow]");
    assertEquals(0, buriedSegments(parametric, "#ffff00"));
  }

  /**
   * A tube is one smooth surface: the seams between its facets are no edges, so nothing is outlined
   * on it. Its ten sides meet at more than the outline angle, and every seam used to be drawn as a
   * dark stripe along it.
   */
  @Test
  public void aTubeDrawsNoOutline() {
    for (String tube : new String[] {
        "Graphics3D[Tube[{{0,0,0},{1,0,0},{1,1,0},{0,1,0}}, 0.1], Boxed->False, Axes->False]",
        "Graphics3D[Tube[{{0,0,0},{1,0,0},{1,1,0},{0,0,0}}, 0.1], Boxed->False, Axes->False]"}) {
      String svg = svg(tube);
      assertTrue(count(svg, "polygon") > 0, tube + " draws the tube");
      assertEquals(0, count(svg, "polyline"), tube + " draws no outline");
    }
  }
}
