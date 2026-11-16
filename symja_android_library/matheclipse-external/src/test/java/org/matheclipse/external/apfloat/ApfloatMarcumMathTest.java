package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.InfiniteExpansionException;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatMarcumMath}. The expected values were calculated with mpmath, from the
 * integral which defines the function and from the Poisson series of the regularized incomplete
 * gamma function.
 */
public class ApfloatMarcumMathTest
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

    @Test
    public void testValues()
    {
        assertClose("Q_1(2, 3)", new Apfloat("0.2143620881626494569706293947232397544233"), ApfloatMarcumMath.marcumQ(q(1, 1), q(2, 1), q(3, 1)), 28);
        assertClose("Q_5/2(3, 1)", new Apfloat("0.99925307560907036252457616440780523"), ApfloatMarcumMath.marcumQ(q(5, 2), q(3, 1), q(1, 1)), 28);
        assertClose("Q_1/2(7, 1/10)", new Apfloat("0.99999999999802365748069498467249658"), ApfloatMarcumMath.marcumQ(q(1, 2), q(7, 1), q(1, 10)), 28);
        // even in a
        assertClose("Q_1/2(-7, 1/10)", new Apfloat("0.99999999999802365748069498467249658"), ApfloatMarcumMath.marcumQ(q(1, 2), q(-7, 1), q(1, 10)), 28);
        // a value far below 1: all terms of the series are positive
        assertClose("Q_1(1, 30)", new Apfloat("1.81057137784067526471145129175030514527e-184"), ApfloatMarcumMath.marcumQ(q(1, 1), q(1, 1), q(30, 1)), 28);
        // the series starts near the mode lambda = a^2/2 = 20000 of the Poisson weights
        assertClose("Q_1(200, 201)", new Apfloat("0.15925942834859220927085355585874149"), ApfloatMarcumMath.marcumQ(q(1, 1), q(200, 1), q(201, 1)), 28);
    }

    @Test
    public void testSpecialValues()
    {
        // Q_m(a, 0) = 1
        assertEquals(0, ApfloatMarcumMath.marcumQ(q(3, 2), q(5, 1), Apfloat.ZERO, PRECISION).compareTo(Apfloat.ONE));
        assertEquals(0, ApfloatMarcumMath.marcumQ(new Apint(2), new Apint(5), Apfloat.ZERO).compareTo(Apfloat.ONE));
        // Q_m(0, b) = Gamma(m, b^2/2) / Gamma(m)
        Apfloat m = q(7, 3),
                y = q(9, 8);
        assertClose("Q_7/3(0, 3/2)", ApfloatMath.gamma(m, y).divide(ApfloatMath.gamma(m)), ApfloatMarcumMath.marcumQ(m, Apfloat.ZERO, q(3, 2)), 28);
    }

    @Test
    public void testDifference()
    {
        // Q_2(3, 0) - Q_2(3, 1/3) = 1 - Q_2(3, 1/3)
        assertClose("Q_2(3, 0) - Q_2(3, 1/3)", new Apfloat("0.000017935428630699120881661099788012249"), ApfloatMarcumMath.marcumQ(q(2, 1), q(3, 1), Apfloat.ZERO, q(1, 3), PRECISION), 28);
        assertClose("Q_2(3, 1/3) - Q_2(3, 0)", new Apfloat("-0.000017935428630699120881661099788012249"), ApfloatMarcumMath.marcumQ(q(2, 1), q(3, 1), q(1, 3), Apfloat.ZERO, PRECISION), 28);
        // 12 digits cancel
        assertClose("Q_1(1/2, 0) - Q_1(1/2, 10^-6)", new Apfloat("4.4124845129220117833372589542498669e-13"), ApfloatMarcumMath.marcumQ(q(1, 1), q(1, 2), Apfloat.ZERO, q(1, 1000000), PRECISION), 28);
        assertEquals(0, ApfloatMarcumMath.marcumQ(q(2, 1), q(3, 1), q(1, 3), q(1, 3), PRECISION).signum());
    }

    @Test
    public void testLogUpperBound()
    {
        // log Q_1(1, 30) = -423.08...
        double bound = ApfloatMarcumMath.logUpperBound(1.0, 1.0, 30.0);
        assertTrue(-423.08 < bound && bound < -400.0, "log Q_1(1, 30) <= " + bound);
        // log Q_2(30, 70) = -803.33...
        bound = ApfloatMarcumMath.logUpperBound(2.0, 30.0, 70.0);
        assertTrue(-803.33 < bound && bound < -780.0, "log Q_2(30, 70) <= " + bound);
        // no bound below the mean
        assertEquals(0.0, ApfloatMarcumMath.logUpperBound(1.0, 3.0, 2.0));
        assertEquals(0.0, ApfloatMarcumMath.logUpperBound(1.0, 3.0, 0.0));
    }

    @Test
    public void testInvalidArguments()
    {
        assertThrows(ArithmeticException.class, () -> ApfloatMarcumMath.marcumQ(q(0, 1), q(2, 1), q(3, 1)));
        assertThrows(ArithmeticException.class, () -> ApfloatMarcumMath.marcumQ(q(-1, 1), q(2, 1), q(3, 1)));
        assertThrows(ArithmeticException.class, () -> ApfloatMarcumMath.marcumQ(q(1, 1), q(2, 1), q(-3, 1)));
        assertThrows(ArithmeticException.class, () -> ApfloatMarcumMath.marcumQ(q(1, 1), q(2, 1), q(1, 1), q(-3, 1), PRECISION));
        assertThrows(InfiniteExpansionException.class, () -> ApfloatMarcumMath.marcumQ(new Apint(1), new Apint(2), new Apint(3)));
        // the series would start at the mode a^2/2 = 5*10^29 of the Poisson weights
        assertThrows(ArithmeticException.class, () -> ApfloatMarcumMath.marcumQ(q(1, 1), new Apfloat("1e15", PRECISION), q(3, 1)));
    }
}
