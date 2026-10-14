package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatZetaZeroMath}. The expected values were calculated with mpmath.
 */
public class ApfloatZetaZeroMathTest
{
    private static void assertClose(String message, String expected, Apfloat actual, long digits)
    {
        Apfloat reference = new Apfloat(expected),
                difference = reference.subtract(actual);
        if (difference.signum() == 0)
        {
            return;
        }
        assertTrue(difference.scale() <= reference.scale() - digits, message + ": expected " + expected + " but was " + actual);
    }

    @Test
    public void testZ()
    {
        assertClose("Z(20)", "1.1478424121851972776350340871796641", ApfloatZetaZeroMath.z(new Apfloat(20, 40), 40), 30);
        assertClose("Z(250.5)", "-1.1758625169225618159998624705582679", ApfloatZetaZeroMath.z(new Apfloat("250.5", 40), 40), 30);
    }

    @Test
    public void testZero()
    {
        // the general zeta function
        assertClose("zero 1", "14.1347251417346937904572519835624702707842571", ApfloatZetaZeroMath.zero(14.0, 14.5, 40), 38);
        assertClose("zero 30", "101.317851005731391228785447940292308906332866", ApfloatZetaZeroMath.zero(101.0, 101.6, 40), 38);
        // the summation on the critical line
        assertClose("zero 127", "282.465114765052096233027201186501024205506836", ApfloatZetaZeroMath.zero(282.2, 282.7, 40), 38);
        assertClose("zero 1000", "1419.4224809459956864659890380799168192321006", ApfloatZetaZeroMath.zero(1419.3, 1419.6, 40), 38);
    }

    @Test
    public void testNoChangeOfSign()
    {
        assertThrows(ArithmeticException.class, () -> ApfloatZetaZeroMath.zero(15.0, 20.0, 30));
    }

    @Test
    public void testBisect()
    {
        Apfloat tolerance = new Apfloat("1e-15", 20);
        assertClose("zero 1", "14.1347251417346937904572519835624702707842571", ApfloatZetaZeroMath.bisect(new Apfloat(14, 20), new Apfloat("14.5", 20), tolerance, 20), 15);
    }
}
