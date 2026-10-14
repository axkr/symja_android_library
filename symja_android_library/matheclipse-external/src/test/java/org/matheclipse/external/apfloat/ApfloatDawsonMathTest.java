package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatDawsonMath}. The expected values were calculated with mpmath.
 */
public class ApfloatDawsonMathTest
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
    public void testModerateArguments()
    {
        assertClose("F(5)", "0.10213407442427683543855100704927", ApfloatDawsonMath.dawsonF(new Apfloat(5, 30)), 29);
        assertClose("F(12)", "0.041812876453988260317929117588809", ApfloatDawsonMath.dawsonF(new Apfloat(12, 30)), 29);
        assertClose("F(30)", "0.016675941401059175798435780415806", ApfloatDawsonMath.dawsonF(new Apfloat(30, 30)), 29);
    }

    @Test
    public void testLargeArguments()
    {
        // the product of the exponentials loses the digits of x^2 here
        assertClose("F(1000)", "0.00050000025000037500093750328126477", ApfloatDawsonMath.dawsonF(new Apfloat(1000, 30)), 29);
        assertClose("F(10^5)", "0.0000050000000002500000000375000000094", ApfloatDawsonMath.dawsonF(new Apfloat(100000, 30)), 29);
        assertClose("F(10^7)", "0.00000005000000000000025000000000000375", ApfloatDawsonMath.dawsonF(new Apfloat(10000000, 30)), 29);
        assertClose("F(-10^7)", "-0.00000005000000000000025000000000000375", ApfloatDawsonMath.dawsonF(new Apfloat(-10000000, 30)), 29);
        assertClose("F(100)", "0.0050002500375093782827273751376423339081627598162479542660049032", ApfloatDawsonMath.dawsonF(new Apfloat(100, 60)), 59);
    }
}
