package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ZetaWorkaround}.
 */
public class ZetaWorkaroundTest
{
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

    @Test
    public void testNextToThePole()
    {
        // zeta(1 + x) = 1 / x + euler - stieltjes(1) x + ..., and stieltjes(1) x is below the precision here
        Apfloat x = new Apfloat("1e-24", 60),
                expected = new Apfloat(1, 60).divide(x).add(ApfloatMath.euler(60));
        assertClose("zeta(1 + 1e-24)", expected, ZetaWorkaround.zeta(new Apfloat(1, 60).add(x)), 44);
        expected = new Apfloat(-1, 60).divide(x).add(ApfloatMath.euler(60));
        assertClose("zeta(1 - 1e-24)", expected, ZetaWorkaround.zeta(new Apfloat(1, 60).subtract(x)), 44);
        // the same next to the pole in the imaginary direction
        Apcomplex ix = new Apcomplex(Apfloat.ZERO, x);
        assertClose("zeta(1 + 1e-24 i)", new Apfloat(1, 60).divide(ix).add(ApfloatMath.euler(60)), ZetaWorkaround.zeta(new Apfloat(1, 60).add(ix)), 44);
        assertClose("zeta(1 - 1e-24 i)", new Apfloat(-1, 60).divide(ix).add(ApfloatMath.euler(60)), ZetaWorkaround.zeta(new Apfloat(1, 60).subtract(ix)), 44);
    }

    @Test
    public void testAgreesWithApfloat()
    {
        // where apfloat itself works, at both sides of the limit of this class
        for (String value : new String[] { "1.00000000001", "0.99999999999", "1.000001", "1.5", "-2.5", "30" })
        {
            Apfloat s = new Apfloat(value, 40);
            assertClose("zeta(" + value + ")", ApcomplexMath.zeta(s), ZetaWorkaround.zeta(s), 25);
        }
    }

    @Test
    public void testPole()
    {
        assertThrows(ArithmeticException.class, () -> ZetaWorkaround.zeta(new Apfloat(1, 40)));
    }
}
