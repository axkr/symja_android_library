package org.apfloat.internal;

import static org.apfloat.internal.DoubleModConstants.*;
import static org.apfloat.internal.DoubleRadixConstants.*;

/**
 * Basic arithmetic for calculating the Chinese Remainder
 * Theorem. Works for the <code>double</code> type.
 *
 * @version 1.6
 * @author Mikko Tommila
 */

public class DoubleCRTMath
    extends DoubleBaseMath
{
    /**
     * Creates a carry-CRT math using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public DoubleCRTMath(int radix)
    {
        super(radix);
        this.base = (long) BASE[radix];
        this.inverseBase = 1.0 / BASE[radix];
    }

    /**
     * Multiplies two words by one word to produce a result of three words.
     * Most significant word is stored first.
     *
     * @param src Source array, first multiplicand.
     * @param factor Second multiplicand.
     * @param dst Destination array.
     */

    public final void multiply(double[] src, double factor, double[] dst)
    {
        long tmp = (long) src[1] * (long) factor,
             carry = (long) ((src[1] * factor + (double) (tmp & 0x8000000000000000L)) * INVERSE_2_64);
        carry = (carry << 64 - MAX_POWER_OF_TWO_BITS) | (tmp >>> MAX_POWER_OF_TWO_BITS);

        dst[2] = (double) (tmp & BASE_MASK);    // = tmp % MAX_POWER_OF_TWO_BASE

        tmp = (long) src[0] * (long) factor + carry;
        carry = (long) ((src[0] * factor + (double) carry + (double) (tmp & 0x8000000000000000L)) * INVERSE_2_64);
        carry = (carry << 64 - MAX_POWER_OF_TWO_BITS) | (tmp >>> MAX_POWER_OF_TWO_BITS);

        dst[1] = (double) (tmp & BASE_MASK);    // = tmp % MAX_POWER_OF_TWO_BASE

        dst[0] = (double) carry;
    }

    /**
     * Compares three words. Most significant word is stored first.
     *
     * @param src1 First operand.
     * @param src2 Second operand.
     *
     * @return Less than zero if <code>src1 < src2</code>, greater than zero if <code>src1 > src2</code> and zero if <code>src1 == src2</code>.
     */

    public final double compare(double[] src1, double[] src2)
    {
        double result = src1[0] - src2[0];

        if (result != 0)
        {
            return result;
        }

        result = src1[1] - src2[1];

        if (result != 0)
        {
            return result;
        }

        return src1[2] - src2[2];
    }

    /**
     * Adds three words. Most significant word is stored first.
     *
     * @param src First operand.
     * @param srcDst Second operand, and destination of the operation.
     *
     * @return Overflow carry bit.
     */

    public final double add(double[] src, double[] srcDst)
    {
        double result = srcDst[2] + src[2],
               carry = (result >= MAX_POWER_OF_TWO_BASE ? 1 : 0);
        result = (result >= MAX_POWER_OF_TWO_BASE ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[2] = result;

        result = srcDst[1] + src[1] + carry;
        carry = (result >= MAX_POWER_OF_TWO_BASE ? 1 : 0);
        result = (result >= MAX_POWER_OF_TWO_BASE ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[1] = result;

        result = srcDst[0] + src[0] + carry;
        carry = (result >= MAX_POWER_OF_TWO_BASE ? 1 : 0);
        result = (result >= MAX_POWER_OF_TWO_BASE ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[0] = result;

        return carry;
    }

    /**
     * Subtracts three words. Most significant word is stored first.
     *
     * @param src First operand.
     * @param srcDst Second operand, and destination of the operation.
     */

    public final void subtract(double[] src, double[] srcDst)
    {
        double result = srcDst[2] - src[2],
               carry = (result < 0 ? 1 : 0);
        result = (result < 0 ? result + MAX_POWER_OF_TWO_BASE : result);

        srcDst[2] = result;

        result = srcDst[1] - src[1] - carry;
        carry = (result < 0 ? 1 : 0);
        result = (result < 0 ? result + MAX_POWER_OF_TWO_BASE : result);

        srcDst[1] = result;

        result = srcDst[0] - src[0] - carry;
        // carry = (result < 0 ? 1 : 0);
        result = (result < 0 ? result + MAX_POWER_OF_TWO_BASE : result);

        srcDst[0] = result;
    }

    /**
     * Divides three words by the base to produce two words. Most significant word is stored first.
     *
     * @param srcDst Source and destination of the operation.
     *
     * @return Remainder of the division.
     */

    public final double divide(double[] srcDst)
    {
        long tmp = ((long) srcDst[0] << MAX_POWER_OF_TWO_BITS) + (long) srcDst[1],
             result = (long) ((srcDst[0] * MAX_POWER_OF_TWO_BASE + srcDst[1]) * this.inverseBase),
             carry = tmp - result * this.base;          // = tmp % divisor

        if (carry >= this.base)
        {
            carry -= this.base;
            result++;
        }
        if (carry < 0)
        {
            carry += this.base;
            result--;
        }

        srcDst[0] = 0;
        srcDst[1] = (double) result;

        tmp = (carry << MAX_POWER_OF_TWO_BITS) + (long) srcDst[2];
        result = (long) (((double) carry * MAX_POWER_OF_TWO_BASE + srcDst[2]) * this.inverseBase);
        carry = tmp - result * this.base;               // = tmp % divisor

        if (carry >= this.base)
        {
            carry -= this.base;
            result++;
        }
        if (carry < 0)
        {
            carry += this.base;
            result--;
        }

        srcDst[2] = (double) result;

        return (double) carry;
    }

    private static final long serialVersionUID = -8414531999881223922L;

    private static final long BASE_MASK = (1L << MAX_POWER_OF_TWO_BITS) - 1;
    private static final double INVERSE_2_64 = 1.0 / 18446744073709551616.0;

    private long base;
    private double inverseBase;
}
