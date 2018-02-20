package org.apfloat.internal;

/**
 * Constants needed for various modular arithmetic operations for the <code>long</code> type.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface LongModConstants
{
    /**
     * Moduli to be used in number theoretic transforms.
     * Allows transform lengths upto 3*2<sup>47</sup>.
     */

    public static final long MODULUS[] = { 136796838681378817L, 127508164449927169L, 119063915148607489L };

    /**
     * Primitive roots for the corresponding moduli.
     */

    public static final long PRIMITIVE_ROOT[] = { 5, 14, 26 };

    /**
     * Maximum transform length for the moduli.
     */

    public static final long MAX_TRANSFORM_LENGTH = 422212465065984L;

    /**
     * Maximum bits in a power-of-two base that fits in a <code>long</code>.
     */

    public static final int MAX_POWER_OF_TWO_BITS = 57;

    /**
     * Maximum power-of-two base that fits in a <code>long</code>.
     */

    public static final long MAX_POWER_OF_TWO_BASE = 1L << MAX_POWER_OF_TWO_BITS;
}
