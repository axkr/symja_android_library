package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatSignificance}. The condition numbers |x f'(x) / f(x)| were calculated with mpmath.
 */
public class ApfloatSignificanceTest
{
    private static void assertRelative(String message, double expected, double actual, double tolerance)
    {
        assertTrue(Math.abs(actual - expected) <= tolerance * Math.abs(expected), message + ": expected " + expected + " but was " + actual);
    }

    private static double condition(ApfloatSignificance.Function function, Apfloat x, Apfloat value)
    {
        double[] c = ApfloatSignificance.conditionNumbers(function, new Apfloat[] { x }, value);
        assertNotNull(c, function + "(" + x + ")");
        return c[0];
    }

    @Test
    public void testInverseFunctionsNextToOne()
    {
        // 1 - x^2 within 1e-16 of +-1: from the digits of x, not from a double, which is 0 there
        Apfloat below = new Apfloat("0.99999999999999999999", 30),
                above = new Apfloat("1.00000000000000000001", 30);
        assertRelative("ArcSin", 4.5015815811908150823e9, condition(ApfloatSignificance.Function.ARCSIN, below, ApfloatMath.asin(below)), 1.0e-9);
        assertRelative("ArcCosh", 5.0000000000000000004e19, condition(ApfloatSignificance.Function.ARCCOSH, above, ApfloatMath.acosh(above)), 1.0e-9);
        assertRelative("ArcTanh", 2.1392731403086991808e18, condition(ApfloatSignificance.Function.ARCTANH, below, ApfloatMath.atanh(below)), 1.0e-9);
        Apfloat half = new Apfloat("0.5", 30);
        assertRelative("ArcSin(1/2)", 1.1026577908435840, condition(ApfloatSignificance.Function.ARCSIN, half, ApfloatMath.asin(half)), 1.0e-12);
    }
}
