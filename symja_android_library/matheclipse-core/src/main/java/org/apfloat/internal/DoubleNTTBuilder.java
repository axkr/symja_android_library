package org.apfloat.internal;

import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.NTTStepStrategy;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.NTTConvolutionStepStrategy;

/**
 * Creates Number Theoretic Transforms for the
 * <code>double</code> type.
 *
 * @see DoubleTableFNTStrategy
 * @see SixStepFNTStrategy
 * @see TwoPassFNTStrategy
 * @see Factor3NTTStrategy
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class DoubleNTTBuilder
    extends AbstractNTTBuilder
{
    /**
     * Default constructor.
     */

    public DoubleNTTBuilder()
    {
    }

    public NTTStepStrategy createNTTSteps()
    {
        return new DoubleNTTStepStrategy();
    }

    public NTTConvolutionStepStrategy createNTTConvolutionSteps()
    {
        return new DoubleNTTConvolutionStepStrategy();
    }

    public Factor3NTTStepStrategy createFactor3NTTSteps()
    {
        return new DoubleFactor3NTTStepStrategy();
    }

    protected NTTStrategy createSimpleFNTStrategy()
    {
        return new DoubleTableFNTStrategy();
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
