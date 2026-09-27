package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.Scene2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * {@code Around}, {@code Interval} and {@code IntervalData} coordinates, drawn at their centre with
 * the markers of the {@code IntervalMarkers} option.
 */
public class IntervalMarkersTest {

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
    evaluator.eval("ClearAll(x,y)");
  }

  private static Scene2D scene(String input) {
    IExpr result = evaluator.eval(input);
    Scene2D scene = Scene2D.of((IAST) result, 600, 400);
    assertNotNull(scene, input);
    return scene;
  }

  private static <T extends Prim2D> List<T> of(Scene2D scene, Class<T> type) {
    List<T> out = new ArrayList<>();
    for (Prim2D p : scene.primitives) {
      if (type.isInstance(p)) {
        out.add(type.cast(p));
      }
    }
    return out;
  }

  private static int segments(List<Prim2D.LinePrim> lines) {
    int n = 0;
    for (Prim2D.LinePrim line : lines) {
      n += line.segments.size();
    }
    return n;
  }

  @Test
  public void testAroundPointDrawnAtCentreWithBars() {
    Scene2D scene = scene("Graphics(Point({{1, Around(2, 0.5)}, {2, Around(3, {0.25, 1})}}))");
    List<Prim2D.PointsPrim> points = of(scene, Prim2D.PointsPrim.class);
    assertEquals(1, points.size());
    assertEquals(2, points.get(0).points.size());
    assertEquals(2.0, points.get(0).points.get(0)[1], 1e-12);
    List<Prim2D.LinePrim> bars = of(scene, Prim2D.LinePrim.class);
    // one vertical bar per point, asymmetric limits kept apart
    assertEquals(2, segments(bars));
    double[] lo = bars.get(0).segments.get(1).get(0);
    double[] hi = bars.get(0).segments.get(1).get(1);
    assertEquals(2.75, lo[1], 1e-12);
    assertEquals(4.0, hi[1], 1e-12);
    // the plot range covers the bars, not only the centres
    assertTrue(scene.bounds.yMax >= 4.0 - 1e-12, scene.bounds.toString());
    assertTrue(scene.bounds.yMin <= 1.5 + 1e-12, scene.bounds.toString());
    // markers first, so the point is drawn on top
    assertTrue(scene.primitives.indexOf(bars.get(0)) < scene.primitives.indexOf(points.get(0)));
  }

  @Test
  public void testBothCoordinatesUncertain() {
    Scene2D scene = scene("Graphics(Point({Around(1, 0.2), Around(2, 0.3)}))");
    assertEquals(1, of(scene, Prim2D.PointsPrim.class).size());
    assertEquals(2, segments(of(scene, Prim2D.LinePrim.class)));
  }

  @Test
  public void testIntervalAndIntervalDataCoordinates() {
    Scene2D scene =
        scene("Graphics(Point({{1, Interval({1, 3})}, {2, IntervalData({2, Less, LessEqual, 4})}}))");
    List<Prim2D.PointsPrim> points = of(scene, Prim2D.PointsPrim.class);
    assertEquals(2.0, points.get(0).points.get(0)[1], 1e-12);
    assertEquals(3.0, points.get(0).points.get(1)[1], 1e-12);
    assertEquals(2, segments(of(scene, Prim2D.LinePrim.class)));
  }

  @Test
  public void testFencesCapOnlyClosedEnds() {
    Scene2D scene = scene("Graphics(Point({{1, Around(2, 0.5)}, {2, IntervalData({2, Less, LessEqual, 4})}}),"
        + " IntervalMarkers -> \"Fences\")");
    List<Prim2D.LinePrim> lines = of(scene, Prim2D.LinePrim.class);
    // two bars, then 2 caps for Around and 1 for the half open interval
    assertEquals(2, lines.size());
    assertEquals(2, lines.get(0).segments.size());
    assertEquals(3, lines.get(1).segments.size());
    // a cap of a vertical bar is horizontal
    List<double[]> cap = lines.get(1).segments.get(0);
    assertEquals(cap.get(0)[1], cap.get(1)[1], 1e-12);
    assertTrue(cap.get(1)[0] != cap.get(0)[0]);
  }

  @Test
  public void testFencesAcrossALogAxis() {
    Scene2D scene = scene("Graphics(Point({{1, Around(2, 0.5)}, {100, Around(3, 0.5)}}),"
        + " IntervalMarkers -> \"Fences\", ScalingFunctions -> {\"Log\", None})");
    List<Prim2D.LinePrim> lines = of(scene, Prim2D.LinePrim.class);
    assertEquals(4, lines.get(1).segments.size());
    // the caps of the bar at x = 100 are as long on the left as on the right of the log axis
    List<double[]> cap = lines.get(1).segments.get(2);
    assertEquals(100.0 * 100.0, cap.get(0)[0] * cap.get(1)[0], 1e-6);
    assertTrue(Math.min(cap.get(0)[0], cap.get(1)[0]) < 100.0
        && Math.max(cap.get(0)[0], cap.get(1)[0]) > 100.0);
  }

  @Test
  public void testBandsAlongLine() {
    Scene2D scene = scene("Graphics(Line({{1, Around(1, 0.5)}, {2, Around(2, 0.5)}, {3, 3}}),"
        + " IntervalMarkers -> \"Bands\")");
    List<Prim2D.PolygonPrim> bands = of(scene, Prim2D.PolygonPrim.class);
    assertEquals(1, bands.size());
    Prim2D.PolygonPrim band = bands.get(0);
    // upper limits forward, lower limits back
    assertEquals(6, band.outer.size());
    assertEquals(1.5, band.outer.get(0)[1], 1e-12);
    assertEquals(3.0, band.outer.get(2)[1], 1e-12);
    assertEquals(0.5, band.outer.get(5)[1], 1e-12);
    assertTrue(band.style.opacity < 1.0);
    assertEquals(1, of(scene, Prim2D.LinePrim.class).size());
  }

  @Test
  public void testBandsSortUnconnectedPoints() {
    Scene2D scene = scene("Graphics(Point({{3, Around(3, 1)}, {1, Around(1, 1)}, {2, Around(2, 1)}}),"
        + " IntervalMarkers -> \"Bands\")");
    Prim2D.PolygonPrim band = of(scene, Prim2D.PolygonPrim.class).get(0);
    assertEquals(1.0, band.outer.get(0)[0], 1e-12);
    assertEquals(3.0, band.outer.get(2)[0], 1e-12);
  }

  @Test
  public void testPointsEllipsesAndNone() {
    Scene2D points = scene("Graphics(Point({Around(1, 0.2), Around(2, 0.3)}), IntervalMarkers -> \"Points\")");
    assertEquals(2, of(points, Prim2D.PointsPrim.class).size());
    assertEquals(4, of(points, Prim2D.PointsPrim.class).get(0).points.size());

    Scene2D ellipses = scene("Graphics(Point({Around(1, 0.2), Around(2, 0.3)}), IntervalMarkers -> \"Ellipses\")");
    List<Prim2D.EllipsePrim> e = of(ellipses, Prim2D.EllipsePrim.class);
    assertEquals(1, e.size());
    assertEquals(0.2, e.get(0).rx, 1e-12);
    assertEquals(0.3, e.get(0).ry, 1e-12);

    Scene2D none = scene("Graphics(Point({Around(1, 0.2), Around(2, 0.3)}), IntervalMarkers -> None)");
    assertEquals(1, none.primitives.size());
  }

  @Test
  public void testIntervalMarkersStyle() {
    Scene2D scene = scene("Graphics({Blue, Point({1, Around(2, 0.5)})}, IntervalMarkersStyle -> Red)");
    Prim2D.LinePrim bar = of(scene, Prim2D.LinePrim.class).get(0);
    assertEquals(Color.RED, bar.style.strokeColor);
    Prim2D.PointsPrim point = of(scene, Prim2D.PointsPrim.class).get(0);
    assertEquals(Color.BLUE, point.style.strokeColor);
  }

  @Test
  public void testMarkersFollowTransforms() {
    Scene2D scene = scene("Graphics(Translate(Point({1, Around(2, 0.5)}), {10, 0}))");
    Prim2D.LinePrim bar = of(scene, Prim2D.LinePrim.class).get(0);
    assertEquals(11.0, bar.segments.get(0).get(0)[0], 1e-12);
  }

  // ------------------------------------------------------------------ 3D

  private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER =
      new com.fasterxml.jackson.databind.ObjectMapper();

  private static com.fasterxml.jackson.databind.JsonNode scene3D(String input) {
    IExpr result = evaluator.eval(input);
    try {
      return MAPPER.readTree(WebGLGraphics3D.generateJSON((IAST) result));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static List<com.fasterxml.jackson.databind.JsonNode> elements(
      com.fasterxml.jackson.databind.JsonNode scene, String type) {
    List<com.fasterxml.jackson.databind.JsonNode> out = new ArrayList<>();
    for (com.fasterxml.jackson.databind.JsonNode e : scene.get("elements")) {
      if (type.equals(e.get("type").asText())) {
        out.add(e);
      }
    }
    return out;
  }

  @Test
  public void testGraphics3DBars() {
    com.fasterxml.jackson.databind.JsonNode scene =
        scene3D("Graphics3D(Point({{1, 2, Around(3, 0.5)}, {Around(2, 0.1), 2, 2}}))");
    assertEquals(1, elements(scene, "Point").size());
    assertEquals(2, elements(scene, "Point").get(0).get("points").size() / 3);
    List<com.fasterxml.jackson.databind.JsonNode> lines = elements(scene, "Line");
    assertEquals(1, lines.size());
    com.fasterxml.jackson.databind.JsonNode bars = lines.get(0).get("polylines");
    assertEquals(2, bars.size());
    // the z bar of the first point, from 2.5 to 3.5
    assertEquals(2.5, bars.get(0).get(2).asDouble(), 1e-12);
    assertEquals(3.5, bars.get(0).get(5).asDouble(), 1e-12);
  }

  @Test
  public void testGraphics3DTubesAndBands() {
    com.fasterxml.jackson.databind.JsonNode tubes = scene3D(
        "Graphics3D(Point({{1, 2, Around(3, 0.5)}, {2, 2, 2}}), IntervalMarkers -> \"Tubes\")");
    List<com.fasterxml.jackson.databind.JsonNode> t = elements(tubes, "Tube");
    assertEquals(1, t.size());
    assertTrue(t.get(0).get("radius").asDouble() > 0);

    com.fasterxml.jackson.databind.JsonNode bands = scene3D(
        "Graphics3D(Line({{1, 1, Around(1, 0.5)}, {2, 2, Around(2, 0.5)}, {3, 3, 3}}),"
            + " IntervalMarkers -> \"Bands\")");
    List<com.fasterxml.jackson.databind.JsonNode> polygons = elements(bands, "Polygon");
    assertEquals(1, polygons.size());
    assertEquals(6, polygons.get(0).get("points").size() / 3);
    assertEquals(12, polygons.get(0).get("indices").size());
    assertTrue(polygons.get(0).get("opacity").asDouble() < 1.0);
    assertEquals(0, elements(bands, "Line").size() - 1);
  }

  @Test
  public void testGraphics3DIntervalMarkersStyleAndNone() {
    com.fasterxml.jackson.databind.JsonNode styled = scene3D(
        "Graphics3D({Blue, Point({1, 2, Interval({2, 4})})}, IntervalMarkersStyle -> Red)");
    assertEquals(0xFF0000, elements(styled, "Line").get(0).get("color").asInt());
    assertEquals(0x0000FF, elements(styled, "Point").get(0).get("color").asInt());

    com.fasterxml.jackson.databind.JsonNode none =
        scene3D("Graphics3D(Point({1, 2, Around(3, 0.5)}), IntervalMarkers -> None)");
    assertEquals(0, elements(none, "Line").size());
    assertEquals(1, elements(none, "Point").size());
  }

  // ---------------------------------------------------------- list plots

  @Test
  public void testListPlotKeepsAroundValues() {
    Scene2D scene = scene("ListPlot({Around(1, 0.1), Around(2, 0.5), 3})");
    List<Prim2D.PointsPrim> points = of(scene, Prim2D.PointsPrim.class);
    int n = 0;
    for (Prim2D.PointsPrim p : points) {
      n += p.points.size();
    }
    assertEquals(3, n);
    assertEquals(2, segments(of(scene, Prim2D.LinePrim.class)));
    assertTrue(scene.bounds.yMax >= 3.0 - 1e-12);
  }

  @Test
  public void testListPlotPairsAndIntervalMarkers() {
    Scene2D scene = scene("ListPlot({{1, Around(1, 0.2)}, {2, Interval({1.5, 2.5})}, {3, 3}},"
        + " IntervalMarkers -> \"Bands\", IntervalMarkersStyle -> Red, PlotRange -> All)");
    List<Prim2D.PolygonPrim> bands = of(scene, Prim2D.PolygonPrim.class);
    assertEquals(1, bands.size());
    assertEquals(Color.RED, bands.get(0).style.fillColor);
  }

  @Test
  public void testListLinePlotBands() {
    IExpr result =
        evaluator.eval("ListLinePlot({Around(1, 0.2), Around(2, 0.3), Around(1.5, 0.4)},"
            + " IntervalMarkers -> \"Bands\")");
    assertTrue(result.toString().contains("IntervalMarkers->Bands"), result.toString());
    Scene2D scene = Scene2D.of((IAST) result, 600, 400);
    assertEquals(1, of(scene, Prim2D.PolygonPrim.class).size());
    assertEquals(6, of(scene, Prim2D.PolygonPrim.class).get(0).outer.size());
  }

  @Test
  public void testListPointPlot3DKeepsAroundValues() {
    IExpr result = evaluator.eval(
        "ListPointPlot3D({{1, 2, Around(3, 0.5)}, {2, 1, Around(1, 0.25)}, {3, 3, 2}})");
    com.fasterxml.jackson.databind.JsonNode scene;
    try {
      scene = MAPPER.readTree(WebGLGraphics3D.generateJSON((IAST) result));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    int points = 0;
    for (com.fasterxml.jackson.databind.JsonNode p : elements(scene, "Point")) {
      points += p.get("points").size() / 3;
    }
    assertEquals(3, points, result.toString());
    assertEquals(2, elements(scene, "Line").get(0).get("polylines").size(), result.toString());

    // every point uncertain: still a picture
    IExpr all = evaluator.eval("ListPointPlot3D({{1, 2, Around(3, 0.5)}, {2, 1, Around(1, 0.25)}})");
    assertTrue(all.isAST(S.Graphics3D), all.toString());
  }

  @Test
  public void testListPlot3DBars() {
    IExpr result = evaluator.eval("ListPlot3D({{1, Around(2, 0.5), 1}, {2, 2, 2}, {Around(3, 1), 1, 1}},"
        + " IntervalMarkersStyle -> Red)");
    com.fasterxml.jackson.databind.JsonNode scene;
    try {
      scene = MAPPER.readTree(WebGLGraphics3D.generateJSON((IAST) result));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    assertEquals(1, elements(scene, "Polygon").size(), result.toString());
    com.fasterxml.jackson.databind.JsonNode red = null;
    for (com.fasterxml.jackson.databind.JsonNode line : elements(scene, "Line")) {
      if (line.get("color").asInt() == 0xFF0000) {
        red = line;
      }
    }
    assertNotNull(red, result.toString());
    assertEquals(2, red.get("polylines").size(), result.toString());

    IExpr coordinates = evaluator.eval(
        "ListPlot3D({{1, 1, Around(1, 0.5)}, {2, 1, 2}, {1, 2, 2}, {2, 2, Interval({2, 4})}},"
            + " IntervalMarkers -> \"Tubes\")");
    try {
      scene = MAPPER.readTree(WebGLGraphics3D.generateJSON((IAST) coordinates));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    assertEquals(1, elements(scene, "Tube").size(), coordinates.toString());
    assertEquals(2, elements(scene, "Tube").get(0).get("polylines").size());
  }

  @Test
  public void testSvgContainsMarkers() {
    IExpr result = evaluator.eval("Graphics(Point({{1, Around(2, 0.5)}, {2, Around(3, 0.5)}}))");
    StringBuilder buf = new StringBuilder();
    assertTrue(GraphicsUtil.renderGraphics2DSVG(buf, (IAST) result, true, EvalEngine.get()));
    String svg = buf.toString();
    assertTrue(svg.contains("<svg"), svg);
    assertTrue(svg.contains("<path") || svg.contains("<line") || svg.contains("<polyline"), svg);
  }
}
