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
 * {@code Prolog} and {@code Epilog} on a {@code Graphics3D}.
 *
 * <p>
 * These are flat pictures laid under and over a finished scene, in coordinates that run from 0 to 1
 * across the image whatever the scene inside it covers. Nothing in the 3D pipeline read the two
 * options at all, so the content was carried on the graphic and then quietly never drawn. They are
 * handed to the ordinary 2D renderer now, and both the interactive and the static output place the
 * picture it returns over the scene.
 */
public class Epilog3DTest {

  /** The example this was reported with: a framed label pinned to the bottom right corner. */
  private static final String LABELLED =
      "Graphics3D[Cylinder[], Epilog -> Inset[Framed[Style[\"Cylinder\", 20],"
          + " Background -> StandardYellow], {Right, Bottom}, {Right, Bottom}]]";

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
    IExpr graphic = evaluator.eval(input);
    assertTrue(graphic.isAST(), input + " did not evaluate to a graphic: " + graphic);
    return WebGLGraphics3D.generateJSON((IAST) graphic);
  }

  private static String svg(String input) {
    return SVGGraphics3D.toSVG((IAST) evaluator.eval(input));
  }

  /** The overlay is drawn once, and carried in the scene both renderers read. */
  @Test
  public void anEpilogIsDrawnAndCarriedInTheScene() {
    String scene = scene(LABELLED);
    int start = scene.indexOf("\"epilog\"");
    assertTrue(start > 0, "the scene has to carry the epilog: " + scene.substring(0, 200));
    String overlay = scene.substring(start, Math.min(start + 1200, scene.length()));
    assertTrue(overlay.contains("<text") || overlay.contains("Cylinder"),
        "the label has to be in the overlay");
  }

  /** A graphic without one carries nothing, so nothing is drawn over it. */
  @Test
  public void aGraphicWithoutAnEpilogCarriesNone() {
    assertTrue(!scene("Graphics3D[Cylinder[]]").contains("\"epilog\""));
    assertTrue(!scene("Graphics3D[Cylinder[]]").contains("\"prolog\""));
    assertTrue(!scene("Graphics3D[Cylinder[], Epilog -> None]").contains("\"epilog\""));
  }

  /** The static picture shows the scene and the label over it. */
  @Test
  public void theSvgShowsBothTheSceneAndTheLabel() {
    String svg = svg(LABELLED);
    assertTrue(svg.contains(">Cylinder<"), "the label is written: " + svg.length());
    assertTrue(svg.contains("<polygon"), "and the cylinder is still drawn");
    assertEquals(2, svg.split("</svg>", -1).length - 1,
        "the outer picture, with the overlay nested inside it");
  }

  /**
   * The overlay must not paint its own background over the scene.
   *
   * <p>
   * A picture of its own starts by covering the canvas with its background colour. Left in, that
   * white sheet hid the whole scene and only the label was left visible.
   */
  @Test
  public void theOverlayDoesNotHideTheScene() {
    String withLabel = svg(LABELLED);
    String plain = svg("Graphics3D[Cylinder[]]");
    int plainFaces = plain.split("<polygon", -1).length - 1;
    int labelledFaces = withLabel.split("<polygon", -1).length - 1;
    assertTrue(labelledFaces >= plainFaces,
        "the scene keeps all its faces: " + labelledFaces + " against " + plainFaces);
    assertTrue(!withLabel.contains("width=\"100%\" height=\"100%\" fill=\"white\""),
        "no opaque sheet is laid over the scene");
  }

  /** Prolog goes under the scene, epilog over it. */
  @Test
  public void aPrologIsDrawnUnderneath() {
    String svg = svg("Graphics3D[Cylinder[], Prolog -> Text[\"under\", {0.5, 0.5}]]");
    assertTrue(svg.contains(">under<"), "the prolog is drawn");
    assertTrue(svg.indexOf(">under<") < svg.indexOf("<polygon"),
        "and it is drawn before the scene, so the scene covers it");
  }

  /**
   * A corner can be named rather than numbered.
   *
   * <p>
   * {@code {Right, Bottom}} is the same place as {@code {1, 0}}. Read as numbers those words are
   * nothing, so anything positioned with them was dropped without a trace.
   */
  @Test
  public void namedCornersArePositions() {
    String named = svg("Graphics3D[Cylinder[], Epilog -> Inset[Framed[Style[\"x\", 20]],"
        + " {Right, Bottom}, {Right, Bottom}]]");
    assertTrue(named.contains(">x<"), "a named corner places the inset");

    String numbered = svg("Graphics3D[Cylinder[], Epilog -> Inset[Framed[Style[\"x\", 20]],"
        + " {1, 0}, {Right, Bottom}]]");
    assertTrue(numbered.contains(">x<"), "and so does the same corner written as numbers");
  }
}
