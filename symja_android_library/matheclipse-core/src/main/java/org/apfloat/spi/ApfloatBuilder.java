package org.apfloat.spi;

import java.io.PushbackReader;
import java.io.IOException;

import org.apfloat.ApfloatRuntimeException;

/**
 * An ApfloatBuilder contains factory methods to create
 * new instances of {@link ApfloatImpl} implementations.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface ApfloatBuilder
{
    /**
     * Create a new <code>ApfloatImpl</code> instance from a <code>String</code>.
     *
     * @param value The string to be parsed to a number.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     * @param isInteger Specifies if the number to be parsed from the string is to be treated as an integer or not.
     *
     * @return A new <code>ApfloatImpl</code>.
     *
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public ApfloatImpl createApfloat(String value, long precision, int radix, boolean isInteger)
        throws NumberFormatException, ApfloatRuntimeException;

    /**
     * Create a new <code>ApfloatImpl</code> instance from a <code>long</code>.
     *
     * @param value The value of the number.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     *
     * @return A new <code>ApfloatImpl</code>.
     *
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public ApfloatImpl createApfloat(long value, long precision, int radix)
        throws NumberFormatException, ApfloatRuntimeException;

    /**
     * Create a new <code>ApfloatImpl</code> instance from a <code>double</code>.
     *
     * @param value The value of the number.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     *
     * @return A new <code>ApfloatImpl</code>.
     *
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public ApfloatImpl createApfloat(double value, long precision, int radix)
        throws NumberFormatException, ApfloatRuntimeException;

    /**
     * Create a new <code>ApfloatImpl</code> instance reading from a stream.
     *
     * @param in The stream to read from.
     * @param precision The precision of the number (in digits of the radix).
     * @param radix The radix in which the number is created.
     * @param isInteger Specifies if the number to be parsed from the stream is to be treated as an integer or not.
     *
     * @return A new <code>ApfloatImpl</code>.
     *
     * @exception java.io.IOException If an I/O error occurs accessing the stream.
     * @exception java.lang.NumberFormatException If the number is not valid.
     */

    public ApfloatImpl createApfloat(PushbackReader in, long precision, int radix, boolean isInteger)
        throws IOException, NumberFormatException, ApfloatRuntimeException;
}
