package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
 * The camera the static picture is drawn through.
 *
 * <p>
 * It used to have none worth the name: everything was projected in parallel and the result was
 * fitted to the canvas afterwards. A fit undoes whatever the camera was asked to do - a pan comes
 * straight back out, a zoom is cancelled - so {@code ViewCenter}, {@code ViewAngle},
 * {@code ViewRange}, {@code SphericalRegion} and the choice of projection all had nothing to bite
 * on. The frustum is the one the interactive output builds, so the two frame a scene alike.
 */
public class CameraFrustumTest {

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

  private static String svg(String input) {
    return SVGGraphics3D.toSVG((IAST) evaluator.eval(input));
  }

  /** How wide the drawing came out. */
  private static double width(String svg) {
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;
    Matcher m = Pattern.compile("(-?[0-9.]+),(-?[0-9.]+)").matcher(svg);
    while (m.find()) {
      double x = Double.parseDouble(m.group(1));
      min = Math.min(min, x);
      max = Math.max(max, x);
    }
    return max - min;
  }

  private static int polygons(String svg) {
    return svg.split("<polygon", -1).length - 1;
  }

  private static final String CUBE = "Graphics3D[{Orange, Cuboid[]}, ImageSize->300";

  /** The two projections are different pictures of the same scene. */
  @Test
  public void perspectiveAndParallelDiffer() {
    assertNotEquals(svg(CUBE + "]"), svg(CUBE + ", ViewProjection->\"Orthographic\"]"));
  }

  /**
   * The field of view is what a perspective camera sees through, and means nothing to a parallel
   * one.
   *
   * <p>
   * That is the difference between the two projections stated as something measurable, and it
   * holds whatever the rest of the framing does.
   */
  @Test
  public void viewAngleActsOnlyOnAPerspectiveCamera() {
    assertNotEquals(width(svg(CUBE + ", ViewAngle->0.2]")),
        width(svg(CUBE + ", ViewAngle->1.2]")), 1e-6,
        "a wider field of view takes in more, so the scene is drawn smaller");

    assertEquals(width(svg(CUBE + ", ViewProjection->\"Orthographic\", ViewAngle->0.2]")),
        width(svg(CUBE + ", ViewProjection->\"Orthographic\", ViewAngle->1.2]")), 1e-6,
        "a parallel camera has no field of view to widen");
  }

  /** A narrower field of view draws the scene larger, since the camera closes in on it. */
  @Test
  public void aNarrowerFieldOfViewFillsMoreOfThePicture() {
    assertTrue(width(svg(CUBE + ", ViewAngle->0.3]")) > width(svg(CUBE + ", ViewAngle->1.0]")));
  }

  /** {@code ViewCenter} is a pan, which a fit would have undone. */
  @Test
  public void viewCenterMovesThePicture() {
    assertNotEquals(svg(CUBE + ", ViewCenter->{0,0,0}]"), svg(CUBE + ", ViewCenter->{1,1,1}]"));
  }

  /**
   * Fitting the sphere around the scene is a different framing from fitting the scene.
   *
   * <p>
   * The camera is never brought closer than the point of view asks for, so with the default
   * viewpoint - well back from the scene - the two framings both leave the scene comfortably
   * inside and settle on the same distance. The difference shows when the fit is what decides,
   * which is why the viewpoint here is close in.
   */
  @Test
  public void sphericalRegionChangesTheFraming() {
    String close = "Graphics3D[{Orange, Cuboid[]}, ImageSize->300, ViewPoint->{0,0,1}";
    assertNotEquals(width(svg(close + "]")), width(svg(close + ", SphericalRegion->True]")), 1e-6,
        "the sphere around a cube is wider than the cube, so it is framed from further back");

    assertNotEquals(width(svg(CUBE + ", ViewProjection->\"Orthographic\"]")),
        width(svg(CUBE + ", ViewProjection->\"Orthographic\", SphericalRegion->True]")), 1e-6,
        "a parallel camera has no distance to settle, so it always tells the two apart");
  }

  /**
   * {@code ViewRange} keeps only what lies between two distances from the camera.
   *
   * <p>
   * Two cubes, one at each end of a deep scene. The camera stands far enough back for the whole
   * scene to fit its field of view, which for a scene this deep is further than the point of view
   * alone would put it, so the limit that separates the two cubes is well beyond the scene's own
   * size. What matters is that some such limit exists and keeps one cube and not the other.
   */
  @Test
  public void viewRangeClipsByDistance() {
    String deep = "Graphics3D[{Orange, Cuboid[{0,0,0},{1,1,1}], Cuboid[{0,0,8},{1,1,9}]},"
        + " ViewPoint->{0,0,1}, ImageSize->300";
    int all = polygons(svg(deep + "]"));
    int nearOnly = polygons(svg(deep + ", ViewRange->{0,15}]"));
    assertTrue(nearOnly > 0 && nearOnly < all,
        "one cube of the two survives: " + nearOnly + " faces of " + all);
    assertEquals(0, polygons(svg(deep + ", ViewRange->{0,0.001}]")),
        "a range that stops in front of everything leaves nothing");
    assertEquals(all, polygons(svg(deep + ", ViewRange->{0,1000}]")),
        "and one that reaches past everything keeps it all");
  }

  /**
   * {@code ViewMatrix} says outright what the other view options describe, so it replaces them.
   */
  @Test
  public void viewMatrixOverridesTheOtherViewOptions() {
    String matrix = "{{{1,0,0,0},{0,1,0,0},{0,0,1,-3},{0,0,0,1}},"
        + "{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,-1,0}}}";
    String fromFront = svg(CUBE + ", ViewMatrix->" + matrix + ", ViewPoint->{0,0,3}]");
    String fromSide = svg(CUBE + ", ViewMatrix->" + matrix + ", ViewPoint->{3,0,0}]");
    assertEquals(fromFront, fromSide, "the matrix decides the view, not ViewPoint");
    assertNotEquals(svg(CUBE + "]"), fromFront, "and it is not the ordinary view either");
  }

  /** The scene carries the camera so the interactive output frames it the same way. */
  @Test
  public void theSceneCarriesTheCamera() {
    String scene = WebGLGraphics3D.generateJSON((IAST) evaluator.eval(
        "Graphics3D[Cuboid[], ViewAngle->0.5, SphericalRegion->True, ViewRange->{1,9},"
            + " ViewProjection->\"Orthographic\"]"));
    assertTrue(scene.contains("\"viewProjection\":\"Orthographic\""));
    assertTrue(scene.contains("\"sphericalRegion\":true"));
    assertTrue(scene.contains("\"viewRange\":[1.0,9.0]"));
    assertTrue(scene.contains("\"viewAngle\":"), "in degrees, as the renderer wants it");
  }

  /** Whatever the camera, the output stays one well formed picture. */
  @Test
  public void theOutputStaysWellFormed() {
    for (String option : new String[] {"ViewProjection->\"Orthographic\"", "ViewAngle->0.4",
        "ViewCenter->{0.5,0.5,0.5}", "SphericalRegion->True", "ViewRange->{0.5,20}",
        "ViewMatrix->{{{1,0,0,0},{0,1,0,0},{0,0,1,-4},{0,0,0,1}},"
            + "{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,-1,0}}}"}) {
      String svg = svg("Graphics3D[{Orange, Sphere[]}, Axes->True, ImageSize->300, " + option
          + "]");
      assertTrue(svg.startsWith("<svg "), option);
      assertEquals(1, svg.split("<svg\\b", -1).length - 1, option);
      assertTrue(svg.contains("</svg>"), option);
    }
  }
}
