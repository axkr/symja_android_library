package org.matheclipse.core.graphics.svg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The option grammar {@code Grid} and the graphics layouts share.
 *
 * <p>
 * These assertions are on the resolved specification rather than on any rendering, so they survive
 * changes to how a grid is drawn. What they pin down is the cyclic list grammar, which is the part
 * every one of the six positional options depends on and the part that is easiest to get subtly
 * wrong.
 */
public class LayoutSpecTest {

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

  private static IExpr expr(String input) {
    return evaluator.eval(input);
  }

  /** The specification of a {@code Grid} written in full. */
  private static LayoutSpec grid(String input) {
    IExpr result = expr(input);
    assertTrue(result instanceof IAST, () -> input + " is not an AST but " + result);
    IAST ast = (IAST) result;
    assertTrue(ast.arg1().isList(), () -> input + " has no list of rows");
    return LayoutSpec.forGrid(ast, (IAST) ast.arg1(), 2, LayoutSpec.Units.EMS);
  }

  /** The resolved values of a positional specification, as their printed forms. */
  private static String[] spread(String spec, int n) {
    IExpr[] values = LayoutSpec.positions(expr(spec), n, S.Automatic);
    String[] out = new String[n];
    for (int i = 0; i < n; i++) {
      out[i] = values[i] == null ? "null" : values[i].toString();
    }
    return out;
  }

  private static void assertSpread(String spec, int n, String... expected) {
    assertArrayEqualsAsStrings(expected, spread(spec, n), spec);
  }

  private static void assertArrayEqualsAsStrings(String[] expected, String[] actual, String what) {
    assertEquals(String.join(",", expected), String.join(",", actual), what);
  }

  // ------------------------------------------------------------- the cyclic list grammar

  @Test
  public void testAScalarAppliesEverywhere() {
    assertSpread("True", 3, "True", "True", "True");
    assertSpread("Red", 2, "RGBColor(1,0,0)", "RGBColor(1,0,0)");
  }

  @Test
  public void testAFlatListIsUsedInOrderThenRunsOut() {
    assertSpread("{True, False}", 4, "True", "False", "Automatic", "Automatic");
    assertSpread("{1, 2, 3}", 2, "1", "2");
  }

  @Test
  public void testANestedBlockCycles() {
    assertSpread("{{True}}", 3, "True", "True", "True");
    assertSpread("{{True, False}}", 5, "True", "False", "True", "False", "True");
    assertSpread("{{1, 2, 3}}", 4, "1", "2", "3", "1");
  }

  @Test
  public void testAPrefixPinsTheStartAndASuffixTheEnd() {
    assertSpread("{False, {True}}", 4, "False", "True", "True", "True");
    assertSpread("{1, {2}, 3}", 5, "1", "2", "2", "2", "3");
    // the two ends win when there is not enough room for the cycle
    assertSpread("{1, {9}, 3}", 2, "1", "3");
  }

  @Test
  public void testRulesNameIndividualPositions() {
    assertSpread("{2 -> True}", 3, "Automatic", "True", "Automatic");
    // a negative index counts back from the end
    assertSpread("{-1 -> True}", 3, "Automatic", "Automatic", "True");
    assertSpread("{-2 -> True}", 3, "Automatic", "True", "Automatic");
    // out of range costs only itself
    assertSpread("{7 -> True}", 2, "Automatic", "Automatic");
  }

  @Test
  public void testRulesOverrideTheSpecificationTheyFollow() {
    assertSpread("{{False}, {2 -> True}}", 3, "False", "True", "False");
  }

  @Test
  public void testAnEmptyOrMalformedSpecificationFallsBackToTheDefault() {
    assertSpread("{}", 2, "Automatic", "Automatic");
    assertSpread("Automatic", 2, "Automatic", "Automatic");
    assertEquals(0, LayoutSpec.positions(expr("True"), 0, S.Automatic).length);
  }

  // ------------------------------------------------------------- alignment

  @Test
  public void testANamedAlignmentOnlyMovesItsOwnAxis() {
    assertEquals(0.0, LayoutSpec.alignFraction(expr("Left"), 0));
    assertEquals(1.0, LayoutSpec.alignFraction(expr("Right"), 0));
    assertTrue(Double.isNaN(LayoutSpec.alignFraction(expr("Left"), 1)));
    // zero is the top, because the vertical axis runs downwards as it does in SVG
    assertEquals(0.0, LayoutSpec.alignFraction(expr("Top"), 1));
    assertEquals(1.0, LayoutSpec.alignFraction(expr("Bottom"), 1));
    assertTrue(Double.isNaN(LayoutSpec.alignFraction(expr("Top"), 0)));
    assertEquals(0.5, LayoutSpec.alignFraction(expr("Center"), 0));
    assertEquals(0.5, LayoutSpec.alignFraction(expr("Center"), 1));
  }

  @Test
  public void testANumericAlignmentIsClamped() {
    assertEquals(0.25, LayoutSpec.alignFraction(expr("0.25"), 0));
    assertEquals(1.0, LayoutSpec.alignFraction(expr("5"), 0));
    assertEquals(0.0, LayoutSpec.alignFraction(expr("-3"), 0));
  }

  @Test
  public void testPerColumnAlignment() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, Alignment -> {{Right, Left}})");
    assertEquals(1.0, spec.alignHAt(0, 0));
    assertEquals(0.0, spec.alignHAt(0, 1));
    // unset positions stay centred
    LayoutSpec plain = grid("Grid({{a, b}})");
    assertEquals(0.5, plain.alignHAt(0, 0));
  }

  @Test
  public void testACellRuleBeatsTheColumnAlignment() {
    LayoutSpec spec =
        grid("Grid({{a, b}, {c, d}}, Alignment -> {Center, Automatic, {{1,1} -> Left}})");
    assertEquals(0.0, spec.alignHAt(0, 0));
    assertEquals(0.5, spec.alignHAt(1, 1));
  }

  // ------------------------------------------------------------- dividers and frame

  @Test
  public void testDividersAllRulesEveryPosition() {
    LayoutSpec spec = grid("Grid({{a, b, c}, {d, e, f}}, Dividers -> All)");
    for (int i = 0; i <= spec.cols; i++) {
      assertNotNull(spec.colDividers[i], "column position " + i);
    }
    for (int i = 0; i <= spec.rows; i++) {
      assertNotNull(spec.rowDividers[i], "row position " + i);
    }
  }

  @Test
  public void testDividersCenterRulesOnlyTheInterior() {
    LayoutSpec spec = grid("Grid({{a, b, c}, {d, e, f}}, Dividers -> Center)");
    assertNull(spec.colDividers[0]);
    assertNotNull(spec.colDividers[1]);
    assertNotNull(spec.colDividers[2]);
    assertNull(spec.colDividers[spec.cols]);
    assertNull(spec.rowDividers[0]);
    assertNotNull(spec.rowDividers[1]);
    assertNull(spec.rowDividers[spec.rows]);
  }

  @Test
  public void testDividersSplitColumnsFromRows() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, Dividers -> {All, False})");
    assertNotNull(spec.colDividers[0]);
    assertNotNull(spec.colDividers[1]);
    for (int i = 0; i <= spec.rows; i++) {
      assertNull(spec.rowDividers[i], "row position " + i);
    }
  }

  /**
   * The four divider names keep their meaning inside a {@code {columns, rows}} pair. Read through
   * the positional grammar instead, {@code Center} would mean "the symbol Center at every
   * position", which draws a full set of lines and looks as though the word was ignored.
   */
  @Test
  public void testAKeywordKeepsItsMeaningInsideAPair() {
    LayoutSpec spec = grid("Grid({{a, b, c}, {d, e, f}}, Dividers -> {All, Center})");
    assertNotNull(spec.colDividers[0], "All rules the exterior too");
    assertNotNull(spec.colDividers[1]);
    assertNull(spec.rowDividers[0], "Center leaves the exterior alone");
    assertNotNull(spec.rowDividers[1]);
    assertNull(spec.rowDividers[spec.rows]);
  }

  @Test
  public void testAnUndrawableValueAtOnePositionDrawsNothingThere() {
    LayoutSpec spec = grid("Grid({{a, b, c}}, Dividers -> {{2 -> foo}, None})");
    assertNull(spec.colDividers[1]);
  }

  @Test
  public void testADividerCarriesItsOwnDirective() {
    LayoutSpec spec = grid("Grid({{a, b, c}}, Dividers -> {{2 -> Red}, None})");
    assertNull(spec.colDividers[0]);
    assertEquals("RGBColor(1,0,0)", spec.colDividers[1].toString());
    assertNull(spec.colDividers[2]);
  }

  @Test
  public void testFrameTrueIsThePerimeterOnly() {
    LayoutSpec spec = grid("Grid({{a, b, c}, {d, e, f}}, Frame -> True)");
    assertNotNull(spec.colDividers[0]);
    assertNotNull(spec.colDividers[spec.cols]);
    assertNull(spec.colDividers[1], "Frame -> True must not draw an interior line");
    assertNotNull(spec.rowDividers[0]);
    assertNotNull(spec.rowDividers[spec.rows]);
    assertNull(spec.rowDividers[1]);
  }

  @Test
  public void testFrameAllRulesEveryCell() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, Frame -> All)");
    assertNotNull(spec.colDividers[1]);
    assertNotNull(spec.rowDividers[1]);
  }

  @Test
  public void testEmptyDividersDefersToTheFrame() {
    LayoutSpec spec = grid("Grid({{a, b}}, Frame -> True, Dividers -> {})");
    assertNotNull(spec.colDividers[0]);
    assertNull(spec.colDividers[1]);
  }

  @Test
  public void testADividerNeverCutsThroughASpan() {
    LayoutSpec spec = grid("Grid({{a, SpanFromLeft, b}, {c, d, e}}, Dividers -> All)");
    assertTrue(spec.columnDividerCrosses(1, 1), "an unspanned row is cut normally");
    assertTrue(!spec.columnDividerCrosses(1, 0), "the spanning row must not be cut");
    assertTrue(spec.columnDividerCrosses(2, 0), "the position past the span is cut again");
  }

  // ------------------------------------------------------------- spans and ragged rows

  @Test
  public void testSpanFromLeftWidensItsOrigin() {
    LayoutSpec spec = grid("Grid({{a, SpanFromLeft}, {b, c}})");
    assertEquals(2, spec.cells[0][0].colSpan);
    assertTrue(spec.cells[0][1].covered);
    assertEquals(1, spec.cells[1][0].colSpan);
  }

  @Test
  public void testSpanFromAboveDeepensItsOrigin() {
    LayoutSpec spec = grid("Grid({{a, b}, {SpanFromAbove, c}})");
    assertEquals(2, spec.cells[0][0].rowSpan);
    assertTrue(spec.cells[1][0].covered);
  }

  @Test
  public void testAChainOfSpansReachesBackToOneOrigin() {
    LayoutSpec spec = grid("Grid({{a, SpanFromLeft, SpanFromLeft}})");
    assertEquals(3, spec.cells[0][0].colSpan);
    assertTrue(spec.cells[0][1].covered);
    assertTrue(spec.cells[0][2].covered);
  }

  @Test
  public void testASpanWithNoOriginIsAnEmptyCell() {
    // nothing to the left to widen: the marker costs itself and nothing else
    LayoutSpec spec = grid("Grid({{SpanFromLeft, a}})");
    assertTrue(spec.cells[0][0].isEmpty());
    assertEquals(1, spec.cells[0][0].colSpan);
    assertEquals("a", spec.cells[0][1].content.toString());
  }

  @Test
  public void testRaggedRowsArePaddedToTheLongest() {
    LayoutSpec spec = grid("Grid({{a}, {b, c, d}})");
    assertEquals(2, spec.rows);
    assertEquals(3, spec.cols);
    assertTrue(spec.cells[0][1].isEmpty());
    assertTrue(spec.cells[0][2].isEmpty());
    assertEquals("d", spec.cells[1][2].content.toString());
  }

  // ------------------------------------------------------------- background

  /**
   * A flat list runs out; only a nested one repeats. {@code {col1, col2}} names the first two
   * positions and no more.
   */
  @Test
  public void testAFlatBackgroundListNamesTheFirstColumnsOnly() {
    LayoutSpec spec = grid("Grid({{a, b, c}, {d, e, f}}, Background -> {{Blue, Gray}, None})");
    assertEquals(Color.BLUE, spec.backgroundAt(0, 0));
    assertEquals(spec.backgroundAt(0, 1), spec.backgroundAt(1, 1));
    assertNull(spec.backgroundAt(1, 2), "the third column is past the end of the list");
  }

  @Test
  public void testANestedBackgroundListRepeatsOverColumns() {
    LayoutSpec spec = grid("Grid({{a, b, c}, {d, e, f}}, Background -> {{{Blue, Gray}}, None})");
    assertEquals(Color.BLUE, spec.backgroundAt(0, 0));
    assertEquals(Color.BLUE, spec.backgroundAt(1, 2), "the cycle restarts at the third column");
  }

  @Test
  public void testBackgroundAppliesToRows() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, Background -> {None, {Red, Green}})");
    assertEquals("java.awt.Color[r=255,g=0,b=0]", spec.backgroundAt(0, 0).toString());
    assertEquals(spec.backgroundAt(1, 0), spec.backgroundAt(1, 1));
  }

  @Test
  public void testASingleColourPaintsTheWholeCanvas() {
    LayoutSpec spec = grid("Grid({{a}}, Background -> LightYellow)");
    assertNotNull(spec.background);
    assertNull(spec.backgroundAt(0, 0), "a canvas colour is not a cell colour");
  }

  @Test
  public void testACellRuleBeatsTheRowColour() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, Background -> {None, {Red}, {{1,2} -> Blue}})");
    assertEquals(Color.BLUE, spec.backgroundAt(0, 1));
    assertEquals("java.awt.Color[r=255,g=0,b=0]", spec.backgroundAt(0, 0).toString());
  }

  @Test
  public void testARegionRuleFillsARectangle() {
    LayoutSpec spec = grid(
        "Grid({{a, b, c}, {d, e, f}, {g, h, i}}, Background -> {None, None, {{{1,2},{2,3}} -> Blue}})");
    assertEquals(Color.BLUE, spec.backgroundAt(0, 1));
    assertEquals(Color.BLUE, spec.backgroundAt(1, 2));
    assertNull(spec.backgroundAt(2, 2), "the third row is outside the region");
    assertNull(spec.backgroundAt(0, 0), "the first column is outside the region");
  }

  @Test
  public void testBackgroundNonePaintsNothing() {
    LayoutSpec spec = grid("Grid({{a}}, Background -> None)");
    assertNull(spec.background);
  }

  // ------------------------------------------------------------- spacings

  @Test
  public void testSpacingsDefaultPerUnitSystem() {
    LayoutSpec ems = grid("Grid({{a, b}, {c, d}})");
    assertEquals(0.8, ems.colGaps[0].value);
    assertEquals(0.2, ems.rowGaps[0].value);
    IExpr row = expr("GraphicsGrid({{a, b}, {c, d}})");
    LayoutSpec pts =
        LayoutSpec.forGrid((IAST) row, (IAST) ((IAST) row).arg1(), 2, LayoutSpec.Units.POINTS);
    assertTrue(pts.colGaps[0].scaled);
    assertEquals(0.1, pts.colGaps[0].value);
  }

  @Test
  public void testSpacingsSplitsHorizontalFromVertical() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, Spacings -> {2, 0})");
    assertEquals(2.0, spec.colGaps[0].value);
    assertEquals(0.0, spec.rowGaps[0].value);
  }

  @Test
  public void testScaledSpacingResolvesAgainstTheItem() {
    LayoutSpec spec = grid("Grid({{a, b}}, Spacings -> Scaled(0.25))");
    assertTrue(spec.colGaps[0].scaled);
    assertEquals(25.0, spec.colGaps[0].resolvePixels(100, LayoutSpec.Units.POINTS));
  }

  @Test
  public void testPointsBecomePixelsAtNinetySixDpi() {
    LayoutSpec spec = grid("Grid({{a, b}}, Spacings -> 3)");
    assertEquals(4.0, spec.colGaps[0].resolvePixels(100, LayoutSpec.Units.POINTS));
    assertEquals(3.0, spec.colGaps[0].resolvePixels(100, LayoutSpec.Units.EMS));
  }

  @Test
  public void testANegativeSpacingIsClampedToZero() {
    LayoutSpec spec = grid("Grid({{a, b}}, Spacings -> -5)");
    assertEquals(0.0, spec.colGaps[0].resolvePixels(100, LayoutSpec.Units.POINTS));
  }

  @Test
  public void testSpacingsAtOnePosition() {
    LayoutSpec spec = grid("Grid({{a, b, c}}, Spacings -> {{2 -> 3}, Automatic})");
    assertEquals(3.0, spec.colGaps[1].value);
  }

  // ------------------------------------------------------------- Item and styles

  @Test
  public void testItemIsUnwrappedAndItsOptionsKept() {
    LayoutSpec spec = grid("Grid({{Item(a, Background -> Red), b}, {c, d}})");
    assertEquals("a", spec.cells[0][0].content.toString());
    assertEquals("java.awt.Color[r=255,g=0,b=0]", spec.backgroundAt(0, 0).toString());
    assertNull(spec.backgroundAt(0, 1));
  }

  @Test
  public void testItemAlignmentBeatsTheColumn() {
    LayoutSpec spec = grid("Grid({{Item(a, Alignment -> Right), b}}, Alignment -> {{Left, Left}})");
    assertEquals(1.0, spec.alignHAt(0, 0));
    assertEquals(0.0, spec.alignHAt(0, 1));
  }

  @Test
  public void testItemStyleAppliesByColumnAndRow() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, ItemStyle -> {{Red}, {Bold}})");
    // {Red} names the first column and {Bold} the first row; the row wins where they meet
    assertEquals("Bold", spec.styleAt(0, 0).toString());
    assertEquals("RGBColor(1,0,0)", spec.styleAt(1, 0).toString());
    assertNull(spec.styleAt(1, 1), "neither list reaches the last cell");
  }

  @Test
  public void testANestedItemStyleListReachesEveryColumn() {
    LayoutSpec spec = grid("Grid({{a, b}, {c, d}}, ItemStyle -> {{{Red}}, None})");
    assertEquals("RGBColor(1,0,0)", spec.styleAt(0, 1).toString());
    assertEquals("RGBColor(1,0,0)", spec.styleAt(1, 1).toString());
  }

  @Test
  public void testBaseStyleIsTheFallback() {
    LayoutSpec spec = grid("Grid({{a}}, BaseStyle -> Italic)");
    assertEquals("Italic", spec.styleAt(0, 0).toString());
  }

  // ------------------------------------------------------------- shape helpers

  @Test
  public void testARowAndAColumnAreOneDimensionalGrids() {
    IExpr rowExpr = expr("GraphicsRow({a, b, c})");
    LayoutSpec row = LayoutSpec.forRow((IAST) rowExpr, (IAST) ((IAST) rowExpr).arg1(), 2,
        LayoutSpec.Units.POINTS);
    assertEquals(1, row.rows);
    assertEquals(3, row.cols);
    IExpr colExpr = expr("GraphicsColumn({a, b, c})");
    LayoutSpec column = LayoutSpec.forColumn((IAST) colExpr, (IAST) ((IAST) colExpr).arg1(), 2,
        LayoutSpec.Units.POINTS);
    assertEquals(3, column.rows);
    assertEquals(1, column.cols);
  }

  @Test
  public void testARowThatIsNotAListIsOneCell() {
    LayoutSpec spec = grid("Grid({\"a title\", {1, 2}})");
    assertEquals(2, spec.rows);
    assertEquals(2, spec.cols);
    assertNotNull(spec.cells[0][0].content);
    assertTrue(spec.cells[0][1].isEmpty());
  }

  @Test
  public void testAspectRatioAndMarginsAreRead() {
    LayoutSpec spec = grid("Grid({{a}}, ItemAspectRatio -> 0.5, ImageMargins -> 7)");
    assertEquals(0.5, spec.itemAspectRatio);
    assertEquals(7.0, spec.imageMargins[0]);
    assertEquals(7.0, spec.imageMargins[3]);
  }

  @Test
  public void testAnUnreadableOptionLeavesTheDefaultsAlone() {
    LayoutSpec spec = grid("Grid({{a, b}}, Dividers -> foo, Spacings -> bar)");
    assertNull(spec.colDividers[1]);
    assertEquals(0.8, spec.colGaps[0].value);
  }
}
