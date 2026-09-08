package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
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
import org.matheclipse.core.interfaces.IExpr;

/**
 * What the static picture shares with the interactive one: its colours and its drawing order.
 *
 * <p>
 * Both outputs are built from the same scene, and they are meant to be the same picture. Two things
 * used to stop them being that.
 *
 * <p>
 * The colours were worked out by multiplying sRGB numbers together. The interactive output is
 * three.js, which takes every colour out of sRGB first, adds the light up in linear, and puts the
 * result back into sRGB at the end. That is a different calculation, and it does not differ by a
 * constant: it bends the whole range, so the two pictures disagreed most in the mid tones. A lit
 * red sphere came out at a flat {@code #ff0000} here while the interactive view showed it muted.
 *
 * <p>
 * The drawing order sorted faces by the depth of their middle, which stops describing a face once
 * the face is large. A floor spanning the picture has its middle nearer the camera than a small box
 * standing on it, so the floor painted over the box.
 */
public class SVGShadingAndOrderTest {

  /** The whole reference example, which is where both faults were noticed. */
  private static final String REFERENCE =
      "Graphics3D[{Blue, Cylinder[], Red, Sphere[{0, 0, 2}], StandardGray, Thick, Dashed,"
          + " Line[{{-2, 0, 2}, {2, 0, 2}, {0, 0, 4}, {-2, 0, 2}}],"
          + " Yellow, Polygon[{{-3, -3, -2}, {-3, 3, -2}, {3, 3, -2}, {3, -3, -2}}],"
          + " Green, Opacity[.3], Cuboid[{-2, -2, -2}, {2, 2, -1}]}]";

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
    IExpr graphic = evaluator.eval(input);
    assertTrue(graphic.isAST(), input + " did not evaluate to a graphic: " + graphic);
    return SVGGraphics3D.toSVG((IAST) graphic);
  }

  /** Every polygon fill in the picture, in the order they are painted. */
  private static List<int[]> fills(String svg) {
    List<int[]> colors = new ArrayList<>();
    Matcher m = Pattern.compile("<polygon[^>]*fill=\"#([0-9a-f]{6})\"").matcher(svg);
    while (m.find()) {
      int rgb = Integer.parseInt(m.group(1), 16);
      colors.add(new int[] {(rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF});
    }
    return colors;
  }

  /**
   * A lit surface is never left at its own full strength.
   *
   * <p>
   * For the default lights no face receives the whole of every lamp at once, so a red sphere cannot
   * contain a face of pure {@code #ff0000}. It did, because multiplying the sRGB numbers straight
   * through let the lights add up past one and clip.
   */
  @Test
  public void aLitSurfaceIsShadedRatherThanLeftAtFullStrength() {
    for (int[] rgb : fills(svg("Graphics3D[{Red, Sphere[]}]"))) {
      assertTrue(rgb[0] < 250 || rgb[1] > 0 || rgb[2] > 0,
          "a face of pure red means the lighting saturated instead of shading");
    }
  }

  /** The lights are coloured, so a white solid picks their colour up rather than staying grey. */
  @Test
  public void theColouredLightsReachTheSurface() {
    List<int[]> colors = fills(svg("Graphics3D[Cuboid[]]"));
    boolean tinted = false;
    for (int[] rgb : colors) {
      if (rgb[0] != rgb[1] || rgb[1] != rgb[2]) {
        tinted = true;
      }
    }
    assertTrue(tinted, "the faces of a white box should carry the colour of the lights");
  }

  /**
   * Shading has to be monotonic: a brighter base cannot come back darker.
   *
   * <p>
   * This is what a wrongly shaped transfer curve breaks, and it holds whatever the curve's
   * steepness, so it pins the fix without pinning the exact numbers.
   */
  @Test
  public void aBrighterBaseColorStaysBrighter() {
    int darkest = brightest(fills(svg("Graphics3D[{GrayLevel[0.2], Sphere[]}]")));
    int middle = brightest(fills(svg("Graphics3D[{GrayLevel[0.5], Sphere[]}]")));
    int lightest = brightest(fills(svg("Graphics3D[{GrayLevel[0.9], Sphere[]}]")));
    assertTrue(darkest < middle,
        "0.2 should shade darker than 0.5, got " + darkest + " and " + middle);
    assertTrue(middle < lightest,
        "0.5 should shade darker than 0.9, got " + middle + " and " + lightest);
  }

  private static int brightest(List<int[]> colors) {
    int best = 0;
    for (int[] rgb : colors) {
      best = Math.max(best, rgb[0] + rgb[1] + rgb[2]);
    }
    return best;
  }

  /** One painted polygon: its corners on screen and the colour it was filled with. */
  private static final class Painted {
    double[] xs;
    double[] ys;
    int red;
    int green;
    int blue;

    double area() {
      double twice = 0;
      for (int i = 0, j = xs.length - 1; i < xs.length; j = i++) {
        twice += (xs[j] + xs[i]) * (ys[j] - ys[i]);
      }
      return Math.abs(twice) / 2.0;
    }

    double[] centre() {
      double x = 0;
      double y = 0;
      for (int i = 0; i < xs.length; i++) {
        x += xs[i];
        y += ys[i];
      }
      return new double[] {x / xs.length, y / ys.length};
    }

    boolean covers(double[] point) {
      boolean inside = false;
      for (int i = 0, j = xs.length - 1; i < xs.length; j = i++) {
        if ((ys[i] > point[1]) != (ys[j] > point[1])
            && point[0] < (xs[j] - xs[i]) * (point[1] - ys[i]) / (ys[j] - ys[i]) + xs[i]) {
          inside = !inside;
        }
      }
      return inside;
    }

    boolean isYellow() {
      return red > 80 && green > 60 && blue < red / 3;
    }

    boolean isGreen() {
      return green > 60 && red < green && blue < green;
    }
  }

  /** Every painted polygon, in the order they are drawn. */
  private static List<Painted> painted(String svg) {
    List<Painted> out = new ArrayList<>();
    Matcher m =
        Pattern.compile("<polygon points=\"([^\"]*)\"[^>]*fill=\"#([0-9a-f]{6})\"").matcher(svg);
    while (m.find()) {
      String[] pairs = m.group(1).trim().split("\\s+");
      Painted face = new Painted();
      face.xs = new double[pairs.length];
      face.ys = new double[pairs.length];
      for (int i = 0; i < pairs.length; i++) {
        String[] xy = pairs[i].split(",");
        face.xs[i] = Double.parseDouble(xy[0]);
        face.ys[i] = Double.parseDouble(xy[1]);
      }
      int rgb = Integer.parseInt(m.group(2), 16);
      face.red = (rgb >> 16) & 0xFF;
      face.green = (rgb >> 8) & 0xFF;
      face.blue = rgb & 0xFF;
      out.add(face);
    }
    return out;
  }

  /**
   * The floor spanning the picture is painted before the box standing on it.
   *
   * <p>
   * This is the failure exactly: the floor is two triangles covering most of the picture, and its
   * middle sits nearer the camera than the box's, so it sorted to the end and painted a yellow
   * wedge over the green box standing on it. The box is above the floor, so no part of the floor
   * may be painted over any part of the box - which is asked here of the faces themselves, by
   * looking for a floor triangle painted after a green face whose middle it covers.
   */
  @Test
  public void aLargeFloorIsNeverPaintedOverTheBoxStandingOnIt() {
    List<Painted> faces = painted(svg(REFERENCE));
    double largest = 0;
    for (Painted face : faces) {
      largest = Math.max(largest, face.area());
    }

    List<double[]> greenCentres = new ArrayList<>();
    int covered = 0;
    for (Painted face : faces) {
      if (face.isGreen()) {
        greenCentres.add(face.centre());
        continue;
      }
      // only the floor's own two triangles are anywhere near the size of the whole picture
      if (face.isYellow() && face.area() > largest / 2) {
        for (double[] centre : greenCentres) {
          if (face.covers(centre)) {
            covered++;
          }
        }
      }
    }
    assertTrue(!greenCentres.isEmpty(), "the green box has to be in the picture");
    assertEquals(0, covered,
        covered + " faces of the box were painted over by the floor it stands on");
  }

  /**
   * The floor is painted in one go, with nothing laid between its two halves.
   *
   * <p>
   * A square floor is drawn as two triangles, and the box standing on it has an underside in
   * exactly the same plane. There is no depth between them to sort by, so the box's half
   * transparent underside was ordered between the floor's two triangles: it tinted the first one
   * green, and the second was then painted over that in solid yellow. The two halves of one flat
   * floor ended up different colours and the join showed as a hard diagonal across it - which is
   * the artifact this pins, and it is invisible to a test that only samples the middle of a face.
   */
  @Test
  public void theFloorIsPaintedInOneGo() {
    List<Painted> faces = painted(svg(REFERENCE));
    double largest = 0;
    for (Painted face : faces) {
      largest = Math.max(largest, face.area());
    }
    List<Integer> floor = new ArrayList<>();
    for (int i = 0; i < faces.size(); i++) {
      Painted face = faces.get(i);
      if (face.isYellow() && face.area() > largest / 2) {
        floor.add(i);
      }
    }
    assertEquals(2, floor.size(), "the floor square is drawn as two big triangles");
    assertEquals(1, floor.get(1) - floor.get(0),
        "nothing may be painted between the two halves of the floor, but "
            + (floor.get(1) - floor.get(0) - 1) + " face(s) were");
  }

  /** Ordering by planes must not cut anything up, or half transparent faces grow seams. */
  @Test
  public void theOrderingDoesNotSplitAnyFaces() {
    String plain = svg("Graphics3D[{Yellow, Polygon[{{-3,-3,-2},{-3,3,-2},{3,3,-2},{3,-3,-2}}]}]");
    assertEquals(2, fills(plain).size(),
        "a square is two triangles before and after ordering, never more");
  }

  /** Whatever the ordering does, the output stays a single well formed picture. */
  @Test
  public void theOutputIsStillOneWellFormedPicture() {
    for (String input : new String[] {REFERENCE, "Graphics3D[Sphere[]]",
        "Plot3D[Sin[x*y],{x,-1,1},{y,-1,1},PlotPoints->8]",
        "Graphics3D[{Opacity[0.3], Cuboid[], Red, Sphere[{0,0,2}]}]"}) {
      String svg = svg(input);
      assertTrue(svg.startsWith("<svg "), input);
      assertEquals(1, svg.split("<svg\\b", -1).length - 1, "exactly one svg element for " + input);
      assertTrue(svg.contains("</svg>"));
    }
  }
}
