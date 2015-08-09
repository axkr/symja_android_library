package org.matheclipse.core.interfaces;

import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;

/**
 * Interface for "rational" numbers (i.e. numbers implementing IInteger or IFraction)
 * 
 */
public interface IRational extends ISignedNumber, IBigNumber {

	/** {@inheritDoc} */
	public IRational abs();

	/**
	 * Return the factors paired with their exponents for integer and fractional numbers. For factors of the denominator part of
	 * fractional numbers the exponents are negative.
	 * 
	 * <pre>
	 * factorInteger(-4) ==> {{-1,1},{2,2}}
	 * </pre>
	 */
	public IAST factorInteger();
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
	
	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	public IInteger getDenominator();

	/**
	 * Returns this number as <code>BigFraction</code> number.
	 * 
	 * @return <code>this</code> number s big fraction.
	 */
	public BigFraction getFraction();

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return denominator
	 */
	public IInteger getNumerator();
}
