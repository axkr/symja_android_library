package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Polygons keep every corner they were given.
 *
 * <p>
 * The vertices of a polygon are welded by position, so that a corner two faces share is stored once
 * and the surface lights as one piece. The key that decided whether two positions were the same
 * combined the three rounded coordinates as {@code (x*73856093) ^ (y*19349663) ^ (z*83492791)}, and
 * an exclusive or of products collides on points that differ only by sign flips that cancel. For
 * the square {@code {{-3,-3,-2},{-3,3,-2},{3,3,-2},{3,-3,-2}}} the two far corners hashed onto the
 * two near ones: the polygon kept two of its four points, its triangles came out with zero area,
 * and it vanished from the picture. It also stopped contributing to the plot range, so the bounding
 * box came out too small as well.
 *
 * <p>
 * Symmetric coordinates like these are entirely ordinary in hand written graphics, so the tests
 * below use exactly that shape.
 */
public class PolygonWeldTest {

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
  }

  private static String json(String input) {
    IExpr graphic = evaluator.eval(input);
    assertTrue(graphic.isAST(), input + " did not evaluate to a graphic: " + graphic);
    return WebGLGraphics3D.generateJSON((IAST) graphic);
  }

  /**
   * The plot range of one axis, as {@code {low, high}}.
   *
   * <p>
   * Read as numbers rather than matched as text, so that the automatic {@code PlotRangePadding}
   * around the data does not turn a question about the corners into a question about the padding.
   */
  private static double[] plotRange(String scene, int axis) {
    try {
      JsonNode range = new ObjectMapper().readTree(scene).get("plotRange").get(axis);
      return new double[] {range.get(0).asDouble(), range.get(1).asDouble()};
    } catch (java.io.IOException ioe) {
      throw new IllegalStateException("the scene is not JSON: " + scene, ioe);
    }
  }

  /** The flat coordinate array of the first polygon element in the scene. */
  private static String polygonPoints(String scene) {
    int polygon = scene.indexOf("\"type\":\"Polygon\"");
    assertTrue(polygon > 0, "no polygon in the scene: " + scene);
    int points = scene.indexOf("\"points\":[", polygon);
    return scene.substring(points + 10, scene.indexOf(']', points));
  }

  private static String polygonIndices(String scene) {
    int polygon = scene.indexOf("\"type\":\"Polygon\"");
    int indices = scene.indexOf("\"indices\":[", polygon);
    return scene.substring(indices + 11, scene.indexOf(']', indices));
  }

  /** How many vertices a comma separated flat coordinate array holds. */
  private static int vertexCount(String flatPoints) {
    return (flatPoints.split(",", -1).length) / 3;
  }

  @Test
  public void aSquareWithSymmetricCornersKeepsAllFour() {
    String scene = json("Graphics3D[Polygon[{{-3,-3,-2},{-3,3,-2},{3,3,-2},{3,-3,-2}}]]");
    assertEquals(4, vertexCount(polygonPoints(scene)),
        "the two far corners used to hash onto the two near ones");
    assertEquals("0,1,2,0,2,3", polygonIndices(scene),
        "a quad is two triangles, neither of them degenerate");
  }

  /** The corners that were lost were also the ones that set the far side of the box. */
  @Test
  public void theLostCornersAlsoDecideThePlotRange() {
    String scene = json("Graphics3D[Polygon[{{-3,-3,-2},{-3,3,-2},{3,3,-2},{3,-3,-2}}]]");
    for (int axis = 0; axis < 2; axis++) {
      double[] range = plotRange(scene, axis);
      assertTrue(range[0] <= -3.0 && range[1] >= 3.0,
          "the box has to reach the far corners, but axis " + axis + " covers only "
              + java.util.Arrays.toString(range));
    }
  }

  /** Sign symmetry is the collision, so each of these shapes is its own case. */
  @Test
  public void everySymmetricShapeKeepsItsCorners() {
    assertEquals(3, vertexCount(
        polygonPoints(json("Graphics3D[Polygon[{{-1,-1,0},{1,1,0},{-1,1,0}}]]"))),
        "a triangle whose corners mirror through the origin");
    assertEquals(4, vertexCount(
        polygonPoints(json("Graphics3D[Polygon[{{-2,0,-2},{2,0,2},{0,-2,2},{0,2,-2}}]]"))),
        "corners that mirror in all three coordinates at once");
    assertEquals(5, vertexCount(polygonPoints(
        json("Graphics3D[Polygon[{{-1,-1,1},{1,-1,1},{1,1,1},{-1,1,1},{0,2,1}}]]"))),
        "a pentagon around the origin");
  }

  /** A polygon with more than three corners is fanned into triangles. */
  @Test
  public void aPentagonBecomesThreeTriangles() {
    String scene =
        json("Graphics3D[Polygon[{{-1,-1,1},{1,-1,1},{1,1,1},{-1,1,1},{0,2,1}}]]");
    assertEquals("0,1,2,0,2,3,0,3,4", polygonIndices(scene));
  }

  /** Welding is still wanted: a corner shared by two faces is stored once. */
  @Test
  public void genuinelyRepeatedCornersAreStillWeldedTogether() {
    // two triangles meeting along the edge from {0,0,0} to {1,1,0}: five distinct corners
    String scene =
        json("Graphics3D[Polygon[{{{0,0,0},{1,1,0},{1,0,0}},{{0,0,0},{1,1,0},{0,1,0}}}]]");
    assertEquals(4, vertexCount(polygonPoints(scene)),
        "the two shared corners are stored once, so six listed corners become four points");
  }

  /** The multi face form and the GraphicsComplex form both go through the same builder. */
  @Test
  public void theOtherPolygonFormsSurviveToo() {
    String multi =
        json("Graphics3D[Polygon[{{{-3,-3,0},{-3,3,0},{3,3,0}},{{3,3,0},{3,-3,0},{-3,-3,0}}}]]");
    assertEquals(4, vertexCount(polygonPoints(multi)),
        "two triangles over the four corners of a square");
    assertEquals("0,1,2,2,3,0", polygonIndices(multi));

    String complex = json("Graphics3D[GraphicsComplex[{{-3,-3,0},{-3,3,0},{3,3,0},{3,-3,0}},"
        + "Polygon[{{1,2,3},{1,3,4}}]]]");
    assertEquals(4, vertexCount(polygonPoints(complex)),
        "inside a GraphicsComplex the corners are indices, which never collided");
    assertEquals("0,1,2,0,2,3", polygonIndices(complex));
  }

  /** The whole example this was found in, drawn end to end. */
  @Test
  public void theReferenceExampleDrawsItsYellowFloor() {
    String scene = json("Graphics3D[{Blue, Cylinder[], Red, Sphere[{0, 0, 2}], StandardGray,"
        + " Thick, Dashed, Line[{{-2, 0, 2}, {2, 0, 2}, {0, 0, 4}, {-2, 0, 2}}],"
        + " Yellow, Polygon[{{-3, -3, -2}, {-3, 3, -2}, {3, 3, -2}, {3, -3, -2}}],"
        + " Green, Opacity[.3], Cuboid[{-2, -2, -2}, {2, 2, -1}]}]");

    assertEquals(4, vertexCount(polygonPoints(scene)), "the yellow floor is a full square");
    assertEquals("0,1,2,0,2,3", polygonIndices(scene));
    assertTrue(scene.contains("\"type\":\"Polygon\",\"color\":16776960"), "and it is yellow");
    assertTrue(plotRange(scene, 0)[0] <= -3.0 && plotRange(scene, 0)[1] >= 3.0,
        "the box reaches the floor's corners across");
    assertTrue(plotRange(scene, 1)[0] <= -3.0 && plotRange(scene, 1)[1] >= 3.0,
        "and along");
    assertTrue(plotRange(scene, 2)[0] <= -2.0 && plotRange(scene, 2)[1] >= 4.0,
        "and from the floor to the top of the sphere");
  }

  /**
   * The static renderer consumes the same scene, so it gains the polygon as well.
   *
   * <p>
   * The fill is the yellow the lights leave behind rather than a flat {@code #ffff00}, so what is
   * checked is that it still reads as yellow: red and green high, blue far below them.
   */
  @Test
  public void theSvgRendererDrawsItToo() {
    IExpr graphic = evaluator.eval("Graphics3D[{Yellow,"
        + " Polygon[{{-3, -3, -2}, {-3, 3, -2}, {3, 3, -2}, {3, -3, -2}}]}]");
    String svg = SVGGraphics3D.toSVG((IAST) graphic);

    java.util.regex.Matcher fills =
        java.util.regex.Pattern.compile("<polygon[^>]*fill=\"#([0-9a-f]{6})\"").matcher(svg);
    int drawn = 0;
    while (fills.find()) {
      int rgb = Integer.parseInt(fills.group(1), 16);
      int red = (rgb >> 16) & 0xFF;
      int green = (rgb >> 8) & 0xFF;
      int blue = rgb & 0xFF;
      assertTrue(red > 150 && green > 120 && blue < red / 2,
          "a lit yellow still has to read as yellow, got #" + fills.group(1));
      drawn++;
    }
    assertEquals(2, drawn, "the square reaches the picture as its two triangles: " + svg);
  }
}
