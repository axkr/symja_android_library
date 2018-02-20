package org.apfloat.internal;

/**
 * Modulo arithmetic functions for <code>int</code> data.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public class IntModMath
    extends IntElementaryModMath
{
    /**
     * Default constructor.
     */

    public IntModMath()
    {
    }

    /**
     * Create a table of powers of n:th root of unity.
     *
     * @param w The n:th root of unity modulo the current modulus.
     * @param n The table length (= transform length).
     *
     * @return Table of <code>table[i]=w<sup>i</sup> mod m</code>, i = 0, ..., n-1.
     */

    public final int[] createWTable(int w, int n)
    {
        int[] wTable = new int[n];
        int wTemp = 1;

        for (int i = 0; i < n; i++)
        {
            wTable[i] = wTemp;
            wTemp = modMultiply(wTemp, w);
        }

        return wTable;
    }

    /**
     * Get forward n:th root of unity. This is <code>w</code>.<p>
     *
     * Assumes that the modulus is prime.
     *
     * @param primitiveRoot Primitive root of the modulus.
     * @param n The transform length.
     *
     * @return Forward n:th root of unity.
     */

    public int getForwardNthRoot(int primitiveRoot, long n)
    {
        return modPow(primitiveRoot, getModulus() - 1 - (getModulus() - 1) / (int) n);
    }

    /**
     * Get inverse n:th root of unity. This is <code>w<sup>-1</sup></code>.<p>
     *
     * Assumes that the modulus is prime.
     *
     * @param primitiveRoot Primitive root of the modulus.
     * @param n The transform length.
     *
     * @return Inverse n:th root of unity.
     */

    public int getInverseNthRoot(int primitiveRoot, long n)
    {
        return modPow(primitiveRoot, (getModulus() - 1) / (int) n);
    }

    /**
     * Modular inverse, that is <code>1 / a</code>. Assumes that the modulus is prime.
     *
     * @param a The operand.
     *
     * @return <code>a<sup>-1</sup> mod m</code>.
     */

    public final int modInverse(int a)
    {
        return modPow(a, getModulus() - 2);
    }

    /**
     * Modular division. Assumes that the modulus is prime.
     *
     * @param a The dividend.
     * @param b The divisor.
     *
     * @return <code>a*b<sup>-1</sup> mod m</code>.
     */

    public final int modDivide(int a, int b)
    {
        return modMultiply(a, modInverse(b));
    }

    /**
     * Modular negation.
     *
     * @param a The argument.
     *
     * @return <code>-a mod m</code>.
     */

    public final int negate(int a)
    {
        return (a == 0 ? 0 : getModulus() - a);
    }

    /**
     * Modular power. Assumes that the modulus is prime.
     *
     * @param a The base.
     * @param n The exponent.
     *
     * @return <code>a<sup>n</sup> mod m</code>.
     */

    public final int modPow(int a, int n)
    {
        assert (a != 0 || n != 0);

        if (n == 0)
        {
            return 1;
        }
        else if (n < 0)
        {
            return modPow(a, getModulus() - 1 + n);
        }

        long exponent = (long) n;

        while ((exponent & 1) == 0)
        {
            a = modMultiply(a, a);
            exponent >>= 1;
        }

        int r = a;

        while ((exponent >>= 1) > 0)
        {
            a = modMultiply(a, a);
            if ((exponent & 1) != 0)
            {
                r = modMultiply(r, a);
            }
        }

        return r;
    }
}
