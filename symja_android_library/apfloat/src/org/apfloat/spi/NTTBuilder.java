package org.apfloat.spi;

/**
 * Interface of a factory for creating Number Theoretic Transforms.
 * The factory method pattern is used.
 *
 * @see NTTStrategy
 * @see NTTStepStrategy
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public interface NTTBuilder
{
    /**
     * Creates a Number Theoretic Transform of suitable
     * type for the specified length.
     *
     * @param size The transform length that will be used.
     *
     * @return A suitable NTT object for performing the transform.
     */

    public NTTStrategy createNTT(long size);

    /**
     * Creates an object for implementing the steps of a step-based
     * Number Theoretic Transform.
     *
     * @return A suitable object for performing the transform steps.
     *
     * @since 1.7.0
     */

    public NTTStepStrategy createNTTSteps();

    /**
     * Creates an object for implementing the steps of a three-NTT
     * based convolution.
     *
     * @return A suitable object for performing the convolution steps.
     *
     * @since 1.7.0
     */

    public NTTConvolutionStepStrategy createNTTConvolutionSteps();

    /**
     * Creates an object for implementing the steps of factor-3 NTT.
     *
     * @return A suitable object for performing the factor-3 NTT steps.
     *
     * @since 1.7.0
     */

    public Factor3NTTStepStrategy createFactor3NTTSteps();
}
