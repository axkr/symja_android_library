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
 * A mesh region is shown in the notebook as its picture, as the reference shows it - without
 * <code>Show</code>. It used to come back as the text of the <code>BoundaryMeshRegion</code>.
 */
public class MeshRegionRenderTest {

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  /** What the servlet sends the browser for one result. */
  private static String render(String input) throws Exception {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    EvalEngine engine = util.getEvalEngine();
    IExpr outExpr = util.eval(input);
    String[] rendered = AJAXQueryServlet.renderResult(engine, outExpr, new StringBuilderWriter(),
        new StringBuilderWriter());
    return rendered[1];
  }

  @Test
  public void aTwoDimensionalMeshIsDrawnAsSVG() throws Exception {
    String json = render("ConvexHullMesh({{0,0},{3,0},{4,2},{2,4},{0,3}})");
    assertTrue(json.contains("<svg"), json);
    assertTrue(!json.contains("BoundaryMeshRegion"), "drawn, not printed: " + json);
    // the reference's face colour
    assertTrue(json.contains("rgb(160,213,234)"), json);
  }

  @Test
  public void aThreeDimensionalMeshIsDrawnInWebGL() throws Exception {
    String json = render("ConvexHullMesh({{0,0,0},{1,0,0},{0,1,0},{0,0,1}})");
    assertTrue(json.contains("webgl"), json);
    assertTrue(!json.contains("BoundaryMeshRegion"), "drawn, not printed: " + json);
  }
}
