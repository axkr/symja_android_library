package org.matheclipse.core.eval.steps;

/**
 * Shown every step of an evaluation as it happens, and says whether to carry on.
 *
 * <p>
 * The evaluation waits inside {@link #atStep} until it answers, so an implementation may take as
 * long as it likes - the notebook's dialog waits there for the reader to press a button. It runs on
 * the evaluating thread, so it must not evaluate anything itself except through
 * {@link DialogStep#evaluate}.
 *
 * @see org.matheclipse.core.expression.S#TraceDialog
 */
@FunctionalInterface
public interface StepDialog {

  /** Carries straight on through every step; what <code>TraceDialog</code> does with no reader. */
  StepDialog CONTINUE_THROUGH = step -> DialogCommand.CONTINUE;

  /**
   * A step has been reached.
   *
   * @param step what the evaluation just did
   * @return whether to carry on, run to the end, or give up
   */
  DialogCommand atStep(DialogStep step);
}
