package org.apfloat.internal;

import static org.apfloat.internal.FloatModConstants.*;
import static org.apfloat.internal.FloatRadixConstants.*;

/**
 * Basic arithmetic for calculating the Chinese Remainder
 * Theorem. Works for the <code>float</code> type.
 *
 * @version 1.6
 * @author Mikko Tommila
 */

public class FloatCRTMath
    extends FloatBaseMath
{
    /**
     * Creates a carry-CRT math using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public FloatCRTMath(int radix)
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

    public final void multiply(float[] src, float factor, float[] dst)
    {
        double tmp = (double) src[1] * (double) factor;
        float carry = (float) (int) (tmp * INVERSE_MAX_POWER_OF_TWO_BASE);

        dst[2] = (float) (tmp - carry * MAX_POWER_OF_TWO_BASE);     // = tmp % MAX_POWER_OF_TWO_BASE

        tmp = (double) src[0] * (double) factor + carry;
        carry = (float) (int) (tmp * INVERSE_MAX_POWER_OF_TWO_BASE);

        dst[1] = (float) (tmp - carry * MAX_POWER_OF_TWO_BASE);     // = tmp % MAX_POWER_OF_TWO_BASE

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

    public final float compare(float[] src1, float[] src2)
    {
        float result = src1[0] - src2[0];

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

    public final float add(float[] src, float[] srcDst)
    {
        double result = (double) srcDst[2] + (double) src[2];
        float carry = (result >= MAX_POWER_OF_TWO_BASE ? 1 : 0);
        result = (result >= MAX_POWER_OF_TWO_BASE ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[2] = (float) result;

        result = (double) srcDst[1] + (double) src[1] + carry;
        carry = (result >= MAX_POWER_OF_TWO_BASE ? 1 : 0);
        result = (result >= MAX_POWER_OF_TWO_BASE ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[1] = (float) result;

        result = (double) srcDst[0] + (double) src[0] + carry;
        carry = (result >= MAX_POWER_OF_TWO_BASE ? 1 : 0);
        result = (result >= MAX_POWER_OF_TWO_BASE ? result - MAX_POWER_OF_TWO_BASE : result);

        srcDst[0] = (float) result;

        return carry;
    }

    /**
     * Subtracts three words. Most significant word is stored first.
     *
     * @param src First operand.
     * @param srcDst Second operand, and destination of the operation.
     */

    public final void subtract(float[] src, float[] srcDst)
    {
        float result = srcDst[2] - src[2],
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

    public final float divide(float[] srcDst)
    {
        double tmp = (double) srcDst[0] * MAX_POWER_OF_TWO_BASE + srcDst[1];
        float result = (float) (int) (tmp / this.base),
              carry = (float) (tmp - result * this.base);       // = tmp % this.base

        srcDst[0] = 0;
        srcDst[1] = result;

        tmp = (double) carry * MAX_POWER_OF_TWO_BASE + srcDst[2];
        result = (float) (int) (tmp / this.base);
        carry = (float) (tmp - result * this.base);             // = tmp % this.base

        srcDst[2] = result;

        return carry;
    }

    private static final long serialVersionUID = 2778445457339436642L;

    private static final double INVERSE_MAX_POWER_OF_TWO_BASE = 1.0 / MAX_POWER_OF_TWO_BASE;

    private double base;
}
