package org.apfloat.spi;

import java.io.Serializable;
import java.io.Writer;
import java.io.IOException;

import org.apfloat.ApfloatRuntimeException;

/**
 * Interface for apfloat implementations. An <code>ApfloatImpl</code> implements all of the
 * low-level functionality that is needed behind the high-level apfloat API.<p>
 *
 * A class implementing <code>ApfloatImpl</code> is not required to accept any other <code>ApfloatImpl</code>
 * class as the argument than the same implementing class.
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public interface ApfloatImpl
    extends Serializable
{
    /**
     * Add or subtract an <code>ApfloatImpl</code> to this object.
     *
     * @param x The number to be added or subtracted to this <code>ApfloatImpl</code>.
     * @param subtract <code>true</code> if the numbers are to be subtracted, <code>false</code> if added.
     *
     * @return <code>this + x</code> or <code>this - x</code> depending on the <code>subtract</code> argument.
     */

    public ApfloatImpl addOrSubtract(ApfloatImpl x, boolean subtract)
        throws ApfloatRuntimeException;

    /**
     * Multiply this object by an <code>ApfloatImpl</code>.
     *
     * @param x The number to be multiplied by this <code>ApfloatImpl</code>.
     *
     * @return <code>this * x</code>.
     */

    public ApfloatImpl multiply(ApfloatImpl x)
        throws ApfloatRuntimeException;

    /**
     * Returns if this <code>ApfloatImpl</code> is "short". Typically <code>ApfloatImpl</code>
     * is "short" if its mantissa fits in one machine word. If the apfloat is "short",
     * some algorithms can be performed faster.<p>
     *
     * The return value of this method is highly implementation dependent.
     *
     * @return <code>true</code> if the <code>ApfloatImpl</code> is "short", <code>false</code> if not.
     *
     * @see org.apfloat.Apfloat#isShort()
     */

    public boolean isShort()
        throws ApfloatRuntimeException;

    /**
     * Divide this <code>ApfloatImpl</code> by an <code>ApfloatImpl</code> that is "short".
     *
     * @param x The number by which this <code>ApfloatImpl</code> is to be divided.
     *
     * @return <code>this / x</code>.
     */

    public ApfloatImpl divideShort(ApfloatImpl x)
        throws ApfloatRuntimeException;

    /**
     * Returns this <code>ApfloatImpl</code> rounded towards zero.
     *
     * @return This <code>ApfloatImpl</code> rounded towards zero.
     */

    public ApfloatImpl absFloor()
        throws ApfloatRuntimeException;

    /**
     * Returns this <code>ApfloatImpl</code> rounded away from zero.
     *
     * @return This <code>ApfloatImpl</code> rounded away from zero.
     */

    public ApfloatImpl absCeil()
        throws ApfloatRuntimeException;

    /**
     * Returns the fractional part of this <code>ApfloatImpl</code>.
     *
     * @return The fractional part of this <code>ApfloatImpl</code>.
     *
     * @since 1.7.0
     */

    public ApfloatImpl frac()
        throws ApfloatRuntimeException;

    /**
     * Returns the radix of this <code>ApfloatImpl</code>.
     *
     * @return The radix of this <code>ApfloatImpl</code>.
     */

    public int radix();

    /**
     * Returns the precision of this <code>ApfloatImpl</code>.
     *
     * @return The precision of this <code>ApfloatImpl</code>.
     */

    public long precision();

    /**
     * Returns the size of the mantissa of this <code>ApfloatImpl</code>.
     *
     * @return The size of the mantissa of this <code>ApfloatImpl</code>.
     *
     * @since 1.2
     */

    public long size()
        throws ApfloatRuntimeException;

    /**
     * Returns this <code>ApfloatImpl</code> with the specified precision.
     *
     * @param precision The precision.
     *
     * @return This <code>ApfloatImpl</code> with the specified precision.
     */

    public ApfloatImpl precision(long precision)
        throws ApfloatRuntimeException;

    /**
     * Returns the scale of this <code>ApfloatImpl</code>.
     *
     * @return The scale of this <code>ApfloatImpl</code>.
     *
     * @see org.apfloat.Apfloat#scale()
     */

    public long scale()
        throws ApfloatRuntimeException;

    /**
     * Returns the signum of this <code>ApfloatImpl</code>.
     *
     * @return The signum of this <code>ApfloatImpl</code>.
     *
     * @see org.apfloat.Apfloat#signum()
     */

    public int signum();

    /**
     * Returns this <code>ApfloatImpl</code> negated.
     *
     * @return <code>-this</code>.
     */

    public ApfloatImpl negate()
        throws ApfloatRuntimeException;

    /**
     * Returns the value of the this number as a <code>double</code>.
     *
     * If the number is greater than <code>Double.MAX_VALUE</code>,
     * then <code>Double.POSITIVE_INFINITY</code> is returned.<p>
     *
     * If the number is less than <code>Double.MIN_VALUE</code>,
     * then <code>Double.NEGATIVE_INFINITY</code> is returned.<p>
     *
     * If the number is very small in magnitude, underflow may
     * happen and zero is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>double</code>.
     */

    public double doubleValue();

    /**
     * Returns the value of the this number as a <code>long</code>.
     * The fractional part is truncated towards zero.<p>
     *
     * If the number is greater than <code>Long.MAX_VALUE</code>,
     * then <code>Long.MAX_VALUE</code> is returned.<p>
     *
     * If the number is less than <code>Long.MIN_VALUE</code>,
     * then <code>Long.MIN_VALUE</code> is returned.
     *
     * @return The numeric value represented by this object after conversion to type <code>long</code>.
     */

    public long longValue();

    /**
     * Tests if this number is equal to 1.
     *
     * @return <code>true</code> if this number is equal to one, otherwise <code>false</code>.
     */

    public boolean isOne()
        throws ApfloatRuntimeException;

    /**
     * The number of equal digits in this <code>ApfloatImpl</code> and another number.
     *
     * @param x The number to compare with.
     *
     * @return The number of equal digits in this <code>ApfloatImpl</code> and <code>x</code>.
     *
     * @see org.apfloat.Apfloat#equalDigits(org.apfloat.Apfloat)
     */

    public long equalDigits(ApfloatImpl x)
        throws ApfloatRuntimeException;

    /**
     * Compare this <code>ApfloatImpl</code> and another number.
     *
     * @param x The number to compare with.
     *
     * @return Zero, less than zero or greater than zero depending on the result of the comparison.
     *
     * @see org.apfloat.Apfloat#compareTo(org.apfloat.Apfloat)
     */

    public int compareTo(ApfloatImpl x)
        throws ApfloatRuntimeException;

    /**
     * Returns the hash code for this <code>ApfloatImpl</code>.
     *
     * @return The hash code for this <code>ApfloatImpl</code>.
     */

    public int hashCode();

    /**
     * Convert this <code>ApfloatImpl</code> to <code>String</code>.
     *
     * @param pretty Flag for formatting.
     *
     * @return String representation of this <code>ApfloatImpl</code>.
     *
     * @see org.apfloat.Apfloat#toString(boolean)
     */

    public String toString(boolean pretty)
        throws ApfloatRuntimeException;

    /**
     * Print this <code>ApfloatImpl</code> to a stream.
     *
     * @param out The stream to write to.
     * @param pretty Flag for formatting.
     *
     * @exception java.io.IOException In case of I/O error writing to the stream.
     *
     * @see org.apfloat.Apfloat#writeTo(Writer,boolean)
     */

    public void writeTo(Writer out, boolean pretty)
        throws IOException, ApfloatRuntimeException;
}
