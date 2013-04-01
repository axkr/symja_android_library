package org.matheclipse.core.interfaces;

/**
 * interface for "ractional" numbers (i.e. IInteger or IFraction)
 * 
 */
public interface ISignedNumber extends INumber {

	public boolean isNegative();

	public boolean isPositive();

	public boolean isZero();

	public boolean isLessThan(ISignedNumber that);

	public boolean isGreaterThan(ISignedNumber that);

	/**
	 * this = (-1) * this
	 * 
	 * @return
	 */
	public ISignedNumber negate();

	/**
	 * Subtract <code>that</code> signed number from <code>this</code> signed
	 * number
	 * 
	 * @param that
	 *          a signed number
	 * @return
	 */
	public ISignedNumber minus(ISignedNumber that);

	/**
	 * Returns the signum function of this number (i.e., -1, 0 or 1 as the value
	 * of this number is negative, zero or positive).
	 * 
	 * @return -1 if this is a negative number;<br/>
	 *         0 if this is a zero;<br/>
	 *         -1 if this is a negative number;
	 */
	public int sign();

	/**
	 * Returns the smallest (closest to negative infinity)
	 * <code>ISignedNumber</code> value that is not less than <code>this</code>
	 * and is equal to a mathematical integer.
	 * 
	 * @return the smallest (closest to negative infinity)
	 *         <code>ISignedNumber</code> value that is not less than
	 *         <code>this</code> and is equal to a mathematical integer.
	 */
	public ISignedNumber ceil();

	/**
	 * Returns the largest (closest to positive infinity)
	 * <code>ISignedNumber</code> value that is not greater than <code>this</code>
	 * and is equal to a mathematical integer.
	 * 
	 * @return the largest (closest to positive infinity)
	 *         <code>ISignedNumber</code> value that is not greater than
	 *         <code>this</code> and is equal to a mathematical integer.
	 */
	public ISignedNumber floor();

	/**
	 * Answers the double conversion of the result of rounding the argument to an
	 * integer.
	 * 
	 * @return the closest integer to the argument (as an IInteger in symbolic
	 *         mode or a INum in numeric mode).
	 */
	public ISignedNumber round();

	public IExpr inverse();

	public IExpr opposite();

	public double doubleValue();
}
