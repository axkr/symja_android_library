package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>Overlay</code> composites its layers onto one canvas.
 *
 * <p>
 * The assertions are structural rather than golden: what matters is that there is one nested
 * viewport per drawn layer, in the right order and at the right offset, and that only the canvas
 * paints a background. The exact geometry inside a layer belongs to the other graphics tests.
 */
public class OverlayTest {

  /** The nested viewport of one layer. */
  private static final Pattern LAYER =
      Pattern.compile("<svg x=\"([-0-9.]+)\" y=\"([-0-9.]+)\" width=\"([0-9.]+)\" "
          + "height=\"([0-9.]+)\"");

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
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
    // other tests in the suite leave values assigned to common one letter symbols
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  private static String svg(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result instanceof IAST, () -> input + " did not evaluate to an AST but " + result);
    String out = new SVGGraphics(600, 400).toSVG((IAST) result, true);
    assertNotNull(out, () -> "toSVG returned null for " + input);
    assertFalse(out.isEmpty(), () -> "toSVG returned an empty string for " + input);
    return out;
  }

  /** The {@code x, y, width, height} of every layer viewport, in drawing order. */
  private static List<double[]> layers(String svg) {
    List<double[]> found = new ArrayList<>();
    Matcher m = LAYER.matcher(svg);
    while (m.find()) {
      found.add(new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
          Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4))});
    }
    return found;
  }

  /** The width and height of the canvas. */
  private static double[] canvas(String svg) {
    Matcher m = Pattern.compile("viewBox=\"0 0 ([0-9.]+) ([0-9.]+)\"").matcher(svg);
    assertTrue(m.find(), "no viewBox on the root");
    return new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))};
  }

  private static int count(String haystack, String needle) {
    int n = 0;
    for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + 1)) {
      n++;
    }
    return n;
  }

  // ------------------------------------------------------------- stacking

  @Test
  public void testLayersShareOneFootprint() {
    String out = svg("Overlay[{Graphics[Disk[]], Graphics[Circle[]]}]");
    List<double[]> found = layers(out);
    assertEquals(2, found.size(), "one nested viewport per layer");
    assertEquals(found.get(0)[0], found.get(1)[0], 1e-9, "layers are stacked, not tiled");
    assertEquals(found.get(0)[1], found.get(1)[1], 1e-9, "layers are stacked, not tiled");
  }

  @Test
  public void testOnlyTheCanvasPaintsABackground() {
    String out = svg("Overlay[{Graphics[Disk[]], Graphics[Circle[]]}]");
    assertEquals(1, count(out, "fill=\"white\""),
        () -> "a layer painted its own canvas, hiding the one below it:\n" + out);
  }

  @Test
  public void testALayerKeepsItsOwnBackground() {
    String out = svg("Overlay[{Graphics[Disk[]], Graphics[Circle[], Background -> Red]}]");
    assertTrue(out.contains("fill=\"rgb(255,0,0)\""),
        () -> "an explicit layer Background must still paint:\n" + out);
  }

  @Test
  public void testTranslucentBackgroundKeepsItsAlpha() {
    String out = svg("Overlay[{Graphics[Disk[]], "
        + "Graphics[Circle[], Background -> Directive[{Opacity[0.5], Orange}]]}]");
    assertTrue(out.contains("fill=\"rgb(255,128,0)\""), () -> "orange background lost:\n" + out);
    assertTrue(out.contains("fill-opacity=\"0.502\""),
        () -> "a translucent background must not render opaque:\n" + out);
  }

  @Test
  public void testDirectiveBackgroundParses() {
    Color c = ColorUtil.parseDirective(evaluator.eval("Directive({Opacity(0.5), Orange})"));
    assertNotNull(c, "Directive is the documented spelling for a see through layer");
    assertEquals(128, c.getAlpha(), 2);
  }

  // ------------------------------------------------------------- selection

  @Test
  public void testIndexListSelectsAndOrders() {
    // {2, 3, 1} draws green, then blue, then red - so the first item ends up on top
    String out = svg("Overlay[{Graphics[{Red, Disk[]}], Graphics[{Green, Rectangle[]}], "
        + "Graphics[{Blue, Circle[]}]}, {2, 3, 1}]");
    assertEquals(3, layers(out).size());
    int green = out.indexOf("rgb(0,255,0)");
    int blue = out.indexOf("rgb(0,0,255)");
    int red = out.indexOf("rgb(255,0,0)");
    assertTrue(green >= 0 && blue >= 0 && red >= 0, () -> "a layer went missing:\n" + out);
    assertTrue(green < blue && blue < red, () -> "wrong drawing order:\n" + out);
  }

  @Test
  public void testASingleIndexShowsOneLayer() {
    String out = svg("Overlay[{Graphics[Disk[]], Graphics[Circle[]], Graphics[Rectangle[]]}, {2}]");
    assertEquals(1, layers(out).size());
  }

  @Test
  public void testOutOfRangeIndicesLeaveABlankPicture() {
    String out = svg("Overlay[{Graphics[Disk[]]}, {7}]");
    assertEquals(0, layers(out).size());
    assertTrue(out.contains("<svg"), "a blank overlay is still a well formed picture");
  }

  @Test
  public void testAnOptionRuleIsNotReadAsASelection() {
    // ImageSize sits where the index list would, and must not be mistaken for one
    String out = svg("Overlay[{Graphics[Disk[]], Graphics[Circle[]]}, ImageSize -> 200]");
    assertEquals(2, layers(out).size());
  }

  @Test
  public void testTheSelectableLayerIsAcceptedAndIgnored() {
    String out = svg("Overlay[{Graphics[Disk[]], Graphics[Circle[]]}, All, 2]");
    assertEquals(2, layers(out).size());
  }

  // ------------------------------------------------------------- sizing

  @Test
  public void testCanvasFitsTheLayersThatAreShown() {
    String out = svg("Overlay[{Graphics[Disk[], ImageSize -> 100], "
        + "Graphics[Rectangle[], ImageSize -> 300]}, {1}]");
    assertEquals(100.0, canvas(out)[0], 1e-9, "a hidden layer must not enlarge the canvas");
  }

  @Test
  public void testImageSizeAllLeavesRoomForHiddenLayers() {
    String out = svg("Overlay[{Graphics[Disk[], ImageSize -> 100], "
        + "Graphics[Rectangle[], ImageSize -> 300]}, {1}, None, ImageSize -> All]");
    assertEquals(300.0, canvas(out)[0], 1e-9, "ImageSize -> All sizes for the largest item");
    assertEquals(1, layers(out).size(), "only the selected layer is drawn");
  }

  @Test
  public void testExplicitImageSizeWins() {
    String out = svg("Overlay[{Graphics[Disk[]], Graphics[Circle[]]}, ImageSize -> {250, 120}]");
    double[] size = canvas(out);
    assertEquals(250.0, size[0], 1e-9);
    assertEquals(120.0, size[1], 1e-9);
  }

  // ------------------------------------------------------------- alignment

  @Test
  public void testAlignmentPlacesTheSmallerLayer() {
    String big = "Graphics[Disk[], ImageSize -> {200, 200}]";
    String small = "Graphics[Rectangle[], ImageSize -> {40, 40}]";

    assertLayerAt("Overlay[{" + big + ", " + small + "}]", 80, 80);
    assertLayerAt("Overlay[{" + big + ", " + small + "}, Alignment -> {0.5, 0.5}]", 80, 80);
    assertLayerAt("Overlay[{" + big + ", " + small + "}, Alignment -> {Left, Top}]", 0, 0);
    assertLayerAt("Overlay[{" + big + ", " + small + "}, Alignment -> {Right, Bottom}]", 160, 160);
    // a single name only moves the axis it belongs to
    assertLayerAt("Overlay[{" + big + ", " + small + "}, Alignment -> Right]", 160, 80);
    assertLayerAt("Overlay[{" + big + ", " + small + "}, Alignment -> Top]", 80, 0);
  }

  private static void assertLayerAt(String input, double x, double y) {
    List<double[]> found = layers(svg(input));
    assertEquals(2, found.size(), input);
    assertEquals(0.0, found.get(0)[0], 1e-9, input + ": the full size layer stays at the origin");
    assertEquals(x, found.get(1)[0], 1e-9, input + ": horizontal placement");
    assertEquals(y, found.get(1)[1], 1e-9, input + ": vertical placement");
  }

  // ------------------------------------------------------------- routing

  @Test
  public void testOnlyAGraphicsOverlayIsAPicture() {
    assertTrue(evaluator.eval("Overlay[{Graphics[Disk[]]}]").isGraphicsObject());
    assertTrue(evaluator.eval("Overlay[{Graphics[Disk[]], 42}]").isGraphicsObject(),
        "a stray non graphic must not disqualify the whole overlay");
    assertFalse(evaluator.eval("Overlay[{1, 2, 3}]").isGraphicsObject());
    assertFalse(evaluator.eval("Overlay[x]").isGraphicsObject());

    assertNotNull(SVGGraphics.svgDocument(evaluator.eval("Overlay[{Graphics[Disk[]]}]")));
    assertNull(SVGGraphics.svgDocument(evaluator.eval("Overlay[{1, 2, 3}]")),
        "Overlay of plain expressions has no picture, and must print as itself");
  }

  @Test
  public void testANonGraphicLayerIsDropped() {
    String out = svg("Overlay[{Graphics[Disk[]], 42}]");
    assertEquals(1, layers(out).size(), "the 42 has no picture and takes no slot");
  }

  @Test
  public void testOverlayNestsInsideAndAroundTheOtherLayouts() {
    // two layers of the outer overlay, and the inner one contributes a viewport of its own
    assertEquals(3, layers(svg("Overlay[{Overlay[{Graphics[Disk[]]}], Graphics[Circle[]]}]")).size(),
        "a nested overlay is composited rather than dropped");
    String row = svg("GraphicsRow[{Overlay[{Graphics[Disk[]], Graphics[Circle[]]}], "
        + "Graphics[Rectangle[]]}]");
    assertTrue(row.contains("<svg"), () -> "an Overlay must survive as a row cell:\n" + row);
  }
}
