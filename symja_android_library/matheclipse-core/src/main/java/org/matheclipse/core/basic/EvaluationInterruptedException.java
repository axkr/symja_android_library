package org.matheclipse.core.basic;

/**
 * 
 */
public class EvaluationInterruptedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2386172732682109588L;

	public EvaluationInterruptedException() {

	}
 
	@Override
	public String getMessage() {
		return "Evaluation Interrupted. Probably time limit exceeded.\n";
	}

}
