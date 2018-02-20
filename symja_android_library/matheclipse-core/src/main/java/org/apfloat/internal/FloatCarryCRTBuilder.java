package org.apfloat.internal;

import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.CarryCRTStrategy;
import org.apfloat.spi.CarryCRTStepStrategy;

/**
 * Creates carry-CRT related objects, for the
 * <code>float</code> type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class FloatCarryCRTBuilder
    implements CarryCRTBuilder<float[]>
{
    /**
     * Default constructor.
     */

    public FloatCarryCRTBuilder()
    {
    }

    public CarryCRTStrategy createCarryCRT(int radix)
    {
        return new StepCarryCRTStrategy(radix);
    }

    public CarryCRTStepStrategy<float[]> createCarryCRTSteps(int radix)
    {
        return new FloatCarryCRTStepStrategy(radix);
    }
}
