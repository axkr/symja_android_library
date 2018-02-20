package org.apfloat.internal;

import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.MatrixStrategy;

/**
 * Creates matrix operations objects, for the
 * <code>float</code> type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class FloatMatrixBuilder
    implements MatrixBuilder
{
    /**
     * Default constructor.
     */

    public FloatMatrixBuilder()
    {
    }

    public MatrixStrategy createMatrix()
    {
        return FloatMatrixBuilder.matrixStrategy;
    }

    private static MatrixStrategy matrixStrategy = new FloatMatrixStrategy();
}
