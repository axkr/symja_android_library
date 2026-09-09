package org.matheclipse.core.eval.steps;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Collects the steps of an evaluation the way {@link StepsListener} does, and shows each one to a
 * {@link StepDialog} as it happens.
 *
 * <p>
 * The dialog is asked on the evaluating thread and the evaluation waits for its answer, so a reader
 * can look at where the evaluation has got to before letting it go on. Whatever the dialog answers,
 * every step is still recorded, so the evaluation ends with the same derivation
 * {@link org.matheclipse.core.expression.S#TraceForm} would have built.
 *
 * <p>
 * Only the steps which were really recorded are shown: the ones filtered out by the level, by the
 * depth cap, or as a rule set's own plumbing never reach the reader.
 *
 * @see org.matheclipse.core.expression.S#TraceDialog
 */
public final class DialogStepsListener extends StepsListener {

  private final StepDialog dialog;

  /** How many steps have been shown, which is what a step is numbered by. */
  private int shown = 0;

  /** The dialog asked to run to the end, so no further step is offered. */
  private boolean finished = false;

  /** The reader gave up and the evaluation is unwinding. */
  private boolean aborting = false;

  public DialogStepsListener(StepDialog dialog, int maxDepth, int stepLevel) {
    super(maxDepth, stepLevel);
    this.dialog = dialog == null ? StepDialog.CONTINUE_THROUGH : dialog;
  }

  @Override
  protected void recorded(StepNode node) {
    if (finished) {
      return;
    }
    if (node.level > maxDepth()) {
      // deeper than the reader asked to see: the finished derivation replaces it with a marker,
      // so stopping for it would offer a step which is then not shown
      return;
    }
    final EvalEngine engine = EvalEngine.get();
    shown++;
    DialogStep step = new DialogStep(node.input, node.display, node.hints, node.level, shown,
        // the evaluation is waiting inside itself, so anything asked here is evaluated with the
        // step collection switched off - it is a question about the evaluation, not part of it
        expr -> engine.evalTraceless(expr));
    DialogCommand command;
    try {
      command = dialog.atStep(step);
    } catch (RuntimeException rex) {
      // a dialog which failed must not take the evaluation down with it; the derivation is still
      // collected to the end and handed back
      finished = true;
      return;
    }
    if (command == DialogCommand.ABORT) {
      // everything on the way to where the reader stopped is kept, see tearDown
      aborting = true;
      finished = true;
      throw AbortException.ABORTED;
    }
    if (command == DialogCommand.FINISH) {
      finished = true;
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>
   * While the evaluation unwinds from a reader who gave up, the frames it unwinds through are kept
   * rather than thrown away: they are the path to the step the reader stopped at, which is the one
   * thing worth having from an evaluation that was given up on.
   */
  @Override
  public void tearDown(IExpr result, int recursionDepth, boolean commitTraceFrame,
      Object stackMarker) {
    super.tearDown(result, recursionDepth, aborting || commitTraceFrame, stackMarker);
  }

  /** How many steps were shown to the dialog. */
  public int shown() {
    return shown;
  }

  /**
   * Evaluate an expression with a reader stepping through it, and give back the derivation.
   *
   * <p>
   * A reader who gives up part way still gets what had been worked out up to there, with
   * {@link org.matheclipse.core.expression.S#$Aborted} as the result: the steps already taken are
   * the point of having asked.
   *
   * @param dialog who is shown the steps, or <code>null</code> to run straight through
   * @param maxDepth how deep the steps may nest
   * @param stepLevel how fine grained they are, see {@link StepLevel}
   * @return <code>TraceForm(HoldForm(result), {step, ...})</code>
   */
  public static IAST evaluate(EvalEngine engine, IExpr expr, StepDialog dialog, int maxDepth,
      int stepLevel) {
    DialogStepsListener listener = new DialogStepsListener(dialog, maxDepth, stepLevel);
    IExpr result;
    try {
      result = engine.evalWithStepListener(expr, listener);
    } catch (AbortException aex) {
      result = S.$Aborted;
    }
    return F.TraceForm(F.HoldForm(result), listener.toExpr());
  }
}
