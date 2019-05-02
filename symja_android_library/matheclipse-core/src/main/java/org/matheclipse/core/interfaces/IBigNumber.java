package org.matheclipse.core.interfaces;

/**
 * 
 * Implemented by all exact &quot;symbolic&quot; number interfaces (i.e. IInteger IFraction, IComplex)
 * 
 * 
 */
public interface IBigNumber extends INumber {
	/**
	 * Create a numeric number from this exact &quot;symbolic&quot; number.
	 * 
	 * @return
	 */
	public INumber numericNumber();

	/**
	 * Get the real part as rational number
	 * 
	 * @return
	 */
	public IRational reRational();

	/**
	 * Get the imaginary part as rational number
	 * 
	 * @return
	 */
	public IRational imRational();
}
