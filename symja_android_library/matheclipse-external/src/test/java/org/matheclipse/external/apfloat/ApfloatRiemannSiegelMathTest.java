package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatRiemannSiegelMath}. The expected values were calculated with mpmath.
 */
public class ApfloatRiemannSiegelMathTest
{
    private static void assertClose(String message, String expected, Apfloat actual, long digits)
    {
        Apfloat difference = new Apfloat(expected).subtract(actual);
        assertTrue(difference.signum() == 0 || difference.scale() <= actual.scale() - digits,
                   message + ": expected " + expected + " but was " + actual);
    }

    @Test
    public void testRiemannSiegelZ()
    {
        assertClose("Z(74920.8)", "0.58217817840796375595231879913830331237701588007966",
                    ApfloatRiemannSiegelMath.riemannSiegelZ(new Apfloat("74920.8", 40)), 38);
        assertClose("Z(1234567.25)", "0.073621642961561920864082154922036029881504011309781",
                    ApfloatRiemannSiegelMath.riemannSiegelZ(new Apfloat("1234567.25", 30)), 28);
        assertClose("Z(6820051.95)", "0.041851253639357657382420149098688315845419721499103",
                    ApfloatRiemannSiegelMath.riemannSiegelZ(new Apfloat("6820051.95", 20)), 18);
    }

    @Test
    public void testArgumentTooSmallForThePrecision()
    {
        assertThrows(ArithmeticException.class, () -> ApfloatRiemannSiegelMath.riemannSiegelZ(new Apfloat("1000.5", 30)));
        assertThrows(ArithmeticException.class, () -> ApfloatRiemannSiegelMath.riemannSiegelZ(new Apfloat("9877.25", 60)));
    }

    @Test
    public void testRiemannSiegelZDouble()
    {
        // 1234567.25 is a binary fraction: the double is the argument of the expected value
        double z = ApfloatRiemannSiegelMath.riemannSiegelZ(1234567.25);
        assertEquals(0.073621642961561920864, z, ApfloatRiemannSiegelMath.doubleError(1234567.25));
        assertEquals(0.073621642961561920864, z, 1.0e-8);
        assertThrows(ArithmeticException.class, () -> ApfloatRiemannSiegelMath.riemannSiegelZ(100.0));
    }
}
