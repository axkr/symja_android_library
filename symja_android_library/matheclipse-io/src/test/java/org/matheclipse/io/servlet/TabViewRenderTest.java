package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.JsonNode;

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

  /** What the servlet sends the browser for the result of a cell. */
  private static JsonNode cell(ExprEvaluator util, String input) throws Exception {
    EvalEngine engine = util.getEvalEngine();
    IExpr outExpr = util.eval(input);
    String[] rendered = AJAXQueryServlet.renderCell(engine, outExpr, new StringBuilderWriter(),
        new StringBuilderWriter());
    return JSONBuilder.JSON_OBJECT_MAPPER.readTree(rendered[1]).path("results").path(0);
  }

  @Test
  public void aCellResultCarriesEveryPane() throws Exception {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    JsonNode result = cell(util,
        "TabView({\"disk\" -> Graphics(Disk()), \"power\" -> uu^3, "
            + "\"inner\" -> TabView({vv, ww})}, 2)");
    assertEquals("tabview", result.path("format").asText());
    JsonNode tabView = result.path("tabview");
    assertEquals(1, tabView.path("selected").asInt(), "the selector counts from one, the page from zero");
    JsonNode tabs = tabView.path("tabs");
    assertEquals(3, tabs.size());
    assertEquals("disk", tabs.path(0).path("label").asText());
    assertTrue(tabs.path(0).path("body").path("results").path(0).path("result").asText()
        .startsWith("<svg"), "a pane is rendered as a result of its own");
    assertEquals("mathml", tabs.path(1).path("body").path("results").path(0).path("format").asText());
    // a pane may be a TabView again; bare panes are labelled by their position
    JsonNode inner = tabs.path(2).path("body").path("results").path(0);
    assertEquals("tabview", inner.path("format").asText());
    assertEquals("1", inner.path("tabview").path("tabs").path(0).path("label").asText());
    assertEquals(0, inner.path("tabview").path("selected").asInt());
  }

  @Test
  public void aDynamicSelectorStaysALiveCell() throws Exception {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    JsonNode result = cell(util, "TabView({\"a\" -> pp, \"b\" -> qq}, Dynamic(tabPick))");
    assertEquals("dynamic", result.path("format").asText());
  }

  @Test
  public void aPanelIsABoxAroundItsContents() throws Exception {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    String plain = cell(util, "Panel(rr^2)").path("result").asText();
    assertTrue(plain.contains("border:1px solid"), plain);
    assertFalse(plain.contains("<mi>panel</mi>") || plain.contains("<mi>Panel</mi>"),
        "drawn, not printed: " + plain);
    String titled = cell(util, "Panel(rr^2, \"Square\")").path("result").asText();
    assertTrue(titled.contains("Square") && titled.contains("<mtable"), titled);
    assertTrue(titled.indexOf("Square") < titled.indexOf("<msup"), "the title comes first");
  }
}
