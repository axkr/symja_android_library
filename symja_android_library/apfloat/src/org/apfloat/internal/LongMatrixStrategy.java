package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.MatrixStrategy;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.Util;

/**
 * Optimized matrix transposition methods for the <code>long</code> type.
 * The matrix transposition algorithm isn't parallelized.<p>
 *
 * While the matrix transposition algorithm could easily be parallelized,
 * on an SMP machine it does not make any sense. If the matrix doesn't fit
 * in any processor specific cache then the memory (or higher level
 * shared cache) bandwidth becomes a bottleneck in the algorithm. Matrix
 * transposition is in principle a very simple algorithm - it doesn't do
 * anything else than move data from one place to another. If shared memory
 * is the bottleneck, then the algorithm isn't any faster if the data is being
 * moved around by one thread or by multiple threads in parallel.<p>
 *
 * If the data fits in a processor specific cache, then the algorithm could
 * theoretically be made faster with parallelization. To make the parallelization
 * effective however, the data would have to be set up in some kind of a NUMA
 * way. For example, each processor core would hold an equal section of
 * the data in the processor cache. Then the algorithm could be made faster
 * as each processor core could quickly transpose blocks of data that are in the
 * processor cache, and then exchange blocks with other processor cores via the
 * slower higher level shared cache or main memory.<p>
 *
 * This approach doesn't work well in practice however, at least not in a Java
 * program. The reason is that there are no guarantees where the data is when
 * the algorithm starts (in which processor core caches), and further there are
 * no guarantees of any processor affinity for the threads that are executing
 * in parallel. Different processor cores could be executing the transposition
 * of different sections of the data at any moment, depending on how the
 * operating system (and the JVM) schedule thread execution. And more often
 * than not, the operating system isn't smart enough to apply any such processor
 * affinity for the threads.<p>
 *
 * An additional problem for any NUMA based attempt is that the data array would
 * have to be aligned on a cache line (e.g. 64 or 128 bytes), to prevent
 * cache contention at the edges of each data section. But a JVM makes no such
 * guarantees about memory alignment. And since pointers do not exist in Java,
 * manually aligning memory addresses isn't possible.<p>
 *
 * Considering all of the above, the parallel algorithm doesn't in practice work
 * any faster than the single-thread algorithm, as the algorithm is bound by the
 * memory bandwidth (or shared cache bandwidth). In some cases parallelization
 * can even make the execution slower due to increased cache contention.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class LongMatrixStrategy
    implements MatrixStrategy
{
    /**
     * Default constructor.
     */

    public LongMatrixStrategy()
    {
    }

    /**
     * Transpose a n<sub>1</sub> x n<sub>2</sub> matrix.<p>
     *
     * Both n<sub>1</sub> and n<sub>2</sub> must be powers of two.
     * Additionally, one of these must be true:<p>
     *
     * n<sub>1</sub> = n<sub>2</sub><br>
     * n<sub>1</sub> = 2*n<sub>2</sub><br>
     * n<sub>2</sub> = 2*n<sub>1</sub><br>
     *
     * @param arrayAccess Accessor to the matrix data. This data will be transposed.
     * @param n1 Number of rows in the matrix.
     * @param n2 Number of columns in the matrix.
     */

    public void transpose(ArrayAccess arrayAccess, int n1, int n2)
        throws ApfloatRuntimeException
    {
        long[] data = arrayAccess.getLongData();
        int offset = arrayAccess.getOffset();

        if (n1 != (n1 & -n1) ||
            n2 != (n2 & -n2) ||
            n1 <= 0 || n2 <= 0)
        {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        if (n1 == n2)
        {
            // Simply transpose

            transposeSquare(data, offset, n1, n1);
        }
        else if (n2 == 2 * n1)
        {
            // First transpose two n1 x n1 blocks
            transposeSquare(data, offset, n1, n2);
            transposeSquare(data, offset + n1, n1, n2);

            // Then permute the rows to correct order
            permuteToHalfWidth(data, offset, n1, n2);
        }
        else if (n1 == 2 * n2)
        {
            // First permute the rows to correct order
            permuteToDoubleWidth(data, offset, n1, n2);

            // Then transpose two n2 x n2 blocks
            transposeSquare(data, offset, n2, n1);
            transposeSquare(data, offset + n2, n2, n1);
        }
        else
        {
            throw new ApfloatInternalException("Must be n1 = n2, n1 = 2*n2 or n2 = 2*n1; matrix is " + n1 + " x " + n2);
        }
    }

    /**
     * Transpose a square n<sub>1</sub> x n<sub>1</sub> block of n<sub>1</sub> x n<sub>2</sub> matrix.<p>
     *
     * Both n<sub>1</sub> and n<sub>2</sub> must be powers of two,
     * and n<sub>1</sub> <= n<sub>2</sub>.
     *
     * @param arrayAccess Accessor to the matrix data. This data will be transposed.
     * @param n1 Number of rows and columns in the block to be transposed.
     * @param n2 Number of columns in the matrix.
     */

    public void transposeSquare(ArrayAccess arrayAccess, int n1, int n2)
        throws ApfloatRuntimeException
    {
        transposeSquare(arrayAccess.getLongData(), arrayAccess.getOffset(), n1, n2);
    }

    /**
     * Permute the rows of the n<sub>1</sub> x n<sub>2</sub> matrix so that it is shaped like a
     * n<sub>1</sub>/2 x 2*n<sub>2</sub> matrix. Logically, the matrix is split in half, and the
     * lower half is moved to the right side of the upper half.<p>
     *
     * Both n<sub>1</sub> and n<sub>2</sub> must be powers of two,
     * and n<sub>1</sub> >= 2.<p>
     *
     * E.g. if the matrix layout is originally as follows:
     * <table style="width:100px; border-collapse:collapse; border:1px solid black" border="1">
     *   <tr>
     *     <td>0</td><td>1</td><td>2</td><td>3</td>
     *   </tr>
     *   <tr>
     *     <td>4</td><td>5</td><td>6</td><td>7</td>
     *   </tr>
     *   <tr>
     *     <td>8</td><td>9</td><td>10</td><td>11</td>
     *   </tr>
     *   <tr>
     *     <td>12</td><td>13</td><td>14</td><td>15</td>
     *   </tr>
     * </table>
     *
     * Then after this method it is as follows:
     * <table style="width:200px; border-collapse:collapse; border:1px solid black" border="1">
     *   <tr>
     *     <td>0</td><td>1</td><td>2</td><td>3</td><td>8</td><td>9</td><td>10</td><td>11</td>
     *   </tr>
     *   <tr>
     *     <td>4</td><td>5</td><td>6</td><td>7</td><td>12</td><td>13</td><td>14</td><td>15</td>
     *   </tr>
     * </table>
     *
     * @param arrayAccess Accessor to the matrix data. This data will be permuted.
     * @param n1 Number of rows in the matrix.
     * @param n2 Number of columns in the matrix.
     *
     * @since 1.7.0
     */

    public void permuteToDoubleWidth(ArrayAccess arrayAccess, int n1, int n2)
        throws ApfloatRuntimeException
    {
        if (n1 != (n1 & -n1) ||
            n2 != (n2 & -n2) ||
            n1 <= 0 || n2 <= 0)
        {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        if (n1 < 2)
        {
            throw new ApfloatInternalException("Matrix height must be at least 2.");
        }
        permuteToDoubleWidth(arrayAccess.getLongData(), arrayAccess.getOffset(), n1, n2);
    }

    /**
     * Permute the rows of the n<sub>1</sub> x n<sub>2</sub> matrix so that it is shaped like a
     * 2*n<sub>1</sub> x n<sub>2</sub>/2 matrix. Logically, the matrix is split in half, and the
     * right half is moved below the left half.<p>
     *
     * Both n<sub>1</sub> and n<sub>2</sub> must be powers of two.
     *
     * E.g. if the matrix layout is originally as follows:
     * <table style="width:200px; border-collapse:collapse; border:1px solid black" border="1">
     *   <tr>
     *     <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td>
     *   </tr>
     *   <tr>
     *     <td>8</td><td>9</td><td>10</td><td>11</td><td>12</td><td>13</td><td>14</td><td>15</td>
     *   </tr>
     * </table>
     *
     * Then after this method it is as follows:
     * <table style="width:100px; border-collapse:collapse; border:1px solid black" border="1">
     *   <tr>
     *     <td>0</td><td>1</td><td>2</td><td>3</td>
     *   </tr>
     *   <tr>
     *     <td>8</td><td>9</td><td>10</td><td>11</td>
     *   </tr>
     *   <tr>
     *     <td>4</td><td>5</td><td>6</td><td>7</td>
     *   </tr>
     *   <tr>
     *     <td>12</td><td>13</td><td>14</td><td>15</td>
     *   </tr>
     * </table>
     *
     * @param arrayAccess Accessor to the matrix data. This data will be permuted.
     * @param n1 Number of rows in the matrix.
     * @param n2 Number of columns in the matrix.
     *
     * @since 1.7.0
     */

    public void permuteToHalfWidth(ArrayAccess arrayAccess, int n1, int n2)
        throws ApfloatRuntimeException
    {
        if (n1 != (n1 & -n1) ||
            n2 != (n2 & -n2) ||
            n1 <= 0 || n2 <= 0)
        {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        permuteToHalfWidth(arrayAccess.getLongData(), arrayAccess.getOffset(), n1, n2);
    }

    // Move a b x b block from source to dest
    private static void moveBlock(long[] source, int sourceOffset, int sourceWidth, long[] dest, int destOffset, int destWidth, int b)
    {
        for (int i = 0; i < b; i++)
        {
            System.arraycopy(source, sourceOffset, dest, destOffset, b);

            destOffset += destWidth;
            sourceOffset += sourceWidth;
        }
    }

    // Transpose two b x b blocks of matrix with specified width
    // data based on offset1 is accessed in columns, data based on offset2 in rows
    private static void transpose2blocks(long[] data, int offset1, int offset2, int width, int b)
    {
        for (int i = 0, position1 = offset2; i < b; i++, position1 += width)
        {
            for (int j = 0, position2 = offset1 + i; j < b; j++, position2 += width)
            {
                long tmp = data[position1 + j];
                data[position1 + j] = data[position2];
                data[position2] = tmp;
            }
        }
    }

    // Transpose a b x b block of matrix with specified width
    private static void transposeBlock(long[] data, int offset, int width, int b)
    {
        for (int i = 0, position1 = offset; i < b; i++, position1 += width)
        {
            for (int j = i + 1, position2 = offset + j * width + i; j < b; j++, position2 += width)
            {
                long tmp = data[position1 + j];
                data[position1 + j] = data[position2];
                data[position2] = tmp;
            }
        }
    }

    // Transpose a square n1 x n1 block of n1 x n2 matrix in b x b blocks
    private static void transposeSquare(long[] data, int offset, int n1, int n2)
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        int cacheBurstBlockSize = Util.round2down(ctx.getCacheBurst() / 8),   // Cache burst in longs
            cacheBlockSize = Util.sqrt4down(ctx.getCacheL1Size() / 8),        // Transpose block size b that fits in processor L1 cache
            cacheTreshold = Util.round2down(ctx.getCacheL2Size() / 8);        // Size of matrix that fits in L2 cache

        if (n1 <= cacheBurstBlockSize || n1 <= cacheBlockSize)
        {
            // Whole matrix fits in L1 cache

            transposeBlock(data, offset, n2, n1);
        }
        else if (n1 * n2 <= cacheTreshold)
        {
            // Whole matrix fits in L2 cache (but not in L1 cache)
            // Sometimes the first algorithm (the block above) is faster, if your L2 cache is very fast

            int b = cacheBurstBlockSize;

            for (int i = 0, position1 = offset; i < n1; i += b, position1 += b * n2)
            {
                transposeBlock(data, position1 + i, n2, b);

                for (int j = i + b, position2 = offset + j * n2 + i; j < n1; j += b, position2 += b * n2)
                {
                    transpose2blocks(data, position1 + j, position2, n2, b);
                }
            }
        }
        else
        {
            // Whole matrix doesn't fit in L2 cache
            // This algorithm works fastest if L1 cache size is set correctly

            int b = cacheBlockSize;

            long[] tmp1 = new long[b * b],
                      tmp2 = new long[b * b];

            for (int i = 0, position1 = offset; i < n1; i += b, position1 += b * n2)
            {
                moveBlock(data, position1 + i, n2, tmp1, 0, b, b);
                transposeBlock(tmp1, 0, b, b);
                moveBlock(tmp1, 0, b, data, position1 + i, n2, b);

                for (int j = i + b, position2 = offset + j * n2 + i; j < n1; j += b, position2 += b * n2)
                {
                    moveBlock(data, position1 + j, n2, tmp1, 0, b, b);
                    transposeBlock(tmp1, 0, b, b);

                    moveBlock(data, position2, n2, tmp2, 0, b, b);
                    transposeBlock(tmp2, 0, b, b);

                    moveBlock(tmp2, 0, b, data, position1 + j, n2, b);
                    moveBlock(tmp1, 0, b, data, position2, n2, b);
                }
            }
        }
    }

    // Permute the rows of matrix to correct order, to make the n1 x n2 matrix half as wide (2*n1 x n2/2)
    private static void permuteToHalfWidth(long[] data, int offset, int n1, int n2)
    {
        if (n1 < 2)
        {
            return;
        }

        int twicen1 = 2 * n1;
        int halfn2 = n2 / 2;
        long[] tmp = new long[halfn2];
        boolean[] isRowDone = new boolean[twicen1];

        int j = 1;
        do
        {
            int o = j,
                m = j;

            System.arraycopy(data, offset + halfn2 * m, tmp, 0, halfn2);

            isRowDone[m] = true;

            m = (m < n1 ? 2 * m : 2 * (m - n1) + 1);

            while (m != j)
            {
                isRowDone[m] = true;

                System.arraycopy(data, offset + halfn2 * m, data, offset + halfn2 * o, halfn2);

                o = m;
                m = (m < n1 ? 2 * m : 2 * (m - n1) + 1);
            }

            System.arraycopy(tmp, 0, data, offset + halfn2 * o, halfn2);

            while (isRowDone[j])
            {
                j++;
            }
        } while (j < twicen1 - 1);
    }

    // Permute the rows of matrix to correct order, to make the n1 x n2 matrix twice as wide (n1/2 x 2*n2)
    private static void permuteToDoubleWidth(long[] data, int offset, int n1, int n2)
    {
        if (n1 < 4)
        {
            return;
        }

        int halfn1 = n1 / 2;
        long[] tmp = new long[n2];
        boolean[] isRowDone = new boolean[n1];

        int j = 1;
        do
        {
            int o = j,
                m = j;

            System.arraycopy(data, offset + n2 * m, tmp, 0, n2);

            isRowDone[m] = true;

            m = ((m & 1) != 0 ? m / 2 + halfn1 : m / 2);

            while (m != j)
            {
                isRowDone[m] = true;

                System.arraycopy(data, offset + n2 * m, data, offset + n2 * o, n2);

                o = m;
                m = ((m & 1) != 0 ? m / 2 + halfn1 : m / 2);
            }

            System.arraycopy(tmp, 0, data, offset + n2 * o, n2);

            while (isRowDone[j])
            {
                j++;
            }
        } while (j < n1 - 1);
    }
}
