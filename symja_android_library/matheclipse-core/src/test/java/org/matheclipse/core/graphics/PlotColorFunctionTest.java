package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.matheclipse.core.graphics.PlotColorFunction.Family;
import org.matheclipse.core.graphics.PlotColorFunction.Sink;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The two policies every plot now shares: how many arguments a colour function is handed, and what
 * happens when it does not answer with a colour.
 *
 * <p>
 * These are unit tests on the class rather than on a picture, because the rules they pin used to
 * differ between the curve plots, the rasters and the surfaces, and the whole point of the shared
 * class is that there is one answer.
 */
public class PlotColorFunctionTest {

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
    evaluator.eval("ClearAll(a,b,c,f,u,v,x,y,z)");
  }

  private static IExpr eval(String input) {
    return evaluator.eval(input);
  }

  private static PlotColorFunction build(Family family, String spec, double... ranges) {
    return PlotColorFunction.of(family, eval(spec), S.True, S.Plot, EvalEngine.get())
        .ranges(ranges).build();
  }

  private static Color colorOf(PlotColorFunction f, double... coordinates) {
    IExpr color = f.color(coordinates);
    assertTrue(color.isPresent(), "color(...) must never answer with nothing");
    Color parsed = ColorUtil.parseDirective(color);
    assertNotNull(parsed, () -> "expected a colour, got " + color);
    return parsed;
  }

  // ------------------------------------------------------- the gradient slot

  /**
   * The defect this whole rework turns on: a colour scheme name is a function of one particular
   * argument, and for a curve that argument is the height, not the position along the axis.
   */
  @Test
  public void testAGradientNameIsFedItsFamilysSlot() {
    Color rainbowAtOne = ColorUtil.parse(eval("ColorData(\"Rainbow\")[1.0]"));
    Color rainbowAtZero = ColorUtil.parse(eval("ColorData(\"Rainbow\")[0.0]"));
    assertFalse(rainbowAtOne.equals(rainbowAtZero), "the test itself needs the two ends to differ");

    // a curve: slot 2, the height
    PlotColorFunction curve = build(Family.CURVE_2D, "\"Rainbow\"", 0, 1, 0, 1);
    assertEquals(rainbowAtOne, colorOf(curve, 0.0, 1.0), "a curve is coloured by its value");
    assertEquals(rainbowAtZero, colorOf(curve, 1.0, 0.0),
        "moving along x must not change the colour of a gradient");

    // a surface: slot 3
    PlotColorFunction surface = build(Family.SURFACE_3D, "\"Rainbow\"", 0, 1, 0, 1, 0, 1);
    assertEquals(rainbowAtOne, colorOf(surface, 0.0, 0.0, 1.0));

    // a field or a raster: slot 1
    assertEquals(rainbowAtOne, colorOf(build(Family.FIELD_2D, "\"Rainbow\"", 0, 1), 1.0));
    assertEquals(rainbowAtOne, colorOf(build(Family.ARRAY, "\"Rainbow\"", 0, 1), 1.0));

    // a contour surface: slot 4
    PlotColorFunction contour3D =
        build(Family.CONTOUR_3D, "\"Rainbow\"", 0, 1, 0, 1, 0, 1, 0, 1);
    assertEquals(rainbowAtOne, colorOf(contour3D, 0.0, 0.0, 0.0, 1.0));
  }

  /** Spelling the gradient out as an object has to mean the same as naming it. */
  @Test
  public void testAColorDataFunctionBehavesLikeItsName() {
    PlotColorFunction byName = build(Family.CURVE_2D, "\"Rainbow\"", 0, 1, 0, 1);
    PlotColorFunction byObject = build(Family.CURVE_2D, "ColorData(\"Rainbow\")", 0, 1, 0, 1);
    assertNotNull(byObject);
    assertEquals(colorOf(byName, 0.0, 0.7), colorOf(byObject, 0.0, 0.7));
    assertTrue(byName.isGradient() && byObject.isGradient());
  }

  // ------------------------------------------------------- the tuple

  @Test
  public void testAFunctionIsGivenTheWholeTuple() {
    PlotColorFunction f =
        build(Family.SURFACE_3D, "RGBColor(#1, #2, #3)&", 0, 1, 0, 1, 0, 1);
    Color c = colorOf(f, 0.2, 0.4, 0.6);
    assertEquals(51, c.getRed(), 2);
    assertEquals(102, c.getGreen(), 2);
    assertEquals(153, c.getBlue(), 2);
    assertFalse(f.isGradient());
  }

  /** A function that names fewer parameters than the plot supplies binds those and drops the rest. */
  @Test
  public void testAFunctionOfFewerParametersIsFine() {
    assertEquals(colorOf(build(Family.SURFACE_3D, "Hue(#1)&", 0, 1, 0, 1, 0, 1), 0.3, 0.9, 0.9),
        ColorUtil.parse(eval("Hue(0.3)")));
    assertEquals(
        colorOf(build(Family.SPHERICAL_3D, "GrayLevel(#)&", 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
            0.25, 0, 0, 0, 0, 0),
        ColorUtil.parse(eval("GrayLevel(0.25)")));
    assertNotNull(build(Family.CURVE_2D, "Function({x, y}, Hue(y))", 0, 1, 0, 1));
  }

  /** Naming more parameters than the plot supplies is a real mistake, and falls back. */
  @Test
  public void testAFunctionOfTooManyParametersFallsBack() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.CURVE_2D, eval("Function({x, y, z}, Hue(z))"), S.True, S.Plot, EvalEngine.get())
        .ranges(0, 1, 0, 1).fallback(F.RGBColor(F.C0, F.C0, F.C1)).build();
    assertNull(f, "a function that cannot be applied at all is not a colouring");
  }

  // ------------------------------------------------------- scaling

  @Test
  public void testEachArgumentIsScaledOverItsOwnRange() {
    PlotColorFunction f = build(Family.CURVE_2D, "RGBColor(#1, #2, 0)&", 0, 10, -1, 1);
    Color middle = colorOf(f, 5.0, 0.0);
    assertEquals(128, middle.getRed(), 2, "x = 5 of 0..10 is the middle of the range");
    assertEquals(128, middle.getGreen(), 2, "y = 0 of -1..1 is the middle of the range");
  }

  @Test
  public void testUnscaledPassesTheRawValues() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.CURVE_2D, eval("GrayLevel(#2)&"), S.False, S.Plot, EvalEngine.get())
        .ranges(0, 10, 0, 10).build();
    assertNotNull(f);
    assertEquals(128, colorOf(f, 0.0, 0.5).getRed(), 2,
        "with scaling off the function sees 0.5, not 0.05");
    assertFalse(f.isScaled());
  }

  @Test
  public void testASlotWithNoDeclaredRangeIsPassedThrough() {
    // only x is given a range; y arrives as it was measured
    PlotColorFunction f = PlotColorFunction
        .of(Family.CURVE_2D, eval("GrayLevel(#2)&"), S.True, S.Plot, EvalEngine.get())
        .range(1, 0, 10).build();
    assertEquals(128, colorOf(f, 5.0, 0.5).getRed(), 2);
  }

  @Test
  public void testADegenerateRangeReportsTheMiddle() {
    PlotColorFunction f = build(Family.FIELD_2D, "GrayLevel(#)&", 3, 3);
    assertEquals(128, colorOf(f, 3.0).getRed(), 2,
        "when every sample is the same value there is no position within the range");
  }

  @Test
  public void testACustomScalerReplacesTheStraightLine() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.ARRAY, eval("GrayLevel(#)&"), S.True, S.MatrixPlot, EvalEngine.get())
        .range(1, 0, 100).scaler(1, v -> v <= 50 ? 0.0 : 1.0).build();
    assertEquals(0, colorOf(f, 10.0).getRed());
    assertEquals(255, colorOf(f, 90.0).getRed());
  }

  // ------------------------------------------------------- when there is nothing to do

  @Test
  public void testAutomaticAndNoneBuildNothing() {
    assertNull(build(Family.CURVE_2D, "Automatic", 0, 1, 0, 1));
    assertNull(build(Family.CURVE_2D, "None", 0, 1, 0, 1));
    assertNull(PlotColorFunction.of(Family.CURVE_2D, null, S.True, S.Plot, EvalEngine.get())
        .build());
    assertNull(PlotColorFunction.of(Family.CURVE_2D, F.NIL, S.True, S.Plot, EvalEngine.get())
        .build());
  }

  @Test
  public void testAnUnknownGradientNameBuildsNothing() {
    assertNull(build(Family.CURVE_2D, "\"Rianbow\"", 0, 1, 0, 1),
        "a misspelled scheme should leave the plot its own colours, not blank it");
  }

  @Test
  public void testAFunctionThatNeverAnswersBuildsNothing() {
    assertNull(build(Family.CURVE_2D, "42", 0, 1, 0, 1));
    assertNull(build(Family.CURVE_2D, "\"not a color\"", 0, 1, 0, 1));
    assertNull(build(Family.FIELD_2D, "Sin(#)&", 0, 1));
  }

  /**
   * A function with a hole at one end is still a colouring. Probing at a single point would decide
   * otherwise and throw the whole picture away.
   */
  @Test
  public void testAFunctionThatFailsAtOneEndStillBuilds() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.CURVE_2D, eval("If(#2 > 0, Hue(#2), Indeterminate)&"), S.True, S.Plot,
            EvalEngine.get())
        .ranges(0, 1, 0, 1).fallback(F.RGBColor(F.C0, F.C0, F.C1)).build();
    assertNotNull(f, "it answers everywhere except at zero");
    assertEquals(ColorUtil.parse(eval("Hue(0.5)")), colorOf(f, 0.0, 0.5));
    assertEquals(Color.BLUE, colorOf(f, 0.0, 0.0), "the hole falls back rather than going blank");
  }

  @Test
  public void testTheFallbackIsAskedForTheGradientSlot() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.CURVE_2D, eval("If(#2 > 0.5, Red, 42)&"), S.True, S.Plot, EvalEngine.get())
        .ranges(0, 1, 0, 1).fallback(t -> F.GrayLevel(F.num(t))).build();
    assertNotNull(f);
    assertEquals(Color.RED, colorOf(f, 0.0, 0.9));
    // below the threshold the function answers 42, so the plot's own scale is asked at y = 0.25
    assertEquals(64, colorOf(f, 0.0, 0.25).getRed(), 2);
  }

  // ------------------------------------------------------- directives

  @Test
  public void testADirectiveSurvivesWhereAPrimitiveListCanHoldIt() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.CURVE_2D, eval("Directive(Opacity(0.5), Red)&"), S.True, S.Plot,
            EvalEngine.get())
        .ranges(0, 1, 0, 1).sink(Sink.DIRECTIVE).build();
    assertNotNull(f);
    assertTrue(f.color(0.0, 0.5).isAST(S.Directive),
        "a curve can carry the whole directive, so it is passed on as it came");
  }

  @Test
  public void testADirectiveIsFoldedDownForARasterCell() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.CURVE_2D, eval("Directive(Opacity(0.5), Red)&"), S.True, S.Plot,
            EvalEngine.get())
        .ranges(0, 1, 0, 1).sink(Sink.FLAT).build();
    assertNotNull(f);
    IExpr color = f.color(0.0, 0.5);
    assertTrue(color.isAST(S.RGBColor), () -> "a cell holds one colour, got " + color);
    Color c = ColorUtil.parse(color);
    assertEquals(255, c.getRed());
    assertEquals(128, c.getAlpha(), 2);
  }

  @Test
  public void testABareOpacityFadesTheDefaultColourOnAFlatSink() {
    PlotColorFunction f = PlotColorFunction
        .of(Family.FIELD_2D, eval("Opacity(0.5)&"), S.True, S.DensityPlot, EvalEngine.get())
        .range(1, 0, 1).sink(Sink.FLAT).fallback(F.RGBColor(F.C0, F.C0, F.C1)).build();
    assertNotNull(f);
    Color c = ColorUtil.parse(f.color(0.5));
    assertEquals(0, c.getRed());
    assertEquals(255, c.getBlue());
    assertEquals(128, c.getAlpha(), 2);
  }

  @Test
  public void testALightingDirectiveIsDropped() {
    assertNull(build(Family.SURFACE_3D, "Glow(Red)&", 0, 1, 0, 1, 0, 1),
        "neither renderer can vary a lighting directive per sample");
    assertNull(build(Family.SURFACE_3D, "Specularity(Red, 10)&", 0, 1, 0, 1, 0, 1));
  }

  // ------------------------------------------------------- the legend

  @Test
  public void testOnlyAGradientHasALegendAxis() {
    assertTrue(build(Family.CURVE_2D, "\"Rainbow\"", 0, 1, 0, 1).legendFunction().isPresent());
    assertFalse(
        build(Family.CURVE_2D, "Hue(#2)&", 0, 1, 0, 1).legendFunction().isPresent(),
        "a function of the whole tuple has no single axis a colour bar could be labelled with");
  }

  // ------------------------------------------------------- housekeeping

  @Test
  public void testTheSameSampleIsOnlyEvaluatedOnce() {
    // a counter in the function itself proves the cache is consulted
    evaluator.eval("ClearAll(cfCount); cfCount = 0");
    PlotColorFunction f = PlotColorFunction
        .of(Family.FIELD_2D, eval("Function(t, cfCount = cfCount + 1; GrayLevel(t))"), S.True,
            S.DensityPlot, EvalEngine.get())
        .range(1, 0, 1).build();
    assertNotNull(f);
    int afterProbe = evaluator.eval("cfCount").toIntDefault(-1);
    for (int i = 0; i < 50; i++) {
      f.color(0.5);
    }
    assertEquals(afterProbe + 1, evaluator.eval("cfCount").toIntDefault(-1),
        "fifty identical samples should cost exactly one evaluation between them");
  }

  @Test
  public void testTheFamilyTable() {
    assertEquals(2, Family.CURVE_2D.arity);
    assertEquals(2, Family.CURVE_2D.gradientSlot);
    assertEquals(1, Family.FIELD_2D.gradientSlot);
    assertEquals(1, Family.ARRAY.gradientSlot);
    assertEquals(1, Family.CHART.gradientSlot);
    assertEquals(3, Family.SURFACE_3D.arity);
    assertEquals(3, Family.SURFACE_3D.gradientSlot);
    assertEquals(4, Family.CONTOUR_3D.arity);
    assertEquals(4, Family.CONTOUR_3D.gradientSlot);
    for (Family family : Family.values()) {
      assertTrue(family.gradientSlot >= 1 && family.gradientSlot <= family.arity,
          family + " must feed a gradient one of its own arguments");
    }
  }
}
