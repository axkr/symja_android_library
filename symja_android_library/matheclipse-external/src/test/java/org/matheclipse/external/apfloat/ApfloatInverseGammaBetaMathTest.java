package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatInverseGammaBetaMath}. The expected values were checked with mpmath, by
 * the incomplete function at the result.
 */
public class ApfloatInverseGammaBetaMathTest
{
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

    // The rational number p/q with the precision
    private static Apfloat q(long p, long q, long precision)
    {
        return new Aprational(new Apint(p), new Apint(q)).precision(precision);
    }

    private static Apfloat inverseBeta(Apfloat p, Apfloat a, Apfloat b, long precision)
    {
        return ApfloatInverseGammaBetaMath.inverseBetaRegularized(p, a, b, precision, Double.NaN);
    }

    @Test
    public void testInverseBeta()
    {
        assertClose("I^-1(1/3; 5/2, 7/3)", new Apfloat("0.417296231056748248499417643992625173868"),
                    inverseBeta(q(1, 3, 40), q(5, 2, 40), q(7, 3, 40), 40), 38);
        // a value next to 1: the other tail keeps the digits
        Apfloat one = new Apfloat(1, 100),
                tiny = ApfloatMath.scale(one, -40);
        assertClose("I^-1(1 - 10^-40; 2, 2)", new Apfloat("0.99999999999999999999422649730810374235489740108386931433241"),
                    inverseBeta(one.subtract(tiny), q(2, 1, 100), q(2, 1, 100), 60), 58);
        // a value below the range of a double
        assertClose("I^-1(10^-400; 3, 1/2)", ApfloatMath.scale(one, -400),
                    regularizedBeta(inverseBeta(ApfloatMath.scale(one, -400).precision(40), q(3, 1, 40), q(1, 2, 40), 40), q(3, 1, 80), q(1, 2, 80)), 37);
        assertEquals(0, inverseBeta(Apfloat.ZERO, q(3, 1, 40), q(1, 2, 40), 40).signum());
        assertThrows(ArithmeticException.class, () -> inverseBeta(q(3, 2, 40), q(3, 1, 40), q(1, 2, 40), 40));
    }

    @Tag("slow")
    @Test
    public void testInverseBetaLargeParameters()
    {
        // large parameters: the incomplete beta function loses hundreds of digits
        Apfloat x = inverseBeta(q(3, 10, 200), q(1000, 1, 200), q(1000, 1, 200), 200);
        assertClose("I^-1(3/10; 1000, 1000)", new Apfloat("0.49413649272798149220515016490785219188483805966490330783227134015735493503517829663231152354179"), x, 95);
        Apfloat a = new Apfloat(1000, 700),
                p = ApfloatMath.beta(x.precision(700), a, a).divide(ApfloatMath.beta(a, a));
        assertClose("I(x; 1000, 1000)", q(3, 10, 200), p, 195);
    }

    private static Apfloat regularizedBeta(Apfloat x, Apfloat a, Apfloat b)
    {
        x = x.precision(Math.max(x.precision(), a.precision()));
        return ApfloatMath.beta(x, a, b).divide(ApfloatMath.beta(a, b));
    }

    @Test
    public void testInverseGamma()
    {
        assertClose("Q^-1(5/2, 1/3)", new Apfloat("2.865242596071248371845701106924432254215"),
                    ApfloatInverseGammaBetaMath.inverseGammaRegularized(q(5, 2, 40), q(1, 3, 40), true, 40, Double.NaN), 38);
        // a result outside of the range where a double can start the iteration
        Apfloat tiny = ApfloatMath.scale(new Apfloat(1, 30), -400);
        assertClose("Q^-1(2, 10^-400)", new Apfloat("927.868003842206103591316736158"),
                    ApfloatInverseGammaBetaMath.inverseGammaRegularized(q(2, 1, 30), tiny, true, 30, Double.NaN), 28);
        // the upper function next to 1 is the lower function next to 0
        Apfloat one = new Apfloat(1, 100);
        assertClose("Q^-1(1/2, 1 - 10^-30)", new Apfloat("7.8539816339744830961566084581987572104929234984378e-61"),
                    ApfloatInverseGammaBetaMath.inverseGammaRegularized(q(1, 2, 100), one.subtract(ApfloatMath.scale(one, -30)), true, 50, Double.NaN), 48);
        assertClose("P^-1(1/2, 10^-30)", new Apfloat("7.8539816339744830961566084581987572104929234984378e-61"),
                    ApfloatInverseGammaBetaMath.inverseGammaRegularized(q(1, 2, 100), ApfloatMath.scale(one, -30), false, 50, Double.NaN), 48);
        // a seed far from the result doesn't change it
        assertClose("Q^-1(300, 1/7)", new Apfloat("318.527415614868827972074126385552847708680615452861971874436"),
                    ApfloatInverseGammaBetaMath.inverseGammaRegularized(q(300, 1, 60), q(1, 7, 60), true, 60, 5.0), 58);
        assertThrows(ArithmeticException.class, () -> ApfloatInverseGammaBetaMath.inverseGammaRegularized(q(2, 1, 30), Apfloat.ZERO, true, 30, Double.NaN));
    }

    @Test
    public void testGeneralized()
    {
        assertClose("I^-1(1/10, 1/3; 9/10, 2)", new Apfloat("0.307180638435827269409397025587"),
                    ApfloatInverseGammaBetaMath.inverseBetaRegularized(q(1, 10, 30), q(1, 3, 30), q(9, 10, 30), q(2, 1, 30), 30), 28);
        assertClose("Q^-1(3; 1/2, 1/5)", new Apfloat("1.59126906023400852597466320528"),
                    ApfloatInverseGammaBetaMath.inverseGammaRegularized(q(3, 1, 30), q(1, 2, 30), q(1, 5, 30), 30), 28);
    }
}
