package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;

/**
 * Generic convolution strategy. To perform the convolution,
 * an implementing class could use e.g.<p>
 *
 * <ul>
 *   <li>A simple long multiplication convolution with O(n<sup>2</sup>) complexity</li>
 *   <li>An O(n<sup>log2(3)</sup>) Karatsuba type algorithm, e.g. <a href="http://www.apfloat.org/log23.html" target="_blank">as desribed in Knuth's Seminumerical Algorithms</a></li>
 *   <li>Floating-point Fast Fourier Transform (FFT) based convolution</li>
 *   <li><a href="http://www.apfloat.org/ntt.html" target="_blank">Number-Theoretic Transform (NTT)</a> based convolution, with the <a href="http://www.apfloat.org/crt.html" target="_blank">Chinese Remainder Theorem</a> used</li>
 * </ul>
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface ConvolutionStrategy
{
    /**
     * Convolutes the two sets of data.
     *
     * @param x First data set.
     * @param y Second data set.
     * @param resultSize Number of elements needed in the result data.
     *
     * @return The convolved data.
     */

    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize)
        throws ApfloatRuntimeException;
}
