package org.matheclipse.external.apfloat;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;

/**
 * The zeros of the Airy functions Ai(<i>x</i>) and Bi(<i>x</i>), which lie on the negative
 * real axis.<p>
 *
 * A zero is started from its asymptotic approximation and refined with Newton's method,
 * which doubles the working precision with every step.
 *
 * @see <a href="https://dlmf.nist.gov/9.9">DLMF 9.9</a>
 */

public class ApfloatAiryMath
{
    private ApfloatAiryMath()
    {
    }

    /**
     * The <i>k</i>-th zero of the Airy function Ai(<i>x</i>).
     *
     * @param k The index of the zero, starting from 1.
     * @param precision The precision of the result.
     *
     * @return The zero <i>a</i><sub><i>k</i></sub>.
     *
     * @throws IllegalArgumentException If <i>k</i> is less than 1.
     *
     * @see <a href="https://dlmf.nist.gov/9.9#E6">DLMF 9.9.6</a>
     */

    public static Apfloat airyAiZero(long k, long precision)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        checkIndex(k);
        // a(k) = -T(3/8 pi (4k - 1))
        return newton(false, -asymptoticT(0.375 * Math.PI * (4.0 * k - 1.0)), precision);
    }

    /**
     * The <i>k</i>-th zero of the Airy function Bi(<i>x</i>).
     *
     * @param k The index of the zero, starting from 1.
     * @param precision The precision of the result.
     *
     * @return The zero <i>b</i><sub><i>k</i></sub>.
     *
     * @throws IllegalArgumentException If <i>k</i> is less than 1.
     *
     * @see <a href="https://dlmf.nist.gov/9.9#E10">DLMF 9.9.10</a>
     */

    public static Apfloat airyBiZero(long k, long precision)
        throws IllegalArgumentException, ApfloatRuntimeException
    {
        checkIndex(k);
        // b(k) = -T(3/8 pi (4k - 3))
        return newton(true, -asymptoticT(0.375 * Math.PI * (4.0 * k - 3.0)), precision);
    }

    private static void checkIndex(long k)
    {
        if (k < 1)
        {
            throw new IllegalArgumentException("Positive index of the zero expected instead of " + k);
        }
    }

    // The asymptotic approximation T(t), see https://dlmf.nist.gov/9.9#E18
    private static double asymptoticT(double t)
    {
        double t2 = 1.0 / (t * t);
        return Math.pow(t, 2.0 / 3.0) * (1.0 + t2 * (5.0 / 48.0 + t2 * (-5.0 / 36.0 + t2 * (77125.0 / 82944.0 + t2 * (-108056875.0 / 6967296.0)))));
    }

    private static Apfloat newton(boolean bi, double start, long precision)
    {
        long workingPrecision = 20;
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(workingPrecision);
        Apfloat x = new Apfloat(start, workingPrecision),
                epsilon = new Apfloat(1.0e-17, workingPrecision);
        for (int i = 0; i < 50; i++)
        {
            Apfloat delta = step(h, bi, x);
            x = h.subtract(x, delta);
            if (h.abs(delta).compareTo(h.multiply(epsilon, h.abs(x))) <= 0)
            {
                break;
            }
        }
        // The number of correct digits is doubled in every Newton step
        long guardedPrecision = precision + 5;
        while (workingPrecision < guardedPrecision)
        {
            workingPrecision = Math.min(2 * workingPrecision - 4, guardedPrecision);
            h = new FixedPrecisionApfloatHelper(workingPrecision);
            x = x.precision(workingPrecision);
            x = h.subtract(x, step(h, bi, x));
        }
        if (precision > 20)
        {
            // One more step for safety
            x = h.subtract(x, step(h, bi, x));
        }
        return x.precision(precision);
    }

    private static Apfloat step(FixedPrecisionApfloatHelper h, boolean bi, Apfloat x)
    {
        return (bi ? h.divide(h.airyBi(x), h.airyBiPrime(x)) : h.divide(h.airyAi(x), h.airyAiPrime(x)));
    }
}
