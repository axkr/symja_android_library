package org.apfloat.internal;

import static org.apfloat.internal.IntModConstants.*;
import static org.apfloat.internal.IntRadixConstants.*;

/**
 * Basic arithmetic for calculating the Chinese Remainder
 * Theorem. Works for the <code>int</code> type.
 *
 * @version 1.6
 * @author Mikko Tommila
 */

public class IntCRTMath
    extends IntBaseMath
{
    /**
     * Creates a carry-CRT math using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public IntCRTMath(int radix)
    {
        super(radix);
        this.base = BASE[radix];
    }

    /**
     * Multiplies two words by one word to produce a result of three words.
     * Most significant word is stored first.
     *
     * @param src Source array, first multiplicand.
     * @param factor Second multiplicand.
     * @param dst Destination array.
     */

    public final void multiply(int[] src, int factor, int[] dst)
    {
        long tmp = (long) src[1] * (long) factor;
        int carry = (int) (tmp >>> MAX_POWER_OF_TWO_BITS);

        dst[2] = (int) tmp & BASE_MASK;     // = tmp % MAX_POWER_OF_TWO_BASE

        tmp = (long) src[0] * (long) factor + carry;
        carry = (int) (tmp >>> MAX_POWER_OF_TWO_BITS);

        dst[1] = (int) tmp & BASE_MASK;     // = tmp % MAX_POWER_OF_TWO_BASE

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

    public final int compare(int[] src1, int[] src2)
    {
        int result = src1[0] - src2[0];

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

    public final int add(int[] src, int[] srcDst)
    {
        int result = srcDst[2] + src[2],
            carry = (result < 0 ? 1 : 0);
        result = (result < 0 ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[2] = result;

        result = srcDst[1] + src[1] + carry;
        carry = (result < 0 ? 1 : 0);
        result = (result < 0 ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[1] = result;

        result = srcDst[0] + src[0] + carry;
        carry = (result < 0 ? 1 : 0);
        result = (result < 0 ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[0] = result;

        return carry;
    }

    /**
     * Subtracts three words. Most significant word is stored first.
     *
     * @param src First operand.
     * @param srcDst Second operand, and destination of the operation.
     */

    public final void subtract(int[] src, int[] srcDst)
    {
        int result = srcDst[2] - src[2],
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

    public final int divide(int[] srcDst)
    {
        long tmp = ((long) srcDst[0] << MAX_POWER_OF_TWO_BITS) + srcDst[1];
        int result = (int) (tmp / this.base),
            carry = (int) tmp - result * this.base;     // = tmp % this.base

        srcDst[0] = 0;
        srcDst[1] = result;

        tmp = ((long) carry << MAX_POWER_OF_TWO_BITS) + srcDst[2];
        result = (int) (tmp / this.base);
        carry = (int) tmp - result * this.base;         // = tmp % this.base

        srcDst[2] = result;

        return carry;
    }

    private static final long serialVersionUID = 6698972116690441263L;

    private static final int BASE_MASK = (1 << MAX_POWER_OF_TWO_BITS) - 1;

    private int base;
}
