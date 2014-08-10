package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.util.DoubleStack;

public interface INumericComplex {
	/**
	 * Evaluate this function to a complex number
	 *
	 * @return an array of double values whose 0-th element is the real part and
	 *         the first element is the imaginary part.
	 *
	 * @see org.matheclipse.core.eval.EvalComplex
	 */
	double[] evalComplex(DoubleStack stack, int size);
}
