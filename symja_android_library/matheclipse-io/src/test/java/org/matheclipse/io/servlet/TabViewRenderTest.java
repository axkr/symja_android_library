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
 * A <code>TabView</code> shows the pane its selector picks, drawn as that pane would be on its
 * own. A selector a <code>Manipulate</code> drives from outside the tab strip - the
 * <code>ControlType -&gt; None</code> idiom - chooses the pane, and an unset one shows the first.
 */
public class TabViewRenderTest {

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  /** What the servlet sends the browser for one result. */
  private static String render(ExprEvaluator util, String input) throws Exception {
    EvalEngine engine = util.getEvalEngine();
    IExpr outExpr = util.eval(input);
    String[] rendered = AJAXQueryServlet.renderResult(engine, outExpr, new StringBuilderWriter(),
        new StringBuilderWriter());
    return rendered[1];
  }

  @Test
  public void theSelectorPicksThePane() throws Exception {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    String code = "TabView({\"a\" -> Graphics(Disk()), \"b\" -> Graphics3D(Sphere())}, "
        + "Dynamic(tabSel))";
    String unset = render(util, code);
    assertTrue(unset.contains("<svg"), "no selector value shows the first pane: " + unset);
    String second = render(util, "tabSel = 2; " + code);
    assertTrue(second.contains("webgl"), "the second pane is a 3D picture: " + second);
    assertFalse(second.contains("TabView"), "drawn, not printed: " + second);
  }
}
