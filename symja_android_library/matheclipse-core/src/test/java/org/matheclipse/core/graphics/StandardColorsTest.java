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

/**
 * The {@code Standard*} family of named colours.
 *
 */
public class StandardColorsTest {

  /** name, own value, and the packed rgb the renderers should end up with. */
  private static final Object[][] COLORS = { //
      {"StandardRed", "RGBColor(0.93,0.27,0.27)", 0xED4545}, //
      {"StandardGreen", "RGBColor(0.14,0.8,0.14)", 0x24CC24}, //
      {"StandardBlue", "RGBColor(0.4,0.6,1.0)", 0x6699FF}, //
      {"StandardGray", "RGBColor(0.62,0.62,0.62)", 0x9E9E9E}, //
      {"StandardCyan", "RGBColor(0.0,0.74,0.74)", 0x00BDBD}, //
      {"StandardMagenta", "RGBColor(0.95,0.43,0.96)", 0xF26EF5}, //
      {"StandardYellow", "RGBColor(1.0,0.75,0.0)", 0xFFBF00}, //
      {"StandardBrown", "RGBColor(0.67,0.54,0.42)", 0xAB8A6B}, //
      {"StandardOrange", "RGBColor(0.98,0.56,0.17)", 0xFA8F2B}, //
      {"StandardPink", "RGBColor(1.0,0.51,0.51)", 0xFF8282}, //
      {"StandardPurple", "RGBColor(0.8,0.3,0.8)", 0xCC4DCC} //
  };

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

  @Test
  public void eachNameHasTheDocumentedValue() {
    for (Object[] color : COLORS) {
      assertEquals(color[1], evaluator.eval((String) color[0]).toString(),
          color[0] + " has to match the value its reference page documents");
    }
  }

  /** The renderers read the colour of a directive, whichever of the two is drawing. */
  @Test
  public void eachNameReachesTheScene() {
    for (Object[] color : COLORS) {
      IExpr graphic = evaluator.eval("Graphics3D[{" + color[0] + ",Line[{{0,0,0},{1,1,1}}]}]");
      String scene = WebGLGraphics3D.generateJSON((IAST) graphic);
      assertTrue(scene.contains("\"type\":\"Line\",\"color\":" + color[2]),
          color[0] + " should reach the scene as " + color[2] + ": "
              + scene.substring(scene.indexOf("\"type\":\"Line\"")));
    }
  }

  /** A name Symja does not know leaves the previous directive in force, which is the bug. */
  @Test
  public void aStandardColorOverridesTheDirectiveBeforeIt() {
    IExpr graphic =
        evaluator.eval("Graphics3D[{Red,Sphere[],StandardGray,Line[{{0,0,0},{1,1,1}}]}]");
    String scene = WebGLGraphics3D.generateJSON((IAST) graphic);
    assertTrue(scene.contains("\"type\":\"Line\",\"color\":10395294"),
        "the line is gray, not the red of the sphere before it: " + scene);
    assertTrue(scene.contains("\"type\":\"Sphere\",\"color\":16711680"),
        "and the sphere is still red");
  }

  /** The dashed triangle of the reference example, which is where this was noticed. */
  @Test
  public void theReferenceExampleDrawsAGrayTriangle() {
    IExpr graphic = evaluator.eval(
        "Graphics3D[{Blue, Cylinder[], Red, Sphere[{0, 0, 2}]," + " StandardGray, Thick, Dashed,"
            + " Line[{{-2, 0, 2}, {2, 0, 2}, {0, 0, 4}, {-2, 0, 2}}]}]");
    String scene = WebGLGraphics3D.generateJSON((IAST) graphic);
    assertTrue(scene.contains("\"type\":\"Line\",\"color\":10395294"),
        "the triangle is StandardGray: " + scene);
    assertTrue(scene.contains("\"dashing\""), "and it is still dashed");
    assertTrue(scene.contains("\"thickness\":2.0"), "and still Thick");
  }

  /** The colours that were already there keep the values they had. */
  @Test
  public void theExistingNamedColorsAreUnchanged() {
    assertEquals("RGBColor(1,1,0)", evaluator.eval("Yellow").toString());
    assertEquals("RGBColor(1,0,0)", evaluator.eval("Red").toString());
    assertEquals("RGBColor(0.5,0.5,0.5)", evaluator.eval("Gray").toString());
    assertEquals("RGBColor(1.0,1.0,0.85)", evaluator.eval("LightYellow").toString());
  }
}
