package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.Util;

/**
 * Fast Number Theoretic Transform that uses a "two-pass"
 * algorithm to calculate a very long transform on data that
 * resides on a mass storage device. The storage medium should
 * preferably be a solid state disk for good performance;
 * on normal hard disks performance is usually inadequate.<p>
 *
 * The "two-pass" algorithm only needs to do two passes through
 * the data set. In comparison, a basic FFT algorithm of length 2<sup>n</sup>
 * needs to do n passes through the data set. Although the
 * algorithm is fairly optimal in terms of amount of data transferred
 * between the mass storage and main memory, the mass storage access is
 * not linear but done in small incontinuous pieces, so due to disk
 * seek times the performance can be quite lousy.<p>
 *
 * When the data to be transformed is considered to be an
 * n<sub>1</sub> x n<sub>2</sub> matrix of data, instead of a linear array,
 * the two passes go as follows:<p>
 *
 * <ol>
 *   <li>Do n<sub>2</sub> transforms of length n<sub>1</sub> by transforming the matrix columns.
 *       Do this by fetching n<sub>1</sub> x b blocks in memory so that the
 *       blocks are as large as possible but fit in main memory.</li>
 *   <li>Then do n<sub>1</sub> transforms of length n<sub>2</sub> by transforming the matrix rows.
 *       Do this also by fetching b x n<sub>2</sub> blocks in memory so that the blocks just
 *       fit in the available memory.</li>
 * </ol>
 *
 * The algorithm requires reading blocks of b elements from the mass storage device.
 * The smaller the amount of memory compared to the transform length is, the smaller
 * is b also. Reading very short blocks of data from hard disks can be prohibitively
 * slow.<p>
 *
 * When reading the column data to be transformed, the data can be transposed to
 * rows by reading the b-length blocks to proper locations in memory and then
 * transposing the b x b blocks.<p>
 *
 * In a convolution algorithm the data elements can remain in any order after
 * the transform, as long as the inverse transform can transform it back.
 * The convolution's element-by-element multiplication is not sensitive
 * to the order in which the elements are.<p>
 *
 * All access to this class must be externally synchronized.
 *
 * @see DataStorage#getTransposedArray(int,int,int,int)
 *
 * @since 1.7.0
 * @version 1.8.1
 * @author Mikko Tommila
 */

public class TwoPassFNTStrategy
    extends AbstractStepFNTStrategy
{
    /**
     * Default constructor.
     */

    public TwoPassFNTStrategy()
    {
    }

    protected void transform(DataStorage dataStorage, int n1, int n2, long length, int modulus)
        throws ApfloatRuntimeException
    {
        assert (n2 >= n1);

        int maxBlockSize = getMaxMemoryBlockSize(length),   // Maximum memory array size that can be allocated
            b;

        if (n1 > maxBlockSize || n2 > maxBlockSize)
        {
            throw new ApfloatInternalException("Not enough memory available to fit one row or column of matrix to memory; n1=" + n1 + ", n2=" + n2 + ", available=" + maxBlockSize);
        }

        b = maxBlockSize / n1;

        for (int i = 0; i < n2; i += b)
        {
            // Read the data in n1 x b blocks, transposed
            ArrayAccess arrayAccess = getColumns(dataStorage, i, b, n1);

            // Do b transforms of size n1
            transformColumns(arrayAccess, n1, b, false, modulus);

            arrayAccess.close();
        }

        b = maxBlockSize / n2;

        for (int i = 0; i < n1; i += b)
        {
            // Read the data in b x n2 blocks
            ArrayAccess arrayAccess = getRows(dataStorage, i, b, n2);

            // Multiply each matrix element by w^(i*j)
            multiplyElements(arrayAccess, i, 0, b, n2, length, 1, false, modulus);

            // Do b transforms of size n2
            transformRows(arrayAccess, n2, b, false, modulus);

            arrayAccess.close();
        }
    }

    protected void inverseTransform(DataStorage dataStorage, int n1, int n2, long length, long totalTransformLength, int modulus)
        throws ApfloatRuntimeException
    {
        assert (n2 >= n1);

        int maxBlockSize = getMaxMemoryBlockSize(length),   // Maximum memory array size that can be allocated
            b;

        if (n1 > maxBlockSize || n2 > maxBlockSize)
        {
            throw new ApfloatInternalException("Not enough memory available to fit one row or column of matrix to memory; n1=" + n1 + ", n2=" + n2 + ", available=" + maxBlockSize);
        }

        b = maxBlockSize / n2;

        for (int i = 0; i < n1; i += b)
        {
            // Read the data in b x n2 blocks
            ArrayAccess arrayAccess = getRows(dataStorage, i, b, n2);

            // Do b transforms of size n2
            transformRows(arrayAccess, n2, b, true, modulus);

            // Multiply each matrix element by w^(i*j) / n
            multiplyElements(arrayAccess, i, 0, b, n2, length, totalTransformLength, true, modulus);

            arrayAccess.close();
        }

        b = maxBlockSize / n1;

        for (int i = 0; i < n2; i += b)
        {
            // Read the data in n1 x b blocks, transposed
            ArrayAccess arrayAccess = getColumns(dataStorage, i, b, n1);

            // Do b transforms of size n1
            transformColumns(arrayAccess, n1, b, true, modulus);

            arrayAccess.close();
        }
    }

    /**
     * Get a block of column data. The data may be transposed, depending on the implementation.
     *
     * @param dataStorage The data storage.
     * @param startColumn The starting column where data is read.
     * @param columns The number of columns of data to read.
     * @param rows The number of rows of data to read. This should be equivalent to n<sub>1</sub>, number of rows in the matrix.
     *
     * @return Access to an array of size <code>columns</code> x <code>rows</code> containing the data.
     */

    protected ArrayAccess getColumns(DataStorage dataStorage, int startColumn, int columns, int rows)
    {
        return dataStorage.getTransposedArray(DataStorage.READ_WRITE, startColumn, columns, rows);
    }

    /**
     * Get a block of row data. The data may be transposed, depending on the implementation.
     *
     * @param dataStorage The data storage.
     * @param startRow The starting row where data is read.
     * @param rows The number of rows of data to read.
     * @param columns The number of columns of data to read. This should be equivalent to n<sub>2</sub>, number of columns in the matrix.
     *
     * @return Access to an array of size <code>columns</code> x <code>rows</code> containing the data.
     */

    protected ArrayAccess getRows(DataStorage dataStorage, int startRow, int rows, int columns)
    {
        return dataStorage.getArray(DataStorage.READ_WRITE, startRow * columns, rows * columns);
    }

    /**
     * Multiply each matrix element <code>(i, j)</code> by <code>w<sup>i * j</sup> / totalTransformLength</code>.
     * The matrix size is n<sub>1</sub> x n<sub>2</sub>.
     *
     * @param arrayAccess The memory array to multiply.
     * @param startRow Which row in the whole matrix the starting row in the <code>arrayAccess</code> is.
     * @param startColumn Which column in the whole matrix the starting column in the <code>arrayAccess</code> is.
     * @param rows The number of rows in the <code>arrayAccess</code> to multiply.
     * @param columns The number of columns in the matrix (= n<sub>2</sub>).
     * @param length The length of data in the matrix being transformed.
     * @param totalTransformLength The total transform length, for the scaling factor. Used only for the inverse case.
     * @param isInverse If the multiplication is done for the inverse transform or not.
     * @param modulus Index of the modulus.
     */

    protected void multiplyElements(ArrayAccess arrayAccess, int startRow, int startColumn, int rows, int columns, long length, long totalTransformLength, boolean isInverse, int modulus)
    {
        super.stepStrategy.multiplyElements(arrayAccess, startRow, startColumn, rows, columns, length, totalTransformLength, isInverse, modulus);
    }

    /**
     * Transform the columns of the data matrix.
     * The data may be in transposed format, depending on the implementation.<p>
     *
     * By default the column transforms permute the data, leaving it in the correct
     * order so the element-by-element multiplication is simpler.
     *
     * @param arrayAccess The memory array to split to columns and to transform.
     * @param length Length of one transform (one columns).
     * @param count Number of columns.
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     * @param modulus Index of the modulus.
     */

    protected void transformColumns(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus)
    {
        super.stepStrategy.transformRows(arrayAccess, length, count, isInverse, true, modulus);
    }

    /**
     * Transform the rows of the data matrix.
     * The data may be in transposed format, depending on the implementation.<p>
     *
     * By default the row transforms do not permute the data, leaving it in
     * scrambled order, as this does not matter when the data is only used for
     * convolution.
     *
     * @param arrayAccess The memory array to split to rows and to transform.
     * @param length Length of one transform (one row).
     * @param count Number of rows.
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     * @param modulus Index of the modulus.
     */

    protected void transformRows(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus)
    {
        super.stepStrategy.transformRows(arrayAccess, length, count, isInverse, false, modulus);
    }

    private int getMaxMemoryBlockSize(long length)
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        long maxMemoryBlockSize = Util.round2down(Math.min(ctx.getMaxMemoryBlockSize(), Integer.MAX_VALUE)) / ctx.getBuilderFactory().getElementSize();
        int maxBlockSize = (int) Math.min(length, maxMemoryBlockSize);

        return maxBlockSize;
    }
}
