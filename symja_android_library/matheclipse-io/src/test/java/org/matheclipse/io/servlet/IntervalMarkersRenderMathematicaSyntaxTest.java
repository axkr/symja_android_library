package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;

/**
 * {@link IntervalMarkersRenderTest}'s pictures as {@code MMAServletServer} reads them.
 *
 * <p>
 * The built-in symbol table is keyed by name and built once per JVM, lower-cased for Symja's syntax
 * and not for this one, so this class runs in a JVM of its own - see the "wolfram-language-syntax"
 * surefire execution in the module's pom.
 */
public class IntervalMarkersRenderMathematicaSyntaxTest {

  static {
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
  }

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  private static String render(String input) throws Exception {
    EvalEngine engine = new EvalEngine("interval-markers-test", 256, System.out, false);
    return IntervalMarkersRenderTest.render(new ExprEvaluator(engine, true, (short) -1), input);
  }

  @Test
  public void aroundPointsAreDrawnWithBars() throws Exception {
    String json = render("Graphics[Point[{{1, Around[2, 0.5]}, {2, Interval[{2, 4}]},"
        + " {3, IntervalData[{1, Less, LessEqual, 2}]}}], IntervalMarkersStyle -> Red]");
    assertTrue(json.contains("<svg"), json);
    assertTrue(json.contains(IntervalMarkersRenderTest.SVG_RED), json);
  }

  @Test
  public void aListPlotIsDrawnWithBands() throws Exception {
    String json = render("ListPlot[{{1, Around[1, 0.2]}, {2, Around[2, 0.3]}, {3, 3}},"
        + " IntervalMarkers -> \"Bands\", IntervalMarkersStyle -> Red]");
    assertTrue(json.contains("<svg"), json);
    assertTrue(json.contains(IntervalMarkersRenderTest.SVG_RED), json);
  }

  @Test
  public void aroundPointsIn3DAreDrawnWithBars() throws Exception {
    String json = render("ListPointPlot3D[{{1, 2, Around[3, 0.5]}, {2, 1, 1}, {3, 3, 2}},"
        + " IntervalMarkersStyle -> Red]");
    assertTrue(json.contains("webgl"), json);
    assertTrue(json.contains(IntervalMarkersRenderTest.WEBGL_RED), json);
  }
}
