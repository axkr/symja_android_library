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

  /** A plotted surface asks for a clean skin, and its mesh is drawn as lines of its own. */
  @Test
  public void aPlottedSurfaceIsNotOutlined() {
    String meshed =
        svg("Plot3D[Sin[x y], {x,-1,1}, {y,-1,1}, PlotPoints->6, Boxed->False, Axes->False]");
    String plain = svg("Plot3D[Sin[x y], {x,-1,1}, {y,-1,1}, PlotPoints->6, Mesh->None,"
        + " Boxed->False, Axes->False]");
    assertEquals(0, count(plain, "polyline"), "no outline around every facet of a surface");
    assertTrue(count(meshed, "polyline") > 0, "the mesh itself is still drawn");
  }
}
