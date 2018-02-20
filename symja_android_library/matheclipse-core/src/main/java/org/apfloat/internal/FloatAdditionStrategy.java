package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.DataStorage.Iterator;

/**
 * Basic addition strategy for the <code>float</code> element type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */
public class FloatAdditionStrategy
    extends FloatBaseMath
    implements AdditionStrategy<Float>
{
    /**
     * Creates an addition strategy using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public FloatAdditionStrategy(int radix)
    {
        super(radix);
    }

    public Float add(Iterator src1, Iterator src2, Float carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseAdd(src1, src2, carry, dst, size);
    }

    public Float subtract(Iterator src1, Iterator src2, Float carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseSubtract(src1, src2, carry, dst, size);
    }

    public Float multiplyAdd(Iterator src1, Iterator src2, Float src3, Float carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseMultiplyAdd(src1, src2, src3, carry, dst, size);
    }

    public Float divide(Iterator src1, Float src2, Float carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseDivide(src1, src2, carry, dst, size);
    }

    public Float zero()
    {
        return (float) 0;
    }

    private static final long serialVersionUID = -8811571288007744481L;
}
