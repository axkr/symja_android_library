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
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The options that decide how a 3D plot is drawn rather than where it is sampled.
 *
 * <p>
 * Colours, markers, labels and legends: the parts of a plot a reader sees before anything else, and
 * the parts that were quietly falling back to a default because the option was never read.
 */
public class Plot3DStyleOptionsTest {

  private static final String RED = "RGBColor(1,0,0";
  private static final String GREEN = "RGBColor(0,1,0";

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
    evaluator.eval("ClearAll(x,y,z,i,j,k,n,t,u,v)");
  }

  private static String plot(String input) {
    // the printer wraps long lines; join them so that contains() is not tripped by a break
    return evaluator.eval(input).toString().replace("\n", "");
  }

  private static int count(String input, String head) {
    return evaluator.eval("Length(Cases(" + input + "," + head + ",Infinity))").toIntDefault(-1);
  }

  private static IExpr head(String input) {
    IExpr result = evaluator.eval(input);
    return result.isAST() ? ((IAST) result).head() : result;
  }

  @Test
  public void discretePlot3DTakesItsColoursFromPlotStyle() {
    assertTrue(
        plot("DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->Full,PlotStyle->Red]").contains(RED),
        "the bars are red");
    assertFalse(plot("DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->Full]").contains(RED),
        "and are not red when nothing asked for that");
  }

  /**
   * {@code ExtentSize} is measured in the gap between neighbouring samples.
   *
   * <p>
   * Resolving it against a gap of one only looked right when the iterator stepped by one: over
   * {@code {i,0,1,0.1}} every bar came out ten times too wide and swallowed its neighbours.
   */
  @Test
  public void extentSizeFollowsTheSampleSpacing() {
    // a bar is centred on its sample and reaches half the spacing to either side, so the corner
    // of the first one says how wide the bars came out
    String tenth = plot("DiscretePlot3D[1,{i,0,0.5,0.1},{j,0,0.5,0.1},ExtentSize->Full]");
    assertTrue(tenth.contains("-0.05"), "a step of 0.1 gives bars 0.1 wide: " + tenth);

    String unit = plot("DiscretePlot3D[1,{i,0,5,1},{j,0,5,1},ExtentSize->Full]");
    assertTrue(unit.contains("-0.5"), "a step of 1 gives bars 1 wide: " + unit);

    assertEquals(S.Graphics3D, head("DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->Scaled[0.5]]"));
    assertEquals(S.Graphics3D, head("DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->All]"));
  }

  @Test
  public void discretePlot3DMarksAndJoinsItsValues() {
    assertTrue(count("DiscretePlot3D[i+j,{i,1,3},{j,1,3}]", "_Point") > 0,
        "the default is a stem with a marker on top");
    assertEquals(0, count("DiscretePlot3D[i+j,{i,1,3},{j,1,3},PlotMarkers->None]", "_Point"),
        "PlotMarkers -> None takes the marker away");
    assertTrue(plot("DiscretePlot3D[i+j,{i,1,3},{j,1,3},PlotMarkers->\"x\"]").contains("Text(x"),
        "a string marker is written as text");

    int joined =
        count("DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->None,Joined->True]", "_Line");
    assertTrue(joined >= 6, "a three by three grid is joined along both directions, got " + joined);
    assertEquals(0, count("DiscretePlot3D[i+j,{i,1,3},{j,1,3},ExtentSize->None]", "_Line"),
        "and is not joined unless asked");
  }

  @Test
  public void listPointPlot3DDropsStemsWhenFillingAsksForThem() {
    String data = "{{1,1,1},{2,2,3},{3,1,2}}";
    assertEquals(0, count("ListPointPlot3D[" + data + "]", "_Line"), "no stems by default");
    for (String filling : new String[] {"Bottom", "Top", "Axis", "0.5"}) {
      assertTrue(count("ListPointPlot3D[" + data + ",Filling->" + filling + "]", "_Line") > 0,
          "Filling -> " + filling + " drops a stem from every point");
    }
    assertTrue(
        plot("ListPointPlot3D[" + data + ",Filling->Bottom,FillingStyle->Green]").contains(GREEN),
        "FillingStyle colours the stems");
  }

  @Test
  public void contourPlot3DPrefersContourStyleOverPlotStyle() {
    String base = "ContourPlot3D[x^2+y^2+z^2==1,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->8";
    assertTrue(plot(base + ",ContourStyle->Red]").contains(RED), "ContourStyle styles the surface");
    assertTrue(plot(base + ",PlotStyle->Red]").contains(RED), "PlotStyle still works on its own");
  }

  /** The surface is read by its colour, so it draws no mesh over it and outlines it. */
  @Test
  public void complexPlot3DDefaultsMatchTheReference() {
    String options = plot("Options(ComplexPlot3D)");
    assertTrue(options.contains("Mesh->None"), "no mesh by default");
    assertTrue(options.contains("BoundaryStyle->RGBColor(0,0,0)"), "a black rim");

    String plainOptions = plot("Options(Plot3D)");
    assertTrue(plainOptions.contains("Mesh->Automatic"), "Plot3D is unaffected");
    assertTrue(plainOptions.contains("BoundaryStyle->Automatic"));

    assertTrue(count("ComplexPlot3D[Sqrt[z],{z,-2-2*I,2+2*I},PlotPoints->6]", "_Line") > 0,
        "the domain is outlined");
  }

  /**
   * Changing a default must not move the options that follow it.
   *
   * <p>
   * {@code ComplexPlot3D} once declared its two changed defaults ahead of the shared block, which
   * put every later option one place along; it then read {@code PlotPoints} out of a neighbouring
   * slot and sampled fifty by fifty whatever the call asked for.
   */
  @Test
  public void complexPlot3DStillReadsItsOwnOptions() {
    for (int n : new int[] {6, 12, 20}) {
      int sampled = evaluator.eval("Module({k=0},ComplexPlot3D[Sqrt[z],{z,-2-2*I,2+2*I},"
          + "PlotPoints->" + n + ",EvaluationMonitor:>k++];k)").toIntDefault(-1);
      assertEquals(n * n, sampled, "PlotPoints -> " + n + " has to reach the sampler");
    }
  }

  @Test
  public void plotLegendsReturnsALegendedGraphic() {
    assertEquals(S.Legended,
        head("Plot3D[x*y,{x,0,1},{y,0,1},PlotPoints->4,PlotLegends->{\"a\"}]"));
    assertEquals(S.Legended, head("ListPointPlot3D[{{1,1,1},{2,2,2}},PlotLegends->Automatic]"));
    assertEquals(S.Graphics3D, head("Plot3D[x*y,{x,0,1},{y,0,1},PlotPoints->4]"),
        "a plot without the option is a plain graphic");
    assertEquals(S.Graphics3D, head("Plot3D[x*y,{x,0,1},{y,0,1},PlotPoints->4,PlotLegends->None]"));
  }

  /**
   * The picture is still underneath the wrapper, which is what both renderers read.
   *
   * <p>
   * Everything that decides whether to draw a graphic has to look through the wrapper as well. The
   * servlet, the API pods and the documentation pages all dispatch on the head, and a plot that
   * renders normally would otherwise print itself as text the moment it was given a legend.
   */
  @Test
  public void aLegendedGraphicStillRenders() {
    IExpr legended =
        evaluator.eval("Plot3D[x*y,{x,0,1},{y,0,1},PlotPoints->6,PlotLegends->{\"a\"}]");
    assertTrue(WebGLGraphics3D.isRenderable(legended), "a legended plot is still a graphic");
    assertTrue(WebGLGraphics3D.isRenderable(evaluator.eval("Graphics3D[Sphere[]]")));
    assertFalse(WebGLGraphics3D.isRenderable(F.ZZ(3)), "a number is not");

    assertTrue(WebGLGraphics3D.generateHTMLSnippet((IAST) legended).contains("data-type=\"webgl\""),
        "and it produces a scene rather than falling through to text");

    String svg = SVGGraphics3D.toSVG((IAST) legended);
    assertTrue(svg.startsWith("<svg"), svg.substring(0, Math.min(60, svg.length())));
    assertTrue(svg.contains("<polygon"), "the surface is drawn under the legend wrapper");
  }

  @Test
  public void listLinePlot3DKeepsTheCoordinatesItWasGiven() {
    String heights = plot("ListLinePlot3D[{{1,2,3,4},{5,6,7,80}}]");
    assertTrue(heights.contains("80.0"), "a height is plotted where it is, not rescaled");
    assertTrue(heights.contains("{1.0,1.0,1.0}"), "the row index runs from one");

    String ranged = plot("ListLinePlot3D[{{1,2,3,4},{5,6,7,8}},DataRange->{{0,10},{0,20}}]");
    assertTrue(ranged.contains("{0.0,0.0,1.0}") && ranged.contains("{10.0,20.0,8.0}"),
        "DataRange lays the rows out over its own rectangle: " + ranged);

    assertEquals(S.Graphics3D, head("ListLinePlot3D[{{5,5,5,5},{5,5,5,5}}]"),
        "a flat dataset used to divide by a zero range and give up");
  }

  @Test
  public void listLinePlot3DStylesEachLine() {
    assertTrue(plot("ListLinePlot3D[{{{0,0,1},{1,1,2}}},PlotStyle->Red]").contains(RED));
    assertTrue(
        plot("ListLinePlot3D[{{{0,0,1},{1,1,2}}},PlotStyle->Red,ViewPoint->{2,2,2}]").contains(RED),
        "an option written after PlotStyle does not swallow it");
    assertTrue(plot("ListLinePlot3D[{{1,2,3,4},{5,6,7,8}},PlotStyle->{Red,Green}]").contains(GREEN),
        "a list of styles is used one per line");
  }

  // --------------------------------------------------- AxesLabel -> Automatic

  /** The three axis names a scene ends up with, as the renderers see them. */
  private static String axesLabels(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(org.matheclipse.core.graphics.WebGLGraphics3D.isRenderable(result),
        () -> input + " did not build a scene: " + result);
    return org.matheclipse.core.graphics.WebGLGraphics3D.buildScene((IAST) result).get("axesLabel")
        .toString();
  }

  /**
   * {@code AxesLabel -> Automatic} names the axes after what was plotted.
   *
   * <p>
   * It used to reach the renderer as the symbol itself and be drawn: the z axis of every plot
   * asked for automatic labels simply read "Automatic".
   */
  @Test
  public void testAutomaticAxesLabelsAreTheVariablesAndTheFunction() {
    assertEquals("[\"x\",\"y\",\"Sin(x+y)\"]",
        axesLabels("Plot3D[Sin[x+y],{x,0,1},{y,0,1},AxesLabel->Automatic]"));
    assertFalse(
        org.matheclipse.core.graphics.SVGGraphics3D
            .toSVG((IAST) evaluator.eval("Plot3D[Sin[x+y],{x,0,1},{y,0,1},AxesLabel->Automatic]"))
            .contains(">Automatic<"),
        "the symbol was drawn as a label");
  }

  /**
   * Several surfaces have no one name, so the vertical axis stays unlabelled rather than reading as
   * whichever of them came first.
   */
  @Test
  public void testAutomaticLeavesTheThirdAxisUnnamedForSeveralSurfaces() {
    assertEquals("[\"x\",\"y\",null]",
        axesLabels("Plot3D[{Sin[x+y],Cos[x+y]},{x,0,1},{y,0,1},AxesLabel->Automatic]"));
  }

  /**
   * The default must not start labelling every plot. {@code base3D()} registers {@code AxesLabel}
   * with a default of {@code Automatic}, so a fix that read the option array rather than the rules
   * the caller wrote would name the axes of every three dimensional plot in the system.
   */
  @Test
  public void testAPlotThatAskedForNoLabelsHasNone() {
    assertEquals("[null,null,null]", axesLabels("Plot3D[Sin[x+y],{x,0,1},{y,0,1}]"));
  }

  /** Nothing can be derived without a plot, so a hand written scene stays unlabelled. */
  @Test
  public void testAutomaticOnAHandWrittenSceneDrawsNoLabel() {
    assertEquals("[null,null,null]", axesLabels("Graphics3D[Sphere[],AxesLabel->Automatic]"));
  }

  /** Labels the caller wrote out are untouched. */
  @Test
  public void testExplicitAxesLabelsStillWin() {
    assertEquals("[\"a\",\"b\",\"c\"]",
        axesLabels("Plot3D[Sin[x+y],{x,0,1},{y,0,1},AxesLabel->{\"a\",\"b\",\"c\"}]"));
  }

  /** {@code PlotLabel -> Automatic} is not a title either. */
  @Test
  public void testAutomaticPlotLabelDrawsNoTitle() {
    assertFalse(
        org.matheclipse.core.graphics.SVGGraphics3D
            .toSVG((IAST) evaluator.eval("Plot3D[Sin[x+y],{x,0,1},{y,0,1},PlotLabel->Automatic]"))
            .contains(">Automatic<"),
        "the symbol was drawn as a title");
  }

}
