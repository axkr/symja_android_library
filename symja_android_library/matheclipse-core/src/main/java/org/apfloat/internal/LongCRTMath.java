package org.apfloat.internal;

import static org.apfloat.internal.LongModConstants.*;
import static org.apfloat.internal.LongRadixConstants.*;

/**
 * Basic arithmetic for calculating the Chinese Remainder
 * Theorem. Works for the <code>long</code> type.
 *
 * @version 1.6
 * @author Mikko Tommila
 */

public class LongCRTMath
    extends LongBaseMath
{
    /**
     * Creates a carry-CRT math using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public LongCRTMath(int radix)
    {
        super(radix);
        this.base = BASE[radix];
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

    public final void multiply(long[] src, long factor, long[] dst)
    {
        long tmp = src[1] * factor,
             carry = (long) ((double) src[1] * (double) factor * INVERSE_MAX_POWER_OF_TWO_BASE);
        carry += tmp - (carry << MAX_POWER_OF_TWO_BITS) >> MAX_POWER_OF_TWO_BITS;

        dst[2] = tmp & BASE_MASK;               // = tmp % MAX_POWER_OF_TWO_BASE

        tmp = src[0] * factor + carry;
        carry = (long) (((double) src[0] * (double) factor + (double) carry) * INVERSE_MAX_POWER_OF_TWO_BASE);
        carry += tmp - (carry << MAX_POWER_OF_TWO_BITS) >> MAX_POWER_OF_TWO_BITS;

        dst[1] = tmp & BASE_MASK;               // = tmp % MAX_POWER_OF_TWO_BASE

        dst[0] = carry;
    }

    /**
     * Compares three words. Most significant word is stored first.
     *
     * @param src1 First operand.
     * @param src2 Second operand.
     *
     * @return Less than zero if <code>src1 < src2</code>, greater than zero if <code>src1 > src2</code> and zero if <code>src1 == src2</code>.
     */

    public final long compare(long[] src1, long[] src2)
    {
        long result = src1[0] - src2[0];

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

    public final long add(long[] src, long[] srcDst)
    {
        long result = srcDst[2] + src[2],
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

    public final void subtract(long[] src, long[] srcDst)
    {
        long result = srcDst[2] - src[2],
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

    public final long divide(long[] srcDst)
    {
        long tmp = (srcDst[0] << MAX_POWER_OF_TWO_BITS) + srcDst[1],
             result = (long) (((double) srcDst[0] * (double) MAX_POWER_OF_TWO_BASE + (double) srcDst[1]) * this.inverseBase),
             carry = tmp - result * this.base;          // = tmp % divisor
        int tmp2 = (int) ((double) carry * this.inverseBase);
        result += tmp2;
        carry -= tmp2 * this.base;

        if (carry >= this.base)
        {
            carry -= this.base;
            result++;
        }
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
        if (carry < 0)
        {
            carry += this.base;
            result--;
        }

        srcDst[0] = 0;
        srcDst[1] = result;

        tmp = (carry << MAX_POWER_OF_TWO_BITS) + srcDst[2];
        result = (long) (((double) carry * (double) MAX_POWER_OF_TWO_BASE + (double) srcDst[2]) * this.inverseBase);
        carry = tmp - result * this.base;               // = tmp % divisor
        tmp2 = (int) ((double) carry * this.inverseBase);
        result += tmp2;
        carry -= tmp2 * this.base;

        if (carry >= this.base)
        {
            carry -= this.base;
            result++;
        }
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
        if (carry < 0)
        {
            carry += this.base;
            result--;
        }

        srcDst[2] = result;

        return carry;
    }

    private static final long serialVersionUID = 7400961005627736773L;

    private static final long BASE_MASK = (1L << MAX_POWER_OF_TWO_BITS) - 1;
    private static final double INVERSE_MAX_POWER_OF_TWO_BASE = 1.0 / MAX_POWER_OF_TWO_BASE;

    private long base;
    private double inverseBase;
}
