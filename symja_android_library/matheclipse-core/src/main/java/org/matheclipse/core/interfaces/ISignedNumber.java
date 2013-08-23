package org.matheclipse.core.interfaces;

/**
 * interface for "ractional" numbers (i.e. IInteger or IFraction)
 * 
 */
public interface ISignedNumber extends INumber {

	/**
	 * Test if this number is negative.
	 * 
	 * @return <code>true</code>, if <code>this < 0</code>
	 */
	public boolean isNegative();

	/**
	 * Test if this number is positive.
	 * 
	 * @return <code>true</code>, if <code>this > 0</code>
	 */
	public boolean isPositive();

	/** {@inheritDoc} */
	public boolean isZero();

	public boolean isLessThan(ISignedNumber that);

	public boolean isGreaterThan(ISignedNumber that);

	/**
	 * Returns  (-1) * this
	 * 
	 * @return
	 */
	public ISignedNumber negate();

	/**
	 * Subtract <code>that</code> signed number from <code>this</code> signed number
	 * 
	 * @param that
	 *            a signed number
	 * @return
	 */
	public ISignedNumber minus(ISignedNumber that);

	/**
	 * Returns the signum function of this number (i.e., -1, 0 or 1 as the value of this number is negative, zero or positive).
	 * 
	 * @return -1 if this is a negative number;<br/>
	 *         0 if this is a zero;<br/>
	 *         -1 if this is a negative number;
	 */
	public int sign();

	/**
	 * Converts this number to <code>int</code>; unlike {@link #intValue} this method raises {@link ArithmeticException} if this
	 * number cannot be represented by an <code>int</code> type.
	 * 
	 * @return the numeric value represented by this integer after conversion to type <code>int</code>.
	 * @throws ArithmeticException
	 *             if conversion to <code>int</code> is not possible.
	 */
	public int toInt() throws ArithmeticException;

	/**
	 * Converts this number to <code>long</code>; unlike {@link #longValue} this method raises {@link ArithmeticException} if this
	 * number cannot be represented by an <code>long</code> type.
	 * 
	 * @return the numeric value represented by this integer after conversion to type <code>long</code>.
	 * @throws ArithmeticException
	 *             if conversion to <code>int</code> is not possible.
	 */
	public long toLong() throws ArithmeticException;

	/**
	 * Returns the smallest (closest to negative infinity) <code>IInteger</code> value that is not less than <code>this</code> and
	 * is equal to a mathematical integer. This method raises {@link ArithmeticException} if a numeric value cannot be represented
	 * by an <code>long</code> type.
	 * 
	 * @return the smallest (closest to negative infinity) <code>IInteger</code> value that is not less than <code>this</code> and
	 *         is equal to a mathematical integer.
	 */
	public IInteger ceil() throws ArithmeticException;

	/**
	 * Returns the largest (closest to positive infinity) <code>IInteger</code> value that is not greater than <code>this</code> and
	 * is equal to a mathematical integer. <br/>
	 * This method raises {@link ArithmeticException} if a numeric value cannot be represented by an <code>long</code> type.
	 * 
	 * @return the largest (closest to positive infinity) <code>IInteger</code> value that is not greater than <code>this</code> and
	 *         is equal to a mathematical integer.
	 */
	public IInteger floor() throws ArithmeticException;

	/**
	 * Returns the closest <code>IInteger</code> to the argument. The result is rounded to an integer by adding 1/2 and taking the
	 * floor of the result.<br/>
	 * This method raises {@link ArithmeticException} if a numeric value cannot be represented by an <code>long</code> type.
	 * 
	 * @return the closest integer to the argument.
	 */
	public IInteger round();

	public IExpr inverse();

	public IExpr opposite();

	public double doubleValue();
}
