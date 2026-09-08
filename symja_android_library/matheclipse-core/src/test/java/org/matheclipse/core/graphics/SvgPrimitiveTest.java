package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.graphics.svg.Bounds2D;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.PrimitiveCollector;
import org.matheclipse.core.graphics.svg.Style2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tests on the collected primitives rather than the rendered SVG string.
 *
 * <p>
 * These assertions survive changes to the exact geometry the renderer emits, so they stay useful
 * while the output is still being improved.
 */
public class SvgPrimitiveTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    // Other test classes lower these shared limits; a density style plot legitimately builds a
    // large expression (one rectangle per cell), so the ceiling has to be restored here rather
    // than inherited from whichever test ran last.
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
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
    // Other tests in the suite leave values assigned to common one letter symbols, and a plot
    // whose variable is already bound does not evaluate to a graphic. Clearing them keeps these
    // tests independent of the order the suite happens to run in.
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  /** Collect the content of a {@code Graphics[...]} expression. */
  private static List<Prim2D> collect(String input) {
    IExpr result = evaluator.eval(input);
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) result).arg1(), new Style2D());
    return collector.primitives();
  }

  private static Bounds2D bounds(String input) {
    Bounds2D b = new Bounds2D();
    for (Prim2D p : collect(input)) {
      p.accumulate(b);
    }
    return b;
  }

  // ------------------------------------------------------------- geometry

  @Test
  public void testCircleKeepsBothRadii() {
    List<Prim2D> prims = collect("Graphics[Circle[{1, 2}, {4, 3}]]");
    assertEquals(1, prims.size());
    Prim2D.EllipsePrim e = (Prim2D.EllipsePrim) prims.get(0);
    assertEquals(4.0, e.rx, 1e-9);
    assertEquals(3.0, e.ry, 1e-9);
    assertFalse(e.filled, "Circle is stroked, not filled");
  }

  @Test
  public void testDiskIsFilled() {
    Prim2D.EllipsePrim e = (Prim2D.EllipsePrim) collect("Graphics[Disk[{0, 0}, 2]]").get(0);
    assertTrue(e.filled);
    assertEquals(2.0, e.rx, 1e-9);
  }

  @Test
  public void testCircleAnglesAreKept() {
    Prim2D.EllipsePrim e =
        (Prim2D.EllipsePrim) collect("Graphics[Circle[{0, 0}, 1, {0, Pi/2}]]").get(0);
    assertNotNull(e.angles, "the angle range must survive collection");
    assertEquals(0.0, e.angles[0], 1e-9);
    assertEquals(Math.PI / 2, e.angles[1], 1e-9);
    assertFalse(e.isFullTurn());
  }

  @Test
  public void testAnnulusHasInnerRadius() {
    Prim2D.EllipsePrim e = (Prim2D.EllipsePrim) collect("Graphics[Annulus[{0, 0}, {1, 3}]]").get(0);
    assertTrue(e.isAnnulus());
    assertEquals(1.0, e.innerRx, 1e-9);
    assertEquals(3.0, e.rx, 1e-9);
  }

  @Test
  public void testEllipseBoundsUseBothRadii() {
    Bounds2D b = bounds("Graphics[Circle[{0, 0}, {4, 1}]]");
    assertEquals(-4.0, b.xMin, 1e-9);
    assertEquals(4.0, b.xMax, 1e-9);
    assertEquals(-1.0, b.yMin, 1e-9);
    assertEquals(1.0, b.yMax, 1e-9);
  }

  /** Every primitive that draws must also contribute to the plot range. */
  @Test
  public void testAllDrawnPrimitivesAffectBounds() {
    String[] inputs = {"Graphics[Parallelogram[{0, 0}, {{2, 0}, {0, 3}}]]", //
        "Graphics[SSSTriangle[3, 4, 5]]", //
        "Graphics[RegularPolygon[{5, 5}, 2, 6]]", //
        "Graphics[StadiumShape[{{0, 0}, {4, 0}}, 1]]", //
        "Graphics[BSplineCurve[{{0, 0}, {1, 2}, {2, 0}}]]", //
        "Graphics[BezierCurve[{{0, 0}, {1, 2}, {2, 0}, {3, 1}}]]", //
        "Graphics[Raster[{{0, 1}, {1, 0}}]]"};
    for (String input : inputs) {
      Bounds2D b = bounds(input);
      assertFalse(b.isEmpty(), input + " draws but contributes no bounds");
      assertTrue(b.width() > 0 || b.height() > 0, input + " has a degenerate bounding box");
    }
  }

  // ----------------------------------------------------------- transforms

  @Test
  public void testTranslateProducesOneCopyPerVector() {
    List<Prim2D> prims = collect("Graphics[Translate[Disk[{0, 0}, 1], {{0, 0}, {5, 0}, {10, 0}}]]");
    assertEquals(3, prims.size());
    assertEquals(0.0, ((Prim2D.EllipsePrim) prims.get(0)).cx, 1e-9);
    assertEquals(5.0, ((Prim2D.EllipsePrim) prims.get(1)).cx, 1e-9);
    assertEquals(10.0, ((Prim2D.EllipsePrim) prims.get(2)).cx, 1e-9);
  }

  @Test
  public void testScaleAboutExplicitCentre() {
    Prim2D.RectPrim r =
        (Prim2D.RectPrim) collect("Graphics[Scale[Rectangle[{1, 1}, {2, 2}], 2, {0, 0}]]").get(0);
    assertEquals(2.0, r.x1, 1e-9);
    assertEquals(4.0, r.x2, 1e-9);
  }

  @Test
  public void testRotateTurnsRectangleIntoPolygon() {
    Prim2D p = collect("Graphics[Rotate[Rectangle[{0, 0}, {2, 1}], Pi/4]]").get(0);
    assertTrue(p instanceof Prim2D.PolygonPrim,
        "a rotated rectangle is no longer axis aligned, so it must become a polygon");
    assertEquals(4, ((Prim2D.PolygonPrim) p).outer.size());
  }

  @Test
  public void testRotatingADiskKeepsItACircle() {
    Prim2D p = collect("Graphics[Rotate[Disk[{0, 0}, 2], Pi/3]]").get(0);
    assertTrue(p instanceof Prim2D.EllipsePrim, "a rotated circle is still a circle");
    assertEquals(2.0, ((Prim2D.EllipsePrim) p).rx, 1e-9);
  }

  // ------------------------------------------------------------ directives

  @Test
  public void testStyleScopeDoesNotLeak() {
    // the Blue inside the nested list must not colour the disk that follows it
    List<Prim2D> prims = collect("Graphics[{Red, {Blue, Disk[{0, 0}, 1]}, Disk[{3, 0}, 1]}]");
    assertEquals(2, prims.size());
    assertEquals(Color.BLUE.getRGB(), prims.get(0).style.effectiveFill().getRGB());
    assertEquals(Color.RED.getRGB(), prims.get(1).style.effectiveFill().getRGB());
  }

  @Test
  public void testOpacityAppliesToLaterPrimitivesOnly() {
    List<Prim2D> prims = collect("Graphics[{Disk[{0, 0}, 1], Opacity[0.5], Disk[{3, 0}, 1]}]");
    assertEquals(1.0, prims.get(0).style.opacity, 1e-9);
    assertEquals(0.5, prims.get(1).style.opacity, 1e-9);
  }

  @Test
  public void testTransparentIsNotOpaqueBlack() {
    Prim2D p = collect("Graphics[{Transparent, Disk[]}]").get(0);
    assertEquals(0, p.style.effectiveFill().getAlpha(), "Transparent must have zero alpha");
  }

  @Test
  public void testArrowheadsSpreadFromTailToTip() {
    Prim2D p = collect("Graphics[{Arrowheads[{-0.06, 0.06}], Arrow[{{0, 0}, {1, 1}}]}]").get(0);
    List<Style2D.ArrowHead> heads = p.style.arrowHeads;
    assertNotNull(heads);
    assertEquals(2, heads.size());
    assertEquals(0.0, heads.get(0).position, 1e-9, "the first head sits at the tail");
    assertTrue(heads.get(0).reversed, "a negative size points the head backwards");
    assertEquals(1.0, heads.get(1).position, 1e-9, "the last head sits at the tip");
    assertFalse(heads.get(1).reversed);
  }

  @Test
  public void testFontDirectives() {
    Prim2D.TextPrim t = (Prim2D.TextPrim) collect(
        "Graphics[Text[Style[\"x\", Bold, Italic, FontSize -> 20], {0, 0}]]").get(0);
    assertEquals("bold", t.style.fontWeight);
    assertEquals("italic", t.style.fontStyle);
    assertEquals(20.0, t.style.fontSize, 1e-9);
  }

  // --------------------------------------------------------------- colours

  @Test
  public void testColorForms() {
    assertEquals(new Color(255, 0, 0).getRGB(),
        ColorUtil.parse(evaluator.eval("RGBColor[1, 0, 0]")).getRGB());
    assertEquals(new Color(255, 0, 0).getRGB(),
        ColorUtil.parse(evaluator.eval("RGBColor[{1, 0, 0}]")).getRGB(),
        "the list argument form must parse");
    assertEquals(128, ColorUtil.parse(evaluator.eval("RGBColor[1, 0, 0, 0.5]")).getAlpha(), 2);
    assertEquals(128, ColorUtil.parse(evaluator.eval("Opacity[0.5, Red]")).getAlpha(), 2);
    Color blend = ColorUtil.parse(evaluator.eval("Blend[{Red, Blue}, 0.5]"));
    assertNotNull(blend, "Blend must resolve to a colour");
    assertEquals(128, blend.getRed(), 2);
    assertEquals(128, blend.getBlue(), 2);
  }

  @Test
  public void testLightGreenIsLighterThanGreen() {
    Color green = ColorUtil.parse(evaluator.eval("Green"));
    Color lightGreen = ColorUtil.parse(evaluator.eval("LightGreen"));
    assertTrue(lightGreen.getRed() > green.getRed() && lightGreen.getBlue() > green.getBlue(),
        "LightGreen must be a pale tint, not a duplicate of Green");
  }

  @Test
  public void testMalformedDirectivesDoNotThrow() {
    String[] inputs = {"Graphics[{RGBColor[], Disk[]}]", "Graphics[{Hue[], Disk[]}]",
        "Graphics[{EdgeForm[], Rectangle[]}]", "Graphics[{FaceForm[], Rectangle[]}]",
        "Graphics[Text[\"only\"]]", "Graphics[Line[{}]]", "Graphics[Polygon[{}]]"};
    for (String input : inputs) {
      collect(input);
    }
  }

  // ------------------------------------------------------------- rasters

  /** Collect the content of any graphics producing expression. */
  private static List<Prim2D> collectPlot(String input) {
    IExpr result = evaluator.eval(input);
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) result).arg1(), new Style2D());
    return collector.primitives();
  }

  private static Prim2D.RasterPrim singleRaster(String input) {
    List<Prim2D> prims = collectPlot(input);
    Prim2D.RasterPrim raster = null;
    for (Prim2D p : prims) {
      if (p instanceof Prim2D.RasterPrim) {
        assertNotNull(p);
        raster = (Prim2D.RasterPrim) p;
      }
    }
    assertNotNull(raster, input + " should emit a Raster");
    return raster;
  }

  /**
   * A grid of cells must be one raster, not one rectangle each: that is what the reference
   * rendering produces and it keeps the expression small enough to stay under the element limit of
   * the SVG rasterizer.
   */
  @Test
  public void testGridPlotsEmitASingleRaster() {
    String[] inputs = {"MatrixPlot[Table[i*j, {i, 6}, {j, 6}]]", //
        "ArrayPlot[Table[Mod[i + j, 2], {i, 6}, {j, 6}]]", //
        "DensityPlot[x*y, {x, 0, 1}, {y, 0, 1}]", //
        "ListDensityPlot[Table[i*j, {i, 6}, {j, 6}]]", //
        "ComplexPlot[z, {z, -1 - I, 1 + I}]"};
    for (String input : inputs) {
      List<Prim2D> prims = collectPlot(input);
      int rasters = 0;
      int rectangles = 0;
      for (Prim2D p : prims) {
        if (p instanceof Prim2D.RasterPrim) {
          rasters++;
        } else if (p instanceof Prim2D.RectPrim) {
          rectangles++;
        }
      }
      assertEquals(1, rasters, input + " should emit exactly one Raster");
      assertEquals(0, rectangles, input + " should not emit per cell rectangles");
    }
  }

  /**
   * The first row of a matrix is drawn at the top, while a {@code Raster} counts its rows from the
   * bottom. Getting that reversal wrong flips the picture, which is easy to miss on symmetric test
   * data, so it is checked on deliberately asymmetric input.
   */
  @Test
  public void testMatrixPlotDrawsFirstRowAtTop() {
    // row 1 is all zeros, row 2 all ones; the two must not come out swapped
    Prim2D.RasterPrim raster = singleRaster("MatrixPlot[{{0, 0}, {1, 1}}]");
    assertEquals(2, raster.cells.length);
    Color bottom = raster.cells[0][0];
    Color top = raster.cells[1][0];
    // the matrix colour map runs from pale at 0 to dark at 1, so the bottom row is the darker one
    assertTrue(luminance(top) > luminance(bottom),
        "matrix row 1 must be drawn at the top: got top=" + top + " bottom=" + bottom);
  }

  /**
   * A matrix whose values span orders of magnitude must still use the whole colour range.
   *
   * <p>
   * Scaling linearly between the smallest and largest entry put almost every cell of
   * {@code Binomial} within a hair of the pale end, so the plot read as a flat field with one
   * bright spot. Ranking the values spends the colour range on where the data actually is.
   */
  @Test
  public void testMatrixPlotSpreadsWideDynamicRange() {
    Prim2D.RasterPrim raster =
        singleRaster("MatrixPlot[Table[Binomial[n, k], {n, 0, 25}, {k, 0, n}]]");
    double darkest = Double.MAX_VALUE;
    double lightest = -Double.MAX_VALUE;
    int opaque = 0;
    int paleCells = 0;
    for (Color[] row : raster.cells) {
      for (Color c : row) {
        if (c == null || c.getAlpha() == 0) {
          continue;
        }
        opaque++;
        double l = luminance(c);
        darkest = Math.min(darkest, l);
        lightest = Math.max(lightest, l);
        if (l > 0.9 * 255) {
          paleCells++;
        }
      }
    }
    final double spread = lightest - darkest;
    final int painted = opaque;
    final int pale = paleCells;
    assertTrue(painted > 100, "expected the whole triangle to be painted, got " + painted);
    assertTrue(spread > 120,
        () -> "expected the colour range to be used, spread was only " + spread);
    assertTrue(pale < painted / 2,
        () -> "most cells collapsed to the pale end: " + pale + " of " + painted);
  }

  /** {@code DensityPlot}'s y axis points up, so the largest value sits at the top. */
  @Test
  public void testDensityPlotYAxisPointsUp() {
    Prim2D.RasterPrim raster = singleRaster("DensityPlot[y, {x, 0, 1}, {y, 0, 1}]");
    int rows = raster.cells.length;
    Color bottom = raster.cells[0][0];
    Color top = raster.cells[rows - 1][0];
    assertTrue(luminance(top) > luminance(bottom),
        "larger y must be drawn higher: got top=" + top + " bottom=" + bottom);
  }

  /**
   * {@code ListDensityPlot} shares the y axis of the other list plots, so the first row of the
   * matrix is drawn at the bottom. {@code ArrayPlot} draws that same row at the top, and the two
   * conventions are deliberately opposite.
   */
  @Test
  public void testListDensityPlotFirstRowIsAtTheBottom() {
    Prim2D.RasterPrim raster = singleRaster("ListDensityPlot[{{0, 0}, {1, 1}}]");
    Color bottom = raster.cells[0][0];
    Color top = raster.cells[raster.cells.length - 1][0];
    assertTrue(luminance(top) > luminance(bottom),
        "the second row holds the larger value and must be drawn higher: got top=" + top
            + " bottom=" + bottom);

    Prim2D.RasterPrim arrayPlot = singleRaster("ArrayPlot[{{0, 0}, {1, 1}}]");
    Color arrayBottom = arrayPlot.cells[0][0];
    Color arrayTop = arrayPlot.cells[arrayPlot.cells.length - 1][0];
    assertTrue(luminance(arrayTop) > luminance(arrayBottom),
        "ArrayPlot draws the first row at the top, where the smaller value is the paler one: got"
            + " top=" + arrayTop + " bottom=" + arrayBottom);
  }

  /**
   * The sampling grid is finer than the data, so that the default interpolation is drawn as a
   * gradient. {@code InterpolationOrder -> 0} paints the same extent in flat cells instead.
   */
  @Test
  public void testListDensityPlotInterpolatesBetweenTheDataPoints() {
    Prim2D.RasterPrim smooth = singleRaster("ListDensityPlot[{{0, 0}, {1, 1}}]");
    assertTrue(smooth.cells.length > 2,
        "the data must be resampled, got only " + smooth.cells.length + " rows");
    assertTrue(distinctColours(smooth) > 2,
        "a linear ramp must be drawn in more than the two colours of the data");

    Prim2D.RasterPrim flat =
        singleRaster("ListDensityPlot[{{0, 0}, {1, 1}}, InterpolationOrder->0]");
    assertEquals(2, distinctColours(flat),
        "order 0 must paint the two data values and nothing between them");
  }

  /** A list of {@code {x, y, z}} triples is read as scattered data, not as a matrix. */
  @Test
  public void testListDensityPlotAcceptsTriples() {
    Prim2D.RasterPrim raster =
        singleRaster("ListDensityPlot[{{0, 0, 1}, {1, 0, 2}, {0, 1, 3}, {1, 1, 4}}]");
    assertEquals(0.0, raster.x1, 1e-9, "the extent comes from the x values of the triples");
    assertEquals(1.0, raster.x2, 1e-9);
    Color lowerLeft = raster.cells[0][0];
    Color upperRight = raster.cells[raster.cells.length - 1][raster.cells[0].length - 1];
    assertTrue(luminance(upperRight) > luminance(lowerLeft),
        "z grows towards {1, 1}: got upperRight=" + upperRight + " lowerLeft=" + lowerLeft);
  }

  private static int distinctColours(Prim2D.RasterPrim raster) {
    java.util.Set<Color> colours = new java.util.HashSet<>();
    for (Color[] row : raster.cells) {
      for (Color cell : row) {
        if (cell != null) {
          colours.add(cell);
        }
      }
    }
    return colours.size();
  }

  private static double luminance(Color c) {
    return 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
  }

  // ------------------------------------------------------- graphics complex

  @Test
  public void testGraphicsComplexResolvesIndices() {
    List<Prim2D> prims = collect(
        "Graphics[GraphicsComplex[{{0, 0}, {2, 0}, {2, 2}}, {Line[{1, 2, 3}], Point[{1, 3}]}]]");
    assertEquals(2, prims.size());
    Prim2D.LinePrim line = (Prim2D.LinePrim) prims.get(0);
    assertEquals(1, line.segments.size());
    assertEquals(3, line.segments.get(0).size());
    assertEquals(2.0, line.segments.get(0).get(1)[0], 1e-9);
    Prim2D.PointsPrim points = (Prim2D.PointsPrim) prims.get(1);
    assertEquals(2, points.points.size());
    assertEquals(2.0, points.points.get(1)[1], 1e-9);
  }

  // ------------------------------------------------------------- chart layout

  /** The bars of a chart, in the order they were drawn. */
  private static List<Prim2D.RectPrim> bars(String input) {
    List<Prim2D.RectPrim> rects = new java.util.ArrayList<>();
    for (Prim2D p : collectPlot(input)) {
      if (p instanceof Prim2D.RectPrim) {
        rects.add((Prim2D.RectPrim) p);
      }
    }
    return rects;
  }

  @Test
  public void testBarChartDrawsEveryDatasetOfNestedData() {
    // nested data used to fall through the value extraction and leave the chart empty
    List<Prim2D.RectPrim> rects = bars("BarChart[{{1, 4}, {3, 2}}]");
    assertEquals(4, rects.size(), "expected one bar per value across both datasets");
    // the datasets are separated, so the gap between the groups exceeds the gap inside one
    double insideGroup = rects.get(1).x1 - rects.get(0).x2;
    double betweenGroups = rects.get(2).x1 - rects.get(1).x2;
    assertTrue(betweenGroups > insideGroup,
        () -> "expected a wider gap between groups, " + betweenGroups + " vs " + insideGroup);
  }

  @Test
  public void testStackedLayoutPilesSegmentsOnOneBar() {
    List<Prim2D.RectPrim> rects = bars("BarChart[{1, 4, 2}, ChartLayout -> \"Stacked\"]");
    assertEquals(3, rects.size());
    // every segment shares the bar, and each starts where the one below ended
    assertEquals(rects.get(0).x1, rects.get(2).x1, 1e-9);
    assertEquals(1.0, rects.get(1).y1, 1e-9);
    assertEquals(5.0, rects.get(1).y2, 1e-9);
    assertEquals(7.0, rects.get(2).y2, 1e-9);
  }

  @Test
  public void testBarOriginLeftTurnsTheBarsSideways() {
    List<Prim2D.RectPrim> rects = bars("BarChart[{1, 4, 2}, BarOrigin -> Left]");
    assertEquals(3, rects.size());
    // the values now run along x and the categories up y
    assertEquals(0.0, rects.get(1).x1, 1e-9);
    assertEquals(4.0, rects.get(1).x2, 1e-9);
    assertTrue(rects.get(1).y1 > rects.get(0).y1, "expected the categories to climb the y axis");
  }

  // ------------------------------------------------------------ colour function

  @Test
  public void testColorFunctionPaintsAlongTheCurve() {
    List<Prim2D> plain = collectPlot("Plot[Sin[x], {x, 0, 6}]");
    assertEquals(1, plain.size(), "a plain curve is one line");

    List<Prim2D> painted = collectPlot("Plot[Sin[x], {x, 0, 6}, ColorFunction -> \"Rainbow\"]");
    assertTrue(painted.size() > 10,
        () -> "expected the curve broken into coloured steps, got " + painted.size());

    // the steps have to differ in colour, or the option achieved nothing visible
    java.util.Set<Color> colors = new java.util.HashSet<>();
    for (Prim2D p : painted) {
      colors.add(p.style.strokeColor);
    }
    assertTrue(colors.size() > 5, () -> "expected a spread of colours, got " + colors.size());

    // and the painted curve must still cover the same ground as the plain one
    Bounds2D plainBounds = new Bounds2D();
    plain.get(0).accumulate(plainBounds);
    Bounds2D paintedBounds = new Bounds2D();
    for (Prim2D p : painted) {
      p.accumulate(paintedBounds);
    }
    assertEquals(plainBounds.xMin, paintedBounds.xMin, 1e-9);
    assertEquals(plainBounds.xMax, paintedBounds.xMax, 1e-9);
    assertEquals(plainBounds.yMin, paintedBounds.yMin, 1e-9);
    assertEquals(plainBounds.yMax, paintedBounds.yMax, 1e-9);
  }

  /**
   * A colour scheme name is a function of the second argument, the height, so a curve that comes
   * back to the same value comes back to the same colour. It used to be given the first argument
   * instead, which swept the gradient across the picture from left to right whatever was drawn.
   */
  @Test
  public void testAGradientFollowsTheValueRatherThanTheAxis() {
    // sin over a full turn is at zero at both ends and in the middle
    List<Prim2D> painted =
        collectPlot("Plot[Sin[x], {x, 0, 6.28}, PlotPoints -> 41, ColorFunction -> \"Rainbow\"]");
    int n = painted.size();
    assertTrue(n > 20, () -> "expected the curve in steps, got " + n);
    Color first = painted.get(0).style.strokeColor;
    Color middle = painted.get(n / 2).style.strokeColor;
    Color last = painted.get(n - 1).style.strokeColor;
    // the sampling grid does not land exactly on the zero crossing, so the middle piece sits a
    // few percent along the gradient rather than at its start; a colouring by x would put it at
    // the halfway point instead, which is a different colour entirely
    assertColorNear(first, middle, 45, "the curve returns to zero in the middle");
    assertColorNear(first, last, 45, "and again at the end");
    // and the peak, where the value is furthest from where it started, must look nothing like it
    Color peak = painted.get(n / 4).style.strokeColor;
    assertFalse(near(first, peak, 45),
        () -> "the top of the wave should be a different colour from the zero crossing: " + first
            + " against " + peak);

    // and it has to turn back on itself, which a sweep along the axis never does
    int reversals = 0;
    int direction = 0;
    for (int i = 1; i < n; i++) {
      int step = Integer.compare(painted.get(i).style.strokeColor.getRed(),
          painted.get(i - 1).style.strokeColor.getRed());
      if (step != 0) {
        if (direction != 0 && step != direction) {
          reversals++;
        }
        direction = step;
      }
    }
    final int turns = reversals;
    assertTrue(turns >= 3,
        () -> "a colouring by value follows the wave up and down, but it turned " + turns
            + " times");
  }

  private static boolean near(Color a, Color b, int tolerance) {
    return Math.abs(a.getRed() - b.getRed()) <= tolerance
        && Math.abs(a.getGreen() - b.getGreen()) <= tolerance
        && Math.abs(a.getBlue() - b.getBlue()) <= tolerance;
  }

  private static void assertColorNear(Color a, Color b, int tolerance, String why) {
    assertTrue(near(a, b, tolerance), () -> why + ": " + a + " against " + b);
  }

  /** A named colour is a bare symbol, and used to be rejected as if it were not a colour at all. */
  @Test
  public void testAColorFunctionMayAnswerWithANamedColor() {
    List<Prim2D> painted = collectPlot("Plot[Sin[x], {x, 0, 6.28}, PlotPoints -> 41, "
        + "ColorFunction -> (If[#2 > 0, Red, Blue]&), ColorFunctionScaling -> False]");
    java.util.Set<Color> colors = new java.util.HashSet<>();
    for (Prim2D p : painted) {
      colors.add(p.style.strokeColor);
    }
    assertEquals(2, colors.size(), () -> "expected red above the axis and blue below, got " + colors);
    assertTrue(colors.contains(Color.RED) && colors.contains(Color.BLUE));
  }

  /** The whole directive reaches the primitive list, so an opacity applies as well as a colour. */
  @Test
  public void testAColorFunctionMayAnswerWithADirective() {
    List<Prim2D> painted = collectPlot("Plot[Sin[x], {x, 0, 6}, PlotPoints -> 21, "
        + "ColorFunction -> (Directive[Opacity[0.4], Red]&)]");
    assertTrue(painted.size() > 5);
    for (Prim2D p : painted) {
      assertEquals(Color.RED, p.style.strokeColor);
      assertEquals(0.4, p.style.opacity, 1e-9, "the opacity of the directive has to be applied");
    }
  }

  /**
   * A step the function cannot answer for keeps the colour the curve would have had. It used to
   * cost the whole curve its colouring, so one hole left a plain line.
   */
  @Test
  public void testAFailingColorFunctionFallsBackPerStep() {
    List<Prim2D> painted = collectPlot("Plot[x, {x, 0, 6}, PlotPoints -> 41, "
        + "ColorFunction -> (If[#2 > 0.5, Red, Indeterminate]&)]");
    assertTrue(painted.size() > 20, "the curve is still drawn in steps");
    java.util.Set<Color> colors = new java.util.HashSet<>();
    for (Prim2D p : painted) {
      colors.add(p.style.strokeColor);
    }
    assertEquals(2, colors.size(),
        () -> "expected red where the function answered and the plot colour where it did not, got "
            + colors);
    assertTrue(colors.contains(Color.RED));
  }

  /**
   * A parametric curve is given the parameter it was drawn at, which is not recoverable from the
   * coordinates: a closed curve comes back to the same place at a different parameter.
   */
  @Test
  public void testAParametricCurveIsGivenItsParameter() {
    List<Double> byParameter =
        hues("ParametricPlot[{Cos[t], Sin[t]}, {t, 0, 6.28}, PlotPoints -> 24, "
            + "ColorFunction -> (Hue[#3]&)]");
    assertTrue(byParameter.size() > 10, () -> "expected the curve in steps, got " + byParameter);
    assertTrue(isAscending(byParameter),
        () -> "the parameter runs one way along the curve: " + byParameter);

    List<Double> byX = hues("ParametricPlot[{Cos[t], Sin[t]}, {t, 0, 6.28}, PlotPoints -> 24, "
        + "ColorFunction -> (Hue[#1]&)]");
    assertFalse(isAscending(byX),
        "x turns back on itself around a circle, so a colouring by it cannot run one way");
  }

  /**
   * The parameter has to survive the curve being broken. Counting steps from the start of each
   * piece would restart it, which is why the sampler records it on the point instead.
   */
  @Test
  public void testTheParameterDoesNotRestartAfterABreak() {
    List<Double> hues = hues("ParametricPlot[{t, t}, {t, 0, 10}, PlotPoints -> 40, "
        + "RegionFunction -> Function[{x, y}, x < 3 || x > 7], ColorFunction -> (Hue[#3]&)]");
    assertTrue(hues.size() > 10);
    assertTrue(isAscending(hues), () -> "the parameter kept running across the hole: " + hues);
    // the hole is 4 of the 10 the parameter covers, so the colours step over it rather than
    // picking up where the first piece left off
    double widest = 0;
    for (int i = 1; i < hues.size(); i++) {
      widest = Math.max(widest, hues.get(i) - hues.get(i - 1));
    }
    final double jump = widest;
    assertTrue(jump > 0.3,
        () -> "expected the colours to skip the hole, but the widest step was " + jump);
  }

  /** The hue of every piece of a curve, in drawing order. */
  private static List<Double> hues(String input) {
    List<Double> out = new java.util.ArrayList<>();
    for (Prim2D p : collectPlot(input)) {
      Color c = p.style.strokeColor;
      if (c != null) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        out.add((double) hsb[0]);
      }
    }
    return out;
  }

  private static boolean isAscending(List<Double> values) {
    for (int i = 1; i < values.size(); i++) {
      if (values.get(i) < values.get(i - 1) - 1e-6) {
        return false;
      }
    }
    return true;
  }

  @Test
  public void testColorFunctionScalingChangesTheArgument() {
    // Hue over 0..1 walks the whole wheel; over the raw x range of 0..6 it wraps several times
    List<Prim2D> scaled = collectPlot("Plot[x, {x, 0, 6}, ColorFunction -> Hue]");
    List<Prim2D> raw =
        collectPlot("Plot[x, {x, 0, 6}, ColorFunction -> Hue, ColorFunctionScaling -> False]");
    assertEquals(scaled.size(), raw.size(), "the curve itself is unchanged");
    boolean differs = false;
    for (int i = 0; i < scaled.size(); i++) {
      if (!scaled.get(i).style.strokeColor.equals(raw.get(i).style.strokeColor)) {
        differs = true;
        break;
      }
    }
    assertTrue(differs, "expected the unscaled colours to come out different");
  }

  @Test
  public void testBarOriginTopHangsBarsAtNegatedCoordinates() {
    List<Prim2D.RectPrim> rects = bars("BarChart[{1, 4}, BarOrigin -> Top]");
    assertEquals(2, rects.size());
    assertEquals(-4.0, Math.min(rects.get(1).y1, rects.get(1).y2), 1e-9);
  }

  // ------------------------------------------------------------- edge form

  /** The SVG of a {@code Graphics[...]} expression, at a fixed size. */
  private static String svg(String input) {
    IExpr result = evaluator.eval(input);
    return new SVGGraphics(600, 400).toSVG((IAST) result, true);
  }

  /**
   * {@code Opacity} tints a face and leaves the outline around it alone, whichever order the two
   * directives come in. A 20% grey square carries a solid black frame.
   */
  @Test
  public void testOpacityDoesNotFadeAnEdgeForm() {
    for (String input : new String[] {"Graphics[{Opacity[0.2], EdgeForm[Black], Rectangle[]}]",
        "Graphics[{EdgeForm[Black], Opacity[0.2], Disk[]}]"}) {
      String svg = svg(input);
      assertTrue(svg.contains("fill-opacity=\"0.200\""), () -> "the face is tinted: " + svg);
      assertFalse(svg.contains("stroke-opacity"), () -> "the outline stays opaque: " + svg);
    }
  }

  // -------------------------------------------------------------- tooltips

  /**
   * A {@code Tooltip} label reaches the SVG as a {@code <title>}, which is what a viewer shows on
   * hover. The collector used to keep what a tooltip wrapped and throw the label away.
   */
  @Test
  public void testATooltipLabelIsWrittenAsATitle() {
    String svg = svg("Graphics[{Tooltip[Disk[], \"a disk\"]}]");
    assertTrue(svg.contains("<title>a disk</title>"), svg);
  }

  /**
   * A tooltip scopes like a directive rather than wrapping one shape, so a cell built from several
   * primitives answers as one.
   *
   * <p>
   * One group holds them all rather than one apiece: the primitives a tooltip scopes over are
   * collected next to one another, so the run shares a title. Writing one per primitive is what
   * made a labelled contour plot several times the markup of the picture it decorated.
   */
  @Test
  public void testATooltipCoversEverythingInsideIt() {
    String svg = svg("Graphics[{Tooltip[{Red, Rectangle[], Text[\"x\", {2, 2}]}, 42]}]");
    assertEquals(1, svg.split("<title>42</title>", -1).length - 1, svg);
    String tail = svg.substring(svg.indexOf("<title>42</title>"));
    final String group = tail.substring(0, tail.indexOf("</g>"));
    assertTrue(group.contains("<rect") && group.contains("<text"),
        () -> "both shapes belong under the one label: " + group);
    // and nothing outside it carries a label
    assertFalse(
        svg("Graphics[{Tooltip[Disk[], 1], Rectangle[{3, 3}]}]").contains("<title></title>"),
        "an untooltipped primitive gets no title");
  }

  /**
   * A tooltip given nothing to show shows what it wraps.
   *
   * <p>
   * That is what makes wrapping a table of bare values worth doing: {@code Tooltip(Prime(i))} is
   * the whole point of the one argument form, and it used to draw no tooltip at all.
   */
  @Test
  public void testATooltipWithoutALabelShowsWhatItWraps() {
    String svg = svg("Graphics[{Tooltip[Disk[]]}]");
    assertTrue(svg.contains("<title>"), svg);
    assertTrue(svg.contains("Disk"), () -> "the expression labels itself: " + svg);
  }

  /**
   * A label is escaped on its way into the markup.
   *
   * <p>
   * The whole of the servlet path writes this straight into the page with {@code innerHTML}, so
   * the escaping is what keeps a label from being markup. Nothing pinned it before.
   */
  @Test
  public void testATooltipLabelIsEscaped() {
    String svg = svg("Graphics[{Tooltip[Disk[], \"<b>a & b</b>\"]}]");
    assertFalse(svg.contains("<b>"), () -> "a label must not become markup: " + svg);
    assertTrue(svg.contains("&lt;b&gt;"), svg);
    assertTrue(svg.contains("&amp;"), svg);
  }

  // --------------------------------------------------------------- legends

  /**
   * A {@code BarLegend} paints itself with the colour function it was given. It used to draw a
   * fixed scheme whatever the picture beside it was drawn in.
   */
  @Test
  public void testABarLegendUsesTheColourFunctionItWasGiven() {
    String svg =
        svg("Graphics[{Disk[]}, PlotLegends -> BarLegend[ColorData[\"Avocado\"], {0, 1}]]");
    // the avocado scale starts at black and ends at pale yellow; sunset, the fallback, does not
    assertTrue(svg.contains("stop-color:rgb(0,0,0)"), svg);
    assertTrue(svg.contains("stop-color:rgb(255,251,59)"), svg);
  }

  /** A {@code SwatchLegend} names areas, so its marker is a square rather than a point. */
  @Test
  public void testASwatchLegendDrawsSquares() {
    String svg =
        svg("Graphics[{Disk[]}, PlotLegends -> SwatchLegend[{Red, Blue}, {\"a\", \"b\"}]]");
    assertTrue(svg.contains("fill=\"rgb(255,0,0)\""), svg);
    assertTrue(svg.contains("fill=\"rgb(0,0,255)\""), svg);
    assertTrue(svg.contains(">a</text>") && svg.contains(">b</text>"), svg);
  }

  /** An {@code Opacity} written inside the {@code EdgeForm} does fade the outline. */
  @Test
  public void testAnOpacityInsideTheEdgeFormFadesIt() {
    String svg = svg("Graphics[{EdgeForm[{Opacity[0.5], Black}], Opacity[0.2], Rectangle[]}]");
    assertTrue(svg.contains("fill-opacity=\"0.200\""), svg);
    assertTrue(svg.contains("stroke-opacity=\"0.500\""), svg);
  }

  /** A line is not an outline: it fades with the {@code Opacity} in force. */
  @Test
  public void testOpacityStillFadesALine() {
    assertTrue(svg("Graphics[{Opacity[0.4], Line[{{0, 0}, {1, 1}}]}]")
        .contains("stroke-opacity=\"0.400\""));
  }
}
