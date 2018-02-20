package org.apfloat.internal;

import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.CarryCRTStrategy;
import org.apfloat.spi.CarryCRTStepStrategy;

/**
 * Creates carry-CRT related objects, for the
 * <code>double</code> type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class DoubleCarryCRTBuilder
    implements CarryCRTBuilder<double[]>
{
    /**
     * Default constructor.
     */

    public DoubleCarryCRTBuilder()
    {
    }

    public CarryCRTStrategy createCarryCRT(int radix)
    {
        return new StepCarryCRTStrategy(radix);
    }

    public CarryCRTStepStrategy<double[]> createCarryCRTSteps(int radix)
    {
        return new DoubleCarryCRTStepStrategy(radix);
    }
}
