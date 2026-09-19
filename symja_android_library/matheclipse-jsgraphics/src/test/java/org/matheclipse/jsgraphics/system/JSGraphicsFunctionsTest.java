package org.matheclipse.jsgraphics.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.JSPageProvider;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * JSXGraph, ECharts and MathCell of a graphic. The assertions look for the library calls a picture
 * has to make rather than comparing whole scripts, so they survive layout changes in the SVG
 * renderer the scene is shared with.
 */
public class JSGraphicsFunctionsTest extends AbstractTestCase {

  /** The script of a <code>JSFormData(js, type)</code> result, checking its type. */
  private String script(String input, String type) {
    IExpr result = evaluator.eval(input);
    assertTrue(result.isAST(S.JSFormData, 3), input + " gave " + result);
    assertEquals(type, ((IAST) result).arg2().toString());
    return ((IAST) result).arg1().toString();
  }

  @Test
  public void testJSXGraphPlot() {
    String js = script("JSXGraph(Plot(Sin(x), {x, 0, 5}))", "jsxgraph");
    assertTrue(js.contains("JXG.JSXGraph.initBoard('jxgbox', {boundingbox: ["), js);
    assertTrue(js.contains("board.create('curve', [[0,"), js);
    // the default curve colour, RGBColor(0.24, 0.6, 0.8)
    assertTrue(js.contains("strokeColor: '#3d99cc'"), js);
    assertTrue(js.contains("board.create('axis'"), js);
    assertTrue(js.contains(".style.aspectRatio = "), js);
  }

  @Test
  public void testJSXGraphPrimitives() {
    String js = script("JSXGraph(Graphics({Red, Disk({0, 0}, 1), Blue, Point({2, 1}),"
        + " Text(\"a<b\", {1, 1}), Arrow({{0, 0}, {2, 2}})}))", "jsxgraph");
    assertTrue(js.contains("board.create('circle', [[0, 0], 1], {fillColor: '#ff0000'"), js);
    assertTrue(js.contains("board.create('point', [2, 1]"), js);
    assertTrue(js.contains("board.create('text', [1, 1, 'a<b']"), js);
    assertTrue(js.contains("lastArrow"), js);
    assertFalse(js.contains("board.create('axis'"), "a Graphics has no axes by default\n" + js);
  }

  @Test
  public void testJSXGraphLegendedPlot() {
    // PlotLegends wraps the picture in Legended, which is looked through
    String js = script("JSXGraph(Plot({Sin(x), Cos(x)}, {x, 0, 5}, PlotLegends -> Automatic))",
        "jsxgraph");
    assertEquals(2, count(js, "board.create('curve'"), js);
  }

  @Test
  public void testJSXGraphPoleSplitsCurve() {
    // a curve through a pole is drawn in pieces, never joined across it
    String js = script("JSXGraph(Graphics(Line({{0, 0}, {1, Indeterminate}, {2, 2}, {3, 3}})))",
        "jsxgraph");
    assertEquals(1, count(js, "board.create('curve'"), js);
    assertFalse(js.contains("NaN"), js);
  }

  @Test
  public void testJSXGraphUnsupported() {
    check("JSXGraph(1 + x)", //
        "JSXGraph(1+x)");
    check("JSXGraph(Graphics(Raster({{0, 1}, {1, 0}})))", //
        "JSXGraph(Graphics(Raster({{0,1},{1,0}})))");
    // a logarithmic axis: the plot stays unevaluated inside the wrapper
    IExpr log = evaluator.eval("JSXGraph(LogPlot(Exp(x), {x, 0, 5}))");
    assertTrue(log.isAST(S.JSXGraph, 2), log.toString());
  }

  @Test
  public void testEChartsListPlot() {
    String js = script("ECharts(ListLinePlot({1, 4, 9, 16}))", "echarts");
    assertTrue(js.startsWith("var eChart = echarts.init(document.getElementById('main'));"), js);
    assertTrue(js.contains("type: 'line'"), js);
    assertTrue(js.contains("data: [[1,1],[2,4],[3,9],[4,16]]"), js);
    assertTrue(js.contains("xAxis: { type: 'value'"), js);
    assertTrue(js.contains("eChart.setOption(option);"), js);
  }

  @Test
  public void testEChartsScatterAndLegend() {
    String js = script("ECharts(ListPlot({{1, 2}, {2, 3}}, PlotLegends -> {\"data\"}))", "echarts");
    assertTrue(js.contains("type: 'scatter', name: 'data'"), js);
    assertTrue(js.contains("legend: {"), js);
  }

  @Test
  public void testEChartsLogAxis() {
    String js = script("ECharts(LogPlot(Exp(x), {x, 0, 5}))", "echarts");
    assertTrue(js.contains("yAxis: { type: 'log'"), js);
  }

  @Test
  public void testEChartsRefusesShapes() {
    // a bar chart is rectangles, which a chart series cannot hold
    check("ECharts(Graphics(Rectangle()))", //
        "ECharts(Graphics(Rectangle({0,0})))");
  }

  @Test
  public void testMathCellFunctionPlot() {
    // the function itself goes to MathCell, which samples it in the browser
    String js = script("MathCell(Plot(Sin(x), {x, 0, 5}))", "mathcell");
    assertTrue(js.contains("MathCell( id, [] );"), js);
    assertTrue(js.contains("function f1(x) { try { return sin(x); }"), js);
    assertTrue(js.contains("plot( x => f1(x), [0, 5, 400]"), js);
    assertTrue(js.contains("var config = { type: 'svg' };"), js);
  }

  @Test
  public void testMathCellPlot3D() {
    String js = script("MathCell(Plot3D(x*y, {x, -1, 1}, {y, -1, 1}))", "mathcell");
    assertTrue(js.contains("parametric( (x, y) => [ x, y, f(x, y) ]"), js);
    assertTrue(js.contains("type: 'threejs'"), js);
  }

  @Test
  public void testMathCellGraphics() {
    // not a plot of a function: drawn from the evaluated primitives
    String js = script("MathCell(Graphics({Red, Line({{0, 0}, {1, 1}}), Point({1, 0})}))",
        "mathcell");
    assertTrue(js.contains("line( [[0,0],[1,1]], { color: '#ff0000'"), js);
    assertTrue(js.contains("point( [1, 0]"), js);
    assertTrue(js.contains("type: 'svg', xMin: "), js);
  }

  @Test
  public void testMathCellHoldsItsArgument() {
    // held, but a variable holding a graphic is still evaluated before it is drawn
    evaluator.eval("g = Graphics(Line({{0, 0}, {1, 1}}))");
    String js = script("MathCell(g)", "mathcell");
    assertTrue(js.contains("line( [[0,0],[1,1]]"), js);
    evaluator.eval("ClearAll(g)");
  }

  @Test
  public void testIFramePage() {
    // the page every front end shows a result in
    boolean old = Config.DISPLAY_JSFIDDLE_BUTTON;
    try {
      Config.DISPLAY_JSFIDDLE_BUTTON = true;
      String js = script("JSXGraph(Plot(x, {x, 0, 1}))", "jsxgraph");
      String iframe = JSPageProvider.iframeOf("jsxgraph", js);
      assertTrue(iframe.startsWith("<iframe srcdoc=\""), iframe);
      assertTrue(iframe.contains("jsxgraphcore.js"), iframe);
      assertTrue(iframe.contains("jsfiddle.net/api/post/library/pure/"), iframe);
    } finally {
      Config.DISPLAY_JSFIDDLE_BUTTON = old;
    }
  }

  private static int count(String s, String what) {
    int n = 0;
    for (int i = s.indexOf(what); i >= 0; i = s.indexOf(what, i + 1)) {
      n++;
    }
    return n;
  }
}
