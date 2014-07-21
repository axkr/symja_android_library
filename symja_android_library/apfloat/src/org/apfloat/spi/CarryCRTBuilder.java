package org.apfloat.spi;

/**
 * Interface of a factory for creating carry-CRT related objects.
 * The factory method pattern is used.
 *
 * @param <T> The element array type of the CRT.
 *
 * @see CarryCRTStrategy
 * @see CarryCRTStepStrategy
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public interface CarryCRTBuilder<T>
{
    /**
     * Creates an object for implementing the carry-CRT of a three-NTT
     * based convolution using the specified radix.
     *
     * @param radix The radix that will be used.
     *
     * @return A suitable object for performing the carry-CRT.
     */

    public CarryCRTStrategy createCarryCRT(int radix);

    /**
     * Creates an object for implementing the steps of the carry-CRT
     * of a three-NTT based convolution using the specified radix.
     *
     * @param radix The radix that will be used.
     *
     * @return A suitable object for performing the carry-CRT steps.
     */

    public CarryCRTStepStrategy<T> createCarryCRTSteps(int radix);
}
