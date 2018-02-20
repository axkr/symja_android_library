package org.apfloat.spi;

/**
 * Interface of a factory for creating matrix related objects.
 * The factory method pattern is used.
 *
 * @see MatrixStrategy
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public interface MatrixBuilder
{
    /**
     * Creates an object for matrix operations.
     *
     * @return A suitable object for performing the matrix operations.
     */

    public MatrixStrategy createMatrix();
}
