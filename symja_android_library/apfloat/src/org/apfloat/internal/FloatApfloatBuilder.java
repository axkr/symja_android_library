package org.apfloat.internal;

import java.io.PushbackReader;
import java.io.IOException;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ApfloatBuilder;
import org.apfloat.spi.ApfloatImpl;

/**
 * Builder class for building {@link ApfloatImpl} implementations with the
 * <code>float</code> data element type.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public class FloatApfloatBuilder
    implements ApfloatBuilder
{
    /**
     * Default constructor.
     */

    public FloatApfloatBuilder()
    {
    }

    public ApfloatImpl createApfloat(String value, long precision, int radix, boolean isInteger)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return new FloatApfloatImpl(value, precision, radix, isInteger);
    }

    public ApfloatImpl createApfloat(long value, long precision, int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return new FloatApfloatImpl(value, precision, radix);
    }

    public ApfloatImpl createApfloat(double value, long precision, int radix)
        throws NumberFormatException, ApfloatRuntimeException
    {
        return new FloatApfloatImpl(value, precision, radix);
    }

    public ApfloatImpl createApfloat(PushbackReader in, long precision, int radix, boolean isInteger)
        throws IOException, NumberFormatException, ApfloatRuntimeException
    {
        return new FloatApfloatImpl(in, precision, radix, isInteger);
    }
}
