package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatDoubleExponential}.
 */
public class ApfloatDoubleExponentialTest
{
    private static final long PRECISION = 30;

    private static void assertClose(String message, Apcomplex expected, Apcomplex actual, long digits)
    {
        Apcomplex difference = expected.subtract(actual);
        if (difference.real().signum() == 0 && difference.imag().signum() == 0)
        {
            return;
        }
        assertTrue(difference.scale() <= expected.scale() - digits, message + ": expected " + expected + " but was " + actual);
    }

    private static Apfloat f(long value)
    {
        return new Apfloat(value, PRECISION);
    }

    // An exact end of the interval
    private static Apfloat end(long value)
    {
        return new Apfloat(value);
    }

    @Test
    public void testFiniteInterval()
        throws ApfloatDoubleExponential.Failure
    {
        // the derivative is singular at 0
        Apcomplex integral = ApfloatDoubleExponential.integrate(x -> ApfloatMath.sqrt(x), end(0), end(1), PRECISION);
        assertClose("Integrate(Sqrt(x), {x, 0, 1})", f(2).divide(f(3)), integral, PRECISION - 1);
        assertEquals(PRECISION, integral.precision(), "precision of the result");
        // an integrable singularity at 0, which is never sampled
        assertClose("Integrate(Log(x), {x, 0, 1})", f(-1), ApfloatDoubleExponential.integrate(x -> ApfloatMath.log(x), end(0), end(1), PRECISION), PRECISION - 1);
        // an end away from zero: the abscissas next to it carry more digits than the working precision, and the integrand must keep them
        assertClose("Integrate(1/Sqrt(3-x), {x, 2, 3})", f(2), ApfloatDoubleExponential.integrate(x -> ApfloatMath.inverseRoot(new Apfloat(3).subtract(x), 2), end(2), end(3), PRECISION), PRECISION - 1);
        // a complex integrand; an inexact end carries more digits than the result
        Apcomplex i = new Apcomplex(f(0), f(1));
        assertClose("Integrate(Exp(I x), {x, 0, Pi})", new Apcomplex(f(0), f(2)), ApfloatDoubleExponential.integrate(x -> ApcomplexMath.exp(i.multiply(x)), end(0), ApfloatMath.pi(PRECISION + 15), PRECISION), PRECISION - 1);
    }

    @Test
    public void testInfiniteIntervals()
        throws ApfloatDoubleExponential.Failure
    {
        assertClose("Integrate(Exp(-x), {x, 0, Infinity})", f(1), ApfloatDoubleExponential.integrate(x -> ApfloatMath.exp(x.negate()), end(0), null, PRECISION), PRECISION - 1);
        assertClose("Integrate(Exp(x), {x, -Infinity, -1})", ApfloatMath.exp(f(-1)), ApfloatDoubleExponential.integrate(x -> ApfloatMath.exp(x), null, end(-1), PRECISION), PRECISION - 1);
        assertClose("Integrate(Exp(-x^2), {x, -Infinity, Infinity})", ApfloatMath.sqrt(ApfloatMath.pi(PRECISION)), ApfloatDoubleExponential.integrate(x -> ApfloatMath.exp(x.multiply(x).negate()), null, null, PRECISION), PRECISION - 1);
        // the abscissa 0 of the whole line is an exact zero, which has no precision: the constant of the integrand carries it, or Cos(1) is asked to infinite precision. Sqrt(Pi) E^(-1/4) Cos(1)
        Apfloat one = new Apfloat(1, PRECISION + 15),
                quarter = new Apfloat("0.25", PRECISION + 15),
                expected = ApfloatMath.sqrt(ApfloatMath.pi(PRECISION + 15)).multiply(ApfloatMath.exp(quarter.negate())).multiply(ApfloatMath.cos(one));
        assertClose("Integrate(Exp(-x^2) Cos(x+1), {x, -Infinity, Infinity})", expected, ApfloatDoubleExponential.integrate(x -> ApfloatMath.exp(x.multiply(x).negate()).multiply(ApfloatMath.cos(x.add(one))), null, null, PRECISION), PRECISION - 1);
    }

    @Test
    public void testFailure()
    {
        // an end with the digits of the result only: the estimates could never agree
        assertThrows(IllegalArgumentException.class, () -> ApfloatDoubleExponential.integrate(x -> x.multiply(x), end(0), ApfloatMath.pi(PRECISION), PRECISION));
        // a sample without a numerical value
        assertThrows(ApfloatDoubleExponential.Failure.class, () -> ApfloatDoubleExponential.integrate(x -> x.compareTo(new Apfloat("0.5")) > 0 ? null : x, end(0), end(1), PRECISION));
    }
}
