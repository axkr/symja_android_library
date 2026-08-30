package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Asserts what the {@code Graphics3D} to WebGL converter puts in the scene it hands the renderer.
 *
 * <p>
 * These are the checks that would otherwise need a browser: that a primitive turns into the element
 * the renderer knows how to build, that a directive survives the walk. They run headlessly, so a
 * regression in the converter is caught without anybody having to look at a picture.
 */
public class WebGLGraphics3DTest {

  private static ExprEvaluator evaluator;
  private static final ObjectMapper MAPPER = new ObjectMapper();

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

  private static JsonNode scene(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result.isAST(), input + " did not evaluate to a graphic: " + result);
    try {
      return MAPPER.readTree(WebGLGraphics3D.generateJSON((IAST) result));
    } catch (Exception e) {
      throw new IllegalStateException("could not parse the scene of " + input, e);
    }
  }

  /** The first element of the given type, or {@code null}. */
  private static JsonNode element(JsonNode scene, String type) {
    for (JsonNode element : scene.get("elements")) {
      if (element.get("type").asText().equals(type)) {
        return element;
      }
    }
    return null;
  }

  private static int count(JsonNode scene, String type) {
    int n = 0;
    for (JsonNode element : scene.get("elements")) {
      if (element.get("type").asText().equals(type)) {
        n++;
      }
    }
    return n;
  }

  // ------------------------------------------------------------------ options

  @Test
  public void defaultsFollowTheWMA() {
    JsonNode scene = scene("Graphics3D[Sphere[]]");
    assertTrue(scene.get("boxed").asBoolean(), "Boxed defaults to True");
    assertTrue(!scene.get("axes").get(0).asBoolean(), "Axes defaults to False for Graphics3D");
    assertEquals(1.3, scene.get("viewPoint").get(0).asDouble(), 1e-9);
    assertEquals(-2.4, scene.get("viewPoint").get(1).asDouble(), 1e-9);
    assertEquals(2.0, scene.get("viewPoint").get(2).asDouble(), 1e-9);
    assertEquals(0.0, scene.get("viewVertical").get(0).asDouble(), 1e-9);
    assertEquals(1.0, scene.get("viewVertical").get(2).asDouble(), 1e-9);
    assertEquals("Perspective", scene.get("viewProjection").asText());
  }

  @Test
  public void plot3DTurnsOnItsAxes() {
    JsonNode scene = scene("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->4]");
    assertTrue(scene.get("axes").get(0).asBoolean(), "a plot draws axes");
    assertEquals(0.4, scene.get("boxRatios").get(2).asDouble(), 1e-9,
        "Plot3D uses WMA's flattened box");
  }

  @Test
  public void viewOptionsReachTheScene() {
    JsonNode scene =
        scene("Graphics3D[Sphere[],ViewPoint->{0,0,3},ViewProjection->\"Orthographic\","
            + "ViewVertical->{0,1,0},Boxed->False,ImageSize->200,Background->White]");
    assertEquals(3.0, scene.get("viewPoint").get(2).asDouble(), 1e-9);
    assertEquals("Orthographic", scene.get("viewProjection").asText());
    assertEquals(1.0, scene.get("viewVertical").get(1).asDouble(), 1e-9);
    assertTrue(!scene.get("boxed").asBoolean());
    assertEquals(200.0, scene.get("imageSize").get(0).asDouble(), 1e-9);
    assertEquals(0xFFFFFF, scene.get("background").asInt());
  }

  @Test
  public void namedViewPointsResolve() {
    assertEquals(2.0,
        scene("Graphics3D[Sphere[],ViewPoint->\"Above\"]").get("viewPoint").get(2).asDouble(),
        1e-9);
    assertEquals(-2.0,
        scene("Graphics3D[Sphere[],ViewPoint->\"Front\"]").get("viewPoint").get(1).asDouble(),
        1e-9);
  }

  /** Ticks are placed by the converter, so both output paths label an axis the same way. */
  @Test
  public void ticksArePlacedAtRoundNumbers() {
    JsonNode ticks = scene("Graphics3D[Cuboid[{0,0,0},{1,1,1}],Axes->True]").get("ticks").get(0);
    assertTrue(ticks.size() >= 3, "expected several ticks, got " + ticks);
    assertEquals("0", ticks.get(0).get("label").asText());
    assertEquals(0.0, ticks.get(0).get("position").asDouble(), 1e-9);
  }

  @Test
  public void ticksNoneSuppressesThem() {
    JsonNode ticks = scene("Graphics3D[Sphere[],Axes->True,Ticks->None]").get("ticks");
    assertEquals(0, ticks.get(0).size());
  }

  /** A logarithmic axis carries mapped coordinates but must read in the original units. */
  @Test
  public void logScalingLabelsThePowers() {
    JsonNode scene = scene("Plot3D[Exp[x+y],{x,0,2},{y,0,2},PlotPoints->6,"
        + "ScalingFunctions->{\"Identity\",\"Identity\",\"Log\"}]");
    assertEquals("Log", scene.get("scaling").get(2).asText());
    JsonNode zTicks = scene.get("ticks").get(2);
    assertTrue(zTicks.size() > 0, "a log axis still gets ticks");
    for (JsonNode tick : zTicks) {
      assertTrue(!tick.get("label").asText().isEmpty());
    }
  }

  // --------------------------------------------------------------- primitives

  @Test
  public void primitivesBecomeTheirOwnElements() {
    assertNotNull(element(scene("Graphics3D[Sphere[]]"), "Sphere"));
    assertNotNull(element(scene("Graphics3D[Cylinder[]]"), "Cylinder"));
    assertNotNull(element(scene("Graphics3D[Cone[]]"), "Cone"));
    assertNotNull(element(scene("Graphics3D[Cuboid[]]"), "Cuboid"));
    assertNotNull(element(scene("Graphics3D[Tetrahedron[]]"), "Polyhedron"));
    assertNotNull(element(scene("Graphics3D[Line[{{0,0,0},{1,1,1}}]]"), "Line"));
    assertNotNull(element(scene("Graphics3D[Point[{0,0,0}]]"), "Point"));
    assertNotNull(element(scene("Graphics3D[Polygon[{{0,0,0},{1,0,0},{1,1,1}}]]"), "Polygon"));
    assertNotNull(element(scene("Graphics3D[Text[\"a\",{0,0,0}]]"), "Text"));
    assertNotNull(element(scene("Graphics3D[Arrow[{{0,0,0},{1,1,1}}]]"), "Arrow"));
    assertNotNull(element(scene("Graphics3D[Tube[{{0,0,0},{1,1,1}}]]"), "Tube"));
    assertNotNull(
        element(scene("Graphics3D[BSplineCurve[{{0,0,0},{1,1,0},{2,0,1}}]]"), "BSplineCurve"));
  }

  /**
   * A multi segment {@code Line} must stay several polylines.
   *
   * <p>
   * Flattening them into one buffer draws a stray segment from the end of each line to the start of
   * the next, which is what the old converter did.
   */
  @Test
  public void separateLinesStaySeparate() {
    JsonNode line =
        element(scene("Graphics3D[Line[{{{0,0,0},{1,0,0}},{{0,1,0},{1,1,0}}}]]"), "Line");
    assertEquals(2, line.get("polylines").size());
    assertEquals(6, line.get("polylines").get(0).size());
  }

  @Test
  public void sphereAcceptsSeveralCentres() {
    JsonNode sphere = element(scene("Graphics3D[Sphere[{{0,0,0},{2,0,0},{4,0,0}},0.5]]"), "Sphere");
    assertEquals(9, sphere.get("centers").size(), "three centres, three coordinates each");
    assertEquals(0.5, sphere.get("radius").asDouble(), 1e-9);
  }

  // --------------------------------------------------------------- directives

  @Test
  public void directivesReachTheElement() {
    assertEquals(0xFF0000,
        element(scene("Graphics3D[{Red,Sphere[]}]"), "Sphere").get("color").asInt());
    assertEquals(0.3,
        element(scene("Graphics3D[{Opacity[0.3],Sphere[]}]"), "Sphere").get("opacity").asDouble(),
        1e-9);
    assertEquals(2.0, element(scene("Graphics3D[{Thick,Line[{{0,0,0},{1,1,1}}]}]"), "Line")
        .get("thickness").asDouble(), 1e-9, "Thick is two printer's points");
    assertTrue(
        element(scene("Graphics3D[{Dashed,Line[{{0,0,0},{1,1,1}}]}]"), "Line").has("dashing"));
    assertTrue(
        element(scene("Graphics3D[{Specularity[0.7],Sphere[]}]"), "Sphere").has("specularity"));
    assertTrue(element(scene("Graphics3D[{Glow[Red],Sphere[]}]"), "Sphere").has("glow"));
    assertTrue(element(scene("Graphics3D[{EdgeForm[Black],Cuboid[]}]"), "Cuboid").get("showMesh")
        .asBoolean());
  }

  /** A directive inside a sublist must not leak out of it. */
  @Test
  public void directivesAreScopedByTheirList() {
    JsonNode scene = scene("Graphics3D[{{Red,Sphere[{0,0,0}]},Sphere[{2,0,0}]}]");
    assertEquals(0xFF0000, scene.get("elements").get(0).get("color").asInt());
    assertTrue(scene.get("elements").get(1).get("color").asInt() != 0xFF0000,
        "the colour set inside the sublist leaked out of it");
  }

  @Test
  public void styleWrapperAppliesToItsContent() {
    assertEquals(0x00FF00,
        element(scene("Graphics3D[Style[Sphere[],Green]]"), "Sphere").get("color").asInt());
  }

  @Test
  public void textDefaultsToBlackButFollowsAColourDirective() {
    assertEquals(0x000000,
        element(scene("Graphics3D[Text[\"a\",{0,0,0}]]"), "Text").get("color").asInt());
    assertEquals(0xFF0000,
        element(scene("Graphics3D[{Red,Text[\"a\",{0,0,0}]}]"), "Text").get("color").asInt());
  }

  // ---------------------------------------------------------- transformations

  @Test
  public void transformationsBecomeAMatrix() {
    JsonNode rotated = element(scene("Graphics3D[Rotate[Cuboid[],Pi/2,{0,0,1}]]"), "Cuboid");
    assertTrue(rotated.has("matrix"));
    assertEquals(16, rotated.get("matrix").size());
    JsonNode translated = element(scene("Graphics3D[Translate[Cuboid[],{2,0,0}]]"), "Cuboid");
    // column major, so the translation sits in the last column
    assertEquals(2.0, translated.get("matrix").get(12).asDouble(), 1e-9);
    assertTrue(!element(scene("Graphics3D[Cuboid[]]"), "Cuboid").has("matrix"),
        "an untransformed primitive carries no matrix");
  }

  @Test
  public void aTransformationWidensTheRange() {
    JsonNode scene = scene("Graphics3D[Translate[Cuboid[],{2,0,0}]]");
    assertEquals(3.0, scene.get("plotRange").get(0).get(1).asDouble(), 1e-9,
        "the moved box has to be inside the plot range");
  }

  // ------------------------------------------------------------ plot geometry

  /**
   * A surface must carry vertex normals, and its winding must agree with them.
   *
   * <p>
   * When the two disagree the renderer treats every visible fragment as a back face and flips the
   * normal, which leaves the surface unlit; that is invisible to any test that only counts
   * elements, so the orientation is checked here directly.
   */
  @Test
  public void surfaceNormalsAgreeWithTheWinding() {
    for (String plot : new String[] { //
        "Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->4]", //
        "Plot3D[Sin[x] Cos[y],{x,-2,2},{y,-2,2},PlotPoints->8]", //
        "ParametricPlot3D[{Cos[u]Cos[v],Sin[u]Cos[v],Sin[v]},{u,0,6},{v,-1,1},PlotPoints->8]", //
        "SphericalPlot3D[1,{t,0,3},{p,0,6},PlotPoints->8]", //
        "RevolutionPlot3D[Sqrt[t],{t,0,4},PlotPoints->8]", //
        "ComplexPlot3D[z^2,{z,-1-I,1+I},PlotPoints->8]", //
        "ContourPlot3D[x^2+y^2+z^2==1,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->12]", //
        "ListPlot3D[{{1,2,3,4},{4,5,6,7},{7,8,9,10},{2,4,6,8}}]"}) {
      JsonNode polygon = element(scene(plot), "Polygon");
      assertNotNull(polygon, plot + " produced no surface");
      assertTrue(polygon.has("vertexNormals"), plot + " supplies no normals");
      assertEquals(polygon.get("points").size(), polygon.get("vertexNormals").size(), plot);

      JsonNode indices = polygon.get("indices");
      int disagreeing = 0;
      for (int t = 0; t + 2 < indices.size(); t += 3) {
        double[] geometric = cross(subtract(corner(polygon, t + 1), corner(polygon, t)),
            subtract(corner(polygon, t + 2), corner(polygon, t)));
        double[] supplied = normal(polygon, indices.get(t).asInt());
        double alignment =
            geometric[0] * supplied[0] + geometric[1] * supplied[1] + geometric[2] * supplied[2];
        if (alignment < 0) {
          disagreeing++;
        }
      }
      assertEquals(0, disagreeing, plot + " winds " + disagreeing
          + " triangles against their own normals, so they would" + " render unlit");
    }
  }

  private static double[] normal(JsonNode polygon, int vertex) {
    return new double[] {polygon.get("vertexNormals").get(vertex * 3).asDouble(),
        polygon.get("vertexNormals").get(vertex * 3 + 1).asDouble(),
        polygon.get("vertexNormals").get(vertex * 3 + 2).asDouble()};
  }

  /** The coordinate of the corner at the given position in the index list. */
  private static double[] corner(JsonNode polygon, int position) {
    int index = polygon.get("indices").get(position).asInt();
    return new double[] {polygon.get("points").get(index * 3).asDouble(),
        polygon.get("points").get(index * 3 + 1).asDouble(),
        polygon.get("points").get(index * 3 + 2).asDouble()};
  }

  private static double[] subtract(double[] a, double[] b) {
    return new double[] {a[0] - b[0], a[1] - b[1], a[2] - b[2]};
  }

  private static double[] cross(double[] a, double[] b) {
    return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]};
  }

  /** Mesh lines are their own lines along the grid, not an outline on every quad. */
  @Test
  public void meshIsDrawnAsGridLines() {
    JsonNode withMesh = scene("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->10]");
    assertTrue(count(withMesh, "Line") > 0, "the default mesh draws lines");
    assertTrue(!element(withMesh, "Polygon").get("showMesh").asBoolean(),
        "the surface itself carries no per quad edges");

    JsonNode withoutMesh = scene("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->10,Mesh->None]");
    assertEquals(0, count(withoutMesh, "Line"), "Mesh -> None draws no lines");
  }

  /**
   * {@code Mesh -> n} spaces the lines by the request, not by how finely the surface is sampled.
   */
  @Test
  public void meshCountIsIndependentOfSampling() {
    int coarse = count(scene("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->20,Mesh->4]"), "Line");
    int fine = count(scene("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->40,Mesh->4]"), "Line");
    assertEquals(coarse, fine, "the same Mesh must give the same number of lines");
    assertTrue(coarse >= 8 && coarse <= 14, "expected about four lines each way, got " + coarse);
  }

  /** Vertices of a {@code GraphicsComplex} keep their own colours. */
  @Test
  public void vertexColoursSurviveTheWalk() {
    JsonNode polygon =
        element(scene("Graphics3D[GraphicsComplex[{{0,0,0},{1,0,0},{0,1,0}},{Polygon[{1,2,3}]},"
            + "VertexColors->{Red,Green,Blue}]]"), "Polygon");
    assertTrue(polygon.has("vertexColors"));
    assertEquals(9, polygon.get("vertexColors").size());
    assertEquals(1.0, polygon.get("vertexColors").get(0).asDouble(), 1e-9,
        "the first vertex is red");
  }

  /**
   * A list of integers inside a {@code GraphicsComplex} indexes the vertex pool.
   *
   * <p>
   * Three integers also look like a coordinate, and reading them that way collapsed every mesh line
   * of every surface plot to a single point, which drew nothing at all.
   */
  @Test
  public void indicesInsideAComplexAreNotCoordinates() {
    JsonNode line = element(
        scene("Graphics3D[GraphicsComplex[{{0,0,0},{1,0,0},{2,1,0}},{Line[{1,2,3}]}]]"), "Line");
    assertNotNull(line, "the indexed line was dropped");
    assertEquals(1, line.get("polylines").size());
    assertEquals(9, line.get("polylines").get(0).size(), "three vertices, three coordinates each");
  }

  /**
   * A closed isosurface must come out watertight.
   *
   * <p>
   * A hole in the surface shows as a white gap through it, and comes from a marching cubes cell
   * that triangulated the wrong edges. Counting edges catches that without anybody having to spot
   * the gap: in a closed surface every edge is shared by exactly two triangles.
   */
  @Test
  public void aClosedIsosurfaceHasNoHoles() {
    JsonNode polygon =
        element(scene("ContourPlot3D[x^2+y^2+z^2==1,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->12]"),
            "Polygon");
    assertNotNull(polygon);
    JsonNode indices = polygon.get("indices");
    java.util.Map<Long, Integer> edges = new java.util.HashMap<>();
    for (int t = 0; t + 2 < indices.size(); t += 3) {
      int[] corner =
          {indices.get(t).asInt(), indices.get(t + 1).asInt(), indices.get(t + 2).asInt()};
      for (int c = 0; c < 3; c++) {
        int a = corner[c];
        int b = corner[(c + 1) % 3];
        edges.merge((long) Math.min(a, b) * 1_000_000L + Math.max(a, b), 1, Integer::sum);
      }
    }
    int unpaired = 0;
    for (int shared : edges.values()) {
      if (shared != 2) {
        unpaired++;
      }
    }
    assertEquals(0, unpaired, "the sphere has " + unpaired + " edges that are not shared by two"
        + " triangles, so it is not closed");
  }

  /**
   * {@code ExtentSize} chooses between the three ways a discrete plot can mark a value.
   *
   * <p>
   * {@code Automatic} is default appearance, a stem with a marker on top. The stem has to be a
   * line: drawn as a very thin box instead, a grid of them reads as a barcode.
   */
  @Test
  public void extentSizeChoosesHowValuesAreMarked() {
    JsonNode stems = scene("DiscretePlot3D[i+j,{i,1,4},{j,1,4}]");
    assertTrue(count(stems, "Line") > 0, "the default draws stems");
    assertTrue(count(stems, "Point") > 0, "the default marks each value");
    assertEquals(0, count(stems, "Cuboid"), "the default draws no bars");

    JsonNode bars = scene("DiscretePlot3D[i+j,{i,1,4},{j,1,4},ExtentSize->Full]");
    assertTrue(count(bars, "Cuboid") > 0, "ExtentSize -> Full draws bars");
    assertEquals(0, count(bars, "Line"));

    JsonNode points = scene("DiscretePlot3D[i+j,{i,1,4},{j,1,4},ExtentSize->None]");
    assertTrue(count(points, "Point") > 0, "ExtentSize -> None marks the values");
    assertEquals(0, count(points, "Line"), "ExtentSize -> None draws no stems");
    assertEquals(0, count(points, "Cuboid"));
  }

  /**
   * A discrete plot has to show its own tallest value.
   *
   * <p>
   * The outlier clamp that keeps a sampled function's poles from flattening the rest of a surface
   * is wrong here, because every value of a discrete plot is a real measurement. Applied to a
   * probability mass function it cut the range off below the peak, and the bars that reached above
   * it stood out through the top of the box.
   */
  @Test
  public void aDiscretePlotShowsItsWholeRange() {
    String call = "DiscretePlot3D[PDF[MultivariatePoissonDistribution[3,{1,1}],{t,u}],"
        + "{t,0,8},{u,0,8},ExtentSize->Full]";
    JsonNode scene = scene(call);
    double top = scene.get("plotRange").get(2).get(1).asDouble();
    double tallest = 0;
    for (JsonNode element : scene.get("elements")) {
      if (element.get("type").asText().equals("Cuboid")) {
        tallest = Math.max(tallest, element.get("max").get(2).asDouble());
      }
    }
    assertTrue(tallest > 0, "no bars were drawn");
    assertTrue(tallest <= top + 1e-9,
        "the tallest bar reaches " + tallest + " but the box only goes to " + top);
    assertTrue(top >= 0.06, "the range stops at " + top + ", below the peak of this distribution");
  }

  /** The 8 bit colour a {@code RGBColor} triple resolves to. */
  private static int rgb(double r, double g, double b) {
    return new java.awt.Color((float) r, (float) g, (float) b).getRGB() & 0x00FFFFFF;
  }

  /**
   * A surface does not start from the colour a curve does: a single {@code Plot3D} is gold, and
   * only the second surface in the same picture is the blue that a curve would have started at.
   */
  @Test
  public void surfaceColoursMatchTheWMADefaults() {
    JsonNode one = scene("Plot3D[Sin[x + y^2],{x,-3,3},{y,-2,2},PlotPoints->6]");
    assertEquals(rgb(0.880722, 0.611041, 0.142051), element(one, "Polygon").get("color").asInt(),
        "a single surface is WMA's gold");

    JsonNode many = scene("Plot3D[{x+y,x-y,x y},{x,-1,1},{y,-1,1},PlotPoints->4]");
    List<Integer> colours = new ArrayList<>();
    for (JsonNode element : many.get("elements")) {
      if (element.get("type").asText().equals("Polygon")) {
        colours.add(element.get("color").asInt());
      }
    }
    assertEquals(3, colours.size(), "one surface per function");
    assertEquals(rgb(0.880722, 0.611041, 0.142051), (int) colours.get(0));
    assertEquals(rgb(0.368417, 0.506779, 0.709798), (int) colours.get(1));
    assertEquals(rgb(0.560181, 0.691569, 0.194885), (int) colours.get(2));
    assertEquals(3, new java.util.HashSet<>(colours).size(),
        "surfaces drawn together must not share a colour");
  }

  /** A surface carries the white highlight. */
  @Test
  public void surfacesCarryTheWMAHighlight() {
    JsonNode polygon = element(scene("Plot3D[x+y,{x,0,1},{y,0,1},PlotPoints->4]"), "Polygon");
    assertTrue(polygon.has("specularity"), "a plotted surface has a specular highlight");
    assertEquals(1.0, polygon.get("specularity").asDouble(), 1e-9, "the highlight is white");
    assertEquals(3.0, polygon.get("specularExponent").asDouble(), 1e-9);
  }

  /**
   * A chart element is a darkened palette entry, and the body of a bar a lightened translucent one,
   * which is how we keeps overlapping bars readable.
   */
  @Test
  public void chartColoursMatchTheWMADefaults() {
    JsonNode bars = scene("DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->Full]");
    JsonNode bar = element(bars, "Cuboid");
    assertEquals(rgb(0.4512, 0.678, 0.8039999999999999), bar.get("color").asInt(),
        "the bar face is the lightened palette entry");
    assertEquals(0.5, bar.get("opacity").asDouble(), 1e-9, "and it is half transparent");

    JsonNode stems = scene("DiscretePlot3D[i+j,{i,1,3},{j,1,3}]");
    assertEquals(rgb(0.216, 0.54, 0.72), element(stems, "Line").get("color").asInt(),
        "a stem is the darkened palette entry");
  }

  /** A curve or a scatter of points follows the ordinary plot cycle, which starts at blue. */
  @Test
  public void curvesAndPointsUseThePlotCycle() {
    assertEquals(rgb(0.24, 0.6, 0.8),
        element(scene("ListPointPlot3D[{{1,1,1},{2,2,2}}]"), "Point").get("color").asInt());
    assertEquals(rgb(0.24, 0.6, 0.8),
        element(scene("ParametricPlot3D[{Cos[t],Sin[t],t},{t,0,6},PlotPoints->8]"), "Line")
            .get("color").asInt());
  }

  /**
   * An unstyled solid is white and gets its shape from coloured lights.
   *
   * <p>
   * The three visible faces of a plain {@code Cuboid[]} are warm on top, magenta towards the viewer
   * and blue to the right. None of that is in the box: it is the lighting, and giving the box a
   * colour of its own would tint all three faces alike and flatten it.
   */
  @Test
  public void anUnstyledSolidIsWhiteAndLitByColouredLights() {
    JsonNode scene = scene("Graphics3D[Cuboid[]]");
    assertEquals(0xFFFFFF, element(scene, "Cuboid").get("color").asInt(),
        "an unstyled solid carries no colour of its own");

    JsonNode lights = scene.get("lights");
    assertEquals(5, lights.size(), "WMA's Automatic lighting is an ambient and four lights");
    assertEquals("AmbientLight", lights.get(0).get("type").asText());
    assertEquals(rgb(0.4, 0.2, 0.2), lights.get(0).get("color").asInt(), "a warm ambient");
    assertEquals(rgb(0.0, 0.18, 0.5), lights.get(1).get("color").asInt(), "blue from the right");
    assertEquals(rgb(0.18, 0.5, 0.18), lights.get(2).get("color").asInt(), "green from above");
    assertEquals(rgb(0.5, 0.18, 0.0), lights.get(3).get("color").asInt(), "red from the left");
    for (int i = 1; i < lights.size(); i++) {
      assertTrue(lights.get(i).get("fixedToCamera").asBoolean(),
          "the lights travel with the camera, so shading holds while the scene is turned");
    }
    // ImageScaled coordinates run across the box, so the direction is measured from its centre
    assertEquals(1.5, lights.get(1).get("position").get(0).asDouble(), 1e-9);
    assertEquals(-0.5, lights.get(1).get("position").get(1).asDouble(), 1e-9);
  }

  /** An unstyled line is not lit, so white would make it invisible; it is black. */
  @Test
  public void anUnstyledLineIsBlack() {
    assertEquals(0x000000,
        element(scene("Graphics3D[Line[{{0,0,0},{1,1,1}}]]"), "Line").get("color").asInt());
    assertEquals(0x000000,
        element(scene("Graphics3D[Point[{0,0,0}]]"), "Point").get("color").asInt());
    assertEquals(0xFF0000,
        element(scene("Graphics3D[{Red,Line[{{0,0,0},{1,1,1}}]}]"), "Line").get("color").asInt(),
        "a colour directive still wins");
  }

  /** {@code Lighting -> "Neutral"} is white light, which a plotted surface asks for. */
  @Test
  public void neutralLightingIsWhite() {
    JsonNode lights = scene("Graphics3D[Sphere[],Lighting->\"Neutral\"]").get("lights");
    assertEquals(4, lights.size());
    assertEquals(rgb(0.35, 0.35, 0.35), lights.get(0).get("color").asInt());
    for (int i = 1; i < lights.size(); i++) {
      assertEquals(rgb(0.37, 0.37, 0.37), lights.get(i).get("color").asInt());
    }
    assertEquals(0, scene("Graphics3D[Sphere[],Lighting->None]").get("lights").size(),
        "Lighting -> None installs no lights at all");
  }

  @Test
  public void legendedIsUnwrappedAndItsTextKept() {
    JsonNode scene = scene("Legended[Graphics3D[Sphere[]],\"a sphere\"]");
    assertTrue(scene.get("showLegend").asBoolean());
    assertEquals("a sphere", scene.get("legendText").asText());
    assertNotNull(element(scene, "Sphere"));
  }

  /** The snippet has to carry the size the graphic asked for. */
  @Test
  public void theSnippetUsesTheRequestedImageSize() {
    IExpr result = evaluator.eval("Graphics3D[Sphere[],ImageSize->{640,480}]");
    String html = WebGLGraphics3D.generateHTMLSnippet((IAST) result);
    assertTrue(html.contains("width: 640px"), html.substring(0, Math.min(200, html.length())));
    assertTrue(html.contains("height: 480px"));
    assertTrue(html.contains("data-type=\"webgl\""),
        "the front end finds a 3D graphic by this marker");
  }

  /** The renderer the pages inline has to be readable from the classpath. */
  @Test
  public void theRendererScriptIsOnTheClasspath() {
    String script = WebGLGraphics3D.rendererScript();
    assertTrue(script.contains("renderSymjaWebGL"),
        "symja_webgl.js was not found next to the converter");
    assertTrue(script.length() > 10000, "the renderer looks truncated: " + script.length());
  }

  // --------------------------------------------------------------- outline

  /**
   * A face is outlined whether or not an {@code EdgeForm} asked for it, and the crease angle says
   * how much of the shape the outline follows: the whole mesh when the user named an
   * {@code EdgeForm}, only the creases of the shape when nobody did.
   */
  @Test
  public void everyFaceAsksForAnOutline() {
    JsonNode plain = element(scene("Graphics3D[Cuboid[]]"), "Cuboid");
    assertTrue(plain.get("showMesh").asBoolean(), "a bare Cuboid is outlined");
    assertEquals(30.0, plain.get("edgeAngle").asDouble(), 1e-9, "creases only");

    JsonNode asked = element(scene("Graphics3D[{EdgeForm[Black],Cuboid[]}]"), "Cuboid");
    assertEquals(1.0, asked.get("edgeAngle").asDouble(), 1e-9, "an EdgeForm means the mesh");
    assertEquals(0, asked.get("edgeColor").asInt());

    for (String off : new String[] {"Graphics3D[{EdgeForm[],Cuboid[]}]",
        "Graphics3D[{EdgeForm[None],Cuboid[]}]"}) {
      assertFalse(element(scene(off), "Cuboid").get("showMesh").asBoolean(),
          off + " draws no edge");
    }
  }

  /** {@code Opacity} tints the face; the outline keeps its own transparency. */
  @Test
  public void opacityDoesNotReachTheOutline() {
    JsonNode faded = element(scene("Graphics3D[{Opacity[0.3],EdgeForm[Black],Cuboid[]}]"), "Cuboid");
    assertEquals(0.3, faded.get("opacity").asDouble(), 1e-9);
    assertEquals(1.0, faded.get("edgeOpacity").asDouble(), 1e-9, "the outline stays opaque");

    JsonNode wireframe = element(scene("Graphics3D[{Opacity[0],Cuboid[]}]"), "Cuboid");
    assertEquals(0.0, wireframe.get("opacity").asDouble(), 1e-9);
    assertEquals(1.0, wireframe.get("edgeOpacity").asDouble(), 1e-9);

    // an Opacity inside the EdgeForm, in either of the two forms it can take, does fade it
    for (String input : new String[] {"Graphics3D[{EdgeForm[Opacity[0.5,Black]],Cuboid[]}]",
        "Graphics3D[{EdgeForm[{Opacity[0.5],Black}],Cuboid[]}]"}) {
      assertEquals(0.5, element(scene(input), "Cuboid").get("edgeOpacity").asDouble(), 0.01, input);
    }
  }

  /** A plotted surface carries an explicit {@code EdgeForm[None]}, so the default cannot reach it. */
  @Test
  public void aPlottedSurfaceKeepsItsCleanSkin() {
    for (String input : new String[] {"Plot3D[Sin[x y],{x,-1,1},{y,-1,1},PlotPoints->4]",
        "ParametricPlot3D[{Cos[t],Sin[t],u},{t,0,Pi},{u,0,1},PlotPoints->4]",
        "ListPlot3D[{{1,2},{3,4}}]"}) {
      assertFalse(element(scene(input), "Polygon").get("showMesh").asBoolean(),
          input + " must not outline every facet");
    }
  }
}
