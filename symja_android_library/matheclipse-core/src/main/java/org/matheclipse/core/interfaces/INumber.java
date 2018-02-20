package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ComplexNum;

/**
 * Implemented by all number interfaces
 */
public interface INumber extends IExpr {

    /**
     * Get the absolute value for a given number
     *
     * @return
     */
    @Override
    IExpr abs();

    /**
     * Get a <code>Apcomplex</code> number wrapped into an
     * <code>ApcomplexNum</code> object.
     *
     * @param precision set the precision of the resulting ApcomplexNum
     * @return this signed number represented as an ApcomplexNum
     */
    ApcomplexNum apcomplexNumValue(long precision);

    /**
     * Returns the smallest (closest to negative infinity) <code>IInteger</code>
     * value that is not less than <code>this</code> and is equal to a
     * mathematical integer. This method raises {@link ArithmeticException} if a
     * numeric value cannot be represented by an <code>long</code> type.
     *
     * @return the smallest (closest to negative infinity) <code>IInteger</code>
     * value that is not less than <code>this</code> and is equal to a
     * mathematical integer.
     */
    INumber ceilFraction() throws ArithmeticException;

    /**
     * Compare the absolute value of this number with <code>1</code> and return
     * <ul>
     * <li><code>1</code>, if the absolute value is greater than 1</li>
     * <li><code>0</code>, if the absolute value equals 1</li>
     * <li><code>-1</code>, if the absolute value is less than 1</li>
     * </ul>
     *
     * @return
     */
    int compareAbsValueToOne();

    /**
     * Get the absolute value for a given number
     *
     * @return
     */
    @Override
    IExpr complexArg();

    /**
     * Get a <code>ComplexNum</code> number bject.
     *
     * @return
     */
    ComplexNum complexNumValue();

    /**
     * Gets the signum value of a complex number
     *
     * @return 0 for <code>this == 0</code>; +1 for
     * <code>real(this) &gt; 0</code> or
     * <code>( real(this)==0 &amp;&amp; imaginary(this) &gt; 0 )</code>;
     * -1 for <code>real(this) &lt; 0 || ( real(this) == 0 &amp;&amp;
     * imaginary(this) &lt; 0 )
     */
    int complexSign();

    @Override
    INumber conjugate();

    /**
     * Get the absolute value for a given number
     *
     * @return
     * @deprecated use abs()
     */
    @Deprecated
    IExpr eabs();

    /**
     * Check if this number equals the given <code>int</code> number?
     *
     * @param i the integer number
     * @return
     */
    boolean equalsInt(int i);

    /**
     * Returns the largest (closest to positive infinity) <code>IInteger</code>
     * value that is not greater than <code>this</code> and is equal to a
     * mathematical integer. <br/>
     * This method raises {@link ArithmeticException} if a numeric value cannot
     * be represented by an <code>long</code> type.
     *
     * @return the largest (closest to positive infinity) <code>IInteger</code>
     * value that is not greater than <code>this</code> and is equal to
     * a mathematical integer.
     */
    INumber floorFraction() throws ArithmeticException;

    /**
     * Returns the imaginary part of a complex number
     *
     * @return real part
     */
    double getImaginary();

    /**
     * Returns the real part of a complex number
     *
     * @return real part
     */
    double getReal();

    /**
     * Returns the imaginary part of a complex number
     *
     * @return real part
     */
    @Override
    ISignedNumber im();

    @Override
    INumber opposite();

    /**
     * Returns the real part of a complex number
     *
     * @return real part
     */
    @Override
    ISignedNumber re();

}
