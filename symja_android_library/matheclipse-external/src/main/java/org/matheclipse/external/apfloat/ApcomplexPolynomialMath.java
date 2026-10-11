package org.matheclipse.external.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;

/**
 * Roots of polynomials with {@link Apcomplex} coefficients.
 */

public class ApcomplexPolynomialMath
{
    // Newton's method doubles the number of correct digits with a step
    private static final int MAX_ITERATIONS = 32;

    private ApcomplexPolynomialMath()
    {
    }

    /**
     * Refines an approximation of a simple root of a polynomial with Newton's method.<p>
     *
     * The value of a polynomial next to one of its roots has as many digits cancelled as
     * the approximation has correct ones: the coefficients must have a precision of some
     * digits more than the one which is asked for.
     *
     * @param coefficients The coefficients of the polynomial, indexed by their exponent.
     * @param approximation The approximation of the root.
     * @param separation The distance of the approximation to the nearest other root: an iteration which ends farther than half of it from the approximation has found another root.
     * @param precision The precision of the result.
     *
     * @return The root.
     *
     * @throws ArithmeticException If the iteration doesn't converge, meets a zero of the derivative, or ends at another root.
     */

    public static Apcomplex root(Apcomplex[] coefficients, Apcomplex approximation, double separation, long precision)
        throws ArithmeticException, ApfloatRuntimeException
    {
        int degree = coefficients.length - 1;
        long workingPrecision = precision;
        for (Apcomplex coefficient : coefficients)
        {
            workingPrecision = Math.max(workingPrecision, coefficient.precision() == Apfloat.INFINITE ? precision : coefficient.precision());
        }
        Apcomplex seed = approximation.precision(workingPrecision),
                  x = seed;
        Apfloat tolerance = ApfloatMath.scale(new Apfloat(1, workingPrecision, approximation.radix()), -(precision + 1));
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++)
        {
            // Horner's scheme for the polynomial and its derivative
            Apcomplex p = coefficients[degree],
                      dp = Apcomplex.ZERO;
            for (int i = degree - 1; i >= 0; i--)
            {
                dp = dp.multiply(x).add(p);
                p = p.multiply(x).add(coefficients[i]);
            }
            if (dp.real().signum() == 0 && dp.imag().signum() == 0)
            {
                throw new ArithmeticException("Derivative of the polynomial is zero");
            }
            Apcomplex delta = p.divide(dp);
            x = x.subtract(delta);
            Apfloat absDelta = ApcomplexMath.abs(delta),
                    absX = ApcomplexMath.abs(x);
            if (absDelta.signum() == 0 || (absX.signum() != 0 && absDelta.compareTo(absX.multiply(tolerance)) < 0))
            {
                if (ApcomplexMath.abs(x.subtract(seed)).doubleValue() > separation / 2.0)
                {
                    throw new ArithmeticException("Newton's method left the root");
                }
                return x.precision(precision);
            }
        }
        throw new ArithmeticException("Newton's method does not converge");
    }
}
