package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.Num;

/**
 * interface for "ractional" numbers (i.e. IInteger or IFraction)
 * 
 */
public interface ISignedNumber extends INumber {

	/**
	 * Get a <code>Apfloat</code> number wrapped into an <code>ApfloatNum</code> object.
	 * 
	 * @param precision
	 *            set the precision of the resulting ApfloatNum
	 * @return this signed number represented as an ApfloatNum
	 */
	public ApfloatNum apfloatNumValue(long precision);

	/**
	 * Get a Java double number wrapped into a <code>Num</code> object.
	 * 
	 * @return
	 */
	public Num numValue();

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

	/**
	 * Test if <code>this</code> signed number is less <code>than</code> that signed number..
	 * 
	 * @return <code>this < that</code>
	 */
	public boolean isLessThan(ISignedNumber that);

	/**
	 * Test if <code>this</code> signed number is greater <code>than</code> that signed number..
	 * 
	 * @return <code>this > that</code>
	 */
	public boolean isGreaterThan(ISignedNumber that);

	/**
	 * Returns (-1) * this
	 * 
	 * @return
	 */
	public ISignedNumber negate();

	/**
	 * Divide <code>this</code> signed number by <code>that</code> signed number.
	 * 
	 * @param that
	 *            a signed number
	 * @return
	 */
	public ISignedNumber divideBy(ISignedNumber that);

	/**
	 * Subtract <code>that</code> signed number from <code>this</code> signed number
	 * 
	 * @param that
	 *            a signed number
	 * @return
	 */
	public ISignedNumber subtractFrom(ISignedNumber that);

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
	 * Returns the closest <code>IInteger</code> to the argument. The result is rounded to an integer by adding 1/2 and taking the
	 * floor of the result.<br/>
	 * This method raises {@link ArithmeticException} if a numeric value cannot be represented by an <code>long</code> type.
	 * 
	 * @return the closest integer to the argument.
	 */
	public IInteger round();

	public ISignedNumber inverse();

	public ISignedNumber opposite();

	public double doubleValue();

	/** {@inheritDoc} */
	public IInteger ceil() throws ArithmeticException;

	/** {@inheritDoc} */
	public IInteger floor() throws ArithmeticException;
}
