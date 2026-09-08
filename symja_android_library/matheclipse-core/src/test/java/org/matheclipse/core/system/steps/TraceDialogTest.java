package org.matheclipse.core.system.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.steps.DialogCommand;
import org.matheclipse.core.eval.steps.DialogStep;
import org.matheclipse.core.eval.steps.DialogStepsListener;
import org.matheclipse.core.eval.steps.StepDialog;
import org.matheclipse.core.eval.steps.StepsTree;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Stepping through an evaluation: the steps reach the reader one at a time, and the reader decides
 * whether the evaluation goes on.
 */
public class TraceDialogTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Assumptions.assumeTrue(ToggleFeature.SHOW_STEPS,
        "evaluation steps are switched off in this build");
  }

  /** A reader which writes down every step it is shown and answers from a script. */
  private static final class ScriptedDialog implements StepDialog {
    final List<String> seen = new ArrayList<String>();
    final List<DialogCommand> script = new ArrayList<DialogCommand>();
    final List<IExpr> asked = new ArrayList<IExpr>();
    IExpr question = F.NIL;

    ScriptedDialog(DialogCommand... commands) {
      for (DialogCommand command : commands) {
        script.add(command);
      }
    }

    @Override
    public DialogCommand atStep(DialogStep step) {
      seen.add(step.descriptionKey());
      if (question.isPresent()) {
        asked.add(step.evaluate(question));
      }
      int index = seen.size() - 1;
      return index < script.size() ? script.get(index) : DialogCommand.CONTINUE;
    }
  }

  /** Evaluate with a dialog watching, and give back the derivation it collected. */
  private IAST run(String input, StepDialog dialog, int maxDepth) {
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    EvalEngine engine = util.getEvalEngine();
    DialogStepsListener listener =
        new DialogStepsListener(dialog, maxDepth, org.matheclipse.core.eval.steps.StepLevel.RULE);
    IExpr result = engine.evalWithStepListener(engine.parse(input), listener);
    return F.TraceForm(F.HoldForm(result), listener.toExpr());
  }

  @Test
  public void testEveryStepIsShown() {
    ScriptedDialog dialog = new ScriptedDialog();
    run("D(Sin(x^2),x)", dialog, Integer.MAX_VALUE);
    assertEquals("[D::ChainRule, D::PowerRule, D::IdentityRule]", dialog.seen.toString());
  }

  @Test
  public void testTheStepsAreStillCollected() {
    // stepping through is a way of watching the evaluation, not a different evaluation
    IAST traceForm = run("D(Sin(x^2),x)", new ScriptedDialog(), Integer.MAX_VALUE);
    assertEquals("2*x*Cos(x^2)", StepsTree.traceResult(traceForm).toString());
    assertEquals(1, StepsTree.steps(traceForm).argSize());
  }

  @Test
  public void testFinishStopsTheStepping() {
    // the reader has seen enough: the evaluation runs on without stopping again
    ScriptedDialog dialog = new ScriptedDialog(DialogCommand.FINISH);
    IAST traceForm = run("D(Sin(x^2),x)", dialog, Integer.MAX_VALUE);
    assertEquals("[D::ChainRule]", dialog.seen.toString());
    // and the derivation is complete all the same
    assertEquals("2*x*Cos(x^2)", StepsTree.traceResult(traceForm).toString());
    assertEquals("PowerRule",
        StepsTree.ruleKey((IAST) StepsTree.subSteps((IAST) StepsTree.steps(traceForm).arg1())
            .arg1()));
  }

  @Test
  public void testGivingUpKeepsWhatWasWorkedOut() {
    ScriptedDialog dialog = new ScriptedDialog(DialogCommand.ABORT);
    ExprEvaluator util = new ExprEvaluator(true, (short) -1);
    EvalEngine engine = util.getEvalEngine();
    IAST traceForm = DialogStepsListener.evaluate(engine, engine.parse("D(Sin(x^2),x)"), dialog,
        Integer.MAX_VALUE, org.matheclipse.core.eval.steps.StepLevel.RULE);
    assertEquals("$Aborted", StepsTree.traceResult(traceForm).toString());
    assertEquals("[D::ChainRule]", dialog.seen.toString());
    // the one step which had been taken is still there to look at
    assertEquals("ChainRule", StepsTree.ruleKey((IAST) StepsTree.steps(traceForm).arg1()));
  }

  @Test
  public void testTheReaderCanAskAboutTheEvaluation() {
    ScriptedDialog dialog = new ScriptedDialog();
    dialog.question = F.Plus(F.C1, F.C1);
    run("D(Sin(x^2),x)", dialog, Integer.MAX_VALUE);
    assertEquals(3, dialog.asked.size());
    assertEquals("2", dialog.asked.get(0).toString());
  }

  @Test
  public void testAskingSomethingIsNotItselfAStep() {
    // the question is answered with the step collection switched off, or it would appear in the
    // derivation the reader is looking at
    ScriptedDialog dialog = new ScriptedDialog();
    dialog.question = F.D(F.Sin(F.x), F.x);
    IAST traceForm = run("D(Sin(x^2),x)", dialog, Integer.MAX_VALUE);
    assertEquals("Cos(x)", dialog.asked.get(0).toString());
    assertEquals("[D::ChainRule, D::PowerRule, D::IdentityRule]", dialog.seen.toString());
    assertEquals(1, StepsTree.steps(traceForm).argSize());
  }

  @Test
  public void testAFailingDialogDoesNotTakeTheEvaluationDown() {
    StepDialog broken = step -> {
      throw new IllegalStateException("the reader went away");
    };
    IAST traceForm = run("D(Sin(x^2),x)", broken, Integer.MAX_VALUE);
    assertEquals("2*x*Cos(x^2)", StepsTree.traceResult(traceForm).toString());
  }

  @Test
  public void testOnlyRecordedStepsAreShown() {
    // a step dropped by the depth cap is not one the reader is stopped for
    ScriptedDialog dialog = new ScriptedDialog();
    run("D(Sin(x^2),x)", dialog, 1);
    assertEquals("[D::ChainRule]", dialog.seen.toString());
  }

  @Test
  public void testAStepKnowsWhereItIs() {
    final List<String> where = new ArrayList<String>();
    run("D(Sin(x^2),x)", step -> {
      where.add(step.number() + "@" + step.level());
      return DialogCommand.CONTINUE;
    }, Integer.MAX_VALUE);
    assertEquals("[1@1, 2@2, 3@3]", where.toString());
  }

  // ---------------------------------------------------------------- the builtin

  @Test
  public void testWithNobodyToStopForItRunsThrough() {
    // a script, a test, or a TraceDialog inside another expression: there is no reader
    check("TraceDialog(D(Sin(x^2),x))[[1]]", //
        "2*x*Cos(x^2)");
    check("TraceDialog(D(x^2,x))[[2,1,3,2]]", //
        "PowerRule");
  }

  @Test
  public void testItTakesTheSameArgumentsAsTraceForm() {
    check("TraceDialog(D(Sin(x^2),x), 1)[[2,1,4,1,3]]", //
        "{TraceForm,Truncated}");
    check("TraceDialog(Cancel((x^2-1)/(x-1)), Infinity, \"Algebra\")[[2,1,3,2]]", //
        "Factor");
  }

  @Test
  public void testABadDepthIsRejected() {
    check("TraceDialog(D(x^2,x), -1)", //
        "TraceDialog(D(x^2,x),-1)");
  }

  @Test
  public void testTheDescriptionIsReadable() {
    final List<String> sentences = new ArrayList<String>();
    run("D(x^2,x)", step -> {
      sentences.add(step.description());
      return DialogCommand.CONTINUE;
    }, Integer.MAX_VALUE);
    assertTrue(sentences.get(0).startsWith("The derivative of"), sentences.toString());
  }
}
