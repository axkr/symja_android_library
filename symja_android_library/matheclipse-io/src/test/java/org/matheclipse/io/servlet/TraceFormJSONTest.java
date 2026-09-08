package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * What the notebook servlet sends the browser for a <code>TraceForm(...)</code> result: the steps
 * as their own tree, so the page can make every step a section the reader opens and closes.
 */
public class TraceFormJSONTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  /** The one result object of a rendered evaluation. */
  private static JsonNode render(String input) throws Exception {
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    EvalEngine engine = util.getEvalEngine();
    IExpr outExpr = util.eval(input);
    StringBuilderWriter out = new StringBuilderWriter();
    StringBuilderWriter err = new StringBuilderWriter();
    String[] rendered = AJAXQueryServlet.renderResult(engine, outExpr, out, err);
    assertEquals(JSONBuilder.FORMAT_STEPS, rendered[0], "the servlet must recognise a derivation");
    return MAPPER.readTree(rendered[1]).get("results").get(0);
  }

  @Test
  public void testFormatAndKeys() throws Exception {
    JsonNode result = render("TraceForm(D(Sin(x^2),x))");
    assertEquals("steps", result.get("format").asText());
    assertTrue(result.has("steps"), result.toString());
    assertTrue(result.has("latex"), result.toString());
    assertTrue(result.has("plaintext"), result.toString());
    assertTrue(result.has("out"), result.toString());
  }

  @Test
  public void testResultIsTeX() throws Exception {
    JsonNode result = render("TraceForm(D(x^2,x))");
    assertEquals("2 \\cdot x", result.get("result").asText());
  }

  @Test
  public void testStepsAreNested() throws Exception {
    JsonNode steps = render("TraceForm(D(Sin(x^2),x))").get("steps").get("steps");
    assertEquals(1, steps.size());
    JsonNode chainRule = steps.get(0);
    assertEquals("D::ChainRule", chainRule.get("stepKey").asText());
    assertTrue(chainRule.get("step").asText().contains("chain rule"),
        chainRule.get("step").asText());
    assertEquals("D::PowerRule", chainRule.get("subSteps").get(0).get("stepKey").asText());
    assertEquals("D::IdentityRule",
        chainRule.get("subSteps").get(0).get("subSteps").get(0).get("stepKey").asText());
  }

  @Test
  public void testAStepCarriesBothExpressionsAsTeX() throws Exception {
    JsonNode step = render("TraceForm(D(Sin(x^2),x))").get("steps").get("steps").get(0);
    assertEquals("\\frac{\\partial \\sin ({x}^{2})}{\\partial x}",
        step.get("prevExpression").asText());
    assertTrue(step.get("expression").asText().contains("\\frac{\\partial {x}^{2}}{\\partial x}"),
        step.get("expression").asText());
    assertTrue(!step.get("truncated").asBoolean());
  }

  @Test
  public void testDescriptionsCarryTheirFormulas() throws Exception {
    // the substituted expressions are wrapped so the page can typeset them where they stand
    JsonNode steps = render("TraceForm(D(Sin(x^2),x), Infinity)").get("steps").get("steps");
    String identityRule =
        steps.get(0).get("subSteps").get(0).get("subSteps").get(0).get("step").asText();
    assertTrue(identityRule.contains("\\(") && identityRule.contains("\\)"), identityRule);
  }

  @Test
  public void testTruncatedStepIsMarked() throws Exception {
    JsonNode steps = render("TraceForm(D(Sin(x^2),x), 1)").get("steps").get("steps");
    JsonNode truncated = steps.get(0).get("subSteps").get(0);
    assertTrue(truncated.get("truncated").asBoolean(), truncated.toString());
    assertEquals("TraceForm::Truncated", truncated.get("stepKey").asText());
  }

  @Test
  public void testLatexIsTheWholeDerivation() throws Exception {
    String latex = render("TraceForm(D(x^2,x))").get("latex").asText();
    assertTrue(latex.startsWith("\\begin{array}{l}"), latex);
    assertTrue(latex.contains("\\rightarrow"), latex);
    assertTrue(latex.endsWith("\\end{array}"), latex);
  }

  @Test
  public void testAnOrdinaryResultIsUnaffected() throws Exception {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    EvalEngine engine = util.getEvalEngine();
    IExpr outExpr = util.eval("D(x^2,x)");
    StringBuilderWriter out = new StringBuilderWriter();
    StringBuilderWriter err = new StringBuilderWriter();
    String[] rendered = AJAXQueryServlet.renderResult(engine, outExpr, out, err);
    assertEquals("mathml", rendered[0]);
  }

  @Test
  public void testTheBuiltFormIsRecognisedWhateverItsContent() throws Exception {
    // an empty derivation is still a derivation
    JsonNode result = render("TraceForm(x)");
    assertEquals("steps", result.get("format").asText());
    assertEquals(0, result.get("steps").get("steps").size());
  }

  /** Keeps the compiler honest about the type the servlet branch casts to. */
  @Test
  public void testResultIsAnIAST() {
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS);
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    IExpr outExpr = util.eval("TraceForm(D(x^2,x))");
    assertTrue(outExpr instanceof IAST, outExpr.toString());
  }
}
