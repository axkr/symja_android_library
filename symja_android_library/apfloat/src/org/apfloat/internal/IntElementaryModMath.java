package org.apfloat.internal;

/**
 * Elementary modulo arithmetic functions for <code>int</code> data.<p>
 *
 * Modular addition and subtraction are trivial, when the modulus is less
 * than 2<sup>31</sup> and overflow can be detected easily.<p>
 *
 * Modular multiplication is more complicated, and since it is usually
 * the single most time consuming operation in the whole program execution,
 * the very core of the Number Theoretic Transform (NTT), it should be
 * carefully optimized.<p>
 *
 * The obvious (but not very efficient) algorithm for multiplying two
 * <code>int</code>s and taking the remainder is<p>
 *
 * <code>(int) ((long) a * b % modulus)</code><p>
 *
 * The first observation is that since the modulus is practically
 * constant, it should be more efficient to calculate (once) the inverse
 * of the modulus, and then subsequently multiply by the inverse modulus
 * instead of dividing by the modulus.<p>
 *
 * The second observation is that to get the remainder of the division,
 * we don't necessarily need the actual result of the division (we just
 * want the remainder). So, we should discard the topmost 32 bits of the
 * full 64-bit result whenever possible, to save a few operations.<p>
 *
 * The basic approach is to get some approximation of <code>a * b / modulus</code>.
 * The approximation should be within +1 or -1 of the correct result. Then
 * calculate <code>a * b - approximateDivision * modulus</code> to get
 * the remainder. This calculation needs to use only the lowest 32 bits. As
 * the modulus is less than 2<sup>31</sup> it is easy to detect the case
 * when the approximate division was off by one (and the remainder is
 * <code>&#177;modulus</code> off).<p>
 *
 * There are different algorithms to calculate the approximate division
 * <code>a * b / modulus</code>. This implementation simply converts all
 * the operands to <code>double</code> and performs the mulciplications.
 * This requires that converting between integer types and floating point
 * types is efficient. On some platforms this may not be true; in that
 * case it can be more efficient to perform the multiplications using
 * 64-bit integer arithmetic.<p>
 *
 * To simplify the operations, we calculate the inverse modulus as
 * <code>1.0 / (modulus + 0.5)</code>. Since the modulus is assumed to be
 * prime, and a <code>double</code> has more bits for precision than an
 * <code>int</code>, the approximate result of <code>a * b / modulus</code>
 * will always be either correct or one too small (but never one too big).
 *
 * @version 1.0.2
 * @author Mikko Tommila
 */

public class IntElementaryModMath
{
    /**
     * Default constructor.
     */

    public IntElementaryModMath()
    {
    }

    /**
     * Modular multiplication.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>a * b % modulus</code>
     */

    public final int modMultiply(int a, int b)
    {
        int r1 = a * b - (int) (this.inverseModulus * (double) a * (double) b) * this.modulus,
            r2 = r1 - this.modulus;

        return (r2 < 0 ? r1 : r2);
    }

    /**
     * Modular addition.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a + b) % modulus</code>
     */

    public final int modAdd(int a, int b)
    {
        int r1 = a + b,
            r2 = r1 - this.modulus;

        return (r2 < 0 ? r1 : r2);
    }

    /**
     * Modular subtraction. The result is always >= 0.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a - b + modulus) % modulus</code>
     */

    public final int modSubtract(int a, int b)
    {
        int r1 = a - b,
            r2 = r1 + this.modulus;

        return (r1 < 0 ? r2 : r1);
    }

    /**
     * Get the modulus.
     *
     * @return The modulus.
     */

    public final int getModulus()
    {
        return this.modulus;
    }

    /**
     * Set the modulus.
     *
     * @param modulus The modulus.
     */

    public final void setModulus(int modulus)
    {
        this.inverseModulus = 1.0 / (modulus + 0.5);    // Round down
        this.modulus = modulus;
    }

    private int modulus;
    private double inverseModulus;
}
