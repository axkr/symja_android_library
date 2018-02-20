package org.apfloat.internal;

import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.MatrixStrategy;

/**
 * Creates matrix operations objects, for the
 * <code>int</code> type.
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public class IntMatrixBuilder
    implements MatrixBuilder
{
    /**
     * Default constructor.
     */

    public IntMatrixBuilder()
    {
    }

    public MatrixStrategy createMatrix()
    {
        return IntMatrixBuilder.matrixStrategy;
    }

    private static MatrixStrategy matrixStrategy = new IntMatrixStrategy();
}
