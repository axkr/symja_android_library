package org.apfloat.internal;

/**
 * Elementary modulo arithmetic functions for <code>double</code> data.
 * Note that although a floating-point data type is used, the data
 * will always be integers.<p>
 *
 * Modular addition and subtraction are trivial, when the modulus is less
 * than 2<sup>52</sup> and overflow can be detected easily.<p>
 *
 * Modular multiplication is more complicated, and since it is usually
 * the single most time consuming operation in the whole program execution,
 * the very core of the Number Theoretic Transform (NTT), it should be
 * carefully optimized.<p>
 *
 * The algorithm for multiplying two <code>double</code>s containing an
 * integer and taking the remainder is not entirely obvious. The basic problem
 * is to get the full 104-bit result of multiplying two 52-bit integers.
 * This can basically be done in two parts: by multiplying two
 * <code>long</code>s, the lowest 64 bits can be acquired easily. Multiplying
 * the <code>double</code>s as floating-point numbers and scaling properly, the
 * highest (roughly) 52 bits of the result can be acquired.<p>
 *
 * The first observation is that since the modulus is practically
 * constant, it should be more efficient to calculate (once) the inverse
 * of the modulus, and then subsequently multiply by the inverse of the
 * modulus instead of dividing by it.<p>
 *
 * The second observation is that to get the remainder of the division,
 * we don't necessarily need the actual result of the division (we just
 * want the remainder). So, we should discard the topmost 52 bits of the
 * full 104-bit result whenever possible, to save a few operations.<p>
 *
 * The basic approach is to get an approximation of <code>a * b / modulus</code>
 * (using floating-point operands, that is <code>double</code>s). The approximation
 * should be within +1 or -1 of the correct result. Then calculate
 * <code>a * b - approximateDivision * modulus</code> to get the remainder.
 * This calculation must use the lowest 52 (or more, actually 64) bits
 * and is done using <code>long</code>s. As the modulus is less than 2<sup>52</sup>
 * it is easy to detect the case when the approximate division was off by one (and
 * the remainder is <code>&#177;modulus</code> off).<p>
 *
 * To ensure that only one comparison is needed in the check for the approximate
 * division, we use <code>1 / (modulus + 0.5)</code> as the inverse modulus. In
 * this case the result of the approximate division is always either correct or
 * 1 less.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public class DoubleElementaryModMath
{
    /**
     * Default constructor.
     */

    public DoubleElementaryModMath()
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

    public final double modMultiply(double a, double b)
    {
        double r = (double) ((long) a * (long) b - this.longModulus * (long) (a * b * this.inverseModulus));

        return (r >= this.modulus ? r - this.modulus : r);
    }

    /**
     * Modular addition.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a + b) % modulus</code>
     */

    public final double modAdd(double a, double b)
    {
        double r = a + b;

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

    public final double modSubtract(double a, double b)
    {
        double r = a - b;

        return (r < 0.0 ? r + this.modulus : r);
    }

    /**
     * Get the modulus.
     *
     * @return The modulus.
     */

    public final double getModulus()
    {
        return this.modulus;
    }

    /**
     * Set the modulus.
     *
     * @param modulus The modulus.
     */

    public final void setModulus(double modulus)
    {
        this.inverseModulus = 1.0 / (modulus + 0.5);    // Round down
        this.longModulus = (long) modulus;
        this.modulus = modulus;
    }

    private long longModulus;
    private double modulus;
    private double inverseModulus;
}
