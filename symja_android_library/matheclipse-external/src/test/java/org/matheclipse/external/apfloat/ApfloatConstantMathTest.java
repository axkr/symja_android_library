package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.InfiniteExpansionException;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatConstantMath} and of the series of {@link ApcomplexZetaMath} for an
 * integer argument or order. The expected values are those of the library's own algorithms.
 */
public class ApfloatConstantMathTest
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
    public void testCatalan()
    {
        for (long precision : new long[] { 1, 16, 63, 211 })
        {
            Apfloat catalan = ApfloatConstantMath.catalan(precision, 10);
            assertEquals(precision, catalan.precision());
            assertClose("G to " + precision, ApfloatMath.catalan(precision), catalan, precision - 1);
        }
        // a value from the cache, and another radix
        assertClose("G to 37", ApfloatMath.catalan(37), ApfloatConstantMath.catalan(37, 10), 36);
        assertClose("G in radix 7", ApfloatMath.catalan(45, 7), ApfloatConstantMath.catalan(45, 7), 44);
        assertThrows(InfiniteExpansionException.class, () -> ApfloatConstantMath.catalan(Apfloat.INFINITE, 10));
        assertThrows(IllegalArgumentException.class, () -> ApfloatConstantMath.catalan(0, 10));
    }

    @Test
    public void testZetaOfInteger()
    {
        Apfloat pi = ApfloatMath.pi(90);
        assertClose("zeta(2)", pi.multiply(pi).divide(new Apint(6)), ApcomplexZetaMath.zetaOfInteger(2, 90, 10), 88);
        Apfloat pi4 = ApfloatMath.pow(pi, 4);
        assertClose("zeta(4)", pi4.divide(new Apint(90)), ApcomplexZetaMath.zetaOfInteger(4, 90, 10), 88);
        for (long s : new long[] { 3, 5, 9, 31, 250 })
        {
            Apfloat zeta = ApcomplexZetaMath.zetaOfInteger(s, 77, 10);
            assertEquals(77, zeta.precision());
            assertClose("zeta(" + s + ")", ApcomplexMath.zeta(new Apfloat(s, 77)).real(), zeta, 75);
        }
        assertClose("zeta(3) in radix 2", ApcomplexMath.zeta(new Apfloat(3, 150, 2)).real(),
                    ApcomplexZetaMath.zetaOfInteger(3, 150, 2), 146);
        assertThrows(IllegalArgumentException.class, () -> ApcomplexZetaMath.zetaOfInteger(1, 20, 10));
        assertThrows(InfiniteExpansionException.class, () -> ApcomplexZetaMath.zetaOfInteger(3, Apfloat.INFINITE, 10));
    }

    @Test
    public void testPolylogOfSmallArgument()
    {
        Apfloat twoSevenths = new Aprational(new Apint(2), new Apint(7)).precision(66);
        assertClose("Li_2(2/7)", ApcomplexMath.polylog(new Apfloat(2, 66), twoSevenths),
                    ApcomplexZetaMath.polylogOfSmallArgument(2, twoSevenths), 64);
        Apfloat half = new Apfloat("-0.5", 50);
        assertClose("Li_5(-1/2)", ApcomplexMath.polylog(new Apfloat(5, 50), half),
                    ApcomplexZetaMath.polylogOfSmallArgument(5, half), 48);
        // Li_1(z) == -log(1 - z)
        Apcomplex z = new Apcomplex(new Apfloat("0.21", 45), new Apfloat("-0.38", 45));
        assertClose("Li_1(z)", ApcomplexMath.log(Apcomplex.ONE.subtract(z)).negate(),
                    ApcomplexZetaMath.polylogOfSmallArgument(1, z), 43);
        assertClose("Li_3(z)", ApcomplexMath.polylog(new Apfloat(3, 45), z),
                    ApcomplexZetaMath.polylogOfSmallArgument(3, z), 43);
        // terms which decrease slowly
        Apfloat large = new Apfloat("0.74", 40);
        assertClose("Li_2(0.74)", ApcomplexMath.polylog(new Apfloat(2, 40), large),
                    ApcomplexZetaMath.polylogOfSmallArgument(2, large), 38);
        Apcomplex w = new Apcomplex(new Apfloat("-0.6", 40), new Apfloat("0.41", 40));
        assertClose("Li_4(w)", ApcomplexMath.polylog(new Apfloat(4, 40), w),
                    ApcomplexZetaMath.polylogOfSmallArgument(4, w), 38);
        Apfloat zero = new Apfloat(0, 30);
        assertEquals(0, ApcomplexZetaMath.polylogOfSmallArgument(4, zero).real().signum());
        // no series
        assertNull(ApcomplexZetaMath.polylogOfSmallArgument(2, new Apfloat("0.76", 30)));
        assertNull(ApcomplexZetaMath.polylogOfSmallArgument(0, half));
        assertNull(ApcomplexZetaMath.polylogOfSmallArgument(2, new Aprational(new Apint(1), new Apint(3))));
    }
}
