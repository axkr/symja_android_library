package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.DataStorage.Iterator;

/**
 * Basic addition strategy for the <code>double</code> element type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */
public class DoubleAdditionStrategy
    extends DoubleBaseMath
    implements AdditionStrategy<Double>
{
    /**
     * Creates an addition strategy using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public DoubleAdditionStrategy(int radix)
    {
        super(radix);
    }

    public Double add(Iterator src1, Iterator src2, Double carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseAdd(src1, src2, carry, dst, size);
    }

    public Double subtract(Iterator src1, Iterator src2, Double carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseSubtract(src1, src2, carry, dst, size);
    }

    public Double multiplyAdd(Iterator src1, Iterator src2, Double src3, Double carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseMultiplyAdd(src1, src2, src3, carry, dst, size);
    }

    public Double divide(Iterator src1, Double src2, Double carry, Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        return baseDivide(src1, src2, carry, dst, size);
    }

    public Double zero()
    {
        return (double) 0;
    }

    private static final long serialVersionUID = 6863520700151824670L;
}
