package org.apfloat.internal;

/**
 * Functions to perform bit-reverse ordering of <code>double</code> data.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public class DoubleScramble
{
    private DoubleScramble()
    {
    }

    /**
     * Permute the data in the table to bit-reversed order.<p>
     *
     * The permutation table argument should contain pairs of indexes
     * that indicate array elements whose contents are swapped.
     *
     * @param data The array to permute.
     * @param offset The offset within the array to permute.
     * @param permutationTable Table of indexes indicating, which elements in the <code>data</code> are to be swapped.
     */

    public static void scramble(double[] data, int offset, int[] permutationTable)
    {
        for (int k = 0; k < permutationTable.length; k += 2)
        {
            int i = offset + permutationTable[k],
                j = offset + permutationTable[k + 1];
            double tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }
    }
}
