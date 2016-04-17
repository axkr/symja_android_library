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
	 * 
	 * @return <i>&#x03BB;</i>(<i>n</i>) where <i>n</i> is the number
	 *         represented by this factors
	 * @throws ArithmeticException
	 *             if internal conversion to <code>long</code> is not possible.
	 */
	public IInteger charmichaelLambda();

	public IInteger div(final IInteger that);

	/**
	 * Returns an array of two IIntegers containing (this / that) followed by
	 * (this % that).
	 * 
	 * @param that
	 * @return
	 */
	public IInteger[] divideAndRemainder(final IInteger that);

	public IAST divisors();

	@Override
	public IInteger eabs();

	public IInteger eulerPhi() throws ArithmeticException;

	/**
	 * Get the highest exponent of <code>base</code> that divides
	 * <code>this</code>
	 * 
	 * @param base
	 *            an integer greater than 1
	 * 
	 * @return the exponent
	 */
	public IExpr exponent(IInteger base);

	/**
	 * Returns the greatest common divisor of this large integer and the one
	 * specified.
	 * 
	 */
	public IInteger gcd(IInteger val);

	/**
	 * Returns the numerator of this Rational.
	 * 
	 * @return numerator
	 */
	@Override
	public BigInteger getBigNumerator();

	/**
	 * Converts this large integer to <code>int</code>; unlike {@link #toInt}
	 * this method raises no exception, if this integer cannot be represented by
	 * an <code>int</code> type.
	 * 
	 * @return the numeric value represented by this integer after conversion to
	 *         type <code>int</code>.
	 */
	public int intValue();

	public boolean isEven();

	public boolean isOdd();

	public boolean isProbablePrime();

	public boolean isProbablePrime(int certainty);

	public IInteger jacobiSymbol(IInteger b);

	public IInteger jacobiSymbolF();

	public IInteger jacobiSymbolG(IInteger b);

	/**
	 * Returns the least common multiple of this integer and the one specified.
	 * 
	 */
	public IInteger lcm(IInteger val);

	public long longValue();

	public IInteger mod(final IInteger that);

	public IInteger modInverse(final IInteger m);

	public IInteger modPow(final IInteger exp, final IInteger m);

	public IInteger moebiusMu();

	/**
	 * Multiply this integer with value
	 * 
	 * @param value
	 * @return
	 */
	public IInteger multiply(IInteger value);

	/**
	 * Returns an <code>IInteger</code> whose value is <code>(-1) * this</code>.
	 * 
	 * @return
	 */
	@Override
	public IInteger negate();

	/**
	 * Returns the n-th integer root
	 * 
	 * @param n
	 * @return
	 * @throws ArithmeticException
	 */
	public IInteger nthRoot(int n) throws ArithmeticException;

	/**
	 * Split this integer into the nth-root (with prime factors less equal 1021)
	 * and the &quot;rest factor&quot;
	 * 
	 * @return <code>{nth-root, rest factor}</code>
	 */
	public IInteger[] nthRootSplit(int n);

	public IInteger pow(final long exp) throws ArithmeticException;

	public IInteger[] primitiveRoots() throws ArithmeticException;

	public IInteger quotient(final IInteger that);

	public IInteger shiftLeft(final int n);

	public IInteger shiftRight(final int n);

	public IInteger subtract(IInteger value);

	public byte[] toByteArray();
}
