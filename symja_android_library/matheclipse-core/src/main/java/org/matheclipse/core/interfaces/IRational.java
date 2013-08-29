package org.matheclipse.core.interfaces;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * Interface for "rational" numbers (i.e. numbers implementing IInteger or IFraction)
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

	/**
	 * Returns this number as <code>BigFraction</code> number.
	 * 
	 * @return <code>this</code> number s big fraction.
	 */
	public BigFraction getFraction();
}
