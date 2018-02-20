package org.apfloat.internal;

/**
 * Constants needed for various algorithms for the <code>long</code> type.
 *
 * @since 1.4
 * @version 1.4
 * @author Mikko Tommila
 */

public interface LongConstants
{
    /**
     * Relative cost of Karatsuba multiplication.
     */

    public static final float KARATSUBA_COST_FACTOR = 4.9f;

    /**
     * Relative cost of NTT multiplication.
     */

    public static final float NTT_COST_FACTOR = 8.3f;
}
