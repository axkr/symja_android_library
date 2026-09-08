package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A style given to a plot survives being wrapped in the plot's own style.
 *
 * <p>
 * A plot that has a style of its own to apply wraps it together with the one the call supplied, so
 * the user's arrives as a {@code Directive} nested inside the plot's. The walk that reads
 * directives had no case for one holding another, so the inner one was dropped whole: everything in
 * it was lost, and whatever the plot had put at the outer level was what survived.
 *
 * <p>
 * {@code ContourPlot3D[..., ContourStyle -> Directive[FaceForm[Orange, Red], Specularity[White,
 * 30]]]} was the case that showed it. The surface lost its colours and came out the default white,
 * shaded to grey, while the plot's own {@code Specularity} - written outside the wrapper - was the
 * one that took effect.
 */
public class NestedDirectiveTest {

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
  }

  /** The first element of the scene the renderers are given. */
  private static JsonNode firstElement(String input) {
    IExpr graphic = evaluator.eval(input);
    assertTrue(graphic.isAST(), input + " did not evaluate to a graphic: " + graphic);
    try {
      JsonNode root =
          new ObjectMapper().readTree(WebGLGraphics3D.generateJSON((IAST) graphic));
      return root.get("elements").get(0);
    } catch (Exception rex) {
      throw new IllegalStateException(rex);
    }
  }

  /** The colours and the highlight of a nested directive all reach the scene. */
  @Test
  public void aDirectiveInsideADirectiveIsStillApplied() {
    JsonNode surface = firstElement("Graphics3D[{Directive[Directive[FaceForm[Orange, Red],"
        + " Specularity[White, 30]]], Sphere[]}]");
    assertEquals(0xFF8000, surface.get("color").asInt(), "the front colour is orange");
    assertEquals(0xFF0000, surface.get("backColor").asInt(), "the far side is red");
    assertEquals(30.0, surface.get("specularExponent").asDouble(), 1e-9);
  }

  /** A style list nested in a directive behaves the same way. */
  @Test
  public void aListInsideADirectiveIsStillApplied() {
    JsonNode surface = firstElement("Graphics3D[{Directive[{Blue}], Sphere[]}]");
    assertEquals(0x0000FF, surface.get("color").asInt());
  }

  /** The plot wraps the call's style inside its own, which is where this was found. */
  @Test
  public void contourPlot3DKeepsTheStyleItWasGiven() {
    JsonNode surface = firstElement("ContourPlot3D[x^2+y^2+z^2==4,{x,-3,3},{y,-3,3},{z,-3,3},"
        + "PlotPoints->8,ContourStyle->Directive[FaceForm[Orange,Red],Specularity[White,30]],"
        + "Mesh->None]");
    assertEquals(0xFF8000, surface.get("color").asInt(),
        "the surface takes the colour the call asked for, not the default white");
    assertEquals(0xFF0000, surface.get("backColor").asInt());
    assertEquals(30.0, surface.get("specularExponent").asDouble(), 1e-9,
        "the call's Specularity wins over the one the plot supplies");
    assertTrue(!surface.get("showMesh").asBoolean(), "Mesh -> None draws no mesh");
  }

  /** One colour still means one colour: no far side is written unless FaceForm gives two. */
  @Test
  public void aSingleColorLeavesTheFarSideAlone() {
    JsonNode plain = firstElement("Graphics3D[{Red, Sphere[]}]");
    assertEquals(0xFF0000, plain.get("color").asInt());
    assertTrue(plain.get("backColor") == null, "no back colour was asked for");

    JsonNode oneSided = firstElement("Graphics3D[{FaceForm[Orange], Sphere[]}]");
    assertEquals(0xFF8000, oneSided.get("color").asInt());
    assertTrue(oneSided.get("backColor") == null, "FaceForm with one colour has no far side");
  }

  /** The static renderer draws the two sides in their own colours as well. */
  @Test
  public void theSvgRendererDrawsBothSides() {
    IExpr graphic = evaluator.eval("ContourPlot3D[x^2+y^2+z^2==4,{x,-3,3},{y,-3,3},{z,-3,3},"
        + "PlotPoints->10,ContourStyle->Directive[FaceForm[Orange,Red]],Mesh->None]");
    String svg = SVGGraphics3D.toSVG((IAST) graphic);
    boolean orange = false;
    boolean red = false;
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("fill=\"#([0-9a-f]{6})\"").matcher(svg);
    while (m.find()) {
      int rgb = Integer.parseInt(m.group(1), 16);
      int r = (rgb >> 16) & 0xFF;
      int g = (rgb >> 8) & 0xFF;
      int b = rgb & 0xFF;
      if (r > 60 && g > 25 && b < g) {
        orange = true;
      }
      if (r > 60 && g < 20) {
        red = true;
      }
    }
    assertTrue(orange, "the near side is drawn in its own colour");
    assertTrue(red, "and the far side in the other: " + svg.substring(0, Math.min(200, svg.length())));
  }
}
