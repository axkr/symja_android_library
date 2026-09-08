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
import org.matheclipse.core.expression.F;
import org.matheclipse.core.graphics.svg.Bounds2D;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.PrimitiveCollector;
import org.matheclipse.core.graphics.svg.Style2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * {@code Tooltip} around the data of a plot.
 *
 * <p>
 * A wrapper the plots did not recognise did not merely lose its label: it stopped the list counting
 * as a list of points, and the plot then read the coordinates as bare heights. So most of what is
 * asserted here is that the picture is the one it would have been anyway, with a label added.
 */
public class PlotTooltipTest {

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
    engine.setRecursionLimit(512);
    engine.setIterationLimit(10000);
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    evaluator.eval("ClearAll(a,b,c,f,i,j,k,n,t,u,v,x,y,z)");
  }

  private static String svg(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result instanceof IAST, () -> input + " did not draw a picture: " + result);
    String out = new SVGGraphics(600, 400).toSVG((IAST) result, true);
    assertTrue(out != null && !out.isEmpty(), () -> "no output for " + input);
    return out;
  }

  private static int count(String haystack, String needle) {
    int n = 0;
    for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + 1)) {
      n++;
    }
    return n;
  }

  private static int titles(String input) {
    return count(svg(input), "<title>");
  }

  /** The extent the drawn primitives reach, which a lost datum would change. */
  private static String extent(String input) {
    IExpr result = evaluator.eval(input);
    PrimitiveCollector collector = new PrimitiveCollector(360);
    collector.collect(((IAST) result).arg1(), new Style2D());
    Bounds2D bounds = new Bounds2D();
    for (Prim2D p : collector.primitives()) {
      p.accumulate(bounds);
    }
    return String.format(Locale.US, "x[%.4f,%.4f] y[%.4f,%.4f]", bounds.xMin, bounds.xMax,
        bounds.yMin, bounds.yMax);
  }

  // ------------------------------------------------------- nothing changes without one

  /**
   * A plot with no tooltip in it is drawn exactly as before. This is the guard on the hit target:
   * an invisible mark per point would be a large and pointless cost on every plot.
   */
  @Test
  public void testAPlotWithoutATooltipIsUnchanged() {
    String plain = svg("ListPlot[Table[{i, i^2}, {i, 20}]]");
    assertEquals(0, count(plain, "<title>"));
    assertEquals(0, count(plain, "fill-opacity=\"0\" stroke=\"none\""),
        "no tooltip means no hit targets to hover");
    assertEquals(20, count(plain, "<circle"), "one mark per datum and nothing else");
  }

  // ------------------------------------------------------- the four wrapper levels

  /** A point may be wrapped. */
  @Test
  public void testATooltipOnOnePoint() {
    String svg = svg("ListPlot[{{1,1}, Tooltip[{2,4},\"four\"], {3,9}}]");
    assertEquals(1, count(svg, "<title>four</title>"), svg);
    // three drawn marks, and one transparent one to hover
    assertEquals(4, count(svg, "<circle"), svg);
    assertEquals(1, count(svg, "fill-opacity=\"0\" stroke=\"none\""), svg);
  }

  /** A value may be wrapped, in a list of bare heights. */
  @Test
  public void testATooltipOnOneValue() {
    String svg = svg("ListPlot[{1, Tooltip[4,\"four\"], 9}]");
    assertEquals(1, count(svg, "<title>four</title>"), svg);
    assertEquals(4, count(svg, "<circle"), svg);
  }

  /** A whole dataset may be wrapped, and is then one label over the whole curve. */
  @Test
  public void testATooltipOnAWholeDataset() {
    assertEquals(1, titles("ListLinePlot[Tooltip[{1,4,9}, \"squares\"]]"));
    assertEquals(1, titles("ListPlot[Tooltip[{{1,1},{2,4},{3,9}}, \"squares\"]]"));
  }

  /** And a collection of datasets, each with its own. */
  @Test
  public void testATooltipOnEachOfSeveralDatasets() {
    String svg = svg("ListLinePlot[{Tooltip[{1,2,3},\"up\"], Tooltip[{3,2,1},\"down\"]}]");
    assertEquals(1, count(svg, "<title>up</title>"), svg);
    assertEquals(1, count(svg, "<title>down</title>"), svg);
  }

  /** {@code Tooltip(datum)} labels the datum with itself. */
  @Test
  public void testATooltipWithoutALabelUsesTheValue() {
    String svg = svg("ListPlot[Table[Tooltip[Prime[i]], {i, 5}]]");
    for (String prime : new String[] {"2", "3", "5", "7", "11"}) {
      assertEquals(1, count(svg, "<title>" + prime + "</title>"),
          () -> "expected the value " + prime + " as its own label:\n" + svg);
    }
  }

  // ------------------------------------------------------- the data survives

  /**
   * The defect this began with: one wrapper in a list used to stop it counting as points, so the
   * whole dataset was read as bare heights and drawn somewhere else entirely.
   */
  @Test
  public void testAWrappedDatumIsStillPlottedWhereItBelongs() {
    String plain = extent("ListPlot[{{1,1},{2,4},{3,9}}]");
    assertEquals(plain, extent("ListPlot[{{1,1}, Tooltip[{2,4},\"a\"], {3,9}}]"));
    assertEquals(plain, extent("ListPlot[{{1,1}, Tooltip[{2,4}], {3,9}}]"));
    // and the same for the wrappers that carry nothing Symja draws
    assertEquals(plain, extent("ListPlot[{{1,1}, Annotation[{2,4},\"a\"], {3,9}}]"));
    assertEquals(plain, extent("ListPlot[{{1,1}, StatusArea[{2,4},\"a\"], {3,9}}]"));
    assertEquals(plain, extent("ListPlot[{{1,1}, Style[{2,4},Red], {3,9}}]"));
  }

  /** A chart used to lose the whole element rather than just its label. */
  @Test
  public void testAChartKeepsTheWrappedElement() {
    assertEquals(count(svg("BarChart[{1,2,3}]"), "<rect"),
        count(svg("BarChart[{1, Tooltip[2,\"two\"], 3}]"), "<rect"),
        "the tooltipped bar used to be missing altogether");
    assertEquals(1, count(svg("BarChart[{1, Tooltip[2,\"two\"], 3}]"), "<title>two</title>"));

    assertEquals(count(svg("PieChart[{1,2,3}]"), "<path"),
        count(svg("PieChart[{1, Tooltip[2,\"two\"], 3}]"), "<path"),
        "the tooltipped wedge used to be missing altogether");
    assertEquals(1, count(svg("PieChart[{1, Tooltip[2,\"two\"], 3}]"), "<title>two</title>"));
  }

  /** A histogram bins the value; the wrapper only said how to show it. */
  @Test
  public void testAHistogramCountsTheWrappedValue() {
    assertEquals(evaluator.eval("Length(Cases(Histogram({1,2,2,3}), _Rectangle, Infinity))"),
        evaluator
            .eval("Length(Cases(Histogram({1,Tooltip(2),Tooltip(2),3}), _Rectangle, Infinity))"),
        "a wrapped value used to take no part in any bin");
  }

  // ------------------------------------------------------- the function form

  @Test
  public void testAWholeCurveTooltip() {
    String svg = svg("Plot[Tooltip[Sin[x],\"sine\"], {x,0,6}]");
    assertEquals(1, count(svg, "<title>sine</title>"), svg);
    assertEquals(count(svg("Plot[Sin[x],{x,0,6}]"), "<path"), count(svg, "<path"),
        "the curve itself is drawn exactly as it would have been");
  }

  /** With no label, each curve of a list takes its own expression. */
  @Test
  public void testEachCurveLabelsItself() {
    String svg = svg("Plot[Tooltip[{Sin[x], Cos[x]}], {x,0,6}]");
    assertEquals(2, count(svg, "<title>"), svg);
    assertTrue(svg.contains("Sin(x)"), svg);
    assertTrue(svg.contains("Cos(x)"), svg);
  }

  /**
   * One label given to a list of curves covers all of them.
   *
   * <p>
   * They share a single group rather than taking one apiece: the label is the same, so what a
   * reader sees on either curve is the same either way, and a run that shares a label is written
   * once. {@link #testEachCurveLabelsItself()} is the other half of that - two curves with
   * different labels stay apart.
   */
  @Test
  public void testOneLabelOverSeveralCurves() {
    String svg = svg("Plot[Tooltip[{Sin[x], Cos[x]}, \"waves\"], {x,0,6}]");
    assertEquals(1, count(svg, "<title>waves</title>"), svg);
    String tail = svg.substring(svg.indexOf("<title>waves</title>"));
    final String group = tail.substring(0, tail.indexOf("</g>"));
    assertEquals(2, count(group, "<path"), () -> "both curves belong under the one label: " + group);
  }

  // ------------------------------------------------------- it does not disturb anything else

  /**
   * The hit target is drawn with no ink, so it describes no curve and nothing should be filled
   * under it.
   */
  @Test
  public void testFillingIgnoresTheHitTarget() {
    assertEquals(
        evaluator.eval("Length(Cases(ListPlot({{1,1},{2,4},{3,9}}, Filling->Axis),"
            + " _Polygon, Infinity))"),
        evaluator.eval("Length(Cases(ListPlot({{1,1},Tooltip({2,4},\"a\"),{3,9}},"
            + " Filling->Axis), _Polygon, Infinity))"));
  }

  @Test
  public void testTheOtherPlotOptionsAreUndisturbed() {
    // a colour function still paints every step, and the marks are still placed
    assertEquals(extent("ListPlot[{{1,1},{2,4},{3,9}}, ColorFunction->\"Rainbow\"]"),
        extent("ListPlot[{{1,1},Tooltip[{2,4},\"a\"],{3,9}}, ColorFunction->\"Rainbow\"]"));
    assertEquals(extent("ListPlot[{{1,1},{2,4},{3,9}}, PlotMarkers->{\"x\"}]"),
        extent("ListPlot[{{1,1},Tooltip[{2,4},\"a\"],{3,9}}, PlotMarkers->{\"x\"}]"));
  }

  // ------------------------------------------------------- three dimensions

  @Test
  public void testAThreeDimensionalTooltipReachesTheFlatRendering() {
    String svg = SVGGraphics.svgDocument(evaluator.eval("Graphics3D(Tooltip(Cuboid(),\"box\"))"));
    assertTrue(svg != null && svg.contains("<title>box</title>"), svg);
    assertFalse(SVGGraphics.svgDocument(evaluator.eval("Graphics3D(Cuboid())")).contains("<title>"),
        "an untooltipped solid carries no label");
  }

  /** A tooltip covers what it wraps and nothing standing beside it. */
  @Test
  public void testAThreeDimensionalTooltipDoesNotLeakToASibling() {
    IExpr scene = evaluator.eval("Graphics3D({Tooltip(Cuboid(),\"box\"), Sphere({3,0,0})})");
    String json = WebGLGraphics3D.generateJSON((IAST) scene);
    assertEquals(1, count(json, "\"tooltip\""),
        () -> "only the cuboid asked for a label:\n" + json);
    assertTrue(json.contains("\"tooltip\":\"box\""), json);
  }

  /**
   * A labelled point is still plotted where it belongs.
   *
   * <p>
   * The vertices are counted rather than the {@code Point} primitives: one primitive holds every
   * point of a dataset, so a labelled one is drawn by itself to have something of its own to hang
   * the label on. That is one primitive more and no vertex more.
   */
  @Test
  public void testAThreeDimensionalPointPlotKeepsAWrappedPoint() {
    IExpr plain = evaluator.eval(
        "Length(First(Cases(ListPointPlot3D({{1,1,1},{2,2,2},{3,3,3}}), _GraphicsComplex, "
            + "Infinity)))");
    IExpr wrapped = evaluator.eval("Length(First(Cases(ListPointPlot3D({{1,1,1},"
        + "Tooltip({2,2,2},\"mid\"),{3,3,3}}), _GraphicsComplex, Infinity)))");
    assertEquals(plain, wrapped, "a wrapped point used to be skipped");
  }

  // ------------------------------------------- a tooltip around a whole picture

  /**
   * A {@code Tooltip} around a finished picture draws the picture and answers a hover over any of
   * it. Before this the wrapper was not recognised as a graphic at all, so a {@code Grid} cell
   * holding one drew the picture with the literal text {@code Tooltip[...]} around it.
   */
  @Test
  public void testATooltipAroundAWholePictureIsAPicture() {
    assertTrue(evaluator.eval("Tooltip(Graphics(Disk()), \"a disk\")").isGraphicsObject());
    String out = svg("Tooltip(Graphics(Disk()), \"a disk\")");
    assertTrue(out.contains("<title>a disk</title>"), out);
    // first child of the root, so the hover covers the whole picture
    assertTrue(out.indexOf("<title>") < out.indexOf("<ellipse"), out);
  }

  /** The label is the expression itself when none was given. */
  @Test
  public void testAWholePictureTooltipCanLabelItself() {
    assertTrue(svg("Tooltip(Graphics(Disk()))").contains("<title>"));
  }

  /** A tooltip around something that is not a picture stays what it was. */
  @Test
  public void testATooltipAroundAPlainExpressionIsNotAPicture() {
    assertFalse(evaluator.eval("Tooltip(1, \"nope\")").isGraphicsObject());
    assertFalse(evaluator.eval("Tooltip({1, 2, 3})").isGraphicsObject());
  }

  /**
   * A three dimensional scene wrapped in a legend must not become a two dimensional picture: the
   * servlet asks {@code isGraphicsObject()} first and would take it away from the WebGL renderer.
   */
  @Test
  public void testAWrappedThreeDimensionalSceneIsNotATwoDimensionalPicture() {
    assertFalse(evaluator.eval("Legended(Graphics3D(Sphere()), \"l\")").isGraphicsObject());
    assertFalse(evaluator.eval("Tooltip(Graphics3D(Sphere()), \"s\")").isGraphicsObject());
  }

  /** A layout head wrapped in a tooltip keeps both the layout and the label. */
  @Test
  public void testAWrappedLayoutKeepsItsLabel() {
    String out = svg("Tooltip(GraphicsRow({Graphics(Disk()), Graphics(Rectangle())}), \"row\")");
    assertTrue(out.contains("<title>row</title>"), out);
    assertEquals(3, count(out, "<svg"), "the row lost a cell: " + out);
  }

  /**
   * A cell of a layout carries its own tooltip. The label used to be dropped here even though the
   * picture drew, because a layout renders its cells without a root of their own.
   */
  @Test
  public void testAGridCellCarriesItsOwnTooltip() {
    String out = svg("GraphicsGrid({{Tooltip(Graphics(Disk()), \"cellA\"), "
        + "Tooltip(Graphics(Rectangle()), \"cellB\")}})");
    assertTrue(out.contains("<title>cellA</title>"), out);
    assertTrue(out.contains("<title>cellB</title>"), out);
  }

  // ------------------------------------ AxesLabel -> Automatic, two dimensions

  /** The non numeric text a picture draws, which is its labels and nothing else. */
  private static String labelsOf(String input) {
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("<text[^>]*>([^<]*)</text>").matcher(svg(input));
    StringBuilder out = new StringBuilder();
    while (m.find()) {
      if (!m.group(1).matches("[-0-9.]+")) {
        out.append('[').append(m.group(1)).append(']');
      }
    }
    return out.toString();
  }

  /**
   * The horizontal axis is named after the variable and the vertical after the function. The symbol
   * {@code Automatic} used to be drawn on the axis instead.
   */
  @Test
  public void testAutomaticAxesLabelsNameTheVariableAndTheFunction() {
    assertEquals("[x][Sin(x)]", labelsOf("Plot(Sin(x), {x,0,6}, AxesLabel->Automatic)"));
  }

  /** Several curves have no one name, so the vertical axis stays unlabelled. */
  @Test
  public void testAutomaticLeavesTheVerticalAxisUnnamedForSeveralCurves() {
    assertEquals("[x]", labelsOf("Plot({Sin(x),Cos(x)}, {x,0,6}, AxesLabel->Automatic)"));
  }

  /** Nothing to derive from outside a plot, and nothing drawn. */
  @Test
  public void testAutomaticLabelsOutsideAPlotDrawNothing() {
    assertEquals("", labelsOf("Graphics(Disk(), Axes->True, AxesLabel->Automatic)"));
    assertEquals("", labelsOf("Plot(Sin(x), {x,0,6}, Frame->True, FrameLabel->Automatic)"));
    assertEquals("", labelsOf("Plot(Sin(x), {x,0,6}, PlotLabel->Automatic)"));
  }

  /** A pair may ask for one automatic entry and name the other. */
  @Test
  public void testAutomaticMayBeOneHalfOfAPair() {
    assertEquals("[y]", labelsOf("Plot(Sin(x), {x,0,6}, AxesLabel->{Automatic,\"y\"})"));
  }

  /** A plot that asked for no labels still has none. */
  @Test
  public void testAPlotWithoutLabelsIsUnchanged() {
    assertEquals("", labelsOf("Plot(Sin(x), {x,0,6})"));
  }
}
