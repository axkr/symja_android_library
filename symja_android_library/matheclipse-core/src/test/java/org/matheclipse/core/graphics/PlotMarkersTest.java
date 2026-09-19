package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
 * {@code PlotMarkers}, across the plots that take it.
 *
 * <p>
 * {@code PlotMarkers -> Automatic} used to draw nothing at all, because {@code Automatic} was both
 * the internal default and the value meaning "no marker" - so asking for the standard markers was
 * indistinguishable from not asking for anything. The option matrix did not catch it: it exercises
 * {@code PlotMarkers -> {"x"}}, which worked.
 *
 * <p>
 * A marker is drawn as a {@code Text}, so these tests read the glyphs out of the rendered SVG. The
 * axis tick labels are text too, which is why the assertions count particular glyphs rather than
 * {@code &lt;text&gt;} elements.
 */
public class PlotMarkersTest {

  /** The standard sequence: a disk, a square, a diamond, and the two triangles. */
  private static final String DISK = "●";
  private static final String SQUARE = "■";
  private static final String DIAMOND = "◆";
  private static final String OPEN_DISK = "○";

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
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
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  /** The regression that started this: Automatic has to draw the standard marker. */
  @Test
  public void automaticDrawsAMarkerOnEveryDataPoint() {
    String plain = svgOf("ListLinePlot({{1,1},{2,4},{3,9}})");
    String marked = svgOf("ListLinePlot({{1,1},{2,4},{3,9}}, PlotMarkers->Automatic)");
    assertNotEquals(plain, marked, "PlotMarkers->Automatic has to change the picture");
    assertEquals(0, count(plain, DISK), "no markers without the option");
    assertEquals(3, count(marked, DISK), "one marker per data point");
    assertTrue(marked.contains("<path"), "the line is still drawn under the markers");
  }

  /** A marker replaces the plain point of an unjoined plot rather than sitting beside it. */
  @Test
  public void anUnjoinedPlotIsDrawnAsMarkersAlone() {
    String marked = svgOf("ListPlot({{1,1},{2,4},{3,9}}, PlotMarkers->Automatic)");
    assertEquals(3, count(marked, DISK));
    assertTrue(!marked.contains("<circle"), "the point markers are replaced, not added to");
  }

  /** Successive datasets take successive shapes, the way they take successive colours. */
  @Test
  public void datasetsTakeSuccessiveShapes() {
    String svg = svgOf(
        "ListLinePlot({{{1,1},{2,4}},{{1,2},{2,5}},{{1,3},{2,6}}}, PlotMarkers->Automatic)");
    assertEquals(2, count(svg, DISK), "the first dataset gets the disk");
    assertEquals(2, count(svg, SQUARE), "the second the square");
    assertEquals(2, count(svg, DIAMOND), "the third the diamond");
  }

  /** {@code "OpenMarkers"} is the same sequence unfilled. */
  @Test
  public void openMarkersAreTheUnfilledShapes() {
    String svg = svgOf("ListLinePlot({{1,1},{2,4}}, PlotMarkers->\"OpenMarkers\")");
    assertEquals(2, count(svg, OPEN_DISK));
    assertEquals(0, count(svg, DISK));
  }

  /** An explicit marker is used as written, and cycles over the datasets. */
  @Test
  public void explicitMarkersCycleOverTheDatasets() {
    String one = svgOf("ListLinePlot({{1,1},{2,4}}, PlotMarkers->{\"x\"})");
    assertEquals(2, count(one, ">x</text>"));
    String two =
        svgOf("ListLinePlot({{{1,1},{2,4}},{{1,2},{2,5}}}, PlotMarkers->{\"x\",\"o\"})");
    assertEquals(2, count(two, ">x</text>"));
    assertEquals(2, count(two, ">o</text>"));
  }

  /**
   * A size is honoured rather than dropped. It used to be parsed out of a {@code {marker, size}}
   * pair and then thrown away.
   */
  @Test
  public void aSizeIsHonoured() {
    assertEquals(20.0, markerFontSize("ListLinePlot({{1,1},{2,4}}, PlotMarkers->{\"x\",20})"),
        0.001);
    // the named sizes, and Automatic beside one of them
    assertTrue(
        markerFontSize("ListLinePlot({{1,1},{2,4}}, PlotMarkers->{Automatic,Large})") > markerFontSize(
            "ListLinePlot({{1,1},{2,4}}, PlotMarkers->{Automatic,Small})"),
        "Large has to come out larger than Small");
    // one size per dataset
    String svg = svgOf(
        "ListLinePlot({{{1,1},{2,4}},{{1,2},{2,5}}}, PlotMarkers->{{\"x\",8},{\"o\",24}})");
    assertTrue(svg.contains("font-size=\"8\""), "the first dataset's size: " + fontSizes(svg));
    assertTrue(svg.contains("font-size=\"24\""), "the second dataset's size: " + fontSizes(svg));
  }

  /** {@code None} is still nothing at all. */
  @Test
  public void noneDrawsNothing() {
    String svg = svgOf("ListLinePlot({{1,1},{2,4}}, PlotMarkers->None)");
    assertEquals(0, count(svg, DISK));
    assertEquals(svgOf("ListLinePlot({{1,1},{2,4}})"), svg);
  }

  /**
   * A sampled curve is not data. It can carry a thousand adaptive samples, and a marker on each
   * would be a smear, so those are spaced out - while every point of a dataset keeps its own.
   */
  @Test
  public void aSampledCurveGetsFewerMarkersThanItHasSamples() {
    String sampled = svgOf("Plot(Sin(x),{x,0,6}, PlotMarkers->Automatic)");
    int markers = count(sampled, DISK);
    assertTrue(markers > 0 && markers <= 16, "a sampled curve is thinned out, got " + markers);

    String data = svgOf("ListLinePlot(Table(n^2,{n,40}), PlotMarkers->Automatic)");
    assertEquals(40, count(data, DISK), "every point of a dataset is marked");
  }

  /** The option is declared where it is honoured, so it is discoverable. */
  @Test
  public void theOptionIsDeclared() {
    for (String head : new String[] {"Plot", "ListPlot", "ListLinePlot", "ParametricPlot",
        "PolarPlot", "ListPolarPlot", "DiscretePlot", "ListStepPlot", "NumberLinePlot"}) {
      assertEquals("True",
          evaluator.eval("MemberQ(Options(" + head + "), _[PlotMarkers, _])").toString(),
          head + " has to declare PlotMarkers");
    }
  }

  // ------------------------------------------------------------------ helpers

  private static String svgOf(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result instanceof IAST && result.isGraphicsObject(), input + " -> " + result);
    return new SVGGraphics(600, 400).toSVG((IAST) result, true)
        .replaceAll("(plotArea|legendGradient)_[0-9a-f]+", "$1_ID");
  }

  private static int count(String svg, String needle) {
    return svg.split(Pattern.quote(needle), -1).length - 1;
  }

  /** The font size of the first glyph that is not an axis label. */
  private static double markerFontSize(String input) {
    String svg = svgOf(input);
    Matcher m = Pattern.compile("font-size=\"([0-9.]+)\"[^>]*>([^<]+)</text>").matcher(svg);
    while (m.find()) {
      String text = m.group(2);
      if (!text.matches("-?[0-9.]+")) {
        return Double.parseDouble(m.group(1));
      }
    }
    return Double.NaN;
  }

  private static List<String> fontSizes(String svg) {
    List<String> sizes = new ArrayList<>();
    Matcher m = Pattern.compile("font-size=\"([0-9.]+)\"").matcher(svg);
    while (m.find()) {
      sizes.add(m.group(1));
    }
    return sizes;
  }
}
