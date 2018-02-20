package org.apfloat.internal;

import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;
import static org.apfloat.internal.DoubleConstants.*;

/**
 * Creates convolutions of suitable type for the <code>double</code> type.<p>
 *
 * @see DoubleShortConvolutionStrategy
 * @see DoubleMediumConvolutionStrategy
 * @see DoubleKaratsubaConvolutionStrategy
 * @see ThreeNTTConvolutionStrategy
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class DoubleConvolutionBuilder
    extends AbstractConvolutionBuilder
{
    /**
     * Default constructor.
     */

    public DoubleConvolutionBuilder()
    {
    }

    protected int getKaratsubaCutoffPoint()
    {
        return DoubleKaratsubaConvolutionStrategy.CUTOFF_POINT;
    }

    protected float getKaratsubaCostFactor()
    {
        return KARATSUBA_COST_FACTOR;
    }

    protected float getNTTCostFactor()
    {
        return NTT_COST_FACTOR;
    }

    protected ConvolutionStrategy createShortConvolutionStrategy(int radix)
    {
        return new DoubleShortConvolutionStrategy(radix);
    }

    protected ConvolutionStrategy createMediumConvolutionStrategy(int radix)
    {
        return new DoubleMediumConvolutionStrategy(radix);
    }

    protected ConvolutionStrategy createKaratsubaConvolutionStrategy(int radix)
    {
        return new DoubleKaratsubaConvolutionStrategy(radix);
    }


    protected ConvolutionStrategy createThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy)
    {
        return new ParallelThreeNTTConvolutionStrategy(radix, nttStrategy);
    }
}
