package org.apfloat.internal;

import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;

/**
 * Creates Number Theoretic Transforms for the
 * <code>float</code> type.
 *
 * @see FloatTableFNTStrategy
 * @see SixStepFNTStrategy
 * @see TwoPassFNTStrategy
 * @see Factor3NTTStrategy
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class FloatNTTBuilder
    extends AbstractNTTBuilder
{
    /**
     * Default constructor.
     */

    public FloatNTTBuilder()
    {
    }

    public NTTStepStrategy createNTTSteps()
    {
        return new FloatNTTStepStrategy();
    }

    public NTTConvolutionStepStrategy createNTTConvolutionSteps()
    {
        return new FloatNTTConvolutionStepStrategy();
    }

    public Factor3NTTStepStrategy createFactor3NTTSteps()
    {
        return new FloatFactor3NTTStepStrategy();
    }

    protected NTTStrategy createSimpleFNTStrategy()
    {
        return new FloatTableFNTStrategy();
    }

    protected NTTStrategy createSixStepFNTStrategy()
    {
        return new SixStepFNTStrategy();
    }

    protected NTTStrategy createTwoPassFNTStrategy()
    {
        return new TwoPassFNTStrategy();
    }

    protected NTTStrategy createFactor3NTTStrategy(NTTStrategy nttStrategy)
    {
        return new Factor3NTTStrategy(nttStrategy);
    }
}
