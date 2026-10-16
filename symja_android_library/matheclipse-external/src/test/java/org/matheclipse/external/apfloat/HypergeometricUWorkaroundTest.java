package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link HypergeometricUWorkaround}. The expected values were calculated with mpmath.
 */
public class HypergeometricUWorkaroundTest
{
    private static void assertClose(String message, String expected, Apcomplex actual, long digits)
    {
        Apfloat reference = new Apfloat(expected);
        assertEquals(0, actual.imag().signum(), message + ": imaginary part " + actual.imag());
        Apfloat difference = reference.subtract(actual.real());
        if (difference.signum() == 0)
        {
            return;
        }
        assertTrue(difference.scale() <= reference.scale() - digits, message + ": expected " + expected + " but was " + actual);
    }

    private static Apcomplex u(String a, String b, String z)
    {
        FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(30);
        return HypergeometricUWorkaround.hypergeometricU(h, new Apfloat(a, 30), new Apfloat(b, 30), new Apfloat(z, 30));
    }

    @Test
    public void testIntegerBLargeArgument()
    {
        // apfloat 1.16.0 divides by zero for these
        assertClose("U(15,6,60)", "2.61701279292339367001428994910165843e-28", u("15", "6", "60"), 29);
        assertClose("U(15,6,83.75)", "2.98030788119053023676979589774101393e-30", u("15", "6", "83.75"), 29);
        assertClose("U(12,6,97.5)", "6.17607539884738044265007964697964942e-25", u("12", "6", "97.5"), 29);
        assertClose("U(9,3,72.25)", "8.49520341626884463828908201876987447e-18", u("9", "3", "72.25"), 29);
        assertClose("U(7.5,4,55.5)", "4.78296511834729237065165710209743646e-14", u("7.5", "4", "55.5"), 29);
    }

    @Test
    public void testDirectEvaluation()
    {
        // the evaluation where apfloat succeeds, and the one of the workaround itself
        assertClose("U(15,6,40)", "4.9510209941220721114761869460990029e-26", u("15", "6", "40"), 29);
        assertClose("U(1.5,7,4.5) apfloat", "0.554931133906644949855594619726537596", u("1.5", "7", "4.5"), 29);
        assertClose("U(1.5,7,4.5)", "0.554931133906644949855594619726537596",
                    HypergeometricUWorkaround.hypergeometricU(new Apfloat("1.5", 30), new Apfloat(7, 30), new Apfloat("4.5", 30), 30), 29);
        assertClose("U(3,1,2)", "0.0146501591087790464400658018755897863",
                    HypergeometricUWorkaround.hypergeometricU(new Apfloat(3, 30), new Apfloat(1, 30), new Apfloat(2, 30), 30), 29);
    }
}
