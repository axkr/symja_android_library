package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.steps.DialogCommand;
import org.matheclipse.core.eval.steps.StepLevel;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.io.servlet.TraceDialogSession.Dialog;
import org.matheclipse.io.servlet.TraceDialogSession.Frame;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Stepping through an evaluation from the browser: the evaluation waits on its own thread and each
 * request lets it take one more step.
 */
public class TraceDialogSessionTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String SESSION = "test-session";

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
  }

  private Dialog start(String input) {
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
    EvalEngine engine = new EvalEngine(SESSION, 256, System.out, true);
    IAST ast = (IAST) engine.parse(input);
    return TraceDialogSession.start(SESSION, true, ast, Integer.MAX_VALUE, StepLevel.RULE);
  }

  @Test
  public void testItStopsAtTheFirstStep() throws Exception {
    Dialog dialog = start("TraceDialog(D(Sin(x^2),x))");
    Frame frame = dialog.first();
    assertNotNull(frame, "the evaluation did not reach a first step");
    assertFalse(frame.isFinished());
    assertEquals("D::ChainRule", frame.step.descriptionKey());
    assertEquals(1, frame.step.number());
    dialog.advance(DialogCommand.FINISH, null);
  }

  @Test
  public void testEachRequestTakesOneStep() throws Exception {
    Dialog dialog = start("TraceDialog(D(Sin(x^2),x))");
    assertEquals("D::ChainRule", dialog.first().step.descriptionKey());
    assertEquals("D::PowerRule", dialog.advance(DialogCommand.CONTINUE, null).step.descriptionKey());
    assertEquals("D::IdentityRule",
        dialog.advance(DialogCommand.CONTINUE, null).step.descriptionKey());
    Frame last = dialog.advance(DialogCommand.CONTINUE, null);
    assertTrue(last.isFinished(), "the evaluation should be over");
    assertEquals("2*x*Cos(x^2)",
        org.matheclipse.core.eval.steps.StepsTree.traceResult(last.traceForm).toString());
  }

  @Test
  public void testRunningToTheEndKeepsCollecting() throws Exception {
    Dialog dialog = start("TraceDialog(D(Sin(x^2),x))");
    dialog.first();
    Frame frame = dialog.advance(DialogCommand.FINISH, null);
    assertTrue(frame.isFinished());
    // the steps which were never stopped at are in the derivation all the same
    assertEquals(1, org.matheclipse.core.eval.steps.StepsTree.steps(frame.traceForm).argSize());
    assertEquals("2*x*Cos(x^2)",
        org.matheclipse.core.eval.steps.StepsTree.traceResult(frame.traceForm).toString());
  }

  @Test
  public void testGivingUpKeepsWhatWasWorkedOut() throws Exception {
    Dialog dialog = start("TraceDialog(D(Sin(x^2),x))");
    dialog.first();
    Frame frame = dialog.advance(DialogCommand.ABORT, null);
    assertTrue(frame.isFinished());
    assertEquals("$Aborted",
        org.matheclipse.core.eval.steps.StepsTree.traceResult(frame.traceForm).toString());
    assertEquals(1, org.matheclipse.core.eval.steps.StepsTree.steps(frame.traceForm).argSize());
  }

  @Test
  public void testTheReaderCanAskWithoutMovingOn() throws Exception {
    Dialog dialog = start("TraceDialog(D(Sin(x^2),x))");
    dialog.first();
    Frame answered = dialog.advance(DialogCommand.CONTINUE, F.Plus(F.C1, F.C1));
    assertEquals("2", answered.answer.toString());
    // still the first step: a question is not a command
    assertEquals("D::ChainRule", answered.step.descriptionKey());
    Frame next = dialog.advance(DialogCommand.CONTINUE, null);
    assertEquals("D::PowerRule", next.step.descriptionKey());
    dialog.advance(DialogCommand.FINISH, null);
  }

  // ---------------------------------------------------------------- what the browser is sent

  @Test
  public void testTheStepIsSentAsJSON() throws Exception {
    Dialog dialog = start("TraceDialog(D(Sin(x^2),x))");
    Frame frame = dialog.first();
    EvalEngine engine = new EvalEngine(SESSION, 256, System.out, true);
    String[] json = JSONBuilder.createJSONTraceDialog(engine, dialog.id, frame.step, null, null);
    assertEquals("tracedialog", json[0]);
    JsonNode result = MAPPER.readTree(json[1]).get("results").get(0);
    assertEquals("tracedialog", result.get("format").asText());
    JsonNode sent = result.get("dialog");
    assertEquals(dialog.id, sent.get("id").asText());
    assertFalse(sent.get("finished").asBoolean());
    assertEquals(1, sent.get("number").asInt());
    assertEquals("D::ChainRule", sent.get("stepKey").asText());
    assertTrue(sent.get("step").asText().contains("chain rule"), sent.toString());
    assertEquals("\\frac{\\partial \\sin ({x}^{2})}{\\partial x}",
        sent.get("prevExpression").asText());
    dialog.advance(DialogCommand.FINISH, null);
  }

  @Test
  public void testTheEndIsSentWithTheWholeDerivation() throws Exception {
    Dialog dialog = start("TraceDialog(D(x^2,x))");
    dialog.first();
    Frame frame = dialog.advance(DialogCommand.FINISH, null);
    EvalEngine engine = new EvalEngine(SESSION, 256, System.out, true);
    String[] json =
        JSONBuilder.createJSONTraceDialog(engine, dialog.id, null, frame.traceForm, null);
    JsonNode sent = MAPPER.readTree(json[1]).get("results").get(0).get("dialog");
    assertTrue(sent.get("finished").asBoolean());
    assertEquals("2 \\cdot x", sent.get("result").asText());
    assertTrue(sent.has("steps"), sent.toString());
    assertEquals("D::PowerRule", sent.get("steps").get("steps").get(0).get("stepKey").asText());
  }

  @Test
  public void testACellWhichIsNotADialogIsLeftAlone() {
    EvalEngine engine = new EvalEngine(SESSION, 256, System.out, true);
    assertFalse(TraceDialogSession.isTraceDialog(engine.parse("D(Sin(x^2),x)")));
    assertFalse(TraceDialogSession.isTraceDialog(engine.parse("TraceForm(D(x^2,x))")));
    assertTrue(TraceDialogSession.isTraceDialog(engine.parse("TraceDialog(D(x^2,x))")));
  }
}
