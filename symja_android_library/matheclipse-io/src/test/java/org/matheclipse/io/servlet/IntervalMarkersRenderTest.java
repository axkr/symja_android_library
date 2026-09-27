package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * {@code Around}, {@code Interval} and {@code IntervalData} coordinates as the notebook shows them:
 * drawn with their interval markers. {@link IntervalMarkersRenderMathematicaSyntaxTest} sends the
 * same pictures as {@code MMAServletServer} reads them.
 */
public class IntervalMarkersRenderTest {

  /** {@code IntervalMarkersStyle -> Red} in the SVG. */
  static final String SVG_RED = "rgb(255,0,0)";

  /** {@code IntervalMarkersStyle -> Red} in the WebGL scene. */
  static final String WEBGL_RED = "16711680";

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  /** What the servlet sends the browser for one result. */
  static String render(ExprEvaluator util, String input) throws Exception {
    EvalEngine engine = util.getEvalEngine();
    IExpr outExpr = util.eval(input);
    String[] rendered = AJAXQueryServlet.renderResult(engine, outExpr, new StringBuilderWriter(),
        new StringBuilderWriter());
    return rendered[1];
  }

  private static String render(String input) throws Exception {
    return render(new ExprEvaluator(true, (short) -1), input);
  }

  @Test
  public void aroundPointsAreDrawnWithBars() throws Exception {
    String json = render("Graphics(Point({{1, Around(2, 0.5)}, {2, Interval({2, 4})},"
        + " {3, IntervalData({1, Less, LessEqual, 2})}}), IntervalMarkersStyle -> Red)");
    assertTrue(json.contains("<svg"), json);
    assertTrue(json.contains(SVG_RED), json);
  }

  @Test
  public void aListLinePlotIsDrawnWithBands() throws Exception {
    String json = render("ListLinePlot({Around(1, 0.2), Around(2, 0.3), Around(1.5, 0.4)},"
        + " IntervalMarkers -> \"Bands\", IntervalMarkersStyle -> Red)");
    assertTrue(json.contains("<svg"), json);
    assertTrue(json.contains(SVG_RED), json);
  }

  @Test
  public void aroundPointsIn3DAreDrawnWithBars() throws Exception {
    String json = render(
        "Graphics3D(Point({{1, 2, Around(3, 0.5)}, {2, 1, 1}}), IntervalMarkersStyle -> Red)");
    assertTrue(json.contains("webgl"), json);
    assertTrue(json.contains(WEBGL_RED), json);
  }
}
