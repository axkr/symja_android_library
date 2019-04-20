package org.matheclipse.core.interfaces;

import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;

/**
 * Interface for "rational" numbers (i.e. IInteger, IFraction or INum)
 * 
 */
public interface ISignedNumber extends INumber {

	/** {@inheritDoc} */
	@Override
	public ISignedNumber abs();

	/**
	 * Get a <code>Apfloat</code> number wrapped into an <code>ApfloatNum</code> object.
	 * 
	 * @param precision
	 *            set the precision of the resulting ApfloatNum
	 * @return this signed number represented as an ApfloatNum
	 */
	public ApfloatNum apfloatNumValue(long precision);

	/** {@inheritDoc} */
	@Override
	default IExpr complexArg() {
		if (sign() < 0) {
			return F.Pi;
		}
		return F.C0;
	}

	/** {@inheritDoc} */
	@Override
	default INumber conjugate() {
		return this;
	}

	/**
	 * Divide <code>this</code> signed number by <code>that</code> signed number.
	 * 
	 * @param that
	 *            a signed number
	 * @return
	 */
	public ISignedNumber divideBy(ISignedNumber that);

	/**
	 * Returns the value of the specified number as a {@code double}, which may involve rounding.
	 *
	 * @return the numeric value represented by this object after conversion to type {@code double}.
	 */
	public double doubleValue();

	public IInteger ceilFraction() throws ArithmeticException;

	public IInteger floorFraction() throws ArithmeticException;

	/**
	 * Return the fractional part of this fraction
	 * 
	 * @return
	 */
	public ISignedNumber fractionalPart();

	/**
	 * Return the integer part of this number
	 * 
	 * @return
	 */
	default IInteger integerPart() {
		return isNegative() ? ceilFraction() : floorFraction();
	}

	/** {@inheritDoc} */
	@Override
	public ISignedNumber inverse();

	/**
	 * Test if <code>this</code> signed number is greater <code>than</code> that signed number..
	 * 
	 * @return <code>this > that</code>
	 */
	public boolean isGreaterThan(ISignedNumber that);

	/**
	 * Test if <code>this</code> signed number is less <code>than</code> that signed number..
	 * 
	 * @return <code>this < that</code>
	 */
	public boolean isLessThan(ISignedNumber that);

	/**
	 * Test if this number is negative.
	 * 
	 * @return <code>true</code>, if <code>this < 0</code>
	 */
	@Override
	public boolean isNegative();

	default boolean isNegativeResult() {
		return isNegative();
	}

	default boolean isNonNegativeResult() {
		return !isNegative();
	}

	/**
	 * Test if this number is positive.
	 * 
	 * @return <code>true</code>, if <code>this > 0</code>
	 */
	@Override
	public boolean isPositive();

	default boolean isPositiveResult() {
		return isPositive();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isZero();

	/**
	 * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>lower</code> value. If this is a
	 * <code>ISignedNUmber</code> expression return <code>this</code>.
	 * 
	 * @return <code>F.NIL</code> if this expression is no interval and no signed number.
	 */
	@Override
	default public IExpr lower() {
		return this;
	}

	/**
	 * Returns (-1) * this
	 * 
	 * @return
	 */
	@Override
	public ISignedNumber negate();

	/**
	 * Get a Java double number wrapped into a <code>Num</code> object.
	 * 
	 * @return
	 */
	public INum numValue();

	@Override
	public ISignedNumber opposite();

	/**
	 * Returns the closest <code>IInteger</code> to the argument. The result is rounded to an integer by adding 1/2 and
	 * taking the floor of the result. <br/>
	 * This method raises {@link ArithmeticException} if a numeric value cannot be represented by an <code>long</code>
	 * type.
	 * 
	 * @return the closest integer to the argument.
	 */
	public IInteger round();

	/**
	 * Returns the signum function of this number (i.e., -1, 0 or 1 as the value of this number is negative, zero or
	 * positive).
	 * 
	 * @return -1 if this is a negative number;<br/>
	 *         0 if this is a zero;<br/>
	 *         -1 if this is a negative number;
	 */
	public int sign();

	/**
	 * Subtract <code>that</code> signed number from <code>this</code> signed number
	 * 
	 * @param that
	 *            a signed number
	 * @return
	 */
	public ISignedNumber subtractFrom(ISignedNumber that);

	/**
	 * Converts this number to <code>int</code>; unlike {@link #intValue} this method raises {@link ArithmeticException}
	 * if this number cannot be represented by an <code>int</code> type.
	 * 
	 * @return the numeric value represented by this integer after conversion to type <code>int</code>.
	 * @throws ArithmeticException
	 *             if conversion to <code>int</code> is not possible.
	 */
	public int toInt() throws ArithmeticException;

	/**
	 * Converts this number to <code>long</code>; unlike {@link #longValue} this method raises
	 * {@link ArithmeticException} if this number cannot be represented by a <code>long</code> type.
	 * 
	 * @return the numeric value represented by this integer after conversion to type <code>long</code>.
	 * @throws ArithmeticException
	 *             if conversion to <code>long</code> is not possible.
	 */
	public long toLong() throws ArithmeticException;

	default public IExpr unitStep() {
		return isNegative() ? F.C0 : F.C1;
	}

	/**
	 * If this is a <code>Interval[{lower, upper}]</code> expression return the <code>upper</code> value. If this is a
	 * <code>ISignedNUmber</code> expression return <code>this</code>.
	 * 
	 * @return <code>F.NIL</code> if this expression is no interval and no signed number.
	 */
	@Override
	default public IExpr upper() {
		return this;
	}
}
