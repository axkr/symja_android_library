package org.matheclipse.core.eval;

import java.io.Writer;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

/** Run the evaluation of a given math formula <code>String</code> in a time limited thread */
public class TimeConstrainedEvaluator extends EvalUtilities implements Runnable {

  protected IExpr fEvaluationResult;

  protected Throwable fException;

  protected IExpr fParsedExpression;

  private long fMilliSeconds;

  private final boolean fRelaxedSyntax;

  private boolean fTraceEvaluation;

  public TimeConstrainedEvaluator(final EvalEngine evalEngine, final boolean msie,
      final long milliSeconds) {
    this(evalEngine, msie, milliSeconds, false);
  }

  public TimeConstrainedEvaluator(final EvalEngine evalEngine, final boolean msie,
      final long milliSeconds, boolean relaxedSyntax) {
    super(evalEngine, msie, relaxedSyntax);
    fMilliSeconds = milliSeconds;
    fRelaxedSyntax = relaxedSyntax;
    fTraceEvaluation = false;
  }

  @Override
  public void run() {
    try {
      if (fTraceEvaluation) {
        fEvaluationResult = evalTrace(fParsedExpression, null);
      } else {
        fEvaluationResult = evaluate(fParsedExpression);
      }
    } catch (final Exception e) {
      Errors.printMessage(S.TimeConstrained, e, fEvalEngine);
      fException = e;
    } catch (final OutOfMemoryError e) {
      Errors.printMessage(S.TimeConstrained, e, fEvalEngine);
      fEvaluationResult = F.$str("OutOfMemoryError");
    } catch (final StackOverflowError e) {
      Errors.printMessage(S.TimeConstrained, e, fEvalEngine);
      fEvaluationResult = F.$str("StackOverflowError");
    }
  }

  /**
   * Runs the evaluation of the given math formula <code>String</code> in a time limited thread
   *
   * @param traceEvaluation
   */
  public IExpr constrainedEval(final Writer writer, final String inputString,
      boolean traceEvaluation) throws Exception {

    fEvaluationResult = F.NIL;
    fException = null;
    fParsedExpression = null;
    EvalEngine.setReset(fEvalEngine);
    // fEvalEngine.setStopRequested(false);
    fTraceEvaluation = traceEvaluation;

    try {
      fParsedExpression = fEvalEngine.parse(inputString);
    } catch (final RuntimeException e) {
      throw e;
    }

    return constrainedEval(writer, fParsedExpression);
  }

  /** Runs the evaluation of the given math expression in a time limited thread */
  public IExpr constrainedEval(final Writer writer, final IExpr inputExpression) throws Exception {

    fEvaluationResult = F.NIL;
    fException = null;
    fParsedExpression = inputExpression;
    // fEvalEngine.setStopRequested(false);

    try {
      final Thread thread = new Thread(this, "TimeConstrainedEvaluator"); // EvaluationRunnable();
      thread.start();
      thread.join(fMilliSeconds);
      if (thread.isAlive()) {
        thread.interrupt();
        // fEvalEngine.stopRequest();
        // wait a bit, so the thread can stop by itself
        Thread.sleep(Config.TIME_CONSTRAINED_SLEEP_MILLISECONDS);
        if (thread.isAlive()) {
          // call the deprecated method as last possible exit
          // thread.stop();
          return F.NIL;
          // throw new TimeExceeded();
        }
      }
      if (fException != null) {
        writer.write(fException.getMessage() != null ? fException.getMessage()
            : "Exception: " + fException.getClass().getName());
        writer.write('\n');
      }

      if (fEvaluationResult.isPresent() && !fEvaluationResult.equals(S.Null)) {
        if (!OutputFormFactory.get(fRelaxedSyntax).convert(writer, fEvaluationResult)) {
          // TODO define error message?
        }
      }
      return fEvaluationResult;
    } catch (final Exception e) {
      throw e;
    }
  }

  /**
   * Get the parsed expression after calling the <code>constrainedEval()</code> method
   *
   * @return the parsed expression; may return <ode>null</code>
   */
  public IExpr getParsedExpression() {
    return fParsedExpression;
  }
}
