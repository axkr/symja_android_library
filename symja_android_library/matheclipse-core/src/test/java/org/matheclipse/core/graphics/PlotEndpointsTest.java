package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;

/**
 * A range whose endpoints coincide spans nothing to plot: every function plot refuses it with
 * <code>plld</code> and stays unevaluated, as the reference implementation does.
 */
public class PlotEndpointsTest {

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
    evaluator.eval("ClearAll(t,x,y,z,theta)");
  }

  /** The result of <code>input</code> and the messages it printed. */
  private static String[] eval(String input) {
    ByteArrayOutputStream errors = new ByteArrayOutputStream();
    EvalEngine engine = evaluator.getEvalEngine();
    PrintStream saved = engine.getErrorPrintStream();
    engine.setErrorPrintStream(new PrintStream(errors, true, StandardCharsets.UTF_8));
    try {
      String result = evaluator.eval(input).toString();
      return new String[] {result, errors.toString(StandardCharsets.UTF_8)};
    } finally {
      engine.setErrorPrintStream(saved);
    }
  }

  private static void refused(String input, String message) {
    String[] out = eval(input);
    assertEquals(input, out[0]);
    assertTrue(out[1].contains(message), () -> input + " printed " + out[1]);
  }

  @Test
  public void testTheFamilyRefusesADegenerateRange() {
    refused("DensityPlot(x*y,{x,0,0},{y,0,1})", //
        "DensityPlot: Endpoints for x in {x,0,0} must have distinct machine-precision numerical values.");
    refused("ContourPlot(x*y,{x,0,1},{y,2,2})", //
        "ContourPlot: Endpoints for y in {y,2,2}");
    refused("Plot3D(x,{x,1,1},{y,0,1})", //
        "Plot3D: Endpoints for x in {x,1,1}");
    refused("ParametricPlot3D({t,t,t},{t,0,0})", //
        "ParametricPlot3D: Endpoints for t in {t,0,0}");
    refused("VectorPlot3D({x,y,z},{x,0,0},{y,0,1},{z,0,1})", //
        "VectorPlot3D: Endpoints for x in {x,0,0}");
    // RegionPlot3D writes the endpoints as reals
    refused("RegionPlot3D(x<y,{x,0,0},{y,0,1},{z,0,1})", //
        "RegionPlot3D: Endpoints for x in {x,0.0,0.0}");
    // the corners of a complex plot have to span a rectangle
    refused("ComplexPlot(z,{z,0,1})", //
        "ComplexPlot: Corners for z in {z,0,1} must have distinct machine-precision real and imaginary parts.");
    refused("ComplexPlot3D(z,{z,0,I})", //
        "ComplexPlot3D: Corners for z in {z,0,I}");
  }

  @Test
  public void testRevolutionPlot3DFails() {
    // a ParametricPlot3D inside, which gives up
    String[] out = eval("RevolutionPlot3D({1,2,3},{t,0,1},{theta,0,0})");
    assertEquals("$Failed", out[0]);
    assertTrue(out[1].contains("ParametricPlot3D: Endpoints for theta in {theta,0,0}"), out[1]);
  }

  @Test
  public void testProperRangesStillDraw() {
    assertEquals("Graphics", eval("Head(ComplexPlot(z,{z,2}))")[0]);
    // {z, r} is the square of corners -|r| (1 + I) and |r| (1 + I)
    assertEquals("Graphics3D", eval("Head(ComplexPlot3D(z,{z,1+2*I}))")[0]);
    // corners given the other way round
    assertEquals("Graphics3D", eval("Head(ComplexPlot3D(z,{z,1+I,-1-I}))")[0]);
    assertEquals("Graphics", eval("Head(DensityPlot(x*y,{x,0,1},{y,0,1}))")[0]);
  }
}
