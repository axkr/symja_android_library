package org.matheclipse.core.eval.interfaces;

public interface INumeric {
	/**
	 * Evaluate the function to a double number
	 *
	 * @return
	 *
	 * @see org.matheclipse.core.eval.DoubleStackEvaluator
	 */
	double evalReal(double[] stack, int top, int size);
}
