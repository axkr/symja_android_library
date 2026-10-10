package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.Aprational;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexAppellMath}.
 */
public class ApcomplexAppellMathTest
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

    private static Apcomplex f1(Apcomplex a, Apcomplex b1, Apcomplex b2, Apcomplex c, Apcomplex x, Apcomplex y)
    {
        return ApcomplexAppellMath.appellF1(a, b1, b2, c, x, y);
    }

    @Test
    public void testInsideOfTheUnitCircle()
    {
        assertClose("F1(1; 2, 3; 4; 1/10, 1/5)", new Apfloat("1.24826732560011266612992493938925252865294134"),
                    f1(q(1, 1), q(2, 1), q(3, 1), q(4, 1), q(1, 10), q(1, 5)), 38);
        // the series terminates
        assertClose("F1(-2; 13/10, 2/5; 29/10; 1/2, 1/4)", new Apfloat("0.563439434129089301503094606542882404951370469"),
                    f1(q(-2, 1), q(13, 10), q(2, 5), q(29, 10), q(1, 2), q(1, 4)), 38);
    }

    @Test
    public void testTransformed()
    {
        assertClose("F1(1/3; 1/2, 1/2; 4/3; -2, 1/7)", new Apfloat("0.867437951174926371870080714148524793152754954"),
                    f1(q(1, 3), q(1, 2), q(1, 2), q(4, 3), q(-2, 1), q(1, 7)), 37);
        assertClose("F1(7/10; 13/10, 2/5; 29/10; -7/2, -1/2)", new Apfloat("0.54390050649792865339031327368956718307940994"),
                    f1(q(7, 10), q(13, 10), q(2, 5), q(29, 10), q(-7, 2), q(-1, 2)), 37);
        // both arguments next to 1
        assertClose("F1(7/10; 13/10, 2/5; 29/10; 19/20, 97/100)", new Apfloat("2.3056577055598635822526770828022099696652368"),
                    f1(q(7, 10), q(13, 10), q(2, 5), q(29, 10), q(19, 20), q(97, 100)), 36);
    }

    @Test
    public void testComplex()
    {
        Apcomplex expected = new Apcomplex(new Apfloat("1.05285042317427156861046374801316593134511693"),
                                           new Apfloat("0.070649692147503777252044001665247453857302619"));
        assertClose("F1(7/10; 13/10, 2/5; 29/10; 1/5 + 3/10 i, -2/5 i)", expected,
                    f1(q(7, 10), q(13, 10), q(2, 5), q(29, 10), new Apcomplex(q(1, 5), q(3, 10)), new Apcomplex(Apfloat.ZERO, q(-2, 5))), 37);
    }

    @Test
    public void testSymmetry()
    {
        Apcomplex x = q(1, 5),
                  y = q(-3, 1);
        assertClose("F1(a; b1, b2; c; x, y) = F1(a; b2, b1; c; y, x)",
                    f1(q(1, 3), q(1, 2), q(3, 4), q(4, 3), x, y),
                    f1(q(1, 3), q(3, 4), q(1, 2), q(4, 3), y, x), 37);
    }

    @Test
    public void testNotCovered()
    {
        // on the branch cut
        assertThrows(ArithmeticException.class, () -> f1(q(7, 10), q(13, 10), q(2, 5), q(29, 10), q(3, 2), q(1, 5)));
        assertThrows(ArithmeticException.class, () -> f1(q(7, 10), q(13, 10), q(2, 5), q(29, 10), q(1, 5), q(3, 1)));
    }
}
