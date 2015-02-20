package org.matheclipse.core.interfaces;

/**
 * An abstract listener which could listen to the <code>EvalEngine#evalLoop()</code> steps, to implement an evaluation trace or a
 * step by step evaluation.
 *
 */
public abstract class AbstractEvalStepListener implements IEvalStepListener {
	/** {@inheritDoc} */
	@Override
	public void setUp(IExpr expr, int recursionDepth) {

	}

	/** {@inheritDoc} */
	@Override
	public void tearDown(int recursionDepth) {

	}

	/** {@inheritDoc} */
	@Override
	public abstract void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter);

}