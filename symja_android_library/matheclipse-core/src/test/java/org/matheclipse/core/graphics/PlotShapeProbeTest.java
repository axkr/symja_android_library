package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Every plot family splits its functions into curves by the shape of a sampled value, through
 * {@link PlotShapeProbe}.
 *
 * <p>
 * A function that only takes its shape once its variables are numbers, {@code f(u_?NumericQ) :=
 * {Sin(u), Cos(u)}}, is as many curves as its value holds, and an entry of a curve list that never
 * evaluates is a curve that draws nothing rather than a reason to draw nothing at all.
 */
public class PlotShapeProbeTest {

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
    evaluator.eval("ClearAll(t,y,n,i,j,s1,s2,s3,q2,r2,m2,d2,e3,undefinedCurve)");
    evaluator.eval("s1(u_?NumericQ) := Sin(u);" //
        + "s2(u_?NumericQ) := {Sin(u), Cos(u)};" //
        + "s3(u_?NumericQ, v_?NumericQ) := {u*v, u + v};" //
        + "q2(u_?NumericQ) := {Cos(u), Sin(u)};" //
        + "r2(u_?NumericQ) := {u, u^2};" //
        + "m2(u_?NumericQ) := {{u, u}, {u, -u}};" //
        + "d2(k_Integer) := {k, k^2};" //
        + "e3(a_Integer, b_Integer) := {a*b, a + b}");
  }

  private static String plot(String input, ISymbol head) {
    IExpr result = evaluator.eval(input);
    assertTrue(result.isAST(head), input + " did not evaluate to " + head + ": " + result);
    return result.toString();
  }

  private static int count(String output, String primitive) {
    return output.split(primitive + "\\(", -1).length - 1;
  }

  /** Plot and PolarPlot counts of s2(t) and {s2(t), Sin(t)} measured in Mathematica 2026-09-14. */
  @Test
  public void plotFamilySplitsAListValuedFunction() {
    assertEquals(2, count(plot("Plot(s2(t), {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(3, count(plot("Plot({s1(t), s2(t)}, {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(2, count(plot("Plot({s2(t), undefinedCurve(t)}, {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(1, count(plot("Plot(s1(t), {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(2, count(plot("LogPlot(s2(t) + 2, {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(2, count(plot("LogLinearPlot(s2(t), {t, 1, 2})", S.Graphics), "Line"));
    assertEquals(2, count(plot("LogLogPlot(Exp(s2(t)), {t, 1, 2})", S.Graphics), "Line"));
    assertEquals(2, count(plot("PolarPlot(s2(t), {t, 0, 1})", S.Graphics), "Line"));
  }

  @Test
  public void aTooltipOnAListValuedFunctionLabelsEveryCurve() {
    String output = plot("Plot(Tooltip(s2(t), \"both\"), {t, 0, 1})", S.Graphics);
    assertEquals(2, count(output, "Line"));
    assertEquals(2, output.split("both", -1).length - 1, output);
  }

  @Test
  public void parametricPlotReadsPointsOffTheValue() {
    assertEquals(1, count(plot("ParametricPlot(q2(t), {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(2, count(plot("ParametricPlot({q2(t), r2(t)}, {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(2, count(plot("ParametricPlot(m2(t), {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(3, count(plot("ParametricPlot({q2(t), m2(t)}, {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(1, count(plot("ParametricPlot({Cos(t), Sin(t)}, {t, 0, 1})", S.Graphics), "Line"),
        "two scalar components stay one curve");
  }

  /**
   * An entry that never evaluates draws nothing and keeps the others: Mathematica draws 2 lines for
   * {@code {q2(t), undefined(t), q2(2 t)}}, measured 2026-09-14.
   */
  @Test
  public void parametricPlotKeepsTheCurvesBesideAnUndefinedOne() {
    assertEquals(1, count(
        plot("ParametricPlot({q2(t), undefinedCurve(t)}, {t, 0, 1})", S.Graphics), "Line"));
    assertEquals(2, count(
        plot("ParametricPlot({q2(t), undefinedCurve(t), r2(t)}, {t, 0, 1})", S.Graphics), "Line"));
  }

  @Test
  public void aWrappedParametricCurveIsLabelledWithoutAPartMessage() {
    String output = plot("ParametricPlot(Tooltip(q2(t)), {t, 0, 1})", S.Graphics);
    assertEquals(1, count(output, "Line"));
    assertFalse(output.contains("Part("), "the label is the curve as written: " + output);
  }

  @Test
  public void surfacesAreSplitByTheirValue() {
    assertEquals(2, count(
        plot("Plot3D(s3(t, y), {t, 0, 1}, {y, 0, 1}, PlotPoints -> 5)", S.Graphics3D),
        "GraphicsComplex"));
    assertEquals(2, count(plot("SphericalPlot3D(s2(t) + 2, {t, 0, Pi}, {y, 0, 2 Pi},"
        + " PlotPoints -> 5)", S.Graphics3D), "GraphicsComplex"));
    assertEquals(1, count(plot("SphericalPlot3D({1, undefinedCurve(t)}, {t, 0, Pi},"
        + " {y, 0, 2 Pi}, PlotPoints -> 5)", S.Graphics3D), "GraphicsComplex"));
  }

  @Test
  public void contourPlotSplitsAListValuedFunction() {
    String single = plot("ContourPlot(t*y, {t, 0, 1}, {y, 0, 1})", S.Graphics);
    String split = plot("ContourPlot(s3(t, y), {t, 0, 1}, {y, 0, 1})", S.Graphics);
    assertTrue(count(split, "Line") > count(single, "Line"),
        "both functions contribute contours");
    assertTrue(count(plot("ContourPlot(t^2 + y^2 == 1/2, {t, 0, 1}, {y, 0, 1})", S.Graphics),
        "Line") > 0, "an equation is not taken apart");
  }

  /** Discrete plots probe at the iterator's values, where a function of an integer is defined. */
  @Test
  public void discretePlotsProbeAtTheIteratorValues() {
    String sequences = plot("DiscretePlot(d2(n), {n, 1, 5})", S.Graphics);
    assertEquals(2, count(sequences, "Point"));
    String stems = plot("DiscretePlot3D(e3(i, j), {i, 1, 3}, {j, 1, 3})", S.Graphics3D);
    String single = plot("DiscretePlot3D(i*j, {i, 1, 3}, {j, 1, 3})", S.Graphics3D);
    assertEquals(2 * count(single, "Point"), count(stems, "Point"));
  }
}
