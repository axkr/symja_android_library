package org.apfloat;

import org.apfloat.spi.Util;

import static org.apfloat.ApintMath.abs;
import static org.apfloat.ApintMath.scale;

/**
 * Binary recursive GCD algorithm implementation.
 *
 * @since 1.6
 * @version 1.8.1
 * @author Mikko Tommila
 */

class GCDHelper
{
    // Simple 2x2 matrix class
    private static class Matrix
    {
        public Matrix(Apint r11, Apint r12, Apint r21, Apint r22)
        {
            this.r11 = r11;
            this.r12 = r12;
            this.r21 = r21;
            this.r22 = r22;
        }

        public Matrix multiply(Matrix a)
            throws ApfloatRuntimeException
        {
            return new Matrix(multiplyAdd(this.r11, a.r11, this.r12, a.r21),
                              multiplyAdd(this.r11, a.r12, this.r12, a.r22),
                              multiplyAdd(this.r21, a.r11, this.r22, a.r21),
                              multiplyAdd(this.r21, a.r12, this.r22, a.r22));
        }

        private static Apint multiplyAdd(Apint a, Apint b, Apint c, Apint d)
            throws ApfloatRuntimeException
        {
            return a.multiply(b).add(c.multiply(d));
        }

        public final Apint r11;
        public final Apint r12;
        public final Apint r21;
        public final Apint r22;
    }

    // Return type for the half-gcd method
    private static class HalfGcdType
    {
        public HalfGcdType(long j, Matrix r)
        {
            this.j = j;
            this.r = r;
        }

        public final long j;
        public final Matrix r;
    }

    private GCDHelper()
    {
    }

    public static Apint gcd(Apint a, Apint b)
        throws ApfloatRuntimeException
    {
        if (a.signum() == 0)
        {
            return b;
        }
        if (b.signum() == 0)
        {
            return a;
        }

        // First reduce the numbers so that they have roughly the same size, regardless of algorithm used
        if (a.scale() > b.scale())
        {
            a = a.mod(b);
        }
        else if (b.scale() > a.scale())
        {
            b = b.mod(a);
        }

        Apint gcd;
        if (Math.max(a.scale(), b.scale()) * Math.log(Math.max(a.radix(), b.radix())) < 80000)
        {
            // Small number, use the O(n^2) simple algorithm
            gcd = elementaryGcd(a, b);
        }
        else
        {
            // Big number, use the O(n log n) divide-and-conquer algorithm
            gcd = recursiveGcd(a, b);
        }

        return gcd;
    }

    private static Apint elementaryGcd(Apint a, Apint b)
        throws ApfloatRuntimeException
    {
        while (b.signum() != 0)
        {
            Apint r = a.mod(b);
            a = b;
            b = r;
        }

        return abs(a);
    }

    private static Apint recursiveGcd(Apint a, Apint b)
        throws ApfloatRuntimeException
    {
        if (a.radix() != 2 || b.radix() != 2)
        {
            // This algorithm only works with binary numbers; convert to radix 2 and then back to original radix
            return recursiveGcd(a.toRadix(2), b.toRadix(2)).toRadix(a.radix());
        }

        // First count the trailing zero bits of each number - the power of two factor in the gcd
        long zeros = Math.min(v(a), v(b));

        // Then remove the trailing zeros (it doesn't matter if one number has more zeros than the other), and add one zero to b
        // The algorithm only works if a has no trailing zeros, and b has at least one
        a = scale(a, -v(a));
        b = scale(b, 1 - v(b));

        // Call the recursive algorithm to compute the odd part of the gcd; initial k is the bit length of the numbers
        long k = Math.max(a.scale(), b.scale());
        HalfGcdType t = halfBinaryGcd(a, b, k);
        long j = t.j;
        Matrix result = t.r;

        // As the output of the recursive algorithm, we get two terms of the remainder sequence (like in the elementary algorithm)
        Apint c = scale(result.r11.multiply(a).add(result.r12.multiply(b)), -2 * j),
              d = scale(result.r21.multiply(a).add(result.r22.multiply(b)), -2 * j);

        // We have to check if these terms are the *last* terms of the remainder sequence
        Apint gcd;
        if (d.signum() == 0)
        {
            // If d = 0 then c is the odd part of the gcd: c and d are the last terms of the remainder sequence.
            gcd = c;
        }
        else
        {
            // However, with large numbers, the initial k argument for the recursive algorithm isn't many times sufficient,
            // and c and d are not actually the last terms of the remainder sequence. So we continue computing the remainder
            // sequence, until we reach the last terms, to find the gcd (odd part).
            // The numbers remaining in the sequence are small, O(log n), compared to the original input numbers, so the elementary
            // algorithm is sufficient for all practical purposes.
            gcd = elementaryGcd(c, d);
        }

        // Finally scale the odd part of the gcd by the number of trailing zeros in the original numbers
        return abs(scale(gcd, zeros));
    }

    // Based on the "Recursive Binary GCD Algorithm" by Damien Stehlé and Paul Zimmermann.
    // Adapted from the algorithm presented in "Modern Computer Arithmetic" v. 0.5.9 by Richard P. Brent and Paul Zimmermann.
    private static HalfGcdType halfBinaryGcd(Apint a, Apint b, long k)
        throws ApfloatRuntimeException
    {
        assert (v(a) < v(b));

        Apint one = new Apint(1, 2);
        if (v(b) > k)
        {
            return new HalfGcdType(0, new Matrix(one, Apint.ZERO, Apint.ZERO, one));
        }
        long k1 = k >> 1;
        Apint a1 = a.mod(powerOfTwo(2 * k1 + 1)),
              b1 = b.mod(powerOfTwo(2 * k1 + 1));

        HalfGcdType t1 = halfBinaryGcd(a1, b1, k1);
        long j1 = t1.j;

        Apint ac = scale(t1.r.r11.multiply(a).add(t1.r.r12.multiply(b)), -2 * j1),
              bc = scale(t1.r.r21.multiply(a).add(t1.r.r22.multiply(b)), -2 * j1);
        long j0 = v(bc);

        if (Util.ifFinite(j0, j0 + j1) > k)
        {
            return t1;
        }
        Apint[] qr = binaryDivide(ac, bc);
        Apint q = qr[0],
              r = qr[1];
        long k2 = k - (j0 + j1);
        Apint a2 = scale(bc, -j0).mod(powerOfTwo(2 * k2 + 1)),
              b2 = scale(r, -j0).mod(powerOfTwo(2 * k2 + 1));

        HalfGcdType t2 = halfBinaryGcd(a2, b2, k2);
        long j2 = t2.j;

        Matrix qm = new Matrix(Apint.ZERO, powerOfTwo(j0), powerOfTwo(j0), q),
               result = t2.r.multiply(qm).multiply(t1.r);
        long j = j1 + j0 + j2;

        return new HalfGcdType(j, result);
    }

    // The fast "generalized binary division" algorithm.
    // This is another quite strange algorithm, producing a "quotient" and "remainder"
    // but not like in a normal division algorithm. Instead of removing the high-order
    // bits (like in normal division) this algorithm removes the lowest-order bits.
    // It kind of makes sense if you consider the numbers as p-adic numbers.
    private static Apint[] binaryDivide(Apint a, Apint b)
        throws ApfloatRuntimeException
    {
        assert (a.signum() != 0);
        assert (b.signum() != 0);
        assert (v(a) < v(b));

        Apint A = scale(a, -v(a)).negate(),
              B = scale(b, -v(b)),
              one = new Apint(1, 2),
              q = one;
        long n = v(b) - v(a) + 1;
        int maxN = Util.log2up(n);
        for (int i = 1; i <= maxN; i++)
        {
            q = q.add(q.multiply(one.subtract(B.multiply(q)))).mod(powerOfTwo(1L << i));
        }

        q = cmod(A.multiply(q), powerOfTwo(n));
        Apint r = q.multiply(b).divide(powerOfTwo(n - 1)).add(a);

        return new Apint[] { q, r };
    }

    // The p-adic valuation of the number i.e. the number of trailing zero bits (for 2-adic numbers)
    private static long v(Apint a)
        throws ApfloatRuntimeException
    {
        if (a.signum() == 0)
        {
            return Apfloat.INFINITE;
        }
        return a.scale() - a.size();
    }

    // Returns 2^n
    private static Apint powerOfTwo(long n)
        throws ApfloatRuntimeException
    {
        assert (n >= 0);
        return scale(new Apint(1, 2), n);
    }

    // Centered modulus i.e. modulus but scaled so that the result is -2/m < r <= 2/m
    private static Apint cmod(Apint a, Apint m)
        throws ApfloatRuntimeException
    {
        a = a.mod(m);
        Apint halfM = scale(m, -1);
        a = (a.compareTo(halfM) > 0 ? a.subtract(m) : a);
        a = (a.compareTo(halfM.negate()) <= 0 ? a.add(m) : a);
        return a;
    }
}
