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
 * Every plot family carries a {@code Tooltip} through to the picture.
 *
 * <p>
 * A wrapper the plot does not recognise is worse than one it does not offer: the call is accepted,
 * the label is silently dropped, and - because an unrecognised wrapper stops a list counting as a
 * list of points - the data itself can be read wrongly. Each family is checked on its own so that a
 * regression names the plot it broke rather than one of them standing in for the rest.
 */
public class PlotTooltipReachTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
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
    engine.setRecursionLimit(512);
    engine.setIterationLimit(100000);
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    evaluator.eval("ClearAll(a,b,c,f,i,j,k,n,p,t,u,v,x,y,z)");
  }

  private static int count(String haystack, String needle) {
    int n = 0;
    for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + 1)) {
      n++;
    }
    return n;
  }

  /** The flat rendering of a plot, whichever of the two renderers draws it. */
  private static String svg(String input) {
    IExpr result = evaluator.eval(input);
    assertTrue(result instanceof IAST, () -> input + " did not draw a picture: " + result);
    String out = result.isAST(org.matheclipse.core.expression.S.Graphics3D)
        ? SVGGraphics3D.toSVG((IAST) result)
        : new SVGGraphics(600, 400).toSVG((IAST) result, true);
    assertTrue(out != null && !out.isEmpty(), () -> "no output for " + input);
    return out;
  }

  /**
   * The label reaches the picture, and a plot with no label in it gains nothing.
   *
   * <p>
   * Both halves matter. Without the second a plot could pass by labelling everything it draws.
   *
   * @param plain the same call with the wrapper taken off
   */
  private static void assertLabelled(String labelled, String plain) {
    String withLabel = svg(labelled);
    assertTrue(count(withLabel, "<title>lbl</title>") > 0,
        () -> labelled + ": the label never reached the picture");
    assertEquals(0, count(svg(plain), "<title>"),
        () -> plain + ": an unlabelled plot must carry no title at all");
  }

  // ------------------------------------------------------- the list plots, point by point

  @Test
  public void testListPlotFamilyLabelsAPoint() {
    assertLabelled("ListPlot({{1,1}, Tooltip({2,4},\"lbl\"), {3,9}})",
        "ListPlot({{1,1},{2,4},{3,9}})");
    assertLabelled("ListLinePlot({{1,1}, Tooltip({2,4},\"lbl\"), {3,9}})",
        "ListLinePlot({{1,1},{2,4},{3,9}})");
    assertLabelled("ListStepPlot({{1,1}, Tooltip({2,4},\"lbl\"), {3,9}})",
        "ListStepPlot({{1,1},{2,4},{3,9}})");
    assertLabelled("ListPolarPlot({{1,1}, Tooltip({2,4},\"lbl\"), {3,9}})",
        "ListPolarPlot({{1,1},{2,4},{3,9}})");
    assertLabelled("ListLogPlot({{1,1}, Tooltip({2,4},\"lbl\"), {3,9}})",
        "ListLogPlot({{1,1},{2,4},{3,9}})");
    assertLabelled("ListLogLogPlot({{1,1}, Tooltip({2,4},\"lbl\"), {3,9}})",
        "ListLogLogPlot({{1,1},{2,4},{3,9}})");
    assertLabelled("ListLogLinearPlot({{1,1}, Tooltip({2,4},\"lbl\"), {3,9}})",
        "ListLogLinearPlot({{1,1},{2,4},{3,9}})");
  }

  // ------------------------------------------------------- the function plots, curve by curve

  @Test
  public void testFunctionPlotFamilyLabelsACurve() {
    assertLabelled("Plot(Tooltip(Sin(x),\"lbl\"),{x,0,6})", "Plot(Sin(x),{x,0,6})");
    assertLabelled("LogPlot(Tooltip(Exp(x),\"lbl\"),{x,1,3})", "LogPlot(Exp(x),{x,1,3})");
    assertLabelled("LogLogPlot(Tooltip(x^2,\"lbl\"),{x,1,3})", "LogLogPlot(x^2,{x,1,3})");
    assertLabelled("LogLinearPlot(Tooltip(Log(x),\"lbl\"),{x,1,3})",
        "LogLinearPlot(Log(x),{x,1,3})");
    assertLabelled("PolarPlot(Tooltip(1+Cos(t),\"lbl\"),{t,0,6.28})",
        "PolarPlot(1+Cos(t),{t,0,6.28})");
    assertLabelled("ParametricPlot(Tooltip({Cos(t),Sin(t)},\"lbl\"),{t,0,6.28})",
        "ParametricPlot({Cos(t),Sin(t)},{t,0,6.28})");
    assertLabelled("DiscretePlot(Tooltip(i^2,\"lbl\"),{i,1,5})", "DiscretePlot(i^2,{i,1,5})");
  }

  /**
   * {@code ParametricPlot} reads its argument at two levels, and the shallower one is the trap:
   * {@code {fx, fy}} is one curve, so a wrapper around it must not be read as a collection of two.
   */
  @Test
  public void testParametricPlotStillReadsAWrappedPairAsOneCurve() {
    IExpr plain = evaluator.eval("Length(Cases(ParametricPlot({Cos(t),Sin(t)},{t,0,6.28}), "
        + "_Line, Infinity))");
    IExpr wrapped = evaluator.eval("Length(Cases(ParametricPlot(Tooltip({Cos(t),Sin(t)},\"lbl\"), "
        + "{t,0,6.28}), _Line, Infinity))");
    assertEquals(plain, wrapped, "the labelled pair is still one curve");
  }

  // ------------------------------------------------------- the charts, element by element

  @Test
  public void testChartFamilyLabelsAnElement() {
    assertLabelled("BarChart({1, Tooltip(2,\"lbl\"), 3})", "BarChart({1,2,3})");
    assertLabelled("PieChart({1, Tooltip(2,\"lbl\"), 3})", "PieChart({1,2,3})");
    assertLabelled("Histogram(Tooltip({1,2,2,3},\"lbl\"))", "Histogram({1,2,2,3})");
    assertLabelled("BoxWhiskerChart({Tooltip({1,2,3,4,5},\"lbl\")})",
        "BoxWhiskerChart({{1,2,3,4,5}})");
    assertLabelled("WordCloud({Tooltip(\"a\",\"lbl\"),\"b\",\"b\"})", "WordCloud({\"a\",\"b\",\"b\"})");
  }

  // ------------------------------------------------------- the field plots, over the picture

  /**
   * A field or raster plot draws its data as one picture, so the level a label can sit at is the
   * whole of it.
   */
  @Test
  public void testFieldPlotFamilyLabelsThePicture() {
    assertLabelled("DensityPlot(Tooltip(x*y,\"lbl\"),{x,0,1},{y,0,1})",
        "DensityPlot(x*y,{x,0,1},{y,0,1})");
    assertLabelled("ContourPlot(Tooltip(x^2+y^2,\"lbl\"),{x,-1,1},{y,-1,1})",
        "ContourPlot(x^2+y^2,{x,-1,1},{y,-1,1})");
    assertLabelled("ArrayPlot(Tooltip({{1,2},{3,4}},\"lbl\"))", "ArrayPlot({{1,2},{3,4}})");
    assertLabelled("MatrixPlot(Tooltip({{1,2},{3,4}},\"lbl\"))", "MatrixPlot({{1,2},{3,4}})");
    assertLabelled("ListDensityPlot(Tooltip({{1,1,1},{2,2,4},{1,2,3},{2,1,2}},\"lbl\"))",
        "ListDensityPlot({{1,1,1},{2,2,4},{1,2,3},{2,1,2}})");
    assertLabelled("ListContourPlot(Tooltip(Table(i*j,{i,1,5},{j,1,5}),\"lbl\"))",
        "ListContourPlot(Table(i*j,{i,1,5},{j,1,5}))");
    assertLabelled("ComplexPlot(Tooltip(z^2,\"lbl\"),{z,-1-I,1+I})",
        "ComplexPlot(z^2,{z,-1-I,1+I})");
    assertLabelled("DensityHistogram(Tooltip({{1,1},{2,2},{1,2}},\"lbl\"))",
        "DensityHistogram({{1,1},{2,2},{1,2}})");
    assertLabelled("NumberLinePlot(Tooltip({1,2,3},\"lbl\"))", "NumberLinePlot({1,2,3})");
  }

  /**
   * One label over a whole field costs one group, not one per polygon.
   *
   * <p>
   * A contour plot is thousands of polygons that all carry the same label, and a title apiece would
   * be several times the markup of the picture it decorates. They are collected next to one
   * another, so the writer puts the run under one group.
   */
  @Test
  public void testALabelOverAWholeFieldIsWrittenOnce() {
    String svg = svg("ContourPlot(Tooltip(x^2+y^2,\"lbl\"),{x,-1,1},{y,-1,1})");
    assertEquals(1, count(svg, "<title>"),
        () -> "the label should be written once over the whole field, not once per polygon");
  }

  // ------------------------------------------------------- three dimensions

  @Test
  public void testThreeDimensionalFamilyLabelsItsSurface() {
    assertLabelled("Plot3D(Tooltip(Sin(x*y),\"lbl\"),{x,0,3},{y,0,3},PlotPoints->6)",
        "Plot3D(Sin(x*y),{x,0,3},{y,0,3},PlotPoints->6)");
    assertLabelled("ListPlot3D(Tooltip(Table(i*j,{i,1,4},{j,1,4}),\"lbl\"))",
        "ListPlot3D(Table(i*j,{i,1,4},{j,1,4}))");
    assertLabelled("ListLinePlot3D(Tooltip({{1,2,3,4}},\"lbl\"))", "ListLinePlot3D({{1,2,3,4}})");
    assertLabelled("ListPointPlot3D(Tooltip({{1,1,1},{2,2,2}},\"lbl\"))",
        "ListPointPlot3D({{1,1,1},{2,2,2}})");
    assertLabelled(
        "ParametricPlot3D(Tooltip({Cos(u)*Sin(v),Sin(u)*Sin(v),Cos(v)},\"lbl\"),{u,0,6},{v,0,3})",
        "ParametricPlot3D({Cos(u)*Sin(v),Sin(u)*Sin(v),Cos(v)},{u,0,6},{v,0,3})");
    assertLabelled("SphericalPlot3D(Tooltip(1,\"lbl\"),{t,0,3.14},{p,0,6.28})",
        "SphericalPlot3D(1,{t,0,3.14},{p,0,6.28})");
    assertLabelled("RevolutionPlot3D(Tooltip(t,\"lbl\"),{t,0,2})", "RevolutionPlot3D(t,{t,0,2})");
    assertLabelled("DiscretePlot3D(Tooltip(i*j,\"lbl\"),{i,1,3},{j,1,3})",
        "DiscretePlot3D(i*j,{i,1,3},{j,1,3})");
    assertLabelled("ComplexPlot3D(Tooltip(z^2,\"lbl\"),{z,-1-I,1+I},PlotPoints->8)",
        "ComplexPlot3D(z^2,{z,-1-I,1+I},PlotPoints->8)");
    assertLabelled(
        "ContourPlot3D(Tooltip(x^2+y^2+z^2,\"lbl\"),{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->6,"
            + "Contours->2)",
        "ContourPlot3D(x^2+y^2+z^2,{x,-2,2},{y,-2,2},{z,-2,2},PlotPoints->6,Contours->2)");
  }

  /**
   * A labelled surface costs a handful of groups rather than one per facet.
   *
   * <p>
   * The facets are in drawing order by the time they are written, and those of one surface mostly
   * stay together in it, so the runs are merged. Without that a forty by forty surface would carry
   * sixteen hundred titles.
   */
  @Test
  public void testALabelOverASurfaceIsNotWrittenOncePerFacet() {
    String svg = svg("Plot3D(Tooltip(Sin(x*y),\"lbl\"),{x,0,3},{y,0,3},PlotPoints->40)");
    int titles = count(svg, "<title>");
    assertTrue(titles > 0 && titles < 100,
        () -> "expected a handful of groups over the surface, got " + titles);
  }

  /** The label also travels to the interactive rendering, which reads a scene rather than markup. */
  @Test
  public void testTheThreeDimensionalLabelReachesTheInteractiveScene() {
    String json = WebGLGraphics3D
        .generateJSON((IAST) evaluator.eval("Plot3D(Tooltip(Sin(x*y),\"lbl\"),{x,0,3},{y,0,3},"
            + "PlotPoints->6)"));
    assertTrue(json.contains("\"tooltip\":\"lbl\""), json);
    assertEquals(0,
        count(
            WebGLGraphics3D.generateJSON((IAST) evaluator
                .eval("Plot3D(Sin(x*y),{x,0,3},{y,0,3},PlotPoints->6)")),
            "\"tooltip\""),
        "an unlabelled surface carries none");
  }
}
