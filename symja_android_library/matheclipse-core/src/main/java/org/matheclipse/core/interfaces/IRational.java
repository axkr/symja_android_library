package org.matheclipse.core.interfaces;
/**
 * interface for "ractional" numbers (i.e. IInteger or IFraction)
 *
 */
public interface IRational extends ISignedNumber, IBigNumber {
	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	public IInteger getDenominator();

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return denominator
	 */
	public IInteger getNumerator(); 
}
