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
	 * Returns an array of two BigIntegers containing (numerator / denominator) followed by (numerator % denominator).
	 * 
	 * @return
	 */
	public BigInteger[] divideAndRemainder();

	//
	// public IFraction divide(IFraction parm1);

	public BigFraction getRational();

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

	public IFraction multiply(IFraction parm1);

	//
	// public IFraction subtract(IFraction parm1);

	public IFraction pow(final int exp);

	/**
	 * Return the normalized form of this number (i.e. if the denominator part equals one, return the numerator part as an integer
	 * number).
	 * 
	 * @return
	 */
	public INumber normalize();
}
