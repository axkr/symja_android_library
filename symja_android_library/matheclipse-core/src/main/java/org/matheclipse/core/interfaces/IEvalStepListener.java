package org.matheclipse.core.interfaces;

/**
 * A listener which could listen to the <code>EvalEngine#evalLoop()</code> steps, to implement an evaluation trace or a
 * step by step evaluation.
 *
 */
public interface IEvalStepListener {

	/**
	 * Sets up the next evaluation step.
	 * 
	 * @param expr
	 *            the inpput expression which should currently be evaluated
	 * @param recursionDepth
	 *            the current recursion depth of this evaluation step
	 */
	public abstract void setUp(IExpr expr, int recursionDepth);

	/**
	 * Tear down this evaluation step (called finally at the evaluation loop).
	 * 
	 * @param recursionDepth
	 *            the current recursion depth of this evaluation step
	 * 
	 */
	public abstract void tearDown(int recursionDepth);

	/**
	 * Add a new step in which the <code>inputExpr</code> was evaluated to the new <code>resultExpr</code>.
	 * 
	 * @param inputExpr
	 * @param resultExpr
	 * @param recursionDepth
	 *            TODO
	 * @param iterationCounter
	 */
	public abstract void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter);

}