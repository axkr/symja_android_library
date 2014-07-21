package org.apfloat.spi;

/**
 * Interface of a factory for creating convolutors.
 * The factory method pattern is used.
 *
 * @see ConvolutionStrategy
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface ConvolutionBuilder
{
    /**
     * Returns a convolution strategy of suitable
     * type for the specified length.
     *
     * @param radix The radix that will be used.
     * @param size1 Length of first data set.
     * @param size2 Length of second data set.
     * @param resultSize Minimum number of elements needed in the result data.
     *
     * @return A suitable object for performing the convolution.
     */

    public ConvolutionStrategy createConvolution(int radix, long size1, long size2, long resultSize);
}
