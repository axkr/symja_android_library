package org.apfloat.internal;

/**
 * Constants needed for various algorithms for the <code>double</code> type.
 *
 * @since 1.4
 * @version 1.4
 * @author Mikko Tommila
 */

public interface DoubleConstants
{
    /**
     * Relative cost of Karatsuba multiplication.
     */

    public static final float KARATSUBA_COST_FACTOR = 4.3f;

    /**
     * Relative cost of NTT multiplication.
     */

    public static final float NTT_COST_FACTOR = 6.2f;
}
