package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Every plot that declares {@code ColorFunction} acts on it.
 *
 * <p>
 * The option used to be declared on a good many plots that then ignored it, which is worse than not
 * offering it: the call is accepted and the picture comes out unchanged. These are the ones that
 * were in that state, checked one at a time so that a regression names the plot it broke.
 */
public class PlotColorFunctionReachTest {

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
    evaluator.eval("ClearAll(a,b,c,f,i,j,k,n,t,u,v,x,y,z)");
  }

  /** The distinct colours a picture is drawn with, in the order they first appear. */
  private static Set<String> colors(String plot) {
    IExpr found = evaluator.eval("Cases(" + plot + ", _RGBColor|_Hue, Infinity)");
    assertTrue(found.isList(), () -> "not a picture: " + plot);
    Set<String> distinct = new LinkedHashSet<>();
    IAST list = (IAST) found;
    for (int i = 1; i < list.size(); i++) {
      distinct.add(list.get(i).toString());
    }
    return distinct;
  }

  /**
   * The option has to make a visible difference, and to make a different one for a different
   * colour function - a plot that quietly ignored it would pass the first test by accident.
   */
  private static void assertColorFunctionActs(String base, String gradient, String function) {
    Set<String> plain = colors(base + ")");
    Set<String> byGradient = colors(base + ", ColorFunction -> " + gradient + ")");
    Set<String> byFunction = colors(base + ", ColorFunction -> " + function + ")");
    assertFalse(plain.equals(byGradient),
        () -> base + ": the gradient changed nothing, it is still " + plain);
    assertFalse(plain.equals(byFunction),
        () -> base + ": the colour function changed nothing, it is still " + plain);
    assertFalse(byGradient.equals(byFunction),
        () -> base + ": the gradient and the function produced the same picture, so neither is "
            + "really being applied");
  }

  @Test
  public void testListPointPlot3DColorsItsPoints() {
    assertColorFunctionActs(
        "ListPointPlot3D(Table({i,j,i*j}, {i,1,5}, {j,1,5}) // Flatten(#,1)&", "\"Rainbow\"",
        "(Hue(#3)&)");
  }

  @Test
  public void testListLinePlot3DColorsItsSteps() {
    assertColorFunctionActs("ListLinePlot3D({{1,2,3,4},{-1,-2,-3,-4}}", "\"Rainbow\"",
        "(Hue(#3)&)");
    // one colour per step rather than one per line, so a two line plot gains pieces
    assertTrue(colors("ListLinePlot3D({{1,2,3,4},{-1,-2,-3,-4}}, ColorFunction -> (Hue(#3)&))")
        .size() > 2, "a line is painted along its length, not in one colour");
  }

  @Test
  public void testDiscretePlot3DColorsItsElements() {
    assertColorFunctionActs("DiscretePlot3D(i*j, {i,1,5}, {j,1,5}", "\"Rainbow\"", "(Hue(#3)&)");
  }

  @Test
  public void testBoxWhiskerChartColorsItsBoxes() {
    assertColorFunctionActs("BoxWhiskerChart({{1,2,3,4,5},{10,11,12,13,14},{5,6,7,8,9}}",
        "\"Rainbow\"", "(Hue(#)&)");
  }

  @Test
  public void testListPlot3DAndContourPlot3DColorTheirSurfaces() {
    assertColorFunctionActs("ListPlot3D(Table(i*j, {i,1,6}, {j,1,6})", "\"Rainbow\"",
        "Function({x,y,z}, Hue(z))");
    assertColorFunctionActs(
        "ContourPlot3D(x^2+y^2+z^2, {x,-2,2}, {y,-2,2}, {z,-2,2}, PlotPoints->8, Contours->3",
        "\"Rainbow\"", "(Hue(#4)&)");
  }

  // ------------------------------------------------------- the slot each family reads

  /**
   * A contour surface is coloured by its level, which is the fourth argument. Three evenly spaced
   * levels scale onto the ends and the middle of the range exactly.
   */
  @Test
  public void testContourPlot3DIsColoredByItsLevel() {
    Set<String> found = colors("ContourPlot3D(x^2+y^2+z^2, {x,-2,2}, {y,-2,2}, {z,-2,2}, "
        + "PlotPoints->8, Contours->3, ColorFunction -> (Hue(#4)&))");
    assertEquals(3, found.size(), () -> "one colour per level, got " + found);
    assertTrue(found.contains("Hue(0.0)") && found.contains("Hue(0.5)")
        && found.contains("Hue(1.0)"), () -> "the three levels should land on 0, 0.5 and 1: "
            + found);
  }

  /** A discrete plot is coloured by height, which is the third argument of a surface tuple. */
  @Test
  public void testDiscretePlot3DIsColoredByHeight() {
    // the chart palette is still emitted once before the elements, and each element then
    // overrides it, so only the function's own answers are counted here
    // i*j over 1..5 takes 14 distinct values, so a colouring by height takes 14 colours
    assertEquals(14,
        hues(colors("DiscretePlot3D(i*j, {i,1,5}, {j,1,5}, ColorFunction -> (Hue(#3)&))")),
        "one colour per distinct height");
    // and by x alone there are only five columns
    assertEquals(5,
        hues(colors("DiscretePlot3D(i*j, {i,1,5}, {j,1,5}, ColorFunction -> (Hue(#1)&))")),
        "one colour per column when the first argument is read");
  }

  /** A box is coloured by the median it is drawn around, so equal medians share a colour. */
  @Test
  public void testBoxWhiskerChartIsColoredByItsMedian() {
    Set<String> distinct = colors(
        "BoxWhiskerChart({{1,2,3,4,5},{11,12,13,14,15},{1,2,3,4,5}}, ColorFunction -> (Hue(#)&))");
    // two datasets share a median of 3, the third has 13, so there are two box colours among the
    // black lines the whiskers are drawn with
    assertEquals(2, hues(distinct),
        () -> "two distinct medians should give two box colours: " + distinct);
  }

  /** How many of them the colour function itself produced, as opposed to the plot's own palette. */
  private static long hues(Set<String> distinct) {
    return distinct.stream().filter(c -> c.startsWith("Hue(")).count();
  }

  @Test
  public void testScalingReachesThesePlotsToo() {
    for (String base : new String[] {
        "ListPointPlot3D(Table({i,j,i*j}, {i,1,5}, {j,1,5}) // Flatten(#,1)&",
        "ListLinePlot3D({{1,2,3,4},{-1,-2,-3,-4}}",
        "DiscretePlot3D(i*j, {i,1,5}, {j,1,5}"}) {
      Set<String> scaled = colors(base + ", ColorFunction -> (Hue(#3)&))");
      Set<String> raw =
          colors(base + ", ColorFunction -> (Hue(#3)&), ColorFunctionScaling -> False)");
      assertFalse(scaled.equals(raw),
          () -> base + ": ColorFunctionScaling should change what the function is fed");
    }
  }
}
