package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.Factor3NTTStepStrategy;
import org.apfloat.spi.DataStorage;
import static org.apfloat.internal.FloatModConstants.*;

/**
 * Steps for the factor-3 NTT.<p>
 *
 * The transform is done using a parallel algorithm, if the data fits in memory.<p>
 *
 * All access to this class must be externally synchronized.
 *
 * @since 1.7.0
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class FloatFactor3NTTStepStrategy
    extends FloatModMath
    implements Factor3NTTStepStrategy, Parallelizable
{
    // Runnable for transforming the columns in a factor-3 transform
    private class ColumnTransformRunnable
        implements Runnable
    {
        public ColumnTransformRunnable(DataStorage dataStorage0, DataStorage dataStorage1, DataStorage dataStorage2, long startColumn, long columns, float w, float ww, float w1, float w2, boolean isInverse)
        {
            this.dataStorage0 = dataStorage0;
            this.dataStorage1 = dataStorage1;
            this.dataStorage2 = dataStorage2;
            this.startColumn = startColumn;
            this.columns = columns;
            this.w = w;
            this.ww = ww;
            this.w1 = w1;
            this.w2 = w2;
            this.isInverse = isInverse;
        }

        public void run()
        {
            float tmp1 = modPow(this.w, (float) this.startColumn),
                    tmp2 = modPow(this.ww, (float) this.startColumn);

            DataStorage.Iterator iterator0 = this.dataStorage0.iterator(DataStorage.READ_WRITE, this.startColumn, this.startColumn + this.columns),
                                 iterator1 = this.dataStorage1.iterator(DataStorage.READ_WRITE, this.startColumn, this.startColumn + this.columns),
                                 iterator2 = this.dataStorage2.iterator(DataStorage.READ_WRITE, this.startColumn, this.startColumn + this.columns);

            for (long i = 0; i < this.columns; i++)
            {
                // 3-point WFTA on the corresponding array elements

                float x0 = iterator0.getFloat(),
                        x1 = iterator1.getFloat(),
                        x2 = iterator2.getFloat(),
                        t;

                if (this.isInverse)
                {
                    // Multiply before transform
                    x1 = modMultiply(x1, tmp1);
                    x2 = modMultiply(x2, tmp2);
                }

                // Transform columns
                t = modAdd(x1, x2);
                x2 = modSubtract(x1, x2);
                x0 = modAdd(x0, t);
                t = modMultiply(t, this.w1);
                x2 = modMultiply(x2, this.w2);
                t = modAdd(t, x0);
                x1 = modAdd(t, x2);
                x2 = modSubtract(t, x2);

                if (!this.isInverse)
                {
                    // Multiply after transform
                    x1 = modMultiply(x1, tmp1);
                    x2 = modMultiply(x2, tmp2);
                }

                iterator0.setFloat(x0);
                iterator1.setFloat(x1);
                iterator2.setFloat(x2);

                iterator0.next();
                iterator1.next();
                iterator2.next();

                tmp1 = modMultiply(tmp1, this.w);
                tmp2 = modMultiply(tmp2, this.ww);
            }
        }

        private DataStorage dataStorage0;
        private DataStorage dataStorage1;
        private DataStorage dataStorage2;
        private long startColumn;
        private long columns;
        private float w;
        private float ww;
        private float w1;
        private float w2;
        private boolean isInverse;
    }

    /**
     * Default constructor.
     */

    public FloatFactor3NTTStepStrategy()
    {
    }

    public void transformColumns(DataStorage dataStorage0, DataStorage dataStorage1, DataStorage dataStorage2, long startColumn, long columns, long power2length, long length, boolean isInverse, int modulus)
        throws ApfloatRuntimeException
    {
        // Transform length is three times a power of two
        assert (length == 3 * power2length);

        ParallelRunnable parallelRunnable = createColumnTransformParallelRunnable(dataStorage0, dataStorage1, dataStorage2, startColumn, columns, power2length, length, isInverse, modulus);

        if (columns <= Integer.MAX_VALUE &&                                     // Only if the size fits in an integer, but with memory arrays it should
            dataStorage0.isCached() &&                                          // Only if the data storage supports efficient parallel random access
            dataStorage1.isCached() &&
            dataStorage2.isCached())
        {
            ParallelRunner.runParallel(parallelRunnable);
        }
        else
        {
            parallelRunnable.run();                                             // Just run in current thread without parallelization
        }
    }

    public long getMaxTransformLength()
    {
        return MAX_TRANSFORM_LENGTH;
    }

    /**
     * Create a ParallelRunnable object for transforming the columns of the matrix
     * using a 3-point NTT transform.
     *
     * @param dataStorage0 The data of the first column.
     * @param dataStorage1 The data of the second column.
     * @param dataStorage2 The data of the third column.
     * @param startColumn The starting element index in the data storages to transform.
     * @param columns How many columns to transform.
     * @param power2length Length of the column transform.
     * @param length Length of total transform (three times the length of one column).
     * @param isInverse <code>true</code> if an inverse transform is performed, <code>false</code> if a forward transform is performed.
     * @param modulus Index of the modulus.
     *
     * @return A suitable object for performing the 3-point transforms in parallel.
     */

    protected ParallelRunnable createColumnTransformParallelRunnable(final DataStorage dataStorage0, final DataStorage dataStorage1, final DataStorage dataStorage2, final long startColumn, final long columns, long power2length, long length, final boolean isInverse, int modulus)
    {
        setModulus(MODULUS[modulus]);                                             // Modulus
        final float w = (isInverse ?
                           getInverseNthRoot(PRIMITIVE_ROOT[modulus], length) :
                           getForwardNthRoot(PRIMITIVE_ROOT[modulus], length)),   // Forward/inverse n:th root
                      w3 = modPow(w, (float) power2length),                     // Forward/inverse 3rd root
                      ww = modMultiply(w, w),
                      w1 = negate(modDivide((float) 3, (float) 2)),
                      w2 = modAdd(w3, modDivide((float) 1, (float) 2));

        ParallelRunnable parallelRunnable = new ParallelRunnable(columns)
        {
            public Runnable getRunnable(long strideStartColumn, long strideColumns)
            {
                return new ColumnTransformRunnable(dataStorage0, dataStorage1, dataStorage2, startColumn + strideStartColumn, strideColumns, w, ww, w1, w2, isInverse);
            }
        };
        return parallelRunnable;
    }
}
