package org.matheclipse.core.interfaces;

import javax.annotation.Nullable;

/**
 * An abstract listener which could listen to the <code>EvalEngine#evalLoop()</code> steps, to
 * implement an evaluation trace or a step by step evaluation.
 */
public abstract class AbstractEvalStepListener implements IEvalStepListener {

  protected String fHint = null;

  /** {@inheritDoc} */
  @Override
  public abstract void add(
      IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter, IExpr hint);

  /** {@inheritDoc} */
  @Override
  public String getHint() {
    return fHint;
  }

  /** {@inheritDoc} */
  @Override
  public void setHint(String hint) {
    this.fHint = hint;
  }

  /** {@inheritDoc} */
  @Override
  public void setUp(IExpr expr, int recursionDepth) {}

  /** {@inheritDoc} */
  @Override
  public void tearDown(@Nullable IExpr result, int recursionDepth, boolean commitTraceFrame) {}
}
