package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A listener which could listen to the <code>EvalEngine#evalLoop()</code> steps, to implement an
 * evaluation trace or a step by step evaluation.
 */
public interface IEvalStepListener {

  /**
   * Get the current &quot;evaluation step hint&quot; for the evaluation step listener.
   *
   * @return the current hint or <code>null</code> if no hint was available.
   */
  public abstract String getHint();

  /**
   * Set an &quot;evaluation step hint&quot; for the evaluation step listener. If defined, this hint
   * could be used in the <code>add()</code> method instead of the <code>add's</code> hint
   * parameter.
   *
   * @param hint
   */
  public abstract void setHint(String hint);

  /**
   * Sets up the next evaluation step.
   *
   * @param expr the input expression which should currently be evaluated
   * @param recursionDepth the current recursion depth of this evaluation step
   */
  public abstract void setUp(IExpr expr, int recursionDepth, @Nullable Object stackMarker);

  /**
   * Tear down this evaluation step (called finally at the evaluation loop).
   *
   * @param result the result after evaluating the input from {@link #setUp(IExpr, int, Object)}
   * @param recursionDepth the current recursion depth of this evaluation step
   * @param commitTraceFrame set to <code>true</code> if the current trace frame should be stored.
   */
  public abstract void tearDown(@Nonnull IExpr result, int recursionDepth, boolean commitTraceFrame,
      @Nullable Object stackMarker);

  /**
   * Tear down this evaluation step (called finally at the evaluation loop).
   *
   * @param result the result after evaluating the input from {@link #setUp(IExpr, int, Object)}
   * @param hints additional information for this step
   * @param recursionDepth the current recursion depth of this evaluation step
   * @param commitTraceFrame set to <code>true</code> if the current trace frame should be stored.
   */
  default void tearDown(@Nonnull IExpr result, @Nonnull IAST hints, int recursionDepth,
      boolean commitTraceFrame, @Nullable Object stackMarker) {
    tearDown(result, recursionDepth, commitTraceFrame, stackMarker);
  }

  /**
   * Add a new step in which the <code>inputExpr</code> was evaluated to the new <code>resultExpr
   * </code>.
   *
   * @param inputExpr the input expression
   * @param resultExpr the evaluated result expression
   * @param recursionDepth the current recursion depth
   * @param iterationCounter the current iteration counter
   * @param listOfHints this hints will be used in the listener
   * @see IEvalStepListener#setHint(String)
   */
  public abstract void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth,
      long iterationCounter, IAST listOfHints);

  /**
   * The finest level of steps this listener wants to be told about, see
   * {@link org.matheclipse.core.eval.steps.StepLevel}. A site which records a step below this level
   * must not even build the expressions the step is made of.
   *
   * @return {@link org.matheclipse.core.eval.steps.StepLevel#RULE} by default
   */
  default int stepLevel() {
    return org.matheclipse.core.eval.steps.StepLevel.RULE;
  }

  /**
   * Should the pattern matcher announce every rewrite rule it applies as a step?
   *
   * <p>
   * This is expensive - the Rubi rule set alone attempts thousands of rules for one integral - and
   * it changes the output of the <code>Trace()</code> function, so it is off unless a listener asks
   * for it.
   *
   * @return <code>false</code> by default
   */
  default boolean traceRewriteRules() {
    return false;
  }

  /**
   * Solve a polynomial with degree &lt;= 2.
   *
   * @param polynomial the polynomial
   * @return <code>F.NIL</code> if no evaluation was possible, or if this method isn't interesting
   *         for listening
   */
  default IASTAppendable rootsOfQuadraticPolynomial(ExprPolynomial polynomial) {
    return F.NIL;
  }
}
