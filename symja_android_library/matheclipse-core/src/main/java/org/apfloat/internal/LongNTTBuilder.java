package org.apfloat.internal;

import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;

/**
 * Creates Number Theoretic Transforms for the
 * <code>long</code> type.
 *
 * @see LongTableFNTStrategy
 * @see SixStepFNTStrategy
 * @see TwoPassFNTStrategy
 * @see Factor3NTTStrategy
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class LongNTTBuilder
    extends AbstractNTTBuilder
{
    /**
     * Default constructor.
     */

    public LongNTTBuilder()
    {
    }

    public NTTStepStrategy createNTTSteps()
    {
        return new LongNTTStepStrategy();
    }

    public NTTConvolutionStepStrategy createNTTConvolutionSteps()
    {
        return new LongNTTConvolutionStepStrategy();
    }

    public Factor3NTTStepStrategy createFactor3NTTSteps()
    {
        return new LongFactor3NTTStepStrategy();
    }

    protected NTTStrategy createSimpleFNTStrategy()
    {
        return new LongTableFNTStrategy();
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
