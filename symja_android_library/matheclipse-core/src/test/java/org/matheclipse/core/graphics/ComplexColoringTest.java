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
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The domain colouring of {@code ComplexPlot} and {@code ComplexPlot3D}.
 *
 * <p>
 * The assertions are structural - no cell is black, this scheme differs from that one, this hue is
 * a half turn from that one - rather than exact colours, because the constants the schemes are
 * built from were chosen by eye and are expected to be retuned.
 */
public class ComplexColoringTest {

  /** The function of the reference picture: zeros at {@code +-I}, poles at {@code +-1}. */
  private static final String RATIONAL = "(z^2 + 1)/(z^2 - 1), {z, -2 - 2*I, 2 + 2*I}";

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
    engine.setRecursionLimit(512);
    engine.setIterationLimit(10000);
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    // other tests in the suite leave values assigned to common one letter symbols
    evaluator.eval("ClearAll(a,b,c,f,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  /** The cells of the single raster a {@code ComplexPlot} draws. */
  private static Color[][] cells(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result instanceof IAST, () -> input + " did not evaluate to a picture: " + result);
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) result).arg1(), new Style2D());
    for (Prim2D prim : collector.primitives()) {
      if (prim instanceof Prim2D.RasterPrim) {
        return ((Prim2D.RasterPrim) prim).cells;
      }
    }
    throw new AssertionError("no raster in " + input);
  }

  private static String plot(String scheme) {
    return "ComplexPlot(" + RATIONAL + ", ColorFunction -> " + scheme + ", PlotPoints -> 40)";
  }

  /** How many cells of the two pictures differ, as a fraction of the whole. */
  private static double difference(Color[][] a, Color[][] b) {
    assertEquals(a.length, b.length, "the two pictures must have the same shape");
    int differing = 0;
    int total = 0;
    for (int r = 0; r < a.length; r++) {
      for (int c = 0; c < a[r].length; c++) {
        total++;
        if (!a[r][c].equals(b[r][c])) {
          differing++;
        }
      }
    }
    return differing / (double) total;
  }

  private static Color darkest(Color[][] cells) {
    Color darkest = Color.WHITE;
    for (Color[] row : cells) {
      for (Color cell : row) {
        if (brightness(cell) < brightness(darkest)) {
          darkest = cell;
        }
      }
    }
    return darkest;
  }

  private static int brightness(Color c) {
    return Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
  }

  /** The colours of a {@code ComplexPlot3D} surface, in the order the vertices were sampled. */
  private static IAST surfaceColors(String input) {
    IExpr colors = evaluator.eval("Cases(" + input + ", _RGBColor, Infinity)");
    assertTrue(colors.isList() && colors.size() > 1, () -> "no vertex colours in " + input);
    return (IAST) colors;
  }

  // ------------------------------------------------------- the artefact this replaced

  /**
   * The defect that prompted the rework: the shading used to be a product of factors that reached
   * exactly zero on {@code Abs(f) == 1}, which for this function is the pair of diagonals, so the
   * picture was crossed by two hard black lines.
   */
  @Test
  public void testNoSchemeDrawsABlackCurve() {
    for (ComplexColoring.Shading shading : ComplexColoring.Shading.values()) {
      Color[][] cells = cells(plot("\"" + shading.wolframName() + "\""));
      for (int r = 0; r < cells.length; r++) {
        for (int c = 0; c < cells[r].length; c++) {
          Color cell = cells[r][c];
          assertFalse(cell.getRed() == 0 && cell.getGreen() == 0 && cell.getBlue() == 0,
              shading.wolframName() + " painted cell (" + r + "," + c + ") pure black");
        }
      }
    }
  }

  /** The diagonal is where {@code Abs(f) == 1}; it used to be the black cross. */
  @Test
  public void testTheDiagonalOfTheReferenceFunctionIsNotDark() {
    Color[][] cells = cells("ComplexPlot(" + RATIONAL + ", PlotPoints -> 40)");
    for (int i = 0; i < cells.length; i++) {
      Color onDiagonal = cells[i][i];
      assertTrue(brightness(onDiagonal) > 60, "the |f| = 1 diagonal is dark at " + i + ": "
          + onDiagonal + "; it used to be the black cross");
    }
  }

  /** Every cyclic scheme stays inside a band, so nothing it draws is anywhere near black. */
  @Test
  public void testTheCyclicSchemesStayInsideTheirBand() {
    String[] cyclic = {"CyclicLogAbs", "CyclicArg", "CyclicLogAbsArg", "CyclicReImLogAbs",
        "ShiftedCyclicLogAbs"};
    for (String scheme : cyclic) {
      Color darkest = darkest(cells(plot("\"" + scheme + "\"")));
      assertTrue(brightness(darkest) > 90,
          scheme + " reached " + darkest + ", which is darker than a cyclic band should go");
    }
  }

  /** The factor itself, swept across the discontinuities the old formulas turned black. */
  @Test
  public void testTheShadingFactorNeverReachesBlack() {
    String[] cyclic = {"CyclicLogAbs", "CyclicArg", "CyclicLogAbsArg", "CyclicReImLogAbs"};
    for (String scheme : cyclic) {
      ComplexColoring coloring = ComplexColoring.of(F.stringx(scheme), F.True,
          new double[] {-2, 2, -2, 2}, org.matheclipse.core.expression.S.ComplexPlot,
          EvalEngine.get());
      coloring.prepare();
      for (int p = -6; p <= 6; p++) {
        // exactly on a band edge, which is where the product used to vanish
        double abs = Math.pow(2.0, p);
        for (int k = 0; k < 24; k++) {
          double angle = k * Math.PI / 12.0;
          double s = coloring.shade(abs * Math.cos(angle), abs * Math.sin(angle));
          assertTrue(s > -1.0 + 1.0e-9,
              scheme + " drove the shading to black at |f| = " + abs + ", arg = " + angle);
        }
      }
    }
  }

  // ------------------------------------------------------- the schemes

  @Test
  public void testTheNamedSchemesAllDrawSomethingOfTheirOwn() {
    Color[][] automatic = cells(plot("Automatic"));
    for (ComplexColoring.Shading shading : ComplexColoring.Shading.values()) {
      if (shading == ComplexColoring.Shading.AUTOMATIC) {
        continue;
      }
      Color[][] scheme = cells(plot("\"" + shading.wolframName() + "\""));
      assertTrue(difference(automatic, scheme) > 0.10, shading.wolframName()
          + " is indistinguishable from Automatic; it changed only "
          + Math.round(difference(automatic, scheme) * 100) + "% of the cells");
    }
    assertTrue(difference(cells(plot("\"CyclicLogAbs\"")),
        cells(plot("\"CyclicLogAbsArg\""))) > 0.10, "adding the spokes must change the picture");
  }

  @Test
  public void testSchemeNamesIgnoreCase() {
    assertEquals(0.0, difference(cells(plot("\"cycliclogabsarg\"")),
        cells(plot("\"CyclicLogAbsArg\""))), "the name should not have to be spelled exactly");
  }

  /**
   * A name that is neither a scheme nor a gradient used to fall through to {@code ColorData}, which
   * did not resolve it, so every cell came back with no colour and the plot was empty.
   */
  @Test
  public void testAnUnknownNameFallsBackToAutomaticInsteadOfDrawingNothing() {
    for (String unknown : new String[] {"\"Arg\"", "\"LocalAbsArg\"", "\"GlobalAbsArg\"",
        "\"NoSuchScheme\""}) {
      Color[][] cells = cells(plot(unknown));
      assertEquals(0.0, difference(cells(plot("Automatic")), cells),
          unknown + " should have fallen back to Automatic");
      assertEquals(0, transparentCells(plot(unknown)), unknown + " drew an empty picture");
    }
  }

  /** {@code None} shades nothing, so the picture is the phase and only the phase. */
  @Test
  public void testNoneIsAPurePhasePlot() {
    for (Color cell : allCells(cells(plot("\"None\"")))) {
      assertEquals(255, brightness(cell),
          "None must not darken or lighten anything, but produced " + cell);
    }
  }

  private static java.util.List<Color> allCells(Color[][] cells) {
    java.util.List<Color> all = new java.util.ArrayList<>();
    for (Color[] row : cells) {
      java.util.Collections.addAll(all, row);
    }
    return all;
  }

  // ------------------------------------------------------- zeros and poles

  @Test
  public void testAPoleIsWhiteAndAZeroIsDark() {
    // an odd number of points puts a cell centre exactly on the origin
    Color[][] pole = cells("ComplexPlot(1/z, {z, -1 - I, 1 + I}, PlotPoints -> 41)");
    assertEquals(Color.WHITE, pole[20][20], "the pole at the origin should be white");

    Color[][] zero = cells("ComplexPlot(z, {z, -1 - I, 1 + I}, PlotPoints -> 41)");
    assertTrue(brightness(zero[20][20]) < 30,
        "the zero at the origin should be dark under Automatic, but was " + zero[20][20]);

    Color[][] unshaded =
        cells("ComplexPlot(z, {z, -1 - I, 1 + I}, PlotPoints -> 41, ColorFunction -> \"None\")");
    assertEquals(255, brightness(unshaded[20][20]),
        "with no shading even a zero keeps its hue");
  }

  // ------------------------------------------------------- the colour function grammar

  /**
   * A colour function is handed the real part, imaginary part, magnitude and phase of the point and
   * then of the value, so {@code #8} is the scaled phase of the value. With the default scaling
   * that makes {@code Hue(#8 + 0.5)} the same wheel the plot uses by default, and {@code Hue(#8)}
   * that wheel turned half way round.
   */
  @Test
  public void testAFunctionIsGivenTheEightArguments() {
    Color[][] byHand = cells(plot("{Hue(#8 + 0.5)&, None}"));
    Color[][] byDefault = cells(plot("\"None\""));
    for (int r = 0; r < byHand.length; r++) {
      for (int c = 0; c < byHand[r].length; c++) {
        assertEquals(brightness(byDefault[r][c]), brightness(byHand[r][c]), 2,
            "Hue(#8 + 0.5) should reproduce the default wheel at (" + r + "," + c + ")");
      }
    }
    assertTrue(difference(byDefault, cells(plot("{Hue(#8)&, None}"))) > 0.9,
        "Hue(#8) turns the wheel half way round, so almost every cell should change");
  }

  @Test
  public void testColorFunctionScalingChangesWhatTheFunctionIsFed() {
    Color[][] scaled = cells(plot("{Hue(#8)&, None}"));
    Color[][] raw = cells("ComplexPlot(" + RATIONAL
        + ", ColorFunction -> {Hue(#8)&, None}, ColorFunctionScaling -> False, PlotPoints -> 40)");
    assertTrue(difference(scaled, raw) > 0.5,
        "unscaled arguments are radians, not a fraction of a turn, so the picture must change");
  }

  /** {@code {cfunc, sfunc}} takes the base colour from one and the shading from the other. */
  @Test
  public void testAGradientCanBeShadedByAScheme() {
    Color[][] gradient = cells(plot("\"Rainbow\""));
    Color[][] shaded = cells(plot("{\"Rainbow\", \"CyclicLogAbsArg\"}"));
    assertEquals(0, transparentCells(plot("{\"Rainbow\", \"CyclicLogAbsArg\"}")));
    assertTrue(difference(gradient, shaded) > 0.5, "the shading must show on a gradient too");
    assertTrue(difference(cells(plot("\"CyclicLogAbsArg\"")), shaded) > 0.5,
        "the gradient must show through the shading");
  }

  /** A plain gradient name keeps colouring by phase, which is what it did before this rework. */
  @Test
  public void testAGradientNameStillPaintsByPhase() {
    assertEquals(0, transparentCells(plot("\"Rainbow\"")));
    assertTrue(difference(cells(plot("Automatic")), cells(plot("\"Rainbow\""))) > 0.5);
  }

  // ------------------------------------------------------- the distribution based schemes

  /**
   * {@code "LocalMaxAbs"} and {@code "QuantileAbs"} rank each value against the ones that were
   * sampled, so the same value gets a different colour in a picture of a different region.
   */
  @Test
  public void testTheLocalSchemesDependOnWhatWasSampled() {
    for (String scheme : new String[] {"LocalMaxAbs", "QuantileAbs"}) {
      String near = "ComplexPlot(z, {z, -1 - I, 1 + I}, PlotPoints -> 40, ColorFunction -> \""
          + scheme + "\")";
      String far = "ComplexPlot(z, {z, -4 - 4*I, 4 + 4*I}, PlotPoints -> 40, ColorFunction -> \""
          + scheme + "\")";
      // the two grids are the same shape and the same ranking of |z|, so the pictures agree
      assertEquals(0.0, difference(cells(near), cells(far)),
          scheme + " ranks the samples, so scaling the whole region must not change the picture");

      String global = "ComplexPlot(z, {z, -1 - I, 1 + I}, PlotPoints -> 40, ColorFunction -> "
          + "\"GlobalAbs\")";
      assertTrue(difference(cells(near), cells(global)) > 0.10,
          scheme + " should not agree with the scheme that ignores the distribution");
    }
  }

  // ------------------------------------------------------- 2D and 3D agree

  /** Red on the positive real axis, in both plots: they share the colouring now. */
  @Test
  public void testBothPlotsPutRedOnThePositiveRealAxis() {
    Color[][] cells =
        cells("ComplexPlot(z, {z, -1 - I, 1 + I}, PlotPoints -> 41, ColorFunction -> \"None\")");
    // row 20 is the real axis, and the cells right of centre have a positive real value
    Color right = cells[20][30];
    assertTrue(right.getRed() > 200 && right.getGreen() < 120 && right.getBlue() < 120,
        "the positive real axis should be red, but was " + right);

    IAST colors = surfaceColors(
        "ComplexPlot3D(z, {z, -1 - I, 1 + I}, PlotPoints -> 5, ColorFunction -> \"None\")");
    boolean sawRed = false;
    for (int i = 1; i < colors.size(); i++) {
      IAST c = (IAST) colors.get(i);
      if (c.arg1().evalf() > 0.78 && c.arg2().evalf() < 0.5 && c.arg3().evalf() < 0.5) {
        sawRed = true;
        break;
      }
    }
    assertTrue(sawRed, "ComplexPlot3D should put red on the positive real axis too: " + colors);
  }

  @Test
  public void testTheSchemesReachTheThreeDimensionalPlot() {
    String base = "ComplexPlot3D(z, {z, -1 - I, 1 + I}, PlotPoints -> 6, ColorFunction -> ";
    IAST plain = surfaceColors(base + "\"None\")");
    assertFalse(plain.equals(surfaceColors(base + "\"CyclicLogAbsArg\")")),
        "a named scheme must change the surface colours");
    assertFalse(plain.equals(surfaceColors(base + "{Hue(#8)&, None})")),
        "an eight argument colour function must work on the surface too");
  }

  // ------------------------------------------------------- the picture is still a picture

  /** Nothing above may leave a hole where a colour belongs. */
  @Test
  public void testEverySchemePaintsEveryCell() {
    for (ComplexColoring.Shading shading : ComplexColoring.Shading.values()) {
      assertEquals(0, transparentCells(plot("\"" + shading.wolframName() + "\"")),
          shading.wolframName() + " left cells unpainted");
    }
  }

  private static int transparentCells(String plot) {
    IExpr count =
        evaluator.eval("Count(Cases(" + plot + ",_RGBColor,Infinity),RGBColor(0,0,0,0))");
    return count.toIntDefault(-1);
  }

  @Test
  public void testIsSchemeTellsTheOwnSchemesFromTheRest() {
    assertTrue(ComplexColoring.isScheme(org.matheclipse.core.expression.S.Automatic));
    assertTrue(ComplexColoring.isScheme(F.stringx("CyclicLogAbsArg")));
    assertTrue(ComplexColoring.isScheme(F.stringx("globalabs")));
    assertFalse(ComplexColoring.isScheme(F.stringx("Rainbow")));
    assertNotNull(ComplexColoring.Shading.byName("cyclicARG"));
    assertNull(ComplexColoring.Shading.byName("Rainbow"));
  }

  /** The sampling grid is an artefact, so the raster says it may be smoothed over. */
  @Test
  public void testTheDomainColouringIsMarkedAsASampledField() {
    IExpr result = evaluator.eval("ComplexPlot(z, {z, -1 - I, 1 + I}, PlotPoints -> 8)");
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) result).arg1(), new Style2D());
    List<Prim2D> prims = collector.primitives();
    int rasters = 0;
    for (Prim2D prim : prims) {
      if (prim instanceof Prim2D.RasterPrim) {
        rasters++;
        assertTrue(((Prim2D.RasterPrim) prim).smooth,
            "a domain colouring is a sampled field and should not be drawn with crisp cells");
      }
    }
    assertEquals(1, rasters, "the picture is still exactly one raster");
  }

  /** An ArrayPlot is data, and its cells must keep their edges. */
  @Test
  public void testADiscreteRasterStaysCrisp() {
    IExpr result = evaluator.eval("ArrayPlot(Table(Mod(i + j, 2), {i, 6}, {j, 6}))");
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) result).arg1(), new Style2D());
    for (Prim2D prim : collector.primitives()) {
      if (prim instanceof Prim2D.RasterPrim) {
        assertFalse(((Prim2D.RasterPrim) prim).smooth,
            "ArrayPlot draws one cell per datum, so its cells must not be smoothed together");
      }
    }
  }
}
