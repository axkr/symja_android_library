package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.InfiniteExpansionException;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatInverseTrigMath}.
 */
public class ApfloatInverseTrigMathTest
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
    public void testZero()
    {
        // a zero has infinite precision, the precision of pi / 2 is the given one
        Apfloat halfPi = ApfloatMath.pi(40).divide(new Apfloat(2));
        assertEquals(halfPi, ApfloatInverseTrigMath.acot(Apfloat.ZERO, 40));
        assertEquals(halfPi, ApfloatInverseTrigMath.acot(Apcomplex.ZERO, 40));
        assertEquals(halfPi, ApfloatInverseTrigMath.acot(new Apcomplex(new Apfloat("0.0", 40), Apfloat.ZERO), 40));
        assertThrows(InfiniteExpansionException.class, () -> ApfloatInverseTrigMath.acot(Apfloat.ZERO));
        assertThrows(InfiniteExpansionException.class, () -> ApfloatInverseTrigMath.acot(Apcomplex.ZERO));
    }

    @Test
    public void testReal()
    {
        // acot(1) = pi / 4, and the function is odd with a jump at zero
        Apfloat quarterPi = ApfloatMath.pi(40).divide(new Apfloat(4)),
                halfPi = ApfloatMath.pi(40).divide(new Apfloat(2)),
                small = new Apfloat("1e-50", 40);
        assertClose("acot(1)", quarterPi, ApfloatInverseTrigMath.acot(new Apfloat(1, 40)), 38);
        assertClose("acot(-1)", quarterPi.negate(), ApfloatInverseTrigMath.acot(new Apfloat(-1, 40)), 38);
        assertClose("acot(1e-50)", halfPi, ApfloatInverseTrigMath.acot(small), 38);
        assertClose("acot(-1e-50)", halfPi.negate(), ApfloatInverseTrigMath.acot(small.negate()), 38);
        // cot(acot(x)) = x
        Apfloat x = new Apfloat("-0.3", 40),
                y = ApfloatInverseTrigMath.acot(x);
        assertClose("cot(acot(-0.3))", x, ApfloatMath.cos(y).divide(ApfloatMath.sin(y)), 36);
    }

    @Test
    public void testComplex()
    {
        // acot(z) = atan(1 / z)
        Apcomplex z = new Apcomplex(new Apfloat("0.5", 40), new Apfloat("0.25", 40));
        assertClose("acot(0.5 + 0.25 i)", ApcomplexMath.atan(new Apfloat(1, 40).divide(z)), ApfloatInverseTrigMath.acot(z), 36);
        z = new Apcomplex(new Apfloat("-3", 40), new Apfloat("-7", 40));
        assertClose("acot(-3 - 7 i)", ApcomplexMath.atan(new Apfloat(1, 40).divide(z)), ApfloatInverseTrigMath.acot(z), 36);
        // acot(2 i) = -i log(3) / 2
        Apcomplex expected = new Apcomplex(Apfloat.ZERO, ApfloatMath.log(new Apfloat(3, 40)).divide(new Apfloat(-2)));
        assertClose("acot(2 i)", expected, ApfloatInverseTrigMath.acot(new Apcomplex(Apfloat.ZERO, new Apfloat(2, 40))), 36);
        // a real argument gives the real value
        assertEquals(ApfloatInverseTrigMath.acot(new Apfloat("0.3", 40)), ApfloatInverseTrigMath.acot(new Apcomplex(new Apfloat("0.3", 40))));
    }
}
