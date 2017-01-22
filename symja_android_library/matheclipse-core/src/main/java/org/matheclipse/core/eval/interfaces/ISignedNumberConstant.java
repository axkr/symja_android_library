package org.matheclipse.core.eval.interfaces;

/**
 * <p>
 * Interface for &quot;real numeric constants&quot;.
 * </p>
 * 
 */
public interface ISignedNumberConstant {
	/**
	 * Evaluate the symbol to a double number
	 *
	 * @return
	 *
	 * @see org.matheclipse.core.eval.DoubleStackEvaluator
	 */
	double evalReal();
}
