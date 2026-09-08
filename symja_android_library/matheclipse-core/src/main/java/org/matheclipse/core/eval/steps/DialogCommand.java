package org.matheclipse.core.eval.steps;

/**
 * What a {@link StepDialog} asks for when it has been shown a step.
 *
 * @see org.matheclipse.core.expression.S#TraceDialog
 */
public enum DialogCommand {

  /** Carry on and show the next step. */
  CONTINUE,

  /** Carry on to the end without stopping again; the remaining steps are still collected. */
  FINISH,

  /** Give up on the evaluation. */
  ABORT
}
