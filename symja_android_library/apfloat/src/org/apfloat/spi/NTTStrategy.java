package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;

/**
 * Number Theoretic Transform (NTT) strategy.
 * An implementing class could be e.g.<p>
 *
 * <ul>
 *   <li>Fast Number Theoretic Transform (FNT)</li>
 *   <li>"Four-step" FNT</li>
 *   <li>"Two-pass" mass storage FNT</li>
 *   <li>Winograd Fourier Transform Algorithm (WFTA)</li>
 * </ul>
 *
 * Note: an implementing transformation class is required only to be able
 * to perform an inverse transform on data transformed by the same class,
 * not by any other class.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface NTTStrategy
{
    /**
     * Perform a forward transform on the data.<p>
     *
     * Multiple moduli can be used, if the convolution algorithm
     * uses the Chinese Remainder Theorem to calculate the final
     * result.
     *
     * @param dataStorage The data to be transformed.
     * @param modulus Number of modulus to use (in case the transform supports multiple moduli).
     */

    public void transform(DataStorage dataStorage, int modulus)
        throws ApfloatRuntimeException;

    /**
     * Perform an inverse transform on the data.<p>
     *
     * Multiple moduli can be used, if the convolution algorithm
     * uses the Chinese Remainder Theorem to calculate the final
     * result.
     *
     * @param dataStorage The data to be transformed.
     * @param modulus Number of modulus to use (in case the transform supports multiple moduli).
     * @param totalTransformLength Total transform length; the final result elements are divided by this value.
     */

    public void inverseTransform(DataStorage dataStorage, int modulus, long totalTransformLength)
        throws ApfloatRuntimeException;

    /**
     * Return the supported transform length for the specified data size.
     *
     * @param size Length of the data to be transformed.
     *
     * @return Length of the transform needed by this transform.
     */

    public long getTransformLength(long size);
}
