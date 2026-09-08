package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;

/**
 * Options of {@code Graphics3D} that were accepted but never read.
 *
 */
public class Graphics3DViewAndStyleTest {

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

  private static String scene(String input) {
    return WebGLGraphics3D.generateJSON((IAST) evaluator.eval(input));
  }

  private static String svg(String input) {
    return SVGGraphics3D.toSVG((IAST) evaluator.eval(input));
  }

  /** {@code BaseStyle} is what the contents start from, not what they are forced to. */
  @Test
  public void baseStyleIsTheStyleTheContentsInherit() {
    assertTrue(scene("Graphics3D[Cuboid[], BaseStyle->Red]").contains("\"color\":16711680"),
        "an unstyled primitive takes the base style");
    assertTrue(scene("Graphics3D[{Blue, Cuboid[]}, BaseStyle->Red]").contains("\"color\":255"),
        "a primitive that says otherwise keeps its own colour");
    assertNotEquals(svg("Graphics3D[Cuboid[]]"), svg("Graphics3D[Cuboid[], BaseStyle->Red]"),
        "and the static picture shows it too");
  }

  /** {@code FaceGrids} says where the grids go, {@code FaceGridsStyle} what they look like. */
  @Test
  public void faceGridsStyleColorsTheGrids() {
    assertTrue(scene("Graphics3D[Cuboid[], FaceGrids->All, FaceGridsStyle->Red]")
        .contains("\"faceGridsColor\":16711680"));
  }

  /** {@code AxesOrigin} says where the three axes cross. */
  @Test
  public void axesOriginReachesTheScene() {
    assertTrue(scene("Graphics3D[Cuboid[], Axes->True, AxesOrigin->{0,0,0}]")
        .contains("\"axesOrigin\":[0.0,0.0,0.0]"));
    assertTrue(!scene("Graphics3D[Cuboid[], Axes->True]").contains("\"axesOrigin\""),
        "and nothing is written when the call does not ask");
  }

  /**
   * {@code ViewVector} places the camera in the data's own coordinates.
   *
   * <p>
   * One vector is the camera position; two are the position and the point it looks at.
   */
  @Test
  public void viewVectorPlacesTheCamera() {
    assertTrue(scene("Graphics3D[Cuboid[], ViewVector->{{5,0,0},{0,0,0}}]")
        .contains("\"viewVector\":[5.0,0.0,0.0]"));
    assertTrue(
        scene("Graphics3D[Cuboid[], ViewVector->{5,0,0}]").contains("\"viewVector\":[5.0,0.0,0.0]"),
        "a single vector is the position");

    assertNotEquals(svg("Graphics3D[Cuboid[], ViewVector->{{5,0,0},{0,0,0}}]"),
        svg("Graphics3D[Cuboid[], ViewVector->{{0,5,0},{0,0,0}}]"),
        "looking from a different place gives a different picture");
  }

  /** Whatever these do, the output stays a graphic. */
  @Test
  public void theOutputSurvivesEveryOption() {
    for (String option : new String[] {"BaseStyle->Red", "FaceGridsStyle->Red",
        "AxesOrigin->{0,0,0}", "ViewVector->{{5,0,0},{0,0,0}}", "ViewCenter->{0,0,0}"}) {
      String svg = svg("Graphics3D[Cuboid[], Axes->True, " + option + "]");
      assertTrue(svg.startsWith("<svg "), option);
      assertTrue(svg.contains("</svg>"), option);
    }
  }
}
