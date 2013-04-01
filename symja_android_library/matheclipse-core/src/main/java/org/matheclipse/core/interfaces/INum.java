package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.NumberUtil;

/**
 * A numeric (double) number.
 * 
 */
public interface INum extends ISignedNumber {
	public double getRealPart();

	public INum add(INum val);

	public INum multiply(INum val);

	public INum pow(INum val);

	/** {@inheritDoc} */
	public boolean isNumIntValue();

	/**
	 * Returns the value of this number as an <code>int</code> (by simply casting
	 * to type <code>int</code>).
	 * 
	 * @return
	 */
	public int intValue();

	/**
	 * Converts this double value to an <code>int</code> value; unlike
	 * {@link #intValue} this method raises {@link ArithmeticException} if this
	 * integer cannot be represented by an <code>int</code> type.
	 * 
	 * @return the numeric value represented by this integer after conversion to
	 *         type <code>int</code>.
	 * @throws ArithmeticException
	 *           if conversion to <code>int</code> is not possible.
	 */
	public int toInt() throws ArithmeticException;
}
