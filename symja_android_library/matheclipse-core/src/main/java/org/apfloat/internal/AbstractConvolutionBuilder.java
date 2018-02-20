package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.spi.ConvolutionBuilder;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.NTTBuilder;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

/**
 * Abstract base class for creating convolutions of suitable type for the specified length.<p>
 *
 * Based on a work estimate, depending on the operand sizes and implementation-dependent
 * factors, the O(n<sup>2</sup>) long multiplication, Karatsuba multiplication and
 * the NTT algorithms are chosen e.g. as follows:<p>
 *
 * <table border="1">
 * <tr><th>size1</th><th>size2</th><th>Algorithm</th></tr>
 * <tr><td>16</td><td>16</td><td>Long</td></tr>
 * <tr><td>16</td><td>256</td><td>Long</td></tr>
 * <tr><td>32</td><td>32</td><td>Long</td></tr>
 * <tr><td>32</td><td>256</td><td>Long</td></tr>
 * <tr><td>64</td><td>64</td><td>Karatsuba</td></tr>
 * <tr><td>64</td><td>256</td><td>NTT</td></tr>
 * <tr><td>64</td><td>65536</td><td>Karatsuba</td></tr>
 * <tr><td>128</td><td>128</td><td>NTT</td></tr>
 * <tr><td>128</td><td>65536</td><td>NTT</td></tr>
 * <tr><td>128</td><td>4294967296</td><td>Karatsuba</td></tr>
 * <tr><td>256</td><td>256</td><td>NTT</td></tr>
 * <tr><td>256</td><td>4294967296</td><td>Karatsuba</td></tr>
 * <tr><td>512</td><td>512</td><td>NTT</td></tr>
 * <tr><td>512</td><td>4294967296</td><td>NTT</td></tr>
 * </table>
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public abstract class AbstractConvolutionBuilder
    implements ConvolutionBuilder
{
    /**
     * Subclass constructor.
     */

    protected AbstractConvolutionBuilder()
    {
    }

    public ConvolutionStrategy createConvolution(int radix, long size1, long size2, long resultSize)
    {
        long minSize = Math.min(size1, size2),
             maxSize = Math.max(size1, size2),
             totalSize = size1 + size2;

        if (minSize == 1)
        {
            return createShortConvolutionStrategy(radix);
        }
        else if (minSize <= getKaratsubaCutoffPoint())
        {
            return createMediumConvolutionStrategy(radix);
        }
        else
        {
            float mediumCost = (float) minSize * maxSize,
                  karatsubaCost = getKaratsubaCostFactor() * (float) Math.pow((double) minSize, LOG2_3) * maxSize / minSize,
                  nttCost = getNTTCostFactor() * totalSize * Util.log2down(totalSize);

            if (mediumCost <= Math.min(karatsubaCost, nttCost))
            {
                return createMediumConvolutionStrategy(radix);
            }
            else if (karatsubaCost <= nttCost)
            {
                return createKaratsubaConvolutionStrategy(radix);
            }
            else
            {
                ApfloatContext ctx = ApfloatContext.getContext();
                NTTBuilder nttBuilder = ctx.getBuilderFactory().getNTTBuilder();
                NTTStrategy nttStrategy = nttBuilder.createNTT(totalSize);

                return createThreeNTTConvolutionStrategy(radix, nttStrategy);
            }
        }
    }

    /**
     * Get the Karatsuba convolution cutoff point.
     * When either operand is shorter than this then the
     * medium-length convolution strategy should be used instead.
     *
     * @return The Karatsuba convolution cutoff point.
     *
     * @since 1.7.0
     */

    protected abstract int getKaratsubaCutoffPoint();

    /**
     * Get the Karatsuba convolution cost factor.
     * It is used in determining the most efficient
     * convolution strategy for the given data lengths.
     *
     * @return The Karatsuba convolution cost factor.
     *
     * @since 1.7.0
     */

    protected abstract float getKaratsubaCostFactor();

    /**
     * Get the NTT convolution cost factor.
     * It is used in determining the most efficient
     * convolution strategy for the given data lengths.
     *
     * @return The NTT convolution cost factor.
     *
     * @since 1.7.0
     */

    protected abstract float getNTTCostFactor();

    /**
     * Create a short-length convolution strategy where the size of either
     * data set is one.
     *
     * @param radix The radix that will be used.
     *
     * @return A new short-length convolution strategy.
     *
     * @since 1.7.0
     */

    protected abstract ConvolutionStrategy createShortConvolutionStrategy(int radix);

    /**
     * Create a medium-length convolution strategy where the size of one
     * of the data sets is relatively small (but more than one).
     *
     * @param radix The radix that will be used.
     *
     * @return A new medium-length convolution strategy.
     *
     * @since 1.7.0
     */

    protected abstract ConvolutionStrategy createMediumConvolutionStrategy(int radix);

    /**
     * Create a Karatsuba convolution strategy.
     *
     * @param radix The radix that will be used.
     *
     * @return A new Karatsuba convolution strategy.
     *
     * @since 1.7.0
     */

    protected abstract ConvolutionStrategy createKaratsubaConvolutionStrategy(int radix);

    /**
     * Create a 3-NTT convolution strategy.
     *
     * @param radix The radix that will be used.
     * @param nttStrategy The underlying NTT strategy.
     *
     * @return A new 3-NTT convolution strategy.
     *
     * @since 1.7.0
     */

    protected abstract ConvolutionStrategy createThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy);

    private static final double LOG2_3 = Math.log(3.0) / Math.log(2.0);
}
