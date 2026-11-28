package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexMittagLefflerMath}, against the elementary functions the Mittag-Leffler
 * function reduces to.
 */
public class ApcomplexMittagLefflerMathTest
{
    private static final long PRECISION = 40;

    private static void assertClose(String message, Apcomplex expected, Apcomplex actual, long digits)
    {
        Apcomplex difference = expected.subtract(actual);
        if (difference.real().signum() == 0 && difference.imag().signum() == 0)
        {
            return;
        }
        long scale = Math.max(expected.scale(), actual.scale());
        assertTrue(difference.scale() <= scale - digits, message + ": expected " + expected + " but was " + actual);
    }

    // The rational number p/q with the precision of the tests
    private static Apfloat q(long p, long q)
    {
        return new Aprational(new org.apfloat.Apint(p), new org.apfloat.Apint(q)).precision(PRECISION);
    }

    @Test
    public void testExponential()
    {
        Apfloat one = q(1, 1);
        assertClose("E(1, 1, 7/3)", ApcomplexMath.exp(q(7, 3)), ApcomplexMittagLefflerMath.mittagLefflerE(one, one, q(7, 3)), 38);
        // the terms of the series grow to about e^30 before they cancel to e^-30
        assertClose("E(1, 1, -30)", ApcomplexMath.exp(q(-30, 1)), ApcomplexMittagLefflerMath.mittagLefflerE(one, one, q(-30, 1)), 38);
        Apcomplex z = new Apcomplex(q(1, 2), q(-3, 2));
        assertClose("E(1, 1, 1/2 - 3/2 I)", ApcomplexMath.exp(z), ApcomplexMittagLefflerMath.mittagLefflerE(one, one, z), 38);
    }

    @Test
    public void testHyperbolicAndTrigonometric()
    {
        Apfloat one = q(1, 1), two = q(2, 1);
        assertClose("E(2, 1, 9/4)", ApcomplexMath.cosh(q(3, 2)), ApcomplexMittagLefflerMath.mittagLefflerE(two, one, q(9, 4)), 38);
        assertClose("E(2, 1, -25)", ApcomplexMath.cos(q(5, 1)), ApcomplexMittagLefflerMath.mittagLefflerE(two, one, q(-25, 1)), 38);
    }

    @Test
    public void testSecondParameter()
    {
        Apfloat one = q(1, 1), two = q(2, 1), z = q(-5, 4);
        // E(1, 2, z) = (e^z - 1)/z
        Apcomplex expected = ApcomplexMath.exp(z).subtract(one).divide(z);
        assertClose("E(1, 2, -5/4)", expected, ApcomplexMittagLefflerMath.mittagLefflerE(one, two, z), 38);
        // E(a, b, 0) = 1/Gamma(b)
        assertClose("E(3/2, 5/2, 0)", one.divide(ApcomplexMath.gamma(q(5, 2))),
                    ApcomplexMittagLefflerMath.mittagLefflerE(q(3, 2), q(5, 2), q(0, 1)), 38);
    }

    @Test
    public void testRefused()
    {
        assertThrows(ArithmeticException.class, () -> ApcomplexMittagLefflerMath.mittagLefflerE(q(-1, 2), q(1, 1), q(1, 1)));
        // the cancellation needs more than the largest number of extra digits
        assertThrows(ArithmeticException.class, () -> ApcomplexMittagLefflerMath.mittagLefflerE(q(1, 2), q(1, 1), q(-100, 1)));
    }
}
