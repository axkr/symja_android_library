package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.graphics.svg.Bounds2D;
import org.matheclipse.core.graphics.svg.GraphicsOptions2D;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.PrimitiveCollector;
import org.matheclipse.core.graphics.svg.Viewport2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * What {@code PlotRangePadding} does to the range a two dimensional picture covers.
 *
 * <p>
 * The pictures below all draw the unit diagonal, so the data spans exactly one unit on each axis
 * and the numbers stay readable. The viewport is built the way {@code SvgGraphics2D} builds it, but
 * with no pixel padding, so what is asserted is the plot range itself rather than the layout around
 * it.
 */
public class Viewport2DPaddingTest {

  /** 2% of 1/0.96, the room {@code Automatic} leaves around a span of one. */
  private static final double AUTOMATIC = 1.0 / 48.0;

  private static final double TOLERANCE = 1e-12;

  private static final String DIAGONAL = "Graphics[Line[{{0,0},{1,1}}]";

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

  /** Lay a {@code Graphics} out the way the renderer would, without any pixel padding. */
  private static Viewport2D viewport(String input) {
    IExpr graphic = evaluator.eval(input);
    assertTrue(graphic.isAST(), input + " did not evaluate to a graphic: " + graphic);
    IAST graphicsExpr = (IAST) graphic;

    GraphicsOptions2D options = new GraphicsOptions2D();
    PrimitiveCollector collector = new PrimitiveCollector(options.imageSize[0]);
    options.parse(graphicsExpr, collector);
    if (graphicsExpr.argSize() >= 1) {
      collector.collect(graphicsExpr.arg1(), options.globalStyle.clone());
    }
    Bounds2D bounds = new Bounds2D();
    for (Prim2D primitive : collector.primitives()) {
      primitive.accumulate(bounds);
    }
    Viewport2D viewport = new Viewport2D(options);
    viewport.configure(bounds, new double[] {0, 0, 0, 0});
    return viewport;
  }

  private static void assertRangeX(String input, double min, double max) {
    Viewport2D viewport = viewport(input);
    assertEquals(min, viewport.minX, TOLERANCE, input + " across, from");
    assertEquals(max, viewport.maxX, TOLERANCE, input + " across, to");
  }

  private static void assertRangeY(String input, double min, double max) {
    Viewport2D viewport = viewport(input);
    assertEquals(min, viewport.minY, TOLERANCE, input + " up, from");
    assertEquals(max, viewport.maxY, TOLERANCE, input + " up, to");
  }

  private static void assertRange(String input, double min, double max) {
    assertRangeX(input, min, max);
    assertRangeY(input, min, max);
  }

  // ------------------------------------------------------------------ the value forms

  @Test
  public void withoutBeingAskedTheDataIsGivenALittleRoom() {
    assertRange(DIAGONAL + "]", -AUTOMATIC, 1 + AUTOMATIC);
    assertRange(DIAGONAL + ", PlotRangePadding->Automatic]", -AUTOMATIC, 1 + AUTOMATIC);
  }

  @Test
  public void noneDrawsTheDataEdgeToEdge() {
    assertRange(DIAGONAL + ", PlotRangePadding->None]", 0, 1);
  }

  @Test
  public void aNumberIsInTheDataSOwnUnits() {
    assertRange(DIAGONAL + ", PlotRangePadding->0.5]", -0.5, 1.5);
  }

  @Test
  public void scaledIsAFractionOfTheFinishedPlot() {
    // a quarter at either end leaves half the picture for the data
    assertRange(DIAGONAL + ", PlotRangePadding->Scaled[0.25]]", -0.5, 1.5);
  }

  @Test
  public void theAxesAreSetSeparately() {
    assertRangeX(DIAGONAL + ", PlotRangePadding->{0.5, None}]", -0.5, 1.5);
    assertRangeY(DIAGONAL + ", PlotRangePadding->{0.5, None}]", 0, 1);
  }

  @Test
  public void andSoAreTheTwoSidesOfOneAxis() {
    assertRangeX(DIAGONAL + ", PlotRangePadding->{{0, 0.5}, Automatic}]", 0, 1.5);
    assertRangeY(DIAGONAL + ", PlotRangePadding->{{0, 0.5}, Automatic}]", -AUTOMATIC,
        1 + AUTOMATIC);
  }

  @Test
  public void aSettingThatCannotBeReadLeavesTheDefaultInPlace() {
    assertRange(DIAGONAL + ", PlotRangePadding->\"nonsense\"]", -AUTOMATIC, 1 + AUTOMATIC);
  }

  // ------------------------------------------------------------------ against PlotRange

  @Test
  public void aRangeAskedForByNameIsDrawnAsItWasAskedFor() {
    assertRange(DIAGONAL + ", PlotRange->{{0,1},{0,1}}]", 0, 1);
  }

  @Test
  public void onlyThePinnedAxisLosesItsRoom() {
    // a bare pair is the y range alone
    assertRangeY(DIAGONAL + ", PlotRange->{0,1}]", 0, 1);
    assertRangeX(DIAGONAL + ", PlotRange->{0,1}]", -AUTOMATIC, 1 + AUTOMATIC);
  }

  @Test
  public void allAndAutomaticAreNotRangesTheUserPinned() {
    assertRange(DIAGONAL + ", PlotRange->All]", -AUTOMATIC, 1 + AUTOMATIC);
    assertRange(DIAGONAL + ", PlotRange->Automatic]", -AUTOMATIC, 1 + AUTOMATIC);
  }

  @Test
  public void paddingAskedForByNameIsAddedToAPinnedRangeToo() {
    assertRange(DIAGONAL + ", PlotRange->{{0,1},{0,1}}, PlotRangePadding->0.5]", -0.5, 1.5);
  }

  // ------------------------------------------------------------------ logarithmic axes

  @Test
  public void aLengthOnALogarithmicAxisIsStillALength() {
    // the data runs from 1 to 100; a hundred units above it reaches 200, not a hundred decades
    Viewport2D viewport = viewport("Graphics[Line[{{1,1},{2,100}}],"
        + " ScalingFunctions->{\"None\",\"Log10\"}, PlotRangePadding->{None, {None, 100}}]");
    assertEquals(1.0, viewport.rawMinY, 1e-9, "the bottom of the data is untouched");
    assertEquals(200.0, viewport.rawMaxY, 1e-9, "and a hundred units above it is 200");
  }

  @Test
  public void aFractionOnALogarithmicAxisIsAFractionOfTheDecadesDrawn() {
    Viewport2D viewport = viewport("Graphics[Line[{{1,1},{2,100}}],"
        + " ScalingFunctions->{\"None\",\"Log10\"}, PlotRangePadding->{None, {Scaled[0], Scaled[0.25]}}]");
    // two decades of data, a quarter of the finished plot on top: 2/0.75 decades in all
    assertEquals(Math.pow(10.0, 2.0 / 0.75), viewport.rawMaxY, 1e-6);
  }

  @Test
  public void paddingCannotCarryALogarithmicAxisDownToZero() {
    Viewport2D viewport = viewport("Graphics[Line[{{1,1},{2,100}}],"
        + " ScalingFunctions->{\"None\",\"Log10\"}, PlotRangePadding->{None, {5, 0}}]");
    assertTrue(viewport.rawMinY > 0, "a logarithm of nought would lose the whole picture");
    assertTrue(Double.isFinite(viewport.minY), "and the scaled range stays a number");
  }

  // ------------------------------------------------------------------ laying out twice

  @Test
  public void layingOutTheSamePictureTwiceGivesTheSameRange() {
    // the renderer configures the viewport once to estimate the tick labels and again with them,
    // so the padding has to be read from the options rather than accumulated into them
    IExpr graphic = evaluator.eval(DIAGONAL + ", PlotRangePadding->0.25]");
    IAST graphicsExpr = (IAST) graphic;
    GraphicsOptions2D options = new GraphicsOptions2D();
    PrimitiveCollector collector = new PrimitiveCollector(options.imageSize[0]);
    options.parse(graphicsExpr, collector);
    collector.collect(graphicsExpr.arg1(), options.globalStyle.clone());
    Bounds2D bounds = new Bounds2D();
    for (Prim2D primitive : collector.primitives()) {
      primitive.accumulate(bounds);
    }

    Viewport2D viewport = new Viewport2D(options);
    viewport.configure(bounds, new double[] {0, 0, 0, 0});
    double first = viewport.maxX;
    viewport.configure(bounds, new double[] {10, 10, 10, 10});
    assertEquals(first, viewport.maxX, TOLERANCE, "the second pass must not pad what the first did");
  }

  /** The width and height the root {@code <svg>} settled on. */
  private static double[] canvas(String input) {
    org.matheclipse.core.interfaces.IExpr result = evaluator.eval(input);
    String svg = new SVGGraphics(600, 400)
        .toSVG((org.matheclipse.core.interfaces.IAST) result, true);
    java.util.regex.Matcher m = java.util.regex.Pattern
        .compile("<svg[^>]*width=\"([0-9.]+)\"[^>]*height=\"([0-9.]+)\"").matcher(svg);
    assertTrue(m.find(), () -> "no sized root for " + input + ": " + svg);
    return new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))};
  }

  /**
   * A small framed picture keeps the shape it asked for.
   *
   * <p>
   * The margins a frame and its numbers want do not shrink with the picture, and at
   * {@code ImageSize -> 70} they wanted more than the whole width. The drawing width came out
   * negative, the height was left at whatever the caller had passed in, and a grid cell asked for
   * at 70 pixels came back 70 by 400 - a sliver rather than a plot.
   */
  @Test
  public void testASmallFramedPictureIsNotLeftAtTheCallersHeight() {
    double[] framed = canvas("ParametricPlot({Sin(t),Sin(2*t)},{t,0,2*Pi},ImageSize->70,Frame->True)");
    assertEquals(70.0, framed[0], TOLERANCE, "the width was asked for outright");
    assertTrue(framed[1] < 200, "the height was left at the caller's default: " + framed[1]);
    // and it stays in proportion with the unframed picture rather than collapsing
    double[] plain = canvas("ParametricPlot({Sin(t),Sin(2*t)},{t,0,2*Pi},ImageSize->70)");
    assertTrue(Math.abs(framed[1] - plain[1]) < 60,
        "framed " + framed[1] + " is far from unframed " + plain[1]);
  }

  /**
   * With the numbers switched off, the drawing gets the room the margins were holding for them.
   *
   * <p>
   * The margins asked for room whenever there was an axis or a frame, without checking whether
   * anything was written in them. A cell of a grid of framed plots therefore drew at a fraction of
   * the size it had been given: at {@code ImageSize -> 70} roughly two thirds of the width went to
   * margins holding nothing. With {@code FrameTicks -> None} the picture is now square, which is
   * what its data asks for, and fills what it was given.
   */
  @Test
  public void testTicklessMarginsDoNotStealTheDrawingArea() {
    double[] c = canvas("ParametricPlot({Sin(t),Sin(2*t)},{t,0,2*Pi},ImageSize->70,"
        + "Frame->True,FrameTicks->None,Axes->False)");
    assertEquals(70.0, c[0], TOLERANCE);
    // the data spans the same range on both axes, so a picture that fills its box is square
    assertEquals(70.0, c[1], 1.0, "the drawing did not fill the height it was given");
  }

  /** The drawing area of a framed picture: x, y, width, height of the frame rectangle. */
  private static double[] frameRect(String input) {
    org.matheclipse.core.interfaces.IExpr result = evaluator.eval(input);
    String svg = new SVGGraphics(600, 400)
        .toSVG((org.matheclipse.core.interfaces.IAST) result, true);
    java.util.regex.Matcher m = java.util.regex.Pattern
        .compile("<rect x=\"([0-9.]+)\" y=\"([0-9.]+)\" width=\"([0-9.]+)\" height=\"([0-9.]+)\"")
        .matcher(svg);
    assertTrue(m.find(), () -> "no frame rectangle in " + input + ": " + svg);
    return new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
        Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4))};
  }

  /**
   * A picture with nothing written around it gives the whole canvas to the drawing.
   *
   * <p>
   * Two separate strips used to be reserved for text that was never written: one for tick numbers
   * an edge had switched off, and one for an axis label, because every plot emits
   * {@code AxesLabel -> None} and the reservation only tested the option for null. Together they
   * took 20 pixels of each 70 pixel cell on two sides, so the drawing had two thirds of the width
   * it had been given.
   */
  @Test
  public void testAnUnlabelledFramedPictureFillsItsCanvas() {
    double[] f = frameRect("ParametricPlot({Sin(t),Sin(2*t)},{t,0,2*Pi},ImageSize->70,"
        + "Frame->True,FrameTicks->None,Axes->False)");
    assertEquals(2.0, f[0], 0.5, "left margin");
    assertEquals(2.0, f[1], 0.5, "top margin");
    assertEquals(66.0, f[2], 1.0, "the frame does not span the width");
    assertEquals(66.0, f[3], 1.0, "the frame does not span the height");
  }

  /** A picture that does write its numbers still keeps room for them. */
  @Test
  public void testALabelledFramedPictureKeepsItsMargins() {
    double[] f = frameRect("Plot(Sin(x),{x,0,6},Frame->True)");
    assertTrue(f[0] >= 24, "the tick numbers lost their room: left=" + f[0]);
    assertTrue(f[2] < 600 - 24, "the frame ran into the numbers");
  }

  /** A picture with room for its margins is sized exactly as before. */
  @Test
  public void testAFullSizePictureIsUnaffectedByTheMarginClamp() {
    double[] framed = canvas("Plot(Sin(x),{x,0,6},Frame->True)");
    assertEquals(600.0, framed[0], TOLERANCE);
    // 0.6 * 600 is far more than a frame asks for, so the clamp cannot have fired
    assertTrue(framed[1] > 300 && framed[1] < 450, "height changed: " + framed[1]);
  }
}
