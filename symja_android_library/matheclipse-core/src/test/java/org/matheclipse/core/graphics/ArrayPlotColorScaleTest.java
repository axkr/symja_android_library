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

  // ------------------------------------------------------------------ helpers

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

  /** A third of the scale is not exact in binary, so the comparison is to six places. */
  private static double round(double value) {
    return Math.round(value * 1e6) / 1e6;
  }
}
