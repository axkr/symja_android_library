package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexBetaMath}. The expected values were calculated with mpmath.
 */
public class ApcomplexBetaMathTest
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
        return new Aprational(new Apint(p), new Apint(q)).precision(PRECISION);
    }

    @Test
    public void testPochhammerOfALargeArgument()
    {
        // the gamma functions of the quotient are beyond the range of the exponent
        assertClose("(10^100)_(-7/3)", new Apfloat("4.64158883361277889241007635091944657655134913e-234"),
                    ApcomplexBetaMath.pochhammer(new Apfloat("1e100", PRECISION), q(-7, 3)), PRECISION - 2);
        assertClose("(10^20)_(5/2)", new Apfloat("1.00000000000000000001875000000000000000005078e50"),
                    ApcomplexBetaMath.pochhammer(new Apfloat("1e20", PRECISION), q(5, 2)), PRECISION - 2);
        // they have 15 digits less than the argument
        assertClose("(10^15)_(-3)", new Apfloat("1.00000000000000600000000000002500000000000009e-45"),
                    ApcomplexBetaMath.pochhammer(new Apfloat("1e15", PRECISION), q(-3, 1)), PRECISION - 2);
        assertClose("(123456/10 + 2 i)_(1/3 + i)",
                    new Apcomplex(new Apfloat("-23.1069488388865353361424894515581626977258912"),
                                  new Apfloat("0.085090217644434769330077856250009551800807904")),
                    ApcomplexBetaMath.pochhammer(new Apcomplex(q(123456, 10), q(2, 1)), new Apcomplex(q(1, 3), q(1, 1))), PRECISION - 2);
        // a small argument: the function of the library
        assertClose("(7/2)_(1/3)", ApcomplexMath.pochhammer(q(7, 2), q(1, 3)), ApcomplexBetaMath.pochhammer(q(7, 2), q(1, 3)), PRECISION - 2);
    }
}
