package org.apfloat.internal;

/**
 * Elementary modulo arithmetic functions for <code>long</code> data.<p>
 *
 * Modular addition and subtraction are trivial, when the modulus is less
 * than 2<sup>63</sup> and overflow can be detected easily.<p>
 *
 * Modular multiplication is more complicated, and since it is usually
 * the single most time consuming operation in the whole program execution,
 * the very core of the Number Theoretic Transform (NTT), it should be
 * carefully optimized.<p>
 *
 * The algorithm for multiplying two <code>long</code>s and taking the
 * remainder is not entirely obvious. The basic problem is to get the
 * full 128-bit result of multiplying two 64-bit integers. It would be
 * possible to do this by splitting the arguments to high and low 32-bit
 * words and performing four multiplications. The performance of this
 * solution would be not very good.<p>
 *
 * Another approach is to use <code>long</code>s only for getting the lowest
 * 64 bits of the result. Casting the operands to <code>double</code> and
 * multiplying as floating-point numbers, we can get the highest (roughly) 52
 * bits of the result. However since only 116 bits can be acquired this
 * way, it would be possible to only use 58 bits in each of the multiplication
 * operands (not the full 64 or 63 bits). Furthermore, round-off errors in
 * the floating-point multiplications, as allowed by the IEEE specification,
 * actually prevent getting even 52 of the top bits accurately, and actually
 * only 57 bits can be used in the multiplication operands. This is the
 * approach chosen in this implementation.<p>
 *
 * The first observation is that since the modulus is practically
 * constant, it should be more efficient to calculate (once) the inverse
 * of the modulus, and then subsequently multiply by the inverse modulus
 * instead of dividing by the modulus.<p>
 *
 * The second observation is that to get the remainder of the division,
 * we don't necessarily need the actual result of the division (we just
 * want the remainder). So, we should discard the topmost 50 bits of the
 * full 114-bit result whenever possible, to save a few operations.<p>
 *
 * The basic approach is to get an approximation of <code>a * b / modulus</code>
 * (using floating-point operands, that is <code>double</code>s). The approximation
 * should be within +1 or -1 of the correct result. We first calculate
 * <code>a * b - approximateDivision * modulus</code> to get the initial remainder.
 * This calculation can use the lowest 64 bits only and is done using <code>long</code>s.
 * It is enough to use a <code>double</code> to do the approximate division, as it eliminates
 * at least 51 bits from the top of the 114-bit multiplication result, leaving at
 * most 63 bits in the remainder. The calculation <code>result - approximateDivision * modulus</code>
 * must then be done once more to reduce the remainder since the original multiplication operands
 * are only 57-bit numbers. The second reduction reduces the results to the correct value &#177;modulus.
 * It is then easy to detect the case when the approximate division was off by one (and the
 * remainder is <code>&#177;modulus</code> off) as the final step of the algorithm.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public class LongElementaryModMath
{
    /**
     * Default constructor.
     */

    public LongElementaryModMath()
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

    public final long modMultiply(long a, long b)
    {
        long r = a * b - this.modulus * (long) ((double) a * (double) b * this.inverseModulus);
        r -= this.modulus * (int) ((double) r * this.inverseModulus);

        r = (r >= this.modulus ? r - this.modulus : r);
        r = (r < 0 ? r + this.modulus : r);

        return r;
    }

    /**
     * Modular addition.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a + b) % modulus</code>
     */

    public final long modAdd(long a, long b)
    {
        long r = a + b;

        return (r >= this.modulus ? r - this.modulus : r);
    }

    /**
     * Modular subtraction. The result is always >= 0.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a - b + modulus) % modulus</code>
     */

    public final long modSubtract(long a, long b)
    {
        long r = a - b;

        return (r < 0 ? r + this.modulus : r);
    }

    /**
     * Get the modulus.
     *
     * @return The modulus.
     */

    public final long getModulus()
    {
        return this.modulus;
    }

    /**
     * Set the modulus.
     *
     * @param modulus The modulus.
     */

    public final void setModulus(long modulus)
    {
        this.inverseModulus = 1.0 / modulus;
        this.modulus = modulus;
    }

    private long modulus;
    private double inverseModulus;
}
