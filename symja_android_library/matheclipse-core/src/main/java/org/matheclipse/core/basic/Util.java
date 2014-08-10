package org.matheclipse.core.basic;

/**
 * Utility class; checks, if the current math calculation thread was stopped
 * (i.e. <code>Thread.currentThread().isInterrupted()</code>
 * 
 */
public class Util { 
	/**
	 * COMPILER switch - set this boolean variable <code>true</code>, if you
	 * would like to use the TimeConstrainedEvaluator (for example in a web
	 * environment (i.e. tomcat...) )
	 * 
	 * @see org.matheclipse.core.eval.TimeConstrainedEvaluator
	 */
	public final static boolean TIME_CONSTRAINED_EVALUATION = true;

	/**
	 * Check if the current calculation thread was stopped.<br/> This is mainly
	 * caused by exceeding calculation time limits.
	 */
	public static final void checkCanceled() {
		if (TIME_CONSTRAINED_EVALUATION) { // compiler switch
			if (Thread.currentThread().isInterrupted()) {
				throw new EvaluationInterruptedException();
			}
		}
	}

}
