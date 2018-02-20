package org.apfloat.internal;

import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.CarryCRTStrategy;
import org.apfloat.spi.CarryCRTStepStrategy;

/**
 * Creates carry-CRT related objects, for the
 * <code>int</code> type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class IntCarryCRTBuilder
    implements CarryCRTBuilder<int[]>
{
    /**
     * Default constructor.
     */

    public IntCarryCRTBuilder()
    {
    }

    public CarryCRTStrategy createCarryCRT(int radix)
    {
        return new StepCarryCRTStrategy(radix);
    }

    public CarryCRTStepStrategy<int[]> createCarryCRTSteps(int radix)
    {
        return new IntCarryCRTStepStrategy(radix);
    }
}
