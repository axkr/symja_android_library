package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.DataStorage;

/**
 * Convolution strategy using the Karatsuba algorithm.
 * The complexity of the algorithm is O(n<sup>log(3)/log(2)</sup>) as
 * the operands are split to two and multiplied using three multiplications
 * (and five additions / subtractions). This splitting is done recursively
 * until some cut-off point where the basic O(n<sup>2</sup>) algorithm is
 * applied. The Karatsuba algorithm is faster than the basic O(n<sup>2</sup>)
 * multiplication algorithm for medium size numbers larger than some certain
 * size. For very large numbers, the transform-based convolution algorithms
 * are faster.
 *
 * @since 1.4
 * @version 1.4
 * @author Mikko Tommila
 */

public class FloatKaratsubaConvolutionStrategy
    extends FloatMediumConvolutionStrategy
{
    /**
     * Cut-off point for Karatsuba / basic convolution.<p>
     *
     * Convolutions where the shorter number is at most this long
     * are calculated using the basic O(n<sup>2</sup>) algorithm
     * i.e. <code>super.convolute()</code>.
     */

    public static final int CUTOFF_POINT = 15;

    /**
     * Creates a convolution strategy using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public FloatKaratsubaConvolutionStrategy(int radix)
    {
        super(radix);
    }

    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize)
        throws ApfloatRuntimeException
    {
        if (Math.min(x.getSize(), y.getSize()) <= CUTOFF_POINT)
        {
            // The numbers are too short for Karatsuba to have any advantage, fall back to O(n^2) algorithm
            return super.convolute(x, y, resultSize);
        }

        DataStorage shortStorage, longStorage;

        if (x.getSize() > y.getSize())
        {
            shortStorage = y;
            longStorage = x;
        }
        else
        {
            shortStorage = x;
            longStorage = y;
        }

        long shortSize = shortStorage.getSize(),
             longSize = longStorage.getSize(),
             size = shortSize + longSize,
             halfSize = longSize + 1 >> 1,      // Split point for recursion, round up
             x1size = longSize - halfSize,
             x2size = halfSize,
             y1size = shortSize - halfSize;     // y2size = halfSize

        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        DataStorage resultStorage = dataStorageBuilder.createDataStorage(size * 4);
        resultStorage.setSize(size);

        if (y1size <= 0)
        {
            // The shorter number is half of the longer number or less, use simplified algorithm
            DataStorage.Iterator dst = resultStorage.iterator(DataStorage.WRITE, size, 0),
                                 src1 = null;
            float carry = 0;
            long i = longSize,
                 xSize;

            // Calculate sub-results in blocks of size shortSize
            do
            {
                xSize = Math.min(i, shortSize);
                x = longStorage.subsequence(i - xSize, xSize);
                y = shortStorage;

                // Calculate sub-convolutions recursively
                DataStorage a = convolute(x, y, xSize + shortSize);

                assert (a.getSize() == xSize + shortSize);

                // Add the sub-results together
                DataStorage.Iterator src2 = a.iterator(DataStorage.READ, xSize + shortSize, 0);

                carry = baseAdd(src1, src2, carry, dst, shortSize);

                src1 = src2;
                i -= shortSize;
            } while (i > 0);

            // Propagate carry through the last sub-result and store to result data
            carry = baseAdd(src1, null, carry, dst, xSize);

            assert (carry == 0);
        }
        else
        {
            // The numbers are roughly equal size (shorter is more than half of the longer), use Karatsuba algorithm
            DataStorage x1 = longStorage.subsequence(0, x1size),
                        x2 = longStorage.subsequence(x1size, x2size),
                        y1 = shortStorage.subsequence(0, y1size),
                        y2 = shortStorage.subsequence(y1size, halfSize);

            // Calculate a = x1 + x2
            DataStorage a = add(x1, x2);

            // Calculate b = y1 + y2
            DataStorage b = add(y1, y2);

            // Calculate sub-convolutions recursively
            DataStorage c = convolute(a, b, a.getSize() + b.getSize());
            a = convolute(x1, y1, x1size + y1size);
            b = convolute(x2, y2, 2 * halfSize);

            // Calculate c = c - a - b
            subtract(c, a);
            subtract(c, b);

            long cSize = c.getSize(),
                 c1size = cSize - halfSize;

            if (c1size > x1size + y1size)
            {
                // We know that the top one or two words of c are zero
                // Omit them to avoid later having c1size > x1size + y1size
                long zeros = c1size - x1size - y1size;
                assert (isZero(c, 0));
                assert (zeros == 1 || isZero(c, 1));
                assert (zeros <= 2);
                cSize -= zeros;
                c1size -= zeros;
                c = c.subsequence(zeros, cSize);
            }

            assert (a.getSize() == x1size + y1size);
            assert (b.getSize() == 2 * halfSize);
            assert (cSize >= 2 * halfSize && cSize <= 2 * halfSize + 2);
            assert (c1size <= x1size + y1size);

            // Add the sub-results a + b + c together
            DataStorage.Iterator src1 = a.iterator(DataStorage.READ, x1size + y1size, 0),
                                 src2 = b.iterator(DataStorage.READ, 2 * halfSize, 0),
                                 src3 = c.iterator(DataStorage.READ, cSize, 0),
                                 dst = resultStorage.iterator(DataStorage.WRITE, size, 0);

            float carry = 0;
            carry = baseAdd(src2, null, carry, dst, halfSize);
            carry = baseAdd(src2, src3, carry, dst, halfSize);
            carry = baseAdd(src1, src3, carry, dst, c1size);
            carry = baseAdd(src1, null, carry, dst, x1size + y1size - c1size);

            assert (carry == 0);
        }

        return resultStorage;
    }

    // Return x1 + x2
    private DataStorage add(DataStorage x1, DataStorage x2)
    {
        long x1size = x1.getSize(),
             x2size = x2.getSize();

        assert (x1size <= x2size);

        long size = x2size + 1;

        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        DataStorage resultStorage = dataStorageBuilder.createDataStorage(size * 4);
        resultStorage.setSize(size);

        // Calculate x1 + x2
        DataStorage.Iterator src1 = x1.iterator(DataStorage.READ, x1size, 0),
                             src2 = x2.iterator(DataStorage.READ, x2size, 0),
                             dst = resultStorage.iterator(DataStorage.WRITE, size, 0);

        float carry = 0;
        carry = baseAdd(src1, src2, carry, dst, x1size);
        carry = baseAdd(src2, null, carry, dst, x2size - x1size);
        baseAdd(null, null, carry, dst, 1);         // Set carry digit to the top word
        if (carry == 0)
        {
            resultStorage = resultStorage.subsequence(1, size - 1);     // Omit zero top word
        }

        return resultStorage;
    }

    // x1 -= x2
    private void subtract(DataStorage x1, DataStorage x2)
    {
        long x1size = x1.getSize(),
             x2size = x2.getSize();

        assert (x1size >= x2size);

        DataStorage.Iterator src1 = x1.iterator(DataStorage.READ_WRITE, x1size, 0),
                             src2 = x2.iterator(DataStorage.READ, x2size, 0),
                             dst = src1;

        float carry = 0;
        carry = baseSubtract(src1, src2, carry, dst, x2size);
        carry = baseSubtract(src1, null, carry, dst, x1size - x2size);

        assert (carry == 0);
    }

    private boolean isZero(DataStorage x, long index)
    {
        DataStorage.Iterator i = x.iterator(DataStorage.READ, index, index + 1);

        float data = i.getFloat();
        i.next();

        return data == 0;
    }

    private static final long serialVersionUID = -4438101427690647475L;
}
