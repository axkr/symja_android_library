package org.apfloat.internal;

/**
 * Constants needed for various algorithms for the <code>int</code> type.
 *
 * @since 1.4
 * @version 1.4
 * @author Mikko Tommila
 */

public interface IntConstants
{
    /**
     * Relative cost of Karatsuba multiplication.
     */

    public static final float KARATSUBA_COST_FACTOR = 4.8f;

    /**
     * Relative cost of NTT multiplication.
     */

    public static final float NTT_COST_FACTOR = 4.1f;
}
