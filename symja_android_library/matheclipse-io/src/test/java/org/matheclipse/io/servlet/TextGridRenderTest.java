package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>Text@Grid(...)</code> - the notebook idiom for a table set as plain text, and a common
 * <code>Manipulate</code> body - is shown as the table. The head and its parentheses used to be
 * printed round it.
 */
public class TextGridRenderTest {

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  @Test
  public void textAroundAGridShowsTheTable() throws Exception {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    EvalEngine engine = util.getEvalEngine();
    IExpr outExpr = util.eval("Text(Grid({{\"n\", 1}, {\"n^2\", 1}}))");
    String json = AJAXQueryServlet.renderResult(engine, outExpr, new StringBuilderWriter(),
        new StringBuilderWriter())[1];
    // the rendered part only: the plain text beside it is the source, and keeps its Text
    String shown = json.substring(json.indexOf("\"result\""), json.indexOf("\"plaintext\""));
    assertTrue(shown.contains("mtable"), json);
    assertFalse(shown.contains("Text"), "shown, not printed: " + json);
  }
}
