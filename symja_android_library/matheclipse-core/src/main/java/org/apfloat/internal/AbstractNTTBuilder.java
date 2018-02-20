package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.NTTBuilder;
import org.apfloat.spi.NTTStrategy;
import org.apfloat.spi.Util;

/**
 * Abstract base class for creating Number Theoretic Transforms suitable for the
 * specified length, based on available memory configured in the {@link ApfloatContext}.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public abstract class AbstractNTTBuilder
    implements NTTBuilder
{
    /**
     * Subclass constructor.
     */

    protected AbstractNTTBuilder()
    {
    }

    public NTTStrategy createNTT(long size)
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        BuilderFactory builderFactory = ctx.getBuilderFactory();
        int cacheSize = ctx.getCacheL1Size() / builderFactory.getElementSize();
        long maxMemoryBlockSize = ctx.getMaxMemoryBlockSize() / builderFactory.getElementSize();

        NTTStrategy nttStrategy;
        boolean useFactor3 = false;

        size = Util.round23up(size);        // Round up to the nearest power of two or three times a power of two
        long power2size = (size & -size);   // Power-of-two factor of the above
        if (size != power2size)
        {
            // A factor of three will be used, so the power-of-two part is one third of the whole transform length
            useFactor3 = true;
        }

        // Select transform for the power-of-two part
        if (power2size <= cacheSize / 2)
        {
            // The whole transform plus w-table fits into the cache, so use the simplest approach
            nttStrategy = createSimpleFNTStrategy();
        }
        else if (power2size <= maxMemoryBlockSize && power2size <= Integer.MAX_VALUE)
        {
            // The whole transform fits into the available main memory, so use a six-step in-memory approach
            nttStrategy = createSixStepFNTStrategy();
        }
        else
        {
            // The whole transform won't fit into available memory, so use a two-pass disk based approach
            nttStrategy = createTwoPassFNTStrategy();
        }

        if (useFactor3)
        {
            // Allow using a factor of three in any of the above selected transforms
            nttStrategy = createFactor3NTTStrategy(nttStrategy);
        }

        return nttStrategy;
    }

    /**
     * Create a simple NTT strategy.
     *
     * @return A new simple NTT strategy.
     */

    protected abstract NTTStrategy createSimpleFNTStrategy();

    /**
     * Create a six-step NTT strategy.
     *
     * @return A new six-step NTT strategy.
     */

    protected abstract NTTStrategy createSixStepFNTStrategy();

    /**
     * Create a two-pass NTT strategy.
     *
     * @return A new two-pass NTT strategy.
     */

    protected abstract NTTStrategy createTwoPassFNTStrategy();

    /**
     * Create a factor-3 NTT strategy on top of another NTT strategy.
     *
     * @param nttStrategy The underlying factor-2 NTT strategy.
     *
     * @return A new factor-3 NTT strategy.
     */

    protected abstract NTTStrategy createFactor3NTTStrategy(NTTStrategy nttStrategy);
}
