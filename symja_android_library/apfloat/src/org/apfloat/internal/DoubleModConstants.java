package org.apfloat.internal;

/**
 * Constants needed for various modular arithmetic operations for the <code>double</code> type.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface DoubleModConstants
{
    /**
     * Moduli to be used in number theoretic transforms.
     * Allows transform lengths upto 3*2<sup>43</sup>.
     */

    public static final double MODULUS[] = { 1952732650930177.0, 1899956092796929.0, 1636073302130689.0 };

    /**
     * Primitive roots for the corresponding moduli.
     */

    public static final double PRIMITIVE_ROOT[] = { 5.0, 7.0, 17.0 };

    /**
     * Maximum transform length for the moduli.
     */

    public static final long MAX_TRANSFORM_LENGTH = 26388279066624L;

    /**
     * Maximum bits in a power-of-two base that fits in a <code>double</code>.
     */

    public static final int MAX_POWER_OF_TWO_BITS = 51;

    /**
     * Maximum power-of-two base that fits in a <code>double</code>.
     */

    public static final double MAX_POWER_OF_TWO_BASE = (double) (1L << MAX_POWER_OF_TWO_BITS);
}
