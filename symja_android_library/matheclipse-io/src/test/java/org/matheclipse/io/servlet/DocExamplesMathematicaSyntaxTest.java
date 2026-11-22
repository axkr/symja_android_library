package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;

/**
 * The examples of a documentation page as <code>MMAServletServer</code> shows them: written in
 * Symja's relaxed syntax, printed as Wolfram Language input with the built-ins spelled the way the
 * kernel reads them.
 *
 * <p>
 * The built-in symbol table is built once per JVM, lower-cased for Symja's syntax and not for this
 * one, so this class runs in a JVM of its own.
 */
public class DocExamplesMathematicaSyntaxTest {

  static {
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
  }

  @BeforeAll
  public static void beforeAll() {
    F.initSymja();
  }

  private static void assertContains(String html, String expected) {
    assertTrue(html.contains(expected), () -> "expected <" + expected + "> in:\n" + html);
  }

  @Test
  public void examplesUseTheBuiltinNames() {
    String html = AJAXDocServlet.generateHTMLString("```\n" //
        + ">> Cos(2*Pi) + ArcTan(1)\n" //
        + "1+Pi/4\n" //
        + ">> g(t_) := StringJoin(ToString(t), \"!\")\n" //
        + ">> D(Exp(k*x), x) + N(Log(e))\n" //
        + "```\n");
    assertContains(html, ">Cos[2*Pi] + ArcTan[1]</a>");
    assertContains(html, ">g[t_]:=ToString[t]&lt;&gt;&quot;!&quot;</a>");
    // one-letter names keep their case: D and N are built-ins, k, x and e are not
    assertContains(html, ">D[Exp[k*x],x] + N[Log[e]]</a>");
    assertFalse(html.contains("cos["), html);
    assertFalse(html.contains("arctan"), html);
  }

  @Test
  public void machineRealsWithoutPrecisionMark() {
    String html = AJAXDocServlet.generateHTMLString("```\n" //
        + ">> Sin(0.25) + Cos(-12.5) + 3.0*10^(-25)\n" //
        + ">> StringLength(\"Sin(0.5`)\")\n" //
        + "```\n");
    assertContains(html, ">Sin[0.25] + Cos[-12.5] + 3.0/10^25</a>");
    // a string keeps what it says
    assertContains(html, ">StringLength[&quot;Sin(0.5`)&quot;]</a>");
  }

  @Test
  public void onlyTheMachinePrecisionMarkGoes() {
    assertEquals("{0.5, -1.0*^-20, 1.000`20, Jsoup`connect, a2`b, \"7.5`\"}",
        AJAXDocServlet.dropMachinePrecisionMarks(
            "{0.5`, -1.0`*^-20, 1.000`20, Jsoup`connect, a2`b, \"7.5`\"}"));
  }
}
