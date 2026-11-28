package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.PrimitiveCollector;
import org.matheclipse.core.graphics.svg.Style2D;
import org.matheclipse.core.graphics.svg.ThemeColors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The primitives and directives which are drawn by rewriting them (PolarCurve, FilledPolarCurve,
 * AxisObject, Raster3D, ImageMarker) or by SVG definitions (Haloing, DropShadowing,
 * LinearGradientFilling, HatchFilling), the appearance dependent colours, and the options
 * PlotLayout, PolarAxesOrigin, LegendLabel and LegendFunction. Assertions are on the collected
 * primitives where possible, on the SVG text only for what exists only there.
 */
public class GraphicsEffectsTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
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
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  private static IExpr eval(String input) {
    return evaluator.eval(input);
  }

  /** Collect the content of a {@code Graphics[...]} expression. */
  private static List<Prim2D> collect(String input) {
    IExpr result = eval(input);
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) result).arg1(), new Style2D());
    return collector.primitives();
  }

  private static String svg(String input) {
    return new SVGGraphics(360, 240).toSVG((IAST) eval(input));
  }

  private static int count(String text, String part) {
    int n = 0;
    for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + 1)) {
      n++;
    }
    return n;
  }

  // ------------------------------------------------------------ polar curves

  @Test
  public void testPolarCurveIsALineOnTheCurve() {
    List<Prim2D> prims = collect("Graphics(PolarCurve(2, {t, 0, 2*Pi}))");
    assertEquals(1, prims.size());
    Prim2D.LinePrim line = (Prim2D.LinePrim) prims.get(0);
    assertTrue(line.segments.get(0).size() > 20);
    for (double[] p : line.segments.get(0)) {
      assertEquals(2.0, Math.hypot(p[0], p[1]), 1e-9, "every point lies on the circle r = 2");
    }
  }

  @Test
  public void testPolarCurveWithSymbolicRangeDrawsNothing() {
    assertTrue(collect("Graphics(PolarCurve(1 + Cos(t), {t, 0, q}))").isEmpty());
  }

  @Test
  public void testFilledPolarCurveOfAPartialTurnStartsAtThePole() {
    List<Prim2D> prims = collect("Graphics(FilledPolarCurve(1, {t, 0, Pi/2}))");
    Prim2D.PolygonPrim polygon = (Prim2D.PolygonPrim) prims.get(0);
    assertEquals(0.0, polygon.outer.get(0)[0], 1e-12);
    assertEquals(0.0, polygon.outer.get(0)[1], 1e-12);
    // the quarter disk lies in the first quadrant
    for (double[] p : polygon.outer) {
      assertTrue(p[0] >= -1e-9 && p[1] >= -1e-9);
    }
  }

  @Test
  public void testFilledPolarCurveFullTurnForms() {
    Prim2D.PolygonPrim wrapped =
        (Prim2D.PolygonPrim) collect("Graphics(FilledPolarCurve(3, {t, 0, 2*Pi}))").get(0);
    Prim2D.PolygonPrim bare =
        (Prim2D.PolygonPrim) collect("Graphics(FilledPolarCurve(3, t))").get(0);
    assertEquals(wrapped.outer.size(), bare.outer.size());
    for (double[] p : bare.outer) {
      assertEquals(3.0, Math.hypot(p[0], p[1]), 1e-9, "a full turn has no point at the pole");
    }
  }

  // ------------------------------------------------------------ axis objects

  @Test
  public void testAxisObjectLabelsTheRange() {
    List<Prim2D> prims =
        collect("Graphics(AxisObject(Line({{0, 0}, {4, 0}}), {0, 100}))");
    StringBuilder labels = new StringBuilder();
    int lines = 0;
    for (Prim2D p : prims) {
      if (p instanceof Prim2D.TextPrim) {
        Prim2D.TextPrim text = (Prim2D.TextPrim) p;
        labels.append(text.text).append(' ');
        // the value 100 sits at the end of the path, 50 in the middle
        if (text.text.equals("100")) {
          assertEquals(4.0, text.x, 1e-9);
        }
        if (text.text.equals("50")) {
          assertEquals(2.0, text.x, 1e-9);
        }
        // below a path that runs to the right
        assertTrue(text.shiftY < 0);
      } else if (p instanceof Prim2D.LinePrim) {
        lines++;
      }
    }
    assertEquals("0 20 40 60 80 100 ", labels.toString());
    assertTrue(lines > 6, "the path and its ticks");
  }

  @Test
  public void testAxisObjectWithoutLabels() {
    List<Prim2D> prims =
        collect("Graphics(AxisObject(Line({{0, 0}, {0, 3}}), TickLabels -> None))");
    for (Prim2D p : prims) {
      assertFalse(p instanceof Prim2D.TextPrim);
    }
  }

  // ------------------------------------------------------------ directives

  @Test
  public void testHaloingSetsTheOutline() {
    Prim2D line = collect("Graphics({Haloing(Red, 4), Line({{0, 0}, {1, 1}})})").get(0);
    assertEquals(Color.RED, line.style.haloColor);
    assertEquals(4.0, line.style.haloRadius, 1e-12);
    Prim2D plain = collect("Graphics({Haloing(), Haloing(None), Line({{0, 0}, {1, 1}})})").get(0);
    assertNull(plain.style.haloColor);
    Prim2D white = collect("Graphics({Haloing(), Line({{0, 0}, {1, 1}})})").get(0);
    assertEquals(Color.WHITE, white.style.haloColor);
  }

  @Test
  public void testDropShadowingSetsTheShadow() {
    Prim2D disk = collect("Graphics({DropShadowing({5, 1}, 3, Blue), Disk()})").get(0);
    assertEquals(5.0, disk.style.shadow[0], 1e-12);
    assertEquals(1.0, disk.style.shadow[1], 1e-12);
    assertEquals(3.0, disk.style.shadow[2], 1e-12);
    assertEquals(Color.BLUE, disk.style.shadowColor);
    // the default colour is the foreground at a third of its opacity
    Prim2D standard = collect("Graphics({DropShadowing(), Disk()})").get(0);
    assertEquals(85, standard.style.shadowColor.getAlpha(), 1);
    assertNull(collect("Graphics({DropShadowing(None), Disk()})").get(0).style.shadow);
  }

  @Test
  public void testFillingsPaintTheFaces() {
    Prim2D rect =
        collect("Graphics({LinearGradientFilling({Red, Blue}), Rectangle()})").get(0);
    assertNotNull(rect.style.facePaint);
    Prim2D faceForm = collect("Graphics({FaceForm(HatchFilling()), Disk()})").get(0);
    assertNotNull(faceForm.style.facePaint);
    // a plain FaceForm colour replaces the filling
    Prim2D replaced = collect(
        "Graphics({FaceForm(HatchFilling()), FaceForm(Red), Disk()})").get(0);
    assertNull(replaced.style.facePaint);
  }

  @Test
  public void testAntialiasingRule() {
    List<Prim2D> prims = collect(
        "Graphics({Antialiasing -> False, Disk(), Antialiasing -> True, Disk({3, 0})})");
    assertTrue(prims.get(0).style.crispEdges);
    assertFalse(prims.get(1).style.crispEdges);
  }

  @Test
  public void testSvgDefinitionsOfTheEffects() {
    String halo = svg("Graphics({Haloing(Yellow), Line({{0, 0}, {1, 1}})})");
    assertTrue(halo.contains("<filter id=\"halo0_"));
    assertTrue(halo.contains("feComponentTransfer"));
    assertTrue(halo.contains("filter=\"url(#halo0_"));

    String shadow = svg("Graphics({DropShadowing({2, 2}), Disk(), Rectangle({2, 0})})");
    // the two primitives share one filter
    assertEquals(1, count(shadow, "<filter "));
    assertEquals(2, count(shadow, "filter=\"url(#shadow0_"));
    // an offset upwards is a negative SVG dy
    assertTrue(shadow.contains("dy=\"-2\""));

    String gradient = svg("Graphics({LinearGradientFilling({Red, Blue}, Pi/2), Rectangle()})");
    assertTrue(gradient.contains("<linearGradient id=\"gradient0_"));
    assertTrue(gradient.contains("fill=\"url(#gradient0_"));
    // Pi/2 runs from the bottom up
    assertTrue(gradient.contains("y1=\"1\"") && gradient.contains("y2=\"0\""));

    String hatch = svg("Graphics({Green, HatchFilling(Pi/4, 1, 5), Disk()})");
    assertTrue(hatch.contains("<pattern id=\"hatch0_"));
    // the angle 0 is vertical lines: Pi/4 turns them by another 45 degrees
    assertTrue(hatch.contains("rotate(-135)"));
    assertTrue(hatch.contains("fill=\"rgb(0,255,0)\""));

    String crisp = svg("Graphics({Antialiasing -> False, Disk()})");
    assertTrue(crisp.contains("shape-rendering=\"crispEdges\""));
  }

  // ------------------------------------------------------------ appearance colours

  @Test
  public void testAppearanceColoursResolveToTheLightVariant() {
    assertEquals(Color.RED,
        collect("Graphics({LightDarkSwitched(Red, Blue), Disk()})").get(0).style.fillColor);
    // the missing light variant is the dark one with its lightness turned over
    Color derived = collect("Graphics({LightDarkSwitched(Automatic, Black), Disk()})").get(0).style.fillColor;
    assertEquals(Color.WHITE, derived);
    assertEquals(new Color(0x3875d7),
        collect("Graphics({SystemColor(\"Highlight\"), Disk()})").get(0).style.fillColor);
    assertNotNull(
        collect("Graphics({ThemeColor(\"Accent1\"), Disk()})").get(0).style.fillColor);
    // an unknown name changes nothing
    assertEquals(Color.BLACK,
        collect("Graphics({ThemeColor(\"NoSuchColour\"), Disk()})").get(0).style.fillColor);
  }

  @Test
  public void testLightnessFlip() {
    Color navy = new Color(0, 0, 128);
    Color flipped = ThemeColors.flipLightness(navy);
    assertEquals(0, flipped.getRed() - flipped.getGreen());
    assertTrue(flipped.getBlue() == 255 && flipped.getRed() > 120);
    assertEquals(Color.BLACK, ThemeColors.flipLightness(Color.WHITE));
  }

  // ------------------------------------------------------------ markers

  @Test
  public void testImageMarkerIsAnInsetAtThePoint() {
    IExpr plot = eval(
        "ListPlot({2, 5, 3}, PlotMarkers -> {ImageMarker(Graphics({Purple, Disk()}), 16)})");
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) plot).arg1(), new Style2D());
    int insets = 0;
    for (Prim2D p : collector.primitives()) {
      if (p instanceof Prim2D.InsetPrim) {
        insets++;
      }
    }
    assertEquals(3, insets);
  }

  // ------------------------------------------------------------ Raster3D

  @Test
  public void testRaster3DVoxels() {
    IExpr voxels = Raster3DPrimitives.voxels(
        (IAST) eval("Raster3D({{{0, 1}, {1, 0}}, {{1, 0}, {0.5, 1}}})"), EvalEngine.get());
    // EdgeForm(None), then eight cells, none hidden in a 2x2x2 block
    assertEquals(9, voxels.argSize());
    assertEquals("EdgeForm(None)", voxels.first().toString());
    // without a box the cells fill the unit cube
    assertEquals("{RGBColor(0.0,0.0,0.0),Cuboid({0.0,0.0,0.0},{0.5,0.5,0.5})}",
        voxels.second().toString());
    // a box and transparent cells
    IExpr boxed = Raster3DPrimitives.voxels((IAST) eval(
        "Raster3D({{{{1, 0, 0, 0}, {0, 1, 0, 0.5}}}}, {{0, 0, 0}, {4, 2, 1}})"), EvalEngine.get());
    assertEquals(2, boxed.argSize());
    assertEquals(
        "{RGBColor(0.0,1.0,0.0),Opacity(0.5),Cuboid({2.0,0.0,0.0},{4.0,2.0,1.0})}",
        boxed.second().toString());
  }

  @Test
  public void testRaster3DHidesInnerVoxels() {
    // a solid 3x3x3 block shows 26 voxels; the one in the middle can't be seen
    IExpr voxels = Raster3DPrimitives.voxels(
        (IAST) eval("Raster3D(ConstantArray(0.5, {3, 3, 3}))"), EvalEngine.get());
    assertEquals(27, voxels.argSize());
  }

  @Test
  public void testRaster3DInWebGL() {
    String json = WebGLGraphics3D.generateJSON(
        (IAST) eval("Graphics3D(Raster3D({{{0.2, 0.8}}}))"));
    assertEquals(2, count(json, "\"Cuboid\""));
  }

  // ------------------------------------------------------------ options

  @Test
  public void testPolarAxesOriginTurnsTheRadialLabels() {
    IExpr plot = eval(
        "PolarPlot(1, {t, 0, 2*Pi}, PolarAxes -> True, PolarAxesOrigin -> {Pi/2, 0.5})");
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) plot).arg1(), new Style2D());
    boolean circle = false;
    for (Prim2D p : collector.primitives()) {
      if (p instanceof Prim2D.TextPrim) {
        // the distances are written up the y axis
        assertEquals(0.0, ((Prim2D.TextPrim) p).x, 1e-9);
      }
      if (p instanceof Prim2D.EllipsePrim && Math.abs(((Prim2D.EllipsePrim) p).rx - 0.5) < 1e-9) {
        circle = true;
      }
    }
    assertTrue(circle, "the angular axis at radius 0.5");
  }

  @Test
  public void testLegendLabelAndFunction() {
    String framed = svg("Plot({x, 2*x}, {x, 0, 1}, PlotLegends -> LineLegend({Red, Blue}, {\"a\", \"b\"}, LegendLabel -> \"pair\", LegendFunction -> \"Frame\"))");
    assertTrue(framed.contains(">pair</text>"));
    assertTrue(framed.contains("stroke=\"black\""));
    String bare = svg("Plot({x, 2*x}, {x, 0, 1}, PlotLegends -> LineLegend({Red, Blue}, {\"a\", \"b\"}, LegendFunction -> None))");
    assertFalse(bare.contains("stroke=\"#cccccc\""));
  }
}
