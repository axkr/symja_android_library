package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApcomplexExpIntegralMath}: the series against the general algorithms of apfloat.
 */
public class ApcomplexExpIntegralMathTest
{
    private static final long PRECISION = 30;

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

    private static Apcomplex z(String re, String im)
    {
        return new Apcomplex(new Apfloat(re, PRECISION), new Apfloat(im, PRECISION));
    }

    private static final Apcomplex[] ARGUMENTS =
    {
        z("1", "0"),
        z("-1.5", "0"),
        z("7", "2"),
        z("-3", "4"),
        z("25", "0"),
        z("0", "-12.5"),
        z("-30", "10"),
    };

    @Test
    public void testSeriesArgument()
    {
        assertFalse(ApcomplexExpIntegralMath.isSeriesArgument(Apcomplex.ZERO), "zero");
        assertTrue(ApcomplexExpIntegralMath.isSeriesArgument(z("40", "0")), "40");
        assertFalse(ApcomplexExpIntegralMath.isSeriesArgument(z("30", "30")), "30+30i");
        for (Apcomplex z : ARGUMENTS)
        {
            assertTrue(ApcomplexExpIntegralMath.isSeriesArgument(z), z.toString());
        }
    }

    @Test
    public void testSinIntegral()
    {
        for (Apcomplex z : ARGUMENTS)
        {
            Apcomplex series = ApcomplexExpIntegralMath.sinIntegralSeries(z, PRECISION);
            assertClose("Si(" + z + ")", ApcomplexMath.sinIntegral(z), series, PRECISION - 2);
            assertEquals(PRECISION, series.precision(), "precision of Si(" + z + ")");
        }
        assertEquals(Apcomplex.ZERO, ApcomplexExpIntegralMath.sinIntegralSeries(Apcomplex.ZERO, PRECISION), "Si(0)");
    }

    @Test
    public void testSinhIntegral()
    {
        for (Apcomplex z : ARGUMENTS)
        {
            assertClose("Shi(" + z + ")", ApcomplexMath.sinhIntegral(z), ApcomplexExpIntegralMath.sinhIntegralSeries(z, PRECISION), PRECISION - 2);
        }
    }

    @Test
    public void testCosIntegral()
    {
        for (Apcomplex z : ARGUMENTS)
        {
            assertClose("Ci(" + z + ")", ApcomplexMath.cosIntegral(z), ApcomplexExpIntegralMath.cosIntegralSeries(z, PRECISION), PRECISION - 2);
        }
    }

    @Test
    public void testCoshIntegral()
    {
        for (Apcomplex z : ARGUMENTS)
        {
            assertClose("Chi(" + z + ")", ApcomplexMath.coshIntegral(z), ApcomplexExpIntegralMath.coshIntegralSeries(z, PRECISION), PRECISION - 2);
        }
    }

    @Test
    public void testExpIntegralEi()
    {
        for (Apcomplex z : ARGUMENTS)
        {
            assertClose("Ei(" + z + ")", ApcomplexMath.expIntegralEi(z), ApcomplexExpIntegralMath.expIntegralEiSeries(z, PRECISION), PRECISION - 2);
        }
        // real on the negative real axis
        Apcomplex ei = ApcomplexExpIntegralMath.expIntegralEiSeries(z("-1", "0"), PRECISION);
        assertEquals(0, ei.imag().signum(), "Ei(-1) is real");
        assertClose("Ei(-1)", new Apfloat("-0.219383934395520273677163775460"), ei, PRECISION - 2);
    }

    @Test
    public void testNearAZero()
    {
        // Ci(x) has a zero at x = 0.6165054856207162337971104..., where the sum cancels against EulerGamma + Log(x)
        Apcomplex z = z("0.6165054856207162", "0");
        assertClose("Ci(0.6165...)", ApcomplexMath.cosIntegral(z), ApcomplexExpIntegralMath.cosIntegralSeries(z, PRECISION), PRECISION - 3);
    }
}
