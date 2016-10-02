package org.matheclipse.core.eval;

import java.io.Writer;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.TimeExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Run the evaluation of a given math formula <code>String</code> in a time
 * limited thread
 */
public class TimeConstrainedEvaluator extends EvalUtilities implements Runnable {

	protected IExpr fEvaluationResult;

	protected Throwable fException;

	protected IExpr fParsedExpression;

	private long fMilliSeconds;

	private final boolean fRelaxedSyntax;

	private boolean fTraceEvaluation;

	public TimeConstrainedEvaluator(final EvalEngine evalEngine, final boolean msie, final long milliSeconds) {
		this(evalEngine, msie, milliSeconds, false);

	}

	public TimeConstrainedEvaluator(final EvalEngine evalEngine, final boolean msie, final long milliSeconds, boolean relaxedSyntax) {
		super(evalEngine, msie, relaxedSyntax);
		fMilliSeconds = milliSeconds;
		fRelaxedSyntax = relaxedSyntax;
		fTraceEvaluation = false;
	}

	@Override
	public void run() {
		try {
			startRequest();
			if (fTraceEvaluation) {
				fEvaluationResult = evalTrace(fParsedExpression, null, F.List());
			} else {
				fEvaluationResult = evaluate(fParsedExpression);
			}
		} catch (final Exception e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
			fException = e;
		} catch (final OutOfMemoryError e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
			fEvaluationResult = F.$str("OutOfMemoryError");
			// fException = e;
		} catch (final StackOverflowError e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
			fEvaluationResult = F.$str("StackOverflowError");
			// fException = e;
		}
	}

	/**
	 * Runs the evaluation of the given math formula <code>String</code> in a time
	 * limited thread
	 * 
	 * @param traceEvaluation
	 * 
	 */
	public IExpr constrainedEval(final Writer writer, final String inputString, boolean traceEvaluation) throws Exception {

		fEvaluationResult = F.NIL;
		fException = null;
		fParsedExpression = null;
		fEvalEngine.setStopRequested(false);
		fTraceEvaluation = traceEvaluation;

		try {
			EvalEngine.set(fEvalEngine);
			fParsedExpression = fEvalEngine.parse(inputString);
		} catch (final RuntimeException e) {
			throw e;
		}

		return constrainedEval(writer, fParsedExpression);

	}

	/**
	 * Runs the evaluation of the given math expression in a time limited thread
	 * 
	 */
	public IExpr constrainedEval(final Writer writer, final IExpr inputExpression) throws Exception {

		fEvaluationResult = F.NIL;
		fException = null;
		fParsedExpression = inputExpression;
		fEvalEngine.setStopRequested(false);

		try {
			final Thread thread = new Thread(this, "TimeConstrainedEvaluator");// EvaluationRunnable();
			thread.start();
			thread.join(fMilliSeconds);
			if (thread.isAlive()) {
				thread.interrupt();
				fEvalEngine.stopRequest();
				// wait a bit, so the thread can stop by itself
				Thread.sleep(Config.TIME_CONSTRAINED_SLEEP_MILLISECONDS);
				if (thread.isAlive()) {
					// call the deprecated method as last possible exit
					thread.stop();
					throw new TimeExceeded();
				}
			}
			if (fException != null) {
				writer.write(fException.getMessage() != null ? fException.getMessage() : "Exception: " + fException.getClass().getName());
				writer.write('\n');
			}

			if ((fEvaluationResult.isPresent()) && !fEvaluationResult.equals(F.Null)) {
				OutputFormFactory.get(fRelaxedSyntax).convert(writer, fEvaluationResult);
			}
			return fEvaluationResult;
		} catch (final Exception e) {
			throw e;
		}
	}

	/**
	 * Get the parsed expression after calling the <code>constrainedEval()</code>
	 * method
	 * 
	 * @return the parsed expression; may return <ode>null</code>
	 */
	public IExpr getParsedExpression() {
		return fParsedExpression;
	}

}