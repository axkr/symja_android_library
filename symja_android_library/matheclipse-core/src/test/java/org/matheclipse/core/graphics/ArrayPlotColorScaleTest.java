package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import java.util.TreeSet;
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
 * The grey scale {@code ArrayPlot} paints an array of numbers with.
 *
 * <p>
 * It is not the scale the rest of the plots use. Where they run from the smallest value to the
 * largest, this one runs from white at <em>zero</em> to black at the value furthest from zero, and
 * clips whatever falls off the far end. So {@code ArrayPlot[{{1, 2, 3}}]} is three greys with no
 * white among them, and {@code ArrayPlot[{{-1, 0, 1}}]} paints -1 and 0 in the same white.
 *
 * <p>
 * Every expectation below was measured in Mathematica with
 *
 * <pre>
 * Union[Flatten[Rasterize[ArrayPlot[data, Frame -&gt; False], "Data"], 1]]
 * </pre>
 *
 * and the eight bit values it reported are quoted next to each case. They are recorded here
 * because the natural reading of the option is the other scale, so this looks like a defect until
 * one checks: it was implemented as a range over the data until this test was written.
 */
public class ArrayPlotColorScaleTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
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

  /** Positive data is scaled against its maximum, so the smallest value is not white. */
  @Test
  public void testPositiveDataIsScaledAgainstZero() {
    // Mathematica: {0, 85, 170}
    assertGreys("ArrayPlot({{1, 2, 3}})", 0.0, 1 / 3.0, 2 / 3.0);
    // Mathematica: {0, 170}
    assertGreys("ArrayPlot({{0.5, 1.5}})", 0.0, 2 / 3.0);
  }

  /** A zero in the data is the white end of the scale. */
  @Test
  public void testZeroIsWhite() {
    // Mathematica: {0, 85, 170, 255}
    assertGreys("ArrayPlot({{0, 1, 2, 3}})", 0.0, 1 / 3.0, 2 / 3.0, 1.0);
  }

  /**
   * Negative values fall off the white end and are clipped, which is why two different values can
   * come out in the same colour.
   */
  @Test
  public void testNegativeValuesClipToWhite() {
    // Mathematica: {0, 255} - only two colours for three distinct values
    assertGreys("ArrayPlot({{-1, 0, 1}})", 0.0, 1.0);
  }

  /** With nothing above zero the scale turns over and runs to the most negative value. */
  @Test
  public void testDataBelowZeroScalesAgainstItsMinimum() {
    // Mathematica: {0, 85, 170}
    assertGreys("ArrayPlot({{-1, -2, -3}})", 0.0, 1 / 3.0, 2 / 3.0);
  }

  /** Data of one value has no range to scale over, and takes the end of the scale it is on. */
  @Test
  public void testConstantData() {
    // Mathematica: {0}
    assertGreys("ArrayPlot({{2, 2}})", 0.0);
    // Mathematica: {255}
    assertGreys("ArrayPlot({{0, 0}})", 1.0);
  }

  /**
   * A {@code ColorFunction} of one's own is given the usual scale over the range of the data, not
   * the grey scale's own. This is the case that separates the two.
   */
  @Test
  public void testExplicitColorFunctionScalesOverTheDataRange() {
    // Mathematica: {0, 127, 255}, which is 0, 1/2 and 1 rounded down to eight bits
    assertGreys("ArrayPlot({{1, 2, 3}}, ColorFunction->(GrayLevel(#)&))", 0.0, 0.5, 1.0);
  }

  /** Without scaling the value is the position on the scale, whichever function is in use. */
  @Test
  public void testUnscaledValuesArePositionsOnTheScale() {
    // 0.25 -> GrayLevel[1 - 0.25], 0.75 -> GrayLevel[1 - 0.75], and 3 clips to black
    assertGreys("ArrayPlot({{0.25, 0.75, 3}}, ColorFunctionScaling->False)", 0.0, 0.25, 0.75);
  }

  /** {@code ColorRules} still wins over whichever scale is in use. */
  @Test
  public void testColorRulesOverrideTheScale() {
    IExpr plot = evaluator.eval("ArrayPlot({{1, 2}}, ColorRules->{1->GrayLevel(0.125)})");
    assertEquals("[0.0, 0.125]", greys(plot).toString(),
        "the rule paints the 1, the scale still paints the 2");
  }

  /**
   * A value which is not a real still names a rule: the cell holds {@code I}, and the rule written
   * {@code I -> Red} is the one that matches it.
   *
   * <p>
   * This is the case Woxi 49410d8a fixes for itself - there both sides of the comparison went
   * through a machine double first, so {@code I} and {@code -I} collapsed to zero and took the
   * colour written for {@code 0}.
   */
  @Test
  public void testColorRulesMatchNonRealValues() {
    IExpr plot = evaluator
        .eval("ArrayPlot({{0, -I}, {I, 0}}, ColorRules->{0->White, I->Red, -I->Green})");
    assertEquals("[[[1,1,1], [0,1,0]], [[1,0,0], [1,1,1]]]", cells(plot),
        "white where the zeros are, red for I and green for -I");
  }

  /** A pattern on the left of a rule paints every cell it matches; the rest keep the scale. */
  @Test
  public void testColorRulesMatchAPattern() {
    IExpr plot = evaluator.eval("ArrayPlot({{1, -1, 2}}, ColorRules->{_?Positive->Red})");
    assertEquals("[[[1,0,0], [1,1,1], [1,0,0]]]", cells(plot),
        "both positives are red, -1 is clipped to white by the grey scale");
  }

  /** The first rule written wins, as it does under {@code Replace}. */
  @Test
  public void testTheFirstMatchingRuleWins() {
    IExpr plot = evaluator.eval("ArrayPlot({{1}}, ColorRules->{_Integer->Red, 1->Blue})");
    assertEquals("[[[1,0,0]]]", cells(plot));
  }

  /** A delayed rule computes the colour from the value it matched. */
  @Test
  public void testColorRulesEvaluateADelayedRightHandSide() {
    IExpr plot = evaluator.eval("ArrayPlot({{0.25, 0.75}}, ColorRules->{x_ :> GrayLevel(x)})");
    assertEquals("[[[0.25,0.25,0.25], [0.75,0.75,0.75]]]", cells(plot));
  }

  /** {@code MatrixPlot} shares the rule table, so a pattern reaches its cells as well. */
  @Test
  public void testMatrixPlotMatchesAPattern() {
    IExpr plot = evaluator.eval("MatrixPlot({{1, -1}}, ColorRules->{_?Positive->Red})");
    assertTrue(cells(plot).startsWith("[[[1,0,0], "), "the positive cell is red, got " + cells(plot));
  }

  // ------------------------------------------------------------------ helpers

  /**
   * The raster's cells as {@code [[[r,g,b], ...], ...]}, rows top first.
   *
   * <p>
   * {@code rasterTopFirst} writes the rows bottom first, which is the order a {@code Raster} is
   * drawn in, so they are turned back here to read in the order the array was written.
   */
  private static String cells(IExpr plot) {
    IAST raster = (IAST) ((IAST) ((IAST) plot).arg1()).arg1();
    IAST data = (IAST) raster.arg1();
    StringBuilder buf = new StringBuilder("[");
    for (int r = data.argSize(); r >= 1; r--) {
      buf.append(r == data.argSize() ? "[" : ", [");
      IAST row = (IAST) data.get(r);
      for (int c = 1; c < row.size(); c++) {
        IAST cell = (IAST) row.get(c);
        buf.append(c == 1 ? "[" : ", [");
        for (int i = 1; i < cell.size(); i++) {
          buf.append(i == 1 ? "" : ",").append(component(cell.get(i).evalDouble()));
        }
        buf.append("]");
      }
      buf.append("]");
    }
    return buf.append("]").toString();
  }

  private static void assertGreys(String input, double... expected) {
    TreeSet<Double> want = new TreeSet<>();
    for (double e : expected) {
      want.add(round(e));
    }
    assertEquals(want.toString(), greys(evaluator.eval(input)).toString(), input);
  }

  /** The distinct grey levels of the raster the plot drew. */
  private static TreeSet<Double> greys(IExpr plot) {
    IAST raster = (IAST) ((IAST) ((IAST) plot).arg1()).arg1();
    TreeSet<Double> levels = new TreeSet<>();
    for (IExpr row : (IAST) raster.arg1()) {
      for (IExpr cell : (IAST) row) {
        // a grey cell is {g, g, g}: raster data is numbers, which is what a front end draws
        assertTrue(cell.isList() && cell.argSize() == 3 && cell.first().equals(cell.second())
            && cell.second().equals(((IAST) cell).arg3()), "expected a grey, got " + cell);
        levels.add(round(cell.first().evalDouble()));
      }
    }
    return levels;
  }

  /** One colour component, with the whole ones written short so the expectations stay readable. */
  private static String component(double value) {
    double rounded = round(value);
    return rounded == Math.rint(rounded) ? Integer.toString((int) rounded)
        : Double.toString(rounded);
  }

  /** A third of the scale is not exact in binary, so the comparison is to six places. */
  private static double round(double value) {
    return Math.round(value * 1e6) / 1e6;
  }
}
