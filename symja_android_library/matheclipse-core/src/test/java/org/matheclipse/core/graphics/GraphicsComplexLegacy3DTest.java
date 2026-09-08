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
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The older JSON renderer - the one behind the API server and the desktop viewer, as opposed to
 * {@link WebGLGraphics3D} behind the notebook - and what it makes of a {@code GraphicsComplex}.
 *
 * <p>
 * It used to make nothing of one: it looped over the arguments looking for primitives, so a nested
 * {@code {RGBColor(...), Sphere(...)}} was skipped for being a list rather than walked into, and
 * the vertex indices that are the whole point of a complex were never resolved against the
 * coordinate pool. A complex therefore rendered as an empty scene.
 */
public class GraphicsComplexLegacy3DTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    evaluator = new ExprEvaluator(false, (short) 20);
  }

  private static String render(String input) {
    IExpr result = evaluator.eval(input);
    StringBuilder buf = new StringBuilder();
    assertTrue(GraphicsUtil.renderGraphics3D(buf, (IAST) result, EvalEngine.get()),
        "renderGraphics3D refused " + input);
    return buf.toString();
  }

  private static int count(String haystack, String needle) {
    return haystack.split(java.util.regex.Pattern.quote(needle), -1).length - 1;
  }

  @Test
  public void testNestedStyleGroupsAreWalkedInto() {
    String json = render("Graphics3D(GraphicsComplex({{0,0,0},{1,0,0},{0,1,0}}, "
        + "{{RGBColor(1,0,0), Sphere({1,2}, 0.3)}, {RGBColor(0,0,1), Sphere({3}, 0.3)}}))");
    assertFalse(json.contains("\"elements\":[]"), "the complex rendered as an empty scene");
    assertEquals(2, count(json, "\"sphere\""));
  }

  @Test
  public void testVertexIndicesResolveAgainstTheCoordinatePool() {
    // vertex 2 is {1,0,0}: the coordinate has to reach the primitive, not the index
    String json = render(
        "Graphics3D(GraphicsComplex({{0,0,0},{1,0,0}}, {{RGBColor(1,0,0), Sphere({2}, 0.3)}}))");
    assertTrue(json.contains("1.0"), "expected the resolved coordinate in " + json);
  }

  @Test
  public void testCylinderChainUsesTwoVertices() {
    String json = render("Graphics3D(GraphicsComplex({{0,0,0},{1,0,0}}, "
        + "{{RGBColor(1,0,0), Cylinder({1,2}, 0.1)}}))");
    assertEquals(1, count(json, "\"cylinder\""));
  }
}
