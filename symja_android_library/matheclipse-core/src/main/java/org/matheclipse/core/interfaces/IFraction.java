package org.matheclipse.core.interfaces;

import java.math.BigInteger;

import org.apache.commons.math4.fraction.BigFraction;

/**
 * interface for "fractional" numbers
 * 
 */
public interface IFraction extends IRational {

	/** {@inheritDoc} */
	@Override
	public IFraction abs();

	public IFraction add(IFraction parm1);

	public IFraction div(IFraction other);

	/**
	 * Returns an array of two BigIntegers containing (numerator / denominator)
	 * followed by (numerator % denominator).
	 * 
	 * @return
	 */
	public IInteger[] divideAndRemainder();

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return denominator
	 */
	@Override
	public BigInteger getBigDenominator();

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return denominator
	 */
	@Override
	public BigInteger getBigNumerator();

	public BigFraction getRational();

	/**
	 * Returns a new rational representing the inverse of <code>this</code>.
	 * 
	 * @return Inverse of <code>this</code>.
	 */
	@Override
	public IFraction inverse();

	public IFraction mul(IFraction other);

	public IFraction multiply(IFraction parm1);

	/**
	 * Returns a new rational equal to <code>-this</code>.
	 * 
	 * @return <code>-this</code>.
	 */
	@Override
	public IFraction negate();

	@Override
	public INumber normalize(); 

	/**
	 * Returns this number raised at the specified exponent.
	 * 
	 * @param exp
	 *            the exponent.
	 * @return <code>this<sup>exp</sup></code>
	 * @throws ArithmeticException
	 *             if {@code 0^0} is given.
	 */
	public IFraction pow(final long exp) throws ArithmeticException;
	
	public IFraction sub(IFraction parm1);
}
