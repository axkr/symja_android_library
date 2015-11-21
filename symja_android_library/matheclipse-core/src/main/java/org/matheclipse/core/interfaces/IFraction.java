package org.matheclipse.core.interfaces;

import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;

/**
 * interface for "fractional" numbers
 * 
 */
public interface IFraction extends IRational {

	/** {@inheritDoc} */
	public IFraction abs();

	public IFraction add(IFraction parm1);

	/**
	 * Returns an array of two BigIntegers containing (numerator / denominator)
	 * followed by (numerator % denominator).
	 * 
	 * @return
	 */
	public BigInteger[] divideAndRemainder();

	//
	// public IFraction divide(IFraction parm1);

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	public BigInteger getBigDenominator();

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return denominator
	 */
	public BigInteger getBigNumerator();

	public BigFraction getRational();
	
	public IFraction multiply(IFraction parm1);
	
	/**
	 * Returns this number raised at the specified exponent.
	 * 
	 * @param exp
	 *            the exponent.
	 * @return <code>this<sup>exp</sup></code>
	 * @throws ArithmeticException if {@code 0^0} is given.  
	 */
	public IFraction pow(final long exp) throws ArithmeticException;

}
