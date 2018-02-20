package org.apfloat.internal;

import java.io.Serializable;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;
import static org.apfloat.internal.DoubleRadixConstants.*;

/**
 * Mathematical operations on numbers in a base.
 * Implementation for the <code>double</code> type.
 *
 * @version 1.6
 * @author Mikko Tommila
 */

public class DoubleBaseMath
    implements Serializable
{
    /**
     * Creates a base math using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public DoubleBaseMath(int radix)
    {
        this.radix = radix;
        this.inverseBase = 1.0 / BASE[radix];
    }

    /**
     * Addition in some base. Adds the data words
     * of <code>src1</code> and <code>src2</code> and stores the result to
     * <code>dst</code>. <code>src2</code> may be <code>null</code>, in
     * which case it is ignored (only the carry is propagated).<p>
     *
     * Essentially calculates <code>dst[i] = src1[i] + src2[i]</code>.
     *
     * @param src1 First source data sequence. Can be <code>null</code>, in which case it's ignored.
     * @param src2 Second source data sequence. Can be <code>null</code>, in which case it's ignored.
     * @param carry Input carry bit. This is added to the first (rightmost) word in the accessed sequence.
     * @param dst Destination data sequence.
     * @param size Number of elements to process.
     *
     * @return Overflow carry bit. Propagated carry bit from the addition of the last (leftmost) word in the accessed sequence.
     */

    public double baseAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, double carry, DataStorage.Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        assert (src1 == null || src1 != src2);

        boolean sameDst = (src1 == dst || src2 == dst);
        double base = BASE[this.radix];

        for (long i = 0; i < size; i++)
        {
            double result = (src1 == null ? 0 : src1.getDouble()) + carry +
                            (src2 == null ? 0 : src2.getDouble());

            if (result >= base)
            {
                result -= base;
                carry = 1;
            }
            else
            {
                carry = 0;
            }

            dst.setDouble(result);

            if (src1 != null) src1.next();
            if (src2 != null) src2.next();
            if (!sameDst) dst.next();
        }

        return carry;
    }

    /**
     * Subtraction in some base. Subtracts the data words
     * of <code>src1</code> and <code>src2</code> and stores the result to
     * <code>dst</code>. <code>src1</code> and <code>src2</code> may be
     * <code>null</code>, in which case they are ignored (the values are assumed
     * to be zero and only the carry is propagated).<p>
     *
     * Essentially calculates <code>dst[i] = src1[i] - src2[i]</code>.
     *
     * @param src1 First source data sequence. Can be <code>null</code>, in which case the input values are assumed to be zero.
     * @param src2 Second source data sequence. Can be <code>null</code>, in which case it's ignored, or can be the same as <code>dst</code>.
     * @param carry Input carry bit. This is subtracted from the first (rightmost) word in the accessed sequence.
     * @param dst Destination data sequence.
     * @param size Number of elements to process.
     *
     * @return Overflow carry bit. Propagated carry bit from the subtraction of the last (leftmost) word in the accessed sequence. The value is <code>1</code> if the carry is set, and <code>0</code> otherwise.
     */

    public double baseSubtract(DataStorage.Iterator src1, DataStorage.Iterator src2, double carry, DataStorage.Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        assert (src1 == null || src1 != src2);
        assert (src2 != dst);

        double base = BASE[this.radix];

        for (long i = 0; i < size; i++)
        {
            double result = (src1 == null ? 0 : src1.getDouble()) - carry -
                            (src2 == null ? 0 : src2.getDouble());

            if (result < 0)
            {
                result += base;
                carry = 1;
            }
            else
            {
                carry = 0;
            }

            dst.setDouble(result);

            if (src1 != null && src1 != dst) src1.next();
            if (src2 != null) src2.next();
            dst.next();
        }

        return carry;
    }

    /**
     * Multiplication and addition in some base. Multiplies the data words
     * of <code>src1</code> by <code>src3</code> and adds the result to the
     * words in <code>src2</code>, and stores the result to <code>dst</code>.
     * <code>src2</code> may be <code>null</code>, in which case it is ignored
     * (the values are assumed to be zero).<p>
     *
     * Assumes that the result from the addition doesn't overflow the upper
     * result word (to larger than the base). This is the case e.g. when using
     * this method to perform an arbitrary precision multiplication.<p>
     *
     * Essentially calculates <code>dst[i] = src1[i] * src3 + src2[i]</code>.
     *
     * @param src1 First source data sequence.
     * @param src2 Second source data sequence. Can be <code>null</code>, in which case it's ignored, or can be the same as <code>dst</code>.
     * @param src3 Multiplicand. All elements of <code>src1</code> are multiplied by this value.
     * @param carry Input carry word. This is added to the first (rightmost) word in the accessed sequence.
     * @param dst Destination data sequence.
     * @param size Number of elements to process.
     *
     * @return Overflow carry word. Propagated carry word from the multiplication and addition of the last (leftmost) word in the accessed sequence.
     */

    public double baseMultiplyAdd(DataStorage.Iterator src1, DataStorage.Iterator src2, double src3, double carry, DataStorage.Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        assert (src1 != src2);
        assert (src1 != dst);

        double base = BASE[this.radix];

        for (long i = 0; i < size; i++)
        {
            double a = src1.getDouble(),
                   b = src3;
            carry += (src2 == null ? 0 : src2.getDouble());
            long tmp = (long) a * (long) b + (long) carry;
            carry = (double) (long) ((a * b + carry) * this.inverseBase);
            double result = (double) (tmp - (long) carry * (long) base);

            if (result >= base)
            {
                result -= base;
                carry++;
            }
            if (result < 0)
            {
                result += base;
                carry--;
            }

            dst.setDouble(result);                      // = a * b % base

            src1.next();
            if (src2 != null && src2 != dst) src2.next();
            dst.next();
        }

        return carry;
    }

    /**
     * Division in some base. Divides the data words
     * of <code>src1</code> by <code>src2</code> and stores the result to
     * <code>dst</code>. <code>src1</code> may be <code>null</code>,
     * in which case it is ignored (the values are assumed to be
     * zero and only the carry division is propagated).<p>
     *
     * Essentially calculates <code>dst[i] = src1[i] / src2</code>.
     *
     * @param src1 First source data sequence. Can be <code>null</code>, in which case the input values are assumed to be zero.
     * @param src2 Divisor. All elements of <code>src1</code> are divided by this value.
     * @param carry Input carry word. Used as the upper word for the division of the first input element. This should be the remainder word returned from the previous block processed.
     * @param dst Destination data sequence.
     * @param size Number of elements to process.
     *
     * @return Remainder word of the propagated division of the last (rightmost) word in the accessed sequence.
     */

    public double baseDivide(DataStorage.Iterator src1, double src2, double carry, DataStorage.Iterator dst, long size)
        throws ApfloatRuntimeException
    {
        assert (src1 != dst);

        double base = BASE[this.radix],
               inverseDivisor = 1.0 / src2;

        for (long i = 0; i < size; i++)
        {
            double a = (src1 == null ? 0 : src1.getDouble());
            long tmp = (long) carry * (long) base + (long) a;
            double result = (double) (long) ((carry * base + a) * inverseDivisor);
            carry = (double) (tmp - (long) result * (long) src2);

            if (carry >= src2)
            {
                carry -= src2;
                result++;
            }
            if (carry < 0)
            {
                carry += src2;
                result--;
            }

            dst.setDouble(result);                      // = carry * base % src2

            if (src1 != null) src1.next();
            dst.next();
        }

        return carry;
    }

    private static final long serialVersionUID = 4560898425815362356L;

    private int radix;
    private double inverseBase;
}
