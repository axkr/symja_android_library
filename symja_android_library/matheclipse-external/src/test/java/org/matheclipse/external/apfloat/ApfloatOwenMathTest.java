package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatOwenMath}. The expected values were calculated with mpmath, from the
 * integral which defines the function.
 */
public class ApfloatOwenMathTest
{
    private static final long PRECISION = 30;

    private static void assertClose(String message, Apfloat expected, Apfloat actual, long digits)
    {
        Apfloat difference = expected.subtract(actual);
        if (difference.signum() == 0)
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

    // The upper tail of the normal distribution
    private static Apfloat tail(Apfloat h, long precision)
    {
        Apfloat two = new Apfloat(2, precision);
        return ApfloatMath.erfc(h.precision(precision).divide(ApfloatMath.sqrt(two))).divide(two);
    }

    @Test
    public void testValues()
    {
        assertClose("T(1/3, 3/4)", new Apfloat("0.09599884906528637987353004080554220575924"), ApfloatOwenMath.owenT(q(1, 3), q(3, 4)), 28);
        assertClose("T(2, 1/2)", new Apfloat("0.008625077985521507131134883191546371878756"), ApfloatOwenMath.owenT(q(2, 1), q(1, 2)), 28);
        assertClose("T(1/2, 2)", new Apfloat("0.1415806036539783934666281958811154264887"), ApfloatOwenMath.owenT(q(1, 2), q(2, 1)), 28);
        // even in the first argument and odd in the second one
        assertClose("T(-7/4, -7/2)", new Apfloat("-0.02002957843029576060924978835490060322803"), ApfloatOwenMath.owenT(q(-7, 4), q(-7, 2)), 28);
        assertEquals(0, ApfloatOwenMath.owenT(q(2, 1), Apfloat.ZERO).signum());
    }

    @Test
    public void testLargeFirstArgument()
    {
        // T(h, 1) = Q(h) (1 - Q(h)) / 2, also where the value is far below the precision of the series' terms
        for (long h : new long[] { 5, 30, 100 })
        {
            Apfloat tail = tail(q(h, 1), 50),
                    expected = tail.multiply(new Apfloat(1, 50).subtract(tail)).divide(new Apfloat(2, 50));
            assertClose("T(" + h + ", 1)", expected, ApfloatOwenMath.owenT(q(h, 1), q(1, 1)), 28);
        }
        // T(h, a) + T(a h, 1/a) = (Q(h) + Q(a h)) / 2 - Q(h) Q(a h)
        Apfloat h = q(30, 1),
                a = q(5, 4),
                tailH = tail(h, 50),
                tailAH = tail(h.multiply(a), 50),
                expected = tailH.add(tailAH).divide(new Apfloat(2, 50)).subtract(tailH.multiply(tailAH));
        assertClose("T(30, 5/4) + T(75/2, 4/5)", expected,
                    ApfloatOwenMath.owenT(h, a).add(ApfloatOwenMath.owenT(h.multiply(a), q(4, 5))), 27);
        // for a large second argument the function is Q(h) / 2
        assertClose("T(40, 50)", tail(q(40, 1), 50).divide(new Apfloat(2, 50)), ApfloatOwenMath.owenT(q(40, 1), q(50, 1)), 28);
    }

    @Test
    public void testTooLargeFirstArgument()
    {
        // the series would need 10^19 extra digits, for a value below e^(-10^19)
        assertThrows(ArithmeticException.class, () -> ApfloatOwenMath.owenT(new Apfloat("1e10", PRECISION), q(1, 2)));
    }
}
