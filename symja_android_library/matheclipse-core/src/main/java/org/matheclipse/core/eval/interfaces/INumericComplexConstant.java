package org.matheclipse.core.eval.interfaces;

public interface INumericComplexConstant {
	/**
	 * Evaluate the symbol to a complex number
	 *
	 * @return an array of double values whose 0-th element is the real part and
	 *         the first element is the imaginary part.
	 *
	 * @see org.matheclipse.core.eval.EvalComplex
	 */
	double[] evalComplex();
}
