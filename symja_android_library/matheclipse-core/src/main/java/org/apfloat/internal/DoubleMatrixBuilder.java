package org.apfloat.internal;

import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.MatrixStrategy;

/**
 * Creates matrix operations objects, for the
 * <code>double</code> type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class DoubleMatrixBuilder
    implements MatrixBuilder
{
    /**
     * Default constructor.
     */

    public DoubleMatrixBuilder()
    {
    }

    public MatrixStrategy createMatrix()
    {
        return DoubleMatrixBuilder.matrixStrategy;
    }

    private static MatrixStrategy matrixStrategy = new DoubleMatrixStrategy();
}
