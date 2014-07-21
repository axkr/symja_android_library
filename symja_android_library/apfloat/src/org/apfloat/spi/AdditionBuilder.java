package org.apfloat.spi;

/**
 * Interface of a factory for creating addition strategies.
 * The factory method pattern is used.
 *
 * @param <T> The element type of the addition strategies.
 *
 * @see AdditionStrategy
 *
 * @since 1.7.0
 * @version 1.7.0
 * @author Mikko Tommila
 */

public interface AdditionBuilder<T>
{
    /**
     * Returns an addition strategy of suitable type for the specified radix.
     *
     * @param radix The radix that will be used.
     *
     * @return A suitable object for performing the addition.
     */

    public AdditionStrategy<T> createAddition(int radix);
}
