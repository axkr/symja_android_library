package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * How {@code ParametricPlot} and {@code ParametricPlot3D} split their first argument into curves.
 *
 * <p>
 * Substituting a solution into a curve, {@code {x(t), y(t), t} /. sol}, nests it one list deeper,
 * because a solution is a list of rule lists. A list of such curves - how an {@code NDSolve}
 * trajectory is drawn beside its projections - was read as curves that were each a one-element
 * list, and nothing at all was drawn.
 */
public class ParametricCurveListTest {

  private static ExprEvaluator evaluator;
  private static final ObjectMapper MAPPER = new ObjectMapper();

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
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z,p,fx,fy,sol)");
  }

  private static IAST plot(String input, IExpr head) {
    IExpr result = evaluator.eval(input);
    assertTrue(result.isAST(head), input + " did not evaluate to " + head + ": " + result);
    return (IAST) result;
  }

  private static int lineCount(IAST graphics) {
    return graphics.toString().split("Line\\(", -1).length - 1;
  }

  /** Woxi #804: three curves, each with a solution substituted into it, in three colours. */
  @Test
  public void threeCurvesWithASolutionSubstitutedAreThreeCurves() throws Exception {
    IAST graphics = plot("sol = {{fx -> Function(t, Cos(t)), fy -> Function(t, Sin(t))}};"
        + "ParametricPlot3D({{0, fx(t), fy(t)} /. sol, {t, fx(t), 0} /. sol, {t, 2, fy(t)} /. sol},"
        + " {t, 0, 2 Pi}, PlotStyle -> {Red, Blue, Darker(Green)})", S.Graphics3D);
    assertEquals(3, lineCount(graphics));
    Set<Integer> colours = new HashSet<>();
    for (JsonNode element : MAPPER.readTree(WebGLGraphics3D.generateJSON(graphics))
        .get("elements")) {
      if (element.has("color")) {
        colours.add(element.get("color").asInt());
      }
    }
    assertEquals(3, colours.size(), "each curve keeps its own PlotStyle colour: " + colours);
  }

  @Test
  public void ndsolveTrajectoryBesideItsProjections() {
    IAST graphics = plot("sol = NDSolve({x'(t) == -x(t), y'(t) == x(t) - y(t), x(0) == 1,"
        + " y(0) == 0}, {x(t), y(t)}, {t, 0, 3});"
        + "ParametricPlot3D({{0, x(t), y(t)} /. sol, {t, x(t), 0} /. sol, {t, 2, y(t)} /. sol},"
        + " {t, 0, 3})", S.Graphics3D);
    assertEquals(3, lineCount(graphics));
  }

  @Test
  public void threeScalarComponentsStayOneCurve() {
    assertEquals(1, lineCount(plot("ParametricPlot3D({t, t, t}, {t, 0, 1})", S.Graphics3D)));
  }

  @Test
  public void nestedPlaneCurvesAreSeveralCurves() {
    assertEquals(2, lineCount(
        plot("ParametricPlot({{{Cos(t), Sin(t)}}, {{t, Sin(t)}}}, {t, 0, 2 Pi})", S.Graphics)));
    assertEquals(1,
        lineCount(plot("ParametricPlot({Cos(t), Sin(t)}, {t, 0, 2 Pi})", S.Graphics)));
  }

  /**
   * One curve of several wrapped in {@code Tooltip} or {@code Style} used to be sampled with the
   * wrapper still on, which never evaluates to a point, so that curve was silently missing.
   */
  @Test
  public void wrappedCurveAmongSeveralIsDrawnWithItsWrapper() throws Exception {
    IAST graphics = plot("ParametricPlot3D({Tooltip({Cos(t), Sin(t), t}, \"helix\"),"
        + " Style({t, t, t}, Red)}, {t, 0, 1})", S.Graphics3D);
    assertEquals(2, lineCount(graphics));
    JsonNode elements = MAPPER.readTree(WebGLGraphics3D.generateJSON(graphics)).get("elements");
    assertEquals(2, elements.size());
    assertEquals("helix", elements.get(0).get("tooltip").asText());
    assertEquals(0xFF0000, elements.get(1).get("color").asInt(),
        "the curve's own Style wins over the plot colour");

    assertEquals(2, lineCount(
        plot("ParametricPlot3D({Tooltip({Cos(t), Sin(t), t}), {t, t, t}}, {t, 0, 1})",
            S.Graphics3D)));
  }

  @Test
  public void wrappedSurfaceAmongSeveralIsDrawn() throws Exception {
    IAST graphics = plot("ParametricPlot3D({Tooltip({Cos(u), Sin(u), v}, \"s\"), {u, v, 0}},"
        + " {u, 0, 1}, {v, 0, 1}, PlotPoints -> 4)", S.Graphics3D);
    String tooltips = MAPPER.readTree(WebGLGraphics3D.generateJSON(graphics)).get("elements")
        .findValuesAsText("tooltip").toString();
    assertTrue(tooltips.contains("s"), "the labelled surface keeps its tooltip: " + tooltips);
    assertEquals(2, graphics.toString().split("GraphicsComplex\\(", -1).length - 1);
  }

  @Test
  public void nestedSurfaceIsDrawn() {
    plot("ParametricPlot3D({{{Cos(u), Sin(u), v}}}, {u, 0, 2 Pi}, {v, 0, 1})", S.Graphics3D);
  }
}
