package org.apfloat.internal;

/**
 * Constants needed for various algorithms for the <code>float</code> type.
 *
 * @since 1.4
 * @version 1.4
 * @author Mikko Tommila
 */

public interface FloatConstants
{
    /**
     * Relative cost of Karatsuba multiplication.
     */

    public static final float KARATSUBA_COST_FACTOR = 6.1f;

    /**
     * Relative cost of NTT multiplication.
     */

    public static final float NTT_COST_FACTOR = 7.4f;
}
