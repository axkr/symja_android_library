package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.DataStorage;

/**
 * Medium-length convolution strategy.
 * Performs a simple O(n<sup>2</sup>) multiplication when the size of one operand is relatively short.
 *
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class IntMediumConvolutionStrategy
    extends IntBaseMath
    implements ConvolutionStrategy
{
    // Implementation notes:
    // - Assumes that the operands have been already truncated to match resultSize (the resultSize argument is ignored)
    // - This class probably shouldn't be converted to a single class using generics because there is some performance impact

    /**
     * Creates a convolution strategy using the specified radix.
     *
     * @param radix The radix that will be used.
     */

    public IntMediumConvolutionStrategy(int radix)
    {
        super(radix);
    }

    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize)
        throws ApfloatRuntimeException
    {
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
             size = shortSize + longSize;

        if (shortSize > Integer.MAX_VALUE)
        {
            throw new ApfloatInternalException("Too long shorter number, size = " + shortSize);
        }

        final int bufferSize = (int) shortSize;

        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        DataStorage resultStorage = dataStorageBuilder.createDataStorage(size * 4);
        resultStorage.setSize(size);

        DataStorage.Iterator src = longStorage.iterator(DataStorage.READ, longSize, 0),
                             dst = resultStorage.iterator(DataStorage.WRITE, size, 0),
                             tmpDst = new DataStorage.Iterator()                        // Cyclic iterator
                             {
                                 public void next()
                                 {
                                     this.position++;
                                     this.position = (this.position == bufferSize ? 0 : this.position);
                                 }

                                 public int getInt()
                                 {
                                     return this.buffer[this.position];
                                 }

                                 public void setInt(int value)
                                 {
                                     this.buffer[this.position] = value;
                                 }

                                 private static final long serialVersionUID = 1L;

                                 private int[] buffer = new int[bufferSize];
                                 private int position = 0;
                             };

        for (long i = 0; i < longSize; i++)
        {
            DataStorage.Iterator tmpSrc = shortStorage.iterator(DataStorage.READ, shortSize, 0);        // Sub-optimal: this could be cyclic also

            int factor = src.getInt(),          // Get one word of source data
                    carry = baseMultiplyAdd(tmpSrc, tmpDst, factor, 0, tmpDst, shortSize),
                    result = tmpDst.getInt();       // Least significant word of the result

            dst.setInt(result);     // Store one word of result

            tmpDst.setInt(carry);   // Set carry from calculation as new last word in cyclic buffer

            tmpDst.next();              // Cycle buffer; current first word becomes last
            src.next();
            dst.next();
        }

        // Exhaust last words from temporary cyclic buffer and store them to result data
        for (int i = 0; i < bufferSize; i++)
        {
            int result = tmpDst.getInt();
            dst.setInt(result);

            tmpDst.next();
            dst.next();
        }

        return resultStorage;
    }

    private static final long serialVersionUID = -1339358141859224649L;
}
