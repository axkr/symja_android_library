package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>GraphicsRow</code>, <code>GraphicsColumn</code> and <code>GraphicsGrid</code>.
 *
 * <p>
 * The assertions are structural: one viewport per drawn cell, at the offsets and sizes the options
 * ask for, and the right number of dividers and background rectangles. Nothing here compares whole
 * SVG strings, so the tests survive a change to what a cell draws inside itself.
 */
public class GraphicsLayoutTest {

  /** One cell viewport. */
  private static final Pattern CELL =
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

  /** The {@code x, y, width, height} of every cell viewport, in drawing order. */
  private static List<double[]> cells(String svg) {
    List<double[]> found = new ArrayList<>();
    Matcher m = CELL.matcher(svg);
    while (m.find()) {
      found.add(new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
          Double.parseDouble(m.group(3)), Double.parseDouble(m.group(4))});
    }
    return found;
  }

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

  private static final String DISK = "Graphics[Disk[]]";
  private static final String RECT = "Graphics[Rectangle[]]";

  // ------------------------------------------------------------- shape

  @Test
  public void testARowPlacesItsCellsSideBySide() {
    List<double[]> found = cells(svg("GraphicsRow[{" + DISK + ", " + RECT + "}]"));
    assertEquals(2, found.size());
    assertEquals(0.0, found.get(0)[1], 0.5, "both cells sit on the same line");
    assertEquals(found.get(0)[1], found.get(1)[1], 0.5);
    assertTrue(found.get(1)[0] > found.get(0)[0], "the second cell is to the right of the first");
  }

  @Test
  public void testAColumnStacksItsCells() {
    List<double[]> found = cells(svg("GraphicsColumn[{" + DISK + ", " + RECT + "}]"));
    assertEquals(2, found.size());
    assertEquals(found.get(0)[0], found.get(1)[0], 0.5, "both cells share one left edge");
    assertTrue(found.get(1)[1] > found.get(0)[1], "the second cell is below the first");
  }

  @Test
  public void testAGridFillsRowsThenColumns() {
    List<double[]> found = cells(svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", "
        + DISK + "}}]"));
    assertEquals(4, found.size());
    assertEquals(found.get(0)[1], found.get(1)[1], 0.5, "the first two cells share a row");
    assertEquals(found.get(0)[0], found.get(2)[0], 0.5, "the first and third share a column");
    assertTrue(found.get(2)[1] > found.get(0)[1], "the second row is lower");
  }

  @Test
  public void testABareListOfGraphicsIsARow() {
    List<double[]> found = cells(svg("{" + DISK + ", " + RECT + "}"));
    assertEquals(2, found.size());
    assertEquals(found.get(0)[1], found.get(1)[1], 0.5);
  }

  @Test
  public void testARowBringsItsPicturesToOneHeight() {
    // a wide picture and a square one still line up, because the row scales rather than crops
    List<double[]> found = cells(
        svg("GraphicsRow[{Graphics[Rectangle[{0,0},{4,1}]], " + DISK + "}]"));
    assertEquals(2, found.size());
    assertEquals(found.get(0)[3], found.get(1)[3], 1.0, "the cells share one height");
  }

  @Test
  public void testAColumnBringsItsPicturesToOneWidth() {
    List<double[]> found = cells(
        svg("GraphicsColumn[{Graphics[Rectangle[{0,0},{4,1}]], " + DISK + "}]"));
    assertEquals(2, found.size());
    assertEquals(found.get(0)[2], found.get(1)[2], 1.0, "the cells share one width");
  }

  // ------------------------------------------------------------- size

  @Test
  public void testTheCellsAndGapsFillTheCanvasWidth() {
    String out = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", " + DISK + "}}]");
    List<double[]> found = cells(out);
    double width = canvas(out)[0];
    double rightmost = 0;
    for (double[] cell : found) {
      rightmost = Math.max(rightmost, cell[0] + cell[2]);
    }
    assertEquals(width, rightmost, 1.0, "the drawing reaches the right edge of the canvas");
  }

  @Test
  public void testTheLayoutStaysInsideTheWidthBudget() {
    // the servlet renders at 600 wide, and four plots must be scaled down to fit rather than
    // running off the side of the page
    String out = svg("GraphicsRow[{" + DISK + ", " + RECT + ", " + DISK + ", " + RECT + "}]");
    assertTrue(canvas(out)[0] <= 600.5, "canvas was " + canvas(out)[0]);
  }

  @Test
  public void testAnExplicitImageSizeSetsTheCanvas() {
    String out = svg("GraphicsRow[{" + DISK + ", " + RECT + "}, ImageSize -> 300]");
    assertEquals(300.0, canvas(out)[0], 1.0);
  }

  @Test
  public void testAnExplicitImageSizePairSetsBothAxes() {
    String out = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}}, ImageSize -> {320, 120}]");
    double[] size = canvas(out);
    assertEquals(320.0, size[0], 1.0);
    assertEquals(120.0, size[1], 1.0);
  }

  @Test
  public void testImageMarginsInsetTheDrawing() {
    List<double[]> found =
        cells(svg("GraphicsRow[{" + DISK + ", " + RECT + "}, ImageMargins -> 10]"));
    assertEquals(10.0, found.get(0)[0], 0.5, "the first cell starts one margin in");
  }

  @Test
  public void testItemAspectRatioSquaresTheCellBoxes() {
    String out = svg("GraphicsGrid[{{Graphics[Rectangle[{0,0},{4,1}]], " + DISK
        + "}}, ItemAspectRatio -> 1]");
    double[] size = canvas(out);
    // two square boxes side by side, so the canvas is about twice as wide as it is tall
    assertEquals(2.0, size[0] / size[1], 0.35, "canvas was " + size[0] + "x" + size[1]);
  }

  // ------------------------------------------------------------- spacings

  @Test
  public void testSpacingsZeroButtsTheCellsTogether() {
    List<double[]> found =
        cells(svg("GraphicsRow[{" + DISK + ", " + RECT + "}, Spacings -> 0]"));
    assertEquals(found.get(0)[0] + found.get(0)[2], found.get(1)[0], 1.0,
        "the second cell starts where the first ends");
  }

  @Test
  public void testAPositionalSpacingWidensTheGap() {
    List<double[]> tight = cells(svg("GraphicsRow[{" + DISK + ", " + RECT + "}, 0]"));
    List<double[]> loose = cells(svg("GraphicsRow[{" + DISK + ", " + RECT + "}, 40]"));
    double tightGap = tight.get(1)[0] - (tight.get(0)[0] + tight.get(0)[2]);
    double looseGap = loose.get(1)[0] - (loose.get(0)[0] + loose.get(0)[2]);
    assertTrue(looseGap > tightGap + 10, "gap went from " + tightGap + " to " + looseGap);
  }

  @Test
  public void testASpacingInPointsBecomesPixels() {
    // 30 printer's points is 40 pixels at 96 dpi. The budget is set wide enough that the layout
    // is not scaled down to fit, which would scale the gap with it.
    List<double[]> found = cells(
        svg("GraphicsRow[{" + DISK + ", " + RECT + "}, Spacings -> 30, ImageSize -> 800]"));
    double gap = found.get(1)[0] - (found.get(0)[0] + found.get(0)[2]);
    assertEquals(40.0, gap, 2.0, "gap was " + gap);
  }

  @Test
  public void testScaledSpacingGrowsWithTheCells() {
    List<double[]> small = cells(svg("GraphicsRow[{" + DISK + ", " + RECT + "}, Scaled(0.05)]"));
    List<double[]> large = cells(svg("GraphicsRow[{" + DISK + ", " + RECT + "}, Scaled(0.4)]"));
    double smallGap = small.get(1)[0] - (small.get(0)[0] + small.get(0)[2]);
    double largeGap = large.get(1)[0] - (large.get(0)[0] + large.get(0)[2]);
    assertTrue(largeGap > smallGap * 3, smallGap + " then " + largeGap);
  }

  // ------------------------------------------------------------- frame and dividers

  @Test
  public void testNoFrameDrawsNoLines() {
    assertEquals(0, count(svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}}]"), "<line"));
  }

  /**
   * <code>Frame -> True</code> is the perimeter and nothing else. It used to rule every interior
   * boundary as well, which is what <code>Frame -> All</code> means.
   */
  @Test
  public void testFrameTrueDrawsThePerimeterOnly() {
    String out = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", " + DISK
        + "}}, Frame -> True]");
    assertEquals(4, count(out, "<line"), "a frame is four lines");
  }

  @Test
  public void testFrameAllRulesEveryBoundary() {
    String out = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", " + DISK
        + "}}, Frame -> All]");
    // three column positions and three row positions on a two by two grid
    assertEquals(6, count(out, "<line"));
  }

  @Test
  public void testDividersCenterRulesOnlyTheInterior() {
    String out = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", " + DISK
        + "}}, Dividers -> Center]");
    assertEquals(2, count(out, "<line"), "one interior line each way");
  }

  @Test
  public void testADividerCarriesItsColour() {
    String out = svg("GraphicsRow[{" + DISK + ", " + RECT + "}, Dividers -> {{2 -> Red}, None}]");
    assertEquals(1, count(out, "<line"));
    assertTrue(out.contains("stroke=\"" + org.matheclipse.core.graphics.svg.ColorUtil
        .css(java.awt.Color.RED) + "\""), "the divider is not red: " + out);
  }

  @Test
  public void testADividerCarriesItsThickness() {
    String out = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}}, Dividers -> Directive(Thick)]");
    Matcher m = Pattern.compile("stroke-width=\"([0-9.]+)\"").matcher(out);
    assertTrue(m.find(), "no stroke width on the divider");
    assertTrue(Double.parseDouble(m.group(1)) > 1.0, "Thick did not thicken the line");
  }

  /** The top of the first vertical divider, which is where the column line begins. */
  private static double verticalDividerTop(String svg) {
    Matcher m = Pattern.compile(
        "<line x1=\"([0-9.]+)\" y1=\"([0-9.]+)\" x2=\"([0-9.]+)\" y2=\"([0-9.]+)\"")
        .matcher(svg);
    while (m.find()) {
      if (m.group(1).equals(m.group(3))) {
        return Double.parseDouble(m.group(2));
      }
    }
    return Double.NaN;
  }

  @Test
  public void testADividerDoesNotCutThroughASpan() {
    String spanning = svg("GraphicsGrid[{{" + DISK + ", SpanFromLeft}, {" + RECT + ", " + DISK
        + "}}, Dividers -> Center]");
    String plain = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", " + DISK
        + "}}, Dividers -> Center]");
    // the interior column line starts below the spanning row rather than cutting it in two
    double spanningTop = verticalDividerTop(spanning);
    double plainTop = verticalDividerTop(plain);
    assertFalse(Double.isNaN(spanningTop), "no column divider was drawn at all");
    assertTrue(spanningTop > plainTop + 10,
        "the divider began at " + spanningTop + ", the unspanned one at " + plainTop);
  }

  @Test
  public void testAnUnreadableDividerSpecificationDrawsNothing() {
    assertEquals(0, count(svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}}, Dividers -> foo]"),
        "<line"));
  }

  // ------------------------------------------------------------- background

  @Test
  public void testABackgroundColourPaintsTheCanvas() {
    String out = svg("GraphicsGrid[{{" + DISK + "}}, Background -> LightYellow]");
    assertTrue(count(out, "<rect") >= 1, "no background rectangle");
  }

  @Test
  public void testARowBackgroundStripesTheGrid() {
    String plain = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", " + DISK
        + "}}]");
    String striped = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {" + RECT + ", " + DISK
        + "}}, Background -> {None, {{LightGray, White}}}]");
    assertEquals(count(plain, "<rect") + 4, count(striped, "<rect"),
        "one background rectangle per cell was expected");
  }

  @Test
  public void testACellBackgroundPaintsOneCellOnly() {
    String out = svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}}, "
        + "Background -> {None, None, {{1,1} -> Red}}]");
    assertTrue(out.contains("fill=\"" + org.matheclipse.core.graphics.svg.ColorUtil
        .css(java.awt.Color.RED) + "\""), "the cell was not painted red");
  }

  // ------------------------------------------------------------- spanning

  @Test
  public void testASpanningCellCoversItsColumns() {
    List<double[]> found = cells(svg("GraphicsGrid[{{" + DISK + ", SpanFromLeft}, {" + RECT + ", "
        + DISK + "}}]"));
    assertEquals(3, found.size(), "the covered position draws nothing of its own");
    double spanning = found.get(0)[2];
    double single = found.get(1)[2];
    assertTrue(spanning > single * 1.5, "the spanning cell is " + spanning + " wide, not " + single);
  }

  @Test
  public void testASpanFromAboveCoversItsRows() {
    List<double[]> found = cells(svg("GraphicsGrid[{{" + DISK + ", " + RECT + "}, {SpanFromAbove, "
        + DISK + "}}]"));
    assertEquals(3, found.size());
  }

  @Test
  public void testASpanWithNothingToExtendLosesOnlyItself() {
    List<double[]> found = cells(svg("GraphicsGrid[{{SpanFromLeft, " + DISK + "}}]"));
    assertEquals(1, found.size(), "the picture beside the stray marker still draws");
  }

  // ------------------------------------------------------------- cell contents

  @Test
  public void testANonGraphicCellIsSetAsText() {
    String out = svg("GraphicsRow[{" + DISK + ", \"a caption\"}]");
    assertEquals(2, cells(out).size(), "the caption takes a cell of its own");
    assertTrue(out.contains("a caption"), "the caption was not drawn");
  }

  @Test
  public void testATextCellIsNotStretchedToTheHeightOfThePictures() {
    // bringing a caption to the height of the plot beside it would set it in enormous letters
    List<double[]> found = cells(svg("GraphicsRow[{" + DISK + ", \"x\"}]"));
    assertTrue(found.get(1)[3] < found.get(0)[3], "the caption was scaled up with the picture");
  }

  @Test
  public void testAStyledTextCellKeepsItsStyle() {
    String out = svg("GraphicsRow[{" + DISK + ", Style(\"big\", Red, 24)}]");
    assertTrue(out.contains("font-size=\"24"), "the font size was ignored: " + out);
    assertTrue(out.contains("fill=\"" + org.matheclipse.core.graphics.svg.ColorUtil
        .css(java.awt.Color.RED) + "\""), "the colour was ignored");
  }

  @Test
  public void testTextIsEscaped() {
    String out = svg("GraphicsRow[{" + DISK + ", \"a & b < c\"}]");
    assertTrue(out.contains("&amp;") && out.contains("&lt;"), "the text was not escaped: " + out);
  }

  @Test
  public void testAGraphics3DCellIsDrawnRatherThanDropped() {
    String out = svg("GraphicsRow[{" + DISK + ", Graphics3D[Sphere[]]}]");
    assertEquals(2, cells(out).size(), "the three dimensional cell was dropped");
  }

  @Test
  public void testALegendedPictureIsStillAPicture() {
    String out = svg("GraphicsRow[{" + DISK + ", Legended[" + RECT + ", \"r\"]}]");
    assertEquals(2, cells(out).size());
  }

  @Test
  public void testANestedLayoutIsLaidOutInsideItsCell() {
    String out = svg("GraphicsGrid[{{GraphicsRow[{" + DISK + ", " + RECT + "}], " + DISK + "}}]");
    // the outer two cells, plus the two the nested row contributes
    assertEquals(4, cells(out).size());
  }

  @Test
  public void testAnOverlayNestsInsideARow() {
    String out = svg("GraphicsRow[{Overlay[{" + DISK + ", Graphics[Circle[]]}], " + RECT + "}]");
    assertEquals(4, cells(out).size());
  }

  // ------------------------------------------------------------- alignment

  @Test
  public void testAColumnAlignmentArgumentMovesTheCells() {
    List<double[]> left = cells(
        svg("GraphicsColumn[{Graphics[Rectangle[{0,0},{4,1}]], " + DISK + "}, Left]"));
    List<double[]> right = cells(
        svg("GraphicsColumn[{Graphics[Rectangle[{0,0},{4,1}]], " + DISK + "}, Right]"));
    // the two forms differ only in where the spare room around the narrower cell goes
    assertTrue(left.get(1)[0] <= right.get(1)[0], "Left put the cell further right than Right did");
  }

  @Test
  public void testAlignmentPlacesASmallerCellInItsBox() {
    String grid = "GraphicsGrid[{{Graphics[Rectangle[{0,0},{1,4}]], " + DISK + "}}, Alignment -> ";
    List<double[]> top = cells(svg(grid + "{Center, Top}]"));
    List<double[]> bottom = cells(svg(grid + "{Center, Bottom}]"));
    assertTrue(bottom.get(1)[1] > top.get(1)[1], "Bottom did not move the cell down");
  }

  // ------------------------------------------------------------- degenerate input

  /**
   * A layout with nothing in it is an empty canvas, not an empty result. The servlet shows
   * whatever comes back, so an empty string would read as though the input had done nothing.
   */
  @Test
  public void testAnEmptyLayoutIsAnEmptyCanvas() {
    String out = svg("GraphicsGrid[{}]");
    assertTrue(out.startsWith("<svg"), out);
    assertEquals(0, cells(out).size());
  }

  @Test
  public void testRaggedRowsAreLaidOutWithoutTheMissingCells() {
    List<double[]> found =
        cells(svg("GraphicsGrid[{{" + DISK + "}, {" + RECT + ", " + DISK + "}}]"));
    assertEquals(3, found.size());
  }

  @Test
  public void testANullCellIsSkipped() {
    String out = svg("GraphicsGrid[{{" + DISK + ", None}}]");
    assertEquals(1, cells(out).size());
  }

  @Test
  public void testEveryLayoutIsWellFormedXml() throws Exception {
    String[] inputs = {"GraphicsGrid[{{" + DISK + ", " + RECT + "}}, Frame -> All]",
        "GraphicsRow[{" + DISK + ", \"a & b\"}, Dividers -> Red]",
        "GraphicsColumn[{" + DISK + "}, Left, 10]",
        "GraphicsGrid[{{" + DISK + ", SpanFromLeft}}, Background -> {None, {LightGray}}]"};
    javax.xml.parsers.DocumentBuilderFactory factory =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    for (String input : inputs) {
      String out = svg(input);
      factory.newDocumentBuilder()
          .parse(new org.xml.sax.InputSource(new java.io.StringReader(out)));
    }
  }
}
