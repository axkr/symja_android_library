package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The grammar and the arithmetic of {@code PlotRangePadding}, away from either renderer.
 *
 * <p>
 * The arithmetic is the part worth pinning down. {@code Scaled[s]} is a fraction of the finished
 * plot rather than of the data, so a plot padded with {@code Scaled[s]} on both sides shows the
 * data across a fraction {@code 1 - 2 s} of its width. That makes the default {@code Automatic}
 * exactly 4% of the picture, 2% at either end, and not quite 4% of the data.
 */
public class PlotRangePaddingSpecTest {

  private static final double TOLERANCE = 1e-12;

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
  }

  private static IExpr value(String input) {
    return evaluator.eval(input);
  }

  private static PlotRangePaddingSpec spec(String input, int axisCount) {
    PlotRangePaddingSpec spec = PlotRangePaddingSpec.parse(value(input), axisCount);
    assertNotNull(spec, input + " should be a padding specification");
    return spec;
  }

  /** The padding of an unpinned axis of the given extent. */
  private static double[] pad(String input, int axisCount, int axis, double span) {
    return spec(input, axisCount).resolve(axis, span, false, false);
  }

  // ------------------------------------------------------------------ arithmetic

  @Test
  public void automaticLeavesTwoPercentOfTheFinishedPlotOnEachSide() {
    double[] pad = pad("Automatic", 2, 0, 1.0);
    assertEquals(1.0 / 48.0, pad[0], TOLERANCE, "2% of 1/0.96 is 1/48 of the data's own span");
    assertEquals(1.0 / 48.0, pad[1], TOLERANCE);
    double drawn = 1.0 + pad[0] + pad[1];
    assertEquals(0.04, (pad[0] + pad[1]) / drawn, TOLERANCE,
        "which is 4% of the finished plot, as the option is documented to be");
  }

  @Test
  public void aPinnedSideIsNotPaddedAutomatically() {
    PlotRangePaddingSpec automatic = PlotRangePaddingSpec.automatic(2);
    assertArrayEquals(new double[] {0, 0}, automatic.resolve(0, 1.0, true, true), TOLERANCE,
        "a range the user asked for by name is drawn as it was asked for");
    double[] halfPinned = automatic.resolve(0, 1.0, true, false);
    assertEquals(0, halfPinned[0], TOLERANCE, "the pinned end stays put");
    assertTrue(halfPinned[1] > 0, "and the free end still gets its room");
  }

  @Test
  public void anExplicitPaddingIsAddedEvenToAPinnedRange() {
    assertArrayEquals(new double[] {0.5, 0.5},
        spec("0.5", 2).resolve(0, 1.0, true, true), TOLERANCE,
        "asking for padding by name overrules the rule that keeps an explicit range intact");
  }

  @Test
  public void aNumberIsInCoordinateUnitsAndScaledIsAFractionOfThePlot() {
    assertArrayEquals(new double[] {1, 1}, pad("1", 2, 0, 1.0), TOLERANCE);
    assertArrayEquals(new double[] {1, 1}, pad("1", 2, 0, 1000.0), TOLERANCE,
        "a length does not care how big the data is");

    double[] quarter = pad("Scaled[0.25]", 2, 0, 1.0);
    assertArrayEquals(new double[] {0.5, 0.5}, quarter, TOLERANCE);
    double drawn = 1.0 + quarter[0] + quarter[1];
    assertEquals(0.5, 1.0 / drawn, TOLERANCE, "1 - 2s of the plot is left for the data");
  }

  @Test
  public void fractionsThatWouldFillTheWholePlotFallBackToTheDataSpan() {
    assertArrayEquals(new double[] {0.5, 0.5}, pad("Scaled[0.5]", 2, 0, 1.0), TOLERANCE,
        "Scaled[0.5] leaves nothing for the data, so the fraction is read against the data");
    assertArrayEquals(new double[] {2, 2}, pad("Scaled[2]", 2, 0, 1.0), TOLERANCE,
        "and the same for anything larger, rather than a negative range");
  }

  @Test
  public void lengthsAndFractionsMixOnOneAxis() {
    // half a unit above the data makes a span of 1.5; a low side worth a quarter of the finished
    // plot then rounds the whole thing out to 2
    assertArrayEquals(new double[] {0.5, 0.5},
        pad("{{Scaled[0.25], 0.5}, None}", 2, 0, 1.0), TOLERANCE);
  }

  @Test
  public void theTwoWaysOfResolvingAgree() {
    String[] inputs = {"Automatic", "None", "0.5", "Scaled[0.1]", "{{Scaled[0.25], 0.5}, None}",
        "{{0.25, Scaled[0.1]}, {Scaled[0.05], Scaled[0.2]}}"};
    for (String input : inputs) {
      PlotRangePaddingSpec spec = spec(input, 2);
      for (int axis = 0; axis < 2; axis++) {
        double[] absolute = spec.absolutePad(axis);
        double[] scaled =
            spec.scaledPad(axis, 4.0 + absolute[0] + absolute[1], false, false);
        assertArrayEquals(new double[] {absolute[0] + scaled[0], absolute[1] + scaled[1]},
            spec.resolve(axis, 4.0, false, false), TOLERANCE,
            input + " must resolve the same in one step as in two");
      }
    }
  }

  // ------------------------------------------------------------------ grammar

  @Test
  public void theWholeSettingMayBeGivenAtOnce() {
    assertEquals(PlotRangePaddingSpec.none(2), spec("None", 2));
    assertEquals(PlotRangePaddingSpec.automatic(2), spec("Automatic", 2));
    assertArrayEquals(new double[] {2, 2}, pad("2", 3, 2, 1.0), TOLERANCE,
        "one number covers both sides of every axis");
  }

  @Test
  public void aListGivesOneSettingPerAxis() {
    PlotRangePaddingSpec spec = spec("{0.5, None}", 2);
    assertArrayEquals(new double[] {0.5, 0.5}, spec.resolve(0, 1.0, false, false), TOLERANCE);
    assertArrayEquals(new double[] {0, 0}, spec.resolve(1, 1.0, false, false), TOLERANCE);

    PlotRangePaddingSpec threeD = spec("{Automatic, None, 1}", 3);
    assertEquals(1.0 / 48.0, threeD.resolve(0, 1.0, false, false)[0], TOLERANCE);
    assertArrayEquals(new double[] {0, 0}, threeD.resolve(1, 1.0, false, false), TOLERANCE);
    assertArrayEquals(new double[] {1, 1}, threeD.resolve(2, 1.0, false, false), TOLERANCE);
  }

  @Test
  public void anAxisMayNameItsTwoSidesSeparately() {
    // the shape the two dimensional parser used to read as {left, bottom}, dropping the other two
    PlotRangePaddingSpec spec = spec("{{0.5, 0.4}, {0.3, 0.2}}", 2);
    assertArrayEquals(new double[] {0.5, 0.4}, spec.resolve(0, 1.0, false, false), TOLERANCE);
    assertArrayEquals(new double[] {0.3, 0.2}, spec.resolve(1, 1.0, false, false), TOLERANCE);
  }

  @Test
  public void aPairIsStillBothSidesOfEveryAxisInThreeDimensions() {
    PlotRangePaddingSpec spec = spec("{1, 2}", 3);
    for (int axis = 0; axis < 3; axis++) {
      assertArrayEquals(new double[] {1, 2}, spec.resolve(axis, 1.0, false, false), TOLERANCE,
          "a pair cannot be one setting per axis when there are three of them");
    }
  }

  @Test
  public void scaledDistributesOverWhateverItWraps() {
    assertArrayEquals(new double[] {0.5, 0.5}, pad("Scaled[0.25]", 2, 1, 1.0), TOLERANCE);

    // Scaled[{lo, hi}] is not a way to write this: Symja's Scaled unwraps a list argument, so
    // the two sides have to carry a Scaled each
    PlotRangePaddingSpec perAxis = spec("{Scaled[0.25], Scaled[0]}", 2);
    assertArrayEquals(new double[] {0.5, 0.5}, perAxis.resolve(0, 1.0, false, false), TOLERANCE);
    assertArrayEquals(new double[] {0, 0}, perAxis.resolve(1, 1.0, false, false), TOLERANCE);

    PlotRangePaddingSpec perSide = spec("{{Scaled[0.25], Scaled[0]}, None}", 2);
    assertArrayEquals(new double[] {1.0 / 3.0, 0}, perSide.resolve(0, 1.0, false, false),
        TOLERANCE, "a quarter of the finished plot below the data and nothing above it");
  }

  @Test
  public void aSettingThatCannotBeReadIsNoSettingAtAll() {
    assertNull(PlotRangePaddingSpec.parse(value("\"nonsense\""), 2));
    assertNull(PlotRangePaddingSpec.parse(value("All"), 2), "All is not a padding");
    assertNull(PlotRangePaddingSpec.parse(value("{1, 2, 3}"), 2),
        "three settings for two axes says nothing about either");
    assertEquals(PlotRangePaddingSpec.automatic(2),
        PlotRangePaddingSpec.parseOrAutomatic(value("\"nonsense\""), 2),
        "and falls back to the default rather than cropping the picture");
  }
}
