package org.apfloat.internal;

/**
 * Elementary modulo arithmetic functions for <code>float</code> data.
 * Note that although a floating-point data type is used, the data
 * will always be integers.<p>
 *
 * Since the moduli are close to 2<sup>24</sup> some attention must be paid
 * to avoiding overflow in modular addition and subtraction. This can be
 * done easily e.g. by casting the operands to <code>double</code>. Note
 * that an IEEE float has a mantissa with a precision of 24 bits (1 + 23).<p>
 *
 * Modular multiplication is more complicated, and since it is usually
 * the single most time consuming operation in the whole program execution,
 * the very core of the Number Theoretic Transform (NTT), it should be
 * carefully optimized.<p>
 *
 * Some obvious (but not very efficient) algorithms for multiplying two
 * <code>float</code>s and taking the remainder would be to call
 * <code>Math.IEEEremainder()</code>, or cast the operands to
 * <code>long</code>, e.g.<p>
 *
 * <code>(float) ((long) a * (long) b % (long) modulus)</code><p>
 *
 * Since the modulus is practically constant, it should be more efficient
 * to calculate (once) the inverse of the modulus, and then subsequently
 * multiply by the inverse modulus instead of dividing by the modulus.<p>
 *
 * The algorithm used in this implementation casts the operands to
 * <code>double</code>, performs the multiplication, multiplies by the
 * inverse modulus, then takes the integer part. Getting the integer
 * part is typically a lot faster by casting to <code>int</code> compared
 * to e.g. calling <code>Math.floor()</code>. An <code>int</code>, holding
 * 32 bits, can easily contain the result of the cast, which will have a
 * maximum of 24 bits.<p>
 *
 * Overflow is not a problem, since a <code>double</code> can hold 53 bits
 * precisely in the mantissa &#150; more than doubly what a <code>float</code>
 * can. Note that multiplying by the inverse modulus is also trivial, when
 * the inverse modulus has more than twice accurate bits than what are in
 * each of the multiplicands. Since the modulus is assumed to be prime, there
 * can be no situations where multiplication by the inverse modulus would
 * have a near-integer result that would be rounded incorrectly, e.g. as in
 * <code>0.333... * 3 = 0.999...</code>.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public class FloatElementaryModMath
{
    /**
     * Default constructor.
     */

    public FloatElementaryModMath()
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

    public final float modMultiply(float a, float b)
    {
        double r = (double) a * (double) b;

        return (float) (r - (double) this.modulus * (double) (int) (this.inverseModulus * r));
    }

    /**
     * Modular addition.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a + b) % modulus</code>
     */

    public final float modAdd(float a, float b)
    {
        double r = (double) a + (double) b;

        return (float) (r >= (double) this.modulus ? r - (double) this.modulus : r);
    }

    /**
     * Modular subtraction. The result is always >= 0.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a - b + modulus) % modulus</code>
     */

    public final float modSubtract(float a, float b)
    {
        float r = a - b;

        return (r < 0.0f ? r + this.modulus : r);
    }

    /**
     * Get the modulus.
     *
     * @return The modulus.
     */

    public final float getModulus()
    {
        return this.modulus;
    }

    /**
     * Set the modulus.
     *
     * @param modulus The modulus.
     */

    public final void setModulus(float modulus)
    {
        this.inverseModulus = 1.0 / (double) modulus;
        this.modulus = modulus;
    }

    private float modulus;
    private double inverseModulus;
}
