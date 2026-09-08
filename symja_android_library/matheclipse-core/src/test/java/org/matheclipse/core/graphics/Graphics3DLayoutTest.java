package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;

/**
 * The options that decide how much room the picture takes and where in it the drawing goes.
 *
 * <p>
 * All five were accepted and then ignored. They are measured here rather than compared, because
 * what each one means is a number - a canvas that grew by the margin, a drawing that shrank by the
 * padding, a range that widened by what it was padded with - and a test that only asked whether
 * something changed would pass on any change at all.
 */
public class Graphics3DLayoutTest {

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

  private static final String BASE = "Graphics3D[Cuboid[], ImageSize->300";

  private static String svg(String input) {
    return SVGGraphics3D.toSVG((IAST) evaluator.eval(input));
  }

  private static String scene(String input) {
    return WebGLGraphics3D.generateJSON((IAST) evaluator.eval(input));
  }

  /** The canvas the picture is drawn on. */
  private static double[] canvas(String svg) {
    Matcher m = Pattern.compile("width=\"([0-9.]+)\" height=\"([0-9.]+)\"").matcher(svg);
    assertTrue(m.find(), "no canvas size in " + svg.substring(0, Math.min(120, svg.length())));
    return new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))};
  }

  /** How wide the drawing itself ended up. */
  private static double drawnWidth(String svg) {
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;
    Matcher m = Pattern.compile("(-?[0-9.]+),(-?[0-9.]+)").matcher(svg);
    while (m.find()) {
      double x = Double.parseDouble(m.group(1));
      min = Math.min(min, x);
      max = Math.max(max, x);
    }
    return max - min;
  }

  @Test
  public void aspectRatioSetsTheHeightFromTheWidth() {
    assertEquals(150.0, canvas(svg(BASE + ", AspectRatio->0.5]"))[1], 1e-9,
        "half as tall as it is wide");
    assertEquals(300.0, canvas(svg(BASE + "]"))[1], 1e-9, "and square without it");
    assertEquals(300.0,
        canvas(svg("Graphics3D[Cuboid[], ImageSize->{300,300}, AspectRatio->0.5]"))[1], 1e-9,
        "a height given outright says the same thing more directly, so it wins");
  }

  /** Margins are outside the picture, so the canvas grows and the drawing does not. */
  @Test
  public void imageMarginsGrowTheCanvas() {
    double[] size = canvas(svg(BASE + ", ImageMargins->20]"));
    assertEquals(340.0, size[0], 1e-9);
    assertEquals(340.0, size[1], 1e-9);

    double[] sided = canvas(svg(BASE + ", ImageMargins->{{10,30},{0,40}}]"));
    assertEquals(340.0, sided[0], 1e-9, "ten on the left and thirty on the right");
    assertEquals(340.0, sided[1], 1e-9, "nothing below and forty above");
  }

  /** Padding is inside the picture, so the canvas stays and the drawing shrinks. */
  @Test
  public void imagePaddingShrinksTheDrawing() {
    assertEquals(300.0, canvas(svg(BASE + ", ImagePadding->60]"))[0], 1e-9,
        "the canvas is the size that was asked for");
    assertTrue(drawnWidth(svg(BASE + ", ImagePadding->60]"))
        < drawnWidth(svg(BASE + ", ImagePadding->None]")),
        "more room kept aside leaves less to draw in");
    assertTrue(drawnWidth(svg(BASE + ", ImagePadding->None]")) > drawnWidth(svg(BASE + "]")),
        "and asking for none leaves more than the renderer's own allowance");
  }

  /** {@code PlotRegion} confines the drawing to a part of the display area. */
  @Test
  public void plotRegionConfinesTheDrawing() {
    double half = drawnWidth(svg(BASE + ", PlotRegion->{{0,0.5},{0,0.5}}]"));
    double whole = drawnWidth(svg(BASE + "]"));
    assertEquals(0.5, half / whole, 0.05, "half the area across gives half the drawing");
    assertEquals(300.0, canvas(svg(BASE + ", PlotRegion->{{0,0.5},{0,0.5}}]"))[0], 1e-9,
        "the canvas is untouched");
  }

  /**
   * {@code PlotRangePadding} widens what the box covers, in the data's own units.
   *
   * <p>
   * It is padding of the range rather than of the picture, so the axes count it too.
   */
  @Test
  public void plotRangePaddingWidensTheRange() {
    // Automatic is the default and leaves 2% of the finished box on each side, which for a unit
    // cuboid is 1/48 of a unit: the box covers 1/0.96 units and the cuboid fills 96% of it
    assertTrue(scene("Graphics3D[Cuboid[]]")
        .contains("\"plotRange\":[[-0.020833333333333336,1.0208333333333333]"),
        "a unit cuboid is given a little room by default");
    assertEquals(scene("Graphics3D[Cuboid[]]"), scene("Graphics3D[Cuboid[], PlotRangePadding->Automatic]"),
        "and that is what Automatic asks for by name");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRangePadding->1]")
        .contains("\"plotRange\":[[-1.0,2.0],[-1.0,2.0],[-1.0,2.0]]"),
        "a number is in the data's units");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRangePadding->Scaled[0.5]]")
        .contains("\"plotRange\":[[-0.5,1.5],[-0.5,1.5],[-0.5,1.5]]"),
        "Scaled is a fraction of each axis's own extent");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRangePadding->None]")
        .contains("\"plotRange\":[[0.0,1.0]"), "None pads nothing");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRangePadding->{0,0,1}]")
        .contains("\"plotRange\":[[0.0,1.0],[0.0,1.0],[-1.0,2.0]]"),
        "one setting per axis");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRangePadding->{{0,0},{0,0},{1,2}}]")
        .contains("\"plotRange\":[[0.0,1.0],[0.0,1.0],[-1.0,3.0]]"),
        "and each axis may name its two sides");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRangePadding->{Automatic,None,1}]")
        .contains("\"plotRange\":[[-0.020833333333333336,1.0208333333333333],[0.0,1.0],[-1.0,2.0]]"),
        "the three kinds mix freely across the axes");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRangePadding->{{Scaled[0.25],0.5},None,None}]")
        .contains("\"plotRange\":[[-0.5,1.5],[0.0,1.0],[0.0,1.0]]"),
        "and within one axis: half a unit above the cuboid makes a span of 1.5, and a low side"
            + " worth a quarter of the finished plot rounds it out to 2");
  }

  /**
   * An explicit {@code PlotRange} is taken at its word: automatic padding would move the very
   * numbers the user asked for.
   */
  @Test
  public void anExplicitRangeIsNotPaddedAutomatically() {
    assertTrue(scene("Graphics3D[Cuboid[], PlotRange->{{0,1},{0,1},{0,1}}]")
        .contains("\"plotRange\":[[0.0,1.0],[0.0,1.0],[0.0,1.0]]"),
        "the range that was asked for is the range that is drawn");
    assertTrue(scene("Graphics3D[Cuboid[], PlotRange->{{0,1},{0,1},{0,1}}, PlotRangePadding->0.5]")
        .contains("\"plotRange\":[[-0.5,1.5],[-0.5,1.5],[-0.5,1.5]]"),
        "but a padding asked for by name is still added");
  }

  /** A value that means nothing falls back to the default rather than cropping the picture. */
  @Test
  public void anUnreadablePaddingFallsBackToAutomatic() {
    assertEquals(scene("Graphics3D[Cuboid[]]"),
        scene("Graphics3D[Cuboid[], PlotRangePadding->\"nonsense\"]"),
        "a padding that cannot be read is no padding setting at all");
  }

  /** The interactive output frames the scene from the same numbers. */
  @Test
  public void theSceneCarriesTheLayoutForTheInteractiveOutput() {
    String scene = scene(BASE + ", ImagePadding->20, ImageMargins->10,"
        + " PlotRegion->{{0,0.5},{0,0.5}}, AspectRatio->0.5]");
    assertTrue(scene.contains("\"imagePadding\":[20.0,20.0,20.0,20.0]"));
    assertTrue(scene.contains("\"imageMargins\":[10.0,10.0,10.0,10.0]"));
    assertTrue(scene.contains("\"plotRegion\":[[0.0,0.5],[0.0,0.5]]"));
    assertTrue(scene.contains("\"imageSize\":[300.0,150.0]"), "AspectRatio decided the height");
  }

  /** Whatever the layout, the output stays one well formed picture. */
  @Test
  public void theOutputStaysWellFormed() {
    for (String option : new String[] {"AspectRatio->2", "ImageMargins->30", "ImagePadding->0",
        "ImagePadding->{{5,10},{15,20}}", "PlotRegion->{{0.25,0.75},{0.25,0.75}}",
        "PlotRangePadding->0.5", "PlotRangePadding->Scaled[0.1]"}) {
      String svg = svg(BASE + ", Axes->True, " + option + "]");
      assertTrue(svg.startsWith("<svg "), option);
      assertEquals(1, svg.split("<svg\\b", -1).length - 1, option);
      assertTrue(svg.contains("</svg>"), option);
    }
  }
}
