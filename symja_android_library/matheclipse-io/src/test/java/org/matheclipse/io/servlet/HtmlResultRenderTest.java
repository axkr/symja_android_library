package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IStringX;

/**
 * A result that is a page of HTML is shown in a frame of its own. The page is the value of an
 * attribute of that frame, and the frame keeps the page from reaching the one around it.
 */
public class HtmlResultRenderTest {

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  @Test
  public void thePageIsQuotedAndTheFrameSandboxed() throws Exception {
    String page = "<p class=\"note\">fish &amp; chips</p>";
    String[] rendered = AJAXQueryServlet.renderResult(new EvalEngine(false),
        F.stringx(page, IStringX.TEXT_HTML), new StringBuilderWriter(), new StringBuilderWriter());
    String frame = JSONBuilder.JSON_OBJECT_MAPPER.readTree(rendered[1]).path("results").path(0)
        .path("result").asText();
    assertTrue(frame.startsWith("<iframe sandbox=\"allow-scripts\" srcdoc=\""), frame);
    assertFalse(frame.contains("allow-same-origin"), frame);

    int start = frame.indexOf("srcdoc=\"") + "srcdoc=\"".length();
    String attribute = frame.substring(start, frame.indexOf('"', start));
    // the attribute runs to the end of the page: no quote of the page ended it early
    assertTrue(attribute.contains("&lt;/html&gt;") || attribute.endsWith("</html>"), attribute);
    assertTrue(attribute.contains("class=&quot;note&quot;"), attribute);
    assertTrue(attribute.contains("fish &amp;amp; chips"), attribute);
    assertEquals(" style=", frame.substring(frame.indexOf('"', start) + 1).substring(0, 7));
  }
}
