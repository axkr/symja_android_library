package org.apfloat.internal;

import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTStrategy;
import static org.apfloat.internal.FloatConstants.*;

/**
 * Creates convolutions of suitable type for the <code>float</code> type.<p>
 *
 * @see FloatShortConvolutionStrategy
 * @see FloatMediumConvolutionStrategy
 * @see FloatKaratsubaConvolutionStrategy
 * @see ThreeNTTConvolutionStrategy
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class FloatConvolutionBuilder
    extends AbstractConvolutionBuilder
{
    /**
     * Default constructor.
     */

    public FloatConvolutionBuilder()
    {
    }

    protected int getKaratsubaCutoffPoint()
    {
        return FloatKaratsubaConvolutionStrategy.CUTOFF_POINT;
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
        return new FloatShortConvolutionStrategy(radix);
    }

    protected ConvolutionStrategy createMediumConvolutionStrategy(int radix)
    {
        return new FloatMediumConvolutionStrategy(radix);
    }

    protected ConvolutionStrategy createKaratsubaConvolutionStrategy(int radix)
    {
        return new FloatKaratsubaConvolutionStrategy(radix);
    }


    protected ConvolutionStrategy createThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy)
    {
        return new ParallelThreeNTTConvolutionStrategy(radix, nttStrategy);
    }
}
