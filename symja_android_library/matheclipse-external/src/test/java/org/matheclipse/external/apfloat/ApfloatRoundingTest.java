package org.matheclipse.external.apfloat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.junit.jupiter.api.Test;

/**
 * Tests of {@link ApfloatRounding}. The expected values were calculated with Python's decimal
 * module.
 */
public class ApfloatRoundingTest
{
    private static final FixedPrecisionApfloatHelper HELPER = new FixedPrecisionApfloatHelper(20);

    @Test
    public void testStoredDigitsRoundTheLastDigit()
    {
        // pi and e rounded to 30 digits: their product is 8.53973422267356706546355086953|9561...,
        // and prints its 30 digits cut off
        FixedPrecisionApfloatHelper helper = new FixedPrecisionApfloatHelper(30);
        Apfloat product = helper.multiply(new Apfloat("3.14159265358979323846264338328", 30), new Apfloat("2.71828182845904523536028747135", 30));
        assertEquals(30, product.precision(), "precision");
        Apfloat rounded = ApfloatRounding.roundToPrecision(product);
        assertEquals(30, rounded.precision(), "rounded precision");
        assertEquals("8.53973422267356706546355086953", product.toString(true), "cut off");
        assertEquals("8.53973422267356706546355086954", rounded.toString(true), "rounded");
    }

    @Test
    public void testDigitBelowFiveIsKept()
    {
        // 2/9 = 0.2222222222222222222222|22...
        Apfloat quotient = HELPER.divide(new Apfloat(2, 20), new Apfloat(9, 20));
        assertEquals("0.22222222222222222222", ApfloatRounding.roundToPrecision(quotient).toString(true), "2/9");
        // 7/9 = 0.7777777777777777777777|77...
        quotient = HELPER.divide(new Apfloat(7, 20), new Apfloat(9, 20));
        assertEquals("0.77777777777777777778", ApfloatRounding.roundToPrecision(quotient).toString(true), "7/9");
    }

    @Test
    public void testCarryIntoTheNextPower()
    {
        // 0.99999999999999999999|7 rounds to 1 with the precision kept
        Apfloat x = new Apfloat("0.999999999999999999997", 22).precision(20);
        Apfloat rounded = ApfloatRounding.roundToPrecision(x);
        assertEquals(new Apfloat(1), rounded, "value");
        assertEquals(20, rounded.precision(), "precision");
    }

    @Test
    public void testNoStoredDigits()
    {
        Apfloat x = new Apfloat("2.5", 30);
        assertSame(x, ApfloatRounding.roundToPrecision(x), "no digits beyond the precision");
        assertSame(Apfloat.ZERO, ApfloatRounding.roundToPrecision(Apfloat.ZERO), "zero");
        Apfloat exact = new Apfloat(12345);
        assertSame(exact, ApfloatRounding.roundToPrecision(exact), "infinite precision");
    }

    @Test
    public void testComplexParts()
    {
        Apfloat seven = new Apfloat(7, 20);
        // 1/7 = 0.14285714285714285714|28..., 6/7 = 0.85714285714285714285|71...
        Apcomplex z = new Apcomplex(HELPER.divide(new Apfloat(1, 20), seven), HELPER.divide(new Apfloat(6, 20), seven));
        Apcomplex rounded = ApfloatRounding.roundToPrecision(z);
        assertEquals("0.14285714285714285714", rounded.real().toString(true), "real part");
        assertEquals("0.85714285714285714286", rounded.imag().toString(true), "imaginary part");
        Apfloat real = new Apfloat("0.5", 20);
        assertSame(real, ApfloatRounding.roundToPrecision((Apcomplex) real), "a real number");
    }
}
