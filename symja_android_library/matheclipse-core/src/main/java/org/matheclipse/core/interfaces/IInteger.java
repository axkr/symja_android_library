package org.matheclipse.core.interfaces;

import java.math.BigInteger;

/**
 * An expression representing a big integer number
 * 
 */
public interface IInteger extends IRational {
	/**
	 * Certainty for the isProbablePrime() method
	 */
	public final static int PRIME_CERTAINTY = 32;

	public IInteger add(IInteger val);

	/**
	 * Get the highest exponent of <code>base</code> that divides <code>this</code>
	 * @param base
	 *            an integer greater than 1
	 * 
	 * @return the exponent
	 */
	public IExpr exponent(IInteger base);

	public boolean isEven();

	public boolean isOdd();

	public boolean isProbablePrime();

	public boolean isProbablePrime(int certainty);

	/**
	 * Returns the numerator of this Rational.
	 * 
	 * @return numerator
	 */
	public BigInteger getBigNumerator();

	/**
	 * Multiply this integer with value
	 * 
	 * @param value
	 * @return
	 */
	public IInteger multiply(IInteger value);

	public IInteger subtract(IInteger value);

	public IInteger pow(int exponent);

	/**
	 * Converts this large integer to <code>int</code>; unlike {@link #toInt} this method raises no exception, if this integer
	 * cannot be represented by an <code>int</code> type.
	 * 
	 * @return the numeric value represented by this integer after conversion to type <code>int</code>.
	 */
	public int intValue();

	public long longValue();

	/**
	 * Returns the greatest common divisor of this large integer and the one specified.
	 * 
	 */
	public IInteger gcd(IInteger val);

	/**
	 * Returns the least common multiple of this large integer and the one specified.
	 * 
	 */
	public IInteger lcm(IInteger val);

	/**
	 * Returns the n-th integer root
	 * 
	 * @param n
	 * @return
	 * @throws ArithmeticException
	 */
	public IInteger nthRoot(int n) throws ArithmeticException;

	/**
	 * Split this integer into the nth-root (with prime factors less equal 1021) and the &quot;rest factor&quot;
	 * 
	 * @return <code>{nth-root, rest factor}</code>
	 */
	public IInteger[] nthRootSplit(int n);
}
