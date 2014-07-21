package org.apfloat.internal;

import java.math.BigInteger;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.DataStorage;
import static org.apfloat.internal.IntModConstants.*;

/**
 * Class for performing the final steps of a three-modulus
 * Number Theoretic Transform based convolution. Works for the
 * <code>int</code> type.<p>
 *
 * All access to this class must be externally synchronized.
 *
 * @since 1.7.0
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class IntCarryCRTStepStrategy
    extends IntCRTMath
    implements CarryCRTStepStrategy<int[]>
{
    /**
     * Creates a carry-CRT steps object using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public IntCarryCRTStepStrategy(int radix)
    {
        super(radix);
    }

    public int[] crt(DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, DataStorage dataStorage, long size, long resultSize, long offset, long length)
        throws ApfloatRuntimeException
    {
        long skipSize = (offset == 0 ? size - resultSize + 1: 0);   // For the first block, ignore the first 1-3 elements
        long lastSize = (offset + length == size ? 1: 0);           // For the last block, add 1 element
        long nonLastSize = 1 - lastSize;                            // For the other than last blocks, move 1 element
        long subResultSize = length - skipSize + lastSize;

        long subStart = size - offset,
             subEnd = subStart - length,
             subResultStart = size - offset - length + nonLastSize + subResultSize,
             subResultEnd = subResultStart - subResultSize;

        DataStorage.Iterator src0 = resultMod0.iterator(DataStorage.READ, subStart, subEnd),
                             src1 = resultMod1.iterator(DataStorage.READ, subStart, subEnd),
                             src2 = resultMod2.iterator(DataStorage.READ, subStart, subEnd),
                             dst = dataStorage.iterator(DataStorage.WRITE, subResultStart, subResultEnd);

        int[] carryResult = new int[3],
                  sum = new int[3],
                  tmp = new int[3];

        // Preliminary carry-CRT calculation (happens in parallel in multiple blocks)
        for (long i = 0; i < length; i++)
        {
            int y0 = MATH_MOD_0.modMultiply(T0, src0.getInt()),
                    y1 = MATH_MOD_1.modMultiply(T1, src1.getInt()),
                    y2 = MATH_MOD_2.modMultiply(T2, src2.getInt());

            multiply(M12, y0, sum);
            multiply(M02, y1, tmp);

            if (add(tmp, sum) != 0 ||
                compare(sum, M012) >= 0)
            {
                subtract(M012, sum);
            }

            multiply(M01, y2, tmp);

            if (add(tmp, sum) != 0 ||
                compare(sum, M012) >= 0)
            {
                subtract(M012, sum);
            }

            add(sum, carryResult);

            int result = divide(carryResult);

            // In the first block, ignore the first element (it's zero in full precision calculations)
            // and possibly one or two more in limited precision calculations
            if (i >= skipSize)
            {
                dst.setInt(result);
                dst.next();
            }

            src0.next();
            src1.next();
            src2.next();
        }

        // Calculate the last words (in base math)
        int result0 = divide(carryResult);
        int result1 = carryResult[2];

        assert (carryResult[0] == 0);
        assert (carryResult[1] == 0);

        // Last block has one extra element (corresponding to the one skipped in the first block)
        if (subResultSize == length - skipSize + 1)
        {
            dst.setInt(result0);
            dst.close();

            result0 = result1;
            assert (result1 == 0);
        }

        int[] results = { result1, result0 };

        return results;
    }

    public int[] carry(DataStorage dataStorage, long size, long resultSize, long offset, long length, int[] results, int[] previousResults)
        throws ApfloatRuntimeException
    {
        long skipSize = (offset == 0 ? size - resultSize + 1: 0);   // For the first block, ignore the first 1-3 elements
        long lastSize = (offset + length == size ? 1: 0);           // For the last block, add 1 element
        long nonLastSize = 1 - lastSize;                            // For the other than last blocks, move 1 element
        long subResultSize = length - skipSize + lastSize;

        long subResultStart = size - offset - length + nonLastSize + subResultSize,
             subResultEnd = subResultStart - subResultSize;

        // Get iterators for the previous block carries, and dst, padded with this block's carries
        // Note that size could be 1 but carries size is 2
        DataStorage.Iterator src = arrayIterator(previousResults);
        DataStorage.Iterator dst = compositeIterator(dataStorage.iterator(DataStorage.READ_WRITE, subResultStart, subResultEnd), subResultSize, arrayIterator(results));

        // Propagate base addition through dst, and this block's carries
        int carry = baseAdd(dst, src, 0, dst, previousResults.length);
        carry = baseCarry(dst, carry, subResultSize);
        dst.close();                                                    // Iterator likely was not iterated to end

        assert (carry == 0);

        return results;
    }

    private int baseCarry(DataStorage.Iterator srcDst, int carry, long size)
        throws ApfloatRuntimeException
    {
        for (long i = 0; i < size && carry > 0; i++)
        {
            carry = baseAdd(srcDst, null, carry, srcDst, 1);
        }

        return carry;
    }

    // Wrap an array in a simple reverse-order iterator, padded with zeros
    private static DataStorage.Iterator arrayIterator(final int[] data)
    {
        return new DataStorage.Iterator()
        {
            public boolean hasNext()
            {
                return true;
            }

            public void next()
            {
                this.position--;
            }

            public int getInt()
            {
                assert (this.position >= 0);
                return data[this.position];
            }

            public void setInt(int value)
            {
                assert (this.position >= 0);
                data[this.position] = value;
            }

            private static final long serialVersionUID = 1L;

            private int position = data.length - 1;
        };
    }

    // Composite iterator, made by concatenating two iterators
    private static DataStorage.Iterator compositeIterator(final DataStorage.Iterator iterator1, final long size, final DataStorage.Iterator iterator2)
    {
        return new DataStorage.Iterator()
        {
            public boolean hasNext()
            {
                return (this.position < size ? iterator1.hasNext() : iterator2.hasNext());
            }

            public void next()
                throws ApfloatRuntimeException
            {
                (this.position < size ? iterator1 : iterator2).next();
                this.position++;
            }

            public int getInt()
                throws ApfloatRuntimeException
            {
                return (this.position < size ? iterator1 : iterator2).getInt();
            }

            public void setInt(int value)
                throws ApfloatRuntimeException
            {
                (this.position < size ? iterator1 : iterator2).setInt(value);
            }

            public void close()
                throws ApfloatRuntimeException
            {
                (this.position < size ? iterator1 : iterator2).close();
            }

            private static final long serialVersionUID = 1L;

            private long position;
        };
    }

    private static final long serialVersionUID = 7666237487091579201L;

    private static final IntModMath MATH_MOD_0,
                                        MATH_MOD_1,
                                        MATH_MOD_2;
    private static final int T0,
                                 T1,
                                 T2;
    private static final int[] M01,
                                   M02,
                                   M12,
                                   M012;

    static
    {
        MATH_MOD_0 = new IntModMath();
        MATH_MOD_1 = new IntModMath();
        MATH_MOD_2 = new IntModMath();

        MATH_MOD_0.setModulus(MODULUS[0]);
        MATH_MOD_1.setModulus(MODULUS[1]);
        MATH_MOD_2.setModulus(MODULUS[2]);

        // Probably sub-optimal, but it's a one-time operation

        BigInteger base = BigInteger.valueOf(Math.abs((long) MAX_POWER_OF_TWO_BASE)),   // In int case the base is 0x80000000
                   m0 = BigInteger.valueOf((long) MODULUS[0]),
                   m1 = BigInteger.valueOf((long) MODULUS[1]),
                   m2 = BigInteger.valueOf((long) MODULUS[2]),
                   m01 = m0.multiply(m1),
                   m02 = m0.multiply(m2),
                   m12 = m1.multiply(m2);

        T0 = m12.modInverse(m0).intValue();
        T1 = m02.modInverse(m1).intValue();
        T2 = m01.modInverse(m2).intValue();

        M01 = new int[2];
        M02 = new int[2];
        M12 = new int[2];
        M012 = new int[3];

        BigInteger[] qr = m01.divideAndRemainder(base);
        M01[0] = qr[0].intValue();
        M01[1] = qr[1].intValue();

        qr = m02.divideAndRemainder(base);
        M02[0] = qr[0].intValue();
        M02[1] = qr[1].intValue();

        qr = m12.divideAndRemainder(base);
        M12[0] = qr[0].intValue();
        M12[1] = qr[1].intValue();

        qr = m0.multiply(m12).divideAndRemainder(base);
        M012[2] = qr[1].intValue();
        qr = qr[0].divideAndRemainder(base);
        M012[0] = qr[0].intValue();
        M012[1] = qr[1].intValue();
    }
}
