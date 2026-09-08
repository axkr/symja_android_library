package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;

/**
 * {@code ClipPlanes} cuts the scene down to a half space, and {@code ClipPlanesStyle} draws the
 * plane that did the cutting.
 *
 * <p>
 * The interactive output hands the planes to the graphics hardware, which cuts as it draws. The
 * static one has no such help, so the geometry itself is cut: every face is trimmed against each
 * plane, and a face that straddles one comes back with new corners along it. Both keep the side
 * where {@code ax+by+cz+d} is positive, which is the convention the hardware uses.
 */
public class ClipPlanesTest {

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

  private static int polygons(String svg) {
    return svg.split("<polygon", -1).length - 1;
  }

  /** Every spelling of a plane ends up as the same four coefficients. */
  @Test
  public void aPlaneMayBeWrittenEitherWay() {
    assertTrue(scene("Graphics3D[Sphere[], ClipPlanes->{1,0,0,0}]")
        .contains("\"clipPlanes\":[[1.0,0.0,0.0,0.0]]"), "four coefficients");
    assertTrue(
        scene("Graphics3D[Sphere[], ClipPlanes->InfinitePlane[{{0,0,0},{0,1,0},{0,0,1}}]]")
            .contains("\"clipPlanes\":[[1.0,0.0,0.0,"),
        "three points give the same plane through their normal");
    assertTrue(scene("Graphics3D[Sphere[], ClipPlanes->{{1,0,0,0},{0,1,0,0}}]")
        .contains("\"clipPlanes\":[[1.0,0.0,0.0,0.0],[0.0,1.0,0.0,0.0]]"), "a list of planes");
    assertTrue(!scene("Graphics3D[Sphere[]]").contains("\"clipPlanes\""), "and none by default");
    assertTrue(!scene("Graphics3D[Sphere[], ClipPlanes->None]").contains("\"clipPlanes\""));
  }

  /** A plane's coefficients are normalised, so the same plane written larger is the same plane. */
  @Test
  public void thePlaneIsNormalised() {
    assertEquals(scene("Graphics3D[Sphere[], ClipPlanes->{1,0,0,0}]"),
        scene("Graphics3D[Sphere[], ClipPlanes->{5,0,0,0}]"));
  }

  /** The static picture really loses the part that was cut away. */
  @Test
  public void theStaticPictureIsCut() {
    int whole = polygons(svg("Graphics3D[Sphere[]]"));
    int half = polygons(svg("Graphics3D[Sphere[], ClipPlanes->{1,0,0,0}]"));
    assertTrue(half < whole, "a half sphere is fewer faces than a whole one: " + half + " of "
        + whole);
    assertTrue(half > whole / 4, "but it is still half a sphere, not a sliver: " + half);

    int quarter = polygons(svg("Graphics3D[Sphere[], ClipPlanes->{{1,0,0,0},{0,1,0,0}}]"));
    assertTrue(quarter < half, "a second plane takes more away: " + quarter + " of " + half);
  }

  /** Which half is kept follows the sign, so turning the plane around keeps the other one. */
  @Test
  public void theSignChoosesWhichHalfIsKept() {
    String kept = svg("Graphics3D[Sphere[], ClipPlanes->{1,0,0,0}]");
    String other = svg("Graphics3D[Sphere[], ClipPlanes->{-1,0,0,0}]");
    assertTrue(polygons(kept) > 0 && polygons(other) > 0, "each keeps a half");
    assertTrue(!kept.equals(other), "and they are not the same half");
  }

  /** A plane that misses the scene leaves it alone; one past it takes everything. */
  @Test
  public void aPlaneOutsideTheSceneIsAllOrNothing() {
    assertEquals(polygons(svg("Graphics3D[Sphere[]]")),
        polygons(svg("Graphics3D[Sphere[], ClipPlanes->{1,0,0,10}]")),
        "wholly on the kept side, so nothing is cut");
    assertEquals(0, polygons(svg("Graphics3D[Sphere[], ClipPlanes->{1,0,0,-10}]")),
        "wholly on the far side, so nothing survives");
  }

  /** {@code ClipPlanesStyle} draws the plane; without it the cut is left open. */
  @Test
  public void clipPlanesStyleDrawsThePlane() {
    String bare = svg("Graphics3D[Sphere[], ClipPlanes->{1,0,0,0}]");
    String styled = svg("Graphics3D[Sphere[], ClipPlanes->{1,0,0,0}, ClipPlanesStyle->Blue]");
    assertEquals(polygons(bare) + 1, polygons(styled), "exactly one more face: the plane");

    boolean blue = false;
    Matcher m = Pattern.compile("<polygon[^>]*fill=\"#([0-9a-f]{6})\"").matcher(styled);
    while (m.find()) {
      int rgb = Integer.parseInt(m.group(1), 16);
      if ((rgb & 0xFF) > 200 && ((rgb >> 16) & 0xFF) < 60) {
        blue = true;
      }
    }
    assertTrue(blue, "and it is blue");

    assertTrue(scene("Graphics3D[Sphere[], ClipPlanes->{1,0,0,0}, ClipPlanesStyle->Blue]")
        .contains("\"clipPlanesStyle\":[{\"color\":255"), "the interactive output is told too");
  }

  /** One style per plane, taken in the order the planes were given. */
  @Test
  public void eachPlaneMayHaveItsOwnStyle() {
    String scene = scene("Graphics3D[Sphere[], ClipPlanes->{{1,0,0,0},{0,1,0,0}},"
        + " ClipPlanesStyle->{Blue,Green}]");
    assertTrue(scene.contains("\"clipPlanesStyle\":[{\"color\":255,"), "the first is blue");
    assertTrue(scene.contains("{\"color\":65280,"), "the second is green");
  }

  /** The frame around the scene is not part of what gets cut. */
  @Test
  public void theBoxAndAxesSurviveTheCut() {
    String svg = svg("Graphics3D[Sphere[], Axes->True, ClipPlanes->{1,0,0,0}]");
    assertTrue(svg.split("<polyline", -1).length - 1 > 8,
        "the box and the axes are still drawn around the cut scene");
    assertTrue(svg.contains("<text"), "and the tick labels with them");
  }

  /** Whatever is clipped, the output stays one well formed picture. */
  @Test
  public void theOutputStaysWellFormed() {
    for (String option : new String[] {"ClipPlanes->{1,0,0,0}",
        "ClipPlanes->{{1,0,0,0},{0,1,0,0},{0,0,1,0}}",
        "ClipPlanes->InfinitePlane[{{0,0,0},{1,1,0},{0,1,1}}], ClipPlanesStyle->Opacity[0.5]",
        "ClipPlanes->{1,1,1,0}, ClipPlanesStyle->Red"}) {
      String svg = svg("Graphics3D[{Orange, Sphere[]}, Axes->True, " + option + "]");
      assertTrue(svg.startsWith("<svg "), option);
      assertEquals(1, svg.split("<svg\\b", -1).length - 1, option);
      assertTrue(svg.contains("</svg>"), option);
    }
  }
}
