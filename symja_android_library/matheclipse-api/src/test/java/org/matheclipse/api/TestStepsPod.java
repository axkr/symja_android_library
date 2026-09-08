package org.matheclipse.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.ToggleFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The pod a derivation gets: the steps of a <code>TraceForm</code> as their own tree, so a client
 * can lay them out itself instead of being handed one finished picture.
 */
public class TestStepsPod {

  private static int FORMATS = 0;

  @BeforeAll
  public static void beforeAll() {
    // the API decides which functions exist and how the input is parsed, and Pods reads that when
    // it is first touched - so nothing here may touch Pods before this has run
    SymjaServer.initAPI();
    FORMATS = Pods.internFormat(new String[] {"steps", "latex", "plaintext"});
  }

  @BeforeEach
  public void setUp() {
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
  }

  /**
   * The pods of one query.
   *
   * <p>
   * The answer is read back from its own text: <code>queryresult</code> is stored with
   * <code>putPOJO</code>, so walking into it with <code>get</code> stops at the wrapper.
   */
  private JsonNode pods(String input) {
    ObjectNode result = Pods.createResult(input, FORMATS, false, null);
    try {
      JsonNode pods = new ObjectMapper().readTree(result.toString()).get("queryresult").get("pods");
      assertTrue(pods != null, "no pods at all for " + input + ": " + result);
      return pods;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  /** The subpod of the pod with this title, or <code>null</code> if there is none. */
  private JsonNode subpod(String input, String title) {
    JsonNode pods = pods(input);
    for (JsonNode pod : pods) {
      if (title.equals(pod.get("title").asText())) {
        return pod.get("subpods").get(0);
      }
    }
    return null;
  }

  @Test
  public void testTheFormatIsUnderstood() {
    assertEquals(Pods.STEPS, Pods.internFormat(new String[] {"steps"}));
  }

  @Test
  public void testADerivationGetsAPodOfItsOwn() {
    JsonNode steps = subpod("TraceForm(D(Sin(x^2),x))", "Steps");
    assertTrue(steps != null, "no Steps pod");
    assertTrue(steps.has("steps"), steps.toString());
  }

  @Test
  public void testTheStepsAreNested() {
    JsonNode tree = subpod("TraceForm(D(Sin(x^2),x))", "Steps").get("steps");
    JsonNode chainRule = tree.get("steps").get(0);
    assertEquals("D::ChainRule", chainRule.get("stepKey").asText());
    assertTrue(chainRule.get("step").asText().contains("chain rule"),
        chainRule.get("step").asText());
    assertEquals("D::PowerRule", chainRule.get("subSteps").get(0).get("stepKey").asText());
  }

  @Test
  public void testTheExpressionsTravelAsTeX() {
    JsonNode step = subpod("TraceForm(D(Sin(x^2),x))", "Steps").get("steps").get("steps").get(0);
    assertEquals("\\frac{\\partial \\sin ({x}^{2})}{\\partial x}",
        step.get("prevExpression").asText());
    assertFalse(step.get("truncated").asBoolean());
  }

  @Test
  public void testThePodAlsoCarriesTheWholeDerivation() {
    JsonNode steps = subpod("TraceForm(D(x^2,x))", "Steps");
    assertTrue(steps.get("latex").asText().startsWith("\\begin{array}{l}"),
        steps.get("latex").asText());
    assertTrue(steps.get("plaintext").asText().contains("2*x"), steps.get("plaintext").asText());
  }

  @Test
  public void testThePodsBelowShowTheResultAndNotTheWrapper() {
    // the derivation is one pod; everything after it is about what it worked out
    JsonNode exact = subpod("TraceForm(D(x^2,x))", "Result");
    if (exact == null) {
      exact = subpod("TraceForm(D(x^2,x))", "Expanded form");
    }
    JsonNode pods = pods("TraceForm(D(x^2,x))");
    boolean sawTheWrapper = false;
    for (JsonNode pod : pods) {
      JsonNode plaintext = pod.get("subpods").get(0).get("plaintext");
      if (plaintext != null && plaintext.asText().startsWith("TraceForm(")
          && !"Input".equals(pod.get("title").asText())) {
        sawTheWrapper = true;
      }
    }
    assertFalse(sawTheWrapper, "a pod below the derivation still showed the wrapper");
  }

  @Test
  public void testAnIntegrationNamesItsRules() {
    JsonNode tree = subpod("TraceForm(Integrate(Sin(x)^3,x), 2)", "Steps").get("steps");
    boolean sawARubiRule = false;
    for (JsonNode step : tree.get("steps")) {
      if ("Integrate::RubiRule".equals(step.get("stepKey").asText())) {
        sawARubiRule = true;
        assertTrue(step.get("step").asText().contains("rule"), step.get("step").asText());
      }
    }
    assertTrue(sawARubiRule, tree.toString());
  }

  @Test
  public void testSteppingThroughIsNotOfferedHere() {
    // TraceDialog holds an evaluation open waiting for a reader, and a request which answers once
    // and is finished has none. It stays out, so the call is simply not a known function.
    assertNull(subpod("TraceDialog(D(x^2,x))", "Steps"));
    assertEquals("TraceDialog(2*x)",
        subpod("TraceDialog(D(x^2,x))", "Result").get("plaintext").asText());
  }

  @Test
  public void testTheUnboundedTracingStaysOut() {
    // Trace writes down every turn of the evaluation loop; it is not something a public request
    // should be able to ask for
    assertEquals("Trace(2*x)", subpod("Trace(D(x^2,x))", "Result").get("plaintext").asText());
  }

  @Test
  public void testAnOrdinaryResultHasNoStepsPod() {
    assertNull(subpod("D(Sin(x^2),x)", "Steps"));
  }

  @Test
  public void testTheStepsAreOnlySentWhenAskedFor() throws Exception {
    ObjectNode result = Pods.createResult("TraceForm(D(x^2,x))",
        Pods.internFormat(new String[] {"plaintext"}), false, null);
    JsonNode pods = new ObjectMapper().readTree(result.toString()).get("queryresult").get("pods");
    for (JsonNode pod : pods) {
      assertFalse(pod.get("subpods").get(0).has("steps"), pod.toString());
    }
  }
}
