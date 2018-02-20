package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;

/**
 * Steps for the factor-3 NTT.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public interface Factor3NTTStepStrategy
{
    /**
     * Transform the columns of a matrix using a 3-point transform.
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
     */

    public void transformColumns(DataStorage dataStorage0, DataStorage dataStorage1, DataStorage dataStorage2, long startColumn, long columns, long power2length, long length, boolean isInverse, int modulus)
        throws ApfloatRuntimeException;

    /**
     * Get the maximum transform length.
     *
     * @return The maximum transform length.
     */

    public long getMaxTransformLength();
}
